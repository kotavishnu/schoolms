# API Specification
**School Management System - Phase 1**

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [API Design Principles](#api-design-principles)
3. [Student Service API](#student-service-api)
4. [Configuration Service API](#configuration-service-api)
5. [Common Patterns](#common-patterns)
6. [Error Handling](#error-handling)
7. [Request/Response Examples](#requestresponse-examples)

---

## Overview

### API Style
- **Architecture**: RESTful HTTP APIs
- **Data Format**: JSON (application/json)
- **Versioning**: URI versioning (`/api/v1/...`)
- **Documentation**: OpenAPI 3.0 (Swagger UI)

### Base URLs

| Service | Development | Production |
|---------|-------------|------------|
| Student Service | `http://localhost:8081` | `https://students.schoolms.com` |
| Configuration Service | `http://localhost:8082` | `https://config.schoolms.com` |

### API Documentation

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

---

## API Design Principles

### 1. Resource-Based URLs

**Good Examples**:
```
GET    /api/v1/students           # List all students
GET    /api/v1/students/{id}      # Get specific student
POST   /api/v1/students           # Create student
PATCH  /api/v1/students/{id}      # Update student
DELETE /api/v1/students/{id}      # Delete student
```

**Bad Examples** (Avoid):
```
GET    /api/v1/getStudents        # Verb in URL
POST   /api/v1/student/create     # Redundant
GET    /api/v1/students/list      # Redundant
```

---

### 2. HTTP Method Semantics

| Method | Purpose | Idempotent | Safe | Request Body | Response Body |
|--------|---------|------------|------|--------------|---------------|
| GET | Retrieve resource(s) | Yes | Yes | No | Yes |
| POST | Create new resource | No | No | Yes | Yes (created resource) |
| PATCH | Partial update | No | No | Yes | Yes (updated resource) |
| DELETE | Remove resource | Yes | No | No | No (204) or Yes (200) |

**PATCH vs PUT**:
- Use PATCH for partial updates (only send fields to change)
- PUT would require sending the entire resource

---

### 3. HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 OK | Success | GET, PATCH successful |
| 201 Created | Resource created | POST successful |
| 204 No Content | Success, no body | DELETE successful |
| 400 Bad Request | Client error | Validation failure |
| 404 Not Found | Resource missing | GET/PATCH/DELETE on non-existent ID |
| 409 Conflict | State conflict | Duplicate unique field |
| 422 Unprocessable Entity | Business rule violation | Age validation, capacity exceeded |
| 500 Internal Server Error | Server error | Unexpected exception |

---

### 4. Request Headers

**Required Headers**:
```http
Content-Type: application/json
Accept: application/json
```

**Optional Headers** (Future):
```http
Authorization: Bearer <token>
X-Request-ID: <uuid>
```

---

### 5. Response Structure

**Success Response**:
```json
{
  "id": "STU-2026-00001",
  "firstName": "John",
  "lastName": "Doe",
  ...
}
```

**Error Response (RFC 7807)**:
```json
{
  "type": "https://api.schoolms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Phone number is already registered",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T10:30:00.123Z",
  "traceId": "abc123def456",
  "errors": {
    "phone": ["Phone number '1234567890' is already in use"]
  }
}
```

---

## Student Service API

**Base Path**: `/api/v1/students`

---

### 1. List All Students

**Endpoint**: `GET /api/v1/students`

**Description**: Retrieve all students with optional filtering

**Query Parameters**:

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `search` | String | No | Search by ID, name, or guardian | `John` |
| `status` | Enum | No | Filter by status: ACTIVE, INACTIVE, ALL | `ACTIVE` |
| `page` | Integer | No | Page number (0-indexed) | `0` |
| `size` | Integer | No | Page size (default: 20, max: 100) | `20` |
| `sort` | String | No | Sort field and direction | `lastName,asc` |

**Request Example**:
```http
GET /api/v1/students?search=John&status=ACTIVE&page=0&size=20 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response (200 OK)**:
```json
{
  "students": [
    {
      "id": "STU-2026-00001",
      "firstName": "John",
      "lastName": "Doe",
      "dateOfBirth": "2015-05-15",
      "age": 10,
      "adhaarNumber": "123456789012",
      "address": "123 Main Street, City, State - 123456",
      "identificationMarks": "Mole on left cheek",
      "guardianName": "Robert Doe",
      "motherName": "Jane Doe",
      "phone": "9876543210",
      "email": "john.doe@example.com",
      "status": "ACTIVE",
      "createdAt": "2026-01-08T10:00:00.000Z",
      "updatedAt": "2026-01-08T10:00:00.000Z"
    }
  ],
  "totalCount": 45,
  "activeCount": 42,
  "inactiveCount": 3,
  "page": 0,
  "size": 20,
  "totalPages": 3
}
```

**Response (200 OK - Empty)**:
```json
{
  "students": [],
  "totalCount": 0,
  "activeCount": 0,
  "inactiveCount": 0,
  "page": 0,
  "size": 20,
  "totalPages": 0
}
```

---

### 2. Get Student by ID

**Endpoint**: `GET /api/v1/students/{id}`

**Description**: Retrieve a specific student by student ID

**Path Parameters**:

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `id` | String | Yes | Student ID | `STU-2026-00001` |

**Request Example**:
```http
GET /api/v1/students/STU-2026-00001 HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response (200 OK)**:
```json
{
  "id": "STU-2026-00001",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2015-05-15",
  "age": 10,
  "adhaarNumber": "123456789012",
  "address": "123 Main Street, City, State - 123456",
  "identificationMarks": "Mole on left cheek",
  "guardianName": "Robert Doe",
  "motherName": "Jane Doe",
  "phone": "9876543210",
  "email": "john.doe@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-01-08T10:00:00.000Z",
  "updatedAt": "2026-01-08T10:00:00.000Z"
}
```

**Response (404 Not Found)**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with ID 'STU-2026-99999' not found",
  "instance": "/api/v1/students/STU-2026-99999",
  "timestamp": "2026-01-08T10:30:00.123Z",
  "traceId": "abc123def456"
}
```

---

### 3. Create Student

**Endpoint**: `POST /api/v1/students`

**Description**: Register a new student

**Request Body Schema**:

```typescript
{
  firstName: string;        // Required, 1-50 chars
  lastName: string;         // Required, 1-50 chars
  dateOfBirth: string;      // Required, ISO 8601 date (YYYY-MM-DD), age 3-18
  adhaarNumber: string;     // Required, 12 digits, unique
  address: string;          // Required, 10-500 chars
  identificationMarks?: string;  // Optional, 0-200 chars
  guardianName: string;     // Required, 1-100 chars
  motherName: string;       // Required, 1-100 chars
  phone: string;            // Required, 10 digits, unique
  email: string;            // Required, valid email, unique
}
```

**Request Example**:
```http
POST /api/v1/students HTTP/1.1
Host: localhost:8081
Content-Type: application/json
Accept: application/json

{
  "firstName": "Emily",
  "lastName": "Smith",
  "dateOfBirth": "2016-03-20",
  "adhaarNumber": "987654321098",
  "address": "456 Oak Avenue, Springfield, IL - 62701",
  "identificationMarks": "Scar on right knee",
  "guardianName": "Michael Smith",
  "motherName": "Sarah Smith",
  "phone": "8765432109",
  "email": "emily.smith@example.com"
}
```

**Response (201 Created)**:
```http
HTTP/1.1 201 Created
Location: /api/v1/students/STU-2026-00002
Content-Type: application/json

{
  "id": "STU-2026-00002",
  "firstName": "Emily",
  "lastName": "Smith",
  "dateOfBirth": "2016-03-20",
  "age": 9,
  "adhaarNumber": "987654321098",
  "address": "456 Oak Avenue, Springfield, IL - 62701",
  "identificationMarks": "Scar on right knee",
  "guardianName": "Michael Smith",
  "motherName": "Sarah Smith",
  "phone": "8765432109",
  "email": "emily.smith@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-01-08T11:15:30.456Z",
  "updatedAt": "2026-01-08T11:15:30.456Z"
}
```

**Response (400 Bad Request - Validation Error)**:
```json
{
  "type": "https://api.schoolms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T11:20:00.123Z",
  "traceId": "def456ghi789",
  "errors": {
    "firstName": ["First name is required"],
    "phone": ["Phone number must be exactly 10 digits"],
    "email": ["Invalid email format"]
  }
}
```

**Response (422 Unprocessable Entity - Business Rule Violation)**:
```json
{
  "type": "https://api.schoolms.com/problems/business-rule-violation",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "Student age must be between 3 and 18 years at registration",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T11:25:00.123Z",
  "traceId": "ghi789jkl012",
  "errors": {
    "dateOfBirth": ["Age calculated as 2 years, must be at least 3 years"]
  }
}
```

**Response (409 Conflict - Duplicate)**:
```json
{
  "type": "https://api.schoolms.com/problems/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Phone number is already registered to another student",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T11:30:00.123Z",
  "traceId": "jkl012mno345",
  "errors": {
    "phone": ["Phone number '8765432109' is already in use by student STU-2026-00001"]
  }
}
```

---

### 4. Update Student

**Endpoint**: `PATCH /api/v1/students/{id}`

**Description**: Partially update student (only allowed fields)

**Path Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | String | Yes | Student ID |

**Request Body Schema** (All fields optional, only send fields to update):

```typescript
{
  firstName?: string;    // 1-50 chars
  lastName?: string;     // 1-50 chars
  phone?: string;        // 10 digits, unique
  status?: string;       // ACTIVE or INACTIVE
}
```

**Immutable Fields** (will be rejected if included):
- `dateOfBirth`
- `adhaarNumber`
- `email`
- `guardianName`
- `motherName`
- `address`
- `identificationMarks`

**Request Example**:
```http
PATCH /api/v1/students/STU-2026-00001 HTTP/1.1
Host: localhost:8081
Content-Type: application/json
Accept: application/json

{
  "firstName": "Jonathan",
  "phone": "9999888877",
  "status": "INACTIVE"
}
```

**Response (200 OK)**:
```json
{
  "id": "STU-2026-00001",
  "firstName": "Jonathan",
  "lastName": "Doe",
  "dateOfBirth": "2015-05-15",
  "age": 10,
  "adhaarNumber": "123456789012",
  "address": "123 Main Street, City, State - 123456",
  "identificationMarks": "Mole on left cheek",
  "guardianName": "Robert Doe",
  "motherName": "Jane Doe",
  "phone": "9999888877",
  "email": "john.doe@example.com",
  "status": "INACTIVE",
  "createdAt": "2026-01-08T10:00:00.000Z",
  "updatedAt": "2026-01-08T12:00:00.000Z"
}
```

**Response (400 Bad Request - Immutable Field)**:
```json
{
  "type": "https://api.schoolms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Cannot update immutable fields",
  "instance": "/api/v1/students/STU-2026-00001",
  "timestamp": "2026-01-08T12:05:00.123Z",
  "traceId": "mno345pqr678",
  "errors": {
    "dateOfBirth": ["Date of birth cannot be modified after registration"],
    "email": ["Email cannot be modified after registration"]
  }
}
```

**Response (404 Not Found)**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with ID 'STU-2026-99999' not found",
  "instance": "/api/v1/students/STU-2026-99999",
  "timestamp": "2026-01-08T12:10:00.123Z",
  "traceId": "pqr678stu901"
}
```

**Response (409 Conflict - Optimistic Lock)**:
```json
{
  "type": "https://api.schoolms.com/problems/optimistic-lock-failure",
  "title": "Concurrent Modification",
  "status": 409,
  "detail": "Student was modified by another user. Please refresh and try again.",
  "instance": "/api/v1/students/STU-2026-00001",
  "timestamp": "2026-01-08T12:15:00.123Z",
  "traceId": "stu901vwx234"
}
```

---

### 5. Delete Student

**Endpoint**: `DELETE /api/v1/students/{id}`

**Description**: Delete a student record

**Path Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | String | Yes | Student ID |

**Request Example**:
```http
DELETE /api/v1/students/STU-2026-00001 HTTP/1.1
Host: localhost:8081
```

**Response (204 No Content)**:
```http
HTTP/1.1 204 No Content
```

**Response (404 Not Found)**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with ID 'STU-2026-99999' not found",
  "instance": "/api/v1/students/STU-2026-99999",
  "timestamp": "2026-01-08T12:20:00.123Z",
  "traceId": "vwx234yza567"
}
```

---

### 6. Get Student Statistics

**Endpoint**: `GET /api/v1/students/statistics`

**Description**: Retrieve dashboard statistics

**Request Example**:
```http
GET /api/v1/students/statistics HTTP/1.1
Host: localhost:8081
Accept: application/json
```

**Response (200 OK)**:
```json
{
  "totalStudents": 45,
  "activeStudents": 42,
  "inactiveStudents": 3,
  "registeredToday": 3,
  "registeredThisWeek": 12,
  "registeredThisMonth": 45,
  "lastUpdated": "2026-01-08T12:30:00.123Z"
}
```

---

### 7. Validate Phone Uniqueness

**Endpoint**: `POST /api/v1/students/validate-phone`

**Description**: Check if phone number is available (for async form validation)

**Request Body**:
```json
{
  "phone": "9876543210",
  "excludeStudentId": "STU-2026-00001"  // Optional: exclude this student from check (for edit)
}
```

**Response (200 OK - Available)**:
```json
{
  "isUnique": true,
  "phone": "9876543210"
}
```

**Response (200 OK - Already in Use)**:
```json
{
  "isUnique": false,
  "phone": "9876543210",
  "existingStudentId": "STU-2026-00005"
}
```

---

## Configuration Service API

**Base Path**: `/api/v1/configurations`

---

### 1. List All Configurations

**Endpoint**: `GET /api/v1/configurations`

**Description**: Retrieve all configurations with optional filtering

**Query Parameters**:

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `category` | Enum | No | Filter by category: GENERAL, ACADEMIC, FINANCE, SYSTEM | `GENERAL` |

**Request Example**:
```http
GET /api/v1/configurations?category=GENERAL HTTP/1.1
Host: localhost:8082
Accept: application/json
```

**Response (200 OK)**:
```json
{
  "configurations": [
    {
      "id": "1",
      "category": "GENERAL",
      "key": "SCHOOL_NAME",
      "value": "St. Mary's High School",
      "description": "Official school name",
      "createdAt": "2026-01-01T00:00:00.000Z",
      "lastUpdated": "2026-01-01T00:00:00.000Z"
    },
    {
      "id": "2",
      "category": "GENERAL",
      "key": "SCHOOL_CODE",
      "value": "SMHS-2026",
      "description": "Unique school identifier",
      "createdAt": "2026-01-01T00:00:00.000Z",
      "lastUpdated": "2026-01-01T00:00:00.000Z"
    }
  ],
  "totalCount": 2
}
```

---

### 2. Get Configuration by ID

**Endpoint**: `GET /api/v1/configurations/{id}`

**Description**: Retrieve a specific configuration

**Path Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | String | Yes | Configuration ID |

**Request Example**:
```http
GET /api/v1/configurations/1 HTTP/1.1
Host: localhost:8082
Accept: application/json
```

**Response (200 OK)**:
```json
{
  "id": "1",
  "category": "GENERAL",
  "key": "SCHOOL_NAME",
  "value": "St. Mary's High School",
  "description": "Official school name",
  "createdAt": "2026-01-01T00:00:00.000Z",
  "lastUpdated": "2026-01-01T00:00:00.000Z"
}
```

**Response (404 Not Found)**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Configuration with ID '999' not found",
  "instance": "/api/v1/configurations/999",
  "timestamp": "2026-01-08T13:00:00.123Z",
  "traceId": "abc123xyz789"
}
```

---

### 3. Create Configuration

**Endpoint**: `POST /api/v1/configurations`

**Description**: Create a new configuration setting

**Request Body Schema**:

```typescript
{
  category: string;       // Required, GENERAL|ACADEMIC|FINANCE|SYSTEM
  key: string;            // Required, 1-100 chars, uppercase/numbers/underscores
  value: string;          // Required, 1-1000 chars
  description?: string;   // Optional, 0-500 chars
}
```

**Request Example**:
```http
POST /api/v1/configurations HTTP/1.1
Host: localhost:8082
Content-Type: application/json
Accept: application/json

{
  "category": "ACADEMIC",
  "key": "STUDENT_CAPACITY",
  "value": "500",
  "description": "Maximum number of students the school can accommodate"
}
```

**Response (201 Created)**:
```http
HTTP/1.1 201 Created
Location: /api/v1/configurations/15
Content-Type: application/json

{
  "id": "15",
  "category": "ACADEMIC",
  "key": "STUDENT_CAPACITY",
  "value": "500",
  "description": "Maximum number of students the school can accommodate",
  "createdAt": "2026-01-08T13:10:00.456Z",
  "lastUpdated": "2026-01-08T13:10:00.456Z"
}
```

**Response (400 Bad Request - Validation Error)**:
```json
{
  "type": "https://api.schoolms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/configurations",
  "timestamp": "2026-01-08T13:15:00.123Z",
  "traceId": "def456uvw901",
  "errors": {
    "key": ["Key must contain only uppercase letters, numbers, and underscores"],
    "value": ["Value is required and cannot be empty"]
  }
}
```

**Response (409 Conflict - Duplicate Key)**:
```json
{
  "type": "https://api.schoolms.com/problems/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Configuration key already exists in this category",
  "instance": "/api/v1/configurations",
  "timestamp": "2026-01-08T13:20:00.123Z",
  "traceId": "ghi789rst234",
  "errors": {
    "key": ["Key 'STUDENT_CAPACITY' already exists in category 'ACADEMIC'"]
  }
}
```

---

### 4. Update Configuration

**Endpoint**: `PATCH /api/v1/configurations/{id}`

**Description**: Update configuration (typically only value and description)

**Path Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | String | Yes | Configuration ID |

**Request Body Schema** (All fields optional):

```typescript
{
  value?: string;         // 1-1000 chars
  description?: string;   // 0-500 chars
}
```

**Note**: `category` and `key` are typically immutable (delete and recreate instead)

**Request Example**:
```http
PATCH /api/v1/configurations/15 HTTP/1.1
Host: localhost:8082
Content-Type: application/json
Accept: application/json

{
  "value": "600",
  "description": "Updated maximum capacity based on new building construction"
}
```

**Response (200 OK)**:
```json
{
  "id": "15",
  "category": "ACADEMIC",
  "key": "STUDENT_CAPACITY",
  "value": "600",
  "description": "Updated maximum capacity based on new building construction",
  "createdAt": "2026-01-08T13:10:00.456Z",
  "lastUpdated": "2026-01-08T13:30:00.789Z"
}
```

---

### 5. Delete Configuration

**Endpoint**: `DELETE /api/v1/configurations/{id}`

**Description**: Delete a configuration setting

**Path Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | String | Yes | Configuration ID |

**Request Example**:
```http
DELETE /api/v1/configurations/15 HTTP/1.1
Host: localhost:8082
```

**Response (204 No Content)**:
```http
HTTP/1.1 204 No Content
```

**Response (404 Not Found)**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Configuration with ID '999' not found",
  "instance": "/api/v1/configurations/999",
  "timestamp": "2026-01-08T13:40:00.123Z",
  "traceId": "jkl012mno345"
}
```

---

## Common Patterns

### Pagination

**Query Parameters**:
- `page`: Page number (0-indexed), default: 0
- `size`: Page size, default: 20, max: 100
- `sort`: Sort field and direction, e.g., `lastName,asc` or `createdAt,desc`

**Response Structure**:
```json
{
  "content": [ /* array of items */ ],
  "page": 0,
  "size": 20,
  "totalElements": 145,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

---

### Filtering

**Single Filter**:
```
GET /api/v1/students?status=ACTIVE
```

**Multiple Filters** (AND logic):
```
GET /api/v1/students?status=ACTIVE&search=John
```

---

### Sorting

**Single Field**:
```
GET /api/v1/students?sort=lastName,asc
```

**Multiple Fields**:
```
GET /api/v1/students?sort=status,desc&sort=lastName,asc
```

---

### Search

**Full-text search**:
```
GET /api/v1/students?search=John
```

Searches across:
- Student ID
- First name
- Last name
- Guardian name

**Backend Implementation**: Use PostgreSQL full-text search (tsvector)

---

## Error Handling

### RFC 7807 Problem Details

All errors follow the RFC 7807 standard for consistent error responses.

**Schema**:
```typescript
{
  type: string;           // URI identifying the problem type
  title: string;          // Short, human-readable summary
  status: number;         // HTTP status code
  detail: string;         // Detailed explanation
  instance: string;       // URI identifying the specific request
  timestamp: string;      // ISO 8601 timestamp
  traceId: string;        // Correlation ID for tracing
  errors?: object;        // Field-level errors (validation)
}
```

---

### Error Types

#### 1. Validation Error (400 Bad Request)

**Type**: `https://api.schoolms.com/problems/validation-error`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T14:00:00.123Z",
  "traceId": "abc123",
  "errors": {
    "firstName": ["First name is required", "First name cannot exceed 50 characters"],
    "phone": ["Phone number must be exactly 10 digits"]
  }
}
```

---

#### 2. Not Found (404)

**Type**: `https://api.schoolms.com/problems/not-found`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with ID 'STU-2026-99999' not found",
  "instance": "/api/v1/students/STU-2026-99999",
  "timestamp": "2026-01-08T14:05:00.123Z",
  "traceId": "def456"
}
```

---

#### 3. Duplicate Resource (409 Conflict)

**Type**: `https://api.schoolms.com/problems/duplicate-resource`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Phone number is already registered",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T14:10:00.123Z",
  "traceId": "ghi789",
  "errors": {
    "phone": ["Phone number '9876543210' is already in use"]
  }
}
```

---

#### 4. Business Rule Violation (422 Unprocessable Entity)

**Type**: `https://api.schoolms.com/problems/business-rule-violation`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/business-rule-violation",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "Student age must be between 3 and 18 years",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T14:15:00.123Z",
  "traceId": "jkl012",
  "errors": {
    "dateOfBirth": ["Age calculated as 2 years, minimum is 3 years"]
  }
}
```

---

#### 5. Optimistic Lock Failure (409 Conflict)

**Type**: `https://api.schoolms.com/problems/optimistic-lock-failure`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/optimistic-lock-failure",
  "title": "Concurrent Modification",
  "status": 409,
  "detail": "Resource was modified by another user",
  "instance": "/api/v1/students/STU-2026-00001",
  "timestamp": "2026-01-08T14:20:00.123Z",
  "traceId": "mno345"
}
```

---

#### 6. Internal Server Error (500)

**Type**: `https://api.schoolms.com/problems/internal-server-error`

**Example**:
```json
{
  "type": "https://api.schoolms.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred. Please try again later.",
  "instance": "/api/v1/students",
  "timestamp": "2026-01-08T14:25:00.123Z",
  "traceId": "pqr678"
}
```

**Note**: Never expose stack traces or sensitive information in production

---

## Request/Response Examples

### Complete Student Lifecycle

#### 1. Create Student

**Request**:
```http
POST /api/v1/students HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "firstName": "Alice",
  "lastName": "Johnson",
  "dateOfBirth": "2014-07-10",
  "adhaarNumber": "111222333444",
  "address": "789 Elm Street, Boston, MA - 02101",
  "identificationMarks": null,
  "guardianName": "David Johnson",
  "motherName": "Emma Johnson",
  "phone": "7777666655",
  "email": "alice.johnson@example.com"
}
```

**Response**:
```json
{
  "id": "STU-2026-00010",
  "firstName": "Alice",
  "lastName": "Johnson",
  "dateOfBirth": "2014-07-10",
  "age": 11,
  "adhaarNumber": "111222333444",
  "address": "789 Elm Street, Boston, MA - 02101",
  "identificationMarks": null,
  "guardianName": "David Johnson",
  "motherName": "Emma Johnson",
  "phone": "7777666655",
  "email": "alice.johnson@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-01-08T15:00:00.000Z",
  "updatedAt": "2026-01-08T15:00:00.000Z"
}
```

---

#### 2. Update Student

**Request**:
```http
PATCH /api/v1/students/STU-2026-00010 HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "phone": "8888777766",
  "status": "INACTIVE"
}
```

**Response**:
```json
{
  "id": "STU-2026-00010",
  "firstName": "Alice",
  "lastName": "Johnson",
  "dateOfBirth": "2014-07-10",
  "age": 11,
  "adhaarNumber": "111222333444",
  "address": "789 Elm Street, Boston, MA - 02101",
  "identificationMarks": null,
  "guardianName": "David Johnson",
  "motherName": "Emma Johnson",
  "phone": "8888777766",
  "email": "alice.johnson@example.com",
  "status": "INACTIVE",
  "createdAt": "2026-01-08T15:00:00.000Z",
  "updatedAt": "2026-01-08T15:30:00.000Z"
}
```

---

#### 3. Search Students

**Request**:
```http
GET /api/v1/students?search=Johnson&status=ALL HTTP/1.1
Host: localhost:8081
```

**Response**:
```json
{
  "students": [
    {
      "id": "STU-2026-00010",
      "firstName": "Alice",
      "lastName": "Johnson",
      "guardianName": "David Johnson",
      "phone": "8888777766",
      "status": "INACTIVE",
      "createdAt": "2026-01-08T15:00:00.000Z"
    }
  ],
  "totalCount": 1,
  "activeCount": 0,
  "inactiveCount": 1
}
```

---

#### 4. Delete Student

**Request**:
```http
DELETE /api/v1/students/STU-2026-00010 HTTP/1.1
Host: localhost:8081
```

**Response**:
```http
HTTP/1.1 204 No Content
```

---

## OpenAPI 3.0 Specification

**Partial OpenAPI YAML** (for reference):

```yaml
openapi: 3.0.3
info:
  title: Student Service API
  version: 1.0.0
  description: School Management System - Student Service

servers:
  - url: http://localhost:8081
    description: Development server
  - url: https://students.schoolms.com
    description: Production server

paths:
  /api/v1/students:
    get:
      summary: List all students
      operationId: listStudents
      parameters:
        - name: search
          in: query
          schema:
            type: string
        - name: status
          in: query
          schema:
            type: string
            enum: [ACTIVE, INACTIVE, ALL]
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentListResponse'

    post:
      summary: Create new student
      operationId: createStudent
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateStudentRequest'
      responses:
        '201':
          description: Student created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentResponse'
        '400':
          description: Validation error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProblemDetail'

components:
  schemas:
    StudentResponse:
      type: object
      properties:
        id:
          type: string
          example: STU-2026-00001
        firstName:
          type: string
        # ... other fields
```

---

## Summary

This API specification provides:

1. **RESTful Design**: Resource-based URLs, proper HTTP methods
2. **Consistency**: Standardized request/response formats
3. **Error Handling**: RFC 7807 problem details for all errors
4. **Validation**: Clear validation rules and error messages
5. **Filtering**: Comprehensive search and filter capabilities
6. **Pagination**: Efficient handling of large result sets
7. **Documentation**: OpenAPI 3.0 compatible (Swagger UI)
8. **Security**: CORS configuration, input validation
9. **Observability**: Trace IDs in all responses
10. **Frontend Alignment**: APIs match frontend data models and requirements

---

**Next Steps**: Proceed to `04-security-architecture.md` for authentication and authorization specifications.
