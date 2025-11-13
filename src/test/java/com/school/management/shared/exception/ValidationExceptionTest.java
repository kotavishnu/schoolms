package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ValidationException.
 */
@DisplayName("ValidationException Tests")
class ValidationExceptionTest {

    @Test
    @DisplayName("Should create ValidationException with message only")
    void shouldCreateValidationException_WhenMessageProvided() {
        // Arrange
        String message = "Validation failed";

        // Act
        ValidationException exception = new ValidationException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
        assertThat(exception.getFieldErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should create ValidationException with message and field errors")
    void shouldCreateValidationException_WhenMessageAndFieldErrorsProvided() {
        // Arrange
        String message = "Multiple validation errors";
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("firstName", "First name is required");
        fieldErrors.put("age", "Age must be between 3 and 18");

        // Act
        ValidationException exception = new ValidationException(message, fieldErrors);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getFieldErrors()).hasSize(2);
        assertThat(exception.getFieldErrors()).containsEntry("firstName", "First name is required");
        assertThat(exception.getFieldErrors()).containsEntry("age", "Age must be between 3 and 18");
    }

    @Test
    @DisplayName("Should create ValidationException with single field error")
    void shouldCreateValidationException_WhenSingleFieldErrorProvided() {
        // Arrange
        String field = "email";
        String errorMessage = "Invalid email format";

        // Act
        ValidationException exception = new ValidationException(field, errorMessage);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(field);
        assertThat(exception.getMessage()).contains(errorMessage);
        assertThat(exception.getFieldErrors()).hasSize(1);
        assertThat(exception.getFieldErrors()).containsEntry(field, errorMessage);
    }

    @Test
    @DisplayName("Should handle empty field errors map")
    void shouldHandleEmptyFieldErrors() {
        // Arrange
        String message = "Validation error";
        Map<String, String> emptyFieldErrors = new HashMap<>();

        // Act
        ValidationException exception = new ValidationException(message, emptyFieldErrors);

        // Assert
        assertThat(exception.getFieldErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        ValidationException exception = new ValidationException("Test");

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
