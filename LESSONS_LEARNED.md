# School Management System - Lessons Learned

## Date: 2025-11-24

This document captures issues encountered during development, their root causes, and solutions applied.

---

## Issue 1: Configuration Service MapStruct Code Generation Failure

### Date Encountered
2025-11-24

### Severity
High - Service startup failure

### Description
Configuration Service failed to start with the following error:
```
java.lang.NoClassDefFoundError: Configuration
	at java.base/java.lang.Class.getDeclaredMethods0(Native Method)
Caused by: java.lang.ClassNotFoundException: Configuration
```

### Root Cause
MapStruct annotation processor did not run during the initial Maven build (`mvn clean install -DskipTests`), resulting in the `ConfigurationMapperImpl` class not being generated properly.

### Impact
- Configuration Service could not start
- Swagger UI was inaccessible
- API endpoints were unavailable

### Solution
1. Ran clean compile on configuration-service module specifically:
   ```bash
   cd configuration-service && mvn clean compile
   ```
2. Verified that MapStruct generated the implementation:
   ```bash
   ls configuration-service/target/generated-sources/annotations/com/school/sms/configuration/application/mapper/
   ```
3. Restarted the service

### Prevention
- Always verify that annotation processors (MapStruct, Lombok) run successfully during build
- Check the `target/generated-sources/annotations` directory for generated code
- Consider adding a Maven enforcer plugin to fail the build if expected generated sources are missing

### File References
- `configuration-service/src/main/java/com/school/sms/configuration/application/mapper/ConfigurationMapper.java`
- `configuration-service/target/generated-sources/annotations/.../ConfigurationMapperImpl.java`

---

## Issue 2: SpringDoc OpenAPI Version Incompatibility with Spring Boot 3.5.0

### Date Encountered
2025-11-24

### Severity
High - Swagger UI completely non-functional

### Description
Swagger UI page loaded but failed to display API documentation. The OpenAPI JSON endpoint returned 500 Internal Server Error:
```
java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'
	at org.springdoc.api.AbstractOpenApiResource.calculatePath(AbstractOpenApiResource.java:499)
```

### Root Cause
SpringDoc OpenAPI version 2.3.0 is incompatible with Spring Boot 3.5.0. The `ControllerAdviceBean` constructor signature changed between Spring Framework versions, causing a method not found error.

### Impact
- Swagger UI was completely non-functional
- OpenAPI documentation could not be generated
- API testing and documentation tools were unavailable

### Solution
1. Upgraded SpringDoc OpenAPI version in parent POM:
   ```xml
   <!-- Before -->
   <springdoc.version>2.3.0</springdoc.version>

   <!-- After -->
   <springdoc.version>2.7.0</springdoc.version>
   ```

2. Rebuilt configuration-service with updated dependency:
   ```bash
   cd configuration-service && mvn clean install -DskipTests
   ```

3. Killed old service process and restarted with new version

4. Verified endpoints:
   - OpenAPI JSON: http://localhost:8082/api/v1/api-docs (200 OK)
   - Swagger UI: http://localhost:8082/swagger-ui/index.html (200 OK)

### Prevention
- Always check Spring Boot and SpringDoc compatibility matrix when upgrading Spring Boot versions
- SpringDoc OpenAPI compatibility reference:
  - Spring Boot 3.0.x - 3.2.x: SpringDoc 2.0.x - 2.3.x
  - Spring Boot 3.3.x - 3.5.x: SpringDoc 2.5.x - 2.7.x+
- Add version compatibility testing to CI/CD pipeline
- Monitor SpringDoc release notes for breaking changes

### File References
- `pom.xml:40` - SpringDoc version property

### Additional Notes
The custom OpenAPI documentation path is configured in `application.yml`:
```yaml
springdoc:
  api-docs:
    path: /api/v1/api-docs  # NOT the default /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---

## Issue 3: Confusion Between Docker and Host Port Mapping

### Date Encountered
2025-11-24

### Severity
Low - Documentation clarity issue

### Description
Initial confusion about PostgreSQL port configuration where Docker Compose mapped port 5433 (host) to 5432 (container), but application.yml files referenced different ports.

### Root Cause
Port mapping in docker-compose.yml vs application configuration:
- Docker Compose: `5433:5432` (host:container)
- Application: Should connect to `localhost:5433` from host machine

### Solution
Verified correct configuration:
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/sms_student_db  # Host port
```

### Prevention
- Document port mappings clearly in README
- Use consistent port naming conventions
- Add comments in docker-compose.yml explaining port mappings

---

## Issue 4: Database URL Mismatch Between Services

### Date Encountered
2025-11-23 (from SERVER_STATUS.md)

### Severity
Medium - Configuration inconsistency

### Description
Configuration Service configured for database `sms_db` but docker-compose creates `sms_student_db`.

### Root Cause
Inconsistent database naming between services and infrastructure configuration.

### Status
**OPEN** - Identified but not yet resolved in this session

### Recommended Solution
1. Update docker-compose.yml to create separate databases for each service:
   ```yaml
   - POSTGRES_DB=sms_student_db
   # Add: sms_config_db
   ```
   OR
2. Update configuration-service application.yml to use `sms_student_db`
3. Ensure all services use correct database names

### File References
- `docker-compose.yml` - Database creation
- `configuration-service/src/main/resources/application.yml` - Database URL

---

## Issue 5: JPA Entity Status Field Annotation Issue

### Date Encountered
2025-11-23 (from SERVER_STATUS.md)

### Severity
Low - Potential runtime issue

### Description
Student entity has `@Enumerated` annotation on a String field, which is incorrect. The `@Enumerated` annotation should only be used with enum types.

### Status
**OPEN** - Identified but not yet resolved

### Recommended Solution
Review `StudentJpaEntity` and ensure proper enum mapping:
```java
// If status is an enum
@Enumerated(EnumType.STRING)
private StudentStatus status;

// If status is a String (no @Enumerated needed)
private String status;
```

---

## Issue 6: Student Service Swagger UI OpenAPI Path Configuration Error

### Date Encountered
2025-11-24

### Severity
High - API documentation completely broken

### Description
Student Service Swagger UI and OpenAPI docs endpoint (`/api/v1/api-docs`) were returning HTTP 500 Internal Server Error, making API documentation and testing impossible.

### Root Cause
The custom OpenAPI path configuration `/api/v1/api-docs` in `application.yml` was causing internal errors in SpringDoc OpenAPI. SpringDoc OpenAPI expects either:
1. Default path: `/v3/api-docs`
2. Custom path that doesn't conflict with application routes

The custom path combined with other configuration issues (missing `@ParameterObject` annotations on `Pageable` parameters) caused OpenAPI spec generation to fail.

### Impact
- Swagger UI showed HTTP 500 error
- OpenAPI documentation could not be generated
- API testing tools (Postman, Swagger) couldn't import API specs
- Developer productivity reduced

### Solution
1. **Fixed OpenAPI Configuration** in `student-service/src/main/resources/application.yml`:
   ```yaml
   springdoc:
     api-docs:
       enabled: true
       path: /v3/api-docs      # Changed from /api/v1/api-docs
     swagger-ui:
       path: /swagger-ui.html
       enabled: true
       operations-sorter: method
       tags-sorter: alpha
     show-actuator: false       # Changed from true
   ```

2. **Added @ParameterObject Annotation** to `StudentController.java`:
   ```java
   @GetMapping
   @Operation(summary = "List all students")
   public ResponseEntity<StudentPageResponse> listStudents(
           @ParameterObject @ModelAttribute StudentSearchCriteria criteria,
           @ParameterObject Pageable pageable) {
       // ...
   }
   ```

3. **Added Error Logging** to `GlobalExceptionHandler.java`:
   ```java
   private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   @ExceptionHandler(Exception.class)
   public ResponseEntity<ProblemDetail> handleGenericException(
           Exception ex, WebRequest request) {
       logger.error("Unexpected error occurred: " + ex.getMessage(), ex);
       // ...
   }
   ```

4. Killed old process (PID 27776) and restarted with fixes:
   ```bash
   taskkill //PID 27776 //F
   cd student-service && mvn spring-boot:run
   ```

### Verification
After fixes:
- OpenAPI JSON: http://localhost:8081/v3/api-docs ✓ (200 OK)
- Swagger UI: http://localhost:8081/swagger-ui.html ✓ (302 redirect OK)
- All API endpoints properly documented in Swagger UI
- Student CRUD operations tested successfully

### Prevention
- Use standard SpringDoc paths (`/v3/api-docs`) unless there's a strong reason for custom paths
- Always annotate Spring Data `Pageable` parameters with `@ParameterObject` for proper OpenAPI generation
- Test Swagger UI immediately after making OpenAPI configuration changes
- Add comprehensive error logging to catch OpenAPI generation failures early
- Document any custom SpringDoc configuration in project README

### File References
- `student-service/src/main/resources/application.yml:86-95` - OpenAPI configuration
- `student-service/src/main/java/com/school/sms/student/presentation/controller/StudentController.java:8,43-44` - @ParameterObject annotations
- `student-service/src/main/java/com/school/sms/student/presentation/exception/GlobalExceptionHandler.java:23,93` - Error logging

### Additional Notes
- SpringDoc OpenAPI requires proper annotations for complex types (Pageable, custom DTOs)
- Missing `@ParameterObject` causes OpenAPI spec generation to fail silently in some cases
- The Swagger UI redirect (HTTP 302) is normal behavior, not an error

---

## Issue 7: Frontend CORS Error - Network Request Blocked

### Date Encountered
2025-11-25

### Severity
High - Frontend completely unable to communicate with backend

### Description
Frontend React application running on `http://localhost:3000` was unable to fetch student data from the backend API at `http://localhost:8081`. The browser blocked all requests with CORS policy errors:
```
Access to XMLHttpRequest at 'http://localhost:8081/api/v1/students?page=0&size=20' from origin 'http://localhost:3000' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

### Root Cause
The backend `WebConfig.java` in student-service was configured to allow CORS requests only from `http://localhost:5173` (Vite dev server default port), but the frontend was actually running on `http://localhost:3000` (React dev server port).

```java
// Before - Only allowed Vite dev server
.allowedOrigins("http://localhost:5173")
```

### Impact
- Student page showed "Failed to load students - Network Error"
- All API calls from frontend failed
- Frontend features completely non-functional
- User experience severely degraded

### Solution
Updated `WebConfig.java` in student-service to allow both ports:

```java
// After - Allow both Vite and React dev servers
.allowedOrigins("http://localhost:5173", "http://localhost:3000")
```

**Steps taken:**
1. Identified CORS error in browser console
2. Located CORS configuration in `student-service/src/main/java/com/school/sms/student/infrastructure/config/WebConfig.java:16`
3. Added `http://localhost:3000` to allowed origins
4. Killed running student-service process (PID 26288)
5. Restarted service with updated configuration
6. Verified fix in browser - students loaded successfully

### Verification
After fix:
- ✓ Student list loads without errors
- ✓ No CORS errors in browser console
- ✓ API requests complete successfully with 200 OK
- ✓ Search filters work correctly
- ✓ Pagination functions properly

### Prevention
- Document all frontend development server ports in README
- Configure CORS to accept requests from all known development ports
- Consider using environment-based CORS configuration:
  ```java
  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;
  ```
- Add CORS testing to integration tests
- Check browser console during initial frontend-backend integration

### File References
- `student-service/src/main/java/com/school/sms/student/infrastructure/config/WebConfig.java:16`

---

## Issue 8: React Infinite Render Loop - Maximum Update Depth Exceeded

### Date Encountered
2025-11-25

### Severity
Critical - Causes browser performance degradation and potential crashes

### Description
React application was experiencing an infinite render loop, generating hundreds of errors in the browser console:
```
Maximum update depth exceeded. This can happen when a component calls setState inside useEffect, but useEffect either doesn't have a dependency array, or one of the dependencies changes on every render.
```

Despite the page appearing to work visually, it was constantly re-rendering in the background, consuming CPU resources and degrading performance.

### Root Cause
The `StudentSearchBar` component had a `useEffect` that depended on the `onSearch` callback function:

```typescript
// StudentSearchBar.tsx
useEffect(() => {
  const params: StudentSearchParams = {};
  // ... build params
  onSearch(params);
}, [debouncedLastName, debouncedGuardianName, status, onSearch]); // ❌ onSearch in dependencies
```

The parent `StudentList` component was passing a non-memoized `handleSearch` function:

```typescript
// StudentList.tsx - BEFORE
const handleSearch = (params: StudentSearchParams) => {
  setSearchParams({
    ...params,
    page: PAGINATION.DEFAULT_PAGE,
    size: searchParams.size, // ❌ References searchParams
  });
};
```

**The infinite loop:**
1. `StudentList` renders and creates new `handleSearch` function
2. `handleSearch` reference changes (not memoized)
3. `StudentSearchBar` useEffect triggers because `onSearch` dependency changed
4. useEffect calls `onSearch(params)` which updates parent state
5. Parent re-renders with new state
6. New `handleSearch` function created → Loop repeats infinitely

### Impact
- Severe browser performance degradation
- High CPU usage
- Potential browser tab crashes
- Poor user experience despite visual appearance working
- Console flooded with error messages (437+ errors captured)

### Solution
Wrapped `handleSearch` in `useCallback` and used functional state update to avoid dependency on `searchParams`:

```typescript
// StudentList.tsx - AFTER
const handleSearch = useCallback((params: StudentSearchParams) => {
  setSearchParams((prev) => ({
    ...params,
    page: PAGINATION.DEFAULT_PAGE,
    size: prev.size, // ✓ Uses prev from functional update
  }));
}, []); // ✓ Empty dependency array - function never recreates
```

**Key improvements:**
1. `useCallback` with empty dependency array ensures function reference stays stable
2. Functional `setState((prev) => ...)` removes need for `searchParams` dependency
3. `StudentSearchBar` useEffect no longer triggers on every render

### Verification
After fix:
- ✓ Zero console errors after page reload
- ✓ No "Maximum update depth exceeded" errors
- ✓ Normal browser performance
- ✓ Search functionality works correctly
- ✓ Pagination works without performance issues

### Prevention
- **Always use `useCallback`** for functions passed as props to child components that use them in `useEffect` dependencies
- **Prefer functional setState** (`setState(prev => ...)`) to avoid depending on current state in callbacks
- **Use React DevTools Profiler** to detect excessive re-renders during development
- **Enable React StrictMode** in development to catch common issues early
- **Monitor browser console** for warning patterns during feature development
- **Add ESLint rules** for exhaustive-deps warnings:
  ```json
  {
    "rules": {
      "react-hooks/exhaustive-deps": "warn"
    }
  }
  ```

### Best Practices for useCallback/useMemo

#### When to use useCallback:
✓ Functions passed to child components as props
✓ Functions used in useEffect dependencies
✓ Event handlers passed to optimized child components (React.memo)
✓ Callbacks passed to third-party libraries

#### When NOT to use useCallback:
✗ Functions only used within the component
✗ Event handlers on native DOM elements (onClick, onChange)
✗ Functions that need to close over frequently changing values

#### Functional setState Pattern:
```typescript
// ❌ Bad - depends on current state
const [count, setCount] = useState(0);
const increment = useCallback(() => {
  setCount(count + 1); // Requires count in deps
}, [count]); // Function recreates when count changes

// ✓ Good - uses functional update
const increment = useCallback(() => {
  setCount(prev => prev + 1); // No deps needed
}, []); // Function never recreates
```

### File References
- `sms-frontend/src/features/students/components/StudentList.tsx:25-31` - Fixed handleSearch with useCallback
- `sms-frontend/src/features/students/components/StudentSearchBar.tsx:22-30` - useEffect with onSearch dependency

### Additional Notes
- This is one of the most common React performance issues
- React DevTools Profiler can help identify components with excessive renders
- Consider using `React.memo()` for child components receiving callback props
- The issue was not immediately visible because the page still rendered correctly
- Performance monitoring tools would have caught this earlier

---

## Successful Configurations

### Working Technology Stack
- **Spring Boot**: 3.5.0 ✓
- **Java**: 21 (OpenJDK 21.0.9) ✓
- **Maven**: 3.9+ ✓
- **PostgreSQL**: 18.1 (Docker) ✓
- **Redis**: 7.2.12 (Docker) ✓
- **SpringDoc OpenAPI**: 2.7.0 ✓
- **MapStruct**: 1.5.5.Final ✓
- **Lombok**: 1.18.30 ✓
- **Drools**: 9.44.0.Final ✓

### Working Service Configuration
Both microservices are successfully running:
- **Student Service**: Port 8081 ✓
  - Health: http://localhost:8081/actuator/health ✓
  - Swagger UI: http://localhost:8081/swagger-ui.html ✓
  - OpenAPI Docs: http://localhost:8081/v3/api-docs ✓
  - CRUD operations fully functional ✓
- **Configuration Service**: Port 8082 ✓
  - Health: http://localhost:8082/actuator/health ✓
  - Swagger UI: http://localhost:8082/swagger-ui/index.html ✓
  - OpenAPI Docs: http://localhost:8082/api/v1/api-docs ✓
- **PostgreSQL**: Port 5433 (host) → 5432 (container) ✓
- **Redis**: Port 6379 ✓
- **Zipkin**: Port 9411 ✓ (disabled in services due to compatibility)

### Student Service API Test Results
All core functionalities tested and working:
- ✓ Create Student (POST /api/v1/students) - HTTP 201
- ✓ List Students (GET /api/v1/students) - HTTP 200 with pagination
- ✓ Get Student by ID (GET /api/v1/students/{id}) - HTTP 200
- ✓ Update Student (PUT /api/v1/students/{id}) - Requires version for optimistic locking
- ✓ Update Status (PATCH /api/v1/students/{id}/status) - Requires version for optimistic locking
- ✓ Search by criteria (GET /api/v1/students?lastName=...) - HTTP 200
- ✓ Error handling (404, 400, 409) working as expected
- ✓ Validation rules enforced (required fields, age range, mobile format, etc.)

---

## Best Practices Learned

### 1. Dependency Version Management
- Always verify compatibility between major framework versions
- Check official compatibility matrices before upgrading
- Test all integrations after version upgrades
- Document known version incompatibilities

### 2. Annotation Processing
- Verify annotation processors run during build
- Check generated sources directories
- Clean build when adding new mappers or entities
- Don't skip compilation steps during multi-module builds

### 3. Service Startup
- Verify all dependencies are resolved before starting
- Check logs for initialization errors
- Test health endpoints after startup
- Document startup order dependencies

### 4. Port Configuration
- Document all port mappings clearly
- Use consistent port references across configurations
- Test connectivity from both host and container perspectives
- Avoid port conflicts in development environment

### 5. API Documentation
- Test Swagger UI immediately after service startup
- Verify OpenAPI JSON generation
- Document custom API documentation paths
- Include API documentation URLs in service README

---

## Testing Checklist (Post-Deployment)

### Service Health
- [ ] Health endpoint returns 200 OK
- [ ] Database connection successful
- [ ] Redis connection successful
- [ ] All actuator endpoints accessible

### API Documentation
- [ ] Swagger UI loads correctly
- [ ] OpenAPI JSON generates without errors
- [ ] All endpoints visible in Swagger UI
- [ ] Try It Out feature works for sample requests

### Functionality
- [ ] CRUD operations working
- [ ] Validation rules enforced
- [ ] Error handling returns proper HTTP status codes
- [ ] Pagination working correctly
- [ ] Filtering/searching functional

### Infrastructure
- [ ] Docker containers running
- [ ] Database accessible from services
- [ ] Redis caching working
- [ ] Logs being generated properly

---

## References

### Documentation Links
- [Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [MapStruct Documentation](https://mapstruct.org/)
- [Spring Boot & SpringDoc Compatibility](https://springdoc.org/#spring-boot-3-support)

### Project Documentation
- `README.md` - Project overview and setup
- `QUICK_START.md` - 5-minute setup guide
- `SERVER_STATUS.md` - Current server status and issues
- `IMPLEMENTATION_STATUS.md` - Implementation progress tracking

---

## Issue 9: Frontend Configuration Service - Wrong Port and Missing CORS

### Date Encountered
2025-11-25

### Severity
High - Configurations page completely non-functional

### Description
The Configurations page at `http://localhost:3000/configurations` was showing "Failed to load configurations - Request failed with status code 404". The frontend was unable to fetch configuration data from the backend.

### Root Cause
Two problems were identified:

1. **Frontend calling wrong service port**: The frontend was configured to call all APIs at `http://localhost:8081/api/v1` (student-service port), but the configurations endpoint is served by configuration-service on port `8082`.

2. **CORS not configured for React dev server**: Similar to Issue #7, the configuration-service `WebConfig.java` only allowed CORS requests from `http://localhost:5173` (Vite dev server) but the frontend was running on `http://localhost:3000` (React dev server).

### Impact
- Configurations page showed 404 error
- No configuration data could be loaded or managed
- Frontend feature completely broken

### Solution

**1. Updated frontend to support multiple service URLs** (`sms-frontend/src/config/env.ts`):
```typescript
export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1',
  configServiceUrl: import.meta.env.VITE_CONFIG_SERVICE_URL || 'http://localhost:8082/api/v1',
  // ...
} as const;
```

**2. Created separate API client for configuration service** (`sms-frontend/src/services/api/configService.ts`):
- Created dedicated `configApiClient` using `axios.create()` with `env.configServiceUrl`
- Added same interceptors for correlation ID and error handling
- Updated all configuration service calls to use `configApiClient` instead of `apiClient`

**3. Fixed CORS in configuration-service** (`configuration-service/src/main/java/.../WebConfig.java:23`):
```java
// Before - Only Vite dev server
.allowedOrigins("http://localhost:5173")

// After - Both Vite and React dev servers
.allowedOrigins("http://localhost:5173", "http://localhost:3000")
```

**4. Recompiled configuration-service**:
```bash
cd configuration-service && mvn clean compile
```
This step was necessary because MapStruct annotation processor needed to regenerate implementation classes.

**5. Restarted configuration-service**:
```bash
# Killed old process on port 8082
taskkill //PID <pid> //F

# Started with updated CORS configuration
cd configuration-service && mvn spring-boot:run
```

### Verification
After fixes:
- ✓ Configurations page loads successfully
- ✓ 12 configurations displayed in table format
- ✓ No CORS errors in browser console
- ✓ API requests to `http://localhost:8082/api/v1/configurations` succeed with 200 OK
- ✓ All CRUD operations available (Edit, Delete, Add New Configuration buttons visible)

### Prevention
- **Use environment variables** for service URLs to support different deployment scenarios
- **Document all service ports** clearly in README and docker-compose files
- **Create separate API clients** for different microservices with their own base URLs
- **Apply CORS lessons consistently** across all services (check Issue #7)
- **Test all frontend pages** after any backend configuration changes
- **Consider API Gateway** for production to provide single entry point and simplify frontend configuration

### Best Practices for Microservices Frontend Integration

#### Multi-Service API Configuration:
```typescript
// ✓ Good - Separate URLs for different services
const env = {
  studentServiceUrl: 'http://localhost:8081/api/v1',
  configServiceUrl: 'http://localhost:8082/api/v1',
  feeServiceUrl: 'http://localhost:8083/api/v1',
};

// ❌ Bad - Single URL for all services (won't work with microservices)
const env = {
  apiBaseUrl: 'http://localhost:8081/api/v1',
};
```

#### CORS Configuration Checklist:
- [ ] Add all development server ports (Vite: 5173, React: 3000, etc.)
- [ ] Add all production/staging frontend URLs
- [ ] Test CORS after any port or URL changes
- [ ] Document allowed origins in service README

### File References
- `sms-frontend/src/config/env.ts:3` - Added configServiceUrl
- `sms-frontend/src/services/api/configService.ts:13-75` - Created configApiClient
- `configuration-service/src/main/java/com/school/sms/configuration/presentation/config/WebConfig.java:23` - CORS configuration

### Additional Notes
- This issue combined two common problems: wrong service URL and CORS misconfiguration
- The MapStruct recompilation was necessary (Issue #1) due to stale generated classes
- Frontend hot reload picked up the changes automatically once backend was fixed
- Consider using an API Gateway (Spring Cloud Gateway, Kong, etc.) in production to avoid managing multiple service URLs in frontend

---

## Update Log

| Date | Updated By | Changes |
|------|-----------|---------|
| 2025-11-24 | Claude | Initial creation with MapStruct and SpringDoc issues |
| 2025-11-24 | Claude | Added Issue #6: Student Service Swagger UI OpenAPI path configuration error and resolution |
| 2025-11-25 | Claude | Added Issue #7: Frontend CORS error - Backend configured for wrong frontend port |
| 2025-11-25 | Claude | Added Issue #8: React infinite render loop - useCallback and functional setState patterns |
| 2025-11-25 | Claude | Added Issue #9: Frontend configuration service - wrong port and missing CORS |

---

*This document should be updated whenever new issues are encountered and resolved during development.*
