package com.school.configuration.domain.entity;

/**
 * Data Type Enum
 *
 * Represents the data type of configuration values.
 * Used for type validation and proper parsing of configuration values.
 *
 * Data Types:
 * - STRING: Text values (default)
 * - NUMBER: Numeric values (integer or decimal)
 * - BOOLEAN: True/False values
 * - JSON: Complex JSON objects or arrays
 */
public enum DataType {
    /**
     * Text values
     * Example: "School Name", "contact@school.com"
     */
    STRING,

    /**
     * Numeric values
     * Example: 100, 75.5, 2024
     */
    NUMBER,

    /**
     * Boolean values
     * Example: true, false
     */
    BOOLEAN,

    /**
     * JSON objects or arrays
     * Example: {"key": "value"}, ["item1", "item2"]
     */
    JSON
}
