package com.school.sms.student.domain.model;

import lombok.Value;

import java.util.Objects;

/**
 * Value object representing a student's contact information.
 *
 * <p>This is an immutable object containing contact details.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Value
public class ContactInfo {

    String mobile;
    String email;
    String address;

    /**
     * Creates a ContactInfo instance.
     *
     * @param mobile the mobile number (required)
     * @param email the email address (optional)
     * @param address the physical address (optional)
     * @throws IllegalArgumentException if mobile is null or blank
     */
    public ContactInfo(String mobile, String email, String address) {
        Objects.requireNonNull(mobile, "Mobile number cannot be null");
        if (mobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number cannot be blank");
        }

        this.mobile = mobile;
        this.email = email;
        this.address = address;
    }
}
