# School Management System - Backend Services

## Overview

This directory contains two microservices for the School Management System:

1. **Student Service** (Port 8081) - Student registration, search, update, enrollment history
2. **Configuration Service** (Port 8082) - System configuration management

## Technology Stack

- **Java**: 21 LTS
- **Spring Boot**: 3.3.5
- **SpringDoc OpenAPI**: 2.6.0 (CRITICAL: DO NOT use 2.7.0 - see D-001)
- **Database**: PostgreSQL 18
- **Cache**: Redis 7.2
- **Rules Engine**: Drools 9.44.0.Final
- **Mapping**: MapStruct 1.5.5.Final

## Quick Start

### Prerequisites

- Java 21 JDK
- Docker Desktop (for PostgreSQL and Redis)
- Maven 3.9+

### 1. Start Infrastructure

```bash
cd backend
docker-compose up -d
```

This starts:
- PostgreSQL 18 (student_db) on port 5433
- PostgreSQL 18 (config_db) on port 5434
- Redis 7 on port 6379

### 2. Initialize Databases

```bash
# Connect to student database
psql -h localhost -p 5433 -U postgres -d student_db -f ../docs/tasks/school_management.sql

# Connect to config database
psql -h localhost -p 5434 -U postgres -d config_db -f ../docs/tasks/school_management.sql
```

### 3. Start Student Service

```bash
cd student-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

Service will start on http://localhost:8081

### 4. Start Configuration Service

```bash
cd configuration-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

Service will start on http://localhost:8082

## API Documentation

### Student Service
- Swagger UI: http://localhost:8081/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api/v1/api-docs

### Configuration Service
- Swagger UI: http://localhost:8082/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/api/v1/api-docs

## Architecture

### Student Service Structure

```
student-service/
├── domain/entity/          # Student, Enrollment entities
├── domain/validation/      # Drools validation request/result
├── repository/             # Spring Data JPA repositories
├── service/                # Business logic layer
├── controller/             # REST API endpoints
├── dto/                    # Request/Response DTOs
├── mapper/                 # MapStruct mappers
├── exception/              # Custom exceptions + global handler
├── config/                 # Spring configurations
└── resources/
    ├── rules/              # Drools DRL files
    └── application.yml
```

### Key Endpoints (Student Service)

- `POST /api/v1/students` - Register student
- `GET /api/v1/students/{id}` - Get student
- `PUT /api/v1/students/{id}` - Update student
- `DELETE /api/v1/students/{id}` - Delete student
- `GET /api/v1/students?lastName=&status=&page=0&size=20` - Search
- `GET /api/v1/students/statistics` - Dashboard stats
- `GET /api/v1/students/{id}/enrollments` - Enrollment history
- `POST /api/v1/students/{id}/enrollments` - Create enrollment

### Key Endpoints (Configuration Service)

- `GET /api/v1/configurations` - Get all configurations
- `GET /api/v1/configurations?category=GENERAL` - Filter by category
- `GET /api/v1/configurations/{category}/{key}` - Get specific config
- `PUT /api/v1/configurations/{category}/{key}` - Upsert configuration
- `DELETE /api/v1/configurations/{category}/{key}` - Delete configuration
- `GET /api/v1/configurations/grouped/{category}` - Get as key-value map

## Business Rules (Drools)

The Student Service implements 7 business rules using Drools:

- **BR-STU-001**: Age must be between 3 and 18 years
- **BR-STU-002**: Mobile number must be unique
- **BR-STU-003**: Email must be valid format
- **BR-STU-004**: Aadhaar must be 12 digits (optional)
- **BR-STU-005**: Names must contain only letters and spaces
- **BR-STU-006**: Mobile must be exactly 10 digits
- **BR-STU-007**: Only firstName, lastName, mobile, status are editable

## Testing

### Run All Tests

```bash
cd student-service
mvn test

cd ../configuration-service
mvn test
```

### Run with Coverage

```bash
mvn verify
# Coverage report: target/site/jacoco/index.html
```

### Target Coverage
- Overall: 70% minimum
- Service Layer: 85%
- Domain Layer: 95%

## Global Directives Compliance

- **D-001**: Spring Boot 3.3.5 + SpringDoc 2.6.0 ✅
- **D-002**: MapStruct for all DTO mapping ✅
- **D-003**: @Version on all entities ✅
- **D-009**: PostgreSQL ports 5433, 5434 ✅
- **D-010**: UTC timezone configuration ✅

## Database Schema

See `docs/tasks/school_management.sql` for complete DDL.

### Students Table
- student_id (business key, STD-YYYYMMDD-NNNN)
- Personal info (firstName, lastName, dateOfBirth, etc.)
- Contact info (mobile, email, address)
- Guardian info (guardianName, motherName)
- Status (ACTIVE/INACTIVE)
- Optimistic locking (version)

### Enrollments Table
- References student
- Academic year, grade/class, section
- Enrollment/withdrawal dates
- Status (ACTIVE/WITHDRAWN/COMPLETED)

### Configurations Table
- Composite unique key (category, key)
- Categories: GENERAL, ACADEMIC, FINANCIAL, SYSTEM
- Data types: STRING, NUMBER, BOOLEAN, JSON

## Monitoring

### Health Checks
- Student Service: http://localhost:8081/actuator/health
- Configuration Service: http://localhost:8082/actuator/health

### Metrics (Prometheus)
- Student Service: http://localhost:8081/actuator/prometheus
- Configuration Service: http://localhost:8082/actuator/prometheus

## Troubleshooting

### PostgreSQL Timezone Error

If you see "invalid value for parameter TimeZone: Asia/Calcutta":

```bash
# Always use UTC timezone
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

### Port Already in Use

```bash
# Check what's using the port
netstat -ano | findstr :8081
# Kill the process or change port in application.yml
```

### Docker Containers Not Starting

```bash
docker-compose down -v
docker-compose up -d
docker ps  # Verify all containers are healthy
```

## Implementation Status

### Student Service ✅ COMPLETE (BE-001 to BE-014)
- [x] Project setup
- [x] Domain entities
- [x] Repositories
- [x] Drools configuration + 7 rules
- [x] Validation service
- [x] DTOs + MapStruct mappers
- [x] Business logic service
- [x] REST controllers
- [x] Exception handling (RFC 7807)
- [x] Redis cache
- [x] CORS configuration

### Configuration Service ⏳ IN PROGRESS (BE-015 to BE-021)
- [x] Project setup
- [x] Application configuration
- [ ] Domain entities (see CREATE_REMAINING_FILES.md)
- [ ] Repositories
- [ ] DTOs + mappers
- [ ] Service layer
- [ ] REST controller
- [ ] Exception handling
- [ ] Cache configuration

### Infrastructure ✅ COMPLETE (BE-026)
- [x] Docker Compose (PostgreSQL 18 x2, Redis 7)
- [x] Database schema SQL script

### Testing ⏳ PENDING (BE-022 to BE-025)
- [ ] Student Service unit tests
- [ ] Drools rules tests
- [ ] Integration tests
- [ ] Configuration Service tests

## Next Steps

1. Complete Configuration Service implementation (13 files remaining)
2. Write comprehensive test suite (70%+ coverage)
3. Run end-to-end manual testing
4. Verify all 29 tasks completed

## Contact

For issues or questions, refer to:
- `specs/LESSONS_LEARNED.md` - Past errors and solutions
- `docs/tasks/BACKEND_TASKS.md` - Complete task list
- `specs/architecture/05-backend-implementation-guide.md` - Implementation patterns
