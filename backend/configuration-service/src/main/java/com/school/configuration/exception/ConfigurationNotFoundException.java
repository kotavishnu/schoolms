package com.school.configuration.exception;

import com.school.configuration.domain.entity.ConfigCategory;

/**
 * Configuration Not Found Exception
 *
 * Thrown when a requested configuration does not exist.
 * Results in HTTP 404 Not Found response.
 *
 * Used by:
 * - ConfigurationService.getConfiguration()
 * - ConfigurationService.deleteConfiguration()
 */
public class ConfigurationNotFoundException extends RuntimeException {

    private final ConfigCategory category;
    private final String key;

    /**
     * Create exception with category and key
     *
     * @param category Configuration category
     * @param key Configuration key
     */
    public ConfigurationNotFoundException(ConfigCategory category, String key) {
        super(String.format(
            "Configuration not found: category=%s, key=%s",
            category.name(),
            key
        ));
        this.category = category;
        this.key = key;
    }

    /**
     * Create exception with custom message
     *
     * @param message Custom error message
     */
    public ConfigurationNotFoundException(String message) {
        super(message);
        this.category = null;
        this.key = null;
    }

    public ConfigCategory getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }
}
