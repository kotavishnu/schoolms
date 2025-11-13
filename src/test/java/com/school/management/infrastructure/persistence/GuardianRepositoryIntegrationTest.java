package com.school.management.infrastructure.persistence;

import com.school.management.domain.student.*;
import com.school.management.domain.user.User;
import com.school.management.domain.security.Role;
import com.school.management.infrastructure.security.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for GuardianRepository
 * Following TDD methodology - tests written FIRST
 *
 * @author School Management System
 * @version 1.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(EncryptionService.class)
@DisplayName("GuardianRepository Integration Tests")
class GuardianRepositoryIntegrationTest {

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    private User testUser;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Clean up
        guardianRepository.deleteAll();
        studentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ADMIN);
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);

        // Create test student
        testStudent = new Student();
        testStudent.setStudentCode("STU-2025-00001");
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setDateOfBirth(LocalDate.of(2015, 1, 1));
        testStudent.setGender(Gender.MALE);
        testStudent.setMobile("9876543210");
        testStudent.setAdmissionDate(LocalDate.now());
        testStudent.setStatus(StudentStatus.ACTIVE);
        testStudent.encryptFields(encryptionService);
        testStudent.setCreatedBy(testUser.getId());
        testStudent.setUpdatedBy(testUser.getId());
        testStudent = studentRepository.save(testStudent);
    }

    @Test
    @DisplayName("Should save guardian successfully")
    void shouldSaveGuardian() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.FATHER, true);

        // When
        Guardian saved = guardianRepository.save(guardian);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudent()).isEqualTo(testStudent);
        assertThat(saved.getRelationship()).isEqualTo(Relationship.FATHER);
        assertThat(saved.getIsPrimary()).isTrue();
    }

    @Test
    @DisplayName("Should find guardian by id")
    void shouldFindGuardianById() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.MOTHER, false);
        guardian = guardianRepository.save(guardian);

        // When
        Optional<Guardian> found = guardianRepository.findById(guardian.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRelationship()).isEqualTo(Relationship.MOTHER);

        // Decrypt and verify
        found.get().decryptFields(encryptionService);
        assertThat(found.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Should find all guardians by student")
    void shouldFindAllGuardiansByStudent() {
        // Given
        Guardian father = createTestGuardian(testStudent, Relationship.FATHER, true);
        Guardian mother = createTestGuardian(testStudent, Relationship.MOTHER, false);
        guardianRepository.save(father);
        guardianRepository.save(mother);

        // When
        List<Guardian> guardians = guardianRepository.findByStudent(testStudent);

        // Then
        assertThat(guardians).hasSize(2);
        assertThat(guardians).extracting(Guardian::getRelationship)
            .containsExactlyInAnyOrder(Relationship.FATHER, Relationship.MOTHER);
    }

    @Test
    @DisplayName("Should find guardian by student and relationship")
    void shouldFindGuardianByStudentAndRelationship() {
        // Given
        Guardian father = createTestGuardian(testStudent, Relationship.FATHER, true);
        guardianRepository.save(father);

        // When
        Optional<Guardian> found = guardianRepository.findByStudentAndRelationship(
            testStudent, Relationship.FATHER);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRelationship()).isEqualTo(Relationship.FATHER);
    }

    @Test
    @DisplayName("Should find primary guardian for student")
    void shouldFindPrimaryGuardianForStudent() {
        // Given
        Guardian father = createTestGuardian(testStudent, Relationship.FATHER, true);
        Guardian mother = createTestGuardian(testStudent, Relationship.MOTHER, false);
        guardianRepository.save(father);
        guardianRepository.save(mother);

        // When
        Optional<Guardian> primary = guardianRepository.findByStudentAndIsPrimary(
            testStudent, true);

        // Then
        assertThat(primary).isPresent();
        assertThat(primary.get().getRelationship()).isEqualTo(Relationship.FATHER);
        assertThat(primary.get().getIsPrimary()).isTrue();
    }

    @Test
    @DisplayName("Should count guardians by student")
    void shouldCountGuardiansByStudent() {
        // Given
        Guardian father = createTestGuardian(testStudent, Relationship.FATHER, true);
        Guardian mother = createTestGuardian(testStudent, Relationship.MOTHER, false);
        guardianRepository.save(father);
        guardianRepository.save(mother);

        // When
        long count = guardianRepository.countByStudent(testStudent);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check if guardian exists by mobile hash")
    void shouldCheckGuardianExistsByMobileHash() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.FATHER, true);
        guardianRepository.save(guardian);

        // When
        boolean exists = guardianRepository.existsByMobileHash(guardian.getMobileHash());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should delete guardian by id")
    void shouldDeleteGuardianById() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.FATHER, true);
        guardian = guardianRepository.save(guardian);
        Long guardianId = guardian.getId();

        // When
        guardianRepository.deleteById(guardianId);

        // Then
        Optional<Guardian> deleted = guardianRepository.findById(guardianId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should cascade delete guardians when student is deleted")
    void shouldCascadeDeleteGuardiansWithStudent() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.FATHER, true);
        guardianRepository.save(guardian);
        Long studentId = testStudent.getId();

        // When
        studentRepository.deleteById(studentId);

        // Then
        List<Guardian> guardians = guardianRepository.findByStudent(testStudent);
        assertThat(guardians).isEmpty();
    }

    @Test
    @DisplayName("Should find guardians by student id")
    void shouldFindGuardiansByStudentId() {
        // Given
        Guardian father = createTestGuardian(testStudent, Relationship.FATHER, true);
        Guardian mother = createTestGuardian(testStudent, Relationship.MOTHER, false);
        guardianRepository.save(father);
        guardianRepository.save(mother);

        // When
        List<Guardian> guardians = guardianRepository.findByStudentId(testStudent.getId());

        // Then
        assertThat(guardians).hasSize(2);
    }

    @Test
    @DisplayName("Should update guardian successfully")
    void shouldUpdateGuardian() {
        // Given
        Guardian guardian = createTestGuardian(testStudent, Relationship.FATHER, false);
        guardian = guardianRepository.save(guardian);

        // When
        guardian.setIsPrimary(true);
        guardian.setOccupation("Engineer");
        Guardian updated = guardianRepository.save(guardian);

        // Then
        assertThat(updated.getIsPrimary()).isTrue();
        assertThat(updated.getOccupation()).isEqualTo("Engineer");
    }

    @Test
    @DisplayName("Should enforce unique student-relationship constraint")
    void shouldEnforceUniqueStudentRelationshipConstraint() {
        // Given
        Guardian father1 = createTestGuardian(testStudent, Relationship.FATHER, true);
        guardianRepository.save(father1);

        // When/Then - Attempting to save another FATHER should fail
        Guardian father2 = createTestGuardian(testStudent, Relationship.FATHER, false);

        assertThatThrownBy(() -> {
            guardianRepository.save(father2);
            guardianRepository.flush();
        }).isInstanceOf(Exception.class);
    }

    private Guardian createTestGuardian(Student student, Relationship relationship,
                                       boolean isPrimary) {
        Guardian guardian = new Guardian();
        guardian.setStudent(student);
        guardian.setRelationship(relationship);
        guardian.setFirstName(relationship == Relationship.FATHER ? "John" : "Jane");
        guardian.setLastName("Doe");
        guardian.setMobile("9876543" + (relationship == Relationship.FATHER ? "210" : "211"));
        guardian.setEmail(relationship.name().toLowerCase() + "@example.com");
        guardian.setOccupation("Business");
        guardian.setIsPrimary(isPrimary);
        guardian.encryptFields(encryptionService);
        guardian.setCreatedBy(testUser.getId());
        guardian.setUpdatedBy(testUser.getId());

        return guardian;
    }
}
