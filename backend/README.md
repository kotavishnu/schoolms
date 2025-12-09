# School Management System - Backend

## Overview
This is a production-ready Spring Boot 3.4.1 backend system using Java 21, following Domain-Driven Design (DDD) and Test-Driven Development (TDD) principles.

## Architecture
- **Layered Architecture**: Presentation → Application → Domain → Infrastructure
- **Microservices**: Student Service, Configuration Service
- **Database**: PostgreSQL 18 with Flyway migrations
- **Cache**: Redis for query result caching
- **Rules Engine**: Drools 9.44 for business rules
- **Observability**: Micrometer, Prometheus, Zipkin

## Prerequisites
- Java 21 (LTS)
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 18 (or use Docker)
- Redis 7.2 (or use Docker)

## Quick Start

### 1. Start Infrastructure (Docker Compose)
```bash
cd backend
docker-compose up -d postgres redis
```

### 2. Build Project
```bash
mvn clean install
```

### 3. Run Student Service
```bash
cd student-service
mvn spring-boot:run
```

The service will start on `http://localhost:8081`

## Project Structure
```
backend/
├── shared-lib/               # Common utilities, exceptions, DTOs
├── student-service/          # Student management microservice
│   ├── domain/               # Business logic and entities
│   ├── application/          # Use cases and orchestration
│   ├── infrastructure/       # Database, cache, external services
│   └── presentation/         # REST controllers
└── configuration-service/    # Configuration management microservice
```

## API Documentation
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI Spec: http://localhost:8081/api-docs

## Database
- **Student DB**: `jdbc:postgresql://localhost:5432/student_db`
- **Config DB**: `jdbc:postgresql://localhost:5432/config_db`
- **User**: sms_user / sms_password

Flyway migrations run automatically on startup.

## Testing
```bash
# Run unit tests
mvn test

# Run integration tests (with TestContainers)
mvn verify

# Generate coverage report
mvn jacoco:report
```

Coverage target: 80%+

## Key Implementation Tasks Remaining

### Student Service (Priority Order)

#### 1. Domain Layer
- [ ] Create `Student` entity (domain/model/Student.java)
- [ ] Create `StudentStatus` enum
- [ ] Create `StudentRepository` interface (domain/repository)
- [ ] Implement `StudentKeyGenerator` (domain/service)
- [ ] Create Drools validation rules (resources/rules/student-validation.drl)

#### 2. Infrastructure Layer
- [ ] Create `StudentEntity` JPA entity (infrastructure/persistence/entity)
- [ ] Implement `JpaStudentRepository` extending JpaRepository
- [ ] Create `StudentRepositoryImpl` (maps between domain and JPA)
- [ ] Configure Drools (infrastructure/config/DroolsConfig.java)
- [ ] Configure Redis (infrastructure/config/RedisConfig.java)

#### 3. Application Layer
- [ ] Create DTOs (presentation/dto):
  - `CreateStudentRequest`
  - `UpdateStudentRequest`
  - `StudentDTO`
- [ ] Create `StudentMapper` with MapStruct
- [ ] Implement `StudentService` and `StudentServiceImpl`:
  - createStudent()
  - getStudentByKey()
  - searchStudents()
  - updateStudent()
  - deleteStudent()

#### 4. Presentation Layer
- [ ] Create `StudentController` with REST endpoints:
  - POST /api/v1/students
  - GET /api/v1/students/{studentKey}
  - GET /api/v1/students (search with pagination)
  - PUT /api/v1/students/{studentKey}
  - DELETE /api/v1/students/{studentKey}
- [ ] Create `GlobalExceptionHandler` for RFC 7807 error responses
- [ ] Add OpenAPI annotations

### Configuration Service
- [ ] Replicate similar structure for Configuration and School Profile management
- [ ] Separate database (config_db)
- [ ] REST endpoints for settings CRUD

## Environment Variables
```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=student_db
export DB_USERNAME=sms_user
export DB_PASSWORD=sms_password

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6379

# Spring Profile
export SPRING_PROFILE=dev
```

## Docker Build
```bash
# Build JAR
mvn clean package

# Build Docker image
docker build -t sms/student-service:1.0.0 .

# Run container
docker run -p 8081:8081 \
  -e DB_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  sms/student-service:1.0.0
```

## Monitoring
- Health: http://localhost:8081/actuator/health
- Metrics: http://localhost:8081/actuator/prometheus
- Info: http://localhost:8081/actuator/info

## Key Design Decisions
1. **SpringDoc 2.7.0**: Compatible with Spring Boot 3.4.1
2. **Separate Redis databases**: DB 0 for student-service, DB 1 for config-service
3. **Cache TTL**: 5 minutes for students, 30 minutes for configurations
4. **Optimistic Locking**: Version field in all entities
5. **RFC 7807**: Standard error response format

## Reference Documentation
- [System Architecture](../specs/architecture/SYSTEM_ARCHITECTURE.md)
- [Database Schema](../specs/architecture/DATABASE_SCHEMA.md)
- [API Design](../specs/architecture/API_DESIGN.md)
- [Backend Tasks](../specs/tasks/BACKEND_TASKS.md)

## Troubleshooting

### Issue: Port 8081 already in use
```bash
# Kill process on port 8081
lsof -ti:8081 | xargs kill -9
```

### Issue: Database connection failure
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Test connection
psql -h localhost -U sms_user -d student_db
```

### Issue: Flyway migration failure
```bash
# Clean Flyway metadata (caution: use only in dev)
mvn flyway:clean
mvn flyway:migrate
```

## Contributing
1. Follow TDD: Write failing test → Implement → Refactor
2. Maintain 80%+ code coverage
3. Follow layered architecture strictly
4. Use conventional commits
5. Run tests before committing

## License
Proprietary - School Management System
