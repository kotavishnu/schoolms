# Backend Implementation Guide - School Management System

## 1. Overview

This guide provides mandatory patterns and conventions for the Backend Developer Agent to implement the Student and Configuration microservices. All implementations must strictly adhere to the DDD layered architecture and OpenAPI specification.

## 2. Project Structure

### 2.1 Directory Layout (Per Service)

```
backend/
├── student-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/school/student/
│   │   │   │   ├── StudentServiceApplication.java
│   │   │   │   ├── presentation/         # Controllers
│   │   │   │   │   ├── StudentController.java
│   │   │   │   │   ├── EnrollmentController.java
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── StudentRequestDTO.java
│   │   │   │   │       ├── StudentResponseDTO.java
│   │   │   │   │       └── ErrorResponseDTO.java
│   │   │   │   ├── application/          # Service Layer
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── StudentService.java
│   │   │   │   │   │   └── EnrollmentService.java
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   ├── StudentMapper.java
│   │   │   │   │   │   └── EnrollmentMapper.java
│   │   │   │   │   └── validation/
│   │   │   │   │       └── ValidationService.java
│   │   │   │   ├── domain/               # Domain Models
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Student.java
│   │   │   │   │   │   ├── Enrollment.java
│   │   │   │   │   │   └── valueobject/
│   │   │   │   │   │       ├── StudentId.java
│   │   │   │   │   │       └── Mobile.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── StudentRepository.java  # Interface
│   │   │   │   │   │   └── EnrollmentRepository.java
│   │   │   │   │   └── event/
│   │   │   │   │       └── StudentRegisteredEvent.java
│   │   │   │   ├── infrastructure/       # Implementation
│   │   │   │   │   ├── persistence/
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   │   ├── StudentEntity.java
│   │   │   │   │   │   │   └── EnrollmentEntity.java
│   │   │   │   │   │   ├── JpaStudentRepository.java
│   │   │   │   │   │   └── JpaEnrollmentRepository.java
│   │   │   │   │   ├── cache/
│   │   │   │   │   │   └── RedisCacheConfig.java
│   │   │   │   │   └── drools/
│   │   │   │   │       └── DroolsConfig.java
│   │   │   │   └── config/               # Configuration
│   │   │   │       ├── OpenApiConfig.java
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       └── ObservabilityConfig.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       ├── db/migration/
│   │   │       │   ├── V1__create_students_table.sql
│   │   │       │   └── V2__create_enrollments_table.sql
│   │   │       └── rules/
│   │   │           └── student/
│   │   │               └── student-validation-rules.drl
│   │   └── test/
│   │       ├── java/com/school/student/
│   │       │   ├── integration/
│   │       │   ├── unit/
│   │       │   └── testdata/
│   │       │       └── StudentTestDataBuilder.java
│   │       └── resources/
│   │           └── application-test.yml
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
└── config-service/
    └── (Similar structure)
```

## 3. Layer-by-Layer Implementation

### 3.1 Domain Layer (Core Business Logic)

**Principle**: The domain layer is the heart of the application. It contains rich domain models with business logic, NOT anemic data containers.

#### Rich Domain Model Example

```java
package com.school.student.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder(access = AccessLevel.PRIVATE)  // Force use of factory method
public class Student {

    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Mobile mobile;
    private String email;
    private Address address;
    private GuardianInfo guardianInfo;
    private String aadhaarNumber;
    private StudentStatus status;
    private Long version;

    // Factory method - encapsulates creation logic
    public static Student register(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Mobile mobile,
        GuardianInfo guardianInfo
    ) {
        validateAge(dateOfBirth);

        return Student.builder()
            .firstName(firstName)
            .lastName(lastName)
            .dateOfBirth(dateOfBirth)
            .mobile(mobile)
            .guardianInfo(guardianInfo)
            .status(StudentStatus.ACTIVE)  // Default status
            .build();
    }

    // Business logic method
    public void updateProfile(String firstName, String lastName, Mobile mobile) {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Cannot update inactive student profile");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
    }

    // Business logic method
    public void deactivate() {
        if (this.status == StudentStatus.INACTIVE) {
            throw new IllegalStateException("Student is already inactive");
        }
        this.status = StudentStatus.INACTIVE;
    }

    // Business rule enforcement
    private static void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new IllegalArgumentException("Student age must be between 3 and 18 years");
        }
    }

    // Computed property
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
```

#### Value Objects

```java
package com.school.student.domain.model.valueobject;

import lombok.Value;

@Value  // Immutable
public class Mobile {

    String number;

    public static Mobile of(String number) {
        if (number == null || !number.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile must be 10 digits");
        }
        return new Mobile(number);
    }

    public String getMasked() {
        return number.substring(0, 3) + "****" + number.substring(7);
    }
}

@Value
public class Address {
    String street;
    String city;
    String state;
    String pincode;

    public String getFullAddress() {
        return String.join(", ", street, city, state, pincode);
    }
}

@Value
public class GuardianInfo {
    String fathersName;
    String mothersName;

    public String getPrimaryGuardian() {
        return fathersName != null ? fathersName : mothersName;
    }
}
```

#### Repository Interface (Domain Layer)

```java
package com.school.student.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentRepository {

    Student save(Student student);

    Optional<Student> findById(Long id);

    Optional<Student> findByStudentId(String studentId);

    Page<Student> findByLastNameContaining(String lastName, Pageable pageable);

    boolean existsByMobile(Mobile mobile);

    boolean existsByMobileAndIdNot(Mobile mobile, Long id);

    void deleteById(Long id);
}
```

### 3.2 Application Layer (Orchestration)

**Responsibility**: Coordinate domain models, repositories, and external services. Handle transactions and DTO mapping.

#### Service Implementation

```java
package com.school.student.application.service;

import com.school.student.domain.model.Student;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.presentation.dto.*;
import com.school.student.application.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieContainer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final KieContainer kieContainer;

    @Transactional  // Write transaction
    public StudentResponseDTO registerStudent(StudentRequestDTO dto) {
        // 1. Map DTO to Domain Model
        Student student = studentMapper.toDomain(dto);

        // 2. Execute Business Rules (Drools)
        ValidationResult validationResult = validateWithRules(student);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        // 3. Persist Domain Model
        Student savedStudent = studentRepository.save(student);

        // 4. Map Domain Model to Response DTO
        return studentMapper.toResponseDTO(savedStudent);
    }

    public StudentResponseDTO getStudentById(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        return studentMapper.toResponseDTO(student);
    }

    @Transactional
    public StudentResponseDTO updateStudent(
        String studentId,
        StudentUpdateRequestDTO dto
    ) {
        // 1. Retrieve existing student
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // 2. Optimistic locking check
        if (!student.getVersion().equals(dto.getVersion())) {
            throw new OptimisticLockException("Student record was modified by another user");
        }

        // 3. Update via domain method (business logic enforced)
        Mobile newMobile = Mobile.of(dto.getMobile());
        student.updateProfile(dto.getFirstName(), dto.getLastName(), newMobile);

        // Update status if provided
        if (dto.getStatus() == StudentStatus.INACTIVE) {
            student.deactivate();
        }

        // 4. Persist and return
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponseDTO(updatedStudent);
    }

    @Transactional
    public void deleteStudent(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.deleteById(student.getId());
    }

    public Page<StudentResponseDTO> searchStudents(
        String lastName,
        StudentStatus status,
        Pageable pageable
    ) {
        Page<Student> students;

        if (lastName != null && status != null) {
            students = studentRepository.findByLastNameContainingAndStatus(
                lastName, status, pageable
            );
        } else if (lastName != null) {
            students = studentRepository.findByLastNameContaining(lastName, pageable);
        } else {
            students = studentRepository.findAll(pageable);
        }

        return students.map(studentMapper::toResponseDTO);
    }

    private ValidationResult validateWithRules(Student student) {
        KieSession kieSession = kieContainer.newKieSession("student-rules");
        ValidationResult result = new ValidationResult();

        kieSession.insert(student);
        kieSession.insert(result);
        kieSession.setGlobal("studentRepository", studentRepository);

        kieSession.fireAllRules();
        kieSession.dispose();

        return result;
    }
}
```

#### MapStruct Mapper

```java
package com.school.student.application.mapper;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.valueobject.*;
import com.school.student.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "mobile", source = "mobile", qualifiedByName = "stringToMobile")
    @Mapping(target = "guardianInfo", source = ".", qualifiedByName = "toGuardianInfo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Student toDomain(StudentRequestDTO dto);

    @Mapping(target = "mobile", source = "mobile.number")
    @Mapping(target = "fathersName", source = "guardianInfo.fathersName")
    @Mapping(target = "mothersName", source = "guardianInfo.mothersName")
    StudentResponseDTO toResponseDTO(Student student);

    @Named("stringToMobile")
    default Mobile stringToMobile(String mobile) {
        return mobile != null ? Mobile.of(mobile) : null;
    }

    @Named("toGuardianInfo")
    default GuardianInfo toGuardianInfo(StudentRequestDTO dto) {
        return new GuardianInfo(dto.getFathersName(), dto.getMothersName());
    }
}
```

### 3.3 Presentation Layer (Controllers)

**Responsibility**: HTTP protocol handling, request validation, response formatting.

```java
package com.school.student.presentation;

import com.school.student.application.service.StudentService;
import com.school.student.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management operations")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Register a new student")
    public ResponseEntity<StudentResponseDTO> registerStudent(
        @Valid @RequestBody StudentRequestDTO dto
    ) {
        StudentResponseDTO response = studentService.registerStudent(dto);

        return ResponseEntity
            .created(URI.create("/api/v1/students/" + response.getStudentId()))
            .body(response);
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Retrieve student by ID")
    public ResponseEntity<StudentResponseDTO> getStudent(
        @PathVariable String studentId
    ) {
        StudentResponseDTO response = studentService.getStudentById(studentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{studentId}")
    @Operation(summary = "Update student profile")
    public ResponseEntity<StudentResponseDTO> updateStudent(
        @PathVariable String studentId,
        @Valid @RequestBody StudentUpdateRequestDTO dto
    ) {
        StudentResponseDTO response = studentService.updateStudent(studentId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student record")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Search and list students with pagination")
    public ResponseEntity<Page<StudentResponseDTO>> searchStudents(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) StudentStatus status,
        Pageable pageable
    ) {
        Page<StudentResponseDTO> students = studentService.searchStudents(
            lastName, status, pageable
        );
        return ResponseEntity.ok(students);
    }
}
```

### 3.4 Infrastructure Layer (Persistence)

#### JPA Entity

```java
package com.school.student.infrastructure.persistence.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "fathers_name", length = 100)
    private String fathersName;

    @Column(name = "mothers_name", length = 100)
    private String mothersName;

    @Column(name = "identification_mark")
    private String identificationMark;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnrollmentEntity> enrollments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StudentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

#### JPA Repository Implementation

```java
package com.school.student.infrastructure.persistence;

import com.school.student.infrastructure.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStudentRepositoryInterface extends JpaRepository<StudentEntity, Long> {

    @EntityGraph(attributePaths = {"enrollments"})  // Prevent N+1
    Optional<StudentEntity> findByStudentId(String studentId);

    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<StudentEntity> findByLastNameContaining(
        @Param("lastName") String lastName,
        Pageable pageable
    );

    boolean existsByMobileAndIdNot(String mobile, Long id);
}

// Adapter: Domain Repository → JPA Repository
@Component
@RequiredArgsConstructor
public class JpaStudentRepository implements StudentRepository {

    private final JpaStudentRepositoryInterface jpaRepository;
    private final StudentEntityMapper entityMapper;

    @Override
    public Student save(Student student) {
        StudentEntity entity = entityMapper.toEntity(student);
        StudentEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId)
            .map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByMobileAndIdNot(Mobile mobile, Long id) {
        return jpaRepository.existsByMobileAndIdNot(mobile.getNumber(), id);
    }
}
```

## 4. Performance Optimization Patterns

### 4.1 N+1 Query Prevention

```java
@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    // GOOD - Fetch enrollments in single query
    @EntityGraph(attributePaths = {"enrollments"})
    @Query("SELECT s FROM StudentEntity s WHERE s.studentId = :studentId")
    Optional<StudentEntity> findByStudentIdWithEnrollments(@Param("studentId") String studentId);

    // GOOD - Join fetch
    @Query("SELECT s FROM StudentEntity s JOIN FETCH s.enrollments WHERE s.status = :status")
    List<StudentEntity> findActiveStudentsWithEnrollments(@Param("status") StudentStatus status);
}
```

### 4.2 Redis Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer()
            ))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()
            ));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}

@Service
public class ConfigurationService {

    @Cacheable(value = "configurations", key = "#category")
    public Map<String, String> getGroupedSettings(String category) {
        // Cache hit: Return from Redis
        // Cache miss: Execute query and store in Redis
        return configRepository.findByCategory(category).stream()
            .collect(Collectors.toMap(
                Configuration::getKey,
                Configuration::getValue
            ));
    }

    @CacheEvict(value = "configurations", key = "#category")
    public void updateConfiguration(String category, String key, String value) {
        // Invalidate cache on update
    }
}
```

### 4.3 HikariCP Tuning

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
      pool-name: StudentServicePool
```

## 5. Error Handling

### 5.1 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
        ValidationException ex,
        WebRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .type("https://api.school.com/errors/validation-error")
            .title("Validation Failed")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .correlationId(MDC.get("correlationId"))
            .errors(ex.getValidationResult().getErrors())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponseDTO> handleOptimisticLockException(
        OptimisticLockException ex
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .type("https://api.school.com/errors/conflict")
            .title("Conflict")
            .status(HttpStatus.CONFLICT.value())
            .detail("Resource was modified by another user")
            .timestamp(LocalDateTime.now())
            .correlationId(MDC.get("correlationId"))
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleStudentNotFoundException(
        StudentNotFoundException ex
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .type("https://api.school.com/errors/not-found")
            .title("Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .detail(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .correlationId(MDC.get("correlationId"))
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

## 6. Observability

### 6.1 Actuator Configuration

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
```

### 6.2 Custom Metrics

```java
@Component
public class StudentMetrics {

    private final Counter registrationCounter;
    private final Gauge activeStudentsGauge;

    public StudentMetrics(MeterRegistry registry, StudentRepository repository) {
        this.registrationCounter = Counter.builder("students.registered.total")
            .description("Total number of student registrations")
            .register(registry);

        this.activeStudentsGauge = Gauge.builder("students.active.count", repository,
                repo -> repo.countByStatus(StudentStatus.ACTIVE))
            .description("Current number of active students")
            .register(registry);
    }

    public void incrementRegistrationCounter() {
        registrationCounter.increment();
    }
}
```

## 7. Testing Strategy

### 7.1 Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private KieContainer kieContainer;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should register student successfully")
    void shouldRegisterStudent() {
        // Arrange
        StudentRequestDTO dto = StudentTestDataBuilder.buildRequestDTO();
        Student student = StudentTestDataBuilder.buildDomain();
        Student savedStudent = StudentTestDataBuilder.buildDomain();
        savedStudent.setId(1L);
        savedStudent.setStudentId("STD-20260203-0001");

        when(studentMapper.toDomain(dto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(savedStudent);
        when(studentMapper.toResponseDTO(savedStudent))
            .thenReturn(StudentTestDataBuilder.buildResponseDTO());

        // Act
        StudentResponseDTO response = studentService.registerStudent(dto);

        // Assert
        assertThat(response.getStudentId()).isEqualTo("STD-20260203-0001");
        verify(studentRepository).save(student);
    }
}
```

### 7.2 Integration Test with TestContainers

```java
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("student_db_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should create student and return 201")
    void shouldCreateStudent() throws Exception {
        String requestBody = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "dateOfBirth": "2015-05-15",
              "mobile": "9876543210",
              "fathersName": "Guardian Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("X-User-ID", "admin@school.com")
                .header("X-User-Role", "SCHOOL_ADMINISTRATOR"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").exists())
            .andExpect(jsonPath("$.firstName").value("John"));
    }
}
```

## 8. API Documentation

### 8.1 OpenAPI Configuration

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("School Management System API")
                .version("1.0.0")
                .description("RESTful API for student registration and school configuration"))
            .servers(List.of(
                new Server().url("http://localhost:8081/api/v1").description("Student Service"),
                new Server().url("http://localhost:8082/api/v1").description("Config Service")
            ));
    }
}
```

## 9. Mandatory Code Quality Checks

### 9.1 Pre-Commit Checklist

- [ ] All DTOs are immutable (`final` fields, `@Builder`)
- [ ] Repository methods return `Optional<T>` for single results
- [ ] Optimistic locking (`@Version`) used on all mutable entities
- [ ] No N+1 queries (verified via `@EntityGraph` or `JOIN FETCH`)
- [ ] All service methods have `@Transactional` annotation where needed
- [ ] Exceptions include correlation ID
- [ ] Unit tests achieve >85% coverage
- [ ] Integration tests cover critical paths
- [ ] OpenAPI documentation updated
- [ ] No hardcoded credentials

---

**Document Version**: 1.0
**Last Updated**: 2026-02-03
**Owner**: Architect Agent
