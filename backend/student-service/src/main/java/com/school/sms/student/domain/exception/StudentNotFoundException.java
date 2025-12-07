package com.school.sms.student.domain.exception;

/**
 * Exception thrown when a student is not found.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class StudentNotFoundException extends DomainException {

    private static final String ERROR_CODE = "STUDENT_NOT_FOUND";

    /**
     * Creates a StudentNotFoundException.
     *
     * @param studentId the student ID that was not found
     */
    public StudentNotFoundException(String studentId) {
        super("Student not found with ID: " + studentId, ERROR_CODE);
    }

    /**
     * Creates a StudentNotFoundException with a custom message.
     *
     * @param message the custom message
     */
    public StudentNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
