package com.schoolms.configuration.domain.exception;

import com.schoolms.configuration.domain.model.ConfigCategory;

/**
 * Exception thrown when a configuration key already exists (BE-029)
 */
public class DuplicateConfigKeyException extends RuntimeException {
    public DuplicateConfigKeyException(ConfigCategory category, String key) {
        super(String.format("Configuration with category '%s' and key '%s' already exists", category, key));
    }
}
