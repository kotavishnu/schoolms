package com.school.sms.student.domain.model;

import com.school.sms.common.util.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

/**
 * Student Aggregate Root.
 * Encapsulates student business logic and invariants.
 * Rich domain model following DDD principles.
 */
@Getter
@Builder(toBuilder = true)
public class Student {

    private Long id;
    private StudentId studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Mobile mobile;
    private String address;
    private String fatherName;
    private String motherName;
    private String identificationMark;
    private String aadhaarNumber;
    private String email;
    private StudentStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Setter
    private Long version; // For optimistic locking

    /**
     * Factory method to register a new student.
     * Default status is ACTIVE.
     *
     * @return new Student instance
     */
    public static Student register(StudentId studentId, String firstName, String lastName,
                                   LocalDate dateOfBirth, Mobile mobile, String address,
                                   String fatherName, String motherName,
                                   String identificationMark, String aadhaarNumber, String email) {
        validateRegistration(firstName, lastName, dateOfBirth, mobile);

        return Student.builder()
            .studentId(studentId)
            .firstName(firstName)
            .lastName(lastName)
            .dateOfBirth(dateOfBirth)
            .mobile(mobile)
            .address(address)
            .fatherName(fatherName)
            .motherName(motherName)
            .identificationMark(identificationMark)
            .aadhaarNumber(aadhaarNumber)
            .email(email)
            .status(StudentStatus.ACTIVE)
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();
    }

    /**
     * Update editable profile fields: firstName, lastName, mobile.
     */
    public void updateProfile(String firstName, String lastName, Mobile mobile) {
        validateProfileUpdate(firstName, lastName, mobile);

        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.updatedAt = LocalDate.now();
    }

    /**
     * Change student status.
     */
    public void changeStatus(StudentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
        this.updatedAt = LocalDate.now();
    }

    /**
     * Calculate current age in years.
     */
    public int getCurrentAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Validate age is within acceptable range (3-18).
     * Note: This is a domain helper. Actual validation happens in Drools.
     */
    public boolean isAgeValid() {
        int age = getCurrentAge();
        return age >= 3 && age <= 18;
    }

    // Private validation methods

    private static void validateRegistration(String firstName, String lastName,
                                            LocalDate dateOfBirth, Mobile mobile) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }
    }

    private void validateProfileUpdate(String firstName, String lastName, Mobile mobile) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }
    }
}
