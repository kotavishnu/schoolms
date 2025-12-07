package com.school.sms.configuration.domain.exception;

/**
 * Exception thrown when a configuration setting is not found.
 *
 * <p>This exception is thrown when attempting to retrieve or update
 * a configuration setting that does not exist in the system.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class ConfigurationNotFoundException extends DomainException {

    private static final String ERROR_CODE = "CONFIGURATION_NOT_FOUND";

    /**
     * Constructs a new exception for a missing configuration.
     *
     * @param category the configuration category
     * @param key the configuration key
     */
    public ConfigurationNotFoundException(String category, String key) {
        super(String.format("Configuration not found for category '%s' and key '%s'", category, key), ERROR_CODE);
    }

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message the detail message
     */
    public ConfigurationNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
}
