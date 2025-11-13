package com.school.management.shared.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when validation fails.
 *
 * <p>This exception represents validation errors for input data,
 * including field-level validation errors and constraint violations.</p>
 *
 * <p>HTTP Status Code: 400 (Bad Request)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Required field missing</li>
 *   <li>Invalid email format</li>
 *   <li>Value out of range</li>
 *   <li>Invalid date format</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Getter
public class ValidationException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "VALIDATION_ERROR";
    private static final int HTTP_STATUS_CODE = 400;

    /**
     * Map of field names to their validation error messages.
     */
    private final Map<String, String> fieldErrors;

    /**
     * Constructs a new validation exception with the specified message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.fieldErrors = Collections.emptyMap();
    }

    /**
     * Constructs a new validation exception with the specified message and field errors.
     *
     * @param message the detail message
     * @param fieldErrors map of field names to error messages
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : Collections.emptyMap();
    }

    /**
     * Constructs a new validation exception with a single field error.
     *
     * @param field the field name
     * @param errorMessage the error message for the field
     */
    public ValidationException(String field, String errorMessage) {
        super(String.format("Validation failed for field '%s': %s", field, errorMessage),
                DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.fieldErrors = Map.of(field, errorMessage);
    }

    /**
     * Constructs a new validation exception with the specified message, error code, and field errors.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param fieldErrors map of field names to error messages
     */
    public ValidationException(String message, String errorCode, Map<String, String> fieldErrors) {
        super(message, errorCode, HTTP_STATUS_CODE);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : Collections.emptyMap();
    }
}
