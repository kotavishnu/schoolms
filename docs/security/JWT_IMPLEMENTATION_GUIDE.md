# JWT Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Components](#components)
4. [Token Structure](#token-structure)
5. [Authentication Flow](#authentication-flow)
6. [Token Refresh](#token-refresh)
7. [Security Considerations](#security-considerations)
8. [Configuration](#configuration)
9. [Usage Examples](#usage-examples)
10. [Troubleshooting](#troubleshooting)

## Overview

The School Management System implements JWT (JSON Web Token) based authentication for stateless, scalable security. This approach provides:

- **Stateless Authentication**: No server-side session storage required
- **Scalability**: Tokens can be validated by any server instance
- **Performance**: Reduced database lookups (user info in token)
- **Mobile-Friendly**: Tokens work seamlessly with mobile apps
- **Microservices Ready**: Tokens can be shared across services

## Architecture

### High-Level Architecture

```
┌─────────────┐                  ┌──────────────────┐
│   Client    │                  │   Spring Boot    │
│             │                  │   Application    │
│             │                  │                  │
│  Browser/   │  1. Login        │  ┌────────────┐  │
│  Mobile App │ ───────────────> │  │Auth        │  │
│             │  (credentials)   │  │Controller  │  │
│             │                  │  └────────────┘  │
│             │                  │         │        │
│             │  2. JWT Token    │         ▼        │
│             │ <─────────────── │  ┌────────────┐  │
│             │                  │  │JWT Token   │  │
│             │                  │  │Provider    │  │
│             │  3. API Request  │  └────────────┘  │
│             │ ───────────────> │         │        │
│             │  + JWT Token     │         ▼        │
│             │                  │  ┌────────────┐  │
│             │                  │  │JWT Auth    │  │
│             │  4. Response     │  │Filter      │  │
│             │ <─────────────── │  └────────────┘  │
└─────────────┘                  └──────────────────┘
```

### Component Interaction

```
Request Flow:
1. Client sends login request → AuthController
2. AuthController validates credentials → UserDetailsService
3. UserDetailsService loads user from DB
4. AuthController generates tokens → JwtTokenProvider
5. JwtTokenProvider creates access + refresh tokens
6. Tokens returned to client

Subsequent Requests:
1. Client sends request with JWT → JwtAuthenticationFilter
2. Filter extracts token from Authorization header
3. Filter validates token → JwtTokenProvider
4. Filter loads user details → UserDetailsService
5. Filter sets authentication → SecurityContext
6. Request proceeds to controller
```

## Components

### 1. JwtTokenProvider

**Location**: `com.school.management.infrastructure.security.JwtTokenProvider`

**Responsibilities**:
- Generate access tokens (15 minutes expiry)
- Generate refresh tokens (7 days expiry)
- Validate tokens
- Extract claims (username, authorities, userId)

**Key Methods**:
```java
public String generateAccessToken(Authentication authentication);
public String generateRefreshToken(Authentication authentication);
public boolean validateToken(String token);
public String getUsernameFromToken(String token);
public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token);
```

### 2. JwtAuthenticationFilter

**Location**: `com.school.management.infrastructure.security.JwtAuthenticationFilter`

**Responsibilities**:
- Intercept all requests
- Extract JWT from Authorization header
- Validate token
- Set authentication in SecurityContext

**Filter Order**:
```
RateLimitingFilter (1st)
    ↓
JwtAuthenticationFilter (2nd)
    ↓
UsernamePasswordAuthenticationFilter (3rd)
```

### 3. CustomUserDetailsService

**Location**: `com.school.management.infrastructure.security.CustomUserDetailsService`

**Responsibilities**:
- Load user from database by username
- Check account status (enabled, locked, expired)
- Return UserDetails with authorities

## Token Structure

### Access Token

**Expiry**: 15 minutes

**Claims**:
```json
{
  "sub": "user@example.com",           // Username (subject)
  "userId": 123,                        // User ID
  "authorities": [                      // Roles and permissions
    "ROLE_ADMIN",
    "STUDENT_MANAGE",
    "CLASS_MANAGE"
  ],
  "iat": 1699999999,                   // Issued at (timestamp)
  "exp": 1700000899,                   // Expiration (timestamp)
  "iss": "school-management-system"    // Issuer
}
```

**Header**:
```json
{
  "alg": "HS512",                      // Algorithm
  "typ": "JWT"                         // Type
}
```

### Refresh Token

**Expiry**: 7 days

**Purpose**: Obtain new access token without re-authentication

**Claims**: Same as access token

## Authentication Flow

### 1. Initial Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response**:
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900
  }
}
```

### 2. Authenticated Request

```http
GET /api/students
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Flow**:
1. JwtAuthenticationFilter extracts token
2. Token validated by JwtTokenProvider
3. Username extracted from token
4. User details loaded from database
5. Authentication set in SecurityContext
6. Request proceeds to controller

### 3. Token Refresh

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response**:
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900
  }
}
```

## Token Refresh

### Why Refresh Tokens?

- **Short-lived access tokens**: Minimize damage if token is stolen
- **Long-lived refresh tokens**: Avoid frequent re-authentication
- **Token rotation**: Generate new refresh token on each refresh

### Refresh Strategy

```java
// Access token expired (after 15 minutes)
if (isAccessTokenExpired()) {
    // Use refresh token to get new access token
    String newAccessToken = refreshAccessToken(refreshToken);

    // Continue API calls with new access token
    makeApiCall(newAccessToken);
}
```

### Refresh Token Security

1. **Rotation**: New refresh token issued on each refresh
2. **Single Use**: Old refresh token invalidated after use
3. **Secure Storage**: Store in HttpOnly cookie (web) or secure storage (mobile)
4. **Revocation**: Ability to revoke refresh tokens (logout)

## Security Considerations

### 1. Token Storage

**Web Applications**:
- **Access Token**: Memory (JavaScript variable)
- **Refresh Token**: HttpOnly cookie (secure, sameSite=strict)

**Mobile Applications**:
- **Access Token**: Secure storage (Keychain/Keystore)
- **Refresh Token**: Secure storage (Keychain/Keystore)

**Never**:
- ❌ localStorage (vulnerable to XSS)
- ❌ sessionStorage (vulnerable to XSS)
- ❌ Regular cookies without HttpOnly flag

### 2. Token Transmission

- ✅ HTTPS only (TLS 1.2+)
- ✅ Authorization header: `Bearer {token}`
- ❌ Never in URL query parameters
- ❌ Never in request body (except refresh endpoint)

### 3. Token Validation

```java
// Always validate:
1. Signature (HMAC-SHA512)
2. Expiration (exp claim)
3. Issuer (iss claim)
4. Token format (header.payload.signature)

// Check user account status:
1. Account enabled
2. Account not locked
3. Credentials not expired
```

### 4. Secret Key Management

```properties
# application.yml (DO NOT commit actual secret)
application:
  security:
    jwt:
      # Generate with: openssl rand -base64 64
      secret-key: ${JWT_SECRET}  # Environment variable
      expiration: 900000          # 15 minutes
      refresh-expiration: 604800000  # 7 days
```

**Generate Secure Secret**:
```bash
# Linux/Mac
openssl rand -base64 64

# Java
SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
```

### 5. Rate Limiting

- Implemented via `RateLimitingFilter`
- Max 5 failed login attempts
- 30-minute account lockout
- Redis-based tracking for distributed systems

## Configuration

### application.yml

```yaml
application:
  security:
    jwt:
      # Secret key for signing tokens (Base64 encoded)
      secret-key: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}

      # Access token expiration (15 minutes in milliseconds)
      expiration: ${JWT_EXPIRATION:900000}

      # Refresh token expiration (7 days in milliseconds)
      refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}

      # Token issuer
      issuer: school-management-system

    # CORS Configuration
    cors:
      allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200}
      allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
      allowed-headers: "*"
      exposed-headers: Authorization,X-Total-Count,X-Page-Number
      allow-credentials: true
      max-age: 3600

    # Rate Limiting
    rate-limit:
      enabled: true
      max-attempts: 5
      lockout-duration: 30  # minutes
```

### Environment Variables

```bash
# Production
export JWT_SECRET="your-very-secure-secret-key-here"
export JWT_EXPIRATION=900000
export JWT_REFRESH_EXPIRATION=604800000
export CORS_ALLOWED_ORIGINS="https://school.example.com"

# Development
export JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:4200"
```

## Usage Examples

### Frontend Integration (React)

```javascript
// Login
async function login(username, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });

  const data = await response.json();

  // Store tokens (in-memory for access, HttpOnly cookie for refresh)
  sessionStorage.setItem('accessToken', data.data.accessToken);

  return data;
}

// Make authenticated request
async function getStudents() {
  const token = sessionStorage.getItem('accessToken');

  const response = await fetch('/api/students', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (response.status === 401) {
    // Token expired, refresh it
    await refreshToken();
    return getStudents(); // Retry
  }

  return response.json();
}

// Refresh token
async function refreshToken() {
  const refreshToken = getCookie('refreshToken');

  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });

  const data = await response.json();
  sessionStorage.setItem('accessToken', data.data.accessToken);

  return data;
}
```

### Backend Controller (Protected Endpoint)

```java
@RestController
@RequestMapping("/api/students")
public class StudentController extends BaseController {

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('STUDENT_READ', 'STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudent(@PathVariable Long id) {
        StudentDTO student = studentService.findByIdOrThrow(id);
        return ok(student);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<ApiResponse<StudentDTO>> createStudent(
            @Valid @RequestBody StudentDTO dto) {
        StudentDTO created = studentService.create(dto);
        return created("Student created successfully", created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return noContent();
    }
}
```

## Troubleshooting

### Issue: "JWT token is expired"

**Cause**: Access token expired (after 15 minutes)

**Solution**:
```java
// Frontend: Implement automatic token refresh
if (response.status === 401) {
    await refreshToken();
    return retryRequest();
}
```

### Issue: "Invalid JWT signature"

**Causes**:
1. Secret key mismatch
2. Token tampered with
3. Different secret in production vs. development

**Solution**:
```bash
# Verify JWT_SECRET environment variable matches
echo $JWT_SECRET

# Generate new secret if needed
openssl rand -base64 64
```

### Issue: "Account locked"

**Cause**: Too many failed login attempts (5+)

**Solution**:
```java
// Wait 30 minutes, or
// Admin can manually unlock account
rateLimitingFilter.resetFailedAttempts(username);
```

### Issue: "CORS error"

**Cause**: Frontend origin not in allowed origins

**Solution**:
```yaml
# application.yml
application:
  security:
    cors:
      allowed-origins: http://localhost:3000,https://your-frontend.com
```

### Issue: "Token not found in request"

**Cause**: Authorization header not set correctly

**Solution**:
```http
# Correct format
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

# Common mistakes:
❌ Authorization: eyJhbGciOiJIUzUxMiJ9...  (missing "Bearer ")
❌ Authorization: bearer eyJhbGciOiJIUzUxMiJ9...  (lowercase "bearer")
❌ Auth: Bearer eyJhbGciOiJIUzUxMiJ9...  (wrong header name)
```

### Debug Logging

```yaml
# Enable debug logging for security
logging:
  level:
    org.springframework.security: DEBUG
    com.school.management.infrastructure.security: DEBUG
```

## Best Practices

1. **Short-lived access tokens**: 15 minutes maximum
2. **Rotate refresh tokens**: Issue new refresh token on each refresh
3. **Use HTTPS**: Always in production
4. **Secure secret key**: 256+ bits, stored in environment variable
5. **Implement logout**: Revoke refresh tokens on logout
6. **Monitor token usage**: Log token generation and validation
7. **Handle expiration gracefully**: Auto-refresh in frontend
8. **Validate token on every request**: Never trust client-side validation

---

**Last Updated**: 2025-11-11
**Version**: 1.0.0
**Author**: School Management System Development Team
