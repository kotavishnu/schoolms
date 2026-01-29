# QA Test Plan
**School Management System - Quality Assurance Engineer Tasks**

Version: 1.0.0
Execution Model: Waterfall / Single-Pass Implementation
Target: Comprehensive Testing Coverage for Backend & Frontend

---

## Test Overview

This plan provides a comprehensive checklist for testing the **Student Service** (Port 8081), **Configuration Service** (Port 8082), and **Frontend SPA** (Port 3000). Tests cover unit, integration, and end-to-end scenarios to ensure production readiness.

**Testing Scope:**
- Backend API functionality and business rules
- Frontend UI interactions and API integrations
- Database integrity and constraints
- Error handling and edge cases
- Performance and load testing
- Security and accessibility

---

## Test Environment Setup

### [QA-001] Verify Test Environment Configuration
**Type:** Pre-requisite Setup

**Steps:**
1. Verify Docker Compose environment is running:
   - `docker-compose ps` shows all services UP
   - Student Service health check passes: `curl http://localhost:8081/actuator/health`
   - Configuration Service health check passes: `curl http://localhost:8082/actuator/health`
   - Frontend accessible: `curl http://localhost:3000`
2. Verify databases initialized:
   - Connect to `student_db`: `psql -h localhost -p 5433 -U postgres -d student_db`
   - Verify tables exist: `\dt`
   - Connect to `config_db` and verify tables
3. Verify Redis is accessible: `redis-cli -p 6379 ping`
4. Clear existing test data from previous runs

**Success Criteria:**
- All services running without errors
- Databases contain expected schema
- Redis responds to ping
- No leftover test data

---

## Backend API Testing (Student Service)

### [QA-002] Test Student Registration (Happy Path)
**Type:** Integration Test

**Steps:**
1. Send POST request to `/api/v1/students` with valid data:
   ```json
   {
       "firstName": "Alice",
       "lastName": "Johnson",
       "dateOfBirth": "2015-06-15",
       "mobile": "9876543100",
       "email": "alice.j@example.com",
       "fathersName": "Bob Johnson",
       "mothersName": "Carol Johnson"
   }
   ```
2. Verify response status: **201 Created**
3. Verify response body contains:
   - `studentId` (format: STD-YYYYMMDD-NNNN)
   - `status: "ACTIVE"`
   - `version: 0`
   - `createdAt` (valid ISO timestamp)
4. Query database to verify record inserted:
   ```sql
   SELECT * FROM students WHERE mobile = '9876543100';
   ```

**Success Criteria:**
- HTTP 201 status returned
- Student ID auto-generated correctly
- Database record matches request payload
- All timestamps populated

---

### [QA-003] Test Student Registration (Age Validation)
**Type:** Unit Test (Business Rules)

**Steps:**
1. **Test Case 1: Student too young (age < 3)**
   - Send POST request with `dateOfBirth: "2024-01-01"` (age ~2 years)
   - Verify response status: **400 Bad Request**
   - Verify error message: "Student must be between 3 and 18 years old"
2. **Test Case 2: Student too old (age > 18)**
   - Send POST request with `dateOfBirth: "2000-01-01"` (age ~26 years)
   - Verify response status: **400 Bad Request**
   - Verify error message contains age violation
3. **Test Case 3: Boundary values**
   - Test with exactly 3 years old (should pass)
   - Test with exactly 18 years old (should pass)

**Success Criteria:**
- Age validation enforced by Drools rules
- Appropriate error messages returned
- Boundary values handled correctly

---

### [QA-004] Test Student Registration (Duplicate Mobile)
**Type:** Integration Test (Constraint Validation)

**Steps:**
1. Register first student with mobile: "9876543200"
2. Verify first registration succeeds (201 Created)
3. Attempt to register second student with same mobile: "9876543200"
4. Verify response status: **409 Conflict**
5. Verify error message: "Mobile number 9876543200 is already registered"
6. Query database to confirm only one record exists with that mobile

**Success Criteria:**
- Duplicate mobile rejected
- HTTP 409 status returned
- Database enforces UNIQUE constraint
- Error message is user-friendly

---

### [QA-005] Test Student Registration (Field Validation)
**Type:** Unit Test (Bean Validation)

**Steps:**
1. **Test Case 1: Missing required field (firstName)**
   - Send POST request without `firstName`
   - Verify 400 Bad Request
   - Verify error: "First name is required"
2. **Test Case 2: Invalid mobile format**
   - Send POST request with `mobile: "123"` (not 10 digits)
   - Verify 400 Bad Request
   - Verify error: "Mobile must be exactly 10 digits"
3. **Test Case 3: Invalid email format**
   - Send POST request with `email: "invalid-email"`
   - Verify 400 Bad Request
   - Verify error: "Invalid email format"
4. **Test Case 4: Name contains numbers**
   - Send POST request with `firstName: "John123"`
   - Verify 400 Bad Request
   - Verify error: "Name must contain only letters and spaces"

**Success Criteria:**
- All validation rules enforced
- Field-level error messages returned in RFC 7807 format
- Multiple validation errors returned if multiple fields invalid

---

### [QA-006] Test Get Student by ID
**Type:** Integration Test

**Steps:**
1. Register a student and note the `studentId` (e.g., "STD-20260128-0001")
2. Send GET request to `/api/v1/students/STD-20260128-0001`
3. Verify response status: **200 OK**
4. Verify response body matches registered student data
5. Test non-existent student ID:
   - Send GET request to `/api/v1/students/STD-99999999-9999`
   - Verify response status: **404 Not Found**
   - Verify error message: "Student not found: STD-99999999-9999"

**Success Criteria:**
- Valid student ID returns correct data
- Invalid student ID returns 404
- Response includes computed `age` field

---

### [QA-007] Test Search Students with Filters
**Type:** Integration Test

**Steps:**
1. Register 5 students with different last names and statuses:
   - Alice Smith (ACTIVE)
   - Bob Smith (ACTIVE)
   - Charlie Johnson (INACTIVE)
   - David Williams (ACTIVE)
   - Eve Brown (ACTIVE)
2. **Test Case 1: Search by lastName**
   - Send GET request to `/api/v1/students?lastName=Smith`
   - Verify only Alice and Bob returned
3. **Test Case 2: Search by status**
   - Send GET request to `/api/v1/students?status=INACTIVE`
   - Verify only Charlie returned
4. **Test Case 3: Combined filters**
   - Send GET request to `/api/v1/students?lastName=Smith&status=ACTIVE`
   - Verify only Alice and Bob returned
5. **Test Case 4: Pagination**
   - Send GET request to `/api/v1/students?page=0&size=2`
   - Verify response contains `pageable` metadata (totalElements, totalPages)
   - Verify only 2 students returned

**Success Criteria:**
- Filters work correctly in isolation and combination
- Pagination metadata accurate
- Case-insensitive search for lastName

---

### [QA-008] Test Update Student (Allowed Fields)
**Type:** Integration Test

**Steps:**
1. Register a student with mobile "9876543300"
2. Send PUT request to `/api/v1/students/{studentId}` with:
   ```json
   {
       "firstName": "UpdatedFirstName",
       "lastName": "UpdatedLastName",
       "mobile": "9876543301",
       "status": "INACTIVE",
       "version": 0
   }
   ```
3. Verify response status: **200 OK**
4. Verify updated fields in response
5. Query database to confirm changes persisted
6. Attempt to update immutable field (e.g., `dateOfBirth`) - verify rejected

**Success Criteria:**
- Only firstName, lastName, mobile, status can be updated
- Version number incremented (optimistic locking)
- Immutable fields cannot be modified

---

### [QA-009] Test Update Student (Optimistic Locking)
**Type:** Integration Test

**Steps:**
1. Register a student (version = 0)
2. Retrieve student details (note `version: 0`)
3. **Simulate concurrent update:**
   - User A sends PUT with `version: 0` to update firstName
   - User B sends PUT with `version: 0` (stale version) to update lastName
4. Verify User A's update succeeds (200 OK, version incremented to 1)
5. Verify User B's update fails (409 Conflict, optimistic lock exception)
6. Verify error message indicates version mismatch

**Success Criteria:**
- First update succeeds, version incremented
- Second update with stale version rejected (409)
- Database integrity maintained

---

### [QA-010] Test Delete Student
**Type:** Integration Test

**Steps:**
1. Register a student with studentId "STD-20260128-0010"
2. Verify student exists via GET request
3. Send DELETE request to `/api/v1/students/STD-20260128-0010`
4. Verify response status: **204 No Content**
5. Send GET request to verify student deleted:
   - Verify response status: **404 Not Found**
6. Query database to confirm record removed

**Success Criteria:**
- DELETE returns 204 on success
- Student no longer retrievable
- Database record removed

---

### [QA-011] Test Create Enrollment
**Type:** Integration Test

**Steps:**
1. Register a student
2. Send POST request to `/api/v1/students/{studentId}/enrollment-history`:
   ```json
   {
       "academicYear": "2025-2026",
       "gradeClass": "Grade-5",
       "section": "Section-A",
       "enrollmentDate": "2025-06-01",
       "remarks": "New enrollment"
   }
   ```
3. Verify response status: **201 Created**
4. Verify enrollment record returned with `status: "ACTIVE"`
5. Query database to confirm enrollment created

**Success Criteria:**
- Enrollment created successfully
- Student can have multiple enrollments for different academic years
- Foreign key (application-level) validated

---

### [QA-012] Test Duplicate Enrollment (Same Academic Year)
**Type:** Integration Test (Business Rule)

**Steps:**
1. Register a student
2. Create enrollment for academic year "2025-2026"
3. Attempt to create second enrollment for same academic year "2025-2026"
4. Verify response status: **409 Conflict**
5. Verify error message: "Enrollment already exists for academic year 2025-2026"
6. Query database to confirm only one enrollment exists for that academic year

**Success Criteria:**
- Duplicate enrollment rejected
- HTTP 409 returned
- UNIQUE constraint (student_id, academic_year) enforced

---

### [QA-013] Test Get Enrollment History
**Type:** Integration Test

**Steps:**
1. Register a student
2. Create 3 enrollments for different academic years:
   - 2023-2024, Grade-3, Section-A
   - 2024-2025, Grade-4, Section-B
   - 2025-2026, Grade-5, Section-A
3. Send GET request to `/api/v1/students/{studentId}/enrollment-history`
4. Verify response status: **200 OK**
5. Verify response contains all 3 enrollments
6. Verify enrollments ordered by enrollment date (descending)

**Success Criteria:**
- All enrollments retrieved correctly
- Response includes studentId and enrollments array
- Historical data preserved

---

## Backend API Testing (Configuration Service)

### [QA-014] Test Get All Configurations
**Type:** Integration Test

**Steps:**
1. Send GET request to `/api/v1/configurations`
2. Verify response status: **200 OK**
3. Verify response contains `configurations` array
4. Verify default configurations from seed data present (school_name, school_code, etc.)
5. **Test category filter:**
   - Send GET request to `/api/v1/configurations?category=GENERAL`
   - Verify only GENERAL category configurations returned

**Success Criteria:**
- All configurations retrieved when no filter applied
- Category filter works correctly
- Response includes id, category, key, value, description, dataType, isEncrypted, version, updatedAt

---

### [QA-015] Test Get Specific Configuration
**Type:** Integration Test

**Steps:**
1. Send GET request to `/api/v1/configurations/GENERAL/school_name`
2. Verify response status: **200 OK**
3. Verify response contains expected configuration (e.g., value: "Springfield Elementary")
4. **Test non-existent configuration:**
   - Send GET request to `/api/v1/configurations/GENERAL/non_existent_key`
   - Verify response status: **404 Not Found**

**Success Criteria:**
- Valid category/key returns configuration
- Invalid category/key returns 404
- Response matches database record

---

### [QA-016] Test Create Configuration (Upsert)
**Type:** Integration Test

**Steps:**
1. Send PUT request to `/api/v1/configurations/ACADEMIC/test_key`:
   ```json
   {
       "value": "test_value",
       "description": "Test configuration",
       "dataType": "STRING",
       "isEncrypted": false
   }
   ```
2. Verify response status: **201 Created** (new configuration)
3. Verify response contains created configuration with auto-generated ID
4. Query database to confirm record inserted
5. **Test idempotency:** Send same PUT request again
6. Verify response status: **200 OK** (updated configuration)

**Success Criteria:**
- First upsert creates configuration (201)
- Second upsert updates configuration (200)
- Composite unique key (category, key) enforced

---

### [QA-017] Test Update Configuration (Upsert)
**Type:** Integration Test

**Steps:**
1. Create configuration: `FINANCIAL/test_fee` with value "5000"
2. Send PUT request to `/api/v1/configurations/FINANCIAL/test_fee`:
   ```json
   {
       "value": "6000",
       "description": "Updated fee",
       "dataType": "NUMBER",
       "isEncrypted": false
   }
   ```
3. Verify response status: **200 OK**
4. Verify `value` updated to "6000"
5. Verify `version` incremented
6. Verify `updatedAt` timestamp changed
7. Query database to confirm changes persisted

**Success Criteria:**
- Configuration updated successfully
- Version number incremented
- Timestamp reflects update time

---

### [QA-018] Test Delete Configuration
**Type:** Integration Test

**Steps:**
1. Create configuration: `GENERAL/temp_setting`
2. Verify configuration exists via GET request
3. Send DELETE request to `/api/v1/configurations/GENERAL/temp_setting`
4. Verify response status: **204 No Content**
5. Send GET request to verify configuration deleted:
   - Verify response status: **404 Not Found**
6. Query database to confirm record removed

**Success Criteria:**
- DELETE returns 204 on success
- Configuration no longer retrievable
- Database record removed

---

### [QA-019] Test Get Grouped Configurations
**Type:** Integration Test

**Steps:**
1. Ensure multiple GENERAL category configurations exist (school_name, school_code, academic_year)
2. Send GET request to `/api/v1/configurations/grouped/GENERAL`
3. Verify response status: **200 OK**
4. Verify response format:
   ```json
   {
       "category": "GENERAL",
       "settings": {
           "school_name": "Springfield Elementary",
           "school_code": "SPFD-001",
           "academic_year": "2025-2026"
       }
   }
   ```
5. Verify all configurations in category included

**Success Criteria:**
- Grouped configurations returned as key-value map
- All configurations in category present
- Useful for bulk retrieval by frontend

---

## Database Testing

### [QA-020] Test Database Constraints
**Type:** Database Integrity Test

**Steps:**
1. **Test UNIQUE constraint on mobile:**
   - Insert student with mobile "9876543400" directly via SQL
   - Attempt to insert another student with same mobile
   - Verify database rejects with UNIQUE constraint violation
2. **Test CHECK constraint on age:**
   - Attempt to insert student with `date_of_birth = '2024-01-01'` (too young)
   - Verify database rejects with CHECK constraint violation
3. **Test NOT NULL constraints:**
   - Attempt to insert student without `first_name`
   - Verify database rejects
4. **Test UNIQUE constraint on aadhaar_number:**
   - Insert student with aadhaar "123456789012"
   - Attempt to insert another student with same aadhaar
   - Verify database rejects

**Success Criteria:**
- All database-level constraints enforced
- Appropriate error messages from PostgreSQL
- Application handles database errors gracefully

---

### [QA-021] Test Optimistic Locking at Database Level
**Type:** Database Integrity Test

**Steps:**
1. Insert student with version = 0
2. Execute UPDATE query with version check:
   ```sql
   UPDATE students SET first_name = 'Updated', version = version + 1, updated_at = NOW()
   WHERE student_id = 'STD-001' AND version = 0;
   ```
3. Verify 1 row affected
4. Execute same UPDATE query again (version still 0 in WHERE clause)
5. Verify 0 rows affected (version mismatch)

**Success Criteria:**
- First update succeeds
- Second update with stale version fails silently (0 rows affected)
- Application layer (JPA) throws OptimisticLockException

---

### [QA-022] Test Enrollment Unique Constraint
**Type:** Database Integrity Test

**Steps:**
1. Insert enrollment for student ID 1, academic year "2025-2026"
2. Attempt to insert another enrollment for same student ID and academic year
3. Verify database rejects with UNIQUE constraint violation on (student_id, academic_year)

**Success Criteria:**
- UNIQUE constraint enforced at database level
- Application catches exception and returns 409 Conflict

---

## Frontend UI Testing

### [QA-023] Test Student Registration Form (Happy Path)
**Type:** End-to-End Test

**Steps:**
1. Open browser and navigate to `http://localhost:3000`
2. Click "Manage Students" button
3. Click "Register New Student" button
4. Fill form with valid data:
   - First Name: "Emily"
   - Last Name: "Davis"
   - Date of Birth: Select "2015-03-20" from date picker
   - Mobile: "9876543500"
   - Email: "emily.d@example.com"
5. Click "Register Student" button
6. Verify toast notification: "Student registered successfully"
7. Verify dialog closes
8. Verify student appears in students table

**Success Criteria:**
- Form submits successfully
- Success toast displayed
- Student list refreshed with new student
- No console errors

---

### [QA-024] Test Student Registration Form (Validation Errors)
**Type:** End-to-End Test

**Steps:**
1. Open student registration dialog
2. **Test Case 1: Submit empty form**
   - Click "Register Student" without filling fields
   - Verify inline error messages appear for required fields:
     - "First name is required"
     - "Last name is required"
     - "Date of birth is required"
     - "Mobile is required"
3. **Test Case 2: Invalid mobile format**
   - Fill firstName: "John"
   - Fill lastName: "Doe"
   - Fill dateOfBirth: valid date
   - Fill mobile: "123" (invalid)
   - Click submit
   - Verify error message: "Mobile must be exactly 10 digits"
4. **Test Case 3: Invalid email format**
   - Fill email: "invalid-email"
   - Click submit
   - Verify error message: "Invalid email format"
5. **Test Case 4: Age out of range**
   - Fill dateOfBirth: "2000-01-01" (too old)
   - Click submit
   - Verify error message: "Student must be between 3 and 18 years old"

**Success Criteria:**
- Client-side validation (Zod) prevents form submission
- Field-level error messages displayed in red below inputs
- Form not submitted to API until all validation passes

---

### [QA-025] Test Student Registration (Duplicate Mobile)
**Type:** End-to-End Test

**Steps:**
1. Register student with mobile "9876543600"
2. Attempt to register another student with same mobile "9876543600"
3. Fill all other fields with valid data
4. Click submit
5. Verify error toast notification: "Mobile number already registered" or "Mobile number 9876543600 is already registered"
6. Verify form remains open (not closed)
7. User can correct mobile and retry

**Success Criteria:**
- API returns 409 Conflict
- Frontend displays user-friendly error message
- User can fix error and resubmit

---

### [QA-026] Test Search Students Functionality
**Type:** End-to-End Test

**Steps:**
1. Navigate to Students page (ensure 5+ students exist)
2. **Test Case 1: Search by last name**
   - Enter "Smith" in search box
   - Wait for search to execute (debounced)
   - Verify only students with lastName containing "Smith" displayed
3. **Test Case 2: Filter by status**
   - Select "INACTIVE" from status dropdown
   - Verify only inactive students displayed
4. **Test Case 3: Clear filters**
   - Clear search box and select "All" status
   - Verify all students displayed again

**Success Criteria:**
- Search filters work correctly
- Results update dynamically (debounced search)
- No page reload required

---

### [QA-027] Test Edit Student Functionality
**Type:** End-to-End Test

**Steps:**
1. Navigate to Students page
2. Click "Edit" icon for a student
3. Verify dialog opens with student data pre-populated
4. Modify allowed fields:
   - firstName: "ModifiedFirstName"
   - mobile: "9876543700" (different from original)
   - status: "INACTIVE"
5. Click "Update Student" button
6. Verify success toast: "Student updated successfully"
7. Verify dialog closes
8. Verify updated data displayed in students table

**Success Criteria:**
- Edit dialog pre-populates existing data
- Only allowed fields are editable (dateOfBirth grayed out)
- Update API call includes version number
- Optimistic locking handled (409 if version mismatch)

---

### [QA-028] Test Delete Student Functionality
**Type:** End-to-End Test

**Steps:**
1. Navigate to Students page
2. Click "Delete" icon for a student
3. Verify browser confirmation dialog: "Are you sure?"
4. Click "OK"
5. Verify success toast: "Student deleted successfully"
6. Verify student removed from table
7. **Test Case 2: Cancel deletion**
   - Click "Delete" icon for another student
   - Click "Cancel" in confirmation dialog
   - Verify student NOT deleted

**Success Criteria:**
- Confirmation dialog prevents accidental deletion
- Successful deletion removes student from UI and database
- Cancel works correctly

---

### [QA-029] Test Configuration Management Page
**Type:** End-to-End Test

**Steps:**
1. Navigate to Configurations page
2. Verify default configurations displayed (school_name, school_code, etc.)
3. **Test category filter:**
   - Select "GENERAL" from category dropdown
   - Verify only GENERAL configurations displayed
4. **Test create configuration:**
   - Click "Add Configuration" button
   - Fill form:
     - Category: ACADEMIC
     - Key: "test_setting"
     - Value: "test_value"
     - Data Type: STRING
   - Click submit
   - Verify success toast
   - Verify new configuration appears in table
5. **Test edit configuration:**
   - Click "Edit" for existing configuration
   - Modify value
   - Click submit
   - Verify success toast and updated value displayed
6. **Test delete configuration:**
   - Click "Delete" for a configuration
   - Confirm deletion
   - Verify success toast and configuration removed

**Success Criteria:**
- All CRUD operations work correctly
- Category filter functions properly
- Toast notifications displayed for all actions
- UI matches reference code 1:1

---

### [QA-030] Test Loading States
**Type:** End-to-End Test

**Steps:**
1. **Test students page loading:**
   - Navigate to Students page
   - Verify loading spinner displayed while fetching students
   - Verify spinner disappears when data loaded
2. **Test dialog loading:**
   - Open student registration dialog
   - Fill form and click submit
   - Verify submit button shows "Registering..." text (disabled)
   - Verify button returns to normal after API completes
3. **Test network delay simulation:**
   - Throttle network to "Slow 3G" in browser DevTools
   - Perform any API operation
   - Verify loading state displayed throughout delay

**Success Criteria:**
- Loading indicators shown during all async operations
- Buttons disabled during submission to prevent double-submit
- User feedback provided during network delays

---

### [QA-031] Test Error Handling in UI
**Type:** End-to-End Test

**Steps:**
1. **Test network error:**
   - Stop backend services (`docker-compose stop student-service`)
   - Attempt to load Students page
   - Verify error toast: "Failed to fetch students"
2. **Test 404 error:**
   - Manually navigate to `/api/v1/students/INVALID-ID`
   - Verify error toast: "Student not found"
3. **Test 500 error:**
   - Trigger server error (if possible, e.g., invalid SQL query)
   - Verify generic error toast: "An unexpected error occurred"
4. Restart services and verify recovery

**Success Criteria:**
- All API errors handled gracefully
- User-friendly error messages displayed
- Application doesn't crash on errors
- User can retry after error

---

## Performance Testing

### [QA-032] Test API Response Times
**Type:** Performance Test

**Steps:**
1. Use tool: JMeter, Gatling, or `wrk` (HTTP benchmarking tool)
2. **Load test: Create student endpoint**
   - Concurrent users: 100
   - Duration: 60 seconds
   - Requests: POST /api/v1/students with valid payloads
   - Measure p50, p95, p99 response times
3. **Load test: Get student endpoint**
   - Concurrent users: 100
   - Duration: 60 seconds
   - Requests: GET /api/v1/students/{randomStudentId}
   - Measure response times and cache hit ratio
4. **Load test: Search students endpoint**
   - Concurrent users: 50
   - Duration: 60 seconds
   - Requests: GET /api/v1/students?lastName={randomLastName}
   - Measure response times

**Success Criteria:**
- p95 response time <200ms for all endpoints
- p99 response time <500ms
- No 500 errors or timeouts
- Database connection pool not exhausted
- Cache hit ratio >80% for GET requests

---

### [QA-033] Test Cache Effectiveness
**Type:** Performance Test

**Steps:**
1. Restart services to clear cache
2. Send GET request to `/api/v1/students/STD-001` (first call, cache miss)
3. Measure response time (should query database)
4. Send same GET request 10 times (cache hits)
5. Measure response times (should be significantly faster)
6. Verify Redis metrics:
   - `redis-cli --stat` to monitor cache operations
   - Check cache keys: `redis-cli KEYS sms:student:*`
7. Update student via PUT request
8. Verify cache invalidated (cache key removed)
9. Send GET request again (cache miss, then refill)

**Success Criteria:**
- Cache hit response time <50ms
- Cache miss response time <200ms
- Cache invalidated on updates
- Cache TTL respected (entries expire after configured time)

---

### [QA-034] Test Database Connection Pool
**Type:** Performance Test

**Steps:**
1. Configure HikariCP connection pool: max 20 connections
2. Run load test with 100 concurrent users hitting database-heavy endpoints
3. Monitor Actuator metrics: `curl http://localhost:8081/actuator/metrics/hikaricp.connections.active`
4. Verify:
   - Active connections stay within pool limit (max 20)
   - No "Connection pool exhausted" errors in logs
   - Requests queue gracefully when pool saturated
5. After load test, verify connections returned to pool (idle connections ~5)

**Success Criteria:**
- Connection pool handles load without exhaustion
- Graceful queuing when pool saturated
- Connections released after use (no leaks)

---

## Security Testing

### [QA-035] Test Input Validation (SQL Injection Prevention)
**Type:** Security Test

**Steps:**
1. Attempt SQL injection via lastName search:
   - Send GET request to `/api/v1/students?lastName=Smith'; DROP TABLE students;--`
   - Verify API handles input safely (parameterized query)
   - Verify no database error or table dropped
2. Attempt SQL injection via POST request:
   - Send POST with firstName: `Robert'); DROP TABLE students;--`
   - Verify validation rejects (special characters not allowed)
3. Query database to confirm tables intact

**Success Criteria:**
- All inputs validated and sanitized
- Parameterized queries prevent SQL injection
- Database schema unchanged

---

### [QA-036] Test CORS Configuration
**Type:** Security Test

**Steps:**
1. **Test allowed origin:**
   - Send API request from frontend (http://localhost:3000)
   - Verify CORS headers present in response:
     - `Access-Control-Allow-Origin: http://localhost:3000`
     - `Access-Control-Allow-Credentials: true`
2. **Test disallowed origin:**
   - Send API request from unauthorized origin (e.g., http://evil.com)
   - Verify request blocked (no CORS headers)
3. **Test preflight request:**
   - Send OPTIONS request to API endpoint
   - Verify response includes allowed methods (GET, POST, PUT, DELETE)

**Success Criteria:**
- Frontend can make API calls without CORS errors
- Unauthorized origins blocked
- Credentials (cookies) supported

---

### [QA-037] Test CSRF Protection (If Implemented)
**Type:** Security Test

**Steps:**
1. If CSRF tokens used:
   - Send POST request without CSRF token
   - Verify request rejected (403 Forbidden)
2. Send POST request with valid CSRF token (from cookie)
3. Verify request succeeds
4. Verify CSRF token rotated after use

**Success Criteria:**
- State-changing requests require CSRF token
- Tokens validated correctly
- Tokens expire after use

---

## Accessibility Testing

### [QA-038] Test Keyboard Navigation
**Type:** Accessibility Test

**Steps:**
1. Navigate to Students page using only keyboard (Tab key)
2. Verify all interactive elements reachable:
   - Navigation links
   - Search input
   - Filter dropdowns
   - Register button
   - Edit/Delete icons in table
3. Open student dialog using Enter key on "Register" button
4. Navigate form fields using Tab
5. Submit form using Enter key
6. Verify focus indicators visible on all elements

**Success Criteria:**
- All interactive elements keyboard-accessible
- Focus indicators visible (blue outline or custom styling)
- Logical tab order (top to bottom, left to right)
- Dialogs trap focus (Esc key closes dialog)

---

### [QA-039] Test Screen Reader Compatibility
**Type:** Accessibility Test

**Steps:**
1. Use screen reader (NVDA on Windows, VoiceOver on macOS)
2. Navigate to Students page
3. Verify screen reader announces:
   - Page title: "Students"
   - Table headers: "Student ID", "Name", "Mobile", etc.
   - Table row data
   - Button labels: "Register New Student", "Edit", "Delete"
4. Open student dialog
5. Verify screen reader announces:
   - Dialog title: "Register New Student"
   - Form labels: "First Name", "Last Name", etc.
   - Required field indicators: "First Name (required)"
   - Error messages: "Mobile must be exactly 10 digits"

**Success Criteria:**
- All content accessible to screen readers
- ARIA labels present on all interactive elements
- Form errors announced to screen reader users
- Semantic HTML used (e.g., `<button>` not `<div onclick>`)

---

### [QA-040] Test Color Contrast
**Type:** Accessibility Test

**Steps:**
1. Use tool: axe DevTools browser extension or WAVE
2. Run accessibility audit on:
   - Home page
   - Students page
   - Configuration page
   - Student dialog
3. Verify no color contrast issues:
   - Text-to-background ratios meet WCAG AA standards (4.5:1 for normal text, 3:1 for large text)
   - Button text readable
   - Error messages have sufficient contrast
4. Test with high contrast mode enabled (OS accessibility setting)

**Success Criteria:**
- All color contrast ratios meet WCAG 2.1 AA standards
- Lighthouse accessibility score >90
- axe DevTools reports no critical issues

---

## Cross-Browser Compatibility Testing

### [QA-041] Test on Google Chrome
**Type:** Compatibility Test

**Steps:**
1. Open application in Chrome (latest version)
2. Test all critical user journeys:
   - Register student
   - Search students
   - Edit student
   - Delete student
   - Manage configurations
3. Verify no console errors
4. Verify UI renders correctly (layout, fonts, colors)
5. Test responsive design (resize window to mobile, tablet, desktop sizes)

**Success Criteria:**
- All features functional in Chrome
- No layout issues
- No JavaScript errors

---

### [QA-042] Test on Mozilla Firefox
**Type:** Compatibility Test

**Steps:**
1. Repeat all test steps from QA-041 in Firefox (latest version)
2. Pay attention to:
   - Date picker rendering (browser-native differences)
   - Form validation messages
   - CSS Grid/Flexbox layouts

**Success Criteria:**
- All features functional in Firefox
- UI consistent with Chrome

---

### [QA-043] Test on Safari (macOS/iOS)
**Type:** Compatibility Test

**Steps:**
1. If available, test on Safari (latest version)
2. Repeat all test steps from QA-041
3. Pay attention to:
   - CSS compatibility (Safari lags behind Chrome/Firefox)
   - JavaScript ES6+ features

**Success Criteria:**
- All features functional in Safari
- No critical layout issues

---

### [QA-044] Test on Microsoft Edge
**Type:** Compatibility Test

**Steps:**
1. Test on Edge (Chromium-based, latest version)
2. Repeat all test steps from QA-041
3. Verify no Edge-specific issues

**Success Criteria:**
- All features functional in Edge
- UI consistent with other Chromium browsers

---

## Regression Testing

### [QA-045] Full Regression Suite
**Type:** Regression Test

**Steps:**
1. After any bug fix or enhancement, re-run all critical test cases:
   - QA-002 (Student Registration Happy Path)
   - QA-003 (Age Validation)
   - QA-004 (Duplicate Mobile)
   - QA-007 (Search Students)
   - QA-008 (Update Student)
   - QA-011 (Create Enrollment)
   - QA-023 (Frontend Registration Form)
   - QA-029 (Configuration Management)
2. Verify no existing functionality broken
3. Document any new bugs found

**Success Criteria:**
- All existing tests pass
- No regressions introduced
- New functionality works as expected

---

## Final Acceptance Testing

### [QA-046] End-to-End User Journey (Complete Workflow)
**Type:** Acceptance Test

**Steps:**
1. **Scenario: Onboard new student and track enrollment**
   - **Step 1:** Register student "Sarah Johnson" with all required details
   - **Step 2:** Verify student appears in students list with ACTIVE status
   - **Step 3:** Search for student by last name "Johnson"
   - **Step 4:** Create enrollment for Sarah in Grade-6, Section-A for 2025-2026
   - **Step 5:** View student details and verify enrollment history displays
   - **Step 6:** Update student mobile number
   - **Step 7:** Change student status to INACTIVE
   - **Step 8:** Verify updated details reflected in database
2. **Scenario: Configure school settings**
   - **Step 1:** Navigate to Configurations page
   - **Step 2:** Update GENERAL > school_name to "New School Name"
   - **Step 3:** Create FINANCIAL > admission_fee = "2000"
   - **Step 4:** Delete temporary configuration
   - **Step 5:** Verify grouped configurations API returns updated settings

**Success Criteria:**
- All steps complete without errors
- Data persisted correctly in database
- User experience smooth and intuitive
- Performance acceptable (no noticeable delays)

---

### [QA-047] Documentation Review
**Type:** Documentation Test

**Steps:**
1. Review README.md in project root
2. Verify setup instructions accurate:
   - Prerequisites listed correctly
   - Docker Compose commands work
   - Environment variables documented
3. Test setup from scratch using README:
   - Clone repository
   - Run `docker-compose up -d`
   - Verify all services start successfully
   - Access frontend at http://localhost:3000
   - Run sample API calls using provided examples
4. Verify troubleshooting section helpful

**Success Criteria:**
- New developer can set up project using README alone
- All commands verified and working
- Documentation clear and comprehensive

---

### [QA-048] Production Readiness Checklist
**Type:** Acceptance Test

**Steps:**
1. Verify all acceptance criteria met:
   - [ ] All backend unit tests pass (Domain: 95%, Service: 85%)
   - [ ] All backend integration tests pass (Infrastructure: 70%)
   - [ ] All frontend tests pass
   - [ ] API endpoints match OpenAPI specification
   - [ ] p95 response time <200ms
   - [ ] Cache hit ratio >80%
   - [ ] Lighthouse Performance score >90
   - [ ] Lighthouse Accessibility score >90
   - [ ] Docker Compose starts all services successfully
   - [ ] No critical security vulnerabilities
   - [ ] CORS configured correctly
   - [ ] Health endpoints return UP status
   - [ ] Structured logging outputs JSON format
   - [ ] Database schema matches DDL script
   - [ ] No console errors in browser
   - [ ] Cross-browser compatibility verified
2. Sign off on production readiness

**Success Criteria:**
- All checklist items marked complete
- Stakeholders approve for production deployment

---

## Test Summary Report Template

After completing all tests, generate a summary report:

```
# QA Test Summary Report

**Project:** School Management System
**Date:** YYYY-MM-DD
**Tested By:** [QA Engineer Name]

## Test Execution Summary
- Total Test Cases: 48
- Passed: X
- Failed: Y
- Blocked: Z
- Pass Rate: (X/48) * 100%

## Critical Issues Found
1. [Issue #1 - Severity: High/Medium/Low]
2. [Issue #2 - Severity: High/Medium/Low]

## Performance Metrics
- API Response Time (p95): X ms
- Cache Hit Ratio: X%
- Lighthouse Performance Score: X
- Lighthouse Accessibility Score: X

## Browser Compatibility
- Chrome: Pass/Fail
- Firefox: Pass/Fail
- Safari: Pass/Fail
- Edge: Pass/Fail

## Recommendations
1. [Recommendation 1]
2. [Recommendation 2]

## Sign-Off
- Backend Ready for Production: Yes/No
- Frontend Ready for Production: Yes/No
- Overall Approval: Yes/No
```

---

**Document Version:** 1.0.0
**Last Updated:** 2026-01-28
**Next Review:** Post-Testing
