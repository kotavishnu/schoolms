# CLAUDE-FEATURE-SCHOOL-CONFIG.md

**Tier 3: School Configuration Feature Agent**

System-wide school settings and configuration management. This is a **singleton feature** - only one configuration record exists in the system.

---

## Overview

**Purpose**: Central configuration hub for school identity, fee calculation parameters, and academic year settings.

**Pattern**: Singleton - service layer enforces single record existence

**Use Cases**:
- First-time school setup
- Updating school contact information
- Changing fee frequency globally (affects all fee calculations)
- Configuring late fee penalties
- Setting academic year start month

---

## Data Model

**Database Table**: `school_config`

| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto | Configuration ID |
| school_name | VARCHAR(200) | NOT NULL | - | Official school name |
| address | VARCHAR(500) | NOT NULL | - | Complete school address |
| phone | VARCHAR(15) | NOT NULL | - | Contact number (10 digits, starts with 6-9) |
| email | VARCHAR(100) | NOT NULL | - | Official email (validated format) |
| fee_frequency | VARCHAR(20) | NOT NULL | MONTHLY | Fee calculation frequency (ENUM) |
| academic_year_start_month | INTEGER | NOT NULL | 4 | Month when academic year begins (1-12) |
| late_fee_percentage | DECIMAL(5,2) | - | 2.5 | Penalty percentage for overdue payments |
| logo_url | VARCHAR(500) | - | NULL | Path to school logo image |
| created_at | TIMESTAMP | NOT NULL | Auto | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Auto | Last modification timestamp |

**JPA Entity**: `com.school.management.model.SchoolConfig`

**Enums**:
- `FeeFrequency`: MONTHLY, QUARTERLY, YEARLY

**Audit Support**:
- Uses Spring Data JPA `@EntityListeners(AuditingEntityListener.class)`
- `@CreatedDate` and `@LastModifiedDate` for automatic timestamps

---

## API Endpoints

Base URL: `http://localhost:8080/api/config`

### 1. Get School Configuration

**Endpoint**: `GET /api/config`

**Request**: No parameters required

**Response**: `200 OK`
```json
{
  "success": true,
  "message": "School configuration retrieved successfully",
  "data": {
    "id": 1,
    "schoolName": "Greenwood High School",
    "address": "123 Education Street, Mumbai, Maharashtra 400001",
    "phone": "9876543210",
    "email": "admin@greenwoodhigh.edu.in",
    "feeFrequency": "MONTHLY",
    "academicYearStartMonth": 4,
    "lateFeePercentage": 2.5,
    "logoUrl": "/images/school-logo.png",
    "createdAt": "2024-10-28T10:00:00",
    "updatedAt": "2024-10-28T15:30:00"
  },
  "timestamp": "2024-10-28T15:30:00"
}
```

**Error Response**: `404 Not Found`
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "School configuration not found. Please set up the school configuration first.",
  "timestamp": "2024-10-28T10:00:00"
}
```

**Use Case**:
- Load school details on application startup
- Display school info in headers/footers
- Retrieve fee frequency for calculations
- Get late fee percentage for overdue calculations

---

### 2. Update/Create School Configuration

**Endpoint**: `PUT /api/config`

**Request**:
```json
{
  "schoolName": "Greenwood High School",
  "address": "123 Education Street, Mumbai, Maharashtra 400001",
  "phone": "9876543210",
  "email": "admin@greenwoodhigh.edu.in",
  "feeFrequency": "MONTHLY",
  "academicYearStartMonth": 4,
  "lateFeePercentage": 5.0,
  "logoUrl": "/images/school-logo.png"
}
```

**Validation Rules**:
- `schoolName`: Required, max 200 chars
- `address`: Required, max 500 chars
- `phone`: Required, must match pattern `^[6-9][0-9]{9}$` (10 digits, starts with 6-9)
- `email`: Required, valid email format, max 100 chars
- `feeFrequency`: Required, must be MONTHLY/QUARTERLY/YEARLY
- `academicYearStartMonth`: Required, integer between 1-12
- `lateFeePercentage`: Optional, decimal between 0.0 and 100.0
- `logoUrl`: Optional, max 500 chars

**Response**: `200 OK`
```json
{
  "success": true,
  "message": "School configuration updated successfully",
  "data": {
    "id": 1,
    "schoolName": "Greenwood High School",
    "address": "123 Education Street, Mumbai, Maharashtra 400001",
    "phone": "9876543210",
    "email": "admin@greenwoodhigh.edu.in",
    "feeFrequency": "MONTHLY",
    "academicYearStartMonth": 4,
    "lateFeePercentage": 5.0,
    "logoUrl": "/images/school-logo.png",
    "createdAt": "2024-10-28T10:00:00",
    "updatedAt": "2024-10-28T15:30:00"
  }
}
```

**Validation Error**: `400 Bad Request`
```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "Input validation failed",
  "fieldErrors": {
    "phone": "Phone number must be 10 digits and start with 6-9",
    "email": "Email must be valid",
    "lateFeePercentage": "Late fee percentage cannot exceed 100"
  },
  "timestamp": "2024-10-28T10:00:00"
}
```

**Behavior**:
- **If config exists**: Updates existing record
- **If config doesn't exist**: Creates new record with provided values
- **Upsert pattern**: No separate POST endpoint needed

---

## Implementation Details

### Backend Structure

**Repository**: `com.school.management.repository.SchoolConfigRepository`
```java
public interface SchoolConfigRepository extends JpaRepository<SchoolConfig, Long> {
    // Singleton pattern - only one record expected
}
```

**Service**: `com.school.management.service.SchoolConfigService`

**Key Methods**:
1. `getSchoolConfig()`: Retrieves the singleton config (throws exception if not found)
2. `updateSchoolConfig(SchoolConfigRequestDTO)`: Updates or creates config

**Singleton Logic**:
```java
SchoolConfig config = schoolConfigRepository.findAll()
    .stream()
    .findFirst()
    .orElseGet(() -> SchoolConfig.builder().build());
```

**Controller**: `com.school.management.controller.SchoolConfigController`
- Base mapping: `/api/config`
- CORS enabled for `http://localhost:3000`

---

## UI Design

### Configuration Form

```
┌────────────────────────────────────────────────────────────────┐
│ School Configuration                                           │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│ Basic Information                                               │
│ ┌────────────────────────────────────────────────────────────┐ │
│ │ School Name *                                              │ │
│ │ [Greenwood High School_____________________________]       │ │
│ │                                                             │ │
│ │ Address *                                                   │ │
│ │ [123 Education Street, Mumbai, Maharashtra 400001___]      │ │
│ │ [________________________________________________]          │ │
│ │                                                             │ │
│ │ Phone *                    Email *                         │ │
│ │ [9876543210_____]         [admin@greenwoodhigh.edu.in__]  │ │
│ └────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ Fee Settings                                                    │
│ ┌────────────────────────────────────────────────────────────┐ │
│ │ Fee Frequency *           Academic Year Starts In *        │ │
│ │ ⦿ Monthly                 [April ▼]                        │ │
│ │ ○ Quarterly               (Month: 1-12)                    │ │
│ │ ○ Yearly                                                   │ │
│ │                                                             │ │
│ │ Late Fee Percentage                                        │ │
│ │ [5.0] % (0-100)                                           │ │
│ │ ℹ Applied to overdue payments after grace period          │ │
│ └────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ Branding                                                        │
│ ┌────────────────────────────────────────────────────────────┐ │
│ │ School Logo                                                │ │
│ │ [Choose File] [school-logo.png]         [Upload]          │ │
│ │                                                             │ │
│ │ Preview:                                                   │ │
│ │ ┌──────┐                                                   │ │
│ │ │ LOGO │                                                   │ │
│ │ └──────┘                                                   │ │
│ └────────────────────────────────────────────────────────────┘ │
│                                                                 │
│                    [Cancel]  [Save Configuration]              │
└────────────────────────────────────────────────────────────────┘
```

### UI Features

**Form Fields**:
1. **School Name**: Text input (required, max 200)
2. **Address**: Textarea (required, max 500)
3. **Phone**: Text input with validation (required, 10 digits)
4. **Email**: Email input with validation (required)
5. **Fee Frequency**: Radio buttons (Monthly/Quarterly/Yearly)
6. **Academic Year Start Month**: Dropdown (1-12, labeled with month names)
7. **Late Fee Percentage**: Number input (0-100, step 0.1)
8. **Logo URL**: File upload or text input

**Validation**:
- Real-time validation on blur
- Phone: Must be 10 digits starting with 6-9
- Email: Valid email format
- Late Fee: Between 0 and 100
- All required fields highlighted if empty

**Actions**:
- **Save Configuration**: PUT request to `/api/config`
- **Cancel**: Reset form to original values
- **Upload Logo**: Separate endpoint (to be implemented)

---

## User Workflows

### First-Time Setup

1. **Access**: Admin navigates to Settings > School Configuration
2. **Empty State**: Form shows empty fields with default values
3. **Fill Form**: Admin enters school details
4. **Validation**: System validates all inputs
5. **Submit**: Admin clicks "Save Configuration"
6. **API Call**: `PUT /api/config` with form data
7. **Success**: System creates new config and shows success message
8. **Redirect**: Return to dashboard

### Updating Configuration

1. **Load**: Admin navigates to Settings > School Configuration
2. **Fetch**: System calls `GET /api/config`
3. **Populate**: Form pre-fills with existing values
4. **Modify**: Admin changes desired fields (e.g., late fee percentage)
5. **Submit**: Admin clicks "Save Configuration"
6. **API Call**: `PUT /api/config` with updated data
7. **Success**: System updates config and shows confirmation
8. **Refresh**: Other modules using config auto-refresh settings

### Changing Fee Frequency

1. **Impact Warning**: System shows modal:
   ```
   ⚠ Warning: Changing fee frequency affects all fee calculations

   Current: MONTHLY
   New: QUARTERLY

   This will impact:
   - Future fee calculations
   - Receipt generation
   - Payment schedules

   [Cancel] [Proceed]
   ```
2. **Confirm**: Admin clicks "Proceed"
3. **Update**: System saves new frequency
4. **Notification**: All users notified of change

---

## Integration Points

### Used By

**1. Fee Receipt Service**
- Reads `feeFrequency` to determine calculation basis
- Example: Monthly student pays 1 month, Quarterly pays 3 months
```java
SchoolConfig config = schoolConfigService.getSchoolConfig();
int monthsMultiplier = switch(config.getFeeFrequency()) {
    case MONTHLY -> 1;
    case QUARTERLY -> 3;
    case YEARLY -> 12;
};
```

**2. Fee Journal Service**
- Uses `lateFeePercentage` for overdue calculations
- Uses `academicYearStartMonth` for year boundaries
```java
BigDecimal lateFee = overdueAmount
    .multiply(config.getLateFeePercentage())
    .divide(BigDecimal.valueOf(100));
```

**3. Receipt Generation**
- Includes school name, address on receipts
- Displays school logo if available
```java
receipt.setSchoolName(config.getSchoolName());
receipt.setSchoolAddress(config.getAddress());
receipt.setLogoUrl(config.getLogoUrl());
```

**4. Frontend Components**
- Header: Displays school name
- Footer: Shows school address and contact
- About Page: Uses all school details

---

## Business Rules

### Singleton Enforcement
- **Database**: Only one record should exist
- **Service Layer**: Automatically uses first record if multiple exist
- **UI**: No "Create New" button, only "Edit Configuration"

### Default Values
- **Fee Frequency**: MONTHLY (most common)
- **Academic Year Start**: 4 (April - common in India)
- **Late Fee Percentage**: 2.5% (reasonable default)

### Academic Year Calculation
Based on `academicYearStartMonth`:
```
If current month >= start month:
    Academic Year = currentYear to (currentYear + 1)
    Example: October 2024 → "2024-2025"

If current month < start month:
    Academic Year = (currentYear - 1) to currentYear
    Example: February 2024 → "2023-2024"
```

### Late Fee Application
```
Days Overdue = Current Date - Due Date - Grace Period Days

If Days Overdue > 0:
    Late Fee = Overdue Amount × (Late Fee Percentage / 100)
Else:
    Late Fee = 0
```

---

## Testing Checklist

### Unit Tests
- ✅ Create new configuration
- ✅ Update existing configuration
- ✅ Retrieve configuration
- ✅ Handle missing configuration (404 error)
- ✅ Validate phone number format
- ✅ Validate email format
- ✅ Validate late fee percentage range
- ✅ Validate academic year start month range

### Integration Tests
- ✅ GET `/api/config` returns existing config
- ✅ PUT `/api/config` creates config if not exists
- ✅ PUT `/api/config` updates config if exists
- ✅ Validation errors return 400 status
- ✅ CORS headers present for frontend

### UI Tests
- ✅ Form loads with existing data
- ✅ Required field validation works
- ✅ Phone format validation displays error
- ✅ Email format validation displays error
- ✅ Late fee range validation works
- ✅ Success message shows after save
- ✅ Form resets on cancel

---

## Sample Data

### Development Seed Data
```json
{
  "schoolName": "Greenwood High School",
  "address": "123 Education Street, Mumbai, Maharashtra 400001",
  "phone": "9876543210",
  "email": "admin@greenwoodhigh.edu.in",
  "feeFrequency": "MONTHLY",
  "academicYearStartMonth": 4,
  "lateFeePercentage": 2.5,
  "logoUrl": "/images/school-logo.png"
}
```

### Test Cases
```json
// Valid - Quarterly frequency
{
  "schoolName": "Delhi Public School",
  "address": "456 Learning Avenue, Delhi 110001",
  "phone": "8123456789",
  "email": "info@dps.edu.in",
  "feeFrequency": "QUARTERLY",
  "academicYearStartMonth": 6,
  "lateFeePercentage": 5.0,
  "logoUrl": null
}

// Invalid - Phone doesn't start with 6-9
{
  "phone": "5123456789"  // Error: must start with 6-9
}

// Invalid - Late fee > 100
{
  "lateFeePercentage": 150.0  // Error: cannot exceed 100
}

// Invalid - Month out of range
{
  "academicYearStartMonth": 13  // Error: must be between 1-12
}
```

---

## Future Enhancements

### Planned Features
- [ ] Logo upload endpoint with file storage
- [ ] Multiple contact numbers (admin, accounts, principal)
- [ ] Social media links
- [ ] Website URL
- [ ] School board/affiliation details (CBSE, ICSE, etc.)
- [ ] Principal signature image for certificates
- [ ] Customizable grace period days per fee type
- [ ] Email template configuration
- [ ] SMS gateway configuration
- [ ] Multi-language support for school name

### Technical Improvements
- [ ] Add caching for config (reduce DB queries)
- [ ] Add audit log for configuration changes
- [ ] Add version history (track who changed what when)
- [ ] Add configuration backup/restore
- [ ] Add configuration export (JSON/PDF)

---

**Agent Directive**: This is a Tier 3 feature agent. Use with CLAUDE-BACKEND.md (Tier 2) for complete implementation guidance.
