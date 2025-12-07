# QA Test Plan

## Overview
This document provides a comprehensive test plan for the School Management System, covering unit tests, integration tests, and end-to-end tests for both backend and frontend.

**Testing Approach:** Waterfall - testing follows development phases
**Target Coverage:** 80%+ code coverage
**Testing Pyramid:** 60% Unit, 30% Integration, 10% E2E

---

## Backend QA Tasks

### Phase 1: Unit Tests - Domain Layer

### [QA-BE-001] Test Student Domain Entity Creation
**Type:** Unit Test
**Component:** Student.java

**Test Scenarios:**
1. Should create student with valid data
   - Given: Valid PersonalInfo, ContactInfo, FamilyInfo
   - When: Student.createNew() is called
   - Then: Student created with ACTIVE status, version 0

2. Should throw InvalidAgeException when age < 3
   - Given: Date of birth 2 years ago
   - When: Student.createNew() is called
   - Then: InvalidAgeException thrown with appropriate message

3. Should throw InvalidAgeException when age > 18
   - Given: Date of birth 19 years ago
   - When: Student.createNew() is called
   - Then: InvalidAgeException thrown

4. Should calculate age correctly
   - Given: Student with DOB 10 years ago
   - When: calculateAge() called
   - Then: Returns 10

**Success Criteria:** All test cases pass, edge cases covered

---

### [QA-BE-002] Test Student Domain Entity Updates
**Type:** Unit Test
**Component:** Student.java

**Test Scenarios:**
1. Should update personal info successfully
   - Given: Existing student
   - When: updatePersonalInfo("Jane", "Smith") called
   - Then: First and last name updated, auditInfo updated

2. Should update contact info successfully
   - Given: Existing student
   - When: updateContactInfo("9876543211") called
   - Then: Mobile updated

3. Should activate inactive student
   - Given: Student with INACTIVE status
   - When: activate() called
   - Then: Status changed to ACTIVE

4. Should throw exception when activating active student
   - Given: Student with ACTIVE status
   - When: activate() called
   - Then: IllegalStateException thrown

5. Should deactivate active student
   - Given: Student with ACTIVE status
   - When: deactivate() called
   - Then: Status changed to INACTIVE

**Success Criteria:** All business logic validated

---

### [QA-BE-003] Test Value Objects Immutability
**Type:** Unit Test
**Components:** PersonalInfo, ContactInfo, FamilyInfo, StudentId

**Test Scenarios:**
1. Should create StudentId with valid format
   - Given: Valid ID "STD-20241206-0001"
   - When: StudentId.of() called
   - Then: StudentId created successfully

2. Should throw exception for invalid StudentId format
   - Given: Invalid ID "INVALID"
   - When: StudentId.of() called
   - Then: IllegalArgumentException thrown

3. Should create PersonalInfo with name update
   - Given: Existing PersonalInfo
   - When: withName() called
   - Then: New PersonalInfo instance returned, original unchanged

4. Should create ContactInfo with mobile update
   - Given: Existing ContactInfo
   - When: withMobile() called
   - Then: New ContactInfo instance returned

**Success Criteria:** Value objects are immutable, factory methods work

---

### [QA-BE-004] Test Configuration Domain Entity
**Type:** Unit Test
**Component:** ConfigurationSetting.java

**Test Scenarios:**
1. Should create configuration with valid data
2. Should validate category enum (GENERAL, ACADEMIC, FINANCIAL)
3. Should validate data type enum (STRING, NUMBER, BOOLEAN, JSON)
4. Should update configuration value
5. Should handle encryption flag correctly

**Success Criteria:** All validations working

---

### Phase 2: Unit Tests - Application Layer

### [QA-BE-005] Test Student Application Service - Create
**Type:** Unit Test
**Component:** StudentApplicationService.java

**Test Scenarios:**
1. Should create student successfully
   - Given: Valid CreateStudentRequest
   - When: createStudent() called
   - Then: Student saved, StudentResponse returned

2. Should throw exception for duplicate mobile
   - Given: Mobile number already exists
   - When: createStudent() called
   - Then: DuplicateMobileException thrown

3. Should throw exception for duplicate Aadhaar
   - Given: Aadhaar number already exists
   - When: createStudent() called
   - Then: DuplicateAadhaarException thrown

4. Should throw exception for invalid age
   - Given: DOB with age 2 or 19
   - When: createStudent() called
   - Then: InvalidAgeException thrown

**Success Criteria:** All business rules enforced, mocked repository works

---

### [QA-BE-006] Test Student Application Service - Update
**Type:** Unit Test
**Component:** StudentApplicationService.java

**Test Scenarios:**
1. Should update student successfully
   - Given: Valid UpdateStudentRequest with correct version
   - When: updateStudent() called
   - Then: Student updated, version incremented

2. Should throw exception for version mismatch
   - Given: UpdateStudentRequest with wrong version
   - When: updateStudent() called
   - Then: OptimisticLockException thrown (or similar)

3. Should throw exception for student not found
   - Given: Invalid studentId
   - When: updateStudent() called
   - Then: StudentNotFoundException thrown

4. Should update only allowed fields
   - Given: UpdateStudentRequest
   - When: updateStudent() called
   - Then: Only firstName, lastName, mobile, status updated

**Success Criteria:** Optimistic locking works, validations pass

---

### [QA-BE-007] Test Student Application Service - Search
**Type:** Unit Test
**Component:** StudentApplicationService.java

**Test Scenarios:**
1. Should search by last name
   - Given: lastName filter
   - When: searchStudents() called
   - Then: Filtered, paginated results returned

2. Should search by father's name
   - Given: fathersName filter
   - When: searchStudents() called
   - Then: Filtered results returned

3. Should search by status
   - Given: status filter (ACTIVE)
   - When: searchStudents() called
   - Then: Only ACTIVE students returned

4. Should handle pagination correctly
   - Given: page=1, size=10
   - When: searchStudents() called
   - Then: Second page with 10 items returned

**Success Criteria:** All search filters work, pagination correct

---

### [QA-BE-008] Test Configuration Application Service
**Type:** Unit Test
**Component:** ConfigurationApplicationService.java

**Test Scenarios:**
1. Should create new configuration (UPSERT - insert)
   - Given: New category and key
   - When: createOrUpdate() called
   - Then: Configuration created with 201

2. Should update existing configuration (UPSERT - update)
   - Given: Existing category and key
   - When: createOrUpdate() called
   - Then: Configuration updated with 200

3. Should get configuration by category and key
   - Given: Valid category and key
   - When: getConfiguration() called
   - Then: Configuration returned

4. Should throw exception for not found
   - Given: Invalid category/key
   - When: getConfiguration() called
   - Then: ConfigurationNotFoundException thrown

5. Should get grouped configurations
   - Given: Category GENERAL
   - When: getGroupedByCategory() called
   - Then: Map<String, String> returned

**Success Criteria:** UPSERT logic works, all operations tested

---

### Phase 3: Integration Tests - Backend

### [QA-BE-009] Test Student Repository Integration
**Type:** Integration Test
**Component:** StudentRepositoryImpl.java

**Setup:**
- Use TestContainers with PostgreSQL
- Load schema from school_management.sql
- Clean database before each test

**Test Scenarios:**
1. Should save new student to database
   - Given: New Student entity
   - When: repository.save() called
   - Then: Student persisted, ID generated, student_id auto-generated

2. Should find student by studentId
   - Given: Saved student
   - When: repository.findByStudentId() called
   - Then: Student returned

3. Should find student by mobile
   - Given: Saved student
   - When: repository.findByMobile() called
   - Then: Student returned

4. Should check mobile exists
   - Given: Saved student with mobile "9876543210"
   - When: repository.existsByMobile("9876543210") called
   - Then: Returns true

5. Should update student
   - Given: Existing student
   - When: Update and save
   - Then: Changes persisted, updated_at changed, version incremented

6. Should delete student
   - Given: Saved student
   - When: repository.deleteById() called
   - Then: Student removed from database

7. Should cascade delete enrollments
   - Given: Student with enrollments
   - When: Student deleted
   - Then: Enrollments also deleted (CASCADE)

**Success Criteria:** All database operations work, constraints enforced

---

### [QA-BE-010] Test Enrollment Repository Integration
**Type:** Integration Test
**Component:** EnrollmentRepositoryImpl.java

**Test Scenarios:**
1. Should save enrollment to database
2. Should find enrollments by studentId
3. Should find enrollment by studentId and academic year
4. Should enforce unique constraint (student + academic year)
   - Given: Existing enrollment for student in 2024-2025
   - When: Try to save another enrollment for same student and year
   - Then: Constraint violation exception

**Success Criteria:** Enrollments persisted correctly, constraints work

---

### [QA-BE-011] Test Configuration Repository Integration
**Type:** Integration Test
**Component:** ConfigurationRepositoryImpl.java

**Test Scenarios:**
1. Should save configuration to database
2. Should find configuration by category and key
3. Should find all configurations by category
4. Should update existing configuration (same category + key)
5. Should delete configuration
6. Should enforce unique constraint on category + key

**Success Criteria:** UPSERT works, constraints enforced

---

### [QA-BE-012] Test Student REST API Integration
**Type:** Integration Test (Spring Boot Test)
**Component:** StudentController.java

**Setup:**
- Use @SpringBootTest with RANDOM_PORT
- Use TestRestTemplate
- Mock database or use TestContainers

**Test Scenarios:**
1. POST /api/v1/students - Create student
   - Given: Valid CreateStudentRequest JSON
   - When: POST request sent
   - Then: 201 Created, StudentResponse in body, Location header

2. GET /api/v1/students/{studentId} - Get student
   - Given: Existing student
   - When: GET request sent
   - Then: 200 OK, StudentResponse in body

3. GET /api/v1/students/{studentId} - Not found
   - Given: Invalid studentId
   - When: GET request sent
   - Then: 404 Not Found, RFC 7807 error body

4. PUT /api/v1/students/{studentId} - Update student
   - Given: Valid UpdateStudentRequest with correct version
   - When: PUT request sent
   - Then: 200 OK, updated StudentResponse

5. PUT /api/v1/students/{studentId} - Version conflict
   - Given: UpdateStudentRequest with wrong version
   - When: PUT request sent
   - Then: 409 Conflict, error body

6. DELETE /api/v1/students/{studentId} - Delete student
   - Given: Existing student
   - When: DELETE request sent
   - Then: 204 No Content

7. GET /api/v1/students - Search students
   - Given: Multiple students in database
   - When: GET with query params (lastName=Doe)
   - Then: 200 OK, paginated response with filtered students

8. POST /api/v1/students - Validation error
   - Given: Invalid request (age < 3)
   - When: POST request sent
   - Then: 400 Bad Request, validation errors in RFC 7807 format

9. POST /api/v1/students - Duplicate mobile
   - Given: Mobile already exists
   - When: POST request sent
   - Then: 409 Conflict, error message

**Success Criteria:** All endpoints return correct status codes, error handling works

---

### [QA-BE-013] Test Configuration REST API Integration
**Type:** Integration Test
**Component:** ConfigurationController.java

**Test Scenarios:**
1. GET /api/v1/configurations - Get all
   - When: GET request
   - Then: 200 OK, list of configurations

2. GET /api/v1/configurations?category=GENERAL - Filter by category
   - When: GET with category param
   - Then: 200 OK, filtered list

3. GET /api/v1/configurations/{category}/{key} - Get specific
   - Given: Existing configuration
   - When: GET request
   - Then: 200 OK, ConfigurationResponse

4. PUT /api/v1/configurations/{category}/{key} - Create new (UPSERT)
   - Given: New category/key
   - When: PUT request
   - Then: 201 Created

5. PUT /api/v1/configurations/{category}/{key} - Update existing (UPSERT)
   - Given: Existing configuration
   - When: PUT request
   - Then: 200 OK

6. DELETE /api/v1/configurations/{category}/{key} - Delete
   - Given: Existing configuration
   - When: DELETE request
   - Then: 204 No Content

7. GET /api/v1/configurations/grouped/{category} - Get grouped
   - Given: Multiple configs in category
   - When: GET request
   - Then: 200 OK, map of key-value pairs

**Success Criteria:** UPSERT logic works via API, all endpoints functional

---

### [QA-BE-014] Test CORS Configuration
**Type:** Integration Test
**Component:** WebConfig.java

**Test Scenarios:**
1. Should allow requests from http://localhost:3000
   - Given: Request from origin http://localhost:3000
   - When: Preflight OPTIONS request
   - Then: CORS headers present, request allowed

2. Should allow requests from http://localhost:5173
   - Given: Request from origin http://localhost:5173
   - When: Preflight OPTIONS request
   - Then: CORS headers present

3. Should reject requests from unknown origin
   - Given: Request from http://evil.com
   - When: OPTIONS request
   - Then: CORS headers absent or request blocked

**Success Criteria:** CORS working for allowed origins, blocked for others

---

### [QA-BE-015] Test Global Exception Handler
**Type:** Integration Test
**Component:** GlobalExceptionHandler.java

**Test Scenarios:**
1. Should return RFC 7807 format for StudentNotFoundException
   - Given: Invalid student ID in request
   - When: GET /api/v1/students/{invalidId}
   - Then: 404, RFC 7807 body with type, title, status, detail, timestamp, correlationId

2. Should return RFC 7807 for validation errors
   - Given: Invalid CreateStudentRequest
   - When: POST /api/v1/students
   - Then: 400, errors array with field-level errors

3. Should return RFC 7807 for optimistic lock exception
   - Given: Version mismatch
   - When: PUT request
   - Then: 409, appropriate error message

4. Should include correlation ID in all errors
   - Given: Any error
   - When: Error occurs
   - Then: correlationId present in response

**Success Criteria:** All exceptions return consistent error format

---

### Phase 4: E2E Tests - Backend API

### [QA-BE-016] E2E Test - Complete Student Lifecycle
**Type:** End-to-End Test
**Tools:** REST Assured or similar

**Test Flow:**
1. Start student-service and database
2. Create new student via POST /api/v1/students
3. Verify student created (201, student_id generated)
4. Get student via GET /api/v1/students/{studentId}
5. Verify all fields correct
6. Update student via PUT /api/v1/students/{studentId}
7. Verify update successful, version incremented
8. Search for student via GET /api/v1/students?lastName=...
9. Verify student in results
10. Delete student via DELETE /api/v1/students/{studentId}
11. Verify deletion (204)
12. Try to get deleted student
13. Verify 404 returned

**Success Criteria:** Complete CRUD lifecycle works end-to-end

---

### [QA-BE-017] E2E Test - Student Enrollment Lifecycle
**Type:** End-to-End Test

**Test Flow:**
1. Create student
2. Create enrollment via POST /api/v1/students/{studentId}/enrollment-history
3. Verify enrollment created
4. Get enrollment history via GET /api/v1/students/{studentId}/enrollment-history
5. Verify enrollment in list
6. Try to create duplicate enrollment (same academic year)
7. Verify 409 Conflict returned

**Success Criteria:** Enrollment management works, constraints enforced

---

### [QA-BE-018] E2E Test - Configuration Management
**Type:** End-to-End Test

**Test Flow:**
1. Start configuration-service
2. Get all configurations via GET /api/v1/configurations
3. Create new config via PUT /api/v1/configurations/GENERAL/test.key
4. Verify 201 Created
5. Update same config via PUT (UPSERT)
6. Verify 200 OK
7. Get specific config via GET /api/v1/configurations/GENERAL/test.key
8. Verify value updated
9. Get grouped configs via GET /api/v1/configurations/grouped/GENERAL
10. Verify test.key in map
11. Delete config via DELETE /api/v1/configurations/GENERAL/test.key
12. Verify 204 No Content

**Success Criteria:** Full configuration lifecycle works, UPSERT logic correct

---

### [QA-BE-019] Test Optimistic Locking Concurrency
**Type:** Integration Test

**Test Scenario:**
1. Thread 1: Get student (version = 0)
2. Thread 2: Get same student (version = 0)
3. Thread 1: Update student (version = 0)
   - Success: version becomes 1
4. Thread 2: Try to update student (version = 0)
   - Failure: 409 Conflict (version mismatch)

**Success Criteria:** Concurrent updates handled correctly, data integrity maintained

---

### [QA-BE-020] Test Data Validation Rules
**Type:** Integration Test

**Test Scenarios:**
1. Age validation - reject if DOB results in age < 3 or > 18
2. Mobile format - reject if not 10 digits
3. Email format - reject if invalid email
4. Aadhaar format - reject if not 12 digits
5. Required fields - reject if firstName, lastName, DOB, mobile missing
6. Status enum - reject if not ACTIVE or INACTIVE
7. Category enum - reject if not GENERAL, ACADEMIC, FINANCIAL

**Success Criteria:** All validation rules enforced at API level

---

## Frontend QA Tasks

### Phase 5: Unit Tests - React Components

### [QA-FE-001] Test Button Component
**Type:** Unit Test (Vitest + React Testing Library)
**Component:** Button.tsx

**Test Scenarios:**
1. Should render button with text
2. Should call onClick handler when clicked
3. Should show loading spinner when loading=true
4. Should be disabled when disabled=true
5. Should apply correct variant styles (primary, secondary, danger)
6. Should apply correct size styles (sm, md, lg)

**Success Criteria:** All props work, events fire correctly

---

### [QA-FE-002] Test Input Component
**Type:** Unit Test
**Component:** Input.tsx

**Test Scenarios:**
1. Should render input with label
2. Should display error message when error prop provided
3. Should apply error styling when error present
4. Should accept user input and update value
5. Should be disabled when disabled=true
6. Should show required indicator when required=true
7. Should work with React Hook Form register

**Success Criteria:** Input functional, validation display works

---

### [QA-FE-003] Test Table Component
**Type:** Unit Test
**Component:** Table.tsx

**Test Scenarios:**
1. Should render table with headers from columns prop
2. Should render rows from data prop
3. Should show loading skeleton when loading=true
4. Should show empty message when data is empty
5. Should call custom render function for columns
6. Should be responsive (horizontal scroll on small screens)

**Success Criteria:** Table renders correctly, handles all states

---

### [QA-FE-004] Test Modal Component
**Type:** Unit Test
**Component:** Modal.tsx

**Test Scenarios:**
1. Should render modal when isOpen=true
2. Should not render when isOpen=false
3. Should call onClose when backdrop clicked
4. Should call onClose when Escape pressed
5. Should trap focus within modal
6. Should render title and children content

**Success Criteria:** Modal behavior correct, accessible

---

### [QA-FE-005] Test Pagination Component
**Type:** Unit Test
**Component:** Pagination.tsx

**Test Scenarios:**
1. Should render page numbers correctly
2. Should disable Previous on first page
3. Should disable Next on last page
4. Should call onPageChange with correct page number
5. Should show ellipsis for large page counts
6. Should highlight current page

**Success Criteria:** Pagination logic correct

---

### [QA-FE-006] Test StudentForm Component
**Type:** Unit Test
**Component:** StudentForm.tsx

**Test Scenarios:**
1. Should render all form fields
2. Should validate required fields
3. Should validate mobile format (10 digits)
4. Should validate email format
5. Should validate DOB and calculate age
6. Should show age validation error if age < 3 or > 18
7. Should call onSubmit with form data
8. Should pre-populate fields in edit mode
9. Should show status field only in edit mode

**Success Criteria:** Form validation works, mode handling correct

---

### [QA-FE-007] Test ConfigurationForm Component
**Type:** Unit Test
**Component:** ConfigurationForm.tsx

**Test Scenarios:**
1. Should render all fields
2. Should validate required fields
3. Should disable category and key in edit mode
4. Should validate data type selection
5. Should call onSubmit with correct data

**Success Criteria:** Form works for create and edit modes

---

### Phase 6: Integration Tests - React Query Hooks

### [QA-FE-008] Test useStudents Hook
**Type:** Integration Test
**Hook:** useStudents

**Test Scenarios:**
1. Should fetch students on mount
2. Should return loading state initially
3. Should return data after successful fetch
4. Should return error on fetch failure
5. Should refetch when page changes
6. Should apply filters correctly (lastName, status)
7. Should cache results for 5 minutes

**Success Criteria:** Hook manages server state correctly

---

### [QA-FE-009] Test useCreateStudent Hook
**Type:** Integration Test
**Hook:** useCreateStudent

**Test Scenarios:**
1. Should call POST /api/v1/students with data
2. Should return loading state during mutation
3. Should return success on creation
4. Should invalidate students query on success
5. Should return error on validation failure (400)
6. Should return error on duplicate mobile (409)

**Success Criteria:** Mutation works, cache invalidation correct

---

### [QA-FE-010] Test useUpdateStudent Hook
**Type:** Integration Test
**Hook:** useUpdateStudent

**Test Scenarios:**
1. Should call PUT /api/v1/students/{id} with data
2. Should handle version in request
3. Should return error on version conflict (409)
4. Should invalidate relevant queries on success

**Success Criteria:** Update mutation works, optimistic locking handled

---

### [QA-FE-011] Test useDeleteStudent Hook
**Type:** Integration Test
**Hook:** useDeleteStudent

**Test Scenarios:**
1. Should call DELETE /api/v1/students/{id}
2. Should invalidate students query on success
3. Should handle errors appropriately

**Success Criteria:** Delete mutation works

---

### [QA-FE-012] Test useConfigurations Hook
**Type:** Integration Test
**Hook:** useConfigurations

**Test Scenarios:**
1. Should fetch all configurations
2. Should filter by category when provided
3. Should cache results
4. Should handle errors

**Success Criteria:** Fetching and filtering work

---

### Phase 7: E2E Tests - User Workflows

### [QA-FE-013] E2E Test - Create Student Flow
**Type:** End-to-End Test (Playwright)
**User Story:** As a staff member, I want to create a new student record

**Test Steps:**
1. Navigate to http://localhost:3000/students
2. Click "Create Student" button
3. Fill in form:
   - First Name: "John"
   - Last Name: "Doe"
   - Date of Birth: "2010-05-15"
   - Mobile: "9876543210"
   - Email: "john.doe@example.com"
   - Father's Name: "Richard Doe"
   - Mother's Name: "Jane Doe"
4. Click "Submit"
5. Verify success toast appears
6. Verify redirected to student detail page
7. Verify all data displayed correctly
8. Verify student_id auto-generated (format: STD-YYYYMMDD-NNNN)

**Success Criteria:** Complete create flow works, data persisted

---

### [QA-FE-014] E2E Test - Search and View Student
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to /students
2. Enter "Doe" in Last Name search field
3. Click Search button
4. Verify results table shows students with lastName "Doe"
5. Click on first student in results
6. Verify navigated to detail page
7. Verify all student information displayed

**Success Criteria:** Search and navigation work

---

### [QA-FE-015] E2E Test - Update Student
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to student detail page
2. Click "Edit" button
3. Verify navigated to edit page
4. Verify form pre-populated with existing data
5. Change first name to "Jane"
6. Change mobile to "9876543211"
7. Click "Save"
8. Verify success toast
9. Verify redirected to detail page
10. Verify changes reflected

**Success Criteria:** Update flow works, changes persisted

---

### [QA-FE-016] E2E Test - Delete Student
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to student detail page
2. Click "Delete" button
3. Verify confirmation modal appears
4. Click "Confirm Delete"
5. Verify success toast
6. Verify redirected to students list
7. Search for deleted student
8. Verify student not in results

**Success Criteria:** Delete flow works, confirmation required

---

### [QA-FE-017] E2E Test - Form Validation
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to /students/new
2. Leave all fields empty
3. Click Submit
4. Verify error messages shown for required fields:
   - "First name is required"
   - "Last name is required"
   - "Date of birth is required"
   - "Mobile is required"
5. Fill First Name: "J" (too short)
6. Verify error: "First name must be at least 2 characters"
7. Fill Mobile: "12345" (invalid)
8. Verify error: "Mobile must be exactly 10 digits"
9. Fill DOB: 2 years ago
10. Verify error: "Student must be between 3 and 18 years old"
11. Fill all fields correctly
12. Verify submit button enabled
13. Submit form
14. Verify success

**Success Criteria:** All validations work, user-friendly error messages

---

### [QA-FE-018] E2E Test - Duplicate Mobile Handling
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Create student with mobile "9876543210"
2. Navigate to create new student
3. Fill form with mobile "9876543210" (duplicate)
4. Submit form
5. Verify error toast appears
6. Verify error message: "Mobile number already exists"
7. Verify form not cleared
8. Change mobile to unique number
9. Submit
10. Verify success

**Success Criteria:** Duplicate mobile rejected, error handled gracefully

---

### [QA-FE-019] E2E Test - Pagination
**Type:** End-to-End Test (Playwright)

**Prerequisites:** Database has 50+ students

**Test Steps:**
1. Navigate to /students
2. Verify first page shows 20 students (default)
3. Verify pagination controls displayed
4. Click "Next" button
5. Verify page 2 loaded, different students shown
6. Verify page number highlighted
7. Click on page number "3"
8. Verify page 3 loaded
9. Verify "Previous" button works
10. Verify "Next" disabled on last page

**Success Criteria:** Pagination functional, all controls work

---

### [QA-FE-020] E2E Test - Configuration Management
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to /configuration
2. Verify configurations grouped by category (GENERAL, ACADEMIC, FINANCIAL)
3. Click "Add Configuration" in GENERAL section
4. Fill form:
   - Category: GENERAL (pre-selected)
   - Key: "test.setting"
   - Value: "Test Value"
   - Data Type: STRING
5. Click Save
6. Verify configuration appears in GENERAL table
7. Click Edit on new configuration
8. Change value to "Updated Value"
9. Click Save
10. Verify value updated in table
11. Click Delete on configuration
12. Confirm deletion
13. Verify configuration removed from table

**Success Criteria:** Full configuration CRUD works

---

### [QA-FE-021] E2E Test - Responsive Design
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Set viewport to mobile (375x667)
2. Navigate to /students
3. Verify hamburger menu visible
4. Verify table horizontal scrollable
5. Click hamburger menu
6. Verify navigation menu opens
7. Set viewport to tablet (768x1024)
8. Verify layout adjusts appropriately
9. Set viewport to desktop (1920x1080)
10. Verify full desktop layout
11. Test create form on mobile
12. Verify fields stack vertically
13. Verify form usable on small screen

**Success Criteria:** Application responsive on all screen sizes

---

### [QA-FE-022] E2E Test - Error Handling
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Stop backend services
2. Navigate to /students
3. Verify error toast: "Failed to load students"
4. Verify friendly error message displayed
5. Start backend services
6. Click retry/refresh
7. Verify data loads successfully

**Success Criteria:** Network errors handled gracefully

---

### [QA-FE-023] E2E Test - Loading States
**Type:** End-to-End Test (Playwright)

**Test Steps:**
1. Navigate to /students
2. Verify loading spinner shown while fetching
3. Verify spinner disappears when data loaded
4. Navigate to /students/new
5. Fill and submit form
6. Verify submit button shows loading state
7. Verify button disabled during submission
8. Verify loading state clears on success

**Success Criteria:** Loading states provide feedback

---

### Phase 8: Performance Tests

### [QA-FE-024] Test Frontend Bundle Size
**Type:** Performance Test

**Test Steps:**
1. Run `npm run build`
2. Check build output for bundle sizes
3. Verify main bundle < 500KB gzipped
4. Verify no single chunk > 1MB
5. Verify code splitting working

**Success Criteria:** Bundle size within acceptable limits

---

### [QA-FE-025] Test Page Load Performance
**Type:** Performance Test (Lighthouse)

**Test Steps:**
1. Run Lighthouse audit on main pages
2. Verify Performance score > 90
3. Verify First Contentful Paint < 1.5s
4. Verify Time to Interactive < 3s
5. Verify Cumulative Layout Shift < 0.1

**Success Criteria:** All performance metrics meet targets

---

### Phase 9: Accessibility Tests

### [QA-FE-026] Test Keyboard Navigation
**Type:** Accessibility Test

**Test Steps:**
1. Navigate to /students using only keyboard
2. Tab through all interactive elements
3. Verify all elements focusable
4. Verify focus indicators visible
5. Test form submission with Enter key
6. Test modal close with Escape key
7. Verify skip-to-content link works

**Success Criteria:** Full keyboard navigation support

---

### [QA-FE-027] Test Screen Reader Compatibility
**Type:** Accessibility Test (NVDA/JAWS)

**Test Steps:**
1. Enable screen reader
2. Navigate through application
3. Verify all images have alt text
4. Verify form labels announced correctly
5. Verify error messages announced
6. Verify table structure announced
7. Verify modal dialogs announced

**Success Criteria:** Screen reader can navigate entire application

---

### [QA-FE-028] Test Color Contrast
**Type:** Accessibility Test

**Test Steps:**
1. Use axe DevTools or similar
2. Check all pages for contrast issues
3. Verify all text meets WCAG AA (4.5:1)
4. Verify interactive elements meet standards
5. Test with different color blindness filters

**Success Criteria:** All contrast ratios meet WCAG 2.1 AA

---

## Cross-Functional Tests

### [QA-XF-001] Integration Test - Frontend + Backend
**Type:** Full Stack Integration

**Test Steps:**
1. Start PostgreSQL database
2. Start student-service (port 8081)
3. Start configuration-service (port 8082)
4. Start frontend (port 3000)
5. Execute complete user workflow:
   - Create student via UI
   - Verify data in database
   - Update student via UI
   - Verify update in database
   - Delete student via UI
   - Verify deletion in database
6. Test configuration management
7. Verify CORS working (no errors in console)

**Success Criteria:** Full stack integration works seamlessly

---

### [QA-XF-002] Test API Contract Compliance
**Type:** Contract Test

**Test Steps:**
1. Verify Student API matches OpenAPI spec (03-api-specification.md)
2. Verify Configuration API matches spec
3. Verify all endpoints return correct status codes
4. Verify all responses match defined schemas
5. Verify error responses follow RFC 7807 format

**Success Criteria:** APIs comply with specifications

---

## Test Execution Checklist

### Backend Tests Complete When:
- [ ] All QA-BE-001 to QA-BE-020 pass
- [ ] Unit test coverage > 80%
- [ ] All integration tests pass
- [ ] E2E API tests pass
- [ ] All validation rules enforced
- [ ] CORS tested and working
- [ ] Error handling comprehensive

### Frontend Tests Complete When:
- [ ] All QA-FE-001 to QA-FE-028 pass
- [ ] Component tests cover all UI components
- [ ] React Query hooks tested
- [ ] E2E tests cover critical user flows
- [ ] Responsive design verified
- [ ] Accessibility tests pass (WCAG 2.1 AA)
- [ ] Performance metrics met

### Full System Tests Complete When:
- [ ] All QA-XF tests pass
- [ ] Frontend-backend integration verified
- [ ] API contracts validated
- [ ] Data flow end-to-end tested
- [ ] No console errors in browser
- [ ] No backend exceptions in logs

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** READY FOR EXECUTION
