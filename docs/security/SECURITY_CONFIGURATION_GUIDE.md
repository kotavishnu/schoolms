# Security Configuration Guide

## Table of Contents
1. [Overview](#overview)
2. [Security Architecture](#security-architecture)
3. [Authentication & Authorization](#authentication--authorization)
4. [Role-Based Access Control (RBAC)](#role-based-access-control-rbac)
5. [Security Filters](#security-filters)
6. [Password Security](#password-security)
7. [CORS Configuration](#cors-configuration)
8. [Security Headers](#security-headers)
9. [Rate Limiting](#rate-limiting)
10. [Best Practices](#best-practices)

## Overview

The School Management System implements comprehensive security measures following industry best practices:

- **JWT Authentication**: Stateless token-based authentication
- **RBAC**: Fine-grained role and permission-based access control
- **BCrypt Password Hashing**: Strong password encryption (12 rounds)
- **Rate Limiting**: Brute force attack prevention
- **CORS**: Cross-origin resource sharing for frontend integration
- **Security Headers**: HSTS, CSP, X-Frame-Options, etc.
- **CSRF Protection**: Cross-site request forgery prevention
- **Account Lockout**: Automatic lockout after failed attempts

## Security Architecture

### Layered Security Model

```
┌─────────────────────────────────────────────────┐
│  Layer 1: Network Security (HTTPS/TLS)         │
├─────────────────────────────────────────────────┤
│  Layer 2: CORS & Security Headers              │
├─────────────────────────────────────────────────┤
│  Layer 3: Rate Limiting (Brute Force Protection)│
├─────────────────────────────────────────────────┤
│  Layer 4: JWT Authentication                    │
├─────────────────────────────────────────────────┤
│  Layer 5: Authorization (RBAC)                  │
├─────────────────────────────────────────────────┤
│  Layer 6: Method-Level Security (@PreAuthorize) │
├─────────────────────────────────────────────────┤
│  Layer 7: Business Logic Validation             │
└─────────────────────────────────────────────────┘
```

### Security Filter Chain

```
HTTP Request
    ↓
[1] RateLimitingFilter
    ↓ (Check failed attempts, account lockout)
[2] JwtAuthenticationFilter
    ↓ (Validate JWT, set SecurityContext)
[3] Spring Security FilterChain
    ↓ (Check authorization rules)
[4] @PreAuthorize / @PostAuthorize
    ↓ (Method-level security)
[5] Controller Method
    ↓
Business Logic
```

## Authentication & Authorization

### Authentication Methods

1. **JWT Token Authentication** (Primary)
   - Stateless
   - Scalable
   - Mobile-friendly

2. **Username/Password** (Login only)
   - BCrypt hashed passwords
   - Account lockout protection
   - Password strength validation

### Authorization Levels

1. **No Authentication** (Public endpoints)
   - `/api/auth/login`
   - `/api/auth/register`
   - `/api/public/**`
   - `/swagger-ui/**`
   - `/actuator/health`

2. **Authenticated** (Any logged-in user)
   - `/api/**` (default for all endpoints)

3. **Role-Based** (Specific roles required)
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   public void deleteUser(Long id) { ... }
   ```

4. **Permission-Based** (Specific permissions required)
   ```java
   @PreAuthorize("hasAuthority('STUDENT_CREATE')")
   public StudentDTO createStudent(StudentDTO dto) { ... }
   ```

## Role-Based Access Control (RBAC)

### Roles

| Role | Description | Typical Users |
|------|-------------|---------------|
| `ADMIN` | Full system access | System administrators |
| `PRINCIPAL` | School management access | School principal |
| `OFFICE_STAFF` | Daily operations | Front desk, admissions |
| `ACCOUNTS_MANAGER` | Financial operations | Accountants |
| `AUDITOR` | Read-only access | Auditors, inspectors |

### Authorities (Permissions)

Authorities follow the pattern: `{RESOURCE}_{ACTION}`

**Student Authorities**:
- `STUDENT_CREATE` - Create new students
- `STUDENT_READ` - View student information
- `STUDENT_UPDATE` - Update student records
- `STUDENT_DELETE` - Delete students
- `STUDENT_MANAGE` - Full student management

**Class Authorities**:
- `CLASS_CREATE`, `CLASS_READ`, `CLASS_UPDATE`, `CLASS_DELETE`, `CLASS_MANAGE`

**Fee Authorities**:
- `FEE_CREATE`, `FEE_READ`, `FEE_UPDATE`, `FEE_DELETE`, `FEE_MANAGE`

**Payment Authorities**:
- `PAYMENT_CREATE`, `PAYMENT_READ`, `PAYMENT_UPDATE`, `PAYMENT_DELETE`, `PAYMENT_MANAGE`

**System Authorities**:
- `SYSTEM_CONFIG` - Manage system configuration
- `SYSTEM_SECURITY` - Manage security settings
- `AUDIT_READ` - View audit logs
- `REPORT_GENERATE` - Generate reports

### Role-Authority Mapping

```java
public enum Role {
    ADMIN {
        @Override
        public Set<Authority> getAuthorities() {
            return EnumSet.allOf(Authority.class); // All permissions
        }
    },

    PRINCIPAL {
        @Override
        public Set<Authority> getAuthorities() {
            return EnumSet.of(
                STUDENT_READ, STUDENT_UPDATE,
                CLASS_MANAGE,
                FEE_READ,
                PAYMENT_READ,
                REPORT_GENERATE,
                AUDIT_READ
            );
        }
    },

    OFFICE_STAFF {
        @Override
        public Set<Authority> getAuthorities() {
            return EnumSet.of(
                STUDENT_CREATE, STUDENT_READ, STUDENT_UPDATE,
                CLASS_READ, CLASS_UPDATE,
                PAYMENT_CREATE, PAYMENT_READ,
                FEE_READ
            );
        }
    },

    ACCOUNTS_MANAGER {
        @Override
        public Set<Authority> getAuthorities() {
            return EnumSet.of(
                FEE_MANAGE,
                PAYMENT_MANAGE,
                STUDENT_READ,
                REPORT_FINANCIAL
            );
        }
    },

    AUDITOR {
        @Override
        public Set<Authority> getAuthorities() {
            return EnumSet.of(
                STUDENT_READ,
                CLASS_READ,
                FEE_READ,
                PAYMENT_READ,
                AUDIT_READ,
                REPORT_GENERATE
            );
        }
    }
}
```

### Usage Examples

#### Controller-Level Security

```java
@RestController
@RequestMapping("/api/students")
@PreAuthorize("isAuthenticated()")  // Require authentication for all methods
public class StudentController extends BaseController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('STUDENT_READ', 'STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<Page<StudentDTO>>> getAllStudents(
            @Valid PageableRequest pageableRequest) {
        // Implementation
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<ApiResponse<StudentDTO>> createStudent(
            @Valid @RequestBody StudentDTO dto) {
        // Implementation
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        // Only admins can delete students
    }
}
```

#### Method-Level Security

```java
@Service
public class StudentService implements BaseService<StudentDTO, Long> {

    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    @PostAuthorize("returnObject != null")
    public StudentDTO create(StudentDTO dto) {
        // Implementation
    }

    @PreAuthorize("hasAuthority('STUDENT_UPDATE') and #dto.id == authentication.principal.id")
    public StudentDTO update(Long id, StudentDTO dto) {
        // Users can only update their own profile (if student user)
    }
}
```

#### Dynamic Authorization

```java
@Service
public class SecurityService {

    public boolean canAccessStudent(Authentication authentication, Long studentId) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        // Admin can access all
        if (hasRole(user, "ADMIN")) {
            return true;
        }

        // Office staff can access students in their assigned classes
        if (hasRole(user, "OFFICE_STAFF")) {
            return isAssignedToStudent(user, studentId);
        }

        // Auditors can only read
        return hasAuthority(user, "STUDENT_READ");
    }
}

// Usage in controller
@GetMapping("/{id}")
@PreAuthorize("@securityService.canAccessStudent(authentication, #id)")
public ResponseEntity<ApiResponse<StudentDTO>> getStudent(@PathVariable Long id) {
    // Implementation
}
```

## Security Filters

### 1. RateLimitingFilter

**Purpose**: Prevent brute force attacks

**Features**:
- Tracks failed login attempts per username
- Locks account after 5 failed attempts
- 30-minute lockout duration
- Redis-based for distributed systems

**Configuration**:
```java
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;
}
```

**Usage**:
```java
// Record failed attempt
rateLimitingFilter.recordFailedAttempt(username);

// Reset after successful login
rateLimitingFilter.resetFailedAttempts(username);

// Check lockout status
boolean isLocked = rateLimitingFilter.isAccountLocked(username);
```

### 2. JwtAuthenticationFilter

**Purpose**: Authenticate requests using JWT tokens

**Features**:
- Extracts JWT from Authorization header
- Validates token signature and expiration
- Loads user details from database
- Sets authentication in SecurityContext

**Public Endpoints** (bypassed):
- `/api/auth/**`
- `/api/public/**`
- `/swagger-ui/**`
- `/actuator/health`

## Password Security

### BCrypt Hashing

**Configuration**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // 12 rounds
}
```

**Strength**: 12 rounds = 2^12 = 4,096 iterations

**Encoding Time**: ~250ms (intentionally slow to resist brute force)

### Password Requirements

```java
public class PasswordPolicy {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public boolean validatePassword(String password) {
        return password.length() >= MIN_LENGTH &&
               password.length() <= MAX_LENGTH &&
               UPPERCASE.matcher(password).find() &&
               LOWERCASE.matcher(password).find() &&
               DIGIT.matcher(password).find() &&
               SPECIAL_CHAR.matcher(password).find();
    }
}
```

**Requirements**:
- ✅ Minimum 8 characters
- ✅ At least one uppercase letter
- ✅ At least one lowercase letter
- ✅ At least one digit
- ✅ At least one special character

### Password Storage

```java
// DO NOT store passwords in plain text
❌ user.setPassword("MyPassword123");

// Always hash passwords before storing
✅ String hashedPassword = passwordEncoder.encode(rawPassword);
✅ user.setPassword(hashedPassword);

// Verify passwords during login
boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
```

## CORS Configuration

### Configuration

```yaml
application:
  security:
    cors:
      # Allowed origins (frontend URLs)
      allowed-origins: http://localhost:3000,http://localhost:4200,https://school.example.com

      # Allowed HTTP methods
      allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS

      # Allowed request headers
      allowed-headers: "*"

      # Headers exposed to frontend
      exposed-headers: Authorization,X-Total-Count,X-Page-Number

      # Allow credentials (cookies, authorization headers)
      allow-credentials: true

      # Preflight cache duration (seconds)
      max-age: 3600
```

### Implementation

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
}
```

### Testing CORS

```bash
# Preflight request (OPTIONS)
curl -X OPTIONS http://localhost:8080/api/students \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type, Authorization"

# Expected response headers:
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## Security Headers

### Configured Headers

1. **HTTP Strict Transport Security (HSTS)**
   ```
   Strict-Transport-Security: max-age=31536000; includeSubDomains
   ```
   - Forces HTTPS for 1 year
   - Includes all subdomains

2. **X-Frame-Options**
   ```
   X-Frame-Options: DENY
   ```
   - Prevents clickjacking attacks
   - Page cannot be embedded in iframe

3. **X-Content-Type-Options**
   ```
   X-Content-Type-Options: nosniff
   ```
   - Prevents MIME sniffing
   - Forces declared content type

4. **X-XSS-Protection**
   ```
   X-XSS-Protection: 1; mode=block
   ```
   - Enables XSS filter in browser
   - Blocks page if attack detected

5. **Content Security Policy (CSP)**
   ```
   Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'
   ```
   - Restricts resource loading
   - Prevents XSS attacks

6. **Referrer-Policy**
   ```
   Referrer-Policy: strict-origin-when-cross-origin
   ```
   - Controls referrer information
   - Protects user privacy

### Implementation

```java
http.headers(headers -> headers
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)
    )
    .frameOptions(frame -> frame.deny())
    .contentTypeOptions(contentType -> {})
    .xssProtection(xss -> xss.headerValue("1; mode=block"))
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'")
    )
    .referrerPolicy(referrer -> referrer
        .policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
    )
);
```

## Rate Limiting

### Configuration

```yaml
application:
  security:
    rate-limit:
      enabled: true
      max-attempts: 5
      lockout-duration: 30  # minutes
      attempt-ttl: 60       # minutes (auto-reset)
```

### Redis Keys

```
login:attempts:{username}  → Counter (failed attempts)
login:lockout:{username}   → Timestamp (lockout end time)
```

### Implementation

```java
// Record failed login attempt
public void recordFailedAttempt(String username) {
    String key = "login:attempts:" + username;
    Long attempts = redisTemplate.opsForValue().increment(key);

    redisTemplate.expire(key, 1, TimeUnit.HOURS);

    if (attempts >= MAX_ATTEMPTS) {
        lockAccount(username);
    }
}

// Lock account
private void lockAccount(String username) {
    String key = "login:lockout:" + username;
    LocalDateTime lockoutEnd = LocalDateTime.now().plusMinutes(30);

    redisTemplate.opsForValue().set(key, lockoutEnd.toString(), 30, TimeUnit.MINUTES);
}

// Check if locked
public boolean isAccountLocked(String username) {
    String key = "login:lockout:" + username;
    return redisTemplate.hasKey(key);
}
```

### Testing

```bash
# Simulate failed attempts
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test@example.com","password":"wrong"}'
done

# 6th attempt should return 423 Locked
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"correct"}'

# Expected response:
{
  "status": 423,
  "title": "Account Locked",
  "detail": "Account 'test@example.com' is locked due to 5 failed login attempts",
  "lockoutEndTime": "2025-11-11T11:30:00",
  "remainingMinutes": 29
}
```

## Best Practices

### 1. Authentication

- ✅ Use HTTPS in production
- ✅ Implement token refresh mechanism
- ✅ Set reasonable token expiration (15 minutes for access, 7 days for refresh)
- ✅ Store tokens securely (HttpOnly cookies for web, secure storage for mobile)
- ✅ Validate tokens on every request
- ✅ Implement logout with token revocation

### 2. Authorization

- ✅ Use principle of least privilege
- ✅ Implement both role-based and permission-based access control
- ✅ Check authorization at multiple levels (controller, service, data)
- ✅ Use @PreAuthorize and @PostAuthorize
- ✅ Validate ownership (users can only access their own data)

### 3. Passwords

- ✅ Use BCrypt with at least 10 rounds (we use 12)
- ✅ Enforce strong password policy
- ✅ Never log or display passwords
- ✅ Implement password reset with secure tokens
- ✅ Force password change on first login

### 4. Error Handling

- ✅ Don't expose sensitive information in error messages
- ✅ Use generic messages for authentication failures
- ✅ Log security events (failed logins, lockouts)
- ✅ Implement proper error responses (RFC 7807)

### 5. Monitoring & Auditing

- ✅ Log all authentication attempts
- ✅ Log authorization failures
- ✅ Monitor for suspicious activity
- ✅ Implement audit trail for sensitive operations
- ✅ Set up alerts for security events

### 6. Configuration

- ✅ Use environment variables for secrets
- ✅ Never commit secrets to version control
- ✅ Use different secrets for different environments
- ✅ Rotate secrets regularly
- ✅ Keep dependencies up to date

---

**Last Updated**: 2025-11-11
**Version**: 1.0.0
**Author**: School Management System Development Team
