package com.school.sms.student.presentation.controller;

import com.school.sms.student.domain.exception.*;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 *
 * <p>Returns RFC 7807 Problem Details format for all errors.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles StudentNotFoundException (404 Not Found).
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFound(StudentNotFoundException ex) {
        log.error("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/student-not-found"));
        problemDetail.setTitle("Student Not Found");
        addCommonProperties(problemDetail, ex.getErrorCode());

        return problemDetail;
    }

    /**
     * Handles InvalidAgeException (422 Unprocessable Entity).
     */
    @ExceptionHandler(InvalidAgeException.class)
    public ProblemDetail handleInvalidAge(InvalidAgeException ex) {
        log.error("Invalid age: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/invalid-age"));
        problemDetail.setTitle("Invalid Student Age");
        addCommonProperties(problemDetail, ex.getErrorCode());

        return problemDetail;
    }

    /**
     * Handles DuplicateMobileException (409 Conflict).
     */
    @ExceptionHandler(DuplicateMobileException.class)
    public ProblemDetail handleDuplicateMobile(DuplicateMobileException ex) {
        log.error("Duplicate mobile: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/duplicate-mobile"));
        problemDetail.setTitle("Duplicate Mobile Number");
        addCommonProperties(problemDetail, ex.getErrorCode());

        return problemDetail;
    }

    /**
     * Handles DuplicateAadhaarException (409 Conflict).
     */
    @ExceptionHandler(DuplicateAadhaarException.class)
    public ProblemDetail handleDuplicateAadhaar(DuplicateAadhaarException ex) {
        log.error("Duplicate Aadhaar: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/duplicate-aadhaar"));
        problemDetail.setTitle("Duplicate Aadhaar Number");
        addCommonProperties(problemDetail, ex.getErrorCode());

        return problemDetail;
    }

    /**
     * Handles validation errors from @Valid (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for one or more fields"
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/validation-failed"));
        problemDetail.setTitle("Validation Error");

        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                fieldError -> fieldError.getField(),
                fieldError -> fieldError.getDefaultMessage() != null
                    ? fieldError.getDefaultMessage()
                    : "Invalid value",
                (existing, replacement) -> existing + "; " + replacement
            ));

        problemDetail.setProperty("errors", errors);
        addCommonProperties(problemDetail, "VALIDATION_ERROR");

        return problemDetail;
    }

    /**
     * Handles OptimisticLockException (409 Conflict).
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ProblemDetail handleOptimisticLock(OptimisticLockException ex) {
        log.error("Optimistic lock exception: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The record was modified by another user. Please refresh and try again."
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/concurrent-modification"));
        problemDetail.setTitle("Concurrent Modification");
        addCommonProperties(problemDetail, "OPTIMISTIC_LOCK");

        return problemDetail;
    }

    /**
     * Handles all other exceptions (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support."
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        addCommonProperties(problemDetail, "INTERNAL_ERROR");

        return problemDetail;
    }

    /**
     * Adds common properties to all problem details.
     */
    private void addCommonProperties(ProblemDetail problemDetail, String errorCode) {
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", UUID.randomUUID().toString());
        problemDetail.setProperty("errorCode", errorCode);
    }
}
