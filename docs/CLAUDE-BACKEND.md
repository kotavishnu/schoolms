# CLAUDE-BACKEND.md

Backend guidance for Claude Code working with the School Management System Spring Boot application.

## Tech Stack
- **Framework**: Spring Boot 3.5 + Java 25
- **Database**: PostgreSQL 18+
- **ORM**: Spring Data JPA/Hibernate
- **Rules Engine**: Drools (fee calculation)
- **Build**: Maven

## Quick Commands

```bash
cd backend
mvn clean install          # Build
mvn spring-boot:run        # Run (http://localhost:8080)
mvn test                   # All tests
mvn test -Dtest=ClassName  # Specific test
mvn clean package          # Create JAR
```

## Architecture Pattern: Layered

```
com.school.management/
├── controller/    # REST endpoints (@RestController)
├── service/       # Business logic (@Service, @Transactional)
├── repository/    # Data access (JpaRepository)
├── model/         # JPA entities (@Entity)
├── dto/           # Request/Response objects
├── config/        # App configuration (@Configuration)
├── exception/     # Global error handling (@ControllerAdvice)
└── util/          # Helpers & constants
```

## Core Domain Models

**Student**: firstName, lastName, dob, address, caste, mobile, religion, molesOnBody, motherName, fatherName, classId, enrollmentDate, status

**SchoolConfig**: name, address, feeFrequency (MONTHLY/QUARTERLY/YEARLY)

**Class**: classNumber (1-10), section, academicYear, capacity

**FeeMaster**: feeType, amount, applicableClasses, frequency, academicYear

**FeeJournal**: studentId, feeReceiptId, paymentDate, amount, monthPaid, isPending

**FeeReceipt**: receiptNumber, studentId, amount, paymentDate, paymentMethod, generatedAt

## REST API Conventions

```
POST   /api/students              # Create
GET    /api/students/{id}         # Read one
GET    /api/students?classId={id} # Query
PUT    /api/students/{id}         # Update
DELETE /api/students/{id}         # Delete
```

**Flow**: Request → Controller (validate DTO) → Service (business logic) → Repository (DB) → Response

## Database Setup

```sql
CREATE DATABASE school_management_db;
```
Tables auto-created by Hibernate (ddl-auto=update). Seed data via `@PostConstruct`:

```java
@Component
public class DataInitializer {
    @Autowired private ClassRepository classRepository;
    
    @PostConstruct
    public void init() {
        if (classRepository.count() == 0) {
            // Seed Class 1-10
        }
    }
}
```

## Drools Rules Engine

**Purpose**: Calculate student fees based on class + add-ons (library, computer, special fee for first month)

**Setup**:
1. Add Drools dependency to `pom.xml`
2. Create `.drl` files in `src/main/resources/rules/`
3. Configure `KieContainer` bean in config
4. Inject into `FeeCalculationService`

**Example Rule Structure**:
```java
rule "Calculate Class 1-5 Base Fee"
when
    $student : Student(classNumber >= 1 && classNumber <= 5)
then
    // Set base fee
end
```

## Configuration

**application.properties**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/school_management_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

**CORS** (WebConfig.java):
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*");
    }
}
```

## Error Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
```

## Test-Driven Development

**Unit Tests** (Service layer with Mockito):
```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentRepository repository;
    @InjectMocks private StudentService service;
    
    @Test
    void shouldCreateStudent() {
        // Given-When-Then
    }
}
```

**Integration Tests** (Repository):
```java
@DataJpaTest
class StudentRepositoryTest {
    @Autowired private StudentRepository repository;
    // Test DB operations
}
```

**Controller Tests** (MockMvc):
```java
@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private StudentService service;
    // Test endpoints
}
```

## Code Patterns

**Use Lombok**: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

**Service Transactions**: Add `@Transactional` to write operations

**DTO Validation**: Use `@Valid` + annotations (`@NotNull`, `@Size`, `@Email`)

## Adding New Features

1. Create Entity (model/) with JPA annotations
2. Create Repository interface
3. Create Request/Response DTOs
4. Write Service with TDD (test first)
5. Create Controller with validation
6. Update Drools rules if fee-related