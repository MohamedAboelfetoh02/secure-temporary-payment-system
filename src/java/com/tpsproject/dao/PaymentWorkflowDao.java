package com.tpsproject.dao;

import com.tpsproject.model.PaymentSessionDetails;
import com.tpsproject.util.DBConnectionUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentWorkflowDao {

    private static final String INSERT_PURCHASE_REQUEST =
            "INSERT INTO purchase_requests (user_id, currency_package_id, amount, request_status) VALUES (?, ?, ?, 'PENDING')";

    private static final String INSERT_PAYMENT_SESSION =
            "INSERT INTO payment_sessions (purchase_request_id, reference_code, session_status, expires_at) VALUES (?, ?, 'PENDING', ?)";

    private static final String INSERT_TRANSACTION =
            "INSERT INTO transactions (payment_session_id, transaction_code, amount, transaction_status) VALUES (?, ?, ?, 'PENDING')";

    private static final String FIND_PAYMENT_SESSION_DETAILS =
            "SELECT pr.id AS purchase_request_id, pr.user_id, pr.request_status, pr.amount, "
            + "ps.id AS payment_session_id, ps.reference_code, ps.payment_method, ps.payment_detail_summary, ps.session_status, ps.expires_at, ps.confirmed_at, ps.cancelled_at, ps.expired_at, ps.created_at, "
            + "tr.id AS transaction_id, tr.transaction_code, tr.transaction_status, tr.processed_at, "
            + "cp.id AS currency_package_id, cp.package_name, cp.currency_name, cp.currency_amount, "
            + "g.id AS game_id, g.title AS game_title, g.description AS game_description, g.genre AS game_genre "
            + "FROM payment_sessions ps "
            + "JOIN purchase_requests pr ON pr.id = ps.purchase_request_id "
            + "JOIN transactions tr ON tr.payment_session_id = ps.id "
            + "JOIN currency_packages cp ON cp.id = pr.currency_package_id "
            + "JOIN games g ON g.id = cp.game_id "
            + "WHERE ps.reference_code = ? AND pr.user_id = ? LIMIT 1";

    private static final String CHECK_REFERENCE_CODE =
            "SELECT COUNT(*) FROM payment_sessions WHERE reference_code = ?";

    private static final String CHECK_TRANSACTION_CODE =
            "SELECT COUNT(*) FROM transactions WHERE transaction_code = ?";

    private static final String UPDATE_PURCHASE_REQUEST_STATUS =
            "UPDATE purchase_requests SET request_status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String UPDATE_TRANSACTION_STATUS =
            "UPDATE transactions SET transaction_status = ?, processed_at = ? WHERE id = ?";

    private static final String UPDATE_PAYMENT_METHOD =
            "UPDATE payment_sessions SET payment_method = ?, payment_detail_summary = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String FIND_USER_PAYMENT_HISTORY =
            "SELECT pr.id AS purchase_request_id, pr.user_id, pr.request_status, pr.amount, "
            + "ps.id AS payment_session_id, ps.reference_code, ps.payment_method, ps.payment_detail_summary, ps.session_status, ps.expires_at, ps.confirmed_at, ps.cancelled_at, ps.expired_at, ps.created_at, "
            + "tr.id AS transaction_id, tr.transaction_code, tr.transaction_status, tr.processed_at, "
            + "cp.id AS currency_package_id, cp.package_name, cp.currency_name, cp.currency_amount, "
            + "g.id AS game_id, g.title AS game_title, g.description AS game_description, g.genre AS game_genre "
            + "FROM payment_sessions ps "
            + "JOIN purchase_requests pr ON pr.id = ps.purchase_request_id "
            + "JOIN transactions tr ON tr.payment_session_id = ps.id "
            + "JOIN currency_packages cp ON cp.id = pr.currency_package_id "
            + "JOIN games g ON g.id = cp.game_id "
            + "WHERE pr.user_id = ? "
            + "ORDER BY ps.created_at DESC, ps.id DESC";

    private static final String FIND_ALL_PAYMENT_HISTORY =
            "SELECT pr.id AS purchase_request_id, pr.user_id, u.username, pr.request_status, pr.amount, "
            + "ps.id AS payment_session_id, ps.reference_code, ps.payment_method, ps.payment_detail_summary, ps.session_status, ps.expires_at, ps.confirmed_at, ps.cancelled_at, ps.expired_at, ps.created_at, "
            + "tr.id AS transaction_id, tr.transaction_code, tr.transaction_status, tr.processed_at, "
            + "cp.id AS currency_package_id, cp.package_name, cp.currency_name, cp.currency_amount, "
            + "g.id AS game_id, g.title AS game_title, g.description AS game_description, g.genre AS game_genre "
            + "FROM payment_sessions ps "
            + "JOIN purchase_requests pr ON pr.id = ps.purchase_request_id "
            + "JOIN users u ON u.id = pr.user_id "
            + "JOIN transactions tr ON tr.payment_session_id = ps.id "
            + "JOIN currency_packages cp ON cp.id = pr.currency_package_id "
            + "JOIN games g ON g.id = cp.game_id "
            + "ORDER BY ps.created_at DESC, ps.id DESC";

    private static final String COUNT_ALL_PAYMENT_SESSIONS =
            "SELECT COUNT(*) FROM payment_sessions";

    private static final String COUNT_PENDING_PAYMENT_SESSIONS =
            "SELECT COUNT(*) FROM payment_sessions WHERE session_status = 'PENDING'";

    public int createPurchaseRequest(Connection connection, int userId, int currencyPackageId, BigDecimal amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PURCHASE_REQUEST, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setInt(2, currencyPackageId);
            statement.setBigDecimal(3, amount);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        throw new SQLException("Purchase request could not be created.");
    }

    public int createPaymentSession(Connection connection, int purchaseRequestId, String referenceCode, Timestamp expiresAt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PAYMENT_SESSION, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, purchaseRequestId);
            statement.setString(2, referenceCode);
            statement.setTimestamp(3, expiresAt);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        throw new SQLException("Payment session could not be created.");
    }

    public int createTransaction(Connection connection, int paymentSessionId, String transactionCode, BigDecimal amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, paymentSessionId);
            statement.setString(2, transactionCode);
            statement.setBigDecimal(3, amount);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        throw new SQLException("Transaction could not be created.");
    }

    public PaymentSessionDetails findDetailsByReference(Connection connection, String referenceCode, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_PAYMENT_SESSION_DETAILS)) {
            statement.setString(1, referenceCode);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDetails(resultSet, false);
                }
            }
        }

        return null;
    }

    public boolean referenceCodeExists(Connection connection, String referenceCode) throws SQLException {
        return codeExists(connection, CHECK_REFERENCE_CODE, referenceCode);
    }

    public boolean transactionCodeExists(Connection connection, String transactionCode) throws SQLException {
        return codeExists(connection, CHECK_TRANSACTION_CODE, transactionCode);
    }

    public void updatePurchaseRequestStatus(Connection connection, int purchaseRequestId, String status) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PURCHASE_REQUEST_STATUS)) {
            statement.setString(1, status);
            statement.setInt(2, purchaseRequestId);
            statement.executeUpdate();
        }
    }

    public void updateTransactionStatus(Connection connection, int transactionId, String status, Timestamp processedAt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_STATUS)) {
            statement.setString(1, status);
            statement.setTimestamp(2, processedAt);
            statement.setInt(3, transactionId);
            statement.executeUpdate();
        }
    }

    public void updatePaymentSessionStatus(Connection connection, int paymentSessionId, String status, Timestamp actionTime) throws SQLException {
        String sql;

        switch (status) {
            case "CONFIRMED":
                sql = "UPDATE payment_sessions SET session_status = 'CONFIRMED', confirmed_at = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                break;
            case "CANCELLED":
                sql = "UPDATE payment_sessions SET session_status = 'CANCELLED', cancelled_at = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                break;
            case "EXPIRED":
                sql = "UPDATE payment_sessions SET session_status = 'EXPIRED', expired_at = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                break;
            default:
                throw new SQLException("Unsupported payment session status: " + status);
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, actionTime);
            statement.setInt(2, paymentSessionId);
            statement.executeUpdate();
        }
    }

    public void updatePaymentMethodDetails(Connection connection, int paymentSessionId, String paymentMethod, String paymentDetailSummary) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PAYMENT_METHOD)) {
            statement.setString(1, paymentMethod);
            statement.setString(2, paymentDetailSummary);
            statement.setInt(3, paymentSessionId);
            statement.executeUpdate();
        }
    }

    public List<PaymentSessionDetails> findHistoryByUser(int userId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_USER_PAYMENT_HISTORY)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapHistoryResults(resultSet, false);
            }
        }
    }

    public List<PaymentSessionDetails> findAllHistory() throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_PAYMENT_HISTORY);
                ResultSet resultSet = statement.executeQuery()) {
            return mapHistoryResults(resultSet, true);
        }
    }

    public int countAllPaymentSessions() throws SQLException {
        return countBySql(COUNT_ALL_PAYMENT_SESSIONS);
    }

    public int countPendingPaymentSessions() throws SQLException {
        return countBySql(COUNT_PENDING_PAYMENT_SESSIONS);
    }

    private int countBySql(String sql) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }

        return 0;
    }

    private boolean codeExists(Connection connection, String sql, String code) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private List<PaymentSessionDetails> mapHistoryResults(ResultSet resultSet, boolean includeUsername) throws SQLException {
        List<PaymentSessionDetails> history = new ArrayList<>();

        while (resultSet.next()) {
            history.add(mapDetails(resultSet, includeUsername));
        }

        return history;
    }

    private PaymentSessionDetails mapDetails(ResultSet resultSet, boolean includeUsername) throws SQLException {
        PaymentSessionDetails details = new PaymentSessionDetails();
        details.setPurchaseRequestId(resultSet.getInt("purchase_request_id"));
        details.setUserId(resultSet.getInt("user_id"));
        details.setUsername(includeUsername ? resultSet.getString("username") : null);
        details.setRequestStatus(resultSet.getString("request_status"));
        details.setAmount(resultSet.getBigDecimal("amount"));
        details.setPaymentSessionId(resultSet.getInt("payment_session_id"));
        details.setReferenceCode(resultSet.getString("reference_code"));
        details.setPaymentMethod(resultSet.getString("payment_method"));
        details.setPaymentDetailSummary(resultSet.getString("payment_detail_summary"));
        details.setSessionStatus(resultSet.getString("session_status"));
        details.setExpiresAt(resultSet.getTimestamp("expires_at"));
        details.setConfirmedAt(resultSet.getTimestamp("confirmed_at"));
        details.setCancelledAt(resultSet.getTimestamp("cancelled_at"));
        details.setExpiredAt(resultSet.getTimestamp("expired_at"));
        details.setCreatedAt(resultSet.getTimestamp("created_at"));
        details.setTransactionId(resultSet.getInt("transaction_id"));
        details.setTransactionCode(resultSet.getString("transaction_code"));
        details.setTransactionStatus(resultSet.getString("transaction_status"));
        details.setProcessedAt(resultSet.getTimestamp("processed_at"));
        details.setCurrencyPackageId(resultSet.getInt("currency_package_id"));
        details.setPackageName(resultSet.getString("package_name"));
        details.setCurrencyName(resultSet.getString("currency_name"));
        details.setCurrencyAmount(resultSet.getInt("currency_amount"));
        details.setGameId(resultSet.getInt("game_id"));
        details.setGameTitle(resultSet.getString("game_title"));
        details.setGameDescription(resultSet.getString("game_description"));
        details.setGameGenre(resultSet.getString("game_genre"));
        return details;
    }
}
