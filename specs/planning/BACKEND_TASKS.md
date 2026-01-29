# Backend Implementation Plan
**School Management System - Spring Boot Developer Tasks**

Version: 1.0.0
Execution Model: Waterfall / Single-Pass Implementation
Target: Complete Backend Services in One Continuous Flow

---

## Task Overview

This plan provides a sequential checklist for implementing the **Student Service** (Port 8081) and **Configuration Service** (Port 8082) as independent microservices.

**Critical Constraints:**
- No JWT/OAuth implementation (basic auth assumed)
- No Flyway/Liquibase (use manual SQL execution)
- No database triggers, stored procedures, or advanced indexes
- Focus on DDL Tables, Primary Keys, Foreign Keys, and Basic Constraints only

---

## STUDENT SERVICE TASKS

### Phase 1: Project Setup & Infrastructure

#### [BE-001] Create Student Service Project Structure
**Goal:** Initialize Spring Boot project with Maven and required dependencies.

**Technical Details:**
- Use Spring Initializr to generate base project:
  - Group: `com.school`
  - Artifact: `student-service`
  - Java Version: 21
  - Spring Boot Version: 3.5.0
  - Packaging: JAR
- Add dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-data-redis`
  - `spring-boot-starter-actuator`
  - `postgresql` (JDBC driver)
  - `drools-core` (version 9.44.0.Final)
  - `mapstruct` (version 1.5.5.Final)
  - `lombok`
  - `micrometer-registry-prometheus`
- Create directory structure as per `05-backend-implementation-guide.md` (Presentation → Application → Domain → Infrastructure)

**Dependencies:** None

**Acceptance Criteria:**
- Project builds successfully with `mvn clean install`
- All layers (controller, service, domain, infrastructure) packages created
- `pom.xml` contains all required dependencies

---

#### [BE-002] Configure Application Properties
**Goal:** Set up database connection, Redis, and application configuration.

**Technical Details:**
- Create `application.yml` in `src/main/resources/`:
  ```yaml
  spring:
    application:
      name: student-service
    datasource:
      url: jdbc:postgresql://localhost:5433/student_db?TimeZone=UTC
      username: postgres
      password: ${DB_PASSWORD:postgres}
      driver-class-name: org.postgresql.Driver
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
    jpa:
      open-in-view: false
      hibernate:
        ddl-auto: validate
      properties:
        hibernate:
          format_sql: true
          jdbc:
            batch_size: 50
    data:
      redis:
        host: localhost
        port: 6379
        database: 0
        lettuce:
          pool:
            max-active: 20
            max-idle: 10
            min-idle: 5
  server:
    port: 8081
  ```
- Create `application-dev.yml` and `application-prod.yml` with environment-specific overrides
- Add CORS configuration for frontend origins (ports 3000, 5173)

**Dependencies:** BE-001

**Acceptance Criteria:**
- Application starts without errors
- Database connection pool initialized successfully
- Redis connection established (if Redis is running)
- Actuator health endpoint accessible at `http://localhost:8081/actuator/health`

---

#### [BE-003] Execute Database Schema
**Goal:** Create database tables using the provided SQL script.

**Technical Details:**
- Run `specs/planning/school_management.sql` script against PostgreSQL
- Verify tables `students` and `enrollments` are created in `student_db`
- Verify all indexes, constraints, and comments are applied
- Seed sample data for testing

**Dependencies:** None (can run independently)

**Acceptance Criteria:**
- `student_db` database exists with `students` and `enrollments` tables
- Sample records inserted (3 students, 3 enrollments)
- Query `SELECT * FROM students;` returns test data

---

### Phase 2: Domain Layer Implementation

#### [BE-004] Create Domain Entities
**Goal:** Define rich domain models for Student and Enrollment with business logic.

**Technical Details:**
- Create `com.school.student.domain.model.Student` class:
  - Fields: id, studentId, firstName, lastName, dateOfBirth, mobile, email, address, fathersName, mothersName, identificationMark, aadhaarNumber, status, version, createdAt, updatedAt
  - Business methods: `updateProfile()`, `activate()`, `deactivate()`
  - Use Lombok `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
  - Private setters (only exposed for MapStruct/JPA)
- Create `com.school.student.domain.model.Enrollment` class:
  - Fields: id, studentId, academicYear, gradeClass, section, enrollmentDate, withdrawalDate, status, remarks, createdAt
  - Validation in constructor to ensure `withdrawalDate >= enrollmentDate`
- Create `StudentStatus` enum: ACTIVE, INACTIVE
- Create `EnrollmentStatus` enum: ACTIVE, WITHDRAWN, TRANSFERRED, COMPLETED

**Dependencies:** BE-001

**Acceptance Criteria:**
- Domain models compile without errors
- Business methods enforce invariants (e.g., cannot deactivate already inactive student)
- Unit tests verify domain logic (e.g., `testActivateAlreadyActiveStudentThrowsException`)

---

#### [BE-005] Define Repository Interfaces
**Goal:** Create domain repository contracts (interfaces only, no implementations).

**Technical Details:**
- Create `com.school.student.domain.repository.StudentRepository` interface:
  ```java
  public interface StudentRepository {
      Student save(Student student);
      Optional<Student> findById(Long id);
      Optional<Student> findByStudentId(String studentId);
      Page<Student> searchStudents(String lastName, String status, Pageable pageable);
      boolean existsByMobile(String mobile);
      boolean existsByAadhaarNumber(String aadhaar);
      long countByCreatedAtDate(LocalDate date);
      void delete(Student student);
  }
  ```
- Create `com.school.student.domain.repository.EnrollmentRepository` interface:
  ```java
  public interface EnrollmentRepository {
      Enrollment save(Enrollment enrollment);
      List<Enrollment> findByStudentId(Long studentId);
      boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear);
  }
  ```

**Dependencies:** BE-004

**Acceptance Criteria:**
- Repository interfaces defined in `domain.repository` package
- Methods follow domain-driven design naming conventions
- No implementation details in domain layer

---

#### [BE-006] Create Domain Exceptions
**Goal:** Define custom exceptions for business rule violations.

**Technical Details:**
- Create abstract `BusinessException` class in `com.school.student.domain.exception`
- Create concrete exceptions:
  - `StudentNotFoundException extends BusinessException`
  - `DuplicateMobileException extends BusinessException`
  - `DuplicateAadhaarException extends BusinessException`
  - `BusinessRuleViolationException extends BusinessException` (with List<String> errors field)
  - `EnrollmentConflictException extends BusinessException`
- Each exception should include meaningful message constructor

**Dependencies:** BE-001

**Acceptance Criteria:**
- All exceptions extend `BusinessException`
- Exception messages are descriptive and user-friendly
- Exceptions can be serialized for API responses

---

### Phase 3: Infrastructure Layer Implementation

#### [BE-007] Create JPA Entities
**Goal:** Implement database persistence entities separate from domain models.

**Technical Details:**
- Create `com.school.student.infrastructure.persistence.StudentEntity` class:
  - Annotate with `@Entity`, `@Table(name = "students")`
  - Map all database columns using `@Column` annotations
  - Use `@Version` for optimistic locking
  - Use `@CreationTimestamp` and `@UpdateTimestamp` for audit columns
  - Define `@OneToMany` relationship to `EnrollmentEntity`
  - Add database indexes using `@Index` annotations
- Create `com.school.student.infrastructure.persistence.EnrollmentEntity` class:
  - Similar annotations as StudentEntity
  - No `@ManyToOne` relationship (microservices constraint)
- Use Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

**Dependencies:** BE-004

**Acceptance Criteria:**
- JPA entities map correctly to database tables
- Hibernate validates schema on startup (`ddl-auto: validate`)
- Entity relationships defined (without database-level foreign keys)

---

#### [BE-008] Implement JPA Repositories
**Goal:** Create Spring Data JPA repositories for database access.

**Technical Details:**
- Create `StudentJpaRepository` interface extending `JpaRepository<StudentEntity, Long>`:
  ```java
  interface StudentJpaRepository extends JpaRepository<StudentEntity, Long> {
      Optional<StudentEntity> findByStudentId(String studentId);
      boolean existsByMobile(String mobile);
      boolean existsByAadhaarNumber(String aadhaar);
      @Query("SELECT COUNT(s) FROM StudentEntity s WHERE DATE(s.createdAt) = :date")
      long countByCreatedAtDate(@Param("date") LocalDate date);
      @Query("SELECT s FROM StudentEntity s WHERE (:lastName IS NULL OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND (:status IS NULL OR s.status = :status)")
      Page<StudentEntity> searchStudents(@Param("lastName") String lastName, @Param("status") String status, Pageable pageable);
  }
  ```
- Create `EnrollmentJpaRepository` interface extending `JpaRepository<EnrollmentEntity, Long>`
- Implement repository interfaces from domain layer (`StudentRepositoryImpl`, `EnrollmentRepositoryImpl`)
- Use MapStruct mappers to convert between JPA entities and domain models

**Dependencies:** BE-007

**Acceptance Criteria:**
- JPA repositories use parameterized queries (prevent SQL injection)
- Custom queries tested with sample data
- Repository implementations correctly map entities to domain models

---

#### [BE-009] Configure Redis Caching
**Goal:** Set up Redis cache manager with appropriate TTLs.

**Technical Details:**
- Create `com.school.student.infrastructure.config.CacheConfig`:
  ```java
  @Configuration
  @EnableCaching
  public class CacheConfig {
      @Bean
      public RedisCacheConfiguration cacheConfiguration() {
          return RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofMinutes(5))
              .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
              .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
      }
      @Bean
      public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
          Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
          cacheConfigurations.put("students", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
          cacheConfigurations.put("studentSearchResults", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
          return RedisCacheManager.builder(connectionFactory)
              .cacheDefaults(cacheConfiguration())
              .withInitialCacheConfigurations(cacheConfigurations)
              .build();
      }
  }
  ```
- Use separate Redis database (database 0) for student service

**Dependencies:** BE-002

**Acceptance Criteria:**
- Redis cache manager bean created successfully
- Cache keys follow naming convention: `sms:student:{studentId}`
- TTLs configured as per requirements (stable data: 5-10 minutes)

---

#### [BE-010] Implement Drools Rule Engine Configuration
**Goal:** Set up Drools for business rule validation (age, mobile uniqueness, etc.).

**Technical Details:**
- Create `com.school.student.infrastructure.config.DroolsConfig`:
  ```java
  @Configuration
  public class DroolsConfig {
      @Bean
      public KieContainer kieContainer() {
          KieServices kieServices = KieServices.Factory.get();
          KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
          kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student/student-age-validation.drl"));
          kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student/mobile-validation.drl"));
          KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
          kieBuilder.buildAll();
          return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
      }
  }
  ```
- Create `com.school.student.rules.RuleExecutor` service to invoke Drools rules
- Create `com.school.student.rules.ValidationResult` DTO to hold validation errors

**Dependencies:** BE-001

**Acceptance Criteria:**
- Drools KieContainer bean initialized successfully
- Rule files loaded from classpath (`src/main/resources/rules/student/*.drl`)
- RuleExecutor can execute rules and return validation results

---

#### [BE-011] Create Drools Rule Files
**Goal:** Define business rules in DRL format for student validation.

**Technical Details:**
- Create `src/main/resources/rules/student/student-age-validation.drl`:
  ```drl
  package com.school.student.rules;
  import com.school.student.domain.model.Student;
  import com.school.student.rules.ValidationResult;
  import java.time.LocalDate;
  import java.time.Period;

  rule "Student age must be between 3 and 18"
      when
          $student: Student($dob: dateOfBirth)
          $result: ValidationResult()
      then
          int age = Period.between($dob, LocalDate.now()).getYears();
          if (age < 3 || age > 18) {
              $result.addError("Student age must be between 3 and 18 years. Current age: " + age);
          }
  end
  ```
- Create `mobile-validation.drl` for mobile format validation
- Create `aadhaar-validation.drl` for Aadhaar format validation

**Dependencies:** BE-010

**Acceptance Criteria:**
- Rule files compile without errors
- Rules execute correctly when invoked by RuleExecutor
- Validation errors captured in ValidationResult object

---

### Phase 4: Application Layer Implementation

#### [BE-012] Create DTOs (Request/Response)
**Goal:** Define Data Transfer Objects for API contracts.

**Technical Details:**
- Create `com.school.student.controller.dto.request.StudentRequest`:
  ```java
  public class StudentRequest {
      @NotBlank @Size(min = 2, max = 100) @Pattern(regexp = "^[a-zA-Z\\s]+$") private String firstName;
      @NotBlank @Size(min = 2, max = 100) @Pattern(regexp = "^[a-zA-Z\\s]+$") private String lastName;
      @NotNull @Past private LocalDate dateOfBirth;
      @NotBlank @Pattern(regexp = "^\\d{10}$") private String mobile;
      @Email private String email;
      @Size(max = 500) private String address;
      @Size(max = 100) private String fathersName;
      @Size(max = 100) private String mothersName;
      @Size(max = 200) private String identificationMark;
      @Pattern(regexp = "^\\d{12}$") private String aadhaarNumber;
  }
  ```
- Create `com.school.student.controller.dto.response.StudentResponse` (all fields from Student + computed age)
- Create `EnrollmentRequest` and `EnrollmentResponse` similarly
- Create `StudentUpdateRequest` (only editable fields: firstName, lastName, mobile, status, version)

**Dependencies:** BE-001

**Acceptance Criteria:**
- DTOs mirror OpenAPI specification in `sms_api_specification.yaml`
- All validation annotations match backend business rules
- DTOs use Java Bean Validation annotations (`@NotNull`, `@Size`, `@Pattern`, etc.)

---

#### [BE-013] Implement MapStruct Mappers
**Goal:** Create DTO-Domain mapping interfaces using MapStruct.

**Technical Details:**
- Create `com.school.student.service.mapper.StudentMapper`:
  ```java
  @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
  public interface StudentMapper {
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "studentId", ignore = true)
      @Mapping(target = "status", ignore = true)
      @Mapping(target = "version", ignore = true)
      @Mapping(target = "createdAt", ignore = true)
      @Mapping(target = "updatedAt", ignore = true)
      Student toDomain(StudentRequest request);

      @Mapping(target = "age", expression = "java(calculateAge(student.getDateOfBirth()))")
      StudentResponse toResponse(Student student);

      default int calculateAge(LocalDate birthDate) {
          return Period.between(birthDate, LocalDate.now()).getYears();
      }
  }
  ```
- Create similar mappers for Enrollment
- Create `StudentEntityMapper` to map between JPA entities and domain models

**Dependencies:** BE-012, BE-004

**Acceptance Criteria:**
- MapStruct generates implementation classes at compile time
- Mappers correctly convert between DTOs, domain models, and JPA entities
- Computed fields (e.g., age) calculated correctly

---

#### [BE-014] Implement Student Service Layer
**Goal:** Create service layer with business logic orchestration.

**Technical Details:**
- Create `com.school.student.service.StudentService` with methods:
  - `registerStudent(StudentRequest)` → `StudentResponse`
    - Check mobile uniqueness
    - Map DTO to domain model
    - Execute Drools validation
    - Generate student ID (format: STD-YYYYMMDD-NNNN)
    - Save student
    - Return response
  - `getStudentById(String studentId)` → `StudentResponse` (with `@Cacheable`)
  - `searchStudents(String lastName, String status, Pageable)` → `Page<StudentResponse>`
  - `updateStudent(String studentId, StudentUpdateRequest)` → `StudentResponse` (with `@CacheEvict`)
  - `deleteStudent(String studentId)` → `void` (with `@CacheEvict`)
- Annotate class with `@Service`, `@Transactional(readOnly = true)`
- Use `@Transactional` on write methods
- Inject `StudentRepository`, `StudentMapper`, `RuleExecutor`

**Dependencies:** BE-005, BE-013, BE-010

**Acceptance Criteria:**
- All service methods implement correct business logic
- Transactions managed correctly (write methods are transactional)
- Caching applied to read operations (`getStudentById`)
- Duplicate mobile/Aadhaar checks prevent conflicts
- Student ID generation follows format (e.g., STD-20260128-0001)

---

#### [BE-015] Implement Enrollment Service Layer
**Goal:** Create service layer for enrollment management.

**Technical Details:**
- Create `com.school.student.service.EnrollmentService` with methods:
  - `createEnrollment(String studentId, EnrollmentRequest)` → `EnrollmentResponse`
    - Verify student exists
    - Check for duplicate enrollment in same academic year
    - Save enrollment
  - `getEnrollmentHistory(String studentId)` → `List<EnrollmentResponse>`
- Apply business rule: One enrollment per student per academic year

**Dependencies:** BE-005, BE-013

**Acceptance Criteria:**
- Enrollment creation validates student existence
- Duplicate academic year enrollment prevented (409 Conflict)
- Enrollment history retrieved correctly

---

### Phase 5: Presentation Layer Implementation

#### [BE-016] Implement Student Controller
**Goal:** Create REST API endpoints for student management.

**Technical Details:**
- Create `com.school.student.controller.StudentController`:
  - `POST /api/v1/students` → 201 Created
  - `GET /api/v1/students/{studentId}` → 200 OK or 404 Not Found
  - `GET /api/v1/students?lastName={}&status={}` → 200 OK (paginated)
  - `PUT /api/v1/students/{studentId}` → 200 OK or 409 Conflict (optimistic lock)
  - `DELETE /api/v1/students/{studentId}` → 204 No Content
- Annotate class with `@RestController`, `@RequestMapping("/api/v1/students")`
- Use `@Valid` on request bodies
- Return `ResponseEntity<T>` with appropriate HTTP status codes
- Log requests with correlation IDs

**Dependencies:** BE-014

**Acceptance Criteria:**
- All endpoints return responses matching OpenAPI specification
- Validation errors return 400 Bad Request with field-level errors
- Duplicate mobile returns 409 Conflict
- Optimistic locking failures return 409 Conflict

---

#### [BE-017] Implement Enrollment Controller
**Goal:** Create REST API endpoints for enrollment management.

**Technical Details:**
- Create `com.school.student.controller.EnrollmentController`:
  - `GET /api/v1/students/{studentId}/enrollment-history` → 200 OK
  - `POST /api/v1/students/{studentId}/enrollment-history` → 201 Created
- Validate student existence before operations

**Dependencies:** BE-015

**Acceptance Criteria:**
- Enrollment endpoints follow RESTful conventions
- Returns 404 if student not found
- Returns 409 if duplicate enrollment for academic year

---

#### [BE-018] Implement Global Exception Handler
**Goal:** Create centralized error handling using @ControllerAdvice.

**Technical Details:**
- Create `com.school.student.common.exception.GlobalExceptionHandler`:
  - Handle `StudentNotFoundException` → 404
  - Handle `DuplicateMobileException` → 409
  - Handle `BusinessRuleViolationException` → 400 (with error list)
  - Handle `MethodArgumentNotValidException` → 400 (Bean Validation errors)
  - Handle `OptimisticLockException` → 409
  - Handle generic `Exception` → 500
- Return RFC 7807 Problem Details format:
  ```json
  {
    "type": "https://api.school.com/errors/validation-error",
    "title": "Validation Failed",
    "status": 400,
    "detail": "Mobile must be exactly 10 digits",
    "timestamp": "2026-01-28T10:30:00Z",
    "errors": [{"field": "mobile", "message": "Mobile must be exactly 10 digits", "code": "Pattern"}]
  }
  ```

**Dependencies:** BE-006

**Acceptance Criteria:**
- All exceptions mapped to appropriate HTTP status codes
- Error responses follow RFC 7807 format
- Field-level validation errors included in response
- Structured logging for all exceptions

---

### Phase 6: Cross-Cutting Concerns

#### [BE-019] Configure CORS
**Goal:** Enable frontend access from different origins.

**Technical Details:**
- Create `com.school.student.infrastructure.config.WebConfig`:
  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
              .allowedOrigins("http://localhost:3000", "http://localhost:5173")
              .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
              .allowedHeaders("*")
              .allowCredentials(true)
              .maxAge(3600);
      }
  }
  ```

**Dependencies:** BE-001

**Acceptance Criteria:**
- Frontend (port 3000, 5173) can make API calls without CORS errors
- Preflight OPTIONS requests handled correctly
- Credentials (cookies) supported

---

#### [BE-020] Configure Actuator Endpoints
**Goal:** Expose health and metrics endpoints for monitoring.

**Technical Details:**
- Configure in `application.yml`:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
    endpoint:
      health:
        show-details: when-authorized
    metrics:
      tags:
        application: ${spring.application.name}
  ```
- Create custom health indicators for Redis connectivity

**Dependencies:** BE-002

**Acceptance Criteria:**
- `/actuator/health` returns UP status
- `/actuator/prometheus` exposes metrics for scraping
- Custom metrics for student registrations, validation failures

---

#### [BE-021] Implement Structured Logging
**Goal:** Configure JSON-based logging with correlation IDs.

**Technical Details:**
- Create `src/main/resources/logback-spring.xml`:
  ```xml
  <configuration>
      <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
          <encoder class="net.logstash.logback.encoder.LogstashEncoder">
              <includeMdcKeyName>correlationId</includeMdcKeyName>
              <includeMdcKeyName>userId</includeMdcKeyName>
              <includeMdcKeyName>studentId</includeMdcKeyName>
          </encoder>
      </appender>
      <root level="INFO">
          <appender-ref ref="CONSOLE"/>
      </root>
      <logger name="com.school.student" level="DEBUG"/>
  </configuration>
  ```
- Add dependency: `logstash-logback-encoder`

**Dependencies:** BE-001

**Acceptance Criteria:**
- All logs output in JSON format
- Correlation IDs captured from request headers
- Logs include timestamp, level, message, and context (studentId, userId)

---

### Phase 7: Testing

#### [BE-022] Write Unit Tests for Domain Layer
**Goal:** Test domain models and business logic in isolation.

**Technical Details:**
- Create tests in `src/test/java/com/school/student/domain/model/StudentTest.java`:
  - `testUpdateProfileValidData()`
  - `testUpdateProfileInvalidMobile()`
  - `testActivateAlreadyActiveStudentThrowsException()`
  - `testDeactivateInactiveStudentThrowsException()`
- Use JUnit 5, AssertJ
- Achieve 95% line coverage for domain layer

**Dependencies:** BE-004

**Acceptance Criteria:**
- All domain logic covered by unit tests
- Tests execute in <1 second
- Coverage report shows 95%+ for domain package

---

#### [BE-023] Write Unit Tests for Service Layer
**Goal:** Test service orchestration with mocked dependencies.

**Technical Details:**
- Create tests in `src/test/java/com/school/student/service/StudentServiceTest.java`:
  - `testRegisterStudentSuccessfully()`
  - `testRegisterStudentDuplicateMobile()`
  - `testRegisterStudentAgeViolation()`
  - `testUpdateStudentSuccessfully()`
  - `testDeleteStudentNotFound()`
- Use Mockito to mock repositories, mappers, rule executor
- Use `@ExtendWith(MockitoExtension.class)`

**Dependencies:** BE-014

**Acceptance Criteria:**
- Service layer tests use mocked dependencies (no database)
- Tests verify business logic flows (e.g., Drools validation invoked)
- Achieve 85% line coverage for service layer

---

#### [BE-024] Write Integration Tests with TestContainers
**Goal:** Test API endpoints with real database and Redis.

**Technical Details:**
- Create tests in `src/test/java/com/school/student/integration/StudentControllerIntegrationTest.java`:
  - `testCreateStudentSuccessfully()`
  - `testGetStudentById()`
  - `testSearchStudentsWithFilters()`
  - `testUpdateStudentOptimisticLocking()`
  - `testDeleteStudent()`
- Use `@SpringBootTest`, `@Testcontainers`
- Start PostgreSQL and Redis containers:
  ```java
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
      .withDatabaseName("student_db_test");
  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
      .withExposedPorts(6379);
  ```
- Use `MockMvc` to call REST endpoints

**Dependencies:** BE-016, BE-003

**Acceptance Criteria:**
- Integration tests run against real database and Redis
- Tests verify end-to-end flows (HTTP request → database → response)
- Achieve 70% branch coverage for infrastructure layer
- All tests pass with `mvn verify`

---

#### [BE-025] Write Performance Tests
**Goal:** Verify API response times meet SLA (<200ms p95).

**Technical Details:**
- Use JMeter or Gatling to simulate load:
  - 100 concurrent users
  - 1000 requests to `POST /api/v1/students`
  - 1000 requests to `GET /api/v1/students/{studentId}`
- Measure p95 response time
- Verify database connection pool not exhausted

**Dependencies:** BE-024

**Acceptance Criteria:**
- p95 response time <200ms for all endpoints
- No database connection pool exhaustion errors
- Cache hit ratio >80% for GET operations

---

### Phase 8: Deployment

#### [BE-026] Create Dockerfile for Student Service
**Goal:** Containerize Student Service for deployment.

**Technical Details:**
- Create `Dockerfile` in project root:
  ```dockerfile
  FROM eclipse-temurin:21-jre-alpine
  WORKDIR /app
  COPY target/student-service-*.jar app.jar
  EXPOSE 8081
  ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Duser.timezone=UTC", "-jar", "app.jar"]
  ```
- Ensure timezone set to UTC to avoid PostgreSQL compatibility issues

**Dependencies:** BE-001

**Acceptance Criteria:**
- Docker image builds successfully: `docker build -t sms/student-service .`
- Container starts without errors: `docker run -p 8081:8081 sms/student-service`
- Health endpoint accessible from host machine

---

#### [BE-027] Create Docker Compose Configuration
**Goal:** Define multi-container setup for local development.

**Technical Details:**
- Create `docker-compose.yml` in project root:
  ```yaml
  version: '3.8'
  services:
    student-db:
      image: postgres:18-alpine
      environment:
        POSTGRES_DB: student_db
        POSTGRES_USER: postgres
        POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}
      ports:
        - "5433:5432"
      volumes:
        - student-data:/var/lib/postgresql/data
        - ./specs/planning/school_management.sql:/docker-entrypoint-initdb.d/init.sql
    redis:
      image: redis:7-alpine
      ports:
        - "6379:6379"
    student-service:
      build: .
      ports:
        - "8081:8081"
      environment:
        DB_URL: jdbc:postgresql://student-db:5432/student_db?TimeZone=UTC
        DB_USERNAME: postgres
        DB_PASSWORD: ${DB_PASSWORD:-postgres}
        REDIS_HOST: redis
        REDIS_PORT: 6379
      depends_on:
        - student-db
        - redis
  volumes:
    student-data:
  ```

**Dependencies:** BE-026, BE-003

**Acceptance Criteria:**
- `docker-compose up -d` starts all services successfully
- Database initialized with schema on first run
- Student Service accessible at `http://localhost:8081`
- Health check passes: `curl http://localhost:8081/actuator/health`

---

## CONFIGURATION SERVICE TASKS

### Phase 9: Configuration Service Implementation

#### [BE-028] Create Configuration Service Project Structure
**Goal:** Initialize Spring Boot project for Configuration Service.

**Technical Details:**
- Similar to BE-001, but with:
  - Group: `com.school`
  - Artifact: `configuration-service`
  - Port: 8082
  - Database: `config_db` (same PostgreSQL instance, port 5433)
  - Redis database: 1 (different from Student Service)
- No Drools dependency needed (simpler service)

**Dependencies:** None

**Acceptance Criteria:**
- Project builds successfully
- Directory structure follows layered architecture

---

#### [BE-029] Configure Configuration Service Properties
**Goal:** Set up database and Redis connections for Configuration Service.

**Technical Details:**
- Create `application.yml`:
  ```yaml
  spring:
    application:
      name: configuration-service
    datasource:
      url: jdbc:postgresql://localhost:5433/config_db?TimeZone=UTC
      username: postgres
      password: ${DB_PASSWORD:postgres}
    data:
      redis:
        database: 1  # Different from Student Service
  server:
    port: 8082
  ```

**Dependencies:** BE-028

**Acceptance Criteria:**
- Application starts on port 8082
- Connects to `config_db` database
- Uses Redis database 1

---

#### [BE-030] Implement Configuration Domain Model
**Goal:** Create domain model for configuration settings.

**Technical Details:**
- Create `com.school.configuration.domain.model.Configuration`:
  - Fields: id, category, key, value, description, dataType, isEncrypted, version, updatedAt
  - Enums: `ConfigCategory` (GENERAL, ACADEMIC, FINANCIAL), `DataType` (STRING, NUMBER, BOOLEAN, JSON)
- No complex business logic (simpler than Student)

**Dependencies:** BE-028

**Acceptance Criteria:**
- Domain model aligns with database schema
- Category and DataType enums defined

---

#### [BE-031] Implement Configuration Repository
**Goal:** Create JPA repository for configuration CRUD operations.

**Technical Details:**
- Create `ConfigurationEntity` (JPA)
- Create `ConfigurationJpaRepository`:
  ```java
  interface ConfigurationJpaRepository extends JpaRepository<ConfigurationEntity, Long> {
      Optional<ConfigurationEntity> findByCategoryAndKey(ConfigCategory category, String key);
      List<ConfigurationEntity> findByCategory(ConfigCategory category);
  }
  ```
- Implement domain repository interface

**Dependencies:** BE-030

**Acceptance Criteria:**
- Repository methods support category-based filtering
- Composite unique constraint (category, key) enforced

---

#### [BE-032] Implement Configuration Service Layer
**Goal:** Create service layer for configuration management.

**Technical Details:**
- Create `com.school.configuration.service.ConfigurationService`:
  - `getAllConfigurations(ConfigCategory category)` → `List<ConfigurationResponse>` (with `@Cacheable`)
  - `getConfiguration(ConfigCategory category, String key)` → `ConfigurationResponse`
  - `upsertConfiguration(ConfigCategory category, String key, ConfigurationRequest)` → `ConfigurationResponse` (with `@CacheEvict`)
  - `deleteConfiguration(ConfigCategory category, String key)` → `void` (with `@CacheEvict`)
  - `getGroupedConfigurations(ConfigCategory category)` → `Map<String, String>`
- Implement upsert logic (create if not exists, update if exists)

**Dependencies:** BE-031

**Acceptance Criteria:**
- Upsert correctly handles both create and update scenarios
- Cache eviction clears all related caches on updates
- Grouped configurations return key-value map

---

#### [BE-033] Implement Configuration Controller
**Goal:** Create REST API endpoints for configuration management.

**Technical Details:**
- Create `com.school.configuration.controller.ConfigurationController`:
  - `GET /api/v1/configurations?category={GENERAL}` → 200 OK
  - `GET /api/v1/configurations/{category}/{key}` → 200 OK or 404
  - `PUT /api/v1/configurations/{category}/{key}` → 200 OK (update) or 201 Created
  - `DELETE /api/v1/configurations/{category}/{key}` → 204 No Content
  - `GET /api/v1/configurations/grouped/{category}` → 200 OK

**Dependencies:** BE-032

**Acceptance Criteria:**
- All endpoints match OpenAPI specification
- Upsert returns correct HTTP status (200 vs 201)
- Returns 404 if configuration not found

---

#### [BE-034] Configure Global Exception Handler for Configuration Service
**Goal:** Implement error handling for Configuration Service.

**Technical Details:**
- Similar to BE-018, but for Configuration-specific exceptions:
  - `ConfigurationNotFoundException` → 404
  - Generic exception handling

**Dependencies:** BE-033

**Acceptance Criteria:**
- All errors return RFC 7807 Problem Details format
- Consistent error structure across both services

---

#### [BE-035] Write Tests for Configuration Service
**Goal:** Create unit and integration tests for Configuration Service.

**Technical Details:**
- Unit tests for service layer (mock repository)
- Integration tests with TestContainers (PostgreSQL + Redis)
- Test upsert logic thoroughly (create vs update)

**Dependencies:** BE-032

**Acceptance Criteria:**
- Service layer: 85% line coverage
- Integration tests verify upsert, cache eviction
- All tests pass with `mvn verify`

---

#### [BE-036] Create Dockerfile and Docker Compose for Configuration Service
**Goal:** Containerize Configuration Service.

**Technical Details:**
- Create `Dockerfile` (similar to Student Service)
- Update `docker-compose.yml` to include:
  - `config-db` service (can share same PostgreSQL instance as separate database)
  - `configuration-service` service on port 8082

**Dependencies:** BE-035

**Acceptance Criteria:**
- Docker Compose starts both Student and Configuration services
- Services can communicate if needed (future)
- Health checks pass for both services

---

## FINAL VALIDATION TASKS

#### [BE-037] API Contract Validation
**Goal:** Verify all endpoints match OpenAPI specification.

**Technical Details:**
- Use Spring REST Docs or Swagger UI to compare actual API with `sms_api_specification.yaml`
- Validate request/response schemas
- Verify HTTP status codes for all scenarios

**Dependencies:** BE-016, BE-017, BE-033

**Acceptance Criteria:**
- All endpoints documented in OpenAPI spec are implemented
- Request/response bodies match schema definitions
- Error responses follow RFC 7807 format

---

#### [BE-038] End-to-End Smoke Tests
**Goal:** Verify complete user workflows.

**Technical Details:**
- Test scenario 1: Register student → View student → Update student → Delete student
- Test scenario 2: Register student with duplicate mobile (expect 409)
- Test scenario 3: Search students by last name
- Test scenario 4: Create enrollment → Retrieve enrollment history
- Test scenario 5: Create configuration → Update configuration → Delete configuration
- Use REST client (Postman, curl, or automated scripts)

**Dependencies:** BE-037

**Acceptance Criteria:**
- All scenarios execute successfully end-to-end
- No unexpected errors in application logs
- Database state consistent after operations

---

#### [BE-039] Performance Benchmarking
**Goal:** Measure and document performance metrics.

**Technical Details:**
- Run load tests with 100 concurrent users
- Measure:
  - API response time (p50, p95, p99)
  - Database query time
  - Cache hit ratio
  - JVM memory usage
- Document results in performance report

**Dependencies:** BE-025

**Acceptance Criteria:**
- p95 response time <200ms for all endpoints
- Cache hit ratio >80% for read operations
- No memory leaks observed during load test

---

#### [BE-040] Documentation and Handoff
**Goal:** Complete implementation documentation.

**Technical Details:**
- Update README with:
  - How to run services locally
  - Docker Compose setup instructions
  - Environment variables reference
  - API endpoint documentation
- Create runbook for operations team:
  - Monitoring dashboards
  - Common troubleshooting steps
  - Database migration procedures

**Dependencies:** BE-039

**Acceptance Criteria:**
- README is comprehensive and accurate
- All commands tested and verified
- Runbook covers critical operational scenarios
- Developer can set up project from scratch using README

---

## Task Dependency Summary

```
BE-001 (Project Setup)
  ├─→ BE-002 (Configure Properties)
  │     └─→ BE-009 (Redis Caching)
  │     └─→ BE-020 (Actuator Endpoints)
  ├─→ BE-004 (Domain Entities)
  │     └─→ BE-005 (Repository Interfaces)
  │     └─→ BE-007 (JPA Entities)
  │           └─→ BE-008 (JPA Repositories)
  │     └─→ BE-022 (Unit Tests - Domain)
  ├─→ BE-006 (Domain Exceptions)
  │     └─→ BE-018 (Global Exception Handler)
  ├─→ BE-010 (Drools Config)
  │     └─→ BE-011 (Drools Rules)
  ├─→ BE-012 (DTOs)
  │     └─→ BE-013 (MapStruct Mappers)
  │           └─→ BE-014 (Student Service)
  │                 └─→ BE-016 (Student Controller)
  │                 └─→ BE-023 (Unit Tests - Service)
  │           └─→ BE-015 (Enrollment Service)
  │                 └─→ BE-017 (Enrollment Controller)
  ├─→ BE-019 (CORS Config)
  ├─→ BE-021 (Structured Logging)

BE-003 (Database Schema) → [Independent, can run anytime]

BE-016, BE-017 → BE-024 (Integration Tests) → BE-025 (Performance Tests)

BE-001 → BE-026 (Dockerfile) → BE-027 (Docker Compose)

BE-028 (Config Service Setup) → BE-029 → BE-030 → BE-031 → BE-032 → BE-033 → BE-034 → BE-035 → BE-036

All Tasks → BE-037 (API Validation) → BE-038 (Smoke Tests) → BE-039 (Benchmarking) → BE-040 (Documentation)
```

---

## Success Criteria for Backend Completion

- [ ] Both Student and Configuration services build successfully (`mvn clean install`)
- [ ] All unit tests pass (Domain: 95%, Service: 85%)
- [ ] All integration tests pass (Infrastructure: 70%)
- [ ] Docker Compose starts all services without errors
- [ ] API endpoints match OpenAPI specification
- [ ] p95 response time <200ms
- [ ] Cache hit ratio >80%
- [ ] Database schema matches DDL script
- [ ] Structured logging outputs JSON format
- [ ] CORS configured for frontend origins
- [ ] Health endpoints return UP status
- [ ] Performance benchmarks documented
- [ ] README and runbook completed

---

**Document Version:** 1.0.0
**Last Updated:** 2026-01-28
**Next Review:** Post-Implementation
