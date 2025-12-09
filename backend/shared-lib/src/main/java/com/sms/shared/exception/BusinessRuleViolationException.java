package com.sms.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule is violated.
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleViolationException extends BaseException {

    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.UNPROCESSABLE_ENTITY.value(), cause);
    }

    public BusinessRuleViolationException(String ruleName, String details) {
        super(
            String.format("Business rule violation: %s - %s", ruleName, details),
            "BUSINESS_RULE_VIOLATION",
            HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }
}
