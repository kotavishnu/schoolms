# API Design Specification - School Management System (SMS)

## 1. Overview

This document defines the complete RESTful API specification for the School Management System. The API follows REST principles, OpenAPI 3.0 standards, and industry best practices.

### 1.1 API Design Principles
- **RESTful**: Resource-based URLs, standard HTTP methods
- **Stateless**: Each request contains all necessary information
- **Consistent**: Uniform response structure and error handling
- **Versioned**: API versioning via URL path (`/api/v1/...`)
- **Secure**: HTTPS only, CORS configured, prepared for JWT auth
- **Documented**: OpenAPI 3.0 specification for auto-generated docs

### 1.2 Base URL Structure
```
Production:  https://api.sms.school.com/api/v1
Development: http://localhost:8080/api/v1
```

### 1.3 API Gateway Routing
```
/api/v1/students/**        → Student Service (Port 8081)
/api/v1/school/**          → Configuration Service (Port 8082)
/api/v1/configurations/**  → Configuration Service (Port 8082)
```

---

## 2. Common Specifications

### 2.1 HTTP Methods
| Method | Usage | Idempotent | Safe |
|--------|-------|------------|------|
| GET | Retrieve resource(s) | Yes | Yes |
| POST | Create new resource | No | No |
| PUT | Update entire resource | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Remove resource | Yes | No |

### 2.2 Standard Headers

#### Request Headers
```http
Content-Type: application/json
Accept: application/json
X-Correlation-ID: a1b2c3d4-e5f6-7890 (auto-generated if not provided)
Authorization: Bearer {jwt_token} (Phase 2)
```

#### Response Headers
```http
Content-Type: application/json
X-Correlation-ID: a1b2c3d4-e5f6-7890 (echoed from request)
Location: /api/v1/students/STU-2025-0001 (for POST/PUT)
```

### 2.3 HTTP Status Codes

#### Success Codes
| Code | Meaning | Usage |
|------|---------|-------|
| 200 OK | Success | GET, PUT successful |
| 201 Created | Resource created | POST successful |
| 204 No Content | Success, no body | DELETE successful |

#### Client Error Codes
| Code | Meaning | Usage |
|------|---------|-------|
| 400 Bad Request | Invalid input | Validation errors |
| 401 Unauthorized | Not authenticated | Missing/invalid JWT (Phase 2) |
| 403 Forbidden | Not authorized | Insufficient permissions |
| 404 Not Found | Resource not found | Invalid ID/key |
| 409 Conflict | Resource conflict | Duplicate mobile number |
| 422 Unprocessable Entity | Business rule violation | Age validation failure |

#### Server Error Codes
| Code | Meaning | Usage |
|------|---------|-------|
| 500 Internal Server Error | Unexpected error | Unhandled exceptions |
| 503 Service Unavailable | Service down | Maintenance mode |

### 2.4 Error Response Format (RFC 7807)

All error responses follow the Problem Details standard:

```json
{
  "type": "https://api.sms.com/problems/{error-type}",
  "title": "Human-readable title",
  "status": 400,
  "detail": "Detailed error message",
  "instance": "/api/v1/students",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:15:30Z",
  "errors": [
    {
      "field": "dateOfBirth",
      "message": "Student must be between 3 and 18 years old",
      "rejectedValue": "2022-01-01",
      "code": "AGE_OUT_OF_RANGE"
    }
  ]
}
```

#### Error Types
| Type | HTTP Status | Description |
|------|-------------|-------------|
| validation-error | 400 | Input validation failure |
| not-found | 404 | Resource not found |
| duplicate-resource | 409 | Unique constraint violation |
| business-rule-violation | 422 | Business logic constraint |
| internal-error | 500 | Unexpected server error |

---

## 3. Student Management API

Base Path: `/api/v1/students`

### 3.1 Create Student

**Endpoint:** `POST /api/v1/students`

**Description:** Register a new student with auto-generated student ID.

**Request Body:**
```json
{
  "firstName": "Rahul",
  "lastName": "Sharma",
  "dateOfBirth": "2012-05-15",
  "mobile": "+919876543210",
  "email": "rahul.sharma@example.com",
  "address": "123 Main Street, City, State - 123456",
  "fatherNameOrGuardian": "Raj Sharma",
  "motherName": "Priya Sharma",
  "identificationMark": "Mole on left cheek",
  "adhaarNumber": "123456789012"
}
```

**Request Schema:**
```json
{
  "type": "object",
  "required": ["firstName", "lastName", "dateOfBirth", "mobile"],
  "properties": {
    "firstName": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100,
      "description": "Student's first name"
    },
    "lastName": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100,
      "description": "Student's last name"
    },
    "dateOfBirth": {
      "type": "string",
      "format": "date",
      "description": "Birth date (YYYY-MM-DD), age must be 3-18"
    },
    "mobile": {
      "type": "string",
      "pattern": "^\\+?[0-9]{10,15}$",
      "description": "Contact number, must be unique"
    },
    "email": {
      "type": "string",
      "format": "email",
      "maxLength": 100,
      "description": "Email address (optional, unique if provided)"
    },
    "address": {
      "type": "string",
      "maxLength": 1000,
      "description": "Residential address"
    },
    "fatherNameOrGuardian": {
      "type": "string",
      "maxLength": 100,
      "description": "Father or guardian name"
    },
    "motherName": {
      "type": "string",
      "maxLength": 100,
      "description": "Mother's name"
    },
    "identificationMark": {
      "type": "string",
      "maxLength": 200,
      "description": "Physical identification mark"
    },
    "adhaarNumber": {
      "type": "string",
      "pattern": "^[0-9]{12}$",
      "description": "12-digit Adhaar number (optional, unique if provided)"
    }
  }
}
```

**Success Response (201 Created):**
```json
{
  "studentId": 1,
  "studentKey": "STU-2025-0001",
  "firstName": "Rahul",
  "lastName": "Sharma",
  "dateOfBirth": "2012-05-15",
  "age": 13,
  "mobile": "+919876543210",
  "email": "rahul.sharma@example.com",
  "address": "123 Main Street, City, State - 123456",
  "fatherNameOrGuardian": "Raj Sharma",
  "motherName": "Priya Sharma",
  "identificationMark": "Mole on left cheek",
  "adhaarNumber": "123456789012",
  "status": "Active",
  "createdAt": "2025-12-08T10:15:30Z",
  "updatedAt": "2025-12-08T10:15:30Z"
}
```

**Headers:**
```http
Location: /api/v1/students/STU-2025-0001
X-Correlation-ID: a1b2c3d4-e5f6-7890
```

**Error Responses:**

*400 Bad Request - Validation Error:*
```json
{
  "type": "https://api.sms.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/students",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:15:30Z",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number must be 10-15 digits",
      "rejectedValue": "123",
      "code": "INVALID_FORMAT"
    }
  ]
}
```

*409 Conflict - Duplicate Mobile:*
```json
{
  "type": "https://api.sms.com/problems/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "A student with this mobile number already exists",
  "instance": "/api/v1/students",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:15:30Z",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number already registered",
      "rejectedValue": "+919876543210",
      "code": "DUPLICATE_MOBILE"
    }
  ]
}
```

*422 Unprocessable Entity - Age Violation:*
```json
{
  "type": "https://api.sms.com/problems/business-rule-violation",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "Student age must be between 3 and 18 years",
  "instance": "/api/v1/students",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:15:30Z",
  "errors": [
    {
      "field": "dateOfBirth",
      "message": "Student must be between 3 and 18 years old (current age: 2)",
      "rejectedValue": "2023-01-01",
      "code": "AGE_OUT_OF_RANGE"
    }
  ]
}
```

---

### 3.2 Get Student by Key

**Endpoint:** `GET /api/v1/students/{studentKey}`

**Description:** Retrieve a specific student by their business key.

**Path Parameters:**
- `studentKey` (required): Student business key (e.g., STU-2025-0001)

**Success Response (200 OK):**
```json
{
  "studentId": 1,
  "studentKey": "STU-2025-0001",
  "firstName": "Rahul",
  "lastName": "Sharma",
  "dateOfBirth": "2012-05-15",
  "age": 13,
  "mobile": "+919876543210",
  "email": "rahul.sharma@example.com",
  "address": "123 Main Street, City, State - 123456",
  "fatherNameOrGuardian": "Raj Sharma",
  "motherName": "Priya Sharma",
  "identificationMark": "Mole on left cheek",
  "adhaarNumber": "123456789012",
  "status": "Active",
  "createdAt": "2025-12-08T10:15:30Z",
  "updatedAt": "2025-12-08T10:15:30Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.sms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with key 'STU-2025-9999' not found",
  "instance": "/api/v1/students/STU-2025-9999",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:15:30Z"
}
```

---

### 3.3 Search Students

**Endpoint:** `GET /api/v1/students`

**Description:** Search and list students with filtering, pagination, and sorting.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| lastName | string | No | - | Filter by last name (case-insensitive, partial match) |
| guardianName | string | No | - | Filter by guardian name (case-insensitive, partial match) |
| status | string | No | - | Filter by status (Active/Inactive) |
| page | integer | No | 0 | Page number (0-indexed) |
| size | integer | No | 20 | Items per page (max 100) |
| sort | string | No | lastName,asc | Sort format: {field},{direction} |

**Supported Sort Fields:**
- `lastName`, `firstName`, `studentKey`, `createdAt`, `status`

**Example Requests:**
```
GET /api/v1/students?lastName=Sharma
GET /api/v1/students?guardianName=Kumar&status=Active
GET /api/v1/students?page=0&size=10&sort=lastName,asc
GET /api/v1/students?lastName=Patel&page=1&size=20&sort=createdAt,desc
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "studentId": 1,
      "studentKey": "STU-2025-0001",
      "firstName": "Rahul",
      "lastName": "Sharma",
      "mobile": "+919876543210",
      "fatherNameOrGuardian": "Raj Sharma",
      "status": "Active",
      "createdAt": "2025-12-08T10:15:30Z"
    },
    {
      "studentId": 4,
      "studentKey": "STU-2025-0004",
      "firstName": "Priya",
      "lastName": "Sharma",
      "mobile": "+919876543213",
      "fatherNameOrGuardian": "Suresh Sharma",
      "status": "Active",
      "createdAt": "2025-12-08T11:20:15Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "empty": false
}
```

**Note:** Response uses Spring Data's `Page` structure for consistency.

---

### 3.4 Update Student

**Endpoint:** `PUT /api/v1/students/{studentKey}`

**Description:** Update student profile (only name, mobile, and status allowed).

**Path Parameters:**
- `studentKey` (required): Student business key

**Request Body:**
```json
{
  "firstName": "Rahul",
  "lastName": "Sharma",
  "mobile": "+919876543299",
  "status": "Active"
}
```

**Request Schema:**
```json
{
  "type": "object",
  "required": ["firstName", "lastName", "mobile", "status"],
  "properties": {
    "firstName": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100
    },
    "lastName": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100
    },
    "mobile": {
      "type": "string",
      "pattern": "^\\+?[0-9]{10,15}$"
    },
    "status": {
      "type": "string",
      "enum": ["Active", "Inactive"]
    }
  }
}
```

**Success Response (200 OK):**
```json
{
  "studentId": 1,
  "studentKey": "STU-2025-0001",
  "firstName": "Rahul",
  "lastName": "Sharma",
  "dateOfBirth": "2012-05-15",
  "age": 13,
  "mobile": "+919876543299",
  "email": "rahul.sharma@example.com",
  "address": "123 Main Street, City, State - 123456",
  "fatherNameOrGuardian": "Raj Sharma",
  "motherName": "Priya Sharma",
  "identificationMark": "Mole on left cheek",
  "adhaarNumber": "123456789012",
  "status": "Active",
  "createdAt": "2025-12-08T10:15:30Z",
  "updatedAt": "2025-12-08T14:25:45Z"
}
```

**Error Response (409 Conflict - Optimistic Lock):**
```json
{
  "type": "https://api.sms.com/problems/optimistic-lock-exception",
  "title": "Concurrent Modification",
  "status": 409,
  "detail": "Student record was modified by another user. Please refresh and try again.",
  "instance": "/api/v1/students/STU-2025-0001",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T14:25:45Z"
}
```

---

### 3.5 Delete Student

**Endpoint:** `DELETE /api/v1/students/{studentKey}`

**Description:** Soft delete a student record (marks as deleted, not physical deletion).

**Path Parameters:**
- `studentKey` (required): Student business key

**Success Response (204 No Content):**
- No response body
- Headers: `X-Correlation-ID`

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.sms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student with key 'STU-2025-9999' not found",
  "instance": "/api/v1/students/STU-2025-9999",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T14:30:00Z"
}
```

**Note:** Consider implementing soft delete with `deleted_at` timestamp instead of physical deletion to maintain data integrity and audit trail.

---

### 3.6 Get Enrollment History

**Endpoint:** `GET /api/v1/students/{studentKey}/enrollment-history`

**Description:** Retrieve the complete enrollment history (status changes) for a student.

**Path Parameters:**
- `studentKey` (required): Student business key

**Success Response (200 OK):**
```json
[
  {
    "enrollmentId": 1,
    "studentKey": "STU-2025-0001",
    "status": "Active",
    "changedBy": "admin",
    "changedAt": "2025-12-08T10:15:30Z",
    "remarks": "Initial registration"
  },
  {
    "enrollmentId": 5,
    "studentKey": "STU-2025-0001",
    "status": "Inactive",
    "changedBy": "admin",
    "changedAt": "2025-12-15T14:20:00Z",
    "remarks": "Student on temporary leave"
  },
  {
    "enrollmentId": 12,
    "studentKey": "STU-2025-0001",
    "status": "Active",
    "changedBy": "admin",
    "changedAt": "2026-01-05T09:30:00Z",
    "remarks": "Returned from leave"
  }
]
```

**Error Response (404 Not Found):**
Same as Get Student by Key.

---

### 3.7 Bulk Student Import (Future Enhancement)

**Endpoint:** `POST /api/v1/students/bulk-import`

**Description:** Import multiple students from CSV/Excel file.

**Request:** `multipart/form-data`
- `file`: CSV/Excel file (max 10MB, max 1000 rows)

**Response:** Batch job ID for async processing.

---

## 4. Configuration Management API

Base Path: `/api/v1/configurations` and `/api/v1/school`

### 4.1 Get School Profile

**Endpoint:** `GET /api/v1/school/profile`

**Description:** Retrieve the school's basic profile information.

**Success Response (200 OK):**
```json
{
  "schoolId": 1,
  "schoolName": "ABC International School",
  "schoolCode": "ABC-2025",
  "schoolLogoUrl": "https://cdn.sms.com/logos/abc-school.png",
  "address": "123 Education Street, Learning City, State - 123456",
  "contactNumber": "+919876543210",
  "email": "info@abcschool.edu",
  "principalName": "Dr. Rajesh Kumar",
  "establishedDate": "2005-06-15",
  "createdAt": "2025-01-01T00:00:00Z",
  "updatedAt": "2025-12-01T10:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.sms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "School profile not configured",
  "instance": "/api/v1/school/profile",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:00:00Z"
}
```

---

### 4.2 Create/Update School Profile

**Endpoint:** `PUT /api/v1/school/profile`

**Description:** Create or update the school profile (upsert operation).

**Request Body:**
```json
{
  "schoolName": "ABC International School",
  "schoolCode": "ABC-2025",
  "schoolLogoUrl": "https://cdn.sms.com/logos/abc-school.png",
  "address": "123 Education Street, Learning City, State - 123456",
  "contactNumber": "+919876543210",
  "email": "info@abcschool.edu",
  "principalName": "Dr. Rajesh Kumar",
  "establishedDate": "2005-06-15"
}
```

**Success Response (200 OK):**
Same as Get School Profile.

**Error Response (400 Bad Request):**
Validation errors for email format, contact number, etc.

---

### 4.3 Get Configuration Settings by Category

**Endpoint:** `GET /api/v1/configurations`

**Description:** Retrieve configuration settings, optionally filtered by category.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| category | string | No | - | Filter by category (General/Academic/Financial) |

**Example Requests:**
```
GET /api/v1/configurations
GET /api/v1/configurations?category=Academic
GET /api/v1/configurations?category=Financial
```

**Success Response (200 OK):**
```json
[
  {
    "settingId": 1,
    "category": "General",
    "settingKey": "school.timezone",
    "settingValue": "Asia/Kolkata",
    "description": "School timezone",
    "updatedAt": "2025-12-01T10:00:00Z",
    "updatedBy": "admin"
  },
  {
    "settingId": 5,
    "category": "Academic",
    "settingKey": "academic.year.current",
    "settingValue": "2025-2026",
    "description": "Current academic year",
    "updatedAt": "2025-04-01T08:00:00Z",
    "updatedBy": "admin"
  },
  {
    "settingId": 10,
    "category": "Financial",
    "settingKey": "fee.registration",
    "settingValue": "5000",
    "description": "One-time registration fee",
    "updatedAt": "2025-01-15T12:00:00Z",
    "updatedBy": "admin"
  }
]
```

**Grouped Response (Alternative):**
```json
{
  "General": [
    {
      "settingKey": "school.timezone",
      "settingValue": "Asia/Kolkata",
      "description": "School timezone"
    }
  ],
  "Academic": [
    {
      "settingKey": "academic.year.current",
      "settingValue": "2025-2026",
      "description": "Current academic year"
    }
  ],
  "Financial": [
    {
      "settingKey": "fee.registration",
      "settingValue": "5000",
      "description": "One-time registration fee"
    }
  ]
}
```

---

### 4.4 Get Specific Configuration Setting

**Endpoint:** `GET /api/v1/configurations/{settingKey}`

**Description:** Retrieve a specific configuration setting by key.

**Path Parameters:**
- `settingKey` (required): Configuration key (e.g., academic.year.current)

**Success Response (200 OK):**
```json
{
  "settingId": 5,
  "category": "Academic",
  "settingKey": "academic.year.current",
  "settingValue": "2025-2026",
  "description": "Current academic year",
  "updatedAt": "2025-04-01T08:00:00Z",
  "updatedBy": "admin"
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "https://api.sms.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Configuration setting 'academic.year.invalid' not found",
  "instance": "/api/v1/configurations/academic.year.invalid",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:00:00Z"
}
```

---

### 4.5 Create Configuration Setting

**Endpoint:** `POST /api/v1/configurations`

**Description:** Create a new configuration setting.

**Request Body:**
```json
{
  "category": "Academic",
  "settingKey": "academic.grading.pass_marks",
  "settingValue": "40",
  "description": "Minimum passing marks percentage"
}
```

**Request Schema:**
```json
{
  "type": "object",
  "required": ["category", "settingKey", "settingValue"],
  "properties": {
    "category": {
      "type": "string",
      "enum": ["General", "Academic", "Financial"]
    },
    "settingKey": {
      "type": "string",
      "minLength": 1,
      "maxLength": 100,
      "pattern": "^[a-z0-9._-]+$",
      "description": "Lowercase, dots, underscores, hyphens only"
    },
    "settingValue": {
      "type": "string",
      "description": "String value (can be JSON)"
    },
    "description": {
      "type": "string",
      "maxLength": 500
    }
  }
}
```

**Success Response (201 Created):**
```json
{
  "settingId": 15,
  "category": "Academic",
  "settingKey": "academic.grading.pass_marks",
  "settingValue": "40",
  "description": "Minimum passing marks percentage",
  "createdAt": "2025-12-08T10:30:00Z",
  "updatedAt": "2025-12-08T10:30:00Z",
  "updatedBy": "admin"
}
```

**Headers:**
```http
Location: /api/v1/configurations/academic.grading.pass_marks
```

**Error Response (409 Conflict):**
```json
{
  "type": "https://api.sms.com/problems/duplicate-resource",
  "title": "Duplicate Resource",
  "status": 409,
  "detail": "Configuration setting 'academic.grading.pass_marks' already exists in category 'Academic'",
  "instance": "/api/v1/configurations",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:30:00Z"
}
```

---

### 4.6 Update Configuration Setting

**Endpoint:** `PUT /api/v1/configurations/{settingKey}`

**Description:** Update an existing configuration setting.

**Path Parameters:**
- `settingKey` (required): Configuration key

**Request Body:**
```json
{
  "settingValue": "45",
  "description": "Updated minimum passing marks percentage"
}
```

**Success Response (200 OK):**
```json
{
  "settingId": 15,
  "category": "Academic",
  "settingKey": "academic.grading.pass_marks",
  "settingValue": "45",
  "description": "Updated minimum passing marks percentage",
  "createdAt": "2025-12-08T10:30:00Z",
  "updatedAt": "2025-12-08T15:45:00Z",
  "updatedBy": "admin"
}
```

---

### 4.7 Delete Configuration Setting

**Endpoint:** `DELETE /api/v1/configurations/{settingKey}`

**Description:** Delete a configuration setting.

**Path Parameters:**
- `settingKey` (required): Configuration key

**Success Response (204 No Content):**
- No response body

**Error Response (404 Not Found):**
Same as Get Specific Configuration Setting.

---

## 5. API Versioning Strategy

### 5.1 Current Approach: URL-Based Versioning

**Format:** `/api/v{major}/...`

**Examples:**
```
/api/v1/students
/api/v2/students (future)
```

**Version Lifecycle:**
- **v1**: Current version (2025)
- **v2**: Planned for 2026 (backward compatible for 6 months)
- **Deprecation Notice**: 6 months before sunset

### 5.2 Breaking Changes Policy

**Major Version Required For:**
- Removing endpoints or fields
- Changing field types or semantics
- Modifying authentication scheme

**Minor Version (Same URL) Allowed For:**
- Adding new optional fields
- Adding new endpoints
- Expanding enum values

---

## 6. Pagination & Filtering Specifications

### 6.1 Pagination Query Parameters

| Parameter | Type | Default | Max | Description |
|-----------|------|---------|-----|-------------|
| page | integer | 0 | - | Page number (0-indexed) |
| size | integer | 20 | 100 | Items per page |
| sort | string | - | - | Sort criteria: {field},{direction} |

**Sort Direction:**
- `asc`: Ascending (default)
- `desc`: Descending

**Multiple Sort Fields:**
```
?sort=lastName,asc&sort=firstName,asc
```

### 6.2 Standard Pagination Response

```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0
  },
  "totalPages": 5,
  "totalElements": 87,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

### 6.3 Filtering Best Practices

**Use Query Parameters for Filtering:**
```
GET /api/v1/students?status=Active&lastName=Sharma
```

**Complex Filters (Future):**
```
GET /api/v1/students?filter=status:Active AND (lastName:Sharma OR guardianName:Kumar)
```

---

## 7. Caching Strategy

### 7.1 Cache-Control Headers

**Cacheable Resources:**
```http
GET /api/v1/school/profile
Cache-Control: public, max-age=3600
ETag: "abc123def456"
```

**Non-Cacheable Resources:**
```http
GET /api/v1/students
Cache-Control: no-cache, no-store, must-revalidate
```

### 7.2 ETags for Conditional Requests

**First Request:**
```http
GET /api/v1/students/STU-2025-0001
Response:
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

**Subsequent Request:**
```http
GET /api/v1/students/STU-2025-0001
If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"

Response:
304 Not Modified (if unchanged)
200 OK (if modified, with new ETag)
```

---

## 8. Rate Limiting

### 8.1 Rate Limit Headers

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 997
X-RateLimit-Reset: 1638960000
```

### 8.2 Rate Limit Exceeded Response

**Status:** 429 Too Many Requests

```json
{
  "type": "https://api.sms.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "You have exceeded the rate limit of 1000 requests per hour",
  "instance": "/api/v1/students",
  "correlationId": "a1b2c3d4-e5f6-7890",
  "timestamp": "2025-12-08T10:00:00Z",
  "retryAfter": 3600
}
```

**Headers:**
```http
Retry-After: 3600
```

---

## 9. CORS Configuration

### 9.1 Allowed Origins

**Development:**
```
http://localhost:3000
http://localhost:5173
```

**Production:**
```
https://sms.school.com
https://app.sms.school.com
```

### 9.2 CORS Headers

```http
Access-Control-Allow-Origin: https://sms.school.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization, X-Correlation-ID
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 86400
```

---

## 10. API Documentation

### 10.1 OpenAPI/Swagger UI

**Endpoint:** `/api/docs`

**Features:**
- Interactive API explorer
- Request/response examples
- Authentication testing (Phase 2)
- Schema validation

### 10.2 ReDoc Alternative

**Endpoint:** `/api/redoc`

**Features:**
- Three-panel documentation
- Searchable endpoints
- Code generation samples

---

## 11. Testing APIs

### 11.1 Postman Collection

**Export Format:** OpenAPI 3.0 → Postman v2.1

**Included:**
- Pre-configured requests for all endpoints
- Environment variables (dev, staging, prod)
- Sample request bodies
- Automated tests (status code validation)

### 11.2 Sample curl Commands

**Create Student:**
```bash
curl -X POST https://api.sms.school.com/api/v1/students \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-123" \
  -d '{
    "firstName": "Rahul",
    "lastName": "Sharma",
    "dateOfBirth": "2012-05-15",
    "mobile": "+919876543210"
  }'
```

**Search Students:**
```bash
curl -X GET "https://api.sms.school.com/api/v1/students?lastName=Sharma&page=0&size=10" \
  -H "Accept: application/json"
```

---

## 12. API Security (Phase 2 Preparation)

### 12.1 JWT Authentication Flow

**Login Endpoint (Future):**
```
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "secure_password"
}

Response:
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGVzdF9yZWZyZXNoX3Rva2Vu...",
  "expiresIn": 1800,
  "tokenType": "Bearer"
}
```

**Authenticated Request:**
```http
GET /api/v1/students
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 12.2 Role-Based Access Control (RBAC)

| Endpoint | SUPER_ADMIN | ADMIN | STAFF | TEACHER |
|----------|-------------|-------|-------|---------|
| POST /students | Yes | Yes | Yes | No |
| GET /students | Yes | Yes | Yes | Yes |
| PUT /students | Yes | Yes | Yes | No |
| DELETE /students | Yes | Yes | No | No |
| POST /configurations | Yes | Yes | No | No |
| PUT /configurations | Yes | Yes | No | No |
| DELETE /configurations | Yes | No | No | No |

### 12.3 API Key for System Integration (Future)

**Header:**
```http
X-API-Key: sk_live_abc123def456ghi789
```

**Use Cases:**
- Third-party integrations
- Mobile app authentication
- Webhook callbacks

---

## 13. Performance Guidelines

### 13.1 Response Time Targets

| Endpoint Type | Target (95th percentile) |
|--------------|--------------------------|
| GET (single resource) | <100ms |
| GET (list with pagination) | <200ms |
| POST (create) | <300ms |
| PUT (update) | <200ms |
| DELETE | <100ms |

### 13.2 Payload Size Limits

| Type | Limit |
|------|-------|
| Request Body | 10 MB |
| Response Body | 50 MB |
| File Upload | 10 MB per file |

### 13.3 Timeout Configuration

- **Connection Timeout**: 5 seconds
- **Read Timeout**: 30 seconds
- **Write Timeout**: 30 seconds

---

## 14. Appendix

### 14.1 Common Field Formats

| Field | Format | Example |
|-------|--------|---------|
| Date | ISO 8601 (YYYY-MM-DD) | 2025-12-08 |
| DateTime | ISO 8601 (UTC) | 2025-12-08T10:15:30Z |
| Mobile | E.164 format | +919876543210 |
| Email | RFC 5322 | user@example.com |
| UUID | UUID v4 | a1b2c3d4-e5f6-7890-1234-567890abcdef |

### 14.2 HTTP Status Code Reference

| Code | Name | Usage in SMS |
|------|------|-------------|
| 200 | OK | Successful GET/PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation errors |
| 401 | Unauthorized | Missing/invalid JWT |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource, optimistic lock |
| 422 | Unprocessable Entity | Business rule violation |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected error |
| 503 | Service Unavailable | Maintenance mode |

### 14.3 Version History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-12-08 | Software Architect | Initial API design |

---

**Document Status**: Approved for Implementation
**OpenAPI Spec Location**: `/specs/openapi/sms-api-v1.yaml`
**Postman Collection**: `/specs/postman/SMS_API_Collection.json`
