# Technology Stack - School Management System (SMS)

## 1. Executive Summary

This document defines the complete technology stack for the School Management System. All technology choices are based on production-readiness, community support, performance requirements, and long-term maintainability.

### 1.1 Stack Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                       │
│  React 19 | Next.js 15 | TypeScript | Tailwind CSS          │
└─────────────────────────────────────────────────────────────┘
                              ↕ REST/JSON
┌─────────────────────────────────────────────────────────────┐
│                     API GATEWAY LAYER                         │
│             Spring Cloud Gateway | Java 21                   │
└─────────────────────────────────────────────────────────────┘
                              ↕ HTTP
┌─────────────────────────────────────────────────────────────┐
│                    MICROSERVICES LAYER                        │
│  Spring Boot 3.5 | Java 21 | Drools 9.44 | Spring Data JPA  │
└─────────────────────────────────────────────────────────────┘
                              ↕ JDBC
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                               │
│         PostgreSQL 18 | Redis 7 | Flyway Migration          │
└─────────────────────────────────────────────────────────────┘
                              ↕
┌─────────────────────────────────────────────────────────────┐
│                   OBSERVABILITY LAYER                         │
│  Zipkin | Prometheus | Micrometer | Grafana | ELK (Future)  │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Backend Technologies

### 2.1 Core Framework

#### Spring Boot 3.5.0
**Purpose:** Primary backend framework for microservices

**Rationale:**
- Mature ecosystem with 10+ years of production usage
- Excellent Spring Data JPA integration
- Built-in support for observability (Actuator, Micrometer)
- Strong community and enterprise support
- Auto-configuration reduces boilerplate
- Production-ready features out-of-the-box

**Key Features Used:**
- Spring Web MVC (REST controllers)
- Spring Data JPA (database access)
- Spring Boot Actuator (health checks, metrics)
- Spring Cache (Redis integration)
- Spring Validation (JSR-380)

**Configuration:**
```yaml
spring:
  boot:
    version: 3.5.0
  profiles:
    active: ${SPRING_PROFILE:dev}
```

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.5.0</version>
</dependency>
```

---

### 2.2 Programming Language

#### Java 21 (LTS)
**Purpose:** Primary programming language

**Rationale:**
- Long-Term Support (LTS) until 2029
- Performance improvements (Project Loom, Virtual Threads)
- Pattern matching for switch expressions
- Record types for immutable DTOs
- Sealed classes for domain modeling
- Strong type safety and IDE support

**Key Java 21 Features Used:**
```java
// Record types for DTOs
public record StudentDTO(
    String studentKey,
    String firstName,
    String lastName,
    String status
) {}

// Pattern matching for switch
String statusMessage = switch (student.getStatus()) {
    case ACTIVE -> "Currently enrolled";
    case INACTIVE -> "Not enrolled";
    case GRADUATED -> "Completed studies";
};

// Virtual Threads for I/O operations
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> studentService.findAll());
}
```

**JVM Configuration:**
```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-Xms512m
-Xmx2048m
--enable-preview (for experimental features)
```

---

### 2.3 Database Access

#### Spring Data JPA 3.2
**Purpose:** Object-relational mapping and data access

**Rationale:**
- Reduces boilerplate CRUD code
- Type-safe query methods
- Pagination and sorting support
- Auditing capabilities
- Transaction management
- Support for entity graphs (N+1 prevention)

**Key Features:**
```java
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Query method derivation
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    // Custom JPQL query
    @Query("SELECT s FROM Student s WHERE s.fatherNameOrGuardian LIKE %:name%")
    List<Student> searchByGuardianName(@Param("name") String name);

    // Entity graph for eager fetching
    @EntityGraph(attributePaths = {"enrollmentHistory"})
    List<Student> findAllWithHistory();

    // Projection for performance
    List<StudentSummaryProjection> findAllProjectedBy();
}
```

#### Hibernate 6.4
**Purpose:** JPA implementation

**Configuration:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        show_sql: false
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

---

### 2.4 Database

#### PostgreSQL 18
**Purpose:** Primary relational database

**Rationale:**
- ACID compliance for data integrity
- Advanced indexing (B-tree, GiST, GIN)
- Full-text search capabilities
- JSONB support for flexible data
- Excellent performance for OLTP workloads
- Strong community and tooling support
- Mature replication and backup solutions

**Key Features Used:**
- BIGSERIAL for primary keys
- Partial indexes for performance
- Check constraints for validation
- Triggers for audit trails
- JSONB for audit log storage
- Full-text search for student names

**Version Requirements:**
```
Minimum: PostgreSQL 18
Recommended: PostgreSQL 18.1+
Extensions: pg_stat_statements, pgcrypto
```

**Connection Configuration:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

### 2.5 Caching

#### Redis 7.2
**Purpose:** Distributed caching and session storage

**Rationale:**
- Sub-millisecond latency
- Rich data structures (strings, hashes, lists, sets)
- Built-in pub/sub for real-time features
- Persistence options (RDB + AOF)
- Cluster mode for high availability
- Spring Cache abstraction support

**Use Cases:**
- Query result caching (student searches)
- Configuration settings cache
- Session storage (Phase 2: JWT blacklist)
- Rate limiting counters

**Configuration:**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
  cache:
    type: redis
    redis:
      time-to-live: 300000 # 5 minutes
      cache-null-values: false
```

**Cache Implementation:**
```java
@Cacheable(value = "students", key = "#studentKey")
public StudentDTO getStudent(String studentKey) {
    return studentRepository.findByStudentKey(studentKey)
        .map(studentMapper::toDTO)
        .orElseThrow(() -> new StudentNotFoundException(studentKey));
}

@CacheEvict(value = "students", key = "#studentKey")
public void updateStudent(String studentKey, StudentUpdateRequest request) {
    // Update logic
}
```

---

### 2.6 Business Rules Engine

#### Drools 9.44.0.Final
**Purpose:** Externalized business rules management

**Rationale:**
- Separate business logic from application code
- Business users can modify rules without deployments
- Complex rule chaining and decision tables
- Performance-optimized rule engine (Rete algorithm)
- Integration with Spring Boot

**Rules Implemented:**
- Age validation (3-18 years)
- Mobile number uniqueness
- Class capacity enforcement (future)
- Fee calculation rules (future)

**Sample Rule (DRL):**
```java
package com.sms.rules;

import com.sms.domain.Student;
import java.time.LocalDate;
import java.time.Period;

rule "Student Age Validation"
    when
        $student: Student(dateOfBirth != null)
        $age: Integer(intValue < 3 || intValue > 18) from Period.between($student.getDateOfBirth(), LocalDate.now()).getYears()
    then
        throw new BusinessRuleViolationException("Student age must be between 3 and 18 years. Current age: " + $age);
end
```

**Configuration:**
```java
@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student-validation.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }
}
```

---

### 2.7 API Gateway

#### Spring Cloud Gateway 4.1
**Purpose:** Unified entry point for all microservices

**Rationale:**
- Non-blocking, reactive architecture
- Dynamic routing based on predicates
- Built-in filters (rate limiting, CORS, logging)
- Circuit breaker integration
- Load balancing support
- Native Spring Boot integration

**Features Used:**
- Path-based routing
- Request/response logging
- CORS configuration
- Rate limiting
- Header manipulation

**Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: student-service
          uri: lb://student-service
          predicates:
            - Path=/api/v1/students/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
        - id: config-service
          uri: lb://config-service
          predicates:
            - Path=/api/v1/configurations/**, /api/v1/school/**
```

---

### 2.8 DTO Mapping

#### MapStruct 1.6.0
**Purpose:** Type-safe bean mapping

**Rationale:**
- Compile-time code generation (no reflection)
- Type-safe mapping
- Faster than reflection-based mappers
- Clear compilation errors for missing mappings
- Support for custom mapping methods

**Example Mapper:**
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StudentMapper {

    @Mapping(target = "age", expression = "java(calculateAge(student.getDateOfBirth()))")
    StudentDTO toDTO(Student student);

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "studentKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(CreateStudentRequest request);

    default int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
```

---

### 2.9 Validation

#### Hibernate Validator 8.0 (JSR-380)
**Purpose:** Bean validation framework

**Rationale:**
- Standard Java validation API
- Declarative validation constraints
- Custom validator support
- Spring Boot integration
- Clear error messages

**Example:**
```java
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile number format")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{12}$", message = "Adhaar number must be 12 digits")
    private String adhaarNumber;
}
```

---

### 2.10 Utility Libraries

#### Lombok 1.18.30
**Purpose:** Reduce boilerplate code

**Rationale:**
- Reduces getter/setter boilerplate
- Automatic constructor generation
- Builder pattern generation
- Cleaner code for domain entities

**Example:**
```java
@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private String studentKey;
    private String firstName;
    private String lastName;
    // ... other fields
}
```

---

### 2.11 Database Migration

#### Flyway 10.4
**Purpose:** Version-controlled database migrations

**Rationale:**
- SQL-based migrations (familiar to DBAs)
- Version control for schema changes
- Repeatable migrations for views/functions
- Rollback support
- Multiple database support

**Configuration:**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
```

**Migration Naming:**
```
V1.0.0__Create_student_table.sql
V1.0.1__Create_enrollment_history_table.sql
V1.1.0__Add_email_column_to_student.sql
R__Create_student_search_view.sql (repeatable)
```

---

## 3. Frontend Technologies

### 3.1 Core Framework

#### React 19
**Purpose:** UI library for building interactive user interfaces

**Rationale:**
- Component-based architecture
- Virtual DOM for performance
- Large ecosystem of libraries
- Strong community support
- Concurrent rendering features
- Server components support

**Key Features Used:**
- Functional components with hooks
- Context API for global state
- Suspense for data fetching
- Error boundaries
- Memoization (React.memo, useMemo, useCallback)

#### Next.js 15 (App Router)
**Purpose:** React framework with server-side rendering

**Rationale:**
- Server-side rendering (SSR) for SEO
- Static site generation (SSG)
- API routes (if needed)
- File-based routing
- Automatic code splitting
- Image optimization
- Built-in TypeScript support

**Configuration:**
```javascript
// next.config.js
module.exports = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['cdn.sms.com'],
  },
  experimental: {
    appDir: true,
  },
};
```

---

### 3.2 Programming Language

#### TypeScript 5.3
**Purpose:** Typed superset of JavaScript

**Rationale:**
- Static type checking (catch errors at compile time)
- Better IDE support (autocomplete, refactoring)
- Self-documenting code
- Safer refactoring
- Interface definitions for API contracts

**Example:**
```typescript
// Type-safe API response
interface Student {
  studentId: number;
  studentKey: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  age: number;
  mobile: string;
  status: 'Active' | 'Inactive';
  createdAt: string;
  updatedAt: string;
}

// Type-safe function
function calculateAge(dateOfBirth: string): number {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);
  return today.getFullYear() - birthDate.getFullYear();
}
```

**tsconfig.json:**
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "jsx": "preserve",
    "module": "ESNext",
    "moduleResolution": "bundler",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "incremental": true
  }
}
```

---

### 3.3 Styling

#### Tailwind CSS 3.4
**Purpose:** Utility-first CSS framework

**Rationale:**
- Rapid UI development
- Consistent design system
- Small bundle size (purges unused CSS)
- No CSS naming conventions needed
- Responsive design utilities
- Dark mode support

**Configuration:**
```javascript
// tailwind.config.js
module.exports = {
  content: [
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6',
        secondary: '#10B981',
        danger: '#EF4444',
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
};
```

**Example:**
```tsx
<button className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition">
  Register Student
</button>
```

---

### 3.4 State Management

#### React Query 4.x (TanStack Query)
**Purpose:** Server state management

**Rationale:**
- Automatic caching and invalidation
- Background refetching
- Optimistic updates
- Request deduplication
- Pagination support
- Error handling

**Example:**
```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// Fetch students
const { data, isLoading, error } = useQuery({
  queryKey: ['students', { lastName }],
  queryFn: () => fetchStudents({ lastName }),
  staleTime: 5 * 60 * 1000, // 5 minutes
});

// Create student mutation
const queryClient = useQueryClient();
const createMutation = useMutation({
  mutationFn: createStudent,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['students'] });
  },
});
```

#### Context API
**Purpose:** Client-side global state

**Rationale:**
- Built-in React feature (no extra dependency)
- Simple API for prop drilling avoidance
- Suitable for theme, locale, user preferences

**Example:**
```typescript
const ThemeContext = createContext<'light' | 'dark'>('light');

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}
```

---

### 3.5 Form Management

#### React Hook Form 7.49
**Purpose:** Form state management and validation

**Rationale:**
- Minimal re-renders (better performance)
- Easy integration with validation libraries
- Built-in error handling
- TypeScript support
- Small bundle size

**Example:**
```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

const { register, handleSubmit, formState: { errors } } = useForm({
  resolver: zodResolver(studentSchema),
});

const onSubmit = (data: StudentFormData) => {
  createMutation.mutate(data);
};

<form onSubmit={handleSubmit(onSubmit)}>
  <input {...register('firstName')} />
  {errors.firstName && <span>{errors.firstName.message}</span>}
</form>
```

#### Zod 3.22
**Purpose:** Schema validation

**Rationale:**
- Type-safe validation schemas
- Infers TypeScript types from schema
- Composable validators
- Clear error messages
- Runtime and compile-time safety

**Example:**
```typescript
import { z } from 'zod';

const studentSchema = z.object({
  firstName: z.string().min(1, "First name is required").max(100),
  lastName: z.string().min(1, "Last name is required").max(100),
  dateOfBirth: z.string().refine((date) => {
    const age = calculateAge(date);
    return age >= 3 && age <= 18;
  }, "Age must be between 3 and 18"),
  mobile: z.string().regex(/^\+?[0-9]{10,15}$/, "Invalid mobile format"),
  email: z.string().email().optional(),
});

type StudentFormData = z.infer<typeof studentSchema>;
```

---

### 3.6 HTTP Client

#### Axios 1.6
**Purpose:** HTTP client for API requests

**Rationale:**
- Promise-based API
- Request/response interceptors
- Automatic JSON transformation
- CSRF protection
- Request cancellation
- Better error handling than fetch

**Configuration:**
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use((config) => {
  const correlationId = uuidv4();
  config.headers['X-Correlation-ID'] = correlationId;
  return config;
});

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle RFC 7807 error format
    const problemDetails = error.response?.data;
    return Promise.reject(new ApiError(problemDetails));
  }
);
```

---

### 3.7 Routing

#### React Router 6.x
**Purpose:** Client-side routing (if not using Next.js routing)

**Note:** With Next.js 15 App Router, file-based routing is preferred.

**Next.js App Router Structure:**
```
app/
├── layout.tsx (Root layout)
├── page.tsx (Home page)
├── students/
│   ├── page.tsx (Student list)
│   ├── [studentKey]/
│   │   └── page.tsx (Student detail)
│   └── new/
│       └── page.tsx (Create student)
└── configurations/
    └── page.tsx (Configuration management)
```

---

### 3.8 Build Tool

#### Vite 5.x
**Purpose:** Fast frontend build tool

**Rationale:**
- Lightning-fast HMR (Hot Module Replacement)
- Native ES modules in development
- Optimized production builds
- Plugin ecosystem
- TypeScript support out-of-the-box

**Note:** Next.js 15 uses Turbopack (successor to Webpack), so Vite is optional if using standalone React.

---

## 4. Observability Stack

### 4.1 Distributed Tracing

#### Zipkin
**Purpose:** Distributed request tracing

**Rationale:**
- Visualize request flow across services
- Identify performance bottlenecks
- Debug latency issues
- Correlate logs with traces

**Configuration:**
```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0 # 100% sampling in dev, 10% in prod
```

**Usage:**
```java
@Slf4j
@RestController
public class StudentController {

    @GetMapping("/students/{studentKey}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable String studentKey) {
        // Zipkin automatically traces this request
        log.info("Fetching student: {}", studentKey);
        return ResponseEntity.ok(studentService.getStudent(studentKey));
    }
}
```

---

### 4.2 Metrics Collection

#### Micrometer 1.12
**Purpose:** Application metrics facade

**Rationale:**
- Vendor-neutral metrics API
- Multiple backend support (Prometheus, Graphite, etc.)
- Spring Boot Actuator integration
- Custom metrics support

**Metrics Exposed:**
- JVM metrics (memory, GC, threads)
- HTTP metrics (request count, duration, status)
- Database metrics (connection pool, query time)
- Cache metrics (hit/miss ratio)
- Custom business metrics

**Custom Metrics:**
```java
@Component
public class StudentMetrics {

    private final Counter studentRegistrations;
    private final Gauge activeStudents;

    public StudentMetrics(MeterRegistry registry) {
        this.studentRegistrations = Counter.builder("students.registered.total")
            .description("Total number of student registrations")
            .register(registry);

        this.activeStudents = Gauge.builder("students.active.count", studentRepository, StudentRepository::countByStatus)
            .description("Number of active students")
            .register(registry);
    }
}
```

#### Prometheus
**Purpose:** Metrics storage and querying

**Rationale:**
- Time-series database
- Powerful query language (PromQL)
- Service discovery
- Alerting support

**Spring Boot Endpoint:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Prometheus Scrape Config:**
```yaml
scrape_configs:
  - job_name: 'student-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
  - job_name: 'config-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8082']
```

---

### 4.3 Visualization

#### Grafana
**Purpose:** Metrics visualization and dashboards

**Rationale:**
- Rich visualization options
- Alerting rules
- Multiple data source support
- Template variables for dynamic dashboards

**Dashboards:**
- JVM Metrics (heap, GC, threads)
- HTTP Request Rates (RPS, latency percentiles)
- Database Performance (query time, connection pool)
- Business Metrics (student registrations, active count)

---

### 4.4 Logging

#### SLF4J + Logback
**Purpose:** Logging framework

**Rationale:**
- Standard logging facade
- High performance
- Flexible configuration
- Async appenders
- Rolling file policies

**Configuration:**
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <fieldNames>
                <timestamp>timestamp</timestamp>
                <message>message</message>
                <logger>logger</logger>
                <level>level</level>
            </fieldNames>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

**Structured Logging:**
```java
log.info("Student created successfully",
    kv("studentKey", student.getStudentKey()),
    kv("correlationId", correlationId),
    kv("userId", currentUser.getId())
);
```

---

## 5. Testing Stack

### 5.1 Backend Testing

#### JUnit 5 (Jupiter)
**Purpose:** Unit testing framework

**Example:**
```java
@Test
void shouldCreateStudent() {
    // Given
    CreateStudentRequest request = new CreateStudentRequest();
    request.setFirstName("Rahul");
    request.setLastName("Sharma");

    // When
    StudentDTO result = studentService.createStudent(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStudentKey()).startsWith("STU-2025-");
}
```

#### Mockito 5.x
**Purpose:** Mocking framework

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(studentRepository.findByStudentKey("STU-2025-9999"))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(StudentNotFoundException.class, () -> {
            studentService.getStudent("STU-2025-9999");
        });
    }
}
```

#### AssertJ
**Purpose:** Fluent assertions

**Rationale:**
- Readable test assertions
- Better error messages
- Type-safe assertions

#### TestContainers 1.19
**Purpose:** Integration testing with real databases

**Example:**
```java
@Testcontainers
@SpringBootTest
class StudentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveAndRetrieveStudent() {
        // Test with real PostgreSQL container
    }
}
```

#### REST Assured
**Purpose:** REST API testing

**Example:**
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StudentApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldCreateStudent() {
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(createStudentRequest)
        .when()
            .post("/api/v1/students")
        .then()
            .statusCode(201)
            .header("Location", startsWith("/api/v1/students/STU-"))
            .body("studentKey", startsWith("STU-2025-"));
    }
}
```

---

### 5.2 Frontend Testing

#### Vitest
**Purpose:** Unit testing framework for Vite projects

**Rationale:**
- Vite-native (fast)
- Jest-compatible API
- TypeScript support

**Example:**
```typescript
import { describe, it, expect } from 'vitest';

describe('calculateAge', () => {
  it('should calculate correct age', () => {
    const age = calculateAge('2012-05-15');
    expect(age).toBe(13);
  });
});
```

#### React Testing Library
**Purpose:** Component testing

**Example:**
```typescript
import { render, screen, fireEvent } from '@testing-library/react';

test('should display validation error for invalid mobile', async () => {
  render(<StudentForm />);

  const mobileInput = screen.getByLabelText('Mobile Number');
  fireEvent.change(mobileInput, { target: { value: '123' } });
  fireEvent.blur(mobileInput);

  expect(await screen.findByText('Invalid mobile format')).toBeInTheDocument();
});
```

#### Playwright
**Purpose:** End-to-end testing

**Rationale:**
- Cross-browser support
- Auto-wait mechanism
- Network interception
- Screenshot/video recording

**Example:**
```typescript
import { test, expect } from '@playwright/test';

test('should register a new student', async ({ page }) => {
  await page.goto('http://localhost:3000/students/new');

  await page.fill('input[name="firstName"]', 'Rahul');
  await page.fill('input[name="lastName"]', 'Sharma');
  await page.fill('input[name="dateOfBirth"]', '2012-05-15');
  await page.fill('input[name="mobile"]', '+919876543210');

  await page.click('button[type="submit"]');

  await expect(page).toHaveURL(/students\/STU-2025-\d+/);
  await expect(page.locator('h1')).toContainText('Rahul Sharma');
});
```

---

## 6. Development Tools

### 6.1 Build Tools

#### Maven 3.9
**Purpose:** Backend build and dependency management

**pom.xml:**
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
    </parent>

    <groupId>com.sms</groupId>
    <artifactId>student-service</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
    </properties>
</project>
```

#### npm/pnpm
**Purpose:** Frontend package management

**Rationale for pnpm:**
- Faster than npm/yarn
- Disk space efficient
- Strict dependency resolution

---

### 6.2 Code Quality

#### SonarQube
**Purpose:** Static code analysis

**Metrics Tracked:**
- Code coverage (target: 80%)
- Code smells
- Security vulnerabilities
- Technical debt

#### ESLint + Prettier
**Purpose:** JavaScript/TypeScript linting and formatting

**Configuration:**
```javascript
// .eslintrc.js
module.exports = {
  extends: [
    'next/core-web-vitals',
    'plugin:@typescript-eslint/recommended',
    'prettier',
  ],
  rules: {
    '@typescript-eslint/no-unused-vars': 'error',
    'no-console': 'warn',
  },
};
```

---

### 6.3 API Documentation

#### Springdoc OpenAPI 2.3
**Purpose:** OpenAPI 3.0 documentation generation

**Configuration:**
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
```

**Annotations:**
```java
@Operation(summary = "Create a new student", tags = {"Student Management"})
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Student created successfully"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "409", description = "Duplicate mobile number")
})
@PostMapping("/students")
public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody CreateStudentRequest request) {
    // Implementation
}
```

---

## 7. DevOps & Deployment

### 7.1 Containerization

#### Docker 24.x
**Purpose:** Application containerization

**Backend Dockerfile:**
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/student-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package*.json ./
RUN npm ci --production
EXPOSE 3000
CMD ["npm", "start"]
```

---

### 7.2 Orchestration

#### Docker Compose (Development)
**Purpose:** Local multi-container setup

**docker-compose.yml:**
```yaml
version: '3.9'
services:
  postgres:
    image: postgres:18
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: sms_user
      POSTGRES_PASSWORD: secure_password
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  student-service:
    build: ./student-service
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/student_db
      SPRING_DATA_REDIS_HOST: redis
    ports:
      - "8081:8081"
    depends_on:
      - postgres
      - redis
```

#### Kubernetes (Production)
**Purpose:** Container orchestration at scale

**Deployment Example:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: student-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: student-service
  template:
    metadata:
      labels:
        app: student-service
    spec:
      containers:
      - name: student-service
        image: sms/student-service:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: production
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

---

### 7.3 CI/CD

#### GitHub Actions
**Purpose:** Continuous integration and deployment

**Workflow Example:**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
      - name: Build with Maven
        run: mvn clean package
      - name: Run tests
        run: mvn test
      - name: SonarQube analysis
        run: mvn sonar:sonar
      - name: Build Docker image
        run: docker build -t sms/student-service:${{ github.sha }} .
```

---

## 8. Security Tools

### 8.1 Dependency Scanning

#### OWASP Dependency Check
**Purpose:** Identify vulnerable dependencies

**Maven Plugin:**
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.0</version>
</plugin>
```

#### Trivy
**Purpose:** Container image scanning

**Usage:**
```bash
trivy image sms/student-service:1.0.0
```

---

### 8.2 Secrets Management

#### Spring Cloud Vault (Future)
**Purpose:** Secure secrets storage

**Configuration:**
```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      authentication: TOKEN
      token: ${VAULT_TOKEN}
```

---

## 9. Technology Decision Matrix

| Requirement | Technology | Alternatives Considered | Reason for Choice |
|-------------|-----------|------------------------|-------------------|
| Backend Framework | Spring Boot 3.5 | Node.js/NestJS, .NET | Maturity, JPA integration, team expertise |
| Language | Java 21 | Kotlin, Go | LTS support, performance, strong typing |
| Database | PostgreSQL 18 | MySQL, MongoDB | ACID compliance, advanced features |
| Cache | Redis 7 | Memcached, Hazelcast | Data structures, persistence, pub/sub |
| Rules Engine | Drools 9.44 | Hardcoded rules, Easy Rules | Business user editable, performance |
| Frontend Framework | React 19 | Angular, Vue.js | Ecosystem, performance, flexibility |
| Meta-Framework | Next.js 15 | Remix, Gatsby | SSR, SSG, routing, image optimization |
| State Management | React Query | Redux, Zustand | Server state focus, caching, simple API |
| Form Library | React Hook Form | Formik | Performance, minimal re-renders |
| Validation | Zod | Yup, Joi | Type inference, composability |
| HTTP Client | Axios | fetch, ky | Interceptors, better API |
| Styling | Tailwind CSS | CSS Modules, Styled Components | Rapid development, consistency |
| Testing (Backend) | JUnit 5 | TestNG | Modern API, better parameterized tests |
| Testing (Frontend) | Vitest | Jest | Vite-native, faster |
| E2E Testing | Playwright | Cypress, Selenium | Cross-browser, auto-wait, speed |
| Tracing | Zipkin | Jaeger, OpenTelemetry | Simple setup, Spring integration |
| Metrics | Prometheus | Graphite, InfluxDB | Industry standard, powerful queries |
| Visualization | Grafana | Kibana | Best for Prometheus, rich dashboards |

---

## 10. Version Compatibility Matrix

| Component | Version | Compatibility Notes |
|-----------|---------|---------------------|
| Java | 21 LTS | Minimum Java 21, LTS until 2029 |
| Spring Boot | 3.5.0 | Requires Java 17+, supports Java 21 |
| PostgreSQL | 18.x | Client backward compatible with 15+ |
| Redis | 7.2+ | Client supports Redis 6+ |
| Node.js | 20 LTS | Minimum Node 18, LTS until 2026 |
| React | 19.x | Breaking changes from React 18 |
| Next.js | 15.x | Requires React 19+ |
| TypeScript | 5.3+ | Minimum 5.0 for latest features |

---

## 11. Conclusion

This technology stack provides a solid foundation for building a production-ready School Management System. Key strengths:

1. **Backend**: Spring Boot + Java 21 provides enterprise-grade stability
2. **Database**: PostgreSQL offers ACID compliance and advanced features
3. **Frontend**: React 19 + Next.js 15 ensures modern UX with SSR
4. **Observability**: Zipkin + Prometheus + Grafana for full visibility
5. **Testing**: Comprehensive testing pyramid with JUnit, Vitest, Playwright
6. **Performance**: Redis caching, optimistic locking, connection pooling

All technologies are LTS versions with active community support and proven production usage.

---

**Document Version**: 1.0
**Last Updated**: 2025-12-08
**Next Review**: 2025-12-22
**Status**: Approved for Implementation
