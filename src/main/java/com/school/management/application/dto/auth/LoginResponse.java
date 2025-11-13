package com.school.management.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful authentication.
 *
 * <p>Contains access token, refresh token, and user information.</p>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT access token for API authentication.
     */
    private String accessToken;

    /**
     * JWT refresh token for obtaining new access tokens.
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiration time in milliseconds.
     */
    private Long expiresIn;

    /**
     * Authenticated user information.
     */
    private UserInfo userInfo;
}
