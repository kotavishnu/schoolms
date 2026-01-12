package com.schoolms.student.domain.model;

import com.schoolms.student.domain.exception.InvalidAgeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Student domain model (QA-001 to QA-005)
 * Tests business logic, validation, and domain behavior
 *
 * Coverage:
 * - Valid student creation
 * - Age validation (3-18 years)
 * - Age boundary testing
 * - Required field validation
 * - Profile update logic
 * - Status management
 * - Immutable field protection
 */
@DisplayName("Student Domain Model Tests")
class StudentTest {

    private static final String VALID_STUDENT_ID = "STU-2026-00001";
    private static final String VALID_FIRST_NAME = "John";
    private static final String VALID_LAST_NAME = "Doe";
    private static final String VALID_ADHAAR = "123456789012";
    private static final String VALID_PHONE = "9876543210";
    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final String VALID_ADDRESS = "123 Main Street, City";
    private static final String VALID_GUARDIAN = "Robert Doe";
    private static final String VALID_MOTHER = "Jane Doe";
    private static final String VALID_MARKS = "Mole on left hand";

    @Nested
    @DisplayName("Student Creation Tests")
    class StudentCreationTests {

        @Test
        @DisplayName("Should create student with valid data successfully")
        void shouldCreateStudentWithValidData() {
            // Given - valid student data with age 10 (within 3-18 range)
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When
            Student student = Student.register(
                VALID_STUDENT_ID,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                validDob,
                VALID_ADHAAR,
                VALID_PHONE,
                VALID_EMAIL,
                VALID_ADDRESS,
                VALID_GUARDIAN,
                VALID_MOTHER,
                VALID_MARKS
            );

            // Then
            assertThat(student).isNotNull();
            assertThat(student.getId().getValue()).isEqualTo(VALID_STUDENT_ID);
            assertThat(student.getFirstName()).isEqualTo(VALID_FIRST_NAME);
            assertThat(student.getLastName()).isEqualTo(VALID_LAST_NAME);
            assertThat(student.getDateOfBirth()).isEqualTo(validDob);
            assertThat(student.getAdhaarNumber()).isEqualTo(VALID_ADHAAR);
            assertThat(student.getPhone()).isEqualTo(VALID_PHONE);
            assertThat(student.getEmail()).isEqualTo(VALID_EMAIL);
            assertThat(student.getAddress()).isEqualTo(VALID_ADDRESS);
            assertThat(student.getGuardianName()).isEqualTo(VALID_GUARDIAN);
            assertThat(student.getMotherName()).isEqualTo(VALID_MOTHER);
            assertThat(student.getIdentificationMarks()).isEqualTo(VALID_MARKS);
            assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
            assertThat(student.getCreatedAt()).isNotNull();
            assertThat(student.getUpdatedAt()).isNotNull();
            assertThat(student.getVersion()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should set default status to ACTIVE when creating student")
        void shouldSetDefaultStatusToActive() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // Then
            assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should set createdAt and updatedAt timestamps on creation")
        void shouldSetTimestampsOnCreation() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Instant beforeCreation = Instant.now().minusSeconds(1);

            // When
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // Then
            Instant afterCreation = Instant.now().plusSeconds(1);
            assertThat(student.getCreatedAt()).isBetween(beforeCreation, afterCreation);
            assertThat(student.getUpdatedAt()).isBetween(beforeCreation, afterCreation);
        }
    }

    @Nested
    @DisplayName("Age Validation Tests (BR-1: Age Range 3-18)")
    class AgeValidationTests {

        @Test
        @DisplayName("Should calculate age correctly from date of birth")
        void shouldCalculateAgeCorrectly() {
            // Given - student with DOB exactly 10 years ago
            LocalDate dobTenYearsAgo = LocalDate.now().minusYears(10);

            // When
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dobTenYearsAgo,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // Then
            assertThat(student.getAge()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should accept minimum valid age of 3 years")
        void shouldAcceptMinimumValidAge() {
            // Given - DOB exactly 3 years ago
            LocalDate dobThreeYearsAgo = LocalDate.now().minusYears(3);

            // When/Then - should not throw exception
            assertThatNoException().isThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dobThreeYearsAgo,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            );
        }

        @Test
        @DisplayName("Should accept maximum valid age of 18 years")
        void shouldAcceptMaximumValidAge() {
            // Given - DOB exactly 18 years ago
            LocalDate dobEighteenYearsAgo = LocalDate.now().minusYears(18);

            // When/Then - should not throw exception
            assertThatNoException().isThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dobEighteenYearsAgo,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            );
        }

        @Test
        @DisplayName("Should reject age below 3 years (boundary test)")
        void shouldRejectAgeBelowMinimum() {
            // Given - DOB 2 years ago (invalid)
            LocalDate dobTwoYearsAgo = LocalDate.now().minusYears(2);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dobTwoYearsAgo,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(InvalidAgeException.class)
            .hasMessageContaining("Age 2 is outside allowed range (3-18 years)");
        }

        @Test
        @DisplayName("Should reject age above 18 years (boundary test)")
        void shouldRejectAgeAboveMaximum() {
            // Given - DOB 19 years ago (invalid)
            LocalDate dobNineteenYearsAgo = LocalDate.now().minusYears(19);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dobNineteenYearsAgo,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(InvalidAgeException.class)
            .hasMessageContaining("Age 19 is outside allowed range (3-18 years)");
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 8, 12, 15, 17})
        @DisplayName("Should accept valid ages within range")
        void shouldAcceptValidAgesWithinRange(int age) {
            // Given
            LocalDate dob = LocalDate.now().minusYears(age);

            // When/Then
            assertThatNoException().isThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 19, 20, 25})
        @DisplayName("Should reject invalid ages outside range")
        void shouldRejectInvalidAgesOutsideRange(int age) {
            // Given
            LocalDate dob = LocalDate.now().minusYears(age);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, dob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(InvalidAgeException.class);
        }
    }

    @Nested
    @DisplayName("Required Field Validation Tests")
    class RequiredFieldValidationTests {

        @Test
        @DisplayName("Should reject null first name")
        void shouldRejectNullFirstName() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, null, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name is required");
        }

        @Test
        @DisplayName("Should reject blank first name")
        void shouldRejectBlankFirstName() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, "   ", VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name is required");
        }

        @Test
        @DisplayName("Should reject null last name")
        void shouldRejectNullLastName() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, null, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Last name is required");
        }

        @Test
        @DisplayName("Should reject null date of birth")
        void shouldRejectNullDateOfBirth() {
            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, null,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Date of birth is required");
        }

        @Test
        @DisplayName("Should reject invalid Adhaar number format")
        void shouldRejectInvalidAdhaarFormat() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    "12345", VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Adhaar number must be exactly 12 digits");
        }

        @Test
        @DisplayName("Should reject invalid phone format")
        void shouldRejectInvalidPhoneFormat() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, "12345", VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Phone must be exactly 10 digits");
        }

        @Test
        @DisplayName("Should reject invalid email format")
        void shouldRejectInvalidEmailFormat() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, "invalid-email", VALID_ADDRESS,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("Should reject null guardian name")
        void shouldRejectNullGuardianName() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    null, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Guardian name is required");
        }

        @Test
        @DisplayName("Should reject null mother name")
        void shouldRejectNullMotherName() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN, null, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mother name is required");
        }

        @Test
        @DisplayName("Should reject null address")
        void shouldRejectNullAddress() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);

            // When/Then
            assertThatThrownBy(() ->
                Student.register(
                    VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                    VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, null,
                    VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
                )
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Address is required");
        }
    }

    @Nested
    @DisplayName("Profile Update Tests")
    class ProfileUpdateTests {

        @Test
        @DisplayName("Should update editable fields successfully")
        void shouldUpdateEditableFields() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // When
            student.updateProfile("Jonathan", "Smith", "9123456789");

            // Then
            assertThat(student.getFirstName()).isEqualTo("Jonathan");
            assertThat(student.getLastName()).isEqualTo("Smith");
            assertThat(student.getPhone()).isEqualTo("9123456789");
        }

        @Test
        @DisplayName("Should update updatedAt timestamp when profile is updated")
        void shouldUpdateTimestampOnProfileUpdate() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );
            Instant originalUpdatedAt = student.getUpdatedAt();

            // When
            try {
                Thread.sleep(10); // Ensure time difference
            } catch (InterruptedException e) {
                // Ignore
            }
            student.updateProfile("Jonathan", "Smith", "9123456789");

            // Then
            assertThat(student.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        @DisplayName("Should ignore null values in profile update")
        void shouldIgnoreNullValuesInUpdate() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // When
            student.updateProfile(null, "Smith", null);

            // Then
            assertThat(student.getFirstName()).isEqualTo(VALID_FIRST_NAME); // Unchanged
            assertThat(student.getLastName()).isEqualTo("Smith"); // Changed
            assertThat(student.getPhone()).isEqualTo(VALID_PHONE); // Unchanged
        }

        @Test
        @DisplayName("Should ignore blank values in profile update")
        void shouldIgnoreBlankValuesInUpdate() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // When
            student.updateProfile("   ", "Smith", "   ");

            // Then
            assertThat(student.getFirstName()).isEqualTo(VALID_FIRST_NAME); // Unchanged
            assertThat(student.getLastName()).isEqualTo("Smith"); // Changed
            assertThat(student.getPhone()).isEqualTo(VALID_PHONE); // Unchanged
        }

        @Test
        @DisplayName("Should validate updated phone format")
        void shouldValidateUpdatedPhoneFormat() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // When/Then
            assertThatThrownBy(() ->
                student.updateProfile("Jonathan", "Smith", "12345")
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Phone must be exactly 10 digits");
        }
    }

    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {

        @Test
        @DisplayName("Should activate student successfully")
        void shouldActivateStudent() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );
            student.deactivate(); // First deactivate

            // When
            student.activate();

            // Then
            assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should deactivate student successfully")
        void shouldDeactivateStudent() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );

            // When
            student.deactivate();

            // Then
            assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        }

        @Test
        @DisplayName("Should update updatedAt timestamp when status changes")
        void shouldUpdateTimestampOnStatusChange() {
            // Given
            LocalDate validDob = LocalDate.now().minusYears(10);
            Student student = Student.register(
                VALID_STUDENT_ID, VALID_FIRST_NAME, VALID_LAST_NAME, validDob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS
            );
            Instant originalUpdatedAt = student.getUpdatedAt();

            // When
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
            student.deactivate();

            // Then
            assertThat(student.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("Reconstruction Tests")
    class ReconstructionTests {

        @Test
        @DisplayName("Should reconstruct student from persistence successfully")
        void shouldReconstructStudentFromPersistence() {
            // Given
            StudentId id = new StudentId(VALID_STUDENT_ID);
            LocalDate dob = LocalDate.now().minusYears(10);
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            // When
            Student student = Student.reconstruct(
                id, VALID_FIRST_NAME, VALID_LAST_NAME, dob,
                VALID_ADHAAR, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN, VALID_MOTHER, VALID_MARKS,
                StudentStatus.INACTIVE, createdAt, updatedAt, 5
            );

            // Then
            assertThat(student).isNotNull();
            assertThat(student.getId()).isEqualTo(id);
            assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);
            assertThat(student.getCreatedAt()).isEqualTo(createdAt);
            assertThat(student.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(student.getVersion()).isEqualTo(5);
        }
    }
}
