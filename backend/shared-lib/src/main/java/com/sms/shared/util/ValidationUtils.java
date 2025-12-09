package com.sms.shared.util;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 */
public final class ValidationUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern ADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");

    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidAdhaar(String adhaar) {
        return adhaar != null && ADHAAR_PATTERN.matcher(adhaar).matches();
    }
}
