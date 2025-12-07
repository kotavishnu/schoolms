package com.school.sms.student.domain.exception;

/**
 * Exception thrown when an invalid student status transition is attempted.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class InvalidStudentStatusException extends DomainException {

    private static final String ERROR_CODE = "INVALID_STATUS";

    /**
     * Creates an InvalidStudentStatusException.
     *
     * @param message the error message
     */
    public InvalidStudentStatusException(String message) {
        super(message, ERROR_CODE);
    }
}
