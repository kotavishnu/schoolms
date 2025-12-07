package com.school.sms.student.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity representing a student's enrollment history record.
 *
 * <p>Tracks a student's enrollment in a specific academic year, grade, and section.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Enrollment {

    private Long id;
    private Long studentId;
    private String academicYear;
    private String gradeClass;
    private String section;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private EnrollmentStatus status;
    private String remarks;
    private AuditInfo auditInfo;
    private Long version;

    /**
     * Factory method to create a new enrollment record.
     *
     * @param studentId the student's database ID
     * @param academicYear the academic year (format: YYYY-YYYY)
     * @param gradeClass the grade/class
     * @param section the section
     * @param enrollmentDate the enrollment date
     * @param createdBy the user creating this enrollment
     * @return a new Enrollment instance
     */
    public static Enrollment createNew(
        Long studentId,
        String academicYear,
        String gradeClass,
        String section,
        LocalDate enrollmentDate,
        String createdBy
    ) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(academicYear, "Academic year cannot be null");
        Objects.requireNonNull(gradeClass, "Grade/Class cannot be null");
        Objects.requireNonNull(enrollmentDate, "Enrollment date cannot be null");

        return new Enrollment(
            null,  // ID will be assigned by repository
            studentId,
            academicYear,
            gradeClass,
            section,
            enrollmentDate,
            null,  // No withdrawal date initially
            EnrollmentStatus.ENROLLED,  // Default status
            null,  // No remarks initially
            AuditInfo.forCreation(createdBy),
            0L  // Initial version
        );
    }

    /**
     * Factory method to recreate an enrollment from repository.
     *
     * @param id the database ID
     * @param studentId the student's database ID
     * @param academicYear the academic year
     * @param gradeClass the grade/class
     * @param section the section
     * @param enrollmentDate the enrollment date
     * @param withdrawalDate the withdrawal date (if any)
     * @param status the enrollment status
     * @param remarks any remarks
     * @param auditInfo audit information
     * @param version the version for optimistic locking
     * @return an Enrollment instance
     */
    public static Enrollment fromRepository(
        Long id,
        Long studentId,
        String academicYear,
        String gradeClass,
        String section,
        LocalDate enrollmentDate,
        LocalDate withdrawalDate,
        EnrollmentStatus status,
        String remarks,
        AuditInfo auditInfo,
        Long version
    ) {
        return new Enrollment(
            id,
            studentId,
            academicYear,
            gradeClass,
            section,
            enrollmentDate,
            withdrawalDate,
            status,
            remarks,
            auditInfo,
            version
        );
    }

    /**
     * Completes this enrollment (at end of academic year).
     *
     * @param updatedBy the user making the update
     * @return a new Enrollment instance with COMPLETED status
     */
    public Enrollment complete(String updatedBy) {
        return new Enrollment(
            this.id,
            this.studentId,
            this.academicYear,
            this.gradeClass,
            this.section,
            this.enrollmentDate,
            this.withdrawalDate,
            EnrollmentStatus.COMPLETED,
            this.remarks,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }

    /**
     * Withdraws this enrollment.
     *
     * @param withdrawalDate the date of withdrawal
     * @param remarks the reason for withdrawal
     * @param updatedBy the user making the update
     * @return a new Enrollment instance with WITHDRAWN status
     */
    public Enrollment withdraw(LocalDate withdrawalDate, String remarks, String updatedBy) {
        Objects.requireNonNull(withdrawalDate, "Withdrawal date cannot be null");

        if (withdrawalDate.isBefore(this.enrollmentDate)) {
            throw new IllegalArgumentException("Withdrawal date cannot be before enrollment date");
        }

        return new Enrollment(
            this.id,
            this.studentId,
            this.academicYear,
            this.gradeClass,
            this.section,
            this.enrollmentDate,
            withdrawalDate,
            EnrollmentStatus.WITHDRAWN,
            remarks,
            this.auditInfo.withUpdate(updatedBy),
            this.version
        );
    }
}
