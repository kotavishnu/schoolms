package com.school.student.domain.exception;

/**
 * Base class for all business domain exceptions
 * Extends RuntimeException for unchecked exception handling
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
