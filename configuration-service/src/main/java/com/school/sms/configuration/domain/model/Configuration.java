package com.school.sms.configuration.domain.model;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Configuration Domain Entity
 * Represents a school configuration setting with category-based grouping
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Configuration {

    private Long id;
    private ConfigurationCategory category;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;

    /**
     * Factory method for creating new configuration
     */
    public static Configuration create(
            ConfigurationCategory category,
            String configKey,
            String configValue,
            String description,
            String createdBy
    ) {
        validateCategory(category);
        validateConfigKey(configKey);

        return Configuration.builder()
                .category(category)
                .configKey(configKey)
                .configValue(configValue)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .version(0)
                .build();
    }

    /**
     * Update configuration value and description
     */
    public void update(String configValue, String description, String updatedBy) {
        this.configValue = configValue;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Validate category is not null
     */
    private static void validateCategory(ConfigurationCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Configuration category cannot be null");
        }
    }

    /**
     * Validate config key is not empty
     */
    private static void validateConfigKey(String configKey) {
        if (configKey == null || configKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration key cannot be empty");
        }
    }
}
