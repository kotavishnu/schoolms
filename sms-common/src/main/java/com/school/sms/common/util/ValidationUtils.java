package com.school.sms.common.util;

import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 */
public final class ValidationUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU-\\d{4}-\\d{5}$");

    private ValidationUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Validate mobile number format (10-15 digits).
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * Validate email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate StudentID format (STU-YYYY-XXXXX).
     */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    /**
     * Check if string is null or blank.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not blank.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
