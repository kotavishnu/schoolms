package com.school.student.domain.exception;

/**
 * Thrown when attempting to register a mobile number that already exists
 * Maps to HTTP 409 Conflict
 */
public class DuplicateMobileException extends BusinessException {

    public DuplicateMobileException(String mobile) {
        super(String.format("Mobile number %s is already registered", mobile));
    }
}
