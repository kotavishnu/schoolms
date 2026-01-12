package com.schoolms.student.domain.exception;

/**
 * Exception thrown when an email already exists (BE-011)
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(String.format("Email '%s' is already registered", email));
    }
}
