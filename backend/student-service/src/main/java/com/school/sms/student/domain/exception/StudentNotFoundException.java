package com.school.sms.student.domain.exception;

/**
 * Exception thrown when a requested student is not found in the system.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
