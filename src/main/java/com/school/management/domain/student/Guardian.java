package com.school.management.domain.student;

import com.school.management.domain.base.BaseEntity;
import com.school.management.infrastructure.security.EncryptionService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Guardian domain entity (placeholder for BE-S2-04)
 * Represents a guardian/parent of a student
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "guardians", indexes = {
    @Index(name = "idx_guardians_student_id", columnList = "student_id"),
    @Index(name = "idx_guardians_is_primary", columnList = "is_primary")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Guardian extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Relationship relationship;

    @Column(nullable = false, name = "first_name_encrypted")
    private byte[] firstNameEncrypted;

    @Column(nullable = false, name = "last_name_encrypted")
    private byte[] lastNameEncrypted;

    @Column(nullable = false, name = "mobile_encrypted")
    private byte[] mobileEncrypted;

    @Column(nullable = false, name = "mobile_hash", length = 64)
    private String mobileHash;

    @Column(name = "email_encrypted")
    private byte[] emailEncrypted;

    @Column(length = 100)
    private String occupation;

    @Column(nullable = false, columnDefinition = "boolean default false", name = "is_primary")
    private Boolean isPrimary = false;

    // Transient fields for decrypted data
    @Transient
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private String mobile;

    @Transient
    private String email;

    public void encryptFields(EncryptionService encryptionService) {
        if (firstName != null) {
            this.firstNameEncrypted = encryptionService.encrypt(firstName);
        }
        if (lastName != null) {
            this.lastNameEncrypted = encryptionService.encrypt(lastName);
        }
        if (mobile != null) {
            this.mobileEncrypted = encryptionService.encrypt(mobile);
            this.mobileHash = encryptionService.hashMobile(mobile);
        }
        if (email != null) {
            this.emailEncrypted = encryptionService.encrypt(email);
        }
    }

    public void decryptFields(EncryptionService encryptionService) {
        if (firstNameEncrypted != null) {
            this.firstName = encryptionService.decrypt(firstNameEncrypted);
        }
        if (lastNameEncrypted != null) {
            this.lastName = encryptionService.decrypt(lastNameEncrypted);
        }
        if (mobileEncrypted != null) {
            this.mobile = encryptionService.decrypt(mobileEncrypted);
        }
        if (emailEncrypted != null) {
            this.email = encryptionService.decrypt(emailEncrypted);
        }
    }

    /**
     * Get full name of guardian
     * @return full name (firstName + lastName)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }
}
