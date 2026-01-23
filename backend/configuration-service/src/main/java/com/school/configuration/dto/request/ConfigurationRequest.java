package com.school.configuration.dto.request;

import com.school.configuration.domain.entity.ConfigCategory;
import com.school.configuration.domain.entity.DataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration Request DTO
 *
 * Used for creating or updating configuration settings.
 * Supports upsert operations (create if not exists, update if exists).
 *
 * Validation rules:
 * - Category: Required, must be valid enum value
 * - Key: Required, uppercase with underscores only (^[A-Z0-9_]+$)
 * - Value: Required, stored as text
 * - Description: Optional, max 500 characters
 * - DataType: Optional, defaults to STRING
 * - IsEncrypted: Optional, defaults to false
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationRequest {

    @NotNull(message = "Category is required")
    private ConfigCategory category;

    @NotBlank(message = "Key is required")
    @Size(min = 1, max = 100, message = "Key must be between 1 and 100 characters")
    @Pattern(
        regexp = "^[A-Z0-9_]+$",
        message = "Key must contain only uppercase letters, numbers, and underscores"
    )
    private String key;

    @NotBlank(message = "Value is required")
    private String value;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Data type of the value
     * Defaults to STRING if not provided
     */
    @Builder.Default
    private DataType dataType = DataType.STRING;

    /**
     * Encryption flag for sensitive values
     * Defaults to false if not provided
     */
    @Builder.Default
    private Boolean isEncrypted = false;

    /**
     * Version for optimistic locking
     * Required for update operations, optional for create
     */
    private Integer version;
}
