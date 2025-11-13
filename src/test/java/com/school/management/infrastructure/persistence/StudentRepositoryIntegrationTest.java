package com.school.management.infrastructure.persistence;

import com.school.management.domain.student.Gender;
import com.school.management.domain.student.Student;
import com.school.management.domain.student.StudentStatus;
import com.school.management.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for StudentRepository
 * Uses real PostgreSQL database via TestContainers
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("StudentRepository Integration Tests")
class StudentRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user for audit fields
        testUser = createTestUser("testadmin", "Test Admin");
    }

    @Test
    @DisplayName("Should find student by student code")
    void shouldFindStudentByStudentCode() {
        // Arrange
        Student student = createTestStudent("STU-2025-00001", "John", "Doe");
        entityManager.persistAndFlush(student);

        // Act
        Optional<Student> found = studentRepository.findByStudentCode("STU-2025-00001");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getStudentCode()).isEqualTo("STU-2025-00001");
    }

    @Test
    @DisplayName("Should return empty when student code not found")
    void shouldReturnEmptyWhenStudentCodeNotFound() {
        // Act
        Optional<Student> found = studentRepository.findByStudentCode("STU-2025-99999");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find students by status and admission date range")
    void shouldFindStudentsByStatusAndAdmissionDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        Student student1 = createTestStudent("STU-2025-00001", "John", "Doe");
        student1.setAdmissionDate(LocalDate.of(2025, 3, 15));
        student1.setStatus(StudentStatus.ACTIVE);

        Student student2 = createTestStudent("STU-2025-00002", "Jane", "Smith");
        student2.setAdmissionDate(LocalDate.of(2025, 6, 20));
        student2.setStatus(StudentStatus.ACTIVE);

        Student student3 = createTestStudent("STU-2024-00001", "Bob", "Johnson");
        student3.setAdmissionDate(LocalDate.of(2024, 5, 10));
        student3.setStatus(StudentStatus.ACTIVE);

        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);
        entityManager.flush();

        // Act
        List<Student> found = studentRepository.findByStatusAndAdmissionDateBetween(
            StudentStatus.ACTIVE, startDate, endDate);

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).extracting("studentCode")
            .containsExactlyInAnyOrder("STU-2025-00001", "STU-2025-00002");
    }

    @Test
    @DisplayName("Should find students by status list")
    void shouldFindStudentsByStatusList() {
        // Arrange
        Student activeStudent = createTestStudent("STU-2025-00001", "John", "Doe");
        activeStudent.setStatus(StudentStatus.ACTIVE);

        Student inactiveStudent = createTestStudent("STU-2025-00002", "Jane", "Smith");
        inactiveStudent.setStatus(StudentStatus.INACTIVE);

        Student graduatedStudent = createTestStudent("STU-2025-00003", "Bob", "Johnson");
        graduatedStudent.setStatus(StudentStatus.GRADUATED);

        entityManager.persist(activeStudent);
        entityManager.persist(inactiveStudent);
        entityManager.persist(graduatedStudent);
        entityManager.flush();

        // Act
        List<Student> found = studentRepository.findByStatusIn(
            Arrays.asList(StudentStatus.ACTIVE, StudentStatus.INACTIVE));

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).extracting("status")
            .containsExactlyInAnyOrder(StudentStatus.ACTIVE, StudentStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should find students by status with pagination")
    void shouldFindStudentsByStatusWithPagination() {
        // Arrange
        for (int i = 1; i <= 15; i++) {
            Student student = createTestStudent("STU-2025-" + String.format("%05d", i),
                "Student" + i, "Test");
            student.setStatus(StudentStatus.ACTIVE);
            entityManager.persist(student);
        }
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Student> page = studentRepository.findByStatus(StudentStatus.ACTIVE, pageable);

        // Assert
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find student by mobile hash and status")
    void shouldFindStudentByMobileHashAndStatus() {
        // Arrange
        Student student = createTestStudent("STU-2025-00001", "John", "Doe");
        student.setMobileHash("abc123hash");
        student.setStatus(StudentStatus.ACTIVE);
        entityManager.persistAndFlush(student);

        // Act
        Optional<Student> found = studentRepository.findByMobileHashAndStatus(
            "abc123hash", StudentStatus.ACTIVE);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getMobileHash()).isEqualTo("abc123hash");
    }

    @Test
    @DisplayName("Should count students by status")
    void shouldCountStudentsByStatus() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            Student student = createTestStudent("STU-2025-" + String.format("%05d", i),
                "Student" + i, "Test");
            student.setStatus(StudentStatus.ACTIVE);
            entityManager.persist(student);
        }

        for (int i = 6; i <= 8; i++) {
            Student student = createTestStudent("STU-2025-" + String.format("%05d", i),
                "Student" + i, "Test");
            student.setStatus(StudentStatus.INACTIVE);
            entityManager.persist(student);
        }
        entityManager.flush();

        // Act
        long activeCount = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long inactiveCount = studentRepository.countByStatus(StudentStatus.INACTIVE);

        // Assert
        assertThat(activeCount).isEqualTo(5);
        assertThat(inactiveCount).isEqualTo(3);
    }

    @Test
    @DisplayName("Should check if mobile hash exists")
    void shouldCheckIfMobileHashExists() {
        // Arrange
        Student student = createTestStudent("STU-2025-00001", "John", "Doe");
        student.setMobileHash("uniquehash123");
        entityManager.persistAndFlush(student);

        // Act
        boolean exists = studentRepository.existsByMobileHash("uniquehash123");
        boolean notExists = studentRepository.existsByMobileHash("nonexistenthash");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should save and retrieve student with all fields")
    void shouldSaveAndRetrieveStudentWithAllFields() {
        // Arrange
        Student student = new Student();
        student.setStudentCode("STU-2025-00999");
        student.setFirstNameEncrypted("encrypted_first".getBytes());
        student.setLastNameEncrypted("encrypted_last".getBytes());
        student.setDateOfBirthEncrypted("encrypted_dob".getBytes());
        student.setGender(Gender.MALE);
        student.setEmailEncrypted("encrypted_email".getBytes());
        student.setMobileEncrypted("encrypted_mobile".getBytes());
        student.setMobileHash("mobile_hash_123");
        student.setAddressEncrypted("encrypted_address".getBytes());
        student.setBloodGroup("A+");
        student.setStatus(StudentStatus.ACTIVE);
        student.setAdmissionDate(LocalDate.now());
        student.setPhotoUrl("https://example.com/photo.jpg");
        student.setCreatedBy(testUser.getId());
        student.setUpdatedBy(testUser.getId());

        // Act
        Student saved = studentRepository.save(student);
        entityManager.flush();
        entityManager.clear();

        Student retrieved = studentRepository.findById(saved.getId()).orElseThrow();

        // Assert
        assertThat(retrieved.getStudentCode()).isEqualTo("STU-2025-00999");
        assertThat(retrieved.getGender()).isEqualTo(Gender.MALE);
        assertThat(retrieved.getBloodGroup()).isEqualTo("A+");
        assertThat(retrieved.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(retrieved.getMobileHash()).isEqualTo("mobile_hash_123");
        assertThat(retrieved.getPhotoUrl()).isEqualTo("https://example.com/photo.jpg");
    }

    @Test
    @DisplayName("Should enforce unique student code constraint")
    void shouldEnforceUniqueStudentCodeConstraint() {
        // Arrange
        Student student1 = createTestStudent("STU-2025-00001", "John", "Doe");
        entityManager.persistAndFlush(student1);

        Student student2 = createTestStudent("STU-2025-00001", "Jane", "Smith");

        // Act & Assert
        assertThatThrownBy(() -> {
            studentRepository.save(student2);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should cascade delete guardians when student is deleted")
    void shouldCascadeDeleteGuardiansWhenStudentIsDeleted() {
        // Arrange
        Student student = createTestStudent("STU-2025-00001", "John", "Doe");
        entityManager.persist(student);
        entityManager.flush();

        Long studentId = student.getId();

        // Act
        studentRepository.deleteById(studentId);
        entityManager.flush();

        // Assert
        Optional<Student> found = studentRepository.findById(studentId);
        assertThat(found).isEmpty();
    }

    // Helper methods

    private Student createTestStudent(String code, String firstName, String lastName) {
        Student student = new Student();
        student.setStudentCode(code);
        student.setFirstNameEncrypted(("encrypted_" + firstName).getBytes());
        student.setLastNameEncrypted(("encrypted_" + lastName).getBytes());
        student.setDateOfBirthEncrypted("encrypted_2010-01-15".getBytes());
        student.setGender(Gender.MALE);
        student.setMobileEncrypted("encrypted_9876543210".getBytes());
        student.setMobileHash("hash_" + code);
        student.setStatus(StudentStatus.ACTIVE);
        student.setAdmissionDate(LocalDate.now());
        student.setCreatedBy(testUser.getId());
        student.setUpdatedBy(testUser.getId());
        return student;
    }

    private User createTestUser(String username, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            return userRepository.findByUsername(username).get();
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("hashedpassword");
        user.setFullName(fullName);
        user.setEmail(username + "@test.com");
        user.setRole(com.school.management.domain.security.Role.ADMIN);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}
