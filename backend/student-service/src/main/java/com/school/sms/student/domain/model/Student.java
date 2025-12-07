package com.school.sms.student.domain.model;

import com.school.sms.student.domain.exception.InvalidAgeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

/**
 * Rich domain model representing a Student.
 *
 * <p>This is NOT a JPA entity. It contains business logic and enforces
 * domain rules. The infrastructure layer will map this to a JPA entity.</p>
 *
 * <p>Invariants:</p>
 * <ul>
 *   <li>Student age must be between 3 and 18 years at registration</li>
 *   <li>Mobile number must be unique (enforced by repository)</li>
 *   <li>Student ID follows format: STD-YYYYMMDD-NNNN</li>
 * </ul>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {

    private static final int MIN_AGE = 3;
    private static final int MAX_AGE = 18;

    private Long id;
    private StudentId studentId;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private FamilyInfo familyInfo;
    private StudentStatus status;
    private AuditInfo auditInfo;
    private Long version;

    /**
     * Factory method to create a new student (for registration).
     *
     * @param studentId the unique student ID
     * @param personalInfo personal information
     * @param contactInfo contact information
     * @param familyInfo family information
     * @param createdBy the user creating this student record
     * @return a new Student instance
     * @throws InvalidAgeException if age is not between 3 and 18
     */
    public static Student createNew(
        StudentId studentId,
        PersonalInfo personalInfo,
        ContactInfo contactInfo,
        FamilyInfo familyInfo,
        String createdBy
    ) {
        // Validate age
        int age = calculateAge(personalInfo.getDateOfBirth());
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new InvalidAgeException(age);
        }

        return new Student(
            null,  // ID will be assigned by repository
            studentId,
            personalInfo,
            contactInfo,
            familyInfo,
            StudentStatus.ACTIVE,  // Default status
            AuditInfo.forCreation(createdBy),
            0L  // Initial version
        );
    }

    /**
     * Factory method to recreate a student from repository.
     *
     * @param id the database ID
     * @param studentId the business key
     * @param personalInfo personal information
     * @param contactInfo contact information
     * @param familyInfo family information
     * @param status the student status
     * @param auditInfo audit information
     * @param version the version for optimistic locking
     * @return a Student instance
     */
    public static Student fromRepository(
        Long id,
        StudentId studentId,
        PersonalInfo personalInfo,
        ContactInfo contactInfo,
        FamilyInfo familyInfo,
        StudentStatus status,
        AuditInfo auditInfo,
        Long version
    ) {
        return new Student(
            id,
            studentId,
            personalInfo,
            contactInfo,
            familyInfo,
            status,
            auditInfo,
            version
        );
    }

    /**
     * Updates the student's personal information.
     * Only firstName and lastName can be updated.
     *
     * @param firstName the new first name
     * @param lastName the new last name
     * @param updatedBy the user making the update
     * @return a new Student instance with updated information
     */
    public Student updatePersonalInfo(String firstName, String lastName, String updatedBy) {
        PersonalInfo updatedInfo = new PersonalInfo(
            firstName,
            lastName,
            this.personalInfo.getDateOfBirth(),
            this.personalInfo.getIdentificationMark(),
            this.personalInfo.getAadhaarNumber()
        );

        return new Student(
            this.id,
            this.studentId,
            updatedInfo,
            this.contactInfo,
            this.familyInfo,
            this.status,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }

    /**
     * Updates the student's contact information.
     * Only mobile number can be updated.
     *
     * @param mobile the new mobile number
     * @param updatedBy the user making the update
     * @return a new Student instance with updated contact info
     */
    public Student updateContactInfo(String mobile, String updatedBy) {
        ContactInfo updatedContact = new ContactInfo(
            mobile,
            this.contactInfo.getEmail(),
            this.contactInfo.getAddress()
        );

        return new Student(
            this.id,
            this.studentId,
            this.personalInfo,
            updatedContact,
            this.familyInfo,
            this.status,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }

    /**
     * Activates the student.
     *
     * @param updatedBy the user making the update
     * @return a new Student instance with ACTIVE status
     */
    public Student activate(String updatedBy) {
        if (this.status == StudentStatus.ACTIVE) {
            return this;  // Already active, no change needed
        }

        return new Student(
            this.id,
            this.studentId,
            this.personalInfo,
            this.contactInfo,
            this.familyInfo,
            StudentStatus.ACTIVE,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }

    /**
     * Deactivates the student.
     *
     * @param updatedBy the user making the update
     * @return a new Student instance with INACTIVE status
     */
    public Student deactivate(String updatedBy) {
        if (this.status == StudentStatus.INACTIVE) {
            return this;  // Already inactive, no change needed
        }

        return new Student(
            this.id,
            this.studentId,
            this.personalInfo,
            this.contactInfo,
            this.familyInfo,
            StudentStatus.INACTIVE,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }

    /**
     * Calculates the current age of the student.
     *
     * @return the age in years
     */
    public int calculateAge() {
        return calculateAge(this.personalInfo.getDateOfBirth());
    }

    /**
     * Calculates age from a date of birth.
     *
     * @param dateOfBirth the date of birth
     * @return the age in years
     */
    private static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Checks if the student is currently active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }
}
