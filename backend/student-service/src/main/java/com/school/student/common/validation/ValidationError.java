package com.school.student.common.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a single validation error.
 * Immutable value object used within ValidationResult.
 *
 * <p>Components:
 * <ul>
 *   <li>field: The field name that failed validation (e.g., "dateOfBirth", "mobile")</li>
 *   <li>message: Human-readable error message</li>
 *   <li>code: Error code for programmatic handling (e.g., "AGE_OUT_OF_RANGE", "MOBILE_ALREADY_EXISTS")</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@ToString
public class ValidationError {

    private final String field;
    private final String message;
    private final String code;
}
