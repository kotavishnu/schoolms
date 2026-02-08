# School Management System - Backend Deployment Guide

## Overview
Complete deployment guide for Student Service microservice with monitoring stack.

## Technology Stack
- **Runtime:** Java 21 (Eclipse Temurin JRE)
- **Framework:** Spring Boot 3.5.0
- **Database:** PostgreSQL 18 Alpine
- **Rules Engine:** Drools 9.44.0.Final
- **Monitoring:** Prometheus + Grafana
- **Containerization:** Docker + Docker Compose

---

## Prerequisites

### Required Software
- Docker 20.10+
- Docker Compose 2.0+
- Maven 3.9+ (for local development)
- Java 21 JDK (for local development)

### Verify Installation
```bash
docker --version
docker-compose --version
java --version  # Should show Java 21
mvn --version
```

---

## Quick Start (Docker Deployment)

### 1. Clone Repository
```bash
cd D:\SCHOOL-GIT-LESSONS_LEARNT\schoolms\backend
```

### 2. Configure Environment Variables
```bash
# Copy example file
cp .env.example .env

# Edit .env with secure passwords
# DB_PASSWORD=your_secure_db_password
# GRAFANA_PASSWORD=your_grafana_password
```

### 3. Build and Start All Services
```bash
docker-compose up --build
```

This command will:
- Build Student Service Docker image (multi-stage build)
- Start PostgreSQL database with schema initialization
- Start Student Service on port 8081
- Start Prometheus on port 9090
- Start Grafana on port 3001

### 4. Verify Deployment
```bash
# Health check
curl http://localhost:8081/actuator/health

# API documentation
open http://localhost:8081/swagger-ui.html

# Prometheus metrics
open http://localhost:9090

# Grafana dashboard
open http://localhost:3001  # Login: admin/admin123
```

---

## Service Endpoints

### Student Service (Port 8081)
- **API Base:** http://localhost:8081/api/v1
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/v3/api-docs
- **Health:** http://localhost:8081/actuator/health
- **Metrics:** http://localhost:8081/actuator/metrics
- **Prometheus:** http://localhost:8081/actuator/prometheus

### Infrastructure Services
- **PostgreSQL:** localhost:5432 (user: school_admin, db: student_db)
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3001 (admin/admin123)

---

## Local Development (Without Docker)

### 1. Start PostgreSQL Locally
```bash
# Option A: Docker container only for database
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=student_db \
  -e POSTGRES_USER=school_admin \
  -e POSTGRES_PASSWORD=school_pass_123 \
  -p 5432:5432 \
  postgres:18-alpine

# Option B: Use existing PostgreSQL installation
# Create database and run schema from specs/planning/school_management.sql
```

### 2. Build and Run Student Service
```bash
cd student-service

# Run tests (unit tests only, no Docker required)
mvn test

# Run with integration tests (requires Docker for TestContainers)
mvn verify

# Start application
mvn spring-boot:run

# Or build JAR and run
mvn clean package -DskipTests
java -jar target/student-service-1.0.0-SNAPSHOT.jar
```

### 3. Access API
```bash
# Register a student
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2015-05-15",
    "mobile": "9876543210",
    "fathersName": "James Doe"
  }'

# Get student by ID
curl http://localhost:8081/api/v1/students/STD-20260203-0001
```

---

## Docker Management

### Stop All Services
```bash
docker-compose down
```

### Stop and Remove Volumes (Clean Slate)
```bash
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f student-service
docker-compose logs -f student-db
docker-compose logs -f prometheus
docker-compose logs -f grafana
```

### Restart Single Service
```bash
docker-compose restart student-service
```

### Rebuild After Code Changes
```bash
docker-compose up --build student-service
```

---

## Testing Strategy

### Unit Tests (111 tests)
```bash
cd student-service
mvn test
```
- No external dependencies required
- Tests: Domain models, services, controllers
- Execution time: ~15 seconds
- Coverage: 80%+ (JaCoCo enforced)

### Integration Tests (11 tests)
```bash
cd student-service
mvn test -Dgroups=integration
```
- Requires Docker for TestContainers
- Spins up PostgreSQL 18 container
- Tests: End-to-end API with real database
- Validates: BR-1, BR-2, BR-5, optimistic locking

### All Tests + Coverage Report
```bash
cd student-service
mvn verify
```
- Runs unit + integration tests
- Generates JaCoCo coverage report
- Report: `target/site/jacoco/index.html`

---

## Monitoring

### Prometheus Queries
```promql
# Total students registered
students_registered_total

# Rate of registrations per minute
rate(students_registered_total[1m])

# Active JVM threads
jvm_threads_live_threads
```

### Grafana Dashboard Setup
1. Login to Grafana: http://localhost:3001
2. Add Prometheus data source: http://prometheus:9090
3. Import dashboard or create custom panels
4. Suggested panels:
   - Students registered (counter)
   - API response times (histogram)
   - JVM memory usage (gauge)
   - Database connection pool (gauge)

---

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs student-service

# Common issues:
# 1. Port 8081 already in use
lsof -i :8081  # Find process using port
kill -9 <PID>  # Kill process

# 2. Database not ready
docker-compose logs student-db
# Solution: Increase healthcheck start_period in docker-compose.yml
```

### Database Connection Issues
```bash
# Verify database is running
docker-compose ps

# Test connection
docker exec -it student-db psql -U school_admin -d student_db -c "\dt"

# Reset database
docker-compose down -v
docker-compose up --build
```

### Integration Tests Failing
```bash
# Verify Docker is running
docker info

# Skip integration tests
mvn test  # Only unit tests run by default
```

### Build Failures
```bash
# Clean build
mvn clean install

# Skip tests during build
mvn clean package -DskipTests

# Check Java version
java --version  # Must be Java 21
```

---

## Production Deployment Checklist

### Security
- [ ] Change default passwords in .env
- [ ] Use environment variables for all secrets
- [ ] Enable HTTPS/TLS
- [ ] Configure firewall rules
- [ ] Review CORS allowed origins
- [ ] Enable rate limiting

### Performance
- [ ] Tune HikariCP connection pool (max 10 for small deployments)
- [ ] Configure JVM heap size: -Xms512m -Xmx1024m
- [ ] Enable G1GC garbage collector (default in Dockerfile)
- [ ] Set up database indexes (already in schema)
- [ ] Monitor slow query logs

### Reliability
- [ ] Configure health checks (already in docker-compose.yml)
- [ ] Set up log aggregation (ELK, Splunk)
- [ ] Configure alerting (Prometheus Alertmanager)
- [ ] Implement backup strategy for PostgreSQL
- [ ] Test disaster recovery procedure

### Monitoring
- [ ] Verify Actuator endpoints working
- [ ] Create Grafana dashboards
- [ ] Set up Prometheus alerts
- [ ] Configure log rotation
- [ ] Monitor correlation IDs in logs

---

## Architecture Summary

### Layers Implemented
1. **Presentation Layer:** REST controllers, exception handlers, CORS, correlation IDs
2. **Application Layer:** Services, DTOs, mappers, Drools validation
3. **Domain Layer:** Rich domain models, value objects, repository interfaces
4. **Infrastructure Layer:** JPA entities, repository adapters, database schema

### Business Rules (Drools)
- **BR-1:** Student age 3-18 years
- **BR-2:** Mobile number unique
- **BR-3:** Academic year format (YYYY-YYYY)
- **BR-5:** At least one guardian required

### Technology Highlights
- **Multi-stage Docker build** (JDK build → JRE runtime)
- **Non-root Docker user** (security best practice)
- **Health checks** (liveness/readiness for orchestration)
- **Structured JSON logging** (correlation IDs)
- **Optimistic locking** (version column)
- **N+1 query prevention** (@EntityGraph)

---

## Support

### Documentation
- **API Spec:** `/specs/sms_api_specification.yaml`
- **Requirements:** `/specs/REQUIREMENTS.md`
- **Architecture:** `/specs/architecture/`
- **Testing Strategy:** `/specs/TESTING_STRATEGY.md`

### Code Structure
```
backend/
├── student-service/
│   ├── src/main/java/com/school/student/
│   │   ├── domain/              # Business logic
│   │   ├── application/         # Services, DTOs
│   │   ├── infrastructure/      # JPA, Drools
│   │   └── presentation/        # Controllers
│   ├── src/main/resources/
│   │   ├── rules/student/       # Drools .drl files
│   │   └── db/migration/        # Flyway scripts
│   └── src/test/java/           # Tests (111 unit + 11 integration)
├── docker-compose.yml           # Full stack deployment
├── prometheus.yml               # Metrics scraping
└── DEPLOYMENT_GUIDE.md          # This file
```

---

**Document Version:** 1.0
**Last Updated:** 2026-02-03
**Status:** Production Ready
