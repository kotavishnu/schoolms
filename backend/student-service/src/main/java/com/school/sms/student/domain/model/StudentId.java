package com.school.sms.student.domain.model;

import lombok.Value;

import java.util.Objects;

/**
 * Value object representing a unique Student ID.
 *
 * <p>Format: STD-YYYYMMDD-NNNN (e.g., STD-20241206-0001)</p>
 * <p>This is immutable and validates the format.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Value
public class StudentId {

    String value;

    /**
     * Private constructor to enforce factory method usage.
     *
     * @param value the student ID string
     */
    private StudentId(String value) {
        Objects.requireNonNull(value, "Student ID cannot be null");
        if (!isValidFormat(value)) {
            throw new IllegalArgumentException(
                "Invalid Student ID format. Expected: STD-YYYYMMDD-NNNN, got: " + value
            );
        }
        this.value = value;
    }

    /**
     * Factory method to create a StudentId instance.
     *
     * @param value the student ID string
     * @return a new StudentId instance
     * @throws IllegalArgumentException if format is invalid
     */
    public static StudentId of(String value) {
        return new StudentId(value);
    }

    /**
     * Validates the Student ID format.
     *
     * @param value the student ID to validate
     * @return true if valid, false otherwise
     */
    private static boolean isValidFormat(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        // Format: STD-YYYYMMDD-NNNN (e.g., STD-20241206-0001)
        return value.matches("^STD-\\d{8}-\\d{4}$");
    }

    @Override
    public String toString() {
        return value;
    }
}
