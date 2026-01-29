package com.school.student.domain.exception;

/**
 * Thrown when attempting to register an Aadhaar number that already exists
 * Maps to HTTP 409 Conflict
 */
public class DuplicateAadhaarException extends BusinessException {

    public DuplicateAadhaarException(String aadhaarNumber) {
        super(String.format("Aadhaar number %s is already registered", aadhaarNumber));
    }
}
