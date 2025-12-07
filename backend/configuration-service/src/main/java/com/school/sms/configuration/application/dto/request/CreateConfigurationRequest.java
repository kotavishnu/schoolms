package com.school.sms.configuration.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a configuration setting.
 *
 * <p>This DTO is used for both create and update operations (UPSERT).
 * The category and key are provided as path parameters in the REST API.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConfigurationRequest {

    @NotBlank(message = "Configuration value is required")
    private String configValue;

    @NotNull(message = "Data type is required")
    private String dataType;  // STRING, NUMBER, BOOLEAN, JSON

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean isEncrypted;

    @NotBlank(message = "Updated by is required")
    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    private String updatedBy;
}
