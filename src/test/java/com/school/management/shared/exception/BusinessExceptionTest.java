package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BusinessException.
 */
@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create BusinessException with message only")
    void shouldCreateBusinessException_WhenMessageProvided() {
        // Arrange
        String message = "Business rule violation occurred";

        // Act
        BusinessException exception = new BusinessException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create BusinessException with message and error code")
    void shouldCreateBusinessException_WhenMessageAndErrorCodeProvided() {
        // Arrange
        String message = "Invalid business operation";
        String errorCode = "BR-001";

        // Act
        BusinessException exception = new BusinessException(message, errorCode);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should create BusinessException with message and cause")
    void shouldCreateBusinessException_WhenMessageAndCauseProvided() {
        // Arrange
        String message = "Business operation failed";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        BusinessException exception = new BusinessException(message, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should create BusinessException with all parameters")
    void shouldCreateBusinessException_WhenAllParametersProvided() {
        // Arrange
        String message = "Complex business error";
        String errorCode = "BR-002";
        Throwable cause = new IllegalStateException("Invalid state");

        // Act
        BusinessException exception = new BusinessException(message, errorCode, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        BusinessException exception = new BusinessException("Test");

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
