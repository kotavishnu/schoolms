# Backend Development Tasks - School Management System

## Project Overview
Implementation of Student Registration and Configuration Management microservices using Spring Boot, PostgreSQL, and Clean Architecture patterns.

## Technology Stack
- Java 21 LTS
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- PostgreSQL 15+
- Maven 3.9.x
- Docker & Docker Compose
- Flyway (Migration)
- MapStruct (Mapping)
- Lombok

---

## PHASE 1: Project Foundation & Setup

### Task 1.1: Development Environment Setup
**Priority**: P0 (Critical)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Setup IDE (IntelliJ IDEA or VS Code) with required plugins
  - Spring Boot plugin
  - Lombok plugin
  - MapStruct support
  - Docker integration
- [ ] Configure Git and setup SSH keys
- [ ] Install Postman or Insomnia for API testing

**Acceptance Criteria**:
- All tools installed and verified with version check
- IDE properly configured with annotation processing enabled
- Docker running and accessible via CLI
- Git configured with user credentials

**Dependencies**: None

---

### Task 1.2: Create Eureka Server (Service Discovery)
**Priority**: P0 (Critical)
**Estimated Time**: 3 hours

**Subtasks**:
- [ ] Create Maven project for eureka-server
- [ ] Add Spring Cloud Netflix Eureka Server dependency
- [ ] Configure application.yml with Eureka server settings
- [ ] Create main application class with @EnableEurekaServer
- [ ] Configure server port (8761)
- [ ] Test Eureka dashboard access (http://localhost:8761)
- [ ] Create Dockerfile for Eureka server

**Acceptance Criteria**:
- Eureka server starts successfully on port 8761
- Eureka dashboard accessible in browser
- No errors in application logs
- Docker image builds successfully

**Dependencies**: Task 1.1

**Reference Files**:
- Location: `eureka-server/`
- Architecture Doc: `01-SYSTEM-ARCHITECTURE.md` (Section: Service Registry)

---

### Task 1.3: Create API Gateway
**Priority**: P0 (Critical)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Create Maven project for api-gateway
- [ ] Add Spring Cloud Gateway dependencies
- [ ] Configure routing for Student Service (/api/v1/students/**)
- [ ] Configure routing for Configuration Service (/api/v1/configurations/**)
- [ ] Setup Eureka client configuration
- [ ] Configure CORS settings
- [ ] Add request logging filter
- [ ] Configure custom error handling
- [ ] Test gateway routing with mock responses
- [ ] Create Dockerfile for API Gateway

**Acceptance Criteria**:
- API Gateway starts and registers with Eureka
- Routes configured for both services
- CORS properly configured
- Request/response logging working
- Docker image builds successfully

**Dependencies**: Task 1.2

**Reference Files**:
- Location: `api-gateway/`
- Architecture Doc: `01-SYSTEM-ARCHITECTURE.md` (Section: API Gateway Pattern)

---

## PHASE 2: Student Service Implementation

### Task 2.1: Student Service - Project Structure Setup
**Priority**: P0 (Critical)
**Estimated Time**: 3 hours

**Subtasks**:
- [ ] Create Maven project for student-service
- [ ] Add all required dependencies (Spring Web, JPA, PostgreSQL, Eureka Client, Validation, Actuator, Flyway)
- [ ] Create package structure following Clean Architecture
  - `presentation` (controllers, DTOs, exception handlers)
  - `application` (services, mappers, use cases)
  - `domain` (entities, value objects, repositories)
  - `infrastructure` (persistence, config, utilities)
- [ ] Configure application.yml with database settings
- [ ] Configure application-docker.yml for containerized environment
- [ ] Setup Eureka client configuration
- [ ] Configure Actuator endpoints
- [ ] Add Lombok and MapStruct annotation processors

**Acceptance Criteria**:
- Project builds without errors
- All dependencies resolved
- Package structure matches Clean Architecture
- Configuration files properly set up
- Application starts without errors

**Dependencies**: Task 1.2

**Reference Files**:
- Location: `student-service/`
- Architecture Doc: `01-SYSTEM-ARCHITECTURE.md`, `08-IMPLEMENTATION-GUIDE.md`

---

### Task 2.2: Student Service - Database Schema Setup
**Priority**: P0 (Critical)
**Estimated Time**: 3 hours

**Subtasks**:
- [ ] Create Flyway migration directory (db/migration)
- [ ] Create V1__create_students_table.sql with complete schema
- [ ] Create V2__create_indexes_students.sql with all indexes
- [ ] Create V3__create_student_views.sql with views
- [ ] Create V4__insert_sample_students.sql with test data
- [ ] Configure Flyway in application.yml
- [ ] Setup local PostgreSQL database (student_db)
- [ ] Test migrations locally
- [ ] Verify all constraints are working
- [ ] Test student ID generation function

**Acceptance Criteria**:
- All migrations execute successfully
- Students table created with all constraints
- All indexes created successfully
- Sample data inserted correctly
- Student ID generation function works (STU-YYYY-NNNNN format)
- Views created and queryable

**Dependencies**: Task 2.1

**Reference Files**:
- SQL Script: `specs/planning/school_management.sql`
- Architecture Doc: `03-DATABASE-ARCHITECTURE.md`

---

### Task 2.3: Student Service - Domain Layer Implementation
**Priority**: P0 (Critical)
**Estimated Time**: 6 hours

**Subtasks**:
- [ ] Create Student entity with all fields
- [ ] Create Address value object (embeddable)
- [ ] Create StudentStatus enum (ACTIVE, INACTIVE)
- [ ] Add JPA annotations to Student entity
- [ ] Implement audit fields (createdAt, updatedAt, createdBy, updatedBy)
- [ ] Add optimistic locking with @Version
- [ ] Create domain exceptions (InvalidAgeException, DuplicateMobileException, StudentNotFoundException)
- [ ] Create StudentRepository interface (domain layer)
- [ ] Add business methods to Student entity (calculateAge, isActive, activate, deactivate)
- [ ] Add @PrePersist and @PreUpdate callbacks
- [ ] Write unit tests for domain logic

**Acceptance Criteria**:
- Student entity maps correctly to database table
- All validations working at entity level
- Address embeddable works correctly
- Audit fields auto-populated
- Optimistic locking prevents concurrent updates
- Domain exceptions thrown correctly
- Unit tests pass with 80%+ coverage

**Dependencies**: Task 2.2

**Reference Files**:
- Location: `student-service/src/main/java/com/school/sms/student/domain/`
- Architecture Doc: `08-IMPLEMENTATION-GUIDE.md` (Section: Domain Layer)

---

### Task 2.4: Student Service - Infrastructure Layer (Persistence)
**Priority**: P0 (Critical)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Create JpaStudentRepository interface extending JpaRepository
- [ ] Add custom query methods (existsByMobile, findByStatus, etc.)
- [ ] Create StudentRepositoryAdapter implementing domain StudentRepository
- [ ] Implement StudentIdGenerator utility class
- [ ] Configure HikariCP connection pool settings
- [ ] Add database configuration class
- [ ] Create integration tests with @DataJpaTest
- [ ] Test all repository methods
- [ ] Verify transaction management

**Acceptance Criteria**:
- JPA repository properly configured
- All custom queries working
- Repository adapter delegates correctly to JPA repository
- Student ID generator creates correct format (STU-YYYY-NNNNN)
- Connection pooling configured
- Integration tests pass
- Transactions rollback on errors

**Dependencies**: Task 2.3

**Reference Files**:
- Location: `student-service/src/main/java/com/school/sms/student/infrastructure/persistence/`
- Architecture Doc: `08-IMPLEMENTATION-GUIDE.md` (Section: Infrastructure Layer)

---

### Task 2.5: Student Service - Application Layer (Services & DTOs)
**Priority**: P0 (Critical)
**Estimated Time**: 8 hours

**Subtasks**:
- [ ] Create CreateStudentRequest DTO with validation annotations
- [ ] Create UpdateStudentRequest DTO with validation annotations
- [ ] Create StudentResponse DTO
- [ ] Create StudentDTO for internal use
- [ ] Create AddressDTO
- [ ] Create StudentMapper interface with MapStruct
- [ ] Implement StudentRegistrationService
  - Register student with age validation (3-18 years)
  - Check mobile uniqueness
  - Generate student ID
  - Set default status to ACTIVE
- [ ] Implement StudentManagementService
  - Update student profile
  - Handle optimistic locking conflicts
- [ ] Implement StudentSearchService
  - Find by ID
  - Find all with pagination
  - Filter by status, name, mobile, age range
- [ ] Implement StudentStatusService (activate/deactivate)
- [ ] Add logging with SLF4J
- [ ] Write unit tests with Mockito

**Acceptance Criteria**:
- All DTOs properly validated
- MapStruct generates mappers correctly
- Age validation working (3-18 years)
- Mobile uniqueness check working
- Student ID auto-generated
- Update service handles concurrent updates
- Search service supports pagination and filtering
- All services have @Transactional annotation
- Logging added at appropriate levels
- Unit tests pass with 80%+ coverage

**Dependencies**: Task 2.4

**Reference Files**:
- Location: `student-service/src/main/java/com/school/sms/student/application/`
- Architecture Doc: `08-IMPLEMENTATION-GUIDE.md` (Section: Application Layer)
- API Spec: `04-API-SPECIFICATIONS.md`

---

### Task 2.6: Student Service - Presentation Layer (REST Controllers)
**Priority**: P0 (Critical)
**Estimated Time**: 6 hours

**Subtasks**:
- [ ] Create StudentController with all CRUD endpoints
  - POST /students (create student)
  - GET /students/{id} (get by ID)
  - PUT /students/{id} (update student)
  - DELETE /students/{id} (soft delete)
  - GET /students (search with filters and pagination)
  - PATCH /students/{id}/status (update status)
- [ ] Create StudentStatisticsController
  - GET /students/statistics (aggregated stats)
- [ ] Add @Valid annotation for request validation
- [ ] Extract X-User-ID header for audit fields
- [ ] Create GlobalExceptionHandler with @ControllerAdvice
- [ ] Implement RFC 7807 ProblemDetail responses
- [ ] Add OpenAPI/Swagger annotations
- [ ] Configure SpringDoc OpenAPI
- [ ] Write integration tests with @WebMvcTest
- [ ] Test all endpoints with MockMvc

**Acceptance Criteria**:
- All endpoints respond with correct status codes
- Request validation working (400 for invalid input)
- Age validation returns 400 with clear message
- Duplicate mobile returns 409 conflict
- Not found returns 404 with problem details
- Concurrent update returns 409 with problem details
- Pagination working correctly
- Status update endpoint working
- OpenAPI documentation accessible at /swagger-ui.html
- Integration tests pass
- All responses follow RFC 7807 format for errors

**Dependencies**: Task 2.5

**Reference Files**:
- Location: `student-service/src/main/java/com/school/sms/student/presentation/`
- API Spec: `04-API-SPECIFICATIONS.md` (Student Service API)

---

### Task 2.7: Student Service - Testing & Documentation
**Priority**: P1 (High)
**Estimated Time**: 6 hours

**Subtasks**:
- [ ] Write unit tests for all service classes
- [ ] Write integration tests for repository layer
- [ ] Write integration tests for REST controllers
- [ ] Setup TestContainers for database integration tests
- [ ] Add end-to-end tests with real database
- [ ] Achieve 80%+ code coverage
- [ ] Generate JaCoCo coverage report
- [ ] Create README.md for student-service
- [ ] Document API endpoints with examples
- [ ] Add code comments and JavaDoc
- [ ] Create developer setup guide

**Acceptance Criteria**:
- All unit tests passing
- All integration tests passing
- Code coverage 80%+
- TestContainers working for database tests
- README.md complete with setup instructions
- API documentation clear and comprehensive
- No critical SonarQube issues

**Dependencies**: Task 2.6

**Reference Files**:
- Testing Strategy: `09-TESTING-STRATEGY.md`

---

### Task 2.8: Student Service - Dockerization
**Priority**: P1 (High)
**Estimated Time**: 3 hours

**Subtasks**:
- [ ] Create optimized multi-stage Dockerfile
- [ ] Configure Docker build with Maven
- [ ] Create docker-compose.yml for local development
  - PostgreSQL container
  - Student service container
  - Eureka server container
- [ ] Setup environment variables for Docker
- [ ] Test Docker build
- [ ] Test docker-compose startup
- [ ] Verify service registration with Eureka
- [ ] Test API endpoints in Docker environment

**Acceptance Criteria**:
- Docker image builds successfully
- Image size optimized (<200MB)
- docker-compose starts all services
- Service registers with Eureka from Docker
- Database migrations run automatically
- API endpoints accessible
- Environment variables properly injected

**Dependencies**: Task 2.7

**Reference Files**:
- Deployment Guide: `07-DEPLOYMENT-ARCHITECTURE.md`

---

## PHASE 3: Configuration Service Implementation

### Task 3.1: Configuration Service - Project Structure Setup
**Priority**: P0 (Critical)
**Estimated Time**: 2 hours

**Subtasks**:
- [ ] Create Maven project for configuration-service
- [ ] Add all required dependencies
- [ ] Create package structure following Clean Architecture
- [ ] Configure application.yml
- [ ] Setup Eureka client configuration
- [ ] Configure Actuator endpoints

**Acceptance Criteria**:
- Project builds without errors
- Configuration service starts successfully
- Registers with Eureka server
- Actuator endpoints accessible

**Dependencies**: Task 1.2

---

### Task 3.2: Configuration Service - Database Schema Setup
**Priority**: P0 (Critical)
**Estimated Time**: 2 hours

**Subtasks**:
- [ ] Create Flyway migration directory
- [ ] Create V1__create_school_profile_table.sql
- [ ] Create V2__create_configuration_settings_table.sql
- [ ] Create V3__create_configuration_views.sql
- [ ] Create V4__insert_default_configuration.sql
- [ ] Configure Flyway in application.yml
- [ ] Setup local PostgreSQL database (configuration_db)
- [ ] Test migrations locally

**Acceptance Criteria**:
- All migrations execute successfully
- school_profile table created with single record
- configuration_settings table created
- Default settings inserted
- Views created successfully

**Dependencies**: Task 3.1

**Reference Files**:
- SQL Script: `specs/planning/school_management.sql`
- Architecture Doc: `03-DATABASE-ARCHITECTURE.md`

---

### Task 3.3: Configuration Service - Domain Layer Implementation
**Priority**: P0 (Critical)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Create SchoolProfile entity
- [ ] Create ConfigurationSetting entity
- [ ] Create SettingCategory enum (GENERAL, ACADEMIC, FINANCIAL)
- [ ] Add JPA annotations
- [ ] Implement audit fields
- [ ] Add optimistic locking
- [ ] Create domain exceptions (DuplicateSettingException, SettingNotFoundException)
- [ ] Create repository interfaces
- [ ] Write unit tests

**Acceptance Criteria**:
- Entities map correctly to database
- All validations working
- Domain exceptions defined
- Unit tests pass

**Dependencies**: Task 3.2

---

### Task 3.4: Configuration Service - Infrastructure Layer
**Priority**: P0 (Critical)
**Estimated Time**: 3 hours

**Subtasks**:
- [ ] Create JPA repositories
- [ ] Add custom query methods
- [ ] Create repository adapters
- [ ] Configure database settings
- [ ] Create integration tests
- [ ] Test all repository methods

**Acceptance Criteria**:
- JPA repositories working
- Custom queries functional
- Integration tests pass
- Transactions working correctly

**Dependencies**: Task 3.3

---

### Task 3.5: Configuration Service - Application Layer
**Priority**: P0 (Critical)
**Estimated Time**: 6 hours

**Subtasks**:
- [ ] Create request/response DTOs
- [ ] Create CreateSettingRequest DTO with validation
- [ ] Create UpdateSettingRequest DTO
- [ ] Create SettingResponse DTO
- [ ] Create SchoolProfileResponse DTO
- [ ] Create ConfigurationMapper with MapStruct
- [ ] Implement ConfigurationManagementService
  - CRUD operations for settings
  - Category-based retrieval
  - Validate category and key format
- [ ] Implement SchoolProfileService
  - Get school profile
  - Update school profile
- [ ] Add logging
- [ ] Write unit tests

**Acceptance Criteria**:
- All DTOs validated correctly
- MapStruct mappers generated
- CRUD operations working
- Category validation enforced (GENERAL, ACADEMIC, FINANCIAL)
- Key format validation (UPPERCASE_SNAKE_CASE)
- Unit tests pass with 80%+ coverage

**Dependencies**: Task 3.4

**Reference Files**:
- API Spec: `04-API-SPECIFICATIONS.md` (Configuration Service API)

---

### Task 3.6: Configuration Service - Presentation Layer
**Priority**: P0 (Critical)
**Estimated Time**: 5 hours

**Subtasks**:
- [ ] Create ConfigurationController
  - POST /configurations/settings (create setting)
  - GET /configurations/settings/{id} (get by ID)
  - PUT /configurations/settings/{id} (update setting)
  - DELETE /configurations/settings/{id} (delete setting)
  - GET /configurations/settings/category/{category} (get by category)
  - GET /configurations/settings (get all grouped by category)
- [ ] Create SchoolProfileController
  - GET /configurations/school-profile
  - PUT /configurations/school-profile
- [ ] Add validation annotations
- [ ] Create GlobalExceptionHandler
- [ ] Implement RFC 7807 error responses
- [ ] Add OpenAPI annotations
- [ ] Write integration tests

**Acceptance Criteria**:
- All endpoints working correctly
- Validation errors return 400
- Duplicate setting returns 409
- Category validation working
- OpenAPI documentation accessible
- Integration tests pass

**Dependencies**: Task 3.5

**Reference Files**:
- API Spec: `04-API-SPECIFICATIONS.md` (Configuration Service API)

---

### Task 3.7: Configuration Service - Testing & Documentation
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Write unit tests for all services
- [ ] Write integration tests
- [ ] Setup TestContainers
- [ ] Achieve 80%+ code coverage
- [ ] Generate coverage report
- [ ] Create README.md
- [ ] Document API endpoints
- [ ] Add JavaDoc

**Acceptance Criteria**:
- All tests passing
- Code coverage 80%+
- Documentation complete
- No critical issues

**Dependencies**: Task 3.6

---

### Task 3.8: Configuration Service - Dockerization
**Priority**: P1 (High)
**Estimated Time**: 2 hours

**Subtasks**:
- [ ] Create Dockerfile
- [ ] Update docker-compose.yml
- [ ] Configure environment variables
- [ ] Test Docker build
- [ ] Test docker-compose startup
- [ ] Verify endpoints

**Acceptance Criteria**:
- Docker image builds
- docker-compose works
- Service registers with Eureka
- API endpoints accessible

**Dependencies**: Task 3.7

---

## PHASE 4: Integration & Deployment

### Task 4.1: Service Integration Testing
**Priority**: P0 (Critical)
**Estimated Time**: 6 hours

**Subtasks**:
- [ ] Create integration test suite
- [ ] Test Student Service via API Gateway
- [ ] Test Configuration Service via API Gateway
- [ ] Test service discovery
- [ ] Test error handling across services
- [ ] Test concurrent requests
- [ ] Performance test with load
- [ ] Test database transactions

**Acceptance Criteria**:
- All services communicate correctly
- API Gateway routes working
- Service discovery functional
- Error responses consistent
- Performance meets targets (<500ms response time)
- No data inconsistencies

**Dependencies**: Task 2.8, Task 3.8

---

### Task 4.2: Monitoring & Observability Setup
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Configure Prometheus metrics export
- [ ] Setup Prometheus server
- [ ] Create Grafana dashboards
- [ ] Configure application logging
- [ ] Setup centralized logging (optional ELK)
- [ ] Add distributed tracing headers
- [ ] Create health check endpoints
- [ ] Setup alerts for critical metrics

**Acceptance Criteria**:
- Prometheus collecting metrics
- Grafana dashboards showing service metrics
- Logs structured and readable
- Health checks responding correctly
- Alerts configured

**Dependencies**: Task 4.1

**Reference Files**:
- Deployment Guide: `07-DEPLOYMENT-ARCHITECTURE.md`

---

### Task 4.3: Security Hardening
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Add input validation at all layers
- [ ] Implement SQL injection prevention (verify parameterized queries)
- [ ] Add XSS prevention headers
- [ ] Configure CORS properly
- [ ] Add rate limiting at API Gateway
- [ ] Sanitize error messages (no stack traces in production)
- [ ] Add security headers (CSP, X-Frame-Options, etc.)
- [ ] Perform security scan with OWASP dependency check
- [ ] Review and fix security vulnerabilities

**Acceptance Criteria**:
- All inputs validated
- No SQL injection vulnerabilities
- XSS prevention headers configured
- CORS working correctly
- Rate limiting functional
- Security scan shows no critical issues
- Error messages sanitized

**Dependencies**: Task 4.1

**Reference Files**:
- Security Guide: `06-SECURITY-ARCHITECTURE.md`

---
### Task 4.4: Run the backend server in local and test
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Start the backend server
- [ ] Test API endpoints


---

## PHASE 5: Documentation & Handover

### Task 5.1: API Documentation
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Verify OpenAPI specs are complete
- [ ] Add comprehensive examples to API docs
- [ ] Create Postman collection for all endpoints
- [ ] Add authentication setup guide (for future)
- [ ] Document error codes and meanings
- [ ] Create API versioning guide
- [ ] Add rate limiting documentation

**Acceptance Criteria**:
- OpenAPI specs 100% complete
- Postman collection works for all endpoints
- Examples provided for each endpoint
- Error documentation complete

**Dependencies**: Task 4.5

---

### Task 5.2: Developer Documentation
**Priority**: P1 (High)
**Estimated Time**: 4 hours

**Subtasks**:
- [ ] Create comprehensive README for project root
- [ ] Document local development setup
- [ ] Create architecture decision records (ADRs)
- [ ] Document database migration process
- [ ] Create troubleshooting guide
- [ ] Document environment variables
- [ ] Add code contribution guidelines
- [ ] Create developer FAQ

**Acceptance Criteria**:
- README guides developer from zero to running app
- ADRs document key decisions
- Migration process clearly explained
- Troubleshooting guide covers common issues
- Contribution guidelines clear

**Dependencies**: Task 5.1

---

**Dependencies**: Task 5.2

---

## Task Summary

### By Priority
- **P0 (Critical)**: 18 tasks - Must complete for MVP
- **P1 (High)**: 10 tasks - Important for production readiness

### By Phase
- **Phase 1 (Foundation)**: 3 tasks, ~11 hours
- **Phase 2 (Student Service)**: 8 tasks, ~43 hours
- **Phase 3 (Configuration Service)**: 8 tasks, ~28 hours
- **Phase 4 (Integration)**: 5 tasks, ~24 hours
- **Phase 5 (Documentation)**: 3 tasks, ~11 hours

### Total Estimated Time: 117 hours (~15 working days for 1 developer)

---

## Success Metrics

### Code Quality
- [ ] 80%+ code coverage
- [ ] Zero critical SonarQube issues
- [ ] All unit tests passing
- [ ] All integration tests passing


### Documentation
- [ ] README complete and accurate
- [ ] API documentation 100% complete
- [ ] Deployment runbook created
- [ ] Developer setup guide working

---

## Risk Mitigation

### Technical Risks
- **Database migration failures**: Test all migrations thoroughly in staging
- **Service discovery issues**: Have fallback direct service URLs
- **Performance bottlenecks**: Load test early and often
- **Concurrent update conflicts**: Implement optimistic locking correctly

---

## Notes for Backend Developers

1. **Follow Clean Architecture**: Keep domain logic independent of frameworks
2. **Test-Driven Development**: Write tests before implementation where possible
3. **Database First**: Ensure migrations work before coding services
4. **API Contract**: Follow API specifications exactly
5. **Error Handling**: Use RFC 7807 format consistently
6. **Logging**: Log at appropriate levels (INFO for business events, DEBUG for technical details)
7. **Security**: Validate all inputs, use parameterized queries
8. **Documentation**: Update docs as you code, not after
9. **Code Review**: All code must be reviewed before merge
10. **Commit Messages**: Use conventional commits (feat:, fix:, docs:, etc.)

---

## Getting Started

1. Start with Task 1.1 (Environment Setup)
2. Complete all Phase 1 tasks before moving to Phase 2
3. Student Service (Phase 2) and Configuration Service (Phase 3) can be developed in parallel by different developers
4. Integration testing (Phase 4) requires both services complete
5. Use the provided SQL script for database setup
6. Refer to architecture documents for detailed specifications
