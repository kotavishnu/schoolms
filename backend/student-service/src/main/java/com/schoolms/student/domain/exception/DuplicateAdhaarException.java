package com.schoolms.student.domain.exception;

/**
 * Exception thrown when an Adhaar number already exists (BE-011)
 */
public class DuplicateAdhaarException extends RuntimeException {
    public DuplicateAdhaarException(String adhaarNumber) {
        super(String.format("Adhaar number '%s' is already registered", adhaarNumber));
    }
}
