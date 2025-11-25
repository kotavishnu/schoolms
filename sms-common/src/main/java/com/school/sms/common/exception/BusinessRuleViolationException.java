package com.school.sms.common.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when business rules are violated.
 */
public class BusinessRuleViolationException extends RuntimeException {

    private final List<String> violations;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.violations = new ArrayList<>();
        this.violations.add(message);
    }

    public BusinessRuleViolationException(List<String> violations) {
        super(String.join("; ", violations));
        this.violations = new ArrayList<>(violations);
    }

    public BusinessRuleViolationException(String message, List<String> violations) {
        super(message);
        this.violations = new ArrayList<>(violations);
    }

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }
}
