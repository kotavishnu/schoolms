# Endpoint Testing Guide

This document provides comprehensive information about all REST API endpoints in the School Management System and instructions on how to test them.

## Table of Contents

- [Quick Start](#quick-start)
- [Testing Methods](#testing-methods)
- [API Base URL](#api-base-url)
- [Endpoints by Controller](#endpoints-by-controller)
  - [Class Controller](#class-controller)
  - [Student Controller](#student-controller)
  - [Fee Master Controller](#fee-master-controller)
  - [Fee Journal Controller](#fee-journal-controller)
  - [Fee Receipt Controller](#fee-receipt-controller)
  - [School Config Controller](#school-config-controller)
- [Common Request/Response Patterns](#common-requestresponse-patterns)
- [Automated Testing](#automated-testing)
- [Validation Rules](#validation-rules)

---

## Quick Start

### Prerequisites
1. Java 21 or higher installed
2. PostgreSQL database running on localhost:5432
3. Database named `school_management_db` created
4. Application running on port 8080

### Start the Application

```bash
cd backend
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

### Verify Server is Running

```bash
curl http://localhost:8080/api/classes
```

---

## Testing Methods

### 1. Using cURL (Command Line)

```bash
# GET request
curl http://localhost:8080/api/students

# POST request with JSON body
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe",...}'

# PUT request
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John Updated",...}'

# DELETE request
curl -X DELETE http://localhost:8080/api/students/1
```

### 2. Using PowerShell

```powershell
# GET request
Invoke-WebRequest -Uri "http://localhost:8080/api/students" -Method GET

# POST request
$body = @{
    firstName = "John"
    lastName = "Doe"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/students" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

### 3. Using Postman or Insomnia
- Import the endpoints manually or use the collection file
- Set base URL: `http://localhost:8080`
- Add `Content-Type: application/json` header for POST/PUT requests

### 4. Using Automated Test Script

```powershell
# Run the comprehensive test suite
powershell -ExecutionPolicy Bypass -File test-all-endpoints-corrected.ps1
```

---

## API Base URL

```
Base URL: http://localhost:8080/api
```

All endpoints are prefixed with `/api`

---

## Endpoints by Controller

### Class Controller

**Base Path**: `/api/classes`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/classes` | Create a new class | ClassRequestDTO | - |
| 2 | GET | `/api/classes/{id}` | Get class by ID | - | - |
| 3 | GET | `/api/classes` | Get all classes | - | `academicYear` (optional) |
| 4 | GET | `/api/classes/by-number` | Get classes by class number | - | `classNumber`, `academicYear` |
| 5 | GET | `/api/classes/available` | Get classes with available seats | - | `academicYear` |
| 6 | GET | `/api/classes/almost-full` | Get almost full classes (>80%) | - | `academicYear` |
| 7 | GET | `/api/classes/total-students` | Get total students for academic year | - | `academicYear` |
| 8 | PUT | `/api/classes/{id}` | Update class details | ClassRequestDTO | - |
| 9 | DELETE | `/api/classes/{id}` | Delete class | - | - |
| 10 | GET | `/api/classes/exists` | Check if class exists | - | `classNumber`, `section`, `academicYear` |

#### Example Requests

**Create Class:**
```json
POST /api/classes
{
  "classNumber": 1,
  "section": "A",
  "academicYear": "2024-2025",
  "capacity": 50,
  "classTeacher": "Mrs. Smith",
  "roomNumber": "101"
}
```

**Get Classes by Academic Year:**
```
GET /api/classes?academicYear=2024-2025
```

**Check if Class Exists:**
```
GET /api/classes/exists?classNumber=1&section=A&academicYear=2024-2025
```

---

### Student Controller

**Base Path**: `/api/students`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/students` | Create a new student | StudentRequestDTO | - |
| 2 | GET | `/api/students/{id}` | Get student by ID | - | - |
| 3 | GET | `/api/students` | Get all students | - | `classId` (optional) |
| 4 | GET | `/api/students/search` | Search students by name | - | `q` (min 2 chars) |
| 5 | GET | `/api/students/autocomplete` | Autocomplete search (name/mobile) | - | `q` |
| 6 | PUT | `/api/students/{id}` | Update student details | StudentRequestDTO | - |
| 7 | DELETE | `/api/students/{id}` | Delete student | - | - |
| 8 | GET | `/api/students/pending-fees` | Get students with pending fees | - | - |

#### Example Requests

**Create Student:**
```json
POST /api/students
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "address": "123 Main St, City",
  "mobile": "9876543210",
  "religion": "Christian",
  "caste": "General",
  "identifyingMarks": "Mole on left cheek",
  "motherName": "Jane Doe",
  "fatherName": "James Doe",
  "classId": 1,
  "enrollmentDate": "2024-04-01",
  "status": "ACTIVE"
}
```

**Search Students:**
```
GET /api/students/search?q=John
```

**Get Students by Class:**
```
GET /api/students?classId=1
```

**Autocomplete:**
```
GET /api/students/autocomplete?q=98
```

---

### Fee Master Controller

**Base Path**: `/api/fee-masters`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/fee-masters` | Create fee master | FeeMasterRequestDTO | - |
| 2 | GET | `/api/fee-masters/{id}` | Get fee master by ID | - | - |
| 3 | GET | `/api/fee-masters` | Get all fee masters | - | `academicYear` (optional) |
| 4 | GET | `/api/fee-masters/by-type/{feeType}` | Get fee masters by type | - | `activeOnly` (optional) |
| 5 | GET | `/api/fee-masters/active` | Get active fee masters | - | `academicYear` (optional) |
| 6 | GET | `/api/fee-masters/applicable` | Get currently applicable fee masters | - | - |
| 7 | GET | `/api/fee-masters/latest/{feeType}` | Get latest applicable fee master | - | - |
| 8 | PUT | `/api/fee-masters/{id}` | Update fee master | FeeMasterRequestDTO | - |
| 9 | PATCH | `/api/fee-masters/{id}/deactivate` | Deactivate fee master | - | - |
| 10 | PATCH | `/api/fee-masters/{id}/activate` | Activate fee master | - | - |
| 11 | DELETE | `/api/fee-masters/{id}` | Delete fee master | - | - |
| 12 | GET | `/api/fee-masters/count` | Count active fee masters | - | `academicYear` |

#### Example Requests

**Create Fee Master:**
```json
POST /api/fee-masters
{
  "feeType": "TUITION",
  "amount": 5000.00,
  "frequency": "MONTHLY",
  "applicableFrom": "2024-11-01",
  "applicableTo": "2025-12-31",
  "description": "Monthly tuition fee",
  "isActive": true,
  "academicYear": "2024-2025"
}
```

**Fee Types:** `TUITION`, `LIBRARY`, `COMPUTER`, `SPORTS`, `SPECIAL`, `EXAMINATION`, `MAINTENANCE`, `TRANSPORT`

**Frequencies:** `MONTHLY`, `QUARTERLY`, `ANNUAL`, `ONE_TIME`

**Get Fee Masters by Type:**
```
GET /api/fee-masters/by-type/TUITION
GET /api/fee-masters/by-type/SPORTS?activeOnly=true
```

**Activate/Deactivate:**
```
PATCH /api/fee-masters/1/activate
PATCH /api/fee-masters/1/deactivate
```

---

### Fee Journal Controller

**Base Path**: `/api/fee-journals`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/fee-journals` | Create fee journal entry | FeeJournalRequestDTO | - |
| 2 | GET | `/api/fee-journals/{id}` | Get fee journal by ID | - | - |
| 3 | GET | `/api/fee-journals` | Get all fee journals | - | - |
| 4 | GET | `/api/fee-journals/student/{studentId}` | Get journals for student | - | - |
| 5 | GET | `/api/fee-journals/student/{studentId}/pending` | Get pending entries for student | - | - |
| 6 | GET | `/api/fee-journals/by-month` | Get journals by month/year | - | `month`, `year` |
| 7 | GET | `/api/fee-journals/by-status/{status}` | Get journals by payment status | - | - |
| 8 | GET | `/api/fee-journals/overdue` | Get overdue journals | - | - |
| 9 | GET | `/api/fee-journals/student/{studentId}/summary` | Get student dues summary | - | - |
| 10 | PUT | `/api/fee-journals/{id}` | Update fee journal | FeeJournalRequestDTO | - |
| 11 | PATCH | `/api/fee-journals/{id}/payment` | Record payment | `{ "amount": 1000.00 }` | - |
| 12 | DELETE | `/api/fee-journals/{id}` | Delete fee journal | - | - |

#### Example Requests

**Create Fee Journal:**
```json
POST /api/fee-journals
{
  "studentId": 1,
  "month": "December",
  "year": 2025,
  "amountDue": 5500.00,
  "amountPaid": 0.00,
  "dueDate": "2025-12-10",
  "remarks": "December 2025 fee"
}
```

**Important:** Month must be full name: "January", "February", etc. (not numeric)

**Payment Statuses:** `PENDING`, `PARTIAL`, `PAID`, `OVERDUE`

**Get Journals by Month:**
```
GET /api/fee-journals/by-month?month=December&year=2025
```

**Record Payment:**
```json
PATCH /api/fee-journals/1/payment
{
  "amount": 1000.00
}
```

**Get Student Summary:**
```
GET /api/fee-journals/student/1/summary
```

Response:
```json
{
  "studentId": 1,
  "pendingEntriesCount": 2,
  "totalPaidAmount": 2000.00,
  "totalPendingDues": 9000.00
}
```

---

### Fee Receipt Controller

**Base Path**: `/api/fee-receipts`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/fee-receipts` | Generate fee receipt | FeeReceiptRequestDTO | - |
| 2 | GET | `/api/fee-receipts/{id}` | Get receipt by ID | - | - |
| 3 | GET | `/api/fee-receipts/number/{receiptNumber}` | Get receipt by receipt number | - | - |
| 4 | GET | `/api/fee-receipts` | Get all receipts | - | - |
| 5 | GET | `/api/fee-receipts/student/{studentId}` | Get receipts for student | - | - |
| 6 | GET | `/api/fee-receipts/by-date` | Get receipts by date range | - | `startDate`, `endDate` |
| 7 | GET | `/api/fee-receipts/by-method/{paymentMethod}` | Get receipts by payment method | - | - |
| 8 | GET | `/api/fee-receipts/today` | Get today's receipts | - | - |
| 9 | GET | `/api/fee-receipts/collection` | Get total collection for date range | - | `startDate`, `endDate` |
| 10 | GET | `/api/fee-receipts/collection/by-method` | Get collection by payment method | - | `paymentMethod`, `startDate`, `endDate` |
| 11 | GET | `/api/fee-receipts/collection/summary` | Get collection summary | - | `startDate`, `endDate` |
| 12 | GET | `/api/fee-receipts/count/{studentId}` | Count receipts for student | - | - |
| 13 | GET | `/api/fee-receipts/{id}/pdf` | Download receipt as PDF | - | - |

**Note:** PDF endpoint is not yet implemented.

#### Example Requests

**Generate Fee Receipt (Cash):**
```json
POST /api/fee-receipts
{
  "studentId": 1,
  "amount": 5500.00,
  "paymentDate": "2025-11-02",
  "paymentMethod": "CASH",
  "monthsPaid": ["December"],
  "feeBreakdown": {
    "TUITION": 5000.00,
    "LIBRARY": 500.00
  },
  "remarks": "Cash payment for December 2025",
  "generatedBy": "Admin"
}
```

**Generate Receipt (Online):**
```json
POST /api/fee-receipts
{
  "studentId": 1,
  "amount": 6000.00,
  "paymentDate": "2025-11-02",
  "paymentMethod": "ONLINE",
  "transactionId": "TXN123456789",
  "monthsPaid": ["December", "January"],
  "feeBreakdown": {
    "TUITION": 5500.00,
    "LIBRARY": 500.00
  },
  "remarks": "Online payment",
  "generatedBy": "Admin"
}
```

**Generate Receipt (Cheque):**
```json
POST /api/fee-receipts
{
  "studentId": 1,
  "amount": 5500.00,
  "paymentDate": "2025-11-02",
  "paymentMethod": "CHEQUE",
  "chequeNumber": "CHQ789456",
  "bankName": "State Bank",
  "monthsPaid": ["December"],
  "feeBreakdown": {
    "TUITION": 5000.00,
    "COMPUTER": 500.00
  },
  "remarks": "Cheque payment",
  "generatedBy": "Admin"
}
```

**Payment Methods:** `CASH`, `ONLINE`, `CHEQUE`, `CARD`

**Get Receipts by Date Range:**
```
GET /api/fee-receipts/by-date?startDate=2025-11-01&endDate=2025-11-03
```

**Get Collection Summary:**
```
GET /api/fee-receipts/collection/summary?startDate=2025-11-01&endDate=2025-11-03
```

Response:
```json
{
  "receiptCount": 3,
  "grandTotal": 17000.00,
  "cashCollection": 5500.00,
  "onlineCollection": 6000.00,
  "chequeCollection": 5500.00,
  "cardCollection": 0,
  "startDate": "2025-11-01",
  "endDate": "2025-11-03"
}
```

**Get Receipt by Number:**
```
GET /api/fee-receipts/number/REC-2025-00001
```

---

### School Config Controller

**Base Path**: `/api/school-config`

| # | Method | Endpoint | Description | Request Body | Query Params |
|---|--------|----------|-------------|--------------|--------------|
| 1 | POST | `/api/school-config` | Create configuration | SchoolConfigRequestDTO | - |
| 2 | GET | `/api/school-config/{id}` | Get config by ID | - | - |
| 3 | GET | `/api/school-config/key/{key}` | Get config by key | - | - |
| 4 | GET | `/api/school-config/value/{key}` | Get config value only | - | - |
| 5 | GET | `/api/school-config` | Get all configs | - | `category` (optional) |
| 6 | GET | `/api/school-config/editable` | Get editable configs only | - | - |
| 7 | PUT | `/api/school-config/{id}` | Update configuration | SchoolConfigRequestDTO | - |
| 8 | PATCH | `/api/school-config/{key}` | Update config value only | `{ "value": "..." }` | - |
| 9 | DELETE | `/api/school-config/{id}` | Delete configuration | - | - |
| 10 | GET | `/api/school-config/exists/{key}` | Check if config key exists | - | - |

#### Example Requests

**Create Config (String):**
```json
POST /api/school-config
{
  "configKey": "SCHOOL_NAME",
  "configValue": "ABC Public School",
  "category": "GENERAL",
  "description": "Official school name",
  "isEditable": true,
  "dataType": "STRING"
}
```

**Create Config (Integer):**
```json
POST /api/school-config
{
  "configKey": "MAX_STUDENTS_PER_CLASS",
  "configValue": "50",
  "category": "ACADEMIC",
  "description": "Maximum students per class",
  "isEditable": true,
  "dataType": "INTEGER"
}
```

**Create Config (Boolean):**
```json
POST /api/school-config
{
  "configKey": "ENABLE_SMS_NOTIFICATIONS",
  "configValue": "true",
  "category": "NOTIFICATION",
  "description": "Enable SMS notifications",
  "isEditable": true,
  "dataType": "BOOLEAN"
}
```

**Data Types:** `STRING`, `INTEGER`, `BOOLEAN`, `JSON`

**Get Config Value Only:**
```
GET /api/school-config/value/SCHOOL_NAME
```

Response:
```
"ABC Public School"
```

**Update Config Value:**
```json
PATCH /api/school-config/MAX_STUDENTS_PER_CLASS
{
  "value": "45"
}
```

**Get Configs by Category:**
```
GET /api/school-config?category=ACADEMIC
```

**Note:** System configs (`isEditable: false`) may not be deletable.

---

## Common Request/Response Patterns

### Standard Response Format

All endpoints return responses in this format:

```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2025-11-02T07:18:41.292245700"
}
```

### Error Response Format

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "classId": "Class ID is required",
    "mobile": "Mobile number must be 10 digits"
  },
  "timestamp": "2025-11-02T07:18:16.123456"
}
```

### HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation error, invalid data |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server-side error |

---

## Automated Testing

### Running the Automated Test Suite

A comprehensive PowerShell test script is available that tests all 65+ endpoints:

```powershell
# Navigate to project root
cd D:\wks-autonomus

# Run the test script
powershell -ExecutionPolicy Bypass -File test-all-endpoints-corrected.ps1
```

### Test Coverage

The automated test suite covers:
-  All CRUD operations
-  Query parameters and filtering
-  Data validation
-  Business logic
-  Error handling
-  Edge cases

### Test Results

After running the tests, you'll find:
1. **Console Output** - Real-time test results with color coding
2. **test-results-corrected.json** - Detailed JSON results
3. **TEST_RESULTS_SUMMARY.md** - Comprehensive test report
4. **QUICK_TEST_SUMMARY.txt** - Quick reference guide

### Latest Test Results

```
Total Tests: 81
Passed: 80 (98.77%)
Failed: 1 (1.23%)

Controllers Tested: 6
Endpoints Covered: 65+
```

---

## Validation Rules

### Student Validation

| Field | Rules |
|-------|-------|
| `firstName` | Required, max 100 chars |
| `lastName` | Required, max 100 chars |
| `mobile` | Required, unique, 10 digits, starts with 6-9 |
| `dateOfBirth` | Required, must result in age 3-18 years |
| `classId` | Required, must reference existing class |
| `enrollmentDate` | Required, cannot be future date |
| `status` | Must be: ACTIVE, INACTIVE, GRADUATED, TRANSFERRED |

### Class Validation

| Field | Rules |
|-------|-------|
| `classNumber` | Required, 1-10 |
| `section` | Required, max 10 chars |
| `academicYear` | Required, format: YYYY-YYYY (e.g., 2024-2025) |
| `capacity` | Required, positive number, default: 50 |
| Unique Constraint | (classNumber, section, academicYear) must be unique |

### Fee Master Validation

| Field | Rules |
|-------|-------|
| `feeType` | Required, valid enum value |
| `amount` | Required, > 0, max 6 digits + 2 decimals |
| `frequency` | Required: MONTHLY, QUARTERLY, ANNUAL, ONE_TIME |
| `applicableFrom` | Required, past or present date |
| `applicableTo` | Must be future date |
| `academicYear` | Format: YYYY-YYYY |

### Fee Journal Validation

| Field | Rules |
|-------|-------|
| `studentId` | Required, must exist |
| `month` | Required, full month name (e.g., "December") |
| `year` | Required, 2000-2100 |
| `amountDue` | Required, >= 0, max 8 digits + 2 decimals |
| `dueDate` | Must be future date |
| Unique Constraint | (studentId, month, year) must be unique |

### Fee Receipt Validation

| Field | Rules |
|-------|-------|
| `studentId` | Required, must exist |
| `amount` | Required, > 0 |
| `paymentDate` | Required, cannot be future date |
| `paymentMethod` | Required: CASH, ONLINE, CHEQUE, CARD |
| `monthsPaid` | Required, array of month names |
| `receiptNumber` | Auto-generated, unique, format: REC-YYYY-NNNNN |

### School Config Validation

| Field | Rules |
|-------|-------|
| `configKey` | Required, unique, max 100 chars |
| `configValue` | Required, max 1000 chars |
| `category` | Max 50 chars |
| `dataType` | STRING, INTEGER, BOOLEAN, JSON |
| `isEditable` | Default: true |

---

## Testing Tips

### 1. Test Order Matters

When testing manually, follow this order:
1. Create Classes first (required for students)
2. Create Students (required for fee journals and receipts)
3. Create Fee Masters
4. Create Fee Journals
5. Generate Fee Receipts
6. Test queries and filters

### 2. Common Issues

**Issue:** Student creation fails with "Class ID is required"
- **Solution:** Ensure you've created a class first and use its ID

**Issue:** Fee Journal creation fails with "Month must be a valid month name"
- **Solution:** Use full month names: "January", "February", etc., not numbers

**Issue:** Fee Master creation fails with "Applicable to date must be in the future"
- **Solution:** Use a date in the future (e.g., "2025-12-31")

**Issue:** "Mobile number already exists"
- **Solution:** Mobile numbers must be unique across all students

### 3. Data Cleanup

To clean test data, delete in reverse order:
1. Fee Receipts (if any)
2. Fee Journals
3. Students
4. Fee Masters
5. Classes
6. School Configs (only editable ones)

### 4. Using Date Formats

Always use ISO date format: `YYYY-MM-DD`

Examples:
- `2024-11-02`
- `2025-12-31`
- `2010-05-15`

### 5. Testing Collections and Summaries

To test collection endpoints effectively:
1. Generate multiple receipts with different payment methods
2. Use date ranges that include those receipts
3. Verify totals match the sum of individual receipts

---

## Sample Test Workflow

### Complete Test Scenario

```bash
# 1. Create a class
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -d '{
    "classNumber": 1,
    "section": "A",
    "academicYear": "2024-2025",
    "capacity": 50,
    "classTeacher": "Mrs. Smith",
    "roomNumber": "101"
  }'
# Response: { "success": true, "data": { "id": 1, ... } }

# 2. Create a student
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2010-05-15",
    "mobile": "9876543210",
    "classId": 1,
    ...
  }'
# Response: { "success": true, "data": { "id": 1, ... } }

# 3. Create a fee master
curl -X POST http://localhost:8080/api/fee-masters \
  -H "Content-Type: application/json" \
  -d '{
    "feeType": "TUITION",
    "amount": 5000.00,
    "frequency": "MONTHLY",
    "applicableFrom": "2024-11-01",
    "applicableTo": "2025-12-31",
    "academicYear": "2024-2025"
  }'

# 4. Create a fee journal
curl -X POST http://localhost:8080/api/fee-journals \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "month": "December",
    "year": 2025,
    "amountDue": 5000.00,
    "dueDate": "2025-12-10"
  }'

# 5. Generate a fee receipt
curl -X POST http://localhost:8080/api/fee-receipts \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "amount": 5000.00,
    "paymentDate": "2025-11-02",
    "paymentMethod": "CASH",
    "monthsPaid": ["December"],
    "feeBreakdown": { "TUITION": 5000.00 },
    "generatedBy": "Admin"
  }'

# 6. Get student summary
curl http://localhost:8080/api/fee-journals/student/1/summary

# 7. Get collection summary
curl "http://localhost:8080/api/fee-receipts/collection/summary?startDate=2025-11-01&endDate=2025-11-03"
```

---

## Additional Resources

- **API Documentation**: See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- **Test Results**: See [TEST_RESULTS_SUMMARY.md](../TEST_RESULTS_SUMMARY.md)
- **Quick Reference**: See [QUICK_TEST_SUMMARY.txt](../QUICK_TEST_SUMMARY.txt)
- **Test Script**: See [test-all-endpoints-corrected.ps1](../test-all-endpoints-corrected.ps1)

---

## Support

For issues or questions:
1. Check validation rules above
2. Review error messages in the response
3. Consult the test results for working examples
4. Check server logs for detailed error information

---

**Last Updated**: November 2, 2025
**Test Coverage**: 100% (65/65 endpoints)
**Pass Rate**: 98.77%
