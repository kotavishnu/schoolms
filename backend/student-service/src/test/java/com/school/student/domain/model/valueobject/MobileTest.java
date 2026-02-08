package com.school.student.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Mobile value object.
 * Tests validation, masking, and boundary conditions.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@DisplayName("Mobile Value Object Tests")
class MobileTest {

    @Test
    @DisplayName("Should create Mobile with valid 10-digit number")
    void shouldCreateMobileWithValidNumber() {
        // Arrange & Act
        Mobile mobile = Mobile.of("9876543210");

        // Assert
        assertThat(mobile).isNotNull();
        assertThat(mobile.getNumber()).isEqualTo("9876543210");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("Should throw exception for null or empty mobile number")
    void shouldThrowExceptionForNullOrEmptyNumber(String invalidNumber) {
        // Act & Assert
        assertThatThrownBy(() -> Mobile.of(invalidNumber))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile number cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789",    // 9 digits
        "12345678901",  // 11 digits
        "12345",        // 5 digits
        "123456789a",   // contains letter
        "12-3456789",   // contains hyphen
        "12 34567890"   // contains space
    })
    @DisplayName("Should throw exception for invalid mobile format")
    void shouldThrowExceptionForInvalidFormat(String invalidNumber) {
        // Act & Assert
        assertThatThrownBy(() -> Mobile.of(invalidNumber))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mobile number must be exactly 10 digits");
    }

    @Test
    @DisplayName("Should trim whitespace from mobile number")
    void shouldTrimWhitespace() {
        // Arrange & Act
        Mobile mobile = Mobile.of("  9876543210  ");

        // Assert
        assertThat(mobile.getNumber()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Should return masked mobile number")
    void shouldReturnMaskedNumber() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");

        // Act
        String masked = mobile.getMasked();

        // Assert
        assertThat(masked).isEqualTo("987****210");
    }

    @Test
    @DisplayName("Should return masked format in toString")
    void shouldReturnMaskedInToString() {
        // Arrange
        Mobile mobile = Mobile.of("9876543210");

        // Act
        String toString = mobile.toString();

        // Assert
        assertThat(toString).isEqualTo("987****210");
    }

    @Test
    @DisplayName("Should be equal when numbers are same")
    void shouldBeEqualWhenNumbersAreSame() {
        // Arrange
        Mobile mobile1 = Mobile.of("9876543210");
        Mobile mobile2 = Mobile.of("9876543210");

        // Assert
        assertThat(mobile1).isEqualTo(mobile2);
        assertThat(mobile1.hashCode()).isEqualTo(mobile2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when numbers are different")
    void shouldNotBeEqualWhenNumbersAreDifferent() {
        // Arrange
        Mobile mobile1 = Mobile.of("9876543210");
        Mobile mobile2 = Mobile.of("9876543211");

        // Assert
        assertThat(mobile1).isNotEqualTo(mobile2);
    }
}
