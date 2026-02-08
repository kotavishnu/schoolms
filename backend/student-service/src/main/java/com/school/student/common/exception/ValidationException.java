package com.school.student.common.exception;

import com.school.student.common.validation.ValidationResult;
import lombok.Getter;

/**
 * Exception thrown when business rule validation fails.
 * Contains the ValidationResult with all validation errors.
 *
 * <p>This exception should be caught by global exception handler
 * and converted to a 400 Bad Request response with error details.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        super("Validation failed with " + validationResult.getErrorCount() + " error(s)");
        this.validationResult = validationResult;
    }

    public ValidationException(String message, ValidationResult validationResult) {
        super(message);
        this.validationResult = validationResult;
    }
}
