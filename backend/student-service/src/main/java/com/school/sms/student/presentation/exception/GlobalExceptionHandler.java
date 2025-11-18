package com.school.sms.student.presentation.exception;

import com.school.sms.student.domain.exception.DuplicateMobileException;
import com.school.sms.student.domain.exception.InvalidAgeException;
import com.school.sms.student.domain.exception.StudentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
 * Global exception handler for REST controllers.
 * Handles exceptions and returns RFC 7807 ProblemDetail responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE_URI = "https://api.school.com/problems/";

    /**
     * Handles StudentNotFoundException.
     * Returns 404 Not Found.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
        log.error("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "not-found"));
        problemDetail.setTitle("Student Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles InvalidAgeException.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(InvalidAgeException.class)
    public ProblemDetail handleInvalidAgeException(InvalidAgeException ex) {
        log.error("Invalid age: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "invalid-age"));
        problemDetail.setTitle("Invalid Age");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles DuplicateMobileException.
     * Returns 409 Conflict.
     */
    @ExceptionHandler(DuplicateMobileException.class)
    public ProblemDetail handleDuplicateMobileException(DuplicateMobileException ex) {
        log.error("Duplicate mobile: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "duplicate-mobile"));
        problemDetail.setTitle("Duplicate Mobile Number");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles ObjectOptimisticLockingFailureException.
     * Returns 409 Conflict for concurrent updates.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        log.error("Concurrent modification detected: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Record was modified by another user. Please refresh and try again."
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "concurrent-update"));
        problemDetail.setTitle("Concurrent Modification");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles MethodArgumentNotValidException for validation errors.
     * Returns 400 Bad Request with list of validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles generic exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + "internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
