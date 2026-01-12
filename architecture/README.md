# School Management System - Architecture Documentation

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Production-Ready

---

## Overview

This directory contains **comprehensive production-ready architectural blueprints** for the School Management System (SMS), a web-based platform for student registration and school configuration management.

---

## Document Structure

All architectural documents follow Domain-Driven Design (DDD) principles and microservices architecture patterns. Read in sequence for complete understanding:

### 1. [System Architecture](./01-system-architecture.md) - 29 KB
**Purpose**: High-level system design and architectural decisions

**Contents**:
- System Context and Container diagrams
- Microservices boundaries (Student Service, Configuration Service)
- Layered architecture (Presentation → Application → Domain → Infrastructure)
- Technology stack and design principles (SOLID, DDD, CQRS)
- Cross-cutting concerns (logging, metrics, tracing)
- Deployment architecture (Docker Compose, port allocations)

**Key Decisions**:
- Database-per-Service pattern (absolute isolation)
- Domain-Driven Design with rich domain models
- Spring Boot 3.5.0 + Java 21 backend
- React 18 + TypeScript + Vite frontend
- PostgreSQL 18 + Redis caching
- Zipkin tracing + Prometheus metrics

---

### 2. [Database Design](./02-database-design.md) - 27 KB
**Purpose**: Complete database schema and migration strategy

**Contents**:
- Entity-Relationship Diagrams (Mermaid)
- Table schemas with all fields, types, constraints
- Indexes for performance optimization
- Triggers for auto-generation (Student ID: STU-YYYY-NNNNN)
- Migration scripts (Flyway)
- Backup and recovery procedures

**Database Architecture**:
- **Student Database** (Port 5433): `students` table
- **Configuration Database** (Port 5434): `configuration_settings` table
- **Constraints**: Age 3-18, unique phone/email/adhaar, optimistic locking
- **Audit Fields**: created_at, updated_at, version (all tables)

**Field Alignment**: All mandatory frontend data model fields included

---

### 3. [API Specification](./03-api-specification.md) - 30 KB
**Purpose**: Complete REST API contracts for all services

**Contents**:
- RESTful endpoint definitions (OpenAPI 3.0 compatible)
- Request/response schemas (JSON)
- HTTP status codes and error responses (RFC 7807)
- Validation rules per endpoint
- Pagination, filtering, and search patterns

**Student Service API**:
```
GET    /api/v1/students              # List all students
GET    /api/v1/students/{id}         # Get student by ID
POST   /api/v1/students              # Create student
PATCH  /api/v1/students/{id}         # Update student
DELETE /api/v1/students/{id}         # Delete student
GET    /api/v1/students/statistics   # Dashboard stats
POST   /api/v1/students/validate-phone # Check uniqueness
```

**Configuration Service API**:
```
GET    /api/v1/configurations         # List all configs
GET    /api/v1/configurations/{id}    # Get config by ID
POST   /api/v1/configurations         # Create config
PATCH  /api/v1/configurations/{id}    # Update config
DELETE /api/v1/configurations/{id}    # Delete config
```

**Error Format**: RFC 7807 Problem Details with trace IDs

---

### 4. [Security Architecture](./04-security-architecture.md) - 26 KB
**Purpose**: Security controls and compliance strategy

**Contents**:
- Authentication strategy (Phase 1: None, Phase 2: JWT)
- Authorization model (RBAC with role hierarchy)
- Input validation (JSR-303, Zod schemas)
- Protection against SQL injection, XSS, CSRF, IDOR
- Data protection (encryption, masking, GDPR compliance)
- Security headers and audit logging

**Phase 1 Security**:
- No authentication (external system in Phase 2)
- Input validation (client + server)
- CORS configuration (environment-based)
- SQL injection prevention (prepared statements)
- XSS prevention (React escaping)

**Phase 2 (Future)**:
- JWT-based authentication
- RBAC: SUPER_ADMIN → ADMIN → STAFF → TEACHER
- Method-level security (@PreAuthorize)

---

### 5. [Backend Implementation Guide](./05-backend-implementation-guide.md) - 48 KB
**Purpose**: Coding standards and patterns for Spring Boot development

**Contents**:
- Complete project structure (package-by-layer)
- Domain layer implementation (rich models, value objects)
- Application layer patterns (CQRS, MapStruct, transactions)
- Infrastructure layer (JPA, Redis, Drools)
- Presentation layer (REST controllers, DTOs, error handlers)
- Business rules with Drools
- Caching strategy (Redis TTLs, eviction)
- Performance optimization (N+1 prevention, HikariCP, batch processing)
- Monitoring (custom metrics, Actuator, Zipkin)

**Code Examples**:
- Student domain model with factory methods
- Application service with caching
- JPA repository with custom queries
- Redis cache manager
- Drools business rules
- Global exception handler

**Key Patterns**:
- Repository pattern (domain interface, infrastructure implementation)
- DTO mapping with MapStruct
- Optimistic locking with @Version
- Custom metrics with Micrometer

---

### 6. [Frontend Implementation Guide](./06-frontend-implementation-guide.md) - 35 KB
**Purpose**: React development standards and architecture enforcement

**Contents**:
- Mandatory project structure (strict directory layout)
- Service Layer pattern (centralized API calls)
- State management (Context API for global, Hooks for local)
- Form handling (React Hook Form + Zod validation)
- Component development (Container/Presentation separation)
- Error handling (Error Boundaries, graceful failures)
- Performance optimization (memoization, debouncing, lazy loading)
- Styling guidelines (Tailwind CSS + Shadcn/ui)

**Architectural Constraints** (MANDATORY):
- Service Layer in `src/services/` (NOT inline axios calls)
- Context API for global state (NOT Redux/Zustand in Phase 1)
- React Hook Form + Zod (NOT Formik)
- Shadcn/ui components (NOT Material-UI)

**Code Examples**:
- HTTP client setup with interceptors
- Student service with all CRUD methods
- Custom hooks (useStudents, useDebounce)
- Form component with Zod validation
- Student card presentation component
- Error boundary component

---

### 7. [Testing Strategy](./07-testing-strategy.md) - 33 KB
**Purpose**: Comprehensive QA approach and quality gates

**Contents**:
- Testing pyramid (60% unit, 30% integration, 10% E2E)
- Backend testing (JUnit 5, Mockito, TestContainers)
- Frontend testing (Vitest, React Testing Library, Playwright)
- Integration testing (API contracts, database tests)
- E2E testing (Playwright scenarios)
- Test scenarios (student registration, search)
- Quality gates (DoD, CI/CD gates)
- CI/CD integration (GitHub Actions)

**Quality Metrics**:
- Code Coverage: >80%
- Test Execution Time: <5 min (unit), <15 min (all)
- API Response Time: <200ms (95th percentile)
- Defect Escape Rate: <5%

**Critical Test Scenarios**:
1. Student Registration (happy path + edge cases)
2. Student Search (by ID, name, guardian, status)
3. Phone Uniqueness Validation
4. Age Validation (3-18 years)
5. Optimistic Locking

---

## Technology Stack Summary

### Backend (Microservices)

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 LTS |
| Framework | Spring Boot | 3.5.0 |
| Data Access | Spring Data JPA | 3.5.0 |
| Database | PostgreSQL | 18+ |
| Cache | Redis | 7.x |
| Business Rules | Drools | 9.44.0.Final |
| DTO Mapping | MapStruct | 1.5.x |
| API Docs | SpringDoc OpenAPI | 2.6.x |
| Tracing | Zipkin | Latest |
| Metrics | Micrometer | 1.x |

---

### Frontend (React SPA)

| Component | Technology | Version |
|-----------|-----------|---------|
| Library | React | 18+ |
| Language | TypeScript | 5.x |
| Build Tool | Vite | 5.x |
| Routing | React Router | 6.x |
| UI Framework | Tailwind CSS | 4.x |
| Components | Shadcn/ui | Latest |
| Forms | React Hook Form | 7.x |
| Validation | Zod | Latest |
| HTTP Client | Axios | 1.x |
| Icons | Lucide React | Latest |
| Notifications | Sonner | Latest |

---

### Infrastructure

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Docker | 24.x | Containerization |
| Docker Compose | 2.x | Local orchestration |
| Nginx | Latest | Reverse proxy |
| Prometheus | Latest | Metrics collection |
| Grafana | Latest | Dashboards |
| Zipkin | Latest | Distributed tracing |

---

## Architecture Principles

### 1. Database-per-Service (Strict Enforcement)
- Student Service: `studentdb` (Port 5433)
- Configuration Service: `configdb` (Port 5434)
- No shared tables, no cross-database queries
- Complete isolation

### 2. Bounded Contexts
- Student Management: Student aggregate, registration, search
- Configuration Management: Key-value settings, categories

### 3. Contract Strictness
- All API communication via DTOs
- Domain models NEVER exposed in API responses
- Separate DTOs for Create, Update, Response

### 4. Layered Architecture (Per Service)
```
Presentation  (Controllers, DTOs)
    ↓
Application   (Services, Mappers)
    ↓
Domain        (Entities, Repository Interfaces, Business Logic)
    ↑
Infrastructure (JPA, Redis, External APIs)
```

### 5. SOLID Principles
- Single Responsibility: One reason to change
- Open/Closed: Extend without modifying
- Liskov Substitution: Interface implementations interchangeable
- Interface Segregation: Minimal interfaces
- Dependency Inversion: Depend on abstractions

---

## Deployment Architecture

### Port Allocation

| Service | Port | Environment |
|---------|------|-------------|
| Frontend (Dev) | 5173 | Vite dev server |
| Frontend (Prod) | 80/443 | Nginx |
| Student Service | 8081 | Spring Boot |
| Config Service | 8082 | Spring Boot |
| Student DB | 5433 | PostgreSQL (mapped from 5432) |
| Config DB | 5434 | PostgreSQL (mapped from 5432) |
| Redis | 6379 | Cache (DB0: students, DB1: config) |
| Zipkin | 9411 | Tracing UI |
| Prometheus | 9090 | Metrics |
| Grafana | 3000 | Dashboards |

---

### Docker Compose Services

```yaml
services:
  - web (React SPA)
  - student-service (Spring Boot)
  - config-service (Spring Boot)
  - student-db (PostgreSQL 18)
  - config-db (PostgreSQL 18)
  - redis (Redis 7-alpine)
  - zipkin (Distributed tracing)
  - prometheus (Metrics)
  - grafana (Visualization)
```

---

## Performance Requirements

| Metric | Target | Strategy |
|--------|--------|----------|
| API Response Time | <200ms (95th percentile) | Caching, N+1 prevention, connection pooling |
| Database Queries | <50ms | Indexes, EntityGraph, batch processing |
| Cache Hit Ratio | >80% | Redis with appropriate TTLs |
| Page Load Time | <2 seconds | Code splitting, lazy loading, image optimization |

---

## Observability Strategy

### Distributed Tracing (Zipkin)
- Correlation IDs for all requests
- Trace ID propagation across services
- 100% sampling in dev, 10% in prod

### Structured Logging
- JSON format for all logs
- Correlation IDs in MDC
- Sensitive data masking
- Separate log levels per environment

### Metrics (Prometheus + Grafana)
**Application Metrics**:
- `students.registered.total` (Counter)
- `students.active.count` (Gauge)
- `api.response.time` (Timer)
- `cache.hit.ratio` (Gauge)

**JVM Metrics**:
- `jvm.memory.used`, `jvm.gc.pause`, `jvm.threads.live`

**Database Metrics**:
- `hikaricp.connections.active`, `jdbc.query.execution.time`

---

## Business Rules

### BR-1: Age Validation
- Students must be 3-18 years old at registration
- Enforced: Database CHECK constraint + Drools rule + Zod schema

### BR-2: Mobile Uniqueness
- Phone numbers must be unique across all students
- Enforced: Database UNIQUE constraint + async validation

### BR-3: Edit Restrictions
- Only firstName, lastName, phone, status can be updated after creation
- Immutable: dateOfBirth, adhaarNumber, email, guardianName, motherName, address

### BR-4: Student ID Generation
- Auto-generated format: `STU-YYYY-NNNNN` (e.g., STU-2026-00001)
- Enforced: Database trigger with sequence

---

## Development Workflow

### Backend Development
1. Define domain entity in Domain layer
2. Create repository interface in Domain layer
3. Implement JPA repository in Infrastructure layer
4. Build application service in Application layer
5. Create DTOs and controller in Presentation layer
6. Write unit tests for each layer
7. Write integration tests for API

### Frontend Development
1. Define TypeScript types in `src/types/`
2. Create service methods in `src/services/`
3. Build custom hooks in `src/hooks/`
4. Develop presentation components in `src/components/`
5. Assemble container components (pages) in `src/pages/`
6. Add unit tests for services and components
7. Write E2E tests for critical flows

---

## Quality Gates

### Definition of Done
A feature is DONE when:
- [ ] All acceptance criteria met
- [ ] Unit tests written (>80% coverage)
- [ ] Integration tests written
- [ ] E2E test for critical path
- [ ] Code reviewed and approved
- [ ] No critical bugs
- [ ] Documentation updated
- [ ] Performance benchmarks met

---

## Future Enhancements (Phase 2)

### Authentication & Authorization
- JWT-based authentication
- RBAC with role hierarchy
- Method-level security
- Audit logging for access

### Advanced Features
- Multi-tenant support (multiple schools)
- Bulk student import (CSV/Excel)
- Class management
- Fee management
- Attendance tracking
- Report generation (PDF/Excel)

### Scalability
- API Gateway (Spring Cloud Gateway)
- Event-driven communication (RabbitMQ/Kafka)
- Saga pattern for distributed transactions
- Kubernetes deployment

---

## Usage

### For Architects
Read all documents in sequence to understand system design decisions.

### For Backend Developers
1. Read: 01-System Architecture
2. Read: 02-Database Design
3. Read: 03-API Specification
4. **Follow**: 05-Backend Implementation Guide
5. **Follow**: 07-Testing Strategy

### For Frontend Developers
1. Read: 01-System Architecture
2. Read: 03-API Specification
3. **Follow**: 06-Frontend Implementation Guide
4. **Follow**: 07-Testing Strategy

### For DevOps Engineers
1. Read: 01-System Architecture (Deployment section)
2. Read: 04-Security Architecture
3. Configure Docker Compose, Prometheus, Grafana

### For QA Engineers
1. Read: 03-API Specification
2. **Follow**: 07-Testing Strategy
3. Implement test scenarios for student registration and search

---

## Document Maintenance

### Version Control
All architecture documents are version-controlled in Git. Update version numbers and revision history when making changes.

### Review Cycle
- **Quarterly**: Review and update based on lessons learned
- **Per Phase**: Update for new features (Phase 2, Phase 3)
- **On Incidents**: Update after post-mortems

---

## Support

For questions or clarifications:
- **Architecture Team**: [Architect contact]
- **Documentation**: This directory (`/architecture`)
- **API Documentation**: `http://localhost:8081/swagger-ui.html`
- **Grafana Dashboards**: `http://localhost:3000`

---

## Summary Statistics

| Document | Size | Lines | Key Topics |
|----------|------|-------|------------|
| 01-System Architecture | 29 KB | 1,100 | Microservices, DDD, Tech Stack |
| 02-Database Design | 27 KB | 892 | Schemas, Indexes, Migrations |
| 03-API Specification | 30 KB | 1,323 | REST APIs, Error Handling |
| 04-Security Architecture | 26 KB | 976 | Auth, Validation, RBAC |
| 05-Backend Implementation Guide | 48 KB | 1,850+ | Spring Boot, Drools, Caching |
| 06-Frontend Implementation Guide | 35 KB | 1,300+ | React, TypeScript, Forms |
| 07-Testing Strategy | 33 KB | 1,200+ | JUnit, Vitest, Playwright |
| **Total** | **228 KB** | **~8,600** | **Complete Architecture** |

---

**Architecture Status**: Production-Ready
**Last Updated**: January 8, 2026
**Next Review**: April 8, 2026
