package com.school.student.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for student data.
 * Includes all request fields plus persistence-managed fields.
 *
 * <p>Immutable record for thread-safety.
 * Field naming matches OpenAPI specification.
 */
@Builder
public record StudentResponseDTO(

    // Persistence fields
    Long id,
    String studentId,
    String status,
    Long version,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt,

    String createdBy,
    String updatedBy,

    // Business fields (from request)
    String firstName,
    String lastName,

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth,

    String mobile,
    String email,
    String address,
    String fathersName,
    String mothersName,
    String identificationMark,
    String aadhaarNumber
) {
}
