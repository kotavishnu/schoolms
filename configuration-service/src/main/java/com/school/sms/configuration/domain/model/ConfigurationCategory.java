package com.school.sms.configuration.domain.model;

/**
 * Configuration Category Enumeration
 * Defines the types of configuration settings
 */
public enum ConfigurationCategory {
    GENERAL("General settings for school operations"),
    ACADEMIC("Academic year, terms, grading settings"),
    FINANCIAL("Fee structure, payment settings"),
    SYSTEM("System-level configuration and technical settings");

    private final String description;

    ConfigurationCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
