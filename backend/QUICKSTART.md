# Backend Quick Start Guide

## 5-Minute Setup

### Step 1: Start Docker (30 seconds)

```bash
cd backend
docker-compose up -d
```

Wait for containers to be healthy:
```bash
docker ps
# Should show 3 containers: sms-student-db, sms-config-db, sms-redis (all healthy)
```

### Step 2: Initialize Databases (1 minute)

```bash
# Student database
psql -h localhost -p 5433 -U postgres -d student_db -f ../docs/tasks/school_management.sql

# Configuration database
psql -h localhost -p 5434 -U postgres -d config_db -f ../docs/tasks/school_management.sql
```

Password: `postgres`

### Step 3: Start Student Service (2 minutes)

```bash
cd student-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

Wait for: `Started StudentServiceApplication in X seconds`

### Step 4: Test Student Service (30 seconds)

Open browser: http://localhost:8081/api/v1/swagger-ui.html

Try:
```bash
# Create student
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2015-01-15",
    "mobile": "9876543210",
    "email": "john.doe@example.com",
    "address": "123 Main St, City, State",
    "guardianName": "Jane Doe",
    "motherName": "Mary Doe"
  }'

# Get statistics
curl http://localhost:8081/api/v1/students/statistics
```

### Step 5: Start Configuration Service (Optional)

```bash
cd ../configuration-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

Test: http://localhost:8082/api/v1/swagger-ui.html

## Verify Everything Works

1. **Student Service**: http://localhost:8081/actuator/health → Status: UP
2. **Configuration Service**: http://localhost:8082/actuator/health → Status: UP
3. **Database**: `psql -h localhost -p 5433 -U postgres -d student_db -c "\dt"` → 2 tables
4. **Redis**: `docker exec sms-redis redis-cli ping` → PONG

## Common Issues

### "Port 8081 already in use"
```bash
netstat -ano | findstr :8081
taskkill /PID <process_id> /F
```

### "PostgreSQL timezone error"
Always use: `-Duser.timezone=UTC`

### "Docker containers unhealthy"
```bash
docker-compose down -v
docker-compose up -d
```

## Next Steps

- Read `README.md` for complete documentation
- See `CREATE_REMAINING_FILES.md` for Configuration Service completion
- Check `../docs/tasks/BACKEND_TASKS.md` for task status
