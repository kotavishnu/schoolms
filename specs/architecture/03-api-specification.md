# API Specification

## 1. Overview

This document defines the RESTful API contracts for the School Management System (SMS). The API follows OpenAPI 3.0 standards and implements RFC 7807 Problem Details for error responses.

## 2. API Design Principles

### 2.1 RESTful Standards
```yaml
Resource-Based URLs: Use nouns, not verbs
HTTP Methods: GET (read), POST (create), PUT (update), DELETE (delete)
Stateless: No server-side session state
Versioning: URL-based versioning (/api/v1/)
Content Type: application/json
Character Encoding: UTF-8
```

### 2.2 Naming Conventions
```yaml
URLs: lowercase, kebab-case
  - /api/v1/students
  - /api/v1/configuration-settings

JSON Fields: camelCase
  - firstName, lastName, dateOfBirth

Query Parameters: camelCase
  - ?page=0&size=20&sortBy=lastName
```

### 2.3 HTTP Status Codes
```yaml
2xx Success:
  200 OK: Successful GET, PUT requests
  201 Created: Successful POST with resource creation
  204 No Content: Successful DELETE

4xx Client Errors:
  400 Bad Request: Validation errors, malformed request
  404 Not Found: Resource does not exist
  409 Conflict: Duplicate resource, optimistic lock failure
  422 Unprocessable Entity: Business rule violation

5xx Server Errors:
  500 Internal Server Error: Unexpected server error
  503 Service Unavailable: Service temporarily unavailable
```

## 3. Common API Patterns

### 3.1 Request Headers
```yaml
Required:
  Content-Type: application/json
  Accept: application/json

Optional:
  X-Correlation-ID: UUID for request tracing
  Authorization: Bearer {token} (Phase 2)
```

### 3.2 Pagination
```yaml
Query Parameters:
  page: Page number (0-based), default: 0
  size: Page size, default: 20, max: 100
  sortBy: Sort field, default: id
  sortDirection: ASC or DESC, default: ASC

Response Structure:
{
  "content": [...],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

### 3.3 Error Response (RFC 7807)
```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Mobile number must be unique",
  "instance": "/api/v1/students",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number 9876543210 already exists",
      "code": "DUPLICATE_MOBILE"
    }
  ]
}
```

## 4. Student Service API

### 4.1 Create Student

**Endpoint:** `POST /api/v1/students`

**Description:** Register a new student with auto-generated student ID

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main St, City, State 12345",
  "fathersName": "Richard Doe",
  "mothersName": "Jane Doe",
  "identificationMark": "Mole on left arm",
  "aadhaarNumber": "123456789012"
}
```

**Validation Rules:**
```yaml
firstName:
  - Required: true
  - MinLength: 2
  - MaxLength: 100
  - Pattern: ^[a-zA-Z\s]+$

lastName:
  - Required: true
  - MinLength: 2
  - MaxLength: 100
  - Pattern: ^[a-zA-Z\s]+$

dateOfBirth:
  - Required: true
  - Format: yyyy-MM-dd
  - BusinessRule: Age must be between 3 and 18 years

mobile:
  - Required: true
  - Pattern: ^\d{10}$
  - Unique: true

email:
  - Required: false
  - Format: Valid email address

aadhaarNumber:
  - Required: false
  - Pattern: ^\d{12}$
  - Unique: true (if provided)
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "studentId": "STD-20241206-0001",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main St, City, State 12345",
  "fathersName": "Richard Doe",
  "mothersName": "Jane Doe",
  "identificationMark": "Mole on left arm",
  "aadhaarNumber": "123456789012",
  "status": "ACTIVE",
  "version": 0,
  "createdAt": "2024-12-06T10:30:00Z",
  "updatedAt": "2024-12-06T10:30:00Z"
}
```

**Error Responses:**

*400 Bad Request - Validation Error:*
```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/students",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "errors": [
    {
      "field": "dateOfBirth",
      "message": "Student age must be between 3 and 18 years",
      "code": "INVALID_AGE"
    }
  ]
}
```

*409 Conflict - Duplicate Mobile:*
```json
{
  "type": "https://api.school.com/errors/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Mobile number already exists",
  "instance": "/api/v1/students",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number 9876543210 is already registered",
      "code": "DUPLICATE_MOBILE"
    }
  ]
}
```

---

### 4.2 Get Student by ID

**Endpoint:** `GET /api/v1/students/{studentId}`

**Description:** Retrieve detailed student information by student ID

**Path Parameters:**
- `studentId` (string, required): Student ID (e.g., STD-20241206-0001)

**Success Response (200 OK):**
```json
{
  "id": 1,
  "studentId": "STD-20241206-0001",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main St, City, State 12345",
  "fathersName": "Richard Doe",
  "mothersName": "Jane Doe",
  "identificationMark": "Mole on left arm",
  "aadhaarNumber": "123456789012",
  "status": "ACTIVE",
  "version": 0,
  "createdAt": "2024-12-06T10:30:00Z",
  "updatedAt": "2024-12-06T10:30:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.school.com/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student not found",
  "instance": "/api/v1/students/STD-20241206-9999",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.3 Update Student

**Endpoint:** `PUT /api/v1/students/{studentId}`

**Description:** Update allowed student fields (firstName, lastName, mobile, status)

**Path Parameters:**
- `studentId` (string, required): Student ID

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "mobile": "9876543211",
  "status": "INACTIVE",
  "version": 0
}
```

**Validation Rules:**
```yaml
firstName:
  - Required: true
  - MinLength: 2
  - MaxLength: 100

lastName:
  - Required: true
  - MinLength: 2
  - MaxLength: 100

mobile:
  - Required: true
  - Pattern: ^\d{10}$
  - Unique: true

status:
  - Required: true
  - Enum: [ACTIVE, INACTIVE]

version:
  - Required: true
  - Description: For optimistic locking
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "studentId": "STD-20241206-0001",
  "firstName": "John",
  "lastName": "Smith",
  "dateOfBirth": "2010-05-15",
  "mobile": "9876543211",
  "email": "john.doe@example.com",
  "address": "123 Main St, City, State 12345",
  "fathersName": "Richard Doe",
  "mothersName": "Jane Doe",
  "identificationMark": "Mole on left arm",
  "aadhaarNumber": "123456789012",
  "status": "INACTIVE",
  "version": 1,
  "createdAt": "2024-12-06T10:30:00Z",
  "updatedAt": "2024-12-06T11:00:00Z"
}
```

**Error Response (409 Conflict - Version Mismatch):**
```json
{
  "type": "https://api.school.com/errors/conflict",
  "title": "Optimistic Lock Exception",
  "status": 409,
  "detail": "Student was modified by another user. Please refresh and try again.",
  "instance": "/api/v1/students/STD-20241206-0001",
  "timestamp": "2024-12-06T11:00:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.4 Delete Student

**Endpoint:** `DELETE /api/v1/students/{studentId}`

**Description:** Delete a student record (soft delete recommended, hard delete shown here)

**Path Parameters:**
- `studentId` (string, required): Student ID

**Success Response (204 No Content):**
```
HTTP/1.1 204 No Content
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.school.com/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student not found",
  "instance": "/api/v1/students/STD-20241206-9999",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.5 Search Students

**Endpoint:** `GET /api/v1/students`

**Description:** Search and list students with pagination

**Query Parameters:**
```yaml
lastName: (string, optional) - Filter by last name (partial match, case-insensitive)
fathersName: (string, optional) - Filter by father's/guardian's name (partial match)
status: (string, optional) - Filter by status (ACTIVE, INACTIVE)
page: (integer, optional, default: 0) - Page number (0-based)
size: (integer, optional, default: 20) - Page size (max: 100)
sortBy: (string, optional, default: "id") - Sort field
sortDirection: (string, optional, default: "ASC") - Sort direction (ASC, DESC)
```

**Example Request:**
```
GET /api/v1/students?lastName=doe&status=ACTIVE&page=0&size=20&sortBy=lastName&sortDirection=ASC
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "studentId": "STD-20241206-0001",
      "firstName": "John",
      "lastName": "Doe",
      "mobile": "9876543210",
      "status": "ACTIVE",
      "createdAt": "2024-12-06T10:30:00Z"
    },
    {
      "id": 5,
      "studentId": "STD-20241206-0005",
      "firstName": "Jane",
      "lastName": "Doe",
      "mobile": "9876543214",
      "status": "ACTIVE",
      "createdAt": "2024-12-06T11:00:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

**Empty Result (200 OK):**
```json
{
  "content": [],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

---

### 4.6 Get Student Enrollment History

**Endpoint:** `GET /api/v1/students/{studentId}/enrollment-history`

**Description:** Retrieve enrollment history for a student

**Path Parameters:**
- `studentId` (string, required): Student ID

**Success Response (200 OK):**
```json
{
  "studentId": "STD-20241206-0001",
  "enrollments": [
    {
      "id": 1,
      "academicYear": "2024-2025",
      "gradeClass": "Grade 5",
      "section": "A",
      "enrollmentDate": "2024-04-01",
      "withdrawalDate": null,
      "status": "ENROLLED",
      "remarks": null,
      "createdAt": "2024-04-01T09:00:00Z"
    },
    {
      "id": 2,
      "academicYear": "2023-2024",
      "gradeClass": "Grade 4",
      "section": "B",
      "enrollmentDate": "2023-04-01",
      "withdrawalDate": "2024-03-31",
      "status": "COMPLETED",
      "remarks": "Promoted to Grade 5",
      "createdAt": "2023-04-01T09:00:00Z"
    }
  ]
}
```

---

### 4.7 Create Enrollment

**Endpoint:** `POST /api/v1/students/{studentId}/enrollment-history`

**Description:** Create a new enrollment record for a student

**Path Parameters:**
- `studentId` (string, required): Student ID

**Request Body:**
```json
{
  "academicYear": "2024-2025",
  "gradeClass": "Grade 5",
  "section": "A",
  "enrollmentDate": "2024-04-01",
  "remarks": "New enrollment"
}
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "studentId": "STD-20241206-0001",
  "academicYear": "2024-2025",
  "gradeClass": "Grade 5",
  "section": "A",
  "enrollmentDate": "2024-04-01",
  "withdrawalDate": null,
  "status": "ENROLLED",
  "remarks": "New enrollment",
  "version": 0,
  "createdAt": "2024-12-06T10:30:00Z",
  "updatedAt": "2024-12-06T10:30:00Z"
}
```

**Error Response (409 Conflict - Duplicate Enrollment):**
```json
{
  "type": "https://api.school.com/errors/conflict",
  "title": "Duplicate Enrollment",
  "status": 409,
  "detail": "Student already enrolled for this academic year",
  "instance": "/api/v1/students/STD-20241206-0001/enrollment-history",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 5. Configuration Service API

### 5.1 Get All Configurations

**Endpoint:** `GET /api/v1/configurations`

**Description:** Retrieve all configuration settings, optionally filtered by category

**Query Parameters:**
```yaml
category: (string, optional) - Filter by category (GENERAL, ACADEMIC, FINANCIAL)
```

**Example Request:**
```
GET /api/v1/configurations?category=GENERAL
```

**Success Response (200 OK):**
```json
{
  "configurations": [
    {
      "id": 1,
      "category": "GENERAL",
      "key": "school.name",
      "value": "ABC High School",
      "description": "Official school name",
      "dataType": "STRING",
      "isEncrypted": false,
      "version": 0,
      "updatedAt": "2024-12-06T10:00:00Z"
    },
    {
      "id": 2,
      "category": "GENERAL",
      "key": "school.code",
      "value": "ABC-001",
      "description": "School identification code",
      "dataType": "STRING",
      "isEncrypted": false,
      "version": 0,
      "updatedAt": "2024-12-06T10:00:00Z"
    }
  ]
}
```

---

### 5.2 Get Configuration by Category and Key

**Endpoint:** `GET /api/v1/configurations/{category}/{key}`

**Description:** Retrieve a specific configuration value

**Path Parameters:**
- `category` (string, required): Configuration category
- `key` (string, required): Configuration key

**Example Request:**
```
GET /api/v1/configurations/GENERAL/school.name
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "category": "GENERAL",
  "key": "school.name",
  "value": "ABC High School",
  "description": "Official school name",
  "dataType": "STRING",
  "isEncrypted": false,
  "version": 0,
  "updatedAt": "2024-12-06T10:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.school.com/errors/not-found",
  "title": "Configuration Not Found",
  "status": 404,
  "detail": "Configuration not found for category=GENERAL, key=invalid.key",
  "instance": "/api/v1/configurations/GENERAL/invalid.key",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 5.3 Create or Update Configuration

**Endpoint:** `PUT /api/v1/configurations/{category}/{key}`

**Description:** Create a new configuration or update an existing one (upsert)

**Path Parameters:**
- `category` (string, required): Configuration category
- `key` (string, required): Configuration key

**Request Body:**
```json
{
  "value": "ABC High School",
  "description": "Official school name",
  "dataType": "STRING",
  "isEncrypted": false
}
```

**Validation Rules:**
```yaml
category:
  - Required: true
  - Enum: [GENERAL, ACADEMIC, FINANCIAL]

key:
  - Required: true
  - Pattern: ^[a-z0-9.]+$
  - MaxLength: 100

value:
  - Required: true
  - MaxLength: 1000 (or TEXT for JSON)

dataType:
  - Required: true
  - Enum: [STRING, NUMBER, BOOLEAN, JSON]
  - Default: STRING
```

**Success Response (200 OK - Updated):**
```json
{
  "id": 1,
  "category": "GENERAL",
  "key": "school.name",
  "value": "ABC High School",
  "description": "Official school name",
  "dataType": "STRING",
  "isEncrypted": false,
  "version": 1,
  "createdAt": "2024-12-06T10:00:00Z",
  "updatedAt": "2024-12-06T11:00:00Z"
}
```

**Success Response (201 Created - New):**
```json
{
  "id": 10,
  "category": "GENERAL",
  "key": "school.email",
  "value": "contact@abcschool.com",
  "description": "School contact email",
  "dataType": "STRING",
  "isEncrypted": false,
  "version": 0,
  "createdAt": "2024-12-06T11:00:00Z",
  "updatedAt": "2024-12-06T11:00:00Z"
}
```

---

### 5.4 Delete Configuration

**Endpoint:** `DELETE /api/v1/configurations/{category}/{key}`

**Description:** Delete a configuration setting

**Path Parameters:**
- `category` (string, required): Configuration category
- `key` (string, required): Configuration key

**Success Response (204 No Content):**
```
HTTP/1.1 204 No Content
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.school.com/errors/not-found",
  "title": "Configuration Not Found",
  "status": 404,
  "detail": "Configuration not found",
  "instance": "/api/v1/configurations/GENERAL/invalid.key",
  "timestamp": "2024-12-06T10:30:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 5.5 Get Configurations by Category (Grouped)

**Endpoint:** `GET /api/v1/configurations/grouped/{category}`

**Description:** Retrieve all configurations for a category as a grouped map

**Path Parameters:**
- `category` (string, required): Configuration category

**Success Response (200 OK):**
```json
{
  "category": "GENERAL",
  "settings": {
    "school.name": "ABC High School",
    "school.code": "ABC-001",
    "school.address": "123 Education Lane, City, State 12345",
    "school.email": "contact@abcschool.com",
    "school.phone": "1234567890"
  }
}
```

---

## 6. API Response Time Requirements

```yaml
Performance Targets (95th Percentile):

Student Service:
  POST /api/v1/students: <150ms
  GET /api/v1/students/{studentId}: <100ms
  PUT /api/v1/students/{studentId}: <150ms
  DELETE /api/v1/students/{studentId}: <100ms
  GET /api/v1/students (search): <200ms
  GET /api/v1/students/{studentId}/enrollment-history: <150ms

Configuration Service:
  GET /api/v1/configurations: <100ms
  GET /api/v1/configurations/{category}/{key}: <50ms (cached)
  PUT /api/v1/configurations/{category}/{key}: <150ms
  DELETE /api/v1/configurations/{category}/{key}: <100ms
```

## 7. API Versioning Strategy

```yaml
Current Version: v1
URL Pattern: /api/v1/{resource}

Versioning Policy:
  - Breaking changes require new version (v2, v3, etc.)
  - Non-breaking changes can be added to current version
  - Support N-1 versions for 6 months after new release

Breaking Changes Include:
  - Removing fields
  - Changing field types
  - Changing URL structure
  - Changing required fields

Non-Breaking Changes Include:
  - Adding new fields (optional)
  - Adding new endpoints
  - Adding new query parameters (optional)
```

## 8. CORS Configuration

```yaml
Allowed Origins (Development):
  - http://localhost:3000 (React dev server)
  - http://localhost:5173 (Vite dev server)

Allowed Origins (Production):
  - https://school.example.com

Allowed Methods:
  - GET, POST, PUT, DELETE, OPTIONS

Allowed Headers:
  - Content-Type, Authorization, X-Correlation-ID

Exposed Headers:
  - X-Correlation-ID

Max Age: 3600 seconds

Reference: LESSONS_LEARNED.md [D-001]
```

## 9. Rate Limiting

```yaml
General Endpoints:
  - 100 requests per minute per IP

Search Endpoints:
  - 30 requests per minute per IP

Configuration Updates:
  - 10 requests per minute per IP

Response Headers:
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 95
  X-RateLimit-Reset: 1638864000
```

## 10. OpenAPI 3.0 Schema Summary

```yaml
openapi: 3.0.3
info:
  title: School Management System API
  version: 1.0.0
  description: RESTful API for student registration and school configuration
  contact:
    name: API Support
    email: api-support@school.com

servers:
  - url: http://localhost:8081/api/v1
    description: Student Service (Development)
  - url: http://localhost:8082/api/v1
    description: Configuration Service (Development)
  - url: https://api.school.com/api/v1
    description: Production

tags:
  - name: Students
    description: Student management operations
  - name: Enrollments
    description: Student enrollment history
  - name: Configurations
    description: School configuration management

paths:
  /students:
    get: Search and list students
    post: Create new student

  /students/{studentId}:
    get: Get student by ID
    put: Update student
    delete: Delete student

  /students/{studentId}/enrollment-history:
    get: Get enrollment history
    post: Create enrollment

  /configurations:
    get: Get all configurations

  /configurations/{category}/{key}:
    get: Get configuration by category and key
    put: Create or update configuration
    delete: Delete configuration

  /configurations/grouped/{category}:
    get: Get configurations grouped by category
```

## 11. Error Code Reference

```yaml
Validation Errors (400):
  INVALID_AGE: Student age not in 3-18 range
  INVALID_MOBILE: Mobile format invalid
  INVALID_EMAIL: Email format invalid
  REQUIRED_FIELD: Required field missing
  INVALID_FIELD_LENGTH: Field length exceeds limit

Business Rule Violations (422):
  AGE_OUT_OF_RANGE: Student age must be 3-18
  DUPLICATE_MOBILE: Mobile already registered
  DUPLICATE_AADHAAR: Aadhaar already registered
  DUPLICATE_ENROLLMENT: Student already enrolled for academic year
  CLASS_CAPACITY_EXCEEDED: Class capacity reached

Resource Errors (404):
  STUDENT_NOT_FOUND: Student does not exist
  CONFIGURATION_NOT_FOUND: Configuration not found

Conflict Errors (409):
  OPTIMISTIC_LOCK_FAILURE: Resource modified by another user
  DUPLICATE_RESOURCE: Resource already exists

Server Errors (500):
  INTERNAL_SERVER_ERROR: Unexpected error occurred
  DATABASE_ERROR: Database operation failed
```

## 12. API Testing with cURL

### Student Operations

```bash
# Create Student
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: $(uuidgen)" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2010-05-15",
    "mobile": "9876543210",
    "email": "john.doe@example.com",
    "fathersName": "Richard Doe",
    "mothersName": "Jane Doe"
  }'

# Get Student by ID
curl -X GET http://localhost:8081/api/v1/students/STD-20241206-0001 \
  -H "Accept: application/json"

# Update Student
curl -X PUT http://localhost:8081/api/v1/students/STD-20241206-0001 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "mobile": "9876543211",
    "status": "ACTIVE",
    "version": 0
  }'

# Search Students
curl -X GET "http://localhost:8081/api/v1/students?lastName=doe&page=0&size=20" \
  -H "Accept: application/json"

# Delete Student
curl -X DELETE http://localhost:8081/api/v1/students/STD-20241206-0001
```

### Configuration Operations

```bash
# Get All Configurations
curl -X GET "http://localhost:8082/api/v1/configurations?category=GENERAL" \
  -H "Accept: application/json"

# Get Single Configuration
curl -X GET http://localhost:8082/api/v1/configurations/GENERAL/school.name \
  -H "Accept: application/json"

# Create/Update Configuration
curl -X PUT http://localhost:8082/api/v1/configurations/GENERAL/school.name \
  -H "Content-Type: application/json" \
  -d '{
    "value": "ABC High School",
    "description": "Official school name",
    "dataType": "STRING",
    "isEncrypted": false
  }'

# Delete Configuration
curl -X DELETE http://localhost:8082/api/v1/configurations/GENERAL/school.name
```

## 13. Integration with Frontend

### Axios Configuration

```typescript
// src/api/axios.config.ts
import axios from 'axios';

const studentApi = axios.create({
  baseURL: 'http://localhost:8081/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

const configApi = axios.create({
  baseURL: 'http://localhost:8082/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for correlation ID
studentApi.interceptors.request.use((config) => {
  config.headers['X-Correlation-ID'] = crypto.randomUUID();
  return config;
});

// Response interceptor for error handling
studentApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data?.type) {
      // RFC 7807 error format
      return Promise.reject(error.response.data);
    }
    return Promise.reject(error);
  }
);

export { studentApi, configApi };
```

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED
