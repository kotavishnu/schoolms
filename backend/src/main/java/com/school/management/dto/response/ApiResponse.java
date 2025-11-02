package com.school.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all controller responses.
 *
 * Provides consistent response structure with success flag, message, data payload, and timestamp.
 *
 * @param <T> Type of data payload
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates if the operation was successful
     */
    private boolean success;

    /**
     * Human-readable message about the operation
     */
    private String message;

    /**
     * Data payload (can be single object or list)
     */
    private T data;

    /**
     * Timestamp of response generation
     */
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();

    /**
     * Create successful response with data
     * @param data Response data
     * @param <T> Type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Create successful response with message and data
     * @param message Success message
     * @param data Response data
     * @param <T> Type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Create error response with message
     * @param message Error message
     * @param <T> Type of data
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
