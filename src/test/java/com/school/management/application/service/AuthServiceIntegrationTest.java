package com.school.management.application.service;

import com.school.management.config.TestContainersConfiguration;
import com.school.management.domain.security.Role;
import com.school.management.domain.user.User;
import com.school.management.infrastructure.persistence.UserRepository;
import com.school.management.shared.exception.AccountLockedException;
import com.school.management.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for AuthService with real database and Redis.
 *
 * <p>Tests the complete authentication flow including:</p>
 * <ul>
 *   <li>User authentication with database</li>
 *   <li>Token generation and validation</li>
 *   <li>Redis-based login attempt tracking</li>
 *   <li>Account lockout mechanism</li>
 *   <li>Token refresh flow</li>
 *   <li>Logout with token revocation</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@SpringBootTest
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthService Integration Tests")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private User testUser;
    private String plainPassword = "Password123!";

    @BeforeEach
    void setUp() {
        // Clean up Redis
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // Create test user
        testUser = User.builder()
                .username("test.user")
                .passwordHash(passwordEncoder.encode(plainPassword))
                .fullName("Test User")
                .email("test.user@school.com")
                .mobile("+1234567890")
                .role(Role.OFFICE_STAFF)
                .isActive(true)
                .build();

        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should authenticate successfully with valid credentials and store tokens in Redis")
    void shouldAuthenticateSuccessfully_WithValidCredentials() {
        // Act
        var response = authService.authenticate(testUser.getUsername(), plainPassword);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(900000L);
        assertThat(response.getUserInfo()).isNotNull();
        assertThat(response.getUserInfo().getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getUserInfo().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getUserInfo().getRole()).isEqualTo("OFFICE_STAFF");
        assertThat(response.getUserInfo().getPermissions()).isNotEmpty();

        // Verify refresh token stored in Redis
        String storedToken = (String) redisTemplate.opsForValue()
                .get("user:refreshtoken:" + testUser.getId());
        assertThat(storedToken).isEqualTo(response.getRefreshToken());

        // Verify login attempts cleared
        Object attempts = redisTemplate.opsForValue()
                .get("user:loginattempts:" + testUser.getId());
        assertThat(attempts).isNull();
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when password is wrong")
    void shouldThrowUnauthorizedException_WhenPasswordIsWrong() {
        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(testUser.getUsername(), "WrongPassword"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");

        // Verify login attempt recorded in Redis
        Object attempts = redisTemplate.opsForValue()
                .get("user:loginattempts:" + testUser.getId());
        assertThat(attempts).isNotNull();
    }

    @Test
    @DisplayName("Should lock account after 5 failed login attempts")
    void shouldLockAccount_AfterFiveFailedAttempts() {
        // Attempt login with wrong password 5 times
        for (int i = 0; i < 4; i++) {
            try {
                authService.authenticate(testUser.getUsername(), "WrongPassword");
            } catch (UnauthorizedException e) {
                // Expected
            }
        }

        // Fifth attempt should lock the account
        assertThatThrownBy(() -> authService.authenticate(testUser.getUsername(), "WrongPassword"))
                .isInstanceOf(AccountLockedException.class)
                .hasMessageContaining("Account locked");

        // Verify account locked in Redis
        Boolean isLocked = (Boolean) redisTemplate.opsForValue()
                .get("user:locked:" + testUser.getId());
        assertThat(isLocked).isTrue();
    }

    @Test
    @DisplayName("Should prevent login when account is locked even with correct password")
    void shouldPreventLogin_WhenAccountIsLocked() {
        // Lock the account
        redisTemplate.opsForValue().set("user:locked:" + testUser.getId(), true);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(testUser.getUsername(), plainPassword))
                .isInstanceOf(AccountLockedException.class)
                .hasMessageContaining("temporarily locked");
    }

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void shouldRefreshToken_WithValidRefreshToken() {
        // Arrange - First authenticate to get refresh token
        var loginResponse = authService.authenticate(testUser.getUsername(), plainPassword);
        String refreshToken = loginResponse.getRefreshToken();

        // Act
        var tokenResponse = authService.refreshToken(refreshToken);

        // Assert
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isNotBlank();
        assertThat(tokenResponse.getAccessToken()).isNotEqualTo(loginResponse.getAccessToken());
        assertThat(tokenResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(tokenResponse.getExpiresIn()).isEqualTo(900000L);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is not in Redis")
    void shouldThrowUnauthorizedException_WhenRefreshTokenNotInRedis() {
        // Arrange - First authenticate
        var loginResponse = authService.authenticate(testUser.getUsername(), plainPassword);
        String refreshToken = loginResponse.getRefreshToken();

        // Delete refresh token from Redis
        redisTemplate.delete("user:refreshtoken:" + testUser.getId());

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not found or expired");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token does not match stored token")
    void shouldThrowUnauthorizedException_WhenRefreshTokenDoesNotMatch() {
        // Arrange - First authenticate
        var loginResponse = authService.authenticate(testUser.getUsername(), plainPassword);

        // Store different token in Redis
        redisTemplate.opsForValue().set(
                "user:refreshtoken:" + testUser.getId(),
                "different-refresh-token"
        );

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(loginResponse.getRefreshToken()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should logout successfully and revoke tokens")
    void shouldLogoutSuccessfully_AndRevokeTokens() {
        // Arrange - First authenticate
        var loginResponse = authService.authenticate(testUser.getUsername(), plainPassword);
        String accessToken = loginResponse.getAccessToken();

        // Verify refresh token exists in Redis before logout
        String storedToken = (String) redisTemplate.opsForValue()
                .get("user:refreshtoken:" + testUser.getId());
        assertThat(storedToken).isNotNull();

        // Act
        authService.logout(accessToken, testUser.getId());

        // Assert - Refresh token should be deleted
        String deletedToken = (String) redisTemplate.opsForValue()
                .get("user:refreshtoken:" + testUser.getId());
        assertThat(deletedToken).isNull();

        // Assert - Access token should be blacklisted
        Boolean isBlacklisted = (Boolean) redisTemplate.opsForValue()
                .get("blacklist:token:" + accessToken);
        assertThat(isBlacklisted).isTrue();
    }

    @Test
    @DisplayName("Should update user lastLoginAt timestamp on successful login")
    void shouldUpdateLastLoginAt_OnSuccessfulLogin() {
        // Arrange
        testUser.recordLogin();
        var initialLoginTime = testUser.getLastLoginAt();
        userRepository.save(testUser);

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        authService.authenticate(testUser.getUsername(), plainPassword);

        // Assert
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfter(initialLoginTime);
    }

    @Test
    @DisplayName("Should clear login attempts on successful authentication")
    void shouldClearLoginAttempts_OnSuccessfulAuthentication() {
        // Arrange - Create some failed attempts
        redisTemplate.opsForValue().set("user:loginattempts:" + testUser.getId(), 3);

        // Act
        authService.authenticate(testUser.getUsername(), plainPassword);

        // Assert - Login attempts should be cleared
        Object attempts = redisTemplate.opsForValue()
                .get("user:loginattempts:" + testUser.getId());
        assertThat(attempts).isNull();
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is inactive")
    void shouldThrowUnauthorizedException_WhenUserIsInactive() {
        // Arrange
        testUser.deactivate();
        userRepository.save(testUser);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticate(testUser.getUsername(), plainPassword))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("Should include all role permissions in user info")
    void shouldIncludeAllRolePermissions_InUserInfo() {
        // Act
        var response = authService.authenticate(testUser.getUsername(), plainPassword);

        // Assert
        assertThat(response.getUserInfo().getPermissions())
                .containsAll(Role.OFFICE_STAFF.getPermissions());
    }

    @Test
    @DisplayName("Should handle concurrent login attempts correctly")
    void shouldHandleConcurrentLoginAttempts_Correctly() throws InterruptedException {
        // This test verifies Redis atomic operations work correctly
        // Arrange
        final int failedAttemptCount = 3;

        // Simulate concurrent failed login attempts
        Thread[] threads = new Thread[failedAttemptCount];
        for (int i = 0; i < failedAttemptCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    authService.authenticate(testUser.getUsername(), "WrongPassword");
                } catch (UnauthorizedException e) {
                    // Expected
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - All attempts should be recorded
        Object attempts = redisTemplate.opsForValue()
                .get("user:loginattempts:" + testUser.getId());
        assertThat(attempts).isNotNull();
        assertThat(((Integer) attempts)).isGreaterThanOrEqualTo(failedAttemptCount);
    }
}
