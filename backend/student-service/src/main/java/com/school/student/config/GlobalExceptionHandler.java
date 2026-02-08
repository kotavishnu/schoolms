package com.school.student.config;

import com.school.student.common.exception.ValidationException;
import com.school.student.presentation.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Converts exceptions to RFC 7807 Problem Details responses.
 *
 * <p>Handles:
 * <ul>
 *   <li>ValidationException - Business rule violations (400)</li>
 *   <li>MethodArgumentNotValidException - Bean validation failures (400)</li>
 *   <li>RuntimeException - General errors (404/409/500)</li>
 *   <li>Exception - Catch-all (500)</li>
 * </ul>
 *
 * <p>All responses include correlation ID from MDC for request tracing.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Handle ValidationException (Drools business rule failures).
     * Returns 400 Bad Request with validation errors.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation exception: {}", ex.getMessage());

        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getValidationResult().getErrors().stream()
                .map(error -> ErrorResponseDTO.FieldErrorDTO.builder()
                        .field(error.getField())
                        .message(error.getMessage())
                        .code(error.getCode())
                        .rejectedValue(null) // Not available from Drools
                        .build())
                .collect(Collectors.toList());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .type("https://api.school.com/errors/validation-error")
                .title("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Business rule validation failed. Please check the errors list.")
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get(CORRELATION_ID_KEY))
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle MethodArgumentNotValidException (Bean Validation failures).
     * Returns 400 Bad Request with field validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Bean validation failed: {} errors", ex.getBindingResult().getErrorCount());

        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError
                            ? ((FieldError) error).getField()
                            : error.getObjectName();
                    Object rejectedValue = error instanceof FieldError
                            ? ((FieldError) error).getRejectedValue()
                            : null;

                    return ErrorResponseDTO.FieldErrorDTO.builder()
                            .field(fieldName)
                            .message(error.getDefaultMessage())
                            .code(error.getCode())
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .collect(Collectors.toList());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .type("https://api.school.com/errors/validation-error")
                .title("Invalid Request")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Request validation failed. Please check the errors list.")
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get(CORRELATION_ID_KEY))
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle RuntimeException with specific message patterns.
     * Maps common error messages to appropriate HTTP status codes.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";

        // Determine HTTP status based on error message
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String type = "https://api.school.com/errors/internal-error";
        String title = "Internal Server Error";

        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            type = "https://api.school.com/errors/not-found";
            title = "Resource Not Found";
        } else if (message.contains("version mismatch") || message.contains("Optimistic locking")) {
            status = HttpStatus.CONFLICT;
            type = "https://api.school.com/errors/conflict";
            title = "Optimistic Locking Conflict";
        }

        log.error("Runtime exception ({}): {}", status, message, ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .type(type)
                .title(title)
                .status(status.value())
                .detail(message)
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get(CORRELATION_ID_KEY))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Catch-all exception handler.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .type("https://api.school.com/errors/internal-error")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred. Please contact support if the problem persists.")
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get(CORRELATION_ID_KEY))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
