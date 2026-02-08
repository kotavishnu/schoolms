# Student Service

RESTful microservice for student management in School Management System.

## Technology Stack
- Java 21, Spring Boot 3.5.0
- PostgreSQL 18+, Drools 9.44.0
- SpringDoc OpenAPI 2.7.0
- Docker, Prometheus, Grafana

## Quick Start
```bash
# Docker deployment
docker-compose up --build

# Local development
mvn spring-boot:run
```

## API Documentation
- Swagger UI: http://localhost:8081/swagger-ui.html
- Health: http://localhost:8081/actuator/health
- Metrics: http://localhost:8081/actuator/prometheus

## Testing
```bash
mvn test                                     # Unit tests only (111 tests)
mvn test -Dgroups=integration               # Integration tests only (requires Docker)
mvn verify                                   # All tests + coverage report
```

**Note:** Integration tests require Docker. Unit tests exclude integration tests by default via JUnit @Tag("integration").

## Features
- CRUD operations with business rules validation
- Drools rule engine (BR-1: Age, BR-2: Mobile uniqueness, BR-5: Guardian)
- OpenAPI documentation
- Prometheus metrics
- Correlation ID tracing
- RFC 7807 error handling

## Production Ready
- Health checks, monitoring, structured logging
- Connection pooling, optimistic locking
- Multi-stage Docker build, non-root user
