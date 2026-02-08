package com.school.student.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDate;

/**
 * Enrollment domain model representing student's academic year enrollment.
 *
 * <p>Business Rules:
 * <ul>
 *   <li>BR-3: One enrollment per student per academic year</li>
 *   <li>Withdrawal date must be after enrollment date</li>
 *   <li>Only ACTIVE enrollments can be withdrawn</li>
 * </ul>
 *
 * <p>Following Domain-Driven Design:
 * <ul>
 *   <li>Rich domain model with business logic</li>
 *   <li>Encapsulates enrollment state transitions</li>
 *   <li>No JPA annotations (pure business logic)</li>
 * </ul>
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Enrollment {

    /**
     * Internal database identifier (set after persistence)
     */
    @Setter
    private Long id;

    /**
     * Reference to the student's internal ID
     */
    private final Long studentId;

    /**
     * Academic year in format "YYYY-YYYY" (e.g., "2025-2026")
     */
    private final String academicYear;

    /**
     * Grade/class level (e.g., "Grade 5", "Pre-K")
     */
    private final String gradeClass;

    /**
     * Section identifier (e.g., "A", "B", "C")
     */
    private final String section;

    /**
     * Date when student enrolled
     */
    private final LocalDate enrollmentDate;

    /**
     * Date when student withdrew (null if active)
     */
    @Setter
    private LocalDate withdrawalDate;

    /**
     * Enrollment status
     */
    @Setter
    private EnrollmentStatus status;

    /**
     * Additional notes or comments
     */
    @Setter
    private String remarks;

    /**
     * Optimistic locking version (set after persistence)
     */
    @Setter
    private Long version;

    /**
     * Audit: Creation timestamp (set after persistence)
     */
    @Setter
    private LocalDate createdAt;

    /**
     * Factory method to create a new enrollment.
     * Enforces invariants and business rules.
     *
     * @param studentId the student's internal ID
     * @param academicYear the academic year (format: "YYYY-YYYY")
     * @param gradeClass the grade/class level
     * @param section the section identifier
     * @param enrollmentDate the enrollment date
     * @param remarks optional remarks
     * @return new Enrollment instance in ACTIVE status
     * @throws IllegalArgumentException if any required field is invalid
     */
    public static Enrollment enroll(Long studentId, String academicYear, String gradeClass,
                                    String section, LocalDate enrollmentDate, String remarks) {
        // Trim strings before validation
        String trimmedAcademicYear = academicYear != null ? academicYear.trim() : null;
        String trimmedGradeClass = gradeClass != null ? gradeClass.trim() : null;
        String trimmedSection = section != null ? section.trim() : null;
        String trimmedRemarks = remarks != null ? remarks.trim() : null;

        // Validate with trimmed values
        validateRequiredFields(studentId, trimmedAcademicYear, trimmedGradeClass, trimmedSection, enrollmentDate);
        validateAcademicYearFormat(trimmedAcademicYear);
        validateEnrollmentDate(enrollmentDate);

        return Enrollment.builder()
                .studentId(studentId)
                .academicYear(trimmedAcademicYear)
                .gradeClass(trimmedGradeClass)
                .section(trimmedSection)
                .enrollmentDate(enrollmentDate)
                .withdrawalDate(null)
                .status(EnrollmentStatus.ACTIVE)
                .remarks(trimmedRemarks)
                .version(0L)
                .build();
    }

    /**
     * Marks the enrollment as withdrawn.
     * Business rule: Can only withdraw ACTIVE enrollments.
     *
     * @param withdrawalDate the date of withdrawal
     * @param remarks optional withdrawal reason
     * @throws IllegalStateException if enrollment is not ACTIVE
     * @throws IllegalArgumentException if withdrawalDate is before enrollmentDate
     */
    public void withdraw(LocalDate withdrawalDate, String remarks) {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot withdraw enrollment with status: " + this.status);
        }
        if (withdrawalDate == null) {
            throw new IllegalArgumentException("Withdrawal date cannot be null");
        }
        if (withdrawalDate.isBefore(this.enrollmentDate)) {
            throw new IllegalArgumentException("Withdrawal date cannot be before enrollment date");
        }

        this.withdrawalDate = withdrawalDate;
        this.status = EnrollmentStatus.WITHDRAWN;
        this.remarks = remarks != null ? remarks.trim() : this.remarks;
    }

    /**
     * Marks the enrollment as completed (end of academic year).
     * Business rule: Can only complete ACTIVE enrollments.
     *
     * @throws IllegalStateException if enrollment is not ACTIVE
     */
    public void complete() {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot complete enrollment with status: " + this.status);
        }
        this.status = EnrollmentStatus.COMPLETED;
    }

    /**
     * Checks if the enrollment is currently active.
     *
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isActive() {
        return this.status == EnrollmentStatus.ACTIVE;
    }

    // Private validation methods

    private static void validateRequiredFields(Long studentId, String academicYear,
                                              String gradeClass, String section,
                                              LocalDate enrollmentDate) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (academicYear == null || academicYear.trim().isEmpty()) {
            throw new IllegalArgumentException("Academic year cannot be null or empty");
        }
        if (gradeClass == null || gradeClass.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade/class cannot be null or empty");
        }
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        if (enrollmentDate == null) {
            throw new IllegalArgumentException("Enrollment date cannot be null");
        }
    }

    private static void validateAcademicYearFormat(String academicYear) {
        // Expected format: "YYYY-YYYY" (e.g., "2025-2026")
        if (!academicYear.matches("\\d{4}-\\d{4}")) {
            throw new IllegalArgumentException("Academic year must be in format YYYY-YYYY (e.g., 2025-2026)");
        }

        String[] years = academicYear.split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);

        if (endYear != startYear + 1) {
            throw new IllegalArgumentException("Academic year must be consecutive years (e.g., 2025-2026)");
        }
    }

    private static void validateEnrollmentDate(LocalDate enrollmentDate) {
        if (enrollmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Enrollment date cannot be in the future");
        }
    }
}
