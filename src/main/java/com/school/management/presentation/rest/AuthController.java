package com.school.management.presentation.rest;

import com.school.management.application.dto.auth.LoginRequest;
import com.school.management.application.dto.auth.LoginResponse;
import com.school.management.application.dto.auth.RefreshTokenRequest;
import com.school.management.application.dto.auth.TokenResponse;
import com.school.management.application.service.AuthService;
import com.school.management.infrastructure.security.JwtTokenProvider;
import com.school.management.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <p>Provides endpoints for:</p>
 * <ul>
 *   <li>User login with username/password</li>
 *   <li>Access token refresh using refresh token</li>
 *   <li>User logout with token revocation</li>
 * </ul>
 *
 * <p>All endpoints return responses in standard ApiResponse format.</p>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and token management APIs")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates user with username and password.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Validates credentials</li>
     *   <li>Checks account status and lockout</li>
     *   <li>Generates access and refresh tokens</li>
     *   <li>Returns user information and tokens</li>
     * </ol>
     *
     * <p>Rate Limiting: Max 100 requests per minute per IP (configured in RateLimitingFilter)</p>
     *
     * @param loginRequest the login credentials
     * @return API response with login response containing tokens and user info
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user with username and password, returns JWT tokens and user information"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - missing or invalid credentials"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid username or password"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "423",
                    description = "Account locked - too many failed login attempts"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - rate limit exceeded"
            )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Login attempt for username: {}", loginRequest.getUsername());

        LoginResponse response = authService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        log.info("Login successful for username: {}", loginRequest.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Refreshes access token using refresh token.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Validates refresh token format and signature</li>
     *   <li>Verifies token matches stored token in Redis</li>
     *   <li>Generates new access token</li>
     *   <li>Returns new access token</li>
     * </ol>
     *
     * <p>Note: Refresh token remains valid and is not rotated.</p>
     *
     * @param refreshTokenRequest the refresh token request
     * @return API response with new access token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates new access token using valid refresh token"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - missing refresh token"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or expired refresh token"
            )
    })
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        log.debug("Token refresh attempt");

        TokenResponse response = authService.refreshToken(
                refreshTokenRequest.getRefreshToken()
        );

        log.debug("Token refreshed successfully");

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    /**
     * Logs out user by revoking tokens.
     *
     * <p>Process:</p>
     * <ol>
     *   <li>Extracts access token from Authorization header</li>
     *   <li>Blacklists access token (until expiration)</li>
     *   <li>Deletes refresh token from Redis</li>
     * </ol>
     *
     * <p>Note: User must be authenticated to call this endpoint.</p>
     *
     * @param authorization the Authorization header containing Bearer token
     * @param authentication the authenticated user
     * @return API response confirming logout
     */
    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "User logout",
            description = "Revokes access and refresh tokens, preventing further use"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token"
            )
    })
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            Authentication authentication) {

        log.info("Logout attempt for user: {}", authentication.getName());

        // Extract token from Authorization header
        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }

        // Extract user ID from token
        Long userId = null;
        if (accessToken != null) {
            userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        }

        // Logout
        authService.logout(accessToken, userId);

        log.info("Logout successful for user: {}", authentication.getName());

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
