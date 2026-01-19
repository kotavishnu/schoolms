# Backend Implementation Plan - School Management System
**Waterfall / Single-Pass Implementation**
**Date**: 2026-01-14
**Architecture**: Microservices (Student Service + Configuration Service)

---

## Overview
This plan provides a sequential, atomic task breakdown for implementing the Student and Configuration microservices as a single continuous execution flow (no sprints). All tasks must be completed in order due to dependencies.

**Critical Constraints**:
- NO JWT/OAuth (basic auth or no auth)
- NO complex database features (indexes, triggers, stored procedures)
- NO migration tools (Flyway/Liquibase)
- Single SQL script execution only

---

## Phase 1: Foundation Setup

### [BE-001] Initialize Student Service Project Structure
**Goal**: Set up Spring Boot microservice for Student Management.

**Technical Details**:
- Create Maven/Gradle project with Spring Boot 3.3.x
- Group ID: `com.school.student`
- Artifact ID: `student-service`
- Package structure:
  ```
  com.school.student/
  ├── domain/model/          # Student entity
  ├── domain/repository/     # JPA repository
  ├── application/service/   # Business logic
  ├── application/dto/       # Request/Response DTOs
  ├── infrastructure/        # Config, exceptions
  └── presentation/          # REST controllers
  ```

**Dependencies**:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- postgresql (runtime)
- lombok
- mapstruct (for DTO mapping)
- springdoc-openapi-starter-webmvc-ui:2.7.0

**Configuration** (`application.yml`):
```yaml
server:
  port: 8081
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/student_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
```

**Dependencies**: None

---

### [BE-002] Initialize Configuration Service Project Structure
**Goal**: Set up Spring Boot microservice for Configuration Management.

**Technical Details**:
- Create Maven/Gradle project with Spring Boot 3.3.x
- Group ID: `com.school.configuration`
- Artifact ID: `configuration-service`
- Package structure: Same as BE-001
- Port: 8082
- Database: `configuration_db` on port 5434

**Dependencies**:
- Same as BE-001

**Dependencies**: None (can run in parallel with BE-001)

---

### [BE-003] Execute Database Schema Creation
**Goal**: Create PostgreSQL databases and tables using the SQL script.

**Technical Details**:
- Use Docker Compose to create two PostgreSQL instances:
  - Student DB: port 5433
  - Configuration DB: port 5434
- Execute `specs/planning/school_management.sql` on each database
- Verify tables created: `students`, `enrollment_history`, `configuration_settings`

**Docker Compose** (`backend/docker-compose.yml`):
```yaml
services:
  student-db:
    image: postgres:18
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: ${DB_USERNAME:-postgres}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}
    ports:
      - "5433:5432"
    volumes:
      - student-data:/var/lib/postgresql/data

  config-db:
    image: postgres:18
    environment:
      POSTGRES_DB: configuration_db
      POSTGRES_USER: ${DB_USERNAME:-postgres}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}
    ports:
      - "5434:5432"
    volumes:
      - config-data:/var/lib/postgresql/data

volumes:
  student-data:
  config-data:
```

**Dependencies**: None

---

## Phase 2: Student Service - Domain Layer

### [BE-004] Create Student Entity
**Goal**: Implement JPA entity for Student with all validations.

**Technical Details**:
- **File**: `domain/model/Student.java`
- **Annotations**: `@Entity`, `@Table(name = "students")`, `@Version`
- **Fields**:
  - `id` (Long, @Id, @GeneratedValue)
  - `studentId` (String, unique, generated)
  - `firstName`, `lastName` (String, validation)
  - `dateOfBirth` (LocalDate)
  - `aadhaarNumber` (String, 12 digits, unique)
  - `identificationMark` (String, nullable)
  - `address` (String)
  - `fathersName`, `mothersName` (String)
  - `mobile` (String, 10 digits, unique)
  - `email` (String, unique)
  - `status` (Enum: ACTIVE/INACTIVE, default ACTIVE)
  - `version` (Integer, for optimistic locking)
  - `createdAt`, `updatedAt` (LocalDateTime, @CreatedDate/@LastModifiedDate)

**Validation**:
- `@NotBlank` on required fields
- `@Pattern` for mobile, aadhaar
- `@Email` for email
- Custom `@ValidAge` annotation (3-18 years)

**Dependencies**: Requires BE-001, BE-003

---

### [BE-005] Create Student Repository
**Goal**: JPA repository for Student CRUD operations.

**Technical Details**:
- **File**: `domain/repository/StudentRepository.java`
- **Interface**: Extends `JpaRepository<Student, Long>`
- **Custom Queries**:
  ```java
  Optional<Student> findByStudentId(String studentId);
  Optional<Student> findByMobile(String mobile);
  Optional<Student> findByEmail(String email);
  Optional<Student> findByAadhaarNumber(String aadhaar);
  Page<Student> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
  Page<Student> findByStatus(String status, Pageable pageable);
  Long countByStatus(String status);
  ```

**Dependencies**: Requires BE-004

---

### [BE-006] Create Enrollment History Entity and Repository
**Goal**: Implement enrollment tracking.

**Technical Details**:
- **Entity**: `domain/model/EnrollmentHistory.java`
- **Repository**: `domain/repository/EnrollmentHistoryRepository.java`
- **Relationship**: Many-to-One with Student
- **Custom Query**: `findByStudent_StudentIdOrderByEnrollmentDateDesc(String studentId)`

**Dependencies**: Requires BE-004

---

## Phase 3: Student Service - Application Layer

### [BE-007] Create Student DTOs
**Goal**: Define request/response data transfer objects.

**Technical Details**:
- **File**: `application/dto/StudentDTO.java` (package with all DTOs)

**DTOs Required**:

1. **StudentCreateRequest**:
   - All fields except `id`, `studentId`, `createdAt`, `updatedAt`, `version`
   - Validation annotations

2. **StudentUpdateRequest**:
   - Only editable fields: `firstName`, `lastName`, `mobile`, `status`, `version` (for optimistic locking)
   - All other fields immutable

3. **StudentResponse**:
   - All fields including computed `age`
   - Format timestamps as ISO 8601

4. **StudentSearchRequest**:
   - `lastName`, `status`, pagination params

**Dependencies**: Requires BE-004

---

### [BE-008] Create Student Mapper
**Goal**: MapStruct mapper for Entity ↔ DTO conversion.

**Technical Details**:
- **File**: `application/mapper/StudentMapper.java`
- **Framework**: MapStruct
- **Methods**:
  ```java
  StudentResponse toResponse(Student student);
  Student toEntity(StudentCreateRequest request);
  void updateEntityFromRequest(StudentUpdateRequest request, @MappingTarget Student student);
  ```
- **Custom Mapping**: Calculate `age` from `dateOfBirth` in `toResponse`

**Dependencies**: Requires BE-007

---

### [BE-009] Create Student Service Interface and Implementation
**Goal**: Business logic for Student CRUD and validation.

**Technical Details**:
- **Interface**: `application/service/StudentService.java`
- **Implementation**: `application/service/impl/StudentServiceImpl.java`

**Methods**:

1. **`createStudent(StudentCreateRequest)`**:
   - Validate age (3-18)
   - Check mobile/email/aadhaar uniqueness
   - Generate `studentId` (format: `STD-YYYYMMDD-XXXX`)
   - Set default status to ACTIVE
   - Save and return response

2. **`updateStudent(String studentId, StudentUpdateRequest)`**:
   - Validate only editable fields changed
   - Check mobile uniqueness (excluding current student)
   - Handle optimistic locking (version check)
   - Update and return response

3. **`getStudentById(String studentId)`**:
   - Find by studentId
   - Throw `StudentNotFoundException` if not found

4. **`searchStudents(StudentSearchRequest)`**:
   - Support lastName search (case-insensitive)
   - Support status filter
   - Return paginated response

5. **`deleteStudent(String studentId)`**:
   - Soft delete or hard delete (clarify requirement)
   - Cascade to enrollment history

6. **`validatePhoneUniqueness(String phone, String excludeId)`**:
   - Check if phone exists
   - Exclude current student if editing

7. **`getStudentStatistics()`**:
   - Return total count, active count, inactive count

**Exception Handling**:
- `InvalidAgeException`
- `DuplicateFieldException` (mobile, email, aadhaar)
- `StudentNotFoundException`
- `OptimisticLockException`

**Dependencies**: Requires BE-005, BE-008

---

### [BE-010] Create Enrollment Service
**Goal**: Manage enrollment history operations.

**Technical Details**:
- **Service**: `application/service/EnrollmentService.java`
- **Methods**:
  - `createEnrollment(String studentId, EnrollmentCreateRequest)`
  - `getEnrollmentHistory(String studentId)`
  - Validate no duplicate enrollment for same academic year

**Dependencies**: Requires BE-006, BE-009

---

## Phase 4: Student Service - Presentation Layer

### [BE-011] Create Student Controller
**Goal**: REST API endpoints for student operations.

**Technical Details**:
- **File**: `presentation/controller/StudentController.java`
- **Base Path**: `/api/v1/students`
- **OpenAPI Annotations**: `@Tag`, `@Operation`, `@ApiResponse`

**Endpoints**:

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| GET | `/` | Search/list students | Query params | Page<StudentResponse> |
| GET | `/{studentId}` | Get student by ID | - | StudentResponse |
| POST | `/` | Create student | StudentCreateRequest | StudentResponse (201) |
| PUT | `/{studentId}` | Update student | StudentUpdateRequest | StudentResponse |
| DELETE | `/{studentId}` | Delete student | - | 204 No Content |
| POST | `/validate-phone` | Check phone uniqueness | PhoneValidationRequest | Boolean |
| GET | `/statistics` | Get counts | - | StatisticsResponse |

**Validation**:
- `@Valid` on request bodies
- `@RequestParam` validation for search

**CORS Configuration**: Allow `http://localhost:5173`, `http://localhost:5174`

**Dependencies**: Requires BE-009

---

### [BE-012] Create Enrollment Controller
**Goal**: REST API for enrollment history.

**Technical Details**:
- **Path**: `/api/v1/students/{studentId}/enrollment-history`
- **Methods**:
  - GET: Retrieve history
  - POST: Create new enrollment

**Dependencies**: Requires BE-010

---

## Phase 5: Student Service - Infrastructure

### [BE-013] Create Global Exception Handler
**Goal**: Centralized exception handling with RFC 7807 Problem Details.

**Technical Details**:
- **File**: `infrastructure/exception/GlobalExceptionHandler.java`
- **Annotation**: `@RestControllerAdvice`

**Handlers**:
- `@ExceptionHandler(MethodArgumentNotValidException.class)` → 400
- `@ExceptionHandler(StudentNotFoundException.class)` → 404
- `@ExceptionHandler(DuplicateFieldException.class)` → 409
- `@ExceptionHandler(OptimisticLockException.class)` → 409
- `@ExceptionHandler(InvalidAgeException.class)` → 422

**Response Format** (RFC 7807):
```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Mobile number is already registered",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-14T10:30:00Z",
  "correlationId": "uuid",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number must be unique",
      "code": "DUPLICATE_MOBILE"
    }
  ]
}
```

**Dependencies**: Requires BE-011

---

### [BE-014] Create Custom Exceptions
**Goal**: Define domain-specific exceptions.

**Technical Details**:
- **File**: `infrastructure/exception/` package
- **Classes**:
  - `StudentNotFoundException` extends `RuntimeException`
  - `DuplicateFieldException` extends `RuntimeException`
  - `InvalidAgeException` extends `RuntimeException`

**Dependencies**: None

---

### [BE-015] Configure OpenAPI Documentation
**Goal**: Swagger UI for API documentation.

**Technical Details**:
- **File**: `infrastructure/config/OpenApiConfig.java`
- **Configuration**:
  ```java
  @Bean
  public OpenAPI customOpenAPI() {
      return new OpenAPI()
          .info(new Info()
              .title("Student Service API")
              .version("1.0.0")
              .description("School Management System - Student Module"));
  }
  ```
- **URL**: `http://localhost:8081/swagger-ui.html`

**Dependencies**: Requires BE-011

---

### [BE-016] Configure CORS
**Goal**: Enable cross-origin requests from frontend.

**Technical Details**:
- **File**: `infrastructure/config/WebConfig.java`
- **Configuration**:
  ```java
  @Override
  public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/api/**")
          .allowedOrigins("http://localhost:5173", "http://localhost:5174")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true);
  }
  ```

**Dependencies**: None

---

## Phase 6: Configuration Service - Complete Implementation

### [BE-017] Create Configuration Entity
**Goal**: JPA entity for configuration settings.

**Technical Details**:
- **File**: `domain/model/ConfigurationSetting.java`
- **Fields**:
  - `id`, `category`, `key`, `value`, `description`
  - `dataType` (Enum: STRING, NUMBER, BOOLEAN, JSON)
  - `isEncrypted` (Boolean)
  - `version`, `createdAt`, `updatedAt`
- **Unique Constraint**: `category` + `key`

**Dependencies**: Requires BE-002, BE-003

---

### [BE-018] Create Configuration Repository
**Goal**: JPA repository for configuration CRUD.

**Technical Details**:
- **Custom Queries**:
  ```java
  Optional<ConfigurationSetting> findByCategoryAndKey(String category, String key);
  List<ConfigurationSetting> findByCategory(String category);
  ```

**Dependencies**: Requires BE-017

---

### [BE-019] Create Configuration Service
**Goal**: Business logic for configuration management.

**Technical Details**:
- **Methods**:
  - `createOrUpdate(ConfigurationRequest)` (upsert logic)
  - `getByCategory(String category)`
  - `getByCategoryAndKey(String category, String key)`
  - `getGroupedByCategory(String category)` → Map<String, String>
  - `deleteConfiguration(String category, String key)`

**Dependencies**: Requires BE-018

---

### [BE-020] Create Configuration Controller
**Goal**: REST API for configuration management.

**Technical Details**:
- **Base Path**: `/api/v1/configurations`
- **Endpoints**:

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| GET | `/` | List all configs (optional category filter) | List<ConfigurationResponse> |
| GET | `/{category}/{key}` | Get specific config | ConfigurationResponse |
| PUT | `/{category}/{key}` | Create or update config | ConfigurationResponse |
| DELETE | `/{category}/{key}` | Delete config | 204 No Content |
| GET | `/grouped/{category}` | Get as key-value map | Map<String, String> |

**CORS**: Same as BE-016

**Dependencies**: Requires BE-019

---

### [BE-021] Configuration Service - Exception Handler and Documentation
**Goal**: Complete infrastructure setup (same as Student Service).

**Technical Details**:
- Global exception handler
- OpenAPI configuration
- Custom exceptions (ConfigurationNotFoundException)

**Dependencies**: Requires BE-020

---

## Phase 7: Testing and Validation

### [BE-022] Manual API Testing with Postman/curl
**Goal**: Verify all endpoints work correctly.

**Technical Details**:
- Test all CRUD operations for students
- Test search/filter functionality
- Test validation rules (age, unique fields)
- Test optimistic locking
- Test enrollment history
- Test configuration CRUD
- Verify error responses (RFC 7807 format)

**Test Scenarios**:
1. Create student with valid data → 201
2. Create student with duplicate mobile → 409
3. Create student with age < 3 → 422
4. Update student with only editable fields → 200
5. Update with wrong version → 409
6. Delete student → 204
7. Search by lastName → 200 with results
8. Get statistics → 200 with counts

**Dependencies**: Requires BE-011, BE-020

---

### [BE-023] Verify OpenAPI Specification Compliance
**Goal**: Ensure API matches `specs/sms_api_specification.yaml`.

**Technical Details**:
- Compare actual API responses with OpenAPI spec
- Verify field names match (e.g., `fathersName` vs `fatherName`)
- Verify response status codes
- Verify pagination format
- Verify error response format

**Dependencies**: Requires BE-022

---

### [BE-024] Integration Testing with Frontend
**Goal**: Ensure backend works with React frontend.

**Technical Details**:
- Start both services (8081, 8082)
- Start frontend (5173)
- Verify CORS works
- Test full CRUD flow from UI
- Verify validation errors display correctly
- Test phone uniqueness check (async)

**Dependencies**: Requires BE-022 and Frontend Implementation

---

## Phase 8: Deployment Preparation

### [BE-025] Create Docker Compose for Full Stack
**Goal**: Single command to start all services.

**Technical Details**:
- **File**: `docker-compose.yml` (root level)
- **Services**:
  - student-db (PostgreSQL 5433)
  - config-db (PostgreSQL 5434)
  - student-service (port 8081)
  - configuration-service (port 8082)
- **Health Checks**: `/actuator/health`
- **Dependency Order**: Databases → Services

**Dependencies**: Requires BE-022

---

### [BE-026] Create README for Backend
**Goal**: Documentation for running and testing backend.

**Technical Details**:
- **File**: `backend/README.md`
- **Sections**:
  - Prerequisites (Java 17+, Maven, Docker)
  - Database setup
  - Running services
  - API documentation URLs
  - Environment variables
  - Testing instructions

**Dependencies**: Requires BE-025

---

## Phase 9: Final Verification

### [BE-027] Code Review Checklist
**Goal**: Ensure code quality standards.

**Checklist**:
- [ ] All DTOs have validation annotations
- [ ] All services have `@Transactional` where needed
- [ ] All exceptions are handled by GlobalExceptionHandler
- [ ] All API endpoints have OpenAPI documentation
- [ ] All entities have proper JPA annotations
- [ ] Optimistic locking implemented for updates
- [ ] CORS configured correctly
- [ ] No hardcoded values (use application.yml)
- [ ] Proper logging (use SLF4J)
- [ ] No security vulnerabilities (SQL injection prevented by JPA)

**Dependencies**: Requires all BE tasks

---

### [BE-028] Performance Baseline Testing
**Goal**: Establish baseline response times.

**Technical Details**:
- Use JMeter or Apache Bench
- Test scenarios:
  - Create student: Target < 200ms (p95)
  - Search students: Target < 150ms (p95)
  - Get student by ID: Target < 100ms (p95)
- Load: 50 concurrent users
- Document results

**Dependencies**: Requires BE-022

---

## Summary

**Total Tasks**: 28
**Estimated Effort**: 10-12 days (1 developer)
**Critical Path**: BE-001 → BE-004 → BE-009 → BE-011 → BE-022 → BE-024

**Key Deliverables**:
1. Student Service (Spring Boot, port 8081)
2. Configuration Service (Spring Boot, port 8082)
3. PostgreSQL databases (ports 5433, 5434)
4. OpenAPI documentation at `/swagger-ui.html`
5. Docker Compose for full stack
6. Comprehensive API testing results

**Success Criteria**:
- All 14 API endpoints functional
- All validation rules enforced
- Error responses in RFC 7807 format
- Frontend integration successful
- CORS working correctly
- No compilation errors
- Documentation complete
