# Backend Implementation Tasks - School Management System

## Overview
This document provides a comprehensive, sequential task list for implementing the Backend services (Student Service and Configuration Service) using Spring Boot 3.5 and Java 21.

**Target:** Complete backend implementation following layered architecture (Presentation → Application → Domain → Infrastructure)

---

## Phase 1: Project Setup & Infrastructure

### [BE-001] Create Parent POM and Maven Multi-Module Structure
**Goal:** Set up the Maven parent POM and module structure for all backend services.

**Technical Details:**
- Create `backend/pom.xml` with Spring Boot 3.5.0 parent
- Define modules: `shared-lib`, `student-service`, `configuration-service`, `api-gateway`
- Configure dependency management for Spring Cloud, MapStruct, Lombok, Drools
- Set Java 21 as target version

**Acceptance Criteria:**
- `mvn clean install` executes successfully
- All modules are recognized by Maven
- Centralized dependency versions are defined in parent POM

**Dependencies:** None

---

### [BE-002] Create Shared Library Module
**Goal:** Implement common utilities, exceptions, and DTOs used across all services.

**Technical Details:**
- Create package structure: `com.sms.shared`
- Implement base exceptions:
  - `BaseException.java`
  - `ResourceNotFoundException.java`
  - `ValidationException.java`
  - `BusinessRuleViolationException.java`
- Create RFC 7807 Problem Details DTO: `ProblemDetail.java`
- Implement validation annotations:
  - `@ValidMobile` with custom validator
  - `@ValidAdhaar` with custom validator
- Create utility classes:
  - `DateTimeUtils.java` (age calculation, date formatting)
  - `ValidationUtils.java` (mobile, email, adhaar validation)
- Define constants: `ErrorCodes.java`, `HttpHeaders.java`

**Acceptance Criteria:**
- All shared classes compile successfully
- Unit tests for validators pass (coverage > 80%)
- Shared library JAR can be imported by other services

**Dependencies:** BE-001

---

### [BE-003] Configure PostgreSQL Database Connections
**Goal:** Set up database connection configuration for Student and Configuration databases.

**Technical Details:**
- Create `application.yml` for Student Service with:
  - PostgreSQL datasource configuration
  - HikariCP connection pool settings (min: 10, max: 50)
  - JPA/Hibernate configuration (ddl-auto: validate)
  - Flyway migration settings
- Create `application.yml` for Configuration Service with similar settings
- Define environment-specific profiles: `application-dev.yml`, `application-prod.yml`
- Configure separate databases: `student_db`, `config_db`

**Acceptance Criteria:**
- Services can connect to PostgreSQL databases
- HikariCP metrics are visible in logs
- Connection pool settings are properly configured
- Environment variables override defaults

**Dependencies:** BE-001

---

### [BE-004] Configure Redis Cache
**Goal:** Set up Redis integration for query result caching.

**Technical Details:**
- Add Redis dependencies: `spring-boot-starter-data-redis`, `lettuce-core`
- Configure Redis in `application.yml`:
  - Connection settings (host, port, password)
  - Lettuce pool configuration
  - Cache TTL: 5 minutes for students, 30 minutes for configurations
- Create `RedisConfig.java` with:
  - `RedisCacheManager` bean
  - Custom `CacheConfiguration` for different TTLs per cache
  - Key serialization strategy

**Acceptance Criteria:**
- Redis connection is established successfully
- Cache manager bean is created
- Cache operations (set, get, evict) work correctly
- TTL settings are respected

**Dependencies:** BE-001, BE-003

---

### [BE-005] Configure Observability (Zipkin, Prometheus, Micrometer)
**Goal:** Set up distributed tracing and metrics collection infrastructure.

**Technical Details:**
- Add dependencies: `spring-cloud-sleuth-zipkin`, `micrometer-registry-prometheus`
- Configure Zipkin in `application.yml`:
  - Base URL: `http://localhost:9411`
  - Sampling probability: 1.0 (dev), 0.1 (prod)
- Configure Prometheus endpoint:
  - Enable `/actuator/prometheus` endpoint
  - Expose metrics: health, info, metrics, prometheus
- Create custom metrics:
  - `students.registered.total` (Counter)
  - `students.active.count` (Gauge)
  - `students.search.duration` (Timer)

**Acceptance Criteria:**
- Zipkin receives traces from services
- Prometheus endpoint exposes metrics
- Custom business metrics are recorded
- Correlation IDs propagate across services

**Dependencies:** BE-001, BE-003

---

## Phase 2: Student Service - Domain Layer

### [BE-006] Create Student Domain Entity
**Goal:** Implement the core Student domain model with rich business behavior.

**Technical Details:**
- Create `Student.java` in `domain.model` package:
  - Fields: studentId, studentKey, firstName, lastName, dateOfBirth, mobile, email, address, fatherNameOrGuardian, motherName, identificationMark, adhaarNumber, status, version, createdAt, updatedAt, createdBy, updatedBy
  - Use Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
  - Add business methods: `activate()`, `deactivate()`, `calculateAge()`
  - Implement validation logic in domain
- Create `StudentStatus.java` enum (ACTIVE, INACTIVE)
- Create value objects:
  - `Mobile.java` (immutable, validated)
  - `Email.java` (immutable, validated)
  - `AdhaarNumber.java` (immutable, validated)

**Acceptance Criteria:**
- Student entity compiles with all fields
- Value objects enforce validation rules
- Business methods work correctly
- Unit tests for domain logic pass

**Dependencies:** BE-002

---

### [BE-007] Create Enrollment History Domain Entity
**Goal:** Implement EnrollmentHistory entity for tracking status changes.

**Technical Details:**
- Create `EnrollmentHistory.java` in `domain.model` package:
  - Fields: enrollmentId, studentId, status, changedBy, changedAt, remarks
  - Use Lombok annotations
  - Add relationship to Student entity (Many-to-One)

**Acceptance Criteria:**
- EnrollmentHistory entity compiles
- Relationship with Student is defined
- Unit tests verify entity creation

**Dependencies:** BE-006

---

### [BE-008] Create Student Repository Interface
**Goal:** Define repository contract for Student data access.

**Technical Details:**
- Create `StudentRepository.java` interface in `domain.repository` package:
  - Method: `Optional<Student> findByStudentKey(String studentKey)`
  - Method: `Optional<Student> findByMobile(String mobile)`
  - Method: `List<Student> findByLastNameContainingIgnoreCase(String lastName)`
  - Method: `List<Student> findByGuardianNameContaining(String guardianName)`
  - Method: `List<Student> findByStatus(StudentStatus status)`
  - Method: `boolean existsByMobile(String mobile)`
  - Extend basic CRUD operations

**Acceptance Criteria:**
- Interface is defined with all required methods
- Method signatures follow Spring Data conventions
- No implementation details in interface

**Dependencies:** BE-006

---

### [BE-009] Implement Student Key Generator
**Goal:** Create service to generate unique student keys (STU-YYYY-NNNN format).

**Technical Details:**
- Create `StudentKeyGenerator.java` in `domain.service` package:
  - Method: `String generateStudentKey()`
  - Format: `STU-{year}-{sequence}` (e.g., STU-2025-0001)
  - Use database sequence for thread-safe generation
  - Zero-pad sequence to 4 digits

**Acceptance Criteria:**
- Generated keys follow format STU-YYYY-NNNN
- Keys are unique across the system
- Sequence numbers increment correctly
- Unit tests verify format and uniqueness

**Dependencies:** BE-006

---

### [BE-010] Implement Student Validator Domain Service
**Goal:** Create domain service for business rule validation using Drools.

**Technical Details:**
- Create `StudentValidator.java` in `domain.service` package:
  - Method: `void validateAge(LocalDate dateOfBirth)` (3-18 years)
  - Method: `void validateMobileUniqueness(String mobile)`
  - Integrate with Drools rules engine
- Create Drools rule file: `student-validation.drl` in `resources/rules/`:
  - Age validation rule (3-18 years)
  - Mobile uniqueness validation rule
- Create `DroolsConfig.java` in `infrastructure.config`:
  - Configure KieContainer
  - Load DRL files from classpath

**Acceptance Criteria:**
- Age validation rule fires correctly
- Mobile uniqueness rule fires correctly
- ValidationException thrown for invalid data
- Unit tests verify all validation scenarios

**Dependencies:** BE-006, BE-008

---

## Phase 3: Student Service - Infrastructure Layer

### [BE-011] Create JPA Student Entity
**Goal:** Implement JPA entity for database persistence.

**Technical Details:**
- Create `StudentEntity.java` in `infrastructure.persistence.entity` package:
  - Annotate with `@Entity`, `@Table(name = "student")`
  - Map all fields with `@Column` annotations
  - Use `@Id`, `@GeneratedValue(strategy = IDENTITY)` for studentId
  - Add `@Version` for optimistic locking
  - Add indexes: `@Index` on lastName, mobile, guardianName
  - Configure audit fields: `@CreatedDate`, `@LastModifiedDate`
  - Define relationship to EnrollmentHistoryEntity

**Acceptance Criteria:**
- JPA entity is properly annotated
- Database table mapping is correct
- Optimistic locking works as expected
- Entity can be persisted and retrieved

**Dependencies:** BE-006

---

### [BE-012] Create JPA Enrollment History Entity
**Goal:** Implement JPA entity for enrollment history persistence.

**Technical Details:**
- Create `EnrollmentHistoryEntity.java` in `infrastructure.persistence.entity` package:
  - Annotate with `@Entity`, `@Table(name = "enrollment_history")`
  - Map all fields
  - Define `@ManyToOne` relationship to StudentEntity
  - Configure cascade and fetch settings

**Acceptance Criteria:**
- JPA entity is properly annotated
- Relationship with StudentEntity works
- Cascade operations function correctly

**Dependencies:** BE-011

---

### [BE-013] Implement JPA Student Repository
**Goal:** Create Spring Data JPA repository implementation.

**Technical Details:**
- Create `JpaStudentRepository.java` extending `JpaRepository<StudentEntity, Long>`:
  - Implement query methods using method naming convention
  - Add custom queries using `@Query` for complex searches
  - Implement specification-based search
- Create `StudentRepositoryImpl.java` implementing `StudentRepository` domain interface:
  - Map between domain model (Student) and JPA entity (StudentEntity)
  - Delegate to JpaStudentRepository
  - Handle entity-to-domain conversion

**Acceptance Criteria:**
- All repository methods work correctly
- Query methods return expected results
- Entity-domain mapping is correct
- Integration tests with TestContainers pass

**Dependencies:** BE-008, BE-011

---

### [BE-014] Implement Database Migration Scripts (Flyway)
**Goal:** Create SQL migration scripts for Student schema.

**Technical Details:**
- Create migration files in `resources/db/migration/`:
  - `V1.0.0__Create_student_table.sql`:
    - All columns as per DATABASE_SCHEMA.md
    - Primary key, unique constraints, check constraints
    - Indexes on lastName, mobile, guardianName
  - `V1.0.1__Create_enrollment_history_table.sql`:
    - All columns
    - Foreign key to student table with CASCADE delete
    - Index on studentId
  - `V1.0.2__Create_audit_log_table.sql`:
    - Audit log structure
    - Indexes on entity_type, changed_at
  - `V1.0.3__Create_triggers.sql`:
    - Trigger for auto-updating updated_at column
  - `V1.1.0__Insert_seed_data.sql`:
    - Sample student records for testing

**Acceptance Criteria:**
- All migration scripts execute successfully
- Tables are created with correct structure
- Constraints and indexes are in place
- Flyway version history is tracked correctly
- Database matches schema design exactly

**Dependencies:** BE-003

---

### [BE-015] Configure Audit Logging
**Goal:** Implement audit trail for all CRUD operations.

**Technical Details:**
- Create `AuditLogger.java` in `infrastructure.audit` package:
  - Method: `void logCreate(String entityType, Long entityId, Object newValue)`
  - Method: `void logUpdate(String entityType, Long entityId, Object oldValue, Object newValue)`
  - Method: `void logDelete(String entityType, Long entityId, Object oldValue)`
  - Store audit logs in audit_log table
  - Capture correlation ID from request context
- Create `AuditLogEntity.java` for persistence
- Create aspect to automatically audit entity changes

**Acceptance Criteria:**
- All CRUD operations are logged
- Audit records include old and new values (JSON)
- Correlation IDs are captured
- Audit logs are queryable

**Dependencies:** BE-013, BE-014

---

## Phase 4: Student Service - Application Layer

### [BE-016] Create Student DTOs
**Goal:** Define Request and Response DTOs for Student API.

**Technical Details:**
- Create DTOs in `presentation.dto` package:
  - `CreateStudentRequest.java`:
    - Fields: firstName, lastName, dateOfBirth, mobile, email, address, fatherNameOrGuardian, motherName, identificationMark, adhaarNumber
    - JSR-380 validation annotations: `@NotBlank`, `@Pattern`, `@Email`, `@Past`
  - `UpdateStudentRequest.java`:
    - Fields: firstName, lastName, mobile, status
    - Validation annotations
  - `StudentDTO.java` (response):
    - All student fields + calculated age
    - Use Java 21 record type
  - `EnrollmentHistoryDTO.java`:
    - All enrollment history fields
    - Use record type

**Acceptance Criteria:**
- All DTOs compile correctly
- Validation annotations are properly configured
- DTOs are immutable where appropriate
- Java 21 record types are used for responses

**Dependencies:** BE-002

---

### [BE-017] Create MapStruct Mapper for Student
**Goal:** Implement type-safe DTO-Entity mapping.

**Technical Details:**
- Create `StudentMapper.java` interface in `application.mapper` package:
  - Annotate with `@Mapper(componentModel = "spring")`
  - Method: `StudentDTO toDTO(Student student)`
  - Method: `Student toEntity(CreateStudentRequest request)`
  - Method: `void updateEntityFromRequest(UpdateStudentRequest request, @MappingTarget Student student)`
  - Custom mapping for age calculation: `@Mapping(target = "age", expression = "java(calculateAge(student.getDateOfBirth()))")`
  - Ignore fields: studentId, studentKey, createdAt, updatedAt
- Implement helper method: `default int calculateAge(LocalDate dateOfBirth)`

**Acceptance Criteria:**
- Mapper compiles and generates implementation
- All mappings work correctly
- Age is calculated properly
- Unit tests verify mapping accuracy

**Dependencies:** BE-006, BE-016

---

### [BE-018] Implement Student Service (Create Operation)
**Goal:** Implement business logic for student registration.

**Technical Details:**
- Create `StudentService.java` interface in `application.service` package:
  - Method: `StudentDTO createStudent(CreateStudentRequest request)`
- Create `StudentServiceImpl.java` implementation:
  - Annotate with `@Service`, `@Transactional`
  - Validate age using StudentValidator (Drools)
  - Check mobile uniqueness
  - Generate student key using StudentKeyGenerator
  - Map request to Student entity
  - Save to repository
  - Create initial enrollment history record
  - Log audit trail
  - Invalidate student list cache
  - Return StudentDTO
  - Handle exceptions: AgeValidationException, DuplicateMobileException

**Acceptance Criteria:**
- Student is created successfully
- Student key is auto-generated
- Age validation works (3-18 years)
- Mobile uniqueness is enforced
- Enrollment history is created
- Audit log entry is created
- Cache is invalidated
- Unit tests pass (coverage > 80%)

**Dependencies:** BE-008, BE-009, BE-010, BE-013, BE-015, BE-017

---

### [BE-019] Implement Student Service (Read Operations)
**Goal:** Implement retrieval and search operations.

**Technical Details:**
- Add methods to `StudentService` interface:
  - Method: `StudentDTO getStudentByKey(String studentKey)`
  - Method: `Page<StudentDTO> searchStudents(String lastName, String guardianName, StudentStatus status, Pageable pageable)`
  - Method: `List<EnrollmentHistoryDTO> getEnrollmentHistory(String studentKey)`
- Implement in `StudentServiceImpl`:
  - Add `@Cacheable` annotations for frequently accessed data
  - Implement pagination and sorting
  - Map entities to DTOs
  - Throw StudentNotFoundException if not found

**Acceptance Criteria:**
- Get by key returns correct student
- Search works with all filter combinations
- Pagination works correctly
- Cache is used for repeated queries
- Not found scenarios throw proper exceptions
- Unit tests pass

**Dependencies:** BE-018

---

### [BE-020] Implement Student Service (Update Operation)
**Goal:** Implement student profile update logic.

**Technical Details:**
- Add method to `StudentService` interface:
  - Method: `StudentDTO updateStudent(String studentKey, UpdateStudentRequest request)`
- Implement in `StudentServiceImpl`:
  - Annotate with `@Transactional`
  - Retrieve student by key
  - Validate mobile uniqueness (exclude current student)
  - Update fields: firstName, lastName, mobile, status
  - Handle optimistic locking (version field)
  - Create enrollment history record if status changed
  - Log audit trail
  - Evict cache: `@CacheEvict(value = "students", key = "#studentKey")`
  - Return updated StudentDTO
  - Handle OptimisticLockException

**Acceptance Criteria:**
- Student is updated successfully
- Status changes create enrollment history
- Optimistic locking prevents concurrent updates
- Mobile uniqueness is checked
- Cache is evicted
- Audit log is created
- Unit tests pass

**Dependencies:** BE-019

---

### [BE-021] Implement Student Service (Delete Operation)
**Goal:** Implement soft delete for student records.

**Technical Details:**
- Add method to `StudentService` interface:
  - Method: `void deleteStudent(String studentKey)`
- Implement in `StudentServiceImpl`:
  - Annotate with `@Transactional`
  - Retrieve student by key
  - Perform soft delete (set status to DELETED or flag)
  - Log audit trail
  - Evict cache
  - Note: Consider implementing soft delete with deleted_at timestamp

**Acceptance Criteria:**
- Student is soft deleted (not physically removed)
- Audit log is created
- Cache is evicted
- Deleted students don't appear in searches
- Unit tests pass

**Dependencies:** BE-020

---

## Phase 5: Student Service - Presentation Layer

### [BE-022] Implement Student REST Controller
**Goal:** Create REST API endpoints for Student operations.

**Technical Details:**
- Create `StudentController.java` in `presentation.controller` package:
  - Annotate with `@RestController`, `@RequestMapping("/api/v1/students")`
  - Inject StudentService
  - Endpoints:
    - `POST /api/v1/students` → createStudent()
    - `GET /api/v1/students/{studentKey}` → getStudent()
    - `GET /api/v1/students` → searchStudents()
    - `PUT /api/v1/students/{studentKey}` → updateStudent()
    - `DELETE /api/v1/students/{studentKey}` → deleteStudent()
  - Add `@Valid` for request body validation
  - Add OpenAPI/Swagger annotations: `@Operation`, `@ApiResponse`
  - Return appropriate HTTP status codes: 201, 200, 204, 404, 400, 409, 422
  - Add Location header for POST

**Acceptance Criteria:**
- All endpoints are accessible
- Request validation works
- Status codes are correct
- Response format matches API design
- OpenAPI documentation is generated
- Integration tests pass

**Dependencies:** BE-018, BE-019, BE-020, BE-021

---

### [BE-023] Implement Enrollment History REST Controller
**Goal:** Create REST API endpoint for enrollment history.

**Technical Details:**
- Create `EnrollmentHistoryController.java`:
  - Annotate with `@RestController`, `@RequestMapping("/api/v1/students/{studentKey}/enrollment-history")`
  - Endpoint: `GET /api/v1/students/{studentKey}/enrollment-history`
  - Return list of enrollment history records
  - Add OpenAPI annotations

**Acceptance Criteria:**
- Endpoint returns correct enrollment history
- Response format matches API design
- Integration tests pass

**Dependencies:** BE-019

---

### [BE-024] Implement Global Exception Handler
**Goal:** Create centralized exception handling for REST API.

**Technical Details:**
- Create `GlobalExceptionHandler.java` in `presentation.exception` package:
  - Annotate with `@RestControllerAdvice`
  - Handle exceptions:
    - `StudentNotFoundException` → 404 Not Found
    - `DuplicateMobileException` → 409 Conflict
    - `AgeValidationException` → 422 Unprocessable Entity
    - `MethodArgumentNotValidException` → 400 Bad Request
    - `OptimisticLockException` → 409 Conflict
    - `Exception` → 500 Internal Server Error
  - Return RFC 7807 ProblemDetail format
  - Include correlation ID in response
  - Log exceptions with appropriate levels

**Acceptance Criteria:**
- All exceptions are handled correctly
- Error responses follow RFC 7807 format
- Correlation IDs are included
- Appropriate HTTP status codes are returned
- Error messages are user-friendly
- Integration tests verify error handling

**Dependencies:** BE-002, BE-022

---

## Phase 6: Configuration Service - Domain Layer

### [BE-025] Create School Profile Domain Entity
**Goal:** Implement SchoolProfile domain model.

**Technical Details:**
- Create `SchoolProfile.java` in `domain.model` package:
  - Fields: schoolId, schoolName, schoolCode, schoolLogoUrl, address, contactNumber, email, principalName, establishedDate, version, createdAt, updatedAt, createdBy, updatedBy
  - Use Lombok annotations
  - Add validation methods

**Acceptance Criteria:**
- Entity compiles with all fields
- Unit tests verify entity creation

**Dependencies:** BE-002

---

### [BE-026] Create Configuration Setting Domain Entity
**Goal:** Implement ConfigurationSetting domain model.

**Technical Details:**
- Create `ConfigurationSetting.java` in `domain.model` package:
  - Fields: settingId, schoolId, category, settingKey, settingValue, description, version, createdAt, updatedAt, updatedBy
  - Create `SettingCategory.java` enum (GENERAL, ACADEMIC, FINANCIAL)
  - Use Lombok annotations
  - Add validation methods

**Acceptance Criteria:**
- Entity compiles with all fields
- Category enum is defined
- Unit tests pass

**Dependencies:** BE-002

---

### [BE-027] Create Configuration Repository Interfaces
**Goal:** Define repository contracts for Configuration data access.

**Technical Details:**
- Create `SchoolProfileRepository.java` interface:
  - Method: `Optional<SchoolProfile> findBySchoolCode(String schoolCode)`
  - Method: `Optional<SchoolProfile> findFirst()` (single row expected)
- Create `ConfigurationSettingRepository.java` interface:
  - Method: `Optional<ConfigurationSetting> findBySettingKey(String settingKey)`
  - Method: `List<ConfigurationSetting> findByCategory(SettingCategory category)`
  - Method: `List<ConfigurationSetting> findBySchoolId(Long schoolId)`

**Acceptance Criteria:**
- Interfaces are defined with all methods
- Method signatures follow conventions

**Dependencies:** BE-025, BE-026

---

## Phase 7: Configuration Service - Infrastructure Layer

### [BE-028] Create JPA Configuration Entities
**Goal:** Implement JPA entities for Configuration persistence.

**Technical Details:**
- Create `SchoolProfileEntity.java`:
  - Annotate with `@Entity`, `@Table(name = "school_profile")`
  - Map all fields with `@Column` annotations
  - Add unique constraints on schoolName, schoolCode
  - Add `@Version` for optimistic locking
- Create `ConfigurationSettingEntity.java`:
  - Annotate with `@Entity`, `@Table(name = "configuration_setting")`
  - Map all fields
  - Add unique constraint on (schoolId, category, settingKey)
  - Add indexes on category, settingKey

**Acceptance Criteria:**
- JPA entities are properly annotated
- Database mappings are correct
- Constraints are defined

**Dependencies:** BE-025, BE-026

---

### [BE-029] Implement JPA Configuration Repositories
**Goal:** Create Spring Data JPA repository implementations.

**Technical Details:**
- Create `JpaSchoolProfileRepository.java` extending `JpaRepository<SchoolProfileEntity, Long>`
- Create `JpaConfigurationSettingRepository.java` extending `JpaRepository<ConfigurationSettingEntity, Long>`
- Create implementation classes for domain repository interfaces:
  - `SchoolProfileRepositoryImpl.java`
  - `ConfigurationSettingRepositoryImpl.java`
  - Handle entity-domain mapping

**Acceptance Criteria:**
- All repository methods work correctly
- Entity-domain mapping is correct
- Integration tests pass

**Dependencies:** BE-027, BE-028

---

### [BE-030] Create Configuration Database Migration Scripts
**Goal:** Create SQL migration scripts for Configuration schema.

**Technical Details:**
- Create migration files in `resources/db/migration/`:
  - `V1.0.0__Create_school_profile_table.sql`:
    - All columns as per DATABASE_SCHEMA.md
    - Unique constraints on schoolName, schoolCode
  - `V1.0.1__Create_configuration_setting_table.sql`:
    - All columns
    - Foreign key to school_profile
    - Unique constraint on (school_id, category, setting_key)
    - Indexes
  - `V1.1.0__Insert_default_settings.sql`:
    - Default school profile
    - Default configuration settings for all categories

**Acceptance Criteria:**
- All migration scripts execute successfully
- Tables are created correctly
- Default data is inserted

**Dependencies:** BE-003

---

## Phase 8: Configuration Service - Application Layer

### [BE-031] Create Configuration DTOs
**Goal:** Define Request and Response DTOs for Configuration API.

**Technical Details:**
- Create DTOs in `presentation.dto` package:
  - `SchoolProfileDTO.java` (record):
    - All school profile fields
  - `UpdateSchoolProfileRequest.java`:
    - Fields for update operation
    - Validation annotations
  - `ConfigurationSettingDTO.java` (record):
    - All configuration setting fields
  - `CreateSettingRequest.java`:
    - Fields: category, settingKey, settingValue, description
    - Validation annotations
  - `UpdateSettingRequest.java`:
    - Fields: settingValue, description

**Acceptance Criteria:**
- All DTOs compile correctly
- Validation is configured
- Record types are used for responses

**Dependencies:** BE-002

---

### [BE-032] Create MapStruct Mappers for Configuration
**Goal:** Implement DTO-Entity mapping for Configuration.

**Technical Details:**
- Create `SchoolProfileMapper.java` interface:
  - Method: `SchoolProfileDTO toDTO(SchoolProfile schoolProfile)`
  - Method: `SchoolProfile toEntity(UpdateSchoolProfileRequest request)`
- Create `ConfigurationSettingMapper.java` interface:
  - Method: `ConfigurationSettingDTO toDTO(ConfigurationSetting setting)`
  - Method: `ConfigurationSetting toEntity(CreateSettingRequest request)`

**Acceptance Criteria:**
- Mappers generate correct implementations
- All mappings work correctly
- Unit tests pass

**Dependencies:** BE-025, BE-026, BE-031

---

### [BE-033] Implement School Profile Service
**Goal:** Implement business logic for school profile management.

**Technical Details:**
- Create `SchoolProfileService.java` interface:
  - Method: `SchoolProfileDTO getSchoolProfile()`
  - Method: `SchoolProfileDTO createOrUpdateSchoolProfile(UpdateSchoolProfileRequest request)`
- Create `SchoolProfileServiceImpl.java`:
  - Annotate with `@Service`, `@Transactional`
  - Implement upsert logic (create if not exists, update if exists)
  - Cache school profile: `@Cacheable("schoolProfile")`
  - Evict cache on update: `@CacheEvict`

**Acceptance Criteria:**
- School profile can be retrieved
- Upsert operation works correctly
- Cache is used effectively
- Unit tests pass

**Dependencies:** BE-027, BE-029, BE-032

---

### [BE-034] Implement Configuration Setting Service
**Goal:** Implement business logic for configuration management.

**Technical Details:**
- Create `ConfigurationSettingService.java` interface:
  - Method: `List<ConfigurationSettingDTO> getAllSettings()`
  - Method: `List<ConfigurationSettingDTO> getSettingsByCategory(SettingCategory category)`
  - Method: `ConfigurationSettingDTO getSettingByKey(String settingKey)`
  - Method: `ConfigurationSettingDTO createSetting(CreateSettingRequest request)`
  - Method: `ConfigurationSettingDTO updateSetting(String settingKey, UpdateSettingRequest request)`
  - Method: `void deleteSetting(String settingKey)`
- Create `ConfigurationSettingServiceImpl.java`:
  - Annotate with `@Service`, `@Transactional`
  - Cache settings: `@Cacheable("configurations")`
  - Evict cache on modifications
  - Validate unique constraint (schoolId, category, settingKey)

**Acceptance Criteria:**
- All CRUD operations work correctly
- Category filtering works
- Cache is used effectively
- Duplicate key validation works
- Unit tests pass

**Dependencies:** BE-027, BE-029, BE-032

---

## Phase 9: Configuration Service - Presentation Layer

### [BE-035] Implement School Profile REST Controller
**Goal:** Create REST API endpoints for school profile.

**Technical Details:**
- Create `SchoolProfileController.java`:
  - Annotate with `@RestController`, `@RequestMapping("/api/v1/school/profile")`
  - Endpoints:
    - `GET /api/v1/school/profile` → getSchoolProfile()
    - `PUT /api/v1/school/profile` → createOrUpdateSchoolProfile()
  - Add OpenAPI annotations
  - Return appropriate status codes

**Acceptance Criteria:**
- All endpoints are accessible
- Response format matches API design
- Integration tests pass

**Dependencies:** BE-033

---

### [BE-036] Implement Configuration Setting REST Controller
**Goal:** Create REST API endpoints for configuration settings.

**Technical Details:**
- Create `ConfigurationSettingController.java`:
  - Annotate with `@RestController`, `@RequestMapping("/api/v1/configurations")`
  - Endpoints:
    - `GET /api/v1/configurations` → getAllSettings() or getSettingsByCategory()
    - `GET /api/v1/configurations/{settingKey}` → getSettingByKey()
    - `POST /api/v1/configurations` → createSetting()
    - `PUT /api/v1/configurations/{settingKey}` → updateSetting()
    - `DELETE /api/v1/configurations/{settingKey}` → deleteSetting()
  - Add query parameter for category filtering
  - Add OpenAPI annotations
  - Return appropriate status codes

**Acceptance Criteria:**
- All endpoints are accessible
- Category filtering works
- Response format matches API design
- Integration tests pass

**Dependencies:** BE-034

---

### [BE-037] Implement Configuration Global Exception Handler
**Goal:** Create exception handling for Configuration Service.

**Technical Details:**
- Create `GlobalExceptionHandler.java` similar to Student Service:
  - Handle SettingNotFoundException → 404
  - Handle duplicate key exceptions → 409
  - Handle validation exceptions → 400
  - Return RFC 7807 format

**Acceptance Criteria:**
- All exceptions are handled correctly
- Error responses follow RFC 7807 format
- Integration tests verify error handling

**Dependencies:** BE-002, BE-035, BE-036

---

## Phase 10: API Gateway

### [BE-038] Create API Gateway Project
**Goal:** Set up Spring Cloud Gateway for routing and cross-cutting concerns.

**Technical Details:**
- Create `api-gateway` module
- Add dependencies: `spring-cloud-starter-gateway`, `spring-boot-starter-data-redis-reactive`
- Create `ApiGatewayApplication.java` main class
- Configure routing in `application.yml`:
  - Route `/api/v1/students/**` to Student Service (port 8081)
  - Route `/api/v1/school/**` to Configuration Service (port 8082)
  - Route `/api/v1/configurations/**` to Configuration Service (port 8082)

**Acceptance Criteria:**
- Gateway starts successfully on port 8080
- Requests are routed to correct services
- All routes are functional

**Dependencies:** BE-001

---

### [BE-039] Implement Correlation ID Filter
**Goal:** Add correlation ID to all requests for distributed tracing.

**Technical Details:**
- Create `CorrelationIdFilter.java` in `filter` package:
  - Implement `GlobalFilter`
  - Generate UUID if X-Correlation-ID header is missing
  - Add/forward X-Correlation-ID header to downstream services
  - Log correlation ID with each request

**Acceptance Criteria:**
- Correlation ID is added to all requests
- Same correlation ID propagates to all services
- Logs include correlation IDs
- Unit tests pass

**Dependencies:** BE-038

---

### [BE-040] Implement Logging Filter
**Goal:** Add request/response logging at gateway level.

**Technical Details:**
- Create `LoggingFilter.java`:
  - Implement `GlobalFilter`
  - Log request: method, path, headers (excluding sensitive)
  - Log response: status code, duration
  - Include correlation ID in logs

**Acceptance Criteria:**
- All requests are logged
- Response times are captured
- Logs are structured (JSON format)
- Unit tests pass

**Dependencies:** BE-039

---

### [BE-041] Configure CORS
**Goal:** Set up CORS configuration for frontend access.

**Technical Details:**
- Create `CorsConfig.java` in `config` package:
  - Configure allowed origins:
    - Development: `http://localhost:3000`, `http://localhost:5173`
    - Production: `https://sms.school.com`
  - Allowed methods: GET, POST, PUT, DELETE, OPTIONS
  - Allowed headers: Content-Type, Authorization, X-Correlation-ID
  - Credentials: true
  - Max age: 86400 seconds

**Acceptance Criteria:**
- CORS headers are present in responses
- Preflight requests (OPTIONS) work correctly
- Frontend can make cross-origin requests
- Integration tests verify CORS

**Dependencies:** BE-038

---

### [BE-042] Configure Rate Limiting
**Goal:** Implement rate limiting to prevent API abuse.

**Technical Details:**
- Create `RateLimitConfig.java`:
  - Use Redis for rate limit storage
  - Configure limits:
    - Replenish rate: 100 requests per second
    - Burst capacity: 200 requests
  - Apply to all routes or specific routes
- Return 429 Too Many Requests when limit exceeded

**Acceptance Criteria:**
- Rate limits are enforced
- Redis stores rate limit data
- 429 status is returned when exceeded
- Retry-After header is included
- Integration tests verify rate limiting

**Dependencies:** BE-004, BE-038

---

## Phase 11: Testing

### [BE-043] Write Unit Tests for Student Service
**Goal:** Achieve 80%+ code coverage for Student Service.

**Technical Details:**
- Use JUnit 5, Mockito, AssertJ
- Test classes:
  - `StudentServiceTest.java`:
    - Test all service methods
    - Mock repository, validator, key generator
    - Verify exception handling
  - `StudentValidatorTest.java`:
    - Test age validation rules
    - Test mobile uniqueness validation
  - `StudentKeyGeneratorTest.java`:
    - Test key format
    - Test sequence generation
  - `StudentMapperTest.java`:
    - Test DTO-Entity mapping
    - Test age calculation

**Acceptance Criteria:**
- Unit test coverage > 80%
- All tests pass
- Edge cases are tested
- Exception scenarios are tested

**Dependencies:** BE-018, BE-019, BE-020, BE-021

---

### [BE-044] Write Integration Tests for Student Service
**Goal:** Test Student Service with real database using TestContainers.

**Technical Details:**
- Use TestContainers with PostgreSQL 18
- Test classes:
  - `StudentRepositoryIntegrationTest.java`:
    - Test all repository methods
    - Test queries, filters, pagination
    - Use real PostgreSQL container
  - `StudentApiIntegrationTest.java`:
    - Test REST endpoints end-to-end
    - Use REST Assured
    - Test request/response formats
    - Test error scenarios
  - `DatabaseIntegrationTest.java`:
    - Test Flyway migrations
    - Test database constraints
    - Test triggers

**Acceptance Criteria:**
- Integration tests use TestContainers
- All tests pass
- Database operations work correctly
- API endpoints return correct responses

**Dependencies:** BE-022, BE-023, BE-024

---

### [BE-045] Write Unit Tests for Configuration Service
**Goal:** Achieve 80%+ code coverage for Configuration Service.

**Technical Details:**
- Test classes:
  - `SchoolProfileServiceTest.java`
  - `ConfigurationSettingServiceTest.java`
  - Mapper tests
- Use mocking for repositories

**Acceptance Criteria:**
- Unit test coverage > 80%
- All tests pass

**Dependencies:** BE-033, BE-034

---

### [BE-046] Write Integration Tests for Configuration Service
**Goal:** Test Configuration Service with real database.

**Technical Details:**
- Test classes:
  - `SchoolProfileRepositoryIntegrationTest.java`
  - `ConfigurationSettingRepositoryIntegrationTest.java`
  - `ConfigurationApiIntegrationTest.java`
- Use TestContainers

**Acceptance Criteria:**
- Integration tests pass
- Database operations work correctly
- API endpoints return correct responses

**Dependencies:** BE-035, BE-036, BE-037

---

### [BE-047] Write Integration Tests for API Gateway
**Goal:** Test gateway routing and filters.

**Technical Details:**
- Test classes:
  - `GatewayRoutingTest.java`:
    - Test route to Student Service
    - Test route to Configuration Service
  - `CorrelationIdFilterTest.java`:
    - Verify correlation ID generation
    - Verify propagation to services
  - `RateLimitTest.java`:
    - Test rate limit enforcement
    - Test 429 response

**Acceptance Criteria:**
- All gateway tests pass
- Routing works correctly
- Filters function as expected

**Dependencies:** BE-038, BE-039, BE-040, BE-041, BE-042

---

## Phase 12: Documentation & Finalization

### [BE-048] Generate OpenAPI Documentation
**Goal:** Generate comprehensive API documentation using Springdoc.

**Technical Details:**
- Add Springdoc dependency: `springdoc-openapi-starter-webmvc-ui`
- Configure Springdoc in `application.yml`:
  - API docs path: `/api-docs`
  - Swagger UI path: `/swagger-ui.html`
- Ensure all endpoints have:
  - `@Operation` annotations with descriptions
  - `@ApiResponse` annotations for all status codes
  - Request/response examples
- Export OpenAPI spec to `specs/openapi/sms-api-v1.yaml`

**Acceptance Criteria:**
- OpenAPI documentation is accessible at `/swagger-ui.html`
- All endpoints are documented
- Request/response schemas are complete
- Examples are provided
- OpenAPI YAML file is generated

**Dependencies:** BE-022, BE-023, BE-035, BE-036

---

### [BE-049] Create Docker Images
**Goal:** Create Docker images for all backend services.

**Technical Details:**
- Create `Dockerfile` for each service:
  - Use multi-stage build
  - Base image: `eclipse-temurin:21-jre-alpine`
  - Copy JAR file
  - Expose appropriate port
  - Set ENTRYPOINT
- Create `.dockerignore` files
- Build images:
  - `docker build -t sms/student-service:1.0.0 .`
  - `docker build -t sms/config-service:1.0.0 .`
  - `docker build -t sms/api-gateway:1.0.0 .`

**Acceptance Criteria:**
- Docker images build successfully
- Images are optimized (Alpine base)
- Images run successfully
- Services start and connect to dependencies

**Dependencies:** BE-022, BE-036, BE-038

---

### [BE-050] Create Docker Compose for Local Development
**Goal:** Set up complete local development environment with Docker Compose.

**Technical Details:**
- Create `infrastructure/docker/docker-compose.yml`:
  - Services:
    - postgres (student_db, config_db)
    - redis
    - student-service
    - configuration-service
    - api-gateway
    - zipkin
    - prometheus
    - grafana
  - Define networks and volumes
  - Set environment variables
  - Configure health checks
  - Define service dependencies

**Acceptance Criteria:**
- `docker-compose up` starts all services
- Services can communicate with each other
- Databases are initialized with schemas
- Observability stack is accessible
- Health checks pass

**Dependencies:** BE-049

---

### [BE-051] Write README and Setup Documentation
**Goal:** Create comprehensive documentation for developers.

**Technical Details:**
- Create `backend/README.md`:
  - Project overview
  - Prerequisites (Java 21, Maven, Docker)
  - Setup instructions
  - Build commands
  - Run commands
  - Testing instructions
  - API documentation links
  - Troubleshooting guide
- Create service-specific READMEs:
  - `student-service/README.md`
  - `configuration-service/README.md`
  - `api-gateway/README.md`

**Acceptance Criteria:**
- Documentation is clear and complete
- Setup instructions work for new developers
- All commands are documented
- Troubleshooting section is helpful

**Dependencies:** BE-048, BE-050

---

## Summary

**Total Tasks:** 51

**Estimated Timeline:**
- Phase 1 (Setup): 2-3 days
- Phase 2-3 (Student Domain & Infrastructure): 3-4 days
- Phase 4-5 (Student Application & Presentation): 3-4 days
- Phase 6-7 (Configuration Domain & Infrastructure): 2-3 days
- Phase 8-9 (Configuration Application & Presentation): 2-3 days
- Phase 10 (API Gateway): 2 days
- Phase 11 (Testing): 3-4 days
- Phase 12 (Documentation & Finalization): 1-2 days

**Total Estimated Time:** 18-25 days

**Key Dependencies:**
- All tasks must be completed sequentially within their phase
- Each phase builds upon the previous phase
- Testing should be done continuously, not just in Phase 11

**Success Criteria:**
- All 51 tasks completed
- Code coverage > 80%
- All tests passing (unit, integration, API)
- API documentation generated
- Docker images built
- Services runnable via Docker Compose
