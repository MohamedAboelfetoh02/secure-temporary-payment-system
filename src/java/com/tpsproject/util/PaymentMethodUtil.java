package com.tpsproject.util;

import com.tpsproject.model.PaymentInputData;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class PaymentMethodUtil {

    private static final String[] PAYMENT_METHODS = {
        "PayPal",
        "Credit/Debit Card",
        "Game Wallet"
    };

    private PaymentMethodUtil() {
    }

    public static String[] getAvailableMethods() {
        return PAYMENT_METHODS.clone();
    }

    public static boolean isValid(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }

        for (String availableMethod : PAYMENT_METHODS) {
            if (availableMethod.equals(paymentMethod)) {
                return true;
            }
        }

        return false;
    }

    public static String validateAndBuildSummary(PaymentInputData paymentInputData) {
        if (paymentInputData == null || !isValid(paymentInputData.getPaymentMethod())) {
            throw new IllegalArgumentException("Select a valid payment method.");
        }

        switch (paymentInputData.getPaymentMethod()) {
            case "PayPal":
                return validatePayPal(paymentInputData.getPaypalEmail());
            case "Credit/Debit Card":
                return validateCard(
                        paymentInputData.getCardholderName(),
                        paymentInputData.getCardNumber(),
                        paymentInputData.getExpiryDate(),
                        paymentInputData.getCvv()
                );
            case "Game Wallet":
                return validateWallet(paymentInputData.getWalletId());
            default:
                throw new IllegalArgumentException("Select a valid payment method.");
        }
    }

    private static String validatePayPal(String paypalEmail) {
        String value = normalize(paypalEmail);
        if (value == null || !value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Enter a valid PayPal email address.");
        }

        return value;
    }

    private static String validateCard(String cardholderName, String cardNumber, String expiryDate, String cvv) {
        String safeName = normalize(cardholderName);
        String safeNumber = normalize(cardNumber);
        String safeExpiry = normalize(expiryDate);
        String safeCvv = normalize(cvv);

        if (safeName == null || safeName.length() < 3 || safeName.length() > 60) {
            throw new IllegalArgumentException("Enter the cardholder name exactly as it appears on the card.");
        }

        String digitsOnly = safeNumber == null ? null : safeNumber.replaceAll("\\s+", "");
        if (digitsOnly == null || !digitsOnly.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Enter a valid card number.");
        }

        if (safeExpiry == null || !safeExpiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("Enter the expiry date in MM/YY format.");
        }

        if (isExpired(safeExpiry)) {
            throw new IllegalArgumentException("The card expiry date must be in the future.");
        }

        if (safeCvv == null || !safeCvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("Enter a valid CVV.");
        }

        String lastFour = digitsOnly.substring(digitsOnly.length() - 4);
        return "Card ending in " + lastFour;
    }

    private static String validateWallet(String walletId) {
        String value = normalize(walletId);
        if (value == null || !value.matches("[A-Za-z0-9 _.-]{3,40}")) {
            throw new IllegalArgumentException("Enter a valid game wallet name or ID.");
        }

        return value;
    }

    private static boolean isExpired(String expiryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        try {
            YearMonth selectedMonth = YearMonth.parse(expiryDate, formatter);
            return selectedMonth.isBefore(YearMonth.now());
        } catch (DateTimeParseException ex) {
            return true;
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
