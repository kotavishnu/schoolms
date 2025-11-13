package com.school.management.domain.student;

import com.school.management.domain.base.BaseEntity;
import com.school.management.infrastructure.security.EncryptionService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

/**
 * Student domain entity
 * Represents a student in the school management system
 * Contains encrypted PII fields for data protection
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_student_code", columnList = "student_code"),
    @Index(name = "idx_students_status", columnList = "status"),
    @Index(name = "idx_students_mobile_hash", columnList = "mobile_hash"),
    @Index(name = "idx_students_admission_date", columnList = "admission_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    /**
     * Unique student code (e.g., STU-2025-00001)
     */
    @Column(nullable = false, unique = true, length = 20, name = "student_code")
    private String studentCode;

    /**
     * Encrypted first name (PII)
     */
    @Column(nullable = false, name = "first_name_encrypted")
    private byte[] firstNameEncrypted;

    /**
     * Encrypted last name (PII)
     */
    @Column(nullable = false, name = "last_name_encrypted")
    private byte[] lastNameEncrypted;

    /**
     * Encrypted date of birth (PII)
     */
    @Column(nullable = false, name = "date_of_birth_encrypted")
    private byte[] dateOfBirthEncrypted;

    /**
     * Gender of the student
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    /**
     * Encrypted email address (PII)
     */
    @Column(name = "email_encrypted")
    private byte[] emailEncrypted;

    /**
     * Encrypted mobile number (PII)
     */
    @Column(nullable = false, name = "mobile_encrypted")
    private byte[] mobileEncrypted;

    /**
     * SHA-256 hash of mobile number for searchability
     */
    @Column(nullable = false, name = "mobile_hash", length = 64)
    private String mobileHash;

    /**
     * Encrypted address (PII)
     */
    @Column(name = "address_encrypted")
    private byte[] addressEncrypted;

    /**
     * Blood group (e.g., A+, B-, O+, AB+)
     */
    @Column(length = 5, name = "blood_group")
    private String bloodGroup;

    /**
     * Current status of the student
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.ACTIVE;

    /**
     * Date when student was admitted to the school
     */
    @Column(nullable = false, name = "admission_date")
    private LocalDate admissionDate;

    /**
     * URL to student's photo
     */
    @Column(name = "photo_url")
    private String photoUrl;

    /**
     * Guardians associated with this student
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Guardian> guardians = new HashSet<>();

    /**
     * Class enrollments for this student
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    /**
     * Fee journals for this student
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FeeJournal> feeJournals = new HashSet<>();

    // Transient fields for decrypted data (not persisted)

    @Transient
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private LocalDate dateOfBirth;

    @Transient
    private String email;

    @Transient
    private String mobile;

    @Transient
    private String address;

    /**
     * Encrypts all PII fields using the provided encryption service
     *
     * @param encryptionService the service to use for encryption
     */
    public void encryptFields(EncryptionService encryptionService) {
        if (firstName != null) {
            this.firstNameEncrypted = encryptionService.encrypt(firstName);
        }
        if (lastName != null) {
            this.lastNameEncrypted = encryptionService.encrypt(lastName);
        }
        if (dateOfBirth != null) {
            this.dateOfBirthEncrypted = encryptionService.encrypt(dateOfBirth.toString());
        }
        if (email != null) {
            this.emailEncrypted = encryptionService.encrypt(email);
        }
        if (mobile != null) {
            this.mobileEncrypted = encryptionService.encrypt(mobile);
            this.mobileHash = encryptionService.hashMobile(mobile);
        }
        if (address != null) {
            this.addressEncrypted = encryptionService.encrypt(address);
        }
    }

    /**
     * Decrypts all PII fields using the provided encryption service
     * Populates transient fields with decrypted values
     *
     * @param encryptionService the service to use for decryption
     */
    public void decryptFields(EncryptionService encryptionService) {
        if (firstNameEncrypted != null) {
            this.firstName = encryptionService.decrypt(firstNameEncrypted);
        }
        if (lastNameEncrypted != null) {
            this.lastName = encryptionService.decrypt(lastNameEncrypted);
        }
        if (dateOfBirthEncrypted != null) {
            String dobString = encryptionService.decrypt(dateOfBirthEncrypted);
            this.dateOfBirth = LocalDate.parse(dobString);
        }
        if (emailEncrypted != null) {
            this.email = encryptionService.decrypt(emailEncrypted);
        }
        if (mobileEncrypted != null) {
            this.mobile = encryptionService.decrypt(mobileEncrypted);
        }
        if (addressEncrypted != null) {
            this.address = encryptionService.decrypt(addressEncrypted);
        }
    }

    /**
     * Checks if the student is currently active
     *
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isActive() {
        return StudentStatus.ACTIVE.equals(status);
    }

    /**
     * Calculates the current age of the student based on date of birth
     *
     * @return age in years, or 0 if date of birth is not set
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Gets the full name of the student
     *
     * @return concatenated first and last name
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
