# QA Test Plan - School Management System
**Waterfall / Single-Pass Testing**
**Date**: 2026-01-15
**Testing Strategy**: Unit, Integration, End-to-End

---

## Overview

This plan provides a comprehensive testing strategy for the School Management System covering backend APIs, frontend UI, and end-to-end user flows. All tests must pass before deployment.

**Testing Layers**:
1. Unit Tests (Backend Services, Frontend Components)
2. Integration Tests (API Endpoints, Database Operations)
3. End-to-End Tests (Full User Flows)
4. Non-Functional Tests (Performance, Security, Usability)

---

## Phase 1: Backend API Testing

### [QA-001] Student API - Create Student (Happy Path)
**Type**: Integration Test
**Priority**: CRITICAL

**Steps**:
1. Send POST request to `/api/v1/students` with valid data:
   ```json
   {
     "firstName": "John",
     "lastName": "Doe",
     "dateOfBirth": "2015-06-15",
     "aadhaarNumber": "123456789012",
     "address": "123 Main Street, City, State, 123456",
     "fathersName": "Father Name",
     "mothersName": "Mother Name",
     "mobile": "9876543210",
     "email": "john.doe@example.com"
   }
   ```
2. Verify response status code is 201 Created
3. Verify response contains auto-generated `studentId` in format `STD-YYYYMMDD-XXXX`
4. Verify `status` defaults to 'ACTIVE'
5. Verify `createdAt` and `updatedAt` timestamps are present
6. Verify `version` is 0

**Success Criteria**:
- Student created successfully
- All fields match request data
- studentId follows correct format
- Age calculated correctly (11 years)

**Dependencies**: Requires BE-011

---

### [QA-002] Student API - Age Validation (3-18 years)
**Type**: Integration Test
**Priority**: CRITICAL

**Test Scenarios**:

**Scenario 2.1: Age < 3 years**
- **Steps**:
  1. Send POST request with `dateOfBirth` = today - 2 years
  2. Verify response status code is 422 Unprocessable Entity
  3. Verify error message: "Student age must be between 3 and 18 years"

**Scenario 2.2: Age = 3 years (Boundary)**
- **Steps**:
  1. Send POST request with `dateOfBirth` = today - 3 years
  2. Verify response status code is 201 Created
  3. Verify student created successfully

**Scenario 2.3: Age = 18 years (Boundary)**
- **Steps**:
  1. Send POST request with `dateOfBirth` = today - 18 years
  2. Verify response status code is 201 Created

**Scenario 2.4: Age > 18 years**
- **Steps**:
  1. Send POST request with `dateOfBirth` = today - 19 years
  2. Verify response status code is 422 Unprocessable Entity

**Success Criteria**:
- Age validation enforced correctly
- Boundary values (3 and 18) accepted
- Out-of-range values rejected with clear error message

**Dependencies**: Requires BE-011

---

### [QA-003] Student API - Mobile Uniqueness Validation
**Type**: Integration Test
**Priority**: CRITICAL

**Steps**:
1. Create student with mobile "9876543210" (QA-001)
2. Attempt to create second student with same mobile "9876543210"
3. Verify response status code is 409 Conflict
4. Verify error message: "Mobile number is already registered"
5. Verify error follows RFC 7807 Problem Details format

**Success Criteria**:
- Duplicate mobile rejected
- Error response in correct format
- First student remains in database

**Dependencies**: Requires QA-001

---

### [QA-004] Student API - Update Allowed Fields Only
**Type**: Integration Test
**Priority**: CRITICAL

**Test Scenarios**:

**Scenario 4.1: Update Allowed Fields (firstName, lastName, mobile, status)**
- **Steps**:
  1. Create student (use QA-001 data)
  2. Send PUT request to `/api/v1/students/{studentId}` with:
     ```json
     {
       "firstName": "Jane",
       "lastName": "Smith",
       "mobile": "9999999999",
       "status": "INACTIVE",
       "version": 0
     }
     ```
  3. Verify response status code is 200 OK
  4. Verify fields updated correctly
  5. Verify `version` incremented to 1

**Scenario 4.2: Attempt to Update Immutable Fields (dateOfBirth, email, aadhaarNumber)**
- **Steps**:
  1. Send PUT request with attempt to change `dateOfBirth` or `email`
  2. Verify response status code is 400 Bad Request OR fields ignored
  3. Verify immutable fields remain unchanged

**Success Criteria**:
- Allowed fields update successfully
- Immutable fields cannot be changed
- Version number increments

**Dependencies**: Requires QA-001

---

### [QA-005] Student API - Optimistic Locking
**Type**: Integration Test
**Priority**: HIGH

**Steps**:
1. Create student (version = 0)
2. Send first PUT request with correct version = 0 → Success (version becomes 1)
3. Send second PUT request with stale version = 0 → Conflict
4. Verify response status code is 409 Conflict
5. Verify error message indicates version mismatch

**Success Criteria**:
- Optimistic locking prevents lost updates
- Concurrent modification detected
- Error response clear

**Dependencies**: Requires QA-004

---

### [QA-006] Student API - Search and Filter
**Type**: Integration Test
**Priority**: HIGH

**Test Scenarios**:

**Scenario 6.1: Search by Last Name**
- **Steps**:
  1. Create 3 students with lastNames: "Doe", "Smith", "Doe"
  2. Send GET request to `/api/v1/students?lastName=Doe`
  3. Verify response contains 2 students
  4. Verify both have lastName containing "Doe" (case-insensitive)

**Scenario 6.2: Filter by Status**
- **Steps**:
  1. Create 2 ACTIVE students and 1 INACTIVE student
  2. Send GET request to `/api/v1/students?status=ACTIVE`
  3. Verify response contains only 2 ACTIVE students

**Success Criteria**:
- Search returns correct results
- Filters work correctly
- Case-insensitive search

**Dependencies**: Requires QA-001, QA-004

---

### [QA-007] Student API - Delete Student
**Type**: Integration Test
**Priority**: MEDIUM

**Steps**:
1. Create student
2. Verify student exists (GET request)
3. Send DELETE request to `/api/v1/students/{studentId}`
4. Verify response status code is 204 No Content
5. Attempt GET request for same student
6. Verify response status code is 404 Not Found

**Success Criteria**:
- Student deleted successfully
- Subsequent GET returns 404
- Cascade delete to enrollments (if exists)

**Dependencies**: Requires QA-001

---

### [QA-008] Configuration API - CRUD Operations
**Type**: Integration Test
**Priority**: HIGH

**Test Scenarios**:

**Scenario 8.1: Create Configuration**
- **Steps**:
  1. Send PUT request to `/api/v1/configurations/GENERAL/SCHOOL_NAME` with:
     ```json
     {
       "value": "Springfield Public School",
       "description": "Official school name",
       "dataType": "STRING"
     }
     ```
  2. Verify response status code is 201 Created
  3. Verify configuration created

**Scenario 8.2: Update Configuration (Upsert)**
- **Steps**:
  1. Send PUT request to same endpoint with updated value
  2. Verify response status code is 200 OK
  3. Verify value updated

**Scenario 8.3: Get Configuration by Category**
- **Steps**:
  1. Create 3 configurations in GENERAL category
  2. Send GET request to `/api/v1/configurations?category=GENERAL`
  3. Verify response contains all 3 configurations

**Scenario 8.4: Delete Configuration**
- **Steps**:
  1. Send DELETE request to `/api/v1/configurations/{category}/{key}`
  2. Verify response status code is 204 No Content
  3. Verify configuration deleted

**Success Criteria**:
- All CRUD operations functional
- Upsert logic works correctly
- Category filtering works

**Dependencies**: Requires BE-020

---

### [QA-009] API Error Handling - RFC 7807 Compliance
**Type**: Integration Test
**Priority**: MEDIUM

**Steps**:
1. Trigger various error scenarios:
   - 404: Request non-existent student
   - 400: Send invalid request body (missing required fields)
   - 409: Duplicate mobile
   - 422: Invalid age
2. For each error, verify response format:
   ```json
   {
     "type": "https://api.school.com/errors/...",
     "title": "Error Title",
     "status": 400,
     "detail": "Detailed error message",
     "instance": "/api/v1/students",
     "timestamp": "2026-01-15T10:30:00Z",
     "errors": [
       {
         "field": "mobile",
         "message": "Mobile number is required",
         "code": "REQUIRED_FIELD"
       }
     ]
   }
   ```
3. Verify all fields present
4. Verify status code matches `status` field

**Success Criteria**:
- All errors follow RFC 7807 format
- Field-level errors included when applicable
- Error messages are user-friendly

**Dependencies**: Requires BE-013

---

### [QA-010] API Performance Testing
**Type**: Non-Functional Test
**Priority**: MEDIUM

**Test Scenarios**:

**Scenario 10.1: Create Student Response Time**
- **Steps**:
  1. Send 100 sequential POST requests to create students
  2. Measure response times
  3. Calculate p50, p95, p99

**Success Criteria**:
- p95 response time < 200ms
- p99 response time < 500ms

**Scenario 10.2: List Students Response Time**
- **Steps**:
  1. Create 100 students
  2. Send GET request to list all students
  3. Measure response time

**Success Criteria**:
- Response time < 150ms (p95)

**Scenario 10.3: Concurrent Create Requests**
- **Steps**:
  1. Send 50 concurrent POST requests
  2. Verify all succeed (or fail correctly for duplicates)
  3. No database locks or timeouts

**Success Criteria**:
- System handles concurrent requests
- No data corruption
- Response times acceptable

**Dependencies**: Requires QA-001

---

## Phase 2: Frontend UI Testing

### [QA-011] HomePage - Statistics Display
**Type**: UI Test
**Priority**: HIGH

**Steps**:
1. Navigate to `http://localhost:5173/`
2. Verify page loads without errors
3. Verify statistics cards display:
   - Total Students
   - Active Students
   - Inactive Students
4. Verify counts are fetched from API (not hardcoded)
5. Create a new student via API
6. Refresh homepage
7. Verify Total Students count incremented

**Success Criteria**:
- Statistics load correctly
- Cards display proper icons and styling
- Counts reflect actual data
- Quick action cards navigate correctly

**Dependencies**: Requires FE-008, QA-001

---

### [QA-012] StudentsPage - List and Search
**Type**: UI Test
**Priority**: CRITICAL

**Steps**:
1. Create 5 students via API with different names and statuses
2. Navigate to `/students`
3. Verify all 5 students displayed in cards
4. Test search:
   - Enter "Doe" in search box
   - Wait 300ms (debounce)
   - Verify only students with lastName "Doe" displayed
5. Test filter:
   - Select "Active" from status filter
   - Verify only ACTIVE students displayed
6. Test combined search + filter

**Success Criteria**:
- All students load correctly
- Student cards display all fields correctly
- Search works with debounce
- Filter works correctly
- Responsive grid layout (1/2/3 columns)

**Dependencies**: Requires FE-009, QA-001

---

### [QA-013] StudentDialog - Create Student
**Type**: UI Test
**Priority**: CRITICAL

**Steps**:
1. On StudentsPage, click "Register New Student" button
2. Verify dialog opens
3. Fill all required fields with valid data
4. Submit form
5. Verify loading state on submit button
6. Verify success toast appears
7. Verify dialog closes
8. Verify new student appears in list

**Success Criteria**:
- Dialog opens correctly
- All form fields visible
- Validation works (try invalid data)
- Submission works
- Toast notification appears
- List refreshes automatically

**Dependencies**: Requires FE-010, QA-001

---

### [QA-014] StudentDialog - Form Validation
**Type**: UI Test
**Priority**: CRITICAL

**Test Scenarios**:

**Scenario 14.1: Required Field Validation**
- **Steps**:
  1. Open dialog
  2. Submit form without filling any fields
  3. Verify all required fields show error messages
  4. Verify form does not submit

**Scenario 14.2: Age Validation (3-18)**
- **Steps**:
  1. Enter DOB with age < 3
  2. Attempt to submit
  3. Verify error message: "Age must be between 3 and 18 years"

**Scenario 14.3: Mobile Format Validation**
- **Steps**:
  1. Enter mobile "123" (invalid)
  2. Verify error: "Mobile must be 10 digits"

**Scenario 14.4: Email Format Validation**
- **Steps**:
  1. Enter email "invalid-email"
  2. Verify error: "Please enter a valid email"

**Scenario 14.5: Async Phone Uniqueness Validation**
- **Steps**:
  1. Create student with mobile "9876543210"
  2. Open dialog for new student
  3. Enter same mobile "9876543210"
  4. Wait for async validation
  5. Verify error: "Mobile number already registered"

**Success Criteria**:
- All validation rules enforced
- Error messages display inline below fields
- Form submission blocked until valid
- Async validation works

**Dependencies**: Requires FE-010, FE-016

---

### [QA-015] StudentDialog - Edit Restrictions
**Type**: UI Test
**Priority**: HIGH

**Steps**:
1. Create student via dialog
2. Click "Edit" on student card
3. Verify dialog opens in edit mode
4. Verify immutable fields are disabled:
   - Date of Birth (disabled)
   - Aadhaar Number (disabled)
   - Email (disabled)
5. Verify editable fields are enabled:
   - First Name
   - Last Name
   - Mobile
   - Status
6. Update editable fields
7. Submit form
8. Verify update successful

**Success Criteria**:
- Edit mode enforces field restrictions
- Immutable fields visually disabled
- Editable fields update correctly
- Optimistic locking version sent with request

**Dependencies**: Requires FE-010, QA-004

---

### [QA-016] StudentDialog - Error Display
**Type**: UI Test
**Priority**: HIGH

**Steps**:
1. Open create dialog
2. Enter duplicate mobile number
3. Submit form
4. Verify API returns 409 error
5. Verify error toast displays: "Failed to save student"
6. Verify field-level error displays below mobile field (if backend returns field errors)

**Success Criteria**:
- API errors handled gracefully
- Toast notifications show for general errors
- Field errors display inline
- Dialog remains open to allow correction

**Dependencies**: Requires FE-017, QA-003

---

### [QA-017] ViewStudentDialog - Display All Fields
**Type**: UI Test
**Priority**: MEDIUM

**Steps**:
1. Create student with all fields filled
2. Click "View" button on student card
3. Verify dialog opens
4. Verify all fields displayed correctly:
   - Student ID
   - Name (First + Last)
   - Date of Birth
   - Age (calculated)
   - Aadhaar Number
   - Address
   - Father's Name
   - Mother's Name
   - Mobile
   - Email
   - Status (badge)
   - Created At (formatted)
   - Updated At (formatted)
5. Verify dialog is read-only (no input fields)
6. Click Close button
7. Verify dialog closes

**Success Criteria**:
- All fields display correctly
- Dates formatted properly
- Status badge has correct color
- Read-only mode (no editing)

**Dependencies**: Requires FE-011, QA-001

---

### [QA-018] ConfigurationsPage - CRUD Operations
**Type**: UI Test
**Priority**: HIGH

**Steps**:
1. Navigate to `/configurations`
2. Verify table loads with existing configurations
3. Click "Add New Configuration" button
4. Fill form (category, key, value, description)
5. Submit
6. Verify new configuration appears in table
7. Filter by category
8. Verify filtered results correct
9. Click Edit on a configuration
10. Update value
11. Submit
12. Verify value updated in table
13. Click Delete
14. Confirm deletion
15. Verify configuration removed

**Success Criteria**:
- All CRUD operations work
- Table updates correctly
- Category filter works
- Category badges have correct colors

**Dependencies**: Requires FE-012, FE-013, QA-008

---

### [QA-019] Navigation and Routing
**Type**: UI Test
**Priority**: MEDIUM

**Steps**:
1. Start at HomePage
2. Click "Manage Students" quick action
3. Verify navigated to `/students`
4. Click "Configurations" in header
5. Verify navigated to `/configurations`
6. Click logo in header
7. Verify navigated back to `/`
8. Use browser back button
9. Verify navigation history works
10. Enter invalid URL `/invalid-page`
11. Verify redirected to `/`

**Success Criteria**:
- All navigation links work
- Active route highlighted in header
- Browser back/forward works
- Invalid routes redirect to home
- URL reflects current page

**Dependencies**: Requires FE-015

---

### [QA-020] Responsive Design - Mobile View
**Type**: UI Test
**Priority**: HIGH

**Steps**:
1. Open browser DevTools
2. Set viewport to 375px (iPhone SE)
3. Navigate to StudentsPage
4. Verify student cards stack in 1 column
5. Verify search and filter inputs stack vertically
6. Open StudentDialog
7. Verify dialog fits mobile screen
8. Verify form is scrollable
9. Navigate to ConfigurationsPage
10. Verify table scrolls horizontally

**Success Criteria**:
- Layout responsive on mobile
- No horizontal overflow
- Dialogs scrollable
- Touch targets at least 44x44px
- Text readable without zooming

**Dependencies**: Requires FE-022

---

### [QA-021] Responsive Design - Tablet and Desktop
**Type**: UI Test
**Priority**: MEDIUM

**Steps**:
1. Test at 768px (Tablet)
   - Verify 2-column grid for student cards
2. Test at 1280px (Desktop)
   - Verify 3-column grid for student cards
3. Test at 1920px (Large Desktop)
   - Verify layout doesn't break

**Success Criteria**:
- Grid adapts to viewport size
- Content uses available space efficiently
- No layout breaks at any viewport

**Dependencies**: Requires FE-022

---

### [QA-022] Loading States and Skeletons
**Type**: UI Test
**Priority**: MEDIUM

**Steps**:
1. Add network throttling (Slow 3G)
2. Navigate to StudentsPage
3. Verify skeleton cards display while loading
4. Wait for data to load
5. Verify skeletons replaced with actual cards
6. Test on HomePage statistics
7. Test on ConfigurationsPage table

**Success Criteria**:
- Loading states visible during data fetch
- Skeletons match actual component layout
- No flash of empty state

**Dependencies**: Requires FE-009

---

### [QA-023] Toast Notifications
**Type**: UI Test
**Priority**: MEDIUM

**Steps**:
1. Perform successful create operation
2. Verify success toast appears (green)
3. Verify toast auto-dismisses after 3 seconds
4. Perform operation that causes error
5. Verify error toast appears (red)
6. Verify toast dismissible manually (X button)

**Success Criteria**:
- Toasts appear in correct position (top-right)
- Correct color for success/error/info
- Auto-dismiss works
- Manual dismiss works
- Multiple toasts stack correctly

**Dependencies**: Requires FE-017

---

## Phase 3: End-to-End User Flows

### [QA-024] E2E - Complete Student Registration Flow
**Type**: End-to-End Test
**Priority**: CRITICAL

**Steps**:
1. Start frontend and backend services
2. Navigate to homepage
3. Verify initial statistics (e.g., 0 students)
4. Click "Register Student" quick action
5. Fill complete student form with valid data
6. Submit form
7. Verify success toast
8. Verify dialog closes
9. Verify redirected to Students page (or list refreshes)
10. Verify new student appears in list
11. Navigate back to homepage
12. Verify statistics incremented (Total and Active +1)
13. Click on student card "View" button
14. Verify all entered data displays correctly

**Success Criteria**:
- Complete flow works end-to-end
- Data persists correctly
- Statistics update
- All UI interactions smooth

**Dependencies**: Requires All FE and BE tasks

---

### [QA-025] E2E - Student Edit and Status Change Flow
**Type**: End-to-End Test
**Priority**: CRITICAL

**Steps**:
1. Create student (use QA-024 flow)
2. Navigate to Students page
3. Click "Edit" on student card
4. Change firstName to "UpdatedName"
5. Change status to "INACTIVE"
6. Submit form
7. Verify success toast
8. Verify student card shows updated name
9. Verify status badge shows "INACTIVE" (gray color)
10. Navigate to homepage
11. Verify Active count decreased by 1
12. Verify Inactive count increased by 1
13. Navigate back to Students page
14. Filter by status "INACTIVE"
15. Verify edited student appears in filtered list

**Success Criteria**:
- Edit flow works correctly
- Status change reflects in UI
- Statistics update correctly
- Filter shows correct results

**Dependencies**: Requires QA-024

---

### [QA-026] E2E - Student Deletion Flow
**Type**: End-to-End Test
**Priority**: HIGH

**Steps**:
1. Create student
2. Note current Total Students count
3. Click "Delete" button on student card
4. Verify confirmation dialog appears (if implemented)
5. Confirm deletion
6. Verify success toast
7. Verify student removed from list
8. Navigate to homepage
9. Verify Total Students count decreased by 1
10. Navigate back to Students page
11. Attempt to view deleted student (if URL known)
12. Verify 404 or appropriate error

**Success Criteria**:
- Delete flow works correctly
- Statistics update
- Student no longer accessible
- No orphaned data

**Dependencies**: Requires QA-024

---

### [QA-027] E2E - Search and Filter Flow
**Type**: End-to-End Test
**Priority**: HIGH

**Steps**:
1. Create 10 students with varying names and statuses:
   - 5 with lastName "Smith" (3 ACTIVE, 2 INACTIVE)
   - 5 with lastName "Doe" (4 ACTIVE, 1 INACTIVE)
2. Navigate to Students page
3. Verify all 10 students displayed
4. Enter "Smith" in search box
5. Wait for debounce (300ms)
6. Verify only 5 "Smith" students displayed
7. Select "ACTIVE" from status filter
8. Verify only 3 ACTIVE "Smith" students displayed
9. Clear search box
10. Verify 7 ACTIVE students displayed (all students with ACTIVE status)
11. Select "ALL" status filter
12. Verify all 10 students displayed again

**Success Criteria**:
- Search works correctly
- Filter works correctly
- Combined search + filter works
- Results accurate

**Dependencies**: Requires QA-024

---

### [QA-028] E2E - Configuration Management Flow
**Type**: End-to-End Test
**Priority**: MEDIUM

**Steps**:
1. Navigate to Configurations page
2. Click "Add New Configuration"
3. Fill form:
   - Category: GENERAL
   - Key: TEST_CONFIG
   - Value: Test Value
   - Description: Test description
   - Data Type: STRING
4. Submit
5. Verify configuration appears in table
6. Filter by category "GENERAL"
7. Verify TEST_CONFIG visible
8. Click Edit on TEST_CONFIG
9. Change value to "Updated Value"
10. Submit
11. Verify value updated in table
12. Click Delete on TEST_CONFIG
13. Confirm deletion
14. Verify configuration removed

**Success Criteria**:
- Configuration CRUD works end-to-end
- Filter works
- Changes persist

**Dependencies**: Requires FE-012, FE-013, QA-008

---

## Phase 4: Cross-Functional Testing

### [QA-029] Cross-Browser Compatibility
**Type**: Non-Functional Test
**Priority**: MEDIUM

**Browsers to Test**:
- Chrome (latest)
- Firefox (latest)
- Edge (latest)
- Safari (latest) - if available

**Steps**:
1. Run QA-024 (E2E Student Registration) on each browser
2. Verify form validation works
3. Verify dialogs display correctly
4. Verify toast notifications appear
5. Verify no console errors
6. Verify responsive design works

**Success Criteria**:
- All features work on all browsers
- No browser-specific bugs
- UI renders consistently

**Dependencies**: Requires QA-024

---

### [QA-030] Security Testing
**Type**: Non-Functional Test
**Priority**: HIGH

**Test Scenarios**:

**Scenario 30.1: SQL Injection Prevention**
- **Steps**:
  1. Attempt to create student with lastName: `'; DROP TABLE students; --`
  2. Verify student created with literal string (JPA prevents injection)
  3. Verify no database error

**Scenario 30.2: XSS Prevention**
- **Steps**:
  1. Create student with firstName: `<script>alert('XSS')</script>`
  2. View student details
  3. Verify script does not execute (React escapes by default)
  4. Verify displayed as literal text

**Scenario 30.3: CORS Validation**
- **Steps**:
  1. Attempt API request from unauthorized origin
  2. Verify CORS error
  3. Verify request from `localhost:5173` succeeds

**Success Criteria**:
- No SQL injection possible
- No XSS vulnerabilities
- CORS configured correctly

**Dependencies**: Requires BE-016

---

### [QA-031] Accessibility Testing
**Type**: Non-Functional Test
**Priority**: MEDIUM

**Steps**:
1. Use browser Accessibility DevTools
2. Run audit on all pages
3. Verify no critical accessibility issues
4. Test keyboard navigation:
   - Tab through form fields
   - Press Enter to submit forms
   - Press Escape to close dialogs
5. Test screen reader (if available)
6. Verify all images have alt text
7. Verify focus indicators visible

**Success Criteria**:
- WCAG 2.1 AA compliance
- Keyboard navigation works
- Focus indicators visible
- Proper ARIA labels

**Dependencies**: Requires All FE tasks

---

### [QA-032] Performance Testing
**Type**: Non-Functional Test
**Priority**: MEDIUM

**Steps**:
1. Use Lighthouse in Chrome DevTools
2. Run audit on all pages
3. Verify Performance score > 90
4. Verify First Contentful Paint < 1.5s
5. Verify Largest Contentful Paint < 2.5s
6. Test with 100 students in list
7. Verify no performance degradation

**Success Criteria**:
- Lighthouse Performance score > 90
- All Core Web Vitals pass
- No memory leaks
- No slow rerenders

**Dependencies**: Requires FE-022

---

## Summary

**Total Test Scenarios**: 32
**Critical Tests**: 12
**High Priority Tests**: 11
**Medium Priority Tests**: 9

**Testing Timeline**:
- Phase 1 (Backend API): 2 days
- Phase 2 (Frontend UI): 2 days
- Phase 3 (E2E Flows): 1 day
- Phase 4 (Cross-Functional): 1 day

**Total Estimated Effort**: 6 days (1 QA Engineer)

**Success Criteria for Release**:
- [ ] All CRITICAL tests passing (12)
- [ ] All HIGH priority tests passing (11)
- [ ] At least 80% of MEDIUM tests passing (7/9)
- [ ] No P0 (showstopper) bugs
- [ ] No P1 (critical) bugs
- [ ] Documentation complete
- [ ] Deployment tested in Docker

**Exit Criteria**:
- [ ] All API endpoints tested
- [ ] All UI components tested
- [ ] All user flows tested end-to-end
- [ ] Security tests passed
- [ ] Performance targets met
- [ ] Cross-browser compatibility verified
- [ ] Accessibility audit passed
- [ ] No unresolved critical issues
