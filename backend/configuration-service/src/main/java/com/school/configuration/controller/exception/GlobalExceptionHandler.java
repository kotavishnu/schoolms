package com.school.configuration.controller.exception;

import com.school.configuration.domain.exception.ConfigurationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for Configuration Service.
 * Returns RFC 7807 Problem Details format for all errors.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_URI_BASE = "https://api.school.com/errors/";

    /**
     * Handle ConfigurationNotFoundException - return 404 Not Found
     */
    @ExceptionHandler(ConfigurationNotFoundException.class)
    public ProblemDetail handleConfigurationNotFoundException(ConfigurationNotFoundException ex) {
        log.warn("Configuration not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create(ERROR_URI_BASE + "not-found"));
        problemDetail.setTitle("Configuration Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle Bean Validation errors - return 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());

        List<Map<String, String>> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    Map<String, String> errorDetails = new HashMap<>();
                    if (error instanceof FieldError fieldError) {
                        errorDetails.put("field", fieldError.getField());
                        errorDetails.put("message", fieldError.getDefaultMessage());
                        errorDetails.put("rejectedValue",
                                fieldError.getRejectedValue() != null
                                        ? fieldError.getRejectedValue().toString()
                                        : "null");
                    } else {
                        errorDetails.put("message", error.getDefaultMessage());
                    }
                    return errorDetails;
                })
                .collect(Collectors.toList());

        String errorMessages = errors.stream()
                .map(e -> e.get("field") + ": " + e.get("message"))
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed: " + errorMessages
        );
        problemDetail.setType(URI.create(ERROR_URI_BASE + "validation-error"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    /**
     * Handle IllegalArgumentException - return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create(ERROR_URI_BASE + "bad-request"));
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle generic exceptions - return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
        problemDetail.setType(URI.create(ERROR_URI_BASE + "internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        // Don't expose internal error details in production
        if (log.isDebugEnabled()) {
            problemDetail.setProperty("exceptionType", ex.getClass().getName());
            problemDetail.setProperty("exceptionMessage", ex.getMessage());
        }

        return problemDetail;
    }
}
