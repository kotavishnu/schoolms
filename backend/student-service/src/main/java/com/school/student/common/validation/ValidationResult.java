package com.school.student.common.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds validation results from business rule execution.
 * Used by Drools rule engine to collect validation errors.
 *
 * <p>Usage Pattern:
 * <pre>
 * ValidationResult result = new ValidationResult();
 * // Insert into Drools session
 * kieSession.insert(result);
 * kieSession.insert(student);
 * kieSession.fireAllRules();
 * // Check results
 * if (!result.isValid()) {
 *     throw new ValidationException(result);
 * }
 * </pre>
 */
@Getter
public class ValidationResult {

    private final List<ValidationError> errors = new ArrayList<>();

    /**
     * Add a validation error.
     *
     * @param field the field that failed validation
     * @param message the error message
     * @param code the error code for programmatic handling
     */
    public void addError(String field, String message, String code) {
        errors.add(new ValidationError(field, message, code));
    }

    /**
     * Check if validation passed (no errors).
     *
     * @return true if no errors, false otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Get immutable list of errors.
     *
     * @return unmodifiable list of validation errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Get count of validation errors.
     *
     * @return number of errors
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Clear all errors (useful for reusing ValidationResult).
     */
    public void clear() {
        errors.clear();
    }

    @Override
    public String toString() {
        if (isValid()) {
            return "ValidationResult{valid=true}";
        }
        return "ValidationResult{valid=false, errorCount=" + errors.size() + ", errors=" + errors + "}";
    }
}
