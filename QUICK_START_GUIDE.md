# Quick Start Guide - School Management System

## Complete Application Setup and Testing

This guide will help you quickly set up and test the complete School Management System with both backend and frontend.

---

## Prerequisites

Before starting, ensure you have:

- ✅ **Java 17 or higher** (`java -version`)
- ✅ **Maven 3.6+** (`mvn -version`)
- ✅ **Node.js 20.x** (`node -version`)
- ✅ **PostgreSQL** running (for backend)
- ✅ **Git** (optional, for version control)

---

## Quick Start (3 Steps)

### Step 1: Start the Backend

Open a terminal and run:

```bash
cd D:\wks-sms-specs\backend\student-service
mvn spring-boot:run
```

**Expected Output**:
```
Started StudentServiceApplication in X.XXX seconds
```

**Verify Backend**:
- Open browser: http://localhost:8081/swagger-ui.html
- You should see the Swagger API documentation

### Step 2: Start the Frontend

Open a NEW terminal and run:

```bash
cd D:\wks-sms-specs\frontend\sms-frontend
npm install  # (only needed once)
npm run dev
```

**Expected Output**:
```
VITE vX.X.X ready in XXXms
Local: http://localhost:5173/
```

### Step 3: Test the Application

Open your browser: **http://localhost:5173**

You should see the School Management System homepage!

---

## Testing the Application

### Test 1: Create a New Student

1. Click **"Add New Student"** button
2. Fill in the form:
   ```
   First Name: Rajesh
   Last Name: Kumar
   Date of Birth: 2015-05-15 (should show Age: 10)
   Street: 123 MG Road
   City: Bangalore
   State: Karnataka
   Pin Code: 560001
   Mobile: 9876543210
   Email: rajesh@example.com
   Father Name: Suresh Kumar
   ```
3. Click **"Create Student"**
4. You should see:
   - ✅ Success toast notification
   - ✅ Redirect to student list
   - ✅ New student in the list

### Test 2: View Student Details

1. From the student list, click on the **Student ID** (e.g., STU-2025-00001)
2. You should see:
   - ✅ Complete student information
   - ✅ Status badge (ACTIVE)
   - ✅ Edit, Delete, and Status toggle buttons

### Test 3: Edit Student

1. From student details, click **"Edit"**
2. Modify some fields (e.g., change mobile number)
3. Click **"Update Student"**
4. You should see:
   - ✅ Success notification
   - ✅ Updated information in details view

### Test 4: Search Students

1. Go to student list
2. Enter search criteria:
   - Last Name: Kumar
   - Status: Active
3. Click **"Search"**
4. You should see:
   - ✅ Filtered results
   - ✅ Matching students only

### Test 5: Change Student Status

1. Open student details
2. Click **"Deactivate"** button
3. You should see:
   - ✅ Status changes to INACTIVE
   - ✅ Badge color changes to red
   - ✅ Button text changes to "Activate"

### Test 6: Delete Student

1. Open student details
2. Click **"Delete"** button
3. Confirm the deletion
4. You should see:
   - ✅ Confirmation dialog
   - ✅ Success notification
   - ✅ Redirect to student list
   - ✅ Student removed from list (soft deleted)

---

## Validation Testing

### Test Invalid Age

1. Create new student
2. Enter Date of Birth: 2023-01-01 (age 2, too young)
3. Try to submit
4. Expected: ✅ Validation error: "Student age must be between 3 and 18 years"

### Test Duplicate Mobile

1. Create a student with mobile: 9876543210
2. Try to create another student with same mobile
3. Expected: ✅ Error toast: "Mobile number already registered"

### Test Invalid Mobile Format

1. Create new student
2. Enter mobile: 12345 (less than 10 digits)
3. Expected: ✅ Validation error: "Mobile must be 10 digits"

### Test Invalid Email

1. Create new student
2. Enter email: invalid-email
3. Expected: ✅ Validation error: "Invalid email"

---

## API Testing (Optional)

### Using Swagger UI

1. Open: http://localhost:8081/swagger-ui.html
2. Try API endpoints directly:
   - POST /students - Create student
   - GET /students - List students
   - GET /students/{id} - Get student details
   - PUT /students/{id} - Update student
   - DELETE /students/{id} - Delete student

### Using cURL

Create a student:
```bash
curl -X POST http://localhost:8081/students \
  -H "Content-Type: application/json" \
  -H "X-User-ID: ADMIN001" \
  -d '{
    "firstName": "Priya",
    "lastName": "Sharma",
    "dateOfBirth": "2016-08-20",
    "street": "456 Park Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pinCode": "400001",
    "mobile": "9876543211",
    "email": "priya@example.com",
    "fatherNameOrGuardian": "Ram Sharma"
  }'
```

---

## Troubleshooting

### Backend Not Starting

**Issue**: Port 8081 already in use
```
Solution: Stop the process using port 8081 or change the port in application.yml
```

**Issue**: Database connection failed
```
Solution: Ensure PostgreSQL is running and credentials are correct in application.yml
```

### Frontend Not Starting

**Issue**: Port 5173 already in use
```
Solution: Vite will automatically use the next available port
```

**Issue**: Cannot connect to backend
```
Solution:
1. Check backend is running on port 8081
2. Verify .env.development has VITE_API_BASE_URL=http://localhost:8081
3. Check CORS is enabled in backend
```

### API Errors

**Issue**: 404 Not Found
```
Solution: Check the backend is running and the endpoint URL is correct
```

**Issue**: CORS error
```
Solution: Backend has CORS enabled, but verify the frontend origin is allowed
```

**Issue**: 409 Conflict (Duplicate mobile)
```
Solution: This is expected behavior. Use a different mobile number
```

---

## Application URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:5173 | Main application |
| Backend API | http://localhost:8081 | Student Service API |
| Swagger UI | http://localhost:8081/swagger-ui.html | API Documentation |
| API Docs | http://localhost:8081/api-docs | OpenAPI JSON |
| Health Check | http://localhost:8081/actuator/health | Backend health status |

---

## Default Configuration

### Backend
- **Port**: 8081
- **Database**: PostgreSQL (student_db)
- **User Header**: X-User-ID (default: SYSTEM)

### Frontend
- **Port**: 5173 (dev server)
- **API Base URL**: http://localhost:8081
- **Page Size**: 20 students per page
- **Toast Duration**: 5 seconds

---

## Development Workflow

### Making Changes

**Backend Changes**:
1. Edit Java code
2. Maven automatically rebuilds (if using Spring Boot DevTools)
3. Or restart: `mvn spring-boot:run`

**Frontend Changes**:
1. Edit React components
2. Vite automatically hot-reloads
3. See changes instantly in browser

### Viewing Logs

**Backend Logs**:
- Terminal where `mvn spring-boot:run` is running
- Look for SQL queries, API calls, exceptions

**Frontend Logs**:
- Browser Developer Console (F12)
- Network tab for API calls
- Console tab for errors

---

## Production Build

### Backend

```bash
cd D:\wks-sms-specs\backend\student-service
mvn clean package
java -jar target/student-service-0.0.1-SNAPSHOT.jar
```

### Frontend

```bash
cd D:\wks-sms-specs\frontend\sms-frontend
npm run build
npm run preview  # Test production build
```

Serve `dist/` folder with any static file server.

---

## Sample Data

### Create Multiple Students Quickly

Use Swagger UI or cURL to create multiple students for testing:

**Student 1**: Rajesh Kumar (M, 10 years)
**Student 2**: Priya Sharma (F, 9 years)
**Student 3**: Amit Patel (M, 12 years)
**Student 4**: Sneha Reddy (F, 15 years)
**Student 5**: Vikram Singh (M, 8 years)

This will give you a good dataset for testing search and pagination.

---

## Next Steps

After successful testing:

1. ✅ **Review Implementation**:
   - Check FRONTEND_IMPLEMENTATION_SUMMARY.md
   - Review PRESENTATION_LAYER_IMPLEMENTATION_SUMMARY.md

2. ✅ **Plan Enhancements**:
   - Add unit tests
   - Implement Configuration Service UI
   - Add authentication

3. ✅ **Deploy**:
   - Choose hosting platform
   - Configure environment variables
   - Deploy and test

---

## Support

For issues or questions:

1. Check troubleshooting section above
2. Review implementation summaries
3. Check backend/frontend logs
4. Verify API specifications in specs/architecture/

---

## Success Criteria

You've successfully set up the application if:

- ✅ Backend running on port 8081
- ✅ Frontend running on port 5173
- ✅ Can create a new student
- ✅ Can view student list
- ✅ Can edit student details
- ✅ Can search students
- ✅ Can delete students
- ✅ Form validation working
- ✅ Toast notifications showing
- ✅ No console errors

---

**Happy Testing!** 🎉

---

**Last Updated**: 2025-11-18
**Version**: 1.0.0
