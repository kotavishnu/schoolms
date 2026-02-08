package com.school.student.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RFC 7807 Problem Details error response.
 * Used by GlobalExceptionHandler for consistent error responses.
 *
 * <p>Fields:
 * <ul>
 *   <li>type: URI identifying the error type</li>
 *   <li>title: Short, human-readable summary</li>
 *   <li>status: HTTP status code</li>
 *   <li>detail: Detailed error explanation</li>
 *   <li>timestamp: When the error occurred</li>
 *   <li>correlationId: For request tracing</li>
 *   <li>errors: List of field validation errors (optional)</li>
 * </ul>
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(

    String type,
    String title,
    Integer status,
    String detail,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,

    String correlationId,
    String path,

    List<FieldErrorDTO> errors
) {

    /**
     * Field-level validation error.
     */
    @Builder
    public record FieldErrorDTO(
        String field,
        String message,
        String code,
        Object rejectedValue
    ) {
    }
}
