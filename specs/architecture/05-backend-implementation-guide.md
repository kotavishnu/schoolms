# 05 - Backend Implementation Guide

## Cross-Reference Index
- API Contracts: `specs/sms_api_specification.yaml` (All schemas and paths)
- System Architecture: `specs/architecture/01-system-architecture.md` (Layer definitions)
- Database Design: `specs/architecture/02-database-design.md` (DDL, indexes)
- Business Rules: `specs/architecture/03-business-rules.md` (Drools integration)
- Security Architecture: `specs/architecture/04-security-architecture.md` (Validation, CORS)
- Testing Strategy: `specs/TESTING_STRATEGY.md` (Coverage targets)

---

## 1. Project Structure (Both Services)

```
student-service/
  src/
    main/
      java/com/sms/student/
        presentation/
          controller/
            StudentController.java
            EnrollmentController.java
          dto/
            request/
              CreateStudentRequest.java
              UpdateStudentRequest.java
              CreateEnrollmentRequest.java
            response/
              StudentResponse.java
              StudentSummaryResponse.java
              PagedStudentResponse.java
              EnrollmentHistoryResponse.java
            error/
              ErrorResponse.java
              FieldErrorDto.java
          advice/
            GlobalExceptionHandler.java
          filter/
            CorrelationIdFilter.java
        application/
          command/
            StudentCommandService.java
            EnrollmentCommandService.java
          query/
            StudentQueryService.java
            EnrollmentQueryService.java
          mapper/
            StudentMapper.java        (MapStruct)
            EnrollmentMapper.java
          exception/
            BusinessRuleViolationException.java
            StudentNotFoundException.java
            DuplicateMobileException.java
            OptimisticLockConflictException.java
        domain/
          model/
            Student.java              (Aggregate Root)
            Enrollment.java           (Entity)
          valueobject/
            StudentId.java
            Mobile.java
            AadhaarNumber.java
            StudentStatus.java        (Enum)
          repository/
            StudentRepository.java    (Interface)
            EnrollmentRepository.java (Interface)
          rules/
            StudentRegistrationFact.java
            EnrollmentFact.java
        infrastructure/
          persistence/
            entity/
              StudentJpaEntity.java
              EnrollmentJpaEntity.java
            repository/
              StudentJpaRepository.java   (Spring Data JPA)
              EnrollmentJpaRepository.java
              StudentRepositoryImpl.java  (implements domain StudentRepository)
              EnrollmentRepositoryImpl.java
            mapper/
              StudentInfraMapper.java   (MapStruct: JPA entity <-> domain)
          config/
            DroolsConfig.java
            HikariConfig.java
            RedisConfig.java            (if needed for student service - omit if not)
            OpenApiConfig.java
            CorsConfig.java
            MicrometerConfig.java
      resources/
        application.yml
        application-local.yml
        logback-spring.xml
        META-INF/kmodule.xml
        rules/student/
          StudentAgeValidation.drl
          StudentMobileValidation.drl
          EnrollmentValidation.drl
          ClassCapacityValidation.drl
        db/migration/
          V1__init_students.sql
          V2__init_enrollments.sql
    test/
      java/com/sms/student/
        domain/
          StudentAgeValidationDroolsTest.java
          StudentMobileValidationTest.java
        application/
          StudentCommandServiceTest.java
          StudentQueryServiceTest.java
        infrastructure/
          StudentRepositoryIntegrationTest.java  (TestContainers)
        presentation/
          StudentControllerTest.java             (MockMvc / @WebMvcTest)
```

---

## 2. Maven `pom.xml` Dependencies

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>

<properties>
    <java.version>21</java.version>
    <mapstruct.version>1.6.3</mapstruct.version>
    <lombok.version>1.18.36</lombok.version>
    <drools.version>9.44.0.Final</drools.version>
    <springdoc.version>2.7.0</springdoc.version>
</properties>

<dependencies>
    <!-- Spring Boot Core -->
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
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- Drools -->
    <dependency>
        <groupId>org.kie</groupId>
        <artifactId>kie-spring</artifactId>
        <version>${drools.version}</version>
    </dependency>
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

    <!-- MapStruct + Lombok -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <optional>true</optional>
    </dependency>

    <!-- Observability -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-otel</artifactId>
    </dependency>
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-zipkin</artifactId>
    </dependency>

    <!-- API Docs -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
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
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<!-- MapStruct + Lombok annotation processor ordering -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
                <compilerArgs>
                    <arg>-Amapstruct.defaultComponentModel=spring</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 3. Application Configuration (`application.yml`)

```yaml
spring:
  application:
    name: student-service

  datasource:
    url: ${STUDENT_DB_URL:jdbc:postgresql://localhost:5432/student_db}
    username: ${STUDENT_DB_USER:student_user}
    password: ${STUDENT_DB_PASSWORD:changeme}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      connection-timeout: 30000
      pool-name: StudentServicePool

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        default_batch_fetch_size: 25

  flyway:
    enabled: true
    locations: classpath:db/migration

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  tracing:
    sampling:
      probability: 1.0

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

server:
  port: 8081
```

---

## 4. Domain Layer Patterns

### 4.1 Rich Domain Model (Aggregate Root)

```java
// Domain Layer: com.sms.student.domain.model.Student
public class Student {

    private Long id;
    private StudentId studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Mobile mobile;
    private String email;
    private String address;
    private String fathersName;
    private String mothersName;
    private String identificationMark;
    private AadhaarNumber aadhaarNumber;
    private StudentStatus status;
    private Integer version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Factory method enforces mandatory fields
    public static Student create(
            String firstName, String lastName, LocalDate dateOfBirth,
            Mobile mobile, String email, String address,
            String fathersName, String mothersName,
            String identificationMark, AadhaarNumber aadhaarNumber) {
        Student s = new Student();
        s.firstName = Objects.requireNonNull(firstName, "firstName is required");
        s.lastName = Objects.requireNonNull(lastName, "lastName is required");
        s.dateOfBirth = Objects.requireNonNull(dateOfBirth, "dateOfBirth is required");
        s.mobile = Objects.requireNonNull(mobile, "mobile is required");
        s.email = email;
        s.address = address;
        s.fathersName = fathersName;
        s.mothersName = mothersName;
        s.identificationMark = identificationMark;
        s.aadhaarNumber = aadhaarNumber;
        s.status = StudentStatus.ACTIVE;
        return s;
    }

    // Only allowed update fields per BR-4
    public void updateAllowedFields(String firstName, String lastName,
                                     Mobile mobile, StudentStatus status) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.mobile = Objects.requireNonNull(mobile);
        this.status = Objects.requireNonNull(status);
    }
}
```

### 4.2 Value Objects

```java
// Domain Layer: com.sms.student.domain.valueobject.Mobile
public record Mobile(String value) {
    public Mobile {
        if (value == null || !value.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile must be exactly 10 digits");
        }
    }
}

// Domain Layer: com.sms.student.domain.valueobject.AadhaarNumber
public record AadhaarNumber(String value) {
    public AadhaarNumber {
        if (value != null && !value.matches("\\d{12}")) {
            throw new IllegalArgumentException("Aadhaar must be exactly 12 digits");
        }
    }
}
```

### 4.3 Repository Interface (Domain Layer)

```java
// Domain Layer: com.sms.student.domain.repository.StudentRepository
public interface StudentRepository {
    Student save(Student student);
    Optional<Student> findByStudentId(String studentId);
    Page<Student> findByLastName(String lastName, Pageable pageable);
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
    Page<Student> findAll(Pageable pageable);
    boolean existsByMobile(String mobile);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    void deleteByStudentId(String studentId);
}
```

---

## 5. Presentation Layer Patterns

### 5.1 REST Controller

```java
// Presentation Layer: com.sms.student.presentation.controller.StudentController
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management operations")
public class StudentController {

    private final StudentCommandService commandService;
    private final StudentQueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse registerStudent(@Valid @RequestBody CreateStudentRequest request) {
        return commandService.registerStudent(request);
    }

    @GetMapping
    public PagedStudentResponse searchStudents(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return queryService.searchStudents(lastName, status,
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy)));
    }

    @GetMapping("/{studentId}")
    public StudentResponse getStudent(@PathVariable String studentId) {
        return queryService.getByStudentId(studentId);
    }

    @PutMapping("/{studentId}")
    public StudentResponse updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequest request) {
        return commandService.updateStudent(studentId, request);
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable String studentId) {
        commandService.deleteStudent(studentId);
    }
}
```

### 5.2 Global Exception Handler (RFC 7807)

```java
// Presentation Layer: com.sms.student.presentation.advice.GlobalExceptionHandler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldErrorDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new FieldErrorDto(fe.getField(), fe.getDefaultMessage(), "VALIDATION_ERROR"))
            .toList();
        return ResponseEntity.badRequest().body(buildError(400,
            "Validation Failed", "Request validation failed", req, errors));
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            StudentNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(404).body(
            buildError(404, "Not Found", ex.getMessage(), req, List.of()));
    }

    @ExceptionHandler(DuplicateMobileException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMobile(
            DuplicateMobileException ex, HttpServletRequest req) {
        return ResponseEntity.status(409).body(
            buildError(409, "Conflict", ex.getMessage(), req, List.of()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(HttpServletRequest req) {
        return ResponseEntity.status(409).body(
            buildError(409, "Conflict", "Record was modified by another request. Retry with latest version.", req, List.of()));
    }

    private ErrorResponse buildError(int status, String title, String detail,
                                      HttpServletRequest req, List<FieldErrorDto> errors) {
        return ErrorResponse.builder()
            .type("https://api.school.com/errors/" + title.toLowerCase().replace(" ", "-"))
            .title(title).status(status).detail(detail)
            .instance(req.getRequestURI())
            .timestamp(OffsetDateTime.now())
            .correlationId((String) req.getAttribute("correlationId"))
            .errors(errors)
            .build();
    }
}
```

### 5.3 Correlation ID Filter

```java
// Presentation Layer: com.sms.student.presentation.filter.CorrelationIdFilter
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                     FilterChain chain) throws ServletException, IOException {
        String correlationId = Optional.ofNullable(req.getHeader(HEADER))
            .orElse(UUID.randomUUID().toString());
        req.setAttribute("correlationId", correlationId);
        MDC.put("correlationId", correlationId);
        res.setHeader(HEADER, correlationId);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

---

## 6. Infrastructure Layer Patterns

### 6.1 JPA Entity (Separate from Domain Model)

```java
// Infrastructure Layer: com.sms.student.infrastructure.persistence.entity.StudentJpaEntity
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class StudentJpaEntity {

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

    @Column(name = "mobile", nullable = false, unique = true, length = 10)
    private String mobile;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "fathers_name", length = 255)
    private String fathersName;

    @Column(name = "mothers_name", length = 255)
    private String mothersName;

    @Column(name = "identification_mark", length = 255)
    private String identificationMark;

    @Column(name = "aadhaar_number", unique = true, length = 12)
    private String aadhaarNumber;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StudentStatusJpa status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
}
```

### 6.2 Spring Data JPA Repository (Infrastructure)

```java
// Infrastructure Layer: com.sms.student.infrastructure.persistence.repository.StudentJpaRepository
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    @EntityGraph(attributePaths = {}) // prevent N+1 if associations added later
    Optional<StudentJpaEntity> findByStudentId(String studentId);

    @EntityGraph(attributePaths = {})
    Page<StudentJpaEntity> findByLastNameIgnoreCase(String lastName, Pageable pageable);

    Page<StudentJpaEntity> findByStatus(StudentStatusJpa status, Pageable pageable);

    boolean existsByMobile(String mobile);
    boolean existsByAadhaarNumber(String aadhaarNumber);

    @Modifying
    @Query("DELETE FROM StudentJpaEntity s WHERE s.studentId = :studentId")
    int deleteByStudentId(@Param("studentId") String studentId);
}
```

### 6.3 Repository Implementation (Domain Interface)

```java
// Infrastructure Layer: StudentRepositoryImpl
@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;
    private final StudentInfraMapper infraMapper;

    @Override
    public Student save(Student student) {
        StudentJpaEntity entity = infraMapper.toEntity(student);
        StudentJpaEntity saved = jpaRepository.save(entity);
        return infraMapper.toDomain(saved);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId).map(infraMapper::toDomain);
    }

    @Override
    public Page<Student> findByLastName(String lastName, Pageable pageable) {
        return jpaRepository.findByLastNameIgnoreCase(lastName, pageable)
            .map(infraMapper::toDomain);
    }
}
```

---

## 7. MapStruct Mapper Patterns

### 7.1 Application Layer Mapper (DTO <-> Domain)

```java
// Application Layer: com.sms.student.application.mapper.StudentMapper
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "mobile", expression = "java(new Mobile(req.getMobile()))")
    @Mapping(target = "aadhaarNumber",
             expression = "java(req.getAadhaarNumber() != null ? new AadhaarNumber(req.getAadhaarNumber()) : null)")
    Student toDomain(CreateStudentRequest req);

    @Mapping(source = "studentId.value", target = "studentId")
    @Mapping(source = "mobile.value", target = "mobile")
    @Mapping(source = "aadhaarNumber.value", target = "aadhaarNumber")
    StudentResponse toResponse(Student student);
}
```

### 7.2 Infrastructure Layer Mapper (JPA Entity <-> Domain)

```java
// Infrastructure Layer: com.sms.student.infrastructure.persistence.mapper.StudentInfraMapper
@Mapper(componentModel = "spring")
public interface StudentInfraMapper {

    @Mapping(source = "mobile.value", target = "mobile")
    @Mapping(source = "aadhaarNumber.value", target = "aadhaarNumber")
    @Mapping(source = "studentId.value", target = "studentId")
    StudentJpaEntity toEntity(Student domain);

    @Mapping(target = "mobile", expression = "java(new Mobile(entity.getMobile()))")
    @Mapping(target = "aadhaarNumber",
             expression = "java(entity.getAadhaarNumber() != null ? new AadhaarNumber(entity.getAadhaarNumber()) : null)")
    @Mapping(target = "studentId",
             expression = "java(new StudentId(entity.getStudentId()))")
    Student toDomain(StudentJpaEntity entity);
}
```

---

## 8. Performance Mandates

### 8.1 N+1 Prevention

Use `@EntityGraph` on all repository methods that fetch associations. When `Enrollment` list is accessed with a `Student`, use `@EntityGraph(attributePaths = {"enrollments"})`.

### 8.2 Batch Processing

Configure `hibernate.jdbc.batch_size=50` in `application.yml` for bulk inserts. Use `@Modifying(clearAutomatically = true)` on bulk update/delete queries.

### 8.3 HikariCP Tuning

```yaml
spring.datasource.hikari:
  minimum-idle: 5
  maximum-pool-size: 20
  idle-timeout: 300000
  connection-timeout: 30000
  max-lifetime: 1800000
  leak-detection-threshold: 60000
```

### 8.4 Redis Caching (Configuration Service)

```java
// Configuration Service Application Layer
@Cacheable(value = "configurations", key = "#category")
public GroupedConfigurationResponse getGroupedByCategory(String category) {
    // DB query, result cached in Redis
}

@CacheEvict(value = "configurations", key = "#category")
public ConfigurationResponse upsertConfiguration(String category, String key,
                                                   UpsertConfigurationRequest request) {
    // ...
}
```

Cache TTL: 10 minutes (600 seconds). Redis serialization: Jackson JSON.

---

## 9. Custom Micrometer Metrics

```java
// Infrastructure Layer: com.sms.student.infrastructure.config.MicrometerConfig
@Configuration
public class MicrometerConfig {

    @Bean
    public Counter studentsRegisteredCounter(MeterRegistry registry) {
        return Counter.builder("students.registered.total")
            .description("Total number of students registered")
            .register(registry);
    }

    @Bean
    public Counter studentsDeletedCounter(MeterRegistry registry) {
        return Counter.builder("students.deleted.total")
            .description("Total number of students deleted")
            .register(registry);
    }

    @Bean
    public Timer studentSearchTimer(MeterRegistry registry) {
        return Timer.builder("students.search.duration")
            .description("Duration of student search queries")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
    }
}
```

Usage in `StudentCommandService`:
```java
studentsRegisteredCounter.increment();
```

Usage in `StudentQueryService`:
```java
return studentSearchTimer.record(() -> studentRepository.findByLastName(lastName, pageable));
```

---

## 10. Docker Configuration

### Dockerfile (Student Service)

```dockerfile
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY target/student-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.9'

services:
  student-db:
    image: postgres:18-alpine
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: student_user
      POSTGRES_PASSWORD: ${STUDENT_DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - student-db-data:/var/lib/postgresql/data

  student-service:
    build: ./student-service
    ports:
      - "8081:8081"
    environment:
      STUDENT_DB_URL: jdbc:postgresql://student-db:5432/student_db
      STUDENT_DB_USER: student_user
      STUDENT_DB_PASSWORD: ${STUDENT_DB_PASSWORD}
    depends_on:
      - student-db
      - zipkin

  config-db:
    image: postgres:18-alpine
    environment:
      POSTGRES_DB: config_db
      POSTGRES_USER: config_user
      POSTGRES_PASSWORD: ${CONFIG_DB_PASSWORD}
    ports:
      - "5433:5433"
    volumes:
      - config-db-data:/var/lib/postgresql/data

  config-service:
    build: ./config-service
    ports:
      - "8082:8082"
    environment:
      CONFIG_DB_URL: jdbc:postgresql://config-db:5433/config_db
      CONFIG_DB_USER: config_user
      CONFIG_DB_PASSWORD: ${CONFIG_DB_PASSWORD}
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      - config-db
      - redis
      - zipkin

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  zipkin:
    image: openzipkin/zipkin:3
    ports:
      - "9411:9411"

volumes:
  student-db-data:
  config-db-data:
```

---

## 11. Testing Alignment

Per `specs/TESTING_STRATEGY.md`:

| Layer | Tool | Coverage Target | Key Tests |
|---|---|---|---|
| Domain | JUnit 5 + Drools | 95% | Age validation, Mobile value object, Enrollment uniqueness |
| Application | JUnit 5 + Mockito | 85% | CommandService, QueryService, MapStruct mappings |
| Infrastructure | TestContainers (PostgreSQL) | 70% | JPA repository queries, constraint violations |
| Presentation | MockMvc (`@WebMvcTest`) | 70% | Request validation, error response format |

Test data builder pattern:
```java
public class StudentTestDataBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private String mobile = "9876543210";

    public StudentTestDataBuilder withMobile(String mobile) {
        this.mobile = mobile; return this;
    }

    public CreateStudentRequest buildRequest() {
        return CreateStudentRequest.builder()
            .firstName(firstName).lastName(lastName)
            .dateOfBirth(dateOfBirth).mobile(mobile)
            .build();
    }
}
```
