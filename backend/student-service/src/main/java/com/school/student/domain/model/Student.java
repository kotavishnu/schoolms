package com.school.student.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Rich Domain Model for Student
 * Encapsulates business logic and enforces invariants
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Student {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private String aadhaarNumber;
    private StudentStatus status;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business method: Update editable fields only
     * Enforces immutability rules
     */
    public void updateProfile(String firstName, String lastName, String mobile, StudentStatus status) {
        validateEditableFields(firstName, lastName, mobile);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method: Activate student
     */
    public void activate() {
        if (this.status == StudentStatus.ACTIVE) {
            throw new IllegalStateException("Student is already active");
        }
        this.status = StudentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method: Deactivate student
     */
    public void deactivate() {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Student is already inactive");
        }
        this.status = StudentStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate age from date of birth
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Validation for editable fields
     */
    private void validateEditableFields(String firstName, String lastName, String mobile) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (mobile == null || !mobile.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile must be exactly 10 digits");
        }
    }

    // Setters for MapStruct and JPA only
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public void setIdentificationMark(String identificationMark) {
        this.identificationMark = identificationMark;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
