package com.school.management.presentation.exception;

import com.school.management.shared.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Should handle BusinessException and return 400 status")
    void shouldHandleBusinessException_WhenBusinessRuleViolated() {
        // Arrange
        BusinessException exception = new BusinessException("Student age must be between 3 and 18");

        // Act
        ProblemDetail response = exceptionHandler.handleBusinessException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getTitle()).isEqualTo("Business Rule Violation");
        assertThat(response.getDetail()).isEqualTo("Student age must be between 3 and 18");
        assertThat(response.getProperties()).containsKey("errorCode");
        assertThat(response.getProperties().get("errorCode")).isEqualTo("BUSINESS_ERROR");
        assertThat(response.getProperties()).containsKey("timestamp");
    }

    @Test
    @DisplayName("Should handle ValidationException with field errors and return 400 status")
    void shouldHandleValidationException_WhenValidationFails() {
        // Arrange
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("firstName", "First name is required");
        fieldErrors.put("age", "Age must be between 3 and 18");
        ValidationException exception = new ValidationException("Validation failed", fieldErrors);

        // Act
        ProblemDetail response = exceptionHandler.handleValidationException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getTitle()).isEqualTo("Validation Failed");
        assertThat(response.getDetail()).isEqualTo("Validation failed");
        assertThat(response.getProperties()).containsKey("fieldErrors");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getProperties().get("fieldErrors");
        assertThat(errors).hasSize(2);
        assertThat(errors).containsEntry("firstName", "First name is required");
    }

    @Test
    @DisplayName("Should handle NotFoundException and return 404 status")
    void shouldHandleNotFoundException_WhenResourceNotFound() {
        // Arrange
        NotFoundException exception = new NotFoundException("Student", 123L);

        // Act
        ProblemDetail response = exceptionHandler.handleNotFoundException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getTitle()).isEqualTo("Resource Not Found");
        assertThat(response.getDetail()).contains("Student");
        assertThat(response.getDetail()).contains("123");
        assertThat(response.getProperties()).containsKey("errorCode");
    }

    @Test
    @DisplayName("Should handle ConflictException and return 409 status")
    void shouldHandleConflictException_WhenConflictOccurs() {
        // Arrange
        ConflictException exception = new ConflictException("Student Code", "STU-2024-001");

        // Act
        ProblemDetail response = exceptionHandler.handleConflictException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getTitle()).isEqualTo("Conflict");
        assertThat(response.getDetail()).contains("Student Code");
        assertThat(response.getDetail()).contains("STU-2024-001");
    }

    @Test
    @DisplayName("Should handle UnauthorizedException and return 401 status")
    void shouldHandleUnauthorizedException_WhenAuthenticationFails() {
        // Arrange
        UnauthorizedException exception = new UnauthorizedException("Invalid credentials");

        // Act
        ProblemDetail response = exceptionHandler.handleUnauthorizedException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getTitle()).isEqualTo("Unauthorized");
        assertThat(response.getDetail()).isEqualTo("Invalid credentials");
    }

    @Test
    @DisplayName("Should handle AccountLockedException and return 423 status with lockout info")
    void shouldHandleAccountLockedException_WhenAccountLocked() {
        // Arrange
        LocalDateTime lockoutTime = LocalDateTime.now().plusMinutes(30);
        AccountLockedException exception = new AccountLockedException("test@example.com", lockoutTime, 5);

        // Act
        ProblemDetail response = exceptionHandler.handleAccountLockedException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(423);
        assertThat(response.getTitle()).isEqualTo("Account Locked");
        assertThat(response.getDetail()).contains("test@example.com");
        assertThat(response.getProperties()).containsKey("lockoutEndTime");
        assertThat(response.getProperties()).containsKey("remainingMinutes");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return field errors")
    void shouldHandleMethodArgumentNotValidException_WhenBeanValidationFails() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("student", "firstName", "must not be blank");
        FieldError fieldError2 = new FieldError("student", "age", "must be greater than or equal to 3");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Act
        ProblemDetail response = exceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getTitle()).isEqualTo("Validation Failed");
        assertThat(response.getProperties()).containsKey("fieldErrors");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getProperties().get("fieldErrors");
        assertThat(errors).hasSize(2);
        assertThat(errors).containsKey("firstName");
        assertThat(errors).containsKey("age");
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 status")
    void shouldHandleGenericException_WhenUnexpectedErrorOccurs() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        ProblemDetail response = exceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getDetail()).isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException and return 400 status")
    void shouldHandleIllegalArgumentException_WhenInvalidArgumentProvided() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ProblemDetail response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getTitle()).isEqualTo("Invalid Argument");
        assertThat(response.getDetail()).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should include timestamp in all error responses")
    void shouldIncludeTimestamp_InAllErrorResponses() {
        // Arrange
        BusinessException exception = new BusinessException("Test error");

        // Act
        ProblemDetail response = exceptionHandler.handleBusinessException(exception, webRequest);

        // Assert
        assertThat(response.getProperties()).containsKey("timestamp");
        assertThat(response.getProperties().get("timestamp")).isNotNull();
    }
}
