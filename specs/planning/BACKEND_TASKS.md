# Backend Implementation Plan
**School Management System - Spring Boot Developer**

**Version**: 1.0
**Date**: 2026-01-08
**Execution Model**: Waterfall / Single-Pass Implementation

---

## Overview

This document provides a **sequential, atomic task list** for implementing the complete Spring Boot backend for the School Management System. All tasks must be completed in order, as each task builds upon the previous ones.

**Scope**: Student Service and Configuration Service microservices with complete REST APIs, business logic, and data persistence.

**Constraints**:
- No JWT/OAuth implementation (basic auth or no auth)
- No database indexes, triggers, or stored procedures
- No migration tools (Flyway/Liquibase)
- Single SQL script for database setup

---

## Project Setup Tasks

### [BE-001] Initialize Student Service Spring Boot Project
**Goal**: Create the Student Service Maven project with Spring Boot 3.x

**Technical Details**:
- Use Spring Initializr or Maven archetype
- Group ID: `com.schoolms`
- Artifact ID: `student-service`
- Package name: `com.schoolms.student`
- Java version: 17 or 21
- Spring Boot version: 3.2.x or later
- Packaging: JAR

**Dependencies to Include**:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot DevTools
- Lombok
- MapStruct (1.5.5.Final)
- Validation (spring-boot-starter-validation)
- Spring Boot Actuator

**Dependencies**: None

---

### [BE-002] Initialize Configuration Service Spring Boot Project
**Goal**: Create the Configuration Service Maven project with Spring Boot 3.x

**Technical Details**:
- Same setup as BE-001 but:
- Artifact ID: `configuration-service`
- Package name: `com.schoolms.configuration`

**Dependencies to Include**: Same as BE-001

**Dependencies**: None

---

### [BE-003] Configure application.yml for Student Service
**Goal**: Set up database connection, server port, and application properties

**Technical Details**:
- Create `src/main/resources/application.yml`
- Server port: 8081
- PostgreSQL connection:
  - URL: `jdbc:postgresql://localhost:5433/studentdb`
  - Username: `student_service`
  - Driver: `org.postgresql.Driver`
- JPA/Hibernate settings:
  - `ddl-auto: validate` (no auto schema generation)
  - Dialect: `PostgreSQLDialect`
  - `show-sql: false`
- HikariCP connection pool settings
- Actuator endpoints configuration

**Dependencies**: Requires BE-001

---

### [BE-004] Configure application.yml for Configuration Service
**Goal**: Set up database connection, server port, and application properties

**Technical Details**:
- Create `src/main/resources/application.yml`
- Server port: 8082
- PostgreSQL connection:
  - URL: `jdbc:postgresql://localhost:5434/configdb`
  - Username: `config_service`
- Same JPA/Hibernate settings as BE-003

**Dependencies**: Requires BE-002

---

### [BE-005] Create Project Directory Structure for Student Service
**Goal**: Establish layered package structure following DDD

**Technical Details**:
Create packages under `com.schoolms.student`:
- `presentation.controller`
- `presentation.dto.request`
- `presentation.dto.response`
- `presentation.exception`
- `application.service`
- `application.mapper`
- `domain.model`
- `domain.repository`
- `domain.exception`
- `infrastructure.persistence.entity`
- `infrastructure.persistence.repository`
- `infrastructure.persistence.adapter`
- `infrastructure.config`

**Dependencies**: Requires BE-001

---

### [BE-006] Create Project Directory Structure for Configuration Service
**Goal**: Establish layered package structure following DDD

**Technical Details**:
Create packages under `com.schoolms.configuration`:
- Same structure as BE-005 but under `configuration` namespace

**Dependencies**: Requires BE-002

---

## Domain Layer Implementation - Student Service

### [BE-007] Create Student Domain Model
**Goal**: Implement the Student entity with domain logic

**Technical Details**:
- Class: `com.schoolms.student.domain.model.Student`
- Use factory method pattern: `Student.register(...)`
- Include business methods:
  - `getAge()`: Calculate age from DOB
  - `updateProfile(firstName, lastName, phone)`: Update editable fields
  - `activate()`, `deactivate()`: Status management
- Private validation method: `validate()`
- Fields: firstName, lastName, dateOfBirth, adhaarNumber, address, identificationMarks, guardianName, motherName, phone, email, status, createdAt, updatedAt, version
- StudentId value object
- No JPA annotations (pure domain model)

**Dependencies**: Requires BE-005

---

### [BE-008] Create StudentStatus Enum
**Goal**: Define student status constants

**Technical Details**:
- Enum: `com.schoolms.student.domain.model.StudentStatus`
- Values: `ACTIVE`, `INACTIVE`

**Dependencies**: Requires BE-005

---

### [BE-009] Create StudentId Value Object
**Goal**: Type-safe student ID with validation

**Technical Details**:
- Class: `com.schoolms.student.domain.model.StudentId`
- Immutable value object
- Validation: Must match pattern `STU-YYYY-NNNNN`
- Override `equals()`, `hashCode()`, `toString()`

**Dependencies**: Requires BE-005

---

### [BE-010] Create Student Repository Interface
**Goal**: Define domain repository contract

**Technical Details**:
- Interface: `com.schoolms.student.domain.repository.StudentRepository`
- Methods:
  - `Student save(Student student)`
  - `void delete(StudentId studentId)`
  - `Optional<Student> findById(StudentId studentId)`
  - `Optional<Student> findByPhone(String phone)`
  - `Optional<Student> findByEmail(String email)`
  - `Optional<Student> findByAdhaarNumber(String adhaarNumber)`
  - `List<Student> findAll()`
  - `List<Student> findByStatus(StudentStatus status)`
  - `List<Student> search(String query)`
  - `long countByStatus(StudentStatus status)`
  - `boolean existsByPhone(String phone)`
  - `boolean existsByEmail(String email)`
  - `boolean existsByAdhaarNumber(String adhaarNumber)`

**Dependencies**: Requires BE-007, BE-008, BE-009

---

### [BE-011] Create Domain Exceptions
**Goal**: Define custom exceptions for business rules

**Technical Details**:
- Package: `com.schoolms.student.domain.exception`
- Exceptions to create:
  - `StudentNotFoundException extends RuntimeException`
  - `DuplicatePhoneException extends RuntimeException`
  - `DuplicateEmailException extends RuntimeException`
  - `DuplicateAdhaarException extends RuntimeException`
  - `InvalidAgeException extends RuntimeException`
  - `ImmutableFieldException extends RuntimeException`

**Dependencies**: Requires BE-005

---

## Infrastructure Layer Implementation - Student Service

### [BE-012] Create StudentJpaEntity
**Goal**: JPA persistence entity mapping to students table

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity`
- Annotations: `@Entity`, `@Table(name = "students")`
- Fields match database columns exactly
- Use `@Version` for optimistic locking
- Lifecycle callbacks: `@PrePersist`, `@PreUpdate` for timestamps and age calculation
- No business logic (anemic model for persistence)

**Dependencies**: Requires BE-006

---

### [BE-013] Create StudentJpaRepository
**Goal**: Spring Data JPA repository for database operations

**Technical Details**:
- Interface: `com.schoolms.student.infrastructure.persistence.repository.StudentJpaRepository`
- Extends: `JpaRepository<StudentJpaEntity, Long>`
- Custom query methods:
  - `Optional<StudentJpaEntity> findByStudentId(String studentId)`
  - `Optional<StudentJpaEntity> findByPhone(String phone)`
  - `Optional<StudentJpaEntity> findByEmail(String email)`
  - `Optional<StudentJpaEntity> findByAdhaarNumber(String adhaarNumber)`
  - `List<StudentJpaEntity> findByStatus(StudentStatus status)`
  - `@Query` for full-text search across firstName, lastName, guardianName
  - `long countByStatus(StudentStatus status)`
  - `boolean existsByPhone(String phone)`
  - `boolean existsByEmail(String email)`
  - `boolean existsByAdhaarNumber(String adhaarNumber)`

**Dependencies**: Requires BE-012

---

### [BE-014] Create StudentEntityMapper
**Goal**: Map between domain model and JPA entity

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.persistence.adapter.StudentEntityMapper`
- Annotation: `@Component`
- Methods:
  - `StudentJpaEntity toJpaEntity(Student domain)`
  - `Student toDomain(StudentJpaEntity entity)`
- Handle StudentId value object conversion
- Handle StudentStatus enum mapping

**Dependencies**: Requires BE-007, BE-012

---

### [BE-015] Create StudentRepositoryAdapter
**Goal**: Implement domain repository using JPA repository

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.persistence.adapter.StudentRepositoryAdapter`
- Annotation: `@Component`
- Implements: `StudentRepository` (domain interface)
- Inject: `StudentJpaRepository`, `StudentEntityMapper`
- Delegate all methods to JPA repository
- Map entities to/from domain models using mapper

**Dependencies**: Requires BE-010, BE-013, BE-014

---

### [BE-016] Create DatabaseConfig
**Goal**: Configure HikariCP connection pool and JPA properties

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.config.DatabaseConfig`
- Annotation: `@Configuration`
- Configure HikariCP:
  - Maximum pool size: 20
  - Minimum idle: 5
  - Connection timeout: 30000ms
- Enable JPA repositories scanning

**Dependencies**: Requires BE-006

---

### [BE-017] Create StudentIdGenerator Service
**Goal**: Generate auto-increment student IDs in format STU-YYYY-NNNNN

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.generator.StudentIdGenerator`
- Annotation: `@Component`
- Method: `String generateNextStudentId()`
- Implementation:
  - Query database for max student ID for current year
  - Extract sequence number, increment
  - Format as STU-{YEAR}-{NNNNN} (5-digit zero-padded)
- Thread-safe implementation

**Dependencies**: Requires BE-013

---

## Application Layer Implementation - Student Service

### [BE-018] Create DTOs - Request Objects
**Goal**: Define API request data transfer objects

**Technical Details**:
- Package: `com.schoolms.student.presentation.dto.request`
- Create classes:
  - `CreateStudentRequest`: All fields except id, status, createdAt, updatedAt
  - `UpdateStudentRequest`: Only firstName, lastName, phone, status (all optional)
- Use Java Records (record keyword)
- Include validation annotations:
  - `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Pattern`, `@Past`

**Dependencies**: Requires BE-005

---

### [BE-019] Create DTOs - Response Objects
**Goal**: Define API response data transfer objects

**Technical Details**:
- Package: `com.schoolms.student.presentation.dto.response`
- Create classes:
  - `StudentResponse`: All student fields (Java Record)
  - `StudentListResponse`: Contains List<StudentResponse>, totalCount, activeCount, inactiveCount
  - `StudentStatisticsResponse`: totalStudents, activeStudents, inactiveStudents

**Dependencies**: Requires BE-005

---

### [BE-020] Create StudentMapper Interface
**Goal**: Map between domain models and DTOs using MapStruct

**Technical Details**:
- Interface: `com.schoolms.student.application.mapper.StudentMapper`
- Annotation: `@Mapper(componentModel = "spring")`
- Methods:
  - `StudentResponse toResponse(Student student)`
  - `List<StudentResponse> toResponseList(List<Student> students)`
  - Custom mappings for StudentId value object

**Dependencies**: Requires BE-007, BE-019

---

### [BE-021] Create StudentApplicationService
**Goal**: Orchestrate use cases, coordinate domain and infrastructure

**Technical Details**:
- Class: `com.schoolms.student.application.service.StudentApplicationService`
- Annotations: `@Service`, `@Transactional`
- Inject: `StudentRepository`, `StudentMapper`, `StudentIdGenerator`
- Implement methods:
  - `StudentResponse createStudent(CreateStudentRequest request)`
    - Check uniqueness (phone, email, adhaar)
    - Generate student ID
    - Create domain entity using factory method
    - Save via repository
    - Map to response DTO
  - `StudentResponse updateStudent(String studentId, UpdateStudentRequest request)`
    - Fetch student, throw exception if not found
    - Validate immutable fields not included
    - Check phone uniqueness if changed
    - Update via domain methods
    - Save and return
  - `void deleteStudent(String studentId)`
  - `StudentResponse getStudent(String studentId)` (read-only)
  - `StudentListResponse listStudents(String search, StudentStatus status)` (read-only)
  - `StudentStatisticsResponse getStatistics()` (read-only)
- Add logging at INFO level for commands
- Handle domain exceptions

**Dependencies**: Requires BE-010, BE-017, BE-018, BE-019, BE-020

---

## Presentation Layer Implementation - Student Service

### [BE-022] Create GlobalExceptionHandler
**Goal**: Centralized exception handling with RFC 7807 ProblemDetail

**Technical Details**:
- Class: `com.schoolms.student.presentation.exception.GlobalExceptionHandler`
- Annotation: `@RestControllerAdvice`
- Handle exceptions:
  - `StudentNotFoundException` -> 404 NOT_FOUND
  - `DuplicatePhoneException`, `DuplicateEmailException`, `DuplicateAdhaarException` -> 409 CONFLICT
  - `InvalidAgeException`, `ImmutableFieldException` -> 422 UNPROCESSABLE_ENTITY
  - `MethodArgumentNotValidException` -> 400 BAD_REQUEST (validation errors)
  - `OptimisticLockingFailureException` -> 409 CONFLICT
  - `Exception` -> 500 INTERNAL_SERVER_ERROR
- Use Spring `ProblemDetail` for RFC 7807 compliance
- Include timestamp, traceId, field-level errors

**Dependencies**: Requires BE-011

---

### [BE-023] Create StudentController
**Goal**: REST API endpoints for student operations

**Technical Details**:
- Class: `com.schoolms.student.presentation.controller.StudentController`
- Annotations: `@RestController`, `@RequestMapping("/api/v1/students")`
- Inject: `StudentApplicationService`
- Endpoints:
  - `GET /api/v1/students` - List all students (query params: search, status, page, size)
    - Returns `StudentListResponse`
    - Status: 200 OK
  - `GET /api/v1/students/{id}` - Get student by ID
    - Returns `StudentResponse`
    - Status: 200 OK, 404 NOT_FOUND
  - `POST /api/v1/students` - Create student
    - Request body: `CreateStudentRequest` (validated with `@Valid`)
    - Returns `StudentResponse`
    - Status: 201 CREATED, Location header
  - `PATCH /api/v1/students/{id}` - Update student
    - Request body: `UpdateStudentRequest` (validated with `@Valid`)
    - Returns `StudentResponse`
    - Status: 200 OK, 404 NOT_FOUND, 409 CONFLICT
  - `DELETE /api/v1/students/{id}` - Delete student
    - Returns: void
    - Status: 204 NO_CONTENT, 404 NOT_FOUND
  - `GET /api/v1/students/statistics` - Get statistics
    - Returns `StudentStatisticsResponse`
    - Status: 200 OK
  - `POST /api/v1/students/validate-phone` - Validate phone uniqueness
    - Request body: `{ phone, excludeStudentId? }`
    - Returns `{ isUnique: boolean }`
    - Status: 200 OK

**Dependencies**: Requires BE-021, BE-022

---

### [BE-024] Add CORS Configuration
**Goal**: Enable frontend to call backend APIs

**Technical Details**:
- Class: `com.schoolms.student.infrastructure.config.CorsConfig`
- Annotation: `@Configuration`
- Implement `WebMvcConfigurer`
- Allow origins: `http://localhost:5173` (Vite dev server)
- Allow methods: GET, POST, PATCH, DELETE, OPTIONS
- Allow headers: Content-Type, Accept
- Allow credentials: true

**Dependencies**: Requires BE-006

---

### [BE-025] Configure Actuator Endpoints
**Goal**: Health checks and metrics for monitoring

**Technical Details**:
- Configure in `application.yml`:
  - Expose: health, metrics, info
  - Health details: always show
- Endpoints:
  - `/actuator/health` - Application health
  - `/actuator/metrics` - Custom metrics

**Dependencies**: Requires BE-003

---

## Domain Layer Implementation - Configuration Service

### [BE-026] Create Configuration Domain Model
**Goal**: Implement the Configuration entity

**Technical Details**:
- Class: `com.schoolms.configuration.domain.model.Configuration`
- Fields: id, category, key, value, description, createdAt, lastUpdated, version
- Factory method: `Configuration.create(...)`
- Business methods: `updateValue(String newValue)`, `updateDescription(String desc)`
- Validation method

**Dependencies**: Requires BE-006

---

### [BE-027] Create ConfigCategory Enum
**Goal**: Define configuration categories

**Technical Details**:
- Enum: `com.schoolms.configuration.domain.model.ConfigCategory`
- Values: `GENERAL`, `ACADEMIC`, `FINANCE`, `SYSTEM`

**Dependencies**: Requires BE-006

---

### [BE-028] Create Configuration Repository Interface
**Goal**: Define domain repository contract

**Technical Details**:
- Interface: `com.schoolms.configuration.domain.repository.ConfigurationRepository`
- Methods:
  - `Configuration save(Configuration config)`
  - `void delete(Long id)`
  - `Optional<Configuration> findById(Long id)`
  - `Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key)`
  - `List<Configuration> findAll()`
  - `List<Configuration> findByCategory(ConfigCategory category)`
  - `boolean existsByCategoryAndKey(ConfigCategory category, String key)`

**Dependencies**: Requires BE-026, BE-027

---

### [BE-029] Create Configuration Domain Exceptions
**Goal**: Define custom exceptions

**Technical Details**:
- Package: `com.schoolms.configuration.domain.exception`
- Exceptions:
  - `ConfigurationNotFoundException extends RuntimeException`
  - `DuplicateConfigKeyException extends RuntimeException`

**Dependencies**: Requires BE-006

---

## Infrastructure Layer Implementation - Configuration Service

### [BE-030] Create ConfigurationJpaEntity
**Goal**: JPA entity for configuration_settings table

**Technical Details**:
- Class: `com.schoolms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity`
- Annotations: `@Entity`, `@Table(name = "configuration_settings")`
- Fields: id, category, key, value, description, createdAt, lastUpdated, version
- Use `@Version` for optimistic locking
- Lifecycle callbacks for timestamps

**Dependencies**: Requires BE-006

---

### [BE-031] Create ConfigurationJpaRepository
**Goal**: Spring Data JPA repository

**Technical Details**:
- Interface: `com.schoolms.configuration.infrastructure.persistence.repository.ConfigurationJpaRepository`
- Extends: `JpaRepository<ConfigurationJpaEntity, Long>`
- Methods:
  - `Optional<ConfigurationJpaEntity> findByCategoryAndKey(ConfigCategory, String)`
  - `List<ConfigurationJpaEntity> findByCategory(ConfigCategory)`
  - `boolean existsByCategoryAndKey(ConfigCategory, String)`

**Dependencies**: Requires BE-030

---

### [BE-032] Create ConfigurationEntityMapper
**Goal**: Map between domain and JPA entity

**Technical Details**:
- Class: `com.schoolms.configuration.infrastructure.persistence.adapter.ConfigurationEntityMapper`
- Annotation: `@Component`
- Methods:
  - `ConfigurationJpaEntity toJpaEntity(Configuration)`
  - `Configuration toDomain(ConfigurationJpaEntity)`

**Dependencies**: Requires BE-026, BE-030

---

### [BE-033] Create ConfigurationRepositoryAdapter
**Goal**: Implement domain repository

**Technical Details**:
- Class: `com.schoolms.configuration.infrastructure.persistence.adapter.ConfigurationRepositoryAdapter`
- Annotation: `@Component`
- Implements: `ConfigurationRepository`
- Inject: `ConfigurationJpaRepository`, `ConfigurationEntityMapper`

**Dependencies**: Requires BE-028, BE-031, BE-032

---

## Application Layer Implementation - Configuration Service

### [BE-034] Create Configuration DTOs
**Goal**: Request and response objects

**Technical Details**:
- Package: `com.schoolms.configuration.presentation.dto`
- Create:
  - `CreateConfigurationRequest` (category, key, value, description)
  - `UpdateConfigurationRequest` (value, description - optional)
  - `ConfigurationResponse` (all fields)
  - `ConfigurationListResponse` (List<ConfigurationResponse>, totalCount)
- Use Java Records with validation annotations

**Dependencies**: Requires BE-006

---

### [BE-035] Create ConfigurationMapper
**Goal**: MapStruct mapper for DTOs

**Technical Details**:
- Interface: `com.schoolms.configuration.application.mapper.ConfigurationMapper`
- Annotation: `@Mapper(componentModel = "spring")`
- Methods:
  - `ConfigurationResponse toResponse(Configuration)`
  - `List<ConfigurationResponse> toResponseList(List<Configuration>)`

**Dependencies**: Requires BE-026, BE-034

---

### [BE-036] Create ConfigurationApplicationService
**Goal**: Configuration use cases

**Technical Details**:
- Class: `com.schoolms.configuration.application.service.ConfigurationApplicationService`
- Annotations: `@Service`, `@Transactional`
- Inject: `ConfigurationRepository`, `ConfigurationMapper`
- Methods:
  - `ConfigurationResponse createConfiguration(CreateConfigurationRequest)`
  - `ConfigurationResponse updateConfiguration(Long id, UpdateConfigurationRequest)`
  - `void deleteConfiguration(Long id)`
  - `ConfigurationResponse getConfiguration(Long id)` (read-only)
  - `ConfigurationListResponse listConfigurations(ConfigCategory category)` (read-only)

**Dependencies**: Requires BE-028, BE-034, BE-035

---

## Presentation Layer Implementation - Configuration Service

### [BE-037] Create Configuration GlobalExceptionHandler
**Goal**: Exception handling for Configuration API

**Technical Details**:
- Class: `com.schoolms.configuration.presentation.exception.GlobalExceptionHandler`
- Same pattern as BE-022
- Handle: ConfigurationNotFoundException (404), DuplicateConfigKeyException (409)

**Dependencies**: Requires BE-029

---

### [BE-038] Create ConfigurationController
**Goal**: REST API for configurations

**Technical Details**:
- Class: `com.schoolms.configuration.presentation.controller.ConfigurationController`
- Annotations: `@RestController`, `@RequestMapping("/api/v1/configurations")`
- Inject: `ConfigurationApplicationService`
- Endpoints:
  - `GET /api/v1/configurations` (query param: category)
  - `GET /api/v1/configurations/{id}`
  - `POST /api/v1/configurations`
  - `PATCH /api/v1/configurations/{id}`
  - `DELETE /api/v1/configurations/{id}`

**Dependencies**: Requires BE-036, BE-037

---

### [BE-039] Add CORS Configuration for Configuration Service
**Goal**: Enable frontend access

**Technical Details**:
- Same as BE-024 but for Configuration Service
- Port: 8082

**Dependencies**: Requires BE-006

---

### [BE-040] Configure Actuator for Configuration Service
**Goal**: Health checks and metrics

**Technical Details**:
- Same as BE-025 but in Configuration Service

**Dependencies**: Requires BE-004

---

## Testing Tasks

### [BE-041] Create Unit Tests for Student Domain Model
**Goal**: Test Student entity business logic

**Technical Details**:
- Test class: `StudentTest`
- Use JUnit 5, AssertJ
- Test scenarios:
  - Valid student creation
  - Age validation (3-18)
  - Age calculation
  - Profile update
  - Status change (activate/deactivate)
  - Invalid data handling

**Dependencies**: Requires BE-007

---

### [BE-042] Create Unit Tests for StudentApplicationService
**Goal**: Test service layer logic

**Technical Details**:
- Test class: `StudentApplicationServiceTest`
- Use Mockito for mocking repositories
- Test scenarios:
  - Create student success
  - Create student with duplicate phone (expect exception)
  - Update student success
  - Update immutable field (expect exception)
  - Delete student

**Dependencies**: Requires BE-021

---

### [BE-043] Create Integration Tests for StudentController
**Goal**: Test REST API endpoints end-to-end

**Technical Details**:
- Test class: `StudentControllerIntegrationTest`
- Use `@SpringBootTest`, TestRestTemplate
- Use TestContainers for PostgreSQL
- Test all endpoints with various scenarios
- Verify HTTP status codes, response bodies

**Dependencies**: Requires BE-023

---

### [BE-044] Create Unit Tests for Configuration Domain Model
**Goal**: Test Configuration entity

**Technical Details**:
- Test class: `ConfigurationTest`
- Test creation, update, validation

**Dependencies**: Requires BE-026

---

### [BE-045] Create Unit Tests for ConfigurationApplicationService
**Goal**: Test configuration service logic

**Technical Details**:
- Test class: `ConfigurationApplicationServiceTest`
- Mock repositories
- Test CRUD operations

**Dependencies**: Requires BE-036

---

### [BE-046] Create Integration Tests for ConfigurationController
**Goal**: Test configuration REST API

**Technical Details**:
- Test class: `ConfigurationControllerIntegrationTest`
- Use TestContainers
- Test all endpoints

**Dependencies**: Requires BE-038

---

## Build and Deployment Tasks

### [BE-047] Create Docker Compose for Local Development
**Goal**: Run PostgreSQL, Redis, and services locally

**Technical Details**:
- Create `docker-compose.yml` at project root
- Services:
  - postgres-student (port 5433)
  - postgres-config (port 5434)
  - redis (port 6379)
- Initialize databases with schema from `school_management.sql`

**Dependencies**: None

---

### [BE-048] Create Dockerfile for Student Service
**Goal**: Containerize Student Service

**Technical Details**:
- Multi-stage build (Maven + JRE)
- Base image: eclipse-temurin:21-jre-alpine
- Expose port 8081

**Dependencies**: Requires BE-023

---

### [BE-049] Create Dockerfile for Configuration Service
**Goal**: Containerize Configuration Service

**Technical Details**:
- Same as BE-048
- Expose port 8082

**Dependencies**: Requires BE-038

---

### [BE-050] Create README for Backend
**Goal**: Documentation for running services

**Technical Details**:
- File: `backend/README.md`
- Include:
  - Prerequisites
  - Database setup instructions
  - How to run Student Service
  - How to run Configuration Service
  - API documentation links
  - Testing instructions

**Dependencies**: Requires all backend tasks

---

## Summary

**Total Tasks**: 50
**Estimated Effort**: 15-20 developer days (single-pass implementation)

**Execution Order**: Sequential (BE-001 through BE-050)

**Key Deliverables**:
1. Student Service (8081): Complete CRUD API for students
2. Configuration Service (8082): Complete CRUD API for configurations
3. Database schema: Pre-initialized with sample data
4. Unit tests: 80%+ coverage
5. Integration tests: All endpoints tested
6. Docker setup: Ready for local development

**Next Phase**: Frontend implementation to integrate with these APIs.
