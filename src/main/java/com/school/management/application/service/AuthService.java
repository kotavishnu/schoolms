package com.school.management.application.service;

import com.school.management.application.dto.auth.LoginResponse;
import com.school.management.application.dto.auth.TokenResponse;
import com.school.management.application.dto.auth.UserInfo;
import com.school.management.domain.user.User;
import com.school.management.infrastructure.persistence.UserRepository;
import com.school.management.infrastructure.security.JwtTokenProvider;
import com.school.management.shared.exception.AccountLockedException;
import com.school.management.shared.exception.NotFoundException;
import com.school.management.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service handling user login, logout, and token management.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>User authentication with username/password</li>
 *   <li>JWT access token generation (15-minute expiry)</li>
 *   <li>JWT refresh token generation (7-day expiry)</li>
 *   <li>Failed login attempt tracking in Redis</li>
 *   <li>Account lockout after 5 failed attempts (30-minute duration)</li>
 *   <li>Token refresh with validation</li>
 *   <li>Token revocation on logout</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 1800000L; // 30 minutes
    private static final String LOGIN_ATTEMPTS_KEY_PREFIX = "user:loginattempts:";
    private static final String LOCKED_KEY_PREFIX = "user:locked:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "user:refreshtoken:";
    private static final String BLACKLIST_TOKEN_KEY_PREFIX = "blacklist:token:";

    /**
     * Authenticates a user with username and password.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Check if account is locked</li>
     *   <li>Validate username exists</li>
     *   <li>Validate user is active</li>
     *   <li>Verify password</li>
     *   <li>Generate access and refresh tokens</li>
     *   <li>Store refresh token in Redis</li>
     *   <li>Update user's lastLoginAt</li>
     *   <li>Clear login attempts</li>
     * </ol>
     *
     * @param username the username
     * @param password the password
     * @return login response with tokens and user info
     * @throws UnauthorizedException if credentials are invalid
     * @throws AccountLockedException if account is locked due to failed attempts
     */
    @Transactional
    public LoginResponse authenticate(String username, String password) {
        log.debug("Authentication attempt for username: {}", username);

        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Check if account is locked
        if (isAccountLocked(user.getId())) {
            log.warn("Authentication failed - account locked for user: {}", username);
            throw new AccountLockedException("Account is temporarily locked due to multiple failed login attempts. Please try again later.");
        }

        // Check if user is active
        if (!user.isEnabled()) {
            log.warn("Authentication failed - account inactive for user: {}", username);
            throw new UnauthorizedException("Account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Authentication failed - invalid password for user: {}", username);
            handleFailedLogin(user.getId());
            throw new UnauthorizedException("Invalid username or password");
        }

        // Clear login attempts
        clearLoginAttempts(user.getId());

        // Generate tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                password,
                user.getAuthorities()
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Store refresh token in Redis
        storeRefreshToken(user.getId(), refreshToken);

        // Update last login timestamp
        user.recordLogin();
        userRepository.save(user);

        log.info("User authenticated successfully: {}", username);

        // Build response
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .userInfo(buildUserInfo(user))
                .build();
    }

    /**
     * Refreshes access token using a valid refresh token.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Validate refresh token format</li>
     *   <li>Extract username from token</li>
     *   <li>Verify token matches stored token in Redis</li>
     *   <li>Generate new access token</li>
     * </ol>
     *
     * @param refreshToken the refresh token
     * @return token response with new access token
     * @throws UnauthorizedException if refresh token is invalid
     */
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String refreshToken) {
        log.debug("Token refresh attempt");

        // Validate token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Extract user information
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Verify refresh token matches stored token
        String storedToken = (String) redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_KEY_PREFIX + userId);

        if (storedToken == null) {
            log.warn("Refresh token not found in Redis for user: {}", username);
            throw new UnauthorizedException("Refresh token not found or expired");
        }

        if (!refreshToken.equals(storedToken)) {
            log.warn("Refresh token mismatch for user: {}", username);
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Generate new access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                user.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        log.info("Token refreshed successfully for user: {}", username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .build();
    }

    /**
     * Logs out user by revoking tokens.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Blacklist access token (until expiration)</li>
     *   <li>Delete refresh token from Redis</li>
     * </ol>
     *
     * @param accessToken the access token to revoke
     * @param userId the user ID
     */
    public void logout(String accessToken, Long userId) {
        log.debug("Logout attempt for user ID: {}", userId);

        // Blacklist access token
        if (jwtTokenProvider.validateToken(accessToken)) {
            Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(accessToken);
            long timeUntilExpiration = expirationDate.getTime() - System.currentTimeMillis();

            if (timeUntilExpiration > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_TOKEN_KEY_PREFIX + accessToken,
                        true,
                        timeUntilExpiration,
                        TimeUnit.MILLISECONDS
                );
                log.debug("Access token blacklisted for user ID: {}", userId);
            }
        }

        // Delete refresh token
        redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + userId);

        log.info("User logged out successfully. User ID: {}", userId);
    }

    /**
     * Checks if account is locked due to failed login attempts.
     *
     * @param userId the user ID
     * @return true if account is locked, false otherwise
     */
    private boolean isAccountLocked(Long userId) {
        Boolean isLocked = (Boolean) redisTemplate.opsForValue()
                .get(LOCKED_KEY_PREFIX + userId);
        return Boolean.TRUE.equals(isLocked);
    }

    /**
     * Handles failed login attempt by incrementing counter and locking account if threshold reached.
     *
     * @param userId the user ID
     * @throws AccountLockedException if max attempts exceeded
     */
    private void handleFailedLogin(Long userId) {
        String attemptsKey = LOGIN_ATTEMPTS_KEY_PREFIX + userId;

        // Increment attempts
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        redisTemplate.expire(attemptsKey, LOCKOUT_DURATION_MS, TimeUnit.MILLISECONDS);

        log.debug("Failed login attempt {} for user ID: {}", attempts, userId);

        // Lock account if max attempts exceeded
        if (attempts != null && attempts >= MAX_LOGIN_ATTEMPTS) {
            redisTemplate.opsForValue().set(
                    LOCKED_KEY_PREFIX + userId,
                    true,
                    LOCKOUT_DURATION_MS,
                    TimeUnit.MILLISECONDS
            );
            log.warn("Account locked for user ID: {} after {} failed attempts", userId, attempts);
            throw new AccountLockedException("Account locked due to too many failed login attempts. Please try again after 30 minutes.");
        }
    }

    /**
     * Clears login attempts for user after successful authentication.
     *
     * @param userId the user ID
     */
    private void clearLoginAttempts(Long userId) {
        redisTemplate.delete(LOGIN_ATTEMPTS_KEY_PREFIX + userId);
        log.debug("Login attempts cleared for user ID: {}", userId);
    }

    /**
     * Stores refresh token in Redis with expiration.
     *
     * @param userId the user ID
     * @param refreshToken the refresh token
     */
    private void storeRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_KEY_PREFIX + userId,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
        log.debug("Refresh token stored for user ID: {}", userId);
    }

    /**
     * Builds user info DTO from user entity.
     *
     * @param user the user entity
     * @return user info DTO
     */
    private UserInfo buildUserInfo(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .permissions(user.getRole().getPermissions())
                .build();
    }
}
