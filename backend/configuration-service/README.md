# Configuration Service

School Management System - Configuration Service

## Overview

This microservice manages system-wide configuration settings for the School Management System. It provides a centralized location for storing and retrieving configuration data across different categories (GENERAL, ACADEMIC, FINANCIAL).

## Technical Specifications

- **Group:** com.school
- **Artifact:** configuration-service
- **Java Version:** 21
- **Spring Boot Version:** 3.3.5
- **Packaging:** JAR
- **Port:** 8082

## Dependencies

### Core Dependencies
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-data-redis
- spring-boot-starter-actuator
- postgresql (JDBC driver)

### Mapping & Code Generation
- mapstruct (version 1.5.5.Final)
- mapstruct-processor
- lombok

### Documentation & Monitoring
- springdoc-openapi-starter-webmvc-ui (version 2.6.0)
- micrometer-registry-prometheus

### Testing
- spring-boot-starter-test
- testcontainers (PostgreSQL and generic for Redis)
- jacoco-maven-plugin (70% minimum threshold)

## Architecture

The project follows a layered architecture pattern:

```
src/main/java/com/school/configuration/
├── controller/           # Presentation Layer - REST Controllers
├── service/              # Application Layer - Business Logic
├── domain/
│   ├── model/           # Domain Models (Entities)
│   ├── repository/      # Repository Interfaces
│   └── exception/       # Custom Domain Exceptions
└── infrastructure/
    ├── persistence/     # JPA Entities & Repository Implementations
    └── config/          # Spring Configuration Classes
```

## Database Configuration

- **Database Name:** config_db
- **PostgreSQL Port:** 5433 (shared instance with student-service)
- **Redis Database:** 1 (different from Student Service which uses 0)
- **Connection URL:** jdbc:postgresql://localhost:5433/config_db?TimeZone=UTC

## Building the Project

```bash
# Clean install without tests
mvn clean install -DskipTests

# Run with tests
mvn clean verify

# Run the application
mvn spring-boot:run
```

## Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn verify

# View coverage report
open target/site/jacoco/index.html
```

## Quality Gates

- **Code Coverage:** Minimum 70% (enforced by JaCoCo)
- **Test Framework:** JUnit 5, Mockito, TestContainers
- **Integration Tests:** TestContainers for PostgreSQL and Redis

## API Documentation

Once the service is running, access the Swagger UI at:
- http://localhost:8082/swagger-ui.html

API documentation available at:
- http://localhost:8082/api-docs

## Health Checks

Actuator endpoints are exposed for monitoring:
- http://localhost:8082/actuator/health
- http://localhost:8082/actuator/metrics
- http://localhost:8082/actuator/prometheus

## Key Differences from Student Service

1. **No Drools Dependency:** Configuration Service is simpler and doesn't require a rules engine
2. **Redis Database:** Uses database 1 (Student Service uses 0)
3. **Simpler Domain Model:** No complex business rules or validations
4. **Database:** Uses config_db on same PostgreSQL instance

## Project Status

**Status:** Structure Created
**Version:** 1.0.0
**Build Status:** SUCCESS

## Next Steps

- Implement domain models (Configuration entity)
- Create repository interfaces
- Implement service layer
- Add REST controllers
- Write comprehensive tests
- Configure Redis caching
