package com.school.configuration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Configuration domain model.
 * Represents a configuration setting with category, key, value, and metadata.
 * This is a pure domain model with no framework dependencies.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    /**
     * Unique identifier (auto-generated)
     */
    private Long id;

    /**
     * Configuration category (GENERAL, ACADEMIC, FINANCIAL)
     */
    private ConfigCategory category;

    /**
     * Configuration key (unique within category)
     */
    private String key;

    /**
     * Configuration value (stored as string, parsed based on dataType)
     */
    private String value;

    /**
     * Description of the configuration setting
     */
    private String description;

    /**
     * Data type of the value (STRING, NUMBER, BOOLEAN, JSON)
     */
    private DataType dataType;

    /**
     * Indicates if the value is encrypted
     */
    private Boolean isEncrypted;

    /**
     * Version for optimistic locking
     */
    private Long version;

    /**
     * Last updated timestamp
     */
    private LocalDateTime updatedAt;
}
