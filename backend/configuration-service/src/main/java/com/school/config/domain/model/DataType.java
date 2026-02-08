package com.school.config.domain.model;

/**
 * Data type enumeration for configuration values.
 * Allows type-safe configuration value handling.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
public enum DataType {
    /**
     * String value
     */
    STRING,

    /**
     * Numeric value (integer or decimal)
     */
    NUMBER,

    /**
     * Boolean value (true/false)
     */
    BOOLEAN,

    /**
     * JSON object or array
     */
    JSON
}
