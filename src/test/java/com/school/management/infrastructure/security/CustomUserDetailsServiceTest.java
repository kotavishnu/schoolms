package com.school.management.infrastructure.security;

import com.school.management.domain.security.Role;
import com.school.management.domain.user.User;
import com.school.management.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 *
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Loading user by username</li>
 *   <li>Loading user by ID</li>
 *   <li>Loading user by email</li>
 *   <li>Exception handling when user not found</li>
 *   <li>Proper UserDetails creation</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("john.doe")
                .passwordHash("$2a$12$hashedPassword")
                .fullName("John Doe")
                .email("john.doe@school.com")
                .mobile("+1234567890")
                .role(Role.OFFICE_STAFF)
                .isActive(true)
                .build();
        testUser.setId(1L);
    }

    @Nested
    @DisplayName("Load User By Username Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should load user by username successfully")
        void shouldLoadUser_WhenUsernameExists() {
            // Arrange
            when(userRepository.findByUsername("john.doe"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("john.doe");
            assertThat(userDetails.getPassword()).isEqualTo("$2a$12$hashedPassword");
            assertThat(userDetails.isEnabled()).isTrue();
            assertThat(userDetails.isAccountNonExpired()).isTrue();
            assertThat(userDetails.isAccountNonLocked()).isTrue();
            assertThat(userDetails.isCredentialsNonExpired()).isTrue();

            verify(userRepository, times(1)).findByUsername("john.doe");
        }

        @Test
        @DisplayName("Should return authorities based on user role")
        void shouldReturnAuthorities_BasedOnUserRole() {
            // Arrange
            when(userRepository.findByUsername("john.doe"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

            // Assert
            assertThat(userDetails.getAuthorities()).isNotEmpty();
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_OFFICE_STAFF");
        }

        @Test
        @DisplayName("Should throw exception when username not found")
        void shouldThrowException_WhenUsernameNotFound() {
            // Arrange
            when(userRepository.findByUsername(anyString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with username: nonexistent");

            verify(userRepository, times(1)).findByUsername("nonexistent");
        }

        @Test
        @DisplayName("Should load inactive user but with enabled=false")
        void shouldLoadInactiveUser_WithEnabledFalse() {
            // Arrange
            testUser.setIsActive(false);
            when(userRepository.findByUsername("john.doe"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

            // Assert
            assertThat(userDetails.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should handle different roles correctly")
        void shouldHandleDifferentRoles_Correctly() {
            // Arrange
            User adminUser = User.builder()
                    .username("admin")
                    .passwordHash("$2a$12$hashedPassword")
                    .fullName("Admin User")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            when(userRepository.findByUsername("admin"))
                    .thenReturn(Optional.of(adminUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

            // Assert
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("Load User By ID Tests")
    class LoadUserByIdTests {

        @Test
        @DisplayName("Should load user by ID successfully")
        void shouldLoadUser_WhenIdExists() {
            // Arrange
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserById(1L);

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("john.doe");
            assertThat(userDetails.getPassword()).isEqualTo("$2a$12$hashedPassword");

            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when ID not found")
        void shouldThrowException_WhenIdNotFound() {
            // Arrange
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userDetailsService.loadUserById(999L))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with ID: 999");

            verify(userRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should load user by ID with correct authorities")
        void shouldLoadUserById_WithCorrectAuthorities() {
            // Arrange
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserById(1L);

            // Assert
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_OFFICE_STAFF");
        }
    }

    @Nested
    @DisplayName("Load User By Email Tests")
    class LoadUserByEmailTests {

        @Test
        @DisplayName("Should load user by email successfully")
        void shouldLoadUser_WhenEmailExists() {
            // Arrange
            when(userRepository.findByEmail("john.doe@school.com"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByEmail("john.doe@school.com");

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("john.doe");
            assertThat(userDetails.getPassword()).isEqualTo("$2a$12$hashedPassword");

            verify(userRepository, times(1)).findByEmail("john.doe@school.com");
        }

        @Test
        @DisplayName("Should throw exception when email not found")
        void shouldThrowException_WhenEmailNotFound() {
            // Arrange
            when(userRepository.findByEmail(anyString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userDetailsService.loadUserByEmail("nonexistent@school.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with email: nonexistent@school.com");

            verify(userRepository, times(1)).findByEmail("nonexistent@school.com");
        }

        @Test
        @DisplayName("Should load user by email case-insensitively")
        void shouldLoadUser_ByEmailCaseInsensitively() {
            // Arrange
            when(userRepository.findByEmail("JOHN.DOE@SCHOOL.COM"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByEmail("JOHN.DOE@SCHOOL.COM");

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("john.doe");
        }
    }

    @Nested
    @DisplayName("User Entity As UserDetails Tests")
    class UserEntityAsUserDetailsTests {

        @Test
        @DisplayName("Should return User entity that implements UserDetails")
        void shouldReturnUserEntity_ThatImplementsUserDetails() {
            // Arrange
            when(userRepository.findByUsername("john.doe"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

            // Assert
            assertThat(userDetails).isInstanceOf(User.class);
            assertThat((User) userDetails).isEqualTo(testUser);
        }

        @Test
        @DisplayName("Should have all UserDetails methods working")
        void shouldHaveAllUserDetailsMethods_Working() {
            // Arrange
            when(userRepository.findByUsername("john.doe"))
                    .thenReturn(Optional.of(testUser));

            // Act
            UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

            // Assert
            assertThat(userDetails.getUsername()).isNotNull();
            assertThat(userDetails.getPassword()).isNotNull();
            assertThat(userDetails.getAuthorities()).isNotEmpty();
            assertThat(userDetails.isAccountNonExpired()).isTrue();
            assertThat(userDetails.isAccountNonLocked()).isTrue();
            assertThat(userDetails.isCredentialsNonExpired()).isTrue();
            assertThat(userDetails.isEnabled()).isTrue();
        }
    }
}
