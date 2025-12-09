package com.sms.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String resourceType, String field, String value) {
        super(
            String.format("%s with %s '%s' already exists", resourceType, field, value),
            "DUPLICATE_RESOURCE",
            HttpStatus.CONFLICT.value()
        );
    }

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE", HttpStatus.CONFLICT.value());
    }
}
