package com.school.student.domain.validation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation Result
 *
 * Collects validation errors from Drools rules engine.
 * Used to aggregate all validation failures before
 * throwing a ValidationException.
 */
@Data
public class ValidationResult {

    private List<ValidationError> errors = new ArrayList<>();

    /**
     * Add a validation error
     *
     * @param field Field name that failed validation
     * @param code Error code (e.g., "INVALID_AGE")
     * @param message Human-readable error message
     */
    public void addError(String field, String code, String message) {
        errors.add(new ValidationError(field, code, message));
    }

    /**
     * Check if validation passed (no errors)
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Get error count
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Clear all errors
     */
    public void clear() {
        errors.clear();
    }

    /**
     * Validation Error Class
     */
    @Data
    public static class ValidationError {
        private final String field;
        private final String code;
        private final String message;

        public ValidationError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
    }
}
