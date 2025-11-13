package com.school.management.domain.student;

import com.school.management.infrastructure.security.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Student entity
 * Tests business logic, validation, and encryption/decryption
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Student Entity Tests")
class StudentTest {

    @Mock
    private EncryptionService encryptionService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
    }

    @Test
    @DisplayName("Should create student with all required fields")
    void shouldCreateStudentWithAllRequiredFields() {
        // Arrange & Act
        student.setStudentCode("STU-2025-00001");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setDateOfBirth(LocalDate.of(2010, 1, 15));
        student.setGender(Gender.MALE);
        student.setMobile("9876543210");
        student.setAdmissionDate(LocalDate.now());
        student.setStatus(StudentStatus.ACTIVE);

        // Assert
        assertThat(student.getStudentCode()).isEqualTo("STU-2025-00001");
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getDateOfBirth()).isEqualTo(LocalDate.of(2010, 1, 15));
        assertThat(student.getGender()).isEqualTo(Gender.MALE);
        assertThat(student.getMobile()).isEqualTo("9876543210");
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should default status to ACTIVE")
    void shouldDefaultStatusToActive() {
        // Arrange & Act
        Student newStudent = new Student();

        // Assert
        assertThat(newStudent.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should calculate age correctly from date of birth")
    void shouldCalculateAgeCorrectlyFromDateOfBirth() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(10).minusMonths(6);
        student.setDateOfBirth(birthDate);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isEqualTo(10);
    }

    @Test
    @DisplayName("Should calculate age when birthday has not occurred this year")
    void shouldCalculateAgeWhenBirthdayHasNotOccurredThisYear() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(10).plusMonths(1);
        student.setDateOfBirth(birthDate);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isEqualTo(9);
    }

    @Test
    @DisplayName("Should return true for isActive when status is ACTIVE")
    void shouldReturnTrueForIsActiveWhenStatusIsActive() {
        // Arrange
        student.setStatus(StudentStatus.ACTIVE);

        // Act & Assert
        assertThat(student.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return false for isActive when status is not ACTIVE")
    void shouldReturnFalseForIsActiveWhenStatusIsNotActive() {
        // Arrange & Act & Assert
        student.setStatus(StudentStatus.INACTIVE);
        assertThat(student.isActive()).isFalse();

        student.setStatus(StudentStatus.GRADUATED);
        assertThat(student.isActive()).isFalse();

        student.setStatus(StudentStatus.TRANSFERRED);
        assertThat(student.isActive()).isFalse();

        student.setStatus(StudentStatus.WITHDRAWN);
        assertThat(student.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should encrypt all PII fields")
    void shouldEncryptAllPiiFields() {
        // Arrange
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setDateOfBirth(LocalDate.of(2010, 1, 15));
        student.setEmail("john.doe@example.com");
        student.setMobile("9876543210");
        student.setAddress("123 Main St, City");

        when(encryptionService.encrypt("John")).thenReturn("encrypted_john".getBytes());
        when(encryptionService.encrypt("Doe")).thenReturn("encrypted_doe".getBytes());
        when(encryptionService.encrypt("2010-01-15")).thenReturn("encrypted_dob".getBytes());
        when(encryptionService.encrypt("john.doe@example.com")).thenReturn("encrypted_email".getBytes());
        when(encryptionService.encrypt("9876543210")).thenReturn("encrypted_mobile".getBytes());
        when(encryptionService.encrypt("123 Main St, City")).thenReturn("encrypted_address".getBytes());
        when(encryptionService.hashMobile("9876543210")).thenReturn("hashed_mobile");

        // Act
        student.encryptFields(encryptionService);

        // Assert
        assertThat(student.getFirstNameEncrypted()).isEqualTo("encrypted_john".getBytes());
        assertThat(student.getLastNameEncrypted()).isEqualTo("encrypted_doe".getBytes());
        assertThat(student.getDateOfBirthEncrypted()).isEqualTo("encrypted_dob".getBytes());
        assertThat(student.getEmailEncrypted()).isEqualTo("encrypted_email".getBytes());
        assertThat(student.getMobileEncrypted()).isEqualTo("encrypted_mobile".getBytes());
        assertThat(student.getAddressEncrypted()).isEqualTo("encrypted_address".getBytes());
        assertThat(student.getMobileHash()).isEqualTo("hashed_mobile");
    }

    @Test
    @DisplayName("Should decrypt all PII fields")
    void shouldDecryptAllPiiFields() {
        // Arrange
        student.setFirstNameEncrypted("encrypted_john".getBytes());
        student.setLastNameEncrypted("encrypted_doe".getBytes());
        student.setDateOfBirthEncrypted("encrypted_dob".getBytes());
        student.setEmailEncrypted("encrypted_email".getBytes());
        student.setMobileEncrypted("encrypted_mobile".getBytes());
        student.setAddressEncrypted("encrypted_address".getBytes());

        when(encryptionService.decrypt("encrypted_john".getBytes())).thenReturn("John");
        when(encryptionService.decrypt("encrypted_doe".getBytes())).thenReturn("Doe");
        when(encryptionService.decrypt("encrypted_dob".getBytes())).thenReturn("2010-01-15");
        when(encryptionService.decrypt("encrypted_email".getBytes())).thenReturn("john.doe@example.com");
        when(encryptionService.decrypt("encrypted_mobile".getBytes())).thenReturn("9876543210");
        when(encryptionService.decrypt("encrypted_address".getBytes())).thenReturn("123 Main St, City");

        // Act
        student.decryptFields(encryptionService);

        // Assert
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getDateOfBirth()).isEqualTo(LocalDate.parse("2010-01-15"));
        assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(student.getMobile()).isEqualTo("9876543210");
        assertThat(student.getAddress()).isEqualTo("123 Main St, City");
    }

    @Test
    @DisplayName("Should handle null optional fields during encryption")
    void shouldHandleNullOptionalFieldsDuringEncryption() {
        // Arrange
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setMobile("9876543210");
        student.setEmail(null);
        student.setAddress(null);

        when(encryptionService.encrypt(anyString())).thenReturn("encrypted".getBytes());
        when(encryptionService.hashMobile(anyString())).thenReturn("hashed");

        // Act
        student.encryptFields(encryptionService);

        // Assert
        assertThat(student.getEmailEncrypted()).isNull();
        assertThat(student.getAddressEncrypted()).isNull();
    }

    @Test
    @DisplayName("Should handle null optional fields during decryption")
    void shouldHandleNullOptionalFieldsDuringDecryption() {
        // Arrange
        student.setFirstNameEncrypted("encrypted_john".getBytes());
        student.setLastNameEncrypted("encrypted_doe".getBytes());
        student.setMobileEncrypted("encrypted_mobile".getBytes());
        student.setEmailEncrypted(null);
        student.setAddressEncrypted(null);

        when(encryptionService.decrypt(any(byte[].class))).thenReturn("decrypted");

        // Act
        student.decryptFields(encryptionService);

        // Assert
        assertThat(student.getEmail()).isNull();
        assertThat(student.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should validate minimum age of 3 years")
    void shouldValidateMinimumAgeOf3Years() {
        // Arrange
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        student.setDateOfBirth(twoYearsAgo);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isLessThan(3);
    }

    @Test
    @DisplayName("Should validate maximum age of 18 years")
    void shouldValidateMaximumAgeOf18Years() {
        // Arrange
        LocalDate nineteenYearsAgo = LocalDate.now().minusYears(19);
        student.setDateOfBirth(nineteenYearsAgo);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isGreaterThan(18);
    }

    @Test
    @DisplayName("Should accept valid age of 3 years")
    void shouldAcceptValidAgeOf3Years() {
        // Arrange
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        student.setDateOfBirth(threeYearsAgo);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isEqualTo(3);
    }

    @Test
    @DisplayName("Should accept valid age of 18 years")
    void shouldAcceptValidAgeOf18Years() {
        // Arrange
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        student.setDateOfBirth(eighteenYearsAgo);

        // Act
        int age = student.getAge();

        // Assert
        assertThat(age).isEqualTo(18);
    }

    @Test
    @DisplayName("Should initialize guardians collection as empty set")
    void shouldInitializeGuardiansCollectionAsEmptySet() {
        // Arrange & Act
        Student newStudent = new Student();

        // Assert
        assertThat(newStudent.getGuardians()).isNotNull();
        assertThat(newStudent.getGuardians()).isEmpty();
    }

    @Test
    @DisplayName("Should initialize enrollments collection as empty set")
    void shouldInitializeEnrollmentsCollectionAsEmptySet() {
        // Arrange & Act
        Student newStudent = new Student();

        // Assert
        assertThat(newStudent.getEnrollments()).isNotNull();
        assertThat(newStudent.getEnrollments()).isEmpty();
    }

    @Test
    @DisplayName("Should initialize fee journals collection as empty set")
    void shouldInitializeFeeJournalsCollectionAsEmptySet() {
        // Arrange & Act
        Student newStudent = new Student();

        // Assert
        assertThat(newStudent.getFeeJournals()).isNotNull();
        assertThat(newStudent.getFeeJournals()).isEmpty();
    }

    @Test
    @DisplayName("Should support all student statuses")
    void shouldSupportAllStudentStatuses() {
        // Act & Assert
        student.setStatus(StudentStatus.ACTIVE);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);

        student.setStatus(StudentStatus.INACTIVE);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.INACTIVE);

        student.setStatus(StudentStatus.GRADUATED);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.GRADUATED);

        student.setStatus(StudentStatus.TRANSFERRED);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.TRANSFERRED);

        student.setStatus(StudentStatus.WITHDRAWN);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.WITHDRAWN);
    }

    @Test
    @DisplayName("Should support all genders")
    void shouldSupportAllGenders() {
        // Act & Assert
        student.setGender(Gender.MALE);
        assertThat(student.getGender()).isEqualTo(Gender.MALE);

        student.setGender(Gender.FEMALE);
        assertThat(student.getGender()).isEqualTo(Gender.FEMALE);

        student.setGender(Gender.OTHER);
        assertThat(student.getGender()).isEqualTo(Gender.OTHER);
    }

    @Test
    @DisplayName("Should store blood group")
    void shouldStoreBloodGroup() {
        // Arrange & Act
        student.setBloodGroup("A+");

        // Assert
        assertThat(student.getBloodGroup()).isEqualTo("A+");
    }

    @Test
    @DisplayName("Should store photo URL")
    void shouldStorePhotoUrl() {
        // Arrange & Act
        student.setPhotoUrl("https://example.com/photos/student123.jpg");

        // Assert
        assertThat(student.getPhotoUrl()).isEqualTo("https://example.com/photos/student123.jpg");
    }
}
