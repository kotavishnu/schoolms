package com.school.student.domain.model.valueobject;

import lombok.Value;

import java.util.Objects;

/**
 * Value Object representing a mobile phone number.
 * Immutable and self-validating.
 * Enforces 10-digit Indian mobile number format.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@Value
public class Mobile {

    String number;

    /**
     * Factory method to create a Mobile value object.
     *
     * @param number the mobile number string (10 digits)
     * @return Mobile instance
     * @throws IllegalArgumentException if number is invalid
     */
    public static Mobile of(String number) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty");
        }

        String cleaned = number.trim();
        if (!cleaned.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile number must be exactly 10 digits");
        }

        return new Mobile(cleaned);
    }

    /**
     * Returns masked mobile number for display purposes.
     * Format: XXX****XXX
     *
     * @return masked mobile number
     */
    public String getMasked() {
        if (number == null || number.length() != 10) {
            return "**********";
        }
        return number.substring(0, 3) + "****" + number.substring(7);
    }

    @Override
    public String toString() {
        return getMasked();
    }
}
