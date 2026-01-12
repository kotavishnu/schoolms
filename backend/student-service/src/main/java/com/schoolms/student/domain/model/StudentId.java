package com.schoolms.student.domain.model;

import java.util.Objects;

/**
 * StudentId Value Object (BE-009)
 * Type-safe student ID with validation
 * Format: STU-YYYY-NNNNN
 */
public class StudentId {
    private final String value;

    public StudentId(String value) {
        if (value == null || !value.matches("^STU-\\d{4}-\\d{5}$")) {
            throw new IllegalArgumentException(
                String.format("Invalid student ID format: '%s'. Expected format: STU-YYYY-NNNNN", value)
            );
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentId studentId = (StudentId) o;
        return Objects.equals(value, studentId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
