package com.school.sms.configuration.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateConfigRequest {

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "GENERAL|ACADEMIC|FINANCIAL|SYSTEM",
             message = "Category must be one of: GENERAL, ACADEMIC, FINANCIAL, SYSTEM")
    private String category;

    @NotBlank(message = "Configuration key is required")
    @Size(min = 1, max = 100, message = "Config key must be between 1 and 100 characters")
    private String configKey;

    @NotBlank(message = "Configuration value is required")
    @Size(min = 1, max = 500, message = "Config value must be between 1 and 500 characters")
    private String configValue;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
