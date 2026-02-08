package com.school.student.infrastructure.drools;

import java.time.LocalDate;
import java.time.Period;

/**
 * Helper class for Drools rules to perform age calculations.
 * Provides methods that can be called from DRL files without using eval().
 */
public class StudentAgeHelper {

    /**
     * Calculate age in years from date of birth.
     *
     * @param dateOfBirth the date of birth
     * @return age in years
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if date is in the future.
     *
     * @param date the date to check
     * @return true if date is in the future
     */
    public static boolean isInFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }

    /**
     * Check if age is within valid range (3-18 years).
     *
     * @param dateOfBirth the date of birth
     * @return true if age is valid
     */
    public static boolean isValidAge(LocalDate dateOfBirth) {
        int age = calculateAge(dateOfBirth);
        return age >= 3 && age <= 18;
    }
}
