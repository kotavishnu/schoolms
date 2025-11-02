package com.school.management.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 *
 * This exception is typically thrown when unique constraints are violated
 * (e.g., duplicate mobile number, duplicate receipt number, etc.)
 *
 * HTTP Status: 409 CONFLICT
 *
 * @author School Management Team
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with specified message
     * @param message Exception message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with message and cause
     * @param message Exception message
     * @param cause The cause of the exception
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience constructor for duplicate resource
     * @param resourceName Name of the resource (e.g., "Student")
     * @param fieldName Name of the unique field (e.g., "mobile")
     * @param fieldValue Value of the field
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
