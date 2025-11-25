package com.school.sms.configuration.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateConfigRequest {

    @NotBlank(message = "Configuration value is required")
    @Size(min = 1, max = 500, message = "Config value must be between 1 and 500 characters")
    private String configValue;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Version is required for optimistic locking")
    private Integer version;
}
