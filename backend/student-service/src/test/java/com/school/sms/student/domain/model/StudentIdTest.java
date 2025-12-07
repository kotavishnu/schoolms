package com.school.sms.student.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StudentId Tests")
class StudentIdTest {

    @Test
    @DisplayName("Create valid StudentId")
    void of_WithValidFormat_ShouldSucceed() {
        StudentId id = StudentId.of("STD-20241207-0001");
        assertThat(id.getValue()).isEqualTo("STD-20241207-0001");
    }

    @Test
    @DisplayName("Reject null StudentId")
    void of_WithNull_ShouldThrowException() {
        assertThatThrownBy(() -> StudentId.of(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Reject invalid format")
    void of_WithInvalidFormat_ShouldThrowException() {
        assertThatThrownBy(() -> StudentId.of("INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid Student ID format");
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_SameValue_ShouldReturnTrue() {
        StudentId id1 = StudentId.of("STD-20241207-0001");
        StudentId id2 = StudentId.of("STD-20241207-0001");
        assertThat(id1).isEqualTo(id2);
    }
}
