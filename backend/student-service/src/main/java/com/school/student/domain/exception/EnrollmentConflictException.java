package com.school.student.domain.exception;

/**
 * Thrown when attempting to create duplicate enrollment for same academic year
 * Business rule: One enrollment per student per academic year
 * Maps to HTTP 409 Conflict
 */
public class EnrollmentConflictException extends BusinessException {

    public EnrollmentConflictException(Long studentId, String academicYear) {
        super(String.format("Student %d already has an enrollment for academic year %s",
            studentId, academicYear));
    }
}
