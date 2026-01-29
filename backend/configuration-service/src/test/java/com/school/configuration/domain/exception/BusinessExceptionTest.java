package com.school.configuration.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    private static class TestBusinessException extends BusinessException {
        public TestBusinessException(String message) {
            super(message);
        }

        public TestBusinessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Test
    @DisplayName("Should create BusinessException with message")
    void shouldCreateBusinessExceptionWithMessage() {
        // Given
        String message = "Test business exception";

        // When
        BusinessException exception = new TestBusinessException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should create BusinessException with message and cause")
    void shouldCreateBusinessExceptionWithMessageAndCause() {
        // Given
        String message = "Test business exception";
        Throwable cause = new RuntimeException("Root cause");

        // When
        BusinessException exception = new TestBusinessException(message, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        // When
        BusinessException exception = new TestBusinessException("Test");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
