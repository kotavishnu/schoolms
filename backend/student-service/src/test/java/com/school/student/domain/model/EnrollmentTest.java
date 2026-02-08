package com.school.student.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Enrollment domain model.
 * Following TDD methodology and testing all business rules.
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Factory method enrollment creation</li>
 *   <li>BR-3: Academic year format validation</li>
 *   <li>Date validation (enrollment date, withdrawal date)</li>
 *   <li>Status transitions (ACTIVE → WITHDRAWN, ACTIVE → COMPLETED)</li>
 *   <li>Edge cases and boundary conditions</li>
 * </ul>
 */
@DisplayName("Enrollment Domain Model Tests")
class EnrollmentTest {

    // Test Data
    private static final Long STUDENT_ID = 1L;
    private static final String VALID_ACADEMIC_YEAR = "2025-2026";
    private static final String VALID_GRADE = "Grade 5";
    private static final String VALID_SECTION = "A";
    private static final LocalDate ENROLLMENT_DATE = LocalDate.of(2025, 6, 1);

    // ==================== Factory Method Tests ====================

    @Test
    @DisplayName("Should create enrollment with valid data")
    void shouldCreateEnrollmentWithValidData() {
        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                "Regular enrollment"
        );

        // Assert
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getStudentId()).isEqualTo(STUDENT_ID);
        assertThat(enrollment.getAcademicYear()).isEqualTo(VALID_ACADEMIC_YEAR);
        assertThat(enrollment.getGradeClass()).isEqualTo(VALID_GRADE);
        assertThat(enrollment.getSection()).isEqualTo(VALID_SECTION);
        assertThat(enrollment.getEnrollmentDate()).isEqualTo(ENROLLMENT_DATE);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getWithdrawalDate()).isNull();
        assertThat(enrollment.getRemarks()).isEqualTo("Regular enrollment");
        assertThat(enrollment.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should create enrollment with null remarks")
    void shouldCreateEnrollmentWithNullRemarks() {
        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );

        // Assert
        assertThat(enrollment.getRemarks()).isNull();
    }

    @Test
    @DisplayName("Should trim whitespace from all string fields")
    void shouldTrimWhitespaceFromFields() {
        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                "  2025-2026  ",
                "  Grade 5  ",
                "  A  ",
                ENROLLMENT_DATE,
                "  Some remarks  "
        );

        // Assert
        assertThat(enrollment.getAcademicYear()).isEqualTo("2025-2026");
        assertThat(enrollment.getGradeClass()).isEqualTo("Grade 5");
        assertThat(enrollment.getSection()).isEqualTo("A");
        assertThat(enrollment.getRemarks()).isEqualTo("Some remarks");
    }

    // ==================== Required Fields Validation ====================

    @Test
    @DisplayName("Should reject null student ID")
    void shouldRejectNullStudentId() {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                null,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Student ID cannot be null");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should reject null or empty academic year")
    void shouldRejectInvalidAcademicYear(String academicYear) {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                academicYear,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Academic year cannot be null or empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should reject null or empty grade/class")
    void shouldRejectInvalidGradeClass(String gradeClass) {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                gradeClass,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Grade/class cannot be null or empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should reject null or empty section")
    void shouldRejectInvalidSection(String section) {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                section,
                ENROLLMENT_DATE,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Section cannot be null or empty");
    }

    @Test
    @DisplayName("Should reject null enrollment date")
    void shouldRejectNullEnrollmentDate() {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                null,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Enrollment date cannot be null");
    }

    // ==================== Academic Year Format Validation ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "2025-2027",  // Non-consecutive years
            "2026-2025",  // Reversed years
            "2025-2025",  // Same year
            "25-26",      // Invalid format (2 digits)
            "2025/2026",  // Wrong separator
            "2025-26",    // Mixed format
            "abcd-efgh"   // Non-numeric
    })
    @DisplayName("Should reject invalid academic year formats")
    void shouldRejectInvalidAcademicYearFormat(String invalidYear) {
        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                invalidYear,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Academic year");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-2025", "2025-2026", "2026-2027"})
    @DisplayName("Should accept valid academic year formats")
    void shouldAcceptValidAcademicYearFormats(String validYear) {
        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                validYear,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );

        // Assert
        assertThat(enrollment.getAcademicYear()).isEqualTo(validYear);
    }

    // ==================== Date Validation ====================

    @Test
    @DisplayName("Should reject future enrollment date")
    void shouldRejectFutureEnrollmentDate() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);

        // Act & Assert
        assertThatThrownBy(() -> Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                futureDate,
                null
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Enrollment date cannot be in the future");
    }

    @Test
    @DisplayName("Should accept today as enrollment date")
    void shouldAcceptTodayAsEnrollmentDate() {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                today,
                null
        );

        // Assert
        assertThat(enrollment.getEnrollmentDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("Should accept past enrollment date")
    void shouldAcceptPastEnrollmentDate() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusYears(1);

        // Act
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                pastDate,
                null
        );

        // Assert
        assertThat(enrollment.getEnrollmentDate()).isEqualTo(pastDate);
    }

    // ==================== Withdrawal Tests ====================

    @Test
    @DisplayName("Should withdraw ACTIVE enrollment successfully")
    void shouldWithdrawActiveEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        LocalDate withdrawalDate = ENROLLMENT_DATE.plusMonths(3);

        // Act
        enrollment.withdraw(withdrawalDate, "Family relocation");

        // Assert
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        assertThat(enrollment.getWithdrawalDate()).isEqualTo(withdrawalDate);
        assertThat(enrollment.getRemarks()).isEqualTo("Family relocation");
        assertThat(enrollment.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should reject withdrawal with null date")
    void shouldRejectWithdrawalWithNullDate() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );

        // Act & Assert
        assertThatThrownBy(() -> enrollment.withdraw(null, "Reason"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Withdrawal date cannot be null");
    }

    @Test
    @DisplayName("Should reject withdrawal with date before enrollment")
    void shouldRejectWithdrawalDateBeforeEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        LocalDate invalidDate = ENROLLMENT_DATE.minusDays(1);

        // Act & Assert
        assertThatThrownBy(() -> enrollment.withdraw(invalidDate, "Reason"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Withdrawal date cannot be before enrollment date");
    }

    @Test
    @DisplayName("Should reject withdrawal of already withdrawn enrollment")
    void shouldRejectWithdrawalOfWithdrawnEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.withdraw(ENROLLMENT_DATE.plusMonths(1), "First withdrawal");

        // Act & Assert
        assertThatThrownBy(() -> enrollment.withdraw(ENROLLMENT_DATE.plusMonths(2), "Second withdrawal"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot withdraw enrollment with status: WITHDRAWN");
    }

    @Test
    @DisplayName("Should reject withdrawal of completed enrollment")
    void shouldRejectWithdrawalOfCompletedEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.complete();

        // Act & Assert
        assertThatThrownBy(() -> enrollment.withdraw(ENROLLMENT_DATE.plusMonths(2), "Reason"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot withdraw enrollment with status: COMPLETED");
    }

    // ==================== Completion Tests ====================

    @Test
    @DisplayName("Should complete ACTIVE enrollment successfully")
    void shouldCompleteActiveEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );

        // Act
        enrollment.complete();

        // Assert
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(enrollment.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should reject completion of withdrawn enrollment")
    void shouldRejectCompletionOfWithdrawnEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.withdraw(ENROLLMENT_DATE.plusMonths(1), "Withdrawn");

        // Act & Assert
        assertThatThrownBy(enrollment::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot complete enrollment with status: WITHDRAWN");
    }

    @Test
    @DisplayName("Should reject completion of already completed enrollment")
    void shouldRejectCompletionOfCompletedEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.complete();

        // Act & Assert
        assertThatThrownBy(enrollment::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot complete enrollment with status: COMPLETED");
    }

    // ==================== Status Check Tests ====================

    @Test
    @DisplayName("Should correctly identify ACTIVE enrollment")
    void shouldIdentifyActiveEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );

        // Assert
        assertThat(enrollment.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should correctly identify WITHDRAWN enrollment as not active")
    void shouldIdentifyWithdrawnEnrollmentAsNotActive() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.withdraw(ENROLLMENT_DATE.plusMonths(1), "Withdrawn");

        // Assert
        assertThat(enrollment.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify COMPLETED enrollment as not active")
    void shouldIdentifyCompletedEnrollmentAsNotActive() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
                STUDENT_ID,
                VALID_ACADEMIC_YEAR,
                VALID_GRADE,
                VALID_SECTION,
                ENROLLMENT_DATE,
                null
        );
        enrollment.complete();

        // Assert
        assertThat(enrollment.isActive()).isFalse();
    }
}
