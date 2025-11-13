package com.school.management.shared.exception;

/**
 * Exception thrown when a business rule violation occurs.
 *
 * <p>This exception represents errors related to business logic violations,
 * such as invalid operations, constraint violations, or rule failures.</p>
 *
 * <p>HTTP Status Code: 400 (Bad Request)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Student age not within allowed range (3-18)</li>
 *   <li>Class capacity exceeded</li>
 *   <li>Invalid fee calculation</li>
 *   <li>Payment amount exceeds due amount</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
public class BusinessException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "BUSINESS_ERROR";
    private static final int HTTP_STATUS_CODE = 400;

    /**
     * Constructs a new business exception with the specified message.
     *
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new business exception with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HTTP_STATUS_CODE);
    }

    /**
     * Constructs a new business exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE, cause);
    }

    /**
     * Constructs a new business exception with the specified message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HTTP_STATUS_CODE, cause);
    }
}
