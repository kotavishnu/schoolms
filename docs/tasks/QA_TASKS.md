# QA Test Plan - School Management System

**Version:** 1.0
**Date:** 2026-01-22
**Target Agent:** QA Engineer
**Execution Model:** Waterfall / Single-Pass Testing

---

## Test Coverage Targets

### Backend QA
- **Domain Layer:** 95% code coverage
- **Application Layer:** 85% code coverage
- **Infrastructure Layer:** 70% code coverage

### Frontend QA
- **Component Coverage:** 80%
- **E2E Coverage:** All critical user flows
- **Screenshot Validation:** 100% pixel-perfect match

### Performance Targets
- **API Response Time:** p95 < 200ms
- **Frontend Load Time:** < 2 seconds
- **Database Query Time:** < 50ms average

---

## Backend QA Tasks

### [QA-BE-001] Unit Testing - Student Domain Layer

**Type:** Unit Test
**Coverage Target:** 95%

**Test Scenarios:**

1. **Student Entity - Business Logic**
   - Test: `generateStudentId()` creates format STD-YYYYMMDD-NNNN
   - Test: `activate()` sets status to ACTIVE
   - Test: `deactivate()` sets status to INACTIVE
   - Test: `isActive()` returns correct boolean

2. **StudentStatus Enum**
   - Test: All enum values (ACTIVE, INACTIVE)
   - Test: String conversion

**Success Criteria:**
- All entity business methods tested
- 95% line coverage achieved
- All tests pass

**Dependencies:** BE-003

---

### [QA-BE-002] Unit Testing - Drools Business Rules

**Type:** Unit Test
**Coverage Target:** 100% of business rules

**Test Scenarios:**

1. **BR-STU-001: Age Range Check**
   - Test: Reject student with age < 3
   - Test: Reject student with age > 18
   - Test: Accept student with age = 3
   - Test: Accept student with age = 18
   - Test: Accept student with age = 10

2. **BR-STU-003: Email Format Validation**
   - Test: Reject invalid email (no @)
   - Test: Reject invalid email (no domain)
   - Test: Reject invalid email (spaces)
   - Test: Accept valid email

3. **BR-STU-004: Aadhaar Format Validation**
   - Test: Reject Aadhaar < 12 digits
   - Test: Reject Aadhaar > 12 digits
   - Test: Reject Aadhaar with letters
   - Test: Accept valid 12-digit Aadhaar

4. **BR-STU-005: Name Pattern Validation**
   - Test: Reject firstName with numbers
   - Test: Reject lastName with special characters
   - Test: Accept names with spaces
   - Test: Accept names with letters only

5. **BR-STU-006: Mobile Format Validation**
   - Test: Reject mobile < 10 digits
   - Test: Reject mobile > 10 digits
   - Test: Reject mobile with letters
   - Test: Accept valid 10-digit mobile

**Success Criteria:**
- All 7 business rules tested (BR-STU-001 to BR-STU-007)
- 100% rule coverage
- All tests pass

**Dependencies:** BE-006, BE-023

---

### [QA-BE-003] Unit Testing - Student Service Layer

**Type:** Unit Test
**Coverage Target:** 85%

**Test Scenarios:**

1. **registerStudent()**
   - Test: Success with valid data
   - Test: Validation failure (age < 3)
   - Test: Validation failure (duplicate mobile)
   - Test: Student ID auto-generation
   - Test: Status defaults to ACTIVE
   - Test: Timestamps populated

2. **getStudentById()**
   - Test: Success with valid ID
   - Test: 404 for non-existent ID
   - Test: Cache hit scenario

3. **updateStudent()**
   - Test: Success with allowed fields (firstName, lastName, mobile, status)
   - Test: Immutable fields not updated (dateOfBirth, email, etc.)
   - Test: Optimistic lock failure
   - Test: Cache eviction

4. **deleteStudent()**
   - Test: Success with valid ID
   - Test: 404 for non-existent ID
   - Test: Cache eviction

5. **searchStudents()**
   - Test: Search by lastName
   - Test: Filter by status
   - Test: Combined lastName + status filter
   - Test: Pagination working

**Success Criteria:**
- All service methods tested
- 85% code coverage
- All tests pass

**Dependencies:** BE-010, BE-022

---

### [QA-BE-004] Integration Testing - Student API Endpoints

**Type:** Integration Test
**Target:** API Contract Compliance

**Test Scenarios:**

1. **POST /api/v1/students (Create Student)**
   - Test: 201 Created with valid data
   - Test: Location header includes studentId
   - Test: Response matches StudentResponse schema
   - Test: 400 Bad Request with invalid data
   - Test: 409 Conflict with duplicate mobile
   - Test: Drools validation errors returned

2. **GET /api/v1/students/{studentId} (Get Student)**
   - Test: 200 OK with valid ID
   - Test: 404 Not Found with invalid ID
   - Test: Response includes all fields
   - Test: Cache headers present

3. **PUT /api/v1/students/{studentId} (Update Student)**
   - Test: 200 OK with valid update
   - Test: Only allowed fields updated (BR-STU-007)
   - Test: 409 Conflict with stale version
   - Test: 400 Bad Request with invalid data

4. **DELETE /api/v1/students/{studentId} (Delete Student)**
   - Test: 204 No Content on success
   - Test: 404 Not Found with invalid ID

5. **GET /api/v1/students (Search Students)**
   - Test: 200 OK with pagination
   - Test: Filter by lastName works
   - Test: Filter by status works
   - Test: Empty results return empty array

**Success Criteria:**
- All endpoints match sms_api_specification.yaml
- HTTP status codes correct
- RFC 7807 error format for failures
- All tests pass

**Dependencies:** BE-011, BE-024

---

### [QA-BE-005] Integration Testing - Configuration API Endpoints

**Type:** Integration Test
**Target:** API Contract Compliance

**Test Scenarios:**

1. **GET /api/v1/configurations**
   - Test: 200 OK returns all configurations
   - Test: Filter by category works
   - Test: Empty results return empty array

2. **GET /api/v1/configurations/{category}/{key}**
   - Test: 200 OK with valid category/key
   - Test: 404 Not Found with invalid category/key

3. **PUT /api/v1/configurations/{category}/{key}**
   - Test: 201 Created for new configuration
   - Test: 200 OK for existing configuration update
   - Test: Upsert logic working

4. **DELETE /api/v1/configurations/{category}/{key}**
   - Test: 204 No Content on success
   - Test: 404 Not Found with invalid category/key

5. **GET /api/v1/configurations/grouped/{category}**
   - Test: 200 OK returns key-value map
   - Test: Grouped by category

**Success Criteria:**
- All endpoints functional
- Upsert logic verified
- All tests pass

**Dependencies:** BE-020, BE-025

---

### [QA-BE-006] Database Isolation Testing

**Type:** Integration Test
**Target:** Database-per-Service Enforcement (D-010)

**Test Scenarios:**

1. **Verify Student Service Isolation**
   - Test: Student Service connects ONLY to student_db (port 5433)
   - Test: No queries to config_db
   - Test: Redis DB 0 usage only

2. **Verify Configuration Service Isolation**
   - Test: Configuration Service connects ONLY to config_db (port 5434)
   - Test: No queries to student_db
   - Test: Redis DB 1 usage only

3. **Code Analysis**
   - Scan: No cross-database SQL in codebase
   - Scan: No shared repository interfaces
   - Scan: Datasource configurations separate

**Commands:**
```bash
# Check for cross-database access
grep -r "config_db" student-service/src/
grep -r "student_db" configuration-service/src/
```

**Success Criteria:**
- Zero cross-database access detected
- Each service isolated
- No SQL joins across services

**Dependencies:** BE-002, BE-015, BE-030

---

### [QA-BE-007] Performance Testing - API Response Times

**Type:** Performance Test
**Target:** p95 < 200ms

**Test Scenarios:**

1. **Student API Load Test**
   - Test: GET /api/v1/students (100 concurrent users)
   - Measure: p50, p95, p99 response times
   - Target: p95 < 200ms

2. **Configuration API Load Test**
   - Test: GET /api/v1/configurations (100 concurrent users)
   - Target: p95 < 200ms

3. **Database Query Performance**
   - Test: Student search queries
   - Target: Average < 50ms
   - Check: N+1 queries prevented

**Tools:** JMeter, Gatling, or wrk

**Success Criteria:**
- p95 response time < 200ms
- No N+1 queries
- Database query time < 50ms average

**Dependencies:** BE-011, BE-020

---

### [QA-BE-008] Cache Validation Testing

**Type:** Integration Test
**Target:** Redis Caching

**Test Scenarios:**

1. **Student Cache Behavior**
   - Test: First request hits database
   - Test: Second request hits cache (Redis DB 0)
   - Test: Update evicts cache
   - Test: Delete evicts cache
   - Test: TTL expires after 4 hours

2. **Configuration Cache Behavior**
   - Test: Cache hit/miss for configurations (Redis DB 1)
   - Test: Upsert evicts cache
   - Test: TTL expires after 2 hours

3. **Cache Key Verification**
   - Test: Student keys prefixed with "sms:student:"
   - Test: Configuration keys prefixed with "sms:config:"

**Success Criteria:**
- Cache working correctly
- Eviction on updates
- TTL configured correctly

**Dependencies:** BE-013

---

### [QA-BE-009] Spring Actuator Metrics Validation

**Type:** Integration Test
**Target:** Monitoring Endpoints

**Test Scenarios:**

1. **/actuator/health**
   - Test: Returns 200 OK
   - Test: Status: UP
   - Test: Database health check

2. **/actuator/metrics**
   - Test: Returns metrics list
   - Test: Custom metrics present (students.registered.total, students.deleted.total)

3. **/actuator/prometheus**
   - Test: Returns Prometheus format
   - Test: Metrics exported

**Success Criteria:**
- All actuator endpoints functional
- Custom metrics incremented correctly

**Dependencies:** BE-028

---

## Frontend QA Tasks

### [QA-FE-001] Component Unit Testing - StudentCard

**Type:** Unit Test
**Coverage Target:** 80%

**Test Scenarios:**

1. **Rendering**
   - Test: Displays student name
   - Test: Displays student ID
   - Test: Displays guardian name
   - Test: Displays phone and email
   - Test: Status badge color (ACTIVE = green, INACTIVE = gray)

2. **Button Actions**
   - Test: onView called with student
   - Test: onEdit called with student
   - Test: onDelete called with student ID

**Success Criteria:**
- All rendering scenarios tested
- All button actions tested
- 80% code coverage

**Dependencies:** FE-016

---

### [QA-FE-002] Component Unit Testing - StudentDialog

**Type:** Unit Test
**Coverage Target:** 80%

**Test Scenarios:**

1. **Create Mode**
   - Test: All fields visible
   - Test: Default status = ACTIVE
   - Test: Form validation errors display
   - Test: Submit button disabled during submission

2. **Edit Mode**
   - Test: Only editable fields enabled (firstName, lastName, phone, status)
   - Test: Immutable fields disabled (dateOfBirth, email, etc.)
   - Test: Form pre-populated with student data

3. **Validation**
   - Test: Age validation (3-18 years)
   - Test: Phone validation (10 digits)
   - Test: Email validation
   - Test: Name pattern validation

**Success Criteria:**
- Create/edit modes tested
- Validation tested
- 80% code coverage

**Dependencies:** FE-017

---

### [QA-FE-003] Integration Testing - Student Service

**Type:** Integration Test
**Target:** API Integration

**Test Scenarios:**

1. **Field Mapping (D-007)**
   - Test: Frontend phone → Backend mobile
   - Test: Frontend id → Backend studentId
   - Test: Backend mobile → Frontend phone
   - Test: Backend studentId → Frontend id

2. **API Calls**
   - Test: getAll() returns students
   - Test: create() sends correct payload
   - Test: update() sends only editable fields
   - Test: delete() calls correct endpoint

3. **Error Handling**
   - Test: Network error throws ApiException
   - Test: 400 error handled
   - Test: 404 error handled

**Success Criteria:**
- Field mapping working correctly
- All API calls functional
- Errors handled properly

**Dependencies:** FE-006

---

### [QA-FE-004] Screenshot Validation - HomePage

**Type:** Visual Regression Test
**Target:** Pixel-Perfect Match

**Reference:** `screenshots/homepage.png`

**Validation Checklist:**
- [ ] Welcome banner: bg-blue-600, white text
- [ ] Banner text: "Welcome to School Management System"
- [ ] 3 stat cards present
- [ ] Card layout: 1 col mobile, 3 col desktop
- [ ] Icons match (Users, UserCheck, Settings)
- [ ] Quick action cards: exact text
- [ ] Color scheme: blues (#2563eb), grays (#6b7280)
- [ ] Spacing matches (p-8, gap-4, etc.)

**Tools:** Manual comparison + DevTools color picker

**Success Criteria:**
- All elements match screenshot
- Colors exact (#2563eb for blue-600)
- Spacing exact
- Typography exact

**Dependencies:** FE-015

---

### [QA-FE-005] Screenshot Validation - StudentsPage

**Type:** Visual Regression Test
**Target:** Pixel-Perfect Match

**Reference:** `screenshots/students-page.png`

**Validation Checklist:**
- [ ] Search input placeholder: "Search by ID, name, or guardian..."
- [ ] Status filter dropdown options (All/Active/Inactive)
- [ ] "Register New Student" button (top-right, primary)
- [ ] Student cards grid: 1 col (mobile), 2 (tablet), 3 (desktop)
- [ ] Card content: ID, Name, Guardian, Phone, Email, Status badge
- [ ] Status badge colors: green-100/green-800 (ACTIVE), gray-100/gray-800 (INACTIVE)
- [ ] Action buttons: View, Edit, Delete with correct icons
- [ ] Icon sizes: h-4 w-4

**Success Criteria:**
- Page matches screenshot pixel-perfectly
- Grid responsive breakpoints correct
- Status badge colors exact

**Dependencies:** FE-019

---

### [QA-FE-006] Screenshot Validation - Student Dialogs

**Type:** Visual Regression Test
**Target:** Pixel-Perfect Match

**References:**
- Create: `screenshots/student-dialog-create.png`
- Edit: `screenshots/student-dialog-edit.png`
- View: `screenshots/student-dialog-view.png`

**Create Dialog Checklist:**
- [ ] Title: "Register New Student"
- [ ] Form sections: Personal Info, Guardian Info, Contact, Status
- [ ] All fields visible
- [ ] 2-column grid for name fields
- [ ] Required field markers (*)
- [ ] Submit button: "Register"

**Edit Dialog Checklist:**
- [ ] Title: "Edit Student"
- [ ] Only editable fields enabled
- [ ] Disabled fields grayed out (opacity-50)
- [ ] Submit button: "Update"

**View Dialog Checklist:**
- [ ] Title: "Student Details"
- [ ] No form inputs (read-only display)
- [ ] Section headers with border-b
- [ ] 2-column grid for data

**Success Criteria:**
- All 3 dialog modes match screenshots
- Disabled state styling correct
- Layout matches exactly

**Dependencies:** FE-017, FE-018

---

### [QA-FE-007] Screenshot Validation - ConfigurationsPage

**Type:** Visual Regression Test
**Target:** Pixel-Perfect Match

**Reference:** `screenshots/configurations-page.png`

**Validation Checklist:**
- [ ] Title: "System Configurations"
- [ ] Category filter dropdown
- [ ] "Add New Configuration" button
- [ ] Table columns: Category, Key, Value, Description, Last Updated, Actions
- [ ] Category badge colors:
  - GENERAL: bg-blue-100 text-blue-800
  - ACADEMIC: bg-purple-100 text-purple-800
  - FINANCE: bg-green-100 text-green-800
  - SYSTEM: bg-gray-100 text-gray-800
- [ ] Table hover effect
- [ ] Action buttons (Edit, Delete)

**Success Criteria:**
- Page matches screenshot
- Table layout exact
- Category colors exact

**Dependencies:** FE-021

---

### [QA-FE-008] Screenshot Validation - Configuration Dialogs

**Type:** Visual Regression Test
**Target:** Pixel-Perfect Match

**References:**
- Add: `screenshots/configuration-dialog-add.png`
- Edit: `screenshots/configuration-dialog-edit.png`

**Add Dialog Checklist:**
- [ ] Title: "Add New Configuration"
- [ ] Fields: Category, Key, Value, Description
- [ ] dataType field NOT visible
- [ ] Category dropdown with 4 options
- [ ] Key validation hint text

**Edit Dialog Checklist:**
- [ ] Title: "Edit Configuration"
- [ ] Category and Key fields disabled
- [ ] Value and Description editable

**Success Criteria:**
- Dialogs match screenshots
- dataType field hidden
- Disabled fields in edit mode

**Dependencies:** FE-020

---

### [QA-FE-009] End-to-End Testing - Student Management Flow

**Type:** E2E Test
**Tool:** Playwright or Cypress

**Test Scenario:**

1. **Navigate to Students Page**
   - Open http://localhost:5173/students
   - Verify page loaded

2. **Create Student**
   - Click "Register New Student"
   - Fill all required fields
   - Submit form
   - Verify toast notification
   - Verify student appears in list

3. **View Student**
   - Click "View" on created student
   - Verify all details correct
   - Close dialog

4. **Edit Student**
   - Click "Edit" on student
   - Change firstName, lastName, phone, status
   - Submit form
   - Verify changes reflected

5. **Search Student**
   - Enter student name in search
   - Verify filtered correctly
   - Clear search

6. **Delete Student**
   - Click "Delete" on student
   - Confirm deletion
   - Verify student removed

**Success Criteria:**
- All steps pass
- No errors in console
- Toast notifications work

**Dependencies:** FE-019, BE-011

---

### [QA-FE-010] End-to-End Testing - Configuration Management Flow

**Type:** E2E Test
**Tool:** Playwright or Cypress

**Test Scenario:**

1. **Navigate to Configurations Page**
   - Open http://localhost:5173/configurations
   - Verify page loaded

2. **Create Configuration**
   - Click "Add New Configuration"
   - Select category
   - Enter key, value, description
   - Submit form
   - Verify toast notification

3. **Filter by Category**
   - Select category from dropdown
   - Verify filtered correctly

4. **Edit Configuration**
   - Click "Edit" on configuration
   - Change value
   - Submit form
   - Verify changes reflected

5. **Delete Configuration**
   - Click "Delete" on configuration
   - Confirm deletion
   - Verify configuration removed

**Success Criteria:**
- All steps pass
- Upsert logic working
- No errors

**Dependencies:** FE-021, BE-020

---

### [QA-FE-011] Form Validation Testing

**Type:** Functional Test
**Target:** All Form Validation Rules

**Test Scenarios:**

1. **Student Form - Age Validation**
   - Input: DOB with age < 3
   - Expected: Error "Age must be between 3 and 18 years"

2. **Student Form - Phone Validation**
   - Input: Phone with 9 digits
   - Expected: Error "Phone must be exactly 10 digits"

3. **Student Form - Email Validation**
   - Input: Invalid email "test@"
   - Expected: Error "Email must be in valid format"

4. **Student Form - Name Pattern**
   - Input: firstName with numbers "John123"
   - Expected: Error "First name must contain only letters and spaces"

5. **Configuration Form - Key Pattern**
   - Input: Key with lowercase "school_name"
   - Expected: Error "Key must contain only uppercase letters, numbers, and underscores"

**Success Criteria:**
- All validation errors display correctly
- Validation matches backend Drools rules (D-008)

**Dependencies:** FE-008, FE-017, FE-020

---

### [QA-FE-012] Responsive Design Testing

**Type:** Visual Test
**Target:** All Breakpoints

**Breakpoints to Test:**
- Mobile: 375px, 414px
- Tablet: 768px, 1024px
- Desktop: 1280px, 1920px

**Pages to Test:**
- HomePage
- StudentsPage
- ConfigurationsPage

**Validation Checklist:**
- [ ] HomePage: 1 col mobile, 3 col desktop (stat cards)
- [ ] StudentsPage: 1 col mobile, 2 tablet, 3 desktop (student cards)
- [ ] ConfigurationsPage: Table scrolls horizontally on mobile
- [ ] Dialogs: max-w-2xl, max-w-md constraints
- [ ] No horizontal scrolling
- [ ] Touch targets ≥ 44x44px

**Success Criteria:**
- All pages responsive
- Grid layouts adjust correctly
- No horizontal overflow

**Dependencies:** FE-015, FE-019, FE-021

---

### [QA-FE-013] Cross-Browser Testing

**Type:** Compatibility Test
**Target:** Modern Browsers

**Browsers to Test:**
- Chrome (latest)
- Firefox (latest)
- Edge (latest)
- Safari (latest, if available)

**Test Scenarios:**
- All pages load correctly
- All forms functional
- All dialogs open/close
- Toast notifications work

**Success Criteria:**
- All browsers functional
- No layout issues
- No JavaScript errors

**Dependencies:** All frontend tasks

---

## Integration QA Tasks

### [QA-INT-001] End-to-End Integration Testing

**Type:** Integration E2E Test
**Target:** Full Stack Integration

**Test Scenario:**

1. **Start All Services**
   - Start Student Service (port 8081)
   - Start Configuration Service (port 8082)
   - Start Frontend (port 5173)

2. **Student Registration to Database**
   - Create student via frontend
   - Verify API call to POST /api/v1/students
   - Verify database record created in student_db
   - Verify Redis cache updated (DB 0)

3. **Student Retrieval from Cache**
   - Get student via frontend
   - Verify Redis cache hit
   - Verify database not queried

4. **Student Update with Optimistic Lock**
   - Open student in two browser tabs
   - Update in tab 1
   - Attempt update in tab 2 with stale version
   - Verify 409 Conflict error
   - Verify error displayed in frontend

5. **Configuration CRUD**
   - Create configuration via frontend
   - Verify database record in config_db
   - Verify Redis cache (DB 1)

**Success Criteria:**
- All services communicate correctly
- Database isolation maintained
- Cache working
- Optimistic locking functional

**Dependencies:** All backend and frontend tasks

---

### [QA-INT-002] API Error Propagation Testing

**Type:** Integration Test
**Target:** Error Handling Across Stack

**Test Scenarios:**

1. **Validation Error Propagation**
   - Submit invalid student data (age < 3)
   - Verify backend returns 400 Bad Request
   - Verify frontend displays field-level errors
   - Verify RFC 7807 error format

2. **Not Found Error**
   - Request non-existent student
   - Verify backend returns 404 Not Found
   - Verify frontend displays toast error

3. **Optimistic Lock Conflict**
   - Trigger concurrent update
   - Verify backend returns 409 Conflict
   - Verify frontend displays error message

**Success Criteria:**
- All backend errors propagate to frontend
- Error messages displayed correctly
- Toast notifications working

**Dependencies:** BE-012, FE-005

---

### [QA-INT-003] Performance Testing - Full Stack

**Type:** Performance Test
**Target:** End-to-End Response Time

**Test Scenarios:**

1. **Load HomePage**
   - Measure: Time to interactive
   - Target: < 2 seconds

2. **Load StudentsPage**
   - Measure: API call + render time
   - Target: < 2 seconds

3. **Create Student Flow**
   - Measure: Form submit to success notification
   - Target: < 1 second

**Success Criteria:**
- All pages load < 2 seconds
- API responses < 200ms (p95)
- No performance regressions

**Dependencies:** All tasks

---

## Test Summary

**Total Tasks:** 28

**Breakdown:**
- Backend QA: 9 tasks
- Frontend QA: 13 tasks
- Integration QA: 3 tasks
- Screenshot Validation: 5 tasks (embedded in frontend)

**Critical Path:**
QA-BE-002 → QA-BE-003 → QA-BE-004 → QA-FE-001 → QA-FE-009 → QA-INT-001

**Estimated Effort:** 5-7 days for QA team

---

## References

- Architecture: `specs/architecture/` (all docs)
- API Specification: `specs/sms_api_specification.yaml`
- Screenshots: `screenshots/` directory
- Backend Tasks: `docs/tasks/BACKEND_TASKS.md`
- Frontend Tasks: `docs/tasks/FRONTEND_TASKS.md`
- Global Directives: D-001 to D-010

---

**Document Status:** READY FOR QA TEAM
**Next Phase:** Production Deployment
