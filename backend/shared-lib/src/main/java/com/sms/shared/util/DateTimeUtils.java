package com.sms.shared.util;

import java.time.LocalDate;
import java.time.Period;

/**
 * Utility class for date and time operations.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Calculate age from date of birth.
     *
     * @param dateOfBirth the date of birth
     * @return age in years
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if age is within the specified range (inclusive).
     *
     * @param dateOfBirth the date of birth
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return true if age is within range, false otherwise
     */
    public static boolean isAgeInRange(LocalDate dateOfBirth, int minAge, int maxAge) {
        int age = calculateAge(dateOfBirth);
        return age >= minAge && age <= maxAge;
    }
}
