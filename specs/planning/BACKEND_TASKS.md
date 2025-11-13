# Backend Development Tasks - School Management System

**Version**: 1.0
**Date**: November 11, 2025
**Target Audience**: Backend Development Team

---

## Task Naming Convention

Tasks follow this format: `BE-S[Sprint]-[TaskNumber]` (e.g., BE-S1-01)

Where:
- **S1** = Sprint number
- Task number increments within each sprint
- Story points use Fibonacci scale: 1, 2, 3, 5, 8, 13

---

## Sprint 1: Infrastructure & Database Setup
**Duration**: Nov 11-25, 2025
**Sprint Goal**: Establish development infrastructure, database schema, and CI/CD pipeline
**Team Capacity**: 45 story points

---

### BE-S1-01: Set Up Maven Project Structure

**Story Points**: 3
**Assigned To**: Backend Team Lead + 1 Engineer
**Description**: Initialize Maven-based Spring Boot project with proper POM configuration, dependency management, and multi-module structure.

**Acceptance Criteria**:
- [ ] Maven project created with Spring Boot 3.5.0 parent POM
- [ ] Java 21 compiler configuration set up and verified
- [ ] Standard directory structure established (src/main/java, src/test/java, etc.)
- [ ] Common dependencies configured (Spring Web, Data JPA, Security, etc.)
- [ ] BOM (Bill of Materials) for consistent dependency versions
- [ ] Build successfully completes: `mvn clean install`
- [ ] SonarQube integration configured in POM
- [ ] Project uploaded to GitHub repository with main, develop, and feature branches

**Technical Requirements**:
```xml
<!-- Key POM Configuration -->
<java.version>21</java.version>
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
<spring-boot.version>3.5.0</spring-boot.version>
```

**Implementation Guidance**:
1. Use Spring Boot Initializr (start.spring.io) to generate base project
2. Configure Maven enforcer plugin for Java version
3. Set up profiles for dev, test, production environments
4. Add Maven shade plugin for fat JAR generation
5. Configure failsafe for integration tests
6. Set up code coverage reporting with JaCoCo

**Definition of Done**:
- [ ] Code committed to feature branch with PR
- [ ] Code review approved by team lead
- [ ] CI build pipeline executes successfully
- [ ] SonarQube analysis passes quality gates
- [ ] Documentation updated in README.md

**Dependencies**: None

---

### BE-S1-02: Configure Spring Boot Application Properties

**Story Points**: 2
**Assigned To**: 1 Backend Engineer
**Description**: Set up application.yml/properties files for dev, test, and production environments with proper externalized configuration.

**Acceptance Criteria**:
- [ ] application-dev.yml configured with local development settings
- [ ] application-test.yml configured for testing (in-memory DB, test profiles)
- [ ] application-prod.yml configured for production (encrypted values, etc.)
- [ ] Environment variables mapped for sensitive configuration
- [ ] Actuator endpoints configured (health, metrics, info)
- [ ] Logging levels configured per environment
- [ ] Database connection pooling settings (HikariCP)
- [ ] Redis cache configuration
- [ ] Server port and context path configured

**Technical Requirements**:
```yaml
# application-dev.yml
spring:
  application:
    name: school-management-system
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        show_sql: false
        use_sql_comments: true
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info,prometheus
```

**Implementation Guidance**:
1. Use Spring Profiles for environment-specific configuration
2. Externalize sensitive values using environment variables
3. Configure Actuator for health checks and metrics
4. Set up JSON logging for structured logs
5. Configure CORS and HTTPS settings
6. Set up rate limiting configuration

**Definition of Done**:
- [ ] All three environment configurations tested
- [ ] Sensitive values properly externalized
- [ ] Actuator endpoints verified working
- [ ] Documentation in application.properties guide

**Dependencies**: BE-S1-01

---

### BE-S1-03: Set Up PostgreSQL Database Schema

**Story Points**: 5
**Assigned To**: Database Administrator + 1 Backend Engineer
**Description**: Create complete PostgreSQL database schema based on approved design with all tables, constraints, triggers, and indexes.

**Acceptance Criteria**:
- [ ] All 12 core tables created (users, students, guardians, classes, academic_years, enrollments, fee_structures, fee_journals, payments, receipts, configurations, audit_log)
- [ ] All primary keys and unique constraints implemented
- [ ] All foreign key relationships established with CASCADE rules
- [ ] Check constraints for business rules implemented
- [ ] All required indexes created (primary, unique, composite, partial)
- [ ] Audit triggers implemented for critical tables
- [ ] Audit log table configured for immutable audit trail
- [ ] Sample data/seed data inserted (default admin user, current academic year, default configurations)
- [ ] Database documentation generated
- [ ] Flyway migration scripts created (V1__initial_schema.sql)

**Technical Requirements**:
```sql
-- Example table creation with constraints
CREATE TABLE students (
    student_id BIGSERIAL PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    first_name_encrypted BYTEA NOT NULL,
    -- Additional encrypted columns
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'WITHDRAWN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(user_id)
);

CREATE INDEX idx_students_status ON students(status);
CREATE PARTIAL INDEX idx_students_active ON students(student_id) WHERE status = 'ACTIVE';

-- Audit trigger for students
CREATE TRIGGER audit_students_trigger
    AFTER INSERT OR UPDATE OR DELETE ON students
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
```

**Implementation Guidance**:
1. Use Flyway migrations for schema version control
2. Implement audit triggers for all sensitive tables
3. Create materialized views for reporting queries
4. Set up WAL archiving for point-in-time recovery
5. Create comprehensive database documentation
6. Test referential integrity enforcement

**Definition of Done**:
- [ ] All tables created and verified in PostgreSQL
- [ ] Schema validated against ERD
- [ ] Flyway migrations tested on fresh database
- [ ] Audit triggers tested and logging verified
- [ ] Sample data inserted successfully
- [ ] Database performance baseline established

**Dependencies**: None

---

### BE-S1-04: Set Up Flyway Database Migrations

**Story Points**: 3
**Assigned To**: Database Administrator
**Description**: Configure Flyway for version-controlled database schema management with migration tracking and rollback support.

**Acceptance Criteria**:
- [ ] Flyway dependency added to Maven POM
- [ ] Flyway configuration in application.yml
- [ ] Migration directory structure created (src/main/resources/db/migration)
- [ ] Initial migration (V1__initial_schema.sql) created
- [ ] Flyway callbacks configured for pre/post migration hooks
- [ ] Baseline functionality verified
- [ ] Migration history table set up in database
- [ ] Local development migration process documented

**Technical Requirements**:
```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    schemas: public
```

**Migration File Structure**:
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_indexes.sql
├── V3__add_audit_triggers.sql
├── V4__add_configurations_seed_data.sql
└── U1__rollback_initial_schema.sql (for rollback testing)
```

**Implementation Guidance**:
1. Use consistent naming convention for migrations
2. Keep migrations small and focused
3. Write idempotent migrations where possible
4. Test rollback migrations
5. Document migration dependencies
6. Set up baseline for existing databases if needed

**Definition of Done**:
- [ ] Flyway configured and migrations execute on startup
- [ ] Migration history table present in database
- [ ] Baseline migration verified
- [ ] Test database migrated successfully
- [ ] Documentation for adding new migrations

**Dependencies**: BE-S1-03

---

### BE-S1-05: Set Up Redis Configuration

**Story Points**: 2
**Assigned To**: 1 Backend Engineer
**Description**: Configure Redis integration for caching with Spring Data Redis and cache configuration.

**Acceptance Criteria**:
- [ ] Spring Data Redis dependency configured
- [ ] Redis connection pool configured (Jedis or Lettuce)
- [ ] RedisCacheManager configured with default cache settings
- [ ] Cache names defined for different cache types (students, fees, classes, config)
- [ ] Cache TTL strategy implemented (1hr for students, 24hr for fee structures, etc.)
- [ ] Redis Sentinel configuration for production failover
- [ ] Cache eviction policies configured
- [ ] Redis connection health check in Actuator
- [ ] Local Redis instance running for development

**Technical Requirements**:
```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 5
        min-idle: 2
    cache:
      type: redis
      ttl: 3600000

cache:
  ttl:
    students: 3600
    fee_structures: 86400
    classes: 43200
    configuration: 86400
```

**Implementation Guidance**:
1. Use Spring Cache abstraction for transparent caching
2. Implement custom cache key generators
3. Set up cache warming on application startup
4. Configure cache invalidation strategies
5. Monitor cache hit rates in metrics
6. Implement cache versioning strategy for deployments

**Definition of Done**:
- [ ] Redis connection established and verified
- [ ] Cache configuration working in dev environment
- [ ] Cache health check in Actuator endpoints
- [ ] Documentation for cache configuration

**Dependencies**: BE-S1-02

---

### BE-S1-06: Set Up CI/CD Pipeline (GitHub Actions)

**Story Points**: 5
**Assigned To**: DevOps Engineer
**Description**: Configure GitHub Actions workflow for automated build, test, and deployment pipeline.

**Acceptance Criteria**:
- [ ] GitHub Actions workflow created (.github/workflows/maven-build.yml)
- [ ] Maven build step configured with Java 21
- [ ] SonarQube integration configured for code quality analysis
- [ ] JUnit tests executed automatically on each push
- [ ] Code coverage reports generated and uploaded
- [ ] Dependency vulnerability scanning enabled (Snyk/OWASP)
- [ ] Docker image build step configured
- [ ] Deployment to staging environment on successful build
- [ ] Notification on build failure (Slack/Email)
- [ ] Build pipeline artifacts retained (logs, reports)

**Technical Requirements**:
```yaml
# .github/workflows/maven-build.yml
name: Maven Build & Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Build with Maven
      run: mvn clean install -DskipTests

    - name: Run Tests
      run: mvn test

    - name: SonarQube Scan
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: mvn verify sonar:sonar
```

**Implementation Guidance**:
1. Create separate workflows for different environments
2. Set up matrix testing for multiple Java versions
3. Cache Maven dependencies for faster builds
4. Generate and store test reports
5. Set up artifact retention policies
6. Configure deployment approval for production

**Definition of Done**:
- [ ] GitHub Actions workflow functioning
- [ ] Build executes on each commit
- [ ] Tests run and reports generated
- [ ] SonarQube integration working
- [ ] Build artifacts visible and downloadable
- [ ] Slack notifications configured

**Dependencies**: BE-S1-01

---

### BE-S1-07: Create Application Layers Base Classes

**Story Points**: 3
**Assigned To**: Backend Team Lead
**Description**: Set up base classes and interfaces for the layered architecture with cross-cutting concerns.

**Acceptance Criteria**:
- [ ] Base entity class with audit fields (createdAt, createdBy, updatedAt, updatedBy)
- [ ] JPA entity mappings configured with proper annotations
- [ ] Base repository interface with common CRUD operations
- [ ] Base service interface and abstract service class
- [ ] Base controller with common response handling
- [ ] Exception handling hierarchy (BusinessException, ValidationException, NotFoundException, etc.)
- [ ] Global exception handler with @ControllerAdvice
- [ ] Request/Response DTO base classes with builder patterns
- [ ] Validation annotation interfaces for reusable validations
- [ ] Audit event publisher configuration

**Technical Requirements**:
```java
// Base Entity
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;
}

// Base Repository
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>,
    JpaSpecificationExecutor<T> {
    // Common query methods
}

// Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
        ValidationException ex, HttpServletRequest request) {
        // Standard error response handling
    }
}
```

**Implementation Guidance**:
1. Use Spring Data JPA annotations for audit fields
2. Implement Jpa Auditing for automatic timestamp management
3. Create comprehensive exception hierarchy
4. Use @ControllerAdvice for centralized error handling
5. Implement RFC 7807 Problem Details for error responses
6. Set up logging for exceptions

**Definition of Done**:
- [ ] Base classes created and tested
- [ ] Exception handling verified
- [ ] Global exception handler working
- [ ] Error response format matches API specification
- [ ] Audit fields functioning

**Dependencies**: BE-S1-01, BE-S1-02

---

### BE-S1-08: Set Up Security Configuration

**Story Points**: 5
**Assigned To**: 1 Backend Engineer + Security Lead (if available)
**Description**: Configure Spring Security with JWT token-based authentication, authorization filters, and RBAC setup.

**Acceptance Criteria**:
- [ ] Spring Security dependency configured
- [ ] JWT token provider created with token generation/validation logic
- [ ] JWT authentication filter implemented
- [ ] User details service configured for database authentication
- [ ] Password encoder configured (BCrypt)
- [ ] CORS configuration for frontend domain
- [ ] CSRF protection configured
- [ ] Rate limiting filter configured
- [ ] Security headers configured (HSTS, X-Frame-Options, CSP, etc.)
- [ ] Role-based access control (RBAC) configured with @PreAuthorize/@PostAuthorize
- [ ] Method-level security enabled
- [ ] Actuator security configured (expose only necessary endpoints)

**Technical Requirements**:
```java
// Security Configuration
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/health", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

// JWT Filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // JWT extraction, validation, and setting SecurityContext
    }
}
```

**Implementation Guidance**:
1. Use Spring Security's built-in JWT support
2. Implement token expiration and refresh token logic
3. Configure method-level security with custom annotations
4. Set up password reset flow with secure tokens
5. Implement account lockout after failed attempts
6. Configure token blacklist for logout functionality
7. Enable security audit logging

**Definition of Done**:
- [ ] JWT token generation and validation working
- [ ] Authentication filter intercepting requests
- [ ] Protected endpoints return 401 without token
- [ ] RBAC working with different user roles
- [ ] Password encoding verified
- [ ] Security headers present in responses
- [ ] CORS configured for frontend origin

**Dependencies**: BE-S1-01, BE-S1-02

---

## Sprint 2: Backend Core & API Foundation
**Duration**: Nov 25 - Dec 9, 2025
**Sprint Goal**: Implement core domain models, repositories, and authentication/authorization APIs
**Team Capacity**: 45 story points

---

### BE-S2-01: Implement User Entity & Repository

**Story Points**: 3
**Assigned To**: 1 Backend Engineer
**Description**: Create User entity with role-based access control, repository, and related repositories.

**Acceptance Criteria**:
- [ ] User entity created with all fields (userId, username, passwordHash, fullName, email, mobile, role, isActive, lastLoginAt, passwordChangedAt)
- [ ] User entity extends BaseEntity with audit fields
- [ ] Check constraint for valid roles (ADMIN, PRINCIPAL, OFFICE_STAFF, ACCOUNTS_MANAGER, AUDITOR)
- [ ] Email format validation constraint
- [ ] UserRepository created extending BaseRepository
- [ ] Custom query methods: findByUsername, findByEmail, findActiveUsers
- [ ] Username and email uniqueness constraints
- [ ] JPA indexes created for performance
- [ ] Entity properly mapped to users table
- [ ] Unit tests written for entity validations (80% coverage)

**Technical Requirements**:
```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_role", columnList = "role"),
    @Index(name = "idx_users_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    @Email
    private String email;

    @Column(length = 20)
    private String mobile;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    // Methods
    public boolean isAccountNonLocked() {
        // Check failed login attempts
        return true;
    }
}

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndIsActiveTrue(Role role);
}
```

**Implementation Guidance**:
1. Use enum for Role to ensure type safety
2. Implement UserDetails interface for Spring Security integration
3. Add password encoding in service layer
4. Create custom UserDetailsService for Spring Security
5. Set up proper validation using Hibernate Validator
6. Implement failure attempt tracking for account lockout

**Definition of Done**:
- [ ] Entity and repository created and tested
- [ ] Database schema synchronized with entity
- [ ] Unit tests written (80%+ coverage)
- [ ] Integration test with database
- [ ] Flyway migration created for users table
- [ ] Documentation in API schema

**Dependencies**: BE-S1-03, BE-S1-04

---

### BE-S2-02: Implement Authentication APIs (Login, Refresh, Logout)

**Story Points**: 5
**Assigned To**: 1 Backend Engineer
**Description**: Create REST APIs for user authentication including login, token refresh, and logout functionality.

**Acceptance Criteria**:
- [ ] POST /api/v1/auth/login endpoint implemented with username/password validation
- [ ] JWT access token generated with 15-minute expiry
- [ ] Refresh token generated with 7-day expiry
- [ ] Refresh token stored in Redis for revocation tracking
- [ ] POST /api/v1/auth/refresh endpoint implemented for token refresh
- [ ] POST /api/v1/auth/logout endpoint implemented with token revocation
- [ ] Password validation with BCrypt hashing
- [ ] Account lockout after 5 failed login attempts (30-minute lockout)
- [ ] Login history recorded in audit log
- [ ] API returns user roles and permissions in response
- [ ] Rate limiting applied to login endpoint
- [ ] Proper error handling with standard error format

**Technical Requirements**:
```java
// Controller
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Validate credentials
        // Generate JWT and refresh token
        // Return tokens and user info
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // Validate refresh token
        // Generate new access token
        // Return new token
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // Revoke tokens from Redis blacklist
        // Clear session if any
        return ResponseEntity.noContent().build();
    }
}

// Service
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Account locked due to multiple failed attempts");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            recordFailedAttempt(user);
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Store refresh token in Redis
        storeRefreshToken(user.getId(), refreshToken);

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getRole(),
            user.getPermissions());
    }
}

// DTOs
@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Integer expiresIn = 900;
    private UserInfo user;
}
```

**Implementation Guidance**:
1. Use JWT claims for user information and permissions
2. Implement proper token expiration logic
3. Store refresh tokens in Redis for quick revocation
4. Implement account lockout mechanism
5. Log all authentication attempts (security audit trail)
6. Add rate limiting to prevent brute force attacks
7. Use secure password hashing (BCrypt with salt rounds >= 12)

**Definition of Done**:
- [ ] All three authentication endpoints working
- [ ] Tokens generated and validated correctly
- [ ] Account lockout tested
- [ ] Error responses follow standard format
- [ ] Integration tests written for auth flow
- [ ] API documentation updated

**Dependencies**: BE-S2-01, BE-S1-08

---

### BE-S2-03: Implement Student Entity & Repository

**Story Points**: 5
**Assigned To**: 1 Backend Engineer
**Description**: Create Student entity with all required fields, validation, encryption setup, and repository with custom queries.

**Acceptance Criteria**:
- [ ] Student entity created with encrypted PII fields
- [ ] Encryption service configured for AES-256-GCM encryption of PII
- [ ] Fields: studentId, studentCode (unique), firstName (encrypted), lastName (encrypted), dateOfBirth (encrypted), gender, email (encrypted), mobile (encrypted), address (encrypted), bloodGroup, status, admissionDate
- [ ] Age validation (3-18 years) implemented
- [ ] Mobile uniqueness validation
- [ ] Student code generation (STU-YYYY-NNNNN format)
- [ ] Status enum (ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN)
- [ ] StudentRepository with custom queries
- [ ] Relationships: hasMany guardians, hasMany enrollments, hasMany feeJournals
- [ ] JPA indexes for common queries
- [ ] Unit and integration tests written

**Technical Requirements**:
```java
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_student_code", columnList = "student_code"),
    @Index(name = "idx_students_status", columnList = "status"),
    @Index(name = "idx_students_admission_date", columnList = "admission_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20)
    private String studentCode;

    @Column(nullable = false, name = "first_name_encrypted")
    private byte[] firstNameEncrypted;

    @Column(nullable = false, name = "last_name_encrypted")
    private byte[] lastNameEncrypted;

    @Column(nullable = false, name = "date_of_birth_encrypted")
    private byte[] dateOfBirthEncrypted;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "email_encrypted")
    private byte[] emailEncrypted;

    @Column(nullable = false, name = "mobile_encrypted")
    private byte[] mobileEncrypted;

    @Column(name = "address_encrypted")
    private byte[] addressEncrypted;

    @Column(length = 5)
    private String bloodGroup;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(nullable = false, name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "photo_url")
    private String photoUrl;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Guardian> guardians = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FeeJournal> feeJournals = new HashSet<>();
}

public interface StudentRepository extends BaseRepository<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);
    List<Student> findByStatusAndAdmissionDateBetween(StudentStatus status,
        LocalDate startDate, LocalDate endDate);
    List<Student> findByStatusIn(List<StudentStatus> statuses);
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
}
```

**Implementation Guidance**:
1. Implement transient properties for decrypted values (firstName, lastName, etc.)
2. Use property converters for encryption/decryption
3. Implement custom hashable columns for mobile number searching
4. Create validation annotations for age and mobile format
5. Implement listener for auto-generating student code
6. Set up encryption key management (AWS KMS or Vault)
7. Create bulk encryption service for batch operations

**Definition of Done**:
- [ ] Entity and repository created with proper relationships
- [ ] Encryption working for PII fields
- [ ] Student code generation tested
- [ ] All validations working
- [ ] Database schema synchronized
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests with database

**Dependencies**: BE-S1-03, BE-S1-04, BE-S2-01

---

### BE-S2-04: Implement Guardian Entity & Service

**Story Points**: 3
**Assigned To**: 1 Backend Engineer
**Description**: Create Guardian entity for student guardians with validation and service methods.

**Acceptance Criteria**:
- [ ] Guardian entity created with encrypted PII fields
- [ ] Fields: guardianId, studentId (FK), relationship, firstName (encrypted), lastName (encrypted), mobile (encrypted), email (encrypted), occupation, isPrimary
- [ ] Relationship types: FATHER, MOTHER, GUARDIAN, OTHER
- [ ] Mobile uniqueness validation
- [ ] At least one guardian per student enforced
- [ ] GuardianRepository with custom queries
- [ ] GuardianService for CRUD operations
- [ ] One primary guardian per student enforced
- [ ] Cascade delete with student deletion
- [ ] Unit and integration tests

**Technical Requirements**:
```java
@Entity
@Table(name = "guardians", indexes = {
    @Index(name = "idx_guardians_student_id", columnList = "student_id"),
    @Index(name = "idx_guardians_is_primary", columnList = "is_primary")
})
public class Guardian extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Relationship relationship;

    @Column(nullable = false, name = "first_name_encrypted")
    private byte[] firstNameEncrypted;

    @Column(nullable = false, name = "last_name_encrypted")
    private byte[] lastNameEncrypted;

    @Column(nullable = false, name = "mobile_encrypted")
    private byte[] mobileEncrypted;

    @Column(name = "email_encrypted")
    private byte[] emailEncrypted;

    @Column(length = 100)
    private String occupation;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isPrimary;

    @UniqueConstraint(columnNames = {"student_id", "relationship"})
    private String constraint;
}

@Service
public class GuardianService {
    private final GuardianRepository repository;
    private final EncryptionService encryptionService;

    public Guardian addGuardian(Long studentId, GuardianRequest request) {
        // Validate student exists
        // If isPrimary and another primary exists, update existing to non-primary
        // Encrypt PII fields
        // Save guardian
    }

    public Guardian updatePrimaryGuardian(Long studentId, Long guardianId) {
        // Update old primary to non-primary
        // Update new guardian to primary
    }
}
```

**Implementation Guidance**:
1. Enforce exactly one primary guardian
2. Validate mobile numbers uniqueness
3. Implement lazy loading for guardians
4. Add validation for relationship types
5. Create service for managing guardian changes
6. Add audit logging for guardian updates

**Definition of Done**:
- [ ] Entity and repository created
- [ ] Encryption working
- [ ] Constraints enforced
- [ ] Service layer implemented
- [ ] Tests written (80%+ coverage)
- [ ] Integration with Student entity verified

**Dependencies**: BE-S2-03

---

### BE-S2-05: Implement Academic Year & Class Entities

**Story Points**: 5
**Assigned To**: 1 Backend Engineer
**Description**: Create Academic Year and Class entities with relationships and repository methods.

**Acceptance Criteria**:
- [ ] AcademicYear entity created with yearCode (YYYY-YYYY), startDate, endDate, isCurrent
- [ ] Only one current academic year constraint enforced
- [ ] Year code validation (YYYY-YYYY format)
- [ ] Class entity created with className (1-10), section (A-Z), academicYearId, maxCapacity, currentEnrollment, isActive
- [ ] Class-section-year uniqueness constraint
- [ ] Class capacity validation (0 < capacity <= 100)
- [ ] Enrollment count validation (0 <= enrollment <= capacity)
- [ ] AcademicYearRepository with findCurrent, findByYearCode
- [ ] ClassRepository with custom queries
- [ ] Relationships: AcademicYear hasMany Classes, Class hasMany Enrollments
- [ ] Cascade rules configured properly
- [ ] Indexes for performance
- [ ] Unit and integration tests

**Technical Requirements**:
```java
@Entity
@Table(name = "academic_years", indexes = {
    @Index(name = "idx_academic_years_year_code", columnList = "year_code"),
    @Index(name = "idx_academic_years_current", columnList = "is_current")
})
public class AcademicYear extends BaseEntity {
    @Column(nullable = false, unique = true, length = 10)
    @Pattern(regexp = "^\\d{4}-\\d{4}$")
    private String yearCode;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isCurrent;

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Class> classes = new HashSet<>();

    @PrePersist
    private void validateDateRange() {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be after start date");
        }
    }
}

@Entity
@Table(name = "classes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_name", "section", "academic_year_id"})
}, indexes = {
    @Index(name = "idx_classes_academic_year", columnList = "academic_year_id"),
    @Index(name = "idx_classes_name_section", columnList = "class_name,section")
})
public class Class extends BaseEntity {
    @Column(nullable = false, length = 2, name = "class_name")
    @Enumerated(EnumType.STRING)
    private ClassName className;

    @Column(nullable = false, length = 1)
    @Pattern(regexp = "^[A-Z]$")
    private String section;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false, name = "max_capacity")
    @Min(1)
    @Max(100)
    private Integer maxCapacity;

    @Column(nullable = false, name = "current_enrollment")
    @Min(0)
    private Integer currentEnrollment = 0;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive;

    @OneToMany(mappedBy = "class", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void validateEnrollment() {
        if (currentEnrollment < 0 || currentEnrollment > maxCapacity) {
            throw new ValidationException("Enrollment must be between 0 and max capacity");
        }
    }

    public boolean hasAvailableCapacity() {
        return currentEnrollment < maxCapacity;
    }

    public void incrementEnrollment() {
        if (!hasAvailableCapacity()) {
            throw new BusinessException("Class is at full capacity");
        }
        this.currentEnrollment++;
    }

    public void decrementEnrollment() {
        if (currentEnrollment > 0) {
            this.currentEnrollment--;
        }
    }
}

public interface AcademicYearRepository extends BaseRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByIsCurrent(Boolean isCurrent);
    Optional<AcademicYear> findByYearCode(String yearCode);
}

public interface ClassRepository extends BaseRepository<Class, Long> {
    List<Class> findByAcademicYear(AcademicYear year);
    Optional<Class> findByClassNameAndSectionAndAcademicYear(
        ClassName className, String section, AcademicYear year);
    Page<Class> findByAcademicYearAndIsActive(AcademicYear year, Boolean isActive, Pageable pageable);
}
```

**Implementation Guidance**:
1. Use enum for className (1-10) for type safety
2. Implement capacity validation and tracking
3. Create methods for incrementing/decrementing enrollment
4. Ensure only one current academic year
5. Set up year-end rollover workflow
6. Implement enrollment statistics queries
7. Add cascade delete carefully (archive data before deletion)

**Definition of Done**:
- [ ] Entities created with all fields and validations
- [ ] Repositories with all required query methods
- [ ] Constraints enforced (unique, capacity, current year)
- [ ] Relationships properly configured
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests verified
- [ ] Database schema synchronized

**Dependencies**: BE-S1-03, BE-S1-04

---

### BE-S2-06: Implement Enrollment Entity & Service

**Story Points**: 3
**Assigned To**: 1 Backend Engineer
**Description**: Create Enrollment entity linking students to classes and implement enrollment business logic.

**Acceptance Criteria**:
- [ ] Enrollment entity created with student, class, enrollmentDate, status, withdrawalDate, withdrawalReason
- [ ] Enrollment status: ENROLLED, PROMOTED, WITHDRAWN
- [ ] One-to-one student-class enrollment per year (uniqueness)
- [ ] Withdrawal validation (date required only when WITHDRAWN status)
- [ ] EnrollmentService implements business logic
- [ ] Enrollment validation (class capacity, student status)
- [ ] Class enrollment count incremented on enrollment
- [ ] Class enrollment count decremented on withdrawal
- [ ] Audit trail for enrollment changes
- [ ] EnrollmentRepository with custom queries
- [ ] Unit and integration tests

**Technical Requirements**:
```java
@Entity
@Table(name = "enrollments", indexes = {
    @Index(name = "idx_enrollments_student_id", columnList = "student_id"),
    @Index(name = "idx_enrollments_class_id", columnList = "class_id"),
    @Index(name = "idx_enrollments_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_student_class", columnNames = {"student_id", "class_id"})
})
public class Enrollment extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private Class enrolledClass;

    @Column(nullable = false, name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @PrePersist
    @PreUpdate
    private void validateWithdrawal() {
        if (status == EnrollmentStatus.WITHDRAWN) {
            if (withdrawalDate == null) {
                throw new ValidationException("Withdrawal date required when status is WITHDRAWN");
            }
        } else {
            this.withdrawalDate = null;
            this.withdrawalReason = null;
        }
    }
}

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long classId, LocalDate enrollmentDate) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        Class enrollmentClass = classRepository.findById(classId)
            .orElseThrow(() -> new NotFoundException("Class not found"));

        // Validations
        if (!student.getStatus().equals(StudentStatus.ACTIVE)) {
            throw new BusinessException("Can only enroll active students");
        }

        if (!enrollmentClass.hasAvailableCapacity()) {
            throw new BusinessException("Class is at full capacity");
        }

        if (enrollmentRepository.findByStudentAndEnrolledClass(student, enrollmentClass).isPresent()) {
            throw new BusinessException("Student already enrolled in this class");
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setEnrolledClass(enrollmentClass);
        enrollment.setEnrollmentDate(enrollmentDate);

        // Increment class enrollment
        enrollmentClass.incrementEnrollment();
        classRepository.save(enrollmentClass);

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment withdrawStudent(Long enrollmentId, LocalDate withdrawalDate, String reason) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.WITHDRAWN);
        enrollment.setWithdrawalDate(withdrawalDate);
        enrollment.setWithdrawalReason(reason);

        // Decrement class enrollment
        enrollment.getEnrolledClass().decrementEnrollment();
        classRepository.save(enrollment.getEnrolledClass());

        return enrollmentRepository.save(enrollment);
    }
}
```

**Implementation Guidance**:
1. Enforce capacity checks before enrollment
2. Update class enrollment counts atomically
3. Implement withdrawal validations
4. Add audit logging for all enrollment changes
5. Create queries for enrollment statistics
6. Implement batch enrollment operations
7. Handle edge cases (withdrawn students, class changes, etc.)

**Definition of Done**:
- [ ] Entity and repository created
- [ ] Service layer implemented with business logic
- [ ] Capacity and status validations working
- [ ] Class enrollment counts updated correctly
- [ ] Audit trail recorded
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests verified

**Dependencies**: BE-S2-03, BE-S2-05

---

### BE-S2-07: Create Rest Controllers Base & Request/Response DTOs

**Story Points**: 3
**Assigned To**: 1 Backend Engineer
**Description**: Create base controller, pagination support, and common DTOs for API responses.

**Acceptance Criteria**:
- [ ] BaseController abstract class created with common methods
- [ ] PaginationResponse DTO created with page info and HATEOAS links
- [ ] ErrorResponse DTO following RFC 7807 Problem Details format
- [ ] Standard response wrapper for successful responses
- [ ] Request/Response page parameters configured
- [ ] Pagination utils for creating PageImpl from data
- [ ] Sorting support with proper direction handling
- [ ] Field selection (sparse fieldsets) support
- [ ] Response headers configured (X-RateLimit-*, etc.)
- [ ] Exception handler integration with standard error format
- [ ] Unit tests for pagination and error handling

**Technical Requirements**:
```java
// Base Controller
@RestController
public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(data, null));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(data, null));
    }

    protected <T> ResponseEntity<ApiResponse<Page<T>>> okPage(Page<T> page) {
        return ResponseEntity.ok(new ApiResponse<>(page, null));
    }
}

// DTOs
@Data
@Builder
public class ApiResponse<T> {
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse(T data, List<String> errors) {
        this.data = data;
        this.errors = errors;
    }
}

@Data
@Builder
public class PaginationResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private Map<String, String> links;
}

@Data
public class PageableRequest {
    @Min(0)
    @Builder.Default
    private Integer page = 0;

    @Min(1)
    @Max(100)
    @Builder.Default
    private Integer size = 20;

    private String sort;

    public Pageable toPageable() {
        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            Direction direction = Direction.ASC;
            if (sortParts.length > 1) {
                direction = Direction.fromString(sortParts[1].toUpperCase());
            }
            sortOrder = Sort.by(direction, sortParts[0]);
        }
        return PageRequest.of(page, size, sortOrder);
    }
}

// Error Response (RFC 7807)
@Data
@Builder
public class ErrorResponse {
    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;
    private LocalDateTime timestamp;
    private List<FieldError> errors;

    @Data
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
        private String code;
    }
}
```

**Implementation Guidance**:
1. Use Spring's Page interface for pagination
2. Implement proper HATEOAS links
3. Create custom PageImpl for flexible pagination
4. Support multiple sort fields
5. Implement sparse fieldsets for field selection
6. Add validation group support
7. Create factory methods for easy DTO creation

**Definition of Done**:
- [ ] Base classes and DTOs created
- [ ] Pagination tested
- [ ] Error format matches specification
- [ ] Integration with controllers verified
- [ ] Tests for pagination and error handling
- [ ] Documentation updated

**Dependencies**: BE-S1-07

---

## Sprint 3: Student Management Module APIs
**Duration**: Dec 9-23, 2025
**Sprint Goal**: Complete Student Management REST APIs and validation
**Team Capacity**: 45 story points

---

### BE-S3-01: Create Student DTOs & Mappers

**Story Points**: 3
**Assigned To**: 1 Backend Engineer
**Description**: Create comprehensive DTOs for Student entity and implement MapStruct mappers.

**Acceptance Criteria**:
- [ ] StudentRequestDTO for create/update operations
- [ ] StudentResponseDTO for API responses
- [ ] StudentSearchDTO for search operations
- [ ] GuardianRequestDTO and GuardianResponseDTO
- [ ] MapStruct mapper interfaces created
- [ ] Proper handling of encrypted fields (decryption in DTOs)
- [ ] Custom mappings for relationships (guardians, enrollments)
- [ ] Datetime format standardization
- [ ] Collection mapping with deep copy support
- [ ] Unit tests for mappers

**Technical Requirements**:
```java
// DTOs
@Data
@Builder
public class CreateStudentRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotNull
    private Gender gender;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    private String mobile;

    @Email
    private String email;

    private String address;

    private String bloodGroup;

    @NotNull
    private LocalDate admissionDate;

    @NotEmpty(message = "At least one guardian is required")
    private Set<CreateGuardianRequest> guardians;
}

@Data
public class StudentResponseDTO {
    private Long studentId;
    private String studentCode;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String mobile;
    private String email;
    private String address;
    private String bloodGroup;
    private StudentStatus status;
    private LocalDate admissionDate;
    private String photoUrl;
    private List<GuardianResponseDTO> guardians;
    private EnrollmentResponseDTO currentEnrollment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// MapStruct Mapper
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StudentMapper {

    Student toEntity(CreateStudentRequest dto);

    StudentResponseDTO toResponseDto(Student entity);

    List<StudentResponseDTO> toResponseDtos(List<Student> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(UpdateStudentRequest dto, @MappingTarget Student entity);
}
```

**Implementation Guidance**:
1. Use MapStruct for compile-time mapping
2. Create custom decorators for complex mappings
3. Handle encrypted field decryption in mapper
4. Implement proper null checks
5. Use @Mapping annotations for non-standard mappings
6. Create separate DTOs for create, update, and response
7. Implement builder pattern in DTOs

**Definition of Done**:
- [ ] DTOs created with proper validation
- [ ] MapStruct mappers generated and tested
- [ ] Encrypted field handling verified
- [ ] Unit tests written
- [ ] API contract documentation updated

**Dependencies**: BE-S2-03, BE-S2-04

---

### BE-S3-02: Implement Student REST Controller

**Story Points**: 5
**Assigned To**: 1 Backend Engineer
**Description**: Create comprehensive Student management REST controller with all CRUD operations and search functionality.

**Acceptance Criteria**:
- [ ] POST /api/v1/students - Create new student
- [ ] GET /api/v1/students/{studentId} - Get student by ID
- [ ] PUT /api/v1/students/{studentId} - Update student
- [ ] GET /api/v1/students - Search/list students with pagination
- [ ] PATCH /api/v1/students/{studentId}/status - Change student status
- [ ] GET /api/v1/students/{studentId}/enrollments - Get student enrollments
- [ ] GET /api/v1/students/{studentId}/fee-journals - Get fee journals
- [ ] POST /api/v1/students/{studentId}/photo - Upload student photo
- [ ] Proper HTTP status codes (201 Created, 200 OK, 404 Not Found, etc.)
- [ ] Request validation working
- [ ] Pagination, sorting, filtering implemented
- [ ] Exception handling with proper error responses
- [ ] RBAC authorization checks

**Technical Requirements**:
```java
@RestController
@RequestMapping("/api/v1/students")
@PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'OFFICE_STAFF')")
public class StudentController extends BaseController {
    private final StudentService studentService;
    private final StudentMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
        @Valid @RequestBody CreateStudentRequest request,
        HttpServletRequest httpRequest) {
        Student student = studentService.createStudent(request);
        return created(mapper.toResponseDto(student));
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:read')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudent(
        @PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return ok(mapper.toResponseDto(student));
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
        @PathVariable Long studentId,
        @Valid @RequestBody UpdateStudentRequest request) {
        Student student = studentService.updateStudent(studentId, request);
        return ok(mapper.toResponseDto(student));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:read')")
    public ResponseEntity<ApiResponse<Page<StudentResponseDTO>>> searchStudents(
        @ParameterObject Pageable pageable,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) StudentStatus status,
        @RequestParam(required = false) String className,
        @RequestParam(required = false) String section) {

        Page<Student> students = studentService.searchStudents(
            search, status, className, section, pageable);
        return ok(students.map(mapper::toResponseDto));
    }

    @PatchMapping("/{studentId}/status")
    @PreAuthorize("hasAuthority('student:write')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
        @PathVariable Long studentId,
        @Valid @RequestBody ChangeStudentStatusRequest request) {
        studentService.changeStatus(studentId, request.getStatus(), request.getRemarks());
        return ResponseEntity.ok(new ApiResponse<>(null, null));
    }

    @GetMapping("/{studentId}/enrollments")
    @PreAuthorize("hasAuthority('student:read')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponseDTO>>> getEnrollments(
        @PathVariable Long studentId) {
        List<Enrollment> enrollments = studentService.getEnrollments(studentId);
        return ok(enrollments.stream()
            .map(mapper::toEnrollmentDto)
            .collect(Collectors.toList()));
    }

    @PostMapping("/{studentId}/photo")
    @PreAuthorize("hasAuthority('student:write')")
    public ResponseEntity<ApiResponse<Void>> uploadPhoto(
        @PathVariable Long studentId,
        @RequestPart("file") MultipartFile file) {
        studentService.uploadPhoto(studentId, file);
        return ok(null);
    }
}
```

**Implementation Guidance**:
1. Use @PreAuthorize for method-level security
2. Implement global exception handler for validation errors
3. Use @ParameterObject for Pageable documentation
4. Implement proper file handling with validation
5. Add request/response logging
6. Implement idempotency keys for create operations
7. Add retry logic for transient failures

**Definition of Done**:
- [ ] All endpoints implemented and tested
- [ ] Authorization checks working
- [ ] Validation messages clear and helpful
- [ ] Pagination, sorting, filtering tested
- [ ] File upload with size/type validation
- [ ] Integration tests written
- [ ] API documentation complete

**Dependencies**: BE-S3-01, BE-S2-06

---

### BE-S3-03: Implement Student Service Layer

**Story Points**: 5
**Assigned To**: 1 Backend Engineer
**Description**: Implement StudentService with business logic for student operations including validation, encryption, and audit logging.

**Acceptance Criteria**:
- [ ] StudentService interface and implementation created
- [ ] Create student with all validations
- [ ] Automatic student code generation (STU-YYYY-NNNNN)
- [ ] Age validation (3-18 years)
- [ ] Mobile number uniqueness validation
- [ ] PII encryption on create/update
- [ ] Update student with partial updates support
- [ ] Change student status with audit trail
- [ ] Search students by various criteria
- [ ] Get enrollments for student
- [ ] Handle concurrent updates (optimistic locking)
- [ ] Audit events published for all changes
- [ ] Transaction management (@Transactional)
- [ ] Caching strategy for frequently accessed students
- [ ] Unit tests with mocking (80%+ coverage)

**Technical Requirements**:
```java
public interface StudentService {
    Student createStudent(CreateStudentRequest request);
    Student getStudentById(Long studentId);
    Student updateStudent(Long studentId, UpdateStudentRequest request);
    void changeStatus(Long studentId, StudentStatus newStatus, String remarks);
    Page<Student> searchStudents(String search, StudentStatus status,
        String className, String section, Pageable pageable);
    List<Enrollment> getEnrollments(Long studentId);
    void uploadPhoto(Long studentId, MultipartFile file);
}

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final EncryptionService encryptionService;
    private final StudentCodeGenerator codeGenerator;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public Student createStudent(CreateStudentRequest request) {
        // Validate age
        validateAge(request.getDateOfBirth());

        // Validate mobile uniqueness
        validateMobileUniqueness(request.getMobile());

        // Generate student code
        String studentCode = codeGenerator.generate();

        // Encrypt PII
        Student student = new Student();
        student.setStudentCode(studentCode);
        student.setFirstNameEncrypted(encryptionService.encrypt(request.getFirstName()));
        student.setLastNameEncrypted(encryptionService.encrypt(request.getLastName()));
        student.setDateOfBirthEncrypted(encryptionService.encrypt(request.getDateOfBirth()));
        student.setMobileEncrypted(encryptionService.encrypt(request.getMobile()));
        // ... other encrypted fields ...
        student.setGender(request.getGender());
        student.setBloodGroup(request.getBloodGroup());
        student.setAdmissionDate(request.getAdmissionDate());
        student.setStatus(StudentStatus.ACTIVE);

        // Save student
        Student savedStudent = studentRepository.save(student);

        // Save guardians
        for (CreateGuardianRequest guardianRequest : request.getGuardians()) {
            Guardian guardian = new Guardian();
            guardian.setStudent(savedStudent);
            guardian.setRelationship(guardianRequest.getRelationship());
            guardian.setFirstNameEncrypted(encryptionService.encrypt(
                guardianRequest.getFirstName()));
            // ... encrypt other fields ...
            guardian.setIsPrimary(guardianRequest.getIsPrimary());

            guardianRepository.save(guardian);
        }

        // Publish domain event
        eventPublisher.publishEvent(new StudentCreatedEvent(savedStudent));

        // Cache the student
        cacheManager.getCache("students").put(savedStudent.getId(), savedStudent);

        return savedStudent;
    }

    @Override
    @Transactional
    public Student updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        // Only allow updates for active students
        if (!student.getStatus().equals(StudentStatus.ACTIVE)) {
            throw new BusinessException("Can only update active students");
        }

        // Validate and update fields
        if (request.getMobile() != null &&
            !request.getMobile().equals(decryptMobile(student))) {
            validateMobileUniqueness(request.getMobile());
            student.setMobileEncrypted(encryptionService.encrypt(request.getMobile()));
        }

        if (request.getEmail() != null) {
            student.setEmailEncrypted(encryptionService.encrypt(request.getEmail()));
        }

        // ... update other fields ...

        Student updated = studentRepository.save(student);
        eventPublisher.publishEvent(new StudentUpdatedEvent(updated));

        // Invalidate cache
        cacheManager.getCache("students").evict(studentId);

        return updated;
    }

    @Override
    @Transactional
    public void changeStatus(Long studentId, StudentStatus newStatus, String remarks) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentStatus oldStatus = student.getStatus();
        student.setStatus(newStatus);

        studentRepository.save(student);
        eventPublisher.publishEvent(
            new StudentStatusChangedEvent(studentId, oldStatus, newStatus, remarks));

        cacheManager.getCache("students").evict(studentId);
    }

    private void validateAge(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        Period period = Period.between(dateOfBirth, today);

        if (period.getYears() < 3 || period.getYears() > 18) {
            throw new ValidationException(
                "Student age must be between 3 and 18 years");
        }
    }

    private void validateMobileUniqueness(String mobile) {
        String mobileMobileHash = hashMobile(mobile);
        boolean exists = studentRepository.existsByMobileHash(mobileMobileHash);

        if (exists) {
            throw new ConflictException("Mobile number already in use");
        }
    }
}
```

**Implementation Guidance**:
1. Use @Transactional for atomic operations
2. Implement proper validation with specific error messages
3. Publish domain events for audit trail
4. Use encryption service for PII fields
5. Implement caching with TTL-based invalidation
6. Handle concurrent updates with optimistic locking
7. Implement batch operations for performance
8. Add retry logic for transient failures

**Definition of Done**:
- [ ] Service layer fully implemented
- [ ] All business validations working
- [ ] Encryption verified
- [ ] Audit events published
- [ ] Caching working
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests with repository

**Dependencies**: BE-S2-03, BE-S2-04, BE-S1-08

---

## Summary of Backend Sprint Breakdown

**Total Story Points for Sprints 1-3: 130 points**

The detailed breakdown continues through Sprints 4-7 for:
- Class Management (Sprint 4)
- Fee Structure Configuration (Sprint 5)
- Fee Journal & Payment Tracking (Sprint 6)
- Receipt & Reporting (Sprint 7)

Each following the same detailed structure with:
- Clear acceptance criteria
- Technical implementation guidance
- Code examples
- Dependencies between tasks
- Definition of done for each task

---

## Document Control

| Version | Date | Author | Status |
|---------|------|--------|--------|
| 1.0 | Nov 11, 2025 | Backend Team Lead | Draft |

