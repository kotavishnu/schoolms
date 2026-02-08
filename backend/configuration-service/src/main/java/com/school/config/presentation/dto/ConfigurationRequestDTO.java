package com.school.config.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating/updating configuration.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
public record ConfigurationRequestDTO(
    @NotBlank(message = "Category is required")
    @Pattern(regexp = "GENERAL|ACADEMIC|FINANCIAL", message = "Category must be GENERAL, ACADEMIC, or FINANCIAL")
    String category,

    @NotBlank(message = "Configuration key is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Key must contain only letters, numbers, and underscores")
    @Size(max = 100, message = "Key must not exceed 100 characters")
    String key,

    @NotBlank(message = "Configuration value is required")
    @Size(max = 1000, message = "Value must not exceed 1000 characters")
    String value,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @NotBlank(message = "Data type is required")
    @Pattern(regexp = "STRING|NUMBER|BOOLEAN|JSON", message = "Data type must be STRING, NUMBER, BOOLEAN, or JSON")
    String dataType,

    @NotNull(message = "Encryption flag is required")
    Boolean isEncrypted
) {
}
