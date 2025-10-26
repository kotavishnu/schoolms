# CLAUDE-BACKEND.md

**Tier 2: Backend Component Agent**

This agent provides comprehensive guidance for building the Spring Boot-based backend of the School Management System. For feature-specific details, refer to Tier 3 feature agents in `docs/features/`.

---

## Agent Role

**Purpose**: Guide implementation of Spring Boot 3.5 backend architecture, layered design, JPA entities, Drools rules engine, and RESTful APIs.

**Scope**: Application-wide backend concerns, not feature-specific logic (see Tier 3 agents).

---

## Tech Stack & Versions

```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.5.0</spring-boot.version>
    <postgresql.version>42.7.1</postgresql.version>
    <drools.version>9.44.0.Final</drools.version>
    <lombok.version>1.18.30</lombok.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>
```

---

## Quick Start Commands

```bash
cd backend

# Build & Run
mvn clean install              # Full build with tests
mvn spring-boot:run            # Run server (http://localhost:8080)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Testing
mvn test                       # All tests
mvn test -Dtest=StudentServiceTest  # Specific test class
mvn test -Dtest=StudentServiceTest#shouldCreateStudent  # Specific test method
mvn verify                     # Integration tests
mvn clean test jacoco:report   # Coverage report (target/site/jacoco/index.html)

# Database
mvn hibernate5:schema-export   # Generate schema DDL
mvn flyway:migrate             # Run migrations (if using Flyway)

# Packaging
mvn clean package              # Create JAR (target/*.jar)
mvn clean package -DskipTests  # Package without running tests

# Code Quality
mvn checkstyle:check           # Code style validation
mvn spotbugs:check             # Bug detection
```

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/school/management/
│   │   │   ├── SchoolManagementApplication.java  # Spring Boot entry point
│   │   │   │
│   │   │   ├── controller/              # REST API Layer
│   │   │   │   ├── StudentController.java
│   │   │   │   ├── ClassController.java
│   │   │   │   ├── FeeMasterController.java
│   │   │   │   ├── FeeReceiptController.java
│   │   │   │   └── SchoolConfigController.java
│   │   │   │
│   │   │   ├── service/                 # Business Logic Layer
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── ClassService.java
│   │   │   │   ├── FeeCalculationService.java
│   │   │   │   ├── ReceiptGenerationService.java
│   │   │   │   └── impl/                # Service implementations
│   │   │   │
│   │   │   ├── repository/              # Data Access Layer
│   │   │   │   ├── StudentRepository.java
│   │   │   │   ├── ClassRepository.java
│   │   │   │   ├── FeeMasterRepository.java
│   │   │   │   └── FeeJournalRepository.java
│   │   │   │
│   │   │   ├── model/                   # JPA Entities
│   │   │   │   ├── Student.java
│   │   │   │   ├── SchoolClass.java
│   │   │   │   ├── FeeMaster.java
│   │   │   │   ├── FeeJournal.java
│   │   │   │   ├── FeeReceipt.java
│   │   │   │   └── SchoolConfig.java
│   │   │   │
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── StudentRequestDTO.java
│   │   │   │   │   ├── FeePaymentRequestDTO.java
│   │   │   │   │   └── ClassRequestDTO.java
│   │   │   │   └── response/
│   │   │   │       ├── StudentResponseDTO.java
│   │   │   │       ├── FeeReceiptResponseDTO.java
│   │   │   │       └── ApiResponse.java
│   │   │   │
│   │   │   ├── mapper/                  # Entity-DTO Mappers
│   │   │   │   ├── StudentMapper.java
│   │   │   │   ├── FeeMapper.java
│   │   │   │   └── ClassMapper.java
│   │   │   │
│   │   │   ├── config/                  # Configuration Classes
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   ├── DroolsConfig.java
│   │   │   │   ├── WebConfig.java       # CORS, etc.
│   │   │   │   └── SecurityConfig.java  # Spring Security (future)
│   │   │   │
│   │   │   ├── exception/               # Error Handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── ErrorResponse.java
│   │   │   │
│   │   │   ├── util/                    # Utilities
│   │   │   │   ├── DateUtils.java
│   │   │   │   ├── FeeCalculator.java
│   │   │   │   └── Constants.java
│   │   │   │
│   │   │   └── seed/                    # Data Initialization
│   │   │       └── DataInitializer.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── rules/                    # Drools DRL files
│   │       │   ├── fee-calculation.drl
│   │       │   └── fee-validation.drl
│   │       └── db/
│   │           └── migration/            # Flyway migrations (optional)
│   │
│   └── test/
│       └── java/com/school/management/
│           ├── controller/               # API Tests
│           ├── service/                  # Unit Tests
│           ├── repository/               # Integration Tests
│           └── integration/              # End-to-end Tests
│
├── pom.xml
├── .mvn/
└── README.md
```

---

## Architecture Pattern: Layered

### Request Flow

```
HTTP Request
    ↓
Controller Layer (@RestController)
    - Input validation (@Valid)
    - Request DTO → Response DTO
    ↓
Service Layer (@Service, @Transactional)
    - Business logic
    - Drools rule execution
    - Entity ↔ DTO mapping
    ↓
Repository Layer (JpaRepository)
    - Database queries
    - JPA entity management
    ↓
Database (PostgreSQL)
```

### Layer Responsibilities

| Layer | Responsibility | Annotations |
|-------|---------------|-------------|
| **Controller** | HTTP handling, validation, DTO conversion | `@RestController`, `@RequestMapping`, `@Valid` |
| **Service** | Business logic, transactions, orchestration | `@Service`, `@Transactional` |
| **Repository** | Data access, queries | `@Repository` (auto-applied by JPA) |
| **Model** | Domain entities, JPA mappings | `@Entity`, `@Table`, `@Id` |

---

## Configuration

### pom.xml (Key Dependencies)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
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
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Drools Rules Engine -->
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-core</artifactId>
        <version>${drools.version}</version>
    </dependency>
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-compiler</artifactId>
        <version>${drools.version}</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### application.properties

```properties
# Application
spring.application.name=School Management System
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/school_management_db
spring.datasource.username=postgres
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Logging
logging.level.com.school.management=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Jackson
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=Asia/Kolkata
```

### application-dev.properties

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.com.school.management=TRACE
```

---

## Core Implementation Patterns

### 1. Entity Design (JPA)

```java
package com.school.management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(length = 50)
    private String caste;
    
    @Column(nullable = false, length = 15, unique = true)
    private String mobile;
    
    @Column(length = 50)
    private String religion;
    
    @Column(length = 200)
    private String molesOnBody;
    
    @Column(nullable = false, length = 100)
    private String motherName;
    
    @Column(nullable = false, length = 100)
    private String fatherName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @Column(nullable = false)
    private LocalDate enrollmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Computed field
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Transient
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}

enum StudentStatus {
    ACTIVE, INACTIVE, GRADUATED, TRANSFERRED
}
```

### 2. Repository Layer

```java
package com.school.management.repository;

import com.school.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // Derived query methods
    List<Student> findBySchoolClassId(Long classId);
    
    Optional<Student> findByMobile(String mobile);
    
    List<Student> findByStatus(StudentStatus status);
    
    // Custom JPQL query
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchByName(@Param("query") String query);
    
    // Native SQL query
    @Query(value = "SELECT * FROM students WHERE class_id = :classId AND status = 'ACTIVE' " +
                   "ORDER BY first_name", nativeQuery = true)
    List<Student> findActiveStudentsByClass(@Param("classId") Long classId);
    
    // Exists check
    boolean existsByMobile(String mobile);
    
    // Count
    long countBySchoolClassId(Long classId);
}
```

### 3. Service Layer with TDD

```java
package com.school.management.service;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.StudentMapper;
import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.repository.ClassRepository;
import com.school.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final StudentMapper studentMapper;
    
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        log.info("Creating student: {} {}", requestDTO.getFirstName(), requestDTO.getLastName());
        
        // Validation
        if (studentRepository.existsByMobile(requestDTO.getMobile())) {
            throw new ValidationException("Student with mobile " + requestDTO.getMobile() + " already exists");
        }
        
        // Fetch related entities
        SchoolClass schoolClass = classRepository.findById(requestDTO.getClassId())
            .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + requestDTO.getClassId()));
        
        // Check capacity
        long currentStudents = studentRepository.countBySchoolClassId(requestDTO.getClassId());
        if (currentStudents >= schoolClass.getCapacity()) {
            throw new ValidationException("Class capacity exceeded");
        }
        
        // Map and save
        Student student = studentMapper.toEntity(requestDTO);
        student.setSchoolClass(schoolClass);
        student.setEnrollmentDate(LocalDate.now());
        
        Student savedStudent = studentRepository.save(student);
        log.info("Student created with id: {}", savedStudent.getId());
        
        return studentMapper.toResponseDTO(savedStudent);
    }
    
    public StudentResponseDTO getStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        return studentMapper.toResponseDTO(student);
    }
    
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
            .stream()
            .map(studentMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<StudentResponseDTO> getStudentsByClass(Long classId) {
        List<Student> students = studentRepository.findBySchoolClassId(classId);
        return students.stream()
            .map(studentMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // Validate mobile uniqueness (excluding current student)
        studentRepository.findByMobile(requestDTO.getMobile())
            .filter(s -> !s.getId().equals(id))
            .ifPresent(s -> {
                throw new ValidationException("Mobile number already in use");
            });
        
        // Update fields
        studentMapper.updateEntityFromDTO(requestDTO, student);
        
        Student updatedStudent = studentRepository.save(student);
        log.info("Student updated with id: {}", updatedStudent.getId());
        
        return studentMapper.toResponseDTO(updatedStudent);
    }
    
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        
        studentRepository.deleteById(id);
        log.info("Student deleted with id: {}", id);
    }
    
    public List<StudentResponseDTO> searchStudents(String query) {
        List<Student> students = studentRepository.searchByName(query);
        return students.stream()
            .map(studentMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
}
```

### 4. Controller Layer

```java
package com.school.management.controller;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    
    private final StudentService studentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        
        log.info("POST /api/students - Creating student");
        StudentResponseDTO student = studentService.createStudent(requestDTO);
        
        ApiResponse<StudentResponseDTO> response = ApiResponse.<StudentResponseDTO>builder()
            .success(true)
            .message("Student created successfully")
            .data(student)
            .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudent(@PathVariable Long id) {
        log.info("GET /api/students/{}", id);
        StudentResponseDTO student = studentService.getStudent(id);
        
        ApiResponse<StudentResponseDTO> response = ApiResponse.<StudentResponseDTO>builder()
            .success(true)
            .data(student)
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents(
            @RequestParam(required = false) Long classId) {
        
        log.info("GET /api/students - classId: {}", classId);
        List<StudentResponseDTO> students = classId != null 
            ? studentService.getStudentsByClass(classId)
            : studentService.getAllStudents();
        
        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.<List<StudentResponseDTO>>builder()
            .success(true)
            .data(students)
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> searchStudents(
            @RequestParam String q) {
        
        log.info("GET /api/students/search?q={}", q);
        List<StudentResponseDTO> students = studentService.searchStudents(q);
        
        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.<List<StudentResponseDTO>>builder()
            .success(true)
            .data(students)
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        
        log.info("PUT /api/students/{}", id);
        StudentResponseDTO student = studentService.updateStudent(id, requestDTO);
        
        ApiResponse<StudentResponseDTO> response = ApiResponse.<StudentResponseDTO>builder()
            .success(true)
            .message("Student updated successfully")
            .data(student)
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/students/{}", id);
        studentService.deleteStudent(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .success(true)
            .message("Student deleted successfully")
            .build();
        
        return ResponseEntity.ok(response);
    }
}
```

### 5. DTO Pattern

```java
package com.school.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @Size(max = 50)
    private String caste;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    
    @Size(max = 50)
    private String religion;
    
    @Size(max = 200)
    private String molesOnBody;
    
    @NotBlank(message = "Mother's name is required")
    @Size(max = 100)
    private String motherName;
    
    @NotBlank(message = "Father's name is required")
    @Size(max = 100)
    private String fatherName;
    
    @NotNull(message = "Class ID is required")
    @Positive(message = "Class ID must be positive")
    private Long classId;
}
```

```java
package com.school.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    
    @Builder.Default
    private String timestamp = java.time.LocalDateTime.now().toString();
}
```

### 6. Global Exception Handler

```java
package com.school.management.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message("Input validation failed")
            .fieldErrors(errors)
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## Drools Rules Engine

### Configuration

```java
package com.school.management.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
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
        
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + "fee-calculation.drl"));
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}
```

### Rule File Example

```drl
package com.school.management.rules

import com.school.management.model.Student;
import com.school.management.dto.FeeCalculationDTO;

rule "Calculate Base Fee for Classes 1-5"
when
    $student : Student(schoolClass.classNumber >= 1 && schoolClass.classNumber <= 5)
    $fee : FeeCalculationDTO(studentId == $student.id)
then
    $fee.setBaseFee(1000.0);
end

rule "Calculate Base Fee for Classes 6-10"
when
    $student : Student(schoolClass.classNumber >= 6 && schoolClass.classNumber <= 10)
    $fee : FeeCalculationDTO(studentId == $student.id)
then
    $fee.setBaseFee(1500.0);
end

rule "Add Library Fee"
when
    $fee : FeeCalculationDTO()
then
    $fee.setLibraryFee(200.0);
end

rule "Add Computer Fee"
when
    $fee : FeeCalculationDTO()
then
    $fee.setComputerFee(300.0);
end

rule "Add Special Fee for First Month"
when
    $fee : FeeCalculationDTO(isFirstMonth == true)
then
    $fee.setSpecialFee(500.0);
end
```

---

## Testing Patterns

### Unit Test (Service Layer)

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private ClassRepository classRepository;
    
    @Mock
    private StudentMapper studentMapper;
    
    @InjectMocks
    private StudentService studentService;
    
    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudent() {
        // Given
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setMobile("1234567890");
        requestDTO.setClassId(1L);
        
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setCapacity(50);
        
        Student student = new Student();
        student.setId(1L);
        
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.countBySchoolClassId(1L)).thenReturn(20L);
        when(studentMapper.toEntity(any())).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(studentMapper.toResponseDTO(any())).thenReturn(new StudentResponseDTO());
        
        // When
        StudentResponseDTO result = studentService.createStudent(requestDTO);
        
        // Then
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @DisplayName("Should throw exception when mobile exists")
    void shouldThrowExceptionWhenMobileExists() {
        // Given
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setMobile("1234567890");
        
        when(studentRepository.existsByMobile("1234567890")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            studentService.createStudent(requestDTO);
        });
    }
}
```

---

## Database Seeding

```java
package com.school.management.seed;

import com.school.management.model.SchoolClass;
import com.school.management.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final ClassRepository classRepository;
    
    @Override
    public void run(String... args) {
        if (classRepository.count() == 0) {
            log.info("Seeding classes...");
            
            for (int i = 1; i <= 10; i++) {
                SchoolClass schoolClass = SchoolClass.builder()
                    .classNumber(i)
                    .section("A")
                    .academicYear("2024-2025")
                    .capacity(50)
                    .build();
                
                classRepository.save(schoolClass);
            }
            
            log.info("Classes seeded successfully");
        }
    }
}
```

---

## Best Practices

### Code Organization
- ✅ Use constructor injection with Lombok `@RequiredArgsConstructor`
- ✅ Mark services with `@Transactional(readOnly = true)` by default
- ✅ Use `@Transactional` on write methods
- ✅ Separate DTOs for request and response
- ✅ Use MapStruct for entity-DTO mapping
- ❌ Don't inject `@Autowired` fields (use constructor injection)
- ❌ Don't put business logic in controllers
- ❌ Don't expose entities directly in API

### Performance
- Use `@ManyToOne(fetch = FetchType.LAZY)` for associations
- Enable batch inserts/updates in Hibernate
- Use DTOs with only required fields
- Add database indexes on frequently queried columns
- Use pagination for large result sets

---

## Next Steps

- For specific feature implementation, load **Tier 3 feature agents**
- For frontend integration, see **CLAUDE-FRONTEND.md**
- For testing strategies, see **CLAUDE-TESTING.md**
- For Git workflow, see **CLAUDE-GIT.md**

---

**Agent Directive**: This is a Tier 2 component agent. Combine with Tier 3 feature agents for complete implementation guidance.
