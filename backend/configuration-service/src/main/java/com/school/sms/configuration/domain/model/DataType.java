package com.school.sms.configuration.domain.model;

/**
 * Enum representing supported data types for configuration values.
 *
 * <p>This determines how the configuration value should be interpreted:</p>
 * <ul>
 *   <li>STRING - Plain text values</li>
 *   <li>NUMBER - Numeric values (integer or decimal)</li>
 *   <li>BOOLEAN - True/false values</li>
 *   <li>JSON - Complex JSON structures</li>
 * </ul>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public enum DataType {
    STRING,
    NUMBER,
    BOOLEAN,
    JSON
}
