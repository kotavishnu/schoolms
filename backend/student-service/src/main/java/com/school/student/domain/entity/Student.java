package com.school.student.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student Entity - Domain Model
 *
 * Represents a student in the school management system.
 * Contains all student information including personal details,
 * contact information, and enrollment status.
 *
 * Business Rules:
 * - BR-STU-001: Age must be between 3 and 18 years
 * - BR-STU-002: Mobile number must be unique
 * - BR-STU-007: Only firstName, lastName, mobile, and status are editable
 *
 * @see com.school.student.domain.entity.StudentStatus
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    /**
     * Primary key (internal database ID)
     * Not exposed to clients - use studentId instead
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business key (student identifier)
     * Format: STD-YYYYMMDD-NNNN
     * Example: STD-20260122-0001
     */
    @Column(name = "student_id", unique = true, nullable = false, length = 50)
    private String studentId;

    // ==================== Personal Information ====================

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Date of birth - used for age calculation
     * Must satisfy: 3 years <= age <= 18 years
     */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Aadhaar number (optional, 12 digits)
     * Unique if provided
     */
    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Column(name = "identification_marks", length = 200)
    private String identificationMarks;

    // ==================== Guardian Information ====================

    @Column(name = "guardian_name", nullable = false, length = 100)
    private String guardianName;

    @Column(name = "mother_name", nullable = false, length = 100)
    private String motherName;

    // ==================== Contact Information ====================

    /**
     * Mobile number (10 digits, unique)
     * BR-STU-002: Must be unique across all students
     */
    @Column(name = "mobile", unique = true, nullable = false, length = 10)
    private String mobile;

    /**
     * Email address (unique)
     */
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    // ==================== Status ====================

    /**
     * Current enrollment status
     * Default: ACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;

    // ==================== Optimistic Locking (D-003) ====================

    /**
     * Version field for optimistic locking
     * Prevents concurrent modification conflicts
     * MANDATORY per Global Directive D-003
     */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // ==================== Audit Fields ====================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== Business Logic ====================

    /**
     * Generate unique student ID
     * Format: STD-YYYYMMDD-NNNN
     * Called during student registration
     */
    public void generateStudentId() {
        if (this.studentId == null) {
            this.studentId = generateUniqueStudentId();
        }
    }

    /**
     * Activate student (change status to ACTIVE)
     */
    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Deactivate student (change status to INACTIVE)
     */
    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Check if student is currently active
     */
    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }

    /**
     * Calculate age in years based on date of birth
     */
    public int getAge() {
        if (this.dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - this.dateOfBirth.getYear();
    }

    // ==================== Private Helpers ====================

    private String generateUniqueStudentId() {
        // Format: STD-YYYYMMDD-NNNN
        // Example: STD-20260122-0001
        String datePart = LocalDate.now().toString().replace("-", "");
        String randomPart = String.format("%04d", (int)(Math.random() * 10000));
        return "STD-" + datePart + "-" + randomPart;
    }
}
