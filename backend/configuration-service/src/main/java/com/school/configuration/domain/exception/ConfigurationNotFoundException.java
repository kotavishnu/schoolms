package com.school.configuration.domain.exception;

import com.school.configuration.domain.model.ConfigCategory;

/**
 * Exception thrown when a configuration is not found.
 */
public class ConfigurationNotFoundException extends BusinessException {

    /**
     * Constructs a new ConfigurationNotFoundException with the specified id.
     *
     * @param id the configuration id that was not found
     */
    public ConfigurationNotFoundException(Long id) {
        super(String.format("Configuration not found with id: %d", id));
    }

    /**
     * Constructs a new ConfigurationNotFoundException with the specified category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     */
    public ConfigurationNotFoundException(ConfigCategory category, String key) {
        super(String.format("Configuration not found with category: %s and key: %s", category, key));
    }
}
