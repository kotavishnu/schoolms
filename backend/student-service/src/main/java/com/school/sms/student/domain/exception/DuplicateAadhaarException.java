package com.school.sms.student.domain.exception;

/**
 * Exception thrown when attempting to register a student with an Aadhaar number
 * that already exists in the system.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class DuplicateAadhaarException extends DomainException {

    private static final String ERROR_CODE = "DUPLICATE_AADHAAR";

    /**
     * Creates a DuplicateAadhaarException.
     *
     * @param aadhaarNumber the duplicate Aadhaar number
     */
    public DuplicateAadhaarException(String aadhaarNumber) {
        super("Aadhaar number already exists: " + aadhaarNumber, ERROR_CODE);
    }
}
