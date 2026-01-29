# Backend Implementation Guidelines
**School Management System (SMS) - Developer Agent Reference**
Version: 1.0.0
Last Updated: 2026-01-28

---

## 1. Overview

This document provides mandatory implementation patterns for the Backend Developer Agent. All code must adhere to these guidelines to ensure consistency, maintainability, and production readiness.

### 1.1 Guiding Principles

- **Contract-First Development**: OpenAPI specification (`sms_api_specification.yaml`) is the single source of truth
- **Domain-Driven Design**: Rich domain models, not anemic entities
- **Layered Architecture**: Strict separation of concerns (Presentation → Application → Domain → Infrastructure)
- **Testability**: Every component must be unit-testable in isolation
- **Performance**: N+1 query prevention, batch processing, caching

---

## 2. Project Structure

### 2.1 Directory Layout

```
student-service/
├── src/
│   ├── main/
│   │   ├── java/com/school/student/
│   │   │   ├── controller/          # Presentation Layer
│   │   │   │   ├── StudentController.java
│   │   │   │   ├── EnrollmentController.java
│   │   │   │   └── dto/
│   │   │   │       ├── request/
│   │   │   │       │   ├── StudentRequest.java
│   │   │   │       │   └── EnrollmentRequest.java
│   │   │   │       └── response/
│   │   │   │           ├── StudentResponse.java
│   │   │   │           └── EnrollmentResponse.java
│   │   │   ├── service/             # Application Layer
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── EnrollmentService.java
│   │   │   │   └── mapper/
│   │   │   │       ├── StudentMapper.java
│   │   │   │       └── EnrollmentMapper.java
│   │   │   ├── domain/              # Domain Layer
│   │   │   │   ├── model/
│   │   │   │   │   ├── Student.java
│   │   │   │   │   ├── Enrollment.java
│   │   │   │   │   └── valueobject/
│   │   │   │   │       ├── StudentId.java
│   │   │   │   │       ├── Mobile.java
│   │   │   │   │       └── AadhaarNumber.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── StudentRepository.java
│   │   │   │   │   └── EnrollmentRepository.java
│   │   │   │   └── exception/
│   │   │   │       ├── StudentNotFoundException.java
│   │   │   │       └── BusinessRuleViolationException.java
│   │   │   ├── infrastructure/      # Infrastructure Layer
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── StudentEntity.java
│   │   │   │   │   ├── EnrollmentEntity.java
│   │   │   │   │   ├── StudentRepositoryImpl.java
│   │   │   │   │   └── EnrollmentRepositoryImpl.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   ├── CacheConfig.java
│   │   │   │   │   └── DroolsConfig.java
│   │   │   │   └── cache/
│   │   │   │       └── CacheKeyGenerator.java
│   │   │   ├── rules/               # Business Rules
│   │   │   │   ├── RuleExecutor.java
│   │   │   │   └── ValidationResult.java
│   │   │   └── common/              # Cross-Cutting
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       └── logging/
│   │   │           └── LoggingAspect.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/        # Flyway scripts
│   │       │   ├── V1__create_students_table.sql
│   │       │   └── V2__create_enrollments_table.sql
│   │       └── rules/               # Drools files
│   │           └── student/
│   │               └── student-age-validation.drl
│   └── test/
│       └── java/com/school/student/
│           ├── controller/
│           ├── service/
│           ├── domain/
│           └── integration/
└── pom.xml
```

---

## 3. Layer Implementation Patterns

### 3.1 Presentation Layer (Controllers)

**Purpose**: HTTP endpoint exposure, request/response handling, DTO validation.

**Mandatory Patterns**:
- Use `@RestController` with `@RequestMapping` base path
- All request bodies must use `@Valid` for Bean Validation
- All responses must return `ResponseEntity<T>`
- HTTP status codes must match OpenAPI specification
- Error handling delegated to `@ControllerAdvice`

**Example**:
```java
package com.school.student.controller;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Validated
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
        @Valid @RequestBody StudentRequest request
    ) {
        log.info("Creating student with mobile: {}", request.getMobile());
        StudentResponse response = studentService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(
        @PathVariable String studentId
    ) {
        log.info("Fetching student: {}", studentId);
        StudentResponse response = studentService.getStudentById(studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> searchStudents(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String status,
        Pageable pageable
    ) {
        log.info("Searching students with lastName={}, status={}", lastName, status);
        Page<StudentResponse> students = studentService.searchStudents(lastName, status, pageable);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable String studentId,
        @Valid @RequestBody StudentRequest request
    ) {
        log.info("Updating student: {}", studentId);
        StudentResponse response = studentService.updateStudent(studentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        log.info("Deleting student: {}", studentId);
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
```

**DTO Validation**:
```java
package com.school.student.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile must be exactly 10 digits")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String fathersName;

    @Size(max = 100)
    private String mothersName;

    @Size(max = 200)
    private String identificationMark;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar must be exactly 12 digits")
    private String aadhaarNumber;
}
```

---

### 3.2 Application Layer (Services)

**Purpose**: Orchestration, transaction boundaries, DTO-Domain mapping.

**Mandatory Patterns**:
- Use `@Service` annotation
- All public methods must be `@Transactional` (except read-only queries)
- Use MapStruct for DTO-Domain conversions
- Delegate domain logic to domain models
- Never expose domain models directly to controllers

**Example**:
```java
package com.school.student.service;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.domain.model.Student;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.domain.exception.StudentNotFoundException;
import com.school.student.domain.exception.DuplicateMobileException;
import com.school.student.service.mapper.StudentMapper;
import com.school.student.rules.RuleExecutor;
import com.school.student.rules.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RuleExecutor ruleExecutor;

    @Transactional
    public StudentResponse registerStudent(StudentRequest request) {
        log.debug("Registering student: {} {}", request.getFirstName(), request.getLastName());

        // Business rule: Check mobile uniqueness
        if (studentRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateMobileException(request.getMobile());
        }

        // Map DTO to domain model
        Student student = studentMapper.toDomain(request);

        // Execute Drools validation
        ValidationResult validationResult = ruleExecutor.validate(student);
        if (!validationResult.isValid()) {
            throw new BusinessRuleViolationException(validationResult.getErrors());
        }

        // Generate student ID
        student.setStudentId(generateStudentId());
        student.setStatus(StudentStatus.ACTIVE);

        // Persist
        Student savedStudent = studentRepository.save(student);
        log.info("Student registered successfully: {}", savedStudent.getStudentId());

        return studentMapper.toResponse(savedStudent);
    }

    @Cacheable(value = "students", key = "#studentId")
    public StudentResponse getStudentById(String studentId) {
        log.debug("Fetching student: {}", studentId);
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toResponse(student);
    }

    public Page<StudentResponse> searchStudents(String lastName, String status, Pageable pageable) {
        log.debug("Searching students with lastName={}, status={}", lastName, status);
        Page<Student> students = studentRepository.searchStudents(lastName, status, pageable);
        return students.map(studentMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "students", key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentRequest request) {
        log.debug("Updating student: {}", studentId);

        Student existingStudent = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Validate edit restrictions (only name, mobile, status allowed)
        existingStudent.updateProfile(
            request.getFirstName(),
            request.getLastName(),
            request.getMobile(),
            StudentStatus.valueOf(request.getStatus())
        );

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Student updated successfully: {}", studentId);

        return studentMapper.toResponse(updatedStudent);
    }

    @Transactional
    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        log.debug("Deleting student: {}", studentId);
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.delete(student);
        log.info("Student deleted successfully: {}", studentId);
    }

    private String generateStudentId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = studentRepository.countByCreatedAtDate(LocalDate.now()) + 1;
        return String.format("STD-%s-%04d", datePart, sequence);
    }
}
```

**MapStruct Mapper**:
```java
package com.school.student.service.mapper;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.domain.model.Student;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.Period;

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

---

### 3.3 Domain Layer (Business Logic)

**Purpose**: Encapsulate business rules, enforce invariants, define repository contracts.

**Mandatory Patterns**:
- Use Rich Domain Models (behavior + data)
- All domain entities must have private setters
- Use factory methods or builders for object creation
- Repository interfaces defined here, implementations in infrastructure
- Domain exceptions for business rule violations

**Example - Domain Model**:
```java
package com.school.student.domain.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Student {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private String aadhaarNumber;
    private StudentStatus status;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Domain behavior - Business rules enforced here
    public void updateProfile(String firstName, String lastName, String mobile, StudentStatus status) {
        validateEditableFields(firstName, lastName, mobile);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == StudentStatus.ACTIVE) {
            throw new IllegalStateException("Student is already active");
        }
        this.status = StudentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Student is already inactive");
        }
        this.status = StudentStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateEditableFields(String firstName, String lastName, String mobile) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (mobile == null || !mobile.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile must be exactly 10 digits");
        }
    }

    // Setter methods only for MapStruct and JPA
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

**Value Object Example**:
```java
package com.school.student.domain.model.valueobject;

import lombok.Value;

@Value
public class Mobile {
    String number;

    public Mobile(String number) {
        if (number == null || !number.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile must be exactly 10 digits");
        }
        this.number = number;
    }

    public static Mobile of(String number) {
        return new Mobile(number);
    }
}
```

**Repository Interface**:
```java
package com.school.student.domain.repository;

import com.school.student.domain.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface StudentRepository {

    Student save(Student student);

    Optional<Student> findById(Long id);

    Optional<Student> findByStudentId(String studentId);

    Page<Student> searchStudents(String lastName, String status, Pageable pageable);

    boolean existsByMobile(String mobile);

    long countByCreatedAtDate(LocalDate date);

    void delete(Student student);
}
```

---

### 3.4 Infrastructure Layer (Persistence)

**Purpose**: Database interactions, JPA implementations, external integrations.

**Mandatory Patterns**:
- Use JPA entities for database mapping (separate from domain models)
- Implement repository interfaces using Spring Data JPA
- Use `@EntityGraph` to prevent N+1 queries
- Configure optimistic locking with `@Version`
- Map JPA entities to domain models in repository implementations

**JPA Entity**:
```java
package com.school.student.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_last_name", columnList = "last_name"),
    @Index(name = "idx_students_status", columnList = "status"),
    @Index(name = "idx_students_mobile", columnList = "mobile")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", unique = true, nullable = false, length = 10)
    private String mobile;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "fathers_name", length = 100)
    private String fathersName;

    @Column(name = "mothers_name", length = 100)
    private String mothersName;

    @Column(name = "identification_mark", length = 200)
    private String identificationMark;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // One-to-many relationship with enrollments
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnrollmentEntity> enrollments = new ArrayList<>();
}
```

**Repository Implementation**:
```java
package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.Student;
import com.school.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
interface StudentJpaRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByStudentId(String studentId);

    boolean existsByMobile(String mobile);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE DATE(s.createdAt) = :date")
    long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM StudentEntity s " +
           "WHERE (:lastName IS NULL OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<StudentEntity> searchStudents(
        @Param("lastName") String lastName,
        @Param("status") String status,
        Pageable pageable
    );
}

@Repository
@RequiredArgsConstructor
class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    @Override
    public Student save(Student student) {
        StudentEntity entity = entityMapper.toEntity(student);
        StudentEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId)
            .map(entityMapper::toDomain);
    }

    @Override
    public Page<Student> searchStudents(String lastName, String status, Pageable pageable) {
        return jpaRepository.searchStudents(lastName, status, pageable)
            .map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return jpaRepository.existsByMobile(mobile);
    }

    @Override
    public long countByCreatedAtDate(LocalDate date) {
        return jpaRepository.countByCreatedAtDate(date);
    }

    @Override
    public void delete(Student student) {
        jpaRepository.deleteById(student.getId());
    }
}
```

---

## 4. Performance Optimization

### 4.1 N+1 Query Prevention

**Use EntityGraph**:
```java
@EntityGraph(attributePaths = {"enrollments"})
@Query("SELECT s FROM StudentEntity s WHERE s.studentId = :studentId")
Optional<StudentEntity> findByIdWithEnrollments(@Param("studentId") String studentId);
```

**Use JOIN FETCH**:
```java
@Query("SELECT DISTINCT s FROM StudentEntity s LEFT JOIN FETCH s.enrollments WHERE s.status = :status")
List<StudentEntity> findAllActiveWithEnrollments(@Param("status") StudentStatus status);
```

### 4.2 Batch Processing

**application.yml**:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
```

**Batch Insert Example**:
```java
@Transactional
public void bulkRegisterStudents(List<StudentRequest> requests) {
    List<Student> students = requests.stream()
        .map(studentMapper::toDomain)
        .collect(Collectors.toList());

    studentRepository.saveAll(students);
    entityManager.flush();
    entityManager.clear(); // Clear persistence context to avoid memory issues
}
```

### 4.3 Caching Strategy

**Redis Configuration**:
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("students",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("configurations",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfiguration())
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
```

**Cache Usage**:
```java
@Cacheable(value = "students", key = "#studentId", unless = "#result == null")
public StudentResponse getStudentById(String studentId) {
    // Method implementation
}

@CacheEvict(value = "students", key = "#studentId")
public StudentResponse updateStudent(String studentId, StudentRequest request) {
    // Method implementation
}

@CacheEvict(value = "students", allEntries = true)
public void clearAllStudentCache() {
    // Evict all student cache entries
}
```

---

## 5. Exception Handling

### 5.1 Exception Hierarchy

```
RuntimeException
├── BusinessException (abstract)
│   ├── StudentNotFoundException
│   ├── DuplicateMobileException
│   ├── BusinessRuleViolationException
│   └── EnrollmentConflictException
└── TechnicalException (abstract)
    ├── DatabaseConnectionException
    └── ExternalServiceException
```

**Custom Exceptions**:
```java
package com.school.student.domain.exception;

public class StudentNotFoundException extends BusinessException {
    public StudentNotFoundException(String studentId) {
        super(String.format("Student not found: %s", studentId));
    }
}

public class DuplicateMobileException extends BusinessException {
    public DuplicateMobileException(String mobile) {
        super(String.format("Mobile number %s is already registered", mobile));
    }
}

public class BusinessRuleViolationException extends BusinessException {
    private final List<String> errors;

    public BusinessRuleViolationException(List<String> errors) {
        super("Business rule validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
```

### 5.2 Global Exception Handler

```java
package com.school.student.common.exception;

import com.school.student.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleStudentNotFound(StudentNotFoundException ex) {
        log.warn("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setTitle("Student Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(DuplicateMobileException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateMobile(DuplicateMobileException ex) {
        log.warn("Duplicate mobile number: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Mobile Number");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        log.warn("Business rule violation: {}", ex.getErrors());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Business rule validation failed"
        );
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("errors", ex.getErrors());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ValidationError(
                error.getField(),
                error.getDefaultMessage(),
                error.getCode()
            ))
            .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Request validation failed"
        );
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private record ValidationError(String field, String message, String code) {}
}
```

---

## 6. Logging & Monitoring

### 6.1 Structured Logging

**Logback Configuration** (`logback-spring.xml`):
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
    <logger name="org.hibernate.SQL" level="DEBUG"/>
</configuration>
```

**Logging Best Practices**:
```java
// ✅ GOOD: Structured logging with key-value pairs
log.info("Student registered",
    kv("studentId", studentId),
    kv("mobile", mobile),
    kv("duration", duration)
);

// ✅ GOOD: Use appropriate log levels
log.debug("Fetching student: {}", studentId);
log.info("Student created successfully: {}", studentId);
log.warn("Failed login attempt for user: {}", username);
log.error("Database connection failed", exception);

// ❌ BAD: Concatenation, no context
log.info("Student " + studentId + " was registered");
```

### 6.2 Actuator Endpoints

**application.yml**:
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

**Custom Metrics**:
```java
@Component
@RequiredArgsConstructor
public class StudentMetrics {

    private final MeterRegistry meterRegistry;

    public void recordStudentRegistration() {
        meterRegistry.counter("students.registered.total").increment();
    }

    public void recordStudentDeletion() {
        meterRegistry.counter("students.deleted.total").increment();
    }

    public void recordValidationFailure(String ruleId) {
        meterRegistry.counter("students.validation.failures",
            "rule", ruleId
        ).increment();
    }
}
```

---

## 7. Testing Guidelines

### 7.1 Unit Testing

**Controller Test**:
```java
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("Should create student and return 201 Created")
    void shouldCreateStudent() throws Exception {
        StudentRequest request = StudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 5, 15))
            .mobile("9876543210")
            .build();

        StudentResponse response = StudentResponse.builder()
            .studentId("STD-20260128-0001")
            .firstName("John")
            .lastName("Doe")
            .build();

        when(studentService.registerStudent(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").value("STD-20260128-0001"));
    }
}
```

**Service Test**:
```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private RuleExecutor ruleExecutor;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should register student successfully")
    void shouldRegisterStudent() {
        StudentRequest request = StudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 5, 15))
            .mobile("9876543210")
            .build();

        Student student = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .build();

        ValidationResult validationResult = new ValidationResult();

        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(studentMapper.toDomain(any())).thenReturn(student);
        when(ruleExecutor.validate(any())).thenReturn(validationResult);
        when(studentRepository.save(any())).thenReturn(student);

        StudentResponse response = studentService.registerStudent(request);

        assertThat(response).isNotNull();
        verify(studentRepository).save(any(Student.class));
    }
}
```

### 7.2 Integration Testing

**TestContainers Setup**:
```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class StudentServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("student_db_test")
        .withUsername("test")
        .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("Should persist student to database")
    void shouldPersistStudent() {
        StudentRequest request = StudentRequest.builder()
            .firstName("Integration")
            .lastName("Test")
            .dateOfBirth(LocalDate.of(2012, 1, 1))
            .mobile("9999999999")
            .build();

        StudentResponse response = studentService.registerStudent(request);

        assertThat(response.getStudentId()).startsWith("STD-");
        assertThat(response.getFirstName()).isEqualTo("Integration");
    }
}
```

---

## 8. Configuration Management

### 8.1 Application Properties

**application.yml** (base):
```yaml
spring:
  application:
    name: student-service

  datasource:
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

  flyway:
    enabled: true
    baseline-on-migrate: true

server:
  port: 8081
  compression:
    enabled: true
```

**application-dev.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_db
    username: postgres
    password: postgres

  jpa:
    show-sql: true

logging:
  level:
    com.school.student: DEBUG
```

**application-prod.yml**:
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

logging:
  level:
    com.school.student: INFO
```

---

## 9. Docker Configuration

**Dockerfile**:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/student-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  student-service:
    build: ./student-service
    ports:
      - "8081:8081"
    environment:
      DB_URL: jdbc:postgresql://student-db:5432/student_db
      DB_USERNAME: postgres
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: redis
    depends_on:
      - student-db
      - redis

  student-db:
    image: postgres:18-alpine
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - student-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  student-data:
```

---

## 10. Code Quality Standards

### 10.1 Checkstyle Rules

**checkstyle.xml**:
```xml
<module name="Checker">
    <module name="TreeWalker">
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>
        <module name="JavadocMethod"/>
        <module name="JavadocType"/>
    </module>
</module>
```

### 10.2 Code Coverage Requirements

- **Domain Layer**: 95% line coverage
- **Application Layer**: 85% line coverage
- **Infrastructure Layer**: 70% branch coverage
- **Overall Project**: 80% line coverage

---

## Appendix A: Dependency Management

**pom.xml** (key dependencies):
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.5.0</spring-boot.version>
    <drools.version>9.44.0.Final</drools.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- Drools -->
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-core</artifactId>
        <version>${drools.version}</version>
    </dependency>

    <!-- Utilities -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

**Document Status**: Final
**Next Review**: 2026-04-28
