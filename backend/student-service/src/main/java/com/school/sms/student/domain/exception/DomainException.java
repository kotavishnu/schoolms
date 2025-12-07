package com.school.sms.student.domain.exception;

/**
 * Base exception for all domain-related exceptions.
 *
 * <p>All custom domain exceptions should extend this class.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public abstract class DomainException extends RuntimeException {

    private final String errorCode;

    /**
     * Creates a DomainException with a message and error code.
     *
     * @param message the exception message
     * @param errorCode the error code
     */
    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates a DomainException with a message, error code, and cause.
     *
     * @param message the exception message
     * @param errorCode the error code
     * @param cause the underlying cause
     */
    protected DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
