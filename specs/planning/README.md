# School Management System - SDLC Planning Deliverables

## Overview
This directory contains the comprehensive implementation plan for the School Management System Phase 1, broken down into atomic, sequential tasks for Backend, Frontend, and QA teams.

**Execution Model:** Waterfall / Single-Pass Implementation
**Generated:** 2026-02-03
**Planner Agent:** Technical SDLC Planner

---

## Deliverables Summary

### 1. Database Schema (`school_management.sql`)
**Purpose:** Single SQL script to create all database objects for Student and Configuration modules.

**Contents:**
- Students table (with age check constraint, mobile uniqueness)
- Enrollments table (with foreign key cascade)
- Configurations table (with category enum constraint)
- Seed data for initial configurations
- Comments for documentation

**Simplifications Applied:**
- NO indexes (per implementation constraints)
- NO triggers (StudentID generation in application layer)
- NO stored procedures
- NO migration tools (Flyway/Liquibase)

**Usage:**
```bash
psql -U postgres -d student_db -f school_management.sql
psql -U postgres -d config_db -f school_management.sql
```

---

### 2. Backend Implementation Plan (`BACKEND_TASKS.md`)
**Purpose:** Sequential task checklist for Spring Boot Developer implementing microservices.

**Structure:**
- 30 tasks organized into 9 phases
- Each task includes:
  - Task ID (BE-001 to BE-030)
  - Goal statement
  - Technical details (specific classes, methods, annotations)
  - Dependencies (prerequisite tasks)
  - Acceptance criteria

**Key Phases:**
1. **Project Setup** (BE-001 to BE-003): Initialize Spring Boot projects, configure databases
2. **Domain Layer** (BE-004 to BE-005): Create rich domain models and repository interfaces
3. **Infrastructure Layer** (BE-006 to BE-009): Implement JPA entities, Drools rules
4. **Application Layer** (BE-010 to BE-013): Create services, DTOs, mappers
5. **Presentation Layer** (BE-014 to BE-016): Implement REST controllers, exception handling
6. **Cross-Cutting** (BE-017 to BE-019): CORS, caching, actuator
7. **Testing** (BE-020 to BE-022): Unit and integration tests
8. **Configuration Service** (BE-023 to BE-028): Parallel implementation
9. **Deployment** (BE-029 to BE-030): Dockerization, documentation

**Critical Constraints Enforced:**
- NO JWT/OAuth (Phase 1 scope)
- DDD layered architecture mandatory
- Drools for business rules externalization
- Optimistic locking with @Version
- N+1 query prevention with @EntityGraph
- MapStruct for DTO-domain mapping
- Spring Boot 3.5.0 + SpringDoc OpenAPI 2.7.x (per D-001 directive)

**Estimated Effort:** 60-80 developer hours

---

### 3. Frontend Implementation Plan (`FRONTEND_TASKS.md`)
**Purpose:** Sequential task checklist for React Developer building UI with strict reference code reuse.

**Structure:**
- 26 tasks organized into 6 phases
- Each task includes:
  - Task ID (FE-001 to FE-026)
  - Goal statement
  - Components to copy/modify
  - Integration points
  - Forbidden actions (strict constraints)

**Key Phases:**
1. **Project Setup** (FE-001 to FE-006): Initialize React, copy reference code
2. **API Integration** (FE-007 to FE-010): Create service layer, interceptors
3. **Form Validation** (FE-011 to FE-012): Zod schemas, React Query hooks
4. **Component Integration** (FE-013 to FE-018): Refactor to use real APIs
5. **Production Readiness** (FE-019 to FE-024): Docker, error boundaries, performance
6. **Testing** (FE-025 to FE-026): Manual testing, visual regression

**CRITICAL CONSTRAINT:**
The code in `frontend/reference-code/` is the **authoritative implementation**. Tasks mandate:
- ✅ COPY all UI components, styles, and layouts from reference code
- ✅ REFACTOR only to replace mock data with API calls
- ❌ FORBIDDEN: Creating new UI components
- ❌ FORBIDDEN: Writing custom CSS
- ❌ FORBIDDEN: Changing component structure

**Strict Reuse Policy:**
- `FE-003`: Copy `theme.css`, `tailwind.css`, `index.css` (exact 1:1)
- `FE-004`: Copy entire `components/ui/` directory (shadcn/ui primitives)
- `FE-005`: Copy all feature components (StudentsPage, StudentDialog, etc.)
- `FE-006`: Copy type definitions
- `FE-013 to FE-015`: Refactor ONLY to replace mock data with API integration

**Key Integrations:**
- React Hook Form + Zod for validation
- React Query (TanStack Query) for server state
- Axios interceptors for error handling
- Sonner for toast notifications
- Debounced search (500ms)

**Estimated Effort:** 40-60 developer hours

---

### 4. QA Test Plan (`QA_TASKS.md`)
**Purpose:** Comprehensive test checklist covering unit, integration, E2E, performance, and security testing.

**Structure:**
- 30 test cases organized into 9 phases
- Each test case includes:
  - Test ID (QA-001 to QA-030)
  - Test type (Unit, Integration, E2E, Performance, Security, UAT)
  - Component under test
  - Test scenarios with GIVEN-WHEN-THEN format
  - Success criteria

**Key Phases:**
1. **Backend Unit Tests** (QA-001 to QA-005): Domain models, Drools rules, services
2. **Backend Integration Tests** (QA-006 to QA-010): Student API endpoints with Testcontainers
3. **Configuration Service Tests** (QA-011 to QA-012): Cache behavior, CRUD operations
4. **Frontend Unit Tests** (QA-013 to QA-014): Zod schemas, React Query hooks
5. **E2E Tests** (QA-015 to QA-020): Full-stack user flows
6. **Performance Tests** (QA-021 to QA-023): API response time, frontend Lighthouse, database queries
7. **Security Tests** (QA-024 to QA-026): SQL injection, XSS, CORS
8. **DevOps Tests** (QA-027 to QA-028): Docker builds, health checks
9. **UAT** (QA-029 to QA-030): User journeys

**Test Pyramid Adherence:**
- Unit Tests: 60% (15 test cases)
- Integration Tests: 30% (9 test cases)
- E2E Tests: 10% (3 test cases)

**Coverage Targets:**
- Domain Layer: ≥ 95% line coverage
- Application Layer: ≥ 85% line coverage
- Frontend Components: Visual regression + manual testing

**Performance Targets:**
- API Response Time: P95 < 200ms
- Frontend Lighthouse: Score > 90
- Database Queries: < 50ms
- Cache Hit Ratio: > 80%

**Estimated Effort:** 40-60 QA hours

---

## Task Dependencies & Critical Path

### Backend Critical Path
```
BE-001 (Project Setup)
  ↓
BE-003 (Database Schema)
  ↓
BE-004 (Domain Models)
  ↓
BE-012 (Student Service)
  ↓
BE-014 (Student Controller)
  ↓
BE-021 (Integration Tests)
```

### Frontend Critical Path
```
FE-001 (Project Setup)
  ↓
FE-003 (Copy Styles)
  ↓
FE-005 (Copy Components)
  ↓
FE-009 (Student API Service)
  ↓
FE-013 (Refactor StudentsPage)
  ↓
FE-025 (Manual Testing)
```

### QA Critical Path
```
QA-006 (Student API Integration Tests)
  ↓
QA-015 (Student Registration E2E)
  ↓
QA-029 (UAT - Student Registration)
```

---

## Implementation Sequence

### Week 1: Foundation
**Backend:**
- BE-001 to BE-003: Project setup, database schema
- BE-004 to BE-009: Domain layer, infrastructure layer

**Frontend:**
- FE-001 to FE-006: Project setup, copy reference code

**QA:**
- QA-001 to QA-005: Backend unit tests

### Week 2: Core Features
**Backend:**
- BE-010 to BE-016: Application layer, presentation layer
- BE-023 to BE-027: Configuration service (parallel)

**Frontend:**
- FE-007 to FE-012: API integration, validation

**QA:**
- QA-006 to QA-012: Backend integration tests

### Week 3: Integration & Testing
**Backend:**
- BE-017 to BE-022: Cross-cutting concerns, testing
- BE-028: Configuration service tests

**Frontend:**
- FE-013 to FE-018: Component integration, routing

**QA:**
- QA-013 to QA-020: Frontend tests, E2E tests

### Week 4: Production Readiness
**Backend:**
- BE-029 to BE-030: Dockerization, documentation

**Frontend:**
- FE-019 to FE-024: Production config, optimization

**QA:**
- QA-021 to QA-030: Performance, security, UAT

---

## Key Constraints & Guidelines

### Database
✅ **Include:** Tables, Primary Keys, Foreign Keys, Basic Constraints
❌ **Exclude:** Indexes, Triggers, Stored Functions, Migration tools

### Backend
✅ **Include:** DDD layers, Drools rules, Optimistic locking, MapStruct, Actuator
❌ **Exclude:** JWT/OAuth, Complex security, Event-driven patterns (Phase 1)

### Frontend
✅ **Include:** Reference code reuse, Service layer, Zod+React Hook Form, React Query
❌ **Exclude:** New UI components, Custom CSS, Direct Axios calls in components

### Testing
✅ **Include:** Unit, Integration (Testcontainers), E2E, Performance, Security
❌ **Exclude:** Load testing beyond basic performance checks

---

## Acceptance Criteria Summary

### Backend Completion
- [ ] All 30 backend tasks completed (BE-001 to BE-030)
- [ ] Student Service running on port 8081
- [ ] Configuration Service running on port 8082
- [ ] OpenAPI documentation accessible at /swagger-ui.html
- [ ] All Drools rules firing correctly
- [ ] Unit test coverage ≥ 85%
- [ ] Integration tests passing with Testcontainers
- [ ] Docker containers build and run successfully

### Frontend Completion
- [ ] All 26 frontend tasks completed (FE-001 to FE-026)
- [ ] UI matches reference code 1:1 (visual regression pass)
- [ ] All forms validate with Zod schemas
- [ ] Service layer handles all HTTP methods
- [ ] Toast notifications functional
- [ ] Pagination and search working
- [ ] Docker build passes
- [ ] Lighthouse score > 90

### QA Completion
- [ ] All 30 test cases executed (QA-001 to QA-030)
- [ ] Backend unit tests passing (≥ 95% coverage for domain)
- [ ] Backend integration tests passing
- [ ] E2E tests covering critical user flows
- [ ] Performance targets met (API < 200ms P95)
- [ ] Security tests passed (no SQL injection, XSS blocked)
- [ ] UAT signed off by stakeholders

---

## Reference Documentation

### Source of Truth
- **Requirements:** `@specs/REQUIREMENTS.md`
- **API Specification:** `@specs/sms_api_specification.yaml`
- **Architecture:** `@specs/architecture/01-system-architecture.md`
- **Database Design:** `@specs/architecture/02-database-design.md`
- **Business Rules:** `@specs/architecture/03-business-rules.md`
- **Backend Guide:** `@specs/architecture/05-backend-implementation-guide.md`
- **Frontend Guide:** `@specs/architecture/06-frontend-implementation-guide.md`
- **Frontend Design:** `@specs/FRONTEND_DESIGN_SPEC.md`
- **Testing Strategy:** `@specs/TESTING_STRATEGY.md`
- **Lessons Learned:** `@specs/LESSONS_LEARNED.md`

### Reference Code
- **Location:** `@frontend/reference-code/`
- **Status:** READ ONLY - Authoritative UI implementation
- **Usage:** Copy files to `src/` directory, refactor to integrate APIs

---

## Getting Started

### For Backend Developers
1. Read `BACKEND_TASKS.md`
2. Start with BE-001 (Project Setup)
3. Follow tasks sequentially (respect dependencies)
4. Check acceptance criteria before marking task complete
5. Refer to `05-backend-implementation-guide.md` for patterns

### For Frontend Developers
1. Read `FRONTEND_TASKS.md`
2. Start with FE-001 (Project Setup)
3. Follow strict reuse policy (copy reference code)
4. NEVER create new UI components or write custom CSS
5. Refer to `06-frontend-implementation-guide.md` for patterns

### For QA Engineers
1. Read `QA_TASKS.md`
2. Start with QA-001 (Backend Unit Tests) once BE-004 is complete
3. Run integration tests after BE-014
4. Execute E2E tests after FE-020 and BE-029
5. Refer to `TESTING_STRATEGY.md` for coverage targets

---

## Support & Escalation

### Questions or Blockers
- Review architectural documentation in `@specs/architecture/`
- Check `LESSONS_LEARNED.md` for known issues and resolutions
- Consult OpenAPI specification for API contract details

### Specification Ambiguity
- Priority 1: OpenAPI specification (`sms_api_specification.yaml`)
- Priority 2: Architecture documents
- Priority 3: REQUIREMENTS.md

### Design Questions
- Frontend: Refer to reference code in `frontend/reference-code/`
- Backend: Refer to `05-backend-implementation-guide.md` code examples

---

## Change Log

| Date | Version | Changes |
|------|---------|---------|
| 2026-02-03 | 1.0 | Initial SDLC plan creation - 30 backend tasks, 26 frontend tasks, 30 QA tasks |

---

**Document Owner:** Technical SDLC Planner Agent
**Review Cycle:** After each iteration
**Next Review:** After Phase 1 completion
