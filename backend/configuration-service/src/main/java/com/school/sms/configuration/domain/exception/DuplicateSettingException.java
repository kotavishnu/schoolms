package com.school.sms.configuration.domain.exception;

import com.school.sms.configuration.domain.enums.SettingCategory;

/**
 * Exception thrown when attempting to create a duplicate configuration setting.
 */
public class DuplicateSettingException extends RuntimeException {

    public DuplicateSettingException(SettingCategory category, String key) {
        super("Configuration setting already exists: " + category + "." + key);
    }

    public DuplicateSettingException(String message) {
        super(message);
    }
}
