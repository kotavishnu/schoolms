package com.school.student.domain.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when Drools business rules validation fails
 * Contains list of all validation errors
 * Maps to HTTP 400 Bad Request
 */
public class BusinessRuleViolationException extends BusinessException {

    private final List<String> errors;

    public BusinessRuleViolationException(List<String> errors) {
        super("Business rule validation failed");
        this.errors = new ArrayList<>(errors);
    }

    public BusinessRuleViolationException(String error) {
        super(error);
        this.errors = List.of(error);
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
}
