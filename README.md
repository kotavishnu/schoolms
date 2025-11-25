# School Management System - Backend

## Overview

The School Management System (SMS) is a web-based platform designed to automate and optimize administrative workflows within schools. This repository contains the backend microservices implementation for Phase 1.

### Phase 1 Features
- Student registration and profile management
- Student status updates (Active, Inactive, Graduated, Transferred)
- School configuration management (key-value settings)
- Auto-generated Student IDs (STU-YYYY-XXXXX format)
- Business rules validation using Drools
- Redis caching for performance
- Distributed tracing with Zipkin
- OpenAPI/Swagger documentation

## Technology Stack

- **Framework:** Spring Boot 3.5.0
- **Java Version:** Java 21
- **Architecture:** Domain-Driven Design (DDD) + Hexagonal Architecture
- **Database:** PostgreSQL 18+
- **Cache:** Redis 7.2+
- **Build Tool:** Maven (multi-module)
- **Business Rules:** Drools 9.44.0
- **Mapping:** MapStruct 1.5.5
- **Testing:** JUnit 5, Mockito, TestContainers
- **Documentation:** SpringDoc OpenAPI 3 (Swagger UI)
- **Observability:** Micrometer + Zipkin

## Project Structure

```
sms-backend/
├── pom.xml (parent)
├── sms-common/                    # Shared utilities and exceptions
├── student-service/               # Student management microservice (port 8081)
│   ├── domain/                    # Domain models, value objects, aggregates
│   ├── application/               # DTOs, services, mappers
│   ├── infrastructure/            # JPA, Drools, Redis, configs
│   └── presentation/              # REST controllers, exception handlers
├── configuration-service/         # Configuration management (port 8082)
└── docker-compose.yml             # Local development environment
```

## Prerequisites

- **Java 21** (OpenJDK or Oracle JDK)
- **Maven 3.9+**
- **Docker & Docker Compose** (for local development)
- **Git**

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd wks-sms-specs-itr2
```

### 2. Start Infrastructure Services

```bash
# Start PostgreSQL, Redis, and Zipkin
docker-compose up -d

# Verify services are running
docker-compose ps
```

**Services:**
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Zipkin UI: `http://localhost:9411`

### 3. Build the Project

```bash
# Build all modules
mvn clean install

# Skip tests if needed
mvn clean install -DskipTests
```

### 4. Run Student Service

```bash
# From root directory
cd student-service
mvn spring-boot:run

# Or run the JAR
java -jar target/student-service-1.0.0-SNAPSHOT.jar
```

**Student Service will start on:** `http://localhost:8081`

### 5. Access API Documentation

- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/api/v1/api-docs
- **Actuator Health:** http://localhost:8081/actuator/health
- **Metrics:** http://localhost:8081/actuator/metrics
- **Prometheus:** http://localhost:8081/actuator/prometheus

## API Endpoints

### Student Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/students` | Create a new student |
| GET | `/api/v1/students` | List students (paginated, searchable) |
| GET | `/api/v1/students/{id}` | Get student by database ID |
| GET | `/api/v1/students/student-id/{studentId}` | Get student by Student ID (STU-YYYY-XXXXX) |
| PUT | `/api/v1/students/{id}` | Update student profile (firstName, lastName, mobile) |
| PATCH | `/api/v1/students/{id}/status` | Update student status |
| DELETE | `/api/v1/students/{id}` | Soft delete student (sets status to INACTIVE) |

### Example: Create Student

```bash
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2015-05-15",
    "mobile": "9876543210",
    "address": "123 Main Street",
    "fatherName": "James Doe",
    "motherName": "Jane Doe",
    "email": "john.doe@example.com"
  }'
```

### Example: Search Students

```bash
# Search by last name
GET http://localhost:8081/api/v1/students?lastName=Doe&page=0&size=10

# Search by status
GET http://localhost:8081/api/v1/students?status=ACTIVE&page=0&size=10
```

## Business Rules

The system enforces the following business rules via Drools:

1. **BR-1:** Student age must be between 3 and 18 years at registration
2. **BR-2:** Mobile number must be unique per student
3. **BR-3:** Only firstName, lastName, mobile, and status are editable after creation
4. **BR-4:** StudentID is auto-generated in format STU-YYYY-XXXXX (e.g., STU-2024-00001)
5. **BR-5:** Soft delete via status change to INACTIVE

## Testing

### Run All Tests

```bash
mvn test
```

### Run with Coverage

```bash
mvn clean verify
```

**Coverage Reports:** `target/site/jacoco/index.html`

**Minimum Coverage:** 80% (enforced by JaCoCo)

### Test Categories

- **Unit Tests:** Domain models, value objects (TDD approach)
- **Integration Tests:** Repository layer with TestContainers
- **Service Tests:** Business logic with Mockito
- **Controller Tests:** REST endpoints with MockMvc

## Development Profiles

### Dev Profile (default)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

- H2 console enabled (if using H2)
- Debug logging
- Local PostgreSQL connection

### Production Profile

```bash
java -jar target/student-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

- Environment variable-based configuration
- Warn/Error logging only
- Production database URLs

## Configuration

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/sms_student_db
DATABASE_USERNAME=sms_user
DATABASE_PASSWORD=sms_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Zipkin
ZIPKIN_URL=http://localhost:9411/api/v2/spans
```

## Architecture Highlights

### Domain-Driven Design (DDD)

- **Aggregates:** Student (root), Configuration
- **Value Objects:** StudentId, Mobile (immutable, validated)
- **Domain Events:** StudentRegisteredEvent, StudentStatusChangedEvent
- **Domain Services:** StudentIdGenerator

### Hexagonal Architecture

- **Ports:** Repository interfaces in domain layer
- **Adapters:** JPA implementations in infrastructure layer
- **Clear separation:** Domain logic independent of frameworks

### Caching Strategy

- **Cache-Aside Pattern**
- **TTL:** 5 minutes
- **Eviction:** On updates and deletes
- **Storage:** Redis

### Validation

- **Input Validation:** Jakarta Bean Validation annotations
- **Business Rules:** Drools DRL files
- **Custom Validators:** @AgeRange annotation

## Troubleshooting

### Port Already in Use

```bash
# Kill process on port 8081
lsof -ti:8081 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8081   # Windows
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker ps | grep postgres

# View logs
docker logs sms-postgres

# Connect to database
psql -h localhost -U sms_user -d sms_student_db
```

### Redis Connection Issues

```bash
# Test Redis connection
redis-cli ping

# View Redis logs
docker logs sms-redis
```

### Clear Redis Cache

```bash
redis-cli FLUSHALL
```

## Monitoring & Observability

### Zipkin Distributed Tracing

1. Access Zipkin UI: http://localhost:9411
2. Search for traces by service name: `student-service`
3. View request flows and latency

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/metrics` - Custom and system metrics
- `/actuator/prometheus` - Metrics in Prometheus format
- `/actuator/info` - Application information

## Next Steps (Phase 2)

- Authentication & Authorization (JWT/OAuth)
- Class management
- Attendance tracking
- Fee management
- Report generation
- Notifications (Email/SMS)

## Contributing

1. Follow TDD approach (tests first)
2. Maintain 80%+ code coverage
3. Use conventional commits
4. Update API documentation
5. Add integration tests for new endpoints

## License

Apache 2.0

## Support

For issues and questions, please create an issue in the repository.
