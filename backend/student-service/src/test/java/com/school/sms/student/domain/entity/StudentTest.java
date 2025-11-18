package com.school.sms.student.domain.entity;

import com.school.sms.student.domain.valueobject.Address;
import com.school.sms.student.domain.valueobject.StudentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Student entity.
 */
class StudentTest {

    @Test
    @DisplayName("Should calculate correct age from date of birth")
    void shouldCalculateCorrectAge() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(10);
        Student student = Student.builder()
            .dateOfBirth(dob)
            .build();

        // When
        int age = student.calculateAge();

        // Then
        assertThat(age).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return true when student status is ACTIVE")
    void shouldReturnTrueWhenActive() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.ACTIVE)
            .build();

        // When & Then
        assertThat(student.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return false when student status is INACTIVE")
    void shouldReturnFalseWhenInactive() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.INACTIVE)
            .build();

        // When & Then
        assertThat(student.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should activate student successfully")
    void shouldActivateStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.INACTIVE)
            .build();

        // When
        student.activate();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(student.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should deactivate student successfully")
    void shouldDeactivateStudent() {
        // Given
        Student student = Student.builder()
            .status(StudentStatus.ACTIVE)
            .build();

        // When
        student.deactivate();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        assertThat(student.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should return full name correctly")
    void shouldReturnFullName() {
        // Given
        Student student = Student.builder()
            .firstName("Rajesh")
            .lastName("Kumar")
            .build();

        // When
        String fullName = student.getFullName();

        // Then
        assertThat(fullName).isEqualTo("Rajesh Kumar");
    }

    @Test
    @DisplayName("Should default status to ACTIVE when building with builder")
    void shouldDefaultStatusToActive() {
        // Given & When
        Student student = Student.builder()
            .studentId("STU-2025-00001")
            .firstName("Test")
            .lastName("Student")
            .dateOfBirth(LocalDate.now().minusYears(10))
            .build();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }
}
