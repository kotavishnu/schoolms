package com.school.management.shared.exception;

import lombok.Getter;

/**
 * Base exception class for all custom exceptions in the School Management System.
 *
 * <p>This class provides common properties and behavior for application-specific exceptions,
 * including error codes, messages, and HTTP status codes for API responses.</p>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * Unique error code for categorizing exceptions.
     */
    private final String errorCode;

    /**
     * HTTP status code to be returned in API responses.
     */
    private final int httpStatusCode;

    /**
     * Constructs a new base exception with the specified detail message and error code.
     *
     * @param message the detail message explaining the exception
     * @param errorCode unique code identifying the error type
     * @param httpStatusCode HTTP status code for API responses
     */
    protected BaseException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Constructs a new base exception with the specified detail message, error code, and cause.
     *
     * @param message the detail message explaining the exception
     * @param errorCode unique code identifying the error type
     * @param httpStatusCode HTTP status code for API responses
     * @param cause the cause of this exception
     */
    protected BaseException(String message, String errorCode, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
}
