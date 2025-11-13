package com.school.management.presentation.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.management.application.dto.auth.*;
import com.school.management.application.service.AuthService;
import com.school.management.config.SecurityConfiguration;
import com.school.management.domain.security.Role;
import com.school.management.infrastructure.security.JwtAuthenticationFilter;
import com.school.management.infrastructure.security.JwtTokenProvider;
import com.school.management.infrastructure.security.RateLimitingFilter;
import com.school.management.presentation.exception.GlobalExceptionHandler;
import com.school.management.shared.exception.AccountLockedException;
import com.school.management.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 *
 * <p>Tests authentication endpoints including:</p>
 * <ul>
 *   <li>POST /api/v1/auth/login</li>
 *   <li>POST /api/v1/auth/refresh</li>
 *   <li>POST /api/v1/auth/logout</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class})
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private RateLimitingFilter rateLimitingFilter;

    @MockBean
    private SecurityConfiguration securityConfiguration;

    private LoginRequest validLoginRequest;
    private LoginResponse successLoginResponse;
    private RefreshTokenRequest validRefreshTokenRequest;
    private TokenResponse successTokenResponse;

    @BeforeEach
    void setUp() {
        // Setup valid login request
        validLoginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("Password123!")
                .build();

        // Setup successful login response
        UserInfo userInfo = UserInfo.builder()
                .userId(1L)
                .username("john.doe")
                .fullName("John Doe")
                .email("john.doe@school.com")
                .role("OFFICE_STAFF")
                .permissions(Set.of("STUDENT_CREATE", "STUDENT_READ", "STUDENT_UPDATE"))
                .build();

        successLoginResponse = LoginResponse.builder()
                .accessToken("eyJhbGciOiJIUzUxMiJ9...")
                .refreshToken("eyJhbGciOiJIUzUxMiJ9...refresh")
                .tokenType("Bearer")
                .expiresIn(900000L)
                .userInfo(userInfo)
                .build();

        // Setup valid refresh token request
        validRefreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("eyJhbGciOiJIUzUxMiJ9...refresh")
                .build();

        // Setup successful token response
        successTokenResponse = TokenResponse.builder()
                .accessToken("eyJhbGciOiJIUzUxMiJ9...new")
                .tokenType("Bearer")
                .expiresIn(900000L)
                .build();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully_WhenCredentialsAreValid() throws Exception {
        // Arrange
        when(authService.authenticate(anyString(), anyString()))
                .thenReturn(successLoginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900000))
                .andExpect(jsonPath("$.data.userInfo.userId").value(1))
                .andExpect(jsonPath("$.data.userInfo.username").value("john.doe"))
                .andExpect(jsonPath("$.data.userInfo.role").value("OFFICE_STAFF"))
                .andExpect(jsonPath("$.data.userInfo.permissions").isArray());

        verify(authService).authenticate("john.doe", "Password123!");
    }

    @Test
    @DisplayName("Should return 400 when username is missing")
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {
        // Arrange
        LoginRequest invalidRequest = LoginRequest.builder()
                .password("Password123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());

        verify(authService, never()).authenticate(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 400 when password is missing")
    void shouldReturn400_WhenPasswordIsMissing() throws Exception {
        // Arrange
        LoginRequest invalidRequest = LoginRequest.builder()
                .username("john.doe")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(authService, never()).authenticate(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void shouldReturn401_WhenCredentialsAreInvalid() throws Exception {
        // Arrange
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new UnauthorizedException("Invalid username or password"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("Should return 423 when account is locked")
    void shouldReturn423_WhenAccountIsLocked() throws Exception {
        // Arrange
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new AccountLockedException("Account is temporarily locked"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Account is temporarily locked"));
    }

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void shouldRefreshTokenSuccessfully_WhenRefreshTokenIsValid() throws Exception {
        // Arrange
        when(authService.refreshToken(anyString()))
                .thenReturn(successTokenResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRefreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900000));

        verify(authService).refreshToken(validRefreshTokenRequest.getRefreshToken());
    }

    @Test
    @DisplayName("Should return 400 when refresh token is missing")
    void shouldReturn400_WhenRefreshTokenIsMissing() throws Exception {
        // Arrange
        RefreshTokenRequest invalidRequest = RefreshTokenRequest.builder().build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(authService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("Should return 401 when refresh token is invalid")
    void shouldReturn401_WhenRefreshTokenIsInvalid() throws Exception {
        // Arrange
        when(authService.refreshToken(anyString()))
                .thenThrow(new UnauthorizedException("Invalid refresh token"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRefreshTokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    @DisplayName("Should return 401 when refresh token is expired")
    void shouldReturn401_WhenRefreshTokenIsExpired() throws Exception {
        // Arrange
        when(authService.refreshToken(anyString()))
                .thenThrow(new UnauthorizedException("Refresh token not found or expired"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRefreshTokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Refresh token not found or expired"));
    }

    @Test
    @WithMockUser(username = "john.doe", roles = "OFFICE_STAFF")
    @DisplayName("Should logout successfully with valid token")
    void shouldLogoutSuccessfully_WhenTokenIsValid() throws Exception {
        // Arrange
        String accessToken = "Bearer eyJhbGciOiJIUzUxMiJ9...";
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        doNothing().when(authService).logout(anyString(), anyLong());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful"));

        verify(authService).logout(anyString(), eq(1L));
    }

    @Test
    @DisplayName("Should return 401 when logout without authentication")
    void shouldReturn401_WhenLogoutWithoutAuthentication() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(authService, never()).logout(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should return 400 when request body is invalid JSON")
    void shouldReturn400_WhenRequestBodyIsInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticate(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 415 when content type is not JSON")
    void shouldReturn415_WhenContentTypeIsNotJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(authService, never()).authenticate(anyString(), anyString());
    }
}
