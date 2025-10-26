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
