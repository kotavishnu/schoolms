package com.school.sms.student.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentId value object.
 * TDD: Tests written FIRST before implementation.
 */
@DisplayName("StudentId Value Object Tests")
class StudentIdTest {

    @Test
    @DisplayName("Should create valid StudentId with correct format")
    void shouldCreateValidStudentId() {
        // Given
        String validId = "STU-2024-00001";

        // When
        StudentId studentId = StudentId.of(validId);

        // Then
        assertNotNull(studentId);
        assertEquals(validId, studentId.getValue());
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StudentId.of(null)
        );
        assertEquals("StudentId cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for blank value")
    void shouldThrowExceptionForBlankValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StudentId.of("   ")
        );
        assertEquals("StudentId cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid format")
    void shouldThrowExceptionForInvalidFormat() {
        // Given
        String invalidId = "INVALID-FORMAT";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StudentId.of(invalidId)
        );
        assertTrue(exception.getMessage().contains("Invalid StudentId format"));
    }

    @Test
    @DisplayName("Should throw exception for wrong year format")
    void shouldThrowExceptionForWrongYearFormat() {
        // Given
        String invalidId = "STU-24-00001";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> StudentId.of(invalidId));
    }

    @Test
    @DisplayName("Should throw exception for wrong sequence format")
    void shouldThrowExceptionForWrongSequenceFormat() {
        // Given
        String invalidId = "STU-2024-001";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> StudentId.of(invalidId));
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreSame() {
        // Given
        StudentId id1 = StudentId.of("STU-2024-00001");
        StudentId id2 = StudentId.of("STU-2024-00001");

        // Then
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        StudentId id1 = StudentId.of("STU-2024-00001");
        StudentId id2 = StudentId.of("STU-2024-00002");

        // Then
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should have valid toString representation")
    void shouldHaveValidToStringRepresentation() {
        // Given
        String validId = "STU-2024-00001";
        StudentId studentId = StudentId.of(validId);

        // Then
        assertTrue(studentId.toString().contains(validId));
    }
}
