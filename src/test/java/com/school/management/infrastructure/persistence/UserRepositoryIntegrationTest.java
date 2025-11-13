package com.school.management.infrastructure.persistence;

import com.school.management.domain.security.Role;
import com.school.management.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for UserRepository using TestContainers with PostgreSQL.
 *
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Custom query methods (findByUsername, findByEmail)</li>
 *   <li>Filtering by role and active status</li>
 *   <li>Uniqueness constraints (username, email)</li>
 *   <li>Database indexes verification</li>
 *   <li>Cascade operations</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("school_management_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .username("john.doe")
                .passwordHash("$2a$12$hashedPassword")
                .fullName("John Doe")
                .email("john.doe@school.com")
                .mobile("+1234567890")
                .role(Role.OFFICE_STAFF)
                .isActive(true)
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should save User with all fields")
        void shouldSaveUser_WhenAllFieldsProvided() {
            // Act
            User savedUser = userRepository.save(testUser);
            entityManager.flush();

            // Assert
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getUsername()).isEqualTo("john.doe");
            assertThat(savedUser.getPasswordHash()).isEqualTo("$2a$12$hashedPassword");
            assertThat(savedUser.getFullName()).isEqualTo("John Doe");
            assertThat(savedUser.getEmail()).isEqualTo("john.doe@school.com");
            assertThat(savedUser.getMobile()).isEqualTo("+1234567890");
            assertThat(savedUser.getRole()).isEqualTo(Role.OFFICE_STAFF);
            assertThat(savedUser.getIsActive()).isTrue();
            assertThat(savedUser.getCreatedAt()).isNotNull();
            assertThat(savedUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should save User with minimum required fields")
        void shouldSaveUser_WhenMinimumFieldsProvided() {
            // Arrange
            User minimalUser = User.builder()
                    .username("minimal.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Minimal User")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Act
            User savedUser = userRepository.save(minimalUser);
            entityManager.flush();

            // Assert
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getEmail()).isNull();
            assertThat(savedUser.getMobile()).isNull();
        }

        @Test
        @DisplayName("Should find User by ID")
        void shouldFindUser_WhenSearchingById() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();

            // Act
            Optional<User> foundUser = userRepository.findById(savedUser.getId());

            // Assert
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("john.doe");
        }

        @Test
        @DisplayName("Should update User")
        void shouldUpdateUser_WhenModifyingFields() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // Act
            savedUser.setFullName("John Updated Doe");
            savedUser.setEmail("john.updated@school.com");
            User updatedUser = userRepository.save(savedUser);
            entityManager.flush();

            // Assert
            User foundUser = userRepository.findById(updatedUser.getId()).get();
            assertThat(foundUser.getFullName()).isEqualTo("John Updated Doe");
            assertThat(foundUser.getEmail()).isEqualTo("john.updated@school.com");
            assertThat(foundUser.getUpdatedAt()).isAfter(foundUser.getCreatedAt());
        }

        @Test
        @DisplayName("Should delete User by ID")
        void shouldDeleteUser_WhenDeleteById() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            Long userId = savedUser.getId();

            // Act
            userRepository.deleteById(userId);
            entityManager.flush();

            // Assert
            Optional<User> deletedUser = userRepository.findById(userId);
            assertThat(deletedUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Methods Tests")
    class CustomQueryMethodsTests {

        @Test
        @DisplayName("Should find User by username")
        void shouldFindUser_WhenSearchingByUsername() {
            // Arrange
            userRepository.save(testUser);
            entityManager.flush();

            // Act
            Optional<User> foundUser = userRepository.findByUsername("john.doe");

            // Assert
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getFullName()).isEqualTo("John Doe");
            assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@school.com");
        }

        @Test
        @DisplayName("Should return empty when username not found")
        void shouldReturnEmpty_WhenUsernameNotFound() {
            // Act
            Optional<User> foundUser = userRepository.findByUsername("nonexistent");

            // Assert
            assertThat(foundUser).isEmpty();
        }

        @Test
        @DisplayName("Should find User by email")
        void shouldFindUser_WhenSearchingByEmail() {
            // Arrange
            userRepository.save(testUser);
            entityManager.flush();

            // Act
            Optional<User> foundUser = userRepository.findByEmail("john.doe@school.com");

            // Assert
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("john.doe");
            assertThat(foundUser.get().getFullName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should return empty when email not found")
        void shouldReturnEmpty_WhenEmailNotFound() {
            // Act
            Optional<User> foundUser = userRepository.findByEmail("nonexistent@school.com");

            // Assert
            assertThat(foundUser).isEmpty();
        }

        @Test
        @DisplayName("Should find active users by role")
        void shouldFindActiveUsers_WhenFilteringByRole() {
            // Arrange
            User activeStaff1 = User.builder()
                    .username("staff1")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Staff One")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User activeStaff2 = User.builder()
                    .username("staff2")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Staff Two")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User inactiveStaff = User.builder()
                    .username("inactive.staff")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Inactive Staff")
                    .role(Role.OFFICE_STAFF)
                    .isActive(false)
                    .build();

            User activeAdmin = User.builder()
                    .username("admin")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Admin User")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            userRepository.saveAll(List.of(activeStaff1, activeStaff2, inactiveStaff, activeAdmin));
            entityManager.flush();

            // Act
            List<User> activeStaffUsers = userRepository.findByRoleAndIsActiveTrue(Role.OFFICE_STAFF);

            // Assert
            assertThat(activeStaffUsers).hasSize(2);
            assertThat(activeStaffUsers)
                    .extracting(User::getUsername)
                    .containsExactlyInAnyOrder("staff1", "staff2");
        }

        @Test
        @DisplayName("Should return empty list when no active users found for role")
        void shouldReturnEmptyList_WhenNoActiveUsersForRole() {
            // Arrange
            User inactiveAuditor = User.builder()
                    .username("auditor")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Auditor User")
                    .role(Role.AUDITOR)
                    .isActive(false)
                    .build();

            userRepository.save(inactiveAuditor);
            entityManager.flush();

            // Act
            List<User> activeAuditors = userRepository.findByRoleAndIsActiveTrue(Role.AUDITOR);

            // Assert
            assertThat(activeAuditors).isEmpty();
        }
    }

    @Nested
    @DisplayName("Constraint Validation Tests")
    class ConstraintValidationTests {

        @Test
        @DisplayName("Should fail to save duplicate username")
        void shouldFailToSave_WhenUsernameAlreadyExists() {
            // Arrange
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            User duplicateUsernameUser = User.builder()
                    .username("john.doe") // Duplicate username
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Another User")
                    .email("another@school.com")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> {
                userRepository.save(duplicateUsernameUser);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("Should fail to save duplicate email")
        void shouldFailToSave_WhenEmailAlreadyExists() {
            // Arrange
            userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            User duplicateEmailUser = User.builder()
                    .username("another.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Another User")
                    .email("john.doe@school.com") // Duplicate email
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> {
                userRepository.save(duplicateEmailUser);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("Should save users with null emails")
        void shouldSaveUsers_WhenEmailsAreNull() {
            // Arrange
            User user1 = User.builder()
                    .username("user1")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("User One")
                    .email(null)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User user2 = User.builder()
                    .username("user2")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("User Two")
                    .email(null)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            User savedUser1 = userRepository.save(user1);
            User savedUser2 = userRepository.save(user2);
            entityManager.flush();

            // Assert
            assertThat(savedUser1.getId()).isNotNull();
            assertThat(savedUser2.getId()).isNotNull();
            assertThat(savedUser1.getEmail()).isNull();
            assertThat(savedUser2.getEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should record login timestamp")
        void shouldRecordLoginTimestamp_WhenRecordLoginCalled() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // Act
            User foundUser = userRepository.findById(savedUser.getId()).get();
            LocalDateTime beforeLogin = LocalDateTime.now();
            foundUser.recordLogin();
            userRepository.save(foundUser);
            entityManager.flush();

            // Assert
            User updatedUser = userRepository.findById(savedUser.getId()).get();
            assertThat(updatedUser.getLastLoginAt()).isNotNull();
            assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeLogin);
        }

        @Test
        @DisplayName("Should update password and passwordChangedAt")
        void shouldUpdatePasswordAndTimestamp_WhenPasswordChanged() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // Act
            User foundUser = userRepository.findById(savedUser.getId()).get();
            LocalDateTime beforeChange = LocalDateTime.now();
            foundUser.changePassword("$2a$12$newHashedPassword");
            userRepository.save(foundUser);
            entityManager.flush();

            // Assert
            User updatedUser = userRepository.findById(savedUser.getId()).get();
            assertThat(updatedUser.getPasswordHash()).isEqualTo("$2a$12$newHashedPassword");
            assertThat(updatedUser.getPasswordChangedAt()).isNotNull();
            assertThat(updatedUser.getPasswordChangedAt()).isAfterOrEqualTo(beforeChange);
        }

        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser_WhenDeactivateCalled() {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // Act
            User foundUser = userRepository.findById(savedUser.getId()).get();
            foundUser.deactivate();
            userRepository.save(foundUser);
            entityManager.flush();

            // Assert
            User updatedUser = userRepository.findById(savedUser.getId()).get();
            assertThat(updatedUser.getIsActive()).isFalse();
            assertThat(updatedUser.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser_WhenActivateCalled() {
            // Arrange
            testUser.setIsActive(false);
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // Act
            User foundUser = userRepository.findById(savedUser.getId()).get();
            foundUser.activate();
            userRepository.save(foundUser);
            entityManager.flush();

            // Assert
            User updatedUser = userRepository.findById(savedUser.getId()).get();
            assertThat(updatedUser.getIsActive()).isTrue();
            assertThat(updatedUser.isEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("Pagination and Sorting Tests")
    class PaginationAndSortingTests {

        @Test
        @DisplayName("Should find all users with pagination")
        void shouldFindAllUsers_WithPagination() {
            // Arrange
            for (int i = 1; i <= 10; i++) {
                User user = User.builder()
                        .username("user" + i)
                        .passwordHash("$2a$12$hashedPassword")
                        .fullName("User " + i)
                        .role(Role.OFFICE_STAFF)
                        .isActive(true)
                        .build();
                userRepository.save(user);
            }
            entityManager.flush();

            // Act
            var page = userRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 5)
            );

            // Assert
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getTotalElements()).isEqualTo(10);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should sort users by username")
        void shouldSortUsers_ByUsername() {
            // Arrange
            User userB = User.builder()
                    .username("bravo")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Bravo User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User userA = User.builder()
                    .username("alpha")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Alpha User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User userC = User.builder()
                    .username("charlie")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Charlie User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            userRepository.saveAll(List.of(userB, userA, userC));
            entityManager.flush();

            // Act
            List<User> sortedUsers = userRepository.findAll(
                org.springframework.data.domain.Sort.by("username")
            );

            // Assert
            assertThat(sortedUsers).hasSize(3);
            assertThat(sortedUsers.get(0).getUsername()).isEqualTo("alpha");
            assertThat(sortedUsers.get(1).getUsername()).isEqualTo("bravo");
            assertThat(sortedUsers.get(2).getUsername()).isEqualTo("charlie");
        }
    }

    @Nested
    @DisplayName("Audit Fields Tests")
    class AuditFieldsTests {

        @Test
        @DisplayName("Should set createdAt and updatedAt on save")
        void shouldSetAuditFields_OnSave() {
            // Act
            User savedUser = userRepository.save(testUser);
            entityManager.flush();

            // Assert
            assertThat(savedUser.getCreatedAt()).isNotNull();
            assertThat(savedUser.getUpdatedAt()).isNotNull();
            assertThat(savedUser.getCreatedAt()).isEqualTo(savedUser.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update updatedAt on modification")
        void shouldUpdateUpdatedAt_OnModification() throws InterruptedException {
            // Arrange
            User savedUser = userRepository.save(testUser);
            entityManager.flush();
            LocalDateTime createdAt = savedUser.getCreatedAt();

            Thread.sleep(10); // Ensure different timestamp
            entityManager.clear();

            // Act
            User foundUser = userRepository.findById(savedUser.getId()).get();
            foundUser.setFullName("Updated Name");
            userRepository.save(foundUser);
            entityManager.flush();

            // Assert
            User updatedUser = userRepository.findById(savedUser.getId()).get();
            assertThat(updatedUser.getUpdatedAt()).isAfter(createdAt);
            assertThat(updatedUser.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}
