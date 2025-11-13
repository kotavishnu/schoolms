package com.school.management.domain.student.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for @ValidMobileNumber annotation validator
 * Tests mobile number format validation (10 digits)
 */
@DisplayName("ValidMobileNumber Validator Tests")
class ValidMobileNumberValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "9876543210",
        "1234567890",
        "9999999999",
        "0000000000",
        "5555555555"
    })
    @DisplayName("Should pass validation for valid 10-digit mobile numbers")
    void shouldPassValidationForValid10DigitMobileNumbers(String mobile) {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile(mobile);

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789",    // 9 digits
        "12345",        // 5 digits
        "123",          // 3 digits
        "1"             // 1 digit
    })
    @DisplayName("Should fail validation for mobile numbers with less than 10 digits")
    void shouldFailValidationForMobileNumbersWithLessThan10Digits(String mobile) {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile(mobile);

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestDto> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("must be exactly 10 digits");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345678901",   // 11 digits
        "123456789012",  // 12 digits
        "12345678901234567890" // 20 digits
    })
    @DisplayName("Should fail validation for mobile numbers with more than 10 digits")
    void shouldFailValidationForMobileNumbersWithMoreThan10Digits(String mobile) {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile(mobile);

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "abcdefghij",
        "12345abcde",
        "abc1234567",
        "12-345-6789",
        "+1234567890",
        "9876 543 210",
        "(987)654-3210"
    })
    @DisplayName("Should fail validation for mobile numbers with non-numeric characters")
    void shouldFailValidationForMobileNumbersWithNonNumericCharacters(String mobile) {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile(mobile);

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestDto> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("must be exactly 10 digits");
    }

    @Test
    @DisplayName("Should pass validation for null mobile number")
    void shouldPassValidationForNullMobileNumber() {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile(null);

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert - null should be handled gracefully (use @NotNull separately if required)
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should pass validation for empty mobile number")
    void shouldPassValidationForEmptyMobileNumber() {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile("");

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert - empty should be handled gracefully (use @NotBlank separately if required)
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation for mobile with spaces")
    void shouldFailValidationForMobileWithSpaces() {
        // Arrange
        TestDto dto = new TestDto();
        dto.setMobile("98765 43210");

        // Act
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Should fail validation for mobile with special characters")
    void shouldFailValidationForMobileWithSpecialCharacters() {
        // Arrange & Act & Assert
        String[] invalidMobiles = {
            "9876-543-210",
            "987.654.3210",
            "987@654#3210",
            "+9876543210",
            "-9876543210"
        };

        for (String mobile : invalidMobiles) {
            TestDto dto = new TestDto();
            dto.setMobile(mobile);

            Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);

            assertThat(violations)
                .as("Mobile '%s' should be invalid", mobile)
                .hasSize(1);
        }
    }

    /**
     * Test DTO class for validation testing
     */
    static class TestDto {
        @ValidMobileNumber
        private String mobile;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
