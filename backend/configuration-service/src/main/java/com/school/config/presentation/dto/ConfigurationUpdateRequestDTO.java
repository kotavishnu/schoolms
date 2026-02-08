package com.school.config.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating configuration.
 * Only value and description can be updated.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
public record ConfigurationUpdateRequestDTO(
    @NotBlank(message = "Configuration value is required")
    @Size(max = 1000, message = "Value must not exceed 1000 characters")
    String value,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @NotNull(message = "Version is required for optimistic locking")
    Long version
) {
}
