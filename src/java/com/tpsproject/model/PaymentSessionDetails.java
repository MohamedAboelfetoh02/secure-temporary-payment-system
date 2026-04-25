package com.tpsproject.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PaymentSessionDetails {

    private int purchaseRequestId;
    private int paymentSessionId;
    private int transactionId;
    private int userId;
    private String username;
    private int currencyPackageId;
    private int gameId;
    private String gameTitle;
    private String gameDescription;
    private String gameGenre;
    private String gameImagePath;
    private String gameOfficialUrl;
    private String packageName;
    private String currencyName;
    private int currencyAmount;
    private BigDecimal amount;
    private String referenceCode;
    private String paymentMethod;
    private String paymentDetailSummary;
    private String transactionCode;
    private String requestStatus;
    private String sessionStatus;
    private String transactionStatus;
    private Timestamp expiresAt;
    private Timestamp confirmedAt;
    private Timestamp cancelledAt;
    private Timestamp expiredAt;
    private Timestamp createdAt;
    private Timestamp processedAt;
    private long secondsRemaining;

    public int getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public int getPaymentSessionId() {
        return paymentSessionId;
    }

    public void setPaymentSessionId(int paymentSessionId) {
        this.paymentSessionId = paymentSessionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrencyPackageId() {
        return currencyPackageId;
    }

    public void setCurrencyPackageId(int currencyPackageId) {
        this.currencyPackageId = currencyPackageId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public String getGameGenre() {
        return gameGenre;
    }

    public void setGameGenre(String gameGenre) {
        this.gameGenre = gameGenre;
    }

    public String getGameImagePath() {
        return gameImagePath;
    }

    public void setGameImagePath(String gameImagePath) {
        this.gameImagePath = gameImagePath;
    }

    public String getGameOfficialUrl() {
        return gameOfficialUrl;
    }

    public void setGameOfficialUrl(String gameOfficialUrl) {
        this.gameOfficialUrl = gameOfficialUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(int currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetailSummary() {
        return paymentDetailSummary;
    }

    public void setPaymentDetailSummary(String paymentDetailSummary) {
        this.paymentDetailSummary = paymentDetailSummary;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Timestamp getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Timestamp confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Timestamp getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Timestamp cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(long secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public boolean isPending() {
        return "PENDING".equals(sessionStatus);
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equals(sessionStatus);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(sessionStatus);
    }

    public boolean isExpired() {
        return "EXPIRED".equals(sessionStatus);
    }
}
