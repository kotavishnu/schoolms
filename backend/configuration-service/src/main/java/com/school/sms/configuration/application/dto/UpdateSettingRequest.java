package com.school.sms.configuration.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a configuration setting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSettingRequest {

    @NotBlank(message = "Value is required")
    private String value;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Version is required for optimistic locking")
    private Long version;
}
