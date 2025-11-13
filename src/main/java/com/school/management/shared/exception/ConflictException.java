package com.school.management.shared.exception;

/**
 * Exception thrown when a conflict occurs with existing data.
 *
 * <p>This exception represents errors when attempting to create or update
 * a resource that conflicts with existing data, typically due to unique
 * constraints or business rules.</p>
 *
 * <p>HTTP Status Code: 409 (Conflict)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Duplicate student code</li>
 *   <li>Mobile number already registered</li>
 *   <li>Email already in use</li>
 *   <li>Class code already exists</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public class ConflictException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "CONFLICT";
    private static final int HTTP_STATUS_CODE = 409;

    /**
     * Constructs a new conflict exception with the specified message.
     *
     * @param message the detail message
     */
    public ConflictException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new conflict exception with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public ConflictException(String message, String errorCode) {
        super(message, errorCode, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new conflict exception for a specific resource type and identifier.
     *
     * @param resourceType the type of resource (e.g., "Student Code", "Mobile Number")
     * @param identifier the conflicting identifier
     * @return a ConflictException instance
     */
    public static ConflictException forResource(String resourceType, String identifier) {
        return new ConflictException(
                String.format("%s '%s' already exists", resourceType, identifier),
                DEFAULT_ERROR_CODE);
    }

    /**
     * Constructs a new conflict exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ConflictException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE, cause);
    }

    /**
     * Constructs a new conflict exception with the specified message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause
     */
    public ConflictException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HTTP_STATUS_CODE, cause);
    }
}
