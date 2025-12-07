# School Management System - Quick Start Guide

**Last Updated:** 2025-12-06

## Prerequisites

- Java 21 installed
- Maven 3.6+ installed
- PostgreSQL 18+ running
- Git (optional)

---

## Step 1: Database Setup

### Start PostgreSQL

Make sure PostgreSQL is running on port 5432.

### Create Databases

```sql
CREATE DATABASE sms_student_db;
CREATE DATABASE sms_config_db;
```

### Run Schema Scripts

**Windows:**
```cmd
psql -U postgres -d sms_student_db -f specs\planning\school_management.sql
psql -U postgres -d sms_config_db -f specs\planning\school_management.sql
```

**Linux/Mac:**
```bash
psql -U postgres -d sms_student_db -f specs/planning/school_management.sql
psql -U postgres -d sms_config_db -f specs/planning/school_management.sql
```

### Verify Database Setup

```sql
-- Connect to sms_student_db
\c sms_student_db

-- List tables
\dt

-- Should show: students, enrollment_history

-- Connect to sms_config_db
\c sms_config_db

-- List tables
\dt

-- Should show: configuration_settings
```

---

## Step 2: Build the Project

### Navigate to Backend Directory

```bash
cd D:\wks-sms-specs-itr3\backend
```

### Build All Services

```bash
mvn clean install -DskipTests
```

**Expected Output:**
```
BUILD SUCCESS
Total time:  ~10-15 seconds
```

---

## Step 3: Run Student Service

### Option A: Using Maven

```bash
cd student-service
mvn spring-boot:run
```

### Option B: Using JAR

```bash
cd student-service
java -jar target/student-service-1.0.0-SNAPSHOT.jar
```

### Verify Service is Running

**Check Logs:**
```
Started StudentServiceApplication in X.XXX seconds
```

**Check Endpoints:**
- Health: http://localhost:8081/actuator/health
- Swagger UI: http://localhost:8081/swagger-ui.html
- API Docs: http://localhost:8081/api/v1/api-docs

---

## Step 4: Test Student Service

### Using Swagger UI

1. Open http://localhost:8081/swagger-ui.html
2. Expand "Student Management" section
3. Try creating a student using the "Try it out" button

### Using cURL

**Create a Student:**
```bash
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2010-05-15",
    "mobile": "9876543210",
    "email": "john.doe@example.com",
    "address": "123 Main St",
    "fathersName": "Richard Doe",
    "mothersName": "Jane Doe",
    "identificationMark": "Mole on left arm",
    "aadhaarNumber": "123456789012"
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "studentId": "STD-20241206-0001",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "age": 14,
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "status": "ACTIVE",
  "version": 0,
  "createdAt": "2025-12-06T...",
  ...
}
```

**Get All Students:**
```bash
curl http://localhost:8081/api/v1/students?page=0&size=20
```

**Get Student by ID:**
```bash
curl http://localhost:8081/api/v1/students/STD-20241206-0001
```

**Update Student:**
```bash
curl -X PUT http://localhost:8081/api/v1/students/STD-20241206-0001 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "mobile": "9876543210",
    "status": "ACTIVE",
    "version": 0
  }'
```

**Delete Student:**
```bash
curl -X DELETE http://localhost:8081/api/v1/students/STD-20241206-0001
```

---

## Step 5: Test Error Scenarios

### Duplicate Mobile
```bash
# Create first student
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Brown",
    "dateOfBirth": "2011-03-20",
    "mobile": "9999999999",
    "email": "alice@example.com"
  }'

# Try creating another student with same mobile (should fail with 409 Conflict)
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bob",
    "lastName": "Green",
    "dateOfBirth": "2012-06-10",
    "mobile": "9999999999",
    "email": "bob@example.com"
  }'
```

**Expected Response (409 Conflict):**
```json
{
  "type": "https://api.school.com/errors/duplicate-mobile",
  "title": "Duplicate Mobile Number",
  "status": 409,
  "detail": "Mobile number already exists: 9999999999",
  "timestamp": "...",
  "correlationId": "...",
  "errorCode": "DUPLICATE_MOBILE"
}
```

### Invalid Age
```bash
# Try creating student who is 2 years old (should fail with 422)
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Baby",
    "lastName": "Young",
    "dateOfBirth": "2023-01-01",
    "mobile": "8888888888",
    "email": "baby@example.com"
  }'
```

**Expected Response (422 Unprocessable Entity):**
```json
{
  "type": "https://api.school.com/errors/invalid-age",
  "title": "Invalid Student Age",
  "status": 422,
  "detail": "Invalid student age: 2. Age must be between 3 and 18 years.",
  "timestamp": "...",
  "correlationId": "...",
  "errorCode": "INVALID_AGE"
}
```

### Validation Errors
```bash
# Try creating student with missing required fields
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "",
    "mobile": "invalid"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "type": "https://api.school.com/errors/validation-failed",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed for one or more fields",
  "timestamp": "...",
  "correlationId": "...",
  "errorCode": "VALIDATION_ERROR",
  "errors": {
    "firstName": "First name cannot be blank",
    "lastName": "Last name is required",
    "dateOfBirth": "Date of birth is required",
    "mobile": "Mobile number must be 10-15 digits"
  }
}
```

---

## Step 6: Test CORS (Optional)

If you have a frontend running on port 3000 or 5173, try making a request to verify CORS is working.

**Example using JavaScript:**
```javascript
fetch('http://localhost:8081/api/v1/students')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('CORS Error:', error));
```

Should work without CORS errors.

---

## Common Issues & Solutions

### Issue: "Connection refused to localhost:5432"
**Solution:** PostgreSQL is not running. Start PostgreSQL service.

### Issue: "Database 'sms_student_db' does not exist"
**Solution:** Run Step 1 to create databases.

### Issue: "Port 8081 already in use"
**Solution:** Another application is using port 8081. Either:
- Stop the other application
- Change port in `application.yml`

### Issue: "Could not find or load main class"
**Solution:** Run `mvn clean install` to rebuild the project.

### Issue: MapStruct mapper not found
**Solution:** Ensure MapStruct processor is configured in parent POM and run `mvn clean compile`.

---

## Environment Variables (Optional)

You can override default configuration using environment variables:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=sms_student_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# Run service
mvn spring-boot:run
```

---

## Logs and Debugging

### Enable DEBUG Logging

Edit `application.yml`:
```yaml
logging:
  level:
    com.school.sms.student: DEBUG
    org.hibernate.SQL: DEBUG
```

### View SQL Queries

With DEBUG logging enabled, you'll see all SQL queries in the console:
```
Hibernate: insert into students (student_id, first_name, ...) values (?, ?, ...)
```

---

## Next Steps

1. **Create Sample Data:** Use POST endpoints to create test students
2. **Test Search:** Try searching by lastName, fathersName, status
3. **Test Pagination:** Experiment with page, size, sort parameters
4. **Test Updates:** Try updating students and verify optimistic locking
5. **Integration Testing:** Run integration tests (to be implemented)

---

## Configuration Service (Not Yet Implemented)

Configuration Service will run on port 8082 once implemented (Phase 6).

---

## Redis Caching (Not Yet Implemented)

Redis caching will be added in Phase 8. For now, all data is fetched from PostgreSQL.

---

## Useful Commands

### Maven Commands
```bash
# Clean build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific service
cd student-service && mvn spring-boot:run

# Run tests
mvn test

# Generate test coverage report
mvn jacoco:report
```

### Database Commands
```bash
# Connect to database
psql -U postgres -d sms_student_db

# View all students
SELECT * FROM students;

# View enrollment history
SELECT * FROM enrollment_history;

# Count students
SELECT COUNT(*) FROM students;

# View active students
SELECT * FROM students WHERE status = 'ACTIVE';
```

---

## Support

For issues or questions:
1. Check `LESSONS_LEARNED.md` for known issues
2. Review `BACKEND_IMPLEMENTATION_SUMMARY.md` for implementation details
3. Consult `BACKEND_TASKS.md` for task-specific information
4. Check Swagger UI for API documentation

---

**Happy Testing!**
