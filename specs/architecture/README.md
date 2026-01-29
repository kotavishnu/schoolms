# School Management System - Architecture Documentation
**Comprehensive Blueprint for Production-Ready Implementation**

Version: 1.0.0
Last Updated: 2026-01-28
Status: Final

---

## Document Overview

This directory contains the complete architectural blueprint for the School Management System (SMS). These documents serve as the definitive guide for Developer Agents and engineering teams implementing the system.

### Document Structure

| Document | Purpose | Key Audience |
|----------|---------|--------------|
| **01-system-architecture.md** | High-level system design, component interactions, technology stack | All stakeholders, architects |
| **02-database-design.md** | Complete database schema, DDL scripts, data model alignment | Backend developers, DBAs |
| **03-business-rules.md** | Drools rule engine implementation strategy | Backend developers, business analysts |
| **04-security-architecture.md** | Security controls, authentication, authorization, threat mitigation | Security engineers, backend developers |
| **05-backend-implementation-guide.md** | Mandatory backend coding patterns, layer responsibilities | Backend Developer Agent |
| **06-frontend-implementation-guide.md** | Frontend implementation patterns, API integration | Frontend Developer Agent |

---

## Quick Start Guide

### For Backend Developers

1. Read **01-system-architecture.md** (Section 3: Container Architecture)
2. Study **02-database-design.md** for schema understanding
3. Review **03-business-rules.md** for Drools integration
4. Implement following **05-backend-implementation-guide.md** patterns

**Key Files to Reference**:
- OpenAPI Spec: `specs/sms_api_specification.yaml`
- Requirements: `specs/REQUIREMENTS.md`
- Testing Strategy: `specs/TESTING_STRATEGY.md`

### For Frontend Developers

1. Read **01-system-architecture.md** (Section 2: System Context)
2. Review **06-frontend-implementation-guide.md** (MANDATORY reading)
3. Copy components from `frontend/reference-code/` EXACTLY
4. Implement API integration using service layer pattern

**Key Files to Reference**:
- Frontend Design Spec: `specs/FRONTEND_DESIGN_SPEC.md`
- Reference Components: `frontend/reference-code/app/components/`
- Theme Tokens: `frontend/reference-code/styles/theme.css`

### For Architects & Tech Leads

Read all documents in sequence to understand:
- Microservices boundaries and isolation patterns
- Database-per-service enforcement
- Security architecture and compliance
- Performance optimization strategies
- Testing pyramid and quality gates

---

## Architecture Highlights

### Microservices Design

```
┌─────────────────┐     ┌──────────────────┐
│  Student Service│     │ Config Service   │
│  Port: 8081     │     │ Port: 8082       │
│  DB: student_db │     │ DB: config_db    │
└─────────────────┘     └──────────────────┘
        │                        │
        └────────┬───────────────┘
                 │
         ┌───────▼────────┐
         │  Frontend SPA  │
         │  Port: 3000    │
         └────────────────┘
```

**Strict Isolation**:
- No cross-database queries
- No shared tables or foreign keys
- Communication via REST APIs only
- Independent deployment units

### Technology Stack Summary

**Backend**:
- Java 21, Spring Boot 3.5.0
- PostgreSQL 18 (database-per-service)
- Drools 9.44.0.Final (business rules)
- Redis 7 (caching)
- MapStruct, Lombok, Micrometer

**Frontend**:
- React 18, TypeScript 5
- Tailwind CSS 4, shadcn/ui
- React Hook Form, Zod
- Axios, React Router 6
- Vite 5

**DevOps**:
- Docker, Docker Compose
- Flyway (database migrations)
- TestContainers (integration testing)
- GitHub Actions (CI/CD)

### Data Model

**Student Database**:
- `students` table (16 columns, 5 indexes)
- `enrollments` table (one-to-many relationship)
- Optimistic locking via `version` column

**Configuration Database**:
- `configuration_settings` table
- Category-based grouping (GENERAL, ACADEMIC, FINANCIAL)
- Support for encrypted values

### Business Rules (Drools)

| Rule ID | Description | Priority |
|---------|-------------|----------|
| BR-1 | Age validation (3-18 years) | HIGH |
| BR-2 | Mobile uniqueness | HIGH |
| BR-3 | Aadhaar format validation | MEDIUM |
| BR-6 | Edit restrictions (immutable fields) | HIGH |
| BR-7 | One enrollment per academic year | HIGH |

---

## Critical Implementation Constraints

### Backend (MANDATORY)

1. **Layered Architecture**: Strict separation (Presentation → Application → Domain → Infrastructure)
2. **Repository Pattern**: Domain interfaces, infrastructure implementations
3. **DTO Mapping**: MapStruct for all DTO-Domain conversions
4. **Optimistic Locking**: `@Version` on all mutable entities
5. **N+1 Prevention**: Use `@EntityGraph` or `JOIN FETCH`
6. **Exception Handling**: RFC 7807 Problem Details format
7. **Logging**: Structured logging with correlation IDs

### Frontend (MANDATORY)

1. **NO CUSTOM STYLES**: Use reference code components ONLY
2. **Service Layer**: All API calls through `services/api/` abstraction
3. **Validation**: React Hook Form + Zod schemas (mirror backend validation)
4. **Error Handling**: Toast notifications for all API errors
5. **State Management**: Context for global, Hooks for local state
6. **Performance**: Lazy loading, memoization, code splitting

---

## Quality Gates

### Code Coverage Targets

| Layer | Target | Metric |
|-------|--------|--------|
| Domain Layer | 95% | Line coverage |
| Application Layer | 85% | Line coverage |
| Infrastructure Layer | 70% | Branch coverage |
| Frontend Components | 70% | Statement coverage |

### Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| API Response Time (p95) | <200ms | Micrometer histograms |
| Database Query Time (p95) | <100ms | Hibernate statistics |
| Frontend Time to Interactive | <3s | Lighthouse CI |

---

## Security Considerations

### Authentication & Authorization

- **Phase 1**: Basic authentication (assumed externalized)
- **Future**: JWT-based OAuth 2.0 / OIDC integration
- **RBAC**: SUPER_ADMIN > ADMIN > STAFF > TEACHER

### Data Protection

- **In Transit**: TLS 1.3 for all connections
- **At Rest**: PostgreSQL encryption, optional field-level encryption
- **PII Fields**: Mobile, email, Aadhaar, address (access-controlled)

### Threat Mitigation

| Threat | Mitigation |
|--------|------------|
| SQL Injection | Prepared statements, JPA parameterized queries |
| XSS | Content Security Policy, React auto-escaping |
| CSRF | Token-based protection for state-changing ops |
| Mass Assignment | Explicit DTO binding, validation |

---

## Development Workflow

### Git Branch Strategy

- `main`: Production-ready code
- `develop`: Integration branch
- `feature/*`: Feature development
- `bugfix/*`: Bug fixes

### Commit Convention

```
<type>(<scope>): <subject>

Types: feat, fix, refactor, test, docs, chore
Scope: student-service, config-service, frontend

Example: feat(student-service): add Drools age validation rule
```

### Pull Request Process

1. Create feature branch from `develop`
2. Implement changes with tests (coverage targets met)
3. Run local quality gates: `mvn verify` (backend), `npm run test` (frontend)
4. Create PR with description linking to requirements
5. Automated CI checks (tests, linting, security scans)
6. Code review approval (2 reviewers)
7. Merge to `develop` (squash commits)

---

## Deployment Architecture

### Local Development

```bash
# Start all services
docker-compose up -d

# Verify services
curl http://localhost:8081/actuator/health  # Student Service
curl http://localhost:8082/actuator/health  # Config Service
curl http://localhost:3000                  # Frontend
```

### Production Deployment

```yaml
# docker-compose.prod.yml (simplified)
services:
  student-service:
    image: sms/student-service:latest
    ports: ["8081:8081"]
    environment:
      DB_URL: ${DB_URL}
      REDIS_HOST: redis

  config-service:
    image: sms/config-service:latest
    ports: ["8082:8082"]

  frontend:
    image: sms/frontend:latest
    ports: ["80:80"]
```

---

## Monitoring & Observability

### Health Endpoints

```
GET /actuator/health          # Service health
GET /actuator/health/liveness # Kubernetes liveness probe
GET /actuator/prometheus      # Prometheus metrics
```

### Key Metrics

**Application Metrics**:
- `students.registered.total` (counter)
- `students.validation.failures` (counter, tagged by rule)
- `config.updates.total` (counter)

**Infrastructure Metrics**:
- JVM heap/non-heap memory
- Database connection pool usage
- HTTP request rate/duration

### Logging

**Structured Format** (JSON):
```json
{
  "timestamp": "2026-01-28T10:30:00Z",
  "level": "INFO",
  "message": "Student registered",
  "studentId": "STD-20260128-0001",
  "correlationId": "abc-123",
  "userId": "admin",
  "duration": 45
}
```

---

## Testing Strategy

### Test Pyramid

```
        ╱╲
       ╱  ╲      E2E Tests (10%)
      ╱────╲     Playwright: Critical user journeys
     ╱      ╲
    ╱────────╲   Integration Tests (30%)
   ╱          ╲  TestContainers: API + Database
  ╱────────────╲ Unit Tests (60%)
 ╱              ╲ JUnit/Vitest: Business logic
╱────────────────╲
```

### Test Data Builders

**Java**:
```java
Student student = StudentTestDataBuilder.aStudent()
    .withFirstName("John")
    .withLastName("Doe")
    .withAge(15)
    .build();
```

**TypeScript**:
```typescript
const student = createMockStudent({
  firstName: 'John',
  lastName: 'Doe',
  status: 'ACTIVE',
});
```

---

## Database Migrations

### Flyway Versioning

```
db/migration/
├── V1__create_students_table.sql
├── V2__create_enrollments_table.sql
├── V3__add_aadhaar_index.sql
└── V4__seed_sample_data.sql
```

### Migration Best Practices

1. **Naming**: `V{version}__{description}.sql`
2. **Idempotency**: Use `IF NOT EXISTS` for additive changes
3. **Backward Compatibility**: Never drop columns in production
4. **Testing**: Run on staging environment first
5. **Rollback Plan**: Maintain rollback scripts for critical changes

---

## API Contract Enforcement

### OpenAPI Specification

**Source**: `specs/sms_api_specification.yaml`

**Validation**:
- Request/response schemas strictly enforced
- Bean Validation annotations match OpenAPI constraints
- Frontend Zod schemas mirror backend validation

**Example Alignment**:
```yaml
# OpenAPI
mobile:
  type: string
  pattern: '^\d{10}$'

# Backend
@Pattern(regexp = "^\d{10}$")
private String mobile;

# Frontend
mobile: z.string().regex(/^\d{10}$/)
```

---

## Troubleshooting Guide

### Common Issues

**Backend**:
- **N+1 Queries**: Enable Hibernate SQL logging, use `@EntityGraph`
- **Optimistic Lock Failures**: Ensure `version` field included in update DTOs
- **Drools Rule Errors**: Check rule syntax, verify global variables injected

**Frontend**:
- **CORS Errors**: Verify backend CORS configuration allows origin
- **Form Validation Not Triggering**: Ensure `@Valid` on controller parameter
- **API 409 Conflict**: Check for duplicate mobile/Aadhaar in database

**Database**:
- **Migration Failed**: Check Flyway schema history table, fix failed migration
- **Connection Pool Exhausted**: Increase HikariCP `maximum-pool-size`

---

## Reference Documents

### Core Specifications

- **Requirements**: `specs/REQUIREMENTS.md`
- **API Contract**: `specs/sms_api_specification.yaml`
- **Frontend Design**: `specs/FRONTEND_DESIGN_SPEC.md`
- **Testing Strategy**: `specs/TESTING_STRATEGY.md`

### Architecture Documents (This Directory)

1. `01-system-architecture.md` - System design & container architecture
2. `02-database-design.md` - Complete schema & DDL scripts
3. `03-business-rules.md` - Drools implementation strategy
4. `04-security-architecture.md` - Security controls & threat model
5. `05-backend-implementation-guide.md` - Backend coding patterns
6. `06-frontend-implementation-guide.md` - Frontend coding patterns

---

## Change Management

### Document Updates

- **Frequency**: Reviewed quarterly or upon major architectural changes
- **Approval**: Requires sign-off from Tech Lead and Product Owner
- **Version Control**: All changes tracked in Git with descriptive commits

### Architectural Decision Records (ADRs)

Future architectural decisions should be documented as ADRs in `specs/adr/`:

```
specs/adr/
├── 001-microservices-database-isolation.md
├── 002-drools-for-business-rules.md
└── 003-react-hook-form-validation.md
```

---

## Support & Contact

### Technical Queries

- **Backend Architecture**: Contact Backend Lead
- **Frontend Architecture**: Contact Frontend Lead
- **Security**: Contact Security Team
- **DevOps/Infrastructure**: Contact Platform Team

### Documentation Feedback

Submit issues or improvement suggestions via GitHub Issues with label `documentation`.

---

## Appendix: Quick Reference

### Environment Variables

**Backend (Student Service)**:
```bash
DB_URL=jdbc:postgresql://localhost:5432/student_db
DB_USERNAME=postgres
DB_PASSWORD=<secure>
REDIS_HOST=localhost
REDIS_PORT=6379
```

**Frontend**:
```bash
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

### Useful Commands

**Backend**:
```bash
mvn clean verify                  # Build + run tests
mvn spring-boot:run               # Run locally
mvn flyway:migrate                # Run database migrations
docker build -t sms/student-service .
```

**Frontend**:
```bash
npm install                       # Install dependencies
npm run dev                       # Start dev server
npm run build                     # Production build
npm run test                      # Run tests
docker build -t sms/frontend .
```

**Docker Compose**:
```bash
docker-compose up -d              # Start all services
docker-compose logs -f student-service
docker-compose down -v            # Stop and remove volumes
```

---

**Document Maintained By**: Architecture Team
**Last Reviewed**: 2026-01-28
**Next Review**: 2026-04-28
