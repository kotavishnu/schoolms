# Backend Implementation Guide - School Management System

**Version:** 1.0
**Date:** January 22, 2026
**Status:** Active - MANDATORY FOR BACKEND DEVELOPER AGENT
**Phase:** 1 - Student Management & Configuration

---

## Table of Contents

1. [Mandatory Constraints](#1-mandatory-constraints)
2. [Layer Architecture Patterns](#2-layer-architecture-patterns)
3. [Domain Layer Implementation](#3-domain-layer-implementation)
4. [Application Layer Implementation](#4-application-layer-implementation)
5. [Infrastructure Layer Implementation](#5-infrastructure-layer-implementation)
6. [Performance Optimization](#6-performance-optimization)
7. [Monitoring & Observability](#7-monitoring--observability)
8. [Global Directives Compliance](#8-global-directives-compliance)

---

## 1. Mandatory Constraints

### 1.1 Critical Rules (STRICT ENFORCEMENT)

**RULE 1: API Contract Adherence**
- Implement endpoints EXACTLY as defined in `sms_api_specification.yaml`
- Request/Response types MUST match specification 100%
- Field names, data types, HTTP status codes are NON-NEGOTIABLE

**RULE 2: Database-per-Service Isolation**
- Student Service connects ONLY to `student_db` (port 5433)
- Configuration Service connects ONLY to `config_db` (port 5434)
- ZERO database cross-access permitted
- Use separate application.yml profiles

**RULE 3: DTO Boundary Enforcement**
- NEVER expose JPA entities in REST APIs
- ALL external communication uses DTOs (Request/Response objects)
- MapStruct MUST translate Entity ↔ DTO at service layer

**RULE 4: Optimistic Locking**
- ALL entities MUST have `@Version` field
- ALL update operations MUST include version check
- Handle `OptimisticLockException` with HTTP 409 Conflict

**RULE 5: Spring Boot Version Compatibility (D-001)**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>  <!-- NOT 3.5.0, NOT 3.4.x -->
</parent>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>  <!-- NOT 2.7.0 -->
</dependency>
```

### 1.2 Technology Versions (EXACT)

| Technology | Version | Notes |
|------------|---------|-------|
| Java | 21 LTS | Use records, pattern matching |
| Spring Boot | 3.3.5 | CRITICAL: See D-001 |
| Spring Data JPA | 3.3.x | Bundled with Boot |
| PostgreSQL Driver | 42.7.x | Latest stable |
| Drools | 9.44.0.Final | Business rules engine |
| MapStruct | 1.5.5.Final | DTO mapping |
| Lombok | 1.18.30 | Boilerplate reduction |
| SpringDoc OpenAPI | 2.6.0 | CRITICAL: See D-001 |
| Flyway | 9.x | Database migrations |

---

## 2. Layer Architecture Patterns

### 2.1 Package Structure (MANDATORY)

```
com.school.student
├── config                      # Spring configurations
│   ├── DroolsConfig.java
│   ├── CacheConfig.java
│   └── CorsConfig.java
├── controller                  # Presentation Layer
│   ├── StudentController.java
│   └── EnrollmentController.java
├── dto                         # Data Transfer Objects
│   ├── request
│   │   ├── StudentRequest.java
│   │   └── StudentUpdateRequest.java
│   └── response
│       ├── StudentResponse.java
│       └── ErrorResponse.java
├── domain                      # Domain Layer
│   ├── entity
│   │   ├── Student.java
│   │   └── Enrollment.java
│   ├── valueobject
│   │   ├── Mobile.java
│   │   └── Email.java
│   └── validation
│       ├── StudentValidationRequest.java
│       └── ValidationResult.java
├── exception                   # Custom exceptions
│   ├── StudentNotFoundException.java
│   ├── ValidationException.java
│   └── GlobalExceptionHandler.java
├── mapper                      # MapStruct interfaces
│   └── StudentMapper.java
├── repository                  # Infrastructure Layer
│   └── StudentRepository.java
├── service                     # Application Layer
│   ├── StudentService.java
│   └── DroolsValidationService.java
└── util                        # Utilities
    └── StudentIdGenerator.java
```

### 2.2 Layer Dependencies (STRICT)

```
Controller → Service → Repository
    ↓          ↓
   DTO      Entity
    ↓          ↓
 Mapper ← ← ← ←
```

**Rules:**
- Controllers depend on Services (NOT Repositories directly)
- Services depend on Repositories and Mappers
- Domain entities NEVER depend on DTOs
- Infrastructure depends on Domain (Dependency Inversion)

---

## 3. Domain Layer Implementation

### 3.1 Entity Pattern (JPA + Lombok + Builder)

**Student.java:**
```java
package com.school.student.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // Optimistic Locking
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
        // Format: STD-YYYYMMDD-NNNN
        // Implementation uses database sequence or timestamp-based logic
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

    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }
}

public enum StudentStatus {
    ACTIVE,
    INACTIVE
}
```

### 3.2 Value Objects (Immutability)

**Mobile.java (Example - Optional for Phase 1):**
```java
package com.school.student.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor  // JPA requirement
public class Mobile {
    private String value;

    public Mobile(String value) {
        if (!value.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile must be 10 digits");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mobile mobile)) return false;
        return Objects.equals(value, mobile.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

---

## 4. Application Layer Implementation

### 4.1 Service Pattern (Transactional + CQRS-light)

**StudentService.java:**
```java
package com.school.student.service;

import com.school.student.domain.entity.Student;
import com.school.student.domain.entity.StudentStatus;
import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import com.school.student.dto.request.StudentRequest;
import com.school.student.dto.request.StudentUpdateRequest;
import com.school.student.dto.response.StudentResponse;
import com.school.student.exception.StudentNotFoundException;
import com.school.student.exception.ValidationException;
import com.school.student.mapper.StudentMapper;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        log.info("Registering student: {} {}", request.getFirstName(), request.getLastName());

        // 1. Validate using Drools
        ValidationResult validationResult = validationService.validateStudent(
            buildValidationRequest(request, null)
        );

        if (!validationResult.isValid()) {
            log.warn("Validation failed: {}", validationResult.getErrors());
            throw new ValidationException("Student validation failed", validationResult.getErrors());
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

    // READ (with caching)
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#studentId")
    public StudentResponse getStudentById(String studentId) {
        log.debug("Fetching student: {}", studentId);

        return repository.findByStudentId(studentId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    // UPDATE (with cache eviction)
    @CacheEvict(value = {"students", "studentSearchResults"}, key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        log.info("Updating student: {}", studentId);

        // 1. Fetch existing
        Student student = repository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // 2. Validate editable fields
        ValidationResult validationResult = validationService.validateUpdate(
            buildUpdateValidationRequest(request, studentId)
        );

        if (!validationResult.isValid()) {
            throw new ValidationException("Update validation failed", validationResult.getErrors());
        }

        // 3. Update ONLY allowed fields (BR-STU-007)
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setMobile(request.getMobile());
        student.setStatus(request.getStatus());

        // 4. Save (optimistic lock check via version)
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
    public Page<StudentResponse> searchStudents(String lastName, StudentStatus status, Pageable pageable) {
        log.debug("Searching students: lastName={}, status={}", lastName, status);

        Page<Student> students;

        if (lastName != null && status != null) {
            students = repository.findByLastNameContainingIgnoreCaseAndStatus(lastName, status, pageable);
        } else if (lastName != null) {
            students = repository.findByLastNameContainingIgnoreCase(lastName, pageable);
        } else if (status != null) {
            students = repository.findByStatus(status, pageable);
        } else {
            students = repository.findAll(pageable);
        }

        return students.map(mapper::toResponse);
    }

    // Helper methods
    private StudentValidationRequest buildValidationRequest(StudentRequest request, String excludeStudentId) {
        return StudentValidationRequest.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth())
            .mobile(request.getMobile())
            .email(request.getEmail())
            .aadhaarNumber(request.getAadhaarNumber())
            .studentId(excludeStudentId)
            .checkMobileUniqueness(true)
            .build();
    }
}
```

### 4.2 MapStruct Mapper

**StudentMapper.java:**
```java
package com.school.student.mapper;

import com.school.student.domain.entity.Student;
import com.school.student.dto.request.StudentRequest;
import com.school.student.dto.response.StudentResponse;
import org.mapstruct.*;

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
    @Mapping(source = "studentId", target = "id")  // Frontend expects "id" field
    StudentResponse toResponse(Student entity);

    // Update Entity from Request (only allowed fields)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "aadhaarNumber", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(StudentUpdateRequest request, @MappingTarget Student entity);
}
```

---

## 5. Infrastructure Layer Implementation

### 5.1 Repository Pattern (Spring Data JPA)

**StudentRepository.java:**
```java
package com.school.student.repository;

import com.school.student.domain.entity.Student;
import com.school.student.domain.entity.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Business key lookup
    Optional<Student> findByStudentId(String studentId);

    // Uniqueness checks
    boolean existsByMobile(String mobile);
    boolean existsByMobileAndStudentIdNot(String mobile, String studentId);

    boolean existsByEmail(String email);
    boolean existsByEmailAndStudentIdNot(String email, String studentId);

    // Search queries
    Page<Student> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    Page<Student> findByLastNameContainingIgnoreCaseAndStatus(
        String lastName,
        StudentStatus status,
        Pageable pageable
    );

    // Custom query with EntityGraph (N+1 prevention)
    @Query("SELECT s FROM Student s WHERE s.guardianName LIKE %:guardianName%")
    Page<Student> findByGuardianNameContaining(@Param("guardianName") String guardianName, Pageable pageable);

    // Count queries for dashboard
    long countByStatus(StudentStatus status);
}
```

### 5.2 Cache Configuration (Redis)

**CacheConfig.java:**
```java
package com.school.student.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure ObjectMapper for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2))  // Default TTL: 2 hours
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .prefixCacheNameWith("sms:student:");  // Key prefix

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("students", config.entryTtl(Duration.ofHours(4)))  // Stable data: 4 hours
            .withCacheConfiguration("studentSearchResults", config.entryTtl(Duration.ofMinutes(15)))  // Dynamic: 15 min
            .build();
    }
}
```

**application.yml (Redis):**
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0  # Student service uses DB 0
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: -1ms
      timeout: 2000ms
```

---

## 6. Performance Optimization

### 6.1 N+1 Query Prevention

**Problem:** Loading students + enrollments triggers N+1 queries

**Solution 1: EntityGraph**
```java
@EntityGraph(attributePaths = {"enrollments"})
@Query("SELECT s FROM Student s WHERE s.studentId = :studentId")
Optional<Student> findByStudentIdWithEnrollments(@Param("studentId") String studentId);
```

**Solution 2: JOIN FETCH**
```java
@Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.studentId = :studentId")
Optional<Student> findByStudentIdWithEnrollments(@Param("studentId") String studentId);
```

### 6.2 Batch Processing

**Insert 1000 students:**
```java
@Transactional
public void bulkRegisterStudents(List<StudentRequest> requests) {
    int batchSize = 50;

    for (int i = 0; i < requests.size(); i++) {
        Student student = mapper.toEntity(requests.get(i));
        student.generateStudentId();
        repository.save(student);

        if (i % batchSize == 0 && i > 0) {
            entityManager.flush();  // Flush batch
            entityManager.clear();  // Clear persistence context
        }
    }
}
```

**application.yml:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
```

### 6.3 HikariCP Tuning

**application.yml:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # Max connections
      minimum-idle: 10
      connection-timeout: 30000  # 30 seconds
      idle-timeout: 600000  # 10 minutes
      max-lifetime: 1800000  # 30 minutes
      leak-detection-threshold: 60000  # Detect leaks after 60 seconds
      connection-test-query: SELECT 1
```

### 6.4 Query Performance Monitoring

**Enable query logging:**
```yaml
spring:
  jpa:
    show-sql: true  # Development only
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        type:
          descriptor:
            sql:
              BasicBinder: TRACE  # Log bind parameters

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## 7. Monitoring & Observability

### 7.1 Spring Actuator Configuration

**pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**application.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}
```

### 7.2 Custom Metrics

**StudentMetrics.java:**
```java
package com.school.student.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class StudentMetrics {

    private final Counter studentsRegisteredCounter;
    private final Counter studentsDeletedCounter;

    public StudentMetrics(MeterRegistry registry) {
        this.studentsRegisteredCounter = Counter.builder("students.registered.total")
            .description("Total number of students registered")
            .register(registry);

        this.studentsDeletedCounter = Counter.builder("students.deleted.total")
            .description("Total number of students deleted")
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

**Usage in Service:**
```java
@Service
public class StudentService {
    private final StudentMetrics metrics;

    public StudentResponse registerStudent(StudentRequest request) {
        Student saved = repository.save(student);
        metrics.incrementRegistered();  // Track metric
        return mapper.toResponse(saved);
    }
}
```

### 7.3 Distributed Tracing (Zipkin)

**pom.xml:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

**application.yml:**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # Sample all requests (reduce in production)
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

---

## 8. Global Directives Compliance

### 8.1 D-001: Spring Boot & SpringDoc Compatibility

**MANDATORY versions:**
```xml
<spring-boot.version>3.3.5</spring-boot.version>
<springdoc.version>2.6.0</springdoc.version>
```

**Test compatibility in CI/CD:**
```bash
mvn dependency:tree | grep springdoc
# Verify: springdoc-openapi-starter-webmvc-ui:2.6.0
```

### 8.2 D-002: CORS Configuration

**CorsConfig.java:**
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
                        "http://localhost:5173",  // Vite
                        "http://localhost:3000",  // Alternative
                        "http://localhost:4173"   // Vite preview
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

### 8.3 D-003: Redis Separate Databases

**Student Service:** `spring.data.redis.database=0`
**Configuration Service:** `spring.data.redis.database=1`

### 8.4 D-006: Redis Connection Pool

**application.yml:**
```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: -1ms
```

---

## Appendix

### A. Project Structure (Complete Example)

```
student-service/
├── src/main/java/com/school/student/
│   ├── StudentServiceApplication.java
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── domain/
│   ├── exception/
│   ├── mapper/
│   ├── repository/
│   └── service/
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── db/migration/
│   │   ├── V1__create_students_table.sql
│   │   └── V2__create_enrollments_table.sql
│   └── rules/
│       └── student-validation.drl
├── src/test/java/
├── pom.xml
└── Dockerfile
```

### B. Cross-References

- **System Architecture:** See `01-system-architecture.md`
- **Database Design:** See `02-database-design.md`
- **Business Rules:** See `03-business-rules.md`
- **Security:** See `04-security-architecture.md`

---

**Document Control:**
**Created:** January 22, 2026
**Last Updated:** January 22, 2026
**Approved By:** Software Architect Agent
**Mandatory For:** Backend Developer Agent
