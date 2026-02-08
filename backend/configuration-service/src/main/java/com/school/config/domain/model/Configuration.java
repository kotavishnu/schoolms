package com.school.config.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Rich Domain Model for Configuration.
 * Encapsulates configuration business logic and enforces invariants.
 * Follows Domain-Driven Design principles.
 *
 * Business Rules Enforced:
 * - Configuration key must be unique per category
 * - Key format: alphanumeric and underscore only
 * - Value and key cannot be empty
 * - Data type specifies value interpretation
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Configuration {

    private Long id;
    private ConfigCategory category;
    private String key;
    private String value;
    private String description;
    private DataType dataType;
    private boolean isEncrypted;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Factory method to create a new configuration.
     * Enforces business rules during creation.
     *
     * @param category configuration category (GENERAL, ACADEMIC, FINANCIAL)
     * @param key unique configuration key within category
     * @param value configuration value
     * @param description human-readable description (optional)
     * @param dataType value data type for interpretation
     * @param isEncrypted whether value is encrypted
     * @return new Configuration instance
     * @throws IllegalArgumentException if business rules are violated
     */
    public static Configuration create(
        ConfigCategory category,
        String key,
        String value,
        String description,
        DataType dataType,
        boolean isEncrypted
    ) {
        validateRequiredFields(category, key, value, dataType);
        validateKeyFormat(key);

        return Configuration.builder()
            .category(category)
            .key(key.trim())
            .value(value.trim())
            .description(description != null ? description.trim() : null)
            .dataType(dataType)
            .isEncrypted(isEncrypted)
            .build();
    }

    /**
     * Updates the configuration value.
     * Enforces validation on new value.
     *
     * @param newValue new configuration value
     * @throws IllegalArgumentException if value is invalid
     */
    public void updateValue(String newValue) {
        if (newValue == null || newValue.isBlank()) {
            throw new IllegalArgumentException("Configuration value is required");
        }
        this.value = newValue.trim();
    }

    /**
     * Updates the configuration description.
     *
     * @param newDescription new description (can be null)
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription != null ? newDescription.trim() : null;
    }

    /**
     * Gets the category name as string.
     *
     * @return category name
     */
    public String getCategoryName() {
        return category != null ? category.name() : null;
    }

    /**
     * Gets the data type name as string.
     *
     * @return data type name
     */
    public String getDataTypeName() {
        return dataType != null ? dataType.name() : null;
    }

    /**
     * Validates required fields for configuration creation.
     *
     * @param category configuration category
     * @param key configuration key
     * @param value configuration value
     * @param dataType data type
     * @throws IllegalArgumentException if any required field is invalid
     */
    private static void validateRequiredFields(
        ConfigCategory category,
        String key,
        String value,
        DataType dataType
    ) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Configuration key is required");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Configuration value is required");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("Data type is required");
        }
    }

    /**
     * Validates configuration key format.
     * Key must contain only alphanumeric characters and underscores.
     *
     * @param key configuration key
     * @throws IllegalArgumentException if key format is invalid
     */
    private static void validateKeyFormat(String key) {
        String trimmedKey = key.trim();
        if (!trimmedKey.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException(
                "Configuration key must contain only letters, numbers, and underscores"
            );
        }
    }

    // Setters for persistence layer (post-creation)

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
