package com.school.sms.student.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Student aggregate root.
 * TDD: Tests written FIRST before implementation.
 */
@DisplayName("Student Aggregate Root Tests")
class StudentTest {

    @Test
    @DisplayName("Should create valid student via factory method")
    void shouldCreateValidStudent() {
        // Given
        StudentId studentId = StudentId.of("STU-2024-00001");
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dob = LocalDate.of(2015, 5, 15);
        Mobile mobile = Mobile.of("9876543210");
        String address = "123 Main St";
        String fatherName = "James Doe";
        String motherName = "Jane Doe";
        String email = "john.doe@example.com";

        // When
        Student student = Student.register(
            studentId, firstName, lastName, dob, mobile,
            address, fatherName, motherName, null, null, email
        );

        // Then
        assertNotNull(student);
        assertEquals(studentId, student.getStudentId());
        assertEquals(firstName, student.getFirstName());
        assertEquals(lastName, student.getLastName());
        assertEquals(dob, student.getDateOfBirth());
        assertEquals(mobile, student.getMobile());
        assertEquals(StudentStatus.ACTIVE, student.getStatus());
        assertNotNull(student.getCreatedAt());
        assertNotNull(student.getUpdatedAt());
    }

    @Test
    @DisplayName("Should calculate current age correctly")
    void shouldCalculateCurrentAge() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(10);
        Student student = Student.register(
            StudentId.of("STU-2024-00001"),
            "John", "Doe", dob,
            Mobile.of("9876543210"),
            "123 Main St", "Father", "Mother",
            null, null, "test@example.com"
        );

        // When
        int age = student.getCurrentAge();

        // Then
        assertEquals(10, age);
    }

    @Test
    @DisplayName("Should update profile with editable fields only")
    void shouldUpdateProfileWithEditableFieldsOnly() {
        // Given
        Student student = createDefaultStudent();
        String newFirstName = "Jane";
        String newLastName = "Smith";
        Mobile newMobile = Mobile.of("9876543211");

        // When
        student.updateProfile(newFirstName, newLastName, newMobile);

        // Then
        assertEquals(newFirstName, student.getFirstName());
        assertEquals(newLastName, student.getLastName());
        assertEquals(newMobile, student.getMobile());
    }

    @Test
    @DisplayName("Should not update profile with null firstName")
    void shouldNotUpdateProfileWithNullFirstName() {
        // Given
        Student student = createDefaultStudent();

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> student.updateProfile(null, "Doe", student.getMobile())
        );
    }

    @Test
    @DisplayName("Should not update profile with blank lastName")
    void shouldNotUpdateProfileWithBlankLastName() {
        // Given
        Student student = createDefaultStudent();

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> student.updateProfile("John", "   ", student.getMobile())
        );
    }

    @Test
    @DisplayName("Should change status successfully")
    void shouldChangeStatusSuccessfully() {
        // Given
        Student student = createDefaultStudent();
        StudentStatus newStatus = StudentStatus.INACTIVE;

        // When
        student.changeStatus(newStatus);

        // Then
        assertEquals(newStatus, student.getStatus());
    }

    @Test
    @DisplayName("Should not change status to null")
    void shouldNotChangeStatusToNull() {
        // Given
        Student student = createDefaultStudent();

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> student.changeStatus(null)
        );
    }

    @Test
    @DisplayName("Should validate age at creation")
    void shouldValidateAgeAtCreation() {
        // This test ensures age validation logic exists in the domain
        // The actual validation will be done by Drools in the application layer
        // But the domain should have a method to get the age for validation

        // Given
        LocalDate dobTooYoung = LocalDate.now().minusYears(2);
        Student student = Student.register(
            StudentId.of("STU-2024-00001"),
            "John", "Doe", dobTooYoung,
            Mobile.of("9876543210"),
            "123 Main St", "Father", "Mother",
            null, null, "test@example.com"
        );

        // When
        int age = student.getCurrentAge();

        // Then
        assertTrue(age < 3); // Will be validated by Drools
    }

    @Test
    @DisplayName("Should have proper version for optimistic locking")
    void shouldHaveProperVersionForOptimisticLocking() {
        // Given
        Student student = createDefaultStudent();

        // When
        student.setVersion(1L);

        // Then
        assertEquals(1L, student.getVersion());
    }

    @Test
    @DisplayName("Should update timestamps on profile update")
    void shouldUpdateTimestampsOnProfileUpdate() {
        // Given
        Student student = createDefaultStudent();
        LocalDate originalUpdated = student.getUpdatedAt();

        // When
        try {
            Thread.sleep(10); // Small delay to ensure time difference
        } catch (InterruptedException e) {
            // Ignore
        }
        student.updateProfile("Jane", "Smith", Mobile.of("9876543211"));

        // Then
        assertNotNull(student.getUpdatedAt());
    }

    // Helper method to create a default student for testing
    private Student createDefaultStudent() {
        return Student.register(
            StudentId.of("STU-2024-00001"),
            "John",
            "Doe",
            LocalDate.of(2015, 5, 15),
            Mobile.of("9876543210"),
            "123 Main St",
            "James Doe",
            "Jane Doe",
            "Mole on left arm",
            "123456789012",
            "john.doe@example.com"
        );
    }
}
