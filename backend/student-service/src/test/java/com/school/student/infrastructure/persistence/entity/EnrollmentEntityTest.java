package com.school.student.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for EnrollmentEntity.
 * Tests JPA entity lifecycle methods (@PrePersist).
 *
 * <p>Following D-013: Infrastructure Layer Testing - Test JPA lifecycle callbacks.
 */
@DisplayName("EnrollmentEntity Unit Tests")
class EnrollmentEntityTest {

    // ==================== Entity Construction Tests ====================

    @Test
    @DisplayName("Should create EnrollmentEntity with builder")
    void shouldCreateEnrollmentEntityWithBuilder() {
        // Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .build();

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getAcademicYear()).isEqualTo("2025-2026");
        assertThat(entity.getGradeClass()).isEqualTo("Grade 5");
        assertThat(entity.getSection()).isEqualTo("Section A");
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
    }

    // ==================== PrePersist Lifecycle Tests ====================

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void shouldSetCreatedAtOnPrePersist() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize version to 0 on PrePersist when null")
    void shouldInitializeVersionToZeroOnPrePersist() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .version(null)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should not override version on PrePersist if already set")
    void shouldNotOverrideVersionOnPrePersistIfAlreadySet() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .version(3L)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getVersion()).isEqualTo(3L);
    }

    // ==================== Status Enum Tests ====================

    @Test
    @DisplayName("Should support ACTIVE status")
    void shouldSupportActiveStatus() {
        // Arrange & Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .build();

        // Assert
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should support WITHDRAWN status")
    void shouldSupportWithdrawnStatus() {
        // Arrange & Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .status(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN)
            .build();

        // Assert
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN);
    }

    @Test
    @DisplayName("Should support COMPLETED status")
    void shouldSupportCompletedStatus() {
        // Arrange & Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .status(EnrollmentEntity.EnrollmentStatusEnum.COMPLETED)
            .build();

        // Assert
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.COMPLETED);
    }

    // ==================== Field Validation Tests ====================

    @Test
    @DisplayName("Should store all required fields")
    void shouldStoreAllRequiredFields() {
        // Arrange
        StudentEntity student = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .build();

        // Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .id(1L)
            .student(student)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .remarks("Regular enrollment")
            .version(0L)
            .build();

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudent()).isNotNull();
        assertThat(entity.getStudent().getId()).isEqualTo(1L);
        assertThat(entity.getAcademicYear()).isEqualTo("2025-2026");
        assertThat(entity.getGradeClass()).isEqualTo("Grade 5");
        assertThat(entity.getSection()).isEqualTo("Section A");
        assertThat(entity.getEnrollmentDate()).isEqualTo(LocalDate.of(2025, 4, 1));
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
        assertThat(entity.getRemarks()).isEqualTo("Regular enrollment");
        assertThat(entity.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Arrange & Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            // Optional fields not set
            .build();

        // Assert
        assertThat(entity.getWithdrawalDate()).isNull();
        assertThat(entity.getRemarks()).isNull();
    }

    @Test
    @DisplayName("Should store withdrawal date for withdrawn enrollment")
    void shouldStoreWithdrawalDateForWithdrawnEnrollment() {
        // Arrange & Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .withdrawalDate(LocalDate.of(2025, 10, 15))
            .status(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN)
            .remarks("Student moved to another school")
            .build();

        // Assert
        assertThat(entity.getWithdrawalDate()).isEqualTo(LocalDate.of(2025, 10, 15));
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN);
        assertThat(entity.getRemarks()).isEqualTo("Student moved to another school");
    }

    @Test
    @DisplayName("Should maintain student relationship")
    void shouldMaintainStudentRelationship() {
        // Arrange
        StudentEntity student = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .build();

        // Act
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .student(student)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .build();

        // Assert
        assertThat(entity.getStudent()).isNotNull();
        assertThat(entity.getStudent().getId()).isEqualTo(1L);
        assertThat(entity.getStudent().getStudentId()).isEqualTo("STD-20260204-0001");
        assertThat(entity.getStudent().getFirstName()).isEqualTo("John");
    }
}
