package com.school.sms.student.domain.exception;

/**
 * Exception thrown when attempting to register a student with a mobile number
 * that already exists in the system.
 */
public class DuplicateMobileException extends RuntimeException {

    public DuplicateMobileException(String message) {
        super(message);
    }

    public DuplicateMobileException(String message, Throwable cause) {
        super(message, cause);
    }
}
