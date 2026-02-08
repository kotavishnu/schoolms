package com.school.student.infrastructure.persistence.mapper;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.EnrollmentStatus;
import com.school.student.infrastructure.persistence.entity.EnrollmentEntity;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for EnrollmentEntityMapper.
 * Tests MapStruct mappings between Enrollment domain and entity.
 *
 * <p>Following D-013: Infrastructure Layer Testing.
 */
@DisplayName("EnrollmentEntityMapper Unit Tests")
class EnrollmentEntityMapperTest {

    private EnrollmentEntityMapper mapper;
    private StudentEntity testStudent;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(EnrollmentEntityMapper.class);

        testStudent = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .build();
    }

    // ==================== Domain to Entity Tests ====================

    @Test
    @DisplayName("Should convert domain Enrollment to EnrollmentEntity")
    void shouldConvertDomainToEntity() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
            1L,
            "2025-2026",
            "Grade 5",
            "Section A",
            LocalDate.of(2025, 4, 1),
            "Regular enrollment"
        );
        enrollment.setId(1L);
        enrollment.setVersion(0L);

        // Act
        EnrollmentEntity entity = mapper.toEntity(enrollment);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getAcademicYear()).isEqualTo("2025-2026");
        assertThat(entity.getGradeClass()).isEqualTo("Grade 5");
        assertThat(entity.getSection()).isEqualTo("Section A");
        assertThat(entity.getEnrollmentDate()).isEqualTo(LocalDate.of(2025, 4, 1));
        assertThat(entity.getRemarks()).isEqualTo("Regular enrollment");
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
        assertThat(entity.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null enrollment in toEntity")
    void shouldHandleNullEnrollmentInToEntity() {
        // Act
        EnrollmentEntity entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle enrollment with withdrawal date")
    void shouldHandleEnrollmentWithWithdrawalDate() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
            1L,
            "2025-2026",
            "Grade 5",
            "Section A",
            LocalDate.of(2025, 4, 1),
            null
        );
        enrollment.withdraw(LocalDate.of(2025, 10, 15), "Student moved");

        // Act
        EnrollmentEntity entity = mapper.toEntity(enrollment);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getWithdrawalDate()).isEqualTo(LocalDate.of(2025, 10, 15));
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN);
    }

    @Test
    @DisplayName("Should handle COMPLETED status in toEntity")
    void shouldHandleCompletedStatusInToEntity() {
        // Arrange
        Enrollment enrollment = Enrollment.enroll(
            1L,
            "2025-2026",
            "Grade 5",
            "Section A",
            LocalDate.of(2025, 4, 1),
            null
        );
        enrollment.complete();

        // Act
        EnrollmentEntity entity = mapper.toEntity(enrollment);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.COMPLETED);
    }

    // ==================== Entity to Domain Tests ====================

    @Test
    @DisplayName("Should convert EnrollmentEntity to domain Enrollment")
    void shouldConvertEntityToDomain() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .id(1L)
            .student(testStudent)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE)
            .remarks("Regular enrollment")
            .version(0L)
            .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
            .build();

        // Act
        Enrollment enrollment = mapper.toDomain(entity);

        // Assert
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getId()).isEqualTo(1L);
        assertThat(enrollment.getStudentId()).isEqualTo(1L);
        assertThat(enrollment.getAcademicYear()).isEqualTo("2025-2026");
        assertThat(enrollment.getGradeClass()).isEqualTo("Grade 5");
        assertThat(enrollment.getSection()).isEqualTo("Section A");
        assertThat(enrollment.getEnrollmentDate()).isEqualTo(LocalDate.of(2025, 4, 1));
        assertThat(enrollment.getRemarks()).isEqualTo("Regular enrollment");
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null entity in toDomain")
    void shouldHandleNullEntityInToDomain() {
        // Act
        Enrollment enrollment = mapper.toDomain(null);

        // Assert
        assertThat(enrollment).isNull();
    }

    @Test
    @DisplayName("Should handle WITHDRAWN status in toDomain")
    void shouldHandleWithdrawnStatusInToDomain() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .id(1L)
            .student(testStudent)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .withdrawalDate(LocalDate.of(2025, 10, 15))
            .status(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN)
            .remarks("Student moved")
            .build();

        // Act
        Enrollment enrollment = mapper.toDomain(entity);

        // Assert
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        assertThat(enrollment.getWithdrawalDate()).isEqualTo(LocalDate.of(2025, 10, 15));
    }

    @Test
    @DisplayName("Should handle COMPLETED status in toDomain")
    void shouldHandleCompletedStatusInToDomain() {
        // Arrange
        EnrollmentEntity entity = EnrollmentEntity.builder()
            .id(1L)
            .student(testStudent)
            .academicYear("2025-2026")
            .gradeClass("Grade 5")
            .section("Section A")
            .enrollmentDate(LocalDate.of(2025, 4, 1))
            .status(EnrollmentEntity.EnrollmentStatusEnum.COMPLETED)
            .build();

        // Act
        Enrollment enrollment = mapper.toDomain(entity);

        // Assert
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    // ==================== Bidirectional Conversion Tests ====================

    @Test
    @DisplayName("Should maintain data integrity in bidirectional conversion")
    void shouldMaintainDataIntegrityInBidirectionalConversion() {
        // Arrange
        Enrollment originalEnrollment = Enrollment.enroll(
            1L,
            "2025-2026",
            "Grade 5",
            "Section A",
            LocalDate.of(2025, 4, 1),
            "Regular enrollment"
        );
        originalEnrollment.setId(1L);
        originalEnrollment.setVersion(0L);

        EnrollmentEntity intermediateEntity = mapper.toEntity(originalEnrollment);
        intermediateEntity.setStudent(testStudent); // Set student reference

        // Act
        Enrollment reconvertedEnrollment = mapper.toDomain(intermediateEntity);

        // Assert
        assertThat(reconvertedEnrollment).isNotNull();
        assertThat(reconvertedEnrollment.getId()).isEqualTo(originalEnrollment.getId());
        assertThat(reconvertedEnrollment.getStudentId()).isEqualTo(1L);
        assertThat(reconvertedEnrollment.getAcademicYear()).isEqualTo(originalEnrollment.getAcademicYear());
        assertThat(reconvertedEnrollment.getGradeClass()).isEqualTo(originalEnrollment.getGradeClass());
        assertThat(reconvertedEnrollment.getSection()).isEqualTo(originalEnrollment.getSection());
        assertThat(reconvertedEnrollment.getEnrollmentDate()).isEqualTo(originalEnrollment.getEnrollmentDate());
        assertThat(reconvertedEnrollment.getRemarks()).isEqualTo(originalEnrollment.getRemarks());
        assertThat(reconvertedEnrollment.getStatus()).isEqualTo(originalEnrollment.getStatus());
    }

    // ==================== Helper Method Tests ====================

    @Test
    @DisplayName("Should convert domain status ACTIVE to entity status")
    void shouldConvertDomainStatusActiveToEntity() {
        // Act
        EnrollmentEntity.EnrollmentStatusEnum result = mapper.domainStatusToEntity(EnrollmentStatus.ACTIVE);

        // Assert
        assertThat(result).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should convert domain status WITHDRAWN to entity status")
    void shouldConvertDomainStatusWithdrawnToEntity() {
        // Act
        EnrollmentEntity.EnrollmentStatusEnum result = mapper.domainStatusToEntity(EnrollmentStatus.WITHDRAWN);

        // Assert
        assertThat(result).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.WITHDRAWN);
    }

    @Test
    @DisplayName("Should convert domain status COMPLETED to entity status")
    void shouldConvertDomainStatusCompletedToEntity() {
        // Act
        EnrollmentEntity.EnrollmentStatusEnum result = mapper.domainStatusToEntity(EnrollmentStatus.COMPLETED);

        // Assert
        assertThat(result).isEqualTo(EnrollmentEntity.EnrollmentStatusEnum.COMPLETED);
    }

    @Test
    @DisplayName("Should handle null status in domainStatusToEntity")
    void shouldHandleNullStatusInDomainStatusToEntity() {
        // Act
        EnrollmentEntity.EnrollmentStatusEnum result = mapper.domainStatusToEntity(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert entity status to domain status")
    void shouldConvertEntityStatusToDomain() {
        // Act
        EnrollmentStatus result = mapper.entityStatusToDomain(EnrollmentEntity.EnrollmentStatusEnum.ACTIVE);

        // Assert
        assertThat(result).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should handle null status in entityStatusToDomain")
    void shouldHandleNullStatusInEntityStatusToDomain() {
        // Act
        EnrollmentStatus result = mapper.entityStatusToDomain(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert LocalDate to OffsetDateTime")
    void shouldConvertLocalDateToOffsetDateTime() {
        // Arrange
        LocalDate localDate = LocalDate.of(2025, 4, 1);

        // Act
        OffsetDateTime result = mapper.localDateToOffsetDateTime(localDate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(localDate);
    }

    @Test
    @DisplayName("Should handle null LocalDate")
    void shouldHandleNullLocalDate() {
        // Act
        OffsetDateTime result = mapper.localDateToOffsetDateTime(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert OffsetDateTime to LocalDate")
    void shouldConvertOffsetDateTimeToLocalDate() {
        // Arrange
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneId.systemDefault());

        // Act
        LocalDate result = mapper.offsetDateTimeToLocalDate(offsetDateTime);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(offsetDateTime.toLocalDate());
    }

    @Test
    @DisplayName("Should handle null OffsetDateTime")
    void shouldHandleNullOffsetDateTime() {
        // Act
        LocalDate result = mapper.offsetDateTimeToLocalDate(null);

        // Assert
        assertThat(result).isNull();
    }
}
