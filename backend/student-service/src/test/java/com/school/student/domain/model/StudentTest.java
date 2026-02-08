package com.school.student.domain.model;

import com.school.student.domain.model.valueobject.GuardianInfo;
import com.school.student.domain.model.valueobject.Mobile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Student domain model.
 * Tests business logic, invariants, and Business Rule BR-1 (age validation).
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-03
 */
@DisplayName("Student Domain Model Tests")
class StudentTest {

    @Test
    @DisplayName("Should register student with valid data")
    void shouldRegisterStudentWithValidData() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(10);
        Mobile mobile = Mobile.of("9876543210");
        GuardianInfo guardianInfo = GuardianInfo.of("John Doe", null);

        // Act
        Student student = Student.register(
            "Alice",
            "Smith",
            dob,
            mobile,
            guardianInfo
        );

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getFirstName()).isEqualTo("Alice");
        assertThat(student.getLastName()).isEqualTo("Smith");
        assertThat(student.getDateOfBirth()).isEqualTo(dob);
        assertThat(student.getMobile()).isEqualTo(mobile);
        assertThat(student.getGuardianInfo()).isEqualTo(guardianInfo);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should calculate student age correctly")
    void shouldCalculateAgeCorrectly() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(10).minusDays(1);
        Student student = createValidStudent(dob);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return full name")
    void shouldReturnFullName() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));

        // Act
        String fullName = student.getFullName();

        // Assert
        assertThat(fullName).isEqualTo("Alice Smith");
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 10, 15, 18})
    @DisplayName("Should accept valid ages between 3 and 18 (BR-1)")
    void shouldAcceptValidAges(int age) {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(age);

        // Act & Assert
        assertThatNoException().isThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                dob,
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 19, 25, 50})
    @DisplayName("Should reject invalid ages outside 3-18 range (BR-1)")
    void shouldRejectInvalidAges(int age) {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(age);

        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                dob,
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Student age must be between 3 and 18 years")
        .hasMessageContaining("BR-1");
    }

    @Test
    @DisplayName("Should reject future date of birth")
    void shouldRejectFutureDateOfBirth() {
        // Arrange
        LocalDate futureDob = LocalDate.now().plusDays(1);

        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                futureDob,
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Date of birth cannot be in the future");
    }

    @Test
    @DisplayName("Should reject null date of birth")
    void shouldRejectNullDateOfBirth() {
        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                null,
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Date of birth is required");
    }

    @Test
    @DisplayName("Should reject null or empty first name")
    void shouldRejectNullOrEmptyFirstName() {
        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                null,
                "Smith",
                LocalDate.now().minusYears(10),
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("First name is required");
    }

    @Test
    @DisplayName("Should reject null or empty last name")
    void shouldRejectNullOrEmptyLastName() {
        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "",
                LocalDate.now().minusYears(10),
                Mobile.of("9876543210"),
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Last name is required");
    }

    @Test
    @DisplayName("Should reject null mobile")
    void shouldRejectNullMobile() {
        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                LocalDate.now().minusYears(10),
                null,
                GuardianInfo.of("John Doe", null)
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Mobile number is required");
    }

    @Test
    @DisplayName("Should reject null guardian info")
    void shouldRejectNullGuardianInfo() {
        // Act & Assert
        assertThatThrownBy(() ->
            Student.register(
                "Alice",
                "Smith",
                LocalDate.now().minusYears(10),
                Mobile.of("9876543210"),
                null
            )
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Guardian information is required");
    }

    @Test
    @DisplayName("Should update profile successfully for active student")
    void shouldUpdateProfileForActiveStudent() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));
        Mobile newMobile = Mobile.of("9999999999");

        // Act
        student.updateProfile("Bob", "Johnson", newMobile);

        // Assert
        assertThat(student.getFirstName()).isEqualTo("Bob");
        assertThat(student.getLastName()).isEqualTo("Johnson");
        assertThat(student.getMobile()).isEqualTo(newMobile);
    }

    @Test
    @DisplayName("Should throw exception when updating inactive student profile")
    void shouldThrowExceptionWhenUpdatingInactiveStudentProfile() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));
        student.deactivate();

        // Act & Assert
        assertThatThrownBy(() ->
            student.updateProfile("Bob", "Johnson", Mobile.of("9999999999"))
        )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot update profile of inactive student");
    }

    @Test
    @DisplayName("Should deactivate active student")
    void shouldDeactivateActiveStudent() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));

        // Act
        student.deactivate();

        // Assert
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when deactivating already inactive student")
    void shouldThrowExceptionWhenDeactivatingInactiveStudent() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));
        student.deactivate();

        // Act & Assert
        assertThatThrownBy(student::deactivate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Student is already inactive");
    }

    @Test
    @DisplayName("Should activate inactive student")
    void shouldActivateInactiveStudent() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));
        student.deactivate();

        // Act
        student.activate();

        // Assert
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when activating already active student")
    void shouldThrowExceptionWhenActivatingActiveStudent() {
        // Arrange
        Student student = createValidStudent(LocalDate.now().minusYears(10));

        // Act & Assert
        assertThatThrownBy(student::activate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Student is already active");
    }

    @Test
    @DisplayName("Should trim whitespace from names during registration")
    void shouldTrimWhitespaceFromNames() {
        // Arrange & Act
        Student student = Student.register(
            "  Alice  ",
            "  Smith  ",
            LocalDate.now().minusYears(10),
            Mobile.of("9876543210"),
            GuardianInfo.of("John Doe", null)
        );

        // Assert
        assertThat(student.getFirstName()).isEqualTo("Alice");
        assertThat(student.getLastName()).isEqualTo("Smith");
    }

    // Helper method to create a valid student for testing
    private Student createValidStudent(LocalDate dob) {
        return Student.register(
            "Alice",
            "Smith",
            dob,
            Mobile.of("9876543210"),
            GuardianInfo.of("John Doe", "Jane Doe")
        );
    }
}
