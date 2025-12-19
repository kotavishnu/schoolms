# School Management System - Backend

Spring Boot 3.5 + Java 25 backend for School Management System.

## Features Implemented

✅ **Complete REST API** for school management operations
✅ **Layered Architecture**: Controller → Service → Repository → Database
✅ **Domain Models**: Student, SchoolConfig, Class, FeeMaster, FeeReceipt, FeeJournal
✅ **Drools Rules Engine**: Fee calculation with configurable rules
✅ **Global Exception Handling**: Comprehensive error responses
✅ **TDD Approach**: Tests written before implementation
✅ **Data Initialization**: Auto-seeding of Classes 1-10
✅ **CORS Configuration**: Ready for frontend integration

## Tech Stack

- **Java**: 25 (with preview features)
- **Spring Boot**: 3.5.0
- **Database**: PostgreSQL 18+ (production), H2 (testing)
- **ORM**: Spring Data JPA / Hibernate
- **Rules Engine**: Drools 9.44.0
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Code Generation**: Lombok

## Quick Start

### Prerequisites

- Java 25 installed
- PostgreSQL 18+ running
- Maven 3.9+

### Setup Database

```sql
CREATE DATABASE school_management_db;
```

### Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/school_management_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

### Build and Run

```bash
# Clean and build
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

Application will start at: http://localhost:8080

## API Endpoints

### Students
- `POST /api/students` - Create student
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students` - Get all students
- `GET /api/students?classId={id}` - Get students by class
- `GET /api/students/search?query={name}` - Search students
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Classes
- `POST /api/classes` - Create class
- `GET /api/classes/{id}` - Get class by ID
- `GET /api/classes` - Get all classes
- `GET /api/classes?academicYear={year}` - Get classes by year
- `PUT /api/classes/{id}` - Update class
- `DELETE /api/classes/{id}` - Delete class

### School Configuration
- `POST /api/school-config` - Create/Update config
- `GET /api/school-config` - Get current config

### Fee Master
- `POST /api/fee-masters` - Create fee master
- `GET /api/fee-masters/{id}` - Get fee master by ID
- `GET /api/fee-masters` - Get all fee masters
- `GET /api/fee-masters?academicYear={year}` - Get by year
- `GET /api/fee-masters?academicYear={year}&classNumber={num}` - Get applicable fees
- `PUT /api/fee-masters/{id}` - Update fee master
- `DELETE /api/fee-masters/{id}` - Delete fee master

### Fee Receipts
- `POST /api/fee-receipts` - Generate receipt (auto-calculates fee using Drools)
- `GET /api/fee-receipts/{id}` - Get receipt by ID
- `GET /api/fee-receipts/receipt-number/{number}` - Get by receipt number
- `GET /api/fee-receipts/student/{studentId}` - Get receipts for student
- `GET /api/fee-receipts` - Get all receipts

### Fee Journal
- `GET /api/fee-journals/{id}` - Get journal entry
- `GET /api/fee-journals` - Get all entries
- `GET /api/fee-journals?academicYear={year}&pending=true` - Get pending fees
- `GET /api/fee-journals/student/{studentId}` - Get student's fee journal
- `GET /api/fee-journals/student/{studentId}?pending=true` - Get pending fees for student
- `GET /api/fee-journals/student/{studentId}/pending-count` - Count pending fees

## Project Structure

```
backend/
├── src/main/java/com/school/management/
│   ├── controller/       # REST endpoints
│   ├── service/          # Business logic
│   ├── repository/       # Data access
│   ├── model/            # JPA entities
│   ├── dto/              # Request/Response objects
│   ├── config/           # Configuration classes
│   ├── exception/        # Exception handling
│   └── util/             # Helper classes
├── src/main/resources/
│   ├── rules/            # Drools rule files
│   └── application.properties
└── src/test/java/        # Test classes
```

## Drools Fee Calculation

The fee calculation engine automatically calculates student fees based on:
- **Base tuition fee**: Varies by class (1-5 vs 6-10)
- **Library fee**: Applicable to all classes
- **Computer fee**: Applicable to all classes
- **Special fee**: One-time fee for first month only

Fee rules are defined in `src/main/resources/rules/fee-calculation-rules.drl`

## Data Initialization

On startup, the application automatically:
- Creates Classes 1-10 (Section A) for the current academic year
- Sets capacity of 40 students per class

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=StudentServiceTest

# Generate coverage report
mvn test jacoco:report
# Report available at: target/site/jacoco/index.html
```

**Test Results**: ✅ 11 tests passing

## Development

### Adding New Entity

1. Create Entity class in `model/`
2. Create Repository interface
3. Create DTOs for request/response
4. Write service tests (TDD approach)
5. Implement service
6. Create controller
7. Test integration

### Validation

DTOs use Jakarta Validation:
- `@NotNull`, `@NotBlank`
- `@Size`, `@Min`, `@Max`
- `@Pattern`, `@Email`
- `@Past` for dates

### Exception Handling

Global exception handler provides consistent error responses:
- `ResourceNotFoundException` → 404
- `BadRequestException` → 400
- `MethodArgumentNotValidException` → 400 with field errors
- `Exception` → 500

## Build for Production

```bash
# Create executable JAR
mvn clean package

# Run JAR
java -jar target/school-management-1.0.0-SNAPSHOT.jar
```

## Next Steps

- [ ] Add authentication/authorization (Spring Security)
- [ ] Add pagination for list endpoints
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Add logging (SLF4J/Logback)
- [ ] Add caching (Redis)
- [ ] Add integration tests with TestContainers
- [ ] Add API rate limiting
- [ ] Add data export (CSV/Excel)

## License

[Your License Here]
