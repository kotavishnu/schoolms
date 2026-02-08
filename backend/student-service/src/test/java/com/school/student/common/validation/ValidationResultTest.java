package com.school.student.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ValidationResult.
 * Tests error aggregation and validation result construction.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 */
@DisplayName("ValidationResult Unit Tests")
class ValidationResultTest {

    private ValidationResult validationResult;

    @BeforeEach
    void setUp() {
        validationResult = new ValidationResult();
    }

    // ==================== Construction Tests ====================

    @Test
    @DisplayName("Should create empty ValidationResult")
    void shouldCreateEmptyValidationResult() {
        // Assert
        assertThat(validationResult).isNotNull();
        assertThat(validationResult.isValid()).isTrue();
        assertThat(validationResult.getErrors()).isEmpty();
        assertThat(validationResult.getErrorCount()).isZero();
    }

    // ==================== Add Error Tests ====================

    @Test
    @DisplayName("Should add single error")
    void shouldAddSingleError() {
        // Act
        validationResult.addError("dateOfBirth", "Age must be between 3 and 18", "AGE_OUT_OF_RANGE");

        // Assert
        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrorCount()).isEqualTo(1);
        assertThat(validationResult.getErrors()).hasSize(1);

        ValidationError error = validationResult.getErrors().get(0);
        assertThat(error.getField()).isEqualTo("dateOfBirth");
        assertThat(error.getMessage()).isEqualTo("Age must be between 3 and 18");
        assertThat(error.getCode()).isEqualTo("AGE_OUT_OF_RANGE");
    }

    @Test
    @DisplayName("Should add multiple errors")
    void shouldAddMultipleErrors() {
        // Act
        validationResult.addError("dateOfBirth", "Age must be between 3 and 18", "AGE_OUT_OF_RANGE");
        validationResult.addError("mobile", "Mobile number already exists", "MOBILE_DUPLICATE");
        validationResult.addError("firstName", "First name is required", "REQUIRED_FIELD");

        // Assert
        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrorCount()).isEqualTo(3);
        assertThat(validationResult.getErrors()).hasSize(3);
    }

    // ==================== Validation State Tests ====================

    @Test
    @DisplayName("Should be valid when no errors")
    void shouldBeValidWhenNoErrors() {
        // Assert
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    @DisplayName("Should be invalid when errors exist")
    void shouldBeInvalidWhenErrorsExist() {
        // Act
        validationResult.addError("mobile", "Mobile required", "REQUIRED_FIELD");

        // Assert
        assertThat(validationResult.isValid()).isFalse();
    }

    // ==================== Get Errors Tests ====================

    @Test
    @DisplayName("Should return immutable error list")
    void shouldReturnImmutableErrorList() {
        // Act
        validationResult.addError("mobile", "Mobile required", "REQUIRED_FIELD");
        List<ValidationError> errors = validationResult.getErrors();

        // Assert
        assertThat(errors).isNotNull();
        assertThatThrownBy(() -> errors.add(new ValidationError("field", "message", "code")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should return empty list when no errors")
    void shouldReturnEmptyListWhenNoErrors() {
        // Act
        List<ValidationError> errors = validationResult.getErrors();

        // Assert
        assertThat(errors).isEmpty();
    }

    // ==================== Error Count Tests ====================

    @Test
    @DisplayName("Should return correct error count")
    void shouldReturnCorrectErrorCount() {
        // Act
        validationResult.addError("field1", "Error 1", "CODE1");
        validationResult.addError("field2", "Error 2", "CODE2");

        // Assert
        assertThat(validationResult.getErrorCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return zero count when no errors")
    void shouldReturnZeroCountWhenNoErrors() {
        // Assert
        assertThat(validationResult.getErrorCount()).isZero();
    }

    // ==================== Clear Tests ====================

    @Test
    @DisplayName("Should clear all errors")
    void shouldClearAllErrors() {
        // Arrange
        validationResult.addError("field1", "Error 1", "CODE1");
        validationResult.addError("field2", "Error 2", "CODE2");

        // Act
        validationResult.clear();

        // Assert
        assertThat(validationResult.isValid()).isTrue();
        assertThat(validationResult.getErrorCount()).isZero();
        assertThat(validationResult.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should be reusable after clear")
    void shouldBeReusableAfterClear() {
        // Arrange
        validationResult.addError("field1", "Error 1", "CODE1");
        validationResult.clear();

        // Act
        validationResult.addError("field2", "Error 2", "CODE2");

        // Assert
        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrorCount()).isEqualTo(1);
    }

    // ==================== ToString Tests ====================

    @Test
    @DisplayName("Should return valid=true in toString when no errors")
    void shouldReturnValidTrueInToStringWhenNoErrors() {
        // Act
        String result = validationResult.toString();

        // Assert
        assertThat(result).contains("valid=true");
    }

    @Test
    @DisplayName("Should return error details in toString when errors exist")
    void shouldReturnErrorDetailsInToStringWhenErrorsExist() {
        // Arrange
        validationResult.addError("mobile", "Mobile required", "REQUIRED_FIELD");
        validationResult.addError("dateOfBirth", "Age invalid", "AGE_OUT_OF_RANGE");

        // Act
        String result = validationResult.toString();

        // Assert
        assertThat(result).contains("valid=false");
        assertThat(result).contains("errorCount=2");
        assertThat(result).contains("errors=");
    }

    // ==================== Edge Cases Tests ====================

    @Test
    @DisplayName("Should handle null field in error")
    void shouldHandleNullFieldInError() {
        // Act
        validationResult.addError(null, "Error message", "CODE");

        // Assert
        assertThat(validationResult.getErrorCount()).isEqualTo(1);
        ValidationError error = validationResult.getErrors().get(0);
        assertThat(error.getField()).isNull();
    }

    @Test
    @DisplayName("Should handle null message in error")
    void shouldHandleNullMessageInError() {
        // Act
        validationResult.addError("field", null, "CODE");

        // Assert
        assertThat(validationResult.getErrorCount()).isEqualTo(1);
        ValidationError error = validationResult.getErrors().get(0);
        assertThat(error.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should handle null code in error")
    void shouldHandleNullCodeInError() {
        // Act
        validationResult.addError("field", "message", null);

        // Assert
        assertThat(validationResult.getErrorCount()).isEqualTo(1);
        ValidationError error = validationResult.getErrors().get(0);
        assertThat(error.getCode()).isNull();
    }

    @Test
    @DisplayName("Should handle many errors efficiently")
    void shouldHandleManyErrorsEfficiently() {
        // Act
        for (int i = 0; i < 100; i++) {
            validationResult.addError("field" + i, "Error " + i, "CODE" + i);
        }

        // Assert
        assertThat(validationResult.getErrorCount()).isEqualTo(100);
        assertThat(validationResult.isValid()).isFalse();
    }
}
