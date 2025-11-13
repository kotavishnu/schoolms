package com.school.management.domain.student;

import com.school.management.domain.academic.SchoolClass;
import com.school.management.domain.base.BaseEntity;
import com.school.management.shared.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Enrollment domain entity
 * Represents a student's enrollment in a class
 *
 * Business Rules:
 * - Student can only be enrolled in one class per academic year
 * - Withdrawal requires date and reason
 * - Cannot modify enrollment after withdrawal
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "enrollments",
    indexes = {
        @Index(name = "idx_enrollments_student_id", columnList = "student_id"),
        @Index(name = "idx_enrollments_class_id", columnList = "class_id"),
        @Index(name = "idx_enrollments_status", columnList = "status"),
        @Index(name = "idx_enrollments_date", columnList = "enrollment_date")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_student_class",
            columnNames = {"student_id", "class_id"}
        )
    })
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass enrolledClass;

    @Column(nullable = false, name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "withdrawal_reason", length = 500)
    private String withdrawalReason;

    @Column(name = "promoted_to_class_id")
    private Long promotedToClassId;

    /**
     * Validate withdrawal data before persistence
     */
    @PrePersist
    @PreUpdate
    private void validateWithdrawal() {
        if (status == EnrollmentStatus.WITHDRAWN) {
            if (withdrawalDate == null) {
                throw new ValidationException(
                    "Withdrawal date is required when status is WITHDRAWN");
            }
            if (withdrawalDate.isBefore(enrollmentDate)) {
                throw new ValidationException(
                    "Withdrawal date cannot be before enrollment date");
            }
        } else {
            // Clear withdrawal data for non-withdrawn status
            this.withdrawalDate = null;
            this.withdrawalReason = null;
        }
    }

    /**
     * Check if enrollment is active
     * @return true if enrolled, false otherwise
     */
    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED;
    }

    /**
     * Check if enrollment is withdrawn
     * @return true if withdrawn, false otherwise
     */
    public boolean isWithdrawn() {
        return status == EnrollmentStatus.WITHDRAWN;
    }

    /**
     * Check if student was promoted
     * @return true if promoted, false otherwise
     */
    public boolean isPromoted() {
        return status == EnrollmentStatus.PROMOTED;
    }

    /**
     * Get enrollment duration in days
     * @return duration in days
     */
    public long getEnrollmentDurationInDays() {
        LocalDate endDate = withdrawalDate != null ? withdrawalDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(enrollmentDate, endDate);
    }
}
