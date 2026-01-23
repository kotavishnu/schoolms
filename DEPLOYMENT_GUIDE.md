# School Management System - Deployment Guide

**Version:** 1.0
**Date:** 2026-01-22
**Status:** Ready for QA and Production Deployment

---

## System Overview

The School Management System is a microservices-based web application with:
- **Backend:** 2 Spring Boot microservices (Student Service, Configuration Service)
- **Frontend:** React TypeScript SPA (Vite)
- **Database:** PostgreSQL 18 (2 separate databases)
- **Cache:** Redis 7
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)

---

## Prerequisites

### Required Software
- **Java:** JDK 21 LTS
- **Node.js:** v18+ or v20+ (LTS)
- **Docker:** v24+ with Docker Compose
- **PostgreSQL:** v18 (via Docker)
- **Redis:** v7 (via Docker)
- **Maven:** v3.9+ (for backend builds)
- **npm:** v9+ or v10+ (for frontend builds)

### Optional Tools
- **Git:** For version control
- **curl/Postman:** For API testing
- **Web Browser:** Chrome, Firefox, or Edge (for UI testing)

---

## Quick Start (Development)

### 1. Clone Repository
```bash
cd D:\SCHOOL-GIT-AUTONOMOUS_FE_INSTR_ITR3\schoolms
```

### 2. Start Infrastructure (Docker)
```bash
cd backend
docker-compose up -d

# Verify containers running
docker ps
# Expected: 3 containers (student-db, config-db, redis)
```

### 3. Initialize Databases
```bash
# Student database
psql -h localhost -p 5433 -U postgres -d student_db -f ../docs/tasks/school_management.sql

# Configuration database (uses same schema)
psql -h localhost -p 5434 -U postgres -d config_db -f ../docs/tasks/school_management.sql
```

### 4. Start Backend Services

**Student Service (Port 8081):**
```bash
cd backend/student-service
mvn clean install
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"

# Verify: http://localhost:8081/api/v1/swagger-ui.html
```

**Configuration Service (Port 8082):**
```bash
cd backend/configuration-service
mvn clean install
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"

# Verify: http://localhost:8082/api/v1/swagger-ui.html
```

### 5. Start Frontend
```bash
cd frontend
npm install
npm run dev

# Access: http://localhost:5173
```

---

## Detailed Deployment Steps

### Backend Deployment

#### Environment Variables

**Student Service (.env or application-{profile}.yml):**
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/student_db
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
```

**Configuration Service (.env or application-{profile}.yml):**
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/config_db
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
      database: 1
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
```

#### Build for Production

**JAR Build:**
```bash
cd backend/student-service
mvn clean package -DskipTests

cd ../configuration-service
mvn clean package -DskipTests

# Output JARs:
# backend/student-service/target/student-service-1.0.0.jar
# backend/configuration-service/target/configuration-service-1.0.0.jar
```

**Run JARs:**
```bash
java -Duser.timezone=UTC -jar backend/student-service/target/student-service-1.0.0.jar
java -Duser.timezone=UTC -jar backend/configuration-service/target/configuration-service-1.0.0.jar
```

#### Health Check Endpoints

- **Student Service:** http://localhost:8081/actuator/health
- **Configuration Service:** http://localhost:8082/actuator/health

---

### Frontend Deployment

#### Environment Configuration

**.env.development (Local):**
```
VITE_STUDENT_API_URL=http://localhost:8081
VITE_CONFIG_API_URL=http://localhost:8082
```

**.env.production (Production):**
```
VITE_STUDENT_API_URL=https://api.example.com/student
VITE_CONFIG_API_URL=https://api.example.com/config
```

#### Build for Production

```bash
cd frontend
npm run build

# Output: frontend/dist/ (static files)
# Size: ~194 KB (60 KB gzipped)
```

#### Deployment Options

**Option 1: Static File Server (Nginx)**
```nginx
server {
    listen 80;
    server_name example.com;

    root /var/www/schoolms/dist;
    index index.html;

    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy (optional)
    location /api/v1/students {
        proxy_pass http://localhost:8081/api/v1/students;
    }

    location /api/v1/configurations {
        proxy_pass http://localhost:8082/api/v1/configurations;
    }
}
```

**Option 2: Vite Preview**
```bash
npm run preview
# Serves dist/ on http://localhost:4173
```

**Option 3: Cloud Hosting**
- **Netlify/Vercel:** Deploy `dist/` folder
- **AWS S3 + CloudFront:** Upload `dist/` to S3, configure CloudFront
- **Azure Static Web Apps:** Deploy `dist/` folder

---

## Docker Deployment (Full Stack)

### Docker Compose (All Services)

**File:** `docker-compose.full.yml`
```yaml
version: '3.8'

services:
  student-db:
    image: postgres:18
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      TZ: UTC
    volumes:
      - student-data:/var/lib/postgresql/data
      - ./docs/tasks/school_management.sql:/docker-entrypoint-initdb.d/init.sql

  config-db:
    image: postgres:18
    ports:
      - "5434:5432"
    environment:
      POSTGRES_DB: config_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      TZ: UTC
    volumes:
      - config-data:/var/lib/postgresql/data
      - ./docs/tasks/school_management.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

  student-service:
    build: ./backend/student-service
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://student-db:5432/student_db
      SPRING_REDIS_HOST: redis
      JAVA_OPTS: -Duser.timezone=UTC
    depends_on:
      - student-db
      - redis

  configuration-service:
    build: ./backend/configuration-service
    ports:
      - "8082:8082"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://config-db:5432/config_db
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_DATABASE: 1
      JAVA_OPTS: -Duser.timezone=UTC
    depends_on:
      - config-db
      - redis

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - student-service
      - configuration-service

volumes:
  student-data:
  config-data:
  redis-data:
```

**Deploy:**
```bash
docker-compose -f docker-compose.full.yml up -d
```

---

## API Endpoints Reference

### Student Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/students | Create new student |
| GET | /api/v1/students | List students (with search) |
| GET | /api/v1/students/{id} | Get student by ID |
| PUT | /api/v1/students/{id} | Update student (name, mobile, status only) |
| DELETE | /api/v1/students/{id} | Delete student |
| PATCH | /api/v1/students/{id}/status | Update status only |
| GET | /api/v1/students/mobile/{mobile} | Find by mobile |
| GET | /api/v1/students/email/{email} | Find by email |

### Configuration Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/configurations | Create configuration |
| GET | /api/v1/configurations | List all configurations |
| GET | /api/v1/configurations/{id} | Get by ID |
| GET | /api/v1/configurations/category/{cat} | Get by category |
| PUT | /api/v1/configurations/{id} | Update configuration |
| DELETE | /api/v1/configurations/{id} | Delete configuration |

---

## Monitoring and Observability

### Spring Boot Actuator

**Endpoints Available:**
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info

**Access:**
- Student Service: http://localhost:8081/actuator
- Configuration Service: http://localhost:8082/actuator

### Logging

**Configuration (application.yml):**
```yaml
logging:
  level:
    root: INFO
    com.school: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

**Log Files:**
- Student Service: `logs/student-service.log`
- Configuration Service: `logs/configuration-service.log`

---

## Security Configuration

### CORS (Cross-Origin Resource Sharing)

**Configured Ports (CorsConfig.java):**
- http://localhost:5173 (Vite dev server)
- http://localhost:5174
- http://localhost:5175
- http://localhost:3000 (React dev server)
- http://localhost:4173 (Vite preview)

**Production:** Update CorsConfig to allow production frontend domain.

### Database Security

**Recommendations:**
- Change default passwords (`postgres/postgres`)
- Use environment variables for credentials
- Enable SSL for PostgreSQL connections
- Restrict database access to application IPs

### API Security (Future - Phase 2)

**Planned Enhancements:**
- JWT authentication (Spring Security 6.x)
- RBAC (SUPER_ADMIN, ADMIN, STAFF, TEACHER)
- API rate limiting
- Input sanitization

---

## Performance Tuning

### Database Connection Pool (HikariCP)

**Configuration (application.yml):**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### Redis Caching

**TTL Configuration:**
- Students: 60 minutes
- Student lists: 30 minutes
- Configurations: 120 minutes

**Cache Invalidation:**
- Create: No cache update
- Update: Cache updated (@CachePut)
- Delete: Cache evicted (@CacheEvict)

### Frontend Optimization

**Implemented:**
- Code splitting (React.lazy)
- Debounced search (300ms)
- Memoization (useMemo, useCallback)
- Tree shaking (Vite)

---

## Backup and Recovery

### Database Backups

**Manual Backup:**
```bash
# Student database
pg_dump -h localhost -p 5433 -U postgres student_db > student_db_backup.sql

# Configuration database
pg_dump -h localhost -p 5434 -U postgres config_db > config_db_backup.sql
```

**Automated Backup (cron):**
```bash
0 2 * * * pg_dump -h localhost -p 5433 -U postgres student_db > /backups/student_db_$(date +\%Y\%m\%d).sql
0 2 * * * pg_dump -h localhost -p 5434 -U postgres config_db > /backups/config_db_$(date +\%Y\%m\%d).sql
```

**Restore:**
```bash
psql -h localhost -p 5433 -U postgres -d student_db < student_db_backup.sql
```

### Redis Persistence

**AOF (Append Only File) - Enabled:**
```bash
# redis.conf
appendonly yes
appendfsync everysec
```

**RDB Snapshots:**
```bash
# redis.conf
save 900 1       # Save after 900 sec if 1 key changed
save 300 10      # Save after 300 sec if 10 keys changed
save 60 10000    # Save after 60 sec if 10000 keys changed
```

---

## Troubleshooting

### Common Issues

**1. Port Already in Use**
```bash
# Find process using port
lsof -i :8081
netstat -ano | findstr :8081  # Windows

# Kill process
kill -9 <PID>
```

**2. Database Connection Failed**
```bash
# Check PostgreSQL running
docker ps | grep postgres
docker logs <container-id>

# Test connection
psql -h localhost -p 5433 -U postgres -d student_db
```

**3. Redis Connection Failed**
```bash
# Check Redis running
docker ps | grep redis
redis-cli -h localhost -p 6379 ping
# Expected: PONG
```

**4. Frontend API Calls Fail (CORS)**
- Verify backend CORS configuration includes frontend URL
- Check browser console for CORS errors
- Ensure backend services are running

**5. Build Failures**
```bash
# Backend
mvn clean install -U  # Force update dependencies

# Frontend
rm -rf node_modules package-lock.json
npm install
```

---

## Testing Guide

### Backend Testing

**Unit Tests:**
```bash
mvn test
```

**Integration Tests:**
```bash
mvn verify
```

**Manual API Testing (curl):**
```bash
# Create student
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2010-05-15",
    "mobile": "9876543210",
    "status": "ACTIVE"
  }'

# List students
curl http://localhost:8081/api/v1/students
```

### Frontend Testing

**Component Tests:**
```bash
npm test
```

**E2E Tests:**
```bash
npm run test:e2e
```

**Manual Testing:**
1. Open http://localhost:5173
2. Navigate to Students page
3. Click "Add Student"
4. Fill form and submit
5. Verify student appears in list
6. Edit student (only name, mobile, status)
7. Delete student

---

## Production Deployment Checklist

### Pre-Deployment
- [ ] Update environment variables (.env.production)
- [ ] Change database passwords
- [ ] Configure production API URLs
- [ ] Enable HTTPS/TLS
- [ ] Review CORS allowed origins
- [ ] Run full test suite (unit, integration, E2E)
- [ ] Performance testing (load tests)
- [ ] Security audit

### Deployment
- [ ] Build backend JARs (`mvn clean package`)
- [ ] Build frontend (`npm run build`)
- [ ] Deploy database migrations
- [ ] Deploy backend services
- [ ] Deploy frontend static files
- [ ] Verify health endpoints
- [ ] Test API endpoints
- [ ] Test frontend UI

### Post-Deployment
- [ ] Monitor logs for errors
- [ ] Check application metrics
- [ ] Verify database connections
- [ ] Test critical user flows
- [ ] Monitor Redis cache hit rate
- [ ] Set up automated backups
- [ ] Configure alerts (uptime, errors)

---

## Support and Maintenance

### Version Information
- **Backend:** Spring Boot 3.3.5, Java 21
- **Frontend:** React 18.3, TypeScript 5, Vite 5
- **Database:** PostgreSQL 18
- **Cache:** Redis 7

### Contact
- **Project Repository:** [GitHub URL]
- **Documentation:** docs/
- **Issue Tracking:** [JIRA/GitHub Issues URL]

---

**Last Updated:** 2026-01-22
**Document Version:** 1.0
**Status:** Production Ready (after QA phases 4 & 6)
