package com.school.management.presentation.exception;

import com.school.management.shared.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the School Management System.
 *
 * <p>This class provides centralized exception handling across all REST controllers
 * using @RestControllerAdvice. It handles both application-specific exceptions
 * and standard Spring framework exceptions.</p>
 *
 * <p>All error responses follow RFC 7807 Problem Detail format for consistency:</p>
 * <ul>
 *   <li>type: URI identifying the problem type</li>
 *   <li>title: Short human-readable summary</li>
 *   <li>status: HTTP status code</li>
 *   <li>detail: Human-readable explanation</li>
 *   <li>instance: URI identifying the specific occurrence</li>
 *   <li>Additional properties: errorCode, timestamp, fieldErrors (for validation)</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Handles BusinessException (400 Bad Request).
     *
     * @param ex the business exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Business Rule Violation");
        enrichProblemDetail(problemDetail, ex, request);

        return problemDetail;
    }

    /**
     * Handles ValidationException (400 Bad Request).
     *
     * @param ex the validation exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response with field errors
     */
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex, WebRequest request) {
        log.warn("Validation exception: {} field errors", ex.getFieldErrors().size());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Validation Failed");
        enrichProblemDetail(problemDetail, ex, request);

        // Add field-specific errors
        if (!ex.getFieldErrors().isEmpty()) {
            problemDetail.setProperty("fieldErrors", ex.getFieldErrors());
        }

        return problemDetail;
    }

    /**
     * Handles NotFoundException (404 Not Found).
     *
     * @param ex the not found exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setTitle("Resource Not Found");
        enrichProblemDetail(problemDetail, ex, request);

        // Add resource-specific information
        if (ex.getResourceType() != null) {
            problemDetail.setProperty("resourceType", ex.getResourceType());
        }
        if (ex.getResourceId() != null) {
            problemDetail.setProperty("resourceId", ex.getResourceId());
        }

        return problemDetail;
    }

    /**
     * Handles ConflictException (409 Conflict).
     *
     * @param ex the conflict exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex, WebRequest request) {
        log.warn("Conflict exception: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problemDetail.setTitle("Conflict");
        enrichProblemDetail(problemDetail, ex, request);

        return problemDetail;
    }

    /**
     * Handles UnauthorizedException (401 Unauthorized).
     *
     * @param ex the unauthorized exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        problemDetail.setTitle("Unauthorized");
        enrichProblemDetail(problemDetail, ex, request);

        return problemDetail;
    }

    /**
     * Handles AccountLockedException (423 Locked).
     *
     * @param ex the account locked exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response with lockout information
     */
    @ExceptionHandler(AccountLockedException.class)
    public ProblemDetail handleAccountLockedException(AccountLockedException ex, WebRequest request) {
        log.warn("Account locked: {} - attempts: {}", ex.getUsername(), ex.getFailedAttempts());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(423), // 423 Locked
                ex.getMessage()
        );

        problemDetail.setTitle("Account Locked");
        enrichProblemDetail(problemDetail, ex, request);

        // Add lockout-specific information
        if (ex.getLockoutEndTime() != null) {
            problemDetail.setProperty("lockoutEndTime", ex.getLockoutEndTime().toString());
            problemDetail.setProperty("remainingMinutes", ex.getRemainingLockoutMinutes());
        }
        if (ex.getUsername() != null) {
            problemDetail.setProperty("username", ex.getUsername());
        }

        return problemDetail;
    }

    /**
     * Handles MethodArgumentNotValidException (Bean Validation errors).
     *
     * @param ex the method argument not valid exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Bean validation failed: {} errors", ex.getBindingResult().getErrorCount());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        problemDetail.setTitle("Validation Failed");

        // Extract field errors from binding result
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("errorCode", "VALIDATION_ERROR");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        problemDetail.setProperty("path", request.getDescription(false));

        return problemDetail;
    }

    /**
     * Handles IllegalArgumentException (400 Bad Request).
     *
     * @param ex the illegal argument exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument exception: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Invalid Argument");
        problemDetail.setProperty("errorCode", "INVALID_ARGUMENT");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        problemDetail.setProperty("path", request.getDescription(false));

        return problemDetail;
    }

    /**
     * Handles all other exceptions (500 Internal Server Error).
     *
     * @param ex the exception
     * @param request the web request
     * @return RFC 7807 Problem Detail response
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", "INTERNAL_ERROR");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        problemDetail.setProperty("path", request.getDescription(false));

        return problemDetail;
    }

    /**
     * Enriches Problem Detail with common properties.
     *
     * @param problemDetail the problem detail to enrich
     * @param ex the base exception
     * @param request the web request
     */
    private void enrichProblemDetail(ProblemDetail problemDetail, BaseException ex, WebRequest request) {
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        problemDetail.setProperty("path", request.getDescription(false));
    }
}
