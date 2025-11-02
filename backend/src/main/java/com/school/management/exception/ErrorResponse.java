package com.school.management.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure for API error responses.
 *
 * This DTO is returned by GlobalExceptionHandler for all exceptions.
 * Provides consistent error format across the application.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * HTTP status code
     */
    private Integer status;

    /**
     * Error type/category
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Detailed field-level errors (for validation failures)
     */
    private Map<String, String> fieldErrors;

    /**
     * Request path where error occurred
     */
    private String path;

    /**
     * Timestamp when error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
