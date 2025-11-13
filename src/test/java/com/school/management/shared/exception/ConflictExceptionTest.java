package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ConflictException.
 */
@DisplayName("ConflictException Tests")
class ConflictExceptionTest {

    @Test
    @DisplayName("Should create ConflictException with message only")
    void shouldCreateConflictException_WhenMessageProvided() {
        // Arrange
        String message = "Resource already exists";

        // Act
        ConflictException exception = new ConflictException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("CONFLICT");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should create ConflictException with message and error code")
    void shouldCreateConflictException_WhenMessageAndErrorCodeProvided() {
        // Arrange
        String message = "Student code already exists";
        String errorCode = "STUDENT_CODE_CONFLICT";

        // Act
        ConflictException exception = new ConflictException(message, errorCode);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should create ConflictException with resource type and identifier")
    void shouldCreateConflictException_WhenResourceTypeAndIdentifierProvided() {
        // Arrange
        String resourceType = "Mobile Number";
        String identifier = "+919876543210";

        // Act
        ConflictException exception = new ConflictException(resourceType, identifier);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(resourceType);
        assertThat(exception.getMessage()).contains(identifier);
        assertThat(exception.getErrorCode()).isEqualTo("CONFLICT");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should create ConflictException with message and cause")
    void shouldCreateConflictException_WhenMessageAndCauseProvided() {
        // Arrange
        String message = "Database constraint violation";
        Throwable cause = new RuntimeException("Unique constraint failed");

        // Act
        ConflictException exception = new ConflictException(message, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("CONFLICT");
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        ConflictException exception = new ConflictException("Test");

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
