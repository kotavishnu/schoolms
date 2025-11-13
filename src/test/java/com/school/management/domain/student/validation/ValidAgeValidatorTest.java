package com.school.management.domain.student.validation;

import com.school.management.domain.student.Student;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for @ValidAge annotation validator
 * Tests age validation (3-18 years) business rule BR-1
 */
@DisplayName("ValidAge Validator Tests")
class ValidAgeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation for age 3 years")
    void shouldPassValidationForAge3Years() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now().minusYears(3));

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should pass validation for age 18 years")
    void shouldPassValidationForAge18Years() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now().minusYears(18));

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should pass validation for age between 3 and 18 years")
    void shouldPassValidationForAgeBetween3And18Years() {
        // Arrange & Act & Assert
        for (int age = 3; age <= 18; age++) {
            TestStudentDto dto = new TestStudentDto();
            dto.setDateOfBirth(LocalDate.now().minusYears(age));

            Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

            assertThat(violations)
                .as("Age %d should be valid", age)
                .isEmpty();
        }
    }

    @Test
    @DisplayName("Should fail validation for age less than 3 years")
    void shouldFailValidationForAgeLessThan3Years() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now().minusYears(2));

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestStudentDto> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("must be between 3 and 18 years");
    }

    @Test
    @DisplayName("Should fail validation for age greater than 18 years")
    void shouldFailValidationForAgeGreaterThan18Years() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now().minusYears(19));

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestStudentDto> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("must be between 3 and 18 years");
    }

    @Test
    @DisplayName("Should fail validation for age 0 years")
    void shouldFailValidationForAge0Years() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now());

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Should fail validation for future date of birth")
    void shouldFailValidationForFutureDateOfBirth() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(LocalDate.now().plusYears(1));

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Should handle null date of birth")
    void shouldHandleNullDateOfBirth() {
        // Arrange
        TestStudentDto dto = new TestStudentDto();
        dto.setDateOfBirth(null);

        // Act
        Set<ConstraintViolation<TestStudentDto>> violations = validator.validate(dto);

        // Assert - null should be handled gracefully (considered valid or use @NotNull separately)
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate exact age boundaries")
    void shouldValidateExactAgeBoundaries() {
        // Test 2 years, 364 days (should fail)
        TestStudentDto dto1 = new TestStudentDto();
        dto1.setDateOfBirth(LocalDate.now().minusYears(3).plusDays(1));
        Set<ConstraintViolation<TestStudentDto>> violations1 = validator.validate(dto1);
        assertThat(violations1).hasSize(1);

        // Test exactly 3 years (should pass)
        TestStudentDto dto2 = new TestStudentDto();
        dto2.setDateOfBirth(LocalDate.now().minusYears(3));
        Set<ConstraintViolation<TestStudentDto>> violations2 = validator.validate(dto2);
        assertThat(violations2).isEmpty();

        // Test exactly 18 years (should pass)
        TestStudentDto dto3 = new TestStudentDto();
        dto3.setDateOfBirth(LocalDate.now().minusYears(18));
        Set<ConstraintViolation<TestStudentDto>> violations3 = validator.validate(dto3);
        assertThat(violations3).isEmpty();

        // Test 18 years, 1 day (should fail)
        TestStudentDto dto4 = new TestStudentDto();
        dto4.setDateOfBirth(LocalDate.now().minusYears(18).minusDays(1));
        Set<ConstraintViolation<TestStudentDto>> violations4 = validator.validate(dto4);
        assertThat(violations4).hasSize(1);
    }

    /**
     * Test DTO class for validation testing
     */
    @ValidAge
    static class TestStudentDto {
        private LocalDate dateOfBirth;

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }
    }
}
