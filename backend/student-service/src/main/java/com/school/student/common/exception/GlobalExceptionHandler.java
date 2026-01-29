package com.school.student.common.exception;

import com.school.student.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler using @ControllerAdvice
 * Returns RFC 7807 Problem Details format for all errors
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle StudentNotFoundException → 404 Not Found
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleStudentNotFound(StudentNotFoundException ex) {
        log.warn("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/student-not-found"));
        problemDetail.setTitle("Student Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handle DuplicateMobileException → 409 Conflict
     */
    @ExceptionHandler(DuplicateMobileException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateMobile(DuplicateMobileException ex) {
        log.warn("Duplicate mobile number: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/duplicate-mobile"));
        problemDetail.setTitle("Duplicate Mobile Number");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle DuplicateAadhaarException → 409 Conflict
     */
    @ExceptionHandler(DuplicateAadhaarException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateAadhaar(DuplicateAadhaarException ex) {
        log.warn("Duplicate Aadhaar number: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/duplicate-aadhaar"));
        problemDetail.setTitle("Duplicate Aadhaar Number");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle EnrollmentConflictException → 409 Conflict
     */
    @ExceptionHandler(EnrollmentConflictException.class)
    public ResponseEntity<ProblemDetail> handleEnrollmentConflict(EnrollmentConflictException ex) {
        log.warn("Enrollment conflict: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/enrollment-conflict"));
        problemDetail.setTitle("Enrollment Conflict");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle BusinessRuleViolationException → 400 Bad Request
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        log.warn("Business rule violation: {}", ex.getErrors());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Business rule validation failed"
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/business-rule-violation"));
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("errors", ex.getErrors());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Handle Bean Validation Errors → 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ValidationError(
                error.getField(),
                error.getDefaultMessage(),
                error.getCode()
            ))
            .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Request validation failed"
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/validation-error"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Handle generic exceptions → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support."
        );
        problemDetail.setType(URI.create("https://api.school.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    /**
     * Validation error detail record
     */
    private record ValidationError(String field, String message, String code) {}
}
