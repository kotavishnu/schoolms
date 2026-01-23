# Phase 2 Handoff Document: SDLC Planning

**From Phase:** 1 - Architecture Design
**To Phase:** 2 - SDLC Planning
**Target Agent:** sdlc-planner
**Date:** 2026-01-22

---

## Objective

The sdlc-planner agent must analyze the architectural blueprint and feature requirements to create a detailed breakdown of technical tasks for Backend, Frontend, and QA teams.

---

## Context Provided

### 1. Preserved Artifacts (Post-/clear)

The following files will be preserved and available:

#### Requirements
- `specs/REQUIREMENTS.md` - Core product requirements and acceptance criteria

#### Architecture Documents (specs/architecture/)
- `01-system-architecture.md` - System design, microservices boundaries, DDD layers
- `02-database-design.md` - ERD, DDL, field alignment matrix
- `03-business-rules.md` - Drools integration, DRL files, validation rules
- `04-security-architecture.md` - Phase 1/2 security blueprints, RBAC
- `05-backend-implementation-guide.md` - Implementation patterns, mandatory constraints
- `06-frontend-implementation-guide.md` - Directory structure, data mapping rules

#### API Contracts
- `sms_api_specification.yaml` - OpenAPI 3.0 specification (if exists in specs/)

#### Phase Summaries
- `docs/phases/PHASE_1_ARCHITECTURE_SUMMARY.md` - This phase's summary

---

## Key Architectural Decisions

### Microservices Architecture
- **Student Service:** Port 8081 → student_db (port 5433) → Redis DB 0
- **Configuration Service:** Port 8082 → config_db (port 5434) → Redis DB 1
- **Constraint:** ZERO cross-service database access (absolute isolation)

### Technology Stack

#### Backend
```
Framework:     Spring Boot 3.3.5
Language:      Java 21
Database:      PostgreSQL 18
Caching:       Redis 7.x
Rules Engine:  Drools 9.44.0.Final
API Docs:      SpringDoc 2.6.0 (MANDATORY with Spring Boot 3.3.5 - Global Directive D-001)
Migrations:    Flyway
```

#### Frontend
```
Framework:     React 18.3 with TypeScript 5
Build Tool:    Vite 5
Styling:       Tailwind CSS 4 + Shadcn/ui
Forms:         React Hook Form + Zod validation
HTTP Client:   Axios with interceptors
```

#### Data Layer
```
ORM:           Spring Data JPA with Hibernate
Mapping:       MapStruct for DTO conversions
Locking:       Optimistic locking with @Version
Connection:    HikariCP (tuned for high performance)
```

### Design Patterns
- **Layered Architecture:** Presentation → Application → Domain → Infrastructure
- **Domain Patterns:** Repository, Service Layer, DTO, Value Objects
- **Validation:** Drools rules engine (student-validation.drl)
- **Caching Strategy:** Redis for frequently accessed data (L2 cache)

---

## Critical Constraints for Task Planning

### Backend Constraints (MANDATORY)

1. **Dependency Compatibility (D-001)**
   - Spring Boot 3.3.5 + SpringDoc 2.6.0 (exact versions)
   - Failure to specify this causes incompatibility errors

2. **Microservices Isolation**
   - Student Service and Configuration Service must be COMPLETELY independent
   - No shared database access (database-per-service pattern)
   - Communication only via REST APIs (if needed in future phases)

3. **Business Rules with Drools**
   - All validation logic MUST use Drools rule engine
   - student-validation.drl file to contain:
     - BR-STU-001: Age between 3-18 validation
     - BR-STU-002: Unique mobile number validation
     - BR-STU-003: Status enum validation (Active/Inactive)
     - BR-STU-004: Required field validation
     - BR-STU-005: Field edit restrictions (only Name, Mobile, Status editable)
   - DroolsValidationService integration in service layer

4. **Database Schema**
   - Use Flyway migrations (sequential V1__, V2__, etc.)
   - DDL provided in 02-database-design.md sections 3.1 and 3.2
   - Must include optimistic locking (@Version field)
   - Must include audit fields (created_at, updated_at)

5. **API Contract Adherence**
   - 100% compliance with sms_api_specification.yaml
   - Field naming: backend uses `mobile` (not `phone`), `studentId` (not `id`)

### Frontend Constraints (STRICT)

1. **No Custom Styles**
   - ONLY use Tailwind CSS 4 + Shadcn/ui components
   - NO custom CSS files or styled-components
   - Rationale: Ensures consistency and avoids style conflicts

2. **Reference Code Reuse**
   - If screenshots show existing UI patterns, MUST reuse those components
   - Check screenshots/ directory for visual validation targets

3. **Field Mapping Layer**
   - Frontend models use: `phone`, `id`, `firstName`, `lastName`
   - Backend API uses: `mobile`, `studentId`, `firstName`, `lastName`
   - Service layer MUST handle mapping (see 06-frontend-implementation-guide.md section 3.2.1)

4. **Validation Rules (Zod)**
   ```typescript
   age: 3-18 years (calculated from DOB)
   phone: 10 digits, unique
   firstName/lastName: required, non-empty
   email: valid email format
   status: enum("Active", "Inactive")
   ```

5. **Directory Structure (ENFORCED)**
   ```
   src/
   ├── components/     # Reusable UI components (Button, Input, etc.)
   ├── pages/          # Route pages (StudentList, StudentForm, etc.)
   ├── services/       # API client with Axios (studentService.ts, configService.ts)
   ├── hooks/          # Custom React hooks
   ├── types/          # TypeScript interfaces matching backend DTOs
   └── utils/          # Helper functions
   ```

### QA Constraints

1. **Test Coverage Targets**
   - Domain layer: 95% coverage
   - Application layer: 85% coverage
   - Infrastructure layer: 70% coverage

2. **Performance Targets**
   - p95 response time: <200ms
   - Database query optimization (N+1 prevention)

3. **Screenshot Validation**
   - Frontend MUST match screenshots pixel-perfectly
   - Screenshots located in: `screenshots/` directory

---

## Expected Task Breakdown Structure

### Backend Tasks (Senior Backend Developer)

#### Student Service Tasks
1. Project setup (Spring Boot 3.3.5, Java 21, Maven/Gradle)
2. Database setup (PostgreSQL 18, Flyway migrations)
3. Redis configuration (separate DB 0)
4. Entity layer (Student, Enrollment JPA entities with @Version)
5. Repository layer (Spring Data JPA)
6. Drools integration (student-validation.drl, DroolsValidationService)
7. Service layer (StudentService with business logic)
8. DTO layer (MapStruct for Student DTOs)
9. Controller layer (REST endpoints per API contract)
10. Exception handling (GlobalExceptionHandler)
11. Spring Actuator + custom metrics (Micrometer)
12. Unit tests (95% coverage for domain)

#### Configuration Service Tasks
1. Project setup (Spring Boot 3.3.5)
2. Database setup (PostgreSQL 18 on port 5434, Flyway)
3. Redis configuration (separate DB 1)
4. Entity layer (ConfigurationSetting JPA entity)
5. Repository layer (category-based retrieval)
6. Service layer (CRUD operations, category grouping)
7. DTO layer (ConfigurationSettingDTO)
8. Controller layer (REST endpoints)
9. Exception handling
10. Unit tests

#### Shared Backend Tasks
11. Docker Compose setup (PostgreSQL × 2, Redis)
12. API documentation (SpringDoc 2.6.0 UI)
13. Logging configuration (JSON format, correlation IDs)
14. Integration tests (API contract validation)

### Frontend Tasks (Senior Frontend Developer)

#### Foundation Tasks
1. Vite project setup (React 18.3, TypeScript 5)
2. Tailwind CSS 4 configuration
3. Shadcn/ui component installation
4. Directory structure creation (components/pages/services/hooks/types/utils)
5. Axios service layer (base client with interceptors)
6. TypeScript types (matching backend DTOs with field mapping)

#### Student Management UI
7. StudentList page (table with search by lastName/guardian)
8. StudentForm page (create/edit with React Hook Form)
9. Student detail view
10. Zod validation schemas (age, phone, email, etc.)
11. Service layer (studentService.ts with field mapping: phone↔mobile, id↔studentId)
12. Error handling (toast notifications)
13. Loading states (Shadcn/ui Skeleton components)

#### Configuration Management UI
14. ConfigurationList page (grouped by category)
15. ConfigurationForm page (CRUD for key-value pairs)
16. Category filter/selector
17. Service layer (configService.ts)

#### Optimization Tasks
18. Code splitting (React.lazy for routes)
19. Debounced search (useDebounce hook)
20. Memoization (useMemo for expensive calculations)

#### Validation Tasks
21. Screenshot comparison (screenshots/ directory)
22. Pixel-perfect validation checklist
23. Responsive design testing

### QA Tasks (Backend QA + Frontend QA Orchestrators)

#### Backend QA
1. Unit test execution (verify 95% domain coverage)
2. Integration test execution (API contract compliance)
3. Drools rule validation (all 7 business rules: BR-STU-001 to BR-STU-007)
4. Database isolation testing (verify no cross-service queries)
5. Performance testing (p95 <200ms)
6. Redis caching validation
7. Spring Actuator metrics verification

#### Frontend QA
8. Unit test execution (React Testing Library)
9. E2E test execution (Playwright/Cypress)
10. Screenshot validation (pixel-perfect comparison)
11. Form validation testing (all Zod rules)
12. Field mapping verification (phone↔mobile, id↔studentId)
13. Error handling validation
14. Loading state validation

#### Integration QA
15. End-to-end flow testing (create → edit → delete student)
16. Cross-browser testing (Chrome, Firefox, Safari)
17. Responsive design validation (mobile, tablet, desktop)
18. API error propagation testing (backend errors → frontend UI)

---

## Validation Rules Summary (For Task Specification)

### Student Validation Rules
- **Age:** 3-18 years at registration (calculated from DOB)
- **Mobile:** Unique, 10 digits
- **Status:** Enum("Active", "Inactive"), default "Active"
- **StudentID:** Auto-generated (BIGSERIAL)
- **Editable Fields:** ONLY Name, Mobile, Status (all other fields immutable after creation)

### Configuration Validation Rules
- **Category:** Required, enum("General", "Academic", "Financial")
- **Key:** Required, unique within category
- **Value:** Required, text
- **SettingID:** Auto-generated

---

## Global Directives to Enforce

From LESSONS_LEARNED.md:

- **D-001:** Spring Boot 3.3.5 + SpringDoc 2.6.0 compatibility
- **D-002:** Use MapStruct for DTO mapping (avoid manual mapping)
- **D-003:** Optimistic locking with @Version (prevent lost updates)
- **D-004:** HikariCP tuning (maximumPoolSize=10, connectionTimeout=30000ms)
- **D-005:** Prevent N+1 queries (use @EntityGraph, batch fetching)
- **D-006:** JSON logging with correlation IDs (traceable across services)
- **D-007:** Frontend field mapping layer (phone↔mobile, id↔studentId)
- **D-008:** Zod validation MUST match backend Drools rules
- **D-009:** Screenshot validation checklist (pixel-perfect requirement)
- **D-010:** Database-per-service isolation (ZERO cross-service access)

---

## Success Criteria for Phase 2

The sdlc-planner agent will be successful when:

1. ✅ **Task Breakdown Completeness**
   - All backend tasks identified (Student Service + Configuration Service + shared)
   - All frontend tasks identified (foundation + Student UI + Config UI + optimization)
   - All QA tasks identified (backend + frontend + integration)

2. ✅ **Task Specificity**
   - Each task has clear acceptance criteria
   - Tasks reference specific architecture documents
   - Tasks include technology/pattern constraints

3. ✅ **Dependency Mapping**
   - Task dependencies clearly identified
   - Critical path identified (blocking tasks)
   - Parallel execution opportunities noted

4. ✅ **Alignment with Architecture**
   - Tasks implement all 6 architecture documents
   - Global Directives (D-001 to D-010) embedded in tasks
   - Validation rules mapped to implementation tasks

5. ✅ **Documentation Created**
   - BACKEND_TASKS.md - Detailed backend task list
   - FRONTEND_TASKS.md - Detailed frontend task list
   - QA_TASKS.md - Detailed QA task list
   - TASK_DEPENDENCIES.md - Dependency graph and critical path

---

## Files to Reference

### Required Inputs
1. `specs/REQUIREMENTS.md` - Product requirements
2. `specs/architecture/01-system-architecture.md` - Microservices design
3. `specs/architecture/02-database-design.md` - Data models, DDL
4. `specs/architecture/03-business-rules.md` - Drools rules
5. `specs/architecture/05-backend-implementation-guide.md` - Backend patterns
6. `specs/architecture/06-frontend-implementation-guide.md` - Frontend patterns
7. `docs/phases/PHASE_1_ARCHITECTURE_SUMMARY.md` - This phase's output

### Optional References
8. `screenshots/` - UI validation targets (if exists)
9. `sms_api_specification.yaml` - API contract (if exists in specs/)
10. `LESSONS_LEARNED.md` - Global Directives (if exists)

---

## Agent Invocation Command

```
Task tool with:
subagent_type: sdlc-planner
description: "Generate SDLC task breakdown"
prompt: "
You are the sdlc-planner agent for Phase 2 of the DevPipeline.

Context:
- Phase 1 (Architecture Design) is complete
- Handoff document: docs/phases/PHASE_2_HANDOFF.md
- Architecture: specs/architecture/*.md (6 documents)
- Requirements: specs/REQUIREMENTS.md

Your Task:
Analyze the architecture and requirements to create detailed task breakdowns for:
1. Backend Development (Student Service + Configuration Service)
2. Frontend Development (Student UI + Configuration UI)
3. QA Testing (Backend + Frontend + Integration)

Deliverables:
- BACKEND_TASKS.md - Backend task list with acceptance criteria
- FRONTEND_TASKS.md - Frontend task list with acceptance criteria
- QA_TASKS.md - QA task list with test scenarios
- TASK_DEPENDENCIES.md - Dependency graph and critical path

Constraints:
- Reference specific architecture documents in each task
- Include Global Directives (D-001 to D-010) in relevant tasks
- Specify technology/pattern constraints per task
- Identify dependencies and critical path

Please proceed with analyzing the artifacts and generating the task breakdown.
"
```

---

## Next Phase Preview

**Phase 3:** Backend Development (senior-backend-developer agent)
- Implements Student Service + Configuration Service
- Uses BACKEND_TASKS.md as implementation guide
- Follows 05-backend-implementation-guide.md patterns

**Phase 4:** Backend QA (backend-qa-orchestrator agent)
- Executes tests from QA_TASKS.md (backend section)
- Validates Drools rules, API contracts, database isolation
- Fixes issues in loop until all tests pass

**Phase 5:** Frontend Development (senior-frontend-developer agent)
- Implements React UI using FRONTEND_TASKS.md
- Follows 06-frontend-implementation-guide.md constraints
- Validates against screenshots/

**Phase 6:** Frontend QA (frontend-qa-orchestrator agent)
- Executes tests from QA_TASKS.md (frontend section)
- Pixel-perfect screenshot validation
- Fixes issues in loop until all tests pass

---

## Handoff Complete

**Status:** ✅ READY FOR PHASE 2
**Target Agent:** sdlc-planner
**Action Required:** Orchestrator to execute `/clear` and launch Phase 2 agent
