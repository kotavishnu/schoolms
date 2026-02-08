package com.school.student.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for StudentEntity.
 * Tests JPA entity lifecycle methods (@PrePersist, @PreUpdate).
 *
 * <p>Following D-013: Infrastructure Layer Testing - Test JPA lifecycle callbacks.
 */
@DisplayName("StudentEntity Unit Tests")
class StudentEntityTest {

    // ==================== Entity Construction Tests ====================

    @Test
    @DisplayName("Should create StudentEntity with builder")
    void shouldCreateStudentEntityWithBuilder() {
        // Act
        StudentEntity entity = StudentEntity.builder()
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getStudentId()).isEqualTo("STD-20260204-0001");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.ACTIVE);
    }

    // ==================== PrePersist Lifecycle Tests ====================

    @Test
    @DisplayName("Should set timestamps on PrePersist")
    void shouldSetTimestampsOnPrePersist() {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isEqualToIgnoringNanos(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should initialize version to 0 on PrePersist when null")
    void shouldInitializeVersionToZeroOnPrePersist() {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
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
        StudentEntity entity = StudentEntity.builder()
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .version(5L)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getVersion()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt to same time on PrePersist")
    void shouldSetCreatedAtAndUpdatedAtToSameTimeOnPrePersist() {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        // Act
        entity.onCreate();

        // Assert
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        // Should be within same second
        assertThat(entity.getCreatedAt().toEpochSecond())
            .isEqualTo(entity.getUpdatedAt().toEpochSecond());
    }

    // ==================== PreUpdate Lifecycle Tests ====================

    @Test
    @DisplayName("Should update timestamp on PreUpdate")
    void shouldUpdateTimestampOnPreUpdate() throws InterruptedException {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        entity.onCreate();
        OffsetDateTime originalUpdatedAt = entity.getUpdatedAt();

        // Wait a bit to ensure time difference
        Thread.sleep(10);

        // Act
        entity.onUpdate();

        // Assert
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should not change createdAt on PreUpdate")
    void shouldNotChangeCreatedAtOnPreUpdate() throws InterruptedException {
        // Arrange
        StudentEntity entity = StudentEntity.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        entity.onCreate();
        OffsetDateTime originalCreatedAt = entity.getCreatedAt();

        // Wait a bit
        Thread.sleep(10);

        // Act
        entity.onUpdate();

        // Assert
        assertThat(entity.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    // ==================== Status Enum Tests ====================

    @Test
    @DisplayName("Should support ACTIVE status")
    void shouldSupportActiveStatus() {
        // Arrange & Act
        StudentEntity entity = StudentEntity.builder()
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .build();

        // Assert
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("Should support INACTIVE status")
    void shouldSupportInactiveStatus() {
        // Arrange & Act
        StudentEntity entity = StudentEntity.builder()
            .status(StudentEntity.StudentStatusEnum.INACTIVE)
            .build();

        // Assert
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.INACTIVE);
    }

    // ==================== Field Validation Tests ====================

    @Test
    @DisplayName("Should store all required fields")
    void shouldStoreAllRequiredFields() {
        // Arrange & Act
        StudentEntity entity = StudentEntity.builder()
            .id(1L)
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .email("john.doe@example.com")
            .address("123 Main St")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .identificationMark("Mole on left arm")
            .aadhaarNumber("123456789012")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            .version(0L)
            .createdBy("admin")
            .updatedBy("admin")
            .build();

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("STD-20260204-0001");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(2015, 5, 15));
        assertThat(entity.getMobile()).isEqualTo("9876543210");
        assertThat(entity.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(entity.getAddress()).isEqualTo("123 Main St");
        assertThat(entity.getFathersName()).isEqualTo("James Doe");
        assertThat(entity.getMothersName()).isEqualTo("Jane Doe");
        assertThat(entity.getIdentificationMark()).isEqualTo("Mole on left arm");
        assertThat(entity.getAadhaarNumber()).isEqualTo("123456789012");
        assertThat(entity.getStatus()).isEqualTo(StudentEntity.StudentStatusEnum.ACTIVE);
        assertThat(entity.getVersion()).isEqualTo(0L);
        assertThat(entity.getCreatedBy()).isEqualTo("admin");
        assertThat(entity.getUpdatedBy()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Arrange & Act
        StudentEntity entity = StudentEntity.builder()
            .studentId("STD-20260204-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2015, 5, 15))
            .mobile("9876543210")
            .fathersName("James Doe")
            .mothersName("Jane Doe")
            .status(StudentEntity.StudentStatusEnum.ACTIVE)
            // Optional fields not set
            .build();

        // Assert
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getAddress()).isNull();
        assertThat(entity.getIdentificationMark()).isNull();
        assertThat(entity.getAadhaarNumber()).isNull();
    }
}
