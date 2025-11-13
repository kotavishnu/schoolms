package com.school.management.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 *
 * <p>This component handles all JWT-related operations including:</p>
 * <ul>
 *   <li>Access token generation (15-minute expiry)</li>
 *   <li>Refresh token generation (7-day expiry)</li>
 *   <li>Token validation and parsing</li>
 *   <li>Claims extraction (username, authorities, userId)</li>
 * </ul>
 *
 * <p>Token Structure:</p>
 * <pre>
 * {
 *   "sub": "user@example.com",        // Username
 *   "userId": 123,                     // User ID
 *   "authorities": ["ROLE_ADMIN"],     // User roles/authorities
 *   "iat": 1699999999,                 // Issued at
 *   "exp": 1699999999,                 // Expiration
 *   "iss": "school-management-system"  // Issuer
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${application.security.jwt.issuer:school-management-system}")
    private String issuer;

    private SecretKey key;

    /**
     * Initializes the signing key from the secret.
     * TEMPORARILY: Error handling added to allow server to start even if JWT secret is invalid
     */
    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            log.info("JWT signing key initialized successfully");
        } catch (Exception e) {
            log.warn("Failed to initialize JWT signing key. JWT authentication will not work: {}", e.getMessage());
            // Create a dummy key to prevent NullPointerException
            // This allows the server to start without JWT functionality
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    /**
     * Generates an access token for the authenticated user.
     *
     * @param authentication Spring Security authentication object
     * @return JWT access token
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accessTokenExpiration);
    }

    /**
     * Generates a refresh token for the authenticated user.
     *
     * @param authentication Spring Security authentication object
     * @return JWT refresh token
     */
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshTokenExpiration);
    }

    /**
     * Generates a JWT token with the specified expiration time.
     *
     * @param authentication Spring Security authentication object
     * @param expirationTime expiration time in milliseconds
     * @return JWT token
     */
    private String generateToken(Authentication authentication, long expirationTime) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorityStrings);

        // Extract user ID if available in authentication details
        if (authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            if (details.containsKey("userId")) {
                claims.put("userId", details.get("userId"));
            }
        }

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(issuer)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extracts username from JWT token.
     *
     * @param token JWT token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extracts user ID from JWT token.
     *
     * @param token JWT token
     * @return user ID, or null if not present
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");

        if (userId == null) {
            return null;
        }

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }

        if (userId instanceof Long) {
            return (Long) userId;
        }

        return Long.valueOf(userId.toString());
    }

    /**
     * Extracts authorities from JWT token.
     *
     * @param token JWT token
     * @return collection of granted authorities
     */
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        List<String> authorities = (List<String>) claims.get("authorities");

        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Gets expiration date from JWT token.
     *
     * @param token JWT token
     * @return expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Checks if JWT token is expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validates JWT token.
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("Token is null or empty");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Extracts all claims from JWT token.
     *
     * @param token JWT token
     * @return claims
     * @throws JwtException if token is invalid
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
