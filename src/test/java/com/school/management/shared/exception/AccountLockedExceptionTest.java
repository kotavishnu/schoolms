package com.school.management.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AccountLockedException.
 */
@DisplayName("AccountLockedException Tests")
class AccountLockedExceptionTest {

    @Test
    @DisplayName("Should create AccountLockedException with username only")
    void shouldCreateAccountLockedException_WhenUsernameProvided() {
        // Arrange
        String username = "john.doe@example.com";

        // Act
        AccountLockedException exception = new AccountLockedException(username);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(username);
        assertThat(exception.getErrorCode()).isEqualTo("ACCOUNT_LOCKED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(423);
        assertThat(exception.getUsername()).isEqualTo(username);
        assertThat(exception.getLockoutEndTime()).isNull();
    }

    @Test
    @DisplayName("Should create AccountLockedException with username and lockout end time")
    void shouldCreateAccountLockedException_WhenUsernameAndLockoutTimeProvided() {
        // Arrange
        String username = "jane.smith@example.com";
        LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(30);

        // Act
        AccountLockedException exception = new AccountLockedException(username, lockoutEndTime);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(username);
        assertThat(exception.getErrorCode()).isEqualTo("ACCOUNT_LOCKED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(423);
        assertThat(exception.getUsername()).isEqualTo(username);
        assertThat(exception.getLockoutEndTime()).isEqualTo(lockoutEndTime);
    }

    @Test
    @DisplayName("Should create AccountLockedException with username, lockout time, and attempts")
    void shouldCreateAccountLockedException_WhenAllParametersProvided() {
        // Arrange
        String username = "test.user@example.com";
        LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(30);
        int failedAttempts = 5;

        // Act
        AccountLockedException exception = new AccountLockedException(username, lockoutEndTime, failedAttempts);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(username);
        assertThat(exception.getMessage()).contains(String.valueOf(failedAttempts));
        assertThat(exception.getErrorCode()).isEqualTo("ACCOUNT_LOCKED");
        assertThat(exception.getUsername()).isEqualTo(username);
        assertThat(exception.getLockoutEndTime()).isEqualTo(lockoutEndTime);
        assertThat(exception.getFailedAttempts()).isEqualTo(failedAttempts);
    }

    @Test
    @DisplayName("Should create AccountLockedException with custom message")
    void shouldCreateAccountLockedException_WhenCustomMessageProvided() {
        // Arrange
        String message = "Account has been locked due to suspicious activity";

        // Act
        AccountLockedException exception = new AccountLockedException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("ACCOUNT_LOCKED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(423);
    }

    @Test
    @DisplayName("Should calculate remaining lockout time correctly")
    void shouldCalculateRemainingLockoutTime_WhenLockoutTimeProvided() {
        // Arrange
        String username = "user@example.com";
        LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(15);

        // Act
        AccountLockedException exception = new AccountLockedException(username, lockoutEndTime);
        long remainingMinutes = exception.getRemainingLockoutMinutes();

        // Assert
        assertThat(remainingMinutes).isBetween(14L, 16L); // Allow 1 minute tolerance
    }

    @Test
    @DisplayName("Should return zero remaining time when lockout has expired")
    void shouldReturnZeroRemainingTime_WhenLockoutExpired() {
        // Arrange
        String username = "user@example.com";
        LocalDateTime lockoutEndTime = LocalDateTime.now().minusMinutes(5);

        // Act
        AccountLockedException exception = new AccountLockedException(username, lockoutEndTime);
        long remainingMinutes = exception.getRemainingLockoutMinutes();

        // Assert
        assertThat(remainingMinutes).isLessThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should be instance of BaseException")
    void shouldBeInstanceOfBaseException() {
        // Arrange & Act
        AccountLockedException exception = new AccountLockedException("test");

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
