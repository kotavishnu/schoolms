package com.school.sms.configuration.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Rich domain model representing a configuration setting.
 *
 * <p>This is NOT a JPA entity. It contains business logic and enforces
 * domain rules. The infrastructure layer will map this to a JPA entity.</p>
 *
 * <p>Configuration settings are stored as key-value pairs grouped by category.
 * Each setting has a data type indicator (STRING, NUMBER, BOOLEAN, JSON)
 * and can optionally be encrypted.</p>
 *
 * <p>Invariants:</p>
 * <ul>
 *   <li>Category and key combination must be unique (enforced by repository)</li>
 *   <li>Key cannot be null or empty</li>
 *   <li>Value cannot be null (but can be empty string)</li>
 *   <li>Data type defaults to STRING if not specified</li>
 * </ul>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationSetting {

    private Long settingId;
    private Category category;
    private ConfigurationKey configKey;
    private ConfigurationValue configValue;
    private DataType dataType;
    private String description;
    private boolean encrypted;
    private Long version;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * Factory method to create a new configuration setting.
     *
     * @param category the configuration category
     * @param configKey the configuration key
     * @param configValue the configuration value
     * @param dataType the data type of the value
     * @param description optional description of the setting
     * @param encrypted whether the value should be encrypted
     * @param updatedBy the user creating this configuration
     * @return a new ConfigurationSetting instance
     */
    public static ConfigurationSetting createNew(
        Category category,
        ConfigurationKey configKey,
        ConfigurationValue configValue,
        DataType dataType,
        String description,
        boolean encrypted,
        String updatedBy
    ) {
        return new ConfigurationSetting(
            null,  // ID will be assigned by repository
            category,
            configKey,
            configValue,
            dataType != null ? dataType : DataType.STRING,  // Default to STRING
            description,
            encrypted,
            0L,  // Initial version
            LocalDateTime.now(),
            updatedBy
        );
    }

    /**
     * Factory method to recreate a configuration setting from repository.
     *
     * @param settingId the database ID
     * @param category the configuration category
     * @param configKey the configuration key
     * @param configValue the configuration value
     * @param dataType the data type
     * @param description the description
     * @param encrypted whether the value is encrypted
     * @param version the version for optimistic locking
     * @param updatedAt the last update timestamp
     * @param updatedBy the user who last updated this setting
     * @return a ConfigurationSetting instance
     */
    public static ConfigurationSetting fromRepository(
        Long settingId,
        Category category,
        ConfigurationKey configKey,
        ConfigurationValue configValue,
        DataType dataType,
        String description,
        boolean encrypted,
        Long version,
        LocalDateTime updatedAt,
        String updatedBy
    ) {
        return new ConfigurationSetting(
            settingId,
            category,
            configKey,
            configValue,
            dataType,
            description,
            encrypted,
            version,
            updatedAt,
            updatedBy
        );
    }

    /**
     * Updates the configuration value.
     *
     * <p>This method creates a new instance with the updated value,
     * maintaining immutability of the domain object.</p>
     *
     * @param newValue the new configuration value
     * @param dataType the data type (optional, keeps existing if null)
     * @param description the description (optional, keeps existing if null)
     * @param updatedBy the user making the update
     * @return a new ConfigurationSetting instance with updated values
     */
    public ConfigurationSetting updateValue(
        ConfigurationValue newValue,
        DataType dataType,
        String description,
        String updatedBy
    ) {
        return new ConfigurationSetting(
            this.settingId,
            this.category,
            this.configKey,
            newValue,
            dataType != null ? dataType : this.dataType,
            description != null ? description : this.description,
            this.encrypted,
            this.version,  // Version will be incremented by JPA
            LocalDateTime.now(),
            updatedBy
        );
    }

    /**
     * Gets the string representation of the configuration value.
     *
     * @return the configuration value as a string
     */
    public String getValueAsString() {
        return configValue.getValue();
    }

    /**
     * Gets the category as a string.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return category.name();
    }

    /**
     * Gets the key as a string.
     *
     * @return the configuration key
     */
    public String getKeyAsString() {
        return configKey.getValue();
    }

    /**
     * Gets the data type as a string.
     *
     * @return the data type name
     */
    public String getDataTypeName() {
        return dataType.name();
    }
}
