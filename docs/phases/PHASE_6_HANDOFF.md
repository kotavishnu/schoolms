# Phase 6 Handoff Document: Frontend QA & Fixes

**From Phase:** 5 - Frontend Development
**To Phase:** 6 - Frontend QA & Fixes
**Target Agent:** frontend-qa-orchestrator
**Date:** 2026-01-22

---

## Objective

The frontend-qa-orchestrator agent must execute comprehensive testing of the React frontend, validate against screenshots for pixel-perfect accuracy, and fix issues in a loop until all tests pass and quality criteria are met.

---

## Context Provided

### Artifacts from Phase 5

#### Frontend Code
- `frontend/` - React TypeScript application
- `frontend/src/` - Source code (components, pages, services, hooks, types, utils)
- `frontend/package.json` - Dependencies
- `frontend/vite.config.ts` - Vite configuration

#### Test Requirements
- `docs/tasks/QA_TASKS.md` - QA test plan (QA-FE-001 to QA-FE-010)
- `docs/tasks/FRONTEND_TASKS.md` - Implementation reference
- `screenshots/` - Visual validation targets

---

## QA Tasks to Execute

### Component Unit Tests (QA-FE-001)
- **Target:** 80% component coverage
- **Tool:** React Testing Library + Vitest
- **Test:** All components in src/components/
- **Test:** User interactions (clicks, inputs, form submissions)
- **Test:** Conditional rendering
- **Verification:** Run `npm test` or `npm run test:coverage`

### Form Validation Tests (QA-FE-002)
- **Test:** StudentForm validation (React Hook Form + Zod)
- **Validate:** Age 3-18 years (from DOB)
- **Validate:** Phone 10 digits, unique
- **Validate:** Email format
- **Validate:** Required fields (firstName, lastName, DOB, mobile)
- **Validate:** Status enum (Active/Inactive)
- **Test:** Error messages display correctly
- **Reference:** D-008 (Zod validation must match backend Drools rules)

### Field Mapping Validation (QA-FE-003)
- **CRITICAL:** Verify service layer transforms fields correctly
- **Test:** phone (frontend) ↔ mobile (backend)
- **Test:** id (frontend) ↔ studentId (backend)
- **Test:** adhaarNumber (frontend) ↔ aadhaarNumber (backend)
- **Verify:** studentService.ts mapping functions
- **Reference:** D-007 (field mapping layer in service)

### Zod Schema Tests (QA-FE-004)
- **Verify:** Zod schemas match backend business rules
- **Test:** Student schema (age, phone, email, status)
- **Test:** Configuration schema (category, key, value)
- **Test:** Schema error messages are user-friendly
- **Reference:** specs/architecture/03-business-rules.md (BR-STU-001 to BR-STU-007)

### E2E Student CRUD Flow (QA-FE-005)
- **Tool:** Playwright or Cypress
- **Test Flow:**
  1. Navigate to Student List page
  2. Click "Add Student" button
  3. Fill form with valid data
  4. Submit form
  5. Verify student appears in list
  6. Click "Edit" on student
  7. Update student data (only Name, Mobile, Status)
  8. Submit form
  9. Verify changes reflected
  10. Click "Delete" on student
  11. Confirm deletion
  12. Verify student removed from list

### E2E Configuration CRUD Flow (QA-FE-006)
- **Tool:** Playwright or Cypress
- **Test Flow:**
  1. Navigate to Configuration List page
  2. Select category filter
  3. Verify configurations grouped by category
  4. Click "Add Configuration"
  5. Fill form (category, key, value)
  6. Submit form
  7. Verify configuration appears
  8. Edit configuration
  9. Delete configuration

### Error Handling Validation (QA-FE-007)
- **Test:** API error responses display toast notifications
- **Test:** Network errors handled gracefully
- **Test:** Form validation errors shown inline
- **Test:** 404 page for invalid routes
- **Test:** Loading states during API calls

### Loading State Validation (QA-FE-008)
- **Verify:** Skeleton components shown during data fetching
- **Test:** Loading spinners for button actions
- **Test:** Disabled state for forms during submission
- **Test:** Optimistic UI updates

### Screenshot Pixel-Perfect Validation (QA-FE-009)
- **CRITICAL:** Compare UI against screenshots/ directory
- **Validate:** Layout matches exactly
- **Validate:** Colors, fonts, spacing match
- **Validate:** Component sizes and positions match
- **Validate:** Responsive design (mobile, tablet, desktop)
- **Reference:** D-009 (screenshot pixel-perfect validation)
- **Tool:** Playwright screenshot comparison or manual validation

### Cross-Browser Testing (QA-FE-010)
- **Browsers:** Chrome, Firefox, Safari (if available), Edge
- **Test:** All CRUD operations work
- **Test:** UI renders correctly
- **Test:** Form validation works
- **Test:** Responsive design on all browsers

---

## Test Execution Strategy

### 1. Component Unit Tests
```bash
cd frontend
npm test
# OR
npm run test:coverage
```

**Expected:** All unit tests pass, 80% coverage

### 2. E2E Tests
```bash
# Start backend services (if not running)
cd backend
docker-compose up -d

# Start frontend dev server
cd frontend
npm run dev

# Run E2E tests
npm run test:e2e
# OR
npx playwright test
```

**Expected:** All E2E flows complete successfully

### 3. Screenshot Validation
```bash
# Generate current screenshots
npx playwright test --update-snapshots

# Compare with reference screenshots in screenshots/
# Manual validation or automated comparison
```

**Expected:** Pixel-perfect match (or within tolerance)

### 4. Manual Testing Checklist
- [ ] Student List page loads
- [ ] Search by lastName works
- [ ] Search by guardian works
- [ ] Add Student dialog opens
- [ ] Student form validation works
- [ ] Create student succeeds
- [ ] Edit student (only Name, Mobile, Status)
- [ ] Delete student with confirmation
- [ ] Configuration list loads
- [ ] Category filter works
- [ ] Configuration CRUD works
- [ ] Error messages display
- [ ] Loading states show
- [ ] Responsive on mobile/tablet

---

## Fix Loop Protocol

### When Tests Fail

1. **Identify Root Cause**
   - Review test failure logs
   - Check browser console errors
   - Review component code
   - Check network requests

2. **Fix Implementation**
   - Update component/page/service as needed
   - Ensure Tailwind + Shadcn/ui only (NO custom CSS)
   - Maintain field mapping in service layer
   - Ensure Zod validation matches backend

3. **Re-run Tests**
   - Run specific failing test
   - Run full test suite

4. **Verify Fix**
   - All tests pass
   - No regressions
   - Screenshot validation passes

5. **Document Issue** (in Phase 6 summary)
   - What failed
   - Root cause
   - Fix applied
   - Verification result

### Maximum Iterations
- **Soft Limit:** 3 iterations
- **Hard Limit:** 5 iterations
- **Escalation:** If >5 iterations needed, document blockers

---

## STRICT Constraints to Verify

### 1. NO CUSTOM STYLES
- **Verify:** No CSS files created (except index.css with Tailwind imports)
- **Verify:** No SASS/SCSS files
- **Verify:** No styled-components
- **Verify:** Only Tailwind utility classes used
- **Verify:** Only Shadcn/ui components used
- **Action:** If custom styles found, REMOVE and replace with Tailwind + Shadcn/ui

### 2. Field Mapping Layer (D-007)
- **Verify:** studentService.ts has mapping functions
- **Check:** phone → mobile transformation on API calls
- **Check:** mobile → phone transformation on API responses
- **Check:** id → studentId transformation
- **Test:** End-to-end field mapping works correctly

### 3. Zod Validation Matches Backend (D-008)
- **Verify:** Age validation 3-18 years (matches BR-STU-001)
- **Verify:** Phone 10 digits (matches BR-STU-002)
- **Verify:** Status enum Active/Inactive (matches BR-STU-003)
- **Verify:** Required fields (matches BR-STU-004)
- **Reference:** specs/architecture/03-business-rules.md

### 4. Screenshot Validation (D-009)
- **Verify:** UI matches screenshots pixel-perfectly
- **Tolerance:** ±2px for minor browser rendering differences
- **Check:** Layout, colors, fonts, spacing, component sizes
- **Action:** If mismatch found, adjust to match screenshots

---

## Success Criteria

### Functional
- ✅ All component unit tests pass
- ✅ All E2E tests pass (Student CRUD + Configuration CRUD)
- ✅ Form validation works (Zod schemas)
- ✅ Field mapping verified (phone↔mobile, id↔studentId)
- ✅ Error handling works (toast notifications)
- ✅ Loading states work (skeleton components)

### Visual
- ✅ NO custom CSS files (only Tailwind + Shadcn/ui)
- ✅ Screenshot validation passed (pixel-perfect or within tolerance)
- ✅ Responsive design works (mobile, tablet, desktop)
- ✅ Cross-browser compatibility verified

### Coverage
- ✅ Component coverage: 80%
- ✅ E2E coverage: All critical user flows
- ✅ Screenshot validation: 100% pages covered

### Quality
- ✅ No console errors
- ✅ No runtime errors
- ✅ Accessible (basic ARIA attributes)
- ✅ Fast load times (<2 seconds)

---

## Integration Testing (with Backend)

### Prerequisites
- Backend services running (Phase 4 complete)
- Student Service on port 8081
- Configuration Service on port 8082
- Databases populated with test data

### Integration Test Scenarios (QA-INT-001)
1. **Create Student via Frontend → Verify in Backend DB**
2. **Update Student via Frontend → Verify field mapping (mobile, studentId)**
3. **Delete Student via Frontend → Verify cascade deletion**
4. **Search Student via Frontend → Verify query parameters**
5. **Create Configuration via Frontend → Verify category grouping**
6. **Backend validation error → Verify frontend error display**

---

## Expected Deliverables

1. **Test Execution Report**
   - All test results (pass/fail)
   - Coverage reports
   - Screenshot validation results
   - Cross-browser test results

2. **Issue Log**
   - List of issues found
   - Root causes
   - Fixes applied
   - Verification results

3. **Phase 6 Summary Document**
   - Testing summary
   - Issues and resolutions
   - Quality metrics achieved
   - Final handoff

4. **Fixed Codebase**
   - All tests passing
   - All quality criteria met
   - Production-ready frontend

---

## Files to Reference

1. **docs/tasks/QA_TASKS.md** - QA test plan (QA-FE-001 to QA-FE-010, QA-INT-001)
2. **specs/architecture/06-frontend-implementation-guide.md** - Implementation patterns
3. **specs/architecture/03-business-rules.md** - Validation rules reference
4. **screenshots/** - Visual validation targets
5. **docs/tasks/FRONTEND_TASKS.md** - Implementation reference

---

## Agent Invocation

```
Task tool with:
subagent_type: frontend-qa-orchestrator
description: "Test and fix frontend application"
prompt: "
Execute comprehensive testing of frontend application following QA_TASKS.md (QA-FE-001 to QA-FE-010 + QA-INT-001).

Test Categories:
1. Component unit tests (80% coverage)
2. Form validation (Zod schemas)
3. Field mapping (phone↔mobile, id↔studentId) - D-007
4. Zod validation matches backend - D-008
5. E2E tests (Student + Configuration CRUD)
6. Error handling
7. Loading states
8. Screenshot validation - D-009 (pixel-perfect)
9. Cross-browser testing

Fix Loop:
- Identify failures
- Fix implementation (Tailwind + Shadcn/ui ONLY)
- Re-run tests
- Verify (max 5 iterations)

PROCEED WITHOUT ASKING FOR PERMISSION.
Document all issues and fixes.
"
```

---

## Handoff Complete

**Status:** ✅ READY FOR PHASE 6
**Target Agent:** frontend-qa-orchestrator
**Prerequisite:** Phase 5 (Frontend Development) must complete first
