# Quick Start Guide - School Management System Backend

## 5-Minute Setup

### 1. Prerequisites Check
```bash
java -version    # Should show Java 21
mvn -version     # Should show Maven 3.9+
docker --version # Should show Docker 20+
```

### 2. Start Infrastructure
```bash
cd D:\wks-sms-specs-itr2
docker-compose up -d
```

**Wait 30 seconds** for services to initialize.

### 3. Build Application
```bash
mvn clean install -DskipTests
```

### 4. Run Student Service
```bash
cd student-service
mvn spring-boot:run
```

### 5. Test API
Open browser: http://localhost:8081/swagger-ui.html

---

## Test the API

### Create a Student
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

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "studentId": "STU-2025-00001",
  "firstName": "John",
  "lastName": "Doe",
  "currentAge": 10,
  "mobile": "9876543210",
  "status": "ACTIVE"
}
```

### List All Students
```bash
curl http://localhost:8081/api/v1/students
```

### Get Student by ID
```bash
curl http://localhost:8081/api/v1/students/student-id/STU-2025-00001
```

---

## Verify Infrastructure

### Check PostgreSQL
```bash
docker exec -it sms-postgres psql -U sms_user -d sms_student_db -c "SELECT * FROM students;"
```

### Check Redis Cache
```bash
docker exec -it sms-redis redis-cli KEYS "students::*"
```

### Check Zipkin Traces
Open browser: http://localhost:9411

---

## Troubleshooting

### Port 8081 Already in Use
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8081 | xargs kill -9
```

### Cannot Connect to Database
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# View logs
docker logs sms-postgres

# Restart
docker-compose restart postgres
```

### Clear Everything and Restart
```bash
# Stop all services
docker-compose down -v

# Remove volumes
docker volume prune -f

# Start fresh
docker-compose up -d

# Wait 30 seconds, then rebuild
mvn clean install
```

---

## Important URLs

| Service | URL |
|---------|-----|
| Student API | http://localhost:8081/api/v1/students |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| API Docs | http://localhost:8081/api/v1/api-docs |
| Health Check | http://localhost:8081/actuator/health |
| Metrics | http://localhost:8081/actuator/metrics |
| Zipkin | http://localhost:9411 |

---

## Next Steps

1. **Explore API:** Use Swagger UI to test all endpoints
2. **View Traces:** Check Zipkin for distributed tracing
3. **Read Docs:** See README.md for detailed documentation
4. **Run Tests:** Execute `mvn test` to run unit tests
5. **Implement Remaining:** Follow REMAINING_TASKS_GUIDE.md

---

## Business Rules to Remember

1. Student age must be 3-18 years
2. Mobile number must be unique
3. StudentID is auto-generated (STU-YYYY-XXXXX)
4. Only firstName, lastName, mobile, status are editable
5. Delete is soft delete (sets status to INACTIVE)

---

## Common Operations

### Stop Services
```bash
docker-compose stop
```

### View Logs
```bash
# Student Service
mvn spring-boot:run | tee logs/student-service.log

# Docker services
docker-compose logs -f postgres
docker-compose logs -f redis
```

### Run with Different Profile
```bash
# Development (default)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Need Help?

1. Check `README.md` for full documentation
2. Review `IMPLEMENTATION_SUMMARY.md` for architecture details
3. See `REMAINING_TASKS_GUIDE.md` for code templates
4. Refer to `specs/planning/BACKEND_TASKS.md` for task specifications

---

**Happy Coding!**
