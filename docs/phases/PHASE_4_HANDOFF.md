# Phase 4 Handoff Document: Backend QA & Fixes

**From Phase:** 3 - Backend Development
**To Phase:** 4 - Backend QA & Fixes
**Target Agent:** backend-qa-orchestrator
**Date:** 2026-01-22

---

## Objective

The backend-qa-orchestrator agent must execute comprehensive testing of both backend microservices, identify issues, and fix them in a loop until all tests pass and quality criteria are met.

---

## Context Provided

### Artifacts from Phase 3

#### Backend Code
- `backend/student-service/` - Student microservice (port 8081)
- `backend/configuration-service/` - Configuration microservice (port 8082)
- `backend/docker-compose.yml` - Database and Redis setup
- `docs/tasks/school_management.sql` - Database schema

#### Test Requirements
- `docs/tasks/QA_TASKS.md` - QA test plan (QA-BE-001 to QA-BE-009)
- `docs/tasks/BACKEND_TASKS.md` - Implementation reference
- `specs/architecture/03-business-rules.md` - Drools rules to validate

---

## QA Tasks to Execute

### Backend Unit Tests (QA-BE-001)
- **Target:** 95% domain layer coverage
- **Test:** All entity business methods
- **Test:** Student entity (generateStudentId, activate, deactivate, isActive)
- **Test:** StudentStatus enum
- **Verification:** Run `mvn test` or `gradle test`

### Drools Business Rules Tests (QA-BE-002)
- **Target:** 100% rule coverage (all 7 rules)
- **Rules to Validate:**
  - BR-STU-001: Age 3-18 years validation
  - BR-STU-002: Unique mobile number
  - BR-STU-003: Status enum (ACTIVE/INACTIVE)
  - BR-STU-004: Required fields (firstName, lastName, DOB, mobile)
  - BR-STU-005: Edit restrictions (only Name, Mobile, Status editable)
  - BR-STU-006: StudentID auto-generation
  - BR-STU-007: Default status ACTIVE
- **Verification:** DroolsValidationService unit tests

### Service Layer Integration Tests (QA-BE-003, QA-BE-004)
- **Target:** 85% application layer coverage
- **Test:** Student Service CRUD operations
- **Test:** Configuration Service CRUD operations
- **Test:** Category-based retrieval for configurations
- **Verification:** Integration tests with Testcontainers

### API Contract Validation (QA-BE-005)
- **Verify:** All endpoints match API specification
- **Test:** Request/response formats
- **Test:** Error responses (400, 404, 500)
- **Test:** Status codes (200, 201, 204)
- **Reference:** sms_api_specification.yaml (if exists)

### Database Constraints (QA-BE-006)
- **Verify:** CHECK constraints (age, mobile, status, category)
- **Verify:** UNIQUE constraints (studentId, mobile, email, category+key)
- **Verify:** Foreign keys (enrollments → students)
- **Verify:** Optimistic locking (@Version field)
- **Test:** Constraint violations return proper errors

### Microservices Isolation (QA-BE-007)
- **CRITICAL:** Verify ZERO cross-service database access
- **Test:** Student Service connects ONLY to student_db (port 5433)
- **Test:** Configuration Service connects ONLY to config_db (port 5434)
- **Verify:** No shared database connections in code
- **Reference:** D-010 (database-per-service isolation)

### Redis Caching (QA-BE-008)
- **Verify:** Student Service uses Redis DB 0
- **Verify:** Configuration Service uses Redis DB 1
- **Test:** Cache hits/misses
- **Test:** Cache invalidation on updates
- **Verify:** Separate keyspaces (no collision)

### Performance Testing (QA-BE-009)
- **Target:** p95 response time <200ms
- **Test:** Load testing (100 concurrent users)
- **Test:** N+1 query prevention (@EntityGraph, batch fetching)
- **Test:** Database connection pool tuning (HikariCP)
- **Tools:** JMeter, Gatling, or Apache Bench

---

## Test Execution Strategy

### 1. Unit Tests First
```bash
cd backend/student-service
mvn test
# OR
gradle test

cd ../configuration-service
mvn test
# OR
gradle test
```

**Expected:** All unit tests pass, 95% domain coverage

### 2. Integration Tests
```bash
# Start Docker Compose
docker-compose up -d

# Run integration tests
mvn integration-test
# OR
gradle integrationTest
```

**Expected:** All integration tests pass, API contracts validated

### 3. Manual Testing (if needed)
- Use SpringDoc UI: http://localhost:8081/swagger-ui.html
- Use SpringDoc UI: http://localhost:8082/swagger-ui.html
- Test endpoints with Postman/curl

### 4. Performance Tests
```bash
# Example with Apache Bench
ab -n 1000 -c 100 http://localhost:8081/api/v1/students
```

**Expected:** p95 <200ms

---

## Fix Loop Protocol

### When Tests Fail

1. **Identify Root Cause**
   - Review test failure logs
   - Check stack traces
   - Review relevant code

2. **Fix Implementation**
   - Update entity/service/controller as needed
   - Ensure fix aligns with architecture documents
   - Maintain Global Directives (D-001 to D-010)

3. **Re-run Tests**
   - Run specific failing test
   - Run full test suite

4. **Verify Fix**
   - All tests pass
   - No regressions introduced
   - Coverage targets maintained

5. **Document Issue** (in Phase 4 summary)
   - What failed
   - Root cause
   - Fix applied
   - Verification result

### Maximum Iterations
- **Soft Limit:** 3 iterations
- **Hard Limit:** 5 iterations
- **Escalation:** If >5 iterations needed, document blockers and escalate

---

## Success Criteria

### Functional
- ✅ All unit tests pass
- ✅ All integration tests pass
- ✅ All 7 Drools rules validated (BR-STU-001 to BR-STU-007)
- ✅ Database constraints enforced
- ✅ API contract compliance verified
- ✅ No cross-service database access

### Coverage
- ✅ Domain layer: 95% coverage
- ✅ Application layer: 85% coverage
- ✅ Infrastructure layer: 70% coverage

### Performance
- ✅ p95 response time <200ms
- ✅ No N+1 queries
- ✅ Redis caching operational

### Quality
- ✅ No critical bugs
- ✅ No security vulnerabilities
- ✅ SpringDoc UI accessible and accurate
- ✅ Docker Compose setup working

---

## Global Directives to Verify

- **D-001:** Spring Boot 3.3.5 + SpringDoc 2.6.0 (check pom.xml/build.gradle)
- **D-002:** MapStruct used for all DTO mapping (no manual mapping)
- **D-003:** @Version field on all entities (Student, Enrollment, ConfigurationSetting)
- **D-010:** Database-per-service isolation (verify connection configs)

---

## Expected Deliverables

1. **Test Execution Report**
   - All test results (pass/fail)
   - Coverage reports
   - Performance test results

2. **Issue Log**
   - List of issues found
   - Root causes
   - Fixes applied
   - Verification results

3. **Phase 4 Summary Document**
   - Testing summary
   - Issues and resolutions
   - Quality metrics achieved
   - Handoff to Phase 5/6

4. **Fixed Codebase**
   - All tests passing
   - All quality criteria met
   - Ready for frontend integration

---

## Files to Reference

1. **docs/tasks/QA_TASKS.md** - QA test plan (QA-BE-001 to QA-BE-009)
2. **specs/architecture/03-business-rules.md** - Drools rules reference
3. **specs/architecture/05-backend-implementation-guide.md** - Implementation patterns
4. **docs/tasks/BACKEND_TASKS.md** - Implementation reference

---

## Agent Invocation

```
Task tool with:
subagent_type: backend-qa-orchestrator
description: "Test and fix backend services"
prompt: "
Execute comprehensive testing of backend services following QA_TASKS.md (QA-BE-001 to QA-BE-009).

Test Categories:
1. Unit tests (95% domain coverage)
2. Drools rules (all 7 rules: BR-STU-001 to BR-STU-007)
3. Integration tests (API contracts)
4. Database constraints
5. Microservices isolation (D-010)
6. Redis caching
7. Performance (p95 <200ms)

Fix Loop:
- Identify failures
- Fix implementation
- Re-run tests
- Verify (max 5 iterations)

PROCEED WITHOUT ASKING FOR PERMISSION.
Document all issues and fixes.
"
```

---

## Handoff Complete

**Status:** ✅ READY FOR PHASE 4
**Target Agent:** backend-qa-orchestrator
**Prerequisite:** Phase 3 (Backend Development) must complete first
