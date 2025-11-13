package com.school.management.domain.user;

import com.school.management.domain.security.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity.
 *
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Entity creation and field validation</li>
 *   <li>Email format validation</li>
 *   <li>Role constraints</li>
 *   <li>UserDetails interface implementation</li>
 *   <li>Account status methods</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Entity Creation Tests")
    class EntityCreationTests {

        @Test
        @DisplayName("Should create User with all valid fields")
        void shouldCreateUser_WhenAllFieldsValid() {
            // Arrange & Act
            User user = User.builder()
                    .username("john.doe")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("John Doe")
                    .email("john.doe@school.com")
                    .mobile("+1234567890")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user).isNotNull();
            assertThat(user.getUsername()).isEqualTo("john.doe");
            assertThat(user.getPasswordHash()).isEqualTo("$2a$12$hashedPassword");
            assertThat(user.getFullName()).isEqualTo("John Doe");
            assertThat(user.getEmail()).isEqualTo("john.doe@school.com");
            assertThat(user.getMobile()).isEqualTo("+1234567890");
            assertThat(user.getRole()).isEqualTo(Role.OFFICE_STAFF);
            assertThat(user.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("Should create User with minimum required fields")
        void shouldCreateUser_WhenMinimumFieldsProvided() {
            // Arrange & Act
            User user = User.builder()
                    .username("admin")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Administrator")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user).isNotNull();
            assertThat(user.getEmail()).isNull();
            assertThat(user.getMobile()).isNull();
        }

        @Test
        @DisplayName("Should default isActive to true when not specified")
        void shouldDefaultIsActiveToTrue_WhenNotSpecified() {
            // Arrange & Act
            User user = new User();
            user.setUsername("test.user");
            user.setPasswordHash("$2a$12$hashedPassword");
            user.setFullName("Test User");
            user.setRole(Role.OFFICE_STAFF);

            // Assert
            assertThat(user.getIsActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should fail validation when username is null")
        void shouldFailValidation_WhenUsernameIsNull() {
            // Arrange
            User user = User.builder()
                    .username(null)
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("username") &&
                v.getMessage().contains("must not be blank")
            );
        }

        @Test
        @DisplayName("Should fail validation when username is blank")
        void shouldFailValidation_WhenUsernameIsBlank() {
            // Arrange
            User user = User.builder()
                    .username("   ")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("username")
            );
        }

        @Test
        @DisplayName("Should fail validation when username exceeds 50 characters")
        void shouldFailValidation_WhenUsernameExceedsMaxLength() {
            // Arrange
            String longUsername = "a".repeat(51);
            User user = User.builder()
                    .username(longUsername)
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("username") &&
                v.getMessage().contains("size must be between")
            );
        }

        @Test
        @DisplayName("Should fail validation when passwordHash is null")
        void shouldFailValidation_WhenPasswordHashIsNull() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash(null)
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("passwordHash")
            );
        }

        @Test
        @DisplayName("Should fail validation when fullName is null")
        void shouldFailValidation_WhenFullNameIsNull() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName(null)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("fullName")
            );
        }

        @Test
        @DisplayName("Should fail validation when fullName exceeds 100 characters")
        void shouldFailValidation_WhenFullNameExceedsMaxLength() {
            // Arrange
            String longName = "a".repeat(101);
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName(longName)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("fullName")
            );
        }

        @Test
        @DisplayName("Should fail validation when email format is invalid")
        void shouldFailValidation_WhenEmailFormatInvalid() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .email("invalid-email")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                v.getMessage().contains("must be a well-formed email address")
            );
        }

        @Test
        @DisplayName("Should pass validation when email is null")
        void shouldPassValidation_WhenEmailIsNull() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .email(null)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation when email is valid")
        void shouldPassValidation_WhenEmailIsValid() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .email("test.user@school.com")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when role is null")
        void shouldFailValidation_WhenRoleIsNull() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(null)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("role")
            );
        }

        @Test
        @DisplayName("Should fail validation when mobile exceeds 20 characters")
        void shouldFailValidation_WhenMobileExceedsMaxLength() {
            // Arrange
            String longMobile = "1".repeat(21);
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .mobile(longMobile)
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("mobile")
            );
        }
    }

    @Nested
    @DisplayName("UserDetails Interface Tests")
    class UserDetailsInterfaceTests {

        @Test
        @DisplayName("Should return username from getUsername()")
        void shouldReturnUsername_WhenGetUsernameCalled() {
            // Arrange
            User user = User.builder()
                    .username("john.doe")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("John Doe")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            String username = user.getUsername();

            // Assert
            assertThat(username).isEqualTo("john.doe");
        }

        @Test
        @DisplayName("Should return passwordHash from getPassword()")
        void shouldReturnPasswordHash_WhenGetPasswordCalled() {
            // Arrange
            User user = User.builder()
                    .username("john.doe")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("John Doe")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            String password = user.getPassword();

            // Assert
            assertThat(password).isEqualTo("$2a$12$hashedPassword");
        }

        @Test
        @DisplayName("Should return authorities based on role")
        void shouldReturnAuthorities_BasedOnRole() {
            // Arrange
            User user = User.builder()
                    .username("admin")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Administrator")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Act
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

            // Assert
            assertThat(authorities).isNotEmpty();
            assertThat(authorities).hasSize(1);
            assertThat(authorities).first().isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        @Test
        @DisplayName("Should return true for isAccountNonExpired()")
        void shouldReturnTrue_WhenIsAccountNonExpiredCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act & Assert
            assertThat(user.isAccountNonExpired()).isTrue();
        }

        @Test
        @DisplayName("Should return true for isAccountNonLocked()")
        void shouldReturnTrue_WhenIsAccountNonLockedCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act & Assert
            assertThat(user.isAccountNonLocked()).isTrue();
        }

        @Test
        @DisplayName("Should return true for isCredentialsNonExpired()")
        void shouldReturnTrue_WhenIsCredentialsNonExpiredCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act & Assert
            assertThat(user.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("Should return isActive value for isEnabled()")
        void shouldReturnIsActiveValue_WhenIsEnabledCalled() {
            // Arrange
            User activeUser = User.builder()
                    .username("active.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Active User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            User inactiveUser = User.builder()
                    .username("inactive.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Inactive User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(false)
                    .build();

            // Act & Assert
            assertThat(activeUser.isEnabled()).isTrue();
            assertThat(inactiveUser.isEnabled()).isFalse();
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should update lastLoginAt when recordLogin is called")
        void shouldUpdateLastLoginAt_WhenRecordLoginCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();
            LocalDateTime beforeLogin = LocalDateTime.now();

            // Act
            user.recordLogin();

            // Assert
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getLastLoginAt()).isAfterOrEqualTo(beforeLogin);
        }

        @Test
        @DisplayName("Should update passwordChangedAt when changePassword is called")
        void shouldUpdatePasswordChangedAt_WhenChangePasswordCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$oldPasswordHash")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();
            LocalDateTime beforeChange = LocalDateTime.now();

            // Act
            user.changePassword("$2a$12$newPasswordHash");

            // Assert
            assertThat(user.getPasswordHash()).isEqualTo("$2a$12$newPasswordHash");
            assertThat(user.getPasswordChangedAt()).isNotNull();
            assertThat(user.getPasswordChangedAt()).isAfterOrEqualTo(beforeChange);
        }

        @Test
        @DisplayName("Should deactivate user when deactivate is called")
        void shouldDeactivateUser_WhenDeactivateCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Act
            user.deactivate();

            // Assert
            assertThat(user.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Should activate user when activate is called")
        void shouldActivateUser_WhenActivateCalled() {
            // Arrange
            User user = User.builder()
                    .username("test.user")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Test User")
                    .role(Role.OFFICE_STAFF)
                    .isActive(false)
                    .build();

            // Act
            user.activate();

            // Assert
            assertThat(user.getIsActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Role Tests")
    class RoleTests {

        @Test
        @DisplayName("Should create user with ADMIN role")
        void shouldCreateUser_WithAdminRole() {
            // Arrange & Act
            User user = User.builder()
                    .username("admin")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Administrator")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Should create user with PRINCIPAL role")
        void shouldCreateUser_WithPrincipalRole() {
            // Arrange & Act
            User user = User.builder()
                    .username("principal")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("School Principal")
                    .role(Role.PRINCIPAL)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user.getRole()).isEqualTo(Role.PRINCIPAL);
        }

        @Test
        @DisplayName("Should create user with OFFICE_STAFF role")
        void shouldCreateUser_WithOfficeStaffRole() {
            // Arrange & Act
            User user = User.builder()
                    .username("staff")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Office Staff")
                    .role(Role.OFFICE_STAFF)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user.getRole()).isEqualTo(Role.OFFICE_STAFF);
        }

        @Test
        @DisplayName("Should create user with ACCOUNTS_MANAGER role")
        void shouldCreateUser_WithAccountsManagerRole() {
            // Arrange & Act
            User user = User.builder()
                    .username("accounts")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Accounts Manager")
                    .role(Role.ACCOUNTS_MANAGER)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user.getRole()).isEqualTo(Role.ACCOUNTS_MANAGER);
        }

        @Test
        @DisplayName("Should create user with AUDITOR role")
        void shouldCreateUser_WithAuditorRole() {
            // Arrange & Act
            User user = User.builder()
                    .username("auditor")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Auditor")
                    .role(Role.AUDITOR)
                    .isActive(true)
                    .build();

            // Assert
            assertThat(user.getRole()).isEqualTo(Role.AUDITOR);
        }
    }
}
