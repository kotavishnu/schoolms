package com.school.sms.student.domain.exception;

/**
 * Exception thrown when a student's age is outside the valid range.
 *
 * <p>Valid age range: 3-18 years at registration.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class InvalidAgeException extends DomainException {

    private static final String ERROR_CODE = "INVALID_AGE";

    /**
     * Creates an InvalidAgeException.
     *
     * @param age the invalid age
     */
    public InvalidAgeException(int age) {
        super("Invalid student age: " + age + ". Age must be between 3 and 18 years.", ERROR_CODE);
    }

    /**
     * Creates an InvalidAgeException with a custom message.
     *
     * @param message the custom message
     */
    public InvalidAgeException(String message) {
        super(message, ERROR_CODE);
    }
}
