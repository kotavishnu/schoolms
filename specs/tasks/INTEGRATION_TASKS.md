# Integration & Deployment Tasks - School Management System

## Overview
This document provides a comprehensive task list for integrating the Backend and Frontend, setting up the complete local development environment, and preparing for production deployment.

**Target:** Complete end-to-end system integration, testing, and deployment infrastructure.

---

## Phase 1: Local Development Environment Setup

### [INT-001] Create Master Docker Compose Configuration
**Goal:** Set up complete local development environment with all services.

**Technical Details:**
- Create `infrastructure/docker/docker-compose.yml`:
  - Services to include:
    - **postgres**: PostgreSQL 18
      - Create two databases: student_db, config_db
      - Volume for data persistence
      - Port: 5432
      - Health check
    - **redis**: Redis 7
      - Volume for persistence
      - Port: 6379
      - Health check
    - **student-service**: Backend Student Service
      - Build from backend/student-service
      - Port: 8081
      - Environment variables for DB and Redis
      - Depends on postgres, redis
      - Health check: /actuator/health
    - **configuration-service**: Backend Configuration Service
      - Build from backend/configuration-service
      - Port: 8082
      - Environment variables
      - Depends on postgres, redis
      - Health check
    - **api-gateway**: Backend API Gateway
      - Build from backend/api-gateway
      - Port: 8080
      - Depends on student-service, configuration-service
      - Health check
    - **frontend**: React/Next.js Frontend
      - Build from frontend/sms-web
      - Port: 3000
      - Environment variable: NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
      - Depends on api-gateway
    - **zipkin**: Distributed Tracing
      - Image: openzipkin/zipkin:latest
      - Port: 9411
    - **prometheus**: Metrics Collection
      - Image: prom/prometheus:latest
      - Port: 9090
      - Volume for config: ./prometheus.yml
    - **grafana**: Metrics Visualization
      - Image: grafana/grafana:latest
      - Port: 3001
      - Volume for dashboards
      - Depends on prometheus
  - Define networks: sms-network
  - Define volumes: postgres-data, redis-data, grafana-data

**Acceptance Criteria:**
- `docker-compose up` starts all services
- All services are healthy
- Services can communicate with each other
- Databases are initialized with schemas
- Frontend can access backend APIs
- Observability stack is accessible

**Dependencies:** BE-049, FE-051

---

### [INT-002] Create Database Initialization Scripts
**Goal:** Automate database setup for local development.

**Technical Details:**
- Create `infrastructure/docker/postgres/init/01-init-databases.sql`:
  - Create student_db database
  - Create config_db database
  - Create application user: sms_app_user
  - Grant privileges on both databases
- Create `infrastructure/docker/postgres/init/02-seed-data.sql`:
  - Insert sample school profile
  - Insert sample configuration settings (all categories)
  - Insert sample students (5-10 records)
  - Insert sample enrollment history
- Update docker-compose.yml:
  - Mount init scripts to postgres container
  - Scripts run automatically on first startup

**Acceptance Criteria:**
- Databases are created automatically
- Application user has correct privileges
- Sample data is inserted
- Flyway migrations run successfully after init

**Dependencies:** BE-014, BE-030, INT-001

---

### [INT-003] Configure Prometheus for Metrics Collection
**Goal:** Set up Prometheus to scrape metrics from backend services.

**Technical Details:**
- Create `infrastructure/docker/prometheus/prometheus.yml`:
  - Scrape configs:
    - Job: student-service
      - Target: student-service:8081
      - Metrics path: /actuator/prometheus
      - Interval: 15s
    - Job: configuration-service
      - Target: configuration-service:8082
      - Metrics path: /actuator/prometheus
      - Interval: 15s
    - Job: api-gateway
      - Target: api-gateway:8080
      - Metrics path: /actuator/prometheus
      - Interval: 15s
  - Global config:
    - Scrape interval: 15s
    - Evaluation interval: 15s
- Mount prometheus.yml to Prometheus container in docker-compose.yml

**Acceptance Criteria:**
- Prometheus scrapes metrics from all services
- Metrics are visible in Prometheus UI (http://localhost:9090)
- Queries work correctly (e.g., jvm_memory_used_bytes)

**Dependencies:** BE-005, INT-001

---

### [INT-004] Configure Grafana Dashboards
**Goal:** Create Grafana dashboards for monitoring system health.

**Technical Details:**
- Create `infrastructure/docker/grafana/dashboards/`:
  - `jvm-metrics.json`:
    - JVM heap memory usage
    - GC time
    - Thread count
    - CPU usage
  - `http-metrics.json`:
    - Request rate (RPS)
    - Response time (p50, p95, p99)
    - Error rate
    - HTTP status code distribution
  - `database-metrics.json`:
    - Connection pool utilization
    - Query execution time
    - Active connections
  - `business-metrics.json`:
    - Total students registered
    - Active students count
    - Student registrations per day
    - Search query count
- Create `infrastructure/docker/grafana/provisioning/`:
  - `datasources.yml`:
    - Configure Prometheus as datasource
  - `dashboards.yml`:
    - Auto-load dashboard JSON files
- Mount provisioning directory to Grafana container

**Acceptance Criteria:**
- Grafana loads with Prometheus datasource
- Dashboards are auto-loaded
- All metrics are displayed correctly
- Dashboards are accessible at http://localhost:3001

**Dependencies:** INT-003

---

### [INT-005] Create Development Setup Script
**Goal:** Create script to automate local development setup.

**Technical Details:**
- Create `infrastructure/docker/setup-dev.sh`:
  ```bash
  #!/bin/bash
  echo "Setting up SMS development environment..."

  # Check prerequisites
  command -v docker >/dev/null 2>&1 || { echo "Docker is not installed"; exit 1; }
  command -v docker-compose >/dev/null 2>&1 || { echo "Docker Compose is not installed"; exit 1; }

  # Stop any running containers
  echo "Stopping existing containers..."
  docker-compose down

  # Remove volumes (clean slate)
  echo "Removing old volumes..."
  docker volume prune -f

  # Build backend services
  echo "Building backend services..."
  cd ../../backend
  mvn clean package -DskipTests
  cd ../infrastructure/docker

  # Build frontend
  echo "Building frontend..."
  cd ../../frontend/sms-web
  pnpm install
  pnpm build
  cd ../../infrastructure/docker

  # Build Docker images
  echo "Building Docker images..."
  docker-compose build

  # Start services
  echo "Starting services..."
  docker-compose up -d

  # Wait for services to be healthy
  echo "Waiting for services to be healthy..."
  sleep 30

  # Check health
  echo "Checking service health..."
  docker-compose ps

  echo "Setup complete!"
  echo "Services:"
  echo "  - Frontend: http://localhost:3000"
  echo "  - API Gateway: http://localhost:8080"
  echo "  - Student Service: http://localhost:8081"
  echo "  - Configuration Service: http://localhost:8082"
  echo "  - Zipkin: http://localhost:9411"
  echo "  - Prometheus: http://localhost:9090"
  echo "  - Grafana: http://localhost:3001 (admin/admin)"
  ```
- Make script executable: `chmod +x setup-dev.sh`
- Create corresponding Windows script: `setup-dev.bat`

**Acceptance Criteria:**
- Script runs without errors
- All services start successfully
- Health checks pass
- URLs are accessible

**Dependencies:** INT-001, INT-002, INT-003, INT-004

---

## Phase 2: API Integration Testing

### [INT-006] Create Postman/Insomnia Collection
**Goal:** Create API collection for manual testing and documentation.

**Technical Details:**
- Create `specs/api/SMS_Postman_Collection.json`:
  - Folder structure:
    - Student Management
      - Create Student (POST)
      - Get Student (GET)
      - Search Students (GET with query params)
      - Update Student (PUT)
      - Delete Student (DELETE)
      - Get Enrollment History (GET)
    - Configuration Management
      - Get School Profile (GET)
      - Update School Profile (PUT)
      - Get All Configurations (GET)
      - Get Configurations by Category (GET)
      - Get Specific Configuration (GET)
      - Create Configuration (POST)
      - Update Configuration (PUT)
      - Delete Configuration (DELETE)
  - Environment variables:
    - `base_url`: http://localhost:8080/api/v1
    - `student_key`: (dynamic, from create response)
    - `setting_key`: (dynamic)
  - Pre-request scripts:
    - Generate correlation ID
  - Tests (assertions):
    - Status code checks
    - Response schema validation
    - Response time < 500ms
  - Examples for each request

**Acceptance Criteria:**
- Collection covers all 13 API endpoints
- Environment variables work correctly
- Pre-request scripts generate correlation IDs
- Tests (assertions) pass for all requests
- Examples show expected responses

**Dependencies:** BE-048

---

### [INT-007] Test Backend API Endpoints Manually
**Goal:** Verify all backend endpoints work correctly using Postman.

**Technical Details:**
- Import Postman collection
- Test each endpoint in sequence:
  1. Create school profile
  2. Create configuration settings (all categories)
  3. Get configurations by category
  4. Create 5 students with different scenarios:
     - Valid student (age 10)
     - Student at age boundary (age 3, age 18)
     - Try invalid age (expect 422)
     - Try duplicate mobile (expect 409)
     - Student with all optional fields
  5. Search students:
     - By last name
     - By guardian name
     - By status
     - With pagination
  6. Get student by key
  7. Update student (change mobile, status)
  8. Get enrollment history
  9. Delete student
  10. Update configuration setting
  11. Delete configuration setting
- Document any issues found
- Verify error responses match RFC 7807 format

**Acceptance Criteria:**
- All endpoints return expected responses
- Status codes are correct
- Error responses are formatted correctly
- Correlation IDs are present in responses
- Pagination works correctly
- Search filters work correctly
- Validation works (age, mobile, etc.)

**Dependencies:** INT-001, INT-006

---

### [INT-008] Create Integration Test Suite for Backend-to-Backend Communication
**Goal:** Test communication between API Gateway and microservices.

**Technical Details:**
- Create `backend/integration-tests/src/test/java/com/sms/integration/`:
  - `GatewayToStudentServiceTest.java`:
    - Test: Request to /api/v1/students routes to Student Service
    - Test: Correlation ID is propagated
    - Test: 404 from Student Service returns correctly
  - `GatewayToConfigServiceTest.java`:
    - Test: Request to /api/v1/school routes to Config Service
    - Test: Request to /api/v1/configurations routes to Config Service
  - `ServiceDiscoveryTest.java`:
    - Test: Gateway can discover services
    - Test: Gateway handles service unavailability (503)
  - `RateLimitIntegrationTest.java`:
    - Test: Rate limit is enforced
    - Test: 429 response when limit exceeded
  - `CorsIntegrationTest.java`:
    - Test: CORS headers are present
    - Test: Preflight requests work
- Use TestContainers to spin up all services
- Use REST Assured for HTTP calls

**Acceptance Criteria:**
- All integration tests pass
- Gateway routes requests correctly
- Correlation IDs propagate
- Rate limiting works
- CORS headers are correct

**Dependencies:** BE-047, INT-001

---

### [INT-009] Test Database Transactions and Consistency
**Goal:** Verify database operations maintain ACID properties.

**Technical Details:**
- Create `backend/student-service/src/test/java/com/sms/student/integration/`:
  - `TransactionTest.java`:
    - Test: Rollback on validation error (no student or enrollment history created)
    - Test: Optimistic locking prevents concurrent updates
    - Test: Cascade delete works (delete student deletes enrollment history)
  - `ConcurrencyTest.java`:
    - Test: Multiple threads updating same student (one succeeds, others fail with optimistic lock)
    - Test: Multiple threads creating students with different data (all succeed)
  - `ConstraintTest.java`:
    - Test: Unique constraint on mobile (duplicate throws exception)
    - Test: Unique constraint on email
    - Test: Check constraint on status (invalid status throws exception)
- Use TestContainers with PostgreSQL
- Use ExecutorService for concurrent tests

**Acceptance Criteria:**
- Transactions rollback correctly on error
- Optimistic locking prevents lost updates
- Cascade operations work
- Constraints are enforced
- Concurrent operations are handled safely

**Dependencies:** BE-044, INT-001

---

## Phase 3: Frontend-Backend Integration

### [INT-010] Test Frontend-Backend API Integration
**Goal:** Verify frontend can communicate with backend APIs correctly.

**Technical Details:**
- Start all services using docker-compose
- Test frontend integration:
  1. **Student Registration Flow**:
     - Navigate to http://localhost:3000/students/new
     - Fill form with valid data
     - Submit form
     - Verify: Student is created (check Network tab, check backend logs)
     - Verify: Redirect to detail page
     - Verify: Student appears in list
  2. **Student Search Flow**:
     - Navigate to http://localhost:3000/students
     - Search by last name
     - Verify: Results are filtered
     - Verify: Pagination works
  3. **Student Update Flow**:
     - Navigate to student detail page
     - Click Edit button
     - Update mobile number
     - Submit form
     - Verify: Student is updated
     - Verify: Updated data appears on detail page
  4. **Validation Errors**:
     - Try to create student with age 2 (invalid)
     - Verify: Error message is displayed
     - Verify: Field-level error is shown
  5. **Duplicate Mobile Error**:
     - Try to create student with duplicate mobile
     - Verify: 409 error is handled
     - Verify: User-friendly error message is shown
  6. **Configuration Management**:
     - Navigate to http://localhost:3000/configurations
     - Create new setting
     - Update setting
     - Delete setting
     - Verify: All operations work
  7. **Error Handling**:
     - Stop backend services
     - Try to fetch students
     - Verify: Error message is displayed
     - Verify: Retry button works when backend is back
- Use browser DevTools to verify:
  - Network requests are sent to correct endpoints
  - Correlation IDs are included
  - Error responses are handled correctly

**Acceptance Criteria:**
- All frontend pages work with backend
- API requests are sent correctly
- Responses are handled correctly
- Error states are displayed properly
- Success messages are shown
- Loading states work
- Correlation IDs are in all requests

**Dependencies:** INT-001, FE-024, FE-023, FE-026, FE-036

---

### [INT-011] Test CORS Configuration
**Goal:** Verify CORS configuration allows frontend to access backend.

**Technical Details:**
- Start backend services
- Run frontend from different origin scenarios:
  1. Same origin: http://localhost:3000 (should work)
  2. Different port: http://localhost:5173 (should work if configured)
  3. Different domain: http://example.com (should fail if not configured)
- Test preflight requests:
  - Make PUT/DELETE requests from frontend
  - Verify OPTIONS request is sent first
  - Verify OPTIONS response has correct headers
- Check CORS headers in responses:
  - Access-Control-Allow-Origin
  - Access-Control-Allow-Methods
  - Access-Control-Allow-Headers
  - Access-Control-Allow-Credentials
- Test with credentials:
  - Enable credentials in Axios
  - Verify cookies/auth headers are sent (Phase 2)

**Acceptance Criteria:**
- Frontend can make requests from allowed origins
- Preflight requests work correctly
- CORS headers are present
- Credentials work (if applicable)

**Dependencies:** BE-041, INT-010

---

### [INT-012] Test Error Handling End-to-End
**Goal:** Verify error scenarios are handled gracefully throughout the stack.

**Technical Details:**
- Test error scenarios:
  1. **400 Bad Request - Validation Error**:
     - Frontend: Submit form with invalid data
     - Expected: Field errors are displayed
  2. **404 Not Found**:
     - Frontend: Navigate to /students/STU-9999-9999
     - Expected: "Student not found" error page
  3. **409 Conflict - Duplicate Mobile**:
     - Frontend: Create student with existing mobile
     - Expected: Error message: "Mobile number already exists"
  4. **409 Conflict - Optimistic Lock**:
     - Simulate: Update same student from two browsers simultaneously
     - Expected: Second update shows "Record was modified by another user"
  5. **422 Unprocessable Entity - Age Validation**:
     - Frontend: Create student with age 2
     - Expected: Error message: "Age must be between 3 and 18"
  6. **500 Internal Server Error**:
     - Simulate: Throw exception in backend
     - Expected: Generic error message, correlation ID shown
  7. **503 Service Unavailable**:
     - Stop Student Service
     - Try to create student
     - Expected: "Service temporarily unavailable" error
- Verify:
  - Error messages are user-friendly
  - Correlation IDs are displayed for debugging
  - Stack traces are not exposed to users
  - Retry mechanisms work

**Acceptance Criteria:**
- All error types are handled correctly
- Error messages are user-friendly
- Correlation IDs are included
- No stack traces in frontend

**Dependencies:** BE-024, FE-013, INT-010

---

## Phase 4: End-to-End Testing

### [INT-013] Create E2E Test Environment
**Goal:** Set up isolated environment for E2E tests.

**Technical Details:**
- Create `infrastructure/docker/docker-compose.test.yml`:
  - Based on docker-compose.yml
  - Use separate database (test_student_db, test_config_db)
  - Separate Redis instance
  - Backend services in test mode
  - Frontend in test mode
  - No observability stack (optional)
- Create `tests/e2e/setup/`:
  - `start-test-env.sh`:
    - Start services with test compose file
    - Wait for health checks
    - Run database migrations
    - Seed test data
  - `teardown-test-env.sh`:
    - Stop services
    - Remove volumes
- Update Playwright config:
  - Use test environment URLs
  - Set base URL: http://localhost:3000
  - Configure beforeAll: start test environment
  - Configure afterAll: teardown test environment

**Acceptance Criteria:**
- Test environment starts successfully
- Test databases are isolated
- Services are healthy
- Environment can be torn down cleanly

**Dependencies:** INT-001, FE-043

---

### [INT-014] Write E2E Test - Complete Student Registration Flow
**Goal:** Test complete student registration from start to finish.

**Technical Details:**
- Create `tests/e2e/flows/student-registration-flow.spec.ts`:
  - Test steps:
    1. Navigate to homepage
    2. Click "Register New Student" button
    3. Fill registration form:
       - First Name: "John"
       - Last Name: "Doe"
       - Date of Birth: (calculate date for age 10)
       - Mobile: "+919876543210"
       - Email: "john.doe@example.com"
       - Address: "123 Main St"
       - Father/Guardian: "Richard Doe"
       - Mother Name: "Jane Doe"
       - Identification Mark: "Birthmark on left arm"
       - Adhaar: "123456789012"
    4. Click Submit button
    5. Verify: Success message is displayed
    6. Verify: Redirected to detail page
    7. Verify: Student details are displayed correctly
    8. Navigate to student list
    9. Verify: New student appears in list
    10. Search for student by last name
    11. Verify: Student is found in search results
    12. Click on student in list
    13. Verify: Navigated to correct detail page
  - Assertions:
    - Form validation works
    - Success message is shown
    - Redirect happens
    - Data is displayed correctly
    - Search works
  - Cleanup:
    - Delete created student after test

**Acceptance Criteria:**
- E2E test passes consistently
- All steps execute correctly
- Assertions pass
- Cleanup works

**Dependencies:** INT-013, FE-044

---

### [INT-015] Write E2E Test - Student Update and Status Change
**Goal:** Test student update including status change.

**Technical Details:**
- Create `tests/e2e/flows/student-update-flow.spec.ts`:
  - Pre-condition: Create a student via API
  - Test steps:
    1. Navigate to student detail page
    2. Click Edit button
    3. Update fields:
       - First Name: "Jane"
       - Mobile: "+919876543299"
       - Status: "Inactive"
    4. Click Submit button
    5. Verify: Success message
    6. Verify: Redirected to detail page
    7. Verify: Updated data is displayed
    8. Verify: Status badge shows "Inactive"
    9. Click "View Enrollment History"
    10. Verify: Status change is recorded in history
    11. Verify: History shows:
        - Previous status: "Active"
        - New status: "Inactive"
        - Changed date
  - Cleanup: Delete student

**Acceptance Criteria:**
- E2E test passes
- Update works correctly
- Status change creates enrollment history
- History is displayed correctly

**Dependencies:** INT-013, FE-045

---

### [INT-016] Write E2E Test - Student Search and Pagination
**Goal:** Test search functionality with pagination.

**Technical Details:**
- Create `tests/e2e/flows/student-search-flow.spec.ts`:
  - Pre-condition: Create 25 students via API (different last names, guardians)
  - Test steps:
    1. Navigate to student list page
    2. Verify: First 20 students are displayed
    3. Verify: Pagination shows page 1 of 2
    4. Click "Next" button
    5. Verify: Next 5 students are displayed
    6. Verify: Pagination shows page 2 of 2
    7. Click "Previous" button
    8. Verify: Back to first 20 students
    9. Enter last name in search field: "Smith"
    10. Verify: Only students with last name "Smith" are shown
    11. Clear search
    12. Enter guardian name in search field: "Johnson"
    13. Verify: Only students with guardian "Johnson" are shown
    14. Select status filter: "Active"
    15. Verify: Only active students are shown
    16. Combine filters: Last name "Smith" + Status "Active"
    17. Verify: Results match both filters
  - Cleanup: Delete all created students

**Acceptance Criteria:**
- E2E test passes
- Pagination works correctly
- Search by last name works
- Search by guardian works
- Status filter works
- Combined filters work

**Dependencies:** INT-013, FE-045

---

### [INT-017] Write E2E Test - Configuration Management Flow
**Goal:** Test complete configuration management.

**Technical Details:**
- Create `tests/e2e/flows/configuration-management-flow.spec.ts`:
  - Test steps:
    1. Navigate to school profile page
    2. Verify: School profile is displayed
    3. Click Edit button
    4. Update school name
    5. Update principal name
    6. Click Save
    7. Verify: Success message
    8. Verify: Updated data is displayed
    9. Navigate to configurations page
    10. Click "Add New Setting" button
    11. Fill form:
        - Category: "Academic"
        - Key: "test.setting.key"
        - Value: "Test Value"
        - Description: "Test setting for E2E"
    12. Click Save
    13. Verify: Success message
    14. Verify: New setting appears in list
    15. Filter by category: "Academic"
    16. Verify: New setting is visible
    17. Click Edit on new setting
    18. Update value: "Updated Value"
    19. Click Save
    20. Verify: Value is updated
    21. Click Delete on setting
    22. Confirm deletion
    23. Verify: Setting is removed from list
  - Cleanup: Restore original school profile

**Acceptance Criteria:**
- E2E test passes
- School profile update works
- Configuration CRUD works
- Category filtering works
- Confirmation dialog works

**Dependencies:** INT-013, FE-046

---

### [INT-018] Write E2E Test - Error Scenarios
**Goal:** Test error handling in real user scenarios.

**Technical Details:**
- Create `tests/e2e/flows/error-handling-flow.spec.ts`:
  - Test scenarios:
    1. **Invalid Age**:
       - Fill registration form with age 2
       - Submit form
       - Verify: Error message is displayed
       - Verify: Field has error highlight
    2. **Duplicate Mobile**:
       - Create student with mobile "+911234567890"
       - Try to create another student with same mobile
       - Verify: Error message: "Mobile number already exists"
    3. **Required Field Validation**:
       - Leave first name empty
       - Submit form
       - Verify: "First name is required" error
    4. **Invalid Mobile Format**:
       - Enter mobile: "123"
       - Verify: "Invalid mobile format" error
    5. **Student Not Found**:
       - Navigate to /students/STU-9999-9999
       - Verify: 404 error page is shown
       - Verify: "Student not found" message
       - Click "Back to Students" link
       - Verify: Navigated to student list
  - No cleanup needed (test data is invalid or not created)

**Acceptance Criteria:**
- All error scenarios are tested
- Error messages are correct
- Error handling is user-friendly
- Navigation works after errors

**Dependencies:** INT-013, INT-012

---

## Phase 5: Performance & Load Testing

### [INT-019] Set Up Performance Testing with k6
**Goal:** Set up performance testing infrastructure.

**Technical Details:**
- Install k6: `brew install k6` or download binary
- Create `tests/performance/`:
  - `student-api-load-test.js`:
    - Test: GET /api/v1/students (paginated)
    - Virtual users: 50
    - Duration: 2 minutes
    - Ramp-up: 10 seconds
    - Thresholds:
      - p95 response time < 200ms
      - Error rate < 1%
      - Requests per second > 100
  - `student-registration-load-test.js`:
    - Test: POST /api/v1/students
    - Virtual users: 20
    - Duration: 1 minute
    - Threshold: p95 < 300ms
  - `configuration-api-load-test.js`:
    - Test: GET /api/v1/configurations
    - Virtual users: 30
    - Duration: 1 minute
    - Threshold: p95 < 100ms (should be cached)
  - `mixed-scenario-test.js`:
    - Mix of GET, POST, PUT operations
    - Realistic user behavior
- Create `tests/performance/run-all.sh`:
  - Run all performance tests
  - Generate HTML report

**Acceptance Criteria:**
- k6 is installed and configured
- Performance tests are defined
- Thresholds are set
- Tests can be run easily

**Dependencies:** INT-001

---

### [INT-020] Run Performance Tests and Analyze Results
**Goal:** Execute performance tests and identify bottlenecks.

**Technical Details:**
- Start all services with docker-compose
- Run performance tests:
  ```bash
  k6 run tests/performance/student-api-load-test.js
  k6 run tests/performance/student-registration-load-test.js
  k6 run tests/performance/configuration-api-load-test.js
  k6 run tests/performance/mixed-scenario-test.js
  ```
- Analyze results:
  - Check if thresholds are met
  - Identify slow endpoints
  - Check database query performance (via logs)
  - Check cache hit ratio (via Prometheus)
  - Check JVM metrics (heap, GC)
- Document findings in `tests/performance/RESULTS.md`:
  - Performance metrics per endpoint
  - Bottlenecks identified
  - Recommendations for optimization
- If thresholds not met:
  - Optimize queries (add indexes, use projections)
  - Optimize cache settings (increase TTL, cache more)
  - Optimize connection pool settings
  - Re-run tests

**Acceptance Criteria:**
- Performance tests run successfully
- Thresholds are met:
  - GET /api/v1/students: p95 < 200ms
  - POST /api/v1/students: p95 < 300ms
  - GET /api/v1/configurations: p95 < 100ms
- No errors during load tests
- Results are documented

**Dependencies:** INT-019

---

### [INT-021] Test Database Performance Under Load
**Goal:** Verify database can handle concurrent operations.

**Technical Details:**
- Use k6 to simulate high concurrency:
  - 100 virtual users
  - Mix of read and write operations:
    - 70% reads (GET students, search)
    - 20% writes (POST students)
    - 10% updates (PUT students)
  - Duration: 5 minutes
- Monitor database metrics during test:
  - Connection pool utilization (via Prometheus)
  - Query execution time (via Prometheus)
  - Active connections
  - Slow queries (via PostgreSQL logs)
  - Lock waits
- Monitor backend metrics:
  - JVM heap usage
  - GC time
  - Thread count
- Analyze results:
  - Check if connection pool is exhausted
  - Check for slow queries
  - Check for deadlocks
  - Document findings

**Acceptance Criteria:**
- Database handles 100 concurrent users
- Connection pool utilization < 80%
- No connection timeouts
- No deadlocks
- Query times remain acceptable

**Dependencies:** INT-003, INT-020

---

## Phase 6: Security Testing

### [INT-022] Test Input Validation and Sanitization
**Goal:** Verify all inputs are validated and sanitized.

**Technical Details:**
- Test input validation:
  1. **SQL Injection**:
     - Try: firstName = "'; DROP TABLE student; --"
     - Verify: Parameterized queries prevent injection
     - Verify: No error exposes database structure
  2. **XSS (Cross-Site Scripting)**:
     - Try: firstName = "<script>alert('XSS')</script>"
     - Verify: Frontend escapes HTML (React does automatically)
     - Verify: Backend stores as-is but frontend renders safely
  3. **Path Traversal**:
     - Try: GET /api/v1/students/../../../etc/passwd
     - Verify: 404 or 400 error
  4. **Large Payloads**:
     - Try: Send 10MB JSON body
     - Verify: 413 Payload Too Large error
  5. **Invalid JSON**:
     - Try: Send malformed JSON
     - Verify: 400 Bad Request error
  6. **Field Length Limits**:
     - Try: firstName = (string with 1000 characters)
     - Verify: 400 error with validation message
- Use OWASP ZAP or Burp Suite for automated scanning (optional)
- Document findings in `tests/security/FINDINGS.md`

**Acceptance Criteria:**
- SQL injection is prevented
- XSS is prevented
- Path traversal is prevented
- Payload limits are enforced
- Invalid inputs are rejected
- No security vulnerabilities found

**Dependencies:** INT-001

---

### [INT-023] Test CORS Security
**Goal:** Verify CORS configuration prevents unauthorized access.

**Technical Details:**
- Test CORS from unauthorized origins:
  1. Make request from http://malicious-site.com
  2. Verify: CORS error in browser
  3. Verify: No Access-Control-Allow-Origin header for unauthorized origin
- Test CORS preflight:
  1. Make DELETE request from frontend
  2. Verify: OPTIONS preflight request is sent
  3. Verify: Correct CORS headers in preflight response
- Test credentials:
  1. Make request with credentials from allowed origin
  2. Verify: Works correctly
  3. Make request with credentials from unauthorized origin
  4. Verify: CORS error
- Test wildcard origin:
  1. Verify: Wildcard (*) is NOT used in production config
- Document findings

**Acceptance Criteria:**
- CORS blocks unauthorized origins
- Preflight requests work correctly
- Credentials are handled securely
- No wildcard origin in production

**Dependencies:** BE-041, INT-011

---

### [INT-024] Test Rate Limiting
**Goal:** Verify rate limiting prevents API abuse.

**Technical Details:**
- Test rate limiting:
  1. Make 150 rapid requests to /api/v1/students (limit: 100/second)
  2. Verify: 429 Too Many Requests after 100 requests
  3. Verify: Retry-After header is present
  4. Verify: X-RateLimit-* headers are present:
     - X-RateLimit-Limit: 1000
     - X-RateLimit-Remaining: 0
     - X-RateLimit-Reset: (timestamp)
  5. Wait for rate limit reset
  6. Verify: Requests work again
- Test different endpoints:
  1. Verify: Rate limits apply to all endpoints
- Test burst capacity:
  1. Make 200 requests in 1 second (burst capacity: 200)
  2. Verify: First 200 succeed, rest fail
- Document findings

**Acceptance Criteria:**
- Rate limiting works correctly
- 429 response is returned when limit exceeded
- Rate limit headers are present
- Retry-After header is accurate

**Dependencies:** BE-042, INT-001

---

### [INT-025] Run Security Vulnerability Scan
**Goal:** Scan for known vulnerabilities in dependencies.

**Technical Details:**
- Backend vulnerability scan:
  - Run OWASP Dependency Check:
    ```bash
    cd backend
    mvn org.owasp:dependency-check-maven:check
    ```
  - Review report: `target/dependency-check-report.html`
  - Fix high/critical vulnerabilities:
    - Update dependencies to patched versions
    - Apply workarounds if update not available
- Frontend vulnerability scan:
  - Run npm audit:
    ```bash
    cd frontend/sms-web
    pnpm audit
    ```
  - Review results
  - Fix high/critical vulnerabilities:
    - Run `pnpm audit fix`
    - Manually update if needed
- Docker image scan:
  - Run Trivy:
    ```bash
    trivy image sms/student-service:1.0.0
    trivy image sms/frontend:1.0.0
    ```
  - Review results
  - Fix vulnerabilities:
    - Use Alpine base images
    - Update base images
- Document findings in `tests/security/VULNERABILITY_REPORT.md`

**Acceptance Criteria:**
- No high or critical vulnerabilities in backend
- No high or critical vulnerabilities in frontend
- No high or critical vulnerabilities in Docker images
- Report is documented

**Dependencies:** BE-049, FE-051

---

## Phase 7: Deployment Preparation

### [INT-026] Create Production Docker Compose
**Goal:** Create production-ready Docker Compose configuration.

**Technical Details:**
- Create `infrastructure/docker/docker-compose.prod.yml`:
  - Based on docker-compose.yml
  - Differences:
    - No exposed ports for databases (internal only)
    - Enable SSL/TLS for postgres (optional)
    - Use production images (tagged versions, not latest)
    - Use production environment variables
    - Set restart policy: always
    - Set resource limits (CPU, memory):
      - Student Service: 512MB-1GB, 0.5-1 CPU
      - Config Service: 256MB-512MB, 0.5 CPU
      - API Gateway: 256MB-512MB, 0.5 CPU
      - Frontend: 256MB-512MB, 0.5 CPU
      - PostgreSQL: 1GB-2GB, 1-2 CPU
      - Redis: 256MB-512MB, 0.5 CPU
    - Set logging:
      - Driver: json-file
      - Max size: 10MB
      - Max files: 3
    - Use secrets for passwords (not environment variables)
- Create `.env.prod` for production environment variables
- Document production deployment in `infrastructure/docker/PRODUCTION_DEPLOY.md`

**Acceptance Criteria:**
- Production compose file is created
- Resource limits are set
- Restart policies are configured
- Secrets are used for sensitive data
- Logging is configured
- Documentation is complete

**Dependencies:** INT-001

---

### [INT-027] Create Kubernetes Deployment Manifests
**Goal:** Create Kubernetes manifests for production deployment.

**Technical Details:**
- Create Kubernetes manifests in `infrastructure/kubernetes/`:
  - **Namespaces**:
    - `namespace.yaml`: Create `sms-prod` namespace
  - **ConfigMaps**:
    - `student-service-configmap.yaml`: Non-sensitive config
    - `config-service-configmap.yaml`
    - `api-gateway-configmap.yaml`
  - **Secrets**:
    - `database-secrets.yaml`: DB credentials (base64 encoded)
    - `redis-secrets.yaml`: Redis password
  - **PersistentVolumes**:
    - `postgres-pv.yaml`: 10Gi storage
    - `redis-pv.yaml`: 1Gi storage
  - **PersistentVolumeClaims**:
    - `postgres-pvc.yaml`
    - `redis-pvc.yaml`
  - **StatefulSets**:
    - `postgres-statefulset.yaml`:
      - Replicas: 1
      - Volume mount for data
      - Init container for database setup
    - `redis-statefulset.yaml`
  - **Deployments**:
    - `student-service-deployment.yaml`:
      - Replicas: 2
      - Image: sms/student-service:1.0.0
      - Resource limits
      - Environment variables from ConfigMap
      - Secrets from Secret
      - Health checks (readiness, liveness)
    - `config-service-deployment.yaml`:
      - Replicas: 2
    - `api-gateway-deployment.yaml`:
      - Replicas: 2
    - `frontend-deployment.yaml`:
      - Replicas: 2
  - **Services**:
    - `student-service-service.yaml`: ClusterIP
    - `config-service-service.yaml`: ClusterIP
    - `api-gateway-service.yaml`: LoadBalancer (external access)
    - `frontend-service.yaml`: LoadBalancer
    - `postgres-service.yaml`: ClusterIP
    - `redis-service.yaml`: ClusterIP
  - **Ingress** (optional):
    - `ingress.yaml`: Route traffic to api-gateway and frontend
  - **HPA (Horizontal Pod Autoscaler)**:
    - `student-service-hpa.yaml`:
      - Min replicas: 2
      - Max replicas: 5
      - CPU target: 70%
    - `config-service-hpa.yaml`

**Acceptance Criteria:**
- All Kubernetes manifests are created
- Manifests are valid (pass `kubectl apply --dry-run`)
- Resource limits are set
- Health checks are configured
- HPA is configured for autoscaling

**Dependencies:** BE-049, FE-051

---

### [INT-028] Create Helm Charts (Optional)
**Goal:** Package Kubernetes manifests as Helm charts for easier deployment.

**Technical Details:**
- Create Helm chart structure:
  ```
  infrastructure/helm/sms/
  ├── Chart.yaml
  ├── values.yaml
  ├── values-dev.yaml
  ├── values-prod.yaml
  └── templates/
      ├── namespace.yaml
      ├── configmap.yaml
      ├── secret.yaml
      ├── deployment.yaml
      ├── service.yaml
      ├── ingress.yaml
      └── hpa.yaml
  ```
- Parameterize all values:
  - Image tags
  - Replica counts
  - Resource limits
  - Environment-specific config
- Create `values.yaml` with default values
- Create `values-dev.yaml` for development
- Create `values-prod.yaml` for production
- Test Helm chart:
  ```bash
  helm lint infrastructure/helm/sms
  helm install sms-dev infrastructure/helm/sms -f infrastructure/helm/sms/values-dev.yaml --dry-run
  ```

**Acceptance Criteria:**
- Helm chart is created
- Chart passes lint checks
- Chart can be installed with different value files
- Documentation is provided

**Dependencies:** INT-027

---

### [INT-029] Create CI/CD Pipeline for Deployment
**Goal:** Automate build, test, and deployment with GitHub Actions.

**Technical Details:**
- Create `.github/workflows/deploy.yml`:
  - Trigger: push to main branch, manual workflow dispatch
  - Jobs:
    1. **Build Backend**:
       - Checkout code
       - Setup Java 21
       - Build with Maven
       - Run tests
       - Upload JAR artifacts
    2. **Build Frontend**:
       - Checkout code
       - Setup Node 20
       - Install dependencies
       - Run tests
       - Build production app
       - Upload build artifacts
    3. **Build Docker Images**:
       - Download artifacts
       - Build Docker images
       - Tag with commit SHA and version
       - Push to Docker registry (Docker Hub, ECR, GCR)
    4. **Deploy to Staging**:
       - Use kubectl or Helm
       - Deploy to staging namespace
       - Run smoke tests
       - Wait for deployment to be ready
    5. **Deploy to Production** (manual approval):
       - Require approval from reviewers
       - Deploy to production namespace
       - Run smoke tests
       - Monitor for errors
  - Secrets to configure:
    - DOCKER_USERNAME
    - DOCKER_PASSWORD
    - KUBECONFIG (base64 encoded)
    - SLACK_WEBHOOK (for notifications, optional)
- Create smoke tests:
  - Check health endpoints
  - Check API gateway responds
  - Check frontend loads

**Acceptance Criteria:**
- CI/CD pipeline is created
- Pipeline builds and tests successfully
- Docker images are built and pushed
- Deployment to staging is automated
- Production deployment requires approval
- Smoke tests run after deployment

**Dependencies:** INT-027, BE-051, FE-053

---

## Phase 8: Documentation & Finalization

### [INT-030] Create Deployment Documentation
**Goal:** Document complete deployment process for operations team.

**Technical Details:**
- Create `docs/deployment/`:
  - `LOCAL_DEVELOPMENT.md`:
    - Prerequisites (Docker, Docker Compose)
    - Setup instructions (run setup-dev.sh)
    - How to access services
    - How to view logs
    - Troubleshooting
  - `DOCKER_DEPLOYMENT.md`:
    - Prerequisites (Docker, Docker Compose)
    - Production deployment with docker-compose.prod.yml
    - Environment variables
    - Secrets management
    - Backup and restore
    - Monitoring and logging
  - `KUBERNETES_DEPLOYMENT.md`:
    - Prerequisites (kubectl, Helm)
    - Cluster setup
    - Deploy with kubectl or Helm
    - Scaling and autoscaling
    - Rolling updates
    - Rollback procedures
    - Monitoring and logging
  - `CI_CD.md`:
    - GitHub Actions workflow
    - Secrets configuration
    - Manual deployment
    - Troubleshooting pipeline failures
  - `PRODUCTION_CHECKLIST.md`:
    - Pre-deployment checklist:
      - [ ] All tests passing
      - [ ] Security scan passed
      - [ ] Performance tests passed
      - [ ] Backup plan in place
      - [ ] Rollback plan documented
      - [ ] Monitoring configured
      - [ ] Alerts configured
      - [ ] Documentation updated
      - [ ] Stakeholders notified
    - Post-deployment checklist:
      - [ ] Smoke tests passed
      - [ ] Health checks green
      - [ ] Logs are flowing
      - [ ] Metrics are being collected
      - [ ] No errors in logs
      - [ ] Monitor for 30 minutes

**Acceptance Criteria:**
- All deployment documentation is complete
- Instructions are clear and accurate
- Checklists are comprehensive
- Troubleshooting sections are helpful

**Dependencies:** INT-001, INT-026, INT-027, INT-029

---

### [INT-031] Create Operations Runbook
**Goal:** Document operational procedures for production support.

**Technical Details:**
- Create `docs/operations/RUNBOOK.md`:
  - **Service Overview**:
    - Architecture diagram
    - Service dependencies
    - Data flow
  - **Monitoring**:
    - Key metrics to monitor
    - Prometheus queries
    - Grafana dashboards
    - Alerting rules
  - **Health Checks**:
    - Health check endpoints
    - Expected responses
    - How to interpret health status
  - **Common Issues & Solutions**:
    - Issue: Service not starting
      - Check: Logs, environment variables, dependencies
      - Solution: Restart service, check config
    - Issue: High response time
      - Check: Database query time, cache hit ratio, JVM heap
      - Solution: Scale horizontally, optimize queries, increase cache
    - Issue: Database connection pool exhausted
      - Check: Connection pool metrics
      - Solution: Increase pool size, check for connection leaks
    - Issue: Out of memory
      - Check: JVM heap usage, GC logs
      - Solution: Increase heap size, investigate memory leak
  - **Scaling Procedures**:
    - How to scale services manually
    - How to scale databases (read replicas)
    - How to scale Redis (cluster mode)
  - **Backup & Restore**:
    - Backup schedule
    - How to create manual backup
    - How to restore from backup
    - Backup retention policy
  - **Rollback Procedures**:
    - How to rollback to previous version
    - How to verify rollback success
  - **Incident Response**:
    - Severity levels
    - Escalation procedures
    - Communication channels
  - **Maintenance Windows**:
    - Scheduled maintenance procedures
    - How to notify users
    - How to minimize downtime

**Acceptance Criteria:**
- Runbook is comprehensive
- Common issues are documented with solutions
- Procedures are clear and actionable
- Contact information is provided

**Dependencies:** INT-004, INT-030

---

### [INT-032] Create API Documentation Portal
**Goal:** Set up centralized API documentation for developers.

**Technical Details:**
- Options:
  1. **Swagger UI** (already available):
     - Access at: http://localhost:8080/swagger-ui.html
     - Export OpenAPI spec: http://localhost:8080/api-docs
  2. **ReDoc** (alternative view):
     - Add Springdoc ReDoc dependency
     - Access at: http://localhost:8080/redoc
  3. **Postman Docs** (public documentation):
     - Publish Postman collection
     - Generate public documentation
- Create landing page: `docs/api/index.html`:
  - Links to Swagger UI
  - Links to ReDoc
  - Links to Postman collection
  - API overview
  - Authentication guide (Phase 2)
  - Rate limiting information
  - Error codes reference
  - Examples
- Host documentation:
  - Static hosting: GitHub Pages, Netlify
  - Or include in frontend app

**Acceptance Criteria:**
- API documentation is accessible
- All endpoints are documented
- Examples are provided
- Documentation is up-to-date

**Dependencies:** BE-048, INT-006

---

### [INT-033] Create User Guide and Training Materials
**Goal:** Create documentation for end users.

**Technical Details:**
- Create `docs/user-guide/`:
  - `STUDENT_MANAGEMENT.md`:
    - How to register a new student
    - How to search for students
    - How to view student details
    - How to update student information
    - How to change student status
    - How to view enrollment history
  - `CONFIGURATION_MANAGEMENT.md`:
    - How to view school profile
    - How to update school information
    - How to view configuration settings
    - How to add new settings
    - How to update settings
    - How to delete settings
  - `FAQ.md`:
    - Common questions and answers
  - `TROUBLESHOOTING.md`:
    - Common user errors and solutions
- Create screenshots for each procedure
- Create video tutorials (optional):
  - Student registration walkthrough
  - Search and filter students
  - Configuration management

**Acceptance Criteria:**
- User guide is complete
- Instructions are clear with screenshots
- FAQ addresses common questions
- Troubleshooting guide is helpful

**Dependencies:** FE-024, FE-036

---

### [INT-034] Final Integration Verification
**Goal:** Perform final end-to-end verification of complete system.

**Technical Details:**
- Deploy complete system:
  - Use production Docker Compose or Kubernetes
  - Use production-like data
- Verification checklist:
  - [ ] All services start successfully
  - [ ] Health checks are green
  - [ ] Frontend loads correctly
  - [ ] User can register a student
  - [ ] User can search students
  - [ ] User can update student
  - [ ] User can view enrollment history
  - [ ] User can delete student
  - [ ] User can view school profile
  - [ ] User can update school profile
  - [ ] User can manage configurations
  - [ ] Error handling works (try invalid data)
  - [ ] Performance is acceptable (< 200ms p95)
  - [ ] Observability works (view traces in Zipkin)
  - [ ] Metrics are collected (view in Prometheus)
  - [ ] Dashboards show data (view in Grafana)
  - [ ] Logs are accessible (docker logs or kubectl logs)
  - [ ] Backup and restore work
  - [ ] Rate limiting works
  - [ ] CORS works from frontend
- Document any issues found
- Re-test after fixes

**Acceptance Criteria:**
- All verification items pass
- No critical issues found
- System is production-ready

**Dependencies:** All previous INT tasks

---

## Summary

**Total Tasks:** 34

**Estimated Timeline:**
- Phase 1 (Local Dev Setup): 3-4 days
- Phase 2 (API Integration Testing): 2-3 days
- Phase 3 (Frontend-Backend Integration): 2 days
- Phase 4 (E2E Testing): 3-4 days
- Phase 5 (Performance Testing): 2-3 days
- Phase 6 (Security Testing): 2-3 days
- Phase 7 (Deployment Preparation): 3-4 days
- Phase 8 (Documentation): 2-3 days

**Total Estimated Time:** 19-28 days

**Key Deliverables:**
1. Complete local development environment (Docker Compose)
2. Comprehensive integration test suite
3. End-to-end test suite (Playwright)
4. Performance test suite (k6)
5. Security test reports
6. Production deployment configurations (Docker Compose, Kubernetes, Helm)
7. CI/CD pipeline (GitHub Actions)
8. Complete documentation (deployment, operations, user guides)
9. API documentation portal
10. Production-ready system

**Success Criteria:**
- All 34 integration tasks completed
- All integration tests pass
- E2E tests pass
- Performance thresholds met
- No security vulnerabilities
- System deployed successfully
- Complete documentation
- User acceptance testing passed
