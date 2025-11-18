package com.school.sms.student.infrastructure;

import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.valueobject.Address;
import com.school.sms.student.domain.valueobject.StudentStatus;
import com.school.sms.student.infrastructure.persistence.repository.JpaStudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Student JPA Repository.
 * Uses TestContainers to spin up a real PostgreSQL database for testing.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("test_student_db")
        .withUsername("test_user")
        .withPassword("test_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private JpaStudentRepository repository;

    @Test
    @DisplayName("Should save and retrieve student successfully")
    void shouldSaveAndRetrieveStudent() {
        // Given
        Student student = createTestStudent("STU-2025-00001", "9876543210");

        // When
        Student saved = repository.save(student);
        Optional<Student> retrieved = repository.findById(saved.getStudentId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStudentId()).isEqualTo("STU-2025-00001");
        assertThat(retrieved.get().getMobile()).isEqualTo("9876543210");
        assertThat(retrieved.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Should detect duplicate mobile numbers")
    void shouldDetectDuplicateMobile() {
        // Given
        Student student = createTestStudent("STU-2025-00001", "9876543210");
        repository.save(student);

        // When
        boolean exists = repository.existsByMobile("9876543210");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should not detect non-existent mobile numbers")
    void shouldNotDetectNonExistentMobile() {
        // When
        boolean exists = repository.existsByMobile("9999999999");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should update student with optimistic locking")
    void shouldUpdateWithOptimisticLocking() {
        // Given
        Student student = createTestStudent("STU-2025-00001", "9876543210");
        Student saved = repository.save(student);
        assertThat(saved.getVersion()).isEqualTo(0L);

        // When
        saved.setFirstName("Updated");
        Student updated = repository.save(saved);

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find students by status")
    void shouldFindStudentsByStatus() {
        // Given
        Student activeStudent = createTestStudent("STU-2025-00001", "9876543210");
        Student inactiveStudent = createTestStudent("STU-2025-00002", "9876543211");
        inactiveStudent.setStatus(StudentStatus.INACTIVE);

        repository.save(activeStudent);
        repository.save(inactiveStudent);

        // When
        long activeCount = repository.findByStatus(StudentStatus.ACTIVE, org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements();
        long inactiveCount = repository.findByStatus(StudentStatus.INACTIVE, org.springframework.data.domain.Pageable.unpaged())
            .getTotalElements();

        // Then
        assertThat(activeCount).isEqualTo(1);
        assertThat(inactiveCount).isEqualTo(1);
    }

    private Student createTestStudent(String id, String mobile) {
        return Student.builder()
            .studentId(id)
            .firstName("Test")
            .lastName("Student")
            .dateOfBirth(LocalDate.now().minusYears(10))
            .address(Address.builder()
                .street("123 Test St")
                .city("Test City")
                .state("Test State")
                .pinCode("123456")
                .country("India")
                .build())
            .mobile(mobile)
            .fatherNameOrGuardian("Test Father")
            .status(StudentStatus.ACTIVE)
            .createdBy("TEST")
            .updatedBy("TEST")
            .build();
    }
}
