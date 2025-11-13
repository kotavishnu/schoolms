# Configuration Guide - School Management System

## Overview

This guide provides detailed information about configuring the School Management System for different environments (development, testing, and production).

## Table of Contents

1. [Environment Profiles](#environment-profiles)
2. [Database Configuration](#database-configuration)
3. [Redis Cache Configuration](#redis-cache-configuration)
4. [Security Configuration](#security-configuration)
5. [Logging Configuration](#logging-configuration)
6. [Actuator Configuration](#actuator-configuration)
7. [Application-Specific Settings](#application-specific-settings)
8. [Environment Variables](#environment-variables)
9. [Configuration Best Practices](#configuration-best-practices)

---

## Environment Profiles

The application supports three profiles:

### 1. Development Profile (`dev`)

**Usage**: `spring.profiles.active=dev`

**Characteristics**:
- Local PostgreSQL database
- Detailed logging (DEBUG level)
- H2 console enabled
- Spring DevTools for hot reload
- All Actuator endpoints exposed
- Swagger UI enabled

**When to use**: Local development and debugging

### 2. Test Profile (`test`)

**Usage**: `spring.profiles.active=test`

**Characteristics**:
- H2 in-memory database
- Flyway disabled (uses Hibernate DDL)
- No caching
- Random server port
- All error details exposed
- Fast startup

**When to use**: Unit and integration testing

### 3. Production Profile (`prod`)

**Usage**: `spring.profiles.active=prod`

**Characteristics**:
- Production PostgreSQL with SSL
- Redis Sentinel for high availability
- Minimal logging (WARN level)
- Limited Actuator endpoints
- Swagger UI disabled
- Optimized connection pooling
- Graceful shutdown enabled

**When to use**: Production deployment

---

## Database Configuration

### PostgreSQL Configuration

#### Development
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/school_management_db
    username: school_user
    password: school_password
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
```

#### Production
```yaml
spring:
  datasource:
    url: jdbc:postgresql://db-server:5432/school_management_db?ssl=true&sslmode=require
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
```

### HikariCP Connection Pool Settings

| Property | Development | Production | Purpose |
|----------|-------------|------------|---------|
| `maximum-pool-size` | 10 | 50 | Max connections in pool |
| `minimum-idle` | 2 | 10 | Minimum idle connections |
| `idle-timeout` | 300000 (5 min) | 600000 (10 min) | Max idle time |
| `max-lifetime` | 600000 (10 min) | 1800000 (30 min) | Max connection lifetime |
| `connection-timeout` | 30000 (30 sec) | 30000 (30 sec) | Max wait for connection |
| `leak-detection-threshold` | 60000 (1 min) | 120000 (2 min) | Connection leak detection |

### Flyway Migration

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    schemas: public
```

**Migration Scripts Location**: `src/main/resources/db/migration/`

**Naming Convention**: `V{version}__{description}.sql`

Examples:
- `V1__initial_schema.sql`
- `V2__add_indexes.sql`
- `V3__create_students_table.sql`

---

## Redis Cache Configuration

### Development (Single Instance)
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 8
        min-idle: 2
```

### Production (Sentinel for HA)
```yaml
spring:
  redis:
    password: ${REDIS_PASSWORD}
    ssl: true
    lettuce:
      pool:
        max-active: 100
        max-idle: 20
        min-idle: 10
    sentinel:
      master: mymaster
      nodes: redis-sentinel-1:26379,redis-sentinel-2:26379,redis-sentinel-3:26379
```

### Cache TTL Configuration

Cache time-to-live values (in seconds):

| Cache Name | Development | Production | Purpose |
|------------|-------------|------------|---------|
| `students` | 3600 (1 hr) | 7200 (2 hr) | Student data |
| `fee-structures` | 86400 (24 hr) | 86400 (24 hr) | Fee configuration |
| `classes` | 43200 (12 hr) | 86400 (24 hr) | Class information |
| `configurations` | 86400 (24 hr) | 172800 (48 hr) | System settings |
| `users` | 7200 (2 hr) | 14400 (4 hr) | User data |

---

## Security Configuration

### JWT Configuration

```yaml
application:
  security:
    jwt:
      secret-key: ${JWT_SECRET}
      expiration: 900000          # 15 minutes
      refresh-expiration: 604800000  # 7 days
      issuer: school-management-system
```

**Generating Secure JWT Secret**:
```bash
# Generate 256-bit random key
openssl rand -base64 32

# Or use online generator
# https://generate-random.org/api-key-generator
```

### CORS Configuration

#### Development (Permissive)
```yaml
application:
  security:
    cors:
      allowed-origins: http://localhost:3000,http://localhost:4200
      allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
```

#### Production (Restrictive)
```yaml
application:
  security:
    cors:
      allowed-origins: https://school.example.com
      allowed-methods: GET,POST,PUT,DELETE,PATCH
      allowed-headers: Authorization,Content-Type,X-Requested-With
      allow-credentials: true
```

### Rate Limiting

```yaml
application:
  security:
    rate-limit:
      enabled: true
      limit: 1000        # Requests per duration
      duration: 60000    # 1 minute in milliseconds
```

---

## Logging Configuration

### Log Levels by Environment

#### Development
```yaml
logging:
  level:
    root: INFO
    com.school.management: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

#### Production
```yaml
logging:
  level:
    root: WARN
    com.school.management: INFO
    org.springframework.web: WARN
```

### Log File Configuration

#### Development
```yaml
logging:
  file:
    name: logs/school-management-dev.log
    max-size: 10MB
    max-history: 30    # Keep 30 days
```

#### Production
```yaml
logging:
  file:
    name: /var/log/school-management/application.log
    max-size: 50MB
    max-history: 60    # Keep 60 days
  logback:
    rollingpolicy:
      total-size-cap: 10GB
```

### Structured Logging (JSON)

For production, use JSON logging for better log aggregation:

```yaml
logging:
  pattern:
    file: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss.SSS}","level":"%level","thread":"%thread","logger":"%logger","message":"%msg","exception":"%ex"}%n'
```

---

## Actuator Configuration

### Endpoints

#### Development (All Exposed)
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers,env,beans
```

#### Production (Minimal)
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### Health Indicators

```yaml
management:
  health:
    redis:
      enabled: true
    db:
      enabled: true
    diskspace:
      enabled: true
```

### Metrics

```yaml
management:
  metrics:
    tags:
      application: ${spring.application.name}
      environment: production
    export:
      prometheus:
        enabled: true
```

**Prometheus Scrape Endpoint**: `http://localhost:8080/api/actuator/prometheus`

---

## Application-Specific Settings

### Student Management

```yaml
application:
  student:
    code-prefix: STU              # Student code prefix
    min-age: 3                    # Minimum age in years
    max-age: 18                   # Maximum age in years
    photo-max-size: 5242880       # 5MB in bytes
    photo-allowed-types: image/jpeg,image/png
```

### Fee Management

```yaml
application:
  fee:
    receipt-prefix: REC           # Receipt number prefix
    academic-year-format: yyyy-yyyy
    default-currency: INR
```

### File Upload

```yaml
application:
  upload:
    max-file-size: 10MB
    max-request-size: 15MB
    upload-dir: /var/uploads/school-management
```

### Pagination

```yaml
application:
  pagination:
    default-page-size: 20
    max-page-size: 100
```

---

## Environment Variables

### Required Variables

Create a `.env` file (copy from `.env.example`):

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=school_management_db
DB_USERNAME=school_user
DB_PASSWORD=secure_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT Security
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### Loading Environment Variables

**Option 1: .env file (development)**
```bash
# Using direnv or similar tool
export $(cat .env | xargs)
java -jar application.jar
```

**Option 2: Command line**
```bash
java -jar application.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://db:5432/school_db
```

**Option 3: System environment variables**
```bash
export DB_HOST=db-server
export DB_PASSWORD=secure-password
java -jar application.jar
```

**Option 4: Docker Compose**
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - DB_HOST=postgres
  - DB_PASSWORD=${DB_PASSWORD}
```

---

## Configuration Best Practices

### 1. Externalize Sensitive Data

Never hardcode passwords or secrets:

**Bad**:
```yaml
spring:
  datasource:
    password: mypassword123  # Never do this!
```

**Good**:
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}  # Use environment variable
```

### 2. Use Secrets Management

For production, use dedicated secrets management:
- **AWS**: AWS Secrets Manager, Parameter Store
- **Azure**: Azure Key Vault
- **GCP**: Secret Manager
- **HashiCorp**: Vault
- **Kubernetes**: Secrets

### 3. Profile-Specific Configuration

Separate configuration by environment:
- `application.yml` - Common settings
- `application-dev.yml` - Development overrides
- `application-test.yml` - Test overrides
- `application-prod.yml` - Production overrides

### 4. Configuration Validation

Validate configuration on startup:

```java
@Configuration
@ConfigurationProperties(prefix = "application.student")
@Validated
public class StudentProperties {

    @NotBlank
    private String codePrefix;

    @Min(1)
    @Max(25)
    private int minAge;

    // ... getters/setters
}
```

### 5. Document Configuration

Maintain this guide and update when adding new configuration properties.

### 6. Security Checklist

Production configuration security:
- [ ] JWT secret is strong and unique (256-bit)
- [ ] Database password is strong
- [ ] SSL/TLS enabled for database connections
- [ ] Redis password set
- [ ] CORS origins restricted to known domains
- [ ] Actuator endpoints secured
- [ ] Swagger UI disabled
- [ ] Error details not exposed
- [ ] File upload size limits set
- [ ] Rate limiting enabled

### 7. Performance Tuning

Key settings for performance:
- Connection pool size appropriate for load
- Cache TTL values optimized
- JPA batch size configured
- Query fetch size set
- HTTP compression enabled
- Database indexes created

### 8. Monitoring

Enable monitoring for:
- Application health
- Database connection pool metrics
- Cache hit/miss rates
- API response times
- Error rates
- JVM metrics

---

## Troubleshooting

### Issue: Application fails to start - Database connection refused

**Cause**: PostgreSQL not running or wrong credentials

**Solution**:
1. Check PostgreSQL status: `pg_ctl status`
2. Verify connection: `psql -h localhost -U school_user -d school_management_db`
3. Check environment variables: `echo $DB_HOST`

### Issue: Redis connection timeout

**Cause**: Redis not running or wrong host/port

**Solution**:
1. Check Redis: `redis-cli ping` (should return PONG)
2. Verify configuration in application.yml
3. For production, check Redis Sentinel status

### Issue: JWT authentication fails

**Cause**: Incorrect JWT secret or token expired

**Solution**:
1. Verify JWT_SECRET environment variable is set
2. Check token expiration time
3. Ensure secret key is at least 256 bits

### Issue: Flyway migration fails

**Cause**: Database schema mismatch or migration conflict

**Solution**:
1. Check Flyway version history: `SELECT * FROM flyway_schema_history;`
2. Repair if needed: `mvn flyway:repair`
3. Clean and re-run (development only): `mvn flyway:clean flyway:migrate`

---

## Additional Resources

- [Spring Boot Configuration Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Redis Configuration](https://redis.io/docs/manual/config/)

---

**Last Updated**: November 11, 2025
**Version**: 1.0.0
