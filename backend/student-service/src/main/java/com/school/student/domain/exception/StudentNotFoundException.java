package com.school.student.domain.exception;

/**
 * Thrown when a requested student cannot be found
 * Maps to HTTP 404 Not Found
 */
public class StudentNotFoundException extends BusinessException {

    public StudentNotFoundException(String studentId) {
        super(String.format("Student not found: %s", studentId));
    }

    public StudentNotFoundException(Long id) {
        super(String.format("Student not found with ID: %d", id));
    }
}
