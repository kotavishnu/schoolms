package com.school.student.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain Model for Enrollment
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    private LocalDateTime createdAt;

    public Enrollment(Long studentId, String academicYear, String gradeClass, String section, LocalDate enrollmentDate) {
        this.studentId = studentId;
        this.academicYear = academicYear;
        this.gradeClass = gradeClass;
        this.section = section;
        this.enrollmentDate = enrollmentDate;
        this.status = EnrollmentStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public void withdraw(LocalDate withdrawalDate, String remarks) {
        if (withdrawalDate.isBefore(this.enrollmentDate)) {
            throw new IllegalArgumentException("Withdrawal date cannot be before enrollment date");
        }
        this.withdrawalDate = withdrawalDate;
        this.status = EnrollmentStatus.WITHDRAWN;
        this.remarks = remarks;
    }

    // Setters for MapStruct and JPA
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public void setGradeClass(String gradeClass) {
        this.gradeClass = gradeClass;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setWithdrawalDate(LocalDate withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
