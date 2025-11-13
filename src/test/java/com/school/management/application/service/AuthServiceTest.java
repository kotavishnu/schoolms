package com.school.management.application.service;

import com.school.management.domain.security.Role;
import com.school.management.domain.user.User;
import com.school.management.infrastructure.persistence.UserRepository;
import com.school.management.infrastructure.security.JwtTokenProvider;
import com.school.management.shared.exception.AccountLockedException;
import com.school.management.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 *
 * <p>Tests authentication logic including:</p>
 * <ul>
 *   <li>Successful authentication flow</li>
 *   <li>Failed authentication with invalid credentials</li>
 *   <li>Account lockout after 5 failed attempts</li>
 *   <li>Account unlock after 30 minutes</li>
 *   <li>Token refresh logic</li>
 *   <li>Token revocation on logout</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private String username = "john.doe";
    private String password = "Password123!";
    private String hashedPassword = "$2a$12$hashedPassword";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username(username)
                .passwordHash(hashedPassword)
                .fullName("John Doe")
                .email("john.doe@school.com")
                .role(Role.OFFICE_STAFF)
                .isActive(true)
                .build();
        testUser.setId(1L); // Set ID separately as it's inherited from BaseEntity

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Inject field values needed by AuthService
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", 900000L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);
    }

    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    void shouldAuthenticateSuccessfully_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(null);

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                username, password, testUser.getAuthorities());
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class)))
                .thenReturn("access-token-123");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class)))
                .thenReturn("refresh-token-456");

        // Act
        var response = authService.authenticate(username, password);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(900000L); // 15 minutes
        assertThat(response.getUserInfo()).isNotNull();
        assertThat(response.getUserInfo().getUserId()).isEqualTo(1L);
        assertThat(response.getUserInfo().getUsername()).isEqualTo(username);
        assertThat(response.getUserInfo().getRole()).isEqualTo("OFFICE_STAFF");

        // Verify user.lastLoginAt was updated
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLastLoginAt()).isNotNull();
        assertThat(userCaptor.getValue().getLastLoginAt()).isBefore(LocalDateTime.now().plusSeconds(1));

        // Verify refresh token stored in Redis
        verify(valueOperations).set(
                eq("user:refreshtoken:" + testUser.getId()),
                eq("refresh-token-456"),
                eq(604800000L),
                eq(TimeUnit.MILLISECONDS)
        );

        // Verify login attempts cleared
        verify(redisTemplate).delete("user:loginattempts:" + testUser.getId());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when username not found")
    void shouldThrowUnauthorizedException_WhenUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when password is incorrect")
    void shouldThrowUnauthorizedException_WhenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");

        // Verify failed attempt was recorded
        verify(valueOperations).increment("user:loginattempts:" + testUser.getId());
        verify(redisTemplate).expire(
                eq("user:loginattempts:" + testUser.getId()),
                eq(1800000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is inactive")
    void shouldThrowUnauthorizedException_WhenUserIsInactive() {
        // Arrange
        testUser.deactivate();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Account is inactive");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should lock account after 5 failed login attempts")
    void shouldLockAccount_AfterFiveFailedAttempts() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(4);
        // Mock increment to return 5 (the 5th failed attempt)
        when(valueOperations.increment("user:loginattempts:" + testUser.getId())).thenReturn(5L);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(AccountLockedException.class)
                .hasMessageContaining("Account locked due to too many failed login attempts");

        // Verify lockout stored in Redis with 30-minute expiry
        verify(valueOperations).set(
                eq("user:locked:" + testUser.getId()),
                eq(true),
                eq(1800000L), // 30 minutes
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("Should throw AccountLockedException when account is locked")
    void shouldThrowAccountLockedException_WhenAccountIsLocked() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(AccountLockedException.class)
                .hasMessageContaining("Account is temporarily locked");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should allow login after lockout period expires")
    void shouldAllowLogin_AfterLockoutExpires() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(null);

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                username, password, testUser.getAuthorities());
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class)))
                .thenReturn("access-token-123");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class)))
                .thenReturn("refresh-token-456");

        // Act
        var response = authService.authenticate(username, password);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
    }

    @Test
    @DisplayName("Should refresh token with valid refresh token")
    void shouldRefreshToken_WhenRefreshTokenIsValid() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(valueOperations.get("user:refreshtoken:" + testUser.getId())).thenReturn(refreshToken);

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                username, null, testUser.getAuthorities());
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class)))
                .thenReturn("new-access-token");

        // Act
        var response = authService.refreshToken(refreshToken);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getExpiresIn()).isEqualTo(900000L);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is invalid")
    void shouldThrowUnauthorizedException_WhenRefreshTokenIsInvalid() {
        // Arrange
        String refreshToken = "invalid-refresh-token";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token not found in Redis")
    void shouldThrowUnauthorizedException_WhenRefreshTokenNotInRedis() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(valueOperations.get("user:refreshtoken:" + testUser.getId())).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Refresh token not found or expired");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token does not match stored token")
    void shouldThrowUnauthorizedException_WhenRefreshTokenDoesNotMatch() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        String storedToken = "different-refresh-token";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(valueOperations.get("user:refreshtoken:" + testUser.getId())).thenReturn(storedToken);

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should logout and revoke tokens successfully")
    void shouldLogout_AndRevokeTokens() {
        // Arrange
        String accessToken = "valid-access-token";
        Long userId = 1L;
        when(jwtTokenProvider.validateToken(accessToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(accessToken)).thenReturn(userId);
        when(jwtTokenProvider.getExpirationDateFromToken(accessToken))
                .thenReturn(new java.util.Date(System.currentTimeMillis() + 900000));

        // Act
        authService.logout(accessToken, userId);

        // Assert
        // Verify access token blacklisted
        verify(valueOperations).set(
                eq("blacklist:token:" + accessToken),
                eq(true),
                anyLong(),
                eq(TimeUnit.MILLISECONDS)
        );

        // Verify refresh token deleted
        verify(redisTemplate).delete("user:refreshtoken:" + userId);
    }

    @Test
    @DisplayName("Should handle logout with invalid token gracefully")
    void shouldHandleLogoutGracefully_WhenTokenIsInvalid() {
        // Arrange
        String accessToken = "invalid-token";
        Long userId = 1L;
        when(jwtTokenProvider.validateToken(accessToken)).thenReturn(false);

        // Act
        authService.logout(accessToken, userId);

        // Assert
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
        verify(redisTemplate).delete("user:refreshtoken:" + userId);
    }

    @Test
    @DisplayName("Should increment login attempts on failed authentication")
    void shouldIncrementLoginAttempts_OnFailedAuthentication() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(2);

        // Act
        try {
            authService.authenticate(username, password);
        } catch (UnauthorizedException e) {
            // Expected
        }

        // Assert
        verify(valueOperations).increment("user:loginattempts:" + testUser.getId());
        verify(redisTemplate).expire(
                eq("user:loginattempts:" + testUser.getId()),
                eq(1800000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("Should extract permissions from role in user info")
    void shouldExtractPermissions_FromRoleInUserInfo() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(valueOperations.get("user:locked:" + testUser.getId())).thenReturn(null);
        when(valueOperations.get("user:loginattempts:" + testUser.getId())).thenReturn(null);

        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                username, password, testUser.getAuthorities());
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class)))
                .thenReturn("access-token-123");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class)))
                .thenReturn("refresh-token-456");

        // Act
        var response = authService.authenticate(username, password);

        // Assert
        assertThat(response.getUserInfo().getPermissions()).isNotEmpty();
        assertThat(response.getUserInfo().getPermissions())
                .containsAll(Role.OFFICE_STAFF.getPermissions());
    }
}
