package com.schoolms.configuration.domain.exception;

/**
 * Exception thrown when a configuration is not found (BE-029)
 */
public class ConfigurationNotFoundException extends RuntimeException {
    public ConfigurationNotFoundException(Long id) {
        super(String.format("Configuration with ID '%d' not found", id));
    }
}
