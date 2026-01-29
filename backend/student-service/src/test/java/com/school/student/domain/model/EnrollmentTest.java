package com.school.student.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Enrollment domain model
 * Pure domain logic tests - no Spring context, no mocks
 */
@DisplayName("Enrollment Domain Model Tests")
class EnrollmentTest {

    @Test
    @DisplayName("Should create enrollment with valid data")
    void shouldCreateEnrollmentWithValidData() {
        // Given
        Long studentId = 1L;
        String academicYear = "2025-2026";
        String gradeClass = "5";
        String section = "A";
        LocalDate enrollmentDate = LocalDate.of(2025, 6, 1);

        // When
        Enrollment enrollment = new Enrollment(studentId, academicYear, gradeClass, section, enrollmentDate);

        // Then
        assertThat(enrollment.getStudentId()).isEqualTo(studentId);
        assertThat(enrollment.getAcademicYear()).isEqualTo(academicYear);
        assertThat(enrollment.getGradeClass()).isEqualTo(gradeClass);
        assertThat(enrollment.getSection()).isEqualTo(section);
        assertThat(enrollment.getEnrollmentDate()).isEqualTo(enrollmentDate);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should withdraw enrollment with valid data")
    void shouldWithdrawEnrollmentWithValidData() {
        // Given
        Enrollment enrollment = new Enrollment(1L, "2025-2026", "5", "A", LocalDate.of(2025, 6, 1));
        LocalDate withdrawalDate = LocalDate.of(2025, 12, 15);
        String remarks = "Family relocation";

        // When
        enrollment.withdraw(withdrawalDate, remarks);

        // Then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        assertThat(enrollment.getWithdrawalDate()).isEqualTo(withdrawalDate);
        assertThat(enrollment.getRemarks()).isEqualTo(remarks);
    }

    @Test
    @DisplayName("Should throw exception when withdrawal date is before enrollment date")
    void shouldThrowExceptionWhenWithdrawalDateBeforeEnrollmentDate() {
        // Given
        LocalDate enrollmentDate = LocalDate.of(2025, 6, 1);
        Enrollment enrollment = new Enrollment(1L, "2025-2026", "5", "A", enrollmentDate);
        LocalDate invalidWithdrawalDate = LocalDate.of(2025, 5, 1); // Before enrollment date

        // When & Then
        assertThatThrownBy(() -> enrollment.withdraw(invalidWithdrawalDate, "Invalid date"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Withdrawal date cannot be before enrollment date");
    }

    @Test
    @DisplayName("Should allow withdrawal on same day as enrollment")
    void shouldAllowWithdrawalOnSameDayAsEnrollment() {
        // Given
        LocalDate enrollmentDate = LocalDate.of(2025, 6, 1);
        Enrollment enrollment = new Enrollment(1L, "2025-2026", "5", "A", enrollmentDate);

        // When
        enrollment.withdraw(enrollmentDate, "Immediate withdrawal");

        // Then
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        assertThat(enrollment.getWithdrawalDate()).isEqualTo(enrollmentDate);
    }
}
