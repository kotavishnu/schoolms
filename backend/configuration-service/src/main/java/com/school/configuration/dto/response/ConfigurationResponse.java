package com.school.configuration.dto.response;

import com.school.configuration.domain.entity.ConfigCategory;
import com.school.configuration.domain.entity.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Configuration Response DTO
 *
 * Returned from all Configuration API endpoints.
 * Contains complete configuration information.
 *
 * IMPORTANT: For sensitive configurations (isEncrypted=true),
 * the value may be masked or excluded in responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationResponse {

    /**
     * Configuration category
     * Example: GENERAL, ACADEMIC, FINANCIAL, SYSTEM
     */
    private ConfigCategory category;

    /**
     * Configuration key (business identifier)
     * Format: UPPERCASE_WITH_UNDERSCORES
     * Example: SCHOOL_NAME, MAX_UPLOAD_SIZE
     */
    private String key;

    /**
     * Configuration value
     * Interpreted based on dataType
     */
    private String value;

    /**
     * Human-readable description
     * Explains the purpose of this configuration
     */
    private String description;

    /**
     * Data type of the value
     * Used for type validation and parsing
     */
    private DataType dataType;

    /**
     * Encryption flag
     * If true, value contains encrypted data
     */
    private Boolean isEncrypted;

    /**
     * Version for optimistic locking
     * Client must send this back during updates
     */
    private Integer version;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Get the composite key (category:key)
     * Useful for client-side identification
     */
    public String getCompositeKey() {
        return category.name() + ":" + key;
    }
}
