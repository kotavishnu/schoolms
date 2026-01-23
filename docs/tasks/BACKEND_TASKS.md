# Backend Implementation Tasks - School Management System

**Version:** 1.0
**Date:** 2026-01-22
**Target Agent:** Backend Developer
**Execution Model:** Waterfall / Single-Pass Implementation

---

## Critical Constraints (MANDATORY)

### Global Directives Enforcement

- **D-001:** Spring Boot 3.3.5 + SpringDoc 2.6.0 (EXACT versions - compatibility critical)
- **D-002:** MapStruct for ALL DTO mapping (no manual mapping)
- **D-003:** Optimistic locking with @Version on ALL entities
- **D-010:** Database-per-service isolation (ZERO cross-service access)

### Technology Stack (NON-NEGOTIABLE)

```xml
Java: 21 LTS
Spring Boot: 3.3.5
SpringDoc OpenAPI: 2.6.0
PostgreSQL: 18
Drools: 9.44.0.Final
MapStruct: 1.5.5.Final
Redis: 7.x
Flyway: 9.x
```

---

## Database Setup

### [BE-001] Generate Single SQL Schema Script

**Goal:** Create a production-ready PostgreSQL DDL script for both Student and Configuration databases.

**Technical Details:**
- **Location:** `docs/tasks/school_management.sql`
- **Contents:**
  - DROP TABLE IF EXISTS statements
  - Students table with ALL constraints (PK, UNIQUE, CHECK, FK)
  - Enrollments table with foreign key to students
  - Configurations table
  - Indexes (B-tree, unique)
  - Triggers (updated_at auto-update)
  - NO stored procedures, NO migration tools

**Reference:** `specs/architecture/02-database-design.md` sections 3.2, 3.3, 4.2

**SQL Requirements:**
```sql
-- Students table constraints
CONSTRAINT pk_students PRIMARY KEY (id)
CONSTRAINT uniq_students_student_id UNIQUE (student_id)
CONSTRAINT uniq_students_mobile UNIQUE (mobile)
CONSTRAINT uniq_students_email UNIQUE (email)
CONSTRAINT chk_students_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
CONSTRAINT chk_students_mobile CHECK (mobile ~ '^\d{10}$')
CONSTRAINT chk_students_age CHECK (
    date_of_birth <= CURRENT_DATE - INTERVAL '3 years' AND
    date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
)

-- Configurations table
CONSTRAINT uniq_configurations_category_key UNIQUE (category, key)
CONSTRAINT chk_configurations_category CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'))
```

**Success Criteria:**
- Script runs cleanly on fresh PostgreSQL 18 instance
- All tables created with correct schema
- All constraints enforced
- Triggers functional for updated_at

**Dependencies:** None

---

## Student Service Implementation

### [BE-002] Student Service Project Setup

**Goal:** Initialize Spring Boot project for Student Service with all dependencies.

**Technical Details:**
- **Service Name:** `student-service`
- **Port:** 8081
- **Base Package:** `com.school.student`

**Dependencies (pom.xml):**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Data -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- Drools -->
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-core</artifactId>
        <version>9.44.0.Final</version>
    </dependency>
    <dependency>
        <groupId>org.kie</groupId>
        <artifactId>kie-spring</artifactId>
        <version>7.74.1.Final</version>
    </dependency>

    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- API Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.6.0</version>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**application.yml:**
```yaml
server:
  port: 8081

spring:
  application:
    name: student-service

  datasource:
    url: jdbc:postgresql://localhost:5433/student_db
    username: postgres
    password: postgres
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
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

springdoc:
  api-docs:
    path: /api/v1/api-docs
  swagger-ui:
    path: /api/v1/swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

**Success Criteria:**
- Project builds successfully
- Application starts on port 8081
- SpringDoc UI accessible at http://localhost:8081/api/v1/swagger-ui.html
- Database connection established to student_db

**Dependencies:** BE-001

---

### [BE-003] Domain Layer - Student Entity

**Goal:** Create JPA entity for Student with business logic and optimistic locking.

**Technical Details:**
- **Package:** `com.school.student.domain.entity`
- **Class:** `Student.java`

**Implementation Pattern (from 05-backend-implementation-guide.md section 3.1):**
```java
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false, length = 50)
    private String studentId;

    // Personal Information
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Column(name = "identification_marks", length = 200)
    private String identificationMarks;

    // Guardian Information
    @Column(name = "guardian_name", nullable = false, length = 100)
    private String guardianName;

    @Column(name = "mother_name", nullable = false, length = 100)
    private String motherName;

    // Contact Information
    @Column(name = "mobile", unique = true, nullable = false, length = 10)
    private String mobile;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;

    // Optimistic Locking (D-003)
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business Logic
    public void generateStudentId() {
        if (this.studentId == null) {
            this.studentId = StudentIdGenerator.generate();
        }
    }

    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
    }
}

public enum StudentStatus {
    ACTIVE,
    INACTIVE
}
```

**Additional Classes:**
- `StudentStatus.java` (enum)
- `StudentIdGenerator.java` (utility for STD-YYYYMMDD-NNNN format)

**Success Criteria:**
- Entity maps to students table
- All fields annotated correctly
- @Version field present for optimistic locking
- Business methods functional

**Dependencies:** BE-002

---

### [BE-004] Domain Layer - Enrollment Entity

**Goal:** Create JPA entity for Enrollment history tracking.

**Technical Details:**
- **Package:** `com.school.student.domain.entity`
- **Class:** `Enrollment.java`

**Implementation:**
```java
@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "grade_class", nullable = false, length = 50)
    private String gradeClass;

    @Column(name = "section", nullable = false, length = 10)
    private String section;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

public enum EnrollmentStatus {
    ACTIVE,
    WITHDRAWN,
    COMPLETED
}
```

**Success Criteria:**
- Entity maps to enrollments table
- Foreign key relationship to Student
- All fields annotated correctly

**Dependencies:** BE-003

---

### [BE-005] Infrastructure Layer - Student Repository

**Goal:** Create Spring Data JPA repository for Student entity.

**Technical Details:**
- **Package:** `com.school.student.repository`
- **Interface:** `StudentRepository.java`

**Implementation (from 05-backend-implementation-guide.md section 5.1):**
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Business key lookup
    Optional<Student> findByStudentId(String studentId);

    // Uniqueness checks
    boolean existsByMobile(String mobile);
    boolean existsByMobileAndStudentIdNot(String mobile, String studentId);

    boolean existsByEmail(String email);
    boolean existsByEmailAndStudentIdNot(String email, String studentId);

    boolean existsByAadhaarNumber(String aadhaarNumber);

    // Search queries
    Page<Student> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    Page<Student> findByLastNameContainingIgnoreCaseAndStatus(
        String lastName,
        StudentStatus status,
        Pageable pageable
    );

    @Query("SELECT s FROM Student s WHERE s.guardianName LIKE %:guardianName%")
    Page<Student> findByGuardianNameContaining(
        @Param("guardianName") String guardianName,
        Pageable pageable
    );

    // Count queries for dashboard
    long countByStatus(StudentStatus status);
}
```

**Success Criteria:**
- All query methods functional
- Pagination support verified
- Uniqueness check methods working

**Dependencies:** BE-003

---

### [BE-006] Drools Configuration and Rules

**Goal:** Setup Drools engine and implement student validation rules.

**Technical Details:**
- **Configuration:** `com.school.student.config.DroolsConfig.java`
- **Rules File:** `src/main/resources/rules/student-validation.drl`

**DroolsConfig.java (from 03-business-rules.md section 2.3):**
```java
@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:rules/**/*.drl");

        for (Resource resource : resources) {
            kieFileSystem.write("src/main/resources/" + resource.getFilename(),
                kieServices.getResources()
                    .newInputStreamResource(resource.getInputStream()));
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}
```

**student-validation.drl (Implement ALL 7 Business Rules):**
- **BR-STU-001:** Age range check (3-18 years)
- **BR-STU-002:** Mobile uniqueness (handled in service)
- **BR-STU-003:** Email format validation
- **BR-STU-004:** Aadhaar format validation (12 digits)
- **BR-STU-005:** Name pattern validation (letters and spaces only)
- **BR-STU-006:** Mobile format (10 digits)
- **BR-STU-007:** Editable fields constraint (enforced in API)

**Reference:** `specs/architecture/03-business-rules.md` section 4.2

**Success Criteria:**
- KieContainer bean created
- DRL file loaded successfully
- All 7 business rules implemented
- Rules fire correctly in tests

**Dependencies:** BE-002

---

### [BE-007] Application Layer - Drools Validation Service

**Goal:** Create service to execute Drools rules with database uniqueness checks.

**Technical Details:**
- **Package:** `com.school.student.service`
- **Class:** `DroolsValidationService.java`

**Implementation (from 03-business-rules.md section 4.3):**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsValidationService {

    private final KieContainer kieContainer;
    private final StudentRepository studentRepository;

    public ValidationResult validateStudent(StudentValidationRequest request) {
        ValidationResult result = new ValidationResult();

        // Execute Drools rules
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.setGlobal("validationResult", result);
            kieSession.insert(request);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        // BR-STU-002: Mobile uniqueness check (database)
        if (request.isCheckMobileUniqueness() && request.getMobile() != null) {
            boolean mobileExists = studentRepository.existsByMobileAndStudentIdNot(
                request.getMobile(),
                request.getStudentId() != null ? request.getStudentId() : ""
            );

            if (mobileExists) {
                result.addError(
                    "mobile",
                    "MOBILE_ALREADY_EXISTS",
                    "Mobile number is already registered to another student"
                );
            }
        }

        log.debug("Validation result: valid={}, errors={}",
            result.isValid(), result.getErrors());
        return result;
    }
}
```

**Additional Classes:**
- `StudentValidationRequest.java` (request fact object)
- `ValidationResult.java` (result with errors list)

**Reference:** `specs/architecture/03-business-rules.md` section 4.1

**Success Criteria:**
- Drools rules execute correctly
- Database uniqueness checks functional
- Validation errors collected properly

**Dependencies:** BE-005, BE-006

---

### [BE-008] DTO Layer - Student DTOs

**Goal:** Create Data Transfer Objects for Student API contract.

**Technical Details:**
- **Package:** `com.school.student.dto.request` and `com.school.student.dto.response`

**Classes:**
```java
// StudentRequest.java
@Data
@Builder
public class StudentRequest {
    @NotBlank
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    private String mobile;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 500)
    private String address;

    @Pattern(regexp = "^\\d{12}$")
    private String aadhaarNumber;

    @NotBlank
    @Size(max = 100)
    private String guardianName;

    @NotBlank
    @Size(max = 100)
    private String motherName;

    @Size(max = 200)
    private String identificationMarks;
}

// StudentUpdateRequest.java (only editable fields)
@Data
@Builder
public class StudentUpdateRequest {
    @NotBlank
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    private String mobile;

    @NotNull
    private StudentStatus status;

    @NotNull
    private Integer version; // For optimistic locking
}

// StudentResponse.java
@Data
@Builder
public class StudentResponse {
    private String studentId; // Business key (NOT database id)
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String address;
    private String aadhaarNumber;
    private String guardianName;
    private String motherName;
    private String identificationMarks;
    private StudentStatus status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Field Mapping Note:**
- Frontend uses `phone`, backend API exposes `mobile`
- Frontend uses `id`, backend API exposes `studentId`
- This is the BACKEND side - use `mobile` and `studentId`

**Success Criteria:**
- All DTOs properly validated with Jakarta annotations
- Field naming matches API specification

**Dependencies:** BE-003

---

### [BE-009] DTO Layer - MapStruct Mapper

**Goal:** Create MapStruct mapper for Student Entity ↔ DTO conversions.

**Technical Details:**
- **Package:** `com.school.student.mapper`
- **Interface:** `StudentMapper.java`

**Implementation (from 05-backend-implementation-guide.md section 4.2):**
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    // Request DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(StudentRequest request);

    // Entity → Response DTO
    StudentResponse toResponse(Student entity);

    // Update Entity from Request (only allowed fields - BR-STU-007)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "aadhaarNumber", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "guardianName", ignore = true)
    @Mapping(target = "motherName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "identificationMarks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(StudentUpdateRequest request, @MappingTarget Student entity);
}
```

**Success Criteria:**
- MapStruct generates implementation at compile time
- All mappings verified
- Immutable fields protected during updates

**Dependencies:** BE-003, BE-008

---

### [BE-010] Application Layer - Student Service

**Goal:** Implement core business logic for Student CRUD operations.

**Technical Details:**
- **Package:** `com.school.student.service`
- **Class:** `StudentService.java`

**Implementation Pattern (from 05-backend-implementation-guide.md section 4.1):**
```java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final DroolsValidationService validationService;

    // CREATE
    public StudentResponse registerStudent(StudentRequest request) {
        log.info("Registering student: {} {}",
            request.getFirstName(), request.getLastName());

        // 1. Validate using Drools
        ValidationResult validationResult = validationService.validateStudent(
            buildValidationRequest(request, null)
        );

        if (!validationResult.isValid()) {
            log.warn("Validation failed: {}", validationResult.getErrors());
            throw new ValidationException(
                "Student validation failed",
                validationResult.getErrors()
            );
        }

        // 2. Map DTO to Entity
        Student student = mapper.toEntity(request);

        // 3. Generate Student ID (business logic)
        student.generateStudentId();

        // 4. Save
        Student saved = repository.save(student);
        log.info("Student registered with ID: {}", saved.getStudentId());

        // 5. Return DTO
        return mapper.toResponse(saved);
    }

    // READ
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#studentId")
    public StudentResponse getStudentById(String studentId) {
        log.debug("Fetching student: {}", studentId);

        return repository.findByStudentId(studentId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    // UPDATE
    @CacheEvict(value = {"students", "studentSearchResults"}, key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        log.info("Updating student: {}", studentId);

        Student student = repository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Validate editable fields
        ValidationResult validationResult = validationService.validateUpdate(
            buildUpdateValidationRequest(request, studentId)
        );

        if (!validationResult.isValid()) {
            throw new ValidationException(
                "Update validation failed",
                validationResult.getErrors()
            );
        }

        // Update ONLY allowed fields (BR-STU-007)
        mapper.updateEntityFromRequest(request, student);

        Student updated = repository.save(student);
        log.info("Student updated: {}", studentId);

        return mapper.toResponse(updated);
    }

    // DELETE
    @CacheEvict(value = {"students", "studentSearchResults"}, allEntries = true)
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = repository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        repository.delete(student);
        log.info("Student deleted: {}", studentId);
    }

    // SEARCH
    @Transactional(readOnly = true)
    public Page<StudentResponse> searchStudents(
        String lastName,
        StudentStatus status,
        Pageable pageable
    ) {
        log.debug("Searching students: lastName={}, status={}", lastName, status);

        Page<Student> students;

        if (lastName != null && status != null) {
            students = repository.findByLastNameContainingIgnoreCaseAndStatus(
                lastName, status, pageable
            );
        } else if (lastName != null) {
            students = repository.findByLastNameContainingIgnoreCase(
                lastName, pageable
            );
        } else if (status != null) {
            students = repository.findByStatus(status, pageable);
        } else {
            students = repository.findAll(pageable);
        }

        return students.map(mapper::toResponse);
    }

    // Dashboard statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        long total = repository.count();
        long active = repository.countByStatus(StudentStatus.ACTIVE);
        long inactive = repository.countByStatus(StudentStatus.INACTIVE);

        return Map.of(
            "total", total,
            "active", active,
            "inactive", inactive
        );
    }
}
```

**Success Criteria:**
- All CRUD operations functional
- Drools validation integrated
- Optimistic locking working (version check)
- Caching applied correctly

**Dependencies:** BE-005, BE-007, BE-009

---

### [BE-011] Presentation Layer - Student Controller

**Goal:** Expose RESTful API endpoints for Student operations.

**Technical Details:**
- **Package:** `com.school.student.controller`
- **Class:** `StudentController.java`
- **Base Path:** `/api/v1/students`

**Implementation (API Contract Compliance - sms_api_specification.yaml):**
```java
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> searchStudents(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) StudentStatus status,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<StudentResponse> students = studentService.searchStudents(
            lastName, status, pageable
        );
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(
        @PathVariable String studentId
    ) {
        StudentResponse student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> registerStudent(
        @Valid @RequestBody StudentRequest request
    ) {
        StudentResponse created = studentService.registerStudent(request);
        URI location = URI.create("/api/v1/students/" + created.getStudentId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable String studentId,
        @Valid @RequestBody StudentUpdateRequest request
    ) {
        StudentResponse updated = studentService.updateStudent(studentId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = studentService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
```

**Success Criteria:**
- All endpoints match API specification
- HTTP methods correct (GET, POST, PUT, DELETE)
- Status codes correct (200, 201, 204, 400, 404, 409)
- Request/Response DTOs match spec

**Dependencies:** BE-010

---

### [BE-012] Exception Handling - Global Exception Handler

**Goal:** Implement RFC 7807 Problem Details error responses.

**Technical Details:**
- **Package:** `com.school.student.exception`
- **Class:** `GlobalExceptionHandler.java`

**Implementation:**
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleStudentNotFound(
        StudentNotFoundException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setTitle("Student Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
        ValidationException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed"
        );

        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("errors", ex.getErrors());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(
        OptimisticLockException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Record was modified by another transaction"
        );
        problemDetail.setTitle("Optimistic Lock Failure");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameters"
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unexpected error", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(problemDetail);
    }
}
```

**Custom Exception Classes:**
- `StudentNotFoundException.java`
- `ValidationException.java` (with List<ValidationError> errors)

**Success Criteria:**
- All exceptions handled with RFC 7807 format
- Appropriate HTTP status codes
- Field-level errors for validation failures

**Dependencies:** BE-010, BE-011

---

### [BE-013] Redis Cache Configuration

**Goal:** Setup Redis caching for Student data.

**Technical Details:**
- **Package:** `com.school.student.config`
- **Class:** `CacheConfig.java`

**Implementation (from 05-backend-implementation-guide.md section 5.2):**
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            )
            .prefixCacheNameWith("sms:student:");

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("students",
                config.entryTtl(Duration.ofHours(4)))
            .withCacheConfiguration("studentSearchResults",
                config.entryTtl(Duration.ofMinutes(15)))
            .build();
    }
}
```

**Success Criteria:**
- Redis connection established (DB 0)
- Cache TTLs configured correctly
- @Cacheable and @CacheEvict annotations working

**Dependencies:** BE-002

---

### [BE-014] CORS Configuration

**Goal:** Enable CORS for frontend integration.

**Technical Details:**
- **Package:** `com.school.student.config`
- **Class:** `CorsConfig.java`

**Implementation (from 05-backend-implementation-guide.md section 8.2):**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://localhost:4173"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

**Success Criteria:**
- Frontend can call APIs from localhost:5173
- Preflight OPTIONS requests handled

**Dependencies:** BE-002

---

## Configuration Service Implementation

### [BE-015] Configuration Service Project Setup

**Goal:** Initialize Spring Boot project for Configuration Service.

**Technical Details:**
- **Service Name:** `configuration-service`
- **Port:** 8082
- **Base Package:** `com.school.configuration`

**Dependencies:** Same as Student Service (BE-002) except:
- NO Drools dependencies (no business rules for Configuration)

**application.yml:**
```yaml
server:
  port: 8082

spring:
  application:
    name: configuration-service

  datasource:
    url: jdbc:postgresql://localhost:5434/config_db
    username: postgres
    password: postgres

  data:
    redis:
      database: 1  # Configuration service uses DB 1
```

**Success Criteria:**
- Project builds successfully
- Application starts on port 8082
- Database connection to config_db

**Dependencies:** BE-001

---

### [BE-016] Domain Layer - Configuration Entity

**Goal:** Create JPA entity for Configuration settings.

**Technical Details:**
- **Package:** `com.school.configuration.domain.entity`
- **Class:** `Configuration.java`

**Implementation:**
```java
@Entity
@Table(name = "configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ConfigCategory category;

    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 20)
    private DataType dataType = DataType.STRING;

    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted = false;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

public enum ConfigCategory {
    GENERAL,
    ACADEMIC,
    FINANCIAL,
    SYSTEM
}

public enum DataType {
    STRING,
    NUMBER,
    BOOLEAN,
    JSON
}
```

**Success Criteria:**
- Entity maps to configurations table
- Unique constraint on (category, key)
- All fields annotated correctly

**Dependencies:** BE-015

---

### [BE-017] Infrastructure Layer - Configuration Repository

**Goal:** Create Spring Data JPA repository for Configuration.

**Technical Details:**
- **Package:** `com.school.configuration.repository`
- **Interface:** `ConfigurationRepository.java`

**Implementation:**
```java
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByCategoryAndKey(
        ConfigCategory category,
        String key
    );

    List<Configuration> findByCategory(ConfigCategory category);

    boolean existsByCategoryAndKey(ConfigCategory category, String key);

    void deleteByCategoryAndKey(ConfigCategory category, String key);
}
```

**Success Criteria:**
- Category-based retrieval working
- Composite key lookup functional

**Dependencies:** BE-016

---

### [BE-018] DTO Layer - Configuration DTOs and Mapper

**Goal:** Create DTOs and MapStruct mapper for Configuration.

**Technical Details:**
- **Package:** `com.school.configuration.dto` and `com.school.configuration.mapper`

**ConfigurationRequest.java:**
```java
@Data
@Builder
public class ConfigurationRequest {
    @NotNull
    private ConfigCategory category;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[A-Z0-9_]+$")
    private String key;

    @NotBlank
    @Size(max = 1000)
    private String value;

    @Size(max = 500)
    private String description;

    private DataType dataType;

    private Boolean isEncrypted;
}
```

**ConfigurationResponse.java:**
```java
@Data
@Builder
public class ConfigurationResponse {
    private Long id;
    private ConfigCategory category;
    private String key;
    private String value;
    private String description;
    private DataType dataType;
    private Integer version;
    private LocalDateTime updatedAt;
}
```

**ConfigurationMapper.java:**
```java
@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Configuration toEntity(ConfigurationRequest request);

    ConfigurationResponse toResponse(Configuration entity);
}
```

**Success Criteria:**
- DTOs validated correctly
- MapStruct mappings functional

**Dependencies:** BE-016

---

### [BE-019] Application Layer - Configuration Service

**Goal:** Implement CRUD operations for Configuration management.

**Technical Details:**
- **Package:** `com.school.configuration.service`
- **Class:** `ConfigurationService.java`

**Implementation:**
```java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ConfigurationRepository repository;
    private final ConfigurationMapper mapper;

    public List<ConfigurationResponse> getAllConfigurations(
        ConfigCategory category
    ) {
        List<Configuration> configs;

        if (category != null) {
            configs = repository.findByCategory(category);
        } else {
            configs = repository.findAll();
        }

        return configs.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "configurations", key = "#category + '-' + #key")
    public ConfigurationResponse getConfiguration(
        ConfigCategory category,
        String key
    ) {
        return repository.findByCategoryAndKey(category, key)
            .map(mapper::toResponse)
            .orElseThrow(() -> new ConfigurationNotFoundException(category, key));
    }

    @CacheEvict(value = "configurations", key = "#category + '-' + #key")
    public ConfigurationResponse upsertConfiguration(
        ConfigCategory category,
        String key,
        ConfigurationRequest request
    ) {
        Optional<Configuration> existing =
            repository.findByCategoryAndKey(category, key);

        Configuration config;
        if (existing.isPresent()) {
            config = existing.get();
            config.setValue(request.getValue());
            config.setDescription(request.getDescription());
            config.setDataType(request.getDataType());
            config.setIsEncrypted(request.getIsEncrypted());
        } else {
            config = mapper.toEntity(request);
            config.setCategory(category);
            config.setKey(key);
        }

        Configuration saved = repository.save(config);
        return mapper.toResponse(saved);
    }

    @CacheEvict(value = "configurations", key = "#category + '-' + #key")
    public void deleteConfiguration(ConfigCategory category, String key) {
        if (!repository.existsByCategoryAndKey(category, key)) {
            throw new ConfigurationNotFoundException(category, key);
        }
        repository.deleteByCategoryAndKey(category, key);
    }

    public Map<String, String> getGroupedConfigurations(ConfigCategory category) {
        return repository.findByCategory(category).stream()
            .collect(Collectors.toMap(
                Configuration::getKey,
                Configuration::getValue
            ));
    }
}
```

**Success Criteria:**
- CRUD operations functional
- Upsert logic working (create or update)
- Caching applied correctly

**Dependencies:** BE-017, BE-018

---

### [BE-020] Presentation Layer - Configuration Controller

**Goal:** Expose RESTful API endpoints for Configuration operations.

**Technical Details:**
- **Package:** `com.school.configuration.controller`
- **Class:** `ConfigurationController.java`
- **Base Path:** `/api/v1/configurations`

**Implementation:**
```java
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Slf4j
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<List<ConfigurationResponse>> getAllConfigurations(
        @RequestParam(required = false) ConfigCategory category
    ) {
        List<ConfigurationResponse> configs =
            configurationService.getAllConfigurations(category);
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{category}/{key}")
    public ResponseEntity<ConfigurationResponse> getConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key
    ) {
        ConfigurationResponse config =
            configurationService.getConfiguration(category, key);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{category}/{key}")
    public ResponseEntity<ConfigurationResponse> upsertConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key,
        @Valid @RequestBody ConfigurationRequest request
    ) {
        ConfigurationResponse config =
            configurationService.upsertConfiguration(category, key, request);
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/{category}/{key}")
    public ResponseEntity<Void> deleteConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key
    ) {
        configurationService.deleteConfiguration(category, key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grouped/{category}")
    public ResponseEntity<Map<String, String>> getGroupedConfigurations(
        @PathVariable ConfigCategory category
    ) {
        Map<String, String> grouped =
            configurationService.getGroupedConfigurations(category);
        return ResponseEntity.ok(grouped);
    }
}
```

**Success Criteria:**
- All endpoints match API specification
- HTTP methods correct
- Status codes correct

**Dependencies:** BE-019

---

### [BE-021] Configuration Service - CORS and Exception Handling

**Goal:** Add CORS config and global exception handler.

**Technical Details:**
- Copy CorsConfig from Student Service (same configuration)
- Create GlobalExceptionHandler with ConfigurationNotFoundException

**Success Criteria:**
- CORS enabled for frontend
- Exceptions handled with RFC 7807

**Dependencies:** BE-020

---

## Testing

### [BE-022] Student Service - Unit Tests

**Goal:** Write unit tests for Student Service layer.

**Technical Details:**
- **Coverage Target:** 95% for domain layer, 85% for application layer
- **Package:** `src/test/java/com/school/student/service`

**Test Cases:**
```java
@SpringBootTest
@Transactional
class StudentServiceTest {

    @Test
    void shouldRegisterStudentSuccessfully() {
        // Given: Valid student request
        // When: registerStudent called
        // Then: Student saved with generated ID
    }

    @Test
    void shouldRejectStudentWithInvalidAge() {
        // Given: Student with age < 3
        // When: registerStudent called
        // Then: ValidationException thrown
    }

    @Test
    void shouldRejectDuplicateMobile() {
        // Given: Existing student with mobile
        // When: New student with same mobile
        // Then: ValidationException with MOBILE_ALREADY_EXISTS
    }

    @Test
    void shouldUpdateOnlyAllowedFields() {
        // Given: Existing student
        // When: Update with firstName, lastName, mobile, status
        // Then: Only these fields updated, others unchanged
    }

    @Test
    void shouldHandleOptimisticLockConflict() {
        // Given: Two concurrent updates
        // When: Second update with stale version
        // Then: OptimisticLockException thrown
    }
}
```

**Success Criteria:**
- All business rules tested (BR-STU-001 to BR-STU-007)
- Edge cases covered
- Coverage targets met

**Dependencies:** BE-010

---

### [BE-023] Drools Rules - Unit Tests

**Goal:** Test Drools validation rules in isolation.

**Technical Details:**
- **Package:** `src/test/java/com/school/student/rules`

**Test Cases (from 03-business-rules.md section 6.1):**
```java
@SpringBootTest(classes = DroolsConfig.class)
class StudentValidationRulesTest {

    @Test
    void shouldRejectStudentBelowMinAge() {
        // BR-STU-001: Age < 3
    }

    @Test
    void shouldRejectStudentAboveMaxAge() {
        // BR-STU-001: Age > 18
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        // BR-STU-003: Invalid email
    }

    @Test
    void shouldRejectInvalidAadhaarFormat() {
        // BR-STU-004: Not 12 digits
    }

    @Test
    void shouldPassValidationForValidData() {
        // All fields valid
    }
}
```

**Success Criteria:**
- All 7 business rules tested
- Validation errors correctly collected

**Dependencies:** BE-006

---

### [BE-024] Integration Tests - Student API

**Goal:** Test Student API endpoints end-to-end.

**Technical Details:**
- **Package:** `src/test/java/com/school/student/controller`
- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- Use TestRestTemplate or MockMvc

**Test Cases:**
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class StudentControllerIntegrationTest {

    @Test
    void shouldCreateStudentAndReturn201() {
        // POST /api/v1/students
        // Assert: 201 Created, Location header, studentId generated
    }

    @Test
    void shouldGetStudentAndReturn200() {
        // GET /api/v1/students/{studentId}
        // Assert: 200 OK, student data correct
    }

    @Test
    void shouldUpdateStudentAndReturn200() {
        // PUT /api/v1/students/{studentId}
        // Assert: 200 OK, only allowed fields updated
    }

    @Test
    void shouldDeleteStudentAndReturn204() {
        // DELETE /api/v1/students/{studentId}
        // Assert: 204 No Content
    }

    @Test
    void shouldReturnValidationErrorsFor400() {
        // POST with invalid data
        // Assert: 400 Bad Request, RFC 7807 format
    }

    @Test
    void shouldReturn404ForNonExistentStudent() {
        // GET /api/v1/students/INVALID
        // Assert: 404 Not Found
    }
}
```

**Success Criteria:**
- All CRUD endpoints tested
- API contract validated
- Error responses correct

**Dependencies:** BE-011

---

### [BE-025] Configuration Service - Tests

**Goal:** Write tests for Configuration Service.

**Technical Details:**
- Unit tests for ConfigurationService
- Integration tests for ConfigurationController

**Test Cases:**
- CRUD operations
- Category-based filtering
- Upsert logic (create vs update)
- Grouped configuration retrieval

**Success Criteria:**
- Coverage targets met
- All endpoints tested

**Dependencies:** BE-020

---

## Docker & DevOps

### [BE-026] Docker Compose Setup

**Goal:** Create docker-compose.yml for local development.

**Technical Details:**
- **Location:** Project root `docker-compose.yml`

**Services:**
```yaml
version: '3.8'

services:
  student-db:
    image: postgres:18
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - student-db-data:/var/lib/postgresql/data

  config-db:
    image: postgres:18
    ports:
      - "5434:5432"
    environment:
      POSTGRES_DB: config_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - config-db-data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

volumes:
  student-db-data:
  config-db-data:
  redis-data:
```

**Success Criteria:**
- All services start successfully
- Databases accessible on ports 5433, 5434
- Redis accessible on port 6379

**Dependencies:** None

---

### [BE-027] API Documentation Setup

**Goal:** Configure SpringDoc OpenAPI UI.

**Technical Details:**
- **SpringDoc Version:** 2.6.0 (D-001)
- **UI Path:** `/api/v1/swagger-ui.html`

**application.yml:**
```yaml
springdoc:
  api-docs:
    path: /api/v1/api-docs
  swagger-ui:
    path: /api/v1/swagger-ui.html
    operationsSorter: method
  show-actuator: true
```

**Success Criteria:**
- Swagger UI accessible
- All endpoints documented
- Request/Response schemas visible

**Dependencies:** BE-002, BE-015

---

### [BE-028] Actuator Metrics Configuration

**Goal:** Setup Spring Actuator with custom metrics.

**Technical Details:**
- **Package:** `com.school.student.metrics`
- **Class:** `StudentMetrics.java`

**Implementation (from 05-backend-implementation-guide.md section 7.2):**
```java
@Component
public class StudentMetrics {

    private final Counter studentsRegisteredCounter;
    private final Counter studentsDeletedCounter;

    public StudentMetrics(MeterRegistry registry) {
        this.studentsRegisteredCounter = Counter.builder("students.registered.total")
            .description("Total students registered")
            .register(registry);

        this.studentsDeletedCounter = Counter.builder("students.deleted.total")
            .description("Total students deleted")
            .register(registry);
    }

    public void incrementRegistered() {
        studentsRegisteredCounter.increment();
    }

    public void incrementDeleted() {
        studentsDeletedCounter.increment();
    }
}
```

**Integrate in StudentService:**
```java
public StudentResponse registerStudent(StudentRequest request) {
    Student saved = repository.save(student);
    metrics.incrementRegistered(); // Track metric
    return mapper.toResponse(saved);
}
```

**Success Criteria:**
- Metrics exposed at `/actuator/prometheus`
- Custom counters incremented correctly

**Dependencies:** BE-010

---

## Final Verification

### [BE-029] End-to-End Manual Testing

**Goal:** Manually test all backend APIs.

**Test Scenarios:**
1. Student Registration Flow
   - Create student with valid data
   - Verify student ID generated (STD-YYYYMMDD-NNNN format)
   - Verify status defaults to ACTIVE
   - Verify timestamps populated

2. Validation Rules
   - Reject age < 3
   - Reject age > 18
   - Reject duplicate mobile
   - Reject invalid email format
   - Reject invalid Aadhaar (not 12 digits)

3. Student Update Flow
   - Update allowed fields (firstName, lastName, mobile, status)
   - Verify immutable fields rejected (dateOfBirth, email, etc.)
   - Test optimistic locking (concurrent updates)

4. Student Search
   - Search by lastName
   - Filter by status
   - Combined lastName + status filter
   - Pagination working

5. Configuration CRUD
   - Create configuration (all categories)
   - Retrieve by category
   - Update (upsert logic)
   - Delete configuration

6. Error Handling
   - 404 for non-existent student
   - 400 for validation errors with field details
   - 409 for optimistic lock conflict

**Success Criteria:**
- All scenarios pass
- No runtime errors
- Logs clean (no stack traces)

**Dependencies:** All previous tasks

---

### [BE-030] Database Isolation Verification

**Goal:** Verify database-per-service isolation (D-010).

**Verification Steps:**
1. Inspect Spring Data JPA datasource configurations
2. Verify Student Service connects ONLY to student_db (port 5433)
3. Verify Configuration Service connects ONLY to config_db (port 5434)
4. Search codebase for cross-service references (MUST be zero)

**Command:**
```bash
# Check for any cross-database queries
grep -r "config_db" student-service/src/  # Should return 0 results
grep -r "student_db" configuration-service/src/  # Should return 0 results
```

**Success Criteria:**
- No cross-database access detected
- Each service isolated to its own database
- No SQL joins across services

**Dependencies:** BE-002, BE-015

---

## Task Summary

**Total Tasks:** 30

**Breakdown:**
- Database: 1 task
- Student Service: 14 tasks
- Configuration Service: 6 tasks
- Testing: 4 tasks
- Docker & DevOps: 4 tasks
- Verification: 2 tasks

**Critical Path:**
BE-001 → BE-002 → BE-003 → BE-005 → BE-006 → BE-007 → BE-010 → BE-011 → BE-029

**Estimated Effort:** 5-7 days for senior backend developer

---

## References

- Architecture: `specs/architecture/01-system-architecture.md`
- Database Design: `specs/architecture/02-database-design.md`
- Business Rules: `specs/architecture/03-business-rules.md`
- Implementation Guide: `specs/architecture/05-backend-implementation-guide.md`
- API Specification: `specs/sms_api_specification.yaml`
- Global Directives: D-001 to D-010 (embedded in tasks)

---

**Document Status:** READY FOR BACKEND DEVELOPER AGENT
**Next Phase:** Backend QA (backend-qa-orchestrator)
