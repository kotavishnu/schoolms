package com.school.config.config;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * Global exception handler for consistent error responses.
 * Returns RFC 7807 Problem Details format.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
            "about:blank",
            "Bad Request",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            MDC.get("correlationId"),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());

            ErrorResponseDTO error = new ErrorResponseDTO(
                "about:blank",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                MDC.get("correlationId"),
                null
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("version mismatch")) {
            log.warn("Optimistic locking conflict: {}", ex.getMessage());

            ErrorResponseDTO error = new ErrorResponseDTO(
                "about:blank",
                "Conflict",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                MDC.get("correlationId"),
                null
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        log.error("Unexpected error", ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
            "about:blank",
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            LocalDateTime.now(),
            MDC.get("correlationId"),
            null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Bean validation failed");

        List<FieldErrorDTO> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldErrorDTO(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue() != null ? error.getRejectedValue().toString() : null
            ))
            .collect(Collectors.toList());

        ErrorResponseDTO error = new ErrorResponseDTO(
            "about:blank",
            "Validation Failed",
            HttpStatus.BAD_REQUEST.value(),
            "Input validation failed",
            LocalDateTime.now(),
            MDC.get("correlationId"),
            fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public record ErrorResponseDTO(
        String type,
        String title,
        int status,
        String detail,
        LocalDateTime timestamp,
        String correlationId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<FieldErrorDTO> errors
    ) {}

    public record FieldErrorDTO(
        String field,
        String message,
        String rejectedValue
    ) {}
}
