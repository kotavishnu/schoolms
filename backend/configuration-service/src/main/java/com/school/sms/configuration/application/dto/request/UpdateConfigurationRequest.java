package com.school.sms.configuration.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a configuration setting.
 *
 * <p>This is similar to CreateConfigurationRequest but can be used
 * when only certain fields need to be updated.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigurationRequest {

    @NotBlank(message = "Configuration value is required")
    private String configValue;

    private String dataType;  // STRING, NUMBER, BOOLEAN, JSON

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Updated by is required")
    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    private String updatedBy;
}
