# School Management System - Architecture Documentation

## Overview

This directory contains the complete architectural blueprint for the School Management System (SMS). These documents provide production-ready specifications for development teams to implement the system independently.

## Document Index

### 1. [System Architecture](01-system-architecture.md)
**Purpose:** Defines the high-level system structure, architectural patterns, and design principles.

**Key Contents:**
- Microservices architecture (Student Service, Configuration Service)
- Domain-Driven Design (DDD) with layered architecture
- System context, container, and component diagrams
- Technology stack rationale (Spring Boot 3.5.0, React 19, PostgreSQL 18)
- Observability strategy (Zipkin, Micrometer, structured logging)
- Cross-cutting concerns (caching, error handling, monitoring)
- Architectural Decision Records (ADRs)

**For:** Architects, Tech Leads, All Developers

---

### 2. [Database Design](02-database-design.md)
**Purpose:** Complete database schema with ERD, constraints, and optimization strategies.

**Key Contents:**
- Entity Relationship Diagram (ERD) for Student and Configuration modules
- Detailed table schemas with all constraints
- Database naming conventions (snake_case)
- Business rule enforcement (BR-1: Age 3-18, BR-2: Mobile Unique)
- Performance indexes and query optimization
- Audit columns and optimistic locking (@Version)
- Sample data and migration scripts (Flyway)
- Backup and recovery strategy

**For:** Backend Developers, Database Administrators

---

### 3. [API Specification](03-api-specification.md)
**Purpose:** RESTful API contracts following OpenAPI 3.0 standards.

**Key Contents:**
- Complete API endpoints for Student and Configuration services
- Request/Response schemas with validation rules
- RFC 7807 Problem Details error format
- HTTP status codes and error handling
- Pagination, filtering, and sorting patterns
- CORS configuration (ports 3000, 5173)
- API response time requirements (<200ms 95th percentile)
- cURL examples for testing

**For:** Frontend Developers, Backend Developers, QA Engineers

---

### 4. [Security Architecture](04-security-architecture.md)
**Purpose:** Comprehensive security blueprint (Phase 2+) and current best practices.

**Key Contents:**
- JWT-based stateless authentication (Phase 2 design)
- Role-Based Access Control (RBAC) hierarchy
- Password security (BCrypt, complexity requirements)
- Input validation and sanitization strategies
- SQL injection, XSS, and CSRF prevention
- Data encryption (at rest and in transit)
- Security headers and HTTPS/TLS configuration
- Audit logging requirements
- Rate limiting and DDoS protection
- Incident response plan

**For:** Security Engineers, Backend Developers, DevOps

---

### 5. [Backend Implementation Guide](05-backend-implementation-guide.md)
**Purpose:** Detailed implementation patterns for Backend Developer Agents.

**Key Contents:**
- Complete project structure (multi-module Maven)
- Domain-Driven Design (DDD) implementation
- Rich domain models with business logic
- Value objects and entity design
- Application services (orchestration, CQRS)
- Infrastructure layer (JPA, repositories, adapters)
- MapStruct DTO mapping (compile-time)
- Drools business rules engine integration
- Global exception handling (RFC 7807)
- Performance optimization (N+1 prevention, caching)
- Custom metrics and monitoring (Micrometer)

**For:** Backend Developers

---

### 6. [Frontend Implementation Guide](06-frontend-implementation-guide.md)
**Purpose:** React 19 and Next.js 15 implementation patterns for Frontend Developer Agents.

**Key Contents:**
- Next.js 15 App Router project structure
- TypeScript configuration and type safety
- State management (React Query for server, Context for client)
- Form handling (React Hook Form + Zod validation)
- Reusable UI components (Button, Input, Table, etc.)
- API layer with Axios interceptors
- Error handling and ErrorBoundary
- Performance optimization (code splitting, memoization)
- Accessibility (a11y) guidelines
- Tailwind CSS patterns

**For:** Frontend Developers

---

### 7. [Testing Strategy](07-testing-strategy.md)
**Purpose:** Comprehensive testing approach with quality gates.

**Key Contents:**
- Testing pyramid (60% unit, 30% integration, 10% E2E)
- Backend testing (JUnit 5, Mockito, TestContainers)
- Frontend testing (Vitest, React Testing Library, Playwright)
- Business requirement test scenarios (BR-1, BR-2, BR-3)
- Code coverage requirements (80%+ overall)
- CI/CD quality gates
- Test data management strategies
- Performance testing (Gatling/JMeter)
- Example test implementations

**For:** QA Engineers, All Developers

---

## Quick Start for Developer Agents

### Backend Developer
1. Read: `01-system-architecture.md` (sections 2-7, 9-10)
2. Read: `02-database-design.md` (complete)
3. Read: `03-api-specification.md` (section 4: Student Service API)
4. Read: `05-backend-implementation-guide.md` (complete)
5. Read: `07-testing-strategy.md` (section 3: Backend Testing)

### Frontend Developer
1. Read: `01-system-architecture.md` (sections 2-7)
2. Read: `03-api-specification.md` (complete)
3. Read: `06-frontend-implementation-guide.md` (complete)
4. Read: `07-testing-strategy.md` (section 4: Frontend Testing)

### QA Engineer
1. Read: `01-system-architecture.md` (sections 2, 10)
2. Read: `03-api-specification.md` (complete)
3. Read: `07-testing-strategy.md` (complete)

### DevOps Engineer
1. Read: `01-system-architecture.md` (sections 12, 13)
2. Read: `04-security-architecture.md` (sections 8, 10, 11)
3. Read: `02-database-design.md` (sections 13, 14, 15)

---

## Technology Stack Summary

### Backend
- **Framework:** Spring Boot 3.5.0
- **Language:** Java 21 (LTS)
- **Database:** PostgreSQL 18+
- **ORM:** Spring Data JPA
- **Business Rules:** Drools 9.44.0.Final
- **Caching:** Redis 7.x
- **Mapping:** MapStruct 1.5.x
- **Utilities:** Lombok 1.18.x
- **Observability:** Zipkin, Micrometer, Prometheus
- **API Docs:** SpringDoc OpenAPI 2.7.0+

### Frontend
- **Framework:** Next.js 15 (App Router)
- **Library:** React 19
- **Language:** TypeScript 5.x
- **Styling:** Tailwind CSS 3.x
- **State (Server):** React Query 4.x
- **State (Client):** Context API
- **Forms:** React Hook Form 7.x + Zod 3.x
- **HTTP Client:** Axios 1.6.x
- **Testing:** Vitest, Playwright
- **Build Tool:** Vite

### Database
- **RDBMS:** PostgreSQL 18+
- **Migrations:** Flyway
- **Connection Pool:** HikariCP

### DevOps
- **Containerization:** Docker
- **Orchestration:** Docker Compose (local), Kubernetes (production)
- **Monitoring:** Prometheus + Grafana
- **Tracing:** Zipkin
- **CI/CD:** GitHub Actions / GitLab CI

---

## Architectural Constraints

1. **Response Time:** APIs must respond <200ms (95th percentile)
2. **Business Rules:**
   - BR-1: Student age must be 3-18 years
   - BR-2: Mobile number must be unique per student
   - BR-3: Class capacity enforcement (future)
3. **Scalability:** Stateless design for horizontal scaling
4. **Code Quality:** All code must be testable and adhere to SOLID principles
5. **Coverage:** Minimum 80% test coverage
6. **Security:** Input validation at all entry points, prepared statements, HTTPS

---

## Lessons Learned Integration

This architecture incorporates lessons from `LESSONS_LEARNED.md`:

### [D-001] SpringDoc OpenAPI Compatibility
- **Issue:** SpringDoc OpenAPI 2.3.0 incompatible with Spring Boot 3.5.0
- **Solution:** Use SpringDoc OpenAPI 2.7.0+ with Spring Boot 3.5.0
- **Reference:** `01-system-architecture.md` section 9.1

### [D-001] CORS Configuration for Multiple Dev Ports
- **Issue:** Frontend running on different ports (3000, 5173) blocked by CORS
- **Solution:** Configure CORS to allow both React and Vite dev server ports
- **Reference:** `05-backend-implementation-guide.md` section 9.2, `03-api-specification.md` section 8

---

## Change Log

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-12-06 | Architect Agent | Initial comprehensive architecture |

---

## Next Steps

1. **Backend Team:**
   - Set up multi-module Maven project
   - Implement Student Service following DDD patterns
   - Set up PostgreSQL with Flyway migrations
   - Implement Configuration Service
   - Set up Redis caching
   - Configure Zipkin tracing

2. **Frontend Team:**
   - Set up Next.js 15 project with App Router
   - Implement Student management UI
   - Set up React Query for server state
   - Implement forms with Zod validation
   - Set up E2E tests with Playwright

3. **QA Team:**
   - Set up test automation framework
   - Implement unit tests (target 80% coverage)
   - Set up TestContainers for integration tests
   - Create E2E test suites
   - Set up CI/CD quality gates

4. **DevOps Team:**
   - Set up Docker Compose for local development
   - Configure CI/CD pipelines
   - Set up monitoring (Prometheus + Grafana)
   - Configure production environment
   - Implement backup and recovery procedures

---

**Architecture Status:** APPROVED
**Last Updated:** 2025-12-06
**Maintained By:** Software Architect Team
