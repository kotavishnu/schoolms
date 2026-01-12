# School Management System - Backend Services

This directory contains the microservices backend for the School Management System built with Spring Boot.

## Services

### 1. Student Service (Port 8081)
- **Purpose**: Manages student records
- **Database**: PostgreSQL (port 5433, DB: studentdb)
- **Redis DB**: 0
- **Endpoints**: `/api/v1/students`

### 2. Configuration Service (Port 8082)
- **Purpose**: Manages configuration settings
- **Database**: PostgreSQL (port 5434, DB: configdb)
- **Redis DB**: 1
- **Endpoints**: `/api/v1/configurations`

## Technology Stack

- **Framework**: Spring Boot 3.4.1
- **Java**: 21
- **Build Tool**: Maven
- **Database**: PostgreSQL 18
- **Cache**: Redis 7
- **ORM**: Spring Data JPA with Hibernate
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Monitoring**: Spring Boot Actuator with Prometheus
- **Testing**: JUnit 5, Mockito, TestContainers
- **Mapping**: MapStruct 1.5.5
- **Code Generation**: Lombok

## Architecture

Both services follow **Domain-Driven Design (DDD)** with clean architecture:

```
presentation/     # REST Controllers, DTOs
├── controller/
├── dto/
└── exception/

application/      # Use Cases, Services
├── service/
└── mapper/

domain/           # Business Logic
├── model/        # Domain entities, value objects
├── repository/   # Repository interfaces
└── exception/    # Domain exceptions

infrastructure/   # External Concerns
├── persistence/  # JPA entities, repositories
├── cache/        # Redis cache
├── config/       # Configuration
└── generator/    # ID generators
```

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker and Docker Compose (for local development)
- PostgreSQL 18 (if running locally without Docker)
- Redis 7 (if running locally without Docker)

## Getting Started

### Option 1: Using Docker Compose (Recommended)

1. **Start all services**:
   ```bash
   cd backend
   docker-compose up -d
   ```

2. **View logs**:
   ```bash
   docker-compose logs -f student-service
   docker-compose logs -f configuration-service
   ```

3. **Stop services**:
   ```bash
   docker-compose down
   ```

4. **Stop and remove volumes** (clean slate):
   ```bash
   docker-compose down -v
   ```

### Option 2: Running Locally

1. **Start PostgreSQL and Redis**:
   ```bash
   cd backend
   docker-compose up -d postgres-student postgres-config redis
   ```

2. **Run Student Service**:
   ```bash
   cd student-service
   mvn spring-boot:run
   ```

3. **Run Configuration Service** (in a new terminal):
   ```bash
   cd configuration-service
   mvn spring-boot:run
   ```

## Database Setup

The database schema is automatically initialized from `../specs/planning/school_management.sql` when using Docker Compose.

For manual setup:
```bash
# Connect to Student DB
psql -h localhost -p 5433 -U student_service -d studentdb -f ../specs/planning/school_management.sql

# Connect to Config DB
psql -h localhost -p 5434 -U config_service -d configdb -f ../specs/planning/school_management.sql
```

## API Documentation

Once services are running, access Swagger UI:

- **Student Service**: http://localhost:8081/swagger-ui.html
- **Configuration Service**: http://localhost:8082/swagger-ui.html

API Docs (JSON):
- **Student Service**: http://localhost:8081/api-docs
- **Configuration Service**: http://localhost:8082/api-docs

## Health Checks

- **Student Service**: http://localhost:8081/actuator/health
- **Configuration Service**: http://localhost:8082/actuator/health

## Metrics (Prometheus)

- **Student Service**: http://localhost:8081/actuator/prometheus
- **Configuration Service**: http://localhost:8082/actuator/prometheus

## Testing

### Run All Tests
```bash
cd student-service
mvn test

cd configuration-service
mvn test
```

### Run with Coverage
```bash
mvn test jacoco:report
```

View coverage report: `target/site/jacoco/index.html`

## Building

### Build JARs
```bash
cd student-service
mvn clean package

cd configuration-service
mvn clean package
```

### Build Docker Images
```bash
cd backend
docker-compose build
```

## Environment Variables

### Student Service
- `DB_HOST`: PostgreSQL host (default: localhost)
- `DB_PORT`: PostgreSQL port (default: 5433)
- `DB_NAME`: Database name (default: studentdb)
- `DB_USERNAME`: Database user (default: student_service)
- `DB_PASSWORD`: Database password (default: student_password)
- `REDIS_HOST`: Redis host (default: localhost)
- `REDIS_PORT`: Redis port (default: 6379)
- `CORS_ORIGINS`: Allowed CORS origins (default: http://localhost:5173,http://localhost:3000)

### Configuration Service
Same as above but with:
- `DB_PORT`: 5434
- `DB_NAME`: configdb
- `DB_USERNAME`: config_service
- `DB_PASSWORD`: config_password

## Troubleshooting

### Port Already in Use
```bash
# Find process using port
lsof -i :8081
lsof -i :8082

# Kill process
kill -9 <PID>
```

### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps postgres-student
docker-compose ps postgres-config

# View PostgreSQL logs
docker-compose logs postgres-student
```

### Redis Connection Issues
```bash
# Check if Redis is running
docker-compose ps redis

# Connect to Redis CLI
docker-compose exec redis redis-cli
> ping
PONG
```

## Development Workflow

1. **Make code changes**
2. **Run tests**: `mvn test`
3. **Build**: `mvn clean package`
4. **Restart service**: `docker-compose restart student-service`
5. **View logs**: `docker-compose logs -f student-service`

## Project Structure

```
backend/
├── student-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/schoolms/student/
│   │   │   │   ├── domain/
│   │   │   │   ├── application/
│   │   │   │   ├── infrastructure/
│   │   │   │   └── presentation/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── configuration-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/schoolms/configuration/
│   │   │   │   ├── domain/
│   │   │   │   ├── application/
│   │   │   │   ├── infrastructure/
│   │   │   │   └── presentation/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## API Endpoints

### Student Service (`/api/v1/students`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/students` | List all students (with search & filter) |
| GET | `/api/v1/students/{id}` | Get student by ID |
| POST | `/api/v1/students` | Create new student |
| PATCH | `/api/v1/students/{id}` | Update student |
| DELETE | `/api/v1/students/{id}` | Delete student |
| GET | `/api/v1/students/statistics` | Get student statistics |

### Configuration Service (`/api/v1/configurations`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/configurations` | List all configurations (with category filter) |
| GET | `/api/v1/configurations/{id}` | Get configuration by ID |
| POST | `/api/v1/configurations` | Create new configuration |
| PATCH | `/api/v1/configurations/{id}` | Update configuration |
| DELETE | `/api/v1/configurations/{id}` | Delete configuration |

## Performance Tuning

### HikariCP Connection Pool
- Maximum pool size: 20
- Minimum idle: 5
- Connection timeout: 30 seconds

### Redis Cache TTL
- Student cache: 2 hours
- List cache: 10 minutes
- Statistics cache: 5 minutes

### JPA Batch Processing
- Batch size: 20
- Order inserts: enabled
- Order updates: enabled

## Security Notes

- **Authentication**: Not implemented (as per requirements)
- **CORS**: Configured for local development
- **SQL Injection**: Protected by JPA/Hibernate parameterized queries
- **Input Validation**: Jakarta Validation annotations
- **Error Handling**: RFC 7807 Problem Details

## Next Steps

1. Integrate with frontend application (React)
2. Add comprehensive integration tests
3. Set up CI/CD pipeline
4. Add monitoring and observability (Prometheus + Grafana)
5. Implement caching strategies
6. Add API rate limiting
7. Implement audit logging

## Support

For issues and questions:
- Check logs: `docker-compose logs -f`
- Verify health: http://localhost:8081/actuator/health
- Review API docs: http://localhost:8081/swagger-ui.html

## License

Copyright © 2026 School Management System
