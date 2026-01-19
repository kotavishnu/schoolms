# Architecture Documentation
**School Management System - Comprehensive Architectural Blueprint**

**Version**: 1.0
**Date**: 2026-01-15
**Status**: Active

---

## Overview

This directory contains the complete architectural blueprint for the School Management System (SMS), a microservices-based web application for student registration and school configuration management.

### Purpose

These documents serve as **mandatory specifications** for developer agents and human developers to implement a production-ready system with:

- Strict service boundaries
- Database-per-service isolation
- Domain-Driven Design principles
- Comprehensive security controls
- Performance optimization strategies

---

## Document Index

### 1. System Architecture
**File**: `01-system-architecture.md`

**Purpose**: High-level system design and architectural patterns

**Contents**:
- System context and boundaries
- Microservices decomposition strategy
- Container architecture (frontend, backend, databases)
- Component architecture (4-layer pattern)
- Communication patterns
- Deployment architecture
- Scalability and performance considerations

**Key Diagrams**:
- System Context Diagram (C4 Model)
- Container Diagram
- Component Layer Architecture
- Deployment Architecture

**For**: Architects, Technical Leads, All Developers

---

### 2. Database Design
**File**: `02-database-design.md`

**Purpose**: Complete data model and persistence strategy

**Contents**:
- Entity Relationship Diagrams (ERD)
- Database schemas (students_db, config_db)
- Table definitions with DDL scripts
- Indexing strategy for performance
- Auditing and versioning (optimistic locking)
- Data integrity constraints
- Migration strategy (Flyway)
- Connection pooling (HikariCP)

**Key Artifacts**:
- Students table DDL
- Enrollments table DDL
- Configurations table DDL
- Database triggers (student_id generation, updated_at)
- Migration scripts structure

**For**: Backend Developers, Database Administrators

---

### 3. Business Rules Strategy
**File**: `03-business-rules.md`

**Purpose**: Drools Rules Engine integration and business logic externalization

**Contents**:
- Business rules inventory (BR-001 through BR-006)
- Drools architecture and session management
- Rule structure and DRL file organization
- Implementation patterns (field validation, cross-field, database-dependent)
- Integration with service layer
- Rule testing strategy
- Rule management and versioning

**Key Rules**:
- BR-001: Student Age Range (3-18 years)
- BR-002: Mobile Number Uniqueness
- BR-003: Class Capacity Enforcement
- BR-005: Immutable Field Protection

**For**: Backend Developers, Business Analysts

---

### 4. Security Architecture
**File**: `04-security-architecture.md`

**Purpose**: Security design and implementation guidelines

**Contents**:
- Security principles (Defense in Depth, Least Privilege)
- Phase 1 security controls (current implementation)
- Authentication strategy (OAuth 2.0 / OIDC - future)
- Authorization and RBAC (role hierarchy, permissions)
- Input validation and sanitization (multi-layer)
- SQL injection prevention (parameterized queries)
- XSS prevention (React escaping, CSP headers)
- CSRF protection (SameSite cookies, CSRF tokens)
- Data protection (encryption, masking)
- API security (CORS, rate limiting, HTTPS)

**Security Checklists**:
- Development phase checklist
- Testing phase checklist
- Deployment phase checklist

**For**: All Developers, Security Engineers, DevOps

---

### 5. Backend Implementation Guidelines
**File**: `05-backend-implementation-guide.md`

**Purpose**: Mandatory patterns for Backend Developer Agent

**Contents**:
- Project structure (Student Service, Configuration Service)
- Layer-by-layer implementation (Domain, Application, Infrastructure, Presentation)
- API implementation standards (OpenAPI 3.0 compliance)
- Data mapping strategy (MapStruct)
- Performance optimization (N+1 prevention, caching, connection pooling)
- Monitoring and observability (Actuator, Micrometer, Zipkin)
- Testing requirements (Unit, Integration, API tests)
- Code quality standards (Lombok usage, logging, exception handling)

**Code Examples**:
- Domain Model (Student Aggregate)
- Value Objects (Mobile, Email)
- Application Service (StudentApplicationService)
- Repository Implementation (StudentRepositoryImpl)
- REST Controller (StudentController)
- JPA Entity (StudentEntity)
- MapStruct Mapper (StudentMapper)
- Drools Integration (DroolsValidationService)

**For**: Backend Developer Agent, Java Developers

---

### 6. Frontend Implementation Guidelines
**File**: `06-frontend-implementation-guide.md`

**Purpose**: Mandatory patterns for Frontend Developer Agent

**Contents**:
- Critical constraints (NO custom styles, use Reference Code only)
- Project setup and structure
- Architecture patterns (Service Layer, Custom Hooks)
- Component implementation (Pages, Dialogs, Forms)
- API integration (Axios, service layer, type safety)
- Form management (React Hook Form + Zod validation)
- State management (React Context, Custom Hooks)
- Styling guidelines (Tailwind CSS, Shadcn/ui)
- Testing requirements (Vitest, React Testing Library)

**Code Examples**:
- Service Layer (studentService.ts)
- Custom Hook (useStudents.ts)
- Page Component (StudentsPage.tsx)
- Form Component (StudentForm.tsx)
- Zod Validation Schema (studentSchema)
- TypeScript Interfaces (Student, StudentCreateDto)

**For**: Frontend Developer Agent, React Developers

---

## Usage Guidelines

### For Developer Agents

1. **Backend Agent**:
   - Start with `01-system-architecture.md` to understand boundaries
   - Follow `02-database-design.md` for schema creation
   - Implement business logic per `03-business-rules.md`
   - Apply security controls from `04-security-architecture.md`
   - **STRICTLY FOLLOW** `05-backend-implementation-guide.md` for code structure

2. **Frontend Agent**:
   - Review `01-system-architecture.md` for API contract understanding
   - Align data models with `02-database-design.md`
   - Implement input validation per `03-business-rules.md`
   - Apply frontend security from `04-security-architecture.md`
   - **STRICTLY FOLLOW** `06-frontend-implementation-guide.md` for implementation
   - **CRITICAL**: Use Reference Code styles ONLY, NO custom CSS

3. **DevOps Agent**:
   - Use `01-system-architecture.md` for deployment architecture
   - Configure databases per `02-database-design.md`
   - Set up security controls from `04-security-architecture.md`

### For Human Developers

1. Read `01-system-architecture.md` first for overall context
2. Consult layer-specific documents as needed
3. Follow code examples in implementation guides
4. Reference security checklist before deployments
5. Use testing strategies from each guide

---

## Architecture Principles

### 1. Bounded Contexts

Services are decomposed by **business capability**, not database tables:

- **Student Service**: Student lifecycle management (registration, updates, status, enrollment)
- **Configuration Service**: School configuration management (general, academic, financial settings)

### 2. Database-per-Service

**Absolute isolation**: No service can access another's database. This ensures:

- Data autonomy
- Independent schema evolution
- Fault isolation
- Scalability

### 3. Contract-First API Design

All APIs defined in `specs/sms_api_specification.yaml`:

- OpenAPI 3.0 specification as source of truth
- Request/response types strictly enforced
- RFC 7807 Problem Details for errors
- X-Correlation-ID for distributed tracing

### 4. Layered Architecture within Services

Each microservice uses **4-layer architecture**:

1. **Presentation Layer**: REST controllers, DTOs, validation
2. **Application Layer**: Service orchestration, transaction boundaries
3. **Domain Layer**: Business logic, aggregates, value objects
4. **Infrastructure Layer**: JPA, Drools, Redis, monitoring

**Critical**: Layers can only depend inward (Presentation → Application → Domain ← Infrastructure).

### 5. Security by Design

- Multi-layer input validation (Frontend, Backend, Database)
- SQL injection prevention (parameterized queries)
- XSS prevention (React escaping, CSP)
- CSRF protection (SameSite cookies)
- Authentication/Authorization (future: OAuth 2.0)

---

## Technology Stack Summary

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.0
- **Database**: PostgreSQL 18+
- **Rules Engine**: Drools 9.44.0.Final
- **Cache**: Redis
- **Mapping**: MapStruct
- **Monitoring**: Micrometer, Zipkin, Prometheus
- **Testing**: JUnit 5, Mockito, TestContainers

### Frontend
- **Framework**: React 18+
- **Language**: TypeScript 5.x
- **Build Tool**: Vite 5.x
- **UI**: Tailwind CSS v4, Shadcn/ui
- **Routing**: React Router v6
- **Forms**: React Hook Form + Zod
- **HTTP**: Axios
- **Testing**: Vitest, React Testing Library, Playwright

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Docker Compose (Kubernetes future)
- **Migration**: Flyway
- **Tracing**: Zipkin
- **Metrics**: Prometheus

---

## Key Architectural Decisions

| Decision | Rationale | Trade-offs |
|----------|-----------|------------|
| Microservices | Independent scaling, fault isolation | Increased complexity, distributed transactions |
| Database-per-Service | Data autonomy, independent evolution | No JOINs across services, eventual consistency |
| Drools Rules Engine | Externalized business logic, non-developer authoring | Learning curve, performance overhead |
| React SPA | Rich UX, component reusability | SEO challenges (mitigated with SSR if needed) |
| PostgreSQL | ACID compliance, JSONB, full-text search | Horizontal scaling complexity |
| Redis Cache | Performance, simple data structures | Cache invalidation complexity |

---

## Validation and Compliance

### API Compliance

All backend endpoints MUST implement `specs/sms_api_specification.yaml`:

- Request/response types match exactly
- HTTP status codes per specification
- Error responses follow RFC 7807 format
- Validation rules align with specification

### Data Model Compliance

Frontend TypeScript interfaces MUST align with:

- Backend DTOs (StudentResponse, ConfigurationResponse)
- Database schema (field names, types, constraints)
- OpenAPI specification (request/response schemas)

**Example**:
```
Frontend: student.phone (string)
Backend: Student.mobile (Mobile value object)
Database: students.mobile (VARCHAR(10))
API Spec: mobile (string, pattern: ^\d{10}$)
```

### Testing Strategy Compliance

Adhere to `specs/TESTING_STRATEGY.md`:

- **Unit Tests**: 60% of test effort (Domain layer focus)
- **Integration Tests**: 30% (Repository, API)
- **E2E Tests**: 10% (Critical user journeys)
- **Coverage Targets**: Domain 95%, Application 85%, Infrastructure 70%

---

## Implementation Workflow

### Phase 1: Foundation (Week 1-2)

1. **Backend**:
   - Database schema creation (Flyway migrations)
   - Domain models (Student, Enrollment aggregates)
   - Repository interfaces and implementations
   - Drools rules setup

2. **Frontend**:
   - Project setup (Vite, Tailwind, Shadcn/ui)
   - Copy Reference Code UI components
   - Service layer implementation (API client)
   - Type definitions

### Phase 2: Core Features (Week 2-3)

1. **Backend**:
   - Application services (StudentApplicationService)
   - REST controllers (StudentController)
   - DTO mapping (MapStruct)
   - Exception handling

2. **Frontend**:
   - Page components (HomePage, StudentsPage)
   - Form components (StudentForm with validation)
   - Custom hooks (useStudents)
   - API integration

### Phase 3: Testing & Refinement (Week 3-4)

1. **Backend**:
   - Unit tests (Domain, Drools rules)
   - Integration tests (Repository, API)
   - Performance optimization (N+1, caching)
   - Monitoring setup (Actuator, metrics)

2. **Frontend**:
   - Component tests (Vitest)
   - E2E tests (Playwright)
   - Error handling refinement
   - Performance optimization

### Phase 4: Deployment (Week 4)

1. **DevOps**:
   - Docker images build and push
   - Docker Compose configuration
   - Environment variables setup
   - Monitoring dashboards (Prometheus, Grafana)

---

## Monitoring and Observability

### Key Metrics to Track

**Backend**:
- `http.server.requests` (response times, status codes)
- `students.registered.total` (business metric counter)
- `cache.gets`, `cache.puts` (Redis effectiveness)
- `db.connection.pool.usage` (HikariCP health)
- `jvm.memory.used` (memory consumption)

**Frontend**:
- Page load time
- API call latency
- Error rates by component
- User interaction metrics

### Distributed Tracing

Use **X-Correlation-ID** header throughout:

1. Frontend generates UUID
2. Passed to all backend services
3. Logged in all service operations
4. Traced in Zipkin for end-to-end visibility

---

## Security Compliance

### OWASP Top 10 Coverage

All security controls mapped to OWASP Top 10 (2021) in `04-security-architecture.md`:

- A01: Broken Access Control → RBAC (future)
- A02: Cryptographic Failures → HTTPS, password hashing
- A03: Injection → Parameterized queries, input validation
- A04: Insecure Design → Security by design, threat modeling
- A05: Security Misconfiguration → Security headers, CORS, CSP
- A06: Vulnerable Components → Dependency scanning
- A07: Authentication Failures → OAuth 2.0 (future)
- A08: Software Integrity → Code signing
- A09: Logging Failures → Audit logging
- A10: SSRF → Input validation, URL allowlisting

---

## Contact and Support

### For Questions or Clarifications

- **Architecture Questions**: Refer to `01-system-architecture.md`
- **Database Design**: Refer to `02-database-design.md`
- **Business Rules**: Refer to `03-business-rules.md`
- **Security Concerns**: Refer to `04-security-architecture.md`
- **Backend Implementation**: Refer to `05-backend-implementation-guide.md`
- **Frontend Implementation**: Refer to `06-frontend-implementation-guide.md`

### Document Maintenance

- **Version**: 1.0 (Initial Release)
- **Last Updated**: 2026-01-15
- **Maintained By**: Software Architect Agent
- **Review Cycle**: Monthly or upon significant requirement changes

---

## Quick Reference

### Backend Developer Checklist

- [ ] Read System Architecture (01)
- [ ] Implement database schema per Database Design (02)
- [ ] Configure Drools rules per Business Rules Strategy (03)
- [ ] Apply security controls from Security Architecture (04)
- [ ] Follow code patterns in Backend Implementation Guide (05)
- [ ] Implement ALL endpoints from OpenAPI specification
- [ ] Write unit and integration tests
- [ ] Configure monitoring and metrics

### Frontend Developer Checklist

- [ ] Read System Architecture (01)
- [ ] Review data models in Database Design (02)
- [ ] Align validation with Business Rules Strategy (03)
- [ ] Apply frontend security from Security Architecture (04)
- [ ] **STRICTLY** follow Frontend Implementation Guide (06)
- [ ] **COPY** UI components from Reference Code (NO custom styles)
- [ ] Implement service layer for API calls
- [ ] Create TypeScript interfaces matching backend DTOs
- [ ] Implement forms with Zod validation
- [ ] Write component and E2E tests
- [ ] Docker build succeeds

---

## Appendix

### Related Documents

- `specs/REQUIREMENTS.md` - Business requirements and features
- `specs/FRONTEND_DESIGN_SPECIFICATION.md` - Detailed frontend spec with data models
- `specs/TESTING_STRATEGY.md` - Comprehensive testing approach
- `specs/sms_api_specification.yaml` - OpenAPI 3.0 API contract

### Glossary

- **Aggregate**: DDD pattern for grouping entities with transactional boundaries
- **Value Object**: Immutable object defined by its value (e.g., Mobile, Email)
- **Bounded Context**: Service boundary aligned with business capability
- **DTO**: Data Transfer Object for API request/response
- **CQRS**: Command Query Responsibility Segregation
- **Optimistic Locking**: Concurrency control using version field
- **N+1 Problem**: Performance issue from lazy loading in loops

---

**End of Architecture Documentation Index**
