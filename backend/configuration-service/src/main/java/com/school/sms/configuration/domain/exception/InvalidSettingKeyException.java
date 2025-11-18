package com.school.sms.configuration.domain.exception;

/**
 * Exception thrown when a configuration setting key is invalid.
 * Keys must be in UPPERCASE_SNAKE_CASE format.
 */
public class InvalidSettingKeyException extends RuntimeException {

    public InvalidSettingKeyException(String key) {
        super("Invalid setting key format: " + key + ". Key must be in UPPERCASE_SNAKE_CASE format.");
    }

    public InvalidSettingKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
