# Configuration Service

Configuration Management Microservice for School Management System.

## Overview

The Configuration Service manages school configuration settings grouped by category (GENERAL, ACADEMIC, FINANCIAL). It provides a RESTful API with Redis caching for high performance.

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.0**
- **PostgreSQL 18+** (Database)
- **Redis 7.2** (Caching)
- **MapStruct 1.5.5** (DTO Mapping)
- **SpringDoc OpenAPI 2.7.0** (API Documentation)

## Features

- CRUD operations for configuration settings
- Category-based grouping (GENERAL, ACADEMIC, FINANCIAL)
- Redis caching with 5-minute TTL
- Optimistic locking for concurrent updates
- RESTful API with OpenAPI documentation
- Correlation ID for request tracing
- Actuator endpoints for monitoring

## Quick Start

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 18+
- Redis 7.2+

### Local Development

1. **Start PostgreSQL and Redis**

```bash
# PostgreSQL
docker run -d --name config-db -e POSTGRES_DB=config_db -e POSTGRES_USER=school_admin -e POSTGRES_PASSWORD=school_password -p 5432:5432 postgres:18-alpine

# Redis
docker run -d --name config-redis -p 6379:6379 redis:7-alpine
```

2. **Build the application**

```bash
mvn clean install
```

3. **Run the application**

```bash
mvn spring-boot:run
```

4. **Access the API**

- Application: http://localhost:8082
- Swagger UI: http://localhost:8082/swagger-ui.html
- Actuator: http://localhost:8082/actuator/health

### Docker Deployment

1. **Build Docker image**

```bash
docker build -t configuration-service:1.0.0 .
```

2. **Run with Docker Compose** (see root docker-compose.yml)

```bash
docker-compose up configuration-service
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/configurations | Create configuration |
| GET | /api/v1/configurations/{id} | Get configuration by ID |
| GET | /api/v1/configurations/grouped/{category} | Get grouped settings by category (cached) |
| GET | /api/v1/configurations | Get all configurations (paginated) |
| PUT | /api/v1/configurations/{id} | Update configuration |
| DELETE | /api/v1/configurations/{id} | Delete configuration |

## Configuration Categories

- **GENERAL**: School name, code, contact information
- **ACADEMIC**: Academic year, class capacity, grading
- **FINANCIAL**: Currency, fees, payment settings

## Data Types

- **STRING**: Text values
- **NUMBER**: Numeric values
- **BOOLEAN**: true/false
- **JSON**: Complex object structures

## Caching Strategy

Grouped settings endpoint (`/grouped/{category}`) is cached in Redis with:
- TTL: 5 minutes
- Cache invalidation: On any create/update/delete operation
- Fallback: Database query if Redis unavailable

## Testing

```bash
# Unit tests only
mvn test

# All tests including integration
mvn verify
```

## Monitoring

- **Health**: /actuator/health
- **Metrics**: /actuator/metrics
- **Prometheus**: /actuator/prometheus

## Database Schema

```sql
configurations (
  id BIGSERIAL PRIMARY KEY,
  category VARCHAR(50),
  config_key VARCHAR(100),
  config_value VARCHAR(1000),
  description VARCHAR(500),
  data_type VARCHAR(20),
  is_encrypted BOOLEAN,
  version BIGINT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  UNIQUE(category, config_key)
)
```

## Error Handling

All errors return RFC 7807 Problem Details format with correlation ID for tracing.

## Architecture

- **Domain Layer**: Business logic and entities
- **Application Layer**: Service orchestration and caching
- **Infrastructure Layer**: JPA persistence and Redis
- **Presentation Layer**: REST controllers

## License

Proprietary - School Management System
