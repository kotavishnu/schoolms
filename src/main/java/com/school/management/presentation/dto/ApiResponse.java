package com.school.management.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for all REST endpoints.
 *
 * <p>This class provides a consistent response structure across the application:</p>
 * <ul>
 *   <li>success: Indicates if operation succeeded</li>
 *   <li>message: Human-readable message</li>
 *   <li>data: The actual response payload (generic type T)</li>
 *   <li>timestamp: When the response was generated</li>
 * </ul>
 *
 * <p>Usage Examples:</p>
 * <pre>
 * {@code
 * // Success with data
 * return ApiResponse.success("Student created successfully", studentDTO);
 *
 * // Success without data
 * return ApiResponse.success("Student deleted successfully");
 *
 * // Error response (typically handled by GlobalExceptionHandler)
 * return ApiResponse.error("Validation failed", errors);
 * }
 * </pre>
 *
 * <p>Response Format:</p>
 * <pre>
 * {
 *   "success": true,
 *   "message": "Operation completed successfully",
 *   "data": { ... },
 *   "timestamp": "2025-11-11T10:30:00"
 * }
 * </pre>
 *
 * @param <T> the type of response data
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if the operation was successful.
     */
    private boolean success;

    /**
     * Human-readable message describing the result.
     */
    private String message;

    /**
     * The actual response data (null for error responses).
     */
    private T data;

    /**
     * Timestamp when the response was generated.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Creates a success response with data.
     *
     * @param message the success message
     * @param data the response data
     * @param <T> the type of response data
     * @return success API response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a success response without data.
     *
     * @param message the success message
     * @param <T> the type of response data
     * @return success API response
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with data.
     *
     * @param message the error message
     * @param data additional error details
     * @param <T> the type of error data
     * @return error API response
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response without data.
     *
     * @param message the error message
     * @param <T> the type of response data
     * @return error API response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
