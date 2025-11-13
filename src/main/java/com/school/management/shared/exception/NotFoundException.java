package com.school.management.shared.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception represents errors when attempting to access
 * a resource that does not exist in the system.</p>
 *
 * <p>HTTP Status Code: 404 (Not Found)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Student with given ID not found</li>
 *   <li>Class not found</li>
 *   <li>Fee structure not found</li>
 *   <li>User account not found</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Getter
public class NotFoundException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "NOT_FOUND";
    private static final int HTTP_STATUS_CODE = 404;

    /**
     * Type of resource that was not found (e.g., "Student", "Class").
     */
    private final String resourceType;

    /**
     * Identifier of the resource that was not found.
     */
    private final String resourceId;

    /**
     * Constructs a new not found exception with the specified message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * Constructs a new not found exception with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public NotFoundException(String message, String errorCode) {
        super(message, errorCode, HTTP_STATUS_CODE);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * Constructs a new not found exception with resource details.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param resourceType the resource type
     * @param resourceId the resource ID
     */
    private NotFoundException(String message, String errorCode, String resourceType, String resourceId) {
        super(message, errorCode, HTTP_STATUS_CODE);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Constructs a new not found exception for a specific resource type and ID.
     *
     * @param resourceType the type of resource (e.g., "Student", "Class")
     * @param id the resource identifier
     */
    public NotFoundException(String resourceType, Long id) {
        super(String.format("%s with ID %d not found", resourceType, id),
                DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.resourceType = resourceType;
        this.resourceId = id.toString();
    }

    /**
     * Constructs a new not found exception for a specific resource type and String ID.
     *
     * @param resourceType the type of resource (e.g., "Student", "Class")
     * @param id the resource identifier
     * @return a NotFoundException instance
     */
    public static NotFoundException forResource(String resourceType, String id) {
        return new NotFoundException(
                String.format("%s with ID '%s' not found", resourceType, id),
                DEFAULT_ERROR_CODE,
                resourceType,
                id);
    }

    /**
     * Constructs a new not found exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE, cause);
        this.resourceType = null;
        this.resourceId = null;
    }
}
