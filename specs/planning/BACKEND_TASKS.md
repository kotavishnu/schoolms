# Backend Implementation Plan - School Management System

## Overview
Sequential task checklist for Spring Boot Developer to implement Student Service (8081) and Configuration Service (8082) microservices following DDD layered architecture.

**Execution Model:** Waterfall / Single-Pass Implementation
**Technology Stack:** Java 21, Spring Boot 3.5.0, PostgreSQL 18+, Drools 9.44.0.Final
**Source of Truth:** `@specs/architecture/05-backend-implementation-guide.md`, `@specs/sms_api_specification.yaml`

---

## Student Service Implementation (Port 8081)

### Phase 1: Project Setup & Infrastructure

#### [BE-001] Initialize Student Service Project
**Goal:** Create Spring Boot 3.5.0 project with all required dependencies.

**Technical Details:**
- Use Spring Initializr or Maven archetype
- Group ID: `com.school`
- Artifact ID: `student-service`
- Package: `com.school.student`
- Java Version: 21

**Dependencies (pom.xml):**
```xml
- spring-boot-starter-web (3.5.0)
- spring-boot-starter-data-jpa (3.5.0)
- spring-boot-starter-validation
- spring-boot-starter-actuator
- spring-boot-starter-cache
- postgresql (runtime)
- drools-core (9.44.0.Final)
- drools-compiler (9.44.0.Final)
- mapstruct (1.5.x)
- mapstruct-processor (1.5.x - annotation processor)
- lombok (1.18.x)
- springdoc-openapi-starter-webmvc-ui (2.7.x) [CRITICAL: Match Spring Boot 3.5.0 per D-001]
- micrometer-tracing-bridge-brave
- testcontainers-postgresql (test scope)
```

**Dependencies:** None

**Acceptance Criteria:**
- [ ] Project builds successfully with `mvn clean install`
- [ ] Application starts without errors
- [ ] All dependencies resolve correctly
- [ ] SpringDoc OpenAPI version is 2.7.x (compatibility verified)

---

#### [BE-002] Configure Application Properties
**Goal:** Set up database connection, server port, and logging configuration.

**Technical Details:**
Create `application.yml` with:
- Server port: 8081
- Database URL: `jdbc:postgresql://localhost:5432/student_db`
- HikariCP connection pool (max 10 connections)
- JPA DDL: `validate` (schema managed by SQL script)
- Logging: JSON format with correlation ID
- Actuator endpoints: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] Application connects to PostgreSQL database successfully
- [ ] Actuator health endpoint returns UP status
- [ ] Logs output in structured JSON format
- [ ] Connection pool initializes with 10 max connections

---

#### [BE-003] Create Database Schema
**Goal:** Execute `school_management.sql` to create students and enrollments tables.

**Technical Details:**
- Run SQL script from `@specs/planning/school_management.sql`
- Verify tables: `students`, `enrollments`
- Verify constraints: age check (3-18), mobile uniqueness, status enum
- Test optimistic locking with `version` column

**Dependencies:** BE-002

**Acceptance Criteria:**
- [ ] Students table created with all columns and constraints
- [ ] Enrollments table created with foreign key to students
- [ ] CHECK constraint for age validation (3-18 years) is active
- [ ] UNIQUE constraint on mobile column enforced
- [ ] Sample INSERT passes validation

---

### Phase 2: Domain Layer (Business Logic)

#### [BE-004] Create Domain Entities
**Goal:** Implement rich domain models with business logic.

**Technical Details:**
Create classes in `com.school.student.domain.model`:
- `Student.java` - Rich domain model with factory methods
  - Methods: `register()`, `updateProfile()`, `deactivate()`, `getAge()`
  - Enforce business rules within domain methods
- `Enrollment.java` - Enrollment entity
- Value Objects: `Mobile.java`, `GuardianInfo.java`, `StudentId.java`

**Dependencies:** BE-003

**Acceptance Criteria:**
- [ ] `Student.register()` factory method validates age and creates student
- [ ] `Student.updateProfile()` throws exception if student is INACTIVE
- [ ] `Mobile` value object validates 10-digit format
- [ ] Value objects are immutable (final fields, no setters)
- [ ] Domain models have NO JPA annotations (pure business logic)

---

#### [BE-005] Create Repository Interfaces
**Goal:** Define repository contracts in domain layer.

**Technical Details:**
Create interfaces in `com.school.student.domain.repository`:
- `StudentRepository.java`:
  - `Student save(Student student)`
  - `Optional<Student> findByStudentId(String studentId)`
  - `Page<Student> findByLastNameContaining(String lastName, Pageable pageable)`
  - `boolean existsByMobile(Mobile mobile)`
  - `boolean existsByMobileAndIdNot(Mobile mobile, Long id)`
  - `void deleteById(Long id)`
- `EnrollmentRepository.java` (similar pattern)

**Dependencies:** BE-004

**Acceptance Criteria:**
- [ ] Repository interfaces use domain models (NOT JPA entities)
- [ ] Methods return `Optional<T>` for single results
- [ ] Pagination support with Spring Data `Pageable`
- [ ] No implementation code (interface only)

---

### Phase 3: Infrastructure Layer (Persistence)

#### [BE-006] Create JPA Entities
**Goal:** Map database tables to JPA entities.

**Technical Details:**
Create classes in `com.school.student.infrastructure.persistence.entity`:
- `StudentEntity.java`:
  - All fields from students table (snake_case column names)
  - `@Version` annotation on `version` column
  - `@PrePersist` and `@PreUpdate` for audit timestamps
  - `@OneToMany` relationship to enrollments
- `EnrollmentEntity.java` (similar pattern)

**Dependencies:** BE-005

**Acceptance Criteria:**
- [ ] All database columns mapped correctly
- [ ] Optimistic locking enabled with `@Version`
- [ ] Audit fields auto-populate on insert/update
- [ ] Relationships configured (students → enrollments)
- [ ] Entity names match table names exactly

---

#### [BE-007] Implement JPA Repositories
**Goal:** Create Spring Data JPA repository implementations.

**Technical Details:**
Create in `com.school.student.infrastructure.persistence`:
- `JpaStudentRepositoryInterface.java` (extends JpaRepository)
  - Custom queries with `@Query` for search
  - `@EntityGraph` to prevent N+1 queries
- `JpaStudentRepository.java` (adapter implementing domain `StudentRepository`)
  - Delegates to JpaStudentRepositoryInterface
  - Converts between JPA entities and domain models
- `StudentEntityMapper.java` (MapStruct mapper)

**Dependencies:** BE-006

**Acceptance Criteria:**
- [ ] Repository extends `JpaRepository<StudentEntity, Long>`
- [ ] `findByStudentIdWithEnrollments()` uses `@EntityGraph` to prevent N+1
- [ ] Adapter pattern bridges JPA repository to domain repository
- [ ] MapStruct generates entity-domain conversion code

---

#### [BE-008] Configure Drools Rule Engine
**Goal:** Set up Drools for business rule validation.

**Technical Details:**
Create in `com.school.student.infrastructure.drools`:
- `DroolsConfig.java`:
  - `@Bean KieContainer` with rule file loading
  - Load rules from `src/main/resources/rules/student/`
- Create `ValidationResult.java` in `com.school.common`:
  - `addError(field, message, code)` method
  - `isValid()` method
  - List of `ValidationError` objects

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] KieContainer bean initializes successfully
- [ ] Rule files are scanned from classpath
- [ ] ValidationResult can collect multiple errors
- [ ] Drools session can be created from container

---

#### [BE-009] Implement Business Rules (Drools)
**Goal:** Create .drl files for all validation rules.

**Technical Details:**
Create in `src/main/resources/rules/student/`:
- `student-age-rules.drl`:
  - Rule: Student age must be 3-18 years
  - Rule: Date of birth cannot be in future
- `student-uniqueness-rules.drl`:
  - Rule: Mobile number must be unique (BR-2)
  - Rule: Mobile format validation (10 digits)
- `student-required-fields-rules.drl`:
  - Rule: At least one guardian name required (BR-5)

**Rule Template:**
```drools
rule "Student_Validation_AgeRange"
    salience 100
    when
        $student : Student($dob : dateOfBirth != null)
        eval(Period.between($dob, LocalDate.now()).getYears() < 3 ||
             Period.between($dob, LocalDate.now()).getYears() > 18)
        $result : ValidationResult()
    then
        $result.addError("dateOfBirth", "Student age must be between 3 and 18 years", "AGE_OUT_OF_RANGE");
end
```

**Dependencies:** BE-008

**Acceptance Criteria:**
- [ ] All business rules (BR-1 through BR-5) implemented
- [ ] Salience priorities set correctly (100-110 range)
- [ ] Rules reference global `studentRepository` for uniqueness checks
- [ ] Each rule produces specific error code
- [ ] Rules can be tested independently

---

### Phase 4: Application Layer (Service Orchestration)

#### [BE-010] Create DTOs
**Goal:** Define request/response Data Transfer Objects matching OpenAPI spec.

**Technical Details:**
Create in `com.school.student.presentation.dto`:
- `StudentRequestDTO.java`:
  - Fields: firstName, lastName, dateOfBirth, mobile, email, address, fathersName, mothersName, identificationMark, aadhaarNumber
  - Bean Validation annotations: `@NotBlank`, `@Pattern`, `@Past`
  - Immutable (final fields, `@Builder`)
- `StudentResponseDTO.java`:
  - Extends StudentRequestDTO fields
  - Additional: id, studentId, status, version, createdAt, updatedAt
- `StudentUpdateRequestDTO.java`:
  - Fields: firstName, lastName, mobile, status, version
- `ErrorResponseDTO.java`:
  - RFC 7807 Problem Details format
  - Fields: type, title, status, detail, timestamp, correlationId, errors[]

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] DTOs are immutable (final fields)
- [ ] All DTOs have `@Builder` for test data creation
- [ ] Field names match OpenAPI specification exactly (camelCase)
- [ ] Bean Validation annotations match backend validation rules
- [ ] ErrorResponseDTO includes correlation ID field

---

#### [BE-011] Create DTO-Domain Mappers
**Goal:** Use MapStruct for automatic DTO-Domain conversion.

**Technical Details:**
Create in `com.school.student.application.mapper`:
- `StudentMapper.java` (MapStruct interface):
  - `Student toDomain(StudentRequestDTO dto)`
  - `StudentResponseDTO toResponseDTO(Student domain)`
  - Custom mappings for value objects (Mobile, GuardianInfo)
  - `@Named` methods for complex conversions

**Dependencies:** BE-010, BE-004

**Acceptance Criteria:**
- [ ] MapStruct generates implementation at compile time
- [ ] Mobile string converted to Mobile value object
- [ ] GuardianInfo constructed from fathersName/mothersName
- [ ] Mapper is Spring component (`@Mapper(componentModel = "spring")`)
- [ ] No manual mapping code (MapStruct handles all conversions)

---

#### [BE-012] Implement Student Service
**Goal:** Create application service for student registration and CRUD operations.

**Technical Details:**
Create `StudentService.java` in `com.school.student.application.service`:
- `registerStudent(StudentRequestDTO dto)`:
  1. Map DTO to domain model
  2. Execute Drools validation
  3. Throw ValidationException if invalid
  4. Save student
  5. Return StudentResponseDTO
- `getStudentById(String studentId)`
- `updateStudent(String studentId, StudentUpdateRequestDTO dto)`:
  - Check optimistic locking version
  - Use domain `updateProfile()` method
- `deleteStudent(String studentId)`
- `searchStudents(String lastName, StudentStatus status, Pageable pageable)`

**Dependencies:** BE-011, BE-009, BE-007

**Acceptance Criteria:**
- [ ] Service is transactional (`@Transactional` on write methods)
- [ ] Drools validation executed before persistence
- [ ] ValidationException thrown with all rule errors
- [ ] Optimistic locking checked on updates (409 Conflict if mismatch)
- [ ] All methods use mapper for DTO-domain conversion
- [ ] Business logic delegated to domain model methods

---

#### [BE-013] Implement StudentID Auto-Generation
**Goal:** Generate unique student IDs in format STD-YYYYMMDD-NNNN.

**Technical Details:**
Create `StudentIdGenerator.java` in `com.school.student.application.service`:
- Generate format: `STD-{YYYYMMDD}-{sequential 4-digit number}`
- Query existing students for current date to get next sequence
- Thread-safe generation (synchronization or database sequence)

**Dependencies:** BE-007

**Acceptance Criteria:**
- [ ] Student ID format matches: STD-20260203-0001
- [ ] Sequence resets daily
- [ ] Thread-safe generation (no duplicates under concurrent load)
- [ ] Student ID assigned before save
- [ ] Integration test verifies uniqueness

---

### Phase 5: Presentation Layer (REST Controllers)

#### [BE-014] Create Student Controller
**Goal:** Implement REST endpoints matching OpenAPI specification.

**Technical Details:**
Create `StudentController.java` in `com.school.student.presentation`:
- `POST /api/v1/students` - Register student
  - Response: 201 Created with Location header
- `GET /api/v1/students/{studentId}` - Get student details
- `PUT /api/v1/students/{studentId}` - Update student
- `DELETE /api/v1/students/{studentId}` - Delete student
- `GET /api/v1/students` - Search students (with pagination)
  - Query params: lastName, status, page, size, sortBy, sortDirection

**OpenAPI Annotations:**
- `@Tag(name = "Students", description = "Student management operations")`
- `@Operation(summary = "Register a new student")`
- `@ApiResponse` for each status code

**Dependencies:** BE-012

**Acceptance Criteria:**
- [ ] All endpoints respond with correct HTTP status codes
- [ ] POST returns 201 Created with Location header
- [ ] Pagination works with Spring Data Pageable
- [ ] Bean Validation triggered on request DTOs (`@Valid`)
- [ ] OpenAPI documentation generated at `/swagger-ui.html`
- [ ] Correlation ID header propagated from request

---

#### [BE-015] Implement Global Exception Handler
**Goal:** Centralize error handling with consistent error responses.

**Technical Details:**
Create `GlobalExceptionHandler.java` in `com.school.student.config`:
- `@RestControllerAdvice` annotation
- Handle exceptions:
  - `ValidationException` → 400 Bad Request
  - `StudentNotFoundException` → 404 Not Found
  - `OptimisticLockException` → 409 Conflict
  - `ConstraintViolationException` → 400 Bad Request (Bean Validation)
  - `Exception` → 500 Internal Server Error
- Return `ErrorResponseDTO` with correlation ID from MDC

**Dependencies:** BE-010

**Acceptance Criteria:**
- [ ] All exceptions return RFC 7807 Problem Details format
- [ ] Correlation ID included in all error responses
- [ ] Validation errors list all field violations
- [ ] Stack traces NOT exposed in production
- [ ] HTTP status codes match error type

---

#### [BE-016] Add Correlation ID Interceptor
**Goal:** Generate and propagate correlation IDs for request tracing.

**Technical Details:**
Create `CorrelationInterceptor.java` in `com.school.student.config`:
- Implement `HandlerInterceptor`
- `preHandle()`: Extract or generate X-Correlation-ID header, add to MDC
- `afterCompletion()`: Clear MDC
- Register interceptor in WebMvcConfigurer

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] Correlation ID extracted from X-Correlation-ID header if present
- [ ] UUID generated if header missing
- [ ] Correlation ID added to SLF4J MDC for logging
- [ ] Correlation ID included in all log entries
- [ ] MDC cleared after request completion

---

### Phase 6: Cross-Cutting Concerns

#### [BE-017] Configure CORS
**Goal:** Allow cross-origin requests from frontend (localhost:3000).

**Technical Details:**
Create `CorsConfig.java` in `com.school.student.config`:
- Implement `WebMvcConfigurer`
- Allow origins: `http://localhost:3000`
- Allow methods: GET, POST, PUT, DELETE
- Allow credentials: true
- Allow headers: X-Correlation-ID, Content-Type

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] Frontend at localhost:3000 can make API calls
- [ ] Preflight OPTIONS requests succeed
- [ ] CORS headers present in responses
- [ ] Credentials allowed for cookie-based auth (Phase 2)

---

#### [BE-018] Configure Redis Caching (Configuration Service Only)
**Goal:** Enable caching for configuration retrieval.

**Technical Details:**
Create `CacheConfig.java` in `com.school.config.config`:
- `@EnableCaching` annotation
- Configure RedisCacheManager with 5-minute TTL
- Apply `@Cacheable` to ConfigurationService.getGroupedSettings()
- Apply `@CacheEvict` to update/delete methods

**Dependencies:** BE-001 (for Configuration Service)

**Acceptance Criteria:**
- [ ] Redis connection established on startup
- [ ] Configuration queries cached for 5 minutes
- [ ] Cache invalidated on configuration updates
- [ ] Cache hit/miss logged for monitoring
- [ ] Fallback to database if Redis unavailable

---

#### [BE-019] Configure Actuator Endpoints
**Goal:** Expose health and metrics endpoints for monitoring.

**Technical Details:**
Add to `application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  endpoint:
    health:
      show-details: always
```

Create custom metrics in `StudentMetrics.java`:
- Counter: `students.registered.total`
- Gauge: `students.active.count`

**Dependencies:** BE-001

**Acceptance Criteria:**
- [ ] `/actuator/health` returns UP status with database connectivity
- [ ] `/actuator/metrics` shows JVM metrics
- [ ] `/actuator/prometheus` exposes Prometheus-format metrics
- [ ] Custom metrics increment on student registration
- [ ] Metrics include correlation ID tag

---

### Phase 7: Testing

#### [BE-020] Write Unit Tests for Domain Layer
**Goal:** Achieve 95% coverage for domain models and business rules.

**Technical Details:**
Create in `src/test/java/com/school/student/domain/`:
- `StudentTest.java`:
  - Test `Student.register()` with valid/invalid ages
  - Test `Student.updateProfile()` on INACTIVE student (should throw exception)
  - Test `Mobile` value object validation
- `StudentAgeRulesTest.java`:
  - Test Drools age validation rules
  - Test boundary conditions (age 2, 3, 18, 19)

**Dependencies:** BE-004, BE-009

**Acceptance Criteria:**
- [ ] All domain methods tested with JUnit 5
- [ ] Drools rules tested in isolation
- [ ] Edge cases covered (age boundaries, null values)
- [ ] Test coverage ≥ 95% for domain layer
- [ ] AssertJ used for fluent assertions

---

#### [BE-021] Write Integration Tests for API Endpoints
**Goal:** Test complete request-response flow with real database.

**Technical Details:**
Create in `src/test/java/com/school/student/integration/`:
- `StudentControllerIntegrationTest.java`:
  - Use `@SpringBootTest` and `@AutoConfigureMockMvc`
  - Use Testcontainers for PostgreSQL
  - Test POST /api/v1/students (201 Created)
  - Test GET /api/v1/students/{id} (200 OK)
  - Test PUT with version mismatch (409 Conflict)
  - Test duplicate mobile (400 Bad Request)

**Dependencies:** BE-014, BE-015

**Acceptance Criteria:**
- [ ] Tests run against real PostgreSQL container
- [ ] Database state cleaned between tests
- [ ] All HTTP status codes verified
- [ ] Response body structure validated
- [ ] Correlation ID present in responses
- [ ] Tests pass with `mvn verify`

---

#### [BE-022] Write Unit Tests for Application Services
**Goal:** Test service orchestration logic with mocked dependencies.

**Technical Details:**
Create `StudentServiceTest.java`:
- Mock `StudentRepository`, `KieContainer`, `StudentMapper`
- Test `registerStudent()` with valid/invalid data
- Test optimistic locking in `updateStudent()`
- Use Mockito `@Mock` and `@InjectMocks`

**Dependencies:** BE-012

**Acceptance Criteria:**
- [ ] All service methods tested
- [ ] Drools validation execution verified (mock KieSession)
- [ ] Repository save() called only after validation
- [ ] Mapper methods called for DTO-domain conversion
- [ ] Test coverage ≥ 85% for application layer

---

## Configuration Service Implementation (Port 8082)

### Phase 8: Configuration Service (Parallel to Student Service)

#### [BE-023] Initialize Configuration Service Project
**Goal:** Create Spring Boot 3.5.0 project for configuration management.

**Technical Details:**
Same structure as Student Service:
- Group ID: `com.school`
- Artifact ID: `config-service`
- Package: `com.school.config`
- Port: 8082
- Database: `config_db`

**Dependencies:** None

**Acceptance Criteria:**
- [ ] Project builds successfully
- [ ] Application starts on port 8082
- [ ] Connects to `config_db` database
- [ ] SpringDoc OpenAPI version 2.7.x

---

#### [BE-024] Implement Configuration Domain Model
**Goal:** Create Configuration entity and repository.

**Technical Details:**
- `Configuration.java` (domain model):
  - Fields: id, category, key, value, description, dataType, isEncrypted, version
  - Enum: `ConfigCategory` (GENERAL, ACADEMIC, FINANCIAL)
  - Enum: `DataType` (STRING, NUMBER, BOOLEAN, JSON)
- `ConfigurationRepository.java` (interface):
  - `List<Configuration> findByCategory(ConfigCategory category)`
  - `Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key)`

**Dependencies:** BE-023

**Acceptance Criteria:**
- [ ] Configuration domain model with business logic
- [ ] Repository interface defined
- [ ] Enums for category and data type
- [ ] Optimistic locking with version field

---

#### [BE-025] Implement Configuration JPA Layer
**Goal:** Create JPA entity and repository implementation.

**Technical Details:**
- `ConfigurationEntity.java` (JPA entity)
- `JpaConfigurationRepository.java` (Spring Data repository)
- `ConfigurationEntityMapper.java` (MapStruct)

**Dependencies:** BE-024

**Acceptance Criteria:**
- [ ] JPA entity maps to configurations table
- [ ] Repository methods use Spring Data JPA
- [ ] MapStruct mapper for entity-domain conversion

---

#### [BE-026] Implement Configuration Service
**Goal:** Create service for CRUD operations with caching.

**Technical Details:**
Create `ConfigurationService.java`:
- `getGroupedSettings(ConfigCategory category)` → Returns Map<String, String>
  - `@Cacheable` with 5-minute TTL
- `createConfiguration(ConfigurationRequestDTO dto)`
- `updateConfiguration(Long id, ConfigurationRequestDTO dto)`
  - `@CacheEvict` to invalidate cache
- `deleteConfiguration(Long id)`

**Dependencies:** BE-025, BE-018

**Acceptance Criteria:**
- [ ] Grouped settings cached in Redis
- [ ] Cache invalidated on updates
- [ ] Service returns configurations by category
- [ ] Optimistic locking enforced

---

#### [BE-027] Create Configuration Controller
**Goal:** Implement REST endpoints for configuration management.

**Technical Details:**
Create `ConfigurationController.java`:
- `GET /api/v1/configurations/grouped/{category}` - Get grouped settings
- `POST /api/v1/configurations` - Create configuration
- `PUT /api/v1/configurations/{id}` - Update configuration
- `DELETE /api/v1/configurations/{id}` - Delete configuration
- `GET /api/v1/configurations` - List all configurations

**Dependencies:** BE-026

**Acceptance Criteria:**
- [ ] All endpoints match OpenAPI specification
- [ ] Grouped endpoint returns Map<String, String>
- [ ] Cache headers set on GET requests
- [ ] OpenAPI documentation generated

---

#### [BE-028] Write Configuration Service Tests
**Goal:** Test configuration service with caching.

**Technical Details:**
- Unit tests for ConfigurationService
- Integration tests for configuration endpoints
- Test cache behavior (hit/miss/eviction)

**Dependencies:** BE-027

**Acceptance Criteria:**
- [ ] Cache hit verified in tests
- [ ] Cache eviction on update verified
- [ ] All CRUD operations tested
- [ ] Test coverage ≥ 85%

---

## Phase 9: Deployment & Documentation

#### [BE-029] Create Dockerfiles
**Goal:** Containerize both microservices.

**Technical Details:**
Create `Dockerfile` in each service root:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `docker-compose.yml`:
- Services: student-db, config-db, student-service, config-service, redis
- Networks: backend network
- Volumes: persist database data

**Dependencies:** BE-022, BE-028

**Acceptance Criteria:**
- [ ] Docker images build successfully
- [ ] Services start with docker-compose up
- [ ] Services communicate with databases
- [ ] Health checks pass in Docker environment
- [ ] Environment variables for DB credentials

---

#### [BE-030] Generate OpenAPI Documentation
**Goal:** Publish interactive API documentation.

**Technical Details:**
- Verify SpringDoc generates spec at `/v3/api-docs`
- Verify Swagger UI at `/swagger-ui.html`
- Add examples to `@Schema` annotations
- Add descriptions to all endpoints

**Dependencies:** BE-014, BE-027

**Acceptance Criteria:**
- [ ] API documentation accessible at /swagger-ui.html
- [ ] All endpoints documented with examples
- [ ] Request/response schemas show validation rules
- [ ] Try-it-out feature works for all endpoints
- [ ] OpenAPI JSON matches sms_api_specification.yaml

---

## Completion Checklist

### Student Service (Port 8081)
- [ ] All CRUD endpoints functional
- [ ] Drools validation active for all business rules
- [ ] StudentID auto-generation working
- [ ] Optimistic locking prevents concurrent update conflicts
- [ ] Correlation IDs in logs and responses
- [ ] Unit test coverage ≥ 85%
- [ ] Integration tests pass
- [ ] Docker container builds and runs
- [ ] OpenAPI documentation accessible

### Configuration Service (Port 8082)
- [ ] All CRUD endpoints functional
- [ ] Redis caching operational with 5-minute TTL
- [ ] Grouped settings endpoint returns correct format
- [ ] Cache invalidation on updates working
- [ ] Docker container builds and runs
- [ ] OpenAPI documentation accessible

### Cross-Cutting
- [ ] CORS enabled for frontend (localhost:3000)
- [ ] Global exception handler returns RFC 7807 errors
- [ ] Actuator endpoints expose health and metrics
- [ ] Database schema matches DDL script
- [ ] No hardcoded credentials (environment variables used)
- [ ] All services start successfully with docker-compose

---

**Total Tasks:** 30
**Estimated Effort:** 60-80 developer hours
**Critical Path:** BE-001 → BE-003 → BE-004 → BE-012 → BE-014 → BE-021

---

**Document Version:** 1.0
**Last Updated:** 2026-02-03
**Owner:** Technical SDLC Planner Agent
