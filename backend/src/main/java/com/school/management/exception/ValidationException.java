package com.school.management.exception;

/**
 * Exception thrown when business validation fails.
 *
 * This exception is thrown by service layer when business rules are violated
 * (e.g., duplicate mobile number, class capacity exceeded, etc.)
 *
 * HTTP Status: 400 BAD_REQUEST
 *
 * @author School Management Team
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new ValidationException with specified message
     * @param message Validation error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with message and cause
     * @param message Validation error message
     * @param cause The cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
