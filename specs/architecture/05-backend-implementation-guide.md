# Backend Implementation Guidelines
**School Management System - Backend Developer Agent Instructions**

**Version**: 1.0
**Date**: 2026-01-15
**Status**: Active
**Target Agent**: Backend Developer Agent

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Layer-by-Layer Implementation](#layer-by-layer-implementation)
4. [API Implementation Standards](#api-implementation-standards)
5. [Data Mapping Strategy](#data-mapping-strategy)
6. [Performance Optimization](#performance-optimization)
7. [Monitoring & Observability](#monitoring--observability)
8. [Testing Requirements](#testing-requirements)
9. [Code Quality Standards](#code-quality-standards)

---

## Overview

### Implementation Mandate

This guide is **MANDATORY** for the Backend Developer Agent. All code generated must strictly adhere to these patterns and standards.

### Technology Stack (Non-Negotiable)

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Database**: PostgreSQL 18+
- **ORM**: Spring Data JPA / Hibernate
- **Rules Engine**: Drools 9.44.0.Final
- **Cache**: Redis
- **Mapping**: MapStruct
- **Monitoring**: Micrometer + Zipkin
- **Testing**: JUnit 5, Mockito, TestContainers

### Architecture Pattern

**Strict 4-Layer Architecture**:
1. **Presentation Layer**: REST controllers, DTOs
2. **Application Layer**: Service orchestration, CQRS
3. **Domain Layer**: Aggregates, Value Objects, Business Logic
4. **Infrastructure Layer**: JPA, Drools, Redis

**CRITICAL**: No layer can depend on layers below it except in the prescribed direction.

---

## Project Structure

### Student Service Structure

```
student-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/school/student/
│   │   │       ├── StudentServiceApplication.java
│   │   │       ├── presentation/
│   │   │       │   ├── controller/
│   │   │       │   │   ├── StudentController.java
│   │   │       │   │   └── EnrollmentController.java
│   │   │       │   ├── dto/
│   │   │       │   │   ├── request/
│   │   │       │   │   │   ├── StudentCreateRequest.java
│   │   │       │   │   │   ├── StudentUpdateRequest.java
│   │   │       │   │   │   └── EnrollmentCreateRequest.java
│   │   │       │   │   └── response/
│   │   │       │   │       ├── StudentResponse.java
│   │   │       │   │       ├── EnrollmentResponse.java
│   │   │       │   │       └── ErrorResponse.java
│   │   │       │   └── exception/
│   │   │       │       └── GlobalExceptionHandler.java
│   │   │       ├── application/
│   │   │       │   ├── service/
│   │   │       │   │   ├── StudentApplicationService.java
│   │   │       │   │   └── EnrollmentApplicationService.java
│   │   │       │   ├── mapper/
│   │   │       │   │   ├── StudentMapper.java
│   │   │       │   │   └── EnrollmentMapper.java
│   │   │       │   └── command/
│   │   │       │       ├── RegisterStudentCommand.java
│   │   │       │       └── UpdateStudentCommand.java
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   │   ├── Student.java (Aggregate Root)
│   │   │       │   │   ├── Enrollment.java (Entity)
│   │   │       │   │   ├── Mobile.java (Value Object)
│   │   │       │   │   ├── Email.java (Value Object)
│   │   │       │   │   └── StudentStatus.java (Enum)
│   │   │       │   ├── repository/
│   │   │       │   │   └── StudentRepository.java (Interface)
│   │   │       │   ├── service/
│   │   │       │   │   └── StudentDomainService.java
│   │   │       │   └── exception/
│   │   │       │       ├── StudentNotFoundException.java
│   │   │       │       └── InvalidAgeException.java
│   │   │       └── infrastructure/
│   │   │           ├── persistence/
│   │   │           │   ├── entity/
│   │   │           │   │   ├── StudentEntity.java
│   │   │           │   │   └── EnrollmentEntity.java
│   │   │           │   ├── repository/
│   │   │           │   │   ├── StudentJpaRepository.java
│   │   │           │   │   └── StudentRepositoryImpl.java
│   │   │           │   └── mapper/
│   │   │           │       └── StudentEntityMapper.java
│   │   │           ├── cache/
│   │   │           │   └── RedisCacheConfig.java
│   │   │           ├── rules/
│   │   │           │   ├── DroolsConfig.java
│   │   │           │   └── DroolsValidationService.java
│   │   │           └── monitoring/
│   │   │               └── MetricsConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/
│   │       │   ├── V1__create_students_table.sql
│   │       │   └── V2__create_enrollments_table.sql
│   │       ├── rules/
│   │       │   └── student/
│   │       │       └── student-validation-rules.drl
│   │       └── META-INF/
│   │           └── kmodule.xml
│   └── test/
│       └── java/
│           └── com/school/student/
│               ├── domain/
│               │   └── StudentTest.java
│               ├── application/
│               │   └── StudentApplicationServiceTest.java
│               ├── infrastructure/
│               │   └── StudentRepositoryImplTest.java
│               └── presentation/
│                   └── StudentControllerIntegrationTest.java
├── pom.xml
└── Dockerfile
```

---

## Layer-by-Layer Implementation

### 1. Domain Layer (Start Here)

**Purpose**: Contains ALL business logic. This is the heart of the application.

#### Aggregate Root: Student

```java
package com.school.student.domain.model;

import lombok.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Student Aggregate Root
 * Contains all business logic for student lifecycle management
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA requirement
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder pattern
@Builder
public class Student {

    private Long id;  // Database ID (infrastructure concern)
    private String studentId;  // Business ID (auto-generated)

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Mobile mobile;
    private Email email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private String aadhaarNumber;

    private StudentStatus status;

    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    private Integer version;  // Optimistic locking

    /**
     * Factory method for student registration
     * Enforces business invariants
     */
    public static Student register(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Mobile mobile,
            Email email,
            String address,
            String fathersName,
            String mothersName,
            String aadhaarNumber
    ) {
        // Business rule validations
        validateAge(dateOfBirth);
        validateNames(firstName, lastName);
        validateAddress(address);

        return Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .mobile(mobile)
                .email(email)
                .address(address)
                .fathersName(fathersName)
                .mothersName(mothersName)
                .aadhaarNumber(aadhaarNumber)
                .status(StudentStatus.ACTIVE)  // Default status
                .build();
    }

    /**
     * Business Rule BR-001: Age must be between 3 and 18 years
     */
    private static void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new InvalidAgeException(
                    "Student age must be between 3 and 18 years at registration. Current age: " + age
            );
        }
    }

    private static void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() || firstName.length() < 2) {
            throw new IllegalArgumentException("First name must be at least 2 characters");
        }
        if (lastName == null || lastName.trim().isEmpty() || lastName.length() < 2) {
            throw new IllegalArgumentException("Last name must be at least 2 characters");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.trim().length() < 10) {
            throw new IllegalArgumentException("Address must be at least 10 characters");
        }
    }

    /**
     * Business Rule BR-005: Only certain fields can be updated
     */
    public void updateAllowedFields(String firstName, String lastName, Mobile mobile, StudentStatus status) {
        if (firstName != null) {
            validateNames(firstName, this.lastName);
            this.firstName = firstName;
        }
        if (lastName != null) {
            validateNames(this.firstName, lastName);
            this.lastName = lastName;
        }
        if (mobile != null) {
            this.mobile = mobile;
        }
        if (status != null) {
            this.status = status;
        }
    }

    /**
     * Activate student account
     */
    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Deactivate student account
     */
    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Add enrollment to student's history
     */
    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        this.enrollments.add(enrollment);
    }

    /**
     * Calculate current age
     */
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if student is active
     */
    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }
}
```

#### Value Object: Mobile

```java
package com.school.student.domain.model;

import lombok.Value;
import java.util.regex.Pattern;

/**
 * Mobile Value Object
 * Ensures mobile number validity and immutability
 */
@Value  // Lombok: Immutable class with equals/hashCode
public class Mobile {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\d{10}$");

    String number;

    /**
     * Factory method with validation
     */
    public static Mobile of(String number) {
        if (number == null || !MOBILE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Mobile number must be exactly 10 digits");
        }
        return new Mobile(number);
    }

    private Mobile(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return number;
    }
}
```

#### Repository Interface (Port)

```java
package com.school.student.domain.repository;

import com.school.student.domain.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Student Repository Interface (Port)
 * Defines contract for persistence operations
 * Implementation in infrastructure layer
 */
public interface StudentRepository {

    /**
     * Save or update student
     */
    Student save(Student student);

    /**
     * Find student by business ID
     */
    Optional<Student> findByStudentId(String studentId);

    /**
     * Find student by database ID
     */
    Optional<Student> findById(Long id);

    /**
     * Find all students matching criteria
     */
    List<Student> findAll(StudentSearchCriteria criteria);

    /**
     * Check if mobile number exists (uniqueness check)
     */
    boolean existsByMobileAndIdNot(String mobile, Long id);

    /**
     * Check if email exists (uniqueness check)
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Delete student
     */
    void delete(Student student);

    /**
     * Count total students
     */
    long count();

    /**
     * Count active students
     */
    long countByStatus(StudentStatus status);
}
```

---

### 2. Application Layer

**Purpose**: Orchestration, transaction management, DTO mapping

#### Application Service

```java
package com.school.student.application.service;

import com.school.student.application.mapper.StudentMapper;
import com.school.student.domain.model.*;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.infrastructure.rules.DroolsValidationService;
import com.school.student.presentation.dto.request.StudentCreateRequest;
import com.school.student.presentation.dto.request.StudentUpdateRequest;
import com.school.student.presentation.dto.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.annotation.Timed;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Student Application Service
 * Orchestrates domain operations and coordinates cross-cutting concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)  // Default to read-only, override for writes
public class StudentApplicationService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final DroolsValidationService droolsValidationService;

    /**
     * Register new student
     * Business Rule: BR-001 (Age 3-18), BR-002 (Mobile uniqueness)
     */
    @Transactional
    @Timed(value = "student.register", description = "Time to register student")
    @CacheEvict(value = "students", allEntries = true)  // Invalidate cache
    public StudentResponse registerStudent(StudentCreateRequest request) {
        log.info("Registering student: {} {}", request.getFirstName(), request.getLastName());

        // 1. Map DTO to Domain Model
        Student student = studentMapper.toDomain(request);

        // 2. Drools Validation (Business Rules)
        ValidationResult validationResult = droolsValidationService.validateStudent(student);
        if (validationResult.hasErrors()) {
            log.warn("Student validation failed: {}", validationResult.getErrors());
            throw new ValidationException(validationResult.getErrors());
        }

        // 3. Check Mobile Uniqueness (BR-002)
        if (studentRepository.existsByMobileAndIdNot(student.getMobile().getNumber(), null)) {
            throw new DuplicateMobileException("Mobile number already registered: " + student.getMobile());
        }

        // 4. Persist Student (auto-generates student_id via trigger)
        Student savedStudent = studentRepository.save(student);
        log.info("Student registered successfully: {}", savedStudent.getStudentId());

        // 5. Map Domain to Response DTO
        return studentMapper.toResponse(savedStudent);
    }

    /**
     * Get student by ID
     */
    @Cacheable(value = "students", key = "#studentId")
    @Timed(value = "student.get", description = "Time to retrieve student")
    public StudentResponse getStudent(String studentId) {
        log.debug("Retrieving student: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        return studentMapper.toResponse(student);
    }

    /**
     * Update student (restricted fields only - BR-005)
     */
    @Transactional
    @Timed(value = "student.update", description = "Time to update student")
    @CacheEvict(value = "students", key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        log.info("Updating student: {}", studentId);

        // 1. Retrieve existing student
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        // 2. Optimistic Locking Check
        if (!student.getVersion().equals(request.getVersion())) {
            throw new OptimisticLockException("Student has been modified by another user");
        }

        // 3. Check mobile uniqueness if mobile is being changed
        if (request.getMobile() != null &&
                !request.getMobile().equals(student.getMobile().getNumber())) {
            if (studentRepository.existsByMobileAndIdNot(request.getMobile(), student.getId())) {
                throw new DuplicateMobileException("Mobile number already registered: " + request.getMobile());
            }
        }

        // 4. Update allowed fields only
        student.updateAllowedFields(
                request.getFirstName(),
                request.getLastName(),
                request.getMobile() != null ? Mobile.of(request.getMobile()) : null,
                request.getStatus()
        );

        // 5. Persist changes
        Student updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully: {}", studentId);

        return studentMapper.toResponse(updatedStudent);
    }

    /**
     * Delete student
     */
    @Transactional
    @Timed(value = "student.delete", description = "Time to delete student")
    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        studentRepository.delete(student);
        log.info("Student deleted successfully: {}", studentId);
    }

    /**
     * List all students with optional filtering
     */
    @Cacheable(value = "studentsList", key = "#criteria.toString()")
    @Timed(value = "student.list", description = "Time to list students")
    public List<StudentResponse> listStudents(StudentSearchCriteria criteria) {
        log.debug("Listing students with criteria: {}", criteria);

        List<Student> students = studentRepository.findAll(criteria);

        return students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get student statistics
     */
    @Cacheable(value = "studentStats")
    public StudentStatistics getStatistics() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long inactiveStudents = studentRepository.countByStatus(StudentStatus.INACTIVE);

        return StudentStatistics.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .inactiveStudents(inactiveStudents)
                .build();
    }
}
```

#### MapStruct Mapper

```java
package com.school.student.application.mapper;

import com.school.student.domain.model.*;
import com.school.student.presentation.dto.request.StudentCreateRequest;
import com.school.student.presentation.dto.response.StudentResponse;
import org.mapstruct.*;

/**
 * MapStruct Mapper for Student
 * Generates mapping code at compile time (zero runtime overhead)
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StudentMapper {

    /**
     * Map CreateRequest DTO to Domain Model
     */
    @Mapping(target = "id", ignore = true)  // Generated by database
    @Mapping(target = "studentId", ignore = true)  // Generated by database trigger
    @Mapping(target = "mobile", source = "mobile", qualifiedByName = "stringToMobile")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "status", constant = "ACTIVE")  // Default status
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "version", ignore = true)
    Student toDomain(StudentCreateRequest request);

    /**
     * Map Domain Model to Response DTO
     */
    @Mapping(source = "mobile.number", target = "mobile")
    @Mapping(source = "email.address", target = "email")
    @Mapping(source = "status", target = "status")
    StudentResponse toResponse(Student student);

    /**
     * Custom mapping for Mobile value object
     */
    @Named("stringToMobile")
    default Mobile stringToMobile(String mobile) {
        return mobile != null ? Mobile.of(mobile) : null;
    }

    /**
     * Custom mapping for Email value object
     */
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }
}
```

---

### 3. Infrastructure Layer

#### JPA Entity

```java
package com.school.student.infrastructure.persistence.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Student JPA Entity
 * Persistence model (separate from domain model)
 */
@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_students_last_name", columnList = "last_name"),
        @Index(name = "idx_students_mobile", columnList = "mobile"),
        @Index(name = "idx_students_student_id", columnList = "student_id")
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

    @Column(name = "student_id", nullable = false, unique = true, length = 50)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile", nullable = false, unique = true, length = 10)
    private String mobile;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "fathers_name", length = 100)
    private String fathersName;

    @Column(name = "mothers_name", length = 100)
    private String mothersName;

    @Column(name = "identification_mark", columnDefinition = "TEXT")
    private String identificationMark;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EnrollmentEntity> enrollments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

#### Repository Implementation

```java
package com.school.student.infrastructure.persistence.repository;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentSearchCriteria;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.infrastructure.persistence.entity.StudentEntity;
import com.school.student.infrastructure.persistence.mapper.StudentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Student Repository Implementation (Adapter)
 * Bridges domain repository interface with JPA
 */
@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    @Override
    public Student save(Student student) {
        StudentEntity entity = entityMapper.toEntity(student);
        StudentEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Student> findAll(StudentSearchCriteria criteria) {
        // Build dynamic query based on criteria
        // Use Spring Data JPA Specifications for complex queries
        return jpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByMobileAndIdNot(String mobile, Long id) {
        return id == null ?
                jpaRepository.existsByMobile(mobile) :
                jpaRepository.existsByMobileAndIdNot(mobile, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return id == null ?
                jpaRepository.existsByEmail(email) :
                jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public void delete(Student student) {
        jpaRepository.deleteById(student.getId());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStatus(StudentStatus status) {
        return jpaRepository.countByStatus(status.name());
    }
}
```

---

### 4. Presentation Layer

#### REST Controller

```java
package com.school.student.presentation.controller;

import com.school.student.application.service.StudentApplicationService;
import com.school.student.presentation.dto.request.StudentCreateRequest;
import com.school.student.presentation.dto.request.StudentUpdateRequest;
import com.school.student.presentation.dto.response.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Student REST Controller
 * Implements OpenAPI 3.0 specification
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Student management operations")
@Validated
public class StudentController {

    private final StudentApplicationService studentService;

    /**
     * Register new student
     * POST /api/v1/students
     */
    @PostMapping
    @Operation(summary = "Register a new student", description = "Creates a new student record with auto-generated student ID")
    public ResponseEntity<StudentResponse> registerStudent(
            @Valid @RequestBody StudentCreateRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId
    ) {
        log.info("POST /api/v1/students - Correlation-ID: {}", correlationId);

        StudentResponse response = studentService.registerStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get student by ID
     * GET /api/v1/students/{studentId}
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Retrieve student by ID")
    public ResponseEntity<StudentResponse> getStudent(
            @PathVariable String studentId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId
    ) {
        log.debug("GET /api/v1/students/{} - Correlation-ID: {}", studentId, correlationId);

        StudentResponse response = studentService.getStudent(studentId);

        return ResponseEntity.ok(response);
    }

    /**
     * List students with optional filtering
     * GET /api/v1/students?status=ACTIVE&lastName=Smith
     */
    @GetMapping
    @Operation(summary = "List all students with optional filters")
    public ResponseEntity<List<StudentResponse>> listStudents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String lastName,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId
    ) {
        log.debug("GET /api/v1/students - Filters: status={}, lastName={}", status, lastName);

        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
                .status(status)
                .lastName(lastName)
                .build();

        List<StudentResponse> response = studentService.listStudents(criteria);

        return ResponseEntity.ok(response);
    }

    /**
     * Update student (restricted fields only)
     * PUT /api/v1/students/{studentId}
     */
    @PutMapping("/{studentId}")
    @Operation(summary = "Update student allowed fields", description = "Only firstName, lastName, mobile, and status can be updated")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody StudentUpdateRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId
    ) {
        log.info("PUT /api/v1/students/{} - Correlation-ID: {}", studentId, correlationId);

        StudentResponse response = studentService.updateStudent(studentId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete student
     * DELETE /api/v1/students/{studentId}
     */
    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student record")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable String studentId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId
    ) {
        log.info("DELETE /api/v1/students/{} - Correlation-ID: {}", studentId, correlationId);

        studentService.deleteStudent(studentId);

        return ResponseEntity.noContent().build();
    }
}
```

---

## API Implementation Standards

### 1. Follow OpenAPI Specification

**CRITICAL**: Implement ALL endpoints defined in `sms_api_specification.yaml` exactly as specified.

- Request/response types must match specification
- HTTP status codes must match specification
- Validation rules must match specification

### 2. Error Handling (RFC 7807 Problem Details)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStudentNotFound(StudentNotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.builder()
                .type("https://api.school.com/errors/student-not-found")
                .title("Student Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex, HttpServletRequest request) {
        return ErrorResponse.builder()
                .type("https://api.school.com/errors/validation-error")
                .title("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("One or more validation errors occurred")
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .errors(ex.getErrors())
                .build();
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLock(OptimisticLockException ex, HttpServletRequest request) {
        return ErrorResponse.builder()
                .type("https://api.school.com/errors/optimistic-lock")
                .title("Conflict")
                .status(HttpStatus.CONFLICT.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }
}
```

### 3. Request/Response DTOs

**Always use DTOs, NEVER expose domain models directly**:

```java
@Data
@Builder
public class StudentResponse {
    private String id;  // studentId, not database id
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer age;  // Calculated field
    private String mobile;
    private String email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private String aadhaarNumber;
    private StudentStatus status;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;
}
```

---

## Data Mapping Strategy

### MapStruct Configuration

```xml
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
```

### Three-Layer Mapping

```
Request DTO → Domain Model → JPA Entity → Domain Model → Response DTO
```

**Never map directly DTO ↔ Entity**. Always go through domain model.

---

## Performance Optimization

### 1. N+1 Query Prevention

**Use EntityGraph**:

```java
@EntityGraph(attributePaths = {"enrollments"})
List<StudentEntity> findAllWithEnrollments();
```

**Use JOIN FETCH**:

```java
@Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.id = :id")
Optional<Student> findByIdWithEnrollments(@Param("id") Long id);
```

### 2. Caching Strategy

```java
@Cacheable(value = "students", key = "#studentId")
public StudentResponse getStudent(String studentId) { ... }

@CacheEvict(value = "students", key = "#studentId")
public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) { ... }

@CacheEvict(value = "students", allEntries = true)
public StudentResponse registerStudent(StudentCreateRequest request) { ... }
```

### 3. HikariCP Tuning

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

---

## Monitoring & Observability

### 1. Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### 2. Custom Metrics

```java
@Component
public class StudentMetrics {

    private final Counter studentRegistrationCounter;
    private final Timer studentRegistrationTimer;

    public StudentMetrics(MeterRegistry registry) {
        this.studentRegistrationCounter = Counter.builder("students.registered.total")
                .description("Total number of students registered")
                .register(registry);

        this.studentRegistrationTimer = Timer.builder("student.registration.time")
                .description("Time to register a student")
                .register(registry);
    }

    public void incrementRegistrationCount() {
        studentRegistrationCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void recordTimer(Timer.Sample sample) {
        sample.stop(studentRegistrationTimer);
    }
}
```

### 3. Distributed Tracing (Zipkin)

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0  # 100% sampling for development
```

---

## Testing Requirements

### 1. Unit Tests (Domain Layer)

```java
class StudentTest {

    @Test
    @DisplayName("Should register student with valid age")
    void shouldRegisterStudentWithValidAge() {
        LocalDate dob = LocalDate.now().minusYears(10);

        Student student = Student.register(
                "John", "Doe", dob,
                Mobile.of("9876543210"),
                Email.of("john@example.com"),
                "123 Main Street, City",
                "Father Name", "Mother Name",
                "123456789012"
        );

        assertThat(student).isNotNull();
        assertThat(student.getAge()).isEqualTo(10);
        assertThat(student.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should reject student below minimum age")
    void shouldRejectStudentBelowMinimumAge() {
        LocalDate dob = LocalDate.now().minusYears(2);

        assertThatThrownBy(() -> Student.register(
                "John", "Doe", dob,
                Mobile.of("9876543210"),
                Email.of("john@example.com"),
                "123 Main Street",
                "Father", "Mother",
                "123456789012"
        )).isInstanceOf(InvalidAgeException.class)
          .hasMessageContaining("between 3 and 18 years");
    }
}
```

### 2. Integration Tests (Repository Layer)

```java
@SpringBootTest
@Testcontainers
class StudentRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should save and retrieve student")
    void shouldSaveAndRetrieveStudent() {
        Student student = Student.register(...);

        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudentId()).matches("STD-\\d{8}-\\d{4}");

        Optional<Student> retrieved = studentRepository.findByStudentId(saved.getStudentId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFirstName()).isEqualTo(student.getFirstName());
    }
}
```

### 3. API Tests (Controller Layer)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should register student via API")
    void shouldRegisterStudentViaApi() {
        StudentCreateRequest request = StudentCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.now().minusYears(10))
                .mobile("9876543210")
                .email("john@example.com")
                .address("123 Main Street, City")
                .fathersName("Father")
                .mothersName("Mother")
                .aadhaarNumber("123456789012")
                .build();

        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(
                "/api/v1/students",
                request,
                StudentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).matches("STD-\\d{8}-\\d{4}");
    }
}
```

---

## Code Quality Standards

### 1. Lombok Usage

**Allowed**:
- `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- `@RequiredArgsConstructor`, `@Slf4j`, `@Data` (DTOs only)
- `@Value` (Value Objects only)

**Not Allowed**:
- `@ToString` on entities (avoid lazy loading issues)
- `@EqualsAndHashCode` on entities (use business keys)

### 2. Logging Standards

```java
// Use SLF4J with Lombok @Slf4j
log.info("Student registered: {}", studentId);  // Good
log.info("Student registered: " + studentId);  // Bad (string concatenation)

// Log levels
log.error("Critical error", exception);  // Errors with stack trace
log.warn("Business rule violation: {}", message);  // Warnings
log.info("Important business event: {}", event);  // Key events
log.debug("Detailed trace: {}", details);  // Debugging (dev/staging only)
```

### 3. Exception Handling

**Domain Exceptions**:
```java
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
```

**Never swallow exceptions**:
```java
// Bad
try {
    riskyOperation();
} catch (Exception e) {
    // Do nothing
}

// Good
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed", e);
    throw new OperationFailedException("Operation failed", e);
}
```

---

## Configuration Management

### application.yml Structure

```yaml
spring:
  application:
    name: student-service
  profiles:
    active: ${ACTIVE_PROFILE:dev}

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/students_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}

  jpa:
    hibernate:
      ddl-auto: validate  # NEVER use 'update' or 'create' in production
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  flyway:
    enabled: true
    locations: classpath:db/migration

  cache:
    type: redis
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

server:
  port: 8081

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

---

**End of Backend Implementation Guidelines**
