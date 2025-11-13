package com.school.management.shared.exception;

/**
 * Exception thrown when authentication fails or is required.
 *
 * <p>This exception represents errors related to authentication failures,
 * missing credentials, or invalid tokens.</p>
 *
 * <p>HTTP Status Code: 401 (Unauthorized)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Invalid username or password</li>
 *   <li>JWT token expired</li>
 *   <li>JWT token invalid</li>
 *   <li>Authentication credentials missing</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public class UnauthorizedException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "UNAUTHORIZED";
    private static final int HTTP_STATUS_CODE = 401;
    private static final String DEFAULT_MESSAGE = "Unauthorized access";

    /**
     * Constructs a new unauthorized exception with default message.
     */
    public UnauthorizedException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new unauthorized exception with the specified message.
     *
     * @param message the detail message
     */
    public UnauthorizedException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new unauthorized exception with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new unauthorized exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE, cause);
    }

    /**
     * Constructs a new unauthorized exception with the specified message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause
     */
    public UnauthorizedException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HTTP_STATUS_CODE, cause);
    }
}
