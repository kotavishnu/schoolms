# School Management System - Complete Project Plan

## Executive Summary

This document provides the master project plan for implementing the School Management System (SMS), a web-based platform for managing student registration and school configuration.

**Project Type:** Greenfield Development
**Execution Model:** Waterfall / Single-Pass Implementation
**Architecture:** Microservices (Student Service, Configuration Service)
**Timeline:** Single continuous execution (no sprints)

---

## Project Overview

### Business Objectives
- Automate student registration and profile management
- Digitize school configuration settings
- Provide a modern, user-friendly web interface
- Ensure data integrity and validation
- Support future scalability

### Scope
**In Scope:**
- Student CRUD operations (Create, Read, Update, Delete)
- Student search and filtering
- Student enrollment history tracking
- School configuration management (key-value pairs)
- Configuration grouping by category (GENERAL, ACADEMIC, FINANCIAL)

**Out of Scope (Phase 1):**
- Authentication and authorization (JWT/OAuth)
- Multi-tenant support
- Payment processing
- Advanced reporting
- Mobile applications
- Complex database features (triggers, stored procedures, advanced indexing)

---

## System Architecture

### Technology Stack

**Backend:**
- Java 21 (LTS)
- Spring Boot 3.5.0
- Spring Data JPA 3.5.x
- PostgreSQL 18+
- MapStruct 1.5.x (DTO mapping)
- Lombok 1.18.x (boilerplate reduction)
- SpringDoc OpenAPI 2.7.0 (API documentation)
- Maven 3.9+ (build tool)

**Frontend:**
- React 19
- Next.js 15 (App Router)
- TypeScript 5.x
- Tailwind CSS 3.x
- React Query 4.x (server state)
- React Hook Form 7.x + Zod 3.x (forms & validation)
- Axios 1.6.0 (HTTP client)
- Vitest 1.0 (unit tests)
- Playwright 1.40 (E2E tests)

**Infrastructure:**
- PostgreSQL 18 (two databases: sms_student_db, sms_config_db)
- Docker Compose (local development)

**Ports:**
- Student Service: 8081
- Configuration Service: 8082
- Frontend (React/Vite): 3000 or 5173
- PostgreSQL: 5432

---

## Project Structure

```
wks-sms-specs-itr3/
├── backend/
│   ├── pom.xml (parent POM)
│   ├── student-service/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/school/sms/student/
│   │       │   ├── presentation/      # REST Controllers, DTOs
│   │       │   ├── application/       # Use Cases, Services
│   │       │   ├── domain/            # Business Logic, Entities
│   │       │   └── infrastructure/    # JPA, Config, Persistence
│   │       └── main/resources/
│   │           ├── application.yml
│   │           └── db/migration/
│   └── configuration-service/
│       └── (similar structure)
│
├── frontend/
│   └── sms-frontend/
│       ├── package.json
│       ├── next.config.js
│       ├── tailwind.config.ts
│       └── src/
│           ├── app/                   # Next.js App Router
│           ├── components/            # React Components
│           ├── lib/                   # Utils, API, Hooks
│           └── types/                 # TypeScript Types
│
├── specs/
│   ├── REQUIREMENTS.md
│   ├── architecture/
│   │   ├── 01-system-architecture.md
│   │   ├── 02-database-design.md
│   │   ├── 03-api-specification.md
│   │   ├── 04-security-architecture.md
│   │   ├── 05-backend-implementation-guide.md
│   │   ├── 06-frontend-implementation-guide.md
│   │   └── 07-testing-strategy.md
│   └── planning/                      # THIS DIRECTORY
│       ├── school_management.sql      # Database Schema
│       ├── BACKEND_TASKS.md           # Backend Task Breakdown
│       ├── FRONTEND_TASKS.md          # Frontend Task Breakdown
│       ├── QA_TASKS.md                # QA Test Plan
│       └── PROJECT_PLAN.md            # This Document
│
├── scripts/
│   ├── init-databases.sh
│   ├── run-student-service.sh
│   └── run-configuration-service.sh
│
├── docker-compose.yml
├── LESSONS_LEARNED.md
└── CLAUDE.md
```

---

## Implementation Phases

### Phase 1: Database Foundation
**Duration:** 1 day
**Deliverable:** Database schema deployed and tested

**Tasks:**
1. Create PostgreSQL databases (sms_student_db, sms_config_db)
2. Execute `school_management.sql` script
3. Verify tables created correctly
4. Insert sample configuration data
5. Test constraints and relationships

**Acceptance Criteria:**
- All tables created successfully
- Primary keys, foreign keys, and constraints working
- Sample data inserted
- Database accessible from backend services

**Reference:** `specs/planning/school_management.sql`

---

### Phase 2: Backend - Student Service
**Duration:** 5-7 days
**Deliverable:** Complete Student Service with REST API

**Sub-Phases:**
1. **Project Setup** (BE-001 to BE-005)
   - Maven parent POM
   - Student service module
   - Application configuration

2. **Domain Layer** (BE-006 to BE-011)
   - Student domain entity
   - Value objects (StudentId, PersonalInfo, etc.)
   - Repository interfaces
   - Domain exceptions
   - Enrollment entity

3. **Infrastructure Layer** (BE-012 to BE-019)
   - JPA entities
   - JPA repositories
   - Entity mappers (MapStruct)
   - Repository implementations

4. **Application Layer** (BE-020 to BE-025)
   - Request/Response DTOs
   - DTO mappers
   - Application services (business logic)
   - Validation

5. **Presentation Layer** (BE-026 to BE-030)
   - REST Controllers
   - Global exception handler
   - CORS configuration
   - OpenAPI documentation

**Acceptance Criteria:**
- Service starts without errors on port 8081
- All REST endpoints functional:
  - POST /api/v1/students (Create)
  - GET /api/v1/students/{studentId} (Read)
  - PUT /api/v1/students/{studentId} (Update)
  - DELETE /api/v1/students/{studentId} (Delete)
  - GET /api/v1/students (Search with pagination)
  - POST /api/v1/students/{studentId}/enrollment-history
  - GET /api/v1/students/{studentId}/enrollment-history
- Swagger UI accessible at http://localhost:8081/swagger-ui/index.html
- CORS configured for ports 3000 and 5173
- Optimistic locking working (version conflicts return 409)
- Validation rules enforced (age 3-18, mobile unique, etc.)
- RFC 7807 error responses working

**Reference:** `specs/planning/BACKEND_TASKS.md` (BE-001 to BE-030)

---

### Phase 3: Backend - Configuration Service
**Duration:** 3-4 days
**Deliverable:** Complete Configuration Service with REST API

**Sub-Phases:**
1. **Domain Layer** (BE-031 to BE-032)
   - Configuration domain entity
   - Repository interface

2. **Infrastructure Layer** (BE-033 to BE-036)
   - JPA entity
   - JPA repository
   - Mappers
   - Repository implementation

3. **Application Layer** (BE-037 to BE-039)
   - DTOs
   - DTO mappers
   - Application service (with UPSERT logic)

4. **Presentation Layer** (BE-040 to BE-043)
   - REST Controller
   - Exception handler
   - CORS configuration
   - OpenAPI documentation

**Acceptance Criteria:**
- Service starts without errors on port 8082
- All REST endpoints functional:
  - GET /api/v1/configurations
  - GET /api/v1/configurations/{category}/{key}
  - PUT /api/v1/configurations/{category}/{key} (UPSERT)
  - DELETE /api/v1/configurations/{category}/{key}
  - GET /api/v1/configurations/grouped/{category}
- Swagger UI accessible at http://localhost:8082/swagger-ui/index.html
- CORS configured
- UPSERT logic working (returns 201 for new, 200 for update)
- Category validation enforced (GENERAL, ACADEMIC, FINANCIAL)

**Reference:** `specs/planning/BACKEND_TASKS.md` (BE-031 to BE-043)

---

### Phase 4: Backend - Integration and Testing
**Duration:** 2-3 days
**Deliverable:** Fully tested and verified backend services

**Tasks:**
1. **Build & Deployment** (BE-044 to BE-048)
   - Database initialization scripts
   - Docker Compose setup
   - Service run scripts
   - Build both services

2. **Integration Testing** (BE-049 to BE-050)
   - Test Student Service endpoints
   - Test Configuration Service endpoints
   - Verify error handling
   - Test CORS
   - Verify Swagger UI

**Acceptance Criteria:**
- Both services build successfully (mvn clean install)
- Both services run concurrently without port conflicts
- All endpoints return expected responses
- Database operations working correctly
- Error handling comprehensive
- No exceptions in logs
- Documentation complete

**Reference:** `specs/planning/BACKEND_TASKS.md` (BE-044 to BE-050)

---

### Phase 5: Frontend - Foundation and Infrastructure
**Duration:** 2-3 days
**Deliverable:** Frontend project with core infrastructure

**Sub-Phases:**
1. **Project Setup** (FE-001 to FE-006)
   - Next.js project initialization
   - Dependencies installation
   - TypeScript configuration
   - Tailwind setup
   - Environment variables
   - Directory structure

2. **Core Infrastructure** (FE-007 to FE-015)
   - TypeScript types
   - Axios configuration
   - API services (Student, Configuration)
   - React Query setup
   - Custom hooks
   - Zod schemas
   - Utility functions

**Acceptance Criteria:**
- Project builds without errors
- TypeScript strict mode enabled
- Axios instances configured for both APIs
- React Query provider set up
- All API service functions defined
- Type definitions complete
- Validation schemas created

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-001 to FE-015)

---

### Phase 6: Frontend - UI Component Library
**Duration:** 3-4 days
**Deliverable:** Reusable UI component library

**Tasks:** (FE-016 to FE-029)
- Base components: Button, Input, Select, Card, Modal, Table
- Shared components: Pagination, LoadingSpinner, ErrorBoundary, Toast
- Layout components: Header, Sidebar, Footer
- Main Layout configuration

**Acceptance Criteria:**
- All components render correctly
- Components accept proper props
- Styling consistent (Tailwind)
- Components accessible (ARIA labels, keyboard navigation)
- Layout responsive on all screen sizes
- Storybook or component showcase (optional)

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-016 to FE-029)

---

### Phase 7: Frontend - Student Management Features
**Duration:** 4-5 days
**Deliverable:** Complete Student Management UI

**Sub-Phases:**
1. **Core Pages** (FE-030 to FE-037)
   - Student list page with search and pagination
   - Student form component (create/edit)
   - New student page
   - Student detail page
   - Edit student page
   - Delete functionality
   - Search/filter component
   - Enrollment history display

**Acceptance Criteria:**
- List page displays students with pagination
- Search filters work (lastName, fathersName, status)
- Create student form validates and submits
- Edit student pre-populates and updates
- Delete shows confirmation and executes
- Student detail shows all information
- Enrollment history displays correctly
- All API integrations working
- Error handling graceful
- Loading states present
- Success/error toasts shown

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-030 to FE-037)

---

### Phase 8: Frontend - Configuration Management Features
**Duration:** 2-3 days
**Deliverable:** Complete Configuration Management UI

**Tasks:** (FE-038 to FE-041)
- Configuration list page with grouping
- Configuration form component
- Create/Edit modal
- Delete functionality

**Acceptance Criteria:**
- Configurations grouped by category
- Create configuration works
- Edit configuration works (UPSERT)
- Delete shows confirmation and executes
- All API integrations working
- Error handling comprehensive

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-038 to FE-041)

---

### Phase 9: Frontend - Polish and Optimization
**Duration:** 3-4 days
**Deliverable:** Production-ready frontend

**Tasks:** (FE-042 to FE-049)
- Home/dashboard page
- Loading states
- Error pages (404, error boundary)
- Form validation feedback
- Responsive design refinement
- Accessibility improvements (WCAG 2.1 AA)
- Performance optimization
- Client-side routing optimization

**Acceptance Criteria:**
- Home page welcoming and functional
- Loading indicators on all async operations
- Error pages user-friendly
- Forms provide real-time feedback
- Responsive on mobile, tablet, desktop
- Keyboard navigation works
- Screen reader compatible
- Performance score > 90 (Lighthouse)
- No console errors or warnings

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-042 to FE-049)

---

### Phase 10: QA - Testing and Validation
**Duration:** 5-7 days
**Deliverable:** Comprehensive test coverage

**Sub-Phases:**
1. **Backend Unit Tests** (QA-BE-001 to QA-BE-008)
   - Domain entity tests
   - Value object tests
   - Application service tests
   - Test coverage > 80%

2. **Backend Integration Tests** (QA-BE-009 to QA-BE-015)
   - Repository tests with TestContainers
   - REST API tests
   - CORS tests
   - Exception handler tests

3. **Backend E2E Tests** (QA-BE-016 to QA-BE-020)
   - Complete CRUD lifecycle
   - Enrollment lifecycle
   - Configuration management
   - Optimistic locking
   - Data validation

4. **Frontend Unit Tests** (QA-FE-001 to QA-FE-007)
   - Component tests
   - Form tests
   - Test coverage > 80%

5. **Frontend Integration Tests** (QA-FE-008 to QA-FE-012)
   - React Query hooks tests
   - API integration tests

6. **Frontend E2E Tests** (QA-FE-013 to QA-FE-023)
   - User workflows (create, search, update, delete)
   - Form validation
   - Error handling
   - Pagination
   - Responsive design

7. **Accessibility & Performance Tests** (QA-FE-024 to QA-FE-028)
   - Bundle size
   - Page load performance
   - Keyboard navigation
   - Screen reader compatibility
   - Color contrast

8. **Cross-Functional Tests** (QA-XF-001 to QA-XF-002)
   - Full stack integration
   - API contract compliance

**Acceptance Criteria:**
- All test suites pass
- Code coverage > 80% (backend and frontend)
- E2E tests cover critical user flows
- Accessibility tests pass (WCAG 2.1 AA)
- Performance metrics met
- No critical or high-priority bugs
- API contracts validated

**Reference:** `specs/planning/QA_TASKS.md`

---

### Phase 11: Deployment and Documentation
**Duration:** 2-3 days
**Deliverable:** Production-ready system with documentation

**Tasks:** (FE-050 to FE-058)
- Vitest setup and configuration tests
- Playwright setup and E2E tests
- Developer documentation
- Integration testing with backend
- Production build
- Deployment scripts

**Acceptance Criteria:**
- Frontend-backend integration verified
- Production build successful
- Deployment scripts functional
- Developer documentation complete
- User guide available (optional)
- No errors in production mode

**Reference:** `specs/planning/FRONTEND_TASKS.md` (FE-050 to FE-058)

---

## Key Deliverables Summary

### 1. Database Schema
**File:** `specs/planning/school_management.sql`
**Contents:**
- Students table
- Enrollment history table
- Configuration settings table
- Sample configuration data
- Constraints and relationships

### 2. Backend Services
**Student Service:**
- REST API for student management
- Student CRUD operations
- Search and filtering
- Enrollment history tracking
- Swagger documentation

**Configuration Service:**
- REST API for configuration management
- CRUD operations with UPSERT
- Category-based grouping
- Swagger documentation

### 3. Frontend Application
**Pages:**
- Home/Dashboard
- Student list (with search and pagination)
- Student create
- Student detail
- Student edit
- Configuration management

**Features:**
- Responsive design
- Form validation
- Error handling
- Loading states
- Toast notifications
- Accessibility compliant

### 4. Documentation
- Architecture specifications (7 documents)
- API specifications
- Implementation task plans (Backend, Frontend, QA)
- Developer guides
- Deployment guides

---

## Risk Management

### Technical Risks

**Risk 1: Spring Boot and SpringDoc OpenAPI Version Incompatibility**
- **Mitigation:** Always use SpringDoc OpenAPI 2.7.0+ with Spring Boot 3.5.0
- **Reference:** LESSONS_LEARNED.md [D-001]
- **Status:** Already mitigated in architecture

**Risk 2: CORS Issues During Frontend-Backend Integration**
- **Mitigation:** Configure CORS for ports 3000 and 5173 from the start
- **Reference:** LESSONS_LEARNED.md [D-001]
- **Status:** Already addressed in backend tasks (BE-029, BE-042)

**Risk 3: Database Schema Changes**
- **Mitigation:** Use simplified schema (no triggers, functions) to minimize complexity
- **Impact:** Low - Schema is straightforward and well-defined
- **Status:** Mitigated

**Risk 4: Optimistic Locking Complexity**
- **Mitigation:** Use JPA @Version annotation, handle 409 conflicts in frontend
- **Impact:** Medium - Testing required
- **Status:** Addressed in backend tasks (BE-012, BE-033)

### Resource Risks

**Risk 5: Developer Skill Gaps**
- **Mitigation:** Comprehensive task breakdowns with technical details
- **Impact:** Medium
- **Contingency:** Reference implementation guides in specs/architecture/

**Risk 6: Testing Resource Availability**
- **Mitigation:** Automated testing with clear test scenarios
- **Impact:** Low
- **Status:** QA tasks clearly defined

### Project Risks

**Risk 7: Scope Creep**
- **Mitigation:** Strict adherence to Phase 1 scope (no auth, no advanced features)
- **Impact:** High if not controlled
- **Status:** Scope explicitly defined

**Risk 8: Integration Issues**
- **Mitigation:** Integration testing phases (Phase 4, Phase 11)
- **Impact:** Medium
- **Status:** Dedicated integration testing phases planned

---

## Quality Gates

### Phase Exit Criteria

**Backend Phases (2, 3, 4):**
- All tasks completed
- Services start without errors
- All endpoints return expected responses
- Swagger UI functional
- CORS working
- No exceptions in logs
- Integration tests pass

**Frontend Phases (5, 6, 7, 8, 9):**
- All tasks completed
- Application builds without errors
- No console errors
- All features functional
- Responsive on all screen sizes
- API integration working
- User flows complete

**QA Phase (10):**
- Test coverage > 80%
- All unit tests pass
- All integration tests pass
- All E2E tests pass
- Accessibility tests pass
- Performance metrics met
- No critical bugs

**Deployment Phase (11):**
- Production build successful
- Integration verified
- Documentation complete
- Deployment tested

---

## Success Metrics

### Technical Metrics
- Code coverage: > 80% (backend and frontend)
- API response time: 95th percentile < 200ms
- Frontend performance score: > 90 (Lighthouse)
- Zero critical security vulnerabilities
- WCAG 2.1 AA compliance

### Functional Metrics
- All requirements from REQUIREMENTS.md implemented
- All API endpoints functional
- All user workflows complete
- All validation rules enforced
- All error scenarios handled

### Quality Metrics
- All unit tests passing
- All integration tests passing
- All E2E tests passing
- Zero production errors on launch
- User acceptance criteria met

---

## Dependencies and Prerequisites

### External Dependencies
- PostgreSQL 18 installed and accessible
- Docker installed (for Docker Compose)
- Java 21 JDK installed
- Maven 3.9+ installed
- Node.js 20.x LTS installed

### Internal Dependencies
- Database schema must be deployed before backend development
- Backend services must be functional before frontend integration
- Backend tasks have sequential dependencies (see BACKEND_TASKS.md)
- Frontend tasks have sequential dependencies (see FRONTEND_TASKS.md)
- QA testing follows development completion

---

## Team Roles and Responsibilities

### Senior Backend Developer
**Responsibilities:**
- Execute all backend tasks (BE-001 to BE-050)
- Implement Student Service and Configuration Service
- Write backend unit tests
- Integrate with database
- Configure OpenAPI documentation
- Follow implementation guide (specs/architecture/05-backend-implementation-guide.md)

**Reference:** `specs/planning/BACKEND_TASKS.md`

### Senior Frontend Developer
**Responsibilities:**
- Execute all frontend tasks (FE-001 to FE-058)
- Build React/Next.js application
- Implement all UI components
- Integrate with backend APIs
- Write frontend unit tests
- Ensure accessibility and performance
- Follow implementation guide (specs/architecture/06-frontend-implementation-guide.md)

**Reference:** `specs/planning/FRONTEND_TASKS.md`

### Backend QA Engineer
**Responsibilities:**
- Execute backend QA tasks (QA-BE-001 to QA-BE-020)
- Write and run unit tests for domain and application layers
- Write and run integration tests for repositories and APIs
- Execute E2E tests for backend services
- Validate API contracts
- Report bugs and verify fixes

**Reference:** `specs/planning/QA_TASKS.md` (Backend section)

### Frontend QA Engineer
**Responsibilities:**
- Execute frontend QA tasks (QA-FE-001 to QA-FE-028)
- Write and run component tests
- Write and run React Query hook tests
- Execute E2E tests using Playwright
- Validate accessibility compliance
- Validate performance metrics
- Report bugs and verify fixes

**Reference:** `specs/planning/QA_TASKS.md` (Frontend section)

### QA Orchestrator
**Responsibilities:**
- Execute cross-functional tests (QA-XF-001 to QA-XF-002)
- Coordinate backend and frontend QA efforts
- Validate full stack integration
- Ensure test coverage targets met
- Final sign-off on quality

**Reference:** `specs/planning/QA_TASKS.md` (Cross-Functional section)

---

## Communication and Coordination

### Daily Sync
- Review progress on current tasks
- Identify blockers
- Coordinate handoffs between phases

### Phase Reviews
- Verify phase completion criteria met
- Demo deliverables
- Sign-off before proceeding to next phase

### Issue Management
- Log issues in LESSONS_LEARNED.md
- Document resolutions
- Update global directives if new patterns emerge

---

## Reference Documents

### Architecture Specifications
- `specs/REQUIREMENTS.md` - Product requirements
- `specs/architecture/01-system-architecture.md` - High-level architecture
- `specs/architecture/02-database-design.md` - Database schema and ERD
- `specs/architecture/03-api-specification.md` - REST API contracts
- `specs/architecture/04-security-architecture.md` - Security patterns
- `specs/architecture/05-backend-implementation-guide.md` - Backend standards
- `specs/architecture/06-frontend-implementation-guide.md` - Frontend standards
- `specs/architecture/07-testing-strategy.md` - Testing approach

### Planning Documents
- `specs/planning/school_management.sql` - Database schema script
- `specs/planning/BACKEND_TASKS.md` - Backend task breakdown (BE-001 to BE-050)
- `specs/planning/FRONTEND_TASKS.md` - Frontend task breakdown (FE-001 to FE-058)
- `specs/planning/QA_TASKS.md` - QA test plan (QA-BE, QA-FE, QA-XF)
- `specs/planning/PROJECT_PLAN.md` - This document

### Project Context
- `LESSONS_LEARNED.md` - Known issues and resolutions
- `CLAUDE.md` - Development guidelines

---

## Estimated Timeline

**Total Duration:** 30-40 working days

### Breakdown by Phase:
- Phase 1 (Database): 1 day
- Phase 2 (Student Service): 5-7 days
- Phase 3 (Configuration Service): 3-4 days
- Phase 4 (Backend Testing): 2-3 days
- Phase 5 (Frontend Foundation): 2-3 days
- Phase 6 (UI Components): 3-4 days
- Phase 7 (Student Features): 4-5 days
- Phase 8 (Config Features): 2-3 days
- Phase 9 (Polish): 3-4 days
- Phase 10 (QA): 5-7 days
- Phase 11 (Deployment): 2-3 days

**Note:** Timeline assumes single developer per role working sequentially. Parallel work by multiple developers could reduce overall timeline.

---

## Next Steps

### Immediate Actions:
1. Review and approve this project plan
2. Assign team members to roles
3. Set up development environments
4. Begin Phase 1: Database Foundation

### Development Workflow:
1. Backend team starts with Phase 1 and 2
2. Frontend team starts with Phase 5 (after backend has some endpoints ready)
3. QA team prepares test environments and frameworks
4. Weekly progress reviews
5. Phase-by-phase delivery

### Success Criteria for Project Completion:
- All 11 phases completed
- All backend tasks (BE-001 to BE-050) done
- All frontend tasks (FE-001 to FE-058) done
- All QA tasks (QA-BE, QA-FE, QA-XF) passing
- System deployed and accessible
- Documentation complete
- No critical or high-priority bugs
- User acceptance criteria met

---

## Appendix A: Task Reference

### Backend Tasks Summary
- **Total Tasks:** 50 (BE-001 to BE-050)
- **Categories:** Project Setup, Domain Layer, Infrastructure, Application Layer, Presentation Layer, Build & Test
- **Details:** See `specs/planning/BACKEND_TASKS.md`

### Frontend Tasks Summary
- **Total Tasks:** 58 (FE-001 to FE-058)
- **Categories:** Setup, Infrastructure, UI Components, Features, Polish, Testing, Deployment
- **Details:** See `specs/planning/FRONTEND_TASKS.md`

### QA Tasks Summary
- **Backend QA:** 20 tasks (QA-BE-001 to QA-BE-020)
- **Frontend QA:** 28 tasks (QA-FE-001 to QA-FE-028)
- **Cross-Functional:** 2 tasks (QA-XF-001 to QA-XF-002)
- **Details:** See `specs/planning/QA_TASKS.md`

---

## Appendix B: Key Constraints

### Simplified Security
- NO JWT/OAuth implementation in Phase 1
- Assume external authentication
- Focus on functionality over security

### Simplified Database
- NO indexes (except auto-created for PKs/UKs)
- NO triggers
- NO stored procedures/functions
- NO migration tools (Flyway/Liquibase)
- Single SQL script for schema

### Technology Constraints
- Spring Boot 3.5.0 requires SpringDoc OpenAPI 2.7.0+
- CORS must include ports 3000 and 5173
- PostgreSQL 18+ required
- Java 21 required
- Node.js 20.x required

---

## Appendix C: Critical Lessons Learned

From `LESSONS_LEARNED.md`:

**[D-001] Spring OpenAPI Compatibility**
- Always check Spring Boot and SpringDoc compatibility matrix
- Spring Boot 3.5.0 requires SpringDoc OpenAPI 2.7.0+

**[D-001] CORS Configuration**
- Document all frontend dev server ports
- Configure CORS for ports 3000 and 5173
- Test CORS during initial integration

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED - READY FOR EXECUTION
**Project Manager:** Technical Project Manager Agent
**Review Date:** 2025-12-06
