package com.school.sms.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 */
public final class DateTimeUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Calculate age in years from date of birth.
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if date of birth falls within age range.
     */
    public static boolean isAgeInRange(LocalDate dateOfBirth, int minAge, int maxAge) {
        int age = calculateAge(dateOfBirth);
        return age >= minAge && age <= maxAge;
    }

    /**
     * Format LocalDate to string.
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Format LocalDateTime to string.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * Get current timestamp.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    /**
     * Get current date.
     */
    public static LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }
}
