# QA Test Plan
# School Management System
# Execution Model: Single-Pass Waterfall

## Source of Truth
- API Contract:       `specs/sms_api_specification.yaml`
- Business Rules:     `specs/architecture/03-business-rules.md`
- Testing Strategy:   `specs/TESTING_STRATEGY.md`
- Requirements:       `specs/REQUIREMENTS.md`

## Test Pyramid Distribution
- Unit Tests (60%): Domain and business rule isolation
- Integration Tests (30%): API contract and persistence
- E2E Tests (10%): Critical user journeys only

## Coverage Targets (from TESTING_STRATEGY.md)
| Layer | Target | Metric |
|---|---|---|
| Domain (Drools rules, Value Objects) | 95% | Line Coverage |
| Application (Command/Query Services) | 85% | Line Coverage |
| Infrastructure (JPA Repositories) | 70% | Branch Coverage |
| Frontend Components and Hooks | 70% | Statement Coverage |

## Tooling
- Backend Unit/Integration: JUnit 5, Mockito, TestContainers (PostgreSQL), MockMvc (`@WebMvcTest`)
- Frontend Component: Vitest + React Testing Library + MSW
- E2E: Playwright (`data-testid` locators, under 50 lines per test)
- Test Data: `StudentTestDataBuilder` and `ConfigurationTestDataBuilder` shared builder pattern

---

## Section A: Backend Unit Tests (Domain Layer)

### [QA-001] BR-1: Student Age Below Minimum (Drools)

**Type:** Unit

**Steps:**
1. Create `StudentRegistrationFact` with `dateOfBirth = LocalDate.now().minusYears(2)`, `mobileAlreadyExists = false`.
2. Execute `StatelessKieSession("studentValidationSession")` with the fact.
3. Check `fact.getViolations()`.

**Success Criteria:** Violations list contains a message matching `"between 3 and 18 years"`.

---

### [QA-002] BR-1: Student Age Above Maximum (Drools)

**Type:** Unit

**Steps:**
1. Create fact with `dateOfBirth = LocalDate.now().minusYears(19)`.
2. Fire Drools session.

**Success Criteria:** Violations list contains a message matching `"between 3 and 18 years"`.

---

### [QA-003] BR-1: Student Age at Boundary (3 years exactly)

**Type:** Unit

**Steps:**
1. Create fact with `dateOfBirth = LocalDate.now().minusYears(3)`.
2. Fire Drools session.

**Success Criteria:** No violations generated (exactly 3 years is valid).

---

### [QA-004] BR-1: Student Age at Boundary (18 years exactly)

**Type:** Unit

**Steps:**
1. Create fact with `dateOfBirth = LocalDate.now().minusYears(18)`.
2. Fire Drools session.

**Success Criteria:** No violations generated (exactly 18 years is valid).

---

### [QA-005] BR-1: Null Date of Birth (Drools)

**Type:** Unit

**Steps:**
1. Create fact with `dateOfBirth = null`.
2. Fire Drools session.

**Success Criteria:** Violations list contains `"Date of birth is required"`.

---

### [QA-006] BR-2: Duplicate Mobile Number (Drools)

**Type:** Unit

**Steps:**
1. Create fact with `mobileAlreadyExists = true`, valid mobile `"9876543210"`, valid DOB (age 10).
2. Fire Drools session.

**Success Criteria:** Violations list contains `"already registered to another student"`.

---

### [QA-007] BR-2: Invalid Mobile Format - Letters (Drools)

**Type:** Unit

**Steps:**
1. Create fact with `mobile = "ABC1234567"`, `mobileAlreadyExists = false`, valid DOB.
2. Fire Drools session.

**Success Criteria:** Violations list contains `"Mobile number must be exactly 10 digits"`.

---

### [QA-008] BR-2: Invalid Mobile Format - 9 Digits (Drools)

**Type:** Unit

**Steps:**
1. Create fact with `mobile = "987654321"` (9 digits), `mobileAlreadyExists = false`, valid DOB.
2. Fire Drools session.

**Success Criteria:** Violations list contains mobile format violation message.

---

### [QA-009] BR-7: Duplicate Enrollment Year (Drools)

**Type:** Unit

**Steps:**
1. Create `EnrollmentFact` with `academicYear = "2025-26"`, `yearAlreadyEnrolled = true`.
2. Fire Drools session.

**Success Criteria:** Violations list contains `"already enrolled for academic year: 2025-26"`.

---

### [QA-010] Mobile Value Object - Valid 10-Digit Number

**Type:** Unit

**Steps:**
1. Instantiate `new Mobile("9876543210")`.

**Success Criteria:** Object created without exception. `mobile.value()` returns `"9876543210"`.

---

### [QA-011] Mobile Value Object - Rejects Invalid Format

**Type:** Unit

**Steps:**
1. Attempt `new Mobile("12345")` (5 digits).

**Success Criteria:** `IllegalArgumentException` thrown with message `"Mobile must be exactly 10 digits"`.

---

### [QA-012] AadhaarNumber Value Object - Accepts Null

**Type:** Unit

**Steps:**
1. Instantiate `new AadhaarNumber(null)`.

**Success Criteria:** Object created without exception (Aadhaar is optional).

---

### [QA-013] AadhaarNumber Value Object - Rejects 11-Digit String

**Type:** Unit

**Steps:**
1. Attempt `new AadhaarNumber("12345678901")` (11 digits).

**Success Criteria:** `IllegalArgumentException` thrown.

---

### [QA-014] Student.create - Status Defaults to ACTIVE (BR-5)

**Type:** Unit

**Steps:**
1. Call `Student.create(firstName, lastName, dateOfBirth, mobile, null, null, null, null, null, null)`.

**Success Criteria:** `student.getStatus()` is `StudentStatus.ACTIVE`.

---

### [QA-015] Student.updateAllowedFields - Only Four Fields Updated (BR-4)

**Type:** Unit

**Steps:**
1. Create a student with all fields populated.
2. Call `student.updateAllowedFields("NewFirst", "NewLast", new Mobile("1234567890"), StudentStatus.INACTIVE)`.
3. Verify `student.getFirstName()`, `student.getLastName()`, `student.getMobile()`, `student.getStatus()`.
4. Verify `student.getDateOfBirth()`, `student.getAddress()`, `student.getEmail()` have NOT changed.

**Success Criteria:** Only the four allowed fields are updated; all others retain original values.

---

## Section B: Backend Unit Tests (Application Layer)

### [QA-016] StudentCommandService.registerStudent - Happy Path

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.existsByMobile(...)` to return `false`.
2. Mock `studentValidationSession.execute(fact)` (no violations injected).
3. Mock `studentRepository.save(student)` to return a student with `studentId = "STD-20260212-0001"`.
4. Call `commandService.registerStudent(validCreateRequest)`.

**Success Criteria:** Returned `StudentResponse.studentId` equals `"STD-20260212-0001"`. `studentRepository.save()` called once. `studentsRegisteredCounter.increment()` called once.

---

### [QA-017] StudentCommandService.registerStudent - Duplicate Mobile Throws DuplicateMobileException

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.existsByMobile(...)` to return `true`.
2. Pre-populate fact so `mobileAlreadyExists = true`.
3. Call `commandService.registerStudent(request)`.

**Success Criteria:** `DuplicateMobileException` is thrown. `studentRepository.save()` is never called.

---

### [QA-018] StudentCommandService.registerStudent - Invalid Age Throws BusinessRuleViolationException

**Type:** Unit (Mockito)

**Steps:**
1. Provide request with `dateOfBirth = LocalDate.now().minusYears(2)`.
2. Drools session fires real rules (use real `KieContainer` in this test or verify via mock that violations are non-empty).
3. Call `commandService.registerStudent(request)`.

**Success Criteria:** `BusinessRuleViolationException` thrown with non-empty violations list.

---

### [QA-019] StudentCommandService.updateStudent - Optimistic Lock Conflict

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.save(student)` to throw `ObjectOptimisticLockingFailureException`.
2. Call `commandService.updateStudent("STD-001", updateRequest)`.

**Success Criteria:** `OptimisticLockConflictException` is propagated (or re-wrapped).

---

### [QA-020] StudentCommandService.deleteStudent - Student Not Found

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.findByStudentId("STD-999")` to return `Optional.empty()`.
2. Call `commandService.deleteStudent("STD-999")`.

**Success Criteria:** `StudentNotFoundException` is thrown. `studentsDeletedCounter.increment()` is never called.

---

### [QA-021] StudentQueryService.searchStudents - Filter by Last Name

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.findByLastName("Smith", pageable)` to return a page of students.
2. Call `queryService.searchStudents("Smith", null, PageRequest.of(0, 20))`.

**Success Criteria:** Returned `PagedStudentResponse.content` maps all students from the mocked page. `studentSearchTimer` records the call.

---

### [QA-022] StudentQueryService.getByStudentId - Student Not Found

**Type:** Unit (Mockito)

**Steps:**
1. Mock `studentRepository.findByStudentId("STD-999")` to return `Optional.empty()`.
2. Call `queryService.getByStudentId("STD-999")`.

**Success Criteria:** `StudentNotFoundException` thrown with message containing `"STD-999"`.

---

### [QA-023] ConfigurationCommandService.upsertConfiguration - Insert New Key

**Type:** Unit (Mockito)

**Steps:**
1. Mock `configurationRepository.findByCategoryAndKey("GENERAL", "schoolName")` to return `Optional.empty()`.
2. Call `commandService.upsertConfiguration("GENERAL", "schoolName", request)`.

**Success Criteria:** `configurationRepository.save(...)` called once with the new setting. Cache eviction annotation triggers (verify via Spy if needed).

---

### [QA-024] ConfigurationCommandService.upsertConfiguration - Update Existing Key

**Type:** Unit (Mockito)

**Steps:**
1. Mock `configurationRepository.findByCategoryAndKey(...)` to return an existing setting.
2. Call `commandService.upsertConfiguration("GENERAL", "schoolName", newRequest)`.

**Success Criteria:** Saved setting has the new `value` from the request. Version is incremented.

---

### [QA-025] ConfigurationQueryService.getGroupedByCategory - Cache Miss Populates Cache

**Type:** Unit (Mockito + Spring Cache Test)

**Steps:**
1. First call to `queryService.getGroupedByCategory("GENERAL")`.
2. Second call with same category.
3. Count `configurationRepository.findByCategory(...)` invocations.

**Success Criteria:** Repository called exactly once (second call served from cache). Cache miss counter incremented once; cache hit counter incremented once.

---

## Section C: Backend Integration Tests (Infrastructure Layer)

### [QA-026] StudentRepository - Save and Retrieve by Student ID

**Type:** Integration (TestContainers PostgreSQL)

**Steps:**
1. Start a TestContainers PostgreSQL instance and run Flyway migrations.
2. Build a `StudentJpaEntity` via `StudentTestDataBuilder`.
3. Call `studentJpaRepository.save(entity)`.
4. Call `studentJpaRepository.findByStudentId(entity.getStudentId())`.

**Success Criteria:** Returned entity is non-empty and all fields match the saved entity.

---

### [QA-027] StudentRepository - Mobile Uniqueness Constraint Violation

**Type:** Integration (TestContainers)

**Steps:**
1. Save a student with `mobile = "9876543210"`.
2. Attempt to save a second student with the same mobile.

**Success Criteria:** `DataIntegrityViolationException` (or PostgreSQL constraint violation) is thrown.

---

### [QA-028] StudentRepository - Aadhaar Uniqueness Constraint Violation

**Type:** Integration (TestContainers)

**Steps:**
1. Save a student with `aadhaar_number = "123456789012"`.
2. Attempt to save a second student with the same Aadhaar.

**Success Criteria:** Constraint violation exception thrown.

---

### [QA-029] StudentRepository - Date of Birth Constraint: Age Below 3

**Type:** Integration (TestContainers)

**Steps:**
1. Attempt to save a `StudentJpaEntity` with `date_of_birth = CURRENT_DATE - 2 years`.

**Success Criteria:** PostgreSQL CHECK constraint violation for `chk_students_dob_min_age`.

---

### [QA-030] StudentRepository - Find By Last Name Ignore Case

**Type:** Integration (TestContainers)

**Steps:**
1. Save a student with `last_name = "SMITH"`.
2. Call `studentJpaRepository.findByLastNameIgnoreCase("smith", PageRequest.of(0, 10))`.

**Success Criteria:** Page contains the saved student.

---

### [QA-031] EnrollmentRepository - Unique Enrollment Per Year Constraint

**Type:** Integration (TestContainers)

**Steps:**
1. Save an enrollment for `student_fk = 1`, `academic_year = "2025-26"`.
2. Attempt to save a second enrollment for the same `student_fk` and `academic_year`.

**Success Criteria:** Unique constraint `uq_enrollments_student_year` violation thrown.

---

### [QA-032] EnrollmentRepository - Cascade Delete with Student

**Type:** Integration (TestContainers)

**Steps:**
1. Save a student with one enrollment.
2. Delete the student via `studentJpaRepository.deleteByStudentId(studentId)`.
3. Query enrollments for that `student_fk`.

**Success Criteria:** All enrollments for the student are deleted (ON DELETE CASCADE).

---

### [QA-033] ConfigurationRepository - Upsert Behavior

**Type:** Integration (TestContainers)

**Steps:**
1. Save a configuration setting with `category = GENERAL`, `key = "schoolName"`, `value = "Test School"`.
2. Retrieve via `findByCategoryAndKey("GENERAL", "schoolName")`.
3. Update value to `"Updated School"` and save again.
4. Retrieve again.

**Success Criteria:** Retrieved setting has `value = "Updated School"` and `version = 1`.

---

### [QA-034] Optimistic Locking - Concurrent Update Conflict

**Type:** Integration (TestContainers)

**Steps:**
1. Save a student. Retrieve two separate entity instances (both at version 0).
2. Update and save the first instance (version becomes 1).
3. Attempt to save the second instance (still at version 0).

**Success Criteria:** `ObjectOptimisticLockingFailureException` thrown on the second save.

---

## Section D: Backend Presentation Layer Tests (Controller / MockMvc)

### [QA-035] POST /api/v1/students - Returns 201 Created with Student ID

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentCommandService.registerStudent(...)` to return a `StudentResponse` with `studentId = "STD-20260212-0001"`.
2. Perform `POST /api/v1/students` with valid JSON body (firstName, lastName, dateOfBirth, mobile).
3. Assert HTTP status and response body.

**Success Criteria:** HTTP 201. Response body contains `"studentId": "STD-20260212-0001"` and `"status": "ACTIVE"`.

---

### [QA-036] POST /api/v1/students - Returns 400 for Missing Required Field

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Perform `POST /api/v1/students` with JSON body missing `mobile` field.

**Success Criteria:** HTTP 400. Response body is RFC 7807 `ErrorResponse` with `status: 400` and `errors[]` containing `field: "mobile"`.

---

### [QA-037] POST /api/v1/students - Returns 409 for Duplicate Mobile

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentCommandService.registerStudent(...)` to throw `DuplicateMobileException("9876543210")`.
2. Perform `POST /api/v1/students` with valid body.

**Success Criteria:** HTTP 409 Conflict. Response body has `status: 409`.

---

### [QA-038] POST /api/v1/students - Returns 422 for Business Rule Violation (Age)

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentCommandService.registerStudent(...)` to throw `BusinessRuleViolationException(List.of("Student age must be between 3 and 18 years"))`.
2. Perform `POST /api/v1/students`.

**Success Criteria:** HTTP 422. Response body has `status: 422` and violations in `errors[]`.

---

### [QA-039] GET /api/v1/students/{studentId} - Returns 404 When Not Found

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentQueryService.getByStudentId("STD-999")` to throw `StudentNotFoundException("STD-999")`.
2. Perform `GET /api/v1/students/STD-999`.

**Success Criteria:** HTTP 404. Response body has `status: 404` and `detail` containing `"STD-999"`.

---

### [QA-040] PUT /api/v1/students/{studentId} - Returns 409 for Optimistic Lock Failure

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentCommandService.updateStudent(...)` to throw `OptimisticLockConflictException`.
2. Perform `PUT /api/v1/students/STD-001` with valid body including `version: 0`.

**Success Criteria:** HTTP 409 Conflict.

---

### [QA-041] DELETE /api/v1/students/{studentId} - Returns 204 on Success

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentCommandService.deleteStudent("STD-001")` to execute without throwing.
2. Perform `DELETE /api/v1/students/STD-001`.

**Success Criteria:** HTTP 204 No Content. Response body is empty.

---

### [QA-042] GET /api/v1/students - Pagination Response Structure

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `StudentQueryService.searchStudents(...)` to return a `PagedStudentResponse` with `pageable: { page: 0, size: 20, totalElements: 1, totalPages: 1 }` and one student in `content`.
2. Perform `GET /api/v1/students?page=0&size=20`.

**Success Criteria:** HTTP 200. Response JSON has `content` array and `pageable` object matching `PaginationMetadata` schema.

---

### [QA-043] PUT /api/v1/configurations/{category}/{key} - Returns 200 with Updated Setting

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Mock `ConfigurationCommandService.upsertConfiguration(...)` to return a `ConfigurationResponse`.
2. Perform `PUT /api/v1/configurations/GENERAL/schoolName` with body `{ "value": "My School", "dataType": "STRING", "isEncrypted": false }`.

**Success Criteria:** HTTP 200. Response body contains `"category": "GENERAL"` and `"key": "schoolName"`.

---

### [QA-044] X-Correlation-ID Header Propagation

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Perform `POST /api/v1/students` with header `X-Correlation-ID: test-uuid-1234`.
2. Inspect the response headers.

**Success Criteria:** Response contains header `X-Correlation-ID: test-uuid-1234` (echoed back).

---

### [QA-045] Missing X-Correlation-ID - Auto-Generated

**Type:** Integration (`@WebMvcTest`)

**Steps:**
1. Perform `GET /api/v1/students` without `X-Correlation-ID` header.
2. Inspect response headers.

**Success Criteria:** Response contains a `X-Correlation-ID` header with a valid UUID format.

---

## Section E: Frontend Component Tests (Vitest + RTL + MSW)

### [QA-046] StudentRegisterDialog - Renders All Required Fields

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Render `<StudentRegisterDialog open={true} onClose={jest.fn()} onSuccess={jest.fn()} />` inside `<ToastProvider>`.
2. Query for `data-testid="input-first-name"`, `data-testid="input-last-name"`, `data-testid="input-dob"`, `data-testid="input-mobile"`.

**Success Criteria:** All four inputs are present in the document.

---

### [QA-047] StudentRegisterDialog - Shows Validation Error for Short First Name

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Render dialog.
2. Type `"A"` into `input-first-name` (1 character).
3. Click `btn-submit-register`.

**Success Criteria:** Error message `"First name must be at least 2 characters"` appears in the document.

---

### [QA-048] StudentRegisterDialog - Shows Validation Error for Invalid Mobile

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Fill `input-mobile` with `"12345"` (5 digits).
2. Click submit.

**Success Criteria:** Error message `"Mobile must be exactly 10 digits"` appears.

---

### [QA-049] StudentRegisterDialog - Shows Validation Error for Age Out of Range

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Fill `input-dob` with a date representing a 2-year-old (2 years before today).
2. Click submit.

**Success Criteria:** Error message `"Student age must be between 3 and 18 years"` appears.

---

### [QA-050] StudentRegisterDialog - Successful Submit Calls API and Closes Dialog

**Type:** Unit (Vitest + RTL + MSW)

**Steps:**
1. MSW handler returns HTTP 201 `{ studentId: "STD-20260212-0001", status: "ACTIVE", ... }` for `POST /students`.
2. Fill all required fields with valid data.
3. Click `btn-submit-register`.
4. Wait for async completion.

**Success Criteria:** `onSuccess` callback was called. Dialog is closed (or `onClose` called). Toast with `"STD-20260212-0001"` visible.

---

### [QA-051] StudentRegisterDialog - Shows Error Toast on 409 Conflict

**Type:** Unit (Vitest + RTL + MSW)

**Steps:**
1. MSW handler returns HTTP 409 for `POST /students`.
2. Submit form with valid data.

**Success Criteria:** Error toast appears. Dialog remains open.

---

### [QA-052] StudentEditDialog - Pre-Populates Form with Existing Student Data

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Render `<StudentEditDialog open={true} student={mockStudent} onClose={fn} onSuccess={fn} />`.
2. Check value of `input-first-name`.

**Success Criteria:** `input-first-name` has value equal to `mockStudent.firstName`.

---

### [QA-053] useStudents Hook - search() Populates Students State

**Type:** Unit (Vitest + RTL renderHook + MSW)

**Steps:**
1. MSW handler returns `{ content: [mockStudent], pageable: { page: 0, size: 20, totalElements: 1, totalPages: 1 } }` for `GET /students`.
2. `renderHook(() => useStudents(), { wrapper: ToastProvider })`.
3. Call `result.current.search({})`.
4. Wait for state update.

**Success Criteria:** `result.current.students` has length 1. `result.current.loading` is `false`.

---

### [QA-054] useStudents Hook - remove() Shows Success Toast

**Type:** Unit (Vitest + RTL renderHook + MSW)

**Steps:**
1. MSW handler returns HTTP 204 for `DELETE /students/STD-001`.
2. Call `result.current.remove("STD-001")`.

**Success Criteria:** Toast with text `"Student deleted"` appears in the document.

---

### [QA-055] useConfigurations Hook - fetchAll() Populates Configurations

**Type:** Unit (Vitest + RTL renderHook + MSW)

**Steps:**
1. MSW handler returns `{ configurations: [mockConfig] }` for `GET /configurations`.
2. Call `result.current.fetchAll()`.

**Success Criteria:** `result.current.configurations` has length 1.

---

### [QA-056] ConfigurationAddDialog - Validates Required Fields

**Type:** Unit (Vitest + RTL)

**Steps:**
1. Render `<ConfigurationAddDialog open={true} onClose={fn} onSuccess={fn} />`.
2. Leave `input-value` empty.
3. Click `btn-submit-config`.

**Success Criteria:** Error message `"Value is required"` appears.

---

## Section F: End-to-End Tests (Playwright)

### [QA-057] E2E: Register a New Student - Happy Path

**Type:** E2E (Playwright)

**Steps:**
1. Navigate to `http://localhost:5173/students`.
2. Click `data-testid="btn-register-student"`.
3. Fill form using `fillStudentForm` helper with: firstName `"John"`, lastName `"Doe"`, dateOfBirth `"2015-06-15"` (age ~10), mobile `"9876543210"`.
4. Click `data-testid="btn-submit-register"`.
5. Wait for toast.

**Success Criteria:** Toast message visible containing `"STD-"`. Dialog closes. Student row with `"Doe"` appears in the table.

---

### [QA-058] E2E: Search Students by Last Name

**Type:** E2E (Playwright)

**Steps:**
1. Ensure a student with last name `"Smith"` exists (seed via API before test).
2. Navigate to `/students`.
3. Fill `data-testid="search-input"` with `"Smith"`.
4. Submit search (press Enter or click search button).
5. Inspect table rows.

**Success Criteria:** `student-table` contains at least one row with `"Smith"` in the last name column.

---

### [QA-059] E2E: Edit Student Status to Inactive

**Type:** E2E (Playwright)

**Steps:**
1. Register a student via API, note the `studentId`.
2. Navigate to `/students`.
3. Find the student row and click the Edit action button.
4. Change `data-testid="select-status"` to `"INACTIVE"`.
5. Click `data-testid="btn-submit-edit"`.

**Success Criteria:** Toast success visible. Student row shows `"INACTIVE"` status badge.

---

### [QA-060] E2E: Delete a Student

**Type:** E2E (Playwright)

**Steps:**
1. Register a student via API.
2. Navigate to `/students`.
3. Find the student row and click Delete.
4. Accept browser confirmation dialog.

**Success Criteria:** Success toast visible. Student no longer appears in the table.

---

### [QA-061] E2E: Add a Configuration Setting

**Type:** E2E (Playwright)

**Steps:**
1. Navigate to `http://localhost:5173/configurations`.
2. Click `data-testid="btn-add-config"`.
3. Select `GENERAL` from `data-testid="select-category"`.
4. Fill `data-testid="input-key"` with `"schoolName"`.
5. Fill `data-testid="input-value"` with `"Test School"`.
6. Click `data-testid="btn-submit-config"`.

**Success Criteria:** Success toast visible. `config-table` shows a row with key `"schoolName"` and value `"Test School"`.

---

### [QA-062] E2E: Edit an Existing Configuration Setting

**Type:** E2E (Playwright)

**Steps:**
1. Ensure `GENERAL/schoolName` exists (seed via API or from QA-061).
2. Click Edit action on the `schoolName` row.
3. Change value to `"Updated School"`.
4. Submit.

**Success Criteria:** Toast success. Table row reflects `"Updated School"`.

---

### [QA-063] E2E: Navigate Between Students and Configurations Pages

**Type:** E2E (Playwright)

**Steps:**
1. Navigate to `/`.
2. Verify redirect to `/students` (URL check).
3. Click `data-testid="nav-configurations"` in sidebar.
4. Verify URL changes to `/configurations`.
5. Click `data-testid="nav-students"`.
6. Verify URL changes back to `/students`.

**Success Criteria:** All navigation transitions complete without page error. Active sidebar item is highlighted on each page.

---

## Section G: Non-Functional Tests

### [QA-064] API Response Time SLA - Student Search Under 200ms (p95)

**Type:** Performance (Manual / Actuator)

**Steps:**
1. Seed 100 student records via a test data script.
2. Execute `GET /api/v1/students?page=0&size=20` 50 times in rapid succession.
3. Query `GET /actuator/metrics/students.search.duration` or use `GET /actuator/prometheus`.

**Success Criteria:** `students.search.duration[0.95]` (p95) is below 200ms per architecture SLA.

---

### [QA-065] Actuator Health Check - Both Services Up

**Type:** Integration (Smoke Test)

**Steps:**
1. Start both services with `docker-compose up`.
2. `GET http://localhost:8081/actuator/health`.
3. `GET http://localhost:8082/actuator/health`.

**Success Criteria:** Both return HTTP 200 with `{ "status": "UP" }`. Database connectivity component shows `"UP"`.

---

### [QA-066] CORS Headers - Frontend Origin Allowed

**Type:** Integration

**Steps:**
1. Send `OPTIONS http://localhost:8081/api/v1/students` with `Origin: http://localhost:5173`.
2. Inspect response headers.

**Success Criteria:** Response contains `Access-Control-Allow-Origin: http://localhost:5173` and `Access-Control-Allow-Methods` includes `GET, POST, PUT, DELETE`.

---

### [QA-067] Database Isolation - Configuration Service Cannot Access Student DB

**Type:** Architecture Verification (Manual)

**Steps:**
1. Review `config-service/src/main/resources/application.yml`.
2. Verify datasource URL points to `config_db` on port 5433 only.
3. Verify no imports or beans reference `student_db`, port 5432, or any student package.

**Success Criteria:** No cross-database references exist. D-003 Global Directive is satisfied.

---

### [QA-068] Docker Build - Frontend Image Builds Successfully

**Type:** Build Verification

**Steps:**
1. From project root: `docker build -t schoolms-frontend ./frontend`.

**Success Criteria:** Build completes with exit code 0. `docker run -p 8080:80 schoolms-frontend` serves the SPA and returns HTTP 200 on `GET /`.

---

### [QA-069] TypeScript Strict Mode - No Compilation Errors

**Type:** Static Analysis

**Steps:**
1. From `frontend/`: run `npx tsc --noEmit`.

**Success Criteria:** Zero TypeScript errors. All types derived from `specs/sms_api_specification.yaml` schemas are used correctly (D-004 compliance).

---

## Test Data Builders

### Backend - StudentTestDataBuilder

```java
public class StudentTestDataBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private String mobile = "9876543210";
    private String aadhaarNumber = null;

    public StudentTestDataBuilder withMobile(String mobile) { this.mobile = mobile; return this; }
    public StudentTestDataBuilder withDateOfBirth(LocalDate dob) { this.dateOfBirth = dob; return this; }
    public StudentTestDataBuilder withLastName(String lastName) { this.lastName = lastName; return this; }

    public CreateStudentRequest buildRequest() {
        return CreateStudentRequest.builder()
            .firstName(firstName).lastName(lastName)
            .dateOfBirth(dateOfBirth).mobile(mobile)
            .aadhaarNumber(aadhaarNumber).build();
    }

    public StudentRegistrationFact buildFact(boolean mobileExists) {
        return StudentRegistrationFact.builder()
            .dateOfBirth(dateOfBirth).mobile(mobile)
            .mobileAlreadyExists(mobileExists)
            .violations(new ArrayList<>()).build();
    }
}
```

### Frontend - mockStudent (TypeScript)

```typescript
export const mockStudent: StudentResponse = {
  id: 1,
  studentId: 'STD-20260212-0001',
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '2015-06-15',
  mobile: '9876543210',
  status: 'ACTIVE',
  version: 0,
  createdAt: '2026-02-12T10:00:00Z',
  updatedAt: '2026-02-12T10:00:00Z',
};

export const mockConfiguration: Configuration = {
  id: 1,
  category: 'GENERAL',
  key: 'schoolName',
  value: 'Test School',
  dataType: 'STRING',
  isEncrypted: false,
  version: 0,
  updatedAt: '2026-02-12T10:00:00Z',
};
```
