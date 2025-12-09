package com.sms.student.config.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating/updating a configuration setting.
 */
@Schema(description = "Request to create or update a configuration setting")
public record ConfigurationSettingRequest(
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    @Schema(description = "Category of the setting", example = "SYSTEM", requiredMode = Schema.RequiredMode.REQUIRED)
    String category,

    @NotBlank(message = "Key is required")
    @Size(min = 1, max = 100, message = "Key must be between 1 and 100 characters")
    @Schema(description = "Key of the setting", example = "MAX_STUDENTS_PER_CLASS", requiredMode = Schema.RequiredMode.REQUIRED)
    String key,

    @NotBlank(message = "Value is required")
    @Schema(description = "Value of the setting", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    String value,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Description of the setting")
    String description
) {}
