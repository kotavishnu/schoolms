# Backend Implementation Tasks - School Management System

## Overview
This document provides a sequential, atomic task list for building the Student and Configuration microservices using Spring Boot 3.5.0, Java 21, and Domain-Driven Design principles.

**Execution Model:** Single-pass Waterfall implementation (No Sprints)
**Target:** Complete backend implementation for Phase 1

---

## Project Setup Tasks

### [BE-001] Initialize Maven Multi-Module Project
**Goal:** Create the root parent POM and module structure.

**Technical Details:**
- Create `sms-backend/pom.xml` as parent POM
- Define modules: `student-service`, `configuration-service`, `sms-common`
- Configure parent dependencies: Spring Boot 3.5.0, Java 21
- Set up dependency management for common libraries

**Dependencies:** None

---

### [BE-002] Configure Student Service Module
**Goal:** Set up the student-service module with required dependencies.

**Best Practice**:
  - Configure CORS only on the API Gateway, not on individual microservices
  - Never expose microservice ports directly to the frontend

**Technical Details:**
- Create `student-service/pom.xml`
- Add dependencies:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - PostgreSQL Driver
  - Spring Boot Starter Validation
  - Lombok
  - MapStruct 1.5.5
  - Drools 9.44.0
  - Spring Boot Starter Data Redis
  - Spring Boot Starter Actuator
  - Micrometer + Zipkin
- Create `application.yml` with database, Redis, and server configuration (port 8081)

**Dependencies:** BE-001

---

### [BE-003] Configure Configuration Service Module
**Goal:** Set up the configuration-service module with required dependencies.

**Technical Details:**
- Create `configuration-service/pom.xml`
- Add dependencies (similar to BE-002, excluding Drools)
- Create `application.yml` with database, Redis, and server configuration (port 8082)

**Dependencies:** BE-001

---

### [BE-004] Configure Common Module
**Goal:** Create shared utilities and exception classes.

**Technical Details:**
- Create `sms-common/pom.xml`
- Create common exception classes:
  - `ResourceNotFoundException`
  - `BusinessRuleViolationException`
  - `DuplicateResourceException`
- Create utility classes for date handling, validation

**Dependencies:** BE-001

---

## Student Service - Domain Layer

### [BE-005] Create Student Aggregate Root
**Goal:** Implement the Student domain entity with business logic.

**Technical Details:**
- Create `com.school.sms.student.domain.model.Student` class
- Implement factory method: `Student.register()`
- Implement business methods:
  - `updateProfile()` - Updates editable fields only
  - `changeStatus()` - Changes student status
  - `getCurrentAge()` - Calculates current age
- Implement domain invariant validation: `validateAge()`
- Use Lombok @Builder, @Getter

**Dependencies:** BE-002

---

### [BE-006] Create Value Objects
**Goal:** Implement immutable value objects for StudentId and Mobile.

**Technical Details:**
- Create `StudentId` value object with format validation (STU-YYYY-XXXXX)
- Create `Mobile` value object with phone number validation (10-15 digits)
- Use Lombok @Value for immutability
- Add static factory methods: `of(String value)`

**Dependencies:** BE-002

---

### [BE-007] Create StudentStatus Enum
**Goal:** Define student status enumeration.

**Technical Details:**
- Create `StudentStatus` enum with values: ACTIVE, INACTIVE, GRADUATED, TRANSFERRED

**Dependencies:** BE-002

---

### [BE-008] Create Student Repository Interface
**Goal:** Define domain repository contract (Port).

**Technical Details:**
- Create `com.school.sms.student.domain.repository.StudentRepository` interface
- Define methods:
  - `save(Student)`: Student
  - `findById(Long)`: Optional<Student>
  - `findByStudentId(StudentId)`: Optional<Student>
  - `findByMobile(Mobile)`: Optional<Student>
  - `findAll(Pageable)`: Page<Student>
  - `findByLastNameContaining(String, Pageable)`: Page<Student>
  - `findByFatherNameContaining(String, Pageable)`: Page<Student>
  - `findByStatus(String, Pageable)`: Page<Student>
  - `existsByMobile(Mobile)`: boolean
  - `countByStudentIdStartingWith(String)`: long

**Dependencies:** BE-005, BE-006

---

### [BE-009] Create StudentId Generator Domain Service
**Goal:** Implement domain service for generating unique Student IDs.

**Technical Details:**
- Create `com.school.sms.student.domain.service.StudentIdGenerator` class
- Implement `generateNext()` method:
  - Get current year
  - Count existing students with prefix "STU-YYYY-"
  - Generate format: STU-YYYY-XXXXX (5-digit sequence)
- Mark as @Service

**Dependencies:** BE-006, BE-008

---

### [BE-010] Create Domain Events
**Goal:** Define domain events for audit and future event sourcing.

**Technical Details:**
- Create `StudentRegisteredEvent` with fields: studentId, firstName, lastName, mobile, registeredAt
- Create `StudentStatusChangedEvent` with fields: studentId, oldStatus, newStatus, changedAt
- Use Lombok @Value for immutability

**Dependencies:** BE-006

---

## Student Service - Application Layer

### [BE-011] Create DTOs - Request Objects
**Goal:** Define request DTOs for API operations.

**Technical Details:**
- Create `CreateStudentRequest` with validation annotations:
  - @NotBlank for firstName, lastName, address, mobile, fatherName
  - @NotNull for dateOfBirth
  - @Pattern for mobile format
  - @Email for email
  - @Size constraints
- Create `UpdateStudentRequest` with firstName, lastName, mobile, status, version
- Create `UpdateStatusRequest` with status, version
- Create `StudentSearchCriteria` with lastName, guardianName, status filters

**Dependencies:** BE-002

---

### [BE-012] Create DTOs - Response Objects
**Goal:** Define response DTOs for API operations.

**Technical Details:**
- Create `StudentResponse` with all student fields including calculated age
- Create `StudentPageResponse` with content list and pagination metadata
- Use Lombok @Data

**Dependencies:** BE-002

---

### [BE-013] Create Custom Validators
**Goal:** Implement custom validation annotations.

**Technical Details:**
- Create `@AgeRange` annotation with min=3, max=18
- Create `AgeRangeValidator` implementing ConstraintValidator
- Validate date of birth falls within age range

**Dependencies:** BE-002

---

### [BE-014] Create MapStruct Mapper Interface
**Goal:** Define DTO-Entity mapping interface.

**Technical Details:**
- Create `com.school.sms.student.application.mapper.StudentMapper` interface
- Add @Mapper annotation with componentModel = "spring"
- Define mapping methods:
  - `toResponse(Student)`: StudentResponse
  - `toPageResponse(Page<Student>)`: StudentPageResponse
- Add custom mappings for StudentId.value, Mobile.number, calculated age

**Dependencies:** BE-005, BE-012

---

### [BE-015] Create StudentService Interface
**Goal:** Define application service contract.

**Technical Details:**
- Create `com.school.sms.student.application.service.StudentService` interface
- Define methods:
  - `createStudent(CreateStudentRequest)`: StudentResponse
  - `getStudentById(Long)`: StudentResponse
  - `getStudentByStudentId(String)`: StudentResponse
  - `searchStudents(StudentSearchCriteria, Pageable)`: StudentPageResponse
  - `updateStudent(Long, UpdateStudentRequest)`: StudentResponse
  - `updateStatus(Long, UpdateStatusRequest)`: StudentResponse
  - `deleteStudent(Long)`: void

**Dependencies:** BE-011, BE-012

---

### [BE-016] Implement StudentService
**Goal:** Implement application service with business orchestration.

**Technical Details:**
- Create `StudentServiceImpl` implementing `StudentService`
- Inject dependencies: StudentRepository, StudentIdGenerator, RulesService, StudentCacheService, StudentMapper
- Implement `createStudent()`:
  - Validate via Drools
  - Check mobile uniqueness
  - Generate StudentId
  - Create Student entity via factory method
  - Save to repository
  - Cache student
  - Return mapped response
- Implement `getStudentByStudentId()`:
  - Check cache first
  - Fetch from DB on cache miss
  - Cache result
- Implement `updateStudent()`:
  - Fetch student
  - Validate mobile uniqueness if changed
  - Update via domain method
  - Invalidate cache
- Implement `updateStatus()`:
  - Fetch student
  - Change status via domain method
  - Invalidate cache
- Implement `deleteStudent()`:
  - Soft delete by setting status to INACTIVE
  - Invalidate cache
- Add @Transactional annotations appropriately
- Add structured logging with SLF4J

**Dependencies:** BE-008, BE-009, BE-014, BE-015

---

## Student Service - Infrastructure Layer

### [BE-017] Create JPA Entity
**Goal:** Create database entity for ORM mapping.

**Technical Details:**
- Create `com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity`
- Map to `students` table
- Define all columns with JPA annotations (@Column, @Id, @GeneratedValue)
- Add @Version for optimistic locking
- Add @PrePersist and @PreUpdate callbacks for timestamps
- Add @Index annotations for last_name, status, father_name

**Dependencies:** BE-002

---

### [BE-018] Create JPA Repository Interface
**Goal:** Define Spring Data JPA repository.

**Technical Details:**
- Create `com.school.sms.student.infrastructure.persistence.repository.JpaStudentRepository`
- Extend `JpaRepository<StudentJpaEntity, Long>`
- Add query methods:
  - `findByStudentId(String)`: Optional<StudentJpaEntity>
  - `findByMobile(String)`: Optional<StudentJpaEntity>
  - `findByLastNameContainingIgnoreCase(String, Pageable)`: Page<StudentJpaEntity>
  - `findByFatherNameContainingIgnoreCase(String, Pageable)`: Page<StudentJpaEntity>
  - `findByStatus(String, Pageable)`: Page<StudentJpaEntity>
  - `existsByMobile(String)`: boolean
- Add @Query for counting by studentId prefix

**Dependencies:** BE-017

---

### [BE-019] Create Repository Adapter
**Goal:** Implement domain repository using JPA repository.

**Technical Details:**
- Create `StudentRepositoryAdapter` implementing `StudentRepository`
- Inject `JpaStudentRepository`
- Implement all domain repository methods
- Add mapping methods:
  - `toJpaEntity(Student)`: StudentJpaEntity
  - `toDomainModel(StudentJpaEntity)`: Student
- Handle value object conversions (StudentId, Mobile)
- Mark as @Component

**Dependencies:** BE-008, BE-018

---

### [BE-020] Configure Drools Rules Engine
**Goal:** Set up Drools for business rule validation.

**Technical Details:**
- Create `DroolsConfig` class with @Configuration
- Create `kieContainer()` bean that loads rules from classpath
- Create `kieSession()` bean for rule execution
- Create `src/main/resources/rules/student-rules.drl` with rules:
  - Validate age range (3-18 years)
  - Validate mobile not empty
  - Add placeholder for class capacity rule (design only, not active)

**Dependencies:** BE-002

---

### [BE-021] Create RulesService
**Goal:** Implement service to execute Drools rules.

**Technical Details:**
- Create `com.school.sms.student.infrastructure.rules.RulesService`
- Inject KieSession
- Implement `validateStudentRegistration(CreateStudentRequest)`:
  - Create validation errors list
  - Set as global in session
  - Insert request object
  - Fire all rules
  - Throw BusinessRuleViolationException if errors exist
- Mark as @Service

**Dependencies:** BE-020

---

### [BE-022] Configure Redis Cache
**Goal:** Set up Redis caching for performance.

**Technical Details:**
- Create `RedisCacheConfig` with @Configuration and @EnableCaching
- Create `cacheManager()` bean with RedisCacheConfiguration:
  - TTL: 5 minutes for students
  - Key serializer: StringRedisSerializer
  - Value serializer: GenericJackson2JsonRedisSerializer

**Dependencies:** BE-002

---

### [BE-023] Create StudentCacheService
**Goal:** Implement caching service for students.

**Technical Details:**
- Create `StudentCacheService` with cache name "students"
- Add methods:
  - `@Cacheable getStudent(String studentId)`: Student
  - `cacheStudent(Student)`: void
  - `@CacheEvict evictStudent(String studentId)`: void
  - `@CacheEvict(allEntries=true) evictAll()`: void
- Mark as @Service

**Dependencies:** BE-022

---

## Student Service - Presentation Layer

### [BE-024] Create REST Controller
**Goal:** Implement REST API endpoints.

**Technical Details:**
- Create `com.school.sms.student.presentation.controller.StudentController`
- Add @RestController and @RequestMapping("/api/v1/students")
- Inject StudentService
- Implement endpoints:
  - POST / - createStudent (@Valid @RequestBody CreateStudentRequest) -> 201 Created
  - GET / - listStudents (@ModelAttribute StudentSearchCriteria, Pageable) -> 200 OK
  - GET /{id} - getStudentById (@PathVariable Long id) -> 200 OK
  - GET /student-id/{studentId} - getStudentByStudentId (@PathVariable String) -> 200 OK
  - PUT /{id} - updateStudent (@PathVariable Long, @Valid @RequestBody UpdateStudentRequest) -> 200 OK
  - PATCH /{id}/status - updateStatus (@PathVariable Long, @Valid @RequestBody UpdateStatusRequest) -> 200 OK
  - DELETE /{id} - deleteStudent (@PathVariable Long) -> 204 No Content
- Add OpenAPI annotations (@Operation, @Tag)
- Add structured logging

**Dependencies:** BE-016

---

### [BE-025] Create Global Exception Handler
**Goal:** Implement centralized exception handling with RFC 7807 Problem Details.

**Technical Details:**
- Create `GlobalExceptionHandler` with @RestControllerAdvice
- Implement exception handlers:
  - `@ExceptionHandler(StudentNotFoundException)` -> 404 Not Found
  - `@ExceptionHandler(DuplicateMobileException)` -> 409 Conflict
  - `@ExceptionHandler(MethodArgumentNotValidException)` -> 400 Bad Request with field errors
  - `@ExceptionHandler(OptimisticLockingFailureException)` -> 409 Conflict
  - `@ExceptionHandler(BusinessRuleViolationException)` -> 400 Bad Request
  - `@ExceptionHandler(Exception)` -> 500 Internal Server Error
- Return ProblemDetail objects with:
  - type: URI to problem type
  - title: Short summary
  - status: HTTP status code
  - detail: Detailed message
  - instance: API endpoint path
  - correlationId: From X-Correlation-ID header
  - timestamp: Current timestamp
  - Additional context fields as needed

**Dependencies:** BE-004, BE-024

---

### [BE-026] Configure OpenAPI Documentation
**Goal:** Set up Swagger/OpenAPI documentation.

**Technical Details:**
- Create `OpenApiConfig` with @Configuration
- Create `@Bean OpenAPI` with:
  - API info (title, version, description, contact)
  - Server URLs (dev: localhost:8081, prod placeholder)
- Add springdoc dependency to POM
- Enable Swagger UI at `/swagger-ui.html`
- Enable API docs at `/api/v1/api-docs`

**Dependencies:** BE-002

---

### [BE-027] Configure CORS and Web Settings
**Goal:** Set up CORS and web MVC configuration.

**Technical Details:**
- Create `WebConfig` implementing WebMvcConfigurer
- Add CORS mappings:
  - Allow origins: http://localhost:5173 (Vite dev server)
  - Allow methods: GET, POST, PUT, PATCH, DELETE
  - Allow headers: Content-Type, X-Correlation-ID
  - Allow credentials: true
- Configure Jackson for date/time formatting

**Dependencies:** BE-002

---

### [BE-028] Add Correlation ID Interceptor
**Goal:** Implement request correlation tracking.

**Technical Details:**
- Create `CorrelationIdInterceptor` implementing HandlerInterceptor
- In `preHandle()`:
  - Extract X-Correlation-ID from request header
  - If not present, generate UUID
  - Add to MDC (Mapped Diagnostic Context) for logging
  - Add to response header
- Register interceptor in WebConfig

**Dependencies:** BE-027

---

## Configuration Service - Implementation

### [BE-029] Create Configuration Domain Entity
**Goal:** Implement Configuration domain model.

**Technical Details:**
- Create `com.school.sms.configuration.domain.model.Configuration` class
- Fields: id, category, configKey, configValue, description, createdAt, updatedAt, version
- Implement validation methods for category enum
- Use Lombok @Builder, @Getter

**Dependencies:** BE-003

---

### [BE-030] Create Configuration Repository Interface
**Goal:** Define domain repository contract.

**Technical Details:**
- Create `com.school.sms.configuration.domain.repository.ConfigurationRepository` interface
- Define methods:
  - `save(Configuration)`: Configuration
  - `findById(Long)`: Optional<Configuration>
  - `findByCategory(String)`: List<Configuration>
  - `findAll(Pageable)`: Page<Configuration>
  - `existsByCategoryAndConfigKey(String, String)`: boolean
  - `deleteById(Long)`: void

**Dependencies:** BE-029

---

### [BE-031] Create Configuration DTOs
**Goal:** Define request and response DTOs.

**Technical Details:**
- Create `CreateConfigRequest` with validation:
  - @NotBlank category, configKey, configValue
  - Category must be one of: General, Academic, Financial, System
- Create `UpdateConfigRequest` with all fields plus version
- Create `ConfigurationResponse` with all fields
- Create `ConfigurationPageResponse` for pagination

**Dependencies:** BE-003

---

### [BE-032] Create Configuration MapStruct Mapper
**Goal:** Define DTO-Entity mapping.

**Technical Details:**
- Create `ConfigurationMapper` interface
- Add @Mapper annotation
- Define mapping methods:
  - `toResponse(Configuration)`: ConfigurationResponse
  - `toPageResponse(Page<Configuration>)`: ConfigurationPageResponse

**Dependencies:** BE-029, BE-031

---

### [BE-033] Create Configuration Service
**Goal:** Implement application service.

**Technical Details:**
- Create `ConfigurationService` interface with CRUD methods
- Create `ConfigurationServiceImpl` implementation:
  - Inject ConfigurationRepository, ConfigurationMapper, CacheService
  - Implement create, read, update, delete operations
  - Cache configurations by category (1 hour TTL)
  - Validate unique constraint (category + key)
  - Add @Transactional annotations

**Dependencies:** BE-030, BE-032

---

### [BE-034] Create Configuration JPA Entity and Repository
**Goal:** Implement persistence layer.

**Technical Details:**
- Create `ConfigurationJpaEntity` mapped to `school_configurations` table
- Create `JpaConfigurationRepository` extending JpaRepository
- Add query methods for category-based retrieval
- Create `ConfigurationRepositoryAdapter` implementing domain repository

**Dependencies:** BE-030

---

### [BE-035] Create Configuration REST Controller
**Goal:** Implement REST API endpoints.

**Technical Details:**
- Create `ConfigurationController` with @RestController
- Base path: `/api/v1/configurations`
- Implement endpoints:
  - POST / - createConfiguration
  - GET / - listConfigurations (paginated)
  - GET /category/{category} - getByCategory
  - GET /{id} - getById
  - PUT /{id} - updateConfiguration
  - DELETE /{id} - deleteConfiguration
- Add OpenAPI annotations
- Add logging

**Dependencies:** BE-033

---

### [BE-036] Create Configuration Exception Handler
**Goal:** Add configuration-specific exception handling.

**Technical Details:**
- Create `ConfigurationExceptionHandler` with @RestControllerAdvice
- Handle ConfigurationNotFoundException -> 404
- Handle DuplicateConfigurationException -> 409
- Return RFC 7807 Problem Details

**Dependencies:** BE-035

---

## Observability and Monitoring

### [BE-037] Configure Actuator Endpoints
**Goal:** Enable health checks and metrics.

**Technical Details:**
- Add actuator dependency to both services
- Configure application.yml to expose endpoints:
  - /actuator/health
  - /actuator/metrics
  - /actuator/prometheus
  - /actuator/info
- Add custom health indicators for database and Redis

**Dependencies:** BE-002, BE-003

---

### [BE-038] Add Custom Metrics
**Goal:** Implement business metrics.

**Technical Details:**
- Create `StudentMetrics` component for student-service:
  - Counter: students.registered.total
  - Gauge: students.active.count
  - Gauge: students.inactive.count
  - Timer: api.response.time
- Inject MeterRegistry
- Increment counters in service methods

**Dependencies:** BE-016

---

### [BE-039] Configure Zipkin Tracing
**Goal:** Enable distributed tracing.

**Technical Details:**
- Add spring-cloud-starter-zipkin dependency
- Configure application.yml:
  - zipkin.base-url: http://localhost:9411
  - spring.sleuth.sampler.probability: 1.0 (dev), 0.1 (prod)
- Test trace propagation with X-Correlation-ID

**Dependencies:** BE-002, BE-003

---

## Testing Setup

### [BE-040] Configure Test Dependencies
**Goal:** Add testing frameworks and tools.

**Technical Details:**
- Add to POM:
  - spring-boot-starter-test
  - junit-jupiter (JUnit 5)
  - mockito-core
  - mockito-junit-jupiter
  - assertj-core
  - testcontainers-postgresql
  - testcontainers-junit-jupiter
  - rest-assured (API testing)
- Create test application.yml with test database config

**Dependencies:** BE-002, BE-003

---

### [BE-041] Write Domain Layer Unit Tests
**Goal:** Test Student aggregate and value objects.

**Technical Details:**
- Create `StudentTest` with tests:
  - Valid student creation
  - Age validation (below 3, above 18)
  - Update profile with editable fields only
  - Status change
- Create `StudentIdTest` with format validation tests
- Create `MobileTest` with format validation tests
- Target: 100% coverage for domain layer

**Dependencies:** BE-005, BE-006, BE-040

---

### [BE-042] Write Service Layer Unit Tests
**Goal:** Test StudentService with mocks.

**Technical Details:**
- Create `StudentServiceImplTest` with @ExtendWith(MockitoExtension)
- Mock: StudentRepository, StudentIdGenerator, RulesService, CacheService, StudentMapper
- Test scenarios:
  - Successful student creation
  - Duplicate mobile exception
  - Student not found exception
  - Update student and cache invalidation
  - Status change
- Use ArgumentCaptor for verification
- Target: 80%+ coverage

**Dependencies:** BE-016, BE-040

---

### [BE-043] Write Drools Rules Tests
**Goal:** Test business rules execution.

**Technical Details:**
- Create `StudentRulesTest`
- Set up KieSession from classpath
- Test scenarios:
  - Valid age passes validation
  - Age below 3 fails
  - Age above 18 fails
  - Empty mobile fails
- Verify validation error messages

**Dependencies:** BE-021, BE-040

---

### [BE-044] Write Repository Integration Tests
**Goal:** Test JPA repository with real database.

**Technical Details:**
- Create `StudentRepositoryTest` with @DataJpaTest
- Use @Testcontainers with PostgreSQL container
- Test:
  - Save and retrieve student
  - Find by studentId
  - Unique mobile constraint violation
  - Search by lastName (partial match)
  - Pagination
- Clean up data after each test

**Dependencies:** BE-019, BE-040

---

### [BE-045] Write REST Controller Integration Tests
**Goal:** Test API endpoints with MockMvc.

**Technical Details:**
- Create `StudentControllerTest` with @WebMvcTest
- Mock StudentService
- Test all endpoints:
  - POST /api/v1/students -> 201
  - GET /api/v1/students -> 200 with pagination
  - GET /api/v1/students/{id} -> 200 or 404
  - PUT /api/v1/students/{id} -> 200
  - DELETE /api/v1/students/{id} -> 204
- Verify request/response JSON structure
- Test validation errors

**Dependencies:** BE-024, BE-040

---

## Build and Deployment

### [BE-046] Configure Logging
**Goal:** Set up structured logging.

**Technical Details:**
- Configure logback-spring.xml:
  - Console appender with pattern including correlation ID
  - File appender with rolling policy
  - JSON format for production
- Set log levels:
  - com.school.sms: INFO
  - org.springframework: WARN
  - org.hibernate.SQL: DEBUG (dev only)

**Dependencies:** BE-002, BE-003

---

### [BE-047] Create Docker Compose for Local Development
**Goal:** Provide easy local environment setup.

**Technical Details:**
- Create `docker-compose.yml` with services:
  - postgres:18 (port 5432) with init script
  - redis:7.2 (port 6379)
  - zipkin (port 9411)
- Create init SQL script to create databases and run schema

**Dependencies:** None

---

### [BE-048] Create Application Profiles
**Goal:** Configure environment-specific properties.

**Technical Details:**
- Create application-dev.yml:
  - H2 or local PostgreSQL
  - Redis localhost
  - Debug logging
- Create application-prod.yml:
  - Production database URLs (from env vars)
  - Redis cluster config
  - Warn/Error logging
- Create application-test.yml for testing

**Dependencies:** BE-002, BE-003

---

### [BE-049] Build and Package Applications
**Goal:** Create executable JARs.

**Technical Details:**
- Configure Maven plugins:
  - spring-boot-maven-plugin for fat JAR
  - maven-compiler-plugin for Java 21
- Run `mvn clean package` for both services
- Verify JARs are created in target/ directories
- Test execution: `java -jar student-service.jar`

**Dependencies:** All previous BE tasks

---

### [BE-050] Create README for Backend
**Goal:** Document setup and execution.

**Technical Details:**
- Create `sms-backend/README.md` with:
  - Prerequisites (Java 21, Maven, Docker)
  - Local setup instructions
  - How to run with Docker Compose
  - How to run services individually
  - API documentation URLs
  - Testing instructions
  - Troubleshooting section

**Dependencies:** BE-047, BE-049

---

## Summary

Total Backend Tasks: 50
- Project Setup: 4 tasks (BE-001 to BE-004)
- Student Service Domain: 6 tasks (BE-005 to BE-010)
- Student Service Application: 7 tasks (BE-011 to BE-017)
- Student Service Infrastructure: 7 tasks (BE-017 to BE-023)
- Student Service Presentation: 5 tasks (BE-024 to BE-028)
- Configuration Service: 8 tasks (BE-029 to BE-036)
- Observability: 3 tasks (BE-037 to BE-039)
- Testing: 6 tasks (BE-040 to BE-045)
- Build and Deployment: 5 tasks (BE-046 to BE-050)

**Estimated Effort:** 4-6 weeks for experienced Spring Boot developer

**Key Technologies:**
- Spring Boot 3.5.0, Java 21
- PostgreSQL 18+, Redis 7.2+
- Drools 9.44.0, MapStruct 1.5.5
- JUnit 5, Mockito, TestContainers
- Zipkin for tracing
- OpenAPI/Swagger for documentation

**Note:** No JWT/OAuth implementation in this phase. No migration tools (Flyway/Liquibase). Database schema created via raw SQL script.
