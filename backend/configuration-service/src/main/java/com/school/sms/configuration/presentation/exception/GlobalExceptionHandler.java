package com.school.sms.configuration.presentation.exception;

import com.school.sms.configuration.domain.exception.DuplicateSettingException;
import com.school.sms.configuration.domain.exception.InvalidSettingKeyException;
import com.school.sms.configuration.domain.exception.SchoolProfileNotFoundException;
import com.school.sms.configuration.domain.exception.SettingNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Configuration Service.
 * Implements RFC 7807 Problem Details for HTTP APIs.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_PROPERTY = "timestamp";
    private static final String ERRORS_PROPERTY = "errors";

    /**
     * Handle validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields"
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/validation-failed"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        // Collect field errors
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty(ERRORS_PROPERTY, errors);

        return problemDetail;
    }

    /**
     * Handle setting not found.
     */
    @ExceptionHandler(SettingNotFoundException.class)
    public ProblemDetail handleSettingNotFound(SettingNotFoundException ex) {
        log.warn("Setting not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/setting-not-found"));
        problemDetail.setTitle("Setting Not Found");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle school profile not found.
     */
    @ExceptionHandler(SchoolProfileNotFoundException.class)
    public ProblemDetail handleSchoolProfileNotFound(SchoolProfileNotFoundException ex) {
        log.warn("School profile not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/school-profile-not-found"));
        problemDetail.setTitle("School Profile Not Found");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle duplicate setting.
     */
    @ExceptionHandler(DuplicateSettingException.class)
    public ProblemDetail handleDuplicateSetting(DuplicateSettingException ex) {
        log.warn("Duplicate setting: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/duplicate-setting"));
        problemDetail.setTitle("Duplicate Setting");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle invalid setting key.
     */
    @ExceptionHandler(InvalidSettingKeyException.class)
    public ProblemDetail handleInvalidSettingKey(InvalidSettingKeyException ex) {
        log.warn("Invalid setting key: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/invalid-setting-key"));
        problemDetail.setTitle("Invalid Setting Key");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle optimistic locking failures.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The resource has been modified by another user. Please refresh and try again."
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/concurrent-modification"));
        problemDetail.setTitle("Concurrent Modification");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle method argument type mismatch (e.g., invalid enum values).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch error: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(), ex.getName());

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            message += ". Accepted values are: " + java.util.Arrays.toString(enumConstants);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                message
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/invalid-parameter"));
        problemDetail.setTitle("Invalid Parameter");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        return problemDetail;
    }
}
