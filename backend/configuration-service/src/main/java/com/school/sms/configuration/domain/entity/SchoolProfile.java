package com.school.sms.configuration.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Domain entity representing the school profile.
 * Only one record should exist in the database (enforced by constraint).
 */
@Entity
@Table(name = "school_profile")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class SchoolProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "school_code", nullable = false, unique = true, length = 20)
    private String schoolCode;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(length = 500)
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(length = 100)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy = "SYSTEM";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "SYSTEM";

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Constructor for creating a new school profile.
     *
     * @param schoolName the school name
     * @param schoolCode the unique school code
     * @param logoPath   optional logo path
     * @param address    optional address
     * @param phone      optional phone number
     * @param email      optional email
     */
    public SchoolProfile(String schoolName, String schoolCode, String logoPath,
                         String address, String phone, String email) {
        validateSchoolName(schoolName);
        validateSchoolCode(schoolCode);
        this.schoolName = schoolName;
        this.schoolCode = schoolCode;
        this.logoPath = logoPath;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Update school profile information.
     *
     * @param schoolName the new school name
     * @param schoolCode the new school code
     * @param logoPath   the new logo path
     * @param address    the new address
     * @param phone      the new phone
     * @param email      the new email
     */
    public void updateProfile(String schoolName, String schoolCode, String logoPath,
                              String address, String phone, String email) {
        validateSchoolName(schoolName);
        validateSchoolCode(schoolCode);
        this.schoolName = schoolName;
        this.schoolCode = schoolCode;
        this.logoPath = logoPath;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Set the user who is updating this profile.
     *
     * @param userId the user ID
     */
    public void setUpdatedBy(String userId) {
        this.updatedBy = userId;
    }

    /**
     * Set the user who created this profile.
     *
     * @param userId the user ID
     */
    public void setCreatedBy(String userId) {
        this.createdBy = userId;
    }

    /**
     * Validate school name.
     *
     * @param schoolName the name to validate
     */
    private void validateSchoolName(String schoolName) {
        if (schoolName == null || schoolName.isBlank()) {
            throw new IllegalArgumentException("School name cannot be null or empty");
        }
    }

    /**
     * Validate school code.
     *
     * @param schoolCode the code to validate
     */
    private void validateSchoolCode(String schoolCode) {
        if (schoolCode == null || schoolCode.isBlank()) {
            throw new IllegalArgumentException("School code cannot be null or empty");
        }
    }
}
