package com.school.sms.configuration.application.dto;

import com.school.sms.configuration.domain.enums.SettingCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new configuration setting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSettingRequest {

    @NotNull(message = "Category is required")
    private SettingCategory category;

    @NotBlank(message = "Key is required")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Key must be in UPPERCASE_SNAKE_CASE format")
    @Size(max = 100, message = "Key must not exceed 100 characters")
    private String key;

    @NotBlank(message = "Value is required")
    private String value;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
