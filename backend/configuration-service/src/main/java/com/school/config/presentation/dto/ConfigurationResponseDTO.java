package com.school.config.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Response DTO for configuration.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
public record ConfigurationResponseDTO(
    Long id,
    String category,
    String key,
    String value,
    String description,
    String dataType,
    Boolean isEncrypted,
    Long version,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt,

    String createdBy,
    String updatedBy
) {
}
