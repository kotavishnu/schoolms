package com.school.student.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Student domain model
 * Pure domain logic tests - no Spring context, no mocks
 */
@DisplayName("Student Domain Model Tests")
class StudentTest {

    @Test
    @DisplayName("Should create student with valid data")
    void shouldCreateStudentWithValidData() {
        // Given
        LocalDate dob = LocalDate.of(2010, 5, 15);

        // When
        Student student = Student.builder()
            .studentId("STD-20260128-0001")
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(dob)
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // Then
        assertThat(student.getStudentId()).isEqualTo("STD-20260128-0001");
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getMobile()).isEqualTo("9876543210");
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should calculate age correctly from date of birth")
    void shouldCalculateAgeCorrectly() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(10).minusDays(1);
        Student student = Student.builder()
            .dateOfBirth(dob)
            .build();

        // When
        int age = student.getAge();

        // Then
        assertThat(age).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return 0 age when date of birth is null")
    void shouldReturn0AgeWhenDobIsNull() {
        // Given
        Student student = Student.builder().build();

        // When
        int age = student.getAge();

        // Then
        assertThat(age).isEqualTo(0);
    }

    @Test
    @DisplayName("Should update profile with valid data")
    void shouldUpdateProfileWithValidData() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        student.updateProfile("Jane", "Smith", "9876543211", StudentStatus.INACTIVE);

        // Then
        assertThat(student.getFirstName()).isEqualTo("Jane");
        assertThat(student.getLastName()).isEqualTo("Smith");
        assertThat(student.getMobile()).isEqualTo("9876543211");
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        assertThat(student.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("Should throw exception when updating with null first name")
    void shouldThrowExceptionWhenUpdatingWithNullFirstName() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(() -> student.updateProfile(null, "Doe", "9876543210", StudentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when updating with empty first name")
    void shouldThrowExceptionWhenUpdatingWithEmptyFirstName() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(() -> student.updateProfile("  ", "Doe", "9876543210", StudentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when updating with null last name")
    void shouldThrowExceptionWhenUpdatingWithNullLastName() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(() -> student.updateProfile("John", null, "9876543210", StudentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Last name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid mobile format")
    void shouldThrowExceptionWhenUpdatingWithInvalidMobile() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(() -> student.updateProfile("John", "Doe", "12345", StudentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile must be exactly 10 digits");
    }

    @Test
    @DisplayName("Should throw exception when updating with non-numeric mobile")
    void shouldThrowExceptionWhenUpdatingWithNonNumericMobile() {
        // Given
        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(() -> student.updateProfile("John", "Doe", "ABCDEFGHIJ", StudentStatus.ACTIVE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile must be exactly 10 digits");
    }

    @Test
    @DisplayName("Should activate inactive student")
    void shouldActivateInactiveStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.INACTIVE)
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        LocalDateTime beforeActivation = LocalDateTime.now().minusSeconds(1);

        // When
        student.activate();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(student.getUpdatedAt()).isAfter(beforeActivation);
    }

    @Test
    @DisplayName("Should throw exception when activating already active student")
    void shouldThrowExceptionWhenActivatingAlreadyActiveStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(student::activate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Student is already active");
    }

    @Test
    @DisplayName("Should deactivate active student")
    void shouldDeactivateActiveStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.ACTIVE)
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        LocalDateTime beforeDeactivation = LocalDateTime.now().minusSeconds(1);

        // When
        student.deactivate();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        assertThat(student.getUpdatedAt()).isAfter(beforeDeactivation);
    }

    @Test
    @DisplayName("Should throw exception when deactivating already inactive student")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.INACTIVE)
            .build();

        // When & Then
        assertThatThrownBy(student::deactivate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Student is already inactive");
    }
}
