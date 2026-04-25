package com.tpsproject.util;

import java.security.SecureRandom;

public final class ReferenceCodeUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceCodeUtil() {
    }

    public static String generateCode(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int index = 0; index < length; index++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(randomIndex));
        }

        return builder.toString();
    }

    public static String generateTransactionCode() {
        return "TXN" + generateCode(9);
    }
}
