package com.schoolms.student.presentation.exception;

import com.schoolms.student.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Global Exception Handler (BE-022)
 * Centralized exception handling with RFC 7807 ProblemDetail
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(StudentNotFoundException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.NOT_FOUND,
            "not-found",
            "Resource Not Found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({DuplicatePhoneException.class, DuplicateEmailException.class, DuplicateAdhaarException.class})
    public ResponseEntity<ProblemDetail> handleDuplicate(RuntimeException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.CONFLICT,
            "duplicate-resource",
            "Duplicate Resource",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler({InvalidAgeException.class, ImmutableFieldException.class})
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(RuntimeException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "business-rule-violation",
            "Business Rule Violation",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ProblemDetail problem = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "validation-error",
            "Validation Error",
            "Request validation failed"
        );
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(OptimisticLockingFailureException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.CONFLICT,
            "optimistic-lock-failure",
            "Concurrent Modification",
            "The resource was modified by another transaction. Please retry."
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "invalid-argument",
            "Invalid Argument",
            ex.getMessage()
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "internal-server-error",
            "Internal Server Error",
            "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ProblemDetail createProblemDetail(
        HttpStatus status,
        String type,
        String title,
        String detail
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("https://api.schoolms.com/problems/" + type));
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", UUID.randomUUID().toString());
        return problem;
    }
}
