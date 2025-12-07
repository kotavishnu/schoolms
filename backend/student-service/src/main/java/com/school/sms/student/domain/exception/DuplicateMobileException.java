package com.school.sms.student.domain.exception;

/**
 * Exception thrown when attempting to register a student with a mobile number
 * that already exists in the system.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public class DuplicateMobileException extends DomainException {

    private static final String ERROR_CODE = "DUPLICATE_MOBILE";

    /**
     * Creates a DuplicateMobileException.
     *
     * @param mobile the duplicate mobile number
     */
    public DuplicateMobileException(String mobile) {
        super("Mobile number already exists: " + mobile, ERROR_CODE);
    }
}
