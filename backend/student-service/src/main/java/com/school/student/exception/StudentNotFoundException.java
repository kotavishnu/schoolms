package com.school.student.exception;

/**
 * Student Not Found Exception
 *
 * Thrown when a student lookup by studentId fails.
 * Mapped to HTTP 404 Not Found by GlobalExceptionHandler.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String studentId) {
        super("Student not found with ID: " + studentId);
    }
}
