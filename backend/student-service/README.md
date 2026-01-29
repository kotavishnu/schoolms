# Student Service - School Management System

**Version:** 1.0.0
**Port:** 8081
**Tech Stack:** Spring Boot 3.3.5, Java 21, PostgreSQL 18, Redis 7, Drools 9.44

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

---

## Overview

Student Service is a microservice responsible for managing student records, enrollments, and related operations in the School Management System. It implements Domain-Driven Design (DDD) with clean architecture principles.

---

## Features

### Core Functionality
- ✅ Student CRUD operations (Create, Read, Update, Delete)
- ✅ Enrollment history management
- ✅ Advanced search with pagination
- ✅ Phone number validation
- ✅ Business rule validation using Drools
- ✅ Optimistic locking for concurrent updates

### Technical Features
- ✅ Redis caching for performance
- ✅ MapStruct for DTO-Domain mapping
- ✅ RFC 7807 Problem Details for errors
- ✅ OpenAPI/Swagger documentation
- ✅ Actuator health checks
- ✅ Structured JSON logging
- ✅ Test coverage >70%

### Business Rules (Drools)
- **BR-1:** Student age must be between 3-18 years
- **BR-2:** Mobile number must be unique (10 digits)
- **BR-3:** Aadhaar number must be unique (12 digits, optional)
- **BR-4:** One enrollment per student per academic year

---

## Architecture

### Layered Architecture
```
┌─────────────────────────────────────────────┐
│  Presentation Layer (Controllers, DTOs)    │
├─────────────────────────────────────────────┤
│  Application Layer (Services, Mappers)     │
├─────────────────────────────────────────────┤
│  Domain Layer (Entities, Repositories)     │
├─────────────────────────────────────────────┤
│  Infrastructure (JPA, Redis, Drools)       │
└─────────────────────────────────────────────┘
```

### Key Packages
- `controller` - REST API endpoints
- `service` - Business logic orchestration
- `domain` - Rich domain models and interfaces
- `infrastructure` - JPA entities, repositories, configs
- `rules` - Drools rule execution

---

## Prerequisites

### Required Software
- **Java:** 21 or higher
- **Maven:** 3.8+ (or use wrapper: `./mvnw`)
- **PostgreSQL:** 18+ (running on port 5433)
- **Redis:** 7+ (running on port 6379)
- **Docker:** 24+ (optional, for containerized deployment)

### Environment Variables
```bash
DB_USERNAME=postgres
DB_PASSWORD=postgres
REDIS_HOST=localhost
REDIS_PORT=6379
SPRING_PROFILES_ACTIVE=dev
```

---

## Quick Start

### Option 1: Local Development

#### 1. Start PostgreSQL (Port 5433)
```bash
docker run -d \
  --name sms-postgres \
  -e POSTGRES_DB=student_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e TZ=UTC \
  -p 5433:5432 \
  postgres:18-alpine
```

#### 2. Start Redis
```bash
docker run -d \
  --name sms-redis \
  -p 6379:6379 \
  redis:7-alpine
```

#### 3. Execute Database Schema
```bash
psql -h localhost -p 5433 -U postgres -d student_db -f ../../specs/planning/school_management.sql
```

#### 4. Build and Run Application
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run JAR directly
java -jar target/student-service-1.0.0.jar
```

#### 5. Verify Service
```bash
# Health check
curl http://localhost:8081/actuator/health

# Swagger UI
open http://localhost:8081/swagger-ui.html
```

---

### Option 2: Docker Compose

```bash
# From backend directory
cd backend

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f student-service

# Stop services
docker-compose down
```

---

## Configuration

### Application Profiles

#### `application.yml` (Base)
- Database: PostgreSQL on port 5433 with UTC timezone
- Redis: Database 0
- JPA: Validate schema (ddl-auto: validate)
- Actuator: Health, metrics, prometheus

#### `application-dev.yml` (Development)
- SQL logging enabled
- Redis localhost
- Debug logging for com.school.student

#### `application-prod.yml` (Production)
- Environment variables for sensitive data
- INFO level logging
- Connection pooling tuned for production

### Key Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8081 | Service HTTP port |
| `spring.datasource.url` | jdbc:postgresql://localhost:5433/student_db?TimeZone=UTC | Database URL |
| `spring.data.redis.database` | 0 | Redis database index |
| `management.endpoints.web.exposure.include` | health,info,metrics,prometheus | Actuator endpoints |

---

## API Endpoints

### Student Management

#### Create Student
```http
POST /api/v1/students
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main St",
  "fathersName": "James Doe",
  "mothersName": "Jane Doe",
  "identificationMark": "Mole on left cheek",
  "aadhaarNumber": "123456789012"
}

Response: 201 Created
{
  "id": 1,
  "studentId": "STD-20260128-0001",
  "firstName": "John",
  "lastName": "Doe",
  "age": 15,
  "status": "ACTIVE",
  "version": 0
}
```

#### Get Student by ID
```http
GET /api/v1/students/STD-20260128-0001

Response: 200 OK (Cached for 10 minutes)
```

#### Search Students
```http
GET /api/v1/students?lastName=Doe&status=ACTIVE&page=0&size=20

Response: 200 OK (Paginated)
```

#### Update Student
```http
PUT /api/v1/students/STD-20260128-0001
{
  "firstName": "Jane",
  "lastName": "Smith",
  "mobile": "9876543211",
  "status": "ACTIVE",
  "version": 0
}

Response: 200 OK (Cache evicted)
```

#### Delete Student
```http
DELETE /api/v1/students/STD-20260128-0001

Response: 204 No Content (Cache evicted)
```

#### Validate Phone
```http
POST /api/v1/students/validate-phone
{
  "mobile": "9876543210"
}

Response: 200 OK
{
  "available": true
}
```

### Enrollment Management

#### Create Enrollment
```http
POST /api/v1/students/STD-20260128-0001/enrollment-history
{
  "academicYear": "2025-2026",
  "gradeClass": "5",
  "section": "A",
  "enrollmentDate": "2025-06-01",
  "remarks": "Regular admission"
}

Response: 201 Created
```

#### Get Enrollment History
```http
GET /api/v1/students/STD-20260128-0001/enrollment-history

Response: 200 OK
[
  {
    "id": 1,
    "academicYear": "2025-2026",
    "gradeClass": "5",
    "section": "A",
    "enrollmentDate": "2025-06-01",
    "status": "ACTIVE"
  }
]
```

---

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories

#### Unit Tests (Domain)
```bash
mvn test -Dtest=StudentTest,EnrollmentTest
```

#### Unit Tests (Service)
```bash
mvn test -Dtest=StudentServiceTest
```

#### Integration Tests (TestContainers)
```bash
mvn verify -Dtest=StudentControllerIntegrationTest
```

### Test Coverage Report
```bash
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html
```

### Current Test Coverage
- **Domain Layer:** 100% (17 tests)
- **Service Layer:** 85% (8 tests)
- **Overall:** >70% ✅

---

## Deployment

### Build Docker Image
```bash
docker build -t sms/student-service:1.0.0 .
```

### Run Container
```bash
docker run -d \
  --name student-service \
  -p 8081:8081 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5433/student_db?TimeZone=UTC \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e REDIS_HOST=host.docker.internal \
  sms/student-service:1.0.0
```

### Docker Compose (Recommended)
```bash
docker-compose up -d
```

---

## Troubleshooting

### Issue: Service fails to start

**Symptoms:** Application exits immediately after startup

**Solutions:**
1. Check database connectivity
   ```bash
   psql -h localhost -p 5433 -U postgres -d student_db -c "SELECT 1"
   ```
2. Verify Redis is running
   ```bash
   redis-cli -h localhost -p 6379 ping
   ```
3. Check application logs
   ```bash
   tail -f logs/student-service.log
   ```

---

### Issue: Drools compilation errors

**Symptoms:** "Drools rules compilation failed"

**Solutions:**
1. Verify DRL files exist in `src/main/resources/rules/student/`
2. Check DRL syntax
3. Ensure Drools version matches pom.xml (9.44.0.Final)

---

### Issue: Cache not working

**Symptoms:** Database queries executed on every request

**Solutions:**
1. Check Redis connectivity
2. Verify `@EnableCaching` annotation
3. Check cache configuration in `CacheConfig.java`
4. View cache metrics: `http://localhost:8081/actuator/metrics/cache.gets`

---

### Issue: Test failures with TestContainers

**Symptoms:** "Could not start container"

**Solutions:**
1. Ensure Docker is running
2. Check Docker resources (memory, disk)
3. Pull images manually:
   ```bash
   docker pull postgres:18-alpine
   docker pull redis:7-alpine
   ```

---

### Issue: Timezone mismatch errors

**Symptoms:** "ERROR: time zone 'Asia/Calcutta' not recognized"

**Solutions:**
- **CRITICAL:** Ensure UTC timezone is set in 3 places:
  1. JDBC URL: `?TimeZone=UTC`
  2. Hibernate property: `jdbc.time_zone: UTC`
  3. JVM: `-Duser.timezone=UTC`

---

## Health Checks

### Liveness Probe
```bash
curl http://localhost:8081/actuator/health
```

### Readiness Probe
```bash
curl http://localhost:8081/actuator/health/readiness
```

### Metrics
```bash
# Prometheus format
curl http://localhost:8081/actuator/prometheus

# Application metrics
curl http://localhost:8081/actuator/metrics
```

---

## Performance

### Optimizations Implemented
- ✅ Redis caching (10-minute TTL for student lookups)
- ✅ Database connection pooling (HikariCP)
- ✅ JPA batch inserts (batch size: 50)
- ✅ Optimistic locking (version field)
- ✅ Response compression enabled

### Expected Performance
- **p95 Response Time:** <200ms
- **Throughput:** ~1000 req/sec (concurrent users: 100)
- **Cache Hit Ratio:** >80%

---

## Security

### Authentication
- ⚠️ **Not Implemented:** JWT/OAuth deferred to future iteration
- Current: No authentication required

### Input Validation
- ✅ Bean Validation (@Valid)
- ✅ Drools business rules
- ✅ SQL injection prevention (parameterized queries)

### CORS Configuration
- Allowed Origins: localhost:3000, 5173, 5174, 5175
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS

---

## Monitoring

### Key Metrics to Monitor
- `http.server.requests` - Request count and latency
- `cache.gets` - Cache hit/miss ratio
- `hikaricp.connections.active` - Database connections
- `jvm.memory.used` - Memory usage

### Log Files
- **Location:** `logs/student-service.log`
- **Format:** JSON (Logstash encoder)
- **Rotation:** Daily, 30-day retention

---

## Support

### Useful Commands

```bash
# View active connections
psql -h localhost -p 5433 -U postgres -d student_db -c "SELECT * FROM pg_stat_activity"

# Clear Redis cache
redis-cli FLUSHDB

# View application logs (Docker)
docker logs -f sms-student-service

# Restart service
docker-compose restart student-service
```

---

## License

Internal use only - School Management System

---

**Last Updated:** 2026-01-28
**Maintainer:** Backend Development Team
**Contact:** backend@school.com
