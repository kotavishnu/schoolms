package com.school.management.infrastructure.security;

import com.school.management.shared.exception.AccountLockedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Filter for preventing brute force attacks.
 *
 * <p>This filter implements account lockout mechanism:</p>
 * <ul>
 *   <li>Tracks failed login attempts per username</li>
 *   <li>Locks account after 5 failed attempts</li>
 *   <li>30-minute lockout duration</li>
 *   <li>Uses Redis for distributed tracking</li>
 * </ul>
 *
 * <p>Redis Keys:</p>
 * <pre>
 * login:attempts:{username}  - Failed attempt counter
 * login:lockout:{username}   - Lockout timestamp
 * </pre>
 *
 * <p>Configuration:</p>
 * <ul>
 *   <li>MAX_ATTEMPTS: 5 (configurable)</li>
 *   <li>LOCKOUT_DURATION: 30 minutes (configurable)</li>
 *   <li>ATTEMPT_TTL: 1 hour (auto-reset after no activity)</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;
    private static final int ATTEMPT_TTL_HOURS = 1;

    private static final String ATTEMPTS_KEY_PREFIX = "login:attempts:";
    private static final String LOCKOUT_KEY_PREFIX = "login:lockout:";

    /**
     * Filters login requests to enforce rate limiting.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);
        return ;       
       /*  // Only apply rate limiting to login endpoint
        if (!isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract username from request (would need to parse request body)
            // For now, we'll use IP-based rate limiting
            String identifier = getClientIdentifier(request);

            // Check if account is locked
            if (isAccountLocked(identifier)) {
                LocalDateTime lockoutEndTime = getLockoutEndTime(identifier);
                long remainingMinutes = getRemainingLockoutMinutes(lockoutEndTime);

                log.warn("Login attempt blocked for locked account: {}", identifier);

                throw new AccountLockedException(
                        identifier,
                        lockoutEndTime,
                        MAX_ATTEMPTS
                );
            }

            // Continue with filter chain
            filterChain.doFilter(request, response);

        } catch (AccountLockedException ex) {
            // Re-throw AccountLockedException to be handled by GlobalExceptionHandler
            throw ex;
        } catch (Exception ex) {
            log.error("Error in rate limiting filter", ex);
            filterChain.doFilter(request, response);
        } */
    }

    /**
     * Records a failed login attempt.
     *
     * @param username the username
     */
    public void recordFailedAttempt(String username) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + username;

        // Increment attempt counter
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

        if (attempts == null) {
            attempts = 1L;
        }

        // Set TTL for attempts counter
        redisTemplate.expire(attemptsKey, ATTEMPT_TTL_HOURS, TimeUnit.HOURS);

        log.debug("Failed login attempts for {}: {}", username, attempts);

        // Lock account if max attempts reached
        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(username);
        }
    }

    /**
     * Resets failed attempts counter after successful login.
     *
     * @param username the username
     */
    public void resetFailedAttempts(String username) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + username;
        redisTemplate.delete(attemptsKey);
        log.debug("Reset failed attempts for: {}", username);
    }

    /**
     * Locks an account for the configured duration.
     *
     * @param username the username
     */
    private void lockAccount(String username) {
        String lockoutKey = LOCKOUT_KEY_PREFIX + username;
        LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);

        redisTemplate.opsForValue().set(
                lockoutKey,
                lockoutEndTime.toString(),
                LOCKOUT_DURATION_MINUTES,
                TimeUnit.MINUTES
        );

        log.warn("Account locked due to excessive failed attempts: {}", username);
    }

    /**
     * Checks if an account is currently locked.
     *
     * @param identifier the username or identifier
     * @return true if locked, false otherwise
     */
    private boolean isAccountLocked(String identifier) {
        String lockoutKey = LOCKOUT_KEY_PREFIX + identifier;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockoutKey));
    }

    /**
     * Gets the lockout end time for an account.
     *
     * @param identifier the username or identifier
     * @return lockout end time, or null if not locked
     */
    private LocalDateTime getLockoutEndTime(String identifier) {
        String lockoutKey = LOCKOUT_KEY_PREFIX + identifier;
        Object value = redisTemplate.opsForValue().get(lockoutKey);

        if (value == null) {
            return null;
        }

        return LocalDateTime.parse(value.toString());
    }

    /**
     * Calculates remaining lockout time in minutes.
     *
     * @param lockoutEndTime lockout end time
     * @return remaining minutes
     */
    private long getRemainingLockoutMinutes(LocalDateTime lockoutEndTime) {
        if (lockoutEndTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(lockoutEndTime)) {
            return 0;
        }

        Duration duration = Duration.between(now, lockoutEndTime);
        return duration.toMinutes();
    }

    /**
     * Gets failed attempt count for an account.
     *
     * @param username the username
     * @return failed attempt count
     */
    public int getFailedAttempts(String username) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + username;
        Object value = redisTemplate.opsForValue().get(attemptsKey);

        if (value == null) {
            return 0;
        }

        return Integer.parseInt(value.toString());
    }

    /**
     * Determines if the request is a login request.
     *
     * @param request HTTP request
     * @return true if login request, false otherwise
     */
    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) &&
                request.getRequestURI().endsWith("/api/auth/login");
    }

    /**
     * Gets client identifier (IP address or username).
     *
     * @param request HTTP request
     * @return client identifier
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Get client IP address
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
