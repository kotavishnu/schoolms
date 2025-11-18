package com.school.sms.student.domain.entity;

import com.school.sms.student.domain.valueobject.Address;
import com.school.sms.student.domain.valueobject.StudentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Student entity representing a student in the school management system.
 * This is an aggregate root in the domain model.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @Column(name = "student_id", length = 50)
    private String studentId;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Embedded
    private Address address;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", length = 10, nullable = false, unique = true)
    private String mobile;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "father_name_guardian", length = 100, nullable = false)
    private String fatherNameOrGuardian;

    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "caste", length = 50)
    private String caste;

    @Column(name = "moles", columnDefinition = "TEXT")
    private String moles;

    @Column(name = "aadhaar_number", length = 12)
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "updated_by", length = 50, nullable = false)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * JPA lifecycle callback executed before entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA lifecycle callback executed before entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the current age of the student based on date of birth.
     *
     * @return age in years
     */
    public int calculateAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Checks if the student is currently active.
     *
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    /**
     * Activates the student by setting status to ACTIVE.
     */
    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Deactivates the student by setting status to INACTIVE.
     */
    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Returns the full name of the student.
     *
     * @return full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
