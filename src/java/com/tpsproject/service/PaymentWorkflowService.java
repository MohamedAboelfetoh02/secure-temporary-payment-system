package com.tpsproject.service;

import com.tpsproject.dao.AuditLogDao;
import com.tpsproject.dao.CurrencyPackageDao;
import com.tpsproject.dao.PaymentWorkflowDao;
import com.tpsproject.model.CurrencyPackage;
import com.tpsproject.model.PaymentActionResult;
import com.tpsproject.model.PaymentInputData;
import com.tpsproject.model.PaymentSessionDetails;
import com.tpsproject.util.DBConnectionUtil;
import com.tpsproject.util.PaymentMethodUtil;
import com.tpsproject.util.ReferenceCodeUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class PaymentWorkflowService {

    private static final int REFERENCE_CODE_LENGTH = 10;
    private static final int EXPIRY_MINUTES = 5;

    private final CurrencyPackageDao currencyPackageDao = new CurrencyPackageDao();
    private final PaymentWorkflowDao paymentWorkflowDao = new PaymentWorkflowDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public String startPurchase(int userId, int currencyPackageId, String ipAddress) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);

            try {
                CurrencyPackage currencyPackage = currencyPackageDao.findActiveById(connection, currencyPackageId);
                if (currencyPackage == null) {
                    throw new SQLException("The selected currency package is not available.");
                }

                int purchaseRequestId = paymentWorkflowDao.createPurchaseRequest(
                        connection, userId, currencyPackageId, currencyPackage.getPrice());

                String referenceCode = generateUniqueReferenceCode(connection);
                Timestamp expiresAt = Timestamp.from(Instant.now().plus(Duration.ofMinutes(EXPIRY_MINUTES)));
                int paymentSessionId = paymentWorkflowDao.createPaymentSession(connection, purchaseRequestId, referenceCode, expiresAt);

                String transactionCode = generateUniqueTransactionCode(connection);
                int transactionId = paymentWorkflowDao.createTransaction(connection, paymentSessionId, transactionCode, currencyPackage.getPrice());

                auditLogDao.log(connection, userId, "PURCHASE_INITIATED",
                        "Purchase started for " + currencyPackage.getPackageName() + ".",
                        ipAddress, "PURCHASE_REQUEST", purchaseRequestId);

                auditLogDao.log(connection, userId, "PAYMENT_SESSION_CREATED",
                        "Payment session created with reference code " + referenceCode + ".",
                        ipAddress, "PAYMENT_SESSION", paymentSessionId);

                auditLogDao.log(connection, userId, "TRANSACTION_CREATED",
                        "Transaction created with code " + transactionCode + ".",
                        ipAddress, "TRANSACTION", transactionId);

                connection.commit();
                return referenceCode;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public PaymentSessionDetails getPaymentSessionDetails(String referenceCode, int userId, String ipAddress) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);

            try {
                PaymentSessionDetails details = paymentWorkflowDao.findDetailsByReference(connection, referenceCode, userId);
                if (details == null) {
                    connection.commit();
                    return null;
                }

                details = expireIfNeeded(connection, details, ipAddress);
                updateSecondsRemaining(details);
                connection.commit();
                return details;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public PaymentActionResult confirmPayment(String referenceCode, int userId, PaymentInputData paymentInputData, String ipAddress) throws SQLException {
        return completeAction(referenceCode, userId, paymentInputData, ipAddress, "CONFIRMED");
    }

    public PaymentActionResult cancelPayment(String referenceCode, int userId, PaymentInputData paymentInputData, String ipAddress) throws SQLException {
        return completeAction(referenceCode, userId, paymentInputData, ipAddress, "CANCELLED");
    }

    public List<PaymentSessionDetails> getUserHistory(int userId) throws SQLException {
        List<PaymentSessionDetails> history = paymentWorkflowDao.findHistoryByUser(userId);
        for (PaymentSessionDetails details : history) {
            updateSecondsRemaining(details);
        }
        return history;
    }

    public List<PaymentSessionDetails> getAllHistory() throws SQLException {
        List<PaymentSessionDetails> history = paymentWorkflowDao.findAllHistory();
        for (PaymentSessionDetails details : history) {
            updateSecondsRemaining(details);
        }
        return history;
    }

    private PaymentActionResult completeAction(String referenceCode, int userId, PaymentInputData paymentInputData, String ipAddress, String targetStatus) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);

            try {
                PaymentActionResult result = new PaymentActionResult();
                PaymentSessionDetails details = paymentWorkflowDao.findDetailsByReference(connection, referenceCode, userId);

                if (details == null) {
                    result.setMessageType("error");
                    result.setMessage("That payment session could not be found.");
                    connection.commit();
                    return result;
                }

                String paymentMethod = paymentInputData == null ? null : paymentInputData.getPaymentMethod();
                String paymentDetailSummary = null;

                if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                    if (!PaymentMethodUtil.isValid(paymentMethod)) {
                        result.setPaymentSessionDetails(details);
                        result.setMessageType("error");
                        result.setMessage("Select a valid payment method.");
                        connection.commit();
                        return result;
                    }
                }

                details = expireIfNeeded(connection, details, ipAddress);

                if (details.isExpired()) {
                    updateSecondsRemaining(details);
                    result.setPaymentSessionDetails(details);
                    result.setMessageType("error");
                    result.setMessage("This payment session has expired.");
                    connection.commit();
                    return result;
                }

                if (details.isConfirmed()) {
                    updateSecondsRemaining(details);
                    result.setPaymentSessionDetails(details);
                    result.setMessageType("success");
                    result.setMessage("This payment has already been confirmed.");
                    connection.commit();
                    return result;
                }

                if (details.isCancelled()) {
                    updateSecondsRemaining(details);
                    result.setPaymentSessionDetails(details);
                    result.setMessageType("error");
                    result.setMessage("This payment session was already cancelled.");
                    connection.commit();
                    return result;
                }

                if ("CONFIRMED".equals(targetStatus)) {
                    if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                        updateSecondsRemaining(details);
                        result.setPaymentSessionDetails(details);
                        result.setMessageType("error");
                        result.setMessage("Choose a payment method before confirming the session.");
                        connection.commit();
                        return result;
                    }

                    try {
                        paymentDetailSummary = PaymentMethodUtil.validateAndBuildSummary(paymentInputData);
                    } catch (IllegalArgumentException ex) {
                        updateSecondsRemaining(details);
                        result.setPaymentSessionDetails(details);
                        result.setMessageType("error");
                        result.setMessage(ex.getMessage());
                        connection.commit();
                        return result;
                    }
                }

                if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                    paymentWorkflowDao.updatePaymentMethodDetails(connection, details.getPaymentSessionId(), paymentMethod, paymentDetailSummary);
                }

                Timestamp actionTime = Timestamp.from(Instant.now());
                paymentWorkflowDao.updatePurchaseRequestStatus(connection, details.getPurchaseRequestId(), targetStatus);
                paymentWorkflowDao.updatePaymentSessionStatus(connection, details.getPaymentSessionId(), targetStatus, actionTime);
                paymentWorkflowDao.updateTransactionStatus(connection, details.getTransactionId(), targetStatus, actionTime);

                if ("CONFIRMED".equals(targetStatus)) {
                    auditLogDao.log(connection, userId, "PAYMENT_CONFIRMED",
                            "Payment confirmed for reference code " + details.getReferenceCode()
                            + " using " + paymentMethod + " (" + paymentDetailSummary + ").",
                            ipAddress, "PAYMENT_SESSION", details.getPaymentSessionId());
                    result.setMessageType("success");
                    result.setMessage("Payment confirmed. The purchase is now recorded for fulfillment.");
                } else {
                    auditLogDao.log(connection, userId, "PAYMENT_CANCELLED",
                            "Payment cancelled for reference code " + details.getReferenceCode() + ".",
                            ipAddress, "PAYMENT_SESSION", details.getPaymentSessionId());
                    result.setMessageType("success");
                    result.setMessage("Payment cancelled.");
                }

                details = paymentWorkflowDao.findDetailsByReference(connection, referenceCode, userId);
                updateSecondsRemaining(details);
                result.setPaymentSessionDetails(details);
                connection.commit();
                return result;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private PaymentSessionDetails expireIfNeeded(Connection connection, PaymentSessionDetails details, String ipAddress) throws SQLException {
        if (details == null || !details.isPending()) {
            return details;
        }

        Instant now = Instant.now();
        if (details.getExpiresAt() != null && !now.isBefore(details.getExpiresAt().toInstant())) {
            Timestamp expiredAt = Timestamp.from(now);
            paymentWorkflowDao.updatePurchaseRequestStatus(connection, details.getPurchaseRequestId(), "EXPIRED");
            paymentWorkflowDao.updatePaymentSessionStatus(connection, details.getPaymentSessionId(), "EXPIRED", expiredAt);
            paymentWorkflowDao.updateTransactionStatus(connection, details.getTransactionId(), "EXPIRED", expiredAt);

            auditLogDao.log(connection, details.getUserId(), "PAYMENT_EXPIRED",
                    "Payment session expired for reference code " + details.getReferenceCode() + ".",
                    ipAddress, "PAYMENT_SESSION", details.getPaymentSessionId());

            return paymentWorkflowDao.findDetailsByReference(connection, details.getReferenceCode(), details.getUserId());
        }

        return details;
    }

    private void updateSecondsRemaining(PaymentSessionDetails details) {
        if (details == null || details.getExpiresAt() == null || !details.isPending()) {
            if (details != null) {
                details.setSecondsRemaining(0);
            }
            return;
        }

        long secondsRemaining = Duration.between(Instant.now(), details.getExpiresAt().toInstant()).getSeconds();
        details.setSecondsRemaining(Math.max(secondsRemaining, 0));
    }

    private String generateUniqueReferenceCode(Connection connection) throws SQLException {
        for (int attempt = 0; attempt < 10; attempt++) {
            String referenceCode = ReferenceCodeUtil.generateCode(REFERENCE_CODE_LENGTH);
            if (!paymentWorkflowDao.referenceCodeExists(connection, referenceCode)) {
                return referenceCode;
            }
        }

        throw new SQLException("A unique reference code could not be generated.");
    }

    private String generateUniqueTransactionCode(Connection connection) throws SQLException {
        for (int attempt = 0; attempt < 10; attempt++) {
            String transactionCode = ReferenceCodeUtil.generateTransactionCode();
            if (!paymentWorkflowDao.transactionCodeExists(connection, transactionCode)) {
                return transactionCode;
            }
        }

        throw new SQLException("A unique transaction code could not be generated.");
    }
}
