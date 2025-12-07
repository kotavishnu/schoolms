# Backend Implementation Task Plan

## Overview
This document provides a sequential, waterfall-style implementation plan for building the School Management System backend microservices. All tasks must be completed in order, as later tasks depend on earlier ones.

**Execution Model:** Single-pass, waterfall implementation (No sprints)
**Target:** Complete both Student Service and Configuration Service
**Tech Stack:** Spring Boot 3.5.0, Java 21, PostgreSQL 18, Spring Data JPA

---

## Phase 1: Project Foundation

### [BE-001] Create Parent POM Structure
**Goal:** Set up multi-module Maven project with parent POM for dependency management

**Technical Details:**
- Create `sms-backend/pom.xml` as parent POM
- Define dependency versions:
  - Spring Boot: 3.5.0
  - Java: 21
  - SpringDoc OpenAPI: 2.7.0 (compatible with Spring Boot 3.5.0)
  - Lombok: 1.18.30
  - MapStruct: 1.5.5.Final
  - PostgreSQL Driver: 42.7.1
- Configure Maven compiler plugin (Java 21)
- Set up modules: student-service, configuration-service

**Dependencies:** None

---

### [BE-002] Create Student Service Module Structure
**Goal:** Initialize student-service module with layered package structure

**Technical Details:**
- Create `student-service/pom.xml` inheriting from parent
- Create package structure:
  - `com.school.sms.student.presentation.controller`
  - `com.school.sms.student.presentation.dto.request`
  - `com.school.sms.student.presentation.dto.response`
  - `com.school.sms.student.presentation.mapper`
  - `com.school.sms.student.application.service`
  - `com.school.sms.student.domain.model`
  - `com.school.sms.student.domain.repository`
  - `com.school.sms.student.domain.exception`
  - `com.school.sms.student.infrastructure.persistence.entity`
  - `com.school.sms.student.infrastructure.persistence.repository`
  - `com.school.sms.student.infrastructure.config`
- Create `src/main/resources/application.yml`
- Create Spring Boot main application class: `StudentServiceApplication.java`

**Dependencies:** BE-001

---

### [BE-003] Create Configuration Service Module Structure
**Goal:** Initialize configuration-service module with layered package structure

**Technical Details:**
- Create `configuration-service/pom.xml` inheriting from parent
- Create package structure (similar to student-service):
  - `com.school.sms.configuration.presentation.*`
  - `com.school.sms.configuration.application.*`
  - `com.school.sms.configuration.domain.*`
  - `com.school.sms.configuration.infrastructure.*`
- Create `src/main/resources/application.yml`
- Create Spring Boot main application class: `ConfigurationServiceApplication.java`

**Dependencies:** BE-001

---

### [BE-004] Configure Student Service Application Properties
**Goal:** Set up database connection, server port, and logging for student-service

**Technical Details:**
- Create `application.yml` with:
  - Server port: 8081
  - Database: sms_student_db
  - HikariCP connection pool settings
  - JPA/Hibernate settings (ddl-auto: validate)
  - Logging configuration
  - SpringDoc OpenAPI settings
  - CORS allowed origins: http://localhost:3000, http://localhost:5173

**Dependencies:** BE-002

---

### [BE-005] Configure Configuration Service Application Properties
**Goal:** Set up database connection, server port, and logging for configuration-service

**Technical Details:**
- Create `application.yml` with:
  - Server port: 8082
  - Database: sms_config_db
  - HikariCP connection pool settings
  - JPA/Hibernate settings (ddl-auto: validate)
  - Logging configuration
  - SpringDoc OpenAPI settings
  - CORS allowed origins: http://localhost:3000, http://localhost:5173

**Dependencies:** BE-003

---

## Phase 2: Student Service - Domain Layer

### [BE-006] Create Student Domain Entity
**Goal:** Implement rich domain model for Student with business logic

**Technical Details:**
- Create `Student.java` domain entity (NOT JPA entity)
- Include fields: id, studentId, personalInfo, contactInfo, familyInfo, status, auditInfo, version
- Implement factory methods: `createNew()`, `fromRepository()`
- Implement business methods:
  - `updatePersonalInfo(firstName, lastName)`
  - `updateContactInfo(mobile)`
  - `activate()`
  - `deactivate()`
  - `calculateAge()`
  - `isActive()`
- Add age validation (3-18 years) in creation

**Dependencies:** BE-002

---

### [BE-007] Create Student Value Objects
**Goal:** Implement immutable value objects for Student domain

**Technical Details:**
- Create `StudentId.java` value object
  - Format: STD-YYYYMMDD-NNNN
  - Factory method: `of(String value)`
- Create `PersonalInfo.java` (firstName, lastName, dateOfBirth, identificationMark, aadhaarNumber)
- Create `ContactInfo.java` (mobile, email, address)
- Create `FamilyInfo.java` (fathersName, mothersName)
- Create `AuditInfo.java` (createdAt, updatedAt, createdBy, updatedBy)
- Create `StudentStatus` enum (ACTIVE, INACTIVE)

**Dependencies:** BE-006

---

### [BE-008] Create Student Repository Interface
**Goal:** Define domain repository contract for Student

**Technical Details:**
- Create `StudentRepository.java` interface in domain layer
- Define methods:
  - `Student save(Student student)`
  - `Optional<Student> findById(Long id)`
  - `Optional<Student> findByStudentId(StudentId studentId)`
  - `Optional<Student> findByMobile(String mobile)`
  - `List<Student> findByLastName(String lastName, Pageable pageable)`
  - `List<Student> findByFathersName(String fathersName, Pageable pageable)`
  - `List<Student> findByStatus(StudentStatus status, Pageable pageable)`
  - `void deleteById(Long id)`
  - `boolean existsByMobile(String mobile)`

**Dependencies:** BE-007

---

### [BE-009] Create Student Domain Exceptions
**Goal:** Define custom exceptions for Student domain

**Technical Details:**
- Create base `DomainException.java`
- Create `StudentNotFoundException.java`
- Create `InvalidAgeException.java`
- Create `DuplicateMobileException.java`
- Create `DuplicateAadhaarException.java`
- Create `InvalidStudentStatusException.java`
- All exceptions should include error codes and messages

**Dependencies:** BE-006

---

### [BE-010] Create Enrollment Domain Entity
**Goal:** Implement domain model for student enrollment history

**Technical Details:**
- Create `Enrollment.java` domain entity
- Include fields: id, studentId, academicYear, gradeClass, section, enrollmentDate, withdrawalDate, status, remarks, auditInfo, version
- Create `EnrollmentStatus` enum (ENROLLED, COMPLETED, WITHDRAWN)
- Implement factory methods and business logic

**Dependencies:** BE-007

---

### [BE-011] Create Enrollment Repository Interface
**Goal:** Define domain repository contract for Enrollment

**Technical Details:**
- Create `EnrollmentRepository.java` interface
- Define methods:
  - `Enrollment save(Enrollment enrollment)`
  - `List<Enrollment> findByStudentId(Long studentId)`
  - `Optional<Enrollment> findByStudentIdAndAcademicYear(Long studentId, String academicYear)`
  - `List<Enrollment> findByAcademicYear(String academicYear)`

**Dependencies:** BE-010

---

## Phase 3: Student Service - Infrastructure Layer

### [BE-012] Create Student JPA Entity
**Goal:** Implement JPA persistence entity for Student

**Technical Details:**
- Create `StudentJpaEntity.java` in infrastructure.persistence.entity
- Map to `students` table
- Include all columns from database schema
- Add JPA annotations: @Entity, @Table, @Id, @GeneratedValue, @Column, @Version
- Add optimistic locking with @Version
- Use @PrePersist and @PreUpdate for audit timestamps

**Dependencies:** BE-002, Database Schema (school_management.sql)

---

### [BE-013] Create Enrollment JPA Entity
**Goal:** Implement JPA persistence entity for Enrollment

**Technical Details:**
- Create `EnrollmentJpaEntity.java` in infrastructure.persistence.entity
- Map to `enrollment_history` table
- Include @ManyToOne relationship to StudentJpaEntity
- Add JPA annotations and constraints
- Add optimistic locking with @Version

**Dependencies:** BE-012

---

### [BE-014] Create Student JPA Repository
**Goal:** Implement Spring Data JPA repository for Student persistence

**Technical Details:**
- Create `StudentJpaRepository.java` interface extending JpaRepository
- Add query methods:
  - `Optional<StudentJpaEntity> findByStudentId(String studentId)`
  - `Optional<StudentJpaEntity> findByMobile(String mobile)`
  - `Page<StudentJpaEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable)`
  - `Page<StudentJpaEntity> findByFathersNameContainingIgnoreCase(String fathersName, Pageable pageable)`
  - `Page<StudentJpaEntity> findByStatus(String status, Pageable pageable)`
  - `boolean existsByMobile(String mobile)`
  - `boolean existsByAadhaarNumber(String aadhaarNumber)`

**Dependencies:** BE-012

---

### [BE-015] Create Enrollment JPA Repository
**Goal:** Implement Spring Data JPA repository for Enrollment persistence

**Technical Details:**
- Create `EnrollmentJpaRepository.java` interface extending JpaRepository
- Add query methods:
  - `List<EnrollmentJpaEntity> findByStudentId(Long studentId)`
  - `Optional<EnrollmentJpaEntity> findByStudentIdAndAcademicYear(Long studentId, String academicYear)`
  - `List<EnrollmentJpaEntity> findByAcademicYear(String academicYear)`

**Dependencies:** BE-013

---

### [BE-016] Create Student Entity Mapper
**Goal:** Implement mapping between domain Student and JPA StudentJpaEntity

**Technical Details:**
- Create `StudentEntityMapper.java` using MapStruct
- Define methods:
  - `StudentJpaEntity toJpaEntity(Student domain)`
  - `Student toDomain(StudentJpaEntity entity)`
- Handle value object conversions
- Map all fields correctly

**Dependencies:** BE-012, BE-007

---

### [BE-017] Create Enrollment Entity Mapper
**Goal:** Implement mapping between domain Enrollment and JPA EnrollmentJpaEntity

**Technical Details:**
- Create `EnrollmentEntityMapper.java` using MapStruct
- Define mapping methods
- Handle value object conversions

**Dependencies:** BE-013, BE-010

---

### [BE-018] Implement Student Repository
**Goal:** Create concrete implementation of StudentRepository using JPA

**Technical Details:**
- Create `StudentRepositoryImpl.java` implementing StudentRepository interface
- Inject StudentJpaRepository and StudentEntityMapper
- Implement all repository methods
- Add student ID generation logic (format: STD-YYYYMMDD-NNNN)
- Handle entity-domain conversions

**Dependencies:** BE-014, BE-016

---

### [BE-019] Implement Enrollment Repository
**Goal:** Create concrete implementation of EnrollmentRepository using JPA

**Technical Details:**
- Create `EnrollmentRepositoryImpl.java` implementing EnrollmentRepository interface
- Inject EnrollmentJpaRepository and EnrollmentEntityMapper
- Implement all repository methods
- Handle entity-domain conversions

**Dependencies:** BE-015, BE-017

---

## Phase 4: Student Service - Application Layer

### [BE-020] Create Student Request DTOs
**Goal:** Define API request DTOs for student operations

**Technical Details:**
- Create `CreateStudentRequest.java`:
  - firstName, lastName, dateOfBirth, mobile, email, address
  - fathersName, mothersName, identificationMark, aadhaarNumber
- Create `UpdateStudentRequest.java`:
  - firstName, lastName, mobile, status, version
- Add Jakarta validation annotations (@NotNull, @Size, @Pattern, @Email)

**Dependencies:** BE-002

---

### [BE-021] Create Student Response DTOs
**Goal:** Define API response DTOs for student operations

**Technical Details:**
- Create `StudentResponse.java`:
  - All student fields for detailed view
- Create `StudentSummaryResponse.java`:
  - id, studentId, firstName, lastName, mobile, status, createdAt
- Create `PagedStudentResponse.java`:
  - List<StudentSummaryResponse> content
  - PageableInfo (page, size, totalElements, totalPages)

**Dependencies:** BE-002

---

### [BE-022] Create Enrollment Request/Response DTOs
**Goal:** Define DTOs for enrollment operations

**Technical Details:**
- Create `CreateEnrollmentRequest.java`
- Create `EnrollmentResponse.java`
- Create `EnrollmentHistoryResponse.java` (list wrapper)
- Add validation annotations

**Dependencies:** BE-002

---

### [BE-023] Create Student DTO Mapper
**Goal:** Implement mapping between domain Student and DTOs

**Technical Details:**
- Create `StudentDtoMapper.java` using MapStruct
- Define methods:
  - `Student toDomain(CreateStudentRequest request)`
  - `StudentResponse toResponse(Student domain)`
  - `StudentSummaryResponse toSummaryResponse(Student domain)`
  - `PagedStudentResponse toPagedResponse(Page<Student> page)`
- Handle value object conversions

**Dependencies:** BE-020, BE-021, BE-007

---

### [BE-024] Create Student Application Service
**Goal:** Implement business use cases for student operations

**Technical Details:**
- Create `StudentApplicationService.java`
- Inject StudentRepository and StudentDtoMapper
- Implement methods:
  - `StudentResponse createStudent(CreateStudentRequest request)`
    - Validate unique mobile
    - Validate age (3-18)
    - Create domain entity
    - Save to repository
  - `StudentResponse getStudent(String studentId)`
  - `StudentResponse updateStudent(String studentId, UpdateStudentRequest request)`
    - Validate version for optimistic locking
    - Update only allowed fields
  - `void deleteStudent(String studentId)`
  - `PagedStudentResponse searchStudents(String lastName, String fathersName, String status, Pageable pageable)`
- Add @Transactional annotations
- Implement business rule validations

**Dependencies:** BE-018, BE-023

---

### [BE-025] Create Enrollment Application Service
**Goal:** Implement business use cases for enrollment operations

**Technical Details:**
- Create `EnrollmentApplicationService.java`
- Inject EnrollmentRepository
- Implement methods:
  - `EnrollmentResponse createEnrollment(String studentId, CreateEnrollmentRequest request)`
  - `EnrollmentHistoryResponse getEnrollmentHistory(String studentId)`
- Validate no duplicate enrollment for same academic year
- Add @Transactional annotations

**Dependencies:** BE-019, BE-022

---

## Phase 5: Student Service - Presentation Layer

### [BE-026] Create Global Exception Handler
**Goal:** Implement centralized exception handling with RFC 7807 Problem Details

**Technical Details:**
- Create `GlobalExceptionHandler.java` with @ControllerAdvice
- Handle exceptions:
  - StudentNotFoundException -> 404
  - InvalidAgeException -> 422
  - DuplicateMobileException -> 409
  - DuplicateAadhaarException -> 409
  - MethodArgumentNotValidException -> 400
  - OptimisticLockException -> 409
  - General exceptions -> 500
- Return RFC 7807 Problem Details format
- Include correlationId, timestamp, errors list

**Dependencies:** BE-009

---

### [BE-027] Create Student REST Controller
**Goal:** Implement REST API endpoints for student operations

**Technical Details:**
- Create `StudentController.java` with @RestController
- Base path: `/api/v1/students`
- Inject StudentApplicationService
- Implement endpoints:
  - `POST /api/v1/students` - Create student (201 Created)
  - `GET /api/v1/students/{studentId}` - Get student (200 OK)
  - `PUT /api/v1/students/{studentId}` - Update student (200 OK)
  - `DELETE /api/v1/students/{studentId}` - Delete student (204 No Content)
  - `GET /api/v1/students` - Search students with pagination (200 OK)
- Add @Valid for request validation
- Add Swagger/OpenAPI annotations
- Add correlation ID handling

**Dependencies:** BE-024, BE-026

---

### [BE-028] Create Enrollment REST Controller
**Goal:** Implement REST API endpoints for enrollment operations

**Technical Details:**
- Create `EnrollmentController.java` with @RestController
- Base path: `/api/v1/students/{studentId}/enrollment-history`
- Inject EnrollmentApplicationService
- Implement endpoints:
  - `POST /api/v1/students/{studentId}/enrollment-history` - Create enrollment (201 Created)
  - `GET /api/v1/students/{studentId}/enrollment-history` - Get enrollment history (200 OK)
- Add validation and OpenAPI annotations

**Dependencies:** BE-025, BE-026

---

### [BE-029] Configure CORS
**Goal:** Set up CORS configuration for frontend integration

**Technical Details:**
- Create `WebConfig.java` implementing WebMvcConfigurer
- Configure CORS mappings:
  - Allowed origins: http://localhost:3000, http://localhost:5173
  - Allowed methods: GET, POST, PUT, DELETE, OPTIONS
  - Allowed headers: Content-Type, Authorization, X-Correlation-ID
  - Max age: 3600
- Reference: LESSONS_LEARNED.md [D-001]

**Dependencies:** BE-002

---

### [BE-030] Configure SpringDoc OpenAPI
**Goal:** Set up Swagger UI and OpenAPI documentation

**Technical Details:**
- Create `OpenApiConfig.java`
- Configure OpenAPI metadata:
  - Title: Student Service API
  - Version: 1.0.0
  - Description: Student management operations
- Set server URL: http://localhost:8081
- Ensure SpringDoc version 2.7.0 (compatible with Spring Boot 3.5.0)
- Reference: LESSONS_LEARNED.md [D-001]

**Dependencies:** BE-002

---

## Phase 6: Configuration Service - Complete Implementation

### [BE-031] Create Configuration Domain Entity
**Goal:** Implement domain model for Configuration

**Technical Details:**
- Create `ConfigurationSetting.java` domain entity
- Create value objects: `ConfigurationKey`, `ConfigurationValue`, `Category`
- Create `DataType` enum (STRING, NUMBER, BOOLEAN, JSON)
- Implement factory methods and business logic

**Dependencies:** BE-003

---

### [BE-032] Create Configuration Repository Interface
**Goal:** Define domain repository contract for Configuration

**Technical Details:**
- Create `ConfigurationRepository.java` interface in domain layer
- Define methods:
  - `ConfigurationSetting save(ConfigurationSetting config)`
  - `Optional<ConfigurationSetting> findByCategoryAndKey(Category category, String key)`
  - `List<ConfigurationSetting> findByCategory(Category category)`
  - `List<ConfigurationSetting> findAll()`
  - `void deleteByCategoryAndKey(Category category, String key)`

**Dependencies:** BE-031

---

### [BE-033] Create Configuration JPA Entity
**Goal:** Implement JPA persistence entity for Configuration

**Technical Details:**
- Create `ConfigurationJpaEntity.java`
- Map to `configuration_settings` table
- Add JPA annotations and optimistic locking
- Follow same patterns as StudentJpaEntity

**Dependencies:** BE-003, Database Schema

---

### [BE-034] Create Configuration JPA Repository
**Goal:** Implement Spring Data JPA repository for Configuration

**Technical Details:**
- Create `ConfigurationJpaRepository.java` extending JpaRepository
- Add query methods:
  - `Optional<ConfigurationJpaEntity> findByCategoryAndConfigKey(String category, String key)`
  - `List<ConfigurationJpaEntity> findByCategory(String category)`
  - `void deleteByCategoryAndConfigKey(String category, String key)`

**Dependencies:** BE-033

---

### [BE-035] Create Configuration Entity Mapper
**Goal:** Implement mapping between domain and JPA entity

**Technical Details:**
- Create `ConfigurationEntityMapper.java` using MapStruct
- Define mapping methods for ConfigurationSetting <-> ConfigurationJpaEntity

**Dependencies:** BE-033, BE-031

---

### [BE-036] Implement Configuration Repository
**Goal:** Create concrete implementation using JPA

**Technical Details:**
- Create `ConfigurationRepositoryImpl.java`
- Inject ConfigurationJpaRepository and mapper
- Implement all repository methods

**Dependencies:** BE-034, BE-035

---

### [BE-037] Create Configuration DTOs
**Goal:** Define request/response DTOs for configuration API

**Technical Details:**
- Create `CreateConfigurationRequest.java`
- Create `UpdateConfigurationRequest.java`
- Create `ConfigurationResponse.java`
- Create `GroupedConfigurationResponse.java` (Map<String, String>)
- Add validation annotations

**Dependencies:** BE-003

---

### [BE-038] Create Configuration DTO Mapper
**Goal:** Implement mapping between domain and DTOs

**Technical Details:**
- Create `ConfigurationDtoMapper.java` using MapStruct
- Define mapping methods

**Dependencies:** BE-037, BE-031

---

### [BE-039] Create Configuration Application Service
**Goal:** Implement business use cases for configuration operations

**Technical Details:**
- Create `ConfigurationApplicationService.java`
- Inject ConfigurationRepository and mapper
- Implement methods:
  - `ConfigurationResponse createOrUpdate(String category, String key, CreateConfigurationRequest request)` (UPSERT)
  - `ConfigurationResponse getConfiguration(String category, String key)`
  - `List<ConfigurationResponse> getAllConfigurations(String category)`
  - `GroupedConfigurationResponse getGroupedByCategory(String category)`
  - `void deleteConfiguration(String category, String key)`
- Add @Transactional annotations

**Dependencies:** BE-036, BE-038

---

### [BE-040] Create Configuration Exception Handler
**Goal:** Implement exception handling for configuration service

**Technical Details:**
- Create `GlobalExceptionHandler.java` (similar to student service)
- Handle ConfigurationNotFoundException -> 404
- Handle validation exceptions
- Return RFC 7807 format

**Dependencies:** BE-031

---

### [BE-041] Create Configuration REST Controller
**Goal:** Implement REST API endpoints for configuration operations

**Technical Details:**
- Create `ConfigurationController.java`
- Base path: `/api/v1/configurations`
- Implement endpoints:
  - `GET /api/v1/configurations` - Get all (with optional category filter)
  - `GET /api/v1/configurations/{category}/{key}` - Get specific config
  - `PUT /api/v1/configurations/{category}/{key}` - Create or update (UPSERT)
  - `DELETE /api/v1/configurations/{category}/{key}` - Delete config
  - `GET /api/v1/configurations/grouped/{category}` - Get grouped by category
- Add validation and OpenAPI annotations

**Dependencies:** BE-039, BE-040

---

### [BE-042] Configure CORS for Configuration Service
**Goal:** Set up CORS for frontend integration

**Technical Details:**
- Create `WebConfig.java` (same as student service)
- Allow origins: http://localhost:3000, http://localhost:5173

**Dependencies:** BE-003

---

### [BE-043] Configure SpringDoc OpenAPI for Configuration Service
**Goal:** Set up Swagger UI and API documentation

**Technical Details:**
- Create `OpenApiConfig.java`
- Configure metadata for Configuration Service API
- Server URL: http://localhost:8082
- Use SpringDoc 2.7.0

**Dependencies:** BE-003

---

## Phase 7: Build and Deployment Preparation

### [BE-044] Create Database Initialization Script
**Goal:** Prepare script to initialize databases

**Technical Details:**
- Create `init-databases.sh` script to:
  - Create databases: sms_student_db, sms_config_db
  - Run school_management.sql
- Create `init-databases.bat` for Windows

**Dependencies:** Database Schema (school_management.sql)

---

### [BE-045] Create Docker Compose for Local Development
**Goal:** Set up containerized PostgreSQL for local development

**Technical Details:**
- Create `docker-compose.yml` in project root
- Configure PostgreSQL 18 container
- Expose port 5432
- Set up volumes for data persistence
- Add initialization scripts

**Dependencies:** BE-044

---

### [BE-046] Create Application Run Scripts
**Goal:** Create convenience scripts to run services

**Technical Details:**
- Create `run-student-service.sh` (mvn spring-boot:run)
- Create `run-configuration-service.sh`
- Create Windows equivalents (.bat files)

**Dependencies:** BE-027, BE-041

---

### [BE-047] Build Student Service
**Goal:** Compile and package student-service

**Technical Details:**
- Run `mvn clean install` in student-service directory
- Ensure all tests pass (if any)
- Verify JAR created in target directory

**Dependencies:** BE-001 through BE-030

---

### [BE-048] Build Configuration Service
**Goal:** Compile and package configuration-service

**Technical Details:**
- Run `mvn clean install` in configuration-service directory
- Ensure all tests pass (if any)
- Verify JAR created in target directory

**Dependencies:** BE-031 through BE-043

---

### [BE-049] Integration Testing - Student Service
**Goal:** Verify student service endpoints work correctly

**Technical Details:**
- Start PostgreSQL database
- Run student-service
- Test all endpoints using curl or Postman:
  - Create student
  - Get student by ID
  - Update student
  - Search students
  - Delete student
  - Create enrollment
  - Get enrollment history
- Verify Swagger UI at http://localhost:8081/swagger-ui/index.html
- Check for CORS errors in browser console

**Dependencies:** BE-047

---

### [BE-050] Integration Testing - Configuration Service
**Goal:** Verify configuration service endpoints work correctly

**Technical Details:**
- Run configuration-service
- Test all endpoints:
  - Get all configurations
  - Get specific configuration
  - Create/update configuration
  - Delete configuration
  - Get grouped configurations
- Verify Swagger UI at http://localhost:8082/swagger-ui/index.html
- Check for CORS errors

**Dependencies:** BE-048

---

## Implementation Notes

### Dependency Management
- All tasks marked with "Dependencies" must wait for prerequisite tasks to complete
- Tasks within the same phase can sometimes be worked in parallel if they don't depend on each other
- Infrastructure tasks (JPA) must come before Application layer tasks
- Application layer must come before Presentation layer

### Code Quality Standards
- Follow SOLID principles throughout
- Use constructor injection (not field injection)
- All public methods must have JavaDoc
- Use meaningful variable and method names
- Add logging at appropriate levels (INFO for business events, DEBUG for flow)

### Error Handling
- Always return RFC 7807 Problem Details format
- Include correlation ID in all error responses
- Log errors with stack traces (but don't expose to clients)

### Testing (QA Team)
- Unit tests should be created by QA team after each phase
- Integration tests after Phase 7
- Refer to QA_TASKS.md for detailed test scenarios

### Configuration Reference
- ALWAYS use SpringDoc OpenAPI 2.7.0+ with Spring Boot 3.5.0 (LESSONS_LEARNED.md [D-001])
- ALWAYS configure CORS for ports 3000 and 5173 (LESSONS_LEARNED.md [D-001])

---

---

## Phase 8: Redis Caching Implementation

### [BE-051] Add Redis Dependencies to Parent POM
**Goal:** Add Redis and caching dependencies to the parent POM for both services

**Technical Details:**
- Add dependencies to `sms-backend/pom.xml`:
  - `spring-boot-starter-data-redis`
  - `spring-boot-starter-cache`
  - `lettuce-core` (Redis client)
  - `jackson-databind` (for JSON serialization)
- Version management handled by Spring Boot parent
- Lettuce client preferred over Jedis for better async support

**Reference:** LESSONS_LEARNED.md [D-006]

**Dependencies:** BE-001

---

### [BE-052] Configure Redis for Student Service
**Goal:** Set up Redis cache configuration in student-service

**Technical Details:**
- Update `student-service/src/main/resources/application.yml`:
  ```yaml
  spring:
    cache:
      type: redis
      redis:
        time-to-live: 3600000  # 1 hour for stable data
    data:
      redis:
        host: ${REDIS_HOST:localhost}
        port: ${REDIS_PORT:6379}
        password: ${REDIS_PASSWORD:}
        database: 0  # Separate database for student-service
        timeout: 60000
        lettuce:
          pool:
            max-active: 20
            max-idle: 10
            min-idle: 5
            max-wait: -1ms
          shutdown-timeout: 200ms
  ```
- Configure connection timeout: 60 seconds
- Set database index to 0 (student-service specific)

**Reference:** LESSONS_LEARNED.md [D-003], [D-006]

**Dependencies:** BE-051, BE-004

---

### [BE-053] Configure Redis for Configuration Service
**Goal:** Set up Redis cache configuration in configuration-service

**Technical Details:**
- Update `configuration-service/src/main/resources/application.yml`:
  ```yaml
  spring:
    cache:
      type: redis
      redis:
        time-to-live: 14400000  # 4 hours for configuration data
    data:
      redis:
        host: ${REDIS_HOST:localhost}
        port: ${REDIS_PORT:6379}
        password: ${REDIS_PASSWORD:}
        database: 1  # Separate database for configuration-service
        timeout: 60000
        lettuce:
          pool:
            max-active: 20
            max-idle: 10
            min-idle: 5
            max-wait: -1ms
          shutdown-timeout: 200ms
  ```
- Use database index 1 to separate from student-service
- Longer TTL (4 hours) for stable configuration data

**Reference:** LESSONS_LEARNED.md [D-003], [D-006]

**Dependencies:** BE-051, BE-005

---

### [BE-054] Create Redis Configuration Class for Student Service
**Goal:** Implement custom Redis configuration with proper serialization

**Technical Details:**
- Create `StudentServiceRedisConfig.java` in `infrastructure.config` package
- Configure `RedisCacheManager` with custom settings:
  - Use `Jackson2JsonRedisSerializer` for complex objects
  - Set key prefix: `sms:student:`
  - Configure separate TTLs for different cache names:
    - `students`: 3600 seconds (1 hour)
    - `studentSearchResults`: 900 seconds (15 minutes)
    - `activeStudentsCount`: 900 seconds (15 minutes)
- Enable Redis persistence (AOF + RDB)
- Configure maxmemory-policy: `allkeys-lru`

**Code Structure:**
```java
@Configuration
@EnableCaching
public class StudentServiceRedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Jackson2JsonRedisSerializer for proper deserialization
        // Configure cache-specific TTLs
        // Set key prefix pattern
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // Configure serializers
    }
}
```

**Reference:** LESSONS_LEARNED.md [D-003], [D-004]

**Dependencies:** BE-052

---

### [BE-055] Create Redis Configuration Class for Configuration Service
**Goal:** Implement custom Redis configuration for configuration service

**Technical Details:**
- Create `ConfigurationServiceRedisConfig.java` in `infrastructure.config` package
- Configure `RedisCacheManager` with:
  - Key prefix: `sms:config:`
  - Cache TTLs:
    - `configurations`: 14400 seconds (4 hours)
    - `configByCategory`: 14400 seconds (4 hours)
    - `groupedConfigs`: 14400 seconds (4 hours)
- Use `Jackson2JsonRedisSerializer`
- Enable caching annotations: `@EnableCaching`

**Reference:** LESSONS_LEARNED.md [D-003], [D-004]

**Dependencies:** BE-053

---

### [BE-056] Implement Cache Key Strategy for Student Service
**Goal:** Create consistent cache key naming and management

**Technical Details:**
- Create `CacheKeyGenerator.java` utility class
- Implement cache key patterns following conventions:
  - Student by ID: `sms:student:id:{studentId}`
  - Student search: `sms:student:search:{md5(searchParams)}`
  - Active count: `sms:student:count:active`
- Create `@CacheConfig` annotation on service classes:
  ```java
  @CacheConfig(cacheNames = "students", keyGenerator = "studentKeyGenerator")
  ```
- Ensure keys stay under 100 characters
- Document all cache key patterns in JavaDoc

**Reference:** LESSONS_LEARNED.md [D-004]

**Dependencies:** BE-054

---

### [BE-057] Implement Cache Key Strategy for Configuration Service
**Goal:** Create consistent cache key naming for configuration service

**Technical Details:**
- Create cache key patterns:
  - Config by category and key: `sms:config:category:{category}:key:{key}`
  - All configs by category: `sms:config:category:{category}:all`
  - Grouped configs: `sms:config:grouped:{category}`
- Implement custom key generator for complex lookups
- Keep keys under 100 characters

**Reference:** LESSONS_LEARNED.md [D-004]

**Dependencies:** BE-055

---

### [BE-058] Add Caching Annotations to Student Service Methods
**Goal:** Enable caching for read-heavy student operations

**Technical Details:**
- Update `StudentApplicationService.java` with caching annotations:
  ```java
  @Cacheable(value = "students", key = "'id:' + #studentId")
  public Student getStudent(String studentId) { ... }

  @Cacheable(value = "studentSearchResults", key = "'search:' + T(org.apache.commons.codec.digest.DigestUtils).md5Hex(#criteria.toString())")
  public List<Student> searchStudents(StudentSearchCriteria criteria) { ... }

  @Cacheable(value = "activeStudentsCount", key = "'count:active'")
  public long getActiveStudentsCount() { ... }

  @CacheEvict(value = {"students", "studentSearchResults", "activeStudentsCount"}, key = "'id:' + #studentId")
  public Student updateStudent(String studentId, UpdateStudentCommand command) { ... }

  @CacheEvict(value = {"students", "studentSearchResults", "activeStudentsCount"}, key = "'id:' + #studentId")
  public void deleteStudent(String studentId) { ... }

  @CachePut(value = "students", key = "'id:' + #result.studentId")
  public Student createStudent(CreateStudentCommand command) { ... }
  ```
- Use SpEL expressions for dynamic keys
- Include version in cache key for optimistic locking

**Reference:** LESSONS_LEARNED.md [D-005]

**Dependencies:** BE-024, BE-056

---

### [BE-059] Add Caching Annotations to Configuration Service Methods
**Goal:** Enable caching for configuration operations

**Technical Details:**
- Update `ConfigurationApplicationService.java` with caching:
  ```java
  @Cacheable(value = "configurations", key = "'category:' + #category + ':key:' + #key")
  public ConfigurationResponse getConfiguration(String category, String key) { ... }

  @Cacheable(value = "configByCategory", key = "'category:' + #category")
  public List<ConfigurationResponse> getAllConfigurations(String category) { ... }

  @Cacheable(value = "groupedConfigs", key = "'grouped:' + #category")
  public GroupedConfigurationResponse getGroupedByCategory(String category) { ... }

  @CacheEvict(value = {"configurations", "configByCategory", "groupedConfigs"}, allEntries = true)
  public ConfigurationResponse createOrUpdate(String category, String key, CreateConfigurationRequest request) { ... }

  @CacheEvict(value = {"configurations", "configByCategory", "groupedConfigs"}, allEntries = true)
  public void deleteConfiguration(String category, String key) { ... }
  ```
- Use `allEntries=true` for configuration updates (affects multiple related caches)

**Reference:** LESSONS_LEARNED.md [D-005]

**Dependencies:** BE-039, BE-057

---

### [BE-060] Implement Cache Eviction Strategy
**Goal:** Ensure cache consistency on data modifications

**Technical Details:**
- Review all update/delete methods in both services
- Ensure related caches are evicted:
  - Student updates: evict student, search results, and count caches
  - Configuration updates: evict all related configuration caches
- Use `@Caching` for complex eviction scenarios:
  ```java
  @Caching(evict = {
      @CacheEvict(value = "students", key = "'id:' + #studentId"),
      @CacheEvict(value = "studentSearchResults", allEntries = true),
      @CacheEvict(value = "activeStudentsCount", allEntries = true)
  })
  ```
- Avoid overusing `allEntries=true` except for configuration service
- Always include optimistic locking version in cache keys when applicable

**Reference:** LESSONS_LEARNED.md [D-005]

**Dependencies:** BE-058, BE-059

---

### [BE-061] Implement Scheduled Cache Cleanup
**Goal:** Periodically clean up stale cache entries

**Technical Details:**
- Create `CacheScheduledTasks.java` in `infrastructure.cache` package
- Implement scheduled methods:
  ```java
  @Scheduled(cron = "0 0 * * * *")  // Every hour
  public void clearSearchResultsCache() {
      cacheManager.getCache("studentSearchResults").clear();
  }

  @Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
  public void clearAllStaleEntries() {
      // Custom logic to identify and remove stale entries
  }
  ```
- Document cleanup schedule in code comments
- Add logging for cache cleanup operations

**Reference:** LESSONS_LEARNED.md [D-005]

**Dependencies:** BE-060

---

### [BE-062] Add Cache Monitoring with Micrometer
**Goal:** Track cache performance metrics

**Technical Details:**
- Create `CacheMetricsConfig.java` in `infrastructure.config` package
- Register cache metrics with Micrometer:
  ```java
  @Bean
  public CacheMetricsRegistrar cacheMetricsRegistrar(MeterRegistry meterRegistry, Collection<CacheManager> cacheManagers) {
      return new CacheMetricsRegistrar(meterRegistry, "cache", cacheManagers);
  }
  ```
- Implement custom metrics:
  - `cache.hits.total` (Counter) - Tagged by cache name
  - `cache.misses.total` (Counter) - Tagged by cache name
  - `cache.hit.ratio` (Gauge) - Calculate hit rate, target >80%
  - `cache.evictions.total` (Counter)
  - `redis.memory.used` (Gauge) - Alert if >200MB
- Create `CacheEventLogger.java` to log cache operations at DEBUG level
- Export metrics to Prometheus endpoint

**Reference:** LESSONS_LEARNED.md [D-007]

**Dependencies:** BE-060

---

### [BE-063] Create Cache Health Indicators
**Goal:** Monitor Redis connectivity and health

**Technical Details:**
- Create `RedisHealthIndicator.java` in `infrastructure.config` package:
  ```java
  @Component
  public class RedisHealthIndicator implements HealthIndicator {

      @Autowired
      private RedisConnectionFactory redisConnectionFactory;

      @Override
      public Health health() {
          try {
              redisConnectionFactory.getConnection().ping();
              return Health.up()
                  .withDetail("redis", "Available")
                  .withDetail("database", getRedisDatabase())
                  .build();
          } catch (Exception e) {
              return Health.down()
                  .withDetail("error", e.getMessage())
                  .build();
          }
      }
  }
  ```
- Include in Spring Boot Actuator health endpoint
- Alert on Redis connection failures

**Reference:** LESSONS_LEARNED.md [D-007]

**Dependencies:** BE-062

---

### [BE-064] Implement Circuit Breaker for Redis Failures
**Goal:** Graceful degradation when Redis is unavailable

**Technical Details:**
- Add Resilience4j dependency to parent POM
- Create `RedisFallbackConfig.java`:
  ```java
  @Configuration
  public class RedisFallbackConfig {

      @Bean
      public CircuitBreakerRegistry circuitBreakerRegistry() {
          CircuitBreakerConfig config = CircuitBreakerConfig.custom()
              .failureRateThreshold(50)
              .waitDurationInOpenState(Duration.ofSeconds(30))
              .slidingWindowSize(10)
              .build();

          return CircuitBreakerRegistry.of(config);
      }
  }
  ```
- Wrap cache operations with circuit breaker:
  ```java
  @CircuitBreaker(name = "redis", fallbackMethod = "fallbackToDatabase")
  public Optional<Student> getStudent(String studentId) { ... }

  private Optional<Student> fallbackToDatabase(String studentId, Exception e) {
      log.warn("Redis unavailable, falling back to database: {}", e.getMessage());
      return studentRepository.findByStudentId(StudentId.of(studentId));
  }
  ```
- Configure fallback to always use database when Redis fails
- Test application behavior when Redis is down

**Reference:** LESSONS_LEARNED.md [D-008]

**Dependencies:** BE-063

---

### [BE-065] Configure Redis Maxmemory and Eviction Policy
**Goal:** Prevent Redis memory overflow with proper eviction

**Technical Details:**
- Create Redis configuration file `redis.conf`:
  ```
  maxmemory 256mb
  maxmemory-policy allkeys-lru
  ```
- Update Docker Compose to use custom Redis config:
  ```yaml
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server /usr/local/etc/redis/redis.conf
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
      - redis-data:/data
  ```
- Document maxmemory limits
- Configure alerts when memory usage >80%

**Reference:** LESSONS_LEARNED.md [D-008]

**Dependencies:** BE-045

---

### [BE-066] Implement Cache Warmup Strategy
**Goal:** Pre-populate cache with frequently accessed data on startup

**Technical Details:**
- Create `CacheWarmupService.java` in `infrastructure.cache` package:
  ```java
  @Component
  public class CacheWarmupService implements ApplicationListener<ApplicationReadyEvent> {

      @Override
      public void onApplicationEvent(ApplicationReadyEvent event) {
          log.info("Starting cache warmup...");
          warmupStudentCache();
          warmupConfigurationCache();
          log.info("Cache warmup completed");
      }

      private void warmupStudentCache() {
          // Load active students into cache
          List<Student> activeStudents = studentRepository.findByStatus(StudentStatus.ACTIVE);
          activeStudents.forEach(student ->
              cacheManager.getCache("students").put("id:" + student.getStudentId(), student)
          );
      }

      private void warmupConfigurationCache() {
          // Load all configurations into cache
          List<Configuration> configs = configRepository.findAll();
          // Cache by category
      }
  }
  ```
- Execute warmup after application startup (ApplicationReadyEvent)
- Log cache warmup operations

**Reference:** LESSONS_LEARNED.md [D-003]

**Dependencies:** BE-064

---

### [BE-067] Create Grafana Dashboard for Cache Metrics
**Goal:** Visualize cache performance and health

**Technical Details:**
- Create Grafana dashboard JSON file: `grafana-cache-dashboard.json`
- Include panels for:
  - Cache hit ratio over time (line graph) - Alert if <70%
  - Cache hits vs misses (stacked bar chart)
  - Redis memory usage (gauge) - Alert if >200MB
  - Cache evictions per minute (line graph)
  - Top cached keys by size (table)
  - Redis connection pool metrics
- Configure alerts:
  - Hit ratio drops below 70%
  - Memory usage exceeds 200MB
  - Connection pool exhaustion
- Document how to import dashboard into Grafana

**Reference:** LESSONS_LEARNED.md [D-007]

**Dependencies:** BE-062

---

### [BE-068] Update Docker Compose with Redis Persistence
**Goal:** Enable Redis data persistence to prevent data loss on restart

**Technical Details:**
- Update `docker-compose.yml` to enable AOF and RDB:
  ```yaml
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: >
      redis-server
      --appendonly yes
      --appendfsync everysec
      --save 900 1
      --save 300 10
      --save 60 10000
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  volumes:
    redis-data:
      driver: local
  ```
- Configure AOF (Append-Only File) with `everysec` fsync
- Configure RDB snapshots at intervals
- Add health check for Redis

**Reference:** LESSONS_LEARNED.md [D-003]

**Dependencies:** BE-045

---

### [BE-069] Create Cache Management Admin Endpoints
**Goal:** Provide administrative endpoints to manage cache

**Technical Details:**
- Create `CacheAdminController.java` in student-service:
  ```java
  @RestController
  @RequestMapping("/api/v1/admin/cache")
  public class CacheAdminController {

      @PostMapping("/clear/{cacheName}")
      public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
          // Clear specific cache
      }

      @PostMapping("/clear-all")
      public ResponseEntity<String> clearAllCaches() {
          // Clear all caches
      }

      @GetMapping("/stats")
      public ResponseEntity<CacheStats> getCacheStats() {
          // Return cache statistics
      }

      @GetMapping("/keys/{cacheName}")
      public ResponseEntity<List<String>> getCacheKeys(@PathVariable String cacheName) {
          // List all keys in cache
      }
  }
  ```
- Secure endpoints (future: add authentication)
- Add OpenAPI documentation
- Log all cache admin operations

**Dependencies:** BE-066

---

### [BE-070] Write Cache Integration Tests
**Goal:** Verify cache behavior with integration tests

**Technical Details:**
- Create `CacheIntegrationTest.java` using TestContainers:
  ```java
  @SpringBootTest
  @Testcontainers
  class CacheIntegrationTest {

      @Container
      static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
          .withExposedPorts(6379);

      @Test
      void testStudentCaching() {
          // Create student
          // Verify cached
          // Retrieve from cache
          // Update student
          // Verify cache evicted
      }

      @Test
      void testCacheHitRatio() {
          // Perform multiple reads
          // Verify hit ratio >80%
      }

      @Test
      void testRedisFallback() {
          // Stop Redis container
          // Verify fallback to database
          // Verify no errors
      }
  }
  ```
- Test cache hit/miss scenarios
- Test cache eviction on updates
- Test fallback behavior when Redis is unavailable
- Test concurrent access to cached data

**Dependencies:** BE-066

---

### [BE-071] Document Cache Strategy and Operations
**Goal:** Create comprehensive cache documentation

**Technical Details:**
- Create `docs/CACHE_STRATEGY.md` documenting:
  - Cache key naming conventions
  - TTL values for each cache
  - Cache eviction strategies
  - Cache warmup process
  - Monitoring and alerting setup
  - Troubleshooting guide
  - Redis configuration details
- Update `README.md` with cache setup instructions
- Document rollback plan: setting `spring.cache.type=none` to disable caching
- Create operations runbook for Redis recovery procedures

**Reference:** LESSONS_LEARNED.md [D-008]

**Dependencies:** BE-070

---

### [BE-072] Performance Testing with Cache
**Goal:** Verify cache improves performance metrics

**Technical Details:**
- Create JMeter or Gatling performance test scripts
- Test scenarios:
  - Student retrieval (with and without cache)
  - Search operations (with and without cache)
  - Configuration lookups (with and without cache)
- Measure and compare:
  - 95th percentile response times
  - Throughput (requests/second)
  - Cache hit ratio during load
- Verify performance targets:
  - GET student by ID: <100ms (with cache: <10ms)
  - Search students: <200ms (with cache: <50ms)
  - Cache hit ratio: >80%
- Document performance improvements in `docs/PERFORMANCE_RESULTS.md`

**Dependencies:** BE-071

---

## Completion Checklist

### Student Service Complete When:
- [ ] All BE-001 through BE-030 tasks completed
- [ ] Service starts without errors
- [ ] All endpoints accessible and return expected responses
- [ ] Swagger UI functional at http://localhost:8081/swagger-ui/index.html
- [ ] Database schema applied correctly
- [ ] CORS working for frontend ports
- [ ] Optimistic locking working (version conflicts handled)

### Configuration Service Complete When:
- [ ] All BE-031 through BE-043 tasks completed
- [ ] Service starts without errors
- [ ] All endpoints accessible and return expected responses
- [ ] Swagger UI functional at http://localhost:8082/swagger-ui/index.html
- [ ] Database schema applied correctly
- [ ] CORS working for frontend ports
- [ ] UPSERT logic working correctly

### Redis Caching Complete When:
- [ ] All BE-051 through BE-072 tasks completed
- [ ] Redis running and accessible on port 6379
- [ ] Cache hit ratio consistently >80%
- [ ] Cache eviction working correctly on updates
- [ ] Circuit breaker fallback working when Redis unavailable
- [ ] Cache metrics visible in Grafana dashboard
- [ ] Redis memory usage <200MB under normal load
- [ ] Performance tests show measurable improvement
- [ ] Cache warmup executes successfully on startup
- [ ] All cache integration tests passing
- [ ] Documentation complete (CACHE_STRATEGY.md)

### Backend Complete When:
- [ ] All tasks BE-001 through BE-072 completed
- [ ] Both services running concurrently with Redis
- [ ] Integration tests pass (including cache tests)
- [ ] Performance targets met with caching enabled
- [ ] Monitoring and alerting configured
- [ ] Documentation complete
- [ ] Ready for frontend integration

---

**Document Version:** 2.0
**Last Updated:** 2025-12-06
**Status:** READY FOR EXECUTION - REDIS CACHING INCLUDED
