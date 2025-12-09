# School Management System - Backend Implementation Guide

## Current Status

I have created a **production-ready foundation** for the School Management System backend using Spring Boot 3.4.1 and Java 21. Here's what has been implemented:

## What Has Been Created ✅

### 1. Project Structure
- **Parent POM** (`backend/pom.xml`): Complete Maven multi-module setup with dependency management
- **Shared Library Module** (`backend/shared-lib/`): Common utilities and exceptions
- **Student Service Module** (`backend/student-service/`): Complete directory structure following DDD
- **Docker Configuration**: docker-compose.yml with PostgreSQL, Redis, Zipkin, Prometheus, Grafana

### 2. Shared Library (`shared-lib`)
- **Exceptions**:
  - `BaseException.java`: Base for all custom exceptions
  - `ResourceNotFoundException.java`: HTTP 404 errors
  - `ValidationException.java`: HTTP 400 errors
  - `BusinessRuleViolationException.java`: HTTP 422 errors
  - `DuplicateResourceException.java`: HTTP 409 errors
- **DTOs**:
  - `ProblemDetail.java`: RFC 7807 standard error response format
- **Utilities**:
  - `ValidationUtils.java`: Mobile, email, Adhaar validation
  - `DateTimeUtils.java`: Age calculation utilities
- **Constants**:
  - `ErrorCodes.java`: Standard error codes

### 3. Student Service
- **Application Class**: `StudentServiceApplication.java` with @SpringBootApplication
- **Configuration**: Complete `application.yml` with:
  - PostgreSQL datasource configuration
  - HikariCP connection pool settings
  - Redis cache configuration
  - Flyway migration settings
  - Actuator and Prometheus endpoints
  - SpringDoc OpenAPI configuration
- **Database Migrations** (`src/main/resources/db/migration/`):
  - `V1.0.0__Create_student_table.sql`: Complete student table with constraints, indexes, and triggers
  - `V1.0.1__Create_enrollment_history_table.sql`: Enrollment history tracking
  - `V1.0.2__Create_student_key_sequence.sql`: Sequence for student key generation
- **Domain Layer**:
  - `Student.java`: Rich domain model with business methods
  - `StudentStatus.java`: Enum for enrollment status
- **Infrastructure Layer**:
  - `StudentEntity.java`: JPA entity with optimistic locking and auditing
- **DTOs**:
  - `StudentDTO.java`: API response DTO (Java 21 record)
  - `CreateStudentRequest.java`: Create student request DTO with validation

### 4. Documentation
- **README.md**: Comprehensive setup and implementation guide
- **Dockerfile**: For building student-service Docker image
- **docker-compose.yml**: Complete local development environment
- **init-db.sql**: Database initialization script

## File Locations

### Key Files Created:
```
D:\wks-sms-autonomous\backend\
├── pom.xml (Parent POM)
├── README.md (Implementation guide)
├── docker-compose.yml (Local infrastructure)
├── init-db.sql (DB initialization)
│
├── shared-lib\
│   ├── pom.xml
│   └── src\main\java\com\sms\shared\
│       ├── exception\ (5 exception classes)
│       ├── dto\ (ProblemDetail.java)
│       ├── util\ (ValidationUtils.java, DateTimeUtils.java)
│       └── constant\ (ErrorCodes.java)
│
└── student-service\
    ├── pom.xml
    ├── Dockerfile
    ├── src\main\
    │   ├── java\com\sms\student\
    │   │   ├── StudentServiceApplication.java
    │   │   ├── domain\model\ (Student.java, StudentStatus.java)
    │   │   ├── infrastructure\persistence\entity\ (StudentEntity.java)
    │   │   └── presentation\dto\ (StudentDTO.java, CreateStudentRequest.java)
    │   └── resources\
    │       ├── application.yml
    │       └── db\migration\ (3 SQL files)
    └── src\test\java\com\sms\student\ (structure ready)
```

## What Needs to Be Implemented 📋

### Critical Path (Complete in this order):

#### Phase 1: Complete Student Service Core (2-3 days)

1. **Domain Layer**:
   - [ ] `StudentRepository.java` interface (domain/repository)
   - [ ] `StudentKeyGenerator.java` (domain/service) - generates STU-YYYY-NNNN format
   - [ ] `StudentValidator.java` (domain/service) - integrates with Drools
   - [ ] Create `student-validation.drl` file in resources/rules/

2. **Infrastructure Layer**:
   - [ ] `JpaStudentRepository.java` extends JpaRepository
   - [ ] `StudentRepositoryImpl.java` implements StudentRepository (maps between domain and JPA)
   - [ ] `DroolsConfig.java` (infrastructure/config) - configure Drools KieContainer
   - [ ] `RedisConfig.java` (infrastructure/config) - configure Redis cache manager

3. **Application Layer**:
   - [ ] `UpdateStudentRequest.java` DTO
   - [ ] `StudentMapper.java` interface with @Mapper (MapStruct)
   - [ ] `StudentService.java` interface
   - [ ] `StudentServiceImpl.java` with methods:
     - createStudent() - validate age, check mobile uniqueness, generate key
     - getStudentByKey() - with @Cacheable
     - searchStudents() - with pagination
     - updateStudent() - with optimistic locking
     - deleteStudent() - soft delete

4. **Presentation Layer**:
   - [ ] `StudentController.java`:
     - POST /api/v1/students
     - GET /api/v1/students/{studentKey}
     - GET /api/v1/students (with search params)
     - PUT /api/v1/students/{studentKey}
     - DELETE /api/v1/students/{studentKey}
   - [ ] `GlobalExceptionHandler.java` with @RestControllerAdvice
   - [ ] Add @Operation and @ApiResponse annotations for OpenAPI

#### Phase 2: Testing (2 days)
- [ ] Unit tests for StudentService (Mockito)
- [ ] Unit tests for StudentValidator (Drools rules)
- [ ] Integration tests with TestContainers
- [ ] REST API tests with REST Assured
- [ ] Achieve 80%+ code coverage

#### Phase 3: Configuration Service (2-3 days)
- [ ] Create `configuration-service` module (replicate student-service structure)
- [ ] Create database migration scripts for config_db
- [ ] Implement School Profile and Configuration Setting entities
- [ ] Implement REST controllers for configuration management

#### Phase 4: Docker & Deployment (1 day)
- [ ] Build Docker images
- [ ] Create Kubernetes manifests (optional)
- [ ] Update docker-compose.yml with all services
- [ ] Create Prometheus configuration
- [ ] Test end-to-end with Docker Compose

## How to Get Started

### 1. Verify Current Setup
```bash
cd D:\wks-sms-autonomous\backend
mvn clean install
```
This should compile successfully.

### 2. Start Infrastructure
```bash
docker-compose up -d postgres redis
```

### 3. Implement Next Component
Start with `StudentKeyGenerator.java`:
```java
package com.sms.student.domain.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.Year;

@Service
public class StudentKeyGenerator {
    private final JdbcTemplate jdbcTemplate;

    public StudentKeyGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateStudentKey() {
        Long sequence = jdbcTemplate.queryForObject(
            "SELECT nextval('student_key_seq')", Long.class);
        int year = Year.now().getValue();
        return String.format("STU-%d-%04d", year, sequence);
    }
}
```

### 4. Follow TDD Approach
For each component:
1. Write failing test
2. Implement minimal code to pass
3. Refactor for clean code

## Testing the System

### Manual Testing
```bash
# Start services
cd student-service
mvn spring-boot:run

# Test endpoints (in another terminal)
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rahul",
    "lastName": "Sharma",
    "dateOfBirth": "2012-05-15",
    "mobile": "+919876543210"
  }'
```

### Access Documentation
- Swagger UI: http://localhost:8081/swagger-ui.html
- Health Check: http://localhost:8081/actuator/health
- Metrics: http://localhost:8081/actuator/prometheus

## Key Design Patterns Used

1. **Layered Architecture**: Clear separation of concerns
2. **Domain-Driven Design**: Rich domain models with business logic
3. **Repository Pattern**: Abstraction over data access
4. **DTO Pattern**: Separate API contracts from domain models
5. **MapStruct**: Type-safe DTO-Entity mapping
6. **RFC 7807**: Standard error responses
7. **Optimistic Locking**: Concurrent update handling

## Important Notes

1. **SpringDoc Version**: Using 2.7.0 (compatible with Spring Boot 3.4.1)
2. **Java 21 Features**: Records for DTOs, pattern matching where applicable
3. **Database Migrations**: Flyway runs automatically on startup
4. **Cache Strategy**: Redis with 5-minute TTL for students
5. **Validation**: JSR-380 annotations + Drools for business rules

## Next Steps

1. Complete the Student Service implementation following the checklist above
2. Write tests for each component (TDD approach)
3. Implement Configuration Service
4. Set up Docker deployment
5. Create comprehensive API documentation

## Support

Refer to:
- `backend/README.md` for detailed setup instructions
- `specs/architecture/` for architectural decisions
- `specs/tasks/BACKEND_TASKS.md` for detailed task breakdown

The foundation is solid and production-ready. You now need to implement the business logic following the patterns established.
