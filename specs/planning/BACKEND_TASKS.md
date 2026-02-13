# Backend Implementation Plan
# School Management System - Spring Boot Microservices
# Execution Model: Single-Pass Waterfall (no sprints)

## Source of Truth
- API Contract:       `specs/sms_api_specification.yaml`
- Architecture:       `specs/architecture/01-system-architecture.md`
- Database DDL:       `specs/planning/school_management.sql`
- Business Rules:     `specs/architecture/03-business-rules.md`
- Implementation Ref: `specs/architecture/05-backend-implementation-guide.md`

## Scope
Two independently deployable Spring Boot services:
- **Student Service** (`student-service/`) - Port 8081 - `student_db` (PostgreSQL :5432)
- **Configuration Service** (`config-service/`) - Port 8082 - `config_db` (PostgreSQL :5433)

## Security Note
No JWT / OAuth in Phase 1. No authentication required for any endpoint.

---

## Phase 1: Infrastructure and Project Scaffolding

### [BE-001] Bootstrap Student Service Maven Project

**Goal:** Create a runnable Spring Boot 3.5.0 project skeleton for the student microservice.

**Technical Details:**
- Create directory `student-service/` at project root.
- Create `pom.xml` with the following exact configuration:
  - Parent: `spring-boot-starter-parent` version `3.5.0`
  - Java version: `21`
  - Properties: `mapstruct.version=1.6.3`, `lombok.version=1.18.36`, `drools.version=9.44.0.Final`, `springdoc.version=2.7.0`
  - Dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-cache`, `spring-boot-starter-actuator`, `postgresql` (runtime), `flyway-core`, `flyway-database-postgresql`, `kie-spring:9.44.0.Final`, `drools-core:9.44.0.Final`, `drools-compiler:9.44.0.Final`, `mapstruct:1.6.3`, `lombok:1.18.36` (optional), `micrometer-registry-prometheus`, `micrometer-tracing-bridge-otel`, `opentelemetry-exporter-zipkin`, `springdoc-openapi-starter-webmvc-ui:2.7.0`, `spring-boot-starter-test` (test), `postgresql:testcontainers` (test), `junit-jupiter:testcontainers` (test)
  - Build plugin: `maven-compiler-plugin` with annotation processor paths: Lombok first, then MapStruct. Add compiler arg `-Amapstruct.defaultComponentModel=spring`.
- Create main application class: `com.sms.student.StudentServiceApplication` with `@SpringBootApplication`.
- Create package structure under `src/main/java/com/sms/student/`: `presentation/controller`, `presentation/dto/request`, `presentation/dto/response`, `presentation/dto/error`, `presentation/advice`, `presentation/filter`, `application/command`, `application/query`, `application/mapper`, `application/exception`, `domain/model`, `domain/valueobject`, `domain/repository`, `domain/rules`, `infrastructure/persistence/entity`, `infrastructure/persistence/repository`, `infrastructure/persistence/mapper`, `infrastructure/config`.

**Dependencies:** None (first task).

---

### [BE-002] Configure Student Service Application Properties

**Goal:** Externalize all configuration for the student service using environment-variable-driven YAML.

**Technical Details:**
- Create `src/main/resources/application.yml`:
  ```yaml
  spring:
    application:
      name: student-service
    datasource:
      url: ${STUDENT_DB_URL:jdbc:postgresql://localhost:5432/student_db}
      username: ${STUDENT_DB_USER:student_user}
      password: ${STUDENT_DB_PASSWORD:changeme}
      hikari:
        minimum-idle: 5
        maximum-pool-size: 20
        idle-timeout: 300000
        connection-timeout: 30000
        max-lifetime: 1800000
        leak-detection-threshold: 60000
        pool-name: StudentServicePool
    jpa:
      hibernate:
        ddl-auto: validate
      show-sql: false
      properties:
        hibernate:
          dialect: org.hibernate.dialect.PostgreSQLDialect
          default_batch_fetch_size: 25
    flyway:
      enabled: true
      locations: classpath:db/migration
    cache:
      type: none
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
    tracing:
      sampling:
        probability: 1.0
  springdoc:
    api-docs:
      path: /api-docs
    swagger-ui:
      path: /swagger-ui.html
  server:
    port: 8081
  ```
- Create `src/main/resources/application-local.yml` with `spring.flyway.enabled: true` and `spring.jpa.hibernate.ddl-auto: validate`.
- Create `src/main/resources/logback-spring.xml` using `net.logstash.logback:logstash-logback-encoder` to emit structured JSON logs with fields: `timestamp`, `level`, `service`, `traceId`, `spanId`, `correlationId`, `logger`, `message`.

**Dependencies:** BE-001.

---

### [BE-003] Create Student Service Flyway Migration Scripts

**Goal:** Provide the database schema for the Student Service via Flyway-managed SQL scripts.

**Technical Details:**
- Create `src/main/resources/db/migration/V1__init_students.sql`:
  Copy the `students` table DDL exactly from `specs/planning/school_management.sql`. Include the `student_id_seq` sequence. Include all CONSTRAINT definitions. Include all CREATE INDEX statements.
- Create `src/main/resources/db/migration/V2__init_enrollments.sql`:
  Copy the `enrollments` table DDL exactly from `specs/planning/school_management.sql`. Include all CONSTRAINT and CREATE INDEX definitions.
- Naming convention: `V{n}__{snake_case_description}.sql`.

**Dependencies:** BE-001.

---

### [BE-004] Bootstrap Configuration Service Maven Project

**Goal:** Create a runnable Spring Boot 3.5.0 project skeleton for the configuration microservice.

**Technical Details:**
- Create directory `config-service/` at project root.
- `pom.xml` shares the same parent, properties, and most dependencies as BE-001 EXCEPT:
  - Omit Drools dependencies (`kie-spring`, `drools-core`, `drools-compiler`).
  - Add `spring-boot-starter-data-redis` for Lettuce-based Redis caching.
- Create main application class: `com.sms.config.ConfigServiceApplication` with `@SpringBootApplication @EnableCaching`.
- Create package structure under `src/main/java/com/sms/config/`: `presentation/controller`, `presentation/dto/request`, `presentation/dto/response`, `presentation/dto/error`, `presentation/advice`, `presentation/filter`, `application/command`, `application/query`, `application/mapper`, `application/exception`, `domain/model`, `domain/repository`, `infrastructure/persistence/entity`, `infrastructure/persistence/repository`, `infrastructure/persistence/mapper`, `infrastructure/config`.

**Dependencies:** None (can be started in parallel with BE-001).

---

### [BE-005] Configure Configuration Service Application Properties

**Goal:** Externalize all configuration for the config service, including Redis connection.

**Technical Details:**
- Create `src/main/resources/application.yml` for config-service:
  ```yaml
  spring:
    application:
      name: config-service
    datasource:
      url: ${CONFIG_DB_URL:jdbc:postgresql://localhost:5433/config_db}
      username: ${CONFIG_DB_USER:config_user}
      password: ${CONFIG_DB_PASSWORD:changeme}
      hikari:
        minimum-idle: 5
        maximum-pool-size: 20
        pool-name: ConfigServicePool
    jpa:
      hibernate:
        ddl-auto: validate
      show-sql: false
    flyway:
      enabled: true
      locations: classpath:db/migration
    data:
      redis:
        host: ${REDIS_HOST:localhost}
        port: ${REDIS_PORT:6379}
    cache:
      type: redis
      redis:
        time-to-live: 600000
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
  springdoc:
    api-docs:
      path: /api-docs
    swagger-ui:
      path: /swagger-ui.html
  server:
    port: 8082
  ```
- Create `src/main/resources/db/migration/V1__init_configuration_settings.sql`: Copy `configuration_settings` DDL exactly from `specs/planning/school_management.sql`.
- Same `logback-spring.xml` structured logging pattern as BE-002 (service name: `config-service`).

**Dependencies:** BE-004.

---

## Phase 2: Student Service - Domain Layer

### [BE-006] Implement Student Domain Value Objects

**Goal:** Create self-validating value objects for strongly-typed fields in the student domain.

**Technical Details:**
- `com.sms.student.domain.valueobject.Mobile` - Java record. Constructor validates `value.matches("\\d{10}")`. Throws `IllegalArgumentException` on invalid format.
- `com.sms.student.domain.valueobject.AadhaarNumber` - Java record. Constructor validates `value == null || value.matches("\\d{12}")`. Throws `IllegalArgumentException` on invalid format.
- `com.sms.student.domain.valueobject.StudentId` - Java record wrapping `String value`. No format validation in constructor (generated externally).
- `com.sms.student.domain.valueobject.StudentStatus` - Enum with values `ACTIVE`, `INACTIVE`.
- No Spring annotations in any of these classes (pure Java, no `@Component`).

**Dependencies:** BE-001.

---

### [BE-007] Implement Student Domain Aggregate Root and Enrollment Entity

**Goal:** Create the rich domain model for Student (aggregate root) and Enrollment (entity) enforcing BR-4 and BR-5.

**Technical Details:**
- `com.sms.student.domain.model.Student`:
  - Fields: `Long id`, `StudentId studentId`, `String firstName`, `String lastName`, `LocalDate dateOfBirth`, `Mobile mobile`, `String email`, `String address`, `String fathersName`, `String mothersName`, `String identificationMark`, `AadhaarNumber aadhaarNumber`, `StudentStatus status`, `Integer version`, `OffsetDateTime createdAt`, `OffsetDateTime updatedAt`.
  - Static factory method `Student.create(...)` - sets `status = ACTIVE` (BR-5). Validates all required fields with `Objects.requireNonNull`.
  - Instance method `updateAllowedFields(String firstName, String lastName, Mobile mobile, StudentStatus status)` - enforces BR-4 (only these four fields are mutable post-registration).
  - No Spring annotations.
- `com.sms.student.domain.model.Enrollment`:
  - Fields: `Long id`, `Long studentFk`, `String studentId`, `String academicYear`, `String gradeClass`, `String section`, `LocalDate enrollmentDate`, `LocalDate withdrawalDate`, `String status`, `String remarks`, `OffsetDateTime createdAt`.
  - Lombok `@Data @Builder` (domain layer exception permitted for data classes).

**Dependencies:** BE-006.

---

### [BE-008] Implement Student Domain Repository Interfaces

**Goal:** Define port interfaces in the domain layer (dependency inversion principle).

**Technical Details:**
- `com.sms.student.domain.repository.StudentRepository` (interface):
  - `Student save(Student student)`
  - `Optional<Student> findByStudentId(String studentId)`
  - `Page<Student> findByLastName(String lastName, Pageable pageable)`
  - `Page<Student> findByGuardiansName(String guardianName, Pageable pageable)` (searches `fathersName`)
  - `Page<Student> findByStatus(StudentStatus status, Pageable pageable)`
  - `Page<Student> findAll(Pageable pageable)`
  - `boolean existsByMobile(String mobile)`
  - `boolean existsByAadhaarNumber(String aadhaarNumber)`
  - `void deleteByStudentId(String studentId)`
- `com.sms.student.domain.repository.EnrollmentRepository` (interface):
  - `Enrollment save(Enrollment enrollment)`
  - `List<Enrollment> findByStudentId(String studentId)`
  - `boolean existsByStudentFkAndAcademicYear(Long studentFk, String academicYear)`

**Dependencies:** BE-007.

---

### [BE-009] Implement Drools Fact Objects and Business Rule Files

**Goal:** Create the Drools fact POJOs and all `.drl` rule files for BR-1, BR-2, BR-7, and BR-3 (placeholder).

**Technical Details:**
- `com.sms.student.domain.rules.StudentRegistrationFact` - `@Data @Builder`, fields: `LocalDate dateOfBirth`, `String mobile`, `boolean mobileAlreadyExists`, `List<String> violations`. Methods: `addViolation(String)`, `hasViolations()`.
- `com.sms.student.domain.rules.EnrollmentFact` - `@Data @Builder`, fields: `String academicYear`, `boolean yearAlreadyEnrolled`, `List<String> violations`. Methods: `addViolation(String)`.
- Create `src/main/resources/META-INF/kmodule.xml`:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <kmodule xmlns="http://www.drools.org/xsd/kmodule">
      <kbase name="StudentRules" packages="rules.student">
          <ksession name="studentValidationSession" type="stateless" default="true"/>
      </kbase>
  </kmodule>
  ```
- Create `src/main/resources/rules/student/StudentAgeValidation.drl`:
  Rules: `BR-1: Reject student below minimum age` (age < 3), `BR-1: Reject student above maximum age` (age > 18), `BR-1: Reject null date of birth`. All add violation message `"Student age must be between 3 and 18 years at registration"`.
- Create `src/main/resources/rules/student/StudentMobileValidation.drl`:
  Rules: `BR-2: Reject duplicate mobile number` (when `mobileAlreadyExists == true`), `BR-2: Reject invalid mobile format` (when mobile does not match `\d{10}`).
- Create `src/main/resources/rules/student/EnrollmentValidation.drl`:
  Rule: `BR-7: Reject duplicate enrollment for academic year` (when `yearAlreadyEnrolled == true`).
- Create `src/main/resources/rules/student/ClassCapacityValidation.drl`:
  Rule: `BR-3: Class capacity placeholder - always passes in Phase 1` (uses `eval(false)` so rule never fires).

**Dependencies:** BE-007.

---

## Phase 3: Student Service - Infrastructure Layer

### [BE-010] Implement Student JPA Entities

**Goal:** Create JPA entity classes that map to the `students` and `enrollments` tables. These are separate from domain models.

**Technical Details:**
- `com.sms.student.infrastructure.persistence.entity.StudentJpaEntity`:
  - `@Entity @Table(name = "students") @Data @NoArgsConstructor`
  - All columns mapped per `specs/planning/school_management.sql` field-for-field.
  - `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id`
  - `@Column(name = "student_id", nullable = false, unique = true, length = 20) private String studentId`
  - `@Version @Column(name = "version", nullable = false) private Integer version`
  - `@Column(name = "status", nullable = false, length = 20) @Enumerated(EnumType.STRING) private StudentStatusJpa status` (local enum: `ACTIVE`, `INACTIVE`)
  - `@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ") private OffsetDateTime createdAt`
  - `@Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ") private OffsetDateTime updatedAt`
  - `@PrePersist` sets both timestamps to `OffsetDateTime.now()`.
- `com.sms.student.infrastructure.persistence.entity.EnrollmentJpaEntity`:
  - `@Entity @Table(name = "enrollments") @Data @NoArgsConstructor`
  - `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id`
  - `@Column(name = "student_fk", nullable = false) private Long studentFk`
  - All other columns mapped from `enrollments` table.

**Dependencies:** BE-003.

---

### [BE-011] Implement Student Spring Data JPA Repositories

**Goal:** Create the Spring Data JPA repository interfaces in the infrastructure layer.

**Technical Details:**
- `com.sms.student.infrastructure.persistence.repository.StudentJpaRepository` extends `JpaRepository<StudentJpaEntity, Long>`:
  - `@EntityGraph(attributePaths = {}) Optional<StudentJpaEntity> findByStudentId(String studentId)`
  - `@EntityGraph(attributePaths = {}) Page<StudentJpaEntity> findByLastNameIgnoreCase(String lastName, Pageable pageable)`
  - `Page<StudentJpaEntity> findByFathersNameContainingIgnoreCase(String guardianName, Pageable pageable)`
  - `Page<StudentJpaEntity> findByStatus(StudentStatusJpa status, Pageable pageable)`
  - `boolean existsByMobile(String mobile)`
  - `boolean existsByAadhaarNumber(String aadhaarNumber)`
  - `@Modifying @Query("DELETE FROM StudentJpaEntity s WHERE s.studentId = :studentId") int deleteByStudentId(@Param("studentId") String studentId)`
- `com.sms.student.infrastructure.persistence.repository.EnrollmentJpaRepository` extends `JpaRepository<EnrollmentJpaEntity, Long>`:
  - `List<EnrollmentJpaEntity> findByStudentId(String studentId)`
  - `boolean existsByStudentFkAndAcademicYear(Long studentFk, String academicYear)`

**Dependencies:** BE-010.

---

### [BE-012] Implement Student Infrastructure MapStruct Mappers

**Goal:** Create the MapStruct mapper that converts between domain models and JPA entities.

**Technical Details:**
- `com.sms.student.infrastructure.persistence.mapper.StudentInfraMapper` (`@Mapper(componentModel = "spring")`):
  - `StudentJpaEntity toEntity(Student domain)`:
    - `@Mapping(source = "mobile.value", target = "mobile")`
    - `@Mapping(source = "aadhaarNumber.value", target = "aadhaarNumber")`
    - `@Mapping(source = "studentId.value", target = "studentId")`
  - `Student toDomain(StudentJpaEntity entity)`:
    - `@Mapping(target = "mobile", expression = "java(new Mobile(entity.getMobile()))")`
    - `@Mapping(target = "aadhaarNumber", expression = "java(entity.getAadhaarNumber() != null ? new AadhaarNumber(entity.getAadhaarNumber()) : null)")`
    - `@Mapping(target = "studentId", expression = "java(new StudentId(entity.getStudentId()))")`
- `com.sms.student.infrastructure.persistence.mapper.EnrollmentInfraMapper` (`@Mapper(componentModel = "spring")`):
  - `EnrollmentJpaEntity toEntity(Enrollment domain)`
  - `Enrollment toDomain(EnrollmentJpaEntity entity)`

**Dependencies:** BE-010, BE-007.

---

### [BE-013] Implement Student Repository Adapter Implementations

**Goal:** Implement the domain repository interfaces using the JPA repositories (adapter pattern, dependency inversion).

**Technical Details:**
- `com.sms.student.infrastructure.persistence.repository.StudentRepositoryImpl` implements `com.sms.student.domain.repository.StudentRepository`:
  - `@Repository @RequiredArgsConstructor`
  - Delegates all methods to `StudentJpaRepository` + `StudentInfraMapper`.
  - `save(Student)`: calls `jpaRepository.save(infraMapper.toEntity(student))`, returns `infraMapper.toDomain(saved)`.
  - `findByStudentId(String)`: wraps JPA result with mapper.
  - `findByLastName(String, Pageable)`: uses `findByLastNameIgnoreCase`, maps page.
  - `deleteByStudentId(String)`: calls JPA delete, validates at least 1 row affected; throws `StudentNotFoundException` if 0 rows.
  - `existsByMobile(String)`, `existsByAadhaarNumber(String)`: direct delegation.
- `com.sms.student.infrastructure.persistence.repository.EnrollmentRepositoryImpl` implements `com.sms.student.domain.repository.EnrollmentRepository`:
  - Delegates to `EnrollmentJpaRepository` + `EnrollmentInfraMapper`.

**Dependencies:** BE-011, BE-012, BE-008.

---

### [BE-014] Implement Drools and Infrastructure Configuration Beans

**Goal:** Wire Drools and supporting infrastructure into the Spring context.

**Technical Details:**
- `com.sms.student.infrastructure.config.DroolsConfig` (`@Configuration`):
  ```java
  @Bean KieContainer kieContainer() { return KieServices.Factory.get().getKieClasspathContainer(); }
  @Bean StatelessKieSession studentValidationSession(KieContainer kc) { return kc.newStatelessKieSession("studentValidationSession"); }
  ```
- `com.sms.student.infrastructure.config.OpenApiConfig` (`@Configuration`): Configure `OpenAPI` bean with title `"Student Service API"`, version `"1.0.0"`, servers list pointing to `:8081/api/v1`.
- `com.sms.student.infrastructure.config.CorsConfig` (`@Configuration`): Implement `WebMvcConfigurer.addCorsMappings` to allow `GET, POST, PUT, DELETE, OPTIONS` from `http://localhost:5173`.
- `com.sms.student.infrastructure.config.MicrometerConfig` (`@Configuration`):
  - `Counter` bean: `students.registered.total`
  - `Counter` bean: `students.deleted.total`
  - `Timer` bean: `students.search.duration` with percentiles `0.5, 0.95, 0.99`

**Dependencies:** BE-009, BE-001.

---

## Phase 4: Student Service - Application Layer

### [BE-015] Implement Student Application DTOs

**Goal:** Create all request and response DTOs that form the public API contract. These must match `specs/sms_api_specification.yaml` exactly.

**Technical Details:**
- `com.sms.student.presentation.dto.request.CreateStudentRequest` (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`):
  - `@NotBlank @Size(min=2,max=100) @Pattern(regexp="^[a-zA-Z ]+$") String firstName`
  - `@NotBlank @Size(min=2,max=100) @Pattern(regexp="^[a-zA-Z ]+$") String lastName`
  - `@NotNull LocalDate dateOfBirth`
  - `@NotBlank @Pattern(regexp="^\\d{10}$") String mobile`
  - `@Email String email`
  - `String address`, `String fathersName`, `String mothersName`, `String identificationMark`
  - `@Pattern(regexp="^\\d{12}$") String aadhaarNumber`
- `com.sms.student.presentation.dto.request.UpdateStudentRequest` (`@Data @Builder`):
  - `@NotBlank @Size(min=2,max=100) @Pattern(regexp="^[a-zA-Z ]+$") String firstName`
  - `@NotBlank @Size(min=2,max=100) @Pattern(regexp="^[a-zA-Z ]+$") String lastName`
  - `@NotBlank @Pattern(regexp="^\\d{10}$") String mobile`
  - `@NotNull StudentStatus status`
  - `@NotNull Integer version`
- `com.sms.student.presentation.dto.request.CreateEnrollmentRequest` (`@Data @Builder`):
  - `@NotBlank String academicYear`, `@NotBlank String gradeClass`, `@NotBlank String section`
  - `@NotNull LocalDate enrollmentDate`, `String remarks`
- `com.sms.student.presentation.dto.response.StudentResponse` (`@Data @Builder`): All fields from `StudentBase` schema plus `id`, `studentId`, `status`, `version`, `createdAt`, `updatedAt`.
- `com.sms.student.presentation.dto.response.StudentSummaryResponse` (`@Data @Builder`): `studentId`, `firstName`, `lastName`, `mobile`, `status`.
- `com.sms.student.presentation.dto.response.PagedStudentResponse` (`@Data @Builder`): `List<StudentSummaryResponse> content`, `PaginationMetadata pageable`.
- `com.sms.student.presentation.dto.response.EnrollmentHistoryResponse` (`@Data @Builder`): `String studentId`, `List<EnrollmentResponse> enrollments`.
- `com.sms.student.presentation.dto.error.ErrorResponse` (`@Data @Builder`): `type`, `title`, `status`, `detail`, `instance`, `timestamp (OffsetDateTime)`, `correlationId`, `List<FieldErrorDto> errors`.
- `com.sms.student.presentation.dto.error.FieldErrorDto` (`@Data @AllArgsConstructor`): `String field`, `String message`, `String code`.
- `PaginationMetadata` (`@Data @Builder`): `int page`, `int size`, `long totalElements`, `int totalPages`.

**Dependencies:** BE-006.

---

### [BE-016] Implement Student Application Layer MapStruct Mapper (DTO to/from Domain)

**Goal:** Create the application-layer mapper translating between request DTOs and domain objects, and between domain objects and response DTOs.

**Technical Details:**
- `com.sms.student.application.mapper.StudentMapper` (`@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`):
  - `Student toDomain(CreateStudentRequest req)`:
    - `@Mapping(target = "mobile", expression = "java(new Mobile(req.getMobile()))")`
    - `@Mapping(target = "aadhaarNumber", expression = "java(req.getAadhaarNumber() != null ? new AadhaarNumber(req.getAadhaarNumber()) : null)")`
  - `StudentResponse toResponse(Student student)`:
    - `@Mapping(source = "studentId.value", target = "studentId")`
    - `@Mapping(source = "mobile.value", target = "mobile")`
    - `@Mapping(source = "aadhaarNumber.value", target = "aadhaarNumber")`
  - `StudentSummaryResponse toSummary(Student student)`: maps `studentId.value`, `mobile.value`, `status`.
  - `PagedStudentResponse toPagedResponse(Page<Student> page)`: maps content list and pagination metadata.
- `com.sms.student.application.mapper.EnrollmentMapper` (`@Mapper(componentModel = "spring")`):
  - `EnrollmentResponse toResponse(Enrollment enrollment)`

**Dependencies:** BE-015, BE-007.

---

### [BE-017] Implement Student Application Exceptions

**Goal:** Create all domain-specific exception classes used by services, mapped to HTTP responses by the global handler.

**Technical Details:**
- `com.sms.student.application.exception.BusinessRuleViolationException` extends `RuntimeException`: constructor takes `List<String> violations`. Method `getViolations()` returns unmodifiable list.
- `com.sms.student.application.exception.StudentNotFoundException` extends `RuntimeException`: constructor takes `String studentId`, message = `"Student not found: " + studentId`.
- `com.sms.student.application.exception.DuplicateMobileException` extends `RuntimeException`: constructor takes `String mobile`, message = `"Mobile number already registered: " + mobile`.
- `com.sms.student.application.exception.OptimisticLockConflictException` extends `RuntimeException`: default message = `"Record modified concurrently. Refresh and retry."`.
- `com.sms.student.application.exception.EnrollmentNotFoundException` extends `RuntimeException`.
- `com.sms.student.application.exception.DuplicateEnrollmentException` extends `RuntimeException`.

**Dependencies:** BE-001.

---

### [BE-018] Implement StudentCommandService

**Goal:** Implement all write operations (register, update, delete) for students with Drools rule evaluation.

**Technical Details:**
- `com.sms.student.application.command.StudentCommandService` (`@Service @RequiredArgsConstructor`):
- Injected dependencies: `StudentRepository`, `StatelessKieSession studentValidationSession`, `StudentMapper`, `Counter studentsRegisteredCounter`, `Counter studentsDeletedCounter`.
- Method `registerStudent(CreateStudentRequest request) -> StudentResponse`:
  1. Check `studentRepository.existsByMobile(request.getMobile())`.
  2. Build `StudentRegistrationFact` with `mobileAlreadyExists` pre-populated.
  3. Call `studentValidationSession.execute(fact)`.
  4. If `fact.hasViolations()` and violation is mobile duplicate: throw `DuplicateMobileException`. Otherwise: throw `BusinessRuleViolationException(fact.getViolations())`.
  5. Call `generateStudentId()` (format: `STD-YYYYMMDD-NNNN` using `student_id_seq` via `studentIdSequencePort`).
  6. Build `Student` via `studentMapper.toDomain(request)`, set `studentId`, set `status = ACTIVE`.
  7. `studentRepository.save(student)`, increment `studentsRegisteredCounter`, return `studentMapper.toResponse(saved)`.
- Method `updateStudent(String studentId, UpdateStudentRequest request) -> StudentResponse`:
  1. Find student via `studentRepository.findByStudentId(studentId)` - throw `StudentNotFoundException` if empty.
  2. Call `student.updateAllowedFields(request.getFirstName(), request.getLastName(), new Mobile(request.getMobile()), request.getStatus())`.
  3. Set `student.setVersion(request.getVersion())`.
  4. Save and return response. Catch `ObjectOptimisticLockingFailureException`, re-throw as `OptimisticLockConflictException`.
- Method `deleteStudent(String studentId)`:
  1. Verify student exists - throw `StudentNotFoundException` if not found.
  2. Call `studentRepository.deleteByStudentId(studentId)`.
  3. Increment `studentsDeletedCounter`.
- Private method `generateStudentId()`: uses `nextval('student_id_seq')` via a raw JDBC call or named native query. Format: `String.format("STD-%s-%04d", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), seqVal)`.

**Dependencies:** BE-013, BE-014, BE-016, BE-017, BE-009.

---

### [BE-019] Implement StudentQueryService

**Goal:** Implement all read operations (search, getById) for students with Micrometer timing.

**Technical Details:**
- `com.sms.student.application.query.StudentQueryService` (`@Service @RequiredArgsConstructor`):
- Injected: `StudentRepository`, `StudentMapper`, `Timer studentSearchTimer`.
- Method `searchStudents(String lastName, StudentStatus status, Pageable pageable) -> PagedStudentResponse`:
  - Wrapped in `studentSearchTimer.record(...)`.
  - If `lastName` is non-null: call `studentRepository.findByLastName(lastName, pageable)`.
  - Else if `status` is non-null: call `studentRepository.findByStatus(status, pageable)`.
  - Else: call `studentRepository.findAll(pageable)`.
  - Map page to `PagedStudentResponse` using `studentMapper.toPagedResponse(page)`.
- Method `getByStudentId(String studentId) -> StudentResponse`:
  - Call `studentRepository.findByStudentId(studentId)`, throw `StudentNotFoundException` if empty.
  - Map and return `studentMapper.toResponse(student)`.

**Dependencies:** BE-013, BE-016, BE-014.

---

### [BE-020] Implement EnrollmentCommandService and EnrollmentQueryService

**Goal:** Implement enrollment history management (add enrollment, retrieve history) for students.

**Technical Details:**
- `com.sms.student.application.command.EnrollmentCommandService` (`@Service @RequiredArgsConstructor`):
  - Method `addEnrollment(String studentId, CreateEnrollmentRequest request) -> EnrollmentResponse`:
    1. Verify student exists via `studentRepository.findByStudentId(studentId)` - throw `StudentNotFoundException` if not.
    2. Pre-check `enrollmentRepository.existsByStudentFkAndAcademicYear(studentFk, academicYear)`.
    3. Build `EnrollmentFact` with `yearAlreadyEnrolled` pre-populated.
    4. Fire via a new `StatelessKieSession` from `kieContainer.newStatelessKieSession("studentValidationSession")`.
    5. If violations: throw `DuplicateEnrollmentException`.
    6. Build `Enrollment` domain object, save, return response.
- `com.sms.student.application.query.EnrollmentQueryService` (`@Service @RequiredArgsConstructor`):
  - Method `getEnrollmentHistory(String studentId) -> EnrollmentHistoryResponse`:
    1. Verify student exists.
    2. `enrollmentRepository.findByStudentId(studentId)`.
    3. Map to `EnrollmentHistoryResponse`.

**Dependencies:** BE-013, BE-009, BE-016, BE-017.

---

## Phase 5: Student Service - Presentation Layer

### [BE-021] Implement StudentController

**Goal:** Create the REST controller for student CRUD operations matching the OpenAPI contract exactly.

**Technical Details:**
- `com.sms.student.presentation.controller.StudentController` (`@RestController @RequestMapping("/api/v1/students") @RequiredArgsConstructor @Tag(name = "Students")`):
  - `POST /` -> `registerStudent(@Valid @RequestBody CreateStudentRequest)` -> `@ResponseStatus(CREATED)` returns `StudentResponse`.
  - `GET /` -> `searchStudents(@RequestParam(required=false) String lastName, @RequestParam(required=false) StudentStatus status, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size, @RequestParam(defaultValue="id") String sortBy, @RequestParam(defaultValue="ASC") Sort.Direction sortDirection)` -> `PagedStudentResponse`.
  - `GET /{studentId}` -> `getStudent(@PathVariable String studentId)` -> `StudentResponse`.
  - `PUT /{studentId}` -> `updateStudent(@PathVariable String studentId, @Valid @RequestBody UpdateStudentRequest)` -> `StudentResponse`.
  - `DELETE /{studentId}` -> `deleteStudent(@PathVariable String studentId)` -> `@ResponseStatus(NO_CONTENT)`.
- All methods delegate entirely to `StudentCommandService` or `StudentQueryService`. No logic in controller.

**Dependencies:** BE-018, BE-019, BE-015.

---

### [BE-022] Implement EnrollmentController

**Goal:** Create the REST controller for student enrollment history management.

**Technical Details:**
- `com.sms.student.presentation.controller.EnrollmentController` (`@RestController @RequestMapping("/api/v1/students/{studentId}/enrollment-history") @RequiredArgsConstructor @Tag(name = "Enrollments")`):
  - `GET /` -> `getEnrollmentHistory(@PathVariable String studentId)` -> `EnrollmentHistoryResponse`.
  - `POST /` -> `addEnrollment(@PathVariable String studentId, @Valid @RequestBody CreateEnrollmentRequest)` -> `@ResponseStatus(CREATED)` returns `EnrollmentResponse`.

**Dependencies:** BE-020, BE-015.

---

### [BE-023] Implement Global Exception Handler and Correlation ID Filter

**Goal:** Implement RFC 7807 error responses for all exception types and propagate correlation IDs.

**Technical Details:**
- `com.sms.student.presentation.filter.CorrelationIdFilter` extends `OncePerRequestFilter` (`@Component @Order(1)`):
  - Reads `X-Correlation-ID` header; if absent generates `UUID.randomUUID().toString()`.
  - Sets as request attribute `"correlationId"` and as response header.
  - Puts in `MDC` as `"correlationId"`. Removes from MDC in `finally` block.
- `com.sms.student.presentation.advice.GlobalExceptionHandler` (`@RestControllerAdvice`):
  - `MethodArgumentNotValidException` -> 400 Bad Request with field errors list.
  - `StudentNotFoundException` -> 404 Not Found.
  - `DuplicateMobileException` -> 409 Conflict.
  - `BusinessRuleViolationException` -> 422 Unprocessable Entity with violations list.
  - `OptimisticLockConflictException` / `ObjectOptimisticLockingFailureException` -> 409 Conflict.
  - `DuplicateEnrollmentException` -> 409 Conflict.
  - Catch-all `Exception` -> 500 Internal Server Error. Log with `log.error`.
  - All responses use `ErrorResponse` DTO (RFC 7807 fields: `type`, `title`, `status`, `detail`, `instance`, `timestamp`, `correlationId`, `errors[]`).

**Dependencies:** BE-015, BE-017.

---

## Phase 6: Configuration Service - Full Implementation

### [BE-024] Implement Configuration Domain Model and Repository Interface

**Goal:** Create the Configuration aggregate root and its domain repository interface.

**Technical Details:**
- `com.sms.config.domain.model.ConfigurationSetting` (`@Data @Builder`):
  - Fields: `Long id`, `String category`, `String key`, `String value`, `String description`, `String dataType`, `boolean isEncrypted`, `Integer version`, `OffsetDateTime updatedAt`.
- `com.sms.config.domain.repository.ConfigurationRepository` (interface):
  - `ConfigurationSetting save(ConfigurationSetting setting)`
  - `Optional<ConfigurationSetting> findByCategoryAndKey(String category, String key)`
  - `List<ConfigurationSetting> findAll()`
  - `List<ConfigurationSetting> findByCategory(String category)`
  - `void deleteByCategoryAndKey(String category, String key)`
  - `boolean existsByCategoryAndKey(String category, String key)`

**Dependencies:** BE-004.

---

### [BE-025] Implement Configuration JPA Entity, Repository, and Adapter

**Goal:** Implement the infrastructure layer for the Configuration Service persistence.

**Technical Details:**
- `com.sms.config.infrastructure.persistence.entity.ConfigurationSettingJpaEntity`:
  - `@Entity @Table(name = "configuration_settings") @Data @NoArgsConstructor`
  - All columns from `configuration_settings` DDL. `@Version Integer version`. `@PreUpdate` sets `updatedAt = OffsetDateTime.now()`.
- `com.sms.config.infrastructure.persistence.repository.ConfigurationJpaRepository` extends `JpaRepository<ConfigurationSettingJpaEntity, Long>`:
  - `Optional<ConfigurationSettingJpaEntity> findByCategoryAndKey(String category, String key)`
  - `List<ConfigurationSettingJpaEntity> findByCategory(String category)`
  - `@Modifying @Query("DELETE FROM ConfigurationSettingJpaEntity c WHERE c.category = :category AND c.key = :key") int deleteByCategoryAndKey(...)`
- `com.sms.config.infrastructure.persistence.mapper.ConfigurationInfraMapper` (`@Mapper(componentModel = "spring")`): `toEntity(ConfigurationSetting)`, `toDomain(ConfigurationSettingJpaEntity)`.
- `com.sms.config.infrastructure.persistence.repository.ConfigurationRepositoryImpl` implements `ConfigurationRepository`: delegates to JPA repo + mapper.

**Dependencies:** BE-005, BE-024.

---

### [BE-026] Implement Configuration DTOs, Mapper, Exceptions, and Service Layer

**Goal:** Implement the full Application Layer for the Configuration Service including Redis caching.

**Technical Details:**
- DTOs (in `com.sms.config.presentation.dto`):
  - `UpsertConfigurationRequest`: `@NotBlank String value`, `String description`, `@NotNull String dataType` (validated against enum), `boolean isEncrypted`.
  - `ConfigurationResponse`: mirrors `Configuration` schema from OpenAPI spec. All fields.
  - `GroupedConfigurationResponse`: `String category`, `Map<String, String> settings`.
  - `ConfigurationListResponse`: `List<ConfigurationResponse> configurations`.
- MapStruct mapper: `ConfigurationMapper` (`@Mapper(componentModel = "spring")`): `toResponse(ConfigurationSetting)`, `toDomain(UpsertConfigurationRequest, String category, String key)`.
- Exceptions: `ConfigurationNotFoundException` (404), `ConfigurationConflictException` (409).
- `com.sms.config.application.command.ConfigurationCommandService` (`@Service`):
  - `upsertConfiguration(String category, String key, UpsertConfigurationRequest) -> ConfigurationResponse`:
    - Validate `category` is in `{GENERAL, ACADEMIC, FINANCIAL}`.
    - Find existing via `configurationRepository.findByCategoryAndKey(category, key)`.
    - If exists: update `value`, `description`, `dataType`, `isEncrypted`, increment `version`. Evict cache `@CacheEvict(value = "configurations", key = "#category")`.
    - If not exists: create new entity. Save.
    - Return response.
  - `deleteConfiguration(String category, String key)`:
    - Verify exists; throw `ConfigurationNotFoundException` if not.
    - Delete. Evict cache.
- `com.sms.config.application.query.ConfigurationQueryService` (`@Service`):
  - `getAllConfigurations(String category) -> ConfigurationListResponse`:
    - If `category` non-null: `findByCategory(category)`. Else: `findAll()`. Map and return.
  - `getGroupedByCategory(String category) -> GroupedConfigurationResponse` (`@Cacheable(value = "configurations", key = "#category")`):
    - Fetch `findByCategory(category)`. Build map `key -> value`. Increment cache hit/miss Micrometer counters.

**Dependencies:** BE-025, BE-005.

---

### [BE-027] Implement Configuration Controller, Global Exception Handler, and Correlation ID Filter

**Goal:** Expose all Configuration API endpoints per the OpenAPI specification and handle errors consistently.

**Technical Details:**
- `com.sms.config.presentation.controller.ConfigurationController` (`@RestController @RequestMapping("/api/v1/configurations") @Tag(name = "Configurations")`):
  - `GET /` -> `getAllConfigurations(@RequestParam(required=false) String category)` -> `ConfigurationListResponse`.
  - `GET /grouped/{category}` -> `getGroupedByCategory(@PathVariable String category)` -> `GroupedConfigurationResponse`.
  - `PUT /{category}/{key}` -> `upsertConfiguration(@PathVariable String category, @PathVariable String key, @Valid @RequestBody UpsertConfigurationRequest)` -> `ConfigurationResponse`. `@ResponseStatus(OK)`.
  - `DELETE /{category}/{key}` -> `deleteConfiguration(@PathVariable String category, @PathVariable String key)` -> `@ResponseStatus(NO_CONTENT)`.
- `com.sms.config.presentation.filter.CorrelationIdFilter`: same implementation as BE-023.
- `com.sms.config.presentation.advice.GlobalExceptionHandler`: same handler structure as BE-023 but for config exceptions (`ConfigurationNotFoundException` -> 404, `ConfigurationConflictException` -> 409).
- `com.sms.config.infrastructure.config.RedisConfig` (`@Configuration`): `@Bean RedisCacheConfiguration` with `entryTtl(Duration.ofMinutes(10))` and Jackson JSON serializer.
- `com.sms.config.infrastructure.config.MicrometerConfig`: `Counter configurations.cache.hit`, `Counter configurations.cache.miss`.
- `com.sms.config.infrastructure.config.CorsConfig`: same CORS settings as BE-014.
- `com.sms.config.infrastructure.config.OpenApiConfig`: title `"Configuration Service API"`.

**Dependencies:** BE-026.

---

## Phase 7: DevOps and Observability

### [BE-028] Create Dockerfiles for Both Services

**Goal:** Create production-ready, multi-stage Dockerfiles for each Spring Boot service.

**Technical Details:**
- `student-service/Dockerfile`:
  ```dockerfile
  FROM eclipse-temurin:21-jre-alpine AS runtime
  WORKDIR /app
  COPY target/student-service-*.jar app.jar
  EXPOSE 8081
  ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
  ```
- `config-service/Dockerfile`: Same structure, EXPOSE 8082, copies `config-service-*.jar`.
- `.dockerignore` in each service root: exclude `target/`, `.git/`, `*.md`, `src/test/`.

**Dependencies:** BE-001, BE-004.

---

### [BE-029] Create Root docker-compose.yml

**Goal:** Wire all services, databases, Redis, and Zipkin into a single Compose file for local development.

**Technical Details:**
- Create `docker-compose.yml` at project root containing services per `specs/architecture/05-backend-implementation-guide.md` Section 10:
  - `student-db`: `postgres:18-alpine`, env `POSTGRES_DB=student_db`, `POSTGRES_USER=student_user`, `POSTGRES_PASSWORD=${STUDENT_DB_PASSWORD}`, port `5432:5432`, named volume `student-db-data`.
  - `student-service`: `build: ./student-service`, port `8081:8081`, env vars `STUDENT_DB_URL`, `STUDENT_DB_USER`, `STUDENT_DB_PASSWORD`, `depends_on: [student-db, zipkin]`, `healthcheck: GET /actuator/health`.
  - `config-db`: `postgres:18-alpine`, env `POSTGRES_DB=config_db`, port `5433:5433`, named volume `config-db-data`.
  - `config-service`: `build: ./config-service`, port `8082:8082`, env vars `CONFIG_DB_URL`, `CONFIG_DB_USER`, `CONFIG_DB_PASSWORD`, `REDIS_HOST=redis`, `REDIS_PORT=6379`, `depends_on: [config-db, redis, zipkin]`.
  - `redis`: `redis:7-alpine`, port `6379:6379`.
  - `zipkin`: `openzipkin/zipkin:3`, port `9411:9411`.
- Create `.env.example` at project root with placeholder values for all environment variables (no real credentials).

**Dependencies:** BE-028.

---

### [BE-030] Verify Service Startup and API Documentation Accessibility

**Goal:** Confirm both services start cleanly, Flyway migrations run, and Swagger UI is accessible.

**Technical Details:**
- Run `mvn clean package -DskipTests` for both services.
- Run `docker-compose up student-db config-db redis zipkin` to start infrastructure only.
- Start `student-service` locally: `java -jar target/student-service-*.jar`. Verify:
  - `GET /actuator/health` returns `{"status":"UP"}`.
  - `GET /swagger-ui.html` renders student API documentation.
  - Flyway migration log shows `V1__init_students.sql` and `V2__init_enrollments.sql` executed successfully.
- Start `config-service` locally. Verify:
  - `GET /actuator/health` returns `{"status":"UP"}`.
  - `GET /swagger-ui.html` renders config API documentation.
  - Flyway migration shows `V1__init_configuration_settings.sql` executed.

**Dependencies:** BE-029.

---

## Dependency Summary

```
BE-001 -> BE-002, BE-003, BE-006
BE-004 -> BE-005, BE-024
BE-006 -> BE-007
BE-007 -> BE-008, BE-009, BE-010 (via BE-003)
BE-008 -> BE-013
BE-009 -> BE-014, BE-018, BE-020
BE-010 -> BE-011
BE-011 -> BE-013
BE-012 -> BE-013
BE-013 -> BE-018, BE-019, BE-020
BE-014 -> BE-018, BE-019
BE-015 -> BE-016, BE-021, BE-022, BE-023
BE-016 -> BE-018, BE-019, BE-020
BE-017 -> BE-018, BE-020, BE-023
BE-018 -> BE-021
BE-019 -> BE-021
BE-020 -> BE-022
BE-021 -> BE-030
BE-022 -> BE-030
BE-023 -> BE-030
BE-024 -> BE-025
BE-025 -> BE-026
BE-026 -> BE-027
BE-027 -> BE-030
BE-028 -> BE-029
BE-029 -> BE-030
```
