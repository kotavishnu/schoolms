package com.schoolms.student.domain.exception;

/**
 * Exception thrown when student age is outside valid range (BE-011)
 */
public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String message) {
        super(message);
    }
}
