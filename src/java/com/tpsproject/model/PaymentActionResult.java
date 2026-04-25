package com.tpsproject.model;

public class PaymentActionResult {

    private PaymentSessionDetails paymentSessionDetails;
    private String messageType;
    private String message;

    public PaymentSessionDetails getPaymentSessionDetails() {
        return paymentSessionDetails;
    }

    public void setPaymentSessionDetails(PaymentSessionDetails paymentSessionDetails) {
        this.paymentSessionDetails = paymentSessionDetails;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
