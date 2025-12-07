package com.school.sms.configuration.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object representing a configuration key.
 *
 * <p>This encapsulates the key identifier for a configuration setting.
 * Keys should be descriptive and follow a consistent naming convention
 * (e.g., snake_case or camelCase).</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Getter
@EqualsAndHashCode
public class ConfigurationKey {

    private final String value;

    private ConfigurationKey(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration key cannot be null or empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Configuration key cannot exceed 100 characters");
        }
        this.value = value.trim();
    }

    /**
     * Factory method to create a ConfigurationKey.
     *
     * @param value the key value
     * @return a new ConfigurationKey instance
     * @throws IllegalArgumentException if value is null, empty, or too long
     */
    public static ConfigurationKey of(String value) {
        return new ConfigurationKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
