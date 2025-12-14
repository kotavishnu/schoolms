# Backend Implementation Guide - School Management System

## 1. Overview

This guide provides comprehensive implementation patterns for backend developers building the School Management System using Spring Boot 3.5.0, Java 21, and Domain-Driven Design principles.

## 2. Project Structure

### 2.1 Maven Multi-Module Structure

```
sms-backend/
├── pom.xml (parent)
├── student-service/
│   ├── pom.xml
│   └── src/main/java/com/school/sms/student/
│       ├── domain/
│       ├── application/
│       ├── infrastructure/
│       └── presentation/
├── configuration-service/
│   ├── pom.xml
│   └── src/main/java/com/school/sms/configuration/
│       ├── domain/
│       ├── application/
│       ├── infrastructure/
│       └── presentation/
└── sms-common/
    ├── pom.xml
    └── src/main/java/com/school/sms/common/
        ├── exception/
        ├── util/
        └── dto/
```

### 2.2 Student Service Package Structure

```
com.school.sms.student
├── domain
│   ├── model
│   │   ├── Student.java                 (Aggregate Root)
│   │   ├── StudentId.java               (Value Object)
│   │   ├── Age.java                     (Value Object)
│   │   ├── Mobile.java                  (Value Object)
│   │   └── StudentStatus.java           (Enum)
│   ├── repository
│   │   └── StudentRepository.java       (Interface - Port)
│   ├── event
│   │   ├── StudentRegisteredEvent.java
│   │   └── StudentStatusChangedEvent.java
│   └── service
│       └── StudentIdGenerator.java      (Domain Service)
├── application
│   ├── service
│   │   ├── StudentService.java          (Interface)
│   │   └── StudentServiceImpl.java      (Implementation)
│   ├── dto
│   │   ├── request
│   │   │   ├── CreateStudentRequest.java
│   │   │   ├── UpdateStudentRequest.java
│   │   │   └── UpdateStatusRequest.java
│   │   └── response
│   │       ├── StudentResponse.java
│   │       └── StudentPageResponse.java
│   ├── mapper
│   │   └── StudentMapper.java           (MapStruct Interface)
│   └── validation
│       ├── AgeRange.java                (Custom Validator)
│       └── AgeRangeValidator.java
├── infrastructure
│   ├── persistence
│   │   ├── entity
│   │   │   └── StudentJpaEntity.java    (JPA Entity)
│   │   ├── repository
│   │   │   └── JpaStudentRepository.java (JPA Interface)
│   │   └── adapter
│   │       └── StudentRepositoryAdapter.java (Implements Domain Interface)
│   ├── rules
│   │   ├── DroolsConfig.java
│   │   ├── RulesService.java
│   │   └── resources
│   │       └── student-rules.drl        (Drools Rules)
│   ├── cache
│   │   ├── RedisCacheConfig.java
│   │   └── StudentCacheService.java
│   └── event
│       └── DomainEventPublisher.java
└── presentation
    ├── controller
    │   └── StudentController.java       (REST Controller)
    ├── exception
    │   ├── GlobalExceptionHandler.java
    │   └── ProblemDetailsFactory.java
    └── config
        ├── WebConfig.java
        └── OpenApiConfig.java
```

## 3. Domain Layer Implementation

### 3.1 Student Aggregate Root

**Purpose**: Encapsulate student business logic and enforce invariants.

```java
package com.school.sms.student.domain.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student Aggregate Root
 * Encapsulates student lifecycle and business rules
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Student {

    private Long id;
    private StudentId studentId;
    private String firstName;
    private String lastName;
    private String address;
    private Mobile mobile;
    private LocalDate dateOfBirth;
    private String fatherName;
    private String motherName;
    private String identificationMark;
    private String email;
    private String aadhaarNumber;
    private StudentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;

    /**
     * Factory method for creating new student
     * Enforces business rules at creation time
     */
    public static Student register(
            StudentId studentId,
            String firstName,
            String lastName,
            String address,
            Mobile mobile,
            LocalDate dateOfBirth,
            String fatherName,
            String motherName,
            String identificationMark,
            String email,
            String aadhaarNumber,
            String createdBy
    ) {
        validateAge(dateOfBirth);

        return Student.builder()
                .studentId(studentId)
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .mobile(mobile)
                .dateOfBirth(dateOfBirth)
                .fatherName(fatherName)
                .motherName(motherName)
                .identificationMark(identificationMark)
                .email(email)
                .aadhaarNumber(aadhaarNumber)
                .status(StudentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .version(0)
                .build();
    }

    /**
     * Update editable fields (business rule: only firstName, lastName, mobile, status)
     */
    public void updateProfile(String firstName, String lastName, Mobile mobile, String updatedBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Change student status
     */
    public void changeStatus(StudentStatus newStatus, String updatedBy) {
        if (this.status == newStatus) {
            return; // No change
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Calculate current age
     */
    public int getCurrentAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Domain invariant: Age must be between 3 and 18 years
     */
    private static void validateAge(LocalDate dateOfBirth) {
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();
        if (age < 3 || age > 18) {
            throw new IllegalArgumentException(
                "Student age must be between 3 and 18 years. Current age: " + age
            );
        }
    }

    /**
     * Check if student is active
     */
    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }
}
```

### 3.2 Value Objects

**StudentId Value Object:**
```java
package com.school.sms.student.domain.model;

import lombok.Value;
import java.util.regex.Pattern;

/**
 * Value Object for Student ID
 * Immutable, self-validating
 * Format: STU-YYYY-XXXXX
 */
@Value
public class StudentId {
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU-\\d{4}-\\d{5}$");

    String value;

    public StudentId(String value) {
        if (value == null || !STUDENT_ID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "Invalid StudentId format. Expected: STU-YYYY-XXXXX, got: " + value
            );
        }
        this.value = value;
    }

    public static StudentId of(String value) {
        return new StudentId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

**Mobile Value Object:**
```java
package com.school.sms.student.domain.model;

import lombok.Value;
import java.util.regex.Pattern;

/**
 * Value Object for Mobile Number
 * Ensures mobile format validity
 */
@Value
public class Mobile {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    String number;

    public Mobile(String number) {
        if (number == null || !MOBILE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException(
                "Invalid mobile number format. Expected 10-15 digits, got: " + number
            );
        }
        this.number = number;
    }

    public static Mobile of(String number) {
        return new Mobile(number);
    }

    @Override
    public String toString() {
        return number;
    }
}
```

**StudentStatus Enum:**
```java
package com.school.sms.student.domain.model;

public enum StudentStatus {
    ACTIVE,
    INACTIVE,
    GRADUATED,
    TRANSFERRED
}
```

### 3.3 Repository Interface (Port)

```java
package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.model.Mobile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Domain repository interface (Port)
 * Defines contract for student persistence
 */
public interface StudentRepository {

    Student save(Student student);

    Optional<Student> findById(Long id);

    Optional<Student> findByStudentId(StudentId studentId);

    Optional<Student> findByMobile(Mobile mobile);

    Page<Student> findAll(Pageable pageable);

    Page<Student> findByLastNameContaining(String lastName, Pageable pageable);

    Page<Student> findByFatherNameContaining(String guardianName, Pageable pageable);

    Page<Student> findByStatus(String status, Pageable pageable);

    boolean existsByMobile(Mobile mobile);

    void deleteById(Long id);

    long countByStudentIdStartingWith(String prefix);
}
```

### 3.4 Domain Service - StudentId Generator

```java
package com.school.sms.student.domain.service;

import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

/**
 * Domain Service for generating unique Student IDs
 * Format: STU-YYYY-XXXXX
 */
@Service
@RequiredArgsConstructor
public class StudentIdGenerator {

    private final StudentRepository studentRepository;

    /**
     * Generate next sequential StudentId for current year
     */
    public StudentId generateNext() {
        int currentYear = LocalDate.now().getYear();
        String prefix = "STU-" + currentYear + "-";

        long count = studentRepository.countByStudentIdStartingWith(prefix);
        long nextSequence = count + 1;

        String studentIdValue = String.format("%s%05d", prefix, nextSequence);
        return StudentId.of(studentIdValue);
    }
}
```

### 3.5 Domain Events

```java
package com.school.sms.student.domain.event;

import com.school.sms.student.domain.model.StudentId;
import lombok.Value;
import java.time.LocalDateTime;

/**
 * Domain Event: Student Registered
 */
@Value
public class StudentRegisteredEvent {
    StudentId studentId;
    String firstName;
    String lastName;
    String mobile;
    LocalDateTime registeredAt;
}

/**
 * Domain Event: Student Status Changed
 */
@Value
public class StudentStatusChangedEvent {
    StudentId studentId;
    String oldStatus;
    String newStatus;
    LocalDateTime changedAt;
}
```

## 4. Application Layer Implementation

### 4.1 Service Interface

```java
package com.school.sms.student.application.service;

import com.school.sms.student.application.dto.request.*;
import com.school.sms.student.application.dto.response.*;
import org.springframework.data.domain.Pageable;

/**
 * Application service interface for student operations
 */
public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse getStudentById(Long id);

    StudentResponse getStudentByStudentId(String studentId);

    StudentPageResponse searchStudents(StudentSearchCriteria criteria, Pageable pageable);

    StudentResponse updateStudent(Long id, UpdateStudentRequest request);

    StudentResponse updateStatus(Long id, UpdateStatusRequest request);

    void deleteStudent(Long id);
}
```

### 4.2 Service Implementation

```java
package com.school.sms.student.application.service;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.domain.service.StudentIdGenerator;
import com.school.sms.student.application.dto.request.*;
import com.school.sms.student.application.dto.response.*;
import com.school.sms.student.application.mapper.StudentMapper;
import com.school.sms.student.infrastructure.rules.RulesService;
import com.school.sms.student.infrastructure.cache.StudentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Student service implementation
 * Orchestrates domain logic, validation, and caching
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentIdGenerator studentIdGenerator;
    private final RulesService rulesService;
    private final StudentCacheService cacheService;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating student: {} {}", request.getFirstName(), request.getLastName());

        // Validate business rules via Drools
        rulesService.validateStudentRegistration(request);

        // Check mobile uniqueness
        Mobile mobile = Mobile.of(request.getMobile());
        if (studentRepository.existsByMobile(mobile)) {
            throw new DuplicateMobileException(
                "Student with mobile " + mobile + " already exists"
            );
        }

        // Generate unique StudentId
        StudentId studentId = studentIdGenerator.generateNext();

        // Create domain entity
        Student student = Student.register(
            studentId,
            request.getFirstName(),
            request.getLastName(),
            request.getAddress(),
            mobile,
            request.getDateOfBirth(),
            request.getFatherName(),
            request.getMotherName(),
            request.getIdentificationMark(),
            request.getEmail(),
            request.getAadhaarNumber(),
            "system" // TODO: Get from SecurityContext in Phase 2
        );

        // Persist
        Student savedStudent = studentRepository.save(student);

        // Cache the student
        cacheService.cacheStudent(savedStudent);

        log.info("Student created successfully: {}", studentId);

        return studentMapper.toResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        log.debug("Fetching student by ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getStudentByStudentId(String studentIdValue) {
        log.debug("Fetching student by StudentId: {}", studentIdValue);

        // Check cache first
        Student cachedStudent = cacheService.getStudent(studentIdValue);
        if (cachedStudent != null) {
            log.debug("Cache hit for StudentId: {}", studentIdValue);
            return studentMapper.toResponse(cachedStudent);
        }

        // Cache miss - fetch from database
        StudentId studentId = StudentId.of(studentIdValue);
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(
                "Student with StudentId " + studentIdValue + " not found"
            ));

        // Cache for future requests
        cacheService.cacheStudent(student);

        return studentMapper.toResponse(student);
    }

    @Override
    public StudentPageResponse searchStudents(StudentSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching students with criteria: {}", criteria);

        Page<Student> studentPage;

        if (criteria.getLastName() != null) {
            studentPage = studentRepository.findByLastNameContaining(
                criteria.getLastName(), pageable
            );
        } else if (criteria.getGuardianName() != null) {
            studentPage = studentRepository.findByFatherNameContaining(
                criteria.getGuardianName(), pageable
            );
        } else if (criteria.getStatus() != null) {
            studentPage = studentRepository.findByStatus(criteria.getStatus(), pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        return studentMapper.toPageResponse(studentPage);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        log.info("Updating student ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        // Check mobile uniqueness (if changed)
        Mobile newMobile = Mobile.of(request.getMobile());
        if (!student.getMobile().equals(newMobile) &&
            studentRepository.existsByMobile(newMobile)) {
            throw new DuplicateMobileException(
                "Student with mobile " + newMobile + " already exists"
            );
        }

        // Update editable fields
        student.updateProfile(
            request.getFirstName(),
            request.getLastName(),
            newMobile,
            "system" // TODO: Get from SecurityContext
        );

        Student updatedStudent = studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(updatedStudent.getStudentId().getValue());

        log.info("Student updated successfully: {}", student.getStudentId());

        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateStatus(Long id, UpdateStatusRequest request) {
        log.info("Updating status for student ID: {} to {}", id, request.getStatus());

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        StudentStatus newStatus = StudentStatus.valueOf(request.getStatus());
        student.changeStatus(newStatus, "system");

        Student updatedStudent = studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(updatedStudent.getStudentId().getValue());

        log.info("Student status updated: {} -> {}", student.getStudentId(), newStatus);

        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Soft deleting student ID: {}", id);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        // Soft delete via status change
        student.changeStatus(StudentStatus.INACTIVE, "system");
        studentRepository.save(student);

        // Invalidate cache
        cacheService.evictStudent(student.getStudentId().getValue());

        log.info("Student soft deleted: {}", student.getStudentId());
    }
}
```

### 4.3 DTOs

**CreateStudentRequest:**
```java
package com.school.sms.student.application.dto.request;

import com.school.sms.student.application.validation.AgeRange;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100)
    private String lastName;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500)
    private String address;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile format")
    private String mobile;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @AgeRange(min = 3, max = 18)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Father name/Guardian is required")
    @Size(max = 100)
    private String fatherName;

    @Size(max = 100)
    private String motherName;

    @Size(max = 200)
    private String identificationMark;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;
}
```

**StudentResponse:**
```java
package com.school.sms.student.application.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentResponse {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String address;
    private String mobile;
    private LocalDate dateOfBirth;
    private Integer age;
    private String fatherName;
    private String motherName;
    private String identificationMark;
    private String email;
    private String aadhaarNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}
```

### 4.4 MapStruct Mapper

```java
package com.school.sms.student.application.mapper;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.application.dto.response.StudentResponse;
import com.school.sms.student.application.dto.response.StudentPageResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "mobile", source = "mobile.number")
    @Mapping(target = "age", expression = "java(student.getCurrentAge())")
    @Mapping(target = "status", source = "status")
    StudentResponse toResponse(Student student);

    default StudentPageResponse toPageResponse(Page<Student> page) {
        return StudentPageResponse.builder()
            .content(page.getContent().stream()
                .map(this::toResponse)
                .toList())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .first(page.isFirst())
            .build();
    }
}
```

## 5. Infrastructure Layer Implementation

### 5.1 JPA Entity

```java
package com.school.sms.student.infrastructure.persistence.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_last_name", columnList = "last_name"),
    @Index(name = "idx_students_status", columnList = "status"),
    @Index(name = "idx_students_father_name", columnList = "father_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "mobile", unique = true, nullable = false, length = 15)
    private String mobile;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "father_name", nullable = false, length = 100)
    private String fatherName;

    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "identification_mark", length = 200)
    private String identificationMark;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "aadhaar_number", length = 12)
    private String aadhaarNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 5.2 JPA Repository

```java
package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface JpaStudentRepository extends JpaRepository<StudentJpaEntity, Long> {

    Optional<StudentJpaEntity> findByStudentId(String studentId);

    Optional<StudentJpaEntity> findByMobile(String mobile);

    Page<StudentJpaEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<StudentJpaEntity> findByFatherNameContainingIgnoreCase(String fatherName, Pageable pageable);

    Page<StudentJpaEntity> findByStatus(String status, Pageable pageable);

    boolean existsByMobile(String mobile);

    @Query("SELECT COUNT(s) FROM StudentJpaEntity s WHERE s.studentId LIKE :prefix%")
    long countByStudentIdStartingWith(@Param("prefix") String prefix);
}
```

### 5.3 Repository Adapter

```java
package com.school.sms.student.infrastructure.persistence.adapter;

import com.school.sms.student.domain.model.*;
import com.school.sms.student.domain.repository.StudentRepository;
import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import com.school.sms.student.infrastructure.persistence.repository.JpaStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Adapter implementing domain repository interface using JPA
 */
@Component
@RequiredArgsConstructor
public class StudentRepositoryAdapter implements StudentRepository {

    private final JpaStudentRepository jpaRepository;

    @Override
    public Student save(Student student) {
        StudentJpaEntity entity = toJpaEntity(student);
        StudentJpaEntity saved = jpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        return jpaRepository.findByStudentId(studentId.getValue())
            .map(this::toDomainModel);
    }

    @Override
    public Optional<Student> findByMobile(Mobile mobile) {
        return jpaRepository.findByMobile(mobile.getNumber())
            .map(this::toDomainModel);
    }

    @Override
    public Page<Student> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomainModel);
    }

    @Override
    public boolean existsByMobile(Mobile mobile) {
        return jpaRepository.existsByMobile(mobile.getNumber());
    }

    @Override
    public long countByStudentIdStartingWith(String prefix) {
        return jpaRepository.countByStudentIdStartingWith(prefix);
    }

    // Mapping methods
    private StudentJpaEntity toJpaEntity(Student student) {
        return StudentJpaEntity.builder()
            .id(student.getId())
            .studentId(student.getStudentId().getValue())
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .address(student.getAddress())
            .mobile(student.getMobile().getNumber())
            .dateOfBirth(student.getDateOfBirth())
            .fatherName(student.getFatherName())
            .motherName(student.getMotherName())
            .identificationMark(student.getIdentificationMark())
            .email(student.getEmail())
            .aadhaarNumber(student.getAadhaarNumber())
            .status(student.getStatus().name())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .createdBy(student.getCreatedBy())
            .updatedBy(student.getUpdatedBy())
            .version(student.getVersion())
            .build();
    }

    private Student toDomainModel(StudentJpaEntity entity) {
        return Student.builder()
            .id(entity.getId())
            .studentId(StudentId.of(entity.getStudentId()))
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .address(entity.getAddress())
            .mobile(Mobile.of(entity.getMobile()))
            .dateOfBirth(entity.getDateOfBirth())
            .fatherName(entity.getFatherName())
            .motherName(entity.getMotherName())
            .identificationMark(entity.getIdentificationMark())
            .email(entity.getEmail())
            .aadhaarNumber(entity.getAadhaarNumber())
            .status(StudentStatus.valueOf(entity.getStatus()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .createdBy(entity.getCreatedBy())
            .updatedBy(entity.getUpdatedBy())
            .version(entity.getVersion())
            .build();
    }
}
```

### 5.4 Drools Configuration

**DroolsConfig:**
```java
package com.school.sms.student.infrastructure.rules;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/student-rules.drl";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(kieServices.getResources()
            .newClassPathResource(RULES_PATH));

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

**student-rules.drl:**
```drools
package com.school.sms.student.rules;

import com.school.sms.student.application.dto.request.CreateStudentRequest;
import java.time.LocalDate;
import java.time.Period;

global java.util.List validationErrors;

// Rule: Age must be between 3 and 18 years
rule "Validate Student Age Range"
    when
        $request : CreateStudentRequest()
        $age : Integer() from Period.between($request.getDateOfBirth(), LocalDate.now()).getYears()
        eval($age < 3 || $age > 18)
    then
        validationErrors.add("Student age must be between 3 and 18 years. Current age: " + $age);
end

// Rule: Mobile number must not be empty
rule "Validate Mobile Number"
    when
        $request : CreateStudentRequest(mobile == null || mobile.trim().isEmpty())
    then
        validationErrors.add("Mobile number is required");
end

// Future Rule (Phase 2): Class Capacity Validation
// DESIGN ONLY - NOT IMPLEMENTED IN PHASE 1
rule "Validate Class Capacity"
    salience -10
    when
        // This rule is designed but not activated in Phase 1
        // Will be implemented when class enrollment feature is added
    then
        // Check if class has reached maximum capacity
        // validationErrors.add("Class has reached maximum capacity");
end
```

**RulesService:**
```java
package com.school.sms.student.infrastructure.rules;

import com.school.sms.student.application.dto.request.CreateStudentRequest;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RulesService {

    private final KieSession kieSession;

    public void validateStudentRegistration(CreateStudentRequest request) {
        List<String> validationErrors = new ArrayList<>();

        kieSession.setGlobal("validationErrors", validationErrors);
        kieSession.insert(request);
        kieSession.fireAllRules();

        if (!validationErrors.isEmpty()) {
            throw new BusinessRuleViolationException(
                "Student registration validation failed: " + String.join(", ", validationErrors)
            );
        }
    }
}
```

### 5.5 Redis Cache Configuration

**RedisCacheConfig:**
```java
package com.school.sms.student.infrastructure.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

**StudentCacheService:**
```java
package com.school.sms.student.infrastructure.cache;

import com.school.sms.student.domain.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentCacheService {

    private static final String CACHE_NAME = "students";

    @Cacheable(value = CACHE_NAME, key = "#studentId")
    public Student getStudent(String studentId) {
        return null; // Cache miss - will be loaded from DB
    }

    public void cacheStudent(Student student) {
        // Cached via @Cacheable in service methods
    }

    @CacheEvict(value = CACHE_NAME, key = "#studentId")
    public void evictStudent(String studentId) {
        // Evict cache entry
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void evictAll() {
        // Clear all cache entries
    }
}
```

## 6. Presentation Layer Implementation

### 6.1 REST Controller

```java
package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.dto.request.*;
import com.school.sms.student.application.dto.response.*;
import com.school.sms.student.application.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for student CRUD operations")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create new student", description = "Registers a new student with auto-generated StudentID")
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {
        log.info("POST /api/v1/students - Creating student: {} {}",
            request.getFirstName(), request.getLastName());

        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List students", description = "Retrieve paginated list of students with optional filters")
    public ResponseEntity<StudentPageResponse> listStudents(
            @ModelAttribute StudentSearchCriteria criteria,
            Pageable pageable) {
        log.info("GET /api/v1/students - Searching students: {}", criteria);

        StudentPageResponse response = studentService.searchStudents(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve student details by internal ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        log.info("GET /api/v1/students/{} - Fetching student", id);

        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-id/{studentId}")
    @Operation(summary = "Get student by StudentID", description = "Retrieve student details by system-generated StudentID")
    public ResponseEntity<StudentResponse> getStudentByStudentId(@PathVariable String studentId) {
        log.info("GET /api/v1/students/student-id/{} - Fetching student", studentId);

        StudentResponse response = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Update editable student fields (firstName, lastName, mobile, status)")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        log.info("PUT /api/v1/students/{} - Updating student", id);

        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update student status", description = "Update only the student status")
    public ResponseEntity<StudentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("PATCH /api/v1/students/{}/status - Updating status to {}", id, request.getStatus());

        StudentResponse response = studentService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Soft delete student by setting status to Inactive")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/v1/students/{} - Soft deleting student", id);

        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 6.2 Global Exception Handler

```java
package com.school.sms.student.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFound(StudentNotFoundException ex, WebRequest request) {
        log.error("Student not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://api.sms.example.com/problems/resource-not-found"));
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", getCorrelationId(request));

        return problemDetail;
    }

    @ExceptionHandler(DuplicateMobileException.class)
    public ProblemDetail handleDuplicateMobile(DuplicateMobileException ex, WebRequest request) {
        log.error("Duplicate mobile number: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create("https://api.sms.example.com/problems/duplicate-resource"));
        problemDetail.setTitle("Duplicate Resource");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", getCorrelationId(request));

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation failed: {}", ex.getMessage());

        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ))
            .collect(Collectors.toList());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Request validation failed for one or more fields");
        problemDetail.setType(URI.create("https://api.sms.example.com/problems/validation-error"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", getCorrelationId(request));
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLocking(OptimisticLockingFailureException ex, WebRequest request) {
        log.error("Optimistic locking failure: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Student record was modified by another user. Please refresh and try again.");
        problemDetail.setType(URI.create("https://api.sms.example.com/problems/optimistic-lock-error"));
        problemDetail.setTitle("Concurrent Modification Detected");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", getCorrelationId(request));

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later.");
        problemDetail.setType(URI.create("https://api.sms.example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("correlationId", getCorrelationId(request));

        return problemDetail;
    }

    private String getCorrelationId(WebRequest request) {
        String correlationId = request.getHeader("X-Correlation-ID");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

    record FieldError(String field, String message, Object rejectedValue) {}
}
```

## 7. Performance Optimization

### 7.1 N+1 Query Prevention

```java
// Use @EntityGraph to fetch associations eagerly
@EntityGraph(attributePaths = {"enrollments"})
Optional<StudentJpaEntity> findById(Long id);

// Or use JOIN FETCH in JPQL
@Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.id = :id")
Optional<Student> findByIdWithEnrollments(@Param("id") Long id);
```

### 7.2 HikariCP Configuration

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sms_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: SMS-HikariPool
```

## 8. Observability

### 8.1 Custom Metrics

```java
@Component
public class StudentMetrics {

    private final Counter studentsRegistered;
    private final Gauge activeStudents;

    public StudentMetrics(MeterRegistry registry) {
        this.studentsRegistered = Counter.builder("students.registered.total")
            .description("Total number of students registered")
            .register(registry);

        this.activeStudents = Gauge.builder("students.active.count", this::getActiveStudentCount)
            .description("Number of active students")
            .register(registry);
    }

    public void incrementRegistered() {
        studentsRegistered.increment();
    }

    private int getActiveStudentCount() {
        // Query database for active count
        return 0;
    }
}
```

## 9. Summary

This backend implementation guide provides:

- **DDD Layered Architecture** with clear separation of concerns
- **Rich Domain Models** with business logic encapsulation
- **Repository Pattern** with JPA adapter
- **Drools Integration** for business rules (with Class Capacity design)
- **Redis Caching** for performance
- **MapStruct** for clean DTO mapping
- **Comprehensive Error Handling** with RFC 7807
- **Performance Optimizations** (N+1 prevention, HikariCP)
- **Observability** with metrics and logging

Backend developers can follow these patterns to build a production-ready, maintainable, and scalable system.
