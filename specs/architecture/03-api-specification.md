# API Specification - School Management System

**Version**: 1.0
**Date**: November 10, 2025
**Status**: Approved
**Author**: API Architecture Team

---

## Table of Contents
1. [API Overview](#api-overview)
2. [Authentication & Authorization](#authentication--authorization)
3. [Common Patterns](#common-patterns)
4. [Student Management APIs](#student-management-apis)
5. [Class Management APIs](#class-management-apis)
6. [Fee Management APIs](#fee-management-apis)
7. [Payment Tracking APIs](#payment-tracking-apis)
8. [Receipt Management APIs](#receipt-management-apis)
9. [Configuration APIs](#configuration-apis)
10. [Reporting APIs](#reporting-apis)
11. [Error Handling](#error-handling)
12. [OpenAPI Specification](#openapi-specification)

---

## 1. API Overview

### 1.1 Base Information

**Base URL**: `https://api.school.com/v1`
**Protocol**: HTTPS only (TLS 1.3)
**Content Type**: `application/json`
**Character Encoding**: UTF-8

### 1.2 API Design Principles

- **RESTful**: Resources identified by URIs, HTTP methods for operations
- **Stateless**: No server-side session, JWT for authentication
- **Versioned**: Version in URL path (`/v1/`)
- **HATEOAS**: Links to related resources in responses
- **Idempotent**: Safe retries for PUT, DELETE, and idempotent POST operations
- **Pagination**: Cursor-based pagination for large datasets
- **Filtering**: Query parameters for filtering and searching
- **Sorting**: `sort` query parameter with field names

### 1.3 API Rate Limiting

| User Role | Requests per Minute |
|-----------|---------------------|
| Admin | 300 |
| Office Staff | 200 |
| Accounts Manager | 200 |
| Auditor | 100 |

**Rate Limit Headers**:
```
X-RateLimit-Limit: 300
X-RateLimit-Remaining: 299
X-RateLimit-Reset: 1636531200
```

---

## 2. Authentication & Authorization

### 2.1 Authentication Flow

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant AuthService
    participant Database

    Client->>API: POST /auth/login (username, password)
    API->>AuthService: Validate credentials
    AuthService->>Database: Query user
    Database-->>AuthService: User record
    AuthService->>AuthService: Verify password (BCrypt)
    AuthService-->>API: User authenticated
    API->>API: Generate JWT + Refresh Token
    API-->>Client: 200 OK (accessToken, refreshToken)

    Client->>API: GET /students (Authorization: Bearer JWT)
    API->>API: Validate JWT
    API->>API: Check permissions
    API-->>Client: 200 OK (student data)
```

### 2.2 Login API

**Endpoint**: `POST /auth/login`

**Request**:
```json
{
  "username": "admin",
  "password": "SecurePassword123!"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "userId": 1,
    "username": "admin",
    "fullName": "System Administrator",
    "email": "admin@school.com",
    "role": "ADMIN",
    "permissions": [
      "student:read",
      "student:write",
      "fee:read",
      "fee:write",
      "payment:read",
      "payment:write"
    ]
  }
}
```

**Error Responses**:
- `401 Unauthorized`: Invalid credentials
- `423 Locked`: Account locked due to multiple failed attempts

### 2.3 Refresh Token API

**Endpoint**: `POST /auth/refresh`

**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### 2.4 Logout API

**Endpoint**: `POST /auth/logout`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (204 No Content)

### 2.5 JWT Structure

**Header**:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload**:
```json
{
  "sub": "1",
  "username": "admin",
  "role": "ADMIN",
  "permissions": ["student:read", "student:write", "fee:read", "fee:write"],
  "iat": 1636531200,
  "exp": 1636532100
}
```

### 2.6 Role-Based Permissions

| Permission | Admin | Principal | Office Staff | Accounts Manager | Auditor |
|------------|-------|-----------|--------------|------------------|---------|
| student:read | ✓ | ✓ | ✓ | ✓ | ✓ |
| student:write | ✓ | ✓ | ✓ | ✗ | ✗ |
| class:read | ✓ | ✓ | ✓ | ✓ | ✓ |
| class:write | ✓ | ✓ | ✗ | ✗ | ✗ |
| fee:read | ✓ | ✓ | ✓ | ✓ | ✓ |
| fee:write | ✓ | ✓ | ✗ | ✓ | ✗ |
| payment:read | ✓ | ✓ | ✓ | ✓ | ✓ |
| payment:write | ✓ | ✗ | ✓ | ✓ | ✗ |
| config:read | ✓ | ✓ | ✓ | ✓ | ✓ |
| config:write | ✓ | ✗ | ✗ | ✗ | ✗ |
| audit:read | ✓ | ✓ | ✗ | ✗ | ✓ |

---

## 3. Common Patterns

### 3.1 Pagination

**Request**:
```
GET /students?page=1&size=20&sort=admissionDate,desc
```

**Response**:
```json
{
  "content": [...],
  "page": {
    "number": 1,
    "size": 20,
    "totalElements": 2500,
    "totalPages": 125
  },
  "links": {
    "first": "/students?page=0&size=20",
    "prev": "/students?page=0&size=20",
    "self": "/students?page=1&size=20",
    "next": "/students?page=2&size=20",
    "last": "/students?page=124&size=20"
  }
}
```

### 3.2 Filtering

**Request**:
```
GET /students?status=ACTIVE&class=5&admissionYear=2025
```

**Multiple Values**:
```
GET /students?status=ACTIVE,INACTIVE&class=5,6,7
```

### 3.3 Searching

**Request**:
```
GET /students?search=John
```

**Advanced Search**:
```
GET /students?firstName=John&mobile=9876543210
```

### 3.4 Sorting

**Single Field**:
```
GET /students?sort=lastName,asc
```

**Multiple Fields**:
```
GET /students?sort=className,asc&sort=section,asc&sort=lastName,asc
```

### 3.5 Field Selection (Sparse Fieldsets)

**Request**:
```
GET /students/123?fields=studentId,firstName,lastName,mobile
```

**Response**:
```json
{
  "studentId": 123,
  "firstName": "John",
  "lastName": "Doe",
  "mobile": "9876543210"
}
```

### 3.6 Error Response Format (RFC 7807)

**Structure**:
```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "One or more validation errors occurred",
  "instance": "/students",
  "timestamp": "2025-11-10T10:30:00Z",
  "errors": [
    {
      "field": "dateOfBirth",
      "message": "Student must be between 3 and 18 years old",
      "rejectedValue": "2010-01-01"
    }
  ]
}
```

---

## 4. Student Management APIs

### 4.1 Create Student

**Endpoint**: `POST /students`
**Permission**: `student:write`

**Request**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2015-05-15",
  "gender": "MALE",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main Street, City, State, 12345",
  "bloodGroup": "O+",
  "admissionDate": "2025-04-01",
  "guardians": [
    {
      "relationship": "FATHER",
      "firstName": "Robert",
      "lastName": "Doe",
      "mobile": "9876543211",
      "email": "robert.doe@example.com",
      "occupation": "Engineer",
      "isPrimary": true
    },
    {
      "relationship": "MOTHER",
      "firstName": "Jane",
      "lastName": "Doe",
      "mobile": "9876543212",
      "email": "jane.doe@example.com",
      "occupation": "Teacher",
      "isPrimary": false
    }
  ]
}
```

**Validation Rules**:
- `firstName`, `lastName`: 2-50 characters
- `dateOfBirth`: Age must be 3-18 years
- `mobile`: 10 digits, unique across students
- `email`: Valid email format, unique if provided
- `guardians`: At least one guardian required
- `guardians[].mobile`: 10 digits, unique across guardians

**Response** (201 Created):
```json
{
  "studentId": 123,
  "studentCode": "STU-2025-00123",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2015-05-15",
  "gender": "MALE",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main Street, City, State, 12345",
  "bloodGroup": "O+",
  "status": "ACTIVE",
  "admissionDate": "2025-04-01",
  "guardians": [
    {
      "guardianId": 456,
      "relationship": "FATHER",
      "firstName": "Robert",
      "lastName": "Doe",
      "mobile": "9876543211",
      "email": "robert.doe@example.com",
      "occupation": "Engineer",
      "isPrimary": true
    },
    {
      "guardianId": 457,
      "relationship": "MOTHER",
      "firstName": "Jane",
      "lastName": "Doe",
      "mobile": "9876543212",
      "email": "jane.doe@example.com",
      "occupation": "Teacher",
      "isPrimary": false
    }
  ],
  "createdAt": "2025-11-10T10:30:00Z",
  "createdBy": {
    "userId": 1,
    "username": "admin"
  },
  "links": {
    "self": "/students/123",
    "enrollments": "/students/123/enrollments",
    "fees": "/students/123/fees"
  }
}
```

**Error Responses**:
- `400 Bad Request`: Validation errors
- `409 Conflict`: Duplicate mobile number
- `403 Forbidden`: Insufficient permissions

### 4.2 Get Student by ID

**Endpoint**: `GET /students/{studentId}`
**Permission**: `student:read`

**Response** (200 OK):
```json
{
  "studentId": 123,
  "studentCode": "STU-2025-00123",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2015-05-15",
  "gender": "MALE",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "address": "123 Main Street, City, State, 12345",
  "bloodGroup": "O+",
  "status": "ACTIVE",
  "admissionDate": "2025-04-01",
  "photoUrl": "https://cdn.school.com/photos/students/123.jpg",
  "currentEnrollment": {
    "enrollmentId": 789,
    "class": {
      "classId": 10,
      "className": "5",
      "section": "A"
    },
    "academicYear": {
      "academicYearId": 1,
      "yearCode": "2025-2026"
    },
    "enrollmentDate": "2025-04-01",
    "status": "ENROLLED"
  },
  "guardians": [...],
  "createdAt": "2025-11-10T10:30:00Z",
  "updatedAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/students/123",
    "enrollments": "/students/123/enrollments",
    "fees": "/students/123/fees",
    "payments": "/students/123/payments"
  }
}
```

**Error Responses**:
- `404 Not Found`: Student not found

### 4.3 Update Student

**Endpoint**: `PUT /students/{studentId}`
**Permission**: `student:write`

**Request**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "mobile": "9876543210",
  "email": "john.updated@example.com",
  "address": "456 New Street, City, State, 12345",
  "bloodGroup": "O+",
  "status": "ACTIVE"
}
```

**Response** (200 OK): Same as Get Student by ID

**Error Responses**:
- `404 Not Found`: Student not found
- `400 Bad Request`: Validation errors
- `409 Conflict`: Duplicate mobile number

### 4.4 Search Students

**Endpoint**: `GET /students`
**Permission**: `student:read`

**Query Parameters**:
- `search`: Full-text search (firstName, lastName, mobile)
- `status`: Filter by status (ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN)
- `class`: Filter by class name
- `section`: Filter by section
- `academicYear`: Filter by academic year code
- `admissionYear`: Filter by admission year
- `page`: Page number (default: 0)
- `size`: Page size (default: 20, max: 100)
- `sort`: Sort field and direction (e.g., `lastName,asc`)

**Request**:
```
GET /students?status=ACTIVE&class=5&page=0&size=20&sort=lastName,asc
```

**Response** (200 OK):
```json
{
  "content": [
    {
      "studentId": 123,
      "studentCode": "STU-2025-00123",
      "firstName": "John",
      "lastName": "Doe",
      "mobile": "9876543210",
      "status": "ACTIVE",
      "currentClass": "5-A",
      "admissionDate": "2025-04-01"
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  },
  "links": {
    "self": "/students?status=ACTIVE&class=5&page=0&size=20",
    "next": "/students?status=ACTIVE&class=5&page=1&size=20",
    "last": "/students?status=ACTIVE&class=5&page=7&size=20"
  }
}
```

### 4.5 Change Student Status

**Endpoint**: `PATCH /students/{studentId}/status`
**Permission**: `student:write`

**Request**:
```json
{
  "status": "GRADUATED",
  "effectiveDate": "2025-03-31",
  "remarks": "Completed grade 10 successfully"
}
```

**Response** (200 OK):
```json
{
  "studentId": 123,
  "status": "GRADUATED",
  "effectiveDate": "2025-03-31",
  "updatedAt": "2025-11-10T10:30:00Z",
  "updatedBy": {
    "userId": 1,
    "username": "admin"
  }
}
```

### 4.6 Get Student Enrollments

**Endpoint**: `GET /students/{studentId}/enrollments`
**Permission**: `student:read`

**Response** (200 OK):
```json
{
  "enrollments": [
    {
      "enrollmentId": 789,
      "class": {
        "classId": 10,
        "className": "5",
        "section": "A"
      },
      "academicYear": {
        "academicYearId": 1,
        "yearCode": "2025-2026"
      },
      "enrollmentDate": "2025-04-01",
      "status": "ENROLLED"
    },
    {
      "enrollmentId": 788,
      "class": {
        "classId": 9,
        "className": "4",
        "section": "B"
      },
      "academicYear": {
        "academicYearId": 0,
        "yearCode": "2024-2025"
      },
      "enrollmentDate": "2024-04-01",
      "status": "PROMOTED"
    }
  ]
}
```

---

## 5. Class Management APIs

### 5.1 Create Class

**Endpoint**: `POST /classes`
**Permission**: `class:write`

**Request**:
```json
{
  "className": "5",
  "section": "A",
  "academicYearCode": "2025-2026",
  "maxCapacity": 40
}
```

**Response** (201 Created):
```json
{
  "classId": 10,
  "className": "5",
  "section": "A",
  "academicYear": {
    "academicYearId": 1,
    "yearCode": "2025-2026"
  },
  "maxCapacity": 40,
  "currentEnrollment": 0,
  "isActive": true,
  "createdAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/classes/10",
    "enrollments": "/classes/10/enrollments",
    "students": "/classes/10/students"
  }
}
```

### 5.2 Get Class by ID

**Endpoint**: `GET /classes/{classId}`
**Permission**: `class:read`

**Response** (200 OK):
```json
{
  "classId": 10,
  "className": "5",
  "section": "A",
  "academicYear": {
    "academicYearId": 1,
    "yearCode": "2025-2026",
    "startDate": "2025-04-01",
    "endDate": "2026-03-31",
    "isCurrent": true
  },
  "maxCapacity": 40,
  "currentEnrollment": 35,
  "isActive": true,
  "enrollmentPercentage": 87.5,
  "createdAt": "2025-11-10T10:30:00Z",
  "updatedAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/classes/10",
    "students": "/classes/10/students"
  }
}
```

### 5.3 List Classes

**Endpoint**: `GET /classes`
**Permission**: `class:read`

**Query Parameters**:
- `academicYear`: Filter by academic year code
- `className`: Filter by class name
- `section`: Filter by section
- `isActive`: Filter by active status
- `page`, `size`, `sort`: Pagination parameters

**Response** (200 OK):
```json
{
  "content": [
    {
      "classId": 10,
      "className": "5",
      "section": "A",
      "academicYear": "2025-2026",
      "currentEnrollment": 35,
      "maxCapacity": 40,
      "isActive": true
    }
  ],
  "page": {...}
}
```

### 5.4 Enroll Student in Class

**Endpoint**: `POST /classes/{classId}/enrollments`
**Permission**: `student:write`

**Request**:
```json
{
  "studentId": 123,
  "enrollmentDate": "2025-04-01"
}
```

**Validation**:
- Class must have available capacity
- Student must not be already enrolled in the class
- Student status must be ACTIVE

**Response** (201 Created):
```json
{
  "enrollmentId": 789,
  "student": {
    "studentId": 123,
    "studentCode": "STU-2025-00123",
    "fullName": "John Doe"
  },
  "class": {
    "classId": 10,
    "className": "5",
    "section": "A"
  },
  "enrollmentDate": "2025-04-01",
  "status": "ENROLLED",
  "createdAt": "2025-11-10T10:30:00Z"
}
```

**Error Responses**:
- `400 Bad Request`: Class at full capacity
- `409 Conflict`: Student already enrolled

### 5.5 Get Class Students

**Endpoint**: `GET /classes/{classId}/students`
**Permission**: `class:read`

**Response** (200 OK):
```json
{
  "classInfo": {
    "classId": 10,
    "className": "5",
    "section": "A",
    "academicYear": "2025-2026",
    "currentEnrollment": 35,
    "maxCapacity": 40
  },
  "students": [
    {
      "studentId": 123,
      "studentCode": "STU-2025-00123",
      "fullName": "John Doe",
      "mobile": "9876543210",
      "enrollmentDate": "2025-04-01",
      "status": "ACTIVE"
    }
  ]
}
```

### 5.6 Create Academic Year

**Endpoint**: `POST /academic-years`
**Permission**: `class:write`

**Request**:
```json
{
  "yearCode": "2026-2027",
  "startDate": "2026-04-01",
  "endDate": "2027-03-31",
  "isCurrent": false
}
```

**Response** (201 Created):
```json
{
  "academicYearId": 2,
  "yearCode": "2026-2027",
  "startDate": "2026-04-01",
  "endDate": "2027-03-31",
  "isCurrent": false,
  "createdAt": "2025-11-10T10:30:00Z"
}
```

### 5.7 Year Rollover

**Endpoint**: `POST /academic-years/{yearId}/rollover`
**Permission**: `class:write`

**Description**: Promotes students to next class level, creates new classes for the new academic year

**Request**:
```json
{
  "promotionDate": "2026-04-01",
  "promoteAll": false,
  "studentPromotions": [
    {
      "studentId": 123,
      "fromClassId": 10,
      "toClassId": 15,
      "status": "PROMOTED"
    }
  ]
}
```

**Response** (200 OK):
```json
{
  "rolloverSummary": {
    "fromAcademicYear": "2025-2026",
    "toAcademicYear": "2026-2027",
    "totalStudents": 500,
    "promoted": 480,
    "graduated": 15,
    "withdrawn": 5,
    "newClassesCreated": 12
  }
}
```

---

## 6. Fee Management APIs

### 6.1 Create Fee Structure

**Endpoint**: `POST /fee-structures`
**Permission**: `fee:write`

**Request**:
```json
{
  "feeType": "TUITION",
  "feeName": "Class 5 Tuition Fee",
  "amount": 2000.00,
  "frequency": "MONTHLY",
  "academicYearCode": "2025-2026",
  "applicableClasses": "5,6,7",
  "effectiveFrom": "2025-04-01",
  "description": "Monthly tuition fee for classes 5-7"
}
```

**Response** (201 Created):
```json
{
  "feeStructureId": 50,
  "feeType": "TUITION",
  "feeName": "Class 5 Tuition Fee",
  "amount": 2000.00,
  "frequency": "MONTHLY",
  "academicYear": {
    "academicYearId": 1,
    "yearCode": "2025-2026"
  },
  "applicableClasses": "5,6,7",
  "effectiveFrom": "2025-04-01",
  "effectiveTo": null,
  "isActive": true,
  "version": 1,
  "createdAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/fee-structures/50"
  }
}
```

### 6.2 Get Fee Structure

**Endpoint**: `GET /fee-structures/{feeStructureId}`
**Permission**: `fee:read`

**Response** (200 OK): Same as Create Fee Structure response

### 6.3 List Fee Structures

**Endpoint**: `GET /fee-structures`
**Permission**: `fee:read`

**Query Parameters**:
- `academicYear`: Filter by academic year
- `feeType`: Filter by fee type
- `frequency`: Filter by frequency
- `isActive`: Filter by active status
- `applicableClass`: Filter by class

**Response** (200 OK):
```json
{
  "content": [
    {
      "feeStructureId": 50,
      "feeType": "TUITION",
      "feeName": "Class 5 Tuition Fee",
      "amount": 2000.00,
      "frequency": "MONTHLY",
      "applicableClasses": "5,6,7",
      "isActive": true
    }
  ],
  "page": {...}
}
```

### 6.4 Update Fee Structure

**Endpoint**: `PUT /fee-structures/{feeStructureId}`
**Permission**: `fee:write`

**Note**: Updating a fee structure creates a new version with `effectiveFrom` date

**Request**:
```json
{
  "amount": 2200.00,
  "effectiveFrom": "2025-07-01",
  "description": "Fee increased by 10%"
}
```

**Response** (200 OK):
```json
{
  "feeStructureId": 51,
  "feeType": "TUITION",
  "feeName": "Class 5 Tuition Fee",
  "amount": 2200.00,
  "frequency": "MONTHLY",
  "version": 2,
  "previousVersion": {
    "feeStructureId": 50,
    "amount": 2000.00,
    "effectiveTo": "2025-06-30"
  },
  "effectiveFrom": "2025-07-01"
}
```

### 6.5 Calculate Student Fees

**Endpoint**: `POST /fee-structures/calculate`
**Permission**: `fee:read`

**Request**:
```json
{
  "studentId": 123,
  "academicYearCode": "2025-2026",
  "feeMonth": "2025-04"
}
```

**Response** (200 OK):
```json
{
  "studentId": 123,
  "studentName": "John Doe",
  "class": "5-A",
  "feeMonth": "2025-04",
  "feeBreakdown": [
    {
      "feeStructureId": 50,
      "feeType": "TUITION",
      "feeName": "Class 5 Tuition Fee",
      "amount": 2000.00,
      "frequency": "MONTHLY"
    },
    {
      "feeStructureId": 51,
      "feeType": "LIBRARY",
      "feeName": "Library Fee",
      "amount": 100.00,
      "frequency": "MONTHLY"
    },
    {
      "feeStructureId": 52,
      "feeType": "COMPUTER",
      "feeName": "Computer Lab Fee",
      "amount": 200.00,
      "frequency": "MONTHLY"
    }
  ],
  "totalAmount": 2300.00
}
```

---

## 7. Payment Tracking APIs

### 7.1 Generate Monthly Fee Journal

**Endpoint**: `POST /fee-journals/generate`
**Permission**: `fee:write`

**Description**: Generate fee journal entries for all active students for a specific month

**Request**:
```json
{
  "academicYearCode": "2025-2026",
  "feeMonth": "2025-04",
  "dueDate": "2025-04-05",
  "studentIds": []
}
```

**Note**: If `studentIds` is empty, generates for all active students

**Response** (202 Accepted):
```json
{
  "jobId": "job-2025-04-gen-001",
  "status": "PROCESSING",
  "message": "Fee journal generation initiated",
  "estimatedCompletion": "2025-11-10T10:35:00Z",
  "links": {
    "status": "/jobs/job-2025-04-gen-001"
  }
}
```

**Job Status Check**: `GET /jobs/{jobId}`

**Response** (200 OK):
```json
{
  "jobId": "job-2025-04-gen-001",
  "status": "COMPLETED",
  "summary": {
    "totalStudents": 2500,
    "journalsCreated": 2500,
    "errors": 0
  },
  "completedAt": "2025-11-10T10:34:30Z"
}
```

### 7.2 Get Fee Journal by Student

**Endpoint**: `GET /students/{studentId}/fee-journals`
**Permission**: `fee:read`

**Query Parameters**:
- `academicYear`: Filter by academic year
- `status`: Filter by status (PENDING, PARTIAL, PAID, OVERDUE, WAIVED)
- `feeMonth`: Filter by specific month (YYYY-MM)

**Response** (200 OK):
```json
{
  "student": {
    "studentId": 123,
    "studentCode": "STU-2025-00123",
    "fullName": "John Doe"
  },
  "journals": [
    {
      "journalId": 5001,
      "feeStructure": {
        "feeStructureId": 50,
        "feeType": "TUITION",
        "feeName": "Class 5 Tuition Fee"
      },
      "feeMonth": "2025-04",
      "dueAmount": 2000.00,
      "paidAmount": 2000.00,
      "balanceAmount": 0.00,
      "status": "PAID",
      "dueDate": "2025-04-05",
      "paidDate": "2025-04-03"
    },
    {
      "journalId": 5002,
      "feeStructure": {
        "feeStructureId": 50,
        "feeType": "TUITION",
        "feeName": "Class 5 Tuition Fee"
      },
      "feeMonth": "2025-05",
      "dueAmount": 2000.00,
      "paidAmount": 0.00,
      "balanceAmount": 2000.00,
      "status": "PENDING",
      "dueDate": "2025-05-05",
      "paidDate": null
    }
  ],
  "summary": {
    "totalDue": 2000.00,
    "totalPaid": 2000.00,
    "totalBalance": 2000.00,
    "overdueCount": 0
  }
}
```

### 7.3 Record Payment

**Endpoint**: `POST /payments`
**Permission**: `payment:write`

**Request**:
```json
{
  "studentId": 123,
  "paymentAmount": 2000.00,
  "paymentMethod": "CASH",
  "paymentDate": "2025-04-03",
  "transactionReference": null,
  "remarks": "April month fee",
  "journalAllocations": [
    {
      "journalId": 5002,
      "allocationAmount": 2000.00
    }
  ]
}
```

**Validation**:
- Total `allocationAmount` must equal `paymentAmount`
- Each journal allocation must not exceed journal balance
- Student must exist and be active

**Response** (201 Created):
```json
{
  "paymentId": 10001,
  "receiptNumber": "REC-2025-00001",
  "studentId": 123,
  "paymentAmount": 2000.00,
  "paymentMethod": "CASH",
  "paymentDate": "2025-04-03",
  "receivedBy": {
    "userId": 1,
    "username": "admin"
  },
  "journalsUpdated": [
    {
      "journalId": 5002,
      "previousBalance": 2000.00,
      "allocationAmount": 2000.00,
      "newBalance": 0.00,
      "newStatus": "PAID"
    }
  ],
  "receipt": {
    "receiptId": 20001,
    "receiptNumber": "REC-2025-00001",
    "totalAmount": 2000.00,
    "receiptDate": "2025-04-03"
  },
  "createdAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/payments/10001",
    "receipt": "/receipts/REC-2025-00001",
    "student": "/students/123"
  }
}
```

### 7.4 Get Payment Details

**Endpoint**: `GET /payments/{paymentId}`
**Permission**: `payment:read`

**Response** (200 OK): Similar to Record Payment response

### 7.5 Get Overdue Payments

**Endpoint**: `GET /fee-journals/overdue`
**Permission**: `fee:read`

**Query Parameters**:
- `class`: Filter by class
- `academicYear`: Filter by academic year
- `overdueDays`: Minimum overdue days (default: 1)
- `page`, `size`, `sort`: Pagination

**Response** (200 OK):
```json
{
  "content": [
    {
      "journalId": 5003,
      "student": {
        "studentId": 124,
        "studentCode": "STU-2025-00124",
        "fullName": "Jane Smith",
        "mobile": "9876543213",
        "class": "5-A"
      },
      "feeMonth": "2025-03",
      "dueDate": "2025-03-05",
      "dueAmount": 2000.00,
      "balanceAmount": 2000.00,
      "overdueDays": 36
    }
  ],
  "page": {...},
  "summary": {
    "totalOverdueAmount": 250000.00,
    "totalOverdueStudents": 125
  }
}
```

### 7.6 Waive Fee

**Endpoint**: `POST /fee-journals/{journalId}/waive`
**Permission**: `fee:write`

**Request**:
```json
{
  "waiverReason": "Financial hardship - approved by Principal",
  "waiverAmount": 2000.00,
  "approvedBy": "Principal"
}
```

**Response** (200 OK):
```json
{
  "journalId": 5003,
  "previousStatus": "OVERDUE",
  "newStatus": "WAIVED",
  "dueAmount": 2000.00,
  "waivedAmount": 2000.00,
  "balanceAmount": 0.00,
  "waiverReason": "Financial hardship - approved by Principal",
  "updatedAt": "2025-11-10T10:30:00Z"
}
```

---

## 8. Receipt Management APIs

### 8.1 Get Receipt by Number

**Endpoint**: `GET /receipts/{receiptNumber}`
**Permission**: `payment:read`

**Response** (200 OK):
```json
{
  "receiptId": 20001,
  "receiptNumber": "REC-2025-00001",
  "student": {
    "studentId": 123,
    "studentCode": "STU-2025-00123",
    "fullName": "John Doe",
    "class": "5-A"
  },
  "totalAmount": 2000.00,
  "paymentMethod": "CASH",
  "feeBreakdown": [
    {
      "feeType": "TUITION",
      "feeName": "Class 5 Tuition Fee",
      "amount": 2000.00,
      "feeMonth": "2025-04"
    }
  ],
  "monthsCovered": "2025-04",
  "receiptDate": "2025-04-03",
  "generatedBy": {
    "userId": 1,
    "fullName": "System Administrator"
  },
  "createdAt": "2025-11-10T10:30:00Z",
  "links": {
    "self": "/receipts/REC-2025-00001",
    "pdf": "/receipts/REC-2025-00001/pdf",
    "payment": "/payments/10001"
  }
}
```

### 8.2 Download Receipt PDF

**Endpoint**: `GET /receipts/{receiptNumber}/pdf`
**Permission**: `payment:read`

**Response** (200 OK):
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="REC-2025-00001.pdf"`

### 8.3 Search Receipts

**Endpoint**: `GET /receipts`
**Permission**: `payment:read`

**Query Parameters**:
- `studentId`: Filter by student
- `dateFrom`, `dateTo`: Filter by date range
- `paymentMethod`: Filter by payment method
- `receiptNumber`: Search by receipt number
- `page`, `size`, `sort`: Pagination

**Response** (200 OK):
```json
{
  "content": [
    {
      "receiptNumber": "REC-2025-00001",
      "studentName": "John Doe",
      "totalAmount": 2000.00,
      "paymentMethod": "CASH",
      "receiptDate": "2025-04-03"
    }
  ],
  "page": {...}
}
```

### 8.4 Daily Collection Report

**Endpoint**: `GET /receipts/reports/daily-collection`
**Permission**: `payment:read`

**Query Parameters**:
- `date`: Report date (default: today, format: YYYY-MM-DD)

**Response** (200 OK):
```json
{
  "reportDate": "2025-04-03",
  "summary": {
    "totalReceipts": 50,
    "totalCollection": 100000.00,
    "byPaymentMethod": [
      {
        "paymentMethod": "CASH",
        "receiptCount": 30,
        "amount": 60000.00
      },
      {
        "paymentMethod": "UPI",
        "receiptCount": 15,
        "amount": 30000.00
      },
      {
        "paymentMethod": "CARD",
        "receiptCount": 5,
        "amount": 10000.00
      }
    ],
    "byFeeType": [
      {
        "feeType": "TUITION",
        "amount": 80000.00
      },
      {
        "feeType": "LIBRARY",
        "amount": 10000.00
      },
      {
        "feeType": "COMPUTER",
        "amount": 10000.00
      }
    ]
  },
  "receipts": [...]
}
```

---

## 9. Configuration APIs

### 9.1 Get Configuration by Key

**Endpoint**: `GET /configurations/{configKey}`
**Permission**: `config:read`

**Response** (200 OK):
```json
{
  "configId": 1,
  "configCategory": "GENERAL",
  "configKey": "school_name",
  "configValue": "ABC Public School",
  "dataType": "STRING",
  "description": "School name",
  "isEditable": true,
  "version": 1,
  "updatedAt": "2025-11-10T10:30:00Z"
}
```

### 9.2 List Configurations

**Endpoint**: `GET /configurations`
**Permission**: `config:read`

**Query Parameters**:
- `category`: Filter by category (GENERAL, ACADEMIC, FINANCIAL, NOTIFICATION, SECURITY)

**Response** (200 OK):
```json
{
  "configurations": [
    {
      "configKey": "school_name",
      "configValue": "ABC Public School",
      "configCategory": "GENERAL",
      "dataType": "STRING"
    },
    {
      "configKey": "default_class_capacity",
      "configValue": "40",
      "configCategory": "ACADEMIC",
      "dataType": "NUMBER"
    }
  ]
}
```

### 9.3 Update Configuration

**Endpoint**: `PUT /configurations/{configKey}`
**Permission**: `config:write`

**Request**:
```json
{
  "configValue": "45"
}
```

**Response** (200 OK):
```json
{
  "configId": 2,
  "configKey": "default_class_capacity",
  "configValue": "45",
  "previousValue": "40",
  "version": 2,
  "updatedAt": "2025-11-10T10:30:00Z",
  "updatedBy": {
    "userId": 1,
    "username": "admin"
  }
}
```

---

## 10. Reporting APIs

### 10.1 Enrollment Statistics

**Endpoint**: `GET /reports/enrollment-statistics`
**Permission**: `class:read`

**Query Parameters**:
- `academicYear`: Academic year code

**Response** (200 OK):
```json
{
  "academicYear": "2025-2026",
  "totalEnrollment": 2500,
  "byClass": [
    {
      "className": "1",
      "enrollment": 280,
      "sections": 7,
      "capacity": 280
    },
    {
      "className": "5",
      "enrollment": 240,
      "sections": 6,
      "capacity": 240
    }
  ],
  "byGender": {
    "MALE": 1300,
    "FEMALE": 1200
  },
  "byStatus": {
    "ACTIVE": 2480,
    "INACTIVE": 20
  }
}
```

### 10.2 Fee Collection Report

**Endpoint**: `GET /reports/fee-collection`
**Permission**: `fee:read`

**Query Parameters**:
- `academicYear`: Academic year code
- `dateFrom`, `dateTo`: Date range
- `feeType`: Filter by fee type

**Response** (200 OK):
```json
{
  "academicYear": "2025-2026",
  "dateRange": {
    "from": "2025-04-01",
    "to": "2025-04-30"
  },
  "summary": {
    "totalDue": 5000000.00,
    "totalCollected": 4500000.00,
    "totalPending": 500000.00,
    "collectionPercentage": 90.0
  },
  "byFeeType": [
    {
      "feeType": "TUITION",
      "totalDue": 4000000.00,
      "collected": 3600000.00,
      "pending": 400000.00
    }
  ],
  "byClass": [...]
}
```

### 10.3 Pending Fees Report

**Endpoint**: `GET /reports/pending-fees`
**Permission**: `fee:read`

**Query Parameters**:
- `academicYear`: Academic year code
- `class`: Filter by class
- `feeMonth`: Filter by month

**Response** (200 OK):
```json
{
  "reportDate": "2025-11-10",
  "academicYear": "2025-2026",
  "summary": {
    "totalStudents": 2500,
    "studentsWithPending": 250,
    "totalPendingAmount": 500000.00
  },
  "byClass": [
    {
      "className": "5",
      "studentsWithPending": 25,
      "totalPending": 50000.00
    }
  ],
  "students": [
    {
      "studentId": 124,
      "studentCode": "STU-2025-00124",
      "fullName": "Jane Smith",
      "class": "5-A",
      "mobile": "9876543213",
      "pendingAmount": 6000.00,
      "oldestDueDate": "2025-03-05"
    }
  ]
}
```

---

## 11. Error Handling

### 11.1 Standard Error Response

All error responses follow RFC 7807 Problem Details format:

```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "One or more validation errors occurred",
  "instance": "/students",
  "timestamp": "2025-11-10T10:30:00Z",
  "errors": [
    {
      "field": "dateOfBirth",
      "message": "Student must be between 3 and 18 years old",
      "rejectedValue": "2010-01-01",
      "code": "AGE_VALIDATION_FAILED"
    }
  ]
}
```

### 11.2 HTTP Status Codes

| Status Code | Meaning | Usage |
|-------------|---------|-------|
| 200 OK | Success | GET, PUT, PATCH requests |
| 201 Created | Resource created | POST requests |
| 204 No Content | Success with no body | DELETE requests |
| 400 Bad Request | Validation error | Invalid input |
| 401 Unauthorized | Authentication required | Missing/invalid JWT |
| 403 Forbidden | Permission denied | Insufficient permissions |
| 404 Not Found | Resource not found | Invalid resource ID |
| 409 Conflict | Resource conflict | Duplicate entry |
| 422 Unprocessable Entity | Business rule violation | Cannot process valid input |
| 429 Too Many Requests | Rate limit exceeded | Too many requests |
| 500 Internal Server Error | Server error | Unexpected error |
| 503 Service Unavailable | Service down | Maintenance mode |

### 11.3 Error Types

| Error Type | Status | Description |
|------------|--------|-------------|
| validation-error | 400 | Input validation failed |
| authentication-error | 401 | Authentication failed |
| authorization-error | 403 | Insufficient permissions |
| not-found-error | 404 | Resource not found |
| conflict-error | 409 | Duplicate resource |
| business-rule-error | 422 | Business rule violation |
| rate-limit-error | 429 | Rate limit exceeded |
| internal-error | 500 | Internal server error |

### 11.4 Validation Error Details

```json
{
  "type": "https://api.school.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Input validation failed",
  "instance": "/students",
  "timestamp": "2025-11-10T10:30:00Z",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number must be exactly 10 digits",
      "rejectedValue": "98765",
      "code": "MOBILE_FORMAT_INVALID"
    },
    {
      "field": "email",
      "message": "Email address is already in use",
      "rejectedValue": "john@example.com",
      "code": "EMAIL_DUPLICATE"
    }
  ]
}
```

### 11.5 Business Rule Error

```json
{
  "type": "https://api.school.com/errors/business-rule-error",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "Cannot enroll student: class is at full capacity",
  "instance": "/classes/10/enrollments",
  "timestamp": "2025-11-10T10:30:00Z",
  "businessRule": "BR-3",
  "ruleDescription": "Class capacity cannot be exceeded",
  "context": {
    "classId": 10,
    "className": "5-A",
    "currentEnrollment": 40,
    "maxCapacity": 40
  }
}
```

---

## 12. OpenAPI Specification

### 12.1 OpenAPI YAML Excerpt

```yaml
openapi: 3.0.3
info:
  title: School Management System API
  version: 1.0.0
  description: RESTful API for School Management System
  contact:
    name: API Support
    email: api-support@school.com
  license:
    name: Proprietary

servers:
  - url: https://api.school.com/v1
    description: Production server
  - url: https://staging-api.school.com/v1
    description: Staging server
  - url: http://localhost:8080/v1
    description: Local development

security:
  - bearerAuth: []

paths:
  /students:
    get:
      summary: List students
      tags:
        - Students
      security:
        - bearerAuth: []
      parameters:
        - name: status
          in: query
          schema:
            type: string
            enum: [ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN]
        - name: class
          in: query
          schema:
            type: string
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
            maximum: 100
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentPageResponse'
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '403':
          $ref: '#/components/responses/ForbiddenError'

    post:
      summary: Create student
      tags:
        - Students
      security:
        - bearerAuth: []
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
          $ref: '#/components/responses/ValidationError'

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    StudentResponse:
      type: object
      properties:
        studentId:
          type: integer
          format: int64
        studentCode:
          type: string
        firstName:
          type: string
        lastName:
          type: string
        dateOfBirth:
          type: string
          format: date
        gender:
          type: string
          enum: [MALE, FEMALE, OTHER]
        mobile:
          type: string
        email:
          type: string
        status:
          type: string
          enum: [ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN]
        createdAt:
          type: string
          format: date-time
        links:
          $ref: '#/components/schemas/Links'

    CreateStudentRequest:
      type: object
      required:
        - firstName
        - lastName
        - dateOfBirth
        - mobile
        - admissionDate
        - guardians
      properties:
        firstName:
          type: string
          minLength: 2
          maxLength: 50
        lastName:
          type: string
          minLength: 2
          maxLength: 50
        dateOfBirth:
          type: string
          format: date
        gender:
          type: string
          enum: [MALE, FEMALE, OTHER]
        mobile:
          type: string
          pattern: '^[0-9]{10}$'
        email:
          type: string
          format: email
        address:
          type: string
        admissionDate:
          type: string
          format: date
        guardians:
          type: array
          minItems: 1
          items:
            $ref: '#/components/schemas/GuardianRequest'

    ProblemDetails:
      type: object
      properties:
        type:
          type: string
        title:
          type: string
        status:
          type: integer
        detail:
          type: string
        instance:
          type: string
        timestamp:
          type: string
          format: date-time
        errors:
          type: array
          items:
            type: object
            properties:
              field:
                type: string
              message:
                type: string
              rejectedValue:
                type: string
              code:
                type: string

  responses:
    UnauthorizedError:
      description: Authentication required
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'

    ForbiddenError:
      description: Insufficient permissions
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'

    ValidationError:
      description: Validation error
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'
```

### 12.2 Accessing API Documentation

**Swagger UI**: `https://api.school.com/swagger-ui.html`
**OpenAPI JSON**: `https://api.school.com/v3/api-docs`
**OpenAPI YAML**: `https://api.school.com/v3/api-docs.yaml`

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-11-10 | API Team | Initial version |

