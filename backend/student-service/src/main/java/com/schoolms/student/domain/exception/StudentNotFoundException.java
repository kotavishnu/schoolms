package com.schoolms.student.domain.exception;

/**
 * Exception thrown when a student is not found (BE-011)
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String studentId) {
        super(String.format("Student with ID '%s' not found", studentId));
    }
}
