package com.school.student.domain.model;

import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Rich Domain Model for Student.
 * Encapsulates student business logic and enforces invariants.
 * Follows Domain-Driven Design principles.
 *
 * Business Rules Enforced:
 * - BR-1: Student age must be between 3 and 18 years
 * - BR-5: At least one guardian name required (via GuardianInfo)
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Student {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Mobile mobile;
    private String email;
    private String address;
    private GuardianInfo guardianInfo;
    private String identificationMark;
    private String aadhaarNumber;
    private StudentStatus status;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Factory method to register a new student.
     * Enforces business rules during creation.
     *
     * @param firstName student's first name
     * @param lastName student's last name
     * @param dateOfBirth student's date of birth (for age validation BR-1)
     * @param mobile student's mobile number
     * @param guardianInfo guardian information (BR-5)
     * @return new Student instance
     * @throws IllegalArgumentException if business rules are violated
     */
    public static Student register(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Mobile mobile,
        GuardianInfo guardianInfo
    ) {
        validateAge(dateOfBirth);
        validateRequiredFields(firstName, lastName, mobile, guardianInfo);

        return Student.builder()
            .firstName(firstName.trim())
            .lastName(lastName.trim())
            .dateOfBirth(dateOfBirth)
            .mobile(mobile)
            .guardianInfo(guardianInfo)
            .status(StudentStatus.ACTIVE)
            .build();
    }

    /**
     * Updates the student's profile.
     * Only allowed for ACTIVE students.
     *
     * @param firstName updated first name
     * @param lastName updated last name
     * @param mobile updated mobile number
     * @throws IllegalStateException if student is INACTIVE
     */
    public void updateProfile(String firstName, String lastName, Mobile mobile) {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Cannot update profile of inactive student");
        }

        this.firstName = firstName != null ? firstName.trim() : this.firstName;
        this.lastName = lastName != null ? lastName.trim() : this.lastName;
        this.mobile = mobile != null ? mobile : this.mobile;
    }

    /**
     * Deactivates the student.
     *
     * @throws IllegalStateException if student is already INACTIVE
     */
    public void deactivate() {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Student is already inactive");
        }
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Reactivates the student.
     *
     * @throws IllegalStateException if student is already ACTIVE
     */
    public void activate() {
        if (this.status == StudentStatus.ACTIVE) {
            throw new IllegalStateException("Student is already active");
        }
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Calculates the current age of the student.
     *
     * @return age in years
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Returns the full name of the student.
     *
     * @return full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Validates student age (Business Rule BR-1).
     * Age must be between 3 and 18 years at registration.
     *
     * @param dateOfBirth student's date of birth
     * @throws IllegalArgumentException if age is outside valid range
     */
    private static void validateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new IllegalArgumentException(
                String.format("Student age must be between 3 and 18 years (current age: %d) - BR-1", age)
            );
        }
    }

    /**
     * Validates required fields for student registration.
     *
     * @param firstName student's first name
     * @param lastName student's last name
     * @param mobile student's mobile
     * @param guardianInfo guardian information
     * @throws IllegalArgumentException if any required field is missing
     */
    private static void validateRequiredFields(
        String firstName,
        String lastName,
        Mobile mobile,
        GuardianInfo guardianInfo
    ) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        if (guardianInfo == null) {
            throw new IllegalArgumentException("Guardian information is required");
        }
    }

    /**
     * Sets the student ID (called after persistence).
     *
     * @param studentId auto-generated student ID
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Sets the database ID (called after persistence).
     *
     * @param id database ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the version (for optimistic locking).
     *
     * @param version version number
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Sets additional optional fields.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIdentificationMark(String identificationMark) {
        this.identificationMark = identificationMark;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
