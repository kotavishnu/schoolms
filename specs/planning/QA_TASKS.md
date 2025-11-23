# QA Test Plan - School Management System

## Overview
This document provides a comprehensive test plan for the School Management System, covering Unit, Integration, and End-to-End testing across both backend and frontend components.

**Testing Approach:** Test Pyramid (60% Unit, 30% Integration, 10% E2E)
**Target:** Ensure quality, reliability, and business requirement compliance

---

## Backend Unit Tests

### [QA-001] Test Student Aggregate Root - Valid Creation
**Type:** Unit Test
**Component:** Student Domain Entity

**Steps:**
1. Create Student with valid data (age 10)
2. Use StudentId: "STU-2025-00001"
3. Use Mobile: "+919876543210"
4. Use dateOfBirth: 10 years ago

**Success Criteria:**
- Student object is created successfully
- StudentId matches input
- Status is ACTIVE by default
- getCurrentAge() returns 10

**Test Class:** StudentTest.java
**Dependencies:** None

---

### [QA-002] Test Student Aggregate Root - Age Validation (Below Minimum)
**Type:** Unit Test
**Component:** Student Domain Entity

**Steps:**
1. Attempt to create Student with dateOfBirth 2 years ago (age 2)
2. Call Student.register() factory method

**Success Criteria:**
- IllegalArgumentException is thrown
- Error message contains "Student age must be between 3 and 18 years"

**Test Class:** StudentTest.java
**Dependencies:** None

---

### [QA-003] Test Student Aggregate Root - Age Validation (Above Maximum)
**Type:** Unit Test
**Component:** Student Domain Entity

**Steps:**
1. Attempt to create Student with dateOfBirth 19 years ago (age 19)
2. Call Student.register() factory method

**Success Criteria:**
- IllegalArgumentException is thrown
- Error message contains "Student age must be between 3 and 18 years"

**Test Class:** StudentTest.java
**Dependencies:** None

---

### [QA-004] Test Student Aggregate Root - Update Editable Fields Only
**Type:** Unit Test
**Component:** Student Domain Entity

**Steps:**
1. Create valid Student
2. Call updateProfile() with new firstName, lastName, mobile
3. Verify dateOfBirth is NOT changed

**Success Criteria:**
- firstName, lastName, mobile are updated
- dateOfBirth, fatherName, motherName remain unchanged
- updatedAt timestamp is updated

**Test Class:** StudentTest.java
**Dependencies:** None

---

### [QA-005] Test Student Aggregate Root - Status Change
**Type:** Unit Test
**Component:** Student Domain Entity

**Steps:**
1. Create valid Student (status ACTIVE)
2. Call changeStatus(INACTIVE)
3. Verify status changed

**Success Criteria:**
- Status changes from ACTIVE to INACTIVE
- isActive() returns false
- updatedAt timestamp is updated

**Test Class:** StudentTest.java
**Dependencies:** None

---

### [QA-006] Test StudentId Value Object - Valid Format
**Type:** Unit Test
**Component:** StudentId Value Object

**Steps:**
1. Create StudentId with valid format: "STU-2025-00001"
2. Call getValue()

**Success Criteria:**
- StudentId is created without exception
- getValue() returns "STU-2025-00001"

**Test Class:** StudentIdTest.java
**Dependencies:** None

---

### [QA-007] Test StudentId Value Object - Invalid Format
**Type:** Unit Test
**Component:** StudentId Value Object

**Steps:**
1. Attempt to create StudentId with invalid format: "INVALID"

**Success Criteria:**
- IllegalArgumentException is thrown
- Error message contains "Invalid StudentId format"

**Test Class:** StudentIdTest.java
**Dependencies:** None

---

### [QA-008] Test Mobile Value Object - Valid Formats
**Type:** Unit Test
**Component:** Mobile Value Object

**Steps:**
1. Create Mobile with "+919876543210"
2. Create Mobile with "9876543210"
3. Create Mobile with "1234567890123" (13 digits)

**Success Criteria:**
- All three Mobile objects are created successfully
- getNumber() returns the input value

**Test Class:** MobileTest.java
**Dependencies:** None

---

### [QA-009] Test Mobile Value Object - Invalid Format
**Type:** Unit Test
**Component:** Mobile Value Object

**Steps:**
1. Attempt to create Mobile with "12345" (too short)

**Success Criteria:**
- IllegalArgumentException is thrown
- Error message contains "Invalid mobile number format"

**Test Class:** MobileTest.java
**Dependencies:** None

---

### [QA-010] Test StudentService - Create Student Successfully
**Type:** Unit Test
**Component:** StudentService

**Steps:**
1. Mock StudentRepository, StudentIdGenerator, RulesService, CacheService
2. Configure mocks to return success
3. Call studentService.createStudent(validRequest)

**Success Criteria:**
- StudentResponse is returned
- StudentID is "STU-2025-00001"
- rulesService.validateStudentRegistration() is called
- studentRepository.save() is called
- cacheService.cacheStudent() is called

**Test Class:** StudentServiceImplTest.java
**Dependencies:** BE-016

---

### [QA-011] Test StudentService - Duplicate Mobile Exception
**Type:** Unit Test
**Component:** StudentService

**Steps:**
1. Mock StudentRepository to return true for existsByMobile()
2. Call studentService.createStudent(request)

**Success Criteria:**
- DuplicateMobileException is thrown
- Error message contains "already exists"
- studentRepository.save() is NOT called

**Test Class:** StudentServiceImplTest.java
**Dependencies:** BE-016

---

### [QA-012] Test StudentService - Student Not Found
**Type:** Unit Test
**Component:** StudentService

**Steps:**
1. Mock StudentRepository to return Optional.empty() for findById()
2. Call studentService.getStudentById(999)

**Success Criteria:**
- StudentNotFoundException is thrown
- Error message contains "not found"

**Test Class:** StudentServiceImplTest.java
**Dependencies:** BE-016

---

### [QA-013] Test StudentService - Update Student and Invalidate Cache
**Type:** Unit Test
**Component:** StudentService

**Steps:**
1. Mock StudentRepository to return existing student
2. Call studentService.updateStudent(id, updateRequest)
3. Verify cache is invalidated

**Success Criteria:**
- studentRepository.save() is called
- cacheService.evictStudent() is called with correct studentId

**Test Class:** StudentServiceImplTest.java
**Dependencies:** BE-016

---

### [QA-014] Test Drools Rules - Valid Age Passes
**Type:** Unit Test
**Component:** Drools Rules Engine

**Steps:**
1. Create CreateStudentRequest with dateOfBirth 10 years ago
2. Insert into KieSession
3. Fire all rules
4. Check validation errors list

**Success Criteria:**
- validationErrors list is empty
- No exceptions thrown

**Test Class:** StudentRulesTest.java
**Dependencies:** BE-021

---

### [QA-015] Test Drools Rules - Age Below 3 Fails
**Type:** Unit Test
**Component:** Drools Rules Engine

**Steps:**
1. Create CreateStudentRequest with dateOfBirth 2 years ago
2. Insert into KieSession
3. Fire all rules
4. Check validation errors list

**Success Criteria:**
- validationErrors list is not empty
- Error message contains "age must be between 3 and 18 years"

**Test Class:** StudentRulesTest.java
**Dependencies:** BE-021

---

### [QA-016] Test Drools Rules - Age Above 18 Fails
**Type:** Unit Test
**Component:** Drools Rules Engine

**Steps:**
1. Create CreateStudentRequest with dateOfBirth 19 years ago
2. Insert into KieSession
3. Fire all rules

**Success Criteria:**
- validationErrors list is not empty
- Error message contains "age must be between 3 and 18 years"

**Test Class:** StudentRulesTest.java
**Dependencies:** BE-021

---

### [QA-017] Test Drools Rules - Empty Mobile Fails
**Type:** Unit Test
**Component:** Drools Rules Engine

**Steps:**
1. Create CreateStudentRequest with mobile = ""
2. Insert into KieSession
3. Fire all rules

**Success Criteria:**
- validationErrors list contains "Mobile number is required"

**Test Class:** StudentRulesTest.java
**Dependencies:** BE-021

---

### [QA-018] Test MapStruct Mapper - Student to Response
**Type:** Unit Test
**Component:** StudentMapper

**Steps:**
1. Create Student domain object
2. Call studentMapper.toResponse(student)
3. Verify mapping

**Success Criteria:**
- StudentResponse.id matches Student.id
- StudentResponse.studentId matches Student.studentId.getValue()
- StudentResponse.mobile matches Student.mobile.getNumber()
- StudentResponse.age is calculated correctly

**Test Class:** StudentMapperTest.java
**Dependencies:** BE-014

---

### [QA-019] Test Configuration Service - Create Configuration
**Type:** Unit Test
**Component:** ConfigurationService

**Steps:**
1. Mock ConfigurationRepository
2. Call configService.createConfiguration(validRequest)

**Success Criteria:**
- ConfigurationResponse is returned
- configRepository.save() is called
- Configuration is cached

**Test Class:** ConfigurationServiceImplTest.java
**Dependencies:** BE-033

---

### [QA-020] Test Configuration Service - Duplicate Key Exception
**Type:** Unit Test
**Component:** ConfigurationService

**Steps:**
1. Mock ConfigurationRepository to return true for existsByCategoryAndConfigKey()
2. Call configService.createConfiguration(request)

**Success Criteria:**
- DuplicateConfigurationException is thrown
- configRepository.save() is NOT called

**Test Class:** ConfigurationServiceImplTest.java
**Dependencies:** BE-033

---

## Backend Integration Tests

### [QA-021] Test StudentRepository - Save and Retrieve
**Type:** Integration Test
**Component:** Student Repository with PostgreSQL

**Steps:**
1. Use TestContainers to start PostgreSQL
2. Create StudentJpaEntity with all fields
3. Call repository.save()
4. Call repository.findById()

**Success Criteria:**
- Student is persisted to database
- Retrieved student matches saved student
- All fields are correctly mapped

**Test Class:** StudentRepositoryTest.java
**Dependencies:** BE-019, BE-040

---

### [QA-022] Test StudentRepository - Find by StudentId
**Type:** Integration Test
**Component:** Student Repository

**Steps:**
1. Save student with studentId "STU-2025-00001"
2. Call repository.findByStudentId("STU-2025-00001")

**Success Criteria:**
- Optional<Student> is present
- Student matches saved student

**Test Class:** StudentRepositoryTest.java
**Dependencies:** BE-019, BE-040

---

### [QA-023] Test StudentRepository - Unique Mobile Constraint
**Type:** Integration Test
**Component:** Student Repository

**Steps:**
1. Save student with mobile "+919876543210"
2. Attempt to save another student with same mobile

**Success Criteria:**
- DataIntegrityViolationException is thrown
- Second student is NOT saved

**Test Class:** StudentRepositoryTest.java
**Dependencies:** BE-019, BE-040

---

### [QA-024] Test StudentRepository - Search by Last Name
**Type:** Integration Test
**Component:** Student Repository

**Steps:**
1. Save 3 students with lastName "Kumar"
2. Save 2 students with lastName "Sharma"
3. Call repository.findByLastNameContainingIgnoreCase("Kumar", pageable)

**Success Criteria:**
- Page contains 3 students
- All have lastName containing "Kumar"

**Test Class:** StudentRepositoryTest.java
**Dependencies:** BE-019, BE-040

---

### [QA-025] Test StudentRepository - Pagination
**Type:** Integration Test
**Component:** Student Repository

**Steps:**
1. Save 25 students
2. Request page 0, size 10
3. Request page 1, size 10
4. Request page 2, size 10

**Success Criteria:**
- Page 0 has 10 students
- Page 1 has 10 students
- Page 2 has 5 students
- totalElements = 25, totalPages = 3

**Test Class:** StudentRepositoryTest.java
**Dependencies:** BE-019, BE-040

---

### [QA-026] Test StudentController - POST Create Student
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Use @WebMvcTest with MockMvc
2. Mock StudentService
3. Perform POST to /api/v1/students with valid JSON
4. Verify response

**Success Criteria:**
- HTTP Status 201 Created
- Location header contains student ID
- Response body contains StudentResponse JSON
- studentService.createStudent() is called

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-040

---

### [QA-027] Test StudentController - POST with Validation Errors
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Perform POST to /api/v1/students with invalid JSON (missing firstName)

**Success Criteria:**
- HTTP Status 400 Bad Request
- Response body contains RFC 7807 Problem Details
- errors array contains field "firstName"

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-025, BE-040

---

### [QA-028] Test StudentController - GET List Students
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService to return page of students
2. Perform GET to /api/v1/students?page=0&size=20

**Success Criteria:**
- HTTP Status 200 OK
- Response body contains StudentPageResponse
- content array has students
- pagination metadata is correct

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-040

---

### [QA-029] Test StudentController - GET by ID (Found)
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService to return student
2. Perform GET to /api/v1/students/1

**Success Criteria:**
- HTTP Status 200 OK
- Response body contains StudentResponse

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-040

---

### [QA-030] Test StudentController - GET by ID (Not Found)
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService to throw StudentNotFoundException
2. Perform GET to /api/v1/students/999

**Success Criteria:**
- HTTP Status 404 Not Found
- Response body contains RFC 7807 Problem Details
- detail field contains "not found"

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-025, BE-040

---

### [QA-031] Test StudentController - PUT Update Student
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService
2. Perform PUT to /api/v1/students/1 with valid update JSON

**Success Criteria:**
- HTTP Status 200 OK
- Response body contains updated StudentResponse
- studentService.updateStudent() is called

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-040

---

### [QA-032] Test StudentController - PUT with Optimistic Lock Error
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService to throw OptimisticLockingFailureException
2. Perform PUT to /api/v1/students/1

**Success Criteria:**
- HTTP Status 409 Conflict
- Response body contains Problem Details with "Concurrent Modification Detected"

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-025, BE-040

---

### [QA-033] Test StudentController - DELETE Student
**Type:** Integration Test
**Component:** Student REST API

**Steps:**
1. Mock StudentService
2. Perform DELETE to /api/v1/students/1

**Success Criteria:**
- HTTP Status 204 No Content
- No response body
- studentService.deleteStudent() is called

**Test Class:** StudentControllerTest.java
**Dependencies:** BE-024, BE-040

---

### [QA-034] Test ConfigurationController - CRUD Operations
**Type:** Integration Test
**Component:** Configuration REST API

**Steps:**
1. Test POST /api/v1/configurations (create)
2. Test GET /api/v1/configurations (list)
3. Test GET /api/v1/configurations/category/Academic
4. Test PUT /api/v1/configurations/1 (update)
5. Test DELETE /api/v1/configurations/1

**Success Criteria:**
- All operations return correct HTTP status codes
- Request/response payloads match expected JSON structure

**Test Class:** ConfigurationControllerTest.java
**Dependencies:** BE-035, BE-040

---

### [QA-035] Test Redis Caching - Student Cache Hit
**Type:** Integration Test
**Component:** Redis Cache

**Steps:**
1. Use TestContainers to start Redis
2. Call studentService.getStudentByStudentId() twice
3. Verify cache hit on second call

**Success Criteria:**
- First call: cache miss, DB query executed
- Second call: cache hit, NO DB query
- Same StudentResponse returned both times

**Test Class:** StudentCacheIntegrationTest.java
**Dependencies:** BE-023, BE-040

---

### [QA-036] Test Redis Caching - Cache Invalidation on Update
**Type:** Integration Test
**Component:** Redis Cache

**Steps:**
1. Cache student by calling getStudentByStudentId()
2. Update student via updateStudent()
3. Call getStudentByStudentId() again

**Success Criteria:**
- After update, cache is invalidated
- Third call results in cache miss and DB query
- Updated data is returned

**Test Class:** StudentCacheIntegrationTest.java
**Dependencies:** BE-023, BE-040

---

## Frontend Unit Tests

### [QA-037] Test Button Component - Click Handler
**Type:** Unit Test
**Component:** Button Component

**Steps:**
1. Render Button with onClick handler
2. Simulate click event
3. Verify handler is called

**Success Criteria:**
- onClick callback is invoked once
- Button renders with correct text

**Test File:** Button.test.tsx
**Dependencies:** FE-015, FE-061

---

### [QA-038] Test Button Component - Disabled State
**Type:** Unit Test
**Component:** Button Component

**Steps:**
1. Render Button with disabled={true}
2. Attempt to click button
3. Verify handler is NOT called

**Success Criteria:**
- Button has disabled attribute
- onClick is not invoked

**Test File:** Button.test.tsx
**Dependencies:** FE-015, FE-061

---

### [QA-039] Test Input Component - Value Change
**Type:** Unit Test
**Component:** Input Component

**Steps:**
1. Render Input
2. Simulate typing "test value"
3. Verify value updates

**Success Criteria:**
- Input value is "test value"
- onChange callback is invoked

**Test File:** Input.test.tsx
**Dependencies:** FE-016, FE-061

---

### [QA-040] Test Input Component - Error Display
**Type:** Unit Test
**Component:** Input Component

**Steps:**
1. Render Input with error="This field is required"
2. Verify error message is displayed

**Success Criteria:**
- Error text is visible
- Input has error styling (red border)

**Test File:** Input.test.tsx
**Dependencies:** FE-016, FE-061

---

### [QA-041] Test Modal Component - Open and Close
**Type:** Unit Test
**Component:** Modal Component

**Steps:**
1. Render Modal with isOpen={false}
2. Verify modal is not visible
3. Re-render with isOpen={true}
4. Verify modal is visible
5. Simulate backdrop click
6. Verify onClose is called

**Success Criteria:**
- Modal visibility toggles correctly
- Backdrop click triggers close
- ESC key triggers close

**Test File:** Modal.test.tsx
**Dependencies:** FE-018, FE-061

---

### [QA-042] Test StatusBadge Component - Color Mapping
**Type:** Unit Test
**Component:** StatusBadge Component

**Steps:**
1. Render StatusBadge with status="Active"
2. Verify green color classes applied
3. Re-render with status="Inactive"
4. Verify gray color classes applied

**Success Criteria:**
- Active: green background and text
- Inactive: gray background and text
- Graduated: blue background and text
- Transferred: yellow background and text

**Test File:** StatusBadge.test.tsx
**Dependencies:** FE-033, FE-061

---

### [QA-043] Test useDebounce Hook - Debounce Delay
**Type:** Unit Test
**Component:** useDebounce Hook

**Steps:**
1. Call useDebounce with value "test" and delay 500ms
2. Verify debounced value is initially undefined
3. Wait 500ms
4. Verify debounced value is "test"

**Success Criteria:**
- Value is debounced correctly
- Delay timing is accurate

**Test File:** useDebounce.test.ts
**Dependencies:** FE-054, FE-061

---

### [QA-044] Test usePagination Hook - Page Navigation
**Type:** Unit Test
**Component:** usePagination Hook

**Steps:**
1. Initialize usePagination with initial page 0
2. Call nextPage()
3. Verify currentPage is 1
4. Call prevPage()
5. Verify currentPage is 0

**Success Criteria:**
- Page navigation works correctly
- Boundary conditions are handled (no negative pages)

**Test File:** usePagination.test.ts
**Dependencies:** FE-055, FE-061

---

### [QA-045] Test Date Utility - Calculate Age
**Type:** Unit Test
**Component:** Date Utilities

**Steps:**
1. Call calculateAge("2015-05-15")
2. Verify age is calculated correctly (9 or 10 depending on current date)

**Success Criteria:**
- Age calculation is accurate
- Handles leap years correctly

**Test File:** dateUtils.test.ts
**Dependencies:** FE-056, FE-061

---

### [QA-046] Test Format Utility - Phone Number Formatting
**Type:** Unit Test
**Component:** Format Utilities

**Steps:**
1. Call formatPhoneNumber("+919876543210")
2. Verify formatted output (e.g., "+91 98765 43210")

**Success Criteria:**
- Phone number is formatted consistently
- Handles various input formats

**Test File:** formatters.test.ts
**Dependencies:** FE-057, FE-061

---

## Frontend Integration Tests with MSW

### [QA-047] Test Student Creation - Success Flow
**Type:** Integration Test
**Component:** Student Create Page + API

**Steps:**
1. Mock POST /api/v1/students to return 201 with student data
2. Render StudentCreatePage
3. Fill out form with valid data
4. Submit form
5. Wait for success notification

**Success Criteria:**
- Form validation passes
- API POST is called with correct data
- Success notification appears
- Navigation to student list occurs

**Test File:** StudentCreatePage.test.tsx
**Dependencies:** FE-047, FE-064

---

### [QA-048] Test Student Creation - Validation Errors
**Type:** Integration Test
**Component:** Student Create Page

**Steps:**
1. Render StudentCreatePage
2. Submit form without filling required fields
3. Verify validation errors appear

**Success Criteria:**
- Inline error messages display for required fields
- Form does NOT submit
- No API call is made

**Test File:** StudentCreatePage.test.tsx
**Dependencies:** FE-047, FE-064

---

### [QA-049] Test Student Creation - API Error (Duplicate Mobile)
**Type:** Integration Test
**Component:** Student Create Page + API

**Steps:**
1. Mock POST /api/v1/students to return 409 Conflict with Problem Details
2. Fill and submit form
3. Wait for error notification

**Success Criteria:**
- Error notification displays with message "mobile already exists"
- Form remains visible for correction
- User can retry

**Test File:** StudentCreatePage.test.tsx
**Dependencies:** FE-047, FE-064

---

### [QA-050] Test Student List - Load and Display
**Type:** Integration Test
**Component:** Student List Page + API

**Steps:**
1. Mock GET /api/v1/students to return page with 3 students
2. Render StudentListPage
3. Verify loading spinner appears briefly
4. Wait for students to render

**Success Criteria:**
- Loading spinner displays during fetch
- 3 student cards are rendered
- Student data is displayed correctly (name, ID, status)

**Test File:** StudentListPage.test.tsx
**Dependencies:** FE-046, FE-064

---

### [QA-051] Test Student List - Search Functionality
**Type:** Integration Test
**Component:** Student List Page + API

**Steps:**
1. Mock GET /api/v1/students?lastName=Kumar to return filtered results
2. Render StudentListPage
3. Type "Kumar" in search box
4. Wait for debounce and API call
5. Verify filtered results display

**Success Criteria:**
- Search input triggers API call after debounce (500ms)
- Filtered results are displayed
- Pagination updates accordingly

**Test File:** StudentListPage.test.tsx
**Dependencies:** FE-046, FE-064

---

### [QA-052] Test Student List - Pagination
**Type:** Integration Test
**Component:** Student List Page + API

**Steps:**
1. Mock GET /api/v1/students?page=0 to return page 0
2. Mock GET /api/v1/students?page=1 to return page 1
3. Render StudentListPage
4. Click "Next" button
5. Verify page 1 loads

**Success Criteria:**
- Pagination controls render
- Next button triggers API call for page 1
- Page 1 data displays correctly

**Test File:** StudentListPage.test.tsx
**Dependencies:** FE-046, FE-064

---

### [QA-053] Test Student Detail - Load and Display
**Type:** Integration Test
**Component:** Student Detail Page + API

**Steps:**
1. Mock GET /api/v1/students/1 to return student data
2. Render StudentDetailPage with id=1
3. Verify all student fields are displayed

**Success Criteria:**
- Student details render correctly
- All fields are visible (name, mobile, DOB, address, guardian info)
- Status badge displays correctly
- Action buttons (Edit, Delete) are visible

**Test File:** StudentDetailPage.test.tsx
**Dependencies:** FE-048, FE-064

---

### [QA-054] Test Student Edit - Load and Update
**Type:** Integration Test
**Component:** Student Edit Page + API

**Steps:**
1. Mock GET /api/v1/students/1 to return student
2. Mock PUT /api/v1/students/1 to return updated student
3. Render StudentEditPage with id=1
4. Wait for form to populate
5. Change firstName to "Updated Name"
6. Submit form
7. Wait for success

**Success Criteria:**
- Form pre-populates with existing data
- User can modify editable fields only
- PUT API is called with updated data
- Success notification appears
- Navigation to student detail occurs

**Test File:** StudentEditPage.test.tsx
**Dependencies:** FE-049, FE-064

---

### [QA-055] Test Student Delete - Confirmation Flow
**Type:** Integration Test
**Component:** Student Detail Page + API

**Steps:**
1. Mock GET /api/v1/students/1
2. Mock DELETE /api/v1/students/1 to return 204
3. Render StudentDetailPage
4. Click Delete button
5. Verify confirmation dialog appears
6. Click Confirm
7. Wait for success

**Success Criteria:**
- Confirmation dialog displays with warning message
- Cancel button closes dialog without delete
- Confirm button triggers DELETE API call
- Success notification appears
- Navigation to student list occurs

**Test File:** StudentDetailPage.test.tsx
**Dependencies:** FE-048, FE-064

---

### [QA-056] Test Configuration Management - CRUD Flow
**Type:** Integration Test
**Component:** Configuration Page + API

**Steps:**
1. Mock GET /api/v1/configurations/category/Academic
2. Mock POST /api/v1/configurations
3. Mock PUT /api/v1/configurations/1
4. Mock DELETE /api/v1/configurations/1
5. Test complete CRUD cycle

**Success Criteria:**
- Configurations load and display grouped by category
- Create modal opens and submits successfully
- Edit updates configuration
- Delete removes configuration

**Test File:** ConfigurationPage.test.tsx
**Dependencies:** FE-050, FE-064

---

## End-to-End Tests (E2E with Playwright)

### [QA-057] E2E - Complete Student Registration Journey
**Type:** End-to-End Test
**Scope:** Full user journey from home to registration

**Steps:**
1. Start backend services (student-service on 8081)
2. Start frontend (localhost:5173)
3. Open browser to home page
4. Click "Register New Student" button
5. Fill complete registration form:
   - First Name: "Rajesh"
   - Last Name: "Kumar"
   - Mobile: "+919876543210"
   - Date of Birth: "2015-05-15"
   - Address: "123 MG Road, Bangalore"
   - Father Name: "Suresh Kumar"
   - Mother Name: "Lakshmi Kumar"
   - Email: "rajesh@example.com"
6. Submit form
7. Verify success notification
8. Verify navigation to student detail page
9. Verify student data is displayed correctly

**Success Criteria:**
- All steps complete without errors
- Student is created in database
- Student ID is generated (e.g., STU-2025-00001)
- Status is Active
- All entered data is displayed correctly

**Test File:** student-registration.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-058] E2E - Student Search and View Details
**Type:** End-to-End Test
**Scope:** Search and navigation flow

**Steps:**
1. Pre-populate database with 5 students (3 with lastName "Kumar")
2. Navigate to Students page
3. Enter "Kumar" in search box
4. Wait for search results
5. Verify 3 results are displayed
6. Click on first student card
7. Verify student detail page loads
8. Verify all student information is displayed

**Success Criteria:**
- Search returns correct filtered results
- Student cards display summary info
- Detail page shows complete information
- Back navigation works correctly

**Test File:** student-search.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-059] E2E - Student Update Flow
**Type:** End-to-End Test
**Scope:** Update and optimistic locking

**Steps:**
1. Create student via API (GET version number)
2. Navigate to student detail page
3. Click Edit button
4. Change mobile number
5. Submit form
6. Verify success notification
7. Verify updated mobile on detail page

**Success Criteria:**
- Edit form pre-populates correctly
- Only editable fields are modifiable
- Update succeeds
- Updated data is persisted and displayed

**Test File:** student-update.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-060] E2E - Student Status Change
**Type:** End-to-End Test
**Scope:** Status update flow

**Steps:**
1. Create active student
2. Navigate to student detail page
3. Click "Change Status" button
4. Select "Inactive"
5. Confirm change
6. Verify status badge updates to Inactive

**Success Criteria:**
- Status change dialog displays current status
- Confirmation required before change
- Status badge color changes appropriately
- Status persists across page refresh

**Test File:** student-status-change.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-061] E2E - Student Delete with Soft Delete
**Type:** End-to-End Test
**Scope:** Soft delete verification

**Steps:**
1. Create active student via API
2. Navigate to student detail page
3. Click Delete button
4. Confirm deletion in dialog
5. Verify navigation to student list
6. Search for deleted student by ID
7. Verify student status is Inactive (NOT hard deleted)

**Success Criteria:**
- Confirmation dialog warns about deletion
- Success notification appears
- Student is NOT removed from database (check via API)
- Student status is changed to Inactive
- Student can still be retrieved by ID

**Test File:** student-delete.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-062] E2E - Pagination Through Large Student List
**Type:** End-to-End Test
**Scope:** Pagination and performance

**Steps:**
1. Pre-populate database with 55 students
2. Navigate to Students page (default 20 per page)
3. Verify first page shows 20 students
4. Click "Next" button
5. Verify second page shows 20 students
6. Click "Next" again
7. Verify third page shows 15 students
8. Click "Previous"
9. Verify navigation back to page 2

**Success Criteria:**
- Pagination controls display correctly (Page 1 of 3)
- Page size is respected (20 items)
- Next/Previous navigation works
- Last page shows correct remainder
- No duplicate students across pages

**Test File:** student-pagination.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-063] E2E - Business Rule Validation (Age Range)
**Type:** End-to-End Test
**Scope:** Drools rule enforcement

**Steps:**
1. Navigate to Register Student page
2. Fill form with date of birth: 2 years ago (age 2)
3. Submit form
4. Verify error notification displays: "Student age must be between 3 and 18 years"
5. Change date of birth to 10 years ago (age 10)
6. Submit form
7. Verify success

**Success Criteria:**
- Frontend validation catches age violation
- Backend Drools rule also validates
- Clear error message displayed
- User can correct and resubmit
- Valid age allows registration

**Test File:** student-age-validation.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-064] E2E - Duplicate Mobile Validation
**Type:** End-to-End Test
**Scope:** Unique constraint enforcement

**Steps:**
1. Create student with mobile "+919876543210"
2. Navigate to Register Student page
3. Fill form with same mobile number
4. Submit form
5. Verify error notification: "Student with mobile ... already exists"
6. Change mobile number
7. Submit form
8. Verify success

**Success Criteria:**
- Database unique constraint enforced
- 409 Conflict returned from API
- User-friendly error message displayed
- User can correct and retry
- Different mobile allows registration

**Test File:** student-duplicate-mobile.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-065] E2E - Configuration Management Flow
**Type:** End-to-End Test
**Scope:** Configuration CRUD

**Steps:**
1. Navigate to Configurations page
2. Verify existing configurations load (from seed data)
3. Click "Add Configuration"
4. Fill form:
   - Category: "Academic"
   - Key: "max_students_per_class"
   - Value: "35"
   - Description: "Maximum capacity per class"
5. Submit
6. Verify new config appears in Academic section
7. Click Edit on new config
8. Change Value to "40"
9. Submit
10. Verify updated value displays
11. Click Delete on config
12. Confirm deletion
13. Verify config is removed from list

**Success Criteria:**
- Configurations are grouped by category
- CRUD operations succeed
- Real-time updates without page refresh
- Validation prevents duplicate keys in same category

**Test File:** configuration-management.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-066] E2E - Responsive Design Verification
**Type:** End-to-End Test
**Scope:** Mobile and tablet viewports

**Steps:**
1. Set viewport to mobile (375x667)
2. Navigate through all pages
3. Verify layouts adapt correctly
4. Test hamburger menu (if present)
5. Test form inputs on mobile
6. Set viewport to tablet (768x1024)
7. Repeat navigation and interaction tests

**Success Criteria:**
- All pages render correctly on mobile
- Text is readable without zooming
- Buttons and inputs are tappable
- Tables scroll horizontally if needed
- Navigation menu adapts to mobile
- Forms are usable on small screens

**Test File:** responsive-design.spec.ts
**Dependencies:** FE-065

---

### [QA-067] E2E - Error Handling and Recovery
**Type:** End-to-End Test
**Scope:** Network errors and API failures

**Steps:**
1. Start frontend only (backend NOT running)
2. Navigate to Students page
3. Verify error message displays
4. Start backend
5. Click "Retry" button
6. Verify data loads successfully
7. Simulate 500 error from backend
8. Verify generic error message displays
9. Verify user can navigate away and return

**Success Criteria:**
- Network errors are caught gracefully
- User-friendly error messages (not stack traces)
- Retry mechanisms available
- Application doesn't crash
- Users can recover from errors

**Test File:** error-handling.spec.ts
**Dependencies:** BE-049, FE-065

---

### [QA-068] E2E - Session Timeout and Re-login
**Type:** End-to-End Test (Future)
**Scope:** Authentication flow (Phase 2)

**Steps:**
1. Login to application
2. Perform actions
3. Simulate session expiry
4. Attempt to create student
5. Verify redirect to login
6. Login again
7. Verify return to previous page
8. Verify can continue work

**Success Criteria:**
- Session timeout is detected
- User redirected to login
- After re-login, returns to intended page
- No data loss

**Note:** This test is for Phase 2 when authentication is implemented.

**Test File:** session-timeout.spec.ts
**Dependencies:** Future (Phase 2)

---

## Performance and Load Tests

### [QA-069] Performance - API Response Time
**Type:** Performance Test
**Component:** Student APIs

**Steps:**
1. Use JMeter or similar tool
2. Send 100 concurrent GET requests to /api/v1/students
3. Measure response times
4. Send 50 concurrent POST requests
5. Measure response times

**Success Criteria:**
- GET /students/{id} 95th percentile < 50ms (with cache)
- GET /students (list) 95th percentile < 150ms
- POST /students 95th percentile < 200ms
- No errors under load
- Cache hit rate > 80%

**Test Tool:** JMeter
**Dependencies:** BE-049

---

### [QA-070] Performance - Database Query Optimization
**Type:** Performance Test
**Component:** Database queries

**Steps:**
1. Populate database with 10,000 students
2. Execute search query by last name
3. Measure query execution time
4. Verify indexes are being used (EXPLAIN ANALYZE)

**Success Criteria:**
- Search queries < 50ms
- Index scans (not full table scans)
- Pagination queries optimized
- No N+1 query problems

**Test Tool:** PostgreSQL EXPLAIN ANALYZE
**Dependencies:** Database schema created

---

### [QA-071] Load - Concurrent User Simulation
**Type:** Load Test
**Component:** Full system

**Steps:**
1. Simulate 100 concurrent users
2. Each user performs:
   - Browse students (GET requests)
   - Search students (GET with filters)
   - View student details (GET by ID)
   - Register new student (POST)
3. Run for 10 minutes
4. Monitor system resources (CPU, memory, DB connections)

**Success Criteria:**
- All requests succeed (< 1% error rate)
- Average response time < 500ms
- No memory leaks
- Database connection pool healthy
- Redis cache functioning

**Test Tool:** JMeter, Gatling, or K6
**Dependencies:** BE-049, FE-065

---

## Summary

Total QA Tasks: 71
- Backend Unit Tests: 20 tasks (QA-001 to QA-020)
- Backend Integration Tests: 16 tasks (QA-021 to QA-036)
- Frontend Unit Tests: 10 tasks (QA-037 to QA-046)
- Frontend Integration Tests: 10 tasks (QA-047 to QA-056)
- End-to-End Tests: 12 tasks (QA-057 to QA-068)
- Performance/Load Tests: 3 tasks (QA-069 to QA-071)

**Test Coverage Targets:**
- Backend: 80%+ line coverage, 90%+ for domain layer
- Frontend: 70%+ line coverage for components
- E2E: All critical user journeys covered

**Tools Required:**
- JUnit 5, Mockito, AssertJ (Backend)
- TestContainers (PostgreSQL, Redis)
- Vitest, React Testing Library (Frontend)
- Mock Service Worker (API mocking)
- Playwright (E2E testing)
- JMeter or K6 (Performance testing)

**Estimated Effort:** 3-4 weeks for comprehensive test coverage

**Key Focus Areas:**
- Business rule validation (age, mobile uniqueness)
- Data integrity constraints
- API contract compliance (RFC 7807)
- Optimistic locking behavior
- Cache invalidation correctness
- Responsive design verification
- Error handling and recovery
- Performance under load
