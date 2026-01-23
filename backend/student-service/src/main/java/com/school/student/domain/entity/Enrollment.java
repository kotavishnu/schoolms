package com.school.student.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Enrollment Entity - Domain Model
 *
 * Represents a student's enrollment in a specific grade/class
 * for an academic year. Tracks the complete enrollment history
 * including withdrawals and completions.
 *
 * @see com.school.student.domain.entity.Student
 * @see com.school.student.domain.entity.EnrollmentStatus
 */
@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the student
     * Many enrollments can belong to one student
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Academic year in format YYYY-YYYY
     * Example: 2025-2026
     */
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    /**
     * Grade/Class identifier
     * Example: Grade 5, Class 10-A
     */
    @Column(name = "grade_class", nullable = false, length = 50)
    private String gradeClass;

    /**
     * Section within the grade
     * Example: A, B, C
     */
    @Column(name = "section", nullable = false, length = 10)
    private String section;

    /**
     * Date when student enrolled in this grade/class
     */
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    /**
     * Date when student withdrew from this grade/class
     * Null if still enrolled or completed
     */
    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    /**
     * Current status of this enrollment
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    /**
     * Additional notes or remarks about the enrollment
     */
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // ==================== Optimistic Locking (D-003) ====================

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // ==================== Audit Fields ====================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==================== Business Logic ====================

    /**
     * Mark enrollment as withdrawn
     */
    public void withdraw(LocalDate withdrawalDate, String remarks) {
        this.status = EnrollmentStatus.WITHDRAWN;
        this.withdrawalDate = withdrawalDate;
        this.remarks = remarks;
    }

    /**
     * Mark enrollment as completed
     */
    public void complete(String remarks) {
        this.status = EnrollmentStatus.COMPLETED;
        this.remarks = remarks;
    }

    /**
     * Check if enrollment is currently active
     */
    public boolean isActive() {
        return this.status == EnrollmentStatus.ACTIVE;
    }
}
