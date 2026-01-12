package com.schoolms.configuration.presentation.dto;

import com.schoolms.configuration.domain.model.ConfigCategory;
import jakarta.validation.constraints.*;

/**
 * Create Configuration Request DTO (BE-034)
 */
public record CreateConfigurationRequest(
    @NotNull(message = "Category is required")
    ConfigCategory category,

    @NotBlank(message = "Key is required")
    @Size(max = 100, message = "Key must not exceed 100 characters")
    String key,

    @NotBlank(message = "Value is required")
    @Size(max = 1000, message = "Value must not exceed 1000 characters")
    String value,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {
}
