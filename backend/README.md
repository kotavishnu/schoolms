# School Management System - Backend

Complete Spring Boot 3.5.0 backend with Java 21, PostgreSQL, and Drools integration.

## Project Status

### Completed Components ✅
- ✅ Maven project structure with proper package hierarchy
- ✅ pom.xml with all dependencies (Spring Boot 3.5.0, PostgreSQL 42.7.1, Drools 9.44.0.Final, Lombok, MapStruct)
- ✅ Application properties for all profiles (default, dev, prod)
- ✅ Main Spring Boot application class
- ✅ All 6 JPA entities with proper annotations
- ✅ All 6 repository interfaces with custom queries
- ✅ Custom exception classes (ResourceNotFoundException, ValidationException, DuplicateResourceException)
- ✅ Core DTO classes (ApiResponse, ErrorResponse, StudentRequestDTO, StudentResponseDTO)

### Remaining Components ⏳
- ⏳ Additional DTO classes (Class, Fee, Receipt DTOs)
- ⏳ MapStruct mappers
- ⏳ Service layer (5 services)
- ⏳ Controller layer (5 controllers)
- ⏳ Configuration classes (Drools, Web, Global Exception Handler, Data Initializer)
- ⏳ Drools rule files
- ⏳ Comprehensive test suite

## Build and Run

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 14+
- IntelliJ IDEA or Eclipse with Lombok plugin

### Database Setup

```sql
CREATE DATABASE school_management_db;
CREATE USER postgres WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE school_management_db TO postgres;
```

### Build Commands

```bash
# Navigate to backend directory
cd backend

# Clean and install (includes tests)
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests only
mvn test

# Generate test coverage report
mvn clean test jacoco:report
# View report: target/site/jacoco/index.html
```

## Architecture

### Layered Architecture

```
HTTP Request → Controller (REST endpoints, validation)
                    ↓
              Service (Business logic, transactions)
                    ↓
              Repository (Data access, JPA queries)
                    ↓
              Database (PostgreSQL)
```

### Package Structure

```
com.school.management
├── SchoolManagementApplication.java (Main entry point)
├── controller/          (REST API endpoints)
├── service/             (Business logic layer)
├── repository/          (Data access layer)
├── model/               (JPA entities)
│   ├── Student.java
│   ├── SchoolClass.java
│   ├── FeeMaster.java
│   ├── FeeJournal.java
│   ├── FeeReceipt.java
│   └── SchoolConfig.java
├── dto/
│   ├── request/         (Request DTOs with validation)
│   └── response/        (Response DTOs)
├── mapper/              (MapStruct entity-DTO mappers)
├── config/              (Configuration beans)
├── exception/           (Custom exceptions)
└── util/                (Utility classes)
```

## Database Schema

### Tables Created
- `students` - Student records with personal and family details
- `school_classes` - Class structure (1-10) with capacity tracking
- `fee_master` - Fee configuration by type
- `fee_journal` - Monthly fee tracking per student
- `fee_receipts` - Payment receipts with breakdown
- `school_config` - System configuration key-value pairs

### Key Relationships
- Student → SchoolClass (Many-to-One)
- Student → FeeJournal (One-to-Many)
- Student → FeeReceipt (One-to-Many)
- FeeReceipt → FeeJournal (One-to-Many)

## API Endpoints (Planned)

### Student Management
- `POST /api/students` - Create student
- `GET /api/students` - Get all students (with optional classId filter)
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students/search?q={query}` - Search students (autocomplete)
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Class Management
- `POST /api/classes` - Create class
- `GET /api/classes` - Get all classes
- `GET /api/classes/{id}` - Get class by ID
- `GET /api/classes/{id}/availability` - Check class availability
- `PUT /api/classes/{id}` - Update class

### Fee Master
- `POST /api/fee-master` - Create fee configuration
- `GET /api/fee-master` - Get all fee masters
- `GET /api/fee-master/{id}` - Get fee master by ID
- `PUT /api/fee-master/{id}` - Update fee master

### Fee Receipt
- `POST /api/fee-receipts/calculate` - Calculate fee (preview)
- `POST /api/fee-receipts` - Generate receipt
- `GET /api/fee-receipts/{id}` - Get receipt by ID
- `GET /api/fee-receipts/{id}/pdf` - Download receipt PDF
- `GET /api/fee-receipts/student/{studentId}` - Get all receipts for student

### Fee Journal
- `GET /api/fee-journal/student/{studentId}` - Get journal entries for student
- `GET /api/fee-journal/pending` - Get all pending entries

## Configuration

### Application Profiles

**Default Profile** (application.properties):
- PostgreSQL connection
- DDL auto: update
- Logging: DEBUG level

**Dev Profile** (application-dev.properties):
- DDL auto: create-drop (recreates schema on restart)
- Enhanced logging (TRACE level)
- Show SQL queries

**Prod Profile** (application-prod.properties):
- Environment variable based DB config
- DDL auto: validate (no schema changes)
- Minimal logging (WARN level)

### Drools Rules Engine

Fee calculation rules are defined in `src/main/resources/rules/`:
- `fee-calculation.drl` - Base fee calculation by class
- `fee-validation.drl` - Payment validation rules

## Next Implementation Steps

### 1. Complete DTOs (High Priority)

Create remaining request/response DTOs for:
- ClassRequestDTO, ClassResponseDTO
- FeeMasterRequestDTO, FeeMasterResponseDTO
- FeeReceiptRequestDTO, FeeReceiptResponseDTO
- FeeJournalResponseDTO
- FeeCalculationDTO (for Drools)

### 2. Create MapStruct Mappers

```java
@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentResponseDTO toResponseDTO(Student entity);
    Student toEntity(StudentRequestDTO dto);
    void updateEntityFromDTO(StudentRequestDTO dto, @MappingTarget Student entity);
}
```

### 3. Implement Service Layer

Priority order:
1. StudentService - Core functionality
2. ClassService - Required by StudentService
3. ReceiptGenerationService - Complex with Drools
4. FeeJournalService - Payment tracking
5. FeeMasterService - Configuration

### 4. Implement Controller Layer

Standard pattern for all controllers:
- @RestController
- @RequestMapping("/api/{resource}")
- @CrossOrigin(origins = "http://localhost:3000")
- @RequiredArgsConstructor (constructor injection)
- @Valid for request validation
- ResponseEntity<ApiResponse<T>> return type

### 5. Configuration Classes

**DroolsConfig.java**:
```java
@Configuration
public class DroolsConfig {
    @Bean
    public KieContainer kieContainer() {
        // Load DRL files and build KieContainer
    }
}
```

**GlobalExceptionHandler.java**:
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(...)

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(...)

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(...)
}
```

**DataInitializer.java**:
```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // Seed Classes 1-10
        // Seed default school config
    }
}
```

### 6. Write Tests

**Unit Tests** (Mockito):
- StudentServiceTest
- ClassServiceTest
- ReceiptGenerationServiceTest
- FeeJournalServiceTest
- FeeMasterServiceTest

**Integration Tests** (@DataJpaTest):
- StudentRepositoryTest
- ClassRepositoryTest
- FeeReceiptRepositoryTest
- FeeJournalRepositoryTest
- FeeMasterRepositoryTest

**Controller Tests** (@WebMvcTest + MockMvc):
- StudentControllerTest
- ClassControllerTest
- FeeReceiptControllerTest
- FeeJournalControllerTest
- FeeMasterControllerTest

## Dependencies

### Core
- Spring Boot 3.5.0
- Java 21
- PostgreSQL 42.7.1

### Rules Engine
- Drools 9.44.0.Final

### Utilities
- Lombok 1.18.30
- MapStruct 1.5.5.Final

### Testing
- JUnit 5
- Mockito
- H2 Database (test scope)
- Spring Boot Test
- JaCoCo (code coverage)

## Known Issues / TODO

- [ ] PDF generation for receipts (add iText or JasperReports dependency)
- [ ] Spring Security integration (authentication/authorization)
- [ ] Pagination support for large result sets
- [ ] API rate limiting
- [ ] Caching with Redis
- [ ] Async processing for bulk operations
- [ ] Swagger/OpenAPI documentation
- [ ] Docker configuration
- [ ] CI/CD pipeline setup

## Contact

For questions or issues, refer to documentation in `docs/` directory:
- `docs/CLAUDE-BACKEND.md` - Complete backend patterns
- `docs/features/CLAUDE-FEATURE-STUDENT.md` - Student feature specification
- `docs/features/CLAUDE-FEATURE-CLASS.md` - Class feature specification
- `docs/features/CLAUDE-FEATURE-FEE-RECEIPT.md` - Fee receipt specification

## License

Proprietary - School Management System
