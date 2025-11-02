# CLAUDE-FEATURE-FEE-MASTER.md

**Tier 3: Fee Master Feature Agent**

Complete implementation specification for Fee Master (Fee Structure Configuration) feature.

---

## Feature Overview

**Feature Name**: Fee Master Configuration

**Purpose**: Configure and manage fee structures for different classes, including base fees, additional charges, and frequency settings.

**User Roles**: Administrator, Accountant

**Priority**: P0 (Core Feature)

---

## Executive Summary

The Fee Master feature serves as the central fee structure configuration system for the School Management System. It enables administrators and accountants to define, manage, and maintain comprehensive fee structures across all classes and academic years.

### Key Capabilities

**1. Multi-Tier Fee Structure**
- Support for 7 fee types: BASE (tuition), LIBRARY, COMPUTER, SPECIAL (one-time), TRANSPORT, EXAM, and SPORTS
- Class-group specific pricing (Classes 1-5, Classes 6-10, or ALL classes)
- Flexible amount configuration with decimal precision (₹0.00 format)

**2. Payment Frequency Management**
- Three frequency options: MONTHLY, QUARTERLY, YEARLY
- Automated frequency multipliers (1x, 3x, 11x respectively)
- Yearly frequency includes built-in 1-month discount (11x instead of 12x)

**3. Temporal Control**
- Academic year-based fee organization (e.g., "2024-2025")
- Effective date ranges (effective_from, effective_to)
- Historical fee structure tracking for audits and compliance
- Support for ongoing fees (effective_to = NULL)

**4. Business Rules Engine Integration**
- Drools-powered dynamic fee calculations
- First-month special fee logic (₹500 one-time charge)
- Class-based base fee differentiation (₹1,000 vs ₹1,500)
- Automatic total fee computation per student profile

**5. API-First Design**
- RESTful endpoints for complete CRUD operations
- Class-specific fee retrieval API
- Academic year filtering capabilities
- Standardized JSON request/response formats

### Business Value

- **Flexibility**: Accommodates diverse fee structures without code changes
- **Accuracy**: Rule-based calculations eliminate manual errors
- **Transparency**: Clear fee breakdown visible to all stakeholders
- **Auditability**: Complete historical record of fee structure changes
- **Scalability**: Supports unlimited fee types and academic years

### Technical Architecture

**Backend Stack**:
- Spring Boot 3.5 + Java 25 (Service layer with @Transactional)
- PostgreSQL 18+ (fee_master table with 10 fields)
- Spring Data JPA (Repository pattern)
- Drools 9.x (Fee calculation rules engine)

**Frontend Stack**:
- React 18 + Vite (Fee Master configuration page)
- Tailwind CSS (Responsive table and form UI)
- Axios/Fetch API (RESTful API integration)

**Data Flow**:
```
Admin UI → POST /api/fee-master → FeeMasterController → FeeMasterService
→ Drools Rule Validation → FeeMasterRepository → PostgreSQL
```

### Integration Points

- **Fee Journal**: Provides base fee structures for monthly payment tracking
- **Fee Receipt**: Supplies fee data for payment collection and receipt generation
- **Student Management**: Links fee structures to enrolled students via class assignment
- **School Configuration**: Inherits default frequency settings from school config

### Sample Fee Structure

**Classes 1-5 (Monthly Basis)**:
- BASE: ₹1,000
- LIBRARY: ₹200
- COMPUTER: ₹300
- **Total Regular**: ₹1,500/month
- **First Month**: ₹2,000 (includes ₹500 SPECIAL fee)

**Classes 6-10 (Monthly Basis)**:
- BASE: ₹1,500
- LIBRARY: ₹200
- COMPUTER: ₹300
- **Total Regular**: ₹2,000/month
- **First Month**: ₹2,500 (includes ₹500 SPECIAL fee)

---

## Feature Goals

### Primary Goals
1. **Flexible Configuration**: Support multiple fee types per class
2. **Frequency Management**: Handle monthly, quarterly, and yearly fee structures
3. **Dynamic Pricing**: Different fees for different class groups
4. **Historical Tracking**: Maintain fee structure history for audits

---

## Data Model

### FeeMaster Entity

**Database Table**: `fee_master`

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| **id** | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| **fee_type** | VARCHAR(50) | NOT NULL | BASE, LIBRARY, COMPUTER, SPECIAL, TRANSPORT, etc. |
| **amount** | DECIMAL(10,2) | NOT NULL, CHECK (>= 0) | Fee amount in rupees |
| **frequency** | ENUM | NOT NULL | MONTHLY, QUARTERLY, YEARLY |
| **applicable_to_classes** | VARCHAR(50) | NOT NULL | "1-5" or "6-10" or "ALL" |
| **academic_year** | VARCHAR(20) | NOT NULL | Format: "2024-2025" |
| **is_mandatory** | BOOLEAN | DEFAULT true | Required fee or optional |
| **description** | VARCHAR(200) | NULLABLE | Fee description |
| **effective_from** | DATE | NOT NULL | Start date for this fee |
| **effective_to** | DATE | NULLABLE | End date (NULL = ongoing) |
| **created_at** | TIMESTAMP | NOT NULL, AUTO | Creation timestamp |
| **updated_at** | TIMESTAMP | NOT NULL, AUTO | Last update timestamp |

**Fee Types**:
- BASE: Core tuition fee
- LIBRARY: Library access fee
- COMPUTER: Computer lab fee
- SPECIAL: One-time admission/enrollment fee
- TRANSPORT: Bus fee (optional)
- EXAM: Examination fee
- SPORTS: Sports facility fee

---

## Business Rules

### Fee Calculation Rules
1. **Base Fee**: Varies by class group (1-5: ₹1000, 6-10: ₹1500)
2. **Library Fee**: ₹200/month (all classes)
3. **Computer Fee**: ₹300/month (all classes)
4. **Special Fee**: ₹500 (first month only)
5. **Frequency Multiplier**: 
   - Monthly: 1x
   - Quarterly: 3x
   - Yearly: 12x (with 1 month discount = 11x)

---

## API Endpoints

### 1. Create Fee Structure
```
POST /api/fee-master
```

**Request**:
```json
{
  "feeType": "BASE",
  "amount": 1000,
  "frequency": "MONTHLY",
  "applicableToClasses": "1-5",
  "academicYear": "2024-2025",
  "isMandatory": true,
  "description": "Base tuition fee for primary classes",
  "effectiveFrom": "2024-04-01"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Fee structure created successfully",
  "data": {
    "id": 1,
    "feeType": "BASE",
    "amount": 1000.00,
    "frequency": "MONTHLY",
    "applicableToClasses": "1-5",
    "academicYear": "2024-2025",
    "isMandatory": true,
    "description": "Base tuition fee for primary classes",
    "effectiveFrom": "2024-04-01",
    "effectiveTo": null,
    "createdAt": "2024-10-26T10:30:00"
  }
}
```

### 2. Get All Fee Structures
```
GET /api/fee-master
GET /api/fee-master?academicYear=2024-2025
```

### 3. Get Fees for Specific Class
```
GET /api/fee-master/class/{classNumber}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "classNumber": 5,
    "academicYear": "2024-2025",
    "fees": [
      { "feeType": "BASE", "amount": 1000, "frequency": "MONTHLY" },
      { "feeType": "LIBRARY", "amount": 200, "frequency": "MONTHLY" },
      { "feeType": "COMPUTER", "amount": 300, "frequency": "MONTHLY" }
    ],
    "totalMonthlyFee": 1500
  }
}
```

---

## Frontend Implementation

### Page: Fee Master Configuration

**Route**: `/fee-master`

```
┌─────────────────────────────────────────────────────────┐
│ Fee Master Configuration               [+ Add New Fee]  │
│                                                          │
│ Academic Year: [2024-2025 ▼]                           │
├─────────────────────────────────────────────────────────┤
│                                                          │
│ Fee Structures                                           │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Fee Type │ Amount │ Frequency │ Classes │ Actions │ │
│ ├──────────┼────────┼───────────┼─────────┼─────────┤ │
│ │ BASE     │ ₹1,000 │ Monthly   │ 1-5     │ ✏️ 🗑️  │ │
│ │ BASE     │ ₹1,500 │ Monthly   │ 6-10    │ ✏️ 🗑️  │ │
│ │ LIBRARY  │ ₹200   │ Monthly   │ ALL     │ ✏️ 🗑️  │ │
│ │ COMPUTER │ ₹300   │ Monthly   │ ALL     │ ✏️ 🗑️  │ │
│ │ SPECIAL  │ ₹500   │ One-time  │ ALL     │ ✏️ 🗑️  │ │
│ └────────────────────────────────────────────────────┘ │
│                                                          │
│ Fee Summary by Class                                     │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Class 1-5:  ₹1,500/month                           │ │
│ │ Class 6-10: ₹2,000/month                           │ │
│ │ First Month Additional: ₹500 (Special Fee)        │ │
│ └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

**Agent Directive**: Fee Master configuration feature with Drools integration for dynamic fee calculation.
