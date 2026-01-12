package com.schoolms.student.domain.exception;

/**
 * Exception thrown when a phone number already exists (BE-011)
 */
public class DuplicatePhoneException extends RuntimeException {
    public DuplicatePhoneException(String phone) {
        super(String.format("Phone number '%s' is already registered", phone));
    }
}
