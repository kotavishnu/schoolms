package com.school.sms.student.domain.model;

import lombok.Value;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value object representing a student's personal information.
 *
 * <p>This is an immutable object containing personal details.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Value
public class PersonalInfo {

    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String identificationMark;
    String aadhaarNumber;

    /**
     * Creates a PersonalInfo instance.
     *
     * @param firstName the first name (required)
     * @param lastName the last name (required)
     * @param dateOfBirth the date of birth (required)
     * @param identificationMark any identification mark (optional)
     * @param aadhaarNumber the Aadhaar number (optional)
     * @throws IllegalArgumentException if required fields are null or empty
     */
    public PersonalInfo(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String identificationMark,
        String aadhaarNumber
    ) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.identificationMark = identificationMark;
        this.aadhaarNumber = aadhaarNumber;
    }

    /**
     * Returns the full name of the student.
     *
     * @return full name as "firstName lastName"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
