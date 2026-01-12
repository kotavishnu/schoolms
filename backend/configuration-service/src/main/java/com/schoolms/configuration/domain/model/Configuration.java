package com.schoolms.configuration.domain.model;

import java.time.Instant;

/**
 * Configuration Domain Model (BE-026)
 * Represents a configuration setting
 */
public class Configuration {

    private Long id;
    private ConfigCategory category;
    private String key;
    private String value;
    private String description;
    private Instant createdAt;
    private Instant lastUpdated;
    private Integer version;

    // Private constructor
    private Configuration() {
    }

    /**
     * Factory method for creating new configuration
     */
    public static Configuration create(
        ConfigCategory category,
        String key,
        String value,
        String description
    ) {
        Configuration config = new Configuration();
        config.category = category;
        config.key = key;
        config.value = value;
        config.description = description;
        config.createdAt = Instant.now();
        config.lastUpdated = Instant.now();
        config.version = 0;

        config.validate();

        return config;
    }

    /**
     * Factory method for reconstruction from persistence
     */
    public static Configuration reconstruct(
        Long id,
        ConfigCategory category,
        String key,
        String value,
        String description,
        Instant createdAt,
        Instant lastUpdated,
        Integer version
    ) {
        Configuration config = new Configuration();
        config.id = id;
        config.category = category;
        config.key = key;
        config.value = value;
        config.description = description;
        config.createdAt = createdAt;
        config.lastUpdated = lastUpdated;
        config.version = version;
        return config;
    }

    /**
     * Domain behavior: Update value
     */
    public void updateValue(String newValue) {
        if (newValue != null && !newValue.isBlank()) {
            this.value = newValue;
            this.lastUpdated = Instant.now();
        }
    }

    /**
     * Domain behavior: Update description
     */
    public void updateDescription(String newDescription) {
        if (newDescription != null) {
            this.description = newDescription;
            this.lastUpdated = Instant.now();
        }
    }

    /**
     * Domain validation
     */
    private void validate() {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key is required");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value is required");
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public ConfigCategory getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public Integer getVersion() {
        return version;
    }

    // Package-private setters for infrastructure layer
    void setId(Long id) {
        this.id = id;
    }

    void setVersion(Integer version) {
        this.version = version;
    }
}
