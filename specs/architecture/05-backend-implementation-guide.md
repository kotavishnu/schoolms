# Backend Implementation Guide

## 1. Overview

This document provides comprehensive implementation guidelines for Backend Developer Agents building the School Management System (SMS). It enforces Domain-Driven Design (DDD) with layered architecture, SOLID principles, and production-ready patterns.

## 2. Project Structure

### 2.1 Multi-Module Maven Structure

```
sms-backend/
├── pom.xml (parent POM)
├── student-service/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/school/sms/student/
│       │   │   ├── presentation/         # REST Controllers
│       │   │   │   ├── controller/
│       │   │   │   ├── dto/
│       │   │   │   │   ├── request/
│       │   │   │   │   └── response/
│       │   │   │   └── mapper/           # MapStruct DTOs
│       │   │   ├── application/          # Use Cases / Services
│       │   │   │   ├── service/
│       │   │   │   └── command/          # CQRS Commands
│       │   │   ├── domain/               # Business Logic
│       │   │   │   ├── model/            # Entities
│       │   │   │   ├── repository/       # Interfaces
│       │   │   │   ├── validator/
│       │   │   │   ├── exception/
│       │   │   │   └── event/
│       │   │   └── infrastructure/       # Technical Implementation
│       │   │       ├── persistence/      # JPA Implementations
│       │   │       │   ├── entity/       # JPA Entities
│       │   │       │   ├── repository/
│       │   │       │   └── mapper/       # Entity-Domain Mapping
│       │   │       ├── config/
│       │   │       ├── rules/            # Drools Rules
│       │   │       └── cache/
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── db/migration/         # Flyway scripts
│       │       └── rules/                # Drools DRL files
│       └── test/
│           ├── java/
│           │   ├── unit/
│           │   ├── integration/
│           │   └── e2e/
│           └── resources/
└── configuration-service/
    └── (similar structure)
```

### 2.2 Package Naming Convention

```yaml
Base Package: com.school.sms.{service-name}

Presentation Layer: .presentation
  - .presentation.controller
  - .presentation.dto.request
  - .presentation.dto.response
  - .presentation.mapper

Application Layer: .application
  - .application.service
  - .application.command
  - .application.query

Domain Layer: .domain
  - .domain.model
  - .domain.repository
  - .domain.validator
  - .domain.exception
  - .domain.event

Infrastructure Layer: .infrastructure
  - .infrastructure.persistence.entity
  - .infrastructure.persistence.repository
  - .infrastructure.persistence.mapper
  - .infrastructure.config
  - .infrastructure.rules
  - .infrastructure.cache
```

## 3. Domain Layer (Core Business Logic)

### 3.1 Domain Entities (Rich Domain Models)

**Principle:** Domain entities encapsulate business logic and maintain invariants.

```java
package com.school.sms.student.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Student Domain Entity - Rich domain model with business logic
 * This is NOT a JPA entity - it's a pure domain object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {

    private final Long id;
    private final StudentId studentId;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private FamilyInfo familyInfo;
    private StudentStatus status;
    private final AuditInfo auditInfo;
    private Long version;

    // Factory method for new student creation
    public static Student createNew(
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            FamilyInfo familyInfo) {

        // Business rule validation
        validateAge(personalInfo.getDateOfBirth());

        return new Student(
            null,
            StudentId.generate(),
            personalInfo,
            contactInfo,
            familyInfo,
            StudentStatus.ACTIVE,
            AuditInfo.createNew(),
            0L
        );
    }

    // Factory method for existing student
    public static Student fromRepository(
            Long id,
            StudentId studentId,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            FamilyInfo familyInfo,
            StudentStatus status,
            AuditInfo auditInfo,
            Long version) {

        return new Student(id, studentId, personalInfo, contactInfo, familyInfo, status, auditInfo, version);
    }

    // Business methods
    public void updatePersonalInfo(String firstName, String lastName) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");

        this.personalInfo = this.personalInfo.withName(firstName, lastName);
        this.auditInfo.markUpdated();
    }

    public void updateContactInfo(String mobile) {
        Objects.requireNonNull(mobile, "Mobile cannot be null");

        this.contactInfo = this.contactInfo.withMobile(mobile);
        this.auditInfo.markUpdated();
    }

    public void activate() {
        if (this.status == StudentStatus.ACTIVE) {
            throw new IllegalStateException("Student is already active");
        }
        this.status = StudentStatus.ACTIVE;
        this.auditInfo.markUpdated();
    }

    public void deactivate() {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Student is already inactive");
        }
        this.status = StudentStatus.INACTIVE;
        this.auditInfo.markUpdated();
    }

    public int calculateAge() {
        return Period.between(personalInfo.getDateOfBirth(), LocalDate.now()).getYears();
    }

    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }

    // Business rule: Age must be between 3 and 18
    private static void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new InvalidAgeException("Student age must be between 3 and 18 years");
        }
    }
}
```

### 3.2 Value Objects

**Principle:** Value objects are immutable and defined by their attributes.

```java
package com.school.sms.student.domain.model;

import lombok.Value;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * StudentId Value Object - Auto-generated unique identifier
 * Format: STD-YYYYMMDD-NNNN
 */
@Value
public class StudentId {
    String value;

    private StudentId(String value) {
        Objects.requireNonNull(value, "Student ID cannot be null");
        if (!value.matches("^STD-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Invalid student ID format");
        }
        this.value = value;
    }

    public static StudentId of(String value) {
        return new StudentId(value);
    }

    public static StudentId generate() {
        // This will be delegated to database sequence
        // Placeholder for now
        return null;
    }
}

/**
 * PersonalInfo Value Object
 */
@Value
public class PersonalInfo {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String identificationMark;
    String aadhaarNumber;

    public PersonalInfo withName(String firstName, String lastName) {
        return new PersonalInfo(firstName, lastName, this.dateOfBirth, this.identificationMark, this.aadhaarNumber);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

/**
 * ContactInfo Value Object
 */
@Value
public class ContactInfo {
    String mobile;
    String email;
    String address;

    public ContactInfo withMobile(String mobile) {
        return new ContactInfo(mobile, this.email, this.address);
    }
}

/**
 * FamilyInfo Value Object
 */
@Value
public class FamilyInfo {
    String fathersName;
    String mothersName;
}

/**
 * AuditInfo Value Object
 */
@Value
public class AuditInfo {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;

    public static AuditInfo createNew() {
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(now, now, "system", "system");
    }

    public AuditInfo markUpdated() {
        return new AuditInfo(this.createdAt, LocalDateTime.now(), this.createdBy, "system");
    }
}
```

### 3.3 Repository Interfaces (Domain Layer)

**Principle:** Define contracts in domain, implement in infrastructure.

```java
package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import java.util.List;
import java.util.Optional;

/**
 * Student Repository Interface - Domain contract
 * Implementation will be in infrastructure layer
 */
public interface StudentRepository {

    /**
     * Save a new student or update existing
     */
    Student save(Student student);

    /**
     * Find student by ID
     */
    Optional<Student> findById(Long id);

    /**
     * Find student by student ID (business key)
     */
    Optional<Student> findByStudentId(StudentId studentId);

    /**
     * Find student by mobile number
     */
    Optional<Student> findByMobile(String mobile);

    /**
     * Check if mobile already exists
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if Aadhaar already exists
     */
    boolean existsByAadhaar(String aadhaar);

    /**
     * Search students by criteria
     */
    List<Student> search(StudentSearchCriteria criteria);

    /**
     * Delete student
     */
    void delete(Student student);

    /**
     * Find all students with pagination
     */
    Page<Student> findAll(Pageable pageable);
}
```

### 3.4 Domain Exceptions

```java
package com.school.sms.student.domain.exception;

/**
 * Base domain exception
 */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Business rule violation
 */
public class BusinessRuleException extends DomainException {
    private final String errorCode;

    public BusinessRuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

/**
 * Invalid age exception
 */
public class InvalidAgeException extends BusinessRuleException {
    public InvalidAgeException(String message) {
        super(message, "INVALID_AGE");
    }
}

/**
 * Duplicate resource exception
 */
public class DuplicateResourceException extends DomainException {
    private final String field;
    private final String value;

    public DuplicateResourceException(String field, String value) {
        super(String.format("%s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }
}

/**
 * Resource not found exception
 */
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

/**
 * Optimistic lock exception
 */
public class OptimisticLockException extends DomainException {
    public OptimisticLockException(String message) {
        super(message);
    }
}
```

## 4. Application Layer (Use Cases)

### 4.1 Application Services

**Principle:** Orchestrate domain logic, coordinate transactions, no business logic.

```java
package com.school.sms.student.application.service;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.exception.*;
import com.school.sms.student.application.command.CreateStudentCommand;
import com.school.sms.student.application.command.UpdateStudentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Student Application Service
 * Orchestrates use cases, coordinates transactions
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentApplicationService {

    private final StudentRepository studentRepository;
    private final StudentValidator studentValidator;

    /**
     * Create new student
     */
    @Transactional
    public Student createStudent(CreateStudentCommand command) {
        log.info("Creating student: {}", command);

        // Validate business rules using Drools
        studentValidator.validateForCreation(command);

        // Check duplicate mobile
        if (studentRepository.existsByMobile(command.getMobile())) {
            throw new DuplicateResourceException("mobile", command.getMobile());
        }

        // Check duplicate Aadhaar if provided
        if (command.getAadhaarNumber() != null &&
            studentRepository.existsByAadhaar(command.getAadhaarNumber())) {
            throw new DuplicateResourceException("aadhaar", command.getAadhaarNumber());
        }

        // Create domain entity
        Student student = Student.createNew(
            new PersonalInfo(
                command.getFirstName(),
                command.getLastName(),
                command.getDateOfBirth(),
                command.getIdentificationMark(),
                command.getAadhaarNumber()
            ),
            new ContactInfo(
                command.getMobile(),
                command.getEmail(),
                command.getAddress()
            ),
            new FamilyInfo(
                command.getFathersName(),
                command.getMothersName()
            )
        );

        // Persist
        Student savedStudent = studentRepository.save(student);

        log.info("Student created successfully: {}", savedStudent.getStudentId());

        return savedStudent;
    }

    /**
     * Update student
     */
    @Transactional
    @CacheEvict(value = "students", key = "#command.studentId")
    public Student updateStudent(UpdateStudentCommand command) {
        log.info("Updating student: {}", command.getStudentId());

        // Find existing student
        Student student = studentRepository.findByStudentId(StudentId.of(command.getStudentId()))
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + command.getStudentId()));

        // Optimistic locking check
        if (!student.getVersion().equals(command.getVersion())) {
            throw new OptimisticLockException(
                "Student was modified by another user. Please refresh and try again."
            );
        }

        // Check duplicate mobile if changed
        if (!student.getContactInfo().getMobile().equals(command.getMobile())) {
            if (studentRepository.existsByMobile(command.getMobile())) {
                throw new DuplicateResourceException("mobile", command.getMobile());
            }
        }

        // Update allowed fields only
        student.updatePersonalInfo(command.getFirstName(), command.getLastName());
        student.updateContactInfo(command.getMobile());

        if ("INACTIVE".equals(command.getStatus())) {
            student.deactivate();
        } else {
            student.activate();
        }

        // Persist
        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully: {}", student.getStudentId());

        return updatedStudent;
    }

    /**
     * Get student by ID (with caching)
     */
    @Cacheable(value = "students", key = "#studentId")
    public Student getStudent(String studentId) {
        log.debug("Fetching student: {}", studentId);

        return studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
    }

    /**
     * Delete student
     */
    @Transactional
    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        studentRepository.delete(student);

        log.info("Student deleted successfully: {}", studentId);
    }

    /**
     * Search students
     */
    public List<Student> searchStudents(StudentSearchCriteria criteria) {
        log.debug("Searching students: {}", criteria);

        return studentRepository.search(criteria);
    }
}
```

### 4.2 Command Objects (CQRS)

```java
package com.school.sms.student.application.command;

import lombok.Value;
import java.time.LocalDate;

/**
 * Create Student Command - Immutable
 */
@Value
public class CreateStudentCommand {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String mobile;
    String email;
    String address;
    String fathersName;
    String mothersName;
    String identificationMark;
    String aadhaarNumber;
}

/**
 * Update Student Command
 */
@Value
public class UpdateStudentCommand {
    String studentId;
    String firstName;
    String lastName;
    String mobile;
    String status;
    Long version;  // For optimistic locking
}
```

## 5. Infrastructure Layer

### 5.1 JPA Entities

**Principle:** JPA entities are separate from domain entities.

```java
package com.school.sms.student.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student JPA Entity - Infrastructure concern
 * Separate from domain model
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(name = "email", length = 255)
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

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = StudentStatus.ACTIVE;
        }
        if (version == null) {
            version = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 5.2 JPA Repository Implementation

```java
package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.infrastructure.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Spring Data JPA Repository
 */
@Repository
public interface StudentJpaRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByStudentId(String studentId);

    Optional<StudentEntity> findByMobile(String mobile);

    boolean existsByMobile(String mobile);

    boolean existsByAadhaarNumber(String aadhaarNumber);

    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<StudentEntity> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.fathersName) LIKE LOWER(CONCAT('%', :fathersName, '%'))")
    List<StudentEntity> findByFathersNameContainingIgnoreCase(@Param("fathersName") String fathersName);
}
```

### 5.3 Repository Adapter (Domain to Infrastructure)

```java
package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.infrastructure.persistence.entity.StudentEntity;
import com.school.sms.student.infrastructure.persistence.mapper.StudentEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository Adapter - Bridges domain and infrastructure
 * Implements domain repository interface using JPA
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StudentRepositoryAdapter implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    @Override
    public Student save(Student student) {
        StudentEntity entity = entityMapper.toEntity(student);

        // Generate student ID if new
        if (entity.getStudentId() == null) {
            entity.setStudentId(generateStudentId());
        }

        StudentEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        return jpaRepository.findByStudentId(studentId.getValue())
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByMobile(String mobile) {
        return jpaRepository.findByMobile(mobile)
            .map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return jpaRepository.existsByMobile(mobile);
    }

    @Override
    public boolean existsByAadhaar(String aadhaar) {
        return jpaRepository.existsByAadhaarNumber(aadhaar);
    }

    @Override
    public void delete(Student student) {
        jpaRepository.deleteById(student.getId());
    }

    private String generateStudentId() {
        // Implementation using database sequence or custom logic
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = jpaRepository.count() + 1;
        return String.format("STD-%s-%04d", datePart, sequence);
    }
}
```

### 5.4 Entity Mapper (MapStruct)

```java
package com.school.sms.student.infrastructure.persistence.mapper;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.infrastructure.persistence.entity.StudentEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for Student entity conversion
 * Compile-time generation, no reflection
 */
@Mapper(componentModel = "spring")
public interface StudentEntityMapper {

    /**
     * Convert domain Student to JPA StudentEntity
     */
    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "dateOfBirth", source = "personalInfo.dateOfBirth")
    @Mapping(target = "identificationMark", source = "personalInfo.identificationMark")
    @Mapping(target = "aadhaarNumber", source = "personalInfo.aadhaarNumber")
    @Mapping(target = "mobile", source = "contactInfo.mobile")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "address", source = "contactInfo.address")
    @Mapping(target = "fathersName", source = "familyInfo.fathersName")
    @Mapping(target = "mothersName", source = "familyInfo.mothersName")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "updatedBy", source = "auditInfo.updatedBy")
    StudentEntity toEntity(Student student);

    /**
     * Convert JPA StudentEntity to domain Student
     */
    @Mapping(target = "studentId", expression = "java(StudentId.of(entity.getStudentId()))")
    @Mapping(target = "personalInfo", expression = "java(mapPersonalInfo(entity))")
    @Mapping(target = "contactInfo", expression = "java(mapContactInfo(entity))")
    @Mapping(target = "familyInfo", expression = "java(mapFamilyInfo(entity))")
    @Mapping(target = "auditInfo", expression = "java(mapAuditInfo(entity))")
    Student toDomain(StudentEntity entity);

    default PersonalInfo mapPersonalInfo(StudentEntity entity) {
        return new PersonalInfo(
            entity.getFirstName(),
            entity.getLastName(),
            entity.getDateOfBirth(),
            entity.getIdentificationMark(),
            entity.getAadhaarNumber()
        );
    }

    default ContactInfo mapContactInfo(StudentEntity entity) {
        return new ContactInfo(
            entity.getMobile(),
            entity.getEmail(),
            entity.getAddress()
        );
    }

    default FamilyInfo mapFamilyInfo(StudentEntity entity) {
        return new FamilyInfo(
            entity.getFathersName(),
            entity.getMothersName()
        );
    }

    default AuditInfo mapAuditInfo(StudentEntity entity) {
        return new AuditInfo(
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy()
        );
    }
}
```

## 6. Presentation Layer

### 6.1 REST Controllers

```java
package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.service.StudentApplicationService;
import com.school.sms.student.application.command.*;
import com.school.sms.student.presentation.dto.request.CreateStudentRequest;
import com.school.sms.student.presentation.dto.request.UpdateStudentRequest;
import com.school.sms.student.presentation.dto.response.StudentResponse;
import com.school.sms.student.presentation.mapper.StudentDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Student REST Controller
 * Handles HTTP concerns only, delegates to application service
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management operations")
public class StudentController {

    private final StudentApplicationService studentService;
    private final StudentDtoMapper dtoMapper;

    @Operation(summary = "Create new student")
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {

        log.info("POST /api/v1/students - Create student");

        CreateStudentCommand command = dtoMapper.toCommand(request);
        Student student = studentService.createStudent(command);
        StudentResponse response = dtoMapper.toResponse(student);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get student by ID")
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable String studentId) {

        log.info("GET /api/v1/students/{} - Get student", studentId);

        Student student = studentService.getStudent(studentId);
        StudentResponse response = dtoMapper.toResponse(student);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update student")
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequest request) {

        log.info("PUT /api/v1/students/{} - Update student", studentId);

        UpdateStudentCommand command = dtoMapper.toCommand(studentId, request);
        Student student = studentService.updateStudent(command);
        StudentResponse response = dtoMapper.toResponse(student);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete student")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {

        log.info("DELETE /api/v1/students/{} - Delete student", studentId);

        studentService.deleteStudent(studentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search students")
    @GetMapping
    public ResponseEntity<PagedResponse<StudentResponse>> searchStudents(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String fathersName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/v1/students - Search students");

        // Implementation details...

        return ResponseEntity.ok(pagedResponse);
    }
}
```

### 6.2 Request DTOs (with Validation)

```java
package com.school.sms.student.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Create Student Request DTO
 */
@Data
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be 2-100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be 2-100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile must be exactly 10 digits")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String fathersName;

    private String mothersName;

    private String identificationMark;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar must be exactly 12 digits")
    private String aadhaarNumber;
}
```

### 6.3 Response DTOs

```java
package com.school.sms.student.presentation.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student Response DTO
 */
@Data
public class StudentResponse {
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
    private String status;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 6.4 DTO Mapper (MapStruct)

```java
package com.school.sms.student.presentation.mapper;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.application.command.*;
import com.school.sms.student.presentation.dto.request.*;
import com.school.sms.student.presentation.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * DTO Mapper using MapStruct
 */
@Mapper(componentModel = "spring")
public interface StudentDtoMapper {

    CreateStudentCommand toCommand(CreateStudentRequest request);

    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "mobile", source = "request.mobile")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "version", source = "request.version")
    UpdateStudentCommand toCommand(String studentId, UpdateStudentRequest request);

    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "dateOfBirth", source = "personalInfo.dateOfBirth")
    @Mapping(target = "identificationMark", source = "personalInfo.identificationMark")
    @Mapping(target = "aadhaarNumber", source = "personalInfo.aadhaarNumber")
    @Mapping(target = "mobile", source = "contactInfo.mobile")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "address", source = "contactInfo.address")
    @Mapping(target = "fathersName", source = "familyInfo.fathersName")
    @Mapping(target = "mothersName", source = "familyInfo.mothersName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    StudentResponse toResponse(Student student);
}
```

## 7. Business Rules (Drools)

### 7.1 Drools Configuration

```java
package com.school.sms.student.infrastructure.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + "student-validation.drl"));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    @Bean
    public KieSession kieSession() {
        return kieContainer().newKieSession();
    }
}
```

### 7.2 Drools Rules (DRL)

```drl
// File: src/main/resources/rules/student-validation.drl

package com.school.sms.student.rules;

import com.school.sms.student.application.command.CreateStudentCommand;
import com.school.sms.student.domain.exception.BusinessRuleException;
import java.time.LocalDate;
import java.time.Period;

// Rule: BR-1 - Age must be between 3 and 18
rule "Validate Student Age"
    when
        $cmd : CreateStudentCommand()
        eval(Period.between($cmd.getDateOfBirth(), LocalDate.now()).getYears() < 3 ||
             Period.between($cmd.getDateOfBirth(), LocalDate.now()).getYears() > 18)
    then
        throw new BusinessRuleException(
            "Student age must be between 3 and 18 years",
            "INVALID_AGE"
        );
end

// Rule: BR-2 is enforced at database level (UNIQUE constraint)

// Rule: First name and last name are required
rule "Validate Required Fields"
    when
        $cmd : CreateStudentCommand(firstName == null || firstName.trim().isEmpty() ||
                                     lastName == null || lastName.trim().isEmpty())
    then
        throw new BusinessRuleException(
            "First name and last name are required",
            "REQUIRED_FIELD"
        );
end
```

### 7.3 Validator Service

```java
package com.school.sms.student.domain.validator;

import com.school.sms.student.application.command.CreateStudentCommand;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

/**
 * Student Validator using Drools
 */
@Component
@RequiredArgsConstructor
public class StudentValidator {

    private final KieSession kieSession;

    public void validateForCreation(CreateStudentCommand command) {
        kieSession.insert(command);
        kieSession.fireAllRules();
        kieSession.dispose();
    }
}
```

## 8. Exception Handling

### 8.1 Global Exception Handler

```java
package com.school.sms.student.presentation.controller;

import com.school.sms.student.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * Returns RFC 7807 Problem Details format
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<ErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::mapFieldError)
            .collect(Collectors.toList());

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/validation-error")
            .title("Validation Error")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("Request validation failed")
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .errors(errors)
            .build();

        log.warn("Validation error: {}", problem);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleException(
            BusinessRuleException ex,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/business-rule-violation")
            .title("Business Rule Violation")
            .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .detail(ex.getMessage())
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .errors(List.of(new ErrorDetail(null, ex.getMessage(), ex.getErrorCode())))
            .build();

        log.warn("Business rule violation: {}", problem);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/duplicate-resource")
            .title("Duplicate Resource")
            .status(HttpStatus.CONFLICT.value())
            .detail(ex.getMessage())
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .build();

        log.warn("Duplicate resource: {}", problem);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/not-found")
            .title("Resource Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .detail(ex.getMessage())
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .build();

        log.warn("Resource not found: {}", problem);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLockException(
            OptimisticLockException ex,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/conflict")
            .title("Optimistic Lock Exception")
            .status(HttpStatus.CONFLICT.value())
            .detail(ex.getMessage())
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .build();

        log.warn("Optimistic lock conflict: {}", problem);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.builder()
            .type("https://api.school.com/errors/internal-error")
            .title("Internal Server Error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .detail("An unexpected error occurred")
            .instance(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .correlationId(UUID.randomUUID().toString())
            .build();

        log.error("Internal server error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ErrorDetail mapFieldError(FieldError fieldError) {
        return new ErrorDetail(
            fieldError.getField(),
            fieldError.getDefaultMessage(),
            fieldError.getCode()
        );
    }
}
```

## 9. Configuration

### 9.1 Application Configuration

```yaml
# src/main/resources/application.yml

spring:
  application:
    name: student-service

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/sms_student_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: StudentService-HikariCP

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    show-sql: false

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

  cache:
    type: redis
    redis:
      time-to-live: 3600000 # 1 hour

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

  zipkin:
    base-url: ${ZIPKIN_URL:http://localhost:9411}
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}

logging:
  level:
    com.school.sms: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

server:
  port: 8081
  error:
    include-message: always
    include-binding-errors: always
```

### 9.2 CORS Configuration

```java
package com.school.sms.student.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * CORS Configuration
 * Reference: LESSONS_LEARNED.md [D-001]
 */
@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow both Vite and React dev servers (LESSONS_LEARNED)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React dev server
            "http://localhost:5173"   // Vite dev server
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("X-Correlation-ID"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```

## 10. Performance Optimization

### 10.1 N+1 Query Prevention

```java
// Use EntityGraph for eager fetching
@EntityGraph(attributePaths = {"enrollmentHistory"})
@Query("SELECT s FROM StudentEntity s WHERE s.status = :status")
List<StudentEntity> findActiveStudentsWithEnrollments(@Param("status") StudentStatus status);

// Or use JOIN FETCH
@Query("SELECT DISTINCT s FROM StudentEntity s LEFT JOIN FETCH s.enrollmentHistory WHERE s.id = :id")
Optional<StudentEntity> findByIdWithEnrollments(@Param("id") Long id);
```

### 10.2 Caching Strategy

```java
@Service
public class StudentApplicationService {

    @Cacheable(value = "students", key = "#studentId")
    public Student getStudent(String studentId) {
        // Cached for 1 hour
    }

    @CacheEvict(value = "students", key = "#command.studentId")
    public Student updateStudent(UpdateStudentCommand command) {
        // Evicts cache on update
    }

    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        // Evicts cache on delete
    }
}
```

### 10.3 Batch Operations

```java
// Batch insert students
@Transactional
public List<Student> createStudentsBatch(List<CreateStudentCommand> commands) {
    List<Student> students = commands.stream()
        .map(this::createStudent)
        .collect(Collectors.toList());

    return studentRepository.saveAll(students);
}
```

## 11. Monitoring & Metrics

### 11.1 Custom Metrics

```java
package com.school.sms.student.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class StudentMetrics {

    private final Counter studentsCreatedCounter;
    private final Counter studentsUpdatedCounter;
    private final Counter studentsDeletedCounter;
    private final Timer studentCreationTimer;

    public StudentMetrics(MeterRegistry meterRegistry) {
        this.studentsCreatedCounter = Counter.builder("students.created.total")
            .description("Total number of students created")
            .register(meterRegistry);

        this.studentsUpdatedCounter = Counter.builder("students.updated.total")
            .description("Total number of students updated")
            .register(meterRegistry);

        this.studentsDeletedCounter = Counter.builder("students.deleted.total")
            .description("Total number of students deleted")
            .register(meterRegistry);

        this.studentCreationTimer = Timer.builder("students.creation.duration")
            .description("Time taken to create a student")
            .register(meterRegistry);
    }

    public void incrementStudentsCreated() {
        studentsCreatedCounter.increment();
    }

    public void incrementStudentsUpdated() {
        studentsUpdatedCounter.increment();
    }

    public void incrementStudentsDeleted() {
        studentsDeletedCounter.increment();
    }

    public Timer.Sample startCreationTimer() {
        return Timer.start();
    }

    public void recordCreationDuration(Timer.Sample sample) {
        sample.stop(studentCreationTimer);
    }
}
```

### 11.2 Actuator Endpoints

```yaml
# Expose actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# Custom health indicators
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "UP")
                .build();
        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## 12. Testing Guidelines

See `07-testing-strategy.md` for comprehensive testing guidelines.

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED
