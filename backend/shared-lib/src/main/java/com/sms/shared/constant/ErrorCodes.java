package com.sms.shared.constant;

/**
 * Standard error codes used across the application.
 */
public final class ErrorCodes {

    private ErrorCodes() {
        throw new IllegalStateException("Constant class");
    }

    // Validation errors (400)
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String REQUIRED_FIELD = "REQUIRED_FIELD";

    // Not found errors (404)
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";

    // Conflict errors (409)
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String DUPLICATE_MOBILE = "DUPLICATE_MOBILE";
    public static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
    public static final String OPTIMISTIC_LOCK_ERROR = "OPTIMISTIC_LOCK_ERROR";

    // Business rule violations (422)
    public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final String AGE_OUT_OF_RANGE = "AGE_OUT_OF_RANGE";

    // Server errors (500)
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}
