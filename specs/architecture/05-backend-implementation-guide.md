# Backend Implementation Guide - School Management System

**Version**: 1.0
**Date**: November 10, 2025
**Status**: Approved
**Author**: Backend Architecture Team

---

## Table of Contents
1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Layer Architecture](#layer-architecture)
4. [Domain-Driven Design Implementation](#domain-driven-design-implementation)
5. [Code Examples](#code-examples)
6. [Design Patterns](#design-patterns)
7. [Business Rules with Drools](#business-rules-with-drools)
8. [Caching Strategy](#caching-strategy)
9. [Transaction Management](#transaction-management)
10. [Error Handling](#error-handling)
11. [Performance Optimization](#performance-optimization)
12. [Code Quality Standards](#code-quality-standards)

---

## 1. Overview

### 1.1 Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 3.5.0 |
| Language | Java | 21 (LTS) |
| Build Tool | Maven | 3.9.x |
| ORM | Spring Data JPA (Hibernate) | 6.x |
| Database | PostgreSQL | 18 |
| Cache | Redis | 7.x |
| Rules Engine | Drools | 9.x |
| Migration | Flyway | 10.x |
| Validation | Hibernate Validator | 8.x |
| Mapping | MapStruct | 1.6.x |
| API Docs | SpringDoc OpenAPI | 2.6.x |
| Testing | JUnit 5, Mockito, TestContainers | Latest |

### 1.2 Project Metadata

```xml
<!-- pom.xml -->
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.school</groupId>
    <artifactId>school-management-system</artifactId>
    <version>1.0.0</version>
    <name>School Management System</name>
    <description>School Management System - Student, Fee, and Payment Management</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
    </parent>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <mapstruct.version>1.6.0</mapstruct.version>
        <drools.version>9.44.0.Final</drools.version>
    </properties>
</project>
```

---

## 2. Project Structure

### 2.1 Complete Package Structure

```
com.school.sms
│
├── SchoolManagementSystemApplication.java  (Main class)
│
├── domain                                  (Domain Layer)
│   ├── student
│   │   ├── model
│   │   │   ├── Student.java               (Entity/Aggregate Root)
│   │   │   ├── Guardian.java              (Entity)
│   │   │   ├── StudentStatus.java         (Enum)
│   │   │   └── Gender.java                (Enum)
│   │   ├── repository
│   │   │   ├── StudentRepository.java     (Interface)
│   │   │   └── GuardianRepository.java
│   │   ├── service
│   │   │   └── StudentDomainService.java  (Business logic)
│   │   └── event
│   │       ├── StudentRegisteredEvent.java
│   │       └── StudentEnrolledEvent.java
│   │
│   ├── academic
│   │   ├── model
│   │   │   ├── Class.java
│   │   │   ├── AcademicYear.java
│   │   │   ├── Enrollment.java
│   │   │   └── EnrollmentStatus.java
│   │   ├── repository
│   │   │   ├── ClassRepository.java
│   │   │   ├── AcademicYearRepository.java
│   │   │   └── EnrollmentRepository.java
│   │   └── service
│   │       └── EnrollmentDomainService.java
│   │
│   ├── fee
│   │   ├── model
│   │   │   ├── FeeStructure.java
│   │   │   ├── FeeJournal.java
│   │   │   ├── FeeType.java
│   │   │   ├── FeeFrequency.java
│   │   │   └── PaymentStatus.java
│   │   ├── repository
│   │   │   ├── FeeStructureRepository.java
│   │   │   └── FeeJournalRepository.java
│   │   └── service
│   │       ├── FeeCalculationDomainService.java
│   │       └── FeeRulesService.java
│   │
│   ├── payment
│   │   ├── model
│   │   │   ├── Payment.java
│   │   │   ├── Receipt.java
│   │   │   └── PaymentMethod.java
│   │   ├── repository
│   │   │   ├── PaymentRepository.java
│   │   │   └── ReceiptRepository.java
│   │   └── service
│   │       └── PaymentDomainService.java
│   │
│   ├── configuration
│   │   ├── model
│   │   │   ├── Configuration.java
│   │   │   └── ConfigCategory.java
│   │   └── repository
│   │       └── ConfigurationRepository.java
│   │
│   └── audit
│       ├── model
│       │   └── AuditLog.java
│       └── repository
│           └── AuditLogRepository.java
│
├── application                            (Application Layer)
│   ├── student
│   │   ├── StudentRegistrationService.java
│   │   ├── StudentProfileService.java
│   │   ├── EnrollmentService.java
│   │   └── dto
│   │       ├── CreateStudentRequest.java
│   │       ├── UpdateStudentRequest.java
│   │       ├── StudentResponse.java
│   │       └── GuardianDto.java
│   │
│   ├── academic
│   │   ├── ClassManagementService.java
│   │   ├── AcademicYearService.java
│   │   ├── RolloverService.java
│   │   └── dto
│   │       ├── CreateClassRequest.java
│   │       ├── ClassResponse.java
│   │       └── EnrollmentResponse.java
│   │
│   ├── fee
│   │   ├── FeeStructureService.java
│   │   ├── FeeJournalService.java
│   │   ├── FeeCalculationService.java
│   │   └── dto
│   │       ├── CreateFeeStructureRequest.java
│   │       ├── FeeStructureResponse.java
│   │       └── FeeCalculationResponse.java
│   │
│   ├── payment
│   │   ├── PaymentRecordingService.java
│   │   ├── ReceiptGenerationService.java
│   │   ├── DuesCalculationService.java
│   │   └── dto
│   │       ├── RecordPaymentRequest.java
│   │       ├── PaymentResponse.java
│   │       └── ReceiptResponse.java
│   │
│   ├── reporting
│   │   ├── EnrollmentReportService.java
│   │   ├── FeeCollectionReportService.java
│   │   └── dto
│   │       └── ReportResponse.java
│   │
│   └── mapper                             (MapStruct Mappers)
│       ├── StudentMapper.java
│       ├── ClassMapper.java
│       ├── FeeMapper.java
│       └── PaymentMapper.java
│
├── presentation                           (Presentation Layer)
│   ├── controller
│   │   ├── AuthenticationController.java
│   │   ├── StudentController.java
│   │   ├── ClassController.java
│   │   ├── FeeStructureController.java
│   │   ├── PaymentController.java
│   │   ├── ReceiptController.java
│   │   └── ConfigurationController.java
│   │
│   ├── advice
│   │   └── GlobalExceptionHandler.java   (Exception handling)
│   │
│   └── filter
│       ├── JwtAuthenticationFilter.java
│       └── RequestLoggingFilter.java
│
├── infrastructure                         (Infrastructure Layer)
│   ├── persistence
│   │   ├── StudentRepositoryImpl.java    (Custom repository implementations)
│   │   └── specification
│   │       ├── StudentSpecification.java
│   │       └── FeeJournalSpecification.java
│   │
│   ├── cache
│   │   ├── CacheConfig.java
│   │   └── CacheNames.java
│   │
│   ├── security
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── encryption
│   │       ├── EncryptionService.java
│   │       └── EncryptedStringConverter.java
│   │
│   ├── rules
│   │   ├── DroolsConfig.java
│   │   └── FeeRulesEngine.java
│   │
│   ├── messaging
│   │   ├── EventPublisher.java
│   │   └── EventListener.java
│   │
│   └── audit
│       ├── AuditAspect.java
│       └── AuditService.java
│
├── config                                 (Configuration)
│   ├── DatabaseConfig.java
│   ├── CacheConfig.java
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── AsyncConfig.java
│
└── common                                 (Common/Shared)
    ├── exception
    │   ├── BusinessException.java
    │   ├── NotFoundException.java
    │   ├── ValidationException.java
    │   └── ErrorCode.java
    │
    ├── util
    │   ├── DateUtils.java
    │   ├── ValidationUtils.java
    │   └── SecurityUtils.java
    │
    └── constant
        └── Constants.java
```

---

## 3. Layer Architecture

### 3.1 Dependency Rule

```
Presentation Layer
       ↓ depends on
Application Layer
       ↓ depends on
  Domain Layer
       ↑ implemented by
Infrastructure Layer
```

**Key Principle**: Dependencies point inward. Domain layer has no dependencies on outer layers.

### 3.2 Layer Responsibilities

#### 3.2.1 Domain Layer (Core Business Logic)

**Responsibilities**:
- Define entities and value objects
- Implement business rules and invariants
- Define repository interfaces
- Publish domain events
- No framework dependencies (pure Java)

**Example**:
```java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(unique = true, nullable = false)
    private String studentCode;

    // Encrypted fields
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "first_name_encrypted", columnDefinition = "BYTEA")
    private String firstName;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    // Business method
    public void enroll(Class classEntity, LocalDate enrollmentDate) {
        if (this.status != StudentStatus.ACTIVE) {
            throw new BusinessException("Only active students can be enrolled");
        }
        if (!classEntity.hasCapacity()) {
            throw new BusinessException("Class is at full capacity");
        }
        // Create enrollment
    }

    // Domain event
    public StudentRegisteredEvent toRegisteredEvent() {
        return new StudentRegisteredEvent(this.studentId, this.studentCode);
    }
}
```

#### 3.2.2 Application Layer (Use Cases/Orchestration)

**Responsibilities**:
- Implement use cases/application services
- Transaction boundaries
- Coordinate between domain services
- Map between DTOs and domain entities
- Publish application events

**Example**:
```java
@Service
@Transactional
public class StudentRegistrationService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final EventPublisher eventPublisher;
    private final EncryptionService encryptionService;

    public StudentResponse registerStudent(CreateStudentRequest request) {
        // 1. Validate business rules
        validateStudentRegistration(request);

        // 2. Map DTO to domain entity
        Student student = studentMapper.toEntity(request);

        // 3. Generate student code
        student.setStudentCode(generateStudentCode());

        // 4. Save student
        Student savedStudent = studentRepository.save(student);

        // 5. Publish domain event
        eventPublisher.publish(savedStudent.toRegisteredEvent());

        // 6. Return response DTO
        return studentMapper.toResponse(savedStudent);
    }

    private void validateStudentRegistration(CreateStudentRequest request) {
        // Check age (3-18 years)
        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new ValidationException("Student age must be between 3 and 18");
        }

        // Check mobile uniqueness
        if (studentRepository.existsByMobile(request.getMobile())) {
            throw new ValidationException("Mobile number already exists");
        }
    }

    private String generateStudentCode() {
        int year = LocalDate.now().getYear();
        long count = studentRepository.countByAdmissionYear(year);
        return String.format("STU-%d-%05d", year, count + 1);
    }
}
```

#### 3.2.3 Presentation Layer (REST Controllers)

**Responsibilities**:
- REST endpoint definitions
- HTTP request/response handling
- Input validation
- Status code mapping
- OpenAPI documentation

**Example**:
```java
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Student management APIs")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentRegistrationService studentRegistrationService;
    private final StudentProfileService studentProfileService;

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    @Operation(summary = "Register new student", description = "Create a new student with guardian information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Duplicate mobile number")
    })
    public ResponseEntity<StudentResponse> registerStudent(
            @Valid @RequestBody CreateStudentRequest request) {

        StudentResponse response = studentRegistrationService.registerStudent(request);

        return ResponseEntity
            .created(URI.create("/api/v1/students/" + response.getStudentId()))
            .body(response);
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:read')")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long studentId) {
        StudentResponse response = studentProfileService.getStudent(studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:read')")
    @Operation(summary = "Search students")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) String className,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        Page<StudentResponse> response = studentProfileService.searchStudents(
            search, status, className, pageable);

        return ResponseEntity.ok(response);
    }
}
```

#### 3.2.4 Infrastructure Layer (Technical Implementation)

**Responsibilities**:
- Repository implementations
- External service integrations
- Caching
- Security
- Messaging
- Audit logging

**Example**:
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheNames.STUDENT_PROFILE, config.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put(CacheNames.FEE_STRUCTURE, config.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put(CacheNames.CLASS_INFO, config.entryTtl(Duration.ofHours(12)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
```

---

## 4. Domain-Driven Design Implementation

### 4.1 Aggregate Roots

#### Student Aggregate

```java
@Entity
@Table(name = "students")
@EntityListeners(AuditingEntityListener.class)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(unique = true, nullable = false, length = 20)
    private String studentCode;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "first_name_encrypted", columnDefinition = "BYTEA")
    private String firstName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "last_name_encrypted", columnDefinition = "BYTEA")
    private String lastName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "date_of_birth_encrypted", columnDefinition = "BYTEA")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "mobile_encrypted", columnDefinition = "BYTEA")
    private String mobile;

    @Column(name = "mobile_hash")
    private String mobileHash;  // SHA-256 hash for searching

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate admissionDate;

    // Aggregate boundary - Guardian entities owned by Student
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guardian> guardians = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(nullable = false)
    private Long updatedBy;

    // Business methods
    public void addGuardian(Guardian guardian) {
        guardians.add(guardian);
        guardian.setStudent(this);
    }

    public void removeGuardian(Guardian guardian) {
        guardians.remove(guardian);
        guardian.setStudent(null);
    }

    public void updateStatus(StudentStatus newStatus, String reason) {
        if (this.status == newStatus) {
            return;
        }

        // Business rules for status transitions
        validateStatusTransition(newStatus);

        this.status = newStatus;
    }

    private void validateStatusTransition(StudentStatus newStatus) {
        if (this.status == StudentStatus.GRADUATED && newStatus != StudentStatus.ACTIVE) {
            throw new BusinessException("Cannot change status of graduated student");
        }
    }

    public int getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isActive() {
        return this.status == StudentStatus.ACTIVE;
    }

    // Domain events
    @Transient
    @DomainEvents
    public Collection<Object> domainEvents() {
        // Return list of domain events
        return Collections.emptyList();
    }

    @AfterDomainEventPublication
    public void clearDomainEvents() {
        // Clear events after publication
    }
}
```

### 4.2 Value Objects

```java
@Embeddable
public class Address {

    @Column(name = "address_line1")
    private String line1;

    @Column(name = "address_line2")
    private String line2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    // Value objects are immutable - no setters
    public Address(String line1, String line2, String city, String state, String postalCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    // Only getters
    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }

    // Equals and hashCode based on value
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(line1, address.line1) &&
               Objects.equals(line2, address.line2) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state) &&
               Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, city, state, postalCode);
    }
}
```

### 4.3 Domain Services

```java
@Service
public class FeeCalculationDomainService {

    private final FeeRulesEngine feeRulesEngine;

    public BigDecimal calculateMonthlyFee(Student student, AcademicYear academicYear, YearMonth month) {
        // Get applicable fee structures
        List<FeeStructure> feeStructures = getFeeStructuresForStudent(student, academicYear);

        // Apply business rules using Drools
        FeeCalculationContext context = new FeeCalculationContext(student, feeStructures, month);
        feeRulesEngine.calculate(context);

        return context.getTotalAmount();
    }

    private List<FeeStructure> getFeeStructuresForStudent(Student student, AcademicYear academicYear) {
        // Logic to get applicable fee structures based on student's class
        // This is domain logic that doesn't belong in entity
        return new ArrayList<>();
    }
}
```

### 4.4 Domain Events

```java
public record StudentRegisteredEvent(
    Long studentId,
    String studentCode,
    LocalDateTime occurredOn
) {
    public StudentRegisteredEvent(Long studentId, String studentCode) {
        this(studentId, studentCode, LocalDateTime.now());
    }
}

public record PaymentRecordedEvent(
    Long paymentId,
    Long studentId,
    BigDecimal amount,
    LocalDateTime occurredOn
) {
    public PaymentRecordedEvent(Long paymentId, Long studentId, BigDecimal amount) {
        this(paymentId, studentId, amount, LocalDateTime.now());
    }
}

// Event listener
@Component
public class PaymentEventListener {

    private final AuditService auditService;
    private final NotificationService notificationService;

    @EventListener
    @Async
    public void handlePaymentRecorded(PaymentRecordedEvent event) {
        // Log to audit
        auditService.logPayment(event);

        // Send notification
        notificationService.sendPaymentConfirmation(event.studentId(), event.amount());
    }
}
```

---

## 5. Code Examples

### 5.1 Complete CRUD Service Example

```java
@Service
@Transactional
@Slf4j
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public StudentResponse getStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> searchStudents(String search, StudentStatus status,
                                                 String className, Pageable pageable) {
        Specification<Student> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            spec = spec.and(StudentSpecification.searchByName(search));
        }

        if (status != null) {
            spec = spec.and(StudentSpecification.hasStatus(status));
        }

        if (className != null) {
            spec = spec.and(StudentSpecification.inClass(className));
        }

        Page<Student> students = studentRepository.findAll(spec, pageable);
        return students.map(studentMapper::toResponse);
    }

    @CacheEvict(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public StudentResponse updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        // Update fields
        studentMapper.updateEntity(request, student);

        Student updatedStudent = studentRepository.save(student);

        // Publish event
        eventPublisher.publish(new StudentUpdatedEvent(studentId));

        return studentMapper.toResponse(updatedStudent);
    }

    @CacheEvict(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public void changeStudentStatus(Long studentId, StudentStatus newStatus, String reason) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentStatus oldStatus = student.getStatus();
        student.updateStatus(newStatus, reason);

        studentRepository.save(student);

        eventPublisher.publish(new StudentStatusChangedEvent(studentId, oldStatus, newStatus));

        log.info("Student {} status changed from {} to {}", studentId, oldStatus, newStatus);
    }
}
```

### 5.2 Specification Pattern for Queries

```java
public class StudentSpecification {

    public static Specification<Student> searchByName(String search) {
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                criteriaBuilder.like(root.get("studentCode"), pattern)
            );
        };
    }

    public static Specification<Student> hasStatus(StudentStatus status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Student> inClass(String className) {
        return (root, query, criteriaBuilder) -> {
            Join<Student, Enrollment> enrollmentJoin = root.join("enrollments");
            Join<Enrollment, Class> classJoin = enrollmentJoin.join("classEntity");
            return criteriaBuilder.and(
                criteriaBuilder.equal(classJoin.get("className"), className),
                criteriaBuilder.equal(enrollmentJoin.get("status"), EnrollmentStatus.ENROLLED)
            );
        };
    }

    public static Specification<Student> admittedInYear(int year) {
        return (root, query, criteriaBuilder) -> {
            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);
            return criteriaBuilder.between(root.get("admissionDate"), startOfYear, endOfYear);
        };
    }
}
```

### 5.3 MapStruct Mapper

```java
@Mapper(componentModel = "spring", uses = {GuardianMapper.class})
public interface StudentMapper {

    @Mapping(target = "guardians", source = "guardians")
    @Mapping(target = "currentEnrollment", source = "enrollments",
             qualifiedByName = "getCurrentEnrollment")
    StudentResponse toResponse(Student student);

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "studentCode", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "studentCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "admissionDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateStudentRequest request, @MappingTarget Student student);

    @Named("getCurrentEnrollment")
    default EnrollmentResponse getCurrentEnrollment(List<Enrollment> enrollments) {
        return enrollments.stream()
            .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
            .findFirst()
            .map(this::toEnrollmentResponse)
            .orElse(null);
    }

    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
```

---

## 6. Design Patterns

### 6.1 Repository Pattern

```java
public interface StudentRepository extends JpaRepository<Student, Long>,
                                            JpaSpecificationExecutor<Student> {

    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByMobile(String mobile);

    @Query("SELECT s FROM Student s WHERE s.mobileHash = :mobileHash")
    Optional<Student> findByMobileHash(@Param("mobileHash") String mobileHash);

    @Query("SELECT COUNT(s) FROM Student s WHERE YEAR(s.admissionDate) = :year")
    long countByAdmissionYear(@Param("year") int year);

    @Query("""
        SELECT s FROM Student s
        JOIN s.enrollments e
        WHERE e.classEntity.classId = :classId
        AND e.status = 'ENROLLED'
        """)
    List<Student> findByClassId(@Param("classId") Long classId);
}
```

### 6.2 Factory Pattern

```java
@Component
public class ReceiptFactory {

    public Receipt createReceipt(Payment payment, List<FeeJournal> journals) {
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setPayment(payment);
        receipt.setStudent(payment.getStudent());
        receipt.setTotalAmount(payment.getPaymentAmount());
        receipt.setPaymentMethod(payment.getPaymentMethod());
        receipt.setReceiptDate(LocalDate.now());

        // Build fee breakdown JSON
        JsonObject breakdown = buildFeeBreakdown(journals);
        receipt.setFeeBreakdownJson(breakdown.toString());

        // Extract months covered
        String monthsCovered = extractMonthsCovered(journals);
        receipt.setMonthsCovered(monthsCovered);

        return receipt;
    }

    private String generateReceiptNumber() {
        int year = LocalDate.now().getYear();
        // Get next sequence number from database
        long sequence = getNextSequence();
        return String.format("REC-%d-%05d", year, sequence);
    }

    private JsonObject buildFeeBreakdown(List<FeeJournal> journals) {
        JsonObject breakdown = new JsonObject();
        JsonArray items = new JsonArray();

        for (FeeJournal journal : journals) {
            JsonObject item = new JsonObject();
            item.addProperty("feeType", journal.getFeeStructure().getFeeType().name());
            item.addProperty("feeName", journal.getFeeStructure().getFeeName());
            item.addProperty("amount", journal.getPaidAmount());
            item.addProperty("month", journal.getFeeMonth());
            items.add(item);
        }

        breakdown.add("items", items);
        return breakdown;
    }
}
```

### 6.3 Strategy Pattern (Fee Calculation)

```java
public interface FeeCalculationStrategy {
    BigDecimal calculate(FeeStructure feeStructure, Student student, YearMonth month);
}

@Component("MONTHLY")
public class MonthlyFeeCalculator implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(FeeStructure feeStructure, Student student, YearMonth month) {
        // Monthly fee is straightforward
        return feeStructure.getAmount();
    }
}

@Component("QUARTERLY")
public class QuarterlyFeeCalculator implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(FeeStructure feeStructure, Student student, YearMonth month) {
        // Quarterly fee only charged in first month of quarter
        int monthValue = month.getMonthValue();
        if (monthValue == 4 || monthValue == 7 || monthValue == 10 || monthValue == 1) {
            return feeStructure.getAmount();
        }
        return BigDecimal.ZERO;
    }
}

@Component("ANNUAL")
public class AnnualFeeCalculator implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(FeeStructure feeStructure, Student student, YearMonth month) {
        // Annual fee only charged in first month of academic year
        if (month.getMonthValue() == 4) {  // April
            return feeStructure.getAmount();
        }
        return BigDecimal.ZERO;
    }
}

@Service
public class FeeCalculationService {

    private final Map<String, FeeCalculationStrategy> strategies;

    public FeeCalculationService(List<FeeCalculationStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                strategy -> strategy.getClass().getAnnotation(Component.class).value(),
                strategy -> strategy
            ));
    }

    public BigDecimal calculateFee(FeeStructure feeStructure, Student student, YearMonth month) {
        FeeCalculationStrategy strategy = strategies.get(feeStructure.getFrequency().name());
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy for frequency: " + feeStructure.getFrequency());
        }
        return strategy.calculate(feeStructure, student, month);
    }
}
```

### 6.4 Builder Pattern

```java
public class StudentBuilder {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String mobile;
    private String email;
    private LocalDate admissionDate;
    private List<Guardian> guardians = new ArrayList<>();

    public StudentBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StudentBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public StudentBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public StudentBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public StudentBuilder mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public StudentBuilder addGuardian(Guardian guardian) {
        this.guardians.add(guardian);
        return this;
    }

    public Student build() {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(dateOfBirth);
        student.setGender(gender);
        student.setMobile(mobile);
        student.setEmail(email);
        student.setAdmissionDate(admissionDate != null ? admissionDate : LocalDate.now());
        student.setStatus(StudentStatus.ACTIVE);

        guardians.forEach(student::addGuardian);

        return student;
    }
}
```

---

## 7. Business Rules with Drools

### 7.1 Drools Configuration

```java
@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load rule files
        loadRuleFiles(kieFileSystem);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Failed to load Drools rules");
        }

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    private void loadRuleFiles(KieFileSystem kieFileSystem) {
        try {
            Resource[] resources = ResourcePatternUtils
                .getResourcePatternResolver(new DefaultResourceLoader())
                .getResources("classpath*:" + RULES_PATH + "**/*.drl");

            for (Resource resource : resources) {
                kieFileSystem.write(ResourceFactory.newClassPathResource(
                    RULES_PATH + resource.getFilename(), "UTF-8"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load rule files", e);
        }
    }
}
```

### 7.2 Fee Calculation Rules (DRL)

```drl
// src/main/resources/rules/fee-calculation.drl
package com.school.sms.rules

import com.school.sms.domain.student.model.Student
import com.school.sms.domain.fee.model.FeeStructure
import com.school.sms.domain.fee.model.FeeType
import com.school.sms.application.fee.dto.FeeCalculationContext
import java.math.BigDecimal

// Rule: Apply tuition fee discount for siblings
rule "Sibling Discount - 10% for 2nd child"
    when
        $context : FeeCalculationContext(siblingCount == 2)
        $fee : FeeStructure(feeType == FeeType.TUITION) from $context.feeStructures
    then
        BigDecimal discount = $fee.getAmount().multiply(new BigDecimal("0.10"));
        $context.addDiscount("Sibling Discount (2nd child)", discount);
end

rule "Sibling Discount - 20% for 3rd+ child"
    when
        $context : FeeCalculationContext(siblingCount >= 3)
        $fee : FeeStructure(feeType == FeeType.TUITION) from $context.feeStructures
    then
        BigDecimal discount = $fee.getAmount().multiply(new BigDecimal("0.20"));
        $context.addDiscount("Sibling Discount (3rd+ child)", discount);
end

// Rule: Apply late fee after due date
rule "Late Fee - 2% per month"
    when
        $context : FeeCalculationContext(overdueDays > 0)
    then
        int overdueMonths = ($context.getOverdueDays() / 30) + 1;
        BigDecimal lateFee = $context.getTotalAmount()
            .multiply(new BigDecimal("0.02"))
            .multiply(new BigDecimal(overdueMonths));
        $context.addCharge("Late Fee", lateFee);
end

// Rule: Waive computer fee for classes 1-2
rule "No Computer Fee for Classes 1-2"
    when
        $context : FeeCalculationContext()
        $student : Student(currentClass.className in ("1", "2")) from $context.student
        $fee : FeeStructure(feeType == FeeType.COMPUTER) from $context.feeStructures
    then
        $context.removeFee($fee);
end
```

### 7.3 Fee Rules Engine

```java
@Service
public class FeeRulesEngine {

    private final KieContainer kieContainer;

    public void calculate(FeeCalculationContext context) {
        KieSession kieSession = kieContainer.newKieSession();

        try {
            kieSession.insert(context);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }
}

// Context object for rules
@Data
public class FeeCalculationContext {

    private Student student;
    private List<FeeStructure> feeStructures;
    private YearMonth month;
    private int siblingCount;
    private int overdueDays;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<FeeItem> feeItems = new ArrayList<>();
    private List<Discount> discounts = new ArrayList<>();
    private List<Charge> charges = new ArrayList<>();

    public void addFee(FeeStructure feeStructure, BigDecimal amount) {
        feeItems.add(new FeeItem(feeStructure, amount));
        totalAmount = totalAmount.add(amount);
    }

    public void addDiscount(String reason, BigDecimal amount) {
        discounts.add(new Discount(reason, amount));
        totalAmount = totalAmount.subtract(amount);
    }

    public void addCharge(String reason, BigDecimal amount) {
        charges.add(new Charge(reason, amount));
        totalAmount = totalAmount.add(amount);
    }

    public void removeFee(FeeStructure feeStructure) {
        feeItems.removeIf(item -> item.getFeeStructure().equals(feeStructure));
        recalculateTotal();
    }

    private void recalculateTotal() {
        totalAmount = feeItems.stream()
            .map(FeeItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmount = totalAmount.subtract(
            discounts.stream().map(Discount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        totalAmount = totalAmount.add(
            charges.stream().map(Charge::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
```

---

## 8. Caching Strategy

### 8.1 Cache Configuration

```java
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String STUDENT_PROFILE = "studentProfile";
    public static final String FEE_STRUCTURE = "feeStructure";
    public static final String CLASS_INFO = "classInfo";
    public static final String CONFIGURATION = "configuration";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
            STUDENT_PROFILE, cacheConfig(Duration.ofHours(1)),
            FEE_STRUCTURE, cacheConfig(Duration.ofHours(24)),
            CLASS_INFO, cacheConfig(Duration.ofHours(12)),
            CONFIGURATION, cacheConfig(Duration.ofHours(24))
        );

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig(Duration.ofMinutes(30)))
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }

    private RedisCacheConfiguration cacheConfig(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
    }
}
```

### 8.2 Cache Usage

```java
@Service
@Transactional
public class StudentProfileService {

    // Cache on read
    @Cacheable(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public StudentResponse getStudent(Long studentId) {
        // Database query
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));
        return studentMapper.toResponse(student);
    }

    // Evict cache on update
    @CacheEvict(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public StudentResponse updateStudent(Long studentId, UpdateStudentRequest request) {
        // Update logic
    }

    // Evict cache on delete
    @CacheEvict(value = CacheNames.STUDENT_PROFILE, key = "#studentId")
    public void deleteStudent(Long studentId) {
        // Delete logic
    }

    // Cache multiple entries
    @Cacheable(value = CacheNames.STUDENT_PROFILE, key = "#studentIds")
    public List<StudentResponse> getStudentsByIds(List<Long> studentIds) {
        List<Student> students = studentRepository.findAllById(studentIds);
        return students.stream()
            .map(studentMapper::toResponse)
            .toList();
    }
}
```

### 8.3 Cache Warming

```java
@Component
public class CacheWarmingScheduler {

    private final StudentRepository studentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 2 * * ?")  // 2 AM daily
    public void warmCaches() {
        log.info("Starting cache warming...");

        // Warm student cache (active students)
        List<Student> activeStudents = studentRepository.findByStatus(StudentStatus.ACTIVE);
        Cache studentCache = cacheManager.getCache(CacheNames.STUDENT_PROFILE);
        activeStudents.forEach(student ->
            studentCache.put(student.getStudentId(), studentMapper.toResponse(student))
        );

        // Warm fee structure cache
        List<FeeStructure> activeFees = feeStructureRepository.findByIsActive(true);
        Cache feeCache = cacheManager.getCache(CacheNames.FEE_STRUCTURE);
        activeFees.forEach(fee ->
            feeCache.put(fee.getFeeStructureId(), feeMapper.toResponse(fee))
        );

        log.info("Cache warming completed");
    }
}
```

---

## 9. Transaction Management

### 9.1 Declarative Transactions

```java
@Service
@Transactional  // Class-level: All methods transactional
public class PaymentRecordingService {

    @Transactional(readOnly = true)  // Read-only optimization
    public List<PaymentResponse> getPaymentHistory(Long studentId) {
        // Read operations
    }

    @Transactional(
        propagation = Propagation.REQUIRED,  // Default
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,  // 30 seconds
        rollbackFor = Exception.class
    )
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        // 1. Validate payment
        validatePayment(request);

        // 2. Create payment record
        Payment payment = createPayment(request);

        // 3. Update fee journals
        updateFeeJournals(payment, request.getJournalAllocations());

        // 4. Generate receipt
        Receipt receipt = generateReceipt(payment);

        // 5. Publish event
        publishPaymentEvent(payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // New transaction - commits independently
    public void logPaymentAttempt(RecordPaymentRequest request, String result) {
        // Logging in separate transaction
    }
}
```

### 9.2 Programmatic Transactions

```java
@Service
public class FeeJournalGenerationService {

    private final TransactionTemplate transactionTemplate;
    private final StudentRepository studentRepository;
    private final FeeJournalRepository feeJournalRepository;

    public void generateMonthlyJournals(YearMonth month) {
        List<Student> students = studentRepository.findByStatus(StudentStatus.ACTIVE);

        for (Student student : students) {
            transactionTemplate.execute(status -> {
                try {
                    generateJournalsForStudent(student, month);
                    return true;
                } catch (Exception e) {
                    log.error("Failed to generate journal for student: {}", student.getStudentId(), e);
                    status.setRollbackOnly();
                    return false;
                }
            });
        }
    }
}
```

---

## 10. Error Handling

### 10.1 Exception Hierarchy

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}

public class ValidationException extends BusinessException {
    private final List<FieldError> fieldErrors;

    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(ErrorCode.VALIDATION_ERROR, message);
        this.fieldErrors = fieldErrors;
    }
}

public enum ErrorCode {
    VALIDATION_ERROR("VAL_001", "Validation error"),
    NOT_FOUND("NFD_001", "Resource not found"),
    BUSINESS_RULE_VIOLATION("BUS_001", "Business rule violation"),
    DUPLICATE_ENTRY("DUP_001", "Duplicate entry"),
    UNAUTHORIZED("AUTH_001", "Unauthorized"),
    FORBIDDEN("AUTH_002", "Forbidden");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

### 10.2 Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed"
        );

        problemDetail.setType(URI.create("https://api.school.com/errors/validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage(),
                "rejectedValue", String.valueOf(error.getRejectedValue())
            ))
            .toList();

        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
        );

        problemDetail.setProperty("errorCode", ex.getErrorCode().getCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );

        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }
}
```

---

## 11. Performance Optimization

### 11.1 N+1 Query Prevention

```java
// BAD: N+1 query problem
List<Student> students = studentRepository.findAll();
for (Student student : students) {
    List<Guardian> guardians = student.getGuardians();  // Lazy load triggers N queries
    // Process guardians
}

// GOOD: Use JOIN FETCH
@Query("SELECT s FROM Student s LEFT JOIN FETCH s.guardians WHERE s.status = :status")
List<Student> findAllWithGuardians(@Param("status") StudentStatus status);

// GOOD: Use Entity Graph
@EntityGraph(attributePaths = {"guardians", "enrollments"})
List<Student> findByStatus(StudentStatus status);
```

### 11.2 Batch Operations

```java
@Service
public class FeeJournalBatchService {

    @Value("${batch.size:100}")
    private int batchSize;

    @Transactional
    public void generateJournalsInBatches(List<Student> students, YearMonth month) {
        List<FeeJournal> journals = new ArrayList<>();

        for (Student student : students) {
            FeeJournal journal = createJournalForStudent(student, month);
            journals.add(journal);

            if (journals.size() >= batchSize) {
                feeJournalRepository.saveAll(journals);
                feeJournalRepository.flush();
                entityManager.clear();  // Clear persistence context
                journals.clear();
            }
        }

        // Save remaining
        if (!journals.isEmpty()) {
            feeJournalRepository.saveAll(journals);
        }
    }
}
```

### 11.3 DTO Projections

```java
// Instead of loading full entity
public interface StudentSummaryProjection {
    Long getStudentId();
    String getStudentCode();
    String getFirstName();
    String getLastName();
    String getStatus();
}

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s.studentId as studentId, s.studentCode as studentCode, " +
           "s.firstName as firstName, s.lastName as lastName, s.status as status " +
           "FROM Student s WHERE s.status = :status")
    List<StudentSummaryProjection> findStudentSummaries(@Param("status") StudentStatus status);
}
```

---

## 12. Code Quality Standards

### 12.1 Code Style

- **Formatting**: Google Java Style Guide
- **Naming**: Descriptive names, no abbreviations
- **Methods**: Single responsibility, max 50 lines
- **Classes**: Max 500 lines
- **Comments**: Javadoc for public APIs, inline for complex logic

### 12.2 Static Analysis

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.10.0.2594</version>
</plugin>

<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.6</version>
</plugin>
```

### 12.3 Code Review Checklist

- [ ] Code follows project structure
- [ ] Business logic in domain/application layers
- [ ] Proper exception handling
- [ ] Input validation
- [ ] Unit tests written (80%+ coverage)
- [ ] No hardcoded values
- [ ] Logging added for important operations
- [ ] Performance considerations (N+1, caching)
- [ ] Security considerations (authorization, input sanitization)
- [ ] Documentation updated

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-11-10 | Backend Team | Initial version |

