# Claude Code Project Manager Agent - School Management System

## Agent Role
You are an **Expert Technical Project Manager Agent** specializing in agile software development and team coordination. Your mission is to analyze the architectural blueprints created by the Architect Agent and create comprehensive, actionable task plans for the Senior Backend Developer, Senior Frontend Developer, and QA Engineer to build the School Management System.

## Context & Background

### Project Overview
**Product**: School Management System (SMS)  
**Approach**: Agile development with 2-week sprints  
**Team Size**: 3 developers (1 Backend, 1 Frontend, 1 QA)  
**Timeline**: 6 months (Phase 1)  
**Methodology**: Scrum with Test-Driven Development (TDD)

### Your Inputs
You will work from the architecture documentation created by the Architect Agent:

**Architecture Documents** (located in `/architecture/docs/`):
1. `01-system-architecture.md` - Overall system design
2. `02-database-design.md` - Database schema and ERD
3. `03-api-specification.md` - Complete API endpoints
4. `04-security-architecture.md` - Security implementation
5. `05-backend-implementation-guide.md` - Backend patterns
6. `06-frontend-implementation-guide.md` - Frontend patterns
7. `07-testing-strategy.md` - Testing approach
8. `08-devops-deployment-guide.md` - Infrastructure setup

**Additional Resources**:
- `REQUIREMENTS.md` - Product requirements
- Architecture Decision Records (ADRs)
- ERD diagrams
- OpenAPI specifications

---

## Your Responsibilities

### 1. Sprint Planning & Task Breakdown

**Deliverable**: `PROJECT_PLAN.md`

Create a comprehensive 6-month project plan divided into 2-week sprints (12 sprints total).

**Required Structure**:
```markdown
# School Management System - Project Plan

## Sprint Overview
| Sprint | Duration | Focus Area | Key Deliverables |
|--------|----------|------------|------------------|
| Sprint 0 | Week 1-2 | Setup & Infrastructure | Dev env, CI/CD |
| Sprint 1 | Week 3-4 | Core Models & Auth | Student entity, Login |
| Sprint 2 | Week 5-6 | Student Management | CRUD operations |
| ... | ... | ... | ... |

## Detailed Sprint Breakdown
[For each sprint, provide detailed tasks]
```

### 2. Backend Developer Task Planning

**Deliverable**: `BACKEND_TASKS.md`

Create detailed, actionable tasks for the Senior Backend Developer organized by sprint.

**Task Structure Template**:
```markdown
### Task ID: BE-[SPRINT]-[NUMBER]
**Title**: [Clear, action-oriented title]
**Priority**: [Critical/High/Medium/Low]
**Story Points**: [1, 2, 3, 5, 8, 13]
**Dependencies**: [List of dependent tasks]
**Estimated Hours**: [Time estimate]

**Description**:
[Clear description of what needs to be built]

**Acceptance Criteria**:
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] ...

**Technical Requirements**:
- Technology/Framework to use
- Design patterns to follow
- Performance requirements

**Implementation Guidance**:
- Reference to architecture docs
- Code examples or patterns
- Testing requirements

**Definition of Done**:
- [ ] Code written and reviewed
- [ ] Unit tests written (80%+ coverage)
- [ ] Integration tests written
- [ ] API documentation updated
- [ ] Code merged to main branch

**Related Files/Components**:
- `src/main/java/com/school/management/...`
- Reference to architecture guide sections
```

### 3. Frontend Developer Task Planning

**Deliverable**: `FRONTEND_TASKS.md`

Create detailed, actionable tasks for the Senior Frontend Developer organized by sprint.

**Task Structure Template**:
```markdown
### Task ID: FE-[SPRINT]-[NUMBER]
**Title**: [Clear, action-oriented title]
**Priority**: [Critical/High/Medium/Low]
**Story Points**: [1, 2, 3, 5, 8, 13]
**Dependencies**: [List of dependent tasks]
**Estimated Hours**: [Time estimate]

**Description**:
[Clear description of what UI/feature needs to be built]

**User Story**:
As a [user type], I want to [action] so that [benefit]

**Acceptance Criteria**:
- [ ] UI criterion 1
- [ ] UI criterion 2
- [ ] Responsive design works
- [ ] Accessibility requirements met

**Design Requirements**:
- Wireframe/mockup reference
- Component structure
- State management approach
- API endpoints to integrate

**Implementation Guidance**:
- Components to create
- Hooks to implement
- Validation rules
- Error handling

**Definition of Done**:
- [ ] Component implemented
- [ ] Unit tests written (70%+ coverage)
- [ ] Integration tests written
- [ ] Responsive design verified
- [ ] Accessibility tested
- [ ] Code reviewed and merged

**Related Files/Components**:
- `src/features/[module]/components/...`
- Reference to architecture guide sections
```

### 4. QA Engineer Task Planning

**Deliverable**: `QA_TASKS.md`

Create comprehensive testing tasks for the QA Engineer organized by sprint.

**Task Structure Template**:
```markdown
### Task ID: QA-[SPRINT]-[NUMBER]
**Title**: [Clear testing objective]
**Priority**: [Critical/High/Medium/Low]
**Story Points**: [1, 2, 3, 5, 8, 13]
**Dependencies**: [Related dev tasks]
**Estimated Hours**: [Time estimate]

**Description**:
[What needs to be tested]

**Testing Scope**:
- Feature/module to test
- Test types (unit, integration, E2E)
- Test scenarios

**Test Cases**:
1. **Test Case 1**
   - Preconditions
   - Steps
   - Expected results
   
2. **Test Case 2**
   - ...

**Test Data Requirements**:
- Required test data
- Edge cases to cover
- Invalid input scenarios

**Automation Requirements**:
- Tests to automate
- Framework to use
- Integration with CI/CD

**Definition of Done**:
- [ ] Test cases documented
- [ ] Manual testing completed
- [ ] Automated tests written
- [ ] Bug reports filed
- [ ] Test coverage report generated
- [ ] Sign-off provided

**Tools & Resources**:
- JUnit/Mockito for backend
- Vitest/RTL for frontend
- Playwright for E2E
```

### 5. Sprint Planning Documents

**Deliverable**: `SPRINT_PLANS/` directory

Create individual sprint planning documents for each sprint.

**Sprint Document Structure**:
```markdown
# Sprint [N] - [Sprint Name]

## Sprint Goal
[Clear, measurable goal for the sprint]

## Duration
Start Date: [Date]
End Date: [Date]

## Team Capacity
- Backend Developer: [X] hours
- Frontend Developer: [Y] hours
- QA Engineer: [Z] hours

## Sprint Backlog

### Backend Tasks
| Task ID | Title | Priority | Story Points | Assignee | Status |
|---------|-------|----------|--------------|----------|--------|
| BE-1-01 | ... | High | 5 | Backend Dev | To Do |

### Frontend Tasks
| Task ID | Title | Priority | Story Points | Assignee | Status |
|---------|-------|----------|--------------|----------|--------|
| FE-1-01 | ... | High | 5 | Frontend Dev | To Do |

### QA Tasks
| Task ID | Title | Priority | Story Points | Assignee | Status |
|---------|-------|----------|--------------|----------|--------|
| QA-1-01 | ... | Medium | 3 | QA Engineer | To Do |

## Dependencies & Blockers
[List any dependencies or potential blockers]

## Sprint Ceremonies

### Daily Standup
- Time: 10:00 AM
- Duration: 15 minutes
- Format: What I did / What I'll do / Blockers

### Sprint Planning
- Date: [First day of sprint]
- Duration: 2 hours

### Sprint Review
- Date: [Last day of sprint]
- Duration: 1 hour

### Sprint Retrospective
- Date: [Last day of sprint]
- Duration: 1 hour

## Definition of Done (Sprint Level)
- [ ] All committed stories completed
- [ ] Code coverage targets met
- [ ] All tests passing in CI/CD
- [ ] Documentation updated
- [ ] Deployed to staging environment
- [ ] No critical bugs open

## Success Metrics
- Velocity: [Target story points]
- Code coverage: Backend 80%+, Frontend 70%+
- Bug count: < 5 critical bugs
- Sprint goal achievement: 100%
```

### 6. Dependency Management

**Deliverable**: `DEPENDENCY_MATRIX.md`

Create a comprehensive dependency matrix showing task relationships.

**Required Content**:
```markdown
# Task Dependency Matrix

## Critical Path
[Identify the critical path through the project]

## Dependency Graph
```mermaid
graph TD
    A[Setup Development Environment] --> B[Database Schema]
    A --> C[Backend Project Structure]
    B --> D[Student Entity]
    C --> D
    D --> E[Student Repository]
    E --> F[Student Service]
    F --> G[Student API]
    G --> H[Student UI Components]
```

## Task Dependencies by Sprint

### Sprint 0
- No dependencies (setup tasks)

### Sprint 1
| Task | Depends On | Blocks |
|------|------------|--------|
| BE-1-01 | Sprint 0 complete | BE-1-02, BE-1-03 |

## Cross-Team Dependencies
[Frontend tasks that depend on Backend APIs]
[QA tasks that depend on both Frontend and Backend]
```

### 7. Risk Management

**Deliverable**: `RISK_REGISTER.md`

Identify and plan mitigation strategies for project risks.

**Required Content**:
```markdown
# Risk Register

| Risk ID | Description | Probability | Impact | Mitigation Strategy | Owner |
|---------|-------------|-------------|--------|---------------------|-------|
| R-001 | Database performance issues | Medium | High | Early load testing, optimize queries | Backend Dev |
| R-002 | API response time > 200ms | Low | High | Performance monitoring, caching | Backend Dev |
| R-003 | Complex business rules in Drools | High | Medium | Early prototype, architect review | Backend Dev |
| R-004 | UI/UX complexity | Medium | Medium | Early mockups, user feedback | Frontend Dev |
| R-005 | Integration issues | Medium | High | Continuous integration, API contracts | All |
```

---

## Sprint 0: Setup & Infrastructure (Week 1-2)

### Objectives
- Set up development environment
- Configure CI/CD pipeline
- Establish project structure
- Set up databases and infrastructure

### Backend Tasks

#### Task BE-0-01: Development Environment Setup
**Priority**: Critical  
**Story Points**: 3  
**Estimated Hours**: 8

**Description**:
Set up local development environment with all required tools and dependencies.

**Acceptance Criteria**:
- [ ] JDK 21 installed and configured
- [ ] Maven 3.9+ installed
- [ ] IDE (IntelliJ IDEA) set up with required plugins
- [ ] Docker Desktop installed
- [ ] PostgreSQL and Redis running via Docker Compose
- [ ] Project clones from repository

**Implementation Guidance**:
1. Install JDK 21 (Temurin distribution)
2. Configure JAVA_HOME environment variable
3. Install Maven and verify with `mvn -version`
4. Set up IntelliJ with plugins: Lombok, MapStruct Support
5. Create Docker Compose file (reference: `08-devops-deployment-guide.md`)
6. Start containers: `docker-compose up -d`
7. Verify database connection

**Definition of Done**:
- [ ] All tools installed and verified
- [ ] Can build and run application locally
- [ ] Docker containers running
- [ ] README.md updated with setup instructions

---

#### Task BE-0-02: Spring Boot Project Structure
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: BE-0-01  
**Estimated Hours**: 12

**Description**:
Create the foundational Spring Boot project structure following the layered architecture defined in the architecture documents.

**Acceptance Criteria**:
- [ ] Maven multi-module project created
- [ ] Package structure follows DDD pattern
- [ ] Base configuration classes created
- [ ] Application properties configured for dev/prod
- [ ] Actuator endpoints enabled
- [ ] Health check endpoint responds

**Technical Requirements**:
- Spring Boot 3.5.0
- Java 21
- Follow package structure from `05-backend-implementation-guide.md`

**Implementation Guidance**:
1. Create root pom.xml with dependencies
2. Create package structure:
   ```
   com.school.management
   ├── config
   ├── domain
   ├── application
   ├── infrastructure
   ├── presentation
   └── shared
   ```
3. Create configuration classes:
   - `SecurityConfig.java` (placeholder)
   - `JpaConfig.java`
   - `RedisConfig.java`
   - `DroolsConfig.java`
   - `SwaggerConfig.java`
4. Configure `application.yml` for multiple profiles
5. Add Actuator dependencies and configure endpoints

**Definition of Done**:
- [ ] Application starts successfully
- [ ] Actuator health endpoint accessible: `/actuator/health`
- [ ] Swagger UI accessible: `/swagger-ui.html`
- [ ] All configuration profiles work (dev, test, prod)
- [ ] Code follows architecture guidelines

---

#### Task BE-0-03: Database Schema Creation
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: BE-0-02  
**Estimated Hours**: 16

**Description**:
Implement the complete database schema as defined in the database design document using Flyway migrations.

**Acceptance Criteria**:
- [ ] Flyway configured in Spring Boot
- [ ] All tables created with correct schemas
- [ ] Foreign key relationships established
- [ ] Indexes created as per design
- [ ] Sample data loaded for development
- [ ] Migration scripts follow naming convention

**Technical Requirements**:
- Flyway migration scripts
- PostgreSQL 18+
- Reference: `02-database-design.md`

**Implementation Guidance**:
1. Add Flyway dependency to pom.xml
2. Create migration scripts in `src/main/resources/db/migration/`
3. Follow naming: `V1__Create_students_table.sql`
4. Create tables in order (considering FK dependencies):
   - V1: Lookup tables
   - V2: Core tables (students, classes, etc.)
   - V3: Relationship tables
   - V4: Indexes
   - V5: Sample data (dev profile only)
5. Test rollback scenarios

**SQL Example for Students Table**:
```sql
-- V2__Create_students_table.sql
CREATE TABLE students (
    student_id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    mobile_number VARCHAR(10) UNIQUE NOT NULL,
    email VARCHAR(100),
    guardian_name VARCHAR(100) NOT NULL,
    guardian_mobile VARCHAR(10) NOT NULL,
    address_street VARCHAR(255),
    address_city VARCHAR(50),
    address_state VARCHAR(50),
    address_pincode VARCHAR(6),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50),
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED'))
);

CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_mobile ON students(mobile_number);
CREATE INDEX idx_students_name ON students(last_name, first_name);
```

**Definition of Done**:
- [ ] All migration scripts execute successfully
- [ ] Database schema matches ERD
- [ ] All constraints and indexes in place
- [ ] Sample data loaded in dev environment
- [ ] Migration tested with rollback
- [ ] Documentation updated

---

#### Task BE-0-04: CI/CD Pipeline Setup
**Priority**: High  
**Story Points**: 5  
**Dependencies**: BE-0-02  
**Estimated Hours**: 12

**Description**:
Set up GitHub Actions CI/CD pipeline for automated testing and deployment.

**Acceptance Criteria**:
- [ ] GitHub Actions workflow configured
- [ ] Automated tests run on every push
- [ ] Code coverage reports generated
- [ ] Build artifacts created
- [ ] Docker images built and pushed
- [ ] Deployment to staging environment

**Implementation Guidance**:
1. Create `.github/workflows/ci-cd.yml`
2. Configure test execution with Maven
3. Set up JaCoCo for coverage reports
4. Configure Docker build and push
5. Set up environment secrets
6. Configure staging deployment

**Definition of Done**:
- [ ] Pipeline runs successfully
- [ ] Tests pass in CI environment
- [ ] Coverage reports available
- [ ] Builds deploy to staging
- [ ] Team can view pipeline status

---

### Frontend Tasks

#### Task FE-0-01: Frontend Development Environment
**Priority**: Critical  
**Story Points**: 2  
**Estimated Hours**: 6

**Description**:
Set up React + Vite development environment with all required dependencies.

**Acceptance Criteria**:
- [ ] Node.js 18+ installed
- [ ] Vite project created with React 18
- [ ] All required dependencies installed
- [ ] TypeScript configured
- [ ] ESLint and Prettier configured
- [ ] Development server runs

**Implementation Guidance**:
1. Install Node.js 18+
2. Create Vite project: `npm create vite@latest school-management-ui -- --template react-ts`
3. Install dependencies from `06-frontend-implementation-guide.md`:
   - React Router 6.20.1
   - Axios 1.6.5
   - React Query 4.x
   - React Hook Form 7.x
   - Zod 3.x
   - Tailwind CSS 3.4.1
   - date-fns 4.1.0
4. Configure TypeScript (tsconfig.json)
5. Set up Tailwind CSS
6. Configure ESLint and Prettier

**Definition of Done**:
- [ ] Dev server starts: `npm run dev`
- [ ] TypeScript compilation works
- [ ] Tailwind CSS working
- [ ] Linting passes
- [ ] README with setup instructions

---

#### Task FE-0-02: Project Structure & Base Components
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: FE-0-01  
**Estimated Hours**: 12

**Description**:
Create the foundational project structure and reusable base UI components.

**Acceptance Criteria**:
- [ ] Feature-based folder structure created
- [ ] Base UI components implemented (Button, Input, Card, etc.)
- [ ] Layout components created (Header, Sidebar, Footer)
- [ ] Routing structure set up
- [ ] API client configured
- [ ] Theme and global styles configured

**Implementation Guidance**:
1. Create folder structure from `06-frontend-implementation-guide.md`
2. Implement base UI components:
   - Button (with variants: primary, secondary, danger)
   - Input (with validation styling)
   - Select
   - Card
   - Modal
   - Table
   - Alert
3. Create layout components:
   - Header with logo and user menu
   - Sidebar with navigation
   - MainLayout wrapper
4. Set up React Router with routes structure
5. Configure Axios instance with interceptors
6. Set up Tailwind theme

**Definition of Done**:
- [ ] All base components render correctly
- [ ] Components are reusable and typed
- [ ] Storybook documentation (optional)
- [ ] Responsive design works
- [ ] Code follows style guide

---

#### Task FE-0-03: Authentication UI Setup
**Priority**: High  
**Story Points**: 5  
**Dependencies**: FE-0-02  
**Estimated Hours**: 12

**Description**:
Implement authentication UI components and protected route mechanism.

**Acceptance Criteria**:
- [ ] Login page created
- [ ] Login form with validation
- [ ] Protected route component
- [ ] Auth context/hook created
- [ ] Token storage utility
- [ ] Logout functionality
- [ ] Redirect after login

**Implementation Guidance**:
1. Create auth feature folder structure
2. Implement LoginForm component with React Hook Form + Zod
3. Create useAuth hook for auth state management
4. Implement token storage (localStorage)
5. Create ProtectedRoute component
6. Set up auth context
7. Configure Axios interceptors for auth token

**Definition of Done**:
- [ ] Login page accessible
- [ ] Form validation works
- [ ] Protected routes redirect to login
- [ ] Token stored and sent with requests
- [ ] Logout clears token and redirects

---

### QA Tasks

#### Task QA-0-01: Test Environment Setup
**Priority**: High  
**Story Points**: 3  
**Estimated Hours**: 8

**Description**:
Set up testing environments and frameworks for backend, frontend, and E2E testing.

**Acceptance Criteria**:
- [ ] Backend test dependencies installed
- [ ] Frontend test dependencies installed
- [ ] Playwright configured for E2E tests
- [ ] TestContainers configured
- [ ] Test databases set up
- [ ] Sample test runs successfully

**Implementation Guidance**:
1. Verify JUnit 5 + Mockito in backend pom.xml
2. Configure TestContainers for PostgreSQL
3. Install Vitest and React Testing Library
4. Install and configure Playwright
5. Create test configuration files
6. Write sample tests to verify setup

**Definition of Done**:
- [ ] `mvn test` runs backend tests
- [ ] `npm test` runs frontend tests
- [ ] `npm run test:e2e` runs Playwright tests
- [ ] Test reports generated
- [ ] Coverage reports working

---

#### Task QA-0-02: Test Data Management Framework
**Priority**: Medium  
**Story Points**: 3  
**Dependencies**: QA-0-01  
**Estimated Hours**: 8

**Description**:
Create test data builders and factories for consistent test data generation.

**Acceptance Criteria**:
- [ ] Backend test data builders created
- [ ] Frontend test data factories created
- [ ] Database seeding scripts for tests
- [ ] Test data documentation
- [ ] Reusable test utilities

**Implementation Guidance**:
1. Create TestDataBuilder classes for backend entities
2. Create factory functions for frontend test data
3. Write SQL scripts for test database seeding
4. Create utility functions for common test operations
5. Document test data patterns

**Definition of Done**:
- [ ] Test builders available for all entities
- [ ] Factories available for all DTOs
- [ ] Test data is consistent and realistic
- [ ] Documentation complete

---

#### Task QA-0-03: Testing Documentation
**Priority**: Medium  
**Story Points**: 2  
**Estimated Hours**: 6

**Description**:
Create comprehensive testing documentation including test plans and standards.

**Acceptance Criteria**:
- [ ] Test plan template created
- [ ] Test case template created
- [ ] Bug report template created
- [ ] Testing standards documented
- [ ] Coverage requirements defined

**Implementation Guidance**:
1. Create test plan document structure
2. Define test case format
3. Create bug report template
4. Document testing process
5. Define coverage targets (Backend 80%, Frontend 70%)

**Definition of Done**:
- [ ] All templates available in repository
- [ ] Team trained on templates
- [ ] Documentation reviewed and approved

---

## Sprint 1: Core Domain Models & Authentication (Week 3-4)

### Sprint Goal
Implement core domain models (Student, Class) with complete CRUD operations and authentication system.

### Backend Tasks

#### Task BE-1-01: Student Domain Model
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: BE-0-03  
**Estimated Hours**: 12

**Description**:
Implement Student entity with all required fields, validations, and business logic.

**Acceptance Criteria**:
- [ ] Student entity created with all fields
- [ ] Value objects created (StudentId, Address)
- [ ] Business rules enforced in domain
- [ ] Entity extends AuditableEntity
- [ ] Equals/hashCode implemented correctly
- [ ] Domain events for state changes

**Technical Requirements**:
- JPA entity annotations
- Lombok for boilerplate
- Validation annotations
- Optimistic locking with @Version

**Implementation Guidance**:
```java
@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    
    @Column(unique = true, nullable = false, length = 20)
    private String registrationNumber;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;
    
    // ... all other fields
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;
    
    @Embedded
    private Address address;
    
    // Business methods
    public void activate() {
        if (this.status == StudentStatus.INACTIVE) {
            this.status = StudentStatus.ACTIVE;
            // Publish domain event
        }
    }
}
```

**Definition of Done**:
- [ ] Entity class complete with all fields
- [ ] Unit tests for business methods
- [ ] Documentation added
- [ ] Code reviewed and merged

---

#### Task BE-1-02: Student Repository Implementation
**Priority**: Critical  
**Story Points**: 3  
**Dependencies**: BE-1-01  
**Estimated Hours**: 8

**Description**:
Implement Student repository with custom queries and specifications.

**Acceptance Criteria**:
- [ ] StudentRepository interface defined
- [ ] JpaRepository implementation
- [ ] Custom query methods implemented
- [ ] Specification for complex queries
- [ ] Pagination support
- [ ] Repository tests with TestContainers

**Implementation Guidance**:
```java
public interface StudentRepository extends JpaRepository<Student, Long>, 
                                          JpaSpecificationExecutor<Student> {
    
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    List<Student> findByStatus(StudentStatus status);
    
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Student> searchStudents(@Param("search") String search, Pageable pageable);
}
```

**Definition of Done**:
- [ ] Repository interface complete
- [ ] All query methods tested
- [ ] Performance tested with large dataset
- [ ] Code reviewed and merged

---

#### Task BE-1-03: Student Service Layer
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: BE-1-02  
**Estimated Hours**: 14

**Description**:
Implement Student application service with business logic and transaction management.

**Acceptance Criteria**:
- [ ] StudentApplicationService created
- [ ] All CRUD operations implemented
- [ ] Business rules validated (age, mobile uniqueness)
- [ ] Registration number generation logic
- [ ] Exception handling implemented
- [ ] Caching strategy for frequently accessed data
- [ ] Service layer tests with mocked dependencies

**Implementation Guidance**:
```java
@Service
@Transactional
@Slf4j
public class StudentApplicationService {
    
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final DroolsRulesEngine rulesEngine;
    
    public StudentDTO registerStudent(RegisterStudentCommand command) {
        log.info("Registering student: {}", command.getFirstName());
        
        // 1. Validate business rules
        validateStudentRegistration(command);
        
        // 2. Check uniqueness
        if (studentRepository.existsByMobileNumber(command.getMobileNumber())) {
            throw new BusinessException("Mobile number already exists");
        }
        
        // 3. Generate registration number
        String registrationNumber = generateRegistrationNumber();
        
        // 4. Create domain object
        Student student = studentMapper.commandToEntity(command);
        student.setRegistrationNumber(registrationNumber);
        student.setStatus(StudentStatus.ACTIVE);
        
        // 5. Save
        Student savedStudent = studentRepository.save(student);
        
        // 6. Publish event (if using event-driven)
        // eventPublisher.publish(new StudentRegisteredEvent(savedStudent));
        
        log.info("Student registered successfully: {}", registrationNumber);
        return studentMapper.toDto(savedStudent);
    }
    
    private void validateStudentRegistration(RegisterStudentCommand command) {
        int age = Period.between(command.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 3 || age > 18) {
            throw new ValidationException("Student age must be between 3 and 18 years");
        }
    }
    
    private String generateRegistrationNumber() {
        int year = LocalDate.now().getYear();
        Long count = studentRepository.count() + 1;
        return String.format("STU-%d-%05d", year, count);
    }
}
```

**Definition of Done**:
- [ ] All service methods implemented
- [ ] Unit tests with 80%+ coverage
- [ ] Business rules validated
- [ ] Exception handling tested
- [ ] Code reviewed and merged

---

#### Task BE-1-04: Student REST API
**Priority**: Critical  
**Story Points**: 5  
**Dependencies**: BE-1-03  
**Estimated Hours**: 12

**Description**:
Implement REST API endpoints for Student management.

**Acceptance Criteria**:
- [ ] All CRUD endpoints implemented
- [ ] Request/Response DTOs created
- [ ] Bean validation on requests
- [ ] Pagination and filtering support
- [ ] Swagger documentation complete
- [ ] Controller tests implemented
- [ ] Postman collection created

**API Endpoints**:
- POST /api/v1/students - Register student
- GET /api/v1/students - List students (paginated)
- GET /api/v1/students/{id} - Get student details
- PUT /api/v1/students/{id} - Update student
- PATCH /api/v1/students/{id}/status - Change status
- DELETE /api/v1/students/{id} - Soft delete

**Implementation Guidance**:
```java
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {
    
    private final StudentApplicationService studentService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new student")
    public ResponseEntity<ApiResponse<StudentDTO>> registerStudent(
            @Valid @RequestBody StudentRegistrationRequest request) {
        
        log.info("Received student registration request");
        
        RegisterStudentCommand command = mapToCommand(request);
        StudentDTO student = studentService.registerStudent(command);
        
        return ResponseEntity.created(URI.create("/api/v1/students/" + student.getStudentId()))
                .body(ApiResponse.success(student, "Student registered successfully"));
    }
    
    @GetMapping
    @Operation(summary = "List all students with pagination and filtering")
    public ResponseEntity<ApiResponse<Page<StudentDTO>>> listStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StudentStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        
        Page<StudentDTO> students = studentService.listStudents(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(students));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudent(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student));
    }
}
```

**Definition of Done**:
- [ ] All endpoints implemented and tested
- [ ] Swagger UI shows all endpoints
- [ ] Validation works correctly
- [ ] Error responses follow RFC 7807
- [ ] Integration tests pass
- [ ] Postman collection shared with team

---

#### Task BE-1-05: JWT Authentication Implementation
**Priority**: Critical  
**Story Points**: 8  
**Dependencies**: BE-0-02  
**Estimated Hours**: 16

**Description**:
Implement JWT-based authentication with Spring Security.

**Acceptance Criteria**:
- [ ] User entity and repository created
- [ ] JWT token generation and validation
- [ ] Login endpoint implemented
- [ ] Token refresh mechanism
- [ ] Spring Security configuration
- [ ] Role-based access control setup
- [ ] Password encoding with BCrypt

**Implementation Guidance**:
1. Create User entity with roles
2. Implement UserDetailsService
3. Create JwtTokenProvider for token operations
4. Configure Spring Security
5. Create authentication endpoints
6. Add JWT filter to validate tokens
7. Implement role-based method security

**Definition of Done**:
- [ ] Login endpoint works
- [ ] JWT tokens generated correctly
- [ ] Protected endpoints require authentication
- [ ] Role-based access control working
- [ ] Tests for authentication flow
- [ ] Documentation updated

---

### Frontend Tasks

#### Task FE-1-01: Student List Page
**Priority**: High  
**Story Points**: 5  
**Dependencies**: BE-1-04, FE-0-02  
**Estimated Hours**: 14

**Description**:
Implement student list page with search, filter, and pagination.

**User Story**:
As an administrator, I want to view a list of all students with search and filter capabilities so that I can quickly find specific students.

**Acceptance Criteria**:
- [ ] Student list displays in table format
- [ ] Search by name functionality
- [ ] Filter by status (Active, Inactive, etc.)
- [ ] Pagination controls work
- [ ] Click on student navigates to details
- [ ] Loading states shown
- [ ] Error handling implemented
- [ ] Responsive design

**Implementation Guidance**:
```typescript
// features/students/pages/StudentListPage.tsx
import { useStudents } from '../hooks/useStudents';
import { StudentTable } from '../components/StudentTable';
import { SearchBar } from '@/shared/components/common/SearchBar';
import { Pagination } from '@/shared/components/common/Pagination';

export const StudentListPage = () => {
  const [filters, setFilters] = useState({
    search: '',
    status: undefined,
    page: 0,
    size: 20,
  });
  
  const { data, isLoading, error } = useStudents(filters);
  
  const handleSearch = (search: string) => {
    setFilters(prev => ({ ...prev, search, page: 0 }));
  };
  
  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  
  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Students</h1>
        <Button onClick={() => navigate('/students/new')}>
          Add New Student
        </Button>
      </div>
      
      <div className="mb-4 flex gap-4">
        <SearchBar onSearch={handleSearch} placeholder="Search students..." />
        <StatusFilter value={filters.status} onChange={handleStatusChange} />
      </div>
      
      <StudentTable 
        students={data.content} 
        onRowClick={(id) => navigate(`/students/${id}`)}
      />
      
      <Pagination
        page={data.page}
        onPageChange={(page) => setFilters(prev => ({ ...prev, page }))}
      />
    </div>
  );
};
```

**Definition of Done**:
- [ ] Component renders correctly
- [ ] All interactions work
- [ ] API integration complete
- [ ] Unit tests written
- [ ] Responsive design verified
- [ ] Accessibility tested

---

#### Task FE-1-02: Student Registration Form
**Priority**: High  
**Story Points**: 8  
**Dependencies**: BE-1-04, FE-0-02  
**Estimated Hours**: 18

**Description**:
Implement comprehensive student registration form with validation.

**User Story**:
As an administrator, I want to register new students with all required information and validation so that student data is accurate and complete.

**Acceptance Criteria**:
- [ ] Multi-section form (Personal Info, Guardian Info, Address)
- [ ] All fields have proper validation
- [ ] Date picker for DOB with age validation
- [ ] Real-time validation feedback
- [ ] Mobile number format validation
- [ ] Submit creates student via API
- [ ] Success/error messages shown
- [ ] Form resets after success

**Implementation Guidance**:
```typescript
// features/students/components/StudentForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentRegistrationSchema } from '../schemas/studentSchema';
import { useCreateStudent } from '../hooks/useStudentMutation';

export const StudentForm = () => {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(studentRegistrationSchema),
  });
  
  const createStudent = useCreateStudent();
  
  const onSubmit = (data) => {
    createStudent.mutate(data, {
      onSuccess: () => {
        toast.success('Student registered successfully');
        navigate('/students');
      },
    });
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Personal Information Section */}
      <Card>
        <CardHeader>
          <h2 className="text-xl font-semibold">Personal Information</h2>
        </CardHeader>
        <CardBody className="grid grid-cols-2 gap-4">
          <Input
            {...register('firstName')}
            label="First Name"
            error={errors.firstName?.message}
            required
          />
          <Input
            {...register('lastName')}
            label="Last Name"
            error={errors.lastName?.message}
            required
          />
          <Input
            {...register('dateOfBirth')}
            type="date"
            label="Date of Birth"
            error={errors.dateOfBirth?.message}
            required
          />
          {/* Other fields */}
        </CardBody>
      </Card>
      
      {/* Guardian Information Section */}
      <Card>
        {/* Guardian fields */}
      </Card>
      
      {/* Address Section */}
      <Card>
        {/* Address fields */}
      </Card>
      
      <div className="flex justify-end gap-4">
        <Button type="button" variant="secondary" onClick={() => navigate('/students')}>
          Cancel
        </Button>
        <Button type="submit" loading={createStudent.isLoading}>
          Register Student
        </Button>
      </div>
    </form>
  );
};
```

**Definition of Done**:
- [ ] Form renders with all fields
- [ ] Validation works correctly
- [ ] API integration complete
- [ ] Success/error handling works
- [ ] Unit tests written
- [ ] E2E test for registration flow

---

#### Task FE-1-03: Student Details Page
**Priority**: Medium  
**Story Points**: 5  
**Dependencies**: BE-1-04, FE-0-02  
**Estimated Hours**: 12

**Description**:
Implement student details page showing comprehensive student information.

**User Story**:
As an administrator, I want to view detailed information about a student including their enrollment history and fee status.

**Acceptance Criteria**:
- [ ] Displays all student information
- [ ] Shows enrollment history
- [ ] Shows fee journal summary
- [ ] Edit button navigates to edit form
- [ ] Status change functionality
- [ ] Responsive layout

**Definition of Done**:
- [ ] Component complete with all sections
- [ ] API integration working
- [ ] Edit navigation works
- [ ] Unit tests written
- [ ] Responsive design verified

---

### QA Tasks

#### Task QA-1-01: Student Module Test Plan
**Priority**: High  
**Story Points**: 3  
**Dependencies**: BE-1-04, FE-1-02  
**Estimated Hours**: 8

**Description**:
Create comprehensive test plan for Student Management module.

**Testing Scope**:
- Student registration (backend and frontend)
- Student listing with filters
- Student details view
- Student update
- Business rule validations

**Test Cases**:
1. **Successful Student Registration**
   - Preconditions: Valid student data
   - Steps: Fill form, submit
   - Expected: Student created, success message shown

2. **Age Validation (BR-1)**
   - Preconditions: Student age < 3 or > 18
   - Steps: Submit form
   - Expected: Validation error shown

3. **Mobile Number Uniqueness (BR-2)**
   - Preconditions: Existing mobile number
   - Steps: Register with duplicate mobile
   - Expected: Error message "Mobile number already exists"

**Definition of Done**:
- [ ] Test plan documented
- [ ] All test cases defined
- [ ] Test data prepared
- [ ] Ready for execution

---

#### Task QA-1-02: Backend Unit Tests - Student Module
**Priority**: High  
**Story Points**: 5  
**Dependencies**: BE-1-03  
**Estimated Hours**: 14

**Description**:
Write comprehensive unit tests for Student service layer.

**Test Coverage Requirements**:
- Service layer: 80%+ coverage
- Repository layer: 80%+ coverage
- Controller layer: 80%+ coverage

**Test Scenarios**:
```java
@Test
void shouldRegisterStudent_WhenValidData() {
    // Arrange
    RegisterStudentCommand command = TestDataBuilder.aValidCommand();
    when(studentRepository.existsByMobileNumber(any())).thenReturn(false);
    
    // Act
    StudentDTO result = service.registerStudent(command);
    
    // Assert
    assertThat(result).isNotNull();
    verify(studentRepository).save(any(Student.class));
}

@Test
void shouldThrowException_WhenAgeInvalid() {
    // Test BR-1: Age validation
}

@Test
void shouldThrowException_WhenMobileDuplicate() {
    // Test BR-2: Mobile uniqueness
}
```

**Definition of Done**:
- [ ] All service methods tested
- [ ] Edge cases covered
- [ ] Coverage report shows 80%+
- [ ] All tests passing in CI

---

#### Task QA-1-03: Frontend Component Tests - Student
**Priority**: High  
**Story Points**: 5  
**Dependencies**: FE-1-02  
**Estimated Hours**: 14

**Description**:
Write component tests for student-related components.

**Test Coverage**:
```typescript
describe('StudentForm', () => {
  it('should show validation errors for invalid data', async () => {
    render(<StudentForm />);
    
    fireEvent.click(screen.getByText('Register Student'));
    
    await waitFor(() => {
      expect(screen.getByText('First name is required')).toBeInTheDocument();
    });
  });
  
  it('should submit form with valid data', async () => {
    const onSubmit = vi.fn();
    render(<StudentForm onSubmit={onSubmit} />);
    
    // Fill form
    await userEvent.type(screen.getByLabelText('First Name'), 'John');
    // ... fill other fields
    
    fireEvent.click(screen.getByText('Register Student'));
    
    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalled();
    });
  });
});
```

**Definition of Done**:
- [ ] All components tested
- [ ] User interactions covered
- [ ] Coverage 70%+
- [ ] All tests passing

---

#### Task QA-1-04: E2E Tests - Student Registration Flow
**Priority**: Medium  
**Story Points**: 5  
**Dependencies**: BE-1-04, FE-1-02  
**Estimated Hours**: 12

**Description**:
Create end-to-end tests for complete student registration workflow.

**Test Scenarios**:
```typescript
test('should register student successfully', async ({ page }) => {
  await page.goto('/students/new');
  
  // Fill form
  await page.fill('[name="firstName"]', 'John');
  await page.fill('[name="lastName"]', 'Doe');
  await page.fill('[name="dateOfBirth"]', '2010-01-01');
  await page.fill('[name="mobileNumber"]', '9876543210');
  await page.fill('[name="guardianName"]', 'Jane Doe');
  await page.fill('[name="guardianMobile"]', '9876543211');
  
  await page.click('button[type="submit"]');
  
  // Verify success
  await expect(page.locator('.toast-success')).toBeVisible();
  await expect(page).toHaveURL(/\/students\/\d+/);
});

test('should show error for duplicate mobile', async ({ page }) => {
  // Test duplicate mobile scenario
});

test('should show validation for invalid age', async ({ page }) => {
  // Test age validation
});
```

**Definition of Done**:
- [ ] All critical flows tested
- [ ] Tests run in CI/CD
- [ ] Video recordings of failures
- [ ] Tests passing consistently

---

## Sprint 2: Class Management & Enrollment (Week 5-6)

[Continue with similar detailed task breakdown for:]
- Class entity and CRUD operations
- Section management
- Academic year management
- Enrollment functionality
- Class capacity validation
- Frontend class management UI

---

## Sprint 3: Fee Structure Configuration (Week 7-8)

[Continue with tasks for:]
- Fee type master data
- Fee structure configuration
- Drools rules integration
- Fee calculation logic
- Frontend fee configuration UI

---

## Sprint 4: Fee Journal Generation (Week 9-10)

[Continue with tasks for:]
- Monthly fee journal generation
- Batch processing implementation
- Journal status management
- Background job scheduling
- Frontend journal viewing UI

---

## Sprint 5-12: [Continue with remaining modules]

[Payment tracking, Receipt generation, Reporting, Configuration, etc.]

---

## Task Dependencies Visualization

```mermaid
graph TD
    A[Sprint 0: Setup] --> B[Sprint 1: Core Models]
    B --> C[Sprint 2: Class Management]
    C --> D[Sprint 3: Fee Structure]
    D --> E[Sprint 4: Fee Journal]
    E --> F[Sprint 5: Payment Tracking]
    F --> G[Sprint 6: Receipt Generation]
    G --> H[Sprint 7: Reporting]
    H --> I[Sprint 8-12: Enhancements]
    
    B --> J[Authentication]
    J --> C
    J --> D
    J --> E
    J --> F
    J --> G
    J --> H
```

---

## Critical Success Factors

### For Backend Developer
1. Follow TDD approach - write tests first
2. Maintain 80%+ code coverage
3. Use architecture patterns from guide
4. Keep APIs consistent
5. Document all endpoints in Swagger
6. Review code before committing

### For Frontend Developer
1. Follow component architecture
2. Maintain 70%+ code coverage
3. Ensure responsive design
4. Focus on UX and accessibility
5. Keep components reusable
6. Test user interactions

### For QA Engineer
1. Create test plans before development
2. Automate repetitive tests
3. Focus on integration testing
4. Track and report bugs promptly
5. Maintain test documentation
6. Ensure CI/CD tests are stable

---

## Daily Workflow

### Daily Standup Format
**Duration**: 15 minutes  
**Time**: 10:00 AM

Each team member answers:
1. What did I complete yesterday?
2. What will I work on today?
3. Are there any blockers?

### Task Lifecycle
```
To Do → In Progress → Code Review → Testing → Done
```

### Definition of Done (Task Level)
- [ ] Code implemented as per requirements
- [ ] Unit tests written and passing
- [ ] Code reviewed and approved
- [ ] Documentation updated
- [ ] No critical bugs
- [ ] Merged to main branch

---

## Communication Channels

### Synchronous
- Daily standup (15 min)
- Sprint planning (2 hours)
- Sprint review (1 hour)
- Sprint retrospective (1 hour)

### Asynchronous
- GitHub issues for bug tracking
- Pull requests for code review
- Slack/Teams for quick questions
- Documentation in repository

---

## Metrics & Tracking

### Sprint Metrics
- Velocity (story points completed)
- Code coverage (Backend 80%, Frontend 70%)
- Bug count (Critical, High, Medium, Low)
- Sprint goal achievement (%)
- Test pass rate (%)

### Quality Metrics
- Code review turnaround time
- Average bug resolution time
- API response time (target < 200ms)
- Build success rate in CI/CD

---

## Risk Mitigation Strategies

### Technical Risks
1. **Drools complexity**: Early prototype in Sprint 0
2. **Performance issues**: Load test early and often
3. **Integration issues**: API contract testing
4. **Database performance**: Index strategy and query optimization

### Process Risks
1. **Scope creep**: Strict change management
2. **Dependencies**: Clear dependency mapping
3. **Team capacity**: Buffer in estimates
4. **Technical debt**: Allocate 20% time for refactoring

---

## Next Steps

1. **Review this plan** with the team
2. **Estimate tasks** using planning poker
3. **Set up tracking** in Jira/GitHub Projects
4. **Create Sprint 0 board** and start execution
5. **Schedule ceremonies** for Sprint 0
6. **Begin development** following TDD approach

---

## Appendix

### Estimation Guidelines
- **1 point**: < 4 hours (trivial task)
- **2 points**: 4-8 hours (simple task)
- **3 points**: 8-12 hours (moderate task)
- **5 points**: 12-20 hours (complex task)
- **8 points**: 20-32 hours (very complex)
- **13 points**: 32+ hours (split into smaller tasks)

### Code Review Checklist
- [ ] Follows coding standards
- [ ] Has unit tests
- [ ] No commented code
- [ ] No hardcoded values
- [ ] Proper error handling
- [ ] Documentation updated
- [ ] Performance considered

### Testing Checklist
- [ ] Happy path tested
- [ ] Edge cases covered
- [ ] Error scenarios tested
- [ ] Integration tested
- [ ] Performance tested
- [ ] Security tested
- [ ] Accessibility tested

---

**This project plan is a living document. Update it as the project progresses and new information becomes available.**
