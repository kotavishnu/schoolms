package com.school.management.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 *
 * This exception is typically thrown by service layer when repository
 * fails to find an entity by ID or other unique identifier.
 *
 * HTTP Status: 404 NOT_FOUND
 *
 * @author School Management Team
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with specified message
     * @param message Exception message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with message and cause
     * @param message Exception message
     * @param cause The cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience constructor for resource not found by ID
     * @param resourceName Name of the resource (e.g., "Student")
     * @param fieldName Name of the field (e.g., "id")
     * @param fieldValue Value of the field
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
