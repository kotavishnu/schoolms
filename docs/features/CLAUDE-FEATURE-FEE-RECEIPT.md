# CLAUDE-FEATURE-FEE-RECEIPT.md

**Tier 3: Fee Receipt Feature Agent**

Complete implementation specification for Fee Receipt Generation feature.

---

## Feature Overview

**Feature Name**: Fee Receipt Generation

**Purpose**: Search students, calculate fees automatically, accept payments, and generate printable receipts.

**User Roles**: Administrator, Accountant, Office Staff

**Priority**: P0 (Core Feature)

---

## Implementation Summary

### Architecture Overview

The Fee Receipt feature follows **Spring Boot's layered architecture** with integration to the Drools rules engine for dynamic fee calculation and Fee Journal for payment tracking.

```
HTTP Request → Controller → Service → Repository → Database
                    ↓
              Drools Engine (Fee Calculation)
                    ↓
              PDF Generation (iText/JasperReports)
                    ↓
              Fee Journal Update (Transactional)
```

### Key Components

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **FeeReceiptController** | Spring @RestController | REST API endpoints for calculation & generation |
| **FeeCalculationService** | Spring @Service + Drools | Auto-calculate fees using business rules |
| **ReceiptGenerationService** | Spring @Service + @Transactional | Create receipts, update journal, generate PDF |
| **FeeReceiptRepository** | Spring Data JPA | Database persistence for receipts |
| **Drools Rules Engine** | KieContainer + DRL files | Execute fee calculation rules |
| **PDF Generator** | iText or JasperReports | Generate printable receipts |

### Implementation Flow

**Receipt Generation Workflow:**
1. User searches student (autocomplete API call)
2. User selects months to pay → Calculate Fee API
3. Drools executes rules (base + library + computer + special fees)
4. User confirms payment → Generate Receipt API
5. Service creates receipt record with unique number
6. Updates Fee Journal (marks months as PAID)
7. Generates PDF receipt
8. Returns receipt data with PDF download link
9. Transaction commits or rolls back as atomic operation

### Business Logic (Drools Rules)

**Fee Calculation Rules:**
```
Base Fee:
  - Classes 1-5: ₹1,000/month
  - Classes 6-10: ₹1,500/month

Fixed Fees:
  - Library Fee: ₹200/month
  - Computer Fee: ₹300/month

Conditional Fee:
  - Special Fee: ₹500 (first payment only)

Validation:
  - Receipt number: REC-YYYY-NNNNN (auto-increment)
  - No duplicate payments for same month
  - Payment date cannot be future date
```

### Database Integration

**FeeReceipt Entity (JPA):**
- Primary table: `fee_receipts`
- Foreign key: `student_id` → `students.id`
- JSON field: `fee_breakdown` (stores calculation details)
- Unique constraint: `receipt_number`
- Audit fields: `created_at`, `generated_by`

**Fee Journal Integration:**
- On receipt creation, update `fee_journal` table
- Mark months as PAID status
- Link `receipt_id` to journal entries
- Recalculate pending dues
- All operations within `@Transactional` boundary

### API Contract

**Key Endpoints:**
1. `POST /api/fee-receipts/calculate` - Pre-calculate fees (returns breakdown)
2. `POST /api/fee-receipts` - Generate receipt (creates record + PDF)
3. `GET /api/fee-receipts/{id}/pdf` - Download receipt PDF

**Error Handling:**
- `ResourceNotFoundException` - Student not found (404)
- `ValidationException` - Duplicate payment, invalid data (400)
- Global exception handler returns standardized `ErrorResponse`

### Testing Strategy

**Backend Unit Tests:**
- Drools calculation correctness
- Unique receipt number generation
- First-month special fee logic
- Duplicate payment prevention
- Fee Journal update verification

**Integration Tests:**
- End-to-end receipt generation flow
- Transaction rollback scenarios
- PDF generation validation
- API contract testing

**Test Coverage Target:** 80%+ for services, 100% for critical paths (payment processing)

---

## Feature Goals

### Primary Goals
1. **Quick Student Lookup**: Autocomplete search by student name
2. **Auto-Calculation**: Drools engine calculates total fees
3. **Multiple Payment Methods**: Cash, Online, Cheque, Card
4. **Instant Receipt**: Generate and print PDF receipt
5. **Payment Tracking**: Update Fee Journal automatically

---

## Data Model

### FeeReceipt Entity

**Database Table**: `fee_receipts`

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| **id** | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| **receipt_number** | VARCHAR(50) | NOT NULL, UNIQUE | Format: "REC-2024-00001" |
| **student_id** | BIGINT | NOT NULL, FOREIGN KEY | Reference to students.id |
| **amount** | DECIMAL(10,2) | NOT NULL | Total amount paid |
| **payment_date** | DATE | NOT NULL | Date of payment |
| **payment_method** | ENUM | NOT NULL | CASH, ONLINE, CHEQUE, CARD |
| **transaction_id** | VARCHAR(100) | NULLABLE | For online/card payments |
| **cheque_number** | VARCHAR(50) | NULLABLE | For cheque payments |
| **bank_name** | VARCHAR(100) | NULLABLE | For cheque/online payments |
| **months_paid** | VARCHAR(200) | NOT NULL | "January, February" |
| **fee_breakdown** | JSON | NOT NULL | Detailed fee components |
| **remarks** | VARCHAR(500) | NULLABLE | Additional notes |
| **generated_by** | VARCHAR(100) | NOT NULL | User who generated receipt |
| **created_at** | TIMESTAMP | NOT NULL, AUTO | Receipt generation time |

**Fee Breakdown JSON Structure**:
```json
{
  "baseFee": 1000,
  "libraryFee": 200,
  "computerFee": 300,
  "specialFee": 500,
  "total": 2000
}
```

---

## Business Rules

### Receipt Generation Rules
1. **Auto-calculation**: Use Drools to calculate fees based on class and month
2. **First Month**: Include special fee (₹500) only for first payment
3. **Receipt Number**: Auto-increment format REC-YYYY-NNNNN
4. **Payment Date**: Cannot be future date
5. **Duplicate Prevention**: Check if student already paid for the month

### Fee Journal Integration
1. On receipt generation, create entries in fee_journal
2. Mark months as PAID
3. Update pending dues calculation
4. Link receipt ID to journal entries

---

## API Endpoints

### 1. Calculate Fee for Student
```
POST /api/fee-receipts/calculate
```

**Request**:
```json
{
  "studentId": 1,
  "monthsToPay": ["January", "February"],
  "isFirstPayment": true
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "studentId": 1,
    "studentName": "Rajesh Kumar",
    "classNumber": 5,
    "feeBreakdown": {
      "baseFee": 2000,
      "libraryFee": 400,
      "computerFee": 600,
      "specialFee": 500,
      "total": 3500
    },
    "monthsToPay": ["January", "February"],
    "pendingMonths": ["January", "February", "March", "April"]
  }
}
```

### 2. Generate Receipt
```
POST /api/fee-receipts
```

**Request**:
```json
{
  "studentId": 1,
  "amount": 3500,
  "paymentDate": "2024-10-26",
  "paymentMethod": "CASH",
  "monthsPaid": ["January", "February"],
  "feeBreakdown": {
    "baseFee": 2000,
    "libraryFee": 400,
    "computerFee": 600,
    "specialFee": 500,
    "total": 3500
  },
  "remarks": "Full payment received"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Receipt generated successfully",
  "data": {
    "id": 1,
    "receiptNumber": "REC-2024-00001",
    "studentId": 1,
    "studentName": "Rajesh Kumar",
    "amount": 3500,
    "paymentDate": "2024-10-26",
    "paymentMethod": "CASH",
    "monthsPaid": "January, February",
    "pdfUrl": "/api/fee-receipts/1/pdf"
  }
}
```

### 3. Download Receipt PDF
```
GET /api/fee-receipts/{id}/pdf
```

**Response**: PDF file download

---

## Frontend Implementation

### Page: Fee Receipt Generation

**Route**: `/fee-receipts/generate`

```
┌─────────────────────────────────────────────────────────┐
│ Generate Fee Receipt                                     │
├─────────────────────────────────────────────────────────┤
│                                                          │
│ Step 1: Search Student                                   │
│ ┌────────────────────────────────────────────────────┐ │
│ │ 🔍 Search student by name or mobile...             │ │
│ │ [Rajesh Kumar________________________]             │ │
│ │                                                     │ │
│ │ Suggestions:                                        │ │
│ │ ┌─────────────────────────────────────────────────┐│ │
│ │ │ 👤 Rajesh Kumar - Class 5 - Roll: 5A-15        ││ │
│ │ │    Mobile: 9876543210                           ││ │
│ │ └─────────────────────────────────────────────────┘│ │
│ └────────────────────────────────────────────────────┘ │
│                                                          │
│ Step 2: Fee Calculation                   [Calculate]   │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Student: Rajesh Kumar (Class 5)                    │ │
│ │                                                     │ │
│ │ Select Months to Pay:                              │ │
│ │ ☑ January 2025    ☐ February 2025                │ │
│ │ ☑ February 2025   ☐ March 2025                   │ │
│ │                                                     │ │
│ │ Fee Breakdown:                                      │ │
│ │ Base Fee (2 months):        ₹2,000                │ │
│ │ Library Fee:                ₹400                  │ │
│ │ Computer Fee:               ₹600                  │ │
│ │ Special Fee (First time):   ₹500                  │ │
│ │ ─────────────────────────────────────             │ │
│ │ Total Amount:               ₹3,500                │ │
│ └────────────────────────────────────────────────────┘ │
│                                                          │
│ Step 3: Payment Details                                  │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Payment Method: [Cash ▼]                           │ │
│ │ Payment Date:   [📅 26/10/2024]                   │ │
│ │ Amount Paid:    [₹3,500____]                      │ │
│ │                                                     │ │
│ │ Remarks (Optional):                                 │ │
│ │ [_____________________________________________]    │ │
│ └────────────────────────────────────────────────────┘ │
│                                                          │
│ ┌───────────────────────┐  ┌─────────────────────────┐│
│ │ ← Cancel              │  │ 🖨️ Generate & Print     ││
│ └───────────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

### Receipt PDF Layout

```
┌─────────────────────────────────────────────────────┐
│                 SCHOOL LOGO                          │
│         XYZ International School                     │
│    123 School Road, Bangalore - 560001              │
│           Phone: 080-12345678                        │
│                                                      │
│            FEE RECEIPT                               │
│       Receipt No: REC-2024-00001                    │
│       Date: 26/10/2024                              │
├─────────────────────────────────────────────────────┤
│                                                      │
│ Student Details:                                     │
│ Name:      Rajesh Kumar                             │
│ Class:     5 - A                                    │
│ Roll No:   5A-15                                    │
│ Mobile:    9876543210                               │
│                                                      │
│ Payment Details:                                     │
│ ┌─────────────────────────────────────────────────┐│
│ │ Fee Type        │ Description    │ Amount      ││
│ ├─────────────────┼───────────────┼─────────────┤│
│ │ Base Fee        │ 2 Months      │ ₹2,000     ││
│ │ Library Fee     │ 2 Months      │ ₹400       ││
│ │ Computer Fee    │ 2 Months      │ ₹600       ││
│ │ Special Fee     │ One-time      │ ₹500       ││
│ │                 │               │             ││
│ │ Total:                          │ ₹3,500     ││
│ └─────────────────────────────────────────────────┘│
│                                                      │
│ Months Paid: January 2025, February 2025           │
│ Payment Method: Cash                                │
│                                                      │
│ Remarks: Full payment received                      │
│                                                      │
│ _________________         _________________         │
│ Received By               Authorized Signatory      │
│                                                      │
│ Thank you for your payment!                         │
└─────────────────────────────────────────────────────┘
```

---

## Testing Strategy

### Backend Tests
```java
@Test
void shouldCalculateFeeCorrectly()
@Test
void shouldGenerateUniqueReceiptNumber()
@Test
void shouldIncludeSpecialFeeForFirstPayment()
@Test
void shouldPreventDuplicatePaymentForSameMonth()
@Test
void shouldUpdateFeeJournalAfterReceipt()
```

### Frontend Tests
```javascript
test('autocomplete shows student suggestions', () => {});
test('calculates total fee when months selected', () => {});
test('generates receipt on submit', () => {});
test('downloads PDF receipt', () => {});
```

---

**Agent Directive**: Fee Receipt generation with Drools auto-calculation and PDF generation.
