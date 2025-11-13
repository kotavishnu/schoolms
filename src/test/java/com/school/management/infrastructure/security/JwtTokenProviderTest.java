package com.school.management.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JwtTokenProvider.
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // 512-bit (64-byte) base64-encoded key for HS512 algorithm
    private static final String SECRET_KEY = "M+Ou3ambB/9DF8qHYtUZA61t5syKnBviLwQ6aLWZzsoSDltOVwVDBMoxQ8NBegTVmif/YSQL8PUMGwHNqblFwQ==";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000L; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("Should generate access token successfully")
    void shouldGenerateAccessToken_WhenValidAuthenticationProvided() {
        // Arrange
        Authentication authentication = createAuthentication("test@example.com", List.of("ROLE_ADMIN"));

        // Act
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    @DisplayName("Should generate refresh token successfully")
    void shouldGenerateRefreshToken_WhenValidAuthenticationProvided() {
        // Arrange
        Authentication authentication = createAuthentication("test@example.com", List.of("ROLE_ADMIN"));

        // Act
        String token = jwtTokenProvider.generateRefreshToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void shouldExtractUsername_WhenValidTokenProvided() {
        // Arrange
        String username = "test@example.com";
        Authentication authentication = createAuthentication(username, List.of("ROLE_ADMIN"));
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should extract authorities from valid token")
    void shouldExtractAuthorities_WhenValidTokenProvided() {
        // Arrange
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        Authentication authentication = createAuthentication("test@example.com", roles);
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        Collection<? extends GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        // Assert
        assertThat(authorities).hasSize(2);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("Should validate token successfully when token is valid")
    void shouldValidateToken_WhenTokenIsValid() {
        // Arrange
        Authentication authentication = createAuthentication("test@example.com", List.of("ROLE_ADMIN"));
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false when validating invalid token")
    void shouldReturnFalse_WhenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false when validating malformed token")
    void shouldReturnFalse_WhenTokenIsMalformed() {
        // Arrange
        String malformedToken = "malformed-token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false when validating null token")
    void shouldReturnFalse_WhenTokenIsNull() {
        // Arrange & Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false when validating empty token")
    void shouldReturnFalse_WhenTokenIsEmpty() {
        // Arrange & Act
        boolean isValid = jwtTokenProvider.validateToken("");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should check if token is expired")
    void shouldCheckTokenExpiration() {
        // Arrange
        Authentication authentication = createAuthentication("test@example.com", List.of("ROLE_ADMIN"));
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Assert
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should get expiration date from token")
    void shouldGetExpirationDate_WhenValidTokenProvided() {
        // Arrange
        Authentication authentication = createAuthentication("test@example.com", List.of("ROLE_ADMIN"));
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        var expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        // Assert
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(java.util.Date.from(java.time.Instant.now()));
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokens_ForDifferentUsers() {
        // Arrange
        Authentication auth1 = createAuthentication("user1@example.com", List.of("ROLE_USER"));
        Authentication auth2 = createAuthentication("user2@example.com", List.of("ROLE_USER"));

        // Act
        String token1 = jwtTokenProvider.generateAccessToken(auth1);
        String token2 = jwtTokenProvider.generateAccessToken(auth2);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void shouldExtractUserId_WhenTokenContainsUserId() {
        // Arrange
        Long userId = 123L;
        Authentication authentication = createAuthenticationWithUserId("test@example.com", userId, List.of("ROLE_ADMIN"));
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Act
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // Assert
        assertThat(extractedUserId).isEqualTo(userId);
    }

    // Helper methods

    private Authentication createAuthentication(String username, List<String> roles) {
        return createAuthenticationWithUserId(username, 1L, roles);
    }

    private Authentication createAuthenticationWithUserId(String username, Long userId, List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(authorities)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Mock authentication details with userId
        if (userId != null) {
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("userId", userId);
            when(authentication.getDetails()).thenReturn(details);
        }

        return authentication;
    }
}
