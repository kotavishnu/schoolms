package com.schoolms.configuration.presentation.dto;

import jakarta.validation.constraints.Size;

/**
 * Update Configuration Request DTO (BE-034)
 */
public record UpdateConfigurationRequest(
    @Size(max = 1000, message = "Value must not exceed 1000 characters")
    String value,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {
}
