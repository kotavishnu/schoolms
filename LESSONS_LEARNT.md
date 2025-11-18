# Lessons Learnt

## Network Error and CORS Issues

### Network Error - Wrong API Gateway Port Configuration
- **Issue**: Frontend was calling `http://localhost:8081` instead of the correct API Gateway port `8080`, resulting in network errors
- **Root Cause**: Multiple configuration mismatches between frontend and backend
  - API client baseURL defaulted to port 8081
  - Environment variable `.env.development` had incorrect port 8081
  - Configuration API was bypassing the API Gateway and calling the service directly on port 8082
- **Lesson**: Always verify that frontend environment variables and API client configurations match the actual backend service ports
- **Best Practice**:
  - Use a single source of truth for API base URLs (environment variables)
  - Document the port allocation for all services (Eureka: 8761, Gateway: 8080, Services: 8081+)
  - Ensure all API calls route through the API Gateway for proper load balancing and routing

### CORS Error - Direct Service Access
- **Issue**: Initial CORS error when frontend tried to access `http://localhost:8082/configurations/settings` directly
- **Root Cause**: Frontend was attempting to bypass the API Gateway and call the configuration service directly on port 8082, which doesn't have CORS configured
- **Lesson**: In a microservices architecture with an API Gateway, all frontend requests must go through the Gateway
- **Best Practice**:
  - Configure CORS only on the API Gateway, not on individual microservices
  - Never expose microservice ports directly to the frontend
  - Use consistent API path patterns that route through the Gateway (e.g., `/api/v1/service-name/**`)
  - Remove any hardcoded service URLs from frontend code

### Configuration Management
- **Issue**: Vite dev server cached old module with incorrect port configuration even after code changes
- **Root Cause**: Environment variables in `.env.development` were loaded at startup and took precedence over code defaults
- **Lesson**: Environment variables override code defaults, and changes to `.env` files require a page refresh to take effect
- **Best Practice**:
  - Always check environment files (`.env`, `.env.development`, `.env.production`) when debugging configuration issues
  - Document which environment variables control critical settings
  - Use Vite's HMR (Hot Module Replacement) carefully - some changes (like env vars) require full page reload

### API Routing in Microservices
- **Issue**: Confusion about whether to use direct service URLs or API Gateway URLs
- **Root Cause**: Frontend API client had mixed approaches - some using baseURL overrides, others using full paths
- **Lesson**: Maintain consistency in how API endpoints are called across the frontend application
- **Best Practice**:
  - All API calls should use relative paths that route through the API Gateway
  - Use the same baseURL for all API clients (the Gateway URL)
  - API Gateway routes should match the service's controller paths (e.g., Gateway: `/api/v1/configurations/**` → Service: `/api/v1/configurations/...`)

### API Gateway StripPrefix Filter Requirement
- **Issue**: Frontend calling `/api/v1/students` returned 500 Internal Server Error despite services being up
- **Root Cause**: API Gateway was forwarding `/api/v1/students/**` to student-service, but the service expects `/students` (without the `/api/v1` prefix)
- **Solution**: Added `StripPrefix=2` filter to remove the first two path segments (`/api/v1`) before forwarding to microservices
- **Lesson**: When API Gateway uses versioned paths like `/api/v1/{service}/**`, microservices don't need the prefix in their controller mappings
- **Best Practice**:
  - Frontend calls: `http://localhost:8080/api/v1/students`
  - API Gateway route: `/api/v1/students/**` with `StripPrefix=2` filter
  - Microservice controller: `@RequestMapping("/students")`
  - The gateway strips `/api/v1` and forwards `/students` to the service

### Debugging Methodology
- **Key Steps That Led to Resolution**:
  1. Checked browser console for exact error messages and failed URLs
  2. Inspected network requests to identify which port was being called
  3. Verified backend services were running and responding correctly (direct curl test to Gateway)
  4. Examined frontend API client configuration and environment variables
  5. Identified discrepancy between configured port (8081) and actual Gateway port (8080)
- **Lesson**: Always verify the complete request flow from frontend → Gateway → Backend service
- **Best Practice**:
  - Use browser DevTools Network tab to inspect actual requests being made
  - Test API endpoints directly with curl/Postman to isolate frontend vs backend issues
  - Check both code and environment configuration files when debugging URL issues

## CORS Policy Issues in Microservices Architecture

### Problem Statement 1: Wildcard Origins with Credentials
- **Issue**: CORS error when frontend at `http://localhost:5173` tried to access API Gateway
- **Error**: "Access to XMLHttpRequest has been blocked by CORS policy"
- **Root Cause**: API Gateway CORS configuration had conflicting settings:
  ```yaml
  allowed-origins: "*"  # Wildcard origin
  allow-credentials: false
  ```
- **Why It Failed**: When using credentials (cookies, authorization headers), CORS specification does not allow wildcard origins (`*`). The origin must be explicitly specified.
- **Lesson**: Wildcard origins (`*`) and credentials are mutually exclusive in CORS policy

### Solution 1: Explicit Origins with Credentials
- **Change Made**: Updated API Gateway configuration (`backend/api-gateway/src/main/resources/application.yml`)
  ```yaml
  allowed-origins:
    - "http://localhost:5173"
    - "http://localhost:5174"
    - "http://localhost:3000"
  allow-credentials: true
  ```
- **Result**: CORS preflight requests now return proper headers:
  - `Access-Control-Allow-Origin: http://localhost:5173`
  - `Access-Control-Allow-Credentials: true`

### Problem Statement 2: Duplicate CORS Headers
- **Issue**: Browser error showing duplicate `Access-Control-Allow-Origin` headers
- **Error Message**:
  ```
  The 'Access-Control-Allow-Origin' header contains multiple values
  'http://localhost:5173, http://localhost:5173', but only one is allowed.
  ```
- **Root Cause**: Both API Gateway and Student Service were adding CORS headers to responses
  - API Gateway: Configured CORS in `application.yml`
  - Student Service: Had `CorsFilter` bean in `WebConfig.java`
- **Why It Failed**: When a request flows through the Gateway to a microservice, both added their own CORS headers, resulting in duplicates
- **Lesson**: In a microservices architecture with an API Gateway, CORS should be configured **only at the Gateway level**

### Solution 2: Centralized CORS at Gateway
- **Changes Made**:
  1. **Disabled CORS in Student Service** (`backend/student-service/src/main/java/.../WebConfig.java`)
     - Removed the `CorsFilter` bean
     - Added comment explaining CORS is handled by Gateway
  2. **Kept CORS only in API Gateway**
     - Gateway handles all CORS headers for incoming requests
     - Microservices behind the Gateway don't need CORS configuration
- **Architectural Pattern**:
  ```
  Frontend (localhost:5173)
    ↓ (CORS headers added)
  API Gateway (localhost:8080) ← CORS configured here
    ↓ (No CORS headers)
  Microservices (localhost:8081+) ← CORS NOT configured here
  ```
- **Result**: Single `Access-Control-Allow-Origin` header in responses, CORS working correctly

### Best Practices for CORS in Microservices
1. **Single Point of CORS Configuration**: Configure CORS only at the API Gateway level
2. **Explicit Origins for Credentials**: Never use wildcard (`*`) origins when `allow-credentials: true`
3. **Microservices Behind Gateway**: Backend services don't need CORS if accessed only through Gateway
4. **Development vs Production**:
   - Development: List specific localhost ports
   - Production: List your actual domain(s)
5. **Testing CORS**:
   - Use browser DevTools Network tab to inspect CORS headers
   - Test preflight (OPTIONS) requests separately
   - Check for duplicate headers in response

### Debugging Steps for CORS Issues
1. **Check the error message**: Distinguish between "No header" vs "Multiple headers" vs "Invalid header"
2. **Inspect actual headers**: Use `curl -i -H "Origin: http://localhost:5173"` to see exact headers
3. **Verify configuration points**: Check all places where CORS might be configured
4. **Test preflight requests**: `curl -X OPTIONS -H "Origin: ..." -H "Access-Control-Request-Method: GET"`
5. **Check credentials requirement**: If using cookies/auth, ensure origins are explicit, not wildcards

## API Gateway StripPrefix Misconfiguration

### Problem Statement: 500 Internal Server Error on Configuration Service
- **Issue**: Frontend calling `/api/v1/configurations/settings` returned 500 Internal Server Error
- **Error Message**:
  ```
  org.springframework.web.server.ResponseStatusException: 500 INTERNAL_SERVER_ERROR
  "No handler found for endpoint: settings"
  ```
- **Root Cause**: API Gateway had incorrect `StripPrefix=2` configuration for configuration-service route
  - Frontend called: `/api/v1/configurations/settings`
  - Gateway stripped 2 segments: removed `/api/v1`
  - Gateway forwarded: `/configurations/settings` to configuration-service
  - But configuration-service controller expected: `/api/v1/configurations/settings`
- **Why It Failed**: The StripPrefix filter was removing path segments that the microservice controller needed to match its `@RequestMapping`

### Understanding StripPrefix Filter
The `StripPrefix` filter removes a specified number of path segments from the URL before forwarding to the microservice:
- `StripPrefix=1` removes the first segment (e.g., `/api/service` → `/service`)
- `StripPrefix=2` removes the first two segments (e.g., `/api/v1/service` → `/service`)
- `StripPrefix=3` removes the first three segments (e.g., `/api/v1/service/endpoint` → `/endpoint`)

### When to Use StripPrefix

#### Option 1: Use StripPrefix (Cleaner Microservice Controllers)
```yaml
# API Gateway Configuration
spring:
  cloud:
    gateway:
      routes:
        - id: student-service
          uri: lb://student-service
          predicates:
            - Path=/api/v1/students/**
          filters:
            - StripPrefix=2
```
```java
// Microservice Controller
@RestController
@RequestMapping("/students")  // No /api/v1 prefix needed
public class StudentController {
    @GetMapping
    public List<Student> getAllStudents() { ... }
}
```
**Frontend calls**: `http://localhost:8080/api/v1/students`
**Gateway forwards**: `http://student-service/students`

#### Option 2: No StripPrefix (Full Path in Controllers)
```yaml
# API Gateway Configuration
spring:
  cloud:
    gateway:
      routes:
        - id: configuration-service
          uri: lb://configuration-service
          predicates:
            - Path=/api/v1/configurations/**
          # No StripPrefix filter
```
```java
// Microservice Controller
@RestController
@RequestMapping("/api/v1/configurations/settings")  // Full path including /api/v1
public class ConfigurationController {
    @GetMapping
    public Map<String, Object> getAllSettings() { ... }
}
```
**Frontend calls**: `http://localhost:8080/api/v1/configurations/settings`
**Gateway forwards**: `http://configuration-service/api/v1/configurations/settings`

### Solution Applied
- **Change Made**: Removed `StripPrefix=2` filter from configuration-service route in API Gateway (`backend/api-gateway/src/main/resources/application.yml`)
  ```yaml
  # Before (INCORRECT)
  - id: configuration-service
    uri: lb://configuration-service
    predicates:
      - Path=/api/v1/configurations/**
    filters:
      - StripPrefix=2  # ❌ This was causing the error

  # After (CORRECT)
  - id: configuration-service
    uri: lb://configuration-service
    predicates:
      - Path=/api/v1/configurations/**
    # No filters - Gateway forwards full path
  ```
- **Result**: Gateway now forwards complete path `/api/v1/configurations/settings` to configuration-service, matching the controller's `@RequestMapping`

### Best Practices for API Gateway Routing
1. **Consistency**: Choose one approach (StripPrefix or full paths) and apply it consistently across all microservices
2. **Controller Mapping Alignment**: Ensure microservice `@RequestMapping` paths match what the Gateway forwards after any StripPrefix
3. **Documentation**: Document which services use StripPrefix and which don't to avoid confusion
4. **Testing**: Always test gateway routes with curl to verify path forwarding:
   ```bash
   # Test through Gateway
   curl http://localhost:8080/api/v1/configurations/settings

   # Test direct to service (for debugging)
   curl http://localhost:8082/api/v1/configurations/settings
   ```
5. **Debugging Path Issues**:
   - Enable DEBUG logging for Spring Cloud Gateway to see route matching
   - Check microservice logs for "No handler found" errors
   - Verify the exact path being forwarded by the Gateway
   - Compare Gateway route path with controller `@RequestMapping`

### Common StripPrefix Mistakes
1. **Using StripPrefix when controller has full path**: Strips too much, causes 404
2. **Not using StripPrefix when controller expects short path**: Includes too much, causes 404
3. **Inconsistent configuration across services**: Some services work, others don't
4. **Forgetting to restart Gateway after configuration changes**: Old configuration still active
