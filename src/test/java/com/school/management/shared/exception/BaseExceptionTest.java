package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BaseException class.
 *
 * <p>Tests verify that custom exception properties are correctly initialized
 * and accessible through getters.</p>
 */
@DisplayName("BaseException Tests")
class BaseExceptionTest {

    /**
     * Test exception implementation for testing purposes.
     */
    static class TestException extends BaseException {
        public TestException(String message, String errorCode, int httpStatusCode) {
            super(message, errorCode, httpStatusCode);
        }

        public TestException(String message, String errorCode, int httpStatusCode, Throwable cause) {
            super(message, errorCode, httpStatusCode, cause);
        }
    }

    @Test
    @DisplayName("Should create exception with message and error code")
    void shouldCreateException_WithMessageAndErrorCode() {
        // Arrange
        String message = "Test exception message";
        String errorCode = "TEST_001";
        int httpStatusCode = 400;

        // Act
        TestException exception = new TestException(message, errorCode, httpStatusCode);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(httpStatusCode);
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateException_WithCause() {
        // Arrange
        String message = "Test exception with cause";
        String errorCode = "TEST_002";
        int httpStatusCode = 500;
        Throwable cause = new IllegalArgumentException("Root cause");

        // Act
        TestException exception = new TestException(message, errorCode, httpStatusCode, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(httpStatusCode);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Root cause");
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        // Arrange & Act
        TestException exception = new TestException("Test", "TEST_003", 400);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
