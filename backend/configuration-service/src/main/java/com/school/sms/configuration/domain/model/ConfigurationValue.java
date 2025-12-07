package com.school.sms.configuration.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object representing a configuration value.
 *
 * <p>This encapsulates the actual value of a configuration setting.
 * Values are stored as text but can represent different data types
 * (STRING, NUMBER, BOOLEAN, JSON) as indicated by the DataType enum.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Getter
@EqualsAndHashCode
public class ConfigurationValue {

    private final String value;

    private ConfigurationValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Configuration value cannot be null");
        }
        this.value = value;
    }

    /**
     * Factory method to create a ConfigurationValue.
     *
     * @param value the configuration value (can be empty but not null)
     * @return a new ConfigurationValue instance
     * @throws IllegalArgumentException if value is null
     */
    public static ConfigurationValue of(String value) {
        return new ConfigurationValue(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
