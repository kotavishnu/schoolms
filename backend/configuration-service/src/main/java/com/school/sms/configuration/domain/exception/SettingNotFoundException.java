package com.school.sms.configuration.domain.exception;

/**
 * Exception thrown when a configuration setting is not found.
 */
public class SettingNotFoundException extends RuntimeException {

    public SettingNotFoundException(Long settingId) {
        super("Configuration setting not found with ID: " + settingId);
    }

    public SettingNotFoundException(String category, String key) {
        super("Configuration setting not found: " + category + "." + key);
    }

    public SettingNotFoundException(String message) {
        super(message);
    }
}
