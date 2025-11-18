package com.school.sms.student.domain.exception;

/**
 * Exception thrown when a student's age is invalid (not between 3-18 years).
 */
public class InvalidAgeException extends RuntimeException {

    public InvalidAgeException(String message) {
        super(message);
    }

    public InvalidAgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
