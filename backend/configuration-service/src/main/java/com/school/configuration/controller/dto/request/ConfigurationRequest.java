package com.school.configuration.controller.dto.request;

import com.school.configuration.domain.model.DataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for Configuration creation/update requests.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationRequest {

    @NotBlank(message = "Value is required")
    @Size(max = 5000, message = "Value must not exceed 5000 characters")
    private String value;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Data type is required")
    private DataType dataType;

    @NotNull(message = "isEncrypted flag is required")
    private Boolean isEncrypted;
}
