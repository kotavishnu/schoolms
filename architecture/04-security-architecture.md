# Security Architecture
**School Management System - Phase 1**

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication Strategy](#authentication-strategy)
3. [Authorization Model](#authorization-model)
4. [Input Validation](#input-validation)
5. [Protection Against Common Attacks](#protection-against-common-attacks)
6. [Data Protection](#data-protection)
7. [Security Headers](#security-headers)
8. [Audit and Compliance](#audit-and-compliance)

---

## Overview

### Security Principles

1. **Defense in Depth**: Multiple layers of security controls
2. **Least Privilege**: Minimal permissions required
3. **Fail Securely**: Default deny, explicit allow
4. **Secure by Default**: Security enabled out-of-the-box
5. **Never Trust User Input**: Validate everything

### Phase 1 Security Scope

**In Scope**:
- Input validation (prevent injection attacks)
- CORS configuration (prevent unauthorized access)
- SQL injection prevention
- XSS prevention
- CSRF protection (future)
- Secure password handling (future auth integration)

**Out of Scope (External System)**:
- User authentication (handled by external system in Phase 2)
- User management (CRUD operations on users)
- Session management
- JWT token generation/validation

---

## Authentication Strategy

### Phase 1: No Authentication

**Rationale**: Authentication is explicitly out of scope for Phase 1 as per requirements. The system assumes authentication is handled by an external gateway or authentication service.

**Current State**:
- All API endpoints are publicly accessible
- No user identity verification
- Suitable for internal network deployment only

**Future Integration Points**:

The architecture is designed to easily integrate authentication in Phase 2:

1. **API Gateway Integration**: External gateway handles authentication
2. **Spring Security Integration**: Add Spring Security dependency
3. **JWT Token Validation**: Validate tokens from external auth provider

---


## Authorization Model

### Phase 1: No Authorization

**Current State**: No role-based access control (RBAC) implemented

**Future Phase 2**: RBAC with hierarchical roles

---

### Phase 2: Role-Based Access Control (Future)

**Role Hierarchy**:

```
SUPER_ADMIN (highest privilege)
    └── ADMIN
        └── STAFF
            └── TEACHER (lowest privilege)
```

**Role Definitions**:

| Role | Description | Permissions |
|------|-------------|-------------|
| **SUPER_ADMIN** | System administrator | Full access to all modules, configuration, user management |
| **ADMIN** | School administrator | Manage students, configurations, view reports |
| **STAFF** | Clerical staff | Create/edit students, view configurations (read-only) |
| **TEACHER** | Teaching staff | View student information (read-only) |

---

### Permission Matrix (Phase 2)

**Student Management**:

| Operation | SUPER_ADMIN | ADMIN | STAFF | TEACHER |
|-----------|-------------|-------|-------|---------|
| View Students | ✅ | ✅ | ✅ | ✅ |
| Create Student | ✅ | ✅ | ✅ | ❌ |
| Edit Student | ✅ | ✅ | ✅ | ❌ |
| Delete Student | ✅ | ✅ | ❌ | ❌ |
| View Statistics | ✅ | ✅ | ✅ | ❌ |

**Configuration Management**:

| Operation | SUPER_ADMIN | ADMIN | STAFF | TEACHER |
|-----------|-------------|-------|-------|---------|
| View Configurations | ✅ | ✅ | ✅ (read-only) | ❌ |
| Create Configuration | ✅ | ✅ | ❌ | ❌ |
| Edit Configuration | ✅ | ✅ | ❌ | ❌ |
| Delete Configuration | ✅ | ❌ | ❌ | ❌ |

---

### Method-Level Security (Phase 2)

```java
@Service
public class StudentApplicationService {

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Student createStudent(CreateStudentRequest request) {
        // Create logic
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    public Student getStudent(String studentId) {
        // Read logic
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(String studentId) {
        // Delete logic
    }
}
```

---

## Input Validation

### Validation Layers

**1. Client-Side Validation** (Frontend):
- Immediate user feedback
- Reduce server load
- NOT a security control (easily bypassed)

**2. Server-Side Validation** (Backend):
- **Authoritative validation**
- Security enforcement
- Business rule validation

---

### Server-Side Validation Strategy

**1. JSR-303 Bean Validation**:

```java
public record CreateStudentRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be 1-50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be 1-50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    String lastName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @NotBlank(message = "Adhaar number is required")
    @Pattern(regexp = "^\\d{12}$", message = "Adhaar number must be exactly 12 digits")
    String adhaarNumber,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    String phone,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500, message = "Address must be 10-500 characters")
    String address,

    @Size(max = 200, message = "Identification marks cannot exceed 200 characters")
    String identificationMarks,

    @NotBlank(message = "Guardian name is required")
    @Size(min = 1, max = 100, message = "Guardian name must be 1-100 characters")
    String guardianName,

    @NotBlank(message = "Mother name is required")
    @Size(min = 1, max = 100, message = "Mother name must be 1-100 characters")
    String motherName
) {}
```

**2. Custom Validators**:

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeRangeValidator.class)
public @interface ValidAge {
    String message() default "Age must be between 3 and 18 years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class AgeRangeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) return false;

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < 3 || age > 18) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Age calculated as %d years, must be 3-18 years", age)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
```

**3. Controller-Level Validation**:

```java
@RestController
@RequestMapping("/api/v1/students")
@Validated
public class StudentController {

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
        @Valid @RequestBody CreateStudentRequest request) {

        // Validation errors automatically handled by Spring
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**4. Global Validation Exception Handler**:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
        MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Request validation failed"
        );
        problem.setType(URI.create("https://api.schoolms.com/problems/validation-error"));
        problem.setTitle("Validation Error");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", MDC.get("traceId"));
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }
}
```

---

### Input Sanitization

**1. Prevent HTML/Script Injection**:

```java
public class InputSanitizer {

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static String sanitizeInput(String input) {
        if (input == null) return null;

        // Remove script tags
        String sanitized = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Remove HTML tags (optional - depends on use case)
        // sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Trim whitespace
        sanitized = sanitized.trim();

        return sanitized;
    }
}
```

**2. Encoding for Output** (Frontend automatically handles this in React):
```typescript
// React automatically escapes values in JSX
<div>{student.firstName}</div>  // Safe - React escapes

// Manual escaping if needed
import DOMPurify from 'dompurify';
const clean = DOMPurify.sanitize(dirtyHTML);
```

---

## Protection Against Common Attacks

### 1. SQL Injection Prevention

**Defense**: Always use prepared statements (parameterized queries)

**Spring Data JPA** (automatically parameterized):
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // SAFE: Query parameters are automatically escaped
    @Query("SELECT s FROM Student s WHERE s.phone = :phone")
    Optional<Student> findByPhone(@Param("phone") String phone);

    // SAFE: Method query derivation
    Optional<Student> findByStudentId(String studentId);
}
```

**BAD Example** (NEVER do this):
```java
// VULNERABLE TO SQL INJECTION - DO NOT USE
String query = "SELECT * FROM students WHERE phone = '" + userInput + "'";
```

**GOOD Example**:
```java
// SAFE: Prepared statement
PreparedStatement stmt = connection.prepareStatement(
    "SELECT * FROM students WHERE phone = ?"
);
stmt.setString(1, userInput);
ResultSet rs = stmt.executeQuery();
```

---

### 2. Cross-Site Scripting (XSS) Prevention

**Defense Layers**:

**1. Frontend (React)**:
- React automatically escapes JSX content
- Never use `dangerouslySetInnerHTML` without sanitization

```typescript
// SAFE: React escapes automatically
<div>{student.firstName}</div>

// UNSAFE: Bypasses React's escaping
<div dangerouslySetInnerHTML={{ __html: student.firstName }} />

// SAFE: Sanitize first
import DOMPurify from 'dompurify';
<div dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(student.firstName) }} />
```

**2. Backend (Spring Boot)**:
- Escape output when rendering server-side templates (not applicable for REST APIs)
- Content-Type headers ensure browser interprets response correctly

**3. HTTP Headers**:
```yaml
# application.yml
server:
  servlet:
    session:
      cookie:
        http-only: true
        secure: true
```

---

### 3. Cross-Site Request Forgery (CSRF) Prevention

**Phase 1**: Not implemented (no authentication, no sensitive state-changing operations without user context)

**Phase 2**: Enable Spring Security CSRF protection

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            );
        return http.build();
    }
}
```

**Frontend** (include CSRF token in requests):
```typescript
// Get CSRF token from cookie
const csrfToken = document.cookie
  .split('; ')
  .find(row => row.startsWith('XSRF-TOKEN='))
  ?.split('=')[1];

// Include in request headers
axios.post('/api/v1/students', studentData, {
  headers: {
    'X-XSRF-TOKEN': csrfToken
  }
});
```

---

### 4. CORS (Cross-Origin Resource Sharing) Protection

**Configuration**:

```java
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("Location")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

**Environment-Specific Configuration**:

```yaml
# application-dev.yml
cors:
  allowed-origins:
    - http://localhost:5173
    - http://localhost:3000
    - http://localhost:4200

# application-prod.yml
cors:
  allowed-origins:
    - https://schoolms.example.com
```

**Security Considerations**:
- Never use `allowedOrigins("*")` with `allowCredentials(true)`
- Explicitly list allowed origins
- Test CORS configuration before deployment

---

### 5. Mass Assignment Prevention

**Defense**: Use separate DTOs for input and output

**BAD Example** (vulnerable):
```java
// User can inject arbitrary fields
@PostMapping
public Student createStudent(@RequestBody Student student) {
    // DANGER: User could set student.status = "ADMIN" or other sensitive fields
    return studentRepository.save(student);
}
```

**GOOD Example** (safe):
```java
// Only accept explicitly defined fields
@PostMapping
public StudentResponse createStudent(@RequestBody CreateStudentRequest request) {
    Student student = studentMapper.toEntity(request);
    student.setStatus(StudentStatus.ACTIVE);  // Set by server, not user
    student = studentRepository.save(student);
    return studentMapper.toResponse(student);
}
```

---

### 6. Insecure Direct Object Reference (IDOR) Prevention

**Phase 1**: Not applicable (no authentication)

**Phase 2**: Implement access control checks

```java
@Service
public class StudentApplicationService {

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Student getStudent(String studentId, Authentication auth) {
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Authorization check: Ensure user has access to this student
        if (!hasAccess(auth, student)) {
            throw new AccessDeniedException("You don't have permission to access this student");
        }

        return student;
    }

    private boolean hasAccess(Authentication auth, Student student) {
        String userSchoolId = auth.getPrincipal().getSchoolId();
        String studentSchoolId = student.getSchoolId();
        return userSchoolId.equals(studentSchoolId);
    }
}
```

---

### 7. Denial of Service (DoS) Prevention

**Rate Limiting** (Phase 2):

Use Spring Cloud Gateway or Bucket4j for rate limiting:

```java
@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(
            100,                          // 100 requests
            Refill.intervally(100, Duration.ofMinutes(1))  // per minute
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
```

**Pagination Limits**:
```java
// Enforce maximum page size
public static final int MAX_PAGE_SIZE = 100;

public Page<Student> listStudents(Pageable pageable) {
    if (pageable.getPageSize() > MAX_PAGE_SIZE) {
        pageable = PageRequest.of(
            pageable.getPageNumber(),
            MAX_PAGE_SIZE,
            pageable.getSort()
        );
    }
    return studentRepository.findAll(pageable);
}
```

---

## Data Protection

### 1. Sensitive Data Handling

**Sensitive Fields**:
- Adhaar Number (PII)
- Email
- Phone
- Address

**Protection Measures**:

**1. Encryption at Rest** (Database):
```sql
-- PostgreSQL: Enable encryption (server-level configuration)
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'
```

**2. Encryption in Transit** (HTTPS):
- All production traffic over HTTPS/TLS
- TLS 1.2+ only
- Strong cipher suites

**3. Masking in Logs**:
```java
@Slf4j
public class StudentApplicationService {

    public Student createStudent(CreateStudentRequest request) {
        // Mask sensitive data in logs
        log.info("Creating student: firstName={}, lastName={}, phone={}",
            request.firstName(),
            request.lastName(),
            maskPhone(request.phone())
        );

        // Business logic
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "******" + phone.substring(phone.length() - 4);
    }
}
```

---

### 2. Password Security (Phase 2)

When authentication is implemented:

**1. Use BCrypt for password hashing**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // 12 rounds
}

// Usage
String hashedPassword = passwordEncoder.encode(plainPassword);
boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
```

**2. Password Policy**:
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 digit
- At least 1 special character

**3. Never log passwords**:
```java
// BAD: Password in logs
log.info("User login: username={}, password={}", username, password);

// GOOD: Never log password
log.info("User login attempt: username={}", username);
```

---

### 3. Secure Configuration

**Externalize Credentials**:

```yaml
# application.yml - NO hardcoded credentials
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

# Use environment variables
# export DB_USER=student_service
# export DB_PASSWORD=secure_password_here
```

**Docker Secrets** (Production):
```yaml
version: '3.9'
services:
  student-service:
    image: student-service:1.0
    environment:
      DB_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password

secrets:
  db_password:
    external: true
```

---

## Security Headers

**HTTP Security Headers Configuration**:

```java
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean =
            new FilterRegistrationBean<>();

        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }
}

public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // Prevent MIME sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // Enable XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // HSTS (HTTPS only)
        httpResponse.setHeader("Strict-Transport-Security",
            "max-age=31536000; includeSubDomains");

        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';");

        chain.doFilter(request, response);
    }
}
```

---

## Audit and Compliance

### Audit Logging

**What to Log**:
- All data modifications (create, update, delete)
- Failed authentication attempts (Phase 2)
- Authorization failures (Phase 2)
- Configuration changes
- Abnormal activity (multiple failed requests)

**Audit Log Format**:
```json
{
  "timestamp": "2026-01-08T10:30:00.123Z",
  "level": "INFO",
  "service": "student-service",
  "traceId": "abc123def456",
  "auditType": "STUDENT_CREATED",
  "userId": "admin@example.com",
  "action": "CREATE",
  "resourceType": "Student",
  "resourceId": "STU-2026-00001",
  "changes": {
    "firstName": "John",
    "lastName": "Doe",
    "status": "ACTIVE"
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Implementation**:
```java
@Slf4j
@Component
public class AuditLogger {

    public void logStudentCreated(Student student, String userId, String ipAddress) {
        Map<String, Object> auditLog = Map.of(
            "auditType", "STUDENT_CREATED",
            "userId", userId,
            "action", "CREATE",
            "resourceType", "Student",
            "resourceId", student.getStudentId(),
            "ipAddress", ipAddress
        );

        log.info("Audit: {}", auditLog);
    }
}
```

---

### Data Retention and Privacy

**Data Retention Policy**:
- Active student records: Retained indefinitely
- Inactive student records: Retained for 7 years after graduation
- Audit logs: Retained for 3 years
- Backups: Retained for 1 year

**GDPR Compliance** (if applicable):
- Right to access: API to retrieve all student data
- Right to erasure: Soft delete or anonymization
- Right to portability: Export student data in JSON/CSV

**Anonymization**:
```java
public void anonymizeStudent(String studentId) {
    Student student = studentRepository.findByStudentId(studentId)
        .orElseThrow();

    student.setFirstName("ANONYMIZED");
    student.setLastName("ANONYMIZED");
    student.setEmail("anonymized@example.com");
    student.setPhone("0000000000");
    student.setAdhaarNumber("000000000000");
    student.setAddress("ANONYMIZED");
    student.setStatus(StudentStatus.INACTIVE);

    studentRepository.save(student);
}
```

---

## Security Checklist

**Pre-Deployment Checklist**:

- [ ] All user inputs validated server-side
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention (React escaping, output encoding)
- [ ] CORS configured with specific origins (no wildcards)
- [ ] HTTPS enabled in production
- [ ] Security headers configured
- [ ] Sensitive data masked in logs
- [ ] Database credentials externalized
- [ ] No hardcoded secrets in code
- [ ] Error messages don't expose sensitive information
- [ ] Pagination limits enforced
- [ ] Audit logging enabled
- [ ] Health check endpoints do not expose sensitive data

**Future (Phase 2)**:
- [ ] Authentication implemented
- [ ] Authorization (RBAC) implemented
- [ ] CSRF protection enabled
- [ ] Password policy enforced
- [ ] Rate limiting configured
- [ ] Session management configured
- [ ] JWT validation implemented

---

## Summary

This security architecture provides:

1. **Input Validation**: Multi-layer validation (client + server)
2. **Injection Prevention**: Parameterized queries, output encoding
3. **CORS Protection**: Environment-based origin whitelist
4. **Data Protection**: Sensitive data masking, encryption in transit
5. **Security Headers**: Standard security headers configured
6. **Audit Logging**: Comprehensive audit trail
7. **Future-Ready**: Architecture supports Phase 2 auth integration

**Key Principle**: Security is a continuous process, not a one-time task. Regular security audits and penetration testing are recommended.

---

**Next Steps**: Proceed to `05-backend-implementation-guide.md` for detailed coding standards and patterns.
