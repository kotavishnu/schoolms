# API Specifications

## Table of Contents
1. [API Design Principles](#api-design-principles)
2. [Student Service API](#student-service-api)
3. [Configuration Service API](#configuration-service-api)
4. [Common Response Patterns](#common-response-patterns)
5. [Error Handling](#error-handling)
6. [OpenAPI Specifications](#openapi-specifications)

## API Design Principles

### RESTful Design

**Resource-Based URLs**:
- Use nouns, not verbs
- Plural resource names
- Hierarchical structure

**HTTP Methods**:
- `GET`: Retrieve resources
- `POST`: Create new resources
- `PUT`: Update entire resource
- `PATCH`: Partial update
- `DELETE`: Remove resource

**HTTP Status Codes**:
- `200 OK`: Successful GET/PUT/PATCH
- `201 Created`: Successful POST
- `204 No Content`: Successful DELETE
- `400 Bad Request`: Invalid input
- `404 Not Found`: Resource not found
- `409 Conflict`: Duplicate/concurrent update
- `500 Internal Server Error`: Server error

### API Versioning

**Strategy**: URI versioning

**Format**: `/api/v{version}/{resource}`

**Example**: `/api/v1/students`

### Request/Response Format

**Content Type**: `application/json`

**Request Headers**:
```
Content-Type: application/json
X-User-ID: USER123
X-User-Role: ADMIN
X-Request-ID: uuid-for-tracing
```

**Response Headers**:
```
Content-Type: application/json
X-Request-ID: uuid-for-tracing
X-Response-Time: 150ms
```

### Pagination

**Query Parameters**:
- `page`: Page number (0-indexed)
- `size`: Items per page (default 20, max 100)
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

**Response Format**:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

### Filtering and Searching

**Query Parameters** (Student Service):
- `studentId`: Exact match
- `firstName`: Partial match (starts with)
- `lastName`: Partial match (starts with)
- `mobile`: Exact match
- `status`: Exact match (ACTIVE/INACTIVE)
- `minAge`, `maxAge`: Age range
- `caste`: Exact match

**Example**:
```
GET /api/v1/students?lastName=Sharma&status=ACTIVE&page=0&size=20&sort=createdAt,desc
```

## Student Service API

### Base Information

**Base URL**: `http://localhost:8081`

**Service Path**: `/students`

**Full API Path**: `/api/v1/students` (via API Gateway)

### Endpoints

#### 1. Create Student

**POST** `/students`

**Description**: Register a new student

**Request Body**:
```json
{
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2015-05-15",
  "street": "123 MG Road",
  "city": "Bangalore",
  "state": "Karnataka",
  "pinCode": "560001",
  "mobile": "9876543210",
  "email": "rajesh.kumar@example.com",
  "fatherNameOrGuardian": "Suresh Kumar",
  "motherName": "Lakshmi Kumar",
  "caste": "General",
  "moles": "Small mole on left cheek",
  "aadhaarNumber": "123456789012"
}
```

**Validation Rules**:
- `firstName`: Required, 2-50 characters
- `lastName`: Required, 2-50 characters
- `dateOfBirth`: Required, age must be 3-18 years
- `street`, `city`, `state`: Required
- `pinCode`: Required, 6 digits
- `mobile`: Required, 10 digits, unique
- `email`: Optional, valid email format if provided
- `fatherNameOrGuardian`: Required
- `motherName`: Optional
- `caste`: Optional
- `moles`: Optional
- `aadhaarNumber`: Optional, 12 digits if provided

**Success Response**: `201 Created`
```json
{
  "studentId": "STU-2025-00001",
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2015-05-15",
  "age": 10,
  "address": {
    "street": "123 MG Road",
    "city": "Bangalore",
    "state": "Karnataka",
    "pinCode": "560001",
    "country": "India"
  },
  "mobile": "9876543210",
  "email": "rajesh.kumar@example.com",
  "fatherNameOrGuardian": "Suresh Kumar",
  "motherName": "Lakshmi Kumar",
  "caste": "General",
  "moles": "Small mole on left cheek",
  "aadhaarNumber": "123456789012",
  "status": "ACTIVE",
  "createdAt": "2025-11-17T10:30:00Z",
  "updatedAt": "2025-11-17T10:30:00Z"
}
```

**Error Responses**:

`400 Bad Request` - Invalid age:
```json
{
  "type": "https://api.school.com/problems/invalid-age",
  "title": "Invalid Age",
  "status": 400,
  "detail": "Student age must be between 3 and 18 years. Provided age: 2",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

`409 Conflict` - Duplicate mobile:
```json
{
  "type": "https://api.school.com/problems/duplicate-mobile",
  "title": "Duplicate Mobile Number",
  "status": 409,
  "detail": "Mobile number already registered: 9876543210",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

`400 Bad Request` - Validation errors:
```json
{
  "type": "https://api.school.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed",
  "errors": [
    "firstName: First name is required",
    "mobile: Mobile must be exactly 10 digits"
  ],
  "timestamp": "2025-11-17T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -H "X-User-ID: ADMIN001" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "firstName": "Rajesh",
    "lastName": "Kumar",
    "dateOfBirth": "2015-05-15",
    "street": "123 MG Road",
    "city": "Bangalore",
    "state": "Karnataka",
    "pinCode": "560001",
    "mobile": "9876543210",
    "email": "rajesh.kumar@example.com",
    "fatherNameOrGuardian": "Suresh Kumar",
    "motherName": "Lakshmi Kumar"
  }'
```

#### 2. Get Student by ID

**GET** `/students/{studentId}`

**Description**: Retrieve student details by ID

**Path Parameters**:
- `studentId`: Student ID (e.g., STU-2025-00001)

**Success Response**: `200 OK`
```json
{
  "studentId": "STU-2025-00001",
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2015-05-15",
  "age": 10,
  "address": {
    "street": "123 MG Road",
    "city": "Bangalore",
    "state": "Karnataka",
    "pinCode": "560001",
    "country": "India"
  },
  "mobile": "9876543210",
  "email": "rajesh.kumar@example.com",
  "fatherNameOrGuardian": "Suresh Kumar",
  "motherName": "Lakshmi Kumar",
  "caste": "General",
  "moles": "Small mole on left cheek",
  "aadhaarNumber": "123456789012",
  "status": "ACTIVE",
  "createdAt": "2025-11-17T10:30:00Z",
  "updatedAt": "2025-11-17T10:30:00Z",
  "version": 0
}
```

**Error Response**: `404 Not Found`
```json
{
  "type": "https://api.school.com/problems/not-found",
  "title": "Student Not Found",
  "status": 404,
  "detail": "Student not found: STU-2025-99999",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/v1/students/STU-2025-00001 \
  -H "X-User-ID: ADMIN001"
```

#### 3. Update Student

**PUT** `/students/{studentId}`

**Description**: Update student profile

**Path Parameters**:
- `studentId`: Student ID

**Request Body**:
```json
{
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2015-05-15",
  "street": "456 New Address",
  "city": "Bangalore",
  "state": "Karnataka",
  "pinCode": "560025",
  "mobile": "9876543210",
  "email": "rajesh.new@example.com",
  "fatherNameOrGuardian": "Suresh Kumar",
  "motherName": "Lakshmi Kumar",
  "caste": "General",
  "moles": "Updated: Small mole on left cheek",
  "aadhaarNumber": "123456789012",
  "version": 0
}
```

**Notes**:
- `version`: Required for optimistic locking
- All fields except `version` are optional (only send fields to update)
- Cannot change `studentId` or `status` (use separate status endpoint)

**Success Response**: `200 OK`
```json
{
  "studentId": "STU-2025-00001",
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2015-05-15",
  "age": 10,
  "address": {
    "street": "456 New Address",
    "city": "Bangalore",
    "state": "Karnataka",
    "pinCode": "560025",
    "country": "India"
  },
  "mobile": "9876543210",
  "email": "rajesh.new@example.com",
  "fatherNameOrGuardian": "Suresh Kumar",
  "motherName": "Lakshmi Kumar",
  "caste": "General",
  "moles": "Updated: Small mole on left cheek",
  "aadhaarNumber": "123456789012",
  "status": "ACTIVE",
  "createdAt": "2025-11-17T10:30:00Z",
  "updatedAt": "2025-11-17T11:45:00Z",
  "version": 1
}
```

**Error Responses**:

`404 Not Found` - Student doesn't exist

`409 Conflict` - Concurrent update:
```json
{
  "type": "https://api.school.com/problems/concurrent-update",
  "title": "Concurrent Modification",
  "status": 409,
  "detail": "Record was modified by another user. Please refresh and try again.",
  "timestamp": "2025-11-17T11:45:00Z"
}
```

#### 4. Search Students

**GET** `/students`

**Description**: Search and filter students with pagination

**Query Parameters**:
- `studentId`: Exact match (optional)
- `firstName`: Starts with (optional)
- `lastName`: Starts with (optional)
- `mobile`: Exact match (optional)
- `status`: ACTIVE or INACTIVE (optional)
- `minAge`: Minimum age (optional)
- `maxAge`: Maximum age (optional)
- `caste`: Exact match (optional)
- `page`: Page number, 0-indexed (default: 0)
- `size`: Page size (default: 20, max: 100)
- `sort`: Sort field and direction (default: createdAt,desc)

**Examples**:
```
GET /students?status=ACTIVE&page=0&size=20
GET /students?lastName=Sharma&status=ACTIVE&sort=lastName,asc
GET /students?minAge=10&maxAge=15&page=0&size=50
GET /students?mobile=9876543210
```

**Success Response**: `200 OK`
```json
{
  "content": [
    {
      "studentId": "STU-2025-00001",
      "firstName": "Rajesh",
      "lastName": "Kumar",
      "dateOfBirth": "2015-05-15",
      "age": 10,
      "mobile": "9876543210",
      "email": "rajesh.kumar@example.com",
      "status": "ACTIVE",
      "createdAt": "2025-11-17T10:30:00Z"
    },
    {
      "studentId": "STU-2025-00002",
      "firstName": "Priya",
      "lastName": "Sharma",
      "dateOfBirth": "2016-08-20",
      "age": 9,
      "mobile": "9876543211",
      "email": null,
      "status": "ACTIVE",
      "createdAt": "2025-11-17T11:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/v1/students?status=ACTIVE&page=0&size=20" \
  -H "X-User-ID: ADMIN001"
```

#### 5. Update Student Status

**PATCH** `/students/{studentId}/status`

**Description**: Activate or deactivate a student

**Path Parameters**:
- `studentId`: Student ID

**Request Body**:
```json
{
  "status": "INACTIVE"
}
```

**Valid Values**: `ACTIVE`, `INACTIVE`

**Success Response**: `200 OK`
```json
{
  "studentId": "STU-2025-00001",
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "status": "INACTIVE",
  "updatedAt": "2025-11-17T12:00:00Z",
  "updatedBy": "ADMIN001"
}
```

**cURL Example**:
```bash
curl -X PATCH http://localhost:8080/api/v1/students/STU-2025-00001/status \
  -H "Content-Type: application/json" \
  -H "X-User-ID: ADMIN001" \
  -d '{"status": "INACTIVE"}'
```

#### 6. Delete Student

**DELETE** `/students/{studentId}`

**Description**: Delete a student (soft delete, sets status to INACTIVE)

**Path Parameters**:
- `studentId`: Student ID

**Success Response**: `204 No Content`

**Error Response**: `404 Not Found`

**Note**: In Phase 1, this performs soft delete (sets status to INACTIVE). Hard delete can be added in future phases if needed.

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/v1/students/STU-2025-00001 \
  -H "X-User-ID: ADMIN001"
```

#### 7. Get Student Statistics

**GET** `/students/statistics`

**Description**: Get aggregated student statistics

**Success Response**: `200 OK`
```json
{
  "totalStudents": 250,
  "activeStudents": 235,
  "inactiveStudents": 15,
  "averageAge": 12.5,
  "ageDistribution": {
    "3-6": 45,
    "7-10": 85,
    "11-14": 95,
    "15-18": 25
  },
  "casteDistribution": {
    "General": 120,
    "OBC": 80,
    "SC": 30,
    "ST": 20
  }
}
```

## Configuration Service API

### Base Information

**Base URL**: `http://localhost:8082`

**Service Path**: `/configurations`

**Full API Path**: `/api/v1/configurations` (via API Gateway)

### Endpoints

#### 1. Create Configuration Setting

**POST** `/configurations/settings`

**Description**: Create a new configuration setting

**Request Body**:
```json
{
  "category": "GENERAL",
  "key": "SCHOOL_TIMEZONE",
  "value": "Asia/Kolkata",
  "description": "Default timezone for the school"
}
```

**Validation Rules**:
- `category`: Required, must be GENERAL, ACADEMIC, or FINANCIAL
- `key`: Required, UPPERCASE_SNAKE_CASE format, unique within category
- `value`: Required
- `description`: Optional

**Success Response**: `201 Created`
```json
{
  "settingId": 1,
  "category": "GENERAL",
  "key": "SCHOOL_TIMEZONE",
  "value": "Asia/Kolkata",
  "description": "Default timezone for the school",
  "updatedAt": "2025-11-17T10:30:00Z",
  "updatedBy": "ADMIN001",
  "version": 0
}
```

**Error Response**: `409 Conflict` - Duplicate key:
```json
{
  "type": "https://api.school.com/problems/duplicate-setting",
  "title": "Duplicate Setting",
  "status": 409,
  "detail": "Setting already exists: GENERAL.SCHOOL_TIMEZONE",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/v1/configurations/settings \
  -H "Content-Type: application/json" \
  -H "X-User-ID: ADMIN001" \
  -d '{
    "category": "GENERAL",
    "key": "SCHOOL_TIMEZONE",
    "value": "Asia/Kolkata",
    "description": "Default timezone for the school"
  }'
```

#### 2. Get Setting by ID

**GET** `/configurations/settings/{settingId}`

**Description**: Retrieve a configuration setting by ID

**Path Parameters**:
- `settingId`: Setting ID

**Success Response**: `200 OK`
```json
{
  "settingId": 1,
  "category": "GENERAL",
  "key": "SCHOOL_TIMEZONE",
  "value": "Asia/Kolkata",
  "description": "Default timezone for the school",
  "updatedAt": "2025-11-17T10:30:00Z",
  "updatedBy": "ADMIN001",
  "version": 0
}
```

**Error Response**: `404 Not Found`

#### 3. Update Setting

**PUT** `/configurations/settings/{settingId}`

**Description**: Update configuration setting value

**Request Body**:
```json
{
  "value": "Asia/Calcutta",
  "description": "Updated timezone description",
  "version": 0
}
```

**Notes**:
- Cannot change `category` or `key`
- `version` required for optimistic locking

**Success Response**: `200 OK`
```json
{
  "settingId": 1,
  "category": "GENERAL",
  "key": "SCHOOL_TIMEZONE",
  "value": "Asia/Calcutta",
  "description": "Updated timezone description",
  "updatedAt": "2025-11-17T11:00:00Z",
  "updatedBy": "ADMIN001",
  "version": 1
}
```

#### 4. Get Settings by Category

**GET** `/configurations/settings/category/{category}`

**Description**: Retrieve all settings for a specific category

**Path Parameters**:
- `category`: GENERAL, ACADEMIC, or FINANCIAL

**Success Response**: `200 OK`
```json
{
  "category": "GENERAL",
  "settings": [
    {
      "settingId": 1,
      "key": "SCHOOL_TIMEZONE",
      "value": "Asia/Kolkata",
      "description": "Default timezone for the school",
      "updatedAt": "2025-11-17T10:30:00Z",
      "updatedBy": "ADMIN001"
    },
    {
      "settingId": 2,
      "key": "DATE_FORMAT",
      "value": "dd-MM-yyyy",
      "description": "Default date display format",
      "updatedAt": "2025-11-17T10:30:00Z",
      "updatedBy": "SYSTEM"
    }
  ]
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/v1/configurations/settings/category/GENERAL \
  -H "X-User-ID: ADMIN001"
```

#### 5. Get All Settings Grouped by Category

**GET** `/configurations/settings`

**Description**: Retrieve all settings grouped by category

**Success Response**: `200 OK`
```json
{
  "GENERAL": [
    {
      "settingId": 1,
      "key": "SCHOOL_TIMEZONE",
      "value": "Asia/Kolkata",
      "description": "Default timezone for the school"
    },
    {
      "settingId": 2,
      "key": "DATE_FORMAT",
      "value": "dd-MM-yyyy",
      "description": "Default date display format"
    }
  ],
  "ACADEMIC": [
    {
      "settingId": 10,
      "key": "CURRENT_ACADEMIC_YEAR",
      "value": "2025-2026",
      "description": "Current academic year"
    },
    {
      "settingId": 11,
      "key": "MIN_STUDENT_AGE",
      "value": "3",
      "description": "Minimum student age for admission"
    }
  ],
  "FINANCIAL": [
    {
      "settingId": 20,
      "key": "CURRENCY_CODE",
      "value": "INR",
      "description": "Currency code (ISO 4217)"
    }
  ]
}
```

#### 6. Delete Setting

**DELETE** `/configurations/settings/{settingId}`

**Description**: Delete a configuration setting

**Success Response**: `204 No Content`

**Error Response**: `404 Not Found`

#### 7. Get School Profile

**GET** `/configurations/school-profile`

**Description**: Retrieve school profile information

**Success Response**: `200 OK`
```json
{
  "id": 1,
  "schoolName": "ABC International School",
  "schoolCode": "SCH001",
  "logoPath": "/uploads/logos/school-logo.png",
  "address": "123 Education Street, Bangalore, Karnataka - 560001",
  "phone": "+91-80-12345678",
  "email": "info@abcschool.edu.in",
  "updatedAt": "2025-11-17T10:00:00Z",
  "updatedBy": "ADMIN001"
}
```

#### 8. Update School Profile

**PUT** `/configurations/school-profile`

**Description**: Update school profile information

**Request Body**:
```json
{
  "schoolName": "ABC International School",
  "schoolCode": "SCH001",
  "logoPath": "/uploads/logos/school-logo.png",
  "address": "123 Education Street, Bangalore, Karnataka - 560001",
  "phone": "+91-80-12345678",
  "email": "info@abcschool.edu.in"
}
```

**Success Response**: `200 OK`
```json
{
  "id": 1,
  "schoolName": "ABC International School",
  "schoolCode": "SCH001",
  "logoPath": "/uploads/logos/school-logo.png",
  "address": "123 Education Street, Bangalore, Karnataka - 560001",
  "phone": "+91-80-12345678",
  "email": "info@abcschool.edu.in",
  "updatedAt": "2025-11-17T12:00:00Z",
  "updatedBy": "ADMIN001"
}
```

## Common Response Patterns

### Success Response Structure

**Single Resource**:
```json
{
  "resourceId": "...",
  "field1": "value1",
  "field2": "value2",
  ...
}
```

**Collection**:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

**Grouped Data**:
```json
{
  "category1": [...],
  "category2": [...]
}
```

### Timestamps

**Format**: ISO 8601 UTC

**Example**: `2025-11-17T10:30:00Z`

**Fields**:
- `createdAt`: When resource was created
- `updatedAt`: When resource was last updated

## Error Handling

### RFC 7807 Problem Details

All error responses follow RFC 7807 format:

```json
{
  "type": "https://api.school.com/problems/{problem-type}",
  "title": "Human-readable title",
  "status": 400,
  "detail": "Detailed error message",
  "timestamp": "2025-11-17T10:30:00Z",
  "additionalProperty": "optional"
}
```

### Common Error Types

#### 1. Validation Error (400)

```json
{
  "type": "https://api.school.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed",
  "errors": [
    "firstName: First name is required",
    "mobile: Mobile must be exactly 10 digits"
  ],
  "timestamp": "2025-11-17T10:30:00Z"
}
```

#### 2. Not Found (404)

```json
{
  "type": "https://api.school.com/problems/not-found",
  "title": "Student Not Found",
  "status": 404,
  "detail": "Student not found: STU-2025-99999",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

#### 3. Conflict (409)

```json
{
  "type": "https://api.school.com/problems/duplicate-mobile",
  "title": "Duplicate Mobile Number",
  "status": 409,
  "detail": "Mobile number already registered: 9876543210",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

#### 4. Internal Server Error (500)

```json
{
  "type": "https://api.school.com/problems/internal-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred",
  "timestamp": "2025-11-17T10:30:00Z"
}
```

### Error Type URLs

| Problem Type | URL |
|--------------|-----|
| Validation Error | `https://api.school.com/problems/validation-error` |
| Invalid Age | `https://api.school.com/problems/invalid-age` |
| Duplicate Mobile | `https://api.school.com/problems/duplicate-mobile` |
| Not Found | `https://api.school.com/problems/not-found` |
| Concurrent Update | `https://api.school.com/problems/concurrent-update` |
| Duplicate Setting | `https://api.school.com/problems/duplicate-setting` |
| Internal Error | `https://api.school.com/problems/internal-error` |

## OpenAPI Specifications

### Student Service OpenAPI

```yaml
openapi: 3.0.3
info:
  title: Student Service API
  description: Student management service for School Management System
  version: 1.0.0
  contact:
    name: SMS Development Team
    email: dev@school.com

servers:
  - url: http://localhost:8080/api/v1
    description: Development server (API Gateway)
  - url: http://localhost:8081
    description: Direct service endpoint

tags:
  - name: Students
    description: Student management operations
  - name: Statistics
    description: Student statistics and reporting

paths:
  /students:
    post:
      tags:
        - Students
      summary: Create a new student
      description: Register a new student with validation
      operationId: createStudent
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateStudentRequest'
      responses:
        '201':
          description: Student created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentResponse'
        '400':
          description: Invalid input or age validation failed
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProblemDetail'
        '409':
          description: Duplicate mobile number
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProblemDetail'

    get:
      tags:
        - Students
      summary: Search students
      description: Search and filter students with pagination
      operationId: searchStudents
      parameters:
        - name: studentId
          in: query
          schema:
            type: string
        - name: firstName
          in: query
          schema:
            type: string
        - name: lastName
          in: query
          schema:
            type: string
        - name: mobile
          in: query
          schema:
            type: string
        - name: status
          in: query
          schema:
            type: string
            enum: [ACTIVE, INACTIVE]
        - name: minAge
          in: query
          schema:
            type: integer
            minimum: 3
        - name: maxAge
          in: query
          schema:
            type: integer
            maximum: 18
        - name: caste
          in: query
          schema:
            type: string
        - name: page
          in: query
          schema:
            type: integer
            default: 0
            minimum: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
            minimum: 1
            maximum: 100
        - name: sort
          in: query
          schema:
            type: string
            default: createdAt,desc
      responses:
        '200':
          description: Successful search
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PagedStudentResponse'

  /students/{studentId}:
    get:
      tags:
        - Students
      summary: Get student by ID
      operationId: getStudentById
      parameters:
        - name: studentId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Student found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentResponse'
        '404':
          description: Student not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProblemDetail'

    put:
      tags:
        - Students
      summary: Update student
      operationId: updateStudent
      parameters:
        - name: studentId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateStudentRequest'
      responses:
        '200':
          description: Student updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentResponse'
        '404':
          description: Student not found
        '409':
          description: Concurrent update conflict

    delete:
      tags:
        - Students
      summary: Delete student
      operationId: deleteStudent
      parameters:
        - name: studentId
          in: path
          required: true
          schema:
            type: string
      responses:
        '204':
          description: Student deleted successfully
        '404':
          description: Student not found

  /students/{studentId}/status:
    patch:
      tags:
        - Students
      summary: Update student status
      operationId: updateStudentStatus
      parameters:
        - name: studentId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - status
              properties:
                status:
                  type: string
                  enum: [ACTIVE, INACTIVE]
      responses:
        '200':
          description: Status updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentResponse'

  /students/statistics:
    get:
      tags:
        - Statistics
      summary: Get student statistics
      operationId: getStudentStatistics
      responses:
        '200':
          description: Statistics retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StudentStatistics'

components:
  schemas:
    CreateStudentRequest:
      type: object
      required:
        - firstName
        - lastName
        - dateOfBirth
        - street
        - city
        - state
        - pinCode
        - mobile
        - fatherNameOrGuardian
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
        street:
          type: string
          maxLength: 200
        city:
          type: string
          maxLength: 100
        state:
          type: string
          maxLength: 100
        pinCode:
          type: string
          pattern: '^[0-9]{6}$'
        mobile:
          type: string
          pattern: '^[0-9]{10}$'
        email:
          type: string
          format: email
        fatherNameOrGuardian:
          type: string
          maxLength: 100
        motherName:
          type: string
          maxLength: 100
        caste:
          type: string
          maxLength: 50
        moles:
          type: string
        aadhaarNumber:
          type: string
          pattern: '^[0-9]{12}$'

    UpdateStudentRequest:
      allOf:
        - $ref: '#/components/schemas/CreateStudentRequest'
        - type: object
          required:
            - version
          properties:
            version:
              type: integer
              format: int64

    StudentResponse:
      type: object
      properties:
        studentId:
          type: string
        firstName:
          type: string
        lastName:
          type: string
        dateOfBirth:
          type: string
          format: date
        age:
          type: integer
        address:
          $ref: '#/components/schemas/Address'
        mobile:
          type: string
        email:
          type: string
        fatherNameOrGuardian:
          type: string
        motherName:
          type: string
        caste:
          type: string
        moles:
          type: string
        aadhaarNumber:
          type: string
        status:
          type: string
          enum: [ACTIVE, INACTIVE]
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time
        version:
          type: integer
          format: int64

    Address:
      type: object
      properties:
        street:
          type: string
        city:
          type: string
        state:
          type: string
        pinCode:
          type: string
        country:
          type: string

    PagedStudentResponse:
      type: object
      properties:
        content:
          type: array
          items:
            $ref: '#/components/schemas/StudentResponse'
        page:
          type: integer
        size:
          type: integer
        totalElements:
          type: integer
          format: int64
        totalPages:
          type: integer
        first:
          type: boolean
        last:
          type: boolean

    StudentStatistics:
      type: object
      properties:
        totalStudents:
          type: integer
        activeStudents:
          type: integer
        inactiveStudents:
          type: integer
        averageAge:
          type: number
          format: double
        ageDistribution:
          type: object
          additionalProperties:
            type: integer
        casteDistribution:
          type: object
          additionalProperties:
            type: integer

    ProblemDetail:
      type: object
      properties:
        type:
          type: string
          format: uri
        title:
          type: string
        status:
          type: integer
        detail:
          type: string
        timestamp:
          type: string
          format: date-time
        errors:
          type: array
          items:
            type: string
```

### Configuration Service OpenAPI

```yaml
openapi: 3.0.3
info:
  title: Configuration Service API
  description: Configuration management service for School Management System
  version: 1.0.0

servers:
  - url: http://localhost:8080/api/v1
    description: Development server (API Gateway)
  - url: http://localhost:8082
    description: Direct service endpoint

tags:
  - name: Settings
    description: Configuration settings management
  - name: School Profile
    description: School profile management

paths:
  /configurations/settings:
    post:
      tags:
        - Settings
      summary: Create configuration setting
      operationId: createSetting
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateSettingRequest'
      responses:
        '201':
          description: Setting created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SettingResponse'
        '409':
          description: Duplicate setting key

    get:
      tags:
        - Settings
      summary: Get all settings grouped by category
      operationId: getAllSettings
      responses:
        '200':
          description: Settings retrieved successfully
          content:
            application/json:
              schema:
                type: object
                additionalProperties:
                  type: array
                  items:
                    $ref: '#/components/schemas/SettingResponse'

  /configurations/settings/{settingId}:
    get:
      tags:
        - Settings
      summary: Get setting by ID
      operationId: getSettingById
      parameters:
        - name: settingId
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Setting found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SettingResponse'
        '404':
          description: Setting not found

    put:
      tags:
        - Settings
      summary: Update setting
      operationId: updateSetting
      parameters:
        - name: settingId
          in: path
          required: true
          schema:
            type: integer
            format: int64
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateSettingRequest'
      responses:
        '200':
          description: Setting updated successfully
        '404':
          description: Setting not found
        '409':
          description: Concurrent update conflict

    delete:
      tags:
        - Settings
      summary: Delete setting
      operationId: deleteSetting
      parameters:
        - name: settingId
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '204':
          description: Setting deleted successfully
        '404':
          description: Setting not found

  /configurations/settings/category/{category}:
    get:
      tags:
        - Settings
      summary: Get settings by category
      operationId: getSettingsByCategory
      parameters:
        - name: category
          in: path
          required: true
          schema:
            type: string
            enum: [GENERAL, ACADEMIC, FINANCIAL]
      responses:
        '200':
          description: Settings retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CategorySettingsResponse'

  /configurations/school-profile:
    get:
      tags:
        - School Profile
      summary: Get school profile
      operationId: getSchoolProfile
      responses:
        '200':
          description: School profile retrieved successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SchoolProfileResponse'

    put:
      tags:
        - School Profile
      summary: Update school profile
      operationId: updateSchoolProfile
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateSchoolProfileRequest'
      responses:
        '200':
          description: School profile updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SchoolProfileResponse'

components:
  schemas:
    CreateSettingRequest:
      type: object
      required:
        - category
        - key
        - value
      properties:
        category:
          type: string
          enum: [GENERAL, ACADEMIC, FINANCIAL]
        key:
          type: string
          pattern: '^[A-Z][A-Z0-9_]*$'
        value:
          type: string
        description:
          type: string
          maxLength: 500

    UpdateSettingRequest:
      type: object
      required:
        - value
        - version
      properties:
        value:
          type: string
        description:
          type: string
        version:
          type: integer
          format: int64

    SettingResponse:
      type: object
      properties:
        settingId:
          type: integer
          format: int64
        category:
          type: string
          enum: [GENERAL, ACADEMIC, FINANCIAL]
        key:
          type: string
        value:
          type: string
        description:
          type: string
        updatedAt:
          type: string
          format: date-time
        updatedBy:
          type: string
        version:
          type: integer
          format: int64

    CategorySettingsResponse:
      type: object
      properties:
        category:
          type: string
        settings:
          type: array
          items:
            $ref: '#/components/schemas/SettingResponse'

    UpdateSchoolProfileRequest:
      type: object
      required:
        - schoolName
        - schoolCode
      properties:
        schoolName:
          type: string
          maxLength: 200
        schoolCode:
          type: string
          pattern: '^[A-Z0-9]{3,20}$'
        logoPath:
          type: string
          maxLength: 500
        address:
          type: string
          maxLength: 500
        phone:
          type: string
          pattern: '^[0-9+()-]{10,15}$'
        email:
          type: string
          format: email

    SchoolProfileResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        schoolName:
          type: string
        schoolCode:
          type: string
        logoPath:
          type: string
        address:
          type: string
        phone:
          type: string
        email:
          type: string
        updatedAt:
          type: string
          format: date-time
        updatedBy:
          type: string
```

## Summary

The API specifications provide:

1. **RESTful Design**: Resource-based URLs with standard HTTP methods
2. **Consistent Patterns**: Uniform request/response structures
3. **Comprehensive Validation**: Input validation at API boundary
4. **RFC 7807 Errors**: Standard error response format
5. **Pagination Support**: Efficient data retrieval for large datasets
6. **Filtering/Searching**: Flexible query capabilities
7. **Optimistic Locking**: Concurrent update prevention
8. **OpenAPI Documentation**: Complete API specifications for code generation

The next document ([Technology Stack](05-TECHNOLOGY-STACK.md)) provides detailed technology recommendations for implementation.

---

**Version**: 1.0
**Last Updated**: 2025-11-17
**Status**: Draft for Review
