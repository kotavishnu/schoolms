package com.school.sms.configuration.domain.exception;

/**
 * Exception thrown when school profile is not found.
 */
public class SchoolProfileNotFoundException extends RuntimeException {

    public SchoolProfileNotFoundException() {
        super("School profile not found");
    }

    public SchoolProfileNotFoundException(String message) {
        super(message);
    }
}
