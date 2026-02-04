# QA Test Plan - School Management System

## Overview
Comprehensive test checklist for QA Engineer to verify Student Service, Configuration Service, and Frontend application functionality, performance, and integration.

**Execution Model:** Waterfall / Single-Pass Implementation
**Test Pyramid:** Unit (60%) → Integration (30%) → E2E (10%)
**Source of Truth:** `@specs/REQUIREMENTS.md`, `@specs/TESTING_STRATEGY.md`, `@specs/sms_api_specification.yaml`

---

## Phase 1: Backend Unit Testing (Student Service)

### [QA-001] Student Domain Model Unit Tests
**Type:** Unit Test
**Component:** Student.java domain model

**Test Scenarios:**
1. **Student Registration with Valid Data:**
   - GIVEN: firstName="John", lastName="Doe", dateOfBirth=2015-05-15, mobile="9876543210"
   - WHEN: Student.register() called
   - THEN: Student created with status ACTIVE, age=10 years

2. **Student Registration with Invalid Age (< 3 years):**
   - GIVEN: dateOfBirth=2023-01-01 (age 3 < 3 years)
   - WHEN: Student.register() called
   - THEN: IllegalArgumentException thrown with message "Age must be between 3 and 18"

3. **Student Registration with Invalid Age (> 18 years):**
   - GIVEN: dateOfBirth=2000-01-01 (age > 18)
   - WHEN: Student.register() called
   - THEN: IllegalArgumentException thrown

4. **Update Profile on Active Student:**
   - GIVEN: Active student
   - WHEN: student.updateProfile("Jane", "Smith", Mobile.of("9876543211"))
   - THEN: firstName="Jane", lastName="Smith", mobile="9876543211"

5. **Update Profile on Inactive Student:**
   - GIVEN: Student with status INACTIVE
   - WHEN: student.updateProfile() called
   - THEN: IllegalStateException thrown with message "Cannot update inactive student"

**Success Criteria:**
- [ ] All 5 test scenarios pass
- [ ] Tests use JUnit 5 and AssertJ
- [ ] Test coverage ≥ 95% for Student domain model
- [ ] Edge cases covered (age boundaries: 2, 3, 18, 19)

**Dependencies:** BE-004

---

### [QA-002] Mobile Value Object Validation Tests
**Type:** Unit Test
**Component:** Mobile.java value object

**Test Scenarios:**
1. **Valid Mobile Number:**
   - GIVEN: number="9876543210"
   - WHEN: Mobile.of("9876543210")
   - THEN: Mobile object created, number="9876543210"

2. **Invalid Mobile (Less than 10 digits):**
   - GIVEN: number="987654321"
   - WHEN: Mobile.of("987654321")
   - THEN: IllegalArgumentException thrown

3. **Invalid Mobile (More than 10 digits):**
   - GIVEN: number="98765432109"
   - WHEN: Mobile.of("98765432109")
   - THEN: IllegalArgumentException thrown

4. **Invalid Mobile (Contains Letters):**
   - GIVEN: number="987ABC3210"
   - WHEN: Mobile.of("987ABC3210")
   - THEN: IllegalArgumentException thrown

5. **Masked Mobile Number:**
   - GIVEN: mobile=Mobile.of("9876543210")
   - WHEN: mobile.getMasked()
   - THEN: Returns "987****210"

**Success Criteria:**
- [ ] All 5 test scenarios pass
- [ ] Immutability verified (no setters)
- [ ] Value equality works (equals/hashCode)

**Dependencies:** BE-004

---

### [QA-003] Drools Age Validation Rules Tests
**Type:** Unit Test
**Component:** student-age-rules.drl

**Test Scenarios:**
1. **Valid Age (10 years):**
   - GIVEN: Student with dateOfBirth=2016-01-01
   - WHEN: Drools rules executed
   - THEN: ValidationResult.isValid() == true

2. **Age Too Young (2 years):**
   - GIVEN: Student with dateOfBirth=2024-01-01
   - WHEN: Drools rules executed
   - THEN: ValidationResult contains error with code "AGE_TOO_YOUNG"

3. **Age Too Old (19 years):**
   - GIVEN: Student with dateOfBirth=2007-01-01
   - WHEN: Drools rules executed
   - THEN: ValidationResult contains error with code "AGE_TOO_OLD"

4. **Future Date of Birth:**
   - GIVEN: Student with dateOfBirth=2027-01-01
   - WHEN: Drools rules executed
   - THEN: ValidationResult contains error with code "INVALID_DATE_OF_BIRTH"

5. **Boundary Age (Exactly 3 years):**
   - GIVEN: Student with dateOfBirth=(current date - 3 years)
   - WHEN: Drools rules executed
   - THEN: ValidationResult.isValid() == true

6. **Boundary Age (Exactly 18 years):**
   - GIVEN: Student with dateOfBirth=(current date - 18 years)
   - WHEN: Drools rules executed
   - THEN: ValidationResult.isValid() == true

**Success Criteria:**
- [ ] All 6 test scenarios pass
- [ ] Drools session created from KieContainer
- [ ] Rule salience priorities correct (110 > 100)
- [ ] Error codes match specification

**Dependencies:** BE-009

---

### [QA-004] Drools Mobile Uniqueness Rules Tests
**Type:** Unit Test (with mocked repository)
**Component:** student-uniqueness-rules.drl

**Test Scenarios:**
1. **Unique Mobile Number:**
   - GIVEN: Mobile="9876543210", repository.existsByMobile() returns false
   - WHEN: Drools rules executed
   - THEN: ValidationResult.isValid() == true

2. **Duplicate Mobile Number:**
   - GIVEN: Mobile="9876543210", repository.existsByMobile() returns true
   - WHEN: Drools rules executed
   - THEN: ValidationResult contains error with code "DUPLICATE_MOBILE"

3. **Invalid Mobile Format:**
   - GIVEN: Mobile="987654321" (9 digits)
   - WHEN: Drools rules executed
   - THEN: ValidationResult contains error with code "INVALID_MOBILE_FORMAT"

4. **Update with Same Mobile (Not Duplicate):**
   - GIVEN: Existing student ID=1, mobile="9876543210", repository.existsByMobileAndIdNot("9876543210", 1) returns false
   - WHEN: Drools rules executed
   - THEN: ValidationResult.isValid() == true

**Success Criteria:**
- [ ] All 4 test scenarios pass
- [ ] Repository global injected correctly
- [ ] Salience priorities correct (95 > 90)

**Dependencies:** BE-009

---

### [QA-005] StudentService Unit Tests (Mocked Dependencies)
**Type:** Unit Test
**Component:** StudentService.java

**Test Scenarios:**
1. **Register Student with Valid Data:**
   - GIVEN: Valid StudentRequestDTO, Drools validation passes
   - WHEN: studentService.registerStudent(dto)
   - THEN: Repository.save() called, StudentResponseDTO returned with studentId

2. **Register Student with Validation Failure:**
   - GIVEN: Invalid StudentRequestDTO, Drools validation fails
   - WHEN: studentService.registerStudent(dto)
   - THEN: ValidationException thrown with error list

3. **Update Student with Version Mismatch (Optimistic Locking):**
   - GIVEN: Existing student version=1, update request version=0
   - WHEN: studentService.updateStudent(studentId, dto)
   - THEN: OptimisticLockException thrown

4. **Get Student by ID (Not Found):**
   - GIVEN: studentId="STD-20260203-0001", repository returns empty
   - WHEN: studentService.getStudentById(studentId)
   - THEN: StudentNotFoundException thrown

5. **Delete Student:**
   - GIVEN: Existing student with studentId="STD-20260203-0001"
   - WHEN: studentService.deleteStudent(studentId)
   - THEN: Repository.deleteById() called

**Success Criteria:**
- [ ] All 5 test scenarios pass
- [ ] Mockito used for mocking dependencies
- [ ] Mapper methods verified (toDomain, toResponseDTO)
- [ ] Transaction boundaries not tested (unit test)

**Dependencies:** BE-012

---

## Phase 2: Backend Integration Testing (Student Service)

### [QA-006] Student Registration API Integration Test
**Type:** Integration Test
**Component:** StudentController + StudentService + Database

**Setup:**
- Testcontainers PostgreSQL 18
- Spring Boot test context
- Clean database state before each test

**Test Scenarios:**
1. **POST /api/v1/students - Valid Student:**
   - GIVEN: Valid StudentRequestDTO JSON body
   - WHEN: POST to /api/v1/students
   - THEN:
     - Response: 201 Created
     - Location header: /api/v1/students/{studentId}
     - Body contains studentId (format: STD-YYYYMMDD-NNNN)
     - Database contains new student record

2. **POST /api/v1/students - Age Validation Failure (< 3 years):**
   - GIVEN: StudentRequestDTO with dateOfBirth=2024-01-01
   - WHEN: POST to /api/v1/students
   - THEN:
     - Response: 400 Bad Request
     - Body contains ErrorResponse with error code "AGE_TOO_YOUNG"
     - Database unchanged

3. **POST /api/v1/students - Duplicate Mobile Number:**
   - GIVEN: Existing student with mobile="9876543210"
   - WHEN: POST new student with same mobile
   - THEN:
     - Response: 400 Bad Request
     - Error code: "DUPLICATE_MOBILE"
     - Database unchanged

4. **POST /api/v1/students - Missing Required Field:**
   - GIVEN: StudentRequestDTO without firstName
   - WHEN: POST to /api/v1/students
   - THEN:
     - Response: 400 Bad Request
     - Error list contains field="firstName", message="First name is required"

5. **POST /api/v1/students - Missing Guardian Name:**
   - GIVEN: StudentRequestDTO without fathersName and mothersName
   - WHEN: POST to /api/v1/students
   - THEN:
     - Response: 400 Bad Request
     - Error code: "GUARDIAN_REQUIRED"

**Success Criteria:**
- [ ] All 5 test scenarios pass
- [ ] Database state verified after each test
- [ ] Correlation ID present in response headers
- [ ] Testcontainers PostgreSQL used
- [ ] Tests run with `mvn verify`

**Dependencies:** BE-014, BE-015

---

### [QA-007] Student Retrieval API Integration Test
**Type:** Integration Test
**Component:** GET /api/v1/students/{studentId}

**Test Scenarios:**
1. **GET Student by Valid ID:**
   - GIVEN: Existing student with studentId="STD-20260203-0001"
   - WHEN: GET /api/v1/students/STD-20260203-0001
   - THEN:
     - Response: 200 OK
     - Body contains student details (firstName, lastName, mobile, etc.)

2. **GET Student by Invalid ID:**
   - GIVEN: No student with studentId="STD-99999999-9999"
   - WHEN: GET /api/v1/students/STD-99999999-9999
   - THEN:
     - Response: 404 Not Found
     - ErrorResponse with detail message

3. **GET Student with Enrollment History:**
   - GIVEN: Student with 2 enrollments
   - WHEN: GET /api/v1/students/{studentId}
   - THEN:
     - Response includes enrollment list (no N+1 query)
     - Query count verified with @EntityGraph

**Success Criteria:**
- [ ] All 3 test scenarios pass
- [ ] N+1 query prevention verified (log SQL queries)
- [ ] Response matches OpenAPI schema

**Dependencies:** BE-014

---

### [QA-008] Student Update API Integration Test
**Type:** Integration Test
**Component:** PUT /api/v1/students/{studentId}

**Test Scenarios:**
1. **PUT Valid Update:**
   - GIVEN: Existing student with version=0
   - WHEN: PUT /api/v1/students/{studentId} with updated name and version=0
   - THEN:
     - Response: 200 OK
     - Body contains updated data
     - Database version incremented to 1

2. **PUT with Version Mismatch (Optimistic Locking):**
   - GIVEN: Student with version=1
   - WHEN: PUT with version=0 (stale version)
   - THEN:
     - Response: 409 Conflict
     - ErrorResponse with detail about version mismatch

3. **PUT with Invalid Mobile (Duplicate):**
   - GIVEN: Two students, attempt to update student1 mobile to student2's mobile
   - WHEN: PUT /api/v1/students/{student1Id}
   - THEN:
     - Response: 400 Bad Request
     - Error code: "DUPLICATE_MOBILE"

**Success Criteria:**
- [ ] All 3 test scenarios pass
- [ ] Optimistic locking enforced
- [ ] Only firstName, lastName, mobile, status editable
- [ ] Database updated correctly

**Dependencies:** BE-014

---

### [QA-009] Student Search and Pagination Integration Test
**Type:** Integration Test
**Component:** GET /api/v1/students (with query params)

**Test Scenarios:**
1. **Search by Last Name:**
   - GIVEN: 5 students with lastName containing "Smith"
   - WHEN: GET /api/v1/students?lastName=Smith
   - THEN:
     - Response: 200 OK
     - Content array contains 5 students
     - All have lastName containing "Smith"

2. **Filter by Status:**
   - GIVEN: 3 ACTIVE students, 2 INACTIVE students
   - WHEN: GET /api/v1/students?status=ACTIVE
   - THEN:
     - Response contains only 3 ACTIVE students

3. **Pagination:**
   - GIVEN: 25 students total
   - WHEN: GET /api/v1/students?page=0&size=20
   - THEN:
     - Content array has 20 students
     - pageable.totalElements=25
     - pageable.totalPages=2

4. **Pagination - Second Page:**
   - GIVEN: 25 students total
   - WHEN: GET /api/v1/students?page=1&size=20
   - THEN:
     - Content array has 5 students
     - pageable.page=1

5. **Sorting:**
   - GIVEN: Multiple students
   - WHEN: GET /api/v1/students?sortBy=lastName&sortDirection=ASC
   - THEN:
     - Students sorted by lastName alphabetically

**Success Criteria:**
- [ ] All 5 test scenarios pass
- [ ] Search is case-insensitive
- [ ] Pagination metadata correct
- [ ] Sorting works for all allowed fields

**Dependencies:** BE-014

---

### [QA-010] Student Deletion API Integration Test
**Type:** Integration Test
**Component:** DELETE /api/v1/students/{studentId}

**Test Scenarios:**
1. **DELETE Existing Student:**
   - GIVEN: Existing student with studentId="STD-20260203-0001"
   - WHEN: DELETE /api/v1/students/STD-20260203-0001
   - THEN:
     - Response: 204 No Content
     - Database no longer contains student
     - Enrollments cascade deleted (if configured)

2. **DELETE Non-Existent Student:**
   - GIVEN: No student with studentId="STD-99999999-9999"
   - WHEN: DELETE /api/v1/students/STD-99999999-9999
   - THEN:
     - Response: 404 Not Found

**Success Criteria:**
- [ ] Both test scenarios pass
- [ ] Cascade delete verified for enrollments
- [ ] Idempotent deletion (second DELETE returns 404)

**Dependencies:** BE-014

---

## Phase 3: Backend Integration Testing (Configuration Service)

### [QA-011] Configuration Grouped Retrieval Integration Test
**Type:** Integration Test
**Component:** GET /api/v1/configurations/grouped/{category}

**Test Scenarios:**
1. **GET Grouped GENERAL Settings:**
   - GIVEN: 5 configurations with category=GENERAL
   - WHEN: GET /api/v1/configurations/grouped/GENERAL
   - THEN:
     - Response: 200 OK
     - Body is Map<String, String> with 5 entries
     - Example: {"school_name": "Springfield Elementary", "school_code": "SPR-ELEM-001"}

2. **GET Grouped ACADEMIC Settings:**
   - GIVEN: 6 configurations with category=ACADEMIC
   - WHEN: GET /api/v1/configurations/grouped/ACADEMIC
   - THEN:
     - Response contains all academic settings
     - Values are strings (even numbers: "3", "18")

3. **Cache Hit Test:**
   - GIVEN: First request to /configurations/grouped/GENERAL
   - WHEN: Second request within 5 minutes
   - THEN:
     - Response from Redis cache (verify no DB query)
     - Cache-Control header present

4. **Cache Invalidation on Update:**
   - GIVEN: Cached GENERAL settings
   - WHEN: PUT /api/v1/configurations/{id} to update a GENERAL setting
   - THEN:
     - Cache invalidated
     - Next GET fetches from database

**Success Criteria:**
- [ ] All 4 test scenarios pass
- [ ] Cache hit verified (check Redis)
- [ ] Cache invalidation works
- [ ] Response format matches Map<String, String>

**Dependencies:** BE-027, BE-018

---

### [QA-012] Configuration CRUD Integration Test
**Type:** Integration Test
**Component:** Configuration Service CRUD endpoints

**Test Scenarios:**
1. **POST /api/v1/configurations - Create:**
   - GIVEN: Valid ConfigurationRequestDTO
   - WHEN: POST to /api/v1/configurations
   - THEN:
     - Response: 201 Created
     - Database contains new configuration

2. **PUT /api/v1/configurations/{id} - Update:**
   - GIVEN: Existing configuration
   - WHEN: PUT with updated value
   - THEN:
     - Response: 200 OK
     - Database updated
     - Cache invalidated for category

3. **DELETE /api/v1/configurations/{id}:**
   - GIVEN: Existing configuration
   - WHEN: DELETE /api/v1/configurations/{id}
   - THEN:
     - Response: 204 No Content
     - Database record removed

4. **Unique Constraint Test (Duplicate category+key):**
   - GIVEN: Existing configuration with category=GENERAL, key=school_name
   - WHEN: POST new configuration with same category+key
   - THEN:
     - Response: 409 Conflict
     - Database unchanged

**Success Criteria:**
- [ ] All 4 test scenarios pass
- [ ] Optimistic locking enforced on updates
- [ ] Unique constraint on (category, key) enforced

**Dependencies:** BE-027

---

## Phase 4: Frontend Unit Testing

### [QA-013] Zod Schema Validation Tests
**Type:** Unit Test (Frontend)
**Component:** studentRegistrationSchema

**Test Scenarios:**
1. **Valid Student Data:**
   - GIVEN: Complete valid student data
   - WHEN: studentRegistrationSchema.parse(data)
   - THEN: Validation passes, no errors

2. **Invalid First Name (Too Short):**
   - GIVEN: firstName="J" (1 character)
   - WHEN: Schema validation
   - THEN: Error: "First name must be at least 2 characters"

3. **Invalid Mobile (9 digits):**
   - GIVEN: mobile="987654321"
   - WHEN: Schema validation
   - THEN: Error: "Mobile number must be exactly 10 digits"

4. **Invalid Age (< 3 years):**
   - GIVEN: dateOfBirth=2024-01-01
   - WHEN: Schema validation
   - THEN: Error: "Student age must be between 3 and 18 years"

5. **Missing Guardian Name:**
   - GIVEN: No fathersName and no mothersName
   - WHEN: Schema validation
   - THEN: Error: "At least one guardian name is required"

6. **Invalid Aadhaar (11 digits):**
   - GIVEN: aadhaarNumber="12345678901"
   - WHEN: Schema validation
   - THEN: Error: "Aadhaar number must be exactly 12 digits"

**Success Criteria:**
- [ ] All 6 test scenarios pass
- [ ] Error messages match backend validation
- [ ] Tests use Vitest or Jest

**Dependencies:** FE-011

---

### [QA-014] useStudents Hook Tests
**Type:** Unit Test (Frontend)
**Component:** useStudents.ts React Query hook

**Test Scenarios:**
1. **Fetch Students Success:**
   - GIVEN: Mock studentService.getAllStudents() returns students
   - WHEN: useStudents() hook called
   - THEN: data contains students, isLoading=false

2. **Fetch Students Error:**
   - GIVEN: Mock studentService.getAllStudents() throws error
   - WHEN: useStudents() hook called
   - THEN: error set, isLoading=false

3. **Create Student Mutation:**
   - GIVEN: Mock studentService.createStudent() succeeds
   - WHEN: createMutation.mutate(studentData)
   - THEN: onSuccess callback triggered, cache invalidated

4. **Create Student with Validation Error:**
   - GIVEN: Mock service returns 400 error
   - WHEN: createMutation.mutate(invalidData)
   - THEN: onError callback triggered, toast error shown

**Success Criteria:**
- [ ] All 4 test scenarios pass
- [ ] React Query testing library used
- [ ] Mock service layer
- [ ] Cache invalidation verified

**Dependencies:** FE-012

---

## Phase 5: End-to-End Testing

### [QA-015] Student Registration E2E Flow
**Type:** E2E Test
**Environment:** Full stack (Backend + Frontend + Database)

**Setup:**
- Backend services running (Docker Compose)
- Frontend running (http://localhost:3000)
- Clean database state

**Test Steps:**
1. Navigate to http://localhost:3000/students
2. Click "Register New Student" button
3. Fill form:
   - First Name: "John"
   - Last Name: "Doe"
   - Date of Birth: "2015-05-15" (age 10)
   - Mobile: "9876543210"
   - Email: "john.doe@example.com"
   - Father's Name: "Guardian Doe"
   - Mother's Name: "Jane Doe"
   - Address: "123 Main St"
   - Aadhaar: "123456789012"
4. Click "Register Student"

**Expected Results:**
- [ ] Success toast notification appears
- [ ] Dialog closes automatically
- [ ] New student appears in student list
- [ ] Student ID format: STD-YYYYMMDD-NNNN
- [ ] Status badge shows "ACTIVE"
- [ ] Database record created

**Dependencies:** BE-029, FE-020

---

### [QA-016] Student Registration Validation E2E
**Type:** E2E Test
**Component:** Form validation with backend errors

**Test Steps:**
1. Navigate to student registration form
2. Fill form with invalid age (dateOfBirth=2024-01-01)
3. Click "Register Student"

**Expected Results:**
- [ ] Zod validation triggers before API call
- [ ] Error message appears below date field
- [ ] Form not submitted (no API call)

**Test Steps (Backend Validation):**
1. Bypass frontend validation (direct API call)
2. POST to /api/v1/students with age < 3

**Expected Results:**
- [ ] API returns 400 Bad Request
- [ ] Toast error notification shows backend error
- [ ] Error code "AGE_TOO_YOUNG" in response

**Dependencies:** BE-029, FE-020

---

### [QA-017] Student Search and Filter E2E
**Type:** E2E Test
**Component:** Search functionality

**Test Steps:**
1. Navigate to /students
2. Seed database with 30 students (20 ACTIVE, 10 INACTIVE)
3. Enter "Smith" in Last Name search box
4. Wait for debounce (500ms)

**Expected Results:**
- [ ] Loading spinner appears during API call
- [ ] Results filtered to students with lastName containing "Smith"
- [ ] Search case-insensitive
- [ ] No page refresh (React Query cache)

**Test Steps (Status Filter):**
1. Select "Active" from status dropdown

**Expected Results:**
- [ ] Results show only ACTIVE students
- [ ] Pagination updates if needed

**Test Steps (Pagination):**
1. Remove filters to show all 30 students
2. Verify first page shows 20 students
3. Click "Next" button

**Expected Results:**
- [ ] Second page shows remaining 10 students
- [ ] Page indicator shows "Page 2 of 2"
- [ ] Previous button enabled

**Dependencies:** BE-029, FE-020

---

### [QA-018] Student Update with Optimistic Locking E2E
**Type:** E2E Test
**Component:** Concurrent update detection

**Test Steps:**
1. Open student in two browser tabs
2. Tab 1: Edit student, change firstName to "Jane", click Save
3. Tab 2: Edit same student, change lastName to "Smith", click Save

**Expected Results:**
- [ ] Tab 1 update succeeds (200 OK)
- [ ] Tab 2 update fails (409 Conflict)
- [ ] Toast error in Tab 2: "Resource was modified by another user"
- [ ] Tab 2 student details refresh to show Tab 1 changes

**Dependencies:** BE-029, FE-020

---

### [QA-019] Student Deletion E2E
**Type:** E2E Test
**Component:** Delete operation

**Test Steps:**
1. Navigate to /students
2. Find student "John Doe"
3. Click "Delete" button
4. Confirm in confirmation dialog

**Expected Results:**
- [ ] Confirmation dialog appears
- [ ] After confirm: DELETE API call
- [ ] Success toast notification
- [ ] Student removed from list
- [ ] Database record deleted

**Test Steps (Cancel Deletion):**
1. Click "Delete" button
2. Click "Cancel" in confirmation dialog

**Expected Results:**
- [ ] Dialog closes
- [ ] No API call made
- [ ] Student remains in list

**Dependencies:** BE-029, FE-020

---

### [QA-020] Configuration Management E2E
**Type:** E2E Test
**Component:** Configuration CRUD

**Test Steps:**
1. Navigate to /configurations
2. Verify grouped settings display correctly:
   - GENERAL category shows school_name, school_code, etc.
   - ACADEMIC category shows min_age, max_age, etc.
3. Click "Add Configuration"
4. Fill form:
   - Category: GENERAL
   - Key: new_setting
   - Value: test_value
   - Data Type: STRING
5. Click Save

**Expected Results:**
- [ ] Success toast notification
- [ ] New configuration appears in GENERAL group
- [ ] Cache invalidated (verify next GET fetches from DB)

**Test Steps (Update Configuration):**
1. Click Edit on existing configuration
2. Change value
3. Click Save

**Expected Results:**
- [ ] Configuration updated
- [ ] Cache invalidated for category
- [ ] UI reflects new value

**Dependencies:** BE-029, FE-020

---

## Phase 6: Performance Testing

### [QA-021] API Response Time Performance Test
**Type:** Performance Test
**Tool:** JMeter or k6

**Test Scenarios:**
1. **Student List Endpoint:**
   - Concurrent Users: 100
   - Duration: 5 minutes
   - Endpoint: GET /api/v1/students?page=0&size=20

   **Success Criteria:**
   - [ ] P95 response time < 200ms
   - [ ] P99 response time < 500ms
   - [ ] Throughput > 500 req/sec
   - [ ] Error rate < 1%

2. **Student Registration Endpoint:**
   - Concurrent Users: 50
   - Duration: 5 minutes
   - Endpoint: POST /api/v1/students

   **Success Criteria:**
   - [ ] P95 response time < 300ms (includes Drools validation)
   - [ ] Database connection pool not exhausted
   - [ ] No duplicate mobile errors (concurrent inserts)

3. **Configuration Grouped Settings (Cached):**
   - Concurrent Users: 200
   - Duration: 5 minutes
   - Endpoint: GET /api/v1/configurations/grouped/GENERAL

   **Success Criteria:**
   - [ ] P95 response time < 50ms (cache hit)
   - [ ] Cache hit ratio > 80%
   - [ ] Redis handles load without errors

**Dependencies:** BE-029

---

### [QA-022] Frontend Performance Test
**Type:** Performance Test
**Tool:** Lighthouse

**Test Scenarios:**
1. **Students Page Load:**
   - Navigate to /students
   - Run Lighthouse audit

   **Success Criteria:**
   - [ ] Performance score > 90
   - [ ] First Contentful Paint < 1.5s
   - [ ] Largest Contentful Paint < 2.5s
   - [ ] Total Blocking Time < 300ms
   - [ ] Cumulative Layout Shift < 0.1

2. **Bundle Size:**
   - Run production build: npm run build

   **Success Criteria:**
   - [ ] Total bundle size < 500KB gzipped
   - [ ] Vendor chunk separated
   - [ ] Code splitting for routes

**Dependencies:** FE-024

---

### [QA-023] Database Query Performance Test
**Type:** Performance Test
**Component:** Database queries

**Test Scenarios:**
1. **Search Query with Pagination:**
   - Database: 10,000 student records
   - Query: SELECT * FROM students WHERE last_name ILIKE 'Smith%' AND status='ACTIVE' ORDER BY created_at DESC LIMIT 20 OFFSET 0

   **Success Criteria:**
   - [ ] Query execution time < 50ms
   - [ ] Index used (verify EXPLAIN ANALYZE)
   - [ ] Composite index on (last_name, status, created_at) utilized

2. **N+1 Query Prevention:**
   - Fetch student with enrollments
   - Monitor SQL queries in logs

   **Success Criteria:**
   - [ ] Only 1 query executed (not N+1)
   - [ ] @EntityGraph prevents multiple queries

**Dependencies:** BE-003, BE-007

---

## Phase 7: Security Testing

### [QA-024] SQL Injection Prevention Test
**Type:** Security Test
**Component:** API endpoints

**Test Scenarios:**
1. **Search with SQL Injection Attempt:**
   - GIVEN: GET /api/v1/students?lastName=Smith' OR '1'='1
   - WHEN: API call executed
   - THEN:
     - [ ] No SQL error
     - [ ] Parameterized query prevents injection
     - [ ] Returns only students with lastName containing "Smith' OR '1'='1" (literal match)

2. **Body with SQL Injection:**
   - GIVEN: POST /api/v1/students with firstName="John'; DROP TABLE students;--"
   - WHEN: API call executed
   - THEN:
     - [ ] Student created with firstName exactly as provided (no SQL execution)
     - [ ] Database intact

**Dependencies:** BE-014

---

### [QA-025] Input Validation Security Test
**Type:** Security Test
**Component:** Bean Validation

**Test Scenarios:**
1. **XSS Attempt in Student Name:**
   - GIVEN: POST with firstName="<script>alert('XSS')</script>"
   - WHEN: API call executed
   - THEN:
     - [ ] Validation fails (regex pattern check)
     - [ ] 400 Bad Request
     - [ ] Script tag not stored in database

2. **Oversized Input:**
   - GIVEN: POST with firstName={101 characters}
   - WHEN: API call executed
   - THEN:
     - [ ] Validation fails (maxLength=100)
     - [ ] 400 Bad Request

**Dependencies:** BE-014

---

### [QA-026] CORS Configuration Test
**Type:** Security Test
**Component:** CORS headers

**Test Scenarios:**
1. **Allowed Origin (localhost:3000):**
   - GIVEN: Request from Origin: http://localhost:3000
   - WHEN: OPTIONS /api/v1/students (preflight)
   - THEN:
     - [ ] Response includes Access-Control-Allow-Origin: http://localhost:3000
     - [ ] Access-Control-Allow-Methods includes POST, GET, PUT, DELETE

2. **Disallowed Origin:**
   - GIVEN: Request from Origin: http://malicious.com
   - WHEN: OPTIONS /api/v1/students
   - THEN:
     - [ ] CORS error
     - [ ] Access-Control-Allow-Origin header not present

**Dependencies:** BE-017

---

## Phase 8: Deployment & DevOps Testing

### [QA-027] Docker Build and Startup Test
**Type:** DevOps Test
**Component:** Docker containers

**Test Scenarios:**
1. **Student Service Docker Build:**
   - GIVEN: Dockerfile in student-service/
   - WHEN: docker build -t student-service .
   - THEN:
     - [ ] Build succeeds without errors
     - [ ] Image size < 300MB (multi-stage build)

2. **Docker Compose Startup:**
   - GIVEN: docker-compose.yml
   - WHEN: docker-compose up -d
   - THEN:
     - [ ] All services start (student-db, config-db, student-service, config-service, frontend, redis)
     - [ ] Health checks pass for all services
     - [ ] Services communicate (student-service connects to student-db)

3. **Environment Variables:**
   - GIVEN: .env file with DB credentials
   - WHEN: Services start
   - THEN:
     - [ ] Database connection uses env vars (no hardcoded passwords)
     - [ ] Verify with docker logs

**Dependencies:** BE-029, FE-020

---

### [QA-028] Actuator Health Check Test
**Type:** DevOps Test
**Component:** Actuator endpoints

**Test Scenarios:**
1. **Health Endpoint:**
   - WHEN: GET http://localhost:8081/actuator/health
   - THEN:
     - [ ] Response: 200 OK
     - [ ] status: UP
     - [ ] components.db.status: UP
     - [ ] components.redis.status: UP (if configured)

2. **Metrics Endpoint:**
   - WHEN: GET http://localhost:8081/actuator/metrics
   - THEN:
     - [ ] Response includes JVM metrics
     - [ ] Custom metrics present: students.registered.total, students.active.count

3. **Prometheus Endpoint:**
   - WHEN: GET http://localhost:8081/actuator/prometheus
   - THEN:
     - [ ] Response in Prometheus format
     - [ ] Metrics scrapable by Prometheus

**Dependencies:** BE-019

---

## Phase 9: User Acceptance Testing (UAT)

### [QA-029] UAT - Student Registration User Journey
**Type:** UAT
**Persona:** Clerical Staff

**User Story:**
As a clerical staff member, I want to register a new student so that they can enroll in the school.

**Test Steps:**
1. Login to application (Phase 2 - skip for now)
2. Navigate to Students page
3. Click "Register New Student"
4. Fill all required fields
5. Submit form

**Acceptance Criteria:**
- [ ] Form is intuitive and easy to understand
- [ ] Validation errors are clear and actionable
- [ ] Success notification confirms registration
- [ ] Student ID is displayed and can be printed

**Dependencies:** FE-025

---

### [QA-030] UAT - Student Search User Journey
**Type:** UAT
**Persona:** Clerical Staff

**User Story:**
As a clerical staff member, I want to search for a student by last name so that I can update their information.

**Test Steps:**
1. Navigate to Students page
2. Enter partial last name in search box
3. Review results
4. Click "View Details" on a student

**Acceptance Criteria:**
- [ ] Search is fast and responsive (< 1 second)
- [ ] Results update as user types (debounced)
- [ ] Student details are complete and accurate
- [ ] UI is visually clear and matches design

**Dependencies:** FE-025

---

## Completion Checklist

### Backend Testing
- [ ] All unit tests pass (≥ 95% coverage for domain layer)
- [ ] All integration tests pass (Testcontainers)
- [ ] Drools rules tested and validated
- [ ] Optimistic locking enforced
- [ ] API responses match OpenAPI spec

### Frontend Testing
- [ ] Zod schemas validated
- [ ] React Query hooks tested
- [ ] UI matches reference code 1:1
- [ ] Loading and error states functional

### E2E Testing
- [ ] Student CRUD flows work end-to-end
- [ ] Configuration CRUD flows work end-to-end
- [ ] Form validation prevents invalid submissions
- [ ] Toast notifications appear correctly

### Performance Testing
- [ ] API response time < 200ms (P95)
- [ ] Frontend Lighthouse score > 90
- [ ] Database queries optimized (indexes used)
- [ ] Cache hit ratio > 80% for configurations

### Security Testing
- [ ] SQL injection prevented
- [ ] XSS attacks blocked
- [ ] CORS configured correctly
- [ ] Input validation enforced

### DevOps Testing
- [ ] Docker builds succeed
- [ ] docker-compose starts all services
- [ ] Health checks pass
- [ ] Environment variables used (no hardcoded secrets)

---

**Total Test Cases:** 30
**Estimated Effort:** 40-60 QA hours
**Critical Path:** QA-006 → QA-015 → QA-029 (Integration → E2E → UAT)

---

**Document Version:** 1.0
**Last Updated:** 2026-02-03
**Owner:** Technical SDLC Planner Agent
