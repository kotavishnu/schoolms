package com.school.configuration.domain.entity;

/**
 * Configuration Category Enum
 *
 * Represents different categories of system configurations.
 * Used to organize and filter configuration settings.
 *
 * Categories:
 * - GENERAL: General system settings
 * - ACADEMIC: Academic-related settings (grades, terms, etc.)
 * - FINANCIAL: Financial settings (fees, payment terms)
 * - SYSTEM: System-level technical settings
 */
public enum ConfigCategory {
    /**
     * General system settings
     * Example: school_name, school_address, contact_email
     */
    GENERAL,

    /**
     * Academic-related settings
     * Example: current_academic_year, min_attendance_percentage
     */
    ACADEMIC,

    /**
     * Financial settings
     * Example: default_currency, payment_gateway_url
     */
    FINANCIAL,

    /**
     * System-level technical settings
     * Example: max_upload_size, session_timeout
     */
    SYSTEM
}
