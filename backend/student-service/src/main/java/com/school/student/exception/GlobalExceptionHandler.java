package com.school.student.exception;

import com.school.student.domain.validation.ValidationResult;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 *
 * Handles all exceptions and returns RFC 7807 Problem Detail responses.
 * Provides consistent error format across all API endpoints.
 *
 * RFC 7807 Format:
 * - type: URI identifying the problem type
 * - title: Short human-readable summary
 * - status: HTTP status code
 * - detail: Detailed explanation
 * - instance: URI identifying the specific occurrence
 * - Additional properties: timestamp, correlationId, errors
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle StudentNotFoundException
     * HTTP 404 Not Found
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleStudentNotFound(
        StudentNotFoundException ex,
        HttpServletRequest request
    ) {
        log.warn("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/student-not-found"));
        problemDetail.setTitle("Student Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handle ValidationException (Drools business rule violations)
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
        ValidationException ex,
        HttpServletRequest request
    ) {
        log.warn("Validation failed: {} errors", ex.getErrors().size());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Business rule validation failed"
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/validation-failed"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());

        // Convert validation errors to field-level error map
        Map<String, List<String>> fieldErrors = ex.getErrors().stream()
            .collect(Collectors.groupingBy(
                ValidationResult.ValidationError::getField,
                Collectors.mapping(ValidationResult.ValidationError::getMessage, Collectors.toList())
            ));

        problemDetail.setProperty("errors", fieldErrors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Handle OptimisticLockException (concurrent modification)
     * HTTP 409 Conflict
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(
        OptimisticLockException ex,
        HttpServletRequest request
    ) {
        log.warn("Optimistic lock exception: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The record was modified by another transaction. Please refresh and try again."
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/optimistic-lock-failure"));
        problemDetail.setTitle("Concurrent Modification Detected");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle MethodArgumentNotValidException (Jakarta Validation failures)
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        log.warn("Method argument validation failed: {} errors", ex.getBindingResult().getErrorCount());

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Request validation failed"
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/invalid-request"));
        problemDetail.setTitle("Invalid Request Parameters");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Handle generic exceptions
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support if the problem persists."
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(problemDetail);
    }
}
