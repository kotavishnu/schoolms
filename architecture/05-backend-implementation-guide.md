# Backend Implementation Guide
**School Management System - Phase 1**

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Layer Architecture Patterns](#layer-architecture-patterns)
4. [Domain Layer Implementation](#domain-layer-implementation)
5. [Application Layer Implementation](#application-layer-implementation)
6. [Infrastructure Layer Implementation](#infrastructure-layer-implementation)
7. [Presentation Layer Implementation](#presentation-layer-implementation)
8. [Business Rules with Drools](#business-rules-with-drools)
9. [Caching Strategy](#caching-strategy)
10. [Performance Optimization](#performance-optimization)
11. [Monitoring and Observability](#monitoring-and-observability)
12. [Error Handling Patterns](#error-handling-patterns)
13. [Testing Guidelines](#testing-guidelines)

---

## Overview

### Purpose

This guide provides **comprehensive coding standards and implementation patterns** for backend developers building the School Management System microservices.

### Architectural Principles

1. **Domain-Driven Design (DDD)**: Rich domain models with behavior
2. **Layered Architecture**: Strict layer separation (Presentation → Application → Domain → Infrastructure)
3. **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
4. **CQRS**: Separate command and query methods
5. **Dependency Injection**: Constructor injection via Spring
6. **DTO Pattern**: Domain models never exposed in API responses

---

## Project Structure

### Student Service Project Structure

```
student-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── schoolms/
│   │   │           └── student/
│   │   │               ├── StudentServiceApplication.java
│   │   │               ├── presentation/              # REST Controllers
│   │   │               │   ├── controller/
│   │   │               │   │   └── StudentController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   ├── CreateStudentRequest.java
│   │   │               │   │   │   └── UpdateStudentRequest.java
│   │   │               │   │   └── response/
│   │   │               │   │       ├── StudentResponse.java
│   │   │               │   │       └── StudentListResponse.java
│   │   │               │   └── exception/
│   │   │               │       ├── GlobalExceptionHandler.java
│   │   │               │       └── ProblemDetailFactory.java
│   │   │               ├── application/               # Use Cases / Services
│   │   │               │   ├── service/
│   │   │               │   │   └── StudentApplicationService.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── StudentMapper.java
│   │   │               │   └── validation/
│   │   │               │       └── StudentValidator.java
│   │   │               ├── domain/                    # Domain Models
│   │   │               │   ├── model/
│   │   │               │   │   ├── Student.java
│   │   │               │   │   ├── StudentStatus.java
│   │   │               │   │   └── StudentId.java
│   │   │               │   ├── repository/
│   │   │               │   │   └── StudentRepository.java (interface)
│   │   │               │   ├── rules/
│   │   │               │   │   └── StudentBusinessRules.java
│   │   │               │   ├── event/
│   │   │               │   │   └── StudentCreatedEvent.java
│   │   │               │   └── exception/
│   │   │               │       ├── StudentNotFoundException.java
│   │   │               │       ├── DuplicatePhoneException.java
│   │   │               │       └── InvalidAgeException.java
│   │   │               └── infrastructure/            # External Concerns
│   │   │                   ├── persistence/
│   │   │                   │   ├── entity/
│   │   │                   │   │   └── StudentJpaEntity.java
│   │   │                   │   ├── repository/
│   │   │                   │   │   └── StudentJpaRepository.java
│   │   │                   │   └── adapter/
│   │   │                   │       └── StudentRepositoryAdapter.java
│   │   │                   ├── cache/
│   │   │                   │   ├── RedisCacheManager.java
│   │   │                   │   └── CacheKeyGenerator.java
│   │   │                   ├── config/
│   │   │                   │   ├── DatabaseConfig.java
│   │   │                   │   ├── RedisConfig.java
│   │   │                   │   ├── DroolsConfig.java
│   │   │                   │   └── ObservabilityConfig.java
│   │   │                   └── generator/
│   │   │                       └── StudentIdGenerator.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/
│   │       │   ├── V1__create_students_table.sql
│   │       │   └── V2__add_students_indexes.sql
│   │       ├── rules/
│   │       │   └── student-validation.drl
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/
│               └── schoolms/
│                   └── student/
│                       ├── unit/                      # Unit tests
│                       ├── integration/               # Integration tests
│                       └── e2e/                       # End-to-end tests
├── Dockerfile
├── pom.xml
└── README.md
```

### Configuration Service Project Structure

Similar structure to Student Service:
- Replace `student` package with `configuration`
- Entities: `Configuration`, `ConfigCategory`
- Similar layer separation

---

## Layer Architecture Patterns

### Layer Dependency Rules

**Strict Dependency Direction** (inner layers know nothing about outer layers):

```
Presentation (Controllers, DTOs)
    ↓ depends on
Application (Services, Mappers)
    ↓ depends on
Domain (Entities, Repositories Interfaces, Business Logic)
    ↑ implemented by
Infrastructure (JPA, Redis, External APIs)
```

**Rules**:
1. **Domain layer** has NO dependencies on outer layers
2. **Application layer** depends ONLY on Domain
3. **Infrastructure layer** implements Domain interfaces
4. **Presentation layer** depends on Application layer

---

## Domain Layer Implementation

### 1. Rich Domain Model

**Student.java** (Domain Model):

```java
package com.schoolms.student.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.Instant;

public class Student {

    // Value Objects
    private StudentId id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String adhaarNumber;
    private String address;
    private String identificationMarks;
    private String guardianName;
    private String motherName;
    private String phone;
    private String email;
    private StudentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;

    // Constructor (package-private, use factory methods)
    Student() {
        // For JPA
    }

    // Factory Method (Domain-driven creation)
    public static Student register(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String adhaarNumber,
        String phone,
        String email,
        String address,
        String guardianName,
        String motherName,
        String identificationMarks
    ) {
        Student student = new Student();
        student.firstName = firstName;
        student.lastName = lastName;
        student.dateOfBirth = dateOfBirth;
        student.adhaarNumber = adhaarNumber;
        student.phone = phone;
        student.email = email;
        student.address = address;
        student.guardianName = guardianName;
        student.motherName = motherName;
        student.identificationMarks = identificationMarks;
        student.status = StudentStatus.ACTIVE;
        student.createdAt = Instant.now();
        student.updatedAt = Instant.now();
        student.version = 0;

        student.validate();

        return student;
    }

    // Domain Behavior: Calculate age
    public int getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    // Domain Behavior: Update allowed fields
    public void updateProfile(String firstName, String lastName, String phone) {
        if (firstName != null) this.firstName = firstName;
        if (lastName != null) this.lastName = lastName;
        if (phone != null) this.phone = phone;
        this.updatedAt = Instant.now();
    }

    // Domain Behavior: Change status
    public void activate() {
        this.status = StudentStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = StudentStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    // Domain Validation
    private void validate() {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (getAge() < 3 || getAge() > 18) {
            throw new InvalidAgeException(
                String.format("Age %d is outside allowed range (3-18)", getAge())
            );
        }
        // Additional validations...
    }

    // Getters only (no setters - immutability)
    public StudentId getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getAdhaarNumber() { return adhaarNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public StudentStatus getStatus() { return status; }
    // ... other getters
}
```

---

### 2. Value Objects

**StudentId.java** (Value Object):

```java
package com.schoolms.student.domain.model;

import java.util.Objects;

public class StudentId {
    private final String value;

    public StudentId(String value) {
        if (value == null || !value.matches("^STU-\\d{4}-\\d{5}$")) {
            throw new IllegalArgumentException("Invalid student ID format");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentId studentId = (StudentId) o;
        return Objects.equals(value, studentId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

---

### 3. Domain Repository Interface

**StudentRepository.java** (Interface in Domain Layer):

```java
package com.schoolms.student.domain.repository;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {

    // Commands
    Student save(Student student);
    void delete(StudentId studentId);

    // Queries
    Optional<Student> findById(StudentId studentId);
    Optional<Student> findByPhone(String phone);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByAdhaarNumber(String adhaarNumber);
    List<Student> findAll();
    List<Student> findByStatus(StudentStatus status);
    List<Student> search(String query);

    // Statistics
    long countByStatus(StudentStatus status);
    long count();

    // Existence checks
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByAdhaarNumber(String adhaarNumber);
}
```

**Note**: This interface is **implemented** in the Infrastructure layer, NOT the Domain layer.

---

### 4. Domain Exceptions

```java
package com.schoolms.student.domain.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String studentId) {
        super(String.format("Student with ID '%s' not found", studentId));
    }
}

public class DuplicatePhoneException extends RuntimeException {
    public DuplicatePhoneException(String phone) {
        super(String.format("Phone number '%s' is already registered", phone));
    }
}

public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String message) {
        super(message);
    }
}
```

---

## Application Layer Implementation

### 1. Application Service (Orchestration)

**StudentApplicationService.java**:

```java
package com.schoolms.student.application.service;

import com.schoolms.student.application.mapper.StudentMapper;
import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.infrastructure.cache.RedisCacheManager;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.request.UpdateStudentRequest;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class StudentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(StudentApplicationService.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RedisCacheManager cacheManager;

    public StudentApplicationService(
        StudentRepository studentRepository,
        StudentMapper studentMapper,
        RedisCacheManager cacheManager
    ) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.cacheManager = cacheManager;
    }

    // Command: Create Student
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating student: firstName={}, lastName={}, phone={}",
            request.firstName(), request.lastName(), maskPhone(request.phone()));

        // Check uniqueness
        if (studentRepository.existsByPhone(request.phone())) {
            throw new DuplicatePhoneException(request.phone());
        }
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        if (studentRepository.existsByAdhaarNumber(request.adhaarNumber())) {
            throw new DuplicateAdhaarException(request.adhaarNumber());
        }

        // Create domain entity
        Student student = Student.register(
            request.firstName(),
            request.lastName(),
            request.dateOfBirth(),
            request.adhaarNumber(),
            request.phone(),
            request.email(),
            request.address(),
            request.guardianName(),
            request.motherName(),
            request.identificationMarks()
        );

        // Persist
        student = studentRepository.save(student);

        // Invalidate cache
        cacheManager.evictAllStudentCaches();

        log.info("Student created successfully: studentId={}", student.getId().getValue());

        // Map to response DTO
        return studentMapper.toResponse(student);
    }

    // Command: Update Student
    public StudentResponse updateStudent(String studentId, UpdateStudentRequest request) {
        log.info("Updating student: studentId={}", studentId);

        Student student = studentRepository.findById(new StudentId(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Check phone uniqueness if changed
        if (request.phone() != null && !request.phone().equals(student.getPhone())) {
            if (studentRepository.existsByPhone(request.phone())) {
                throw new DuplicatePhoneException(request.phone());
            }
        }

        // Update domain entity
        student.updateProfile(request.firstName(), request.lastName(), request.phone());

        if (request.status() != null) {
            if (request.status() == StudentStatus.ACTIVE) {
                student.activate();
            } else {
                student.deactivate();
            }
        }

        // Persist
        student = studentRepository.save(student);

        // Evict cache
        cacheManager.evictStudent(studentId);
        cacheManager.evictAllStudentCaches();

        log.info("Student updated successfully: studentId={}", studentId);

        return studentMapper.toResponse(student);
    }

    // Command: Delete Student
    public void deleteStudent(String studentId) {
        log.info("Deleting student: studentId={}", studentId);

        StudentId id = new StudentId(studentId);
        if (!studentRepository.findById(id).isPresent()) {
            throw new StudentNotFoundException(studentId);
        }

        studentRepository.delete(id);

        // Evict cache
        cacheManager.evictStudent(studentId);
        cacheManager.evictAllStudentCaches();

        log.info("Student deleted successfully: studentId={}", studentId);
    }

    // Query: Get Student by ID
    @Transactional(readOnly = true)
    public StudentResponse getStudent(String studentId) {
        log.debug("Fetching student: studentId={}", studentId);

        // Try cache first
        StudentResponse cached = cacheManager.getStudent(studentId);
        if (cached != null) {
            log.debug("Cache hit for student: studentId={}", studentId);
            return cached;
        }

        // Fetch from database
        Student student = studentRepository.findById(new StudentId(studentId))
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        StudentResponse response = studentMapper.toResponse(student);

        // Cache result
        cacheManager.cacheStudent(studentId, response);

        return response;
    }

    // Query: List All Students
    @Transactional(readOnly = true)
    public List<StudentResponse> listStudents(StudentStatus status) {
        log.debug("Listing students: status={}", status);

        List<Student> students = status == null
            ? studentRepository.findAll()
            : studentRepository.findByStatus(status);

        return students.stream()
            .map(studentMapper::toResponse)
            .toList();
    }

    // Helper method
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "******" + phone.substring(phone.length() - 4);
    }
}
```

---

### 2. DTO Mapper (MapStruct)

**StudentMapper.java**:

```java
package com.schoolms.student.application.mapper;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "age", target = "age")
    StudentResponse toResponse(Student student);

    // List mapping (automatic)
    List<StudentResponse> toResponseList(List<Student> students);
}
```

**Maven Dependency**:
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

---

## Infrastructure Layer Implementation

### 1. JPA Entity (Persistence Model)

**StudentJpaEntity.java**:

```java
package com.schoolms.student.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_student_id", columnList = "student_id"),
    @Index(name = "idx_students_name", columnList = "last_name, first_name"),
    @Index(name = "idx_students_status", columnList = "status")
})
public class StudentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "adhaar_number", nullable = false, unique = true, length = 12)
    private String adhaarNumber;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "identification_marks", length = 200)
    private String identificationMarks;

    @Column(name = "guardian_name", nullable = false, length = 100)
    private String guardianName;

    @Column(name = "mother_name", nullable = false, length = 100)
    private String motherName;

    @Column(name = "phone", nullable = false, unique = true, length = 10)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        age = calculateAge();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        age = calculateAge();
    }

    private Integer calculateAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // Getters and setters (required for JPA)
    // ... (omitted for brevity)
}
```

---

### 2. JPA Repository

**StudentJpaRepository.java**:

```java
package com.schoolms.student.infrastructure.persistence.repository;

import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    Optional<StudentJpaEntity> findByStudentId(String studentId);

    Optional<StudentJpaEntity> findByPhone(String phone);

    Optional<StudentJpaEntity> findByEmail(String email);

    Optional<StudentJpaEntity> findByAdhaarNumber(String adhaarNumber);

    List<StudentJpaEntity> findByStatus(StudentStatus status);

    @Query("SELECT s FROM StudentJpaEntity s WHERE " +
           "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.guardianName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentJpaEntity> search(@Param("query") String query);

    long countByStatus(StudentStatus status);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByAdhaarNumber(String adhaarNumber);
}
```

---

### 3. Repository Adapter (Domain to Infrastructure Bridge)

**StudentRepositoryAdapter.java**:

```java
package com.schoolms.student.infrastructure.persistence.adapter;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import com.schoolms.student.infrastructure.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StudentRepositoryAdapter implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentEntityMapper entityMapper;

    public StudentRepositoryAdapter(
        StudentJpaRepository jpaRepository,
        StudentEntityMapper entityMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Student save(Student student) {
        StudentJpaEntity entity = entityMapper.toJpaEntity(student);
        StudentJpaEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public void delete(StudentId studentId) {
        StudentJpaEntity entity = jpaRepository.findByStudentId(studentId.getValue())
            .orElseThrow(() -> new StudentNotFoundException(studentId.getValue()));
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Student> findById(StudentId studentId) {
        return jpaRepository.findByStudentId(studentId.getValue())
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Student> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone)
            .map(entityMapper::toDomain);
    }

    @Override
    public List<Student> findAll() {
        return jpaRepository.findAll().stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaRepository.existsByPhone(phone);
    }

    // ... other methods
}
```

---

## Business Rules with Drools

### Drools Configuration

**DroolsConfig.java**:

```java
package com.schoolms.student.infrastructure.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/student-validation.drl";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieRepository kieRepository = kieServices.getRepository();
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }
}
```

---

### Business Rules Definition

**student-validation.drl**:

```drools
package com.schoolms.student.rules

import com.schoolms.student.domain.model.Student
import com.schoolms.student.domain.exception.InvalidAgeException

rule "Validate student age between 3 and 18"
    when
        $student: Student(age < 3 || age > 18)
    then
        throw new InvalidAgeException(
            "Student age " + $student.getAge() + " is outside allowed range (3-18)"
        );
end

rule "Validate first name not empty"
    when
        $student: Student(firstName == null || firstName.trim().isEmpty())
    then
        throw new IllegalArgumentException("First name is required");
end

rule "Validate phone format"
    when
        $student: Student(phone == null || !phone.matches("^\\d{10}$"))
    then
        throw new IllegalArgumentException("Phone must be exactly 10 digits");
end

rule "Validate adhaar format"
    when
        $student: Student(adhaarNumber == null || !adhaarNumber.matches("^\\d{12}$"))
    then
        throw new IllegalArgumentException("Adhaar must be exactly 12 digits");
end
```

---

### Using Drools in Service

```java
@Service
public class StudentBusinessRules {

    private final KieContainer kieContainer;

    public StudentBusinessRules(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public void validate(Student student) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(student);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }
}
```

---

## Caching Strategy

### Redis Cache Manager

**RedisCacheManager.java**:

```java
package com.schoolms.student.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class RedisCacheManager {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheManager.class);
    private static final String STUDENT_KEY_PREFIX = "sms:student:";
    private static final String STUDENT_LIST_KEY = "sms:students:all";
    private static final String STATISTICS_KEY = "sms:students:statistics";
    private static final Duration STUDENT_TTL = Duration.ofHours(2);
    private static final Duration LIST_TTL = Duration.ofMinutes(10);
    private static final Duration STATS_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheManager(
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Cache student
    public void cacheStudent(String studentId, StudentResponse student) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            redisTemplate.opsForValue().set(key, student, STUDENT_TTL);
            log.debug("Cached student: studentId={}", studentId);
        } catch (Exception e) {
            log.error("Failed to cache student: studentId={}", studentId, e);
            // Fail gracefully - don't throw exception
        }
    }

    // Get cached student
    public StudentResponse getStudent(String studentId) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return objectMapper.convertValue(cached, StudentResponse.class);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve student from cache: studentId={}", studentId, e);
        }
        return null;
    }

    // Evict student cache
    public void evictStudent(String studentId) {
        try {
            String key = STUDENT_KEY_PREFIX + studentId;
            redisTemplate.delete(key);
            log.debug("Evicted student cache: studentId={}", studentId);
        } catch (Exception e) {
            log.error("Failed to evict student cache: studentId={}", studentId, e);
        }
    }

    // Evict all student-related caches
    public void evictAllStudentCaches() {
        try {
            redisTemplate.delete(STUDENT_LIST_KEY);
            redisTemplate.delete(STATISTICS_KEY);
            log.debug("Evicted all student list and statistics caches");
        } catch (Exception e) {
            log.error("Failed to evict student list caches", e);
        }
    }
}
```

---

### Redis Configuration

**RedisConfig.java**:

```java
package com.schoolms.student.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.database}")
    private int redisDatabase;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
        factory.setDatabase(redisDatabase);  // DB0 for student service
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer jsonSerializer =
            new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

---

### Cache Configuration (application.yml)

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      database: 0  # DB0 for student service, DB1 for config service
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: -1ms
      timeout: 60000  # 60 seconds
```

---

## Performance Optimization

### 1. N+1 Query Prevention

**Use EntityGraph**:

```java
@Repository
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    @EntityGraph(attributePaths = {"enrollments", "classes"})
    Optional<StudentJpaEntity> findByStudentId(String studentId);

    @EntityGraph(attributePaths = {"enrollments"})
    List<StudentJpaEntity> findAll();
}
```

---

### 2. Batch Processing

**Batch Insert**:

```java
@Transactional
public void createStudentsBatch(List<CreateStudentRequest> requests) {
    List<Student> students = requests.stream()
        .map(this::createStudentEntity)
        .toList();

    // JPA batch insert (configured in application.yml)
    studentRepository.saveAll(students);
}
```

**application.yml**:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

---

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
      connection-test-query: SELECT 1
```

---

## Monitoring and Observability

### Custom Metrics

**StudentMetrics.java**:

```java
package com.schoolms.student.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class StudentMetrics {

    private final Counter studentsCreatedCounter;
    private final Counter studentsDeletedCounter;
    private final Timer studentCreationTimer;

    public StudentMetrics(MeterRegistry meterRegistry) {
        this.studentsCreatedCounter = Counter.builder("students.registered.total")
            .description("Total students registered")
            .register(meterRegistry);

        this.studentsDeletedCounter = Counter.builder("students.deleted.total")
            .description("Total students deleted")
            .register(meterRegistry);

        this.studentCreationTimer = Timer.builder("students.creation.time")
            .description("Time taken to create a student")
            .register(meterRegistry);
    }

    public void incrementStudentsCreated() {
        studentsCreatedCounter.increment();
    }

    public void incrementStudentsDeleted() {
        studentsDeletedCounter.increment();
    }

    public Timer.Sample startCreationTimer() {
        return Timer.start();
    }

    public void recordCreationTime(Timer.Sample sample) {
        sample.stop(studentCreationTimer);
    }
}
```

**Usage in Service**:

```java
public StudentResponse createStudent(CreateStudentRequest request) {
    Timer.Sample sample = metrics.startCreationTimer();

    try {
        // Create logic...
        Student student = studentRepository.save(student);

        metrics.incrementStudentsCreated();
        return studentMapper.toResponse(student);
    } finally {
        metrics.recordCreationTime(sample);
    }
}
```

---

### Actuator Configuration

```yaml
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
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active}
```

---

## Error Handling Patterns

### Global Exception Handler

**GlobalExceptionHandler.java**:

```java
package com.schoolms.student.presentation.exception;

import com.schoolms.student.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(StudentNotFoundException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.NOT_FOUND,
            "not-found",
            "Resource Not Found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicatePhoneException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.CONFLICT,
            "duplicate-resource",
            "Duplicate Resource",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(InvalidAgeException.class)
    public ResponseEntity<ProblemDetail> handleInvalidAge(InvalidAgeException ex) {
        ProblemDetail problem = createProblemDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "business-rule-violation",
            "Business Rule Violation",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ProblemDetail problem = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "validation-error",
            "Validation Error",
            "Request validation failed"
        );
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "internal-server-error",
            "Internal Server Error",
            "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ProblemDetail createProblemDetail(
        HttpStatus status,
        String type,
        String title,
        String detail
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("https://api.schoolms.com/problems/" + type));
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", MDC.get("traceId"));
        return problem;
    }
}
```

---

## Testing Guidelines

### 1. Unit Tests

**StudentApplicationServiceTest.java**:

```java
package com.schoolms.student.application.service;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentApplicationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private RedisCacheManager cacheManager;

    @InjectMocks
    private StudentApplicationService service;

    @Test
    void createStudent_ShouldSucceed_WhenValidRequest() {
        // Given
        CreateStudentRequest request = createValidRequest();
        Student student = createValidStudent();

        when(studentRepository.existsByPhone(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(any(Student.class))).thenReturn(createValidResponse());

        // When
        StudentResponse response = service.createStudent(request);

        // Then
        assertNotNull(response);
        verify(studentRepository).save(any(Student.class));
        verify(cacheManager).evictAllStudentCaches();
    }

    @Test
    void createStudent_ShouldThrowException_WhenPhoneDuplicate() {
        // Given
        CreateStudentRequest request = createValidRequest();
        when(studentRepository.existsByPhone(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicatePhoneException.class, () -> {
            service.createStudent(request);
        });

        verify(studentRepository, never()).save(any());
    }
}
```

---

### 2. Integration Tests

**StudentIntegrationTest.java**:

```java
package com.schoolms.student.integration;

import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class StudentIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createStudent_ShouldReturn201_WhenValidRequest() {
        // Given
        CreateStudentRequest request = createValidRequest();

        // When
        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(
            "/api/v1/students",
            request,
            StudentResponse.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
    }
}
```

---

## Summary

This backend implementation guide provides:

1. **Project Structure**: Complete package organization following DDD
2. **Layer Separation**: Strict boundaries between Presentation, Application, Domain, Infrastructure
3. **Rich Domain Models**: Behavior-rich entities, not anemic POJOs
4. **Repository Pattern**: Clean abstraction between domain and persistence
5. **DTO Mapping**: MapStruct for efficient conversions
6. **Business Rules**: Drools for externalized validation logic
7. **Caching**: Redis with proper TTL and eviction strategies
8. **Performance**: N+1 prevention, batch processing, connection pooling
9. **Observability**: Custom metrics, structured logging, tracing
10. **Error Handling**: RFC 7807 problem details, graceful failures
11. **Testing**: Unit, integration, and E2E test patterns

**Development Workflow**:
1. Start with Domain layer (entities, repository interfaces)
2. Implement Infrastructure layer (JPA, cache)
3. Build Application layer (services, mappers)
4. Expose via Presentation layer (controllers, DTOs)
5. Add tests at each layer

---

**Next Steps**: Proceed to `06-frontend-implementation-guide.md` for React application development patterns.
