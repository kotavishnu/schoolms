package com.school.sms.configuration.domain.exception;

/**
 * Base exception for domain layer errors.
 *
 * <p>All domain-specific exceptions should extend this class.
 * This provides a common foundation for exception handling across
 * the domain layer.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public abstract class DomainException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new domain exception with the specified detail message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new domain exception with the specified detail message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause
     */
    protected DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
