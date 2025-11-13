package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NotFoundException.
 */
@DisplayName("NotFoundException Tests")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Should create NotFoundException with resource type and ID")
    void shouldCreateNotFoundException_WhenResourceTypeAndIdProvided() {
        // Arrange
        String resourceType = "Student";
        Long id = 123L;

        // Act
        NotFoundException exception = new NotFoundException(resourceType, id);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(resourceType);
        assertThat(exception.getMessage()).contains(id.toString());
        assertThat(exception.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception.getResourceType()).isEqualTo(resourceType);
        assertThat(exception.getResourceId()).isEqualTo(id.toString());
    }

    @Test
    @DisplayName("Should create NotFoundException with resource type and String ID")
    void shouldCreateNotFoundException_WhenResourceTypeAndStringIdProvided() {
        // Arrange
        String resourceType = "Class";
        String id = "CLASS-2024-A";

        // Act
        NotFoundException exception = new NotFoundException(resourceType, id);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(resourceType);
        assertThat(exception.getMessage()).contains(id);
        assertThat(exception.getResourceType()).isEqualTo(resourceType);
        assertThat(exception.getResourceId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should create NotFoundException with custom message")
    void shouldCreateNotFoundException_WhenCustomMessageProvided() {
        // Arrange
        String message = "The requested student record could not be found";

        // Act
        NotFoundException exception = new NotFoundException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should create NotFoundException with message and error code")
    void shouldCreateNotFoundException_WhenMessageAndErrorCodeProvided() {
        // Arrange
        String message = "Fee structure not found";
        String errorCode = "FEE_NOT_FOUND";

        // Act
        NotFoundException exception = new NotFoundException(message, errorCode);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        NotFoundException exception = new NotFoundException("Test");

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
