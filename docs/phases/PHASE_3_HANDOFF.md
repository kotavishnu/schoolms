# Phase 3 Handoff Document: Backend Development

**From Phase:** 2 - SDLC Planning
**To Phase:** 3 - Backend Development
**Target Agent:** senior-backend-developer
**Date:** 2026-01-22

---

## Objective

The senior-backend-developer agent must implement both microservices (Student Service + Configuration Service) following the task breakdown in BACKEND_TASKS.md and adhering to all architectural patterns defined in Phase 1.

---

## Context Provided

### 1. Preserved Artifacts (Post-/clear)

The following files will be preserved and available:

#### Requirements & Architecture
- `specs/REQUIREMENTS.md` - Core product requirements
- `specs/architecture/01-system-architecture.md` - Microservices design, DDD layers
- `specs/architecture/02-database-design.md` - ERD, DDL, field alignment matrix
- `specs/architecture/03-business-rules.md` - Drools integration, DRL files
- `specs/architecture/04-security-architecture.md` - Security blueprints
- `specs/architecture/05-backend-implementation-guide.md` - **PRIMARY REFERENCE**
- `specs/architecture/06-frontend-implementation-guide.md` - For API contract alignment

#### Task Breakdown
- `docs/tasks/BACKEND_TASKS.md` - **EXECUTION CHECKLIST (29 tasks)**
- `docs/tasks/TASK_DEPENDENCIES.md` - Dependency graph, critical path
- `docs/tasks/QA_TASKS.md` - For understanding test requirements

#### Phase Summaries
- `docs/phases/PHASE_1_ARCHITECTURE_SUMMARY.md` - Architecture decisions
- `docs/phases/PHASE_2_SDLC_PLANNING_SUMMARY.md` - Task planning summary

---

## Execution Model

### Task Sequence
Follow BACKEND_TASKS.md sequentially: **BE-001 → BE-002 → ... → BE-029**

### Critical Path (Must Complete in Order)
```
BE-001 → BE-002 → BE-003 → BE-005 → BE-006 → BE-007 → BE-010 → BE-011 → BE-029
```

**Bottleneck:** BE-007 (Drools Validation Service) - Complex business rules implementation

### Parallel Execution Opportunities
- **Day 2:** BE-003, BE-006, BE-013, BE-014 (all depend on BE-002)
- **Day 2:** BE-004, BE-005, BE-008 (all depend on BE-003)
- **Day 3:** BE-007, BE-009 (can run in parallel after dependencies)
- **Services:** Student Service (BE-002 to BE-014) and Configuration Service (BE-015 to BE-020) share NO dependencies after BE-001

---

## CRITICAL CONSTRAINTS (MANDATORY)

### Global Directives (MUST ENFORCE)

1. **D-001: Spring Boot + SpringDoc Compatibility**
   ```xml
   Spring Boot: 3.3.5 (EXACT)
   SpringDoc OpenAPI: 2.6.0 (EXACT)
   ```
   **Violation:** Causes incompatibility errors, API documentation fails

2. **D-002: MapStruct for DTO Mapping**
   - Use MapStruct 1.5.5.Final for ALL DTO conversions
   - NO manual mapping allowed (error-prone, unmaintainable)

3. **D-003: Optimistic Locking**
   - ALL entities MUST have @Version field
   - Prevents lost updates in concurrent scenarios

4. **D-010: Database-per-Service Isolation**
   - Student Service → student_db ONLY
   - Configuration Service → config_db ONLY
   - ZERO cross-service database access (absolute rule)

### Technology Stack (NON-NEGOTIABLE)

```xml
<!-- Parent -->
Spring Boot: 3.3.5

<!-- Core Dependencies -->
Java: 21 LTS
PostgreSQL JDBC: 42.7.x
Drools: 9.44.0.Final
MapStruct: 1.5.5.Final
Redis: Lettuce (included in Spring Boot)
Flyway: 9.x
SpringDoc OpenAPI: 2.6.0

<!-- Testing -->
JUnit: 5.x (Spring Boot default)
Mockito: 5.x (Spring Boot default)
Testcontainers: 1.19.x (for integration tests)
```

### Field Naming Convention (API Layer)

| Database Field | Entity Field | DTO Field | API Field (JSON) |
|----------------|--------------|-----------|------------------|
| mobile | mobile | mobile | mobile |
| student_id | studentId | studentId | studentId |
| aadhaar_number | aadhaarNumber | aadhaarNumber | aadhaarNumber |

**Critical:** Backend uses `mobile` (NOT `phone`), `studentId` (NOT `id`)
**Rationale:** Frontend will handle mapping in service layer (D-007)

---

## Microservices Architecture

### Student Service

**Port:** 8081
**Base Package:** com.school.student
**Database:** student_db (PostgreSQL port 5433)
**Redis:** DB 0
**API Base:** /api/v1/students

**Components:**
- Student entity (id, studentId, firstName, lastName, dateOfBirth, mobile, status, etc.)
- Enrollment entity (optional, for future use)
- StudentRepository (Spring Data JPA)
- DroolsValidationService (7 business rules)
- StudentService (business logic)
- StudentController (REST endpoints)
- Redis cache for frequently accessed students

### Configuration Service

**Port:** 8082
**Base Package:** com.school.configuration
**Database:** config_db (PostgreSQL port 5434)
**Redis:** DB 1
**API Base:** /api/v1/configurations

**Components:**
- ConfigurationSetting entity (id, settingId, category, key, value)
- ConfigurationRepository (Spring Data JPA)
- ConfigurationService (CRUD + category retrieval)
- ConfigurationController (REST endpoints)
- Redis cache for configuration settings

### Shared Infrastructure

- Docker Compose (PostgreSQL x2, Redis)
- SpringDoc OpenAPI UI (Swagger)
- Structured logging (JSON format, correlation IDs)
- Spring Actuator (health, metrics)
- Global exception handler
- CORS configuration

---

## Business Rules Implementation (Drools)

### Required DRL File: student-validation.drl

Location: `student-service/src/main/resources/rules/student-validation.drl`

### Business Rules to Implement

| Rule ID | Description | Priority |
|---------|-------------|----------|
| BR-STU-001 | Age between 3-18 years at registration | HIGH |
| BR-STU-002 | Mobile number must be unique | HIGH |
| BR-STU-003 | Status must be ACTIVE or INACTIVE | MEDIUM |
| BR-STU-004 | Required fields validation (firstName, lastName, DOB, mobile) | HIGH |
| BR-STU-005 | Only Name, Mobile, Status editable after creation | MEDIUM |
| BR-STU-006 | StudentID auto-generated (format: STD-YYYYMMDD-NNNN) | LOW |
| BR-STU-007 | Default status is ACTIVE | LOW |

### Drools Integration Pattern

```java
// DroolsConfig.java
@Configuration
public class DroolsConfig {
    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/student-validation.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }
}

// DroolsValidationService.java
@Service
public class DroolsValidationService {
    private final KieContainer kieContainer;

    public ValidationResult validate(StudentValidationRequest request) {
        KieSession kieSession = kieContainer.newKieSession();
        ValidationResult result = new ValidationResult();
        kieSession.insert(request);
        kieSession.insert(result);
        kieSession.fireAllRules();
        kieSession.dispose();
        return result;
    }
}
```

**Reference:** specs/architecture/03-business-rules.md sections 2.3, 2.4

---

## Database Schema

### SQL Script Location
**File:** docs/tasks/school_management.sql (created in BE-001)

### Key Constraints

#### Students Table
```sql
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    mobile VARCHAR(10) NOT NULL UNIQUE,
    father_name VARCHAR(100),
    mother_name VARCHAR(100),
    identification_mark VARCHAR(255),
    aadhaar_number VARCHAR(12) UNIQUE,
    email VARCHAR(255) UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    version INTEGER NOT NULL DEFAULT 0,  -- Optimistic locking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_students_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_students_mobile CHECK (mobile ~ '^\d{10}$'),
    CONSTRAINT chk_students_age CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years' AND
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    )
);
```

#### Configurations Table
```sql
CREATE TABLE configurations (
    id BIGSERIAL PRIMARY KEY,
    setting_id VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,  -- Optimistic locking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uniq_configurations_category_key UNIQUE (category, key),
    CONSTRAINT chk_configurations_category CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'))
);
```

**Reference:** specs/architecture/02-database-design.md sections 3.2, 3.3

---

## API Endpoints to Implement

### Student Service Endpoints

```
POST   /api/v1/students              - Create student
GET    /api/v1/students              - List students (with search)
GET    /api/v1/students/{studentId}  - Get student by ID
PUT    /api/v1/students/{studentId}  - Update student (only Name, Mobile, Status)
DELETE /api/v1/students/{studentId}  - Delete student
PATCH  /api/v1/students/{studentId}/status - Update status
```

### Configuration Service Endpoints

```
POST   /api/v1/configurations                 - Create configuration
GET    /api/v1/configurations                 - List all configurations
GET    /api/v1/configurations/{settingId}     - Get configuration by ID
GET    /api/v1/configurations/category/{cat}  - Get by category
PUT    /api/v1/configurations/{settingId}     - Update configuration
DELETE /api/v1/configurations/{settingId}     - Delete configuration
```

**Reference:** sms_api_specification.yaml (if exists in specs/)

---

## Implementation Patterns

### Layer Architecture (DDD-Based)

```
Controller Layer (Presentation)
    ↓
Service Layer (Application)
    ↓
Repository Layer (Infrastructure)
    ↓
Entity Layer (Domain)
```

### Code Organization

```
student-service/
├── src/main/java/com/school/student/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Spring Data JPA
│   ├── entity/         # JPA entities
│   ├── dto/            # Data Transfer Objects
│   ├── mapper/         # MapStruct mappers
│   ├── validation/     # Drools integration
│   ├── exception/      # Custom exceptions
│   └── config/         # Configuration classes
├── src/main/resources/
│   ├── rules/          # Drools DRL files
│   ├── application.yml # Spring configuration
│   └── db/migration/   # Flyway scripts (optional)
└── src/test/java/      # Unit + Integration tests
```

**Reference:** specs/architecture/05-backend-implementation-guide.md section 3.1

### MapStruct DTO Mapping Example

```java
@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentDTO toDTO(Student entity);
    Student toEntity(CreateStudentRequest request);
    void updateEntityFromDTO(UpdateStudentRequest request, @MappingTarget Student entity);
}
```

**Reference:** specs/architecture/05-backend-implementation-guide.md section 3.4

### Optimistic Locking Example

```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version  // Optimistic locking
    private Integer version;

    // Other fields...
}
```

**Reference:** specs/architecture/05-backend-implementation-guide.md section 3.3.3

---

## Testing Requirements

### Unit Tests (95% Domain Coverage)
- Test all entity business methods
- Test all Drools rules (100% rule coverage)
- Test service layer business logic
- Mock dependencies (repositories, external services)

### Integration Tests (85% Application Coverage)
- Test API endpoints (contract validation)
- Test database interactions (Testcontainers)
- Test Redis caching
- Test exception handling

### Performance Tests
- p95 response time <200ms
- Load testing (100 concurrent users)
- Database query optimization (N+1 prevention)

**Reference:** docs/tasks/QA_TASKS.md (QA-BE-001 to QA-BE-009)

---

## Docker Compose Configuration

### Required Services

```yaml
version: '3.8'
services:
  student-db:
    image: postgres:18
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: student_db
      POSTGRES_USER: school_admin
      POSTGRES_PASSWORD: school_pass
    volumes:
      - student-data:/var/lib/postgresql/data
      - ./docs/tasks/school_management.sql:/docker-entrypoint-initdb.d/init.sql

  config-db:
    image: postgres:18
    ports:
      - "5434:5432"
    environment:
      POSTGRES_DB: config_db
      POSTGRES_USER: school_admin
      POSTGRES_PASSWORD: school_pass
    volumes:
      - config-data:/var/lib/postgresql/data
      - ./docs/tasks/school_management.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

volumes:
  student-data:
  config-data:
  redis-data:
```

**Reference:** docs/tasks/BACKEND_TASKS.md (BE-021)

---

## Expected Deliverables

### Code Artifacts
1. **Student Service** (complete Spring Boot application)
   - All 14 tasks (BE-002 to BE-014) implemented
   - Port 8081, connects to student_db (port 5433), Redis DB 0

2. **Configuration Service** (complete Spring Boot application)
   - All 6 tasks (BE-015 to BE-020) implemented
   - Port 8082, connects to config_db (port 5434), Redis DB 1

3. **SQL Schema Script** (school_management.sql)
   - BE-001: Complete DDL with all constraints

4. **Docker Compose** (docker-compose.yml)
   - BE-021: PostgreSQL x2, Redis

5. **Documentation**
   - BE-029: Deployment guide, API usage examples

### Testing Artifacts
6. **Unit Tests** (95% domain coverage target)
7. **Integration Tests** (API contract validation)

### API Documentation
8. **SpringDoc UI** (accessible at http://localhost:8081/swagger-ui.html and http://localhost:8082/swagger-ui.html)

---

## Success Criteria

### Functional
- ✅ All 29 backend tasks (BE-001 to BE-029) completed
- ✅ Student Service runs on port 8081
- ✅ Configuration Service runs on port 8082
- ✅ All REST endpoints functional
- ✅ All 7 Drools business rules enforced
- ✅ Database constraints enforced (age, unique mobile, etc.)
- ✅ Redis caching operational

### Technical
- ✅ Spring Boot 3.3.5 + SpringDoc 2.6.0 (D-001)
- ✅ MapStruct used for all DTO mapping (D-002)
- ✅ @Version field on all entities (D-003)
- ✅ Database-per-service isolation verified (D-010)
- ✅ Field naming: `mobile`, `studentId` (NOT `phone`, `id`)

### Quality
- ✅ Unit tests: 95% domain coverage
- ✅ Integration tests: API contract validated
- ✅ Performance: p95 <200ms
- ✅ No cross-service database access
- ✅ SpringDoc UI accessible and accurate

---

## Files to Reference During Implementation

### Primary References (MUST READ)
1. **docs/tasks/BACKEND_TASKS.md** - Task checklist (BE-001 to BE-029)
2. **specs/architecture/05-backend-implementation-guide.md** - Implementation patterns
3. **specs/architecture/02-database-design.md** - Database schema, DDL
4. **specs/architecture/03-business-rules.md** - Drools DRL files

### Secondary References
5. **specs/architecture/01-system-architecture.md** - Microservices design
6. **specs/architecture/04-security-architecture.md** - Security considerations
7. **docs/tasks/TASK_DEPENDENCIES.md** - Critical path, dependencies
8. **docs/tasks/QA_TASKS.md** - Test requirements

### Tertiary References
9. **specs/REQUIREMENTS.md** - Original requirements
10. **docs/phases/PHASE_1_ARCHITECTURE_SUMMARY.md** - Architecture decisions
11. **docs/phases/PHASE_2_SDLC_PLANNING_SUMMARY.md** - Task planning rationale

---

## Agent Invocation Command

```
Task tool with:
subagent_type: senior-backend-developer
description: "Implement backend microservices"
prompt: "
You are the senior-backend-developer agent for Phase 3 of the DevPipeline.

Context:
- Phase 1 (Architecture) and Phase 2 (SDLC Planning) are complete
- Handoff document: docs/phases/PHASE_3_HANDOFF.md
- Task checklist: docs/tasks/BACKEND_TASKS.md (29 tasks)
- Implementation guide: specs/architecture/05-backend-implementation-guide.md

Your Task:
Implement both microservices following BACKEND_TASKS.md sequentially:
1. Student Service (port 8081) - tasks BE-002 to BE-014
2. Configuration Service (port 8082) - tasks BE-015 to BE-020
3. Shared infrastructure - tasks BE-001, BE-021 to BE-029

CRITICAL CONSTRAINTS:
- D-001: Spring Boot 3.3.5 + SpringDoc 2.6.0 (EXACT)
- D-002: MapStruct for ALL DTO mapping
- D-003: @Version on ALL entities
- D-010: Database-per-service isolation (ZERO cross-service access)
- Field naming: mobile (NOT phone), studentId (NOT id)
- Drools: Implement all 7 rules (BR-STU-001 to BR-STU-007)

Deliverables:
- Student Service codebase
- Configuration Service codebase
- SQL schema (school_management.sql)
- Docker Compose setup
- Unit tests (95% domain coverage)
- Integration tests
- SpringDoc API documentation

Please proceed with implementing the backend services following the task checklist.
"
```

---

## Next Phase Preview

**Phase 4:** Backend QA (backend-qa-orchestrator agent)
- Executes QA tasks from QA_TASKS.md (QA-BE-001 to QA-BE-009)
- Validates all 7 Drools rules (BR-STU-001 to BR-STU-007)
- Verifies API contract compliance
- Tests database isolation (no cross-service access)
- Performance testing (p95 <200ms)
- Fixes issues in loop until all tests pass

---

## Handoff Complete

**Status:** ✅ READY FOR PHASE 3
**Target Agent:** senior-backend-developer
**Action Required:** Orchestrator to execute `/clear` and launch Phase 3 agent
