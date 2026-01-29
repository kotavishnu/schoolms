package com.school.configuration.domain.model;

/**
 * Data type enumeration for configuration values.
 * Defines the data type of configuration values for validation and parsing.
 */
public enum DataType {
    /**
     * String/text value
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
