package com.school.sms.student.domain.model;

import lombok.Value;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Value object representing a Student ID.
 * Format: STU-YYYY-XXXXX (e.g., STU-2024-00001)
 * Immutable and validated.
 */
@Value
public class StudentId implements Serializable {

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU-\\d{4}-\\d{5}$");

    String value;

    private StudentId(String value) {
        this.value = value;
    }

    /**
     * Factory method to create a StudentId with validation.
     *
     * @param value the student ID string
     * @return validated StudentId instance
     * @throws IllegalArgumentException if value is invalid
     */
    public static StudentId of(String value) {
        validateStudentId(value);
        return new StudentId(value);
    }

    private static void validateStudentId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("StudentId cannot be null or blank");
        }

        if (!STUDENT_ID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid StudentId format: %s. Expected format: STU-YYYY-XXXXX", value)
            );
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
