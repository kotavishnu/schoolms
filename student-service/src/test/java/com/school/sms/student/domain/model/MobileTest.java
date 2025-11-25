package com.school.sms.student.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Mobile value object.
 * TDD: Tests written FIRST before implementation.
 */
@DisplayName("Mobile Value Object Tests")
class MobileTest {

    @Test
    @DisplayName("Should create valid Mobile with 10 digits")
    void shouldCreateValidMobileWith10Digits() {
        // Given
        String validMobile = "9876543210";

        // When
        Mobile mobile = Mobile.of(validMobile);

        // Then
        assertNotNull(mobile);
        assertEquals(validMobile, mobile.getNumber());
    }

    @Test
    @DisplayName("Should create valid Mobile with 15 digits")
    void shouldCreateValidMobileWith15Digits() {
        // Given
        String validMobile = "123456789012345";

        // When
        Mobile mobile = Mobile.of(validMobile);

        // Then
        assertNotNull(mobile);
        assertEquals(validMobile, mobile.getNumber());
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Mobile.of(null)
        );
        assertEquals("Mobile number cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for blank value")
    void shouldThrowExceptionForBlankValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Mobile.of("   ")
        );
        assertEquals("Mobile number cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for less than 10 digits")
    void shouldThrowExceptionForLessThan10Digits() {
        // Given
        String invalidMobile = "987654321";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Mobile.of(invalidMobile)
        );
        assertTrue(exception.getMessage().contains("Invalid mobile number format"));
    }

    @Test
    @DisplayName("Should throw exception for more than 15 digits")
    void shouldThrowExceptionForMoreThan15Digits() {
        // Given
        String invalidMobile = "1234567890123456";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Mobile.of(invalidMobile)
        );
        assertTrue(exception.getMessage().contains("Invalid mobile number format"));
    }

    @Test
    @DisplayName("Should throw exception for non-numeric characters")
    void shouldThrowExceptionForNonNumericCharacters() {
        // Given
        String invalidMobile = "987654321A";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Mobile.of(invalidMobile));
    }

    @Test
    @DisplayName("Should throw exception for mobile with spaces")
    void shouldThrowExceptionForMobileWithSpaces() {
        // Given
        String invalidMobile = "9876 543 210";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Mobile.of(invalidMobile));
    }

    @Test
    @DisplayName("Should throw exception for mobile with special characters")
    void shouldThrowExceptionForMobileWithSpecialCharacters() {
        // Given
        String invalidMobile = "+9876543210";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Mobile.of(invalidMobile));
    }

    @Test
    @DisplayName("Should be equal when numbers are the same")
    void shouldBeEqualWhenNumbersAreSame() {
        // Given
        Mobile mobile1 = Mobile.of("9876543210");
        Mobile mobile2 = Mobile.of("9876543210");

        // Then
        assertEquals(mobile1, mobile2);
        assertEquals(mobile1.hashCode(), mobile2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when numbers are different")
    void shouldNotBeEqualWhenNumbersAreDifferent() {
        // Given
        Mobile mobile1 = Mobile.of("9876543210");
        Mobile mobile2 = Mobile.of("9876543211");

        // Then
        assertNotEquals(mobile1, mobile2);
    }

    @Test
    @DisplayName("Should have valid toString representation")
    void shouldHaveValidToStringRepresentation() {
        // Given
        String validMobile = "9876543210";
        Mobile mobile = Mobile.of(validMobile);

        // Then
        assertTrue(mobile.toString().contains(validMobile));
    }
}
