# QA Test Plan
**School Management System - QA Engineer**

**Version**: 1.0
**Date**: 2026-01-08
**Execution Model**: Waterfall / Single-Pass Implementation

---

## Overview

This document provides a **comprehensive test plan** for validating the School Management System. Tests are organized by type (Unit, Integration, E2E) and cover all critical functionality across Student Service, Configuration Service, and Frontend application.

**Testing Pyramid**:
- **60% Unit Tests**: Fast, isolated, focused on business logic
- **30% Integration Tests**: API contracts, database interactions
- **10% E2E Tests**: Critical user journeys through UI

**Quality Gates**:
- Code Coverage: >80% (Backend), >70% (Frontend)
- All tests must pass before deployment
- No critical or high-severity bugs in production

---

## Backend Unit Tests - Student Service

### [QA-001] Student Domain Model - Valid Creation
**Type**: Unit Test
**Component**: Student.java

**Steps**:
1. Create Student using factory method `Student.register(...)`
2. Provide valid data (age 10, all required fields)
3. Verify student object created successfully
4. Assert firstName, lastName, age, status match input

**Success Criteria**:
- Student object is not null
- All fields populated correctly
- Status defaults to ACTIVE
- createdAt and updatedAt are set

---

### [QA-002] Student Domain Model - Age Validation
**Type**: Unit Test (Parameterized)
**Component**: Student.java

**Steps**:
1. Attempt to create students with ages: 2, 19, 25 years
2. Each should throw InvalidAgeException
3. Verify exception message contains "outside allowed range"

**Success Criteria**:
- InvalidAgeException thrown for age < 3
- InvalidAgeException thrown for age > 18
- Exception messages are descriptive

---

### [QA-003] Student Domain Model - Age Calculation
**Type**: Unit Test
**Component**: Student.java

**Steps**:
1. Create student with DOB = 2014-05-15
2. Call `getAge()` method
3. Verify age is calculated correctly based on current date

**Success Criteria**:
- Age is between expected range (9-12 years depending on current date)
- Age updates dynamically

---

### [QA-004] Student Domain Model - Profile Update
**Type**: Unit Test
**Component**: Student.java

**Steps**:
1. Create valid student
2. Call `updateProfile("Jonathan", null, "8888777766")`
3. Verify firstName updated to "Jonathan"
4. Verify phone updated to "8888777766"
5. Verify lastName unchanged (passed null)

**Success Criteria**:
- Only specified fields are updated
- Null values leave existing data unchanged
- updatedAt timestamp is refreshed

---

### [QA-005] Student Domain Model - Status Change
**Type**: Unit Test
**Component**: Student.java

**Steps**:
1. Create student (default status: ACTIVE)
2. Call `deactivate()`
3. Assert status is INACTIVE
4. Call `activate()`
5. Assert status is ACTIVE

**Success Criteria**:
- Status transitions correctly
- updatedAt timestamp refreshed on each change

---

### [QA-006] StudentApplicationService - Create Student Success
**Type**: Unit Test (Mocked Dependencies)
**Component**: StudentApplicationService.java

**Steps**:
1. Mock StudentRepository to return false for all existence checks
2. Mock StudentRepository.save to return student with generated ID
3. Call `createStudent(validRequest)`
4. Verify repository.save called once
5. Verify response DTO returned with student ID

**Success Criteria**:
- Student created successfully
- ID generated (STU-2026-NNNNN format)
- Response DTO contains all fields

---

### [QA-007] StudentApplicationService - Duplicate Phone
**Type**: Unit Test (Mocked Dependencies)
**Component**: StudentApplicationService.java

**Steps**:
1. Mock StudentRepository.existsByPhone to return true
2. Call `createStudent(requestWithDuplicatePhone)`
3. Expect DuplicatePhoneException thrown
4. Verify repository.save NOT called

**Success Criteria**:
- DuplicatePhoneException thrown
- Exception message includes phone number
- No student created

---

### [QA-008] StudentApplicationService - Update Student Success
**Type**: Unit Test
**Component**: StudentApplicationService.java

**Steps**:
1. Mock repository to return existing student
2. Mock existsByPhone to return false (phone available)
3. Call `updateStudent(studentId, updateRequest)`
4. Verify student.updateProfile called
5. Verify repository.save called
6. Verify response DTO returned

**Success Criteria**:
- Student updated successfully
- Only editable fields changed
- Response reflects updates

---

### [QA-009] StudentApplicationService - Update Immutable Field
**Type**: Unit Test
**Component**: StudentApplicationService.java

**Steps**:
1. Create UpdateStudentRequest with dateOfBirth field (immutable)
2. Call `updateStudent(studentId, request)`
3. Expect ImmutableFieldException thrown

**Success Criteria**:
- Exception thrown for immutable field
- No update persisted

---

### [QA-010] StudentApplicationService - Delete Non-Existent Student
**Type**: Unit Test
**Component**: StudentApplicationService.java

**Steps**:
1. Mock repository.findById to return Optional.empty()
2. Call `deleteStudent("STU-2026-99999")`
3. Expect StudentNotFoundException thrown

**Success Criteria**:
- StudentNotFoundException thrown
- Exception message includes student ID

---

## Backend Unit Tests - Configuration Service

### [QA-011] Configuration Domain Model - Valid Creation
**Type**: Unit Test
**Component**: Configuration.java

**Steps**:
1. Create Configuration using factory method
2. Provide valid category, key, value, description
3. Verify configuration created
4. Assert all fields populated

**Success Criteria**:
- Configuration object not null
- Fields match input

---

### [QA-012] Configuration Domain Model - Update Value
**Type**: Unit Test
**Component**: Configuration.java

**Steps**:
1. Create configuration with value="500"
2. Call `updateValue("600")`
3. Verify value changed to "600"
4. Verify lastUpdated refreshed

**Success Criteria**:
- Value updated
- Timestamp refreshed

---

### [QA-013] ConfigurationApplicationService - Create Configuration Success
**Type**: Unit Test
**Component**: ConfigurationApplicationService.java

**Steps**:
1. Mock repository.existsByCategoryAndKey to return false
2. Mock repository.save to return saved config
3. Call `createConfiguration(validRequest)`
4. Verify save called
5. Verify response DTO returned

**Success Criteria**:
- Configuration created
- Response contains all fields

---

### [QA-014] ConfigurationApplicationService - Duplicate Key
**Type**: Unit Test
**Component**: ConfigurationApplicationService.java

**Steps**:
1. Mock existsByCategoryAndKey to return true
2. Call `createConfiguration(request)`
3. Expect DuplicateConfigKeyException thrown

**Success Criteria**:
- Exception thrown
- No configuration created

---

### [QA-015] ConfigurationApplicationService - Update Configuration
**Type**: Unit Test
**Component**: ConfigurationApplicationService.java

**Steps**:
1. Mock repository to return existing configuration
2. Call `updateConfiguration(id, updateRequest)`
3. Verify config.updateValue called
4. Verify save called
5. Verify response returned

**Success Criteria**:
- Configuration updated
- Response reflects changes

---

## Backend Integration Tests - Student Service

### [QA-016] Student API - Create Student (201 Created)
**Type**: Integration Test
**Component**: StudentController + Full Stack

**Steps**:
1. Start Student Service with TestContainers PostgreSQL
2. POST /api/v1/students with valid CreateStudentRequest
3. Assert response status: 201 CREATED
4. Assert response body contains student with generated ID
5. Assert Location header present

**Success Criteria**:
- HTTP 201 status
- Student ID in format STU-YYYY-NNNNN
- Student persisted in database
- Location header: /api/v1/students/{id}

---

### [QA-017] Student API - Create Student with Invalid Age (422 Unprocessable)
**Type**: Integration Test
**Component**: StudentController + Validation

**Steps**:
1. POST /api/v1/students with DOB yielding age=2
2. Assert response status: 422 UNPROCESSABLE_ENTITY
3. Assert response body contains ProblemDetail
4. Assert detail mentions age violation

**Success Criteria**:
- HTTP 422 status
- RFC 7807 ProblemDetail structure
- Error message: "Age must be between 3 and 18"

---

### [QA-018] Student API - Create Student with Duplicate Phone (409 Conflict)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student with phone="9876543210"
2. Attempt to create another student with same phone
3. Assert response status: 409 CONFLICT
4. Assert error message mentions duplicate phone

**Success Criteria**:
- HTTP 409 status
- ProblemDetail with type: duplicate-resource
- Second student NOT created

---

### [QA-019] Student API - Get Student by ID (200 OK)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student via POST (capture ID)
2. GET /api/v1/students/{id}
3. Assert response status: 200 OK
4. Assert response body matches created student

**Success Criteria**:
- HTTP 200 status
- All fields returned correctly
- Age calculated correctly

---

### [QA-020] Student API - Get Non-Existent Student (404 Not Found)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. GET /api/v1/students/STU-2026-99999 (non-existent)
2. Assert response status: 404 NOT_FOUND
3. Assert ProblemDetail returned

**Success Criteria**:
- HTTP 404 status
- Error message: "Student with ID 'STU-2026-99999' not found"

---

### [QA-021] Student API - Update Student (200 OK)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student
2. PATCH /api/v1/students/{id} with UpdateStudentRequest (firstName="Jonathan", phone="8888777766")
3. Assert response status: 200 OK
4. Assert response body reflects updates
5. Verify database updated

**Success Criteria**:
- HTTP 200 status
- firstName updated to "Jonathan"
- phone updated to "8888777766"
- updatedAt timestamp refreshed

---

### [QA-022] Student API - Update with Duplicate Phone (409 Conflict)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student A with phone="1111111111"
2. Create student B with phone="2222222222"
3. PATCH student B to change phone to "1111111111"
4. Assert response status: 409 CONFLICT

**Success Criteria**:
- HTTP 409 status
- Error message mentions duplicate phone
- Student B phone unchanged

---

### [QA-023] Student API - Delete Student (204 No Content)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student
2. DELETE /api/v1/students/{id}
3. Assert response status: 204 NO_CONTENT
4. Verify student deleted from database
5. GET same student, expect 404

**Success Criteria**:
- HTTP 204 status
- Empty response body
- Student no longer exists

---

### [QA-024] Student API - List All Students (200 OK)
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create 3 students (2 ACTIVE, 1 INACTIVE)
2. GET /api/v1/students
3. Assert response status: 200 OK
4. Assert students array contains 3 items
5. Assert totalCount=3, activeCount=2, inactiveCount=1

**Success Criteria**:
- HTTP 200 status
- StudentListResponse structure
- Counts match

---

### [QA-025] Student API - List Students with Status Filter
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create 2 ACTIVE and 1 INACTIVE student
2. GET /api/v1/students?status=ACTIVE
3. Assert response contains only 2 students
4. Assert both are ACTIVE

**Success Criteria**:
- HTTP 200 status
- Only ACTIVE students returned
- INACTIVE student excluded

---

### [QA-026] Student API - Search Students by Name
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create students: John Doe, Jane Smith
2. GET /api/v1/students?search=John
3. Assert response contains only John Doe
4. Assert Jane Smith excluded

**Success Criteria**:
- HTTP 200 status
- Search matches firstName, lastName, guardianName
- Correct student returned

---

### [QA-027] Student API - Get Statistics
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create 5 ACTIVE and 2 INACTIVE students
2. GET /api/v1/students/statistics
3. Assert response: totalStudents=7, activeStudents=5, inactiveStudents=2

**Success Criteria**:
- HTTP 200 status
- Counts accurate

---

### [QA-028] Student API - Validate Phone Uniqueness
**Type**: Integration Test
**Component**: StudentController

**Steps**:
1. Create student with phone="9876543210"
2. POST /api/v1/students/validate-phone with body: { phone: "9876543210" }
3. Assert response: { isUnique: false }
4. POST with phone: "1111111111" (unused)
5. Assert response: { isUnique: true }

**Success Criteria**:
- Correctly identifies duplicate
- Correctly identifies available phone

---

## Backend Integration Tests - Configuration Service

### [QA-029] Configuration API - Create Configuration (201 Created)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. POST /api/v1/configurations with valid CreateConfigurationRequest
2. Assert response status: 201 CREATED
3. Assert response body contains configuration with ID
4. Verify persisted in database

**Success Criteria**:
- HTTP 201 status
- Configuration ID generated
- Location header present

---

### [QA-030] Configuration API - Create Duplicate Key (409 Conflict)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create configuration: category=GENERAL, key=SCHOOL_NAME
2. Attempt to create another with same category and key
3. Assert response status: 409 CONFLICT

**Success Criteria**:
- HTTP 409 status
- Error message mentions duplicate key

---

### [QA-031] Configuration API - Get Configuration by ID (200 OK)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create configuration
2. GET /api/v1/configurations/{id}
3. Assert response status: 200 OK
4. Assert all fields returned

**Success Criteria**:
- HTTP 200 status
- Data matches created config

---

### [QA-032] Configuration API - Update Configuration (200 OK)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create configuration with value="500"
2. PATCH /api/v1/configurations/{id} with value="600"
3. Assert response status: 200 OK
4. Assert value updated to "600"
5. Verify database updated

**Success Criteria**:
- HTTP 200 status
- Value updated
- lastUpdated timestamp refreshed

---

### [QA-033] Configuration API - Delete Configuration (204 No Content)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create configuration
2. DELETE /api/v1/configurations/{id}
3. Assert response status: 204 NO_CONTENT
4. Verify deleted from database

**Success Criteria**:
- HTTP 204 status
- Configuration removed

---

### [QA-034] Configuration API - List All Configurations (200 OK)
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create 3 configurations (2 GENERAL, 1 ACADEMIC)
2. GET /api/v1/configurations
3. Assert response contains 3 configurations
4. Assert totalCount=3

**Success Criteria**:
- HTTP 200 status
- All configurations returned

---

### [QA-035] Configuration API - Filter by Category
**Type**: Integration Test
**Component**: ConfigurationController

**Steps**:
1. Create 2 GENERAL and 1 ACADEMIC config
2. GET /api/v1/configurations?category=GENERAL
3. Assert response contains only 2 GENERAL configs

**Success Criteria**:
- HTTP 200 status
- Category filter applied correctly

---

## Frontend Unit Tests

### [QA-036] Student Service - getAll Method
**Type**: Unit Test (Frontend)
**Component**: src/services/studentService.ts

**Steps**:
1. Mock axios.get to return mock StudentListResponse
2. Call `studentService.getAll()`
3. Assert axios.get called with correct URL
4. Assert response data returned

**Success Criteria**:
- Correct API endpoint called
- Data extracted from AxiosResponse

---

### [QA-037] Student Service - create Method
**Type**: Unit Test (Frontend)
**Component**: src/services/studentService.ts

**Steps**:
1. Mock axios.post to return created student
2. Call `studentService.create(mockRequest)`
3. Assert POST /api/v1/students called
4. Assert request body passed correctly

**Success Criteria**:
- Correct endpoint and method
- Request body serialized to JSON

---

### [QA-038] useStudents Hook - Fetch Students on Mount
**Type**: Unit Test (Frontend)
**Component**: src/hooks/useStudents.ts

**Steps**:
1. Mock studentService.getAll
2. Render component using useStudents hook
3. Verify service.getAll called on mount
4. Verify students state populated

**Success Criteria**:
- API called automatically
- Loading state transitions: true -> false
- Students array populated

---

### [QA-039] useStudents Hook - Create Student
**Type**: Unit Test (Frontend)
**Component**: src/hooks/useStudents.ts

**Steps**:
1. Mock studentService.create
2. Call createStudent method from hook
3. Verify service.create called
4. Verify students list refreshed (refetch)

**Success Criteria**:
- Create method calls service
- Success triggers refetch

---

### [QA-040] useDebounce Hook - Debounce Input
**Type**: Unit Test (Frontend)
**Component**: src/hooks/useDebounce.ts

**Steps**:
1. Use hook with value and delay=500ms
2. Change value rapidly 3 times
3. Wait 500ms
4. Assert debounced value updated only once

**Success Criteria**:
- Value debounced correctly
- Only final value emitted after delay

---

## End-to-End Tests

### [QA-041] E2E - Complete Student Registration Flow
**Type**: E2E Test
**Component**: Full Application (Frontend + Backend)

**Steps**:
1. Navigate to http://localhost:5173/students
2. Click "Register New Student" button
3. Fill in all required fields in StudentDialog
4. Submit form
5. Verify success toast displayed
6. Verify new student appears in student list
7. Verify student card shows correct data

**Success Criteria**:
- Dialog opens on button click
- Form validation works (required fields)
- API call succeeds (check Network tab)
- Toast notification shown
- Student list refreshed automatically
- New student visible with correct data

---

### [QA-042] E2E - Student Search and Filter
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Navigate to /students
2. Create 2 students: John Doe (ACTIVE), Jane Smith (INACTIVE)
3. Enter "John" in search box
4. Verify only John Doe displayed
5. Clear search, select status filter: ACTIVE
6. Verify only John Doe displayed
7. Select status: INACTIVE
8. Verify only Jane Smith displayed

**Success Criteria**:
- Search debounced (wait 300ms before API call)
- Filters applied correctly
- API called with correct query params
- Results update dynamically

---

### [QA-043] E2E - Edit Student Information
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Create student: John Doe, phone=1234567890
2. Click "Edit" button on student card
3. Change firstName to "Jonathan"
4. Change phone to "9999888877"
5. Submit form
6. Verify success toast
7. Verify student card updated with new data
8. Refresh page, verify changes persisted

**Success Criteria**:
- Edit dialog pre-populated with existing data
- Editable fields: firstName, lastName, phone, status
- Immutable fields: dateOfBirth, email, adhaarNumber (grayed out or hidden)
- Update API called (PATCH)
- UI reflects changes immediately

---

### [QA-044] E2E - View Student Details
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Create student with all fields populated
2. Click "View Details" button
3. Verify ViewStudentDialog opens
4. Verify all fields displayed correctly
5. Verify read-only (no edit capability)
6. Close dialog

**Success Criteria**:
- Dialog displays all student fields
- Dates formatted correctly
- Status badge styled (ACTIVE=green, INACTIVE=gray)
- No edit controls shown

---

### [QA-045] E2E - Delete Student with Confirmation
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Create student
2. Click "Delete" button
3. Verify ConfirmDialog opens
4. Click "Cancel"
5. Verify student NOT deleted
6. Click "Delete" again
7. Click "Confirm"
8. Verify success toast
9. Verify student removed from list
10. Verify student no longer in database (GET returns 404)

**Success Criteria**:
- Confirmation dialog prevents accidental deletion
- Cancel works (no deletion)
- Confirm triggers DELETE API call
- UI updates (student removed)
- Database updated

---

### [QA-046] E2E - Duplicate Phone Validation
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Create student A with phone="9876543210"
2. Attempt to create student B with same phone
3. Verify async validation error displayed under phone field
4. Verify form cannot be submitted
5. Change phone to unique number
6. Verify validation error clears
7. Submit successfully

**Success Criteria**:
- Async validation calls validatePhone API
- Error message: "Phone number already registered"
- Submit button disabled during validation
- Real-time feedback

---

### [QA-047] E2E - Configuration Management Flow
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Navigate to /configurations
2. Click "Add Configuration" button
3. Fill form: category=ACADEMIC, key=MAX_CLASS_SIZE, value=40
4. Submit
5. Verify configuration appears in table
6. Click "Edit" button
7. Change value to 50
8. Submit
9. Verify updated in table
10. Click "Delete", confirm
11. Verify removed from table

**Success Criteria**:
- Full CRUD workflow functional
- Category filter works
- Table displays all fields
- Edit mode: category and key readonly
- Delete confirmation required

---

### [QA-048] E2E - Navigation and Routing
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Navigate to homepage (/)
2. Verify statistics cards displayed
3. Click "Students" in header nav
4. Verify URL changed to /students
5. Verify StudentsPage rendered
6. Click "Configurations" in header
7. Verify URL changed to /configurations
8. Click "Home" in header
9. Verify URL changed to /
10. Navigate to invalid URL /invalid
11. Verify redirected to /

**Success Criteria**:
- All routes functional
- Active link highlighted in nav
- Invalid routes redirect to home
- Browser back/forward works

---

### [QA-049] E2E - Error Handling and Recovery
**Type**: E2E Test
**Component**: Full Application

**Steps**:
1. Stop backend services
2. Navigate to /students
3. Verify error message displayed (cannot fetch students)
4. Attempt to create student
5. Verify error toast shown
6. Start backend services
7. Click retry or refresh
8. Verify application recovers, data loads

**Success Criteria**:
- Graceful error handling (no white screen)
- User-friendly error messages
- Retry mechanism works
- Application recovers when backend available

---

### [QA-050] E2E - Responsive Design Verification
**Type**: E2E Test (Manual)
**Component**: Full Application

**Steps**:
1. Open app in Chrome DevTools Device Mode
2. Test on iPhone SE (375px width)
   - Verify header mobile menu works
   - Verify student cards stack vertically
   - Verify forms usable
   - Verify dialogs fit screen
3. Test on iPad (768px width)
   - Verify 2-column grid for students
   - Verify tables scroll horizontally
4. Test on Desktop (1920px width)
   - Verify 3-column grid for students
   - Verify all content visible

**Success Criteria**:
- Mobile: Single column, hamburger menu, touch-friendly
- Tablet: 2 columns, readable fonts
- Desktop: 3 columns, optimal spacing
- No horizontal scroll (except tables)
- Buttons and inputs appropriately sized

---

## Performance and Load Tests

### [QA-051] Performance - Student API Response Time
**Type**: Performance Test
**Component**: Backend API

**Steps**:
1. Use Apache JMeter or similar tool
2. Send 100 concurrent GET /api/v1/students requests
3. Measure response times

**Success Criteria**:
- 95th percentile: <200ms
- 99th percentile: <500ms
- 0% error rate

---

### [QA-052] Performance - Frontend Initial Load Time
**Type**: Performance Test
**Component**: Frontend

**Steps**:
1. Open Chrome DevTools, Lighthouse tab
2. Run audit on homepage
3. Check Performance score
4. Check First Contentful Paint (FCP)
5. Check Time to Interactive (TTI)

**Success Criteria**:
- Performance score: >90
- FCP: <1.5s
- TTI: <3s
- No console errors

---

### [QA-053] Load Test - Create 1000 Students
**Type**: Load Test
**Component**: Backend

**Steps**:
1. Use JMeter to POST 1000 students
2. 50 concurrent threads
3. Verify all created successfully
4. Check database for 1000 records

**Success Criteria**:
- All requests succeed (201 status)
- No duplicate IDs generated
- Database integrity maintained

---

## Security Tests

### [QA-054] Security - SQL Injection Prevention
**Type**: Security Test
**Component**: Backend API

**Steps**:
1. POST /api/v1/students with malicious firstName: `'; DROP TABLE students; --`
2. Verify request fails validation or safely handled
3. Verify database intact

**Success Criteria**:
- No SQL injection possible
- Parameterized queries used

---

### [QA-055] Security - XSS Prevention
**Type**: Security Test
**Component**: Frontend

**Steps**:
1. Create student with firstName: `<script>alert('XSS')</script>`
2. View student in UI
3. Verify script NOT executed
4. Verify displayed as plain text

**Success Criteria**:
- No JavaScript execution
- React escapes HTML by default

---

### [QA-056] Security - CORS Configuration
**Type**: Security Test
**Component**: Backend

**Steps**:
1. Attempt API call from unauthorized origin (e.g., http://evil.com)
2. Verify request blocked by CORS
3. Verify allowed origin (http://localhost:5173) works

**Success Criteria**:
- Only whitelisted origins allowed
- Preflight OPTIONS requests handled

---

## Accessibility Tests

### [QA-057] Accessibility - Keyboard Navigation
**Type**: Accessibility Test
**Component**: Frontend

**Steps**:
1. Navigate to /students using only keyboard
2. Tab through all interactive elements
3. Open dialog using Enter/Space
4. Close dialog using Escape
5. Submit form using Enter

**Success Criteria**:
- All elements reachable via Tab
- Focus visible (outline)
- Enter/Space activate buttons
- Escape closes dialogs

---

### [QA-058] Accessibility - Screen Reader Support
**Type**: Accessibility Test
**Component**: Frontend

**Steps**:
1. Use NVDA or JAWS screen reader
2. Navigate to students page
3. Verify all labels announced
4. Verify form errors announced
5. Verify buttons have descriptive labels

**Success Criteria**:
- All content readable
- ARIA labels present
- Form validation errors announced
- Button purpose clear

---

### [QA-059] Accessibility - Color Contrast
**Type**: Accessibility Test
**Component**: Frontend

**Steps**:
1. Use browser DevTools Accessibility panel
2. Check all text elements for contrast ratio
3. Verify buttons, links, form fields have sufficient contrast

**Success Criteria**:
- WCAG AA compliance (4.5:1 for normal text)
- Status badges readable

---

## Cross-Browser Compatibility Tests

### [QA-060] Cross-Browser - Chrome
**Type**: Compatibility Test
**Component**: Frontend

**Steps**:
1. Test full application in Chrome (latest)
2. Verify all features work

**Success Criteria**:
- No layout issues
- All interactions functional

---

### [QA-061] Cross-Browser - Firefox
**Type**: Compatibility Test
**Component**: Frontend

**Steps**:
1. Test full application in Firefox (latest)
2. Verify all features work

**Success Criteria**:
- Consistent with Chrome
- No browser-specific bugs

---

### [QA-062] Cross-Browser - Safari
**Type**: Compatibility Test
**Component**: Frontend

**Steps**:
1. Test on Safari (macOS or iOS)
2. Verify features work

**Success Criteria**:
- Consistent rendering
- Date pickers work

---

### [QA-063] Cross-Browser - Edge
**Type**: Compatibility Test
**Component**: Frontend

**Steps**:
1. Test on Edge (latest)
2. Verify features work

**Success Criteria**:
- No compatibility issues

---

## Data Integrity Tests

### [QA-064] Data Integrity - Optimistic Locking
**Type**: Integration Test
**Component**: Backend

**Steps**:
1. Fetch student (version=0)
2. In parallel, two requests PATCH same student
3. First request succeeds (version incremented to 1)
4. Second request fails (OptimisticLockingFailureException)
5. Verify error response (409 Conflict)

**Success Criteria**:
- Concurrent updates detected
- One update succeeds, other fails
- User notified to retry

---

### [QA-065] Data Integrity - Cascade Delete
**Type**: Integration Test (Future)
**Component**: Backend

**Steps**:
1. When enrollments added (future feature):
2. Delete student
3. Verify related enrollments deleted (if cascade configured)

**Success Criteria**:
- No orphaned records
- Referential integrity maintained

---

### [QA-066] Data Integrity - Unique Constraints
**Type**: Integration Test
**Component**: Backend

**Steps**:
1. Create student with phone="1111111111"
2. Attempt to create another with same phone (bypass validation)
3. Verify database constraint enforced
4. Verify error handled gracefully

**Success Criteria**:
- Database constraints enforced
- Error mapped to 409 Conflict

---

## Regression Tests

### [QA-067] Regression - Existing Students Unaffected by New Create
**Type**: Regression Test
**Component**: Full Stack

**Steps**:
1. Create student A
2. Create student B
3. Verify student A unchanged

**Success Criteria**:
- Student A data intact
- No side effects

---

### [QA-068] Regression - Statistics Accuracy After CRUD
**Type**: Regression Test
**Component**: Backend

**Steps**:
1. GET /api/v1/students/statistics (note counts)
2. Create 2 students
3. GET statistics again
4. Verify totalStudents increased by 2
5. Delete 1 student
6. Verify totalStudents decreased by 1

**Success Criteria**:
- Statistics always accurate
- Counts update in real-time

---

### [QA-069] Regression - Configuration Changes Don't Affect Students
**Type**: Regression Test
**Component**: Full Stack

**Steps**:
1. Create students
2. Update configuration (e.g., change MAX_AGE)
3. Verify existing students unaffected
4. New students validated against new config (future)

**Success Criteria**:
- Existing data grandfathered
- New rules apply prospectively

---

### [QA-070] Regression - All Tests Pass After Code Changes
**Type**: Regression Test Suite
**Component**: All

**Steps**:
1. After any code change (bug fix, feature):
2. Run full test suite (Unit + Integration + E2E)
3. Verify all tests pass

**Success Criteria**:
- 100% pass rate
- No new bugs introduced

---

## Summary

**Total Test Scenarios**: 70
**Distribution**:
- Unit Tests (Backend): 15 scenarios (QA-001 to QA-015)
- Integration Tests (Backend): 21 scenarios (QA-016 to QA-036)
- Unit Tests (Frontend): 5 scenarios (QA-036 to QA-040)
- E2E Tests: 10 scenarios (QA-041 to QA-050)
- Performance/Load Tests: 3 scenarios (QA-051 to QA-053)
- Security Tests: 3 scenarios (QA-054 to QA-056)
- Accessibility Tests: 3 scenarios (QA-057 to QA-059)
- Cross-Browser Tests: 4 scenarios (QA-060 to QA-063)
- Data Integrity Tests: 3 scenarios (QA-064 to QA-066)
- Regression Tests: 4 scenarios (QA-067 to QA-070)

**Estimated Testing Effort**: 8-10 QA days (parallel with development)

**Tools Required**:
- JUnit 5, Mockito, AssertJ (Backend Unit/Integration)
- TestContainers (Docker-based integration tests)
- Vitest or Jest (Frontend Unit)
- Playwright or Cypress (E2E)
- JMeter (Performance/Load)
- Lighthouse (Performance audit)
- NVDA/JAWS (Screen reader)
- Chrome DevTools (Accessibility)

**Quality Gates**:
- All Unit Tests must pass before Integration Tests
- All Integration Tests must pass before E2E Tests
- All E2E Tests must pass before deployment
- Code Coverage: >80% (Backend), >70% (Frontend)
- Performance: 95th percentile <200ms
- Accessibility: WCAG AA compliance
- Security: No critical vulnerabilities

**Next Phase**: Execute tests during development, track defects, generate test reports.
