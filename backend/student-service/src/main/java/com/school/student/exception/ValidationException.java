package com.school.student.exception;

import com.school.student.domain.validation.ValidationResult;
import lombok.Getter;

import java.util.List;

/**
 * Validation Exception
 *
 * Thrown when business rule validation fails.
 * Contains list of validation errors from Drools engine.
 * Mapped to HTTP 400 Bad Request by GlobalExceptionHandler.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final List<ValidationResult.ValidationError> errors;

    public ValidationException(String message, List<ValidationResult.ValidationError> errors) {
        super(message);
        this.errors = errors;
    }
}
