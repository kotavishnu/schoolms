package com.school.student.common.exception;

import com.school.student.common.validation.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ValidationException.
 * Tests exception creation with validation results.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 */
@DisplayName("ValidationException Unit Tests")
class ValidationExceptionTest {

    // ==================== Construction Tests ====================

    @Test
    @DisplayName("Should create ValidationException with validation result")
    void shouldCreateValidationExceptionWithValidationResult() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("mobile", "Mobile already exists", "MOBILE_DUPLICATE");

        // Act
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getValidationResult()).isEqualTo(result);
        assertThat(exception.getMessage()).contains("Validation failed");
    }

    @Test
    @DisplayName("Should create ValidationException with custom message and result")
    void shouldCreateValidationExceptionWithCustomMessageAndResult() {
        // Arrange
        String message = "Custom validation error";
        ValidationResult result = new ValidationResult();
        result.addError("field", "error", "CODE");

        // Act
        ValidationException exception = new ValidationException(message, result);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getValidationResult()).isEqualTo(result);
    }

    // ==================== Get Validation Result Tests ====================

    @Test
    @DisplayName("Should return validation result")
    void shouldReturnValidationResult() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("dateOfBirth", "Age out of range", "AGE_OUT_OF_RANGE");
        result.addError("mobile", "Mobile required", "REQUIRED_FIELD");

        ValidationException exception = new ValidationException(result);

        // Act
        ValidationResult retrievedResult = exception.getValidationResult();

        // Assert
        assertThat(retrievedResult).isNotNull();
        assertThat(retrievedResult.getErrorCount()).isEqualTo(2);
        assertThat(retrievedResult.isValid()).isFalse();
    }

    // ==================== Message Tests ====================

    @Test
    @DisplayName("Should include error count in message")
    void shouldIncludeErrorCountInMessage() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("field1", "Error 1", "CODE1");
        result.addError("field2", "Error 2", "CODE2");
        result.addError("field3", "Error 3", "CODE3");

        // Act
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception.getMessage()).contains("3");
    }

    @Test
    @DisplayName("Should have meaningful message")
    void shouldHaveMeaningfulMessage() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("mobile", "Mobile already exists", "MOBILE_DUPLICATE");

        // Act
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception.getMessage())
            .isNotEmpty()
            .contains("Validation");
    }

    // ==================== Exception Hierarchy Tests ====================

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        // Arrange
        ValidationResult result = new ValidationResult();
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("mobile", "Mobile required", "REQUIRED_FIELD");

        // Act & Assert
        assertThatThrownBy(() -> {
            throw new ValidationException(result);
        }).isInstanceOf(ValidationException.class)
          .hasMessageContaining("Validation");
    }

    // ==================== Edge Cases Tests ====================

    @Test
    @DisplayName("Should handle empty validation result")
    void shouldHandleEmptyValidationResult() {
        // Arrange
        ValidationResult result = new ValidationResult();

        // Act
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getValidationResult().getErrorCount()).isZero();
    }

    @Test
    @DisplayName("Should preserve stack trace")
    void shouldPreserveStackTrace() {
        // Arrange
        ValidationResult result = new ValidationResult();
        result.addError("field", "error", "CODE");

        // Act
        ValidationException exception = new ValidationException(result);

        // Assert
        assertThat(exception.getStackTrace()).isNotEmpty();
    }
}
