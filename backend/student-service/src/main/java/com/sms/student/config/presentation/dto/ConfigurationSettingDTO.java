package com.sms.student.config.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Configuration Setting Data Transfer Object for API responses.
 */
@Schema(description = "Configuration setting information")
public record ConfigurationSettingDTO(
    @Schema(description = "Configuration ID", example = "1")
    Long id,

    @Schema(description = "Category of the setting", example = "SYSTEM")
    String category,

    @Schema(description = "Key of the setting", example = "MAX_STUDENTS_PER_CLASS")
    String key,

    @Schema(description = "Value of the setting", example = "50")
    String value,

    @Schema(description = "Description of the setting")
    String description,

    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {}
