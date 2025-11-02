package com.school.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for school configuration creation and update requests.
 *
 * Contains validation rules for configuration key-value pairs.
 *
 * @author School Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolConfigRequestDTO {

    @NotBlank(message = "Configuration key is required")
    @Size(min = 3, max = 100, message = "Config key must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Z_][A-Z0-9_]*$", message = "Config key must be uppercase with underscores (e.g., SCHOOL_NAME)")
    private String configKey;

    @NotBlank(message = "Configuration value is required")
    @Size(max = 5000, message = "Config value must not exceed 5000 characters")
    private String configValue;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Builder.Default
    private Boolean isEditable = true;

    @NotBlank(message = "Data type is required")
    @Pattern(regexp = "^(STRING|INTEGER|BOOLEAN|JSON)$", message = "Data type must be STRING, INTEGER, BOOLEAN, or JSON")
    @Builder.Default
    private String dataType = "STRING";
}
