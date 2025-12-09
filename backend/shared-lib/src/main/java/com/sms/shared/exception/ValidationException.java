package com.sms.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when validation fails.
 * Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value());
    }

    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value(), cause);
    }

    public ValidationException(String field, String rejectedValue, String reason) {
        super(
            String.format("Validation failed for field '%s' with value '%s': %s",
                field, rejectedValue, reason),
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value()
        );
    }
}
