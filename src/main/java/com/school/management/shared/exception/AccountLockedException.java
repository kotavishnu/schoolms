package com.school.management.shared.exception;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Exception thrown when an account is locked due to security reasons.
 *
 * <p>This exception represents errors when attempting to authenticate
 * with an account that has been locked, typically due to multiple
 * failed login attempts.</p>
 *
 * <p>HTTP Status Code: 423 (Locked)</p>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Account locked after 5 failed login attempts</li>
 *   <li>Account locked by administrator</li>
 *   <li>Account locked due to suspicious activity</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Getter
public class AccountLockedException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "ACCOUNT_LOCKED";
    private static final int HTTP_STATUS_CODE = 423;

    /**
     * Username of the locked account.
     */
    private final String username;

    /**
     * Time when the account lockout will end.
     */
    private final LocalDateTime lockoutEndTime;

    /**
     * Number of failed login attempts that caused the lockout.
     */
    private final int failedAttempts;

    /**
     * Creates a new account locked exception for the specified username.
     *
     * @param username the username of the locked account
     * @return an AccountLockedException instance
     */
    public static AccountLockedException forUsername(String username) {
        return new AccountLockedException(username, null, 0);
    }

    /**
     * Constructs a new account locked exception with username and lockout end time.
     *
     * @param username the username of the locked account
     * @param lockoutEndTime when the account lockout will end
     */
    public AccountLockedException(String username, LocalDateTime lockoutEndTime) {
        super(String.format("Account '%s' is locked until %s", username, lockoutEndTime),
                DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.username = username;
        this.lockoutEndTime = lockoutEndTime;
        this.failedAttempts = 0;
    }

    /**
     * Constructs a new account locked exception with all details.
     *
     * @param username the username of the locked account
     * @param lockoutEndTime when the account lockout will end
     * @param failedAttempts number of failed attempts that caused lockout
     */
    public AccountLockedException(String username, LocalDateTime lockoutEndTime, int failedAttempts) {
        super(lockoutEndTime != null && failedAttempts > 0
                        ? String.format("Account '%s' is locked due to %d failed login attempts. Lockout ends at %s",
                                username, failedAttempts, lockoutEndTime)
                        : String.format("Account '%s' is locked due to multiple failed login attempts", username),
                DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.username = username;
        this.lockoutEndTime = lockoutEndTime;
        this.failedAttempts = failedAttempts;
    }

    /**
     * Constructs a new account locked exception with custom message.
     *
     * @param message the detail message
     */
    public AccountLockedException(String message) {
        super(message, DEFAULT_ERROR_CODE, HTTP_STATUS_CODE);
        this.username = null;
        this.lockoutEndTime = null;
        this.failedAttempts = 0;
    }

    /**
     * Calculates the remaining lockout time in minutes.
     *
     * @return remaining minutes until lockout ends, or 0 if lockout has expired
     */
    public long getRemainingLockoutMinutes() {
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
     * Checks if the lockout has expired.
     *
     * @return true if lockout has expired, false otherwise
     */
    public boolean isLockoutExpired() {
        if (lockoutEndTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(lockoutEndTime);
    }
}
