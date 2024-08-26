package com.satyam.securecapita.infrastructure.constants;

import java.security.SecureRandom;

public class ApplicationUtil {
    private static final String DIGITS = "0123456789";

    public static String generateOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            otp.append(DIGITS.charAt(index));
        }

        return otp.toString();
    }
}
