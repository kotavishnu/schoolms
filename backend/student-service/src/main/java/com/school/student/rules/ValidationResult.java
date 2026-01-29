package com.school.student.rules;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to hold validation results from Drools rules
 * Accumulates error messages during rule execution
 */
@Getter
public class ValidationResult {

    private final List<String> errors = new ArrayList<>();

    /**
     * Add a validation error
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * Check if validation passed
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Check if validation failed
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
               "valid=" + isValid() +
               ", errors=" + errors +
               '}';
    }
}
