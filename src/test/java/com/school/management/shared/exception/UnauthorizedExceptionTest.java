package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UnauthorizedException.
 */
@DisplayName("UnauthorizedException Tests")
class UnauthorizedExceptionTest {

    @Test
    @DisplayName("Should create UnauthorizedException with message only")
    void shouldCreateUnauthorizedException_WhenMessageProvided() {
        // Arrange
        String message = "Authentication required";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("UNAUTHORIZED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should create UnauthorizedException with message and error code")
    void shouldCreateUnauthorizedException_WhenMessageAndErrorCodeProvided() {
        // Arrange
        String message = "Invalid credentials";
        String errorCode = "INVALID_CREDENTIALS";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message, errorCode);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should create UnauthorizedException with default message")
    void shouldCreateUnauthorizedException_WhenNoMessageProvided() {
        // Arrange & Act
        UnauthorizedException exception = new UnauthorizedException();

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Unauthorized access");
        assertThat(exception.getErrorCode()).isEqualTo("UNAUTHORIZED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should create UnauthorizedException with message and cause")
    void shouldCreateUnauthorizedException_WhenMessageAndCauseProvided() {
        // Arrange
        String message = "Token validation failed";
        Throwable cause = new RuntimeException("JWT expired");

        // Act
        UnauthorizedException exception = new UnauthorizedException(message, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        UnauthorizedException exception = new UnauthorizedException();

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
