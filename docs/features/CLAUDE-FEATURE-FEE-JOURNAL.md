# CLAUDE-FEATURE-FEE-JOURNAL.md

**Tier 3: Fee Journal Feature Agent**

Track payment history of student fee payments. Every time the parent pays fee for the student make entry in journal and can be used for pending dues, and monthly payment status per student.

---

## Feature Overview

**Feature Name**: Fee Journal (Payment Tracking)

**Purpose**: Maintain comprehensive payment history, track pending dues, and monitor payment status for each student month-by-month.

**User Roles**: Administrator, Accountant, Office Staff, Parents (view-only)

**Priority**: P0 (Core Feature - Critical for financial tracking)

---

## Implementation Summary

### Architecture Overview

The Fee Journal is the **central payment tracking system** that maintains a complete history of all fee transactions. It serves as the single source of truth for payment status and integrates tightly with Fee Receipt generation.

```
Fee Receipt Generation → Creates/Updates Journal Entries
                              ↓
                    Fee Journal (Payment History)
                              ↓
           ┌──────────────────┴──────────────────┐
           ↓                                      ↓
    Pending Dues Reports              Overdue Payment Alerts
```

### Key Components

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **FeeJournalController** | Spring @RestController | API endpoints for payment history & dues |
| **FeeJournalService** | Spring @Service + @Transactional | Business logic for journal management |
| **FeeJournalRepository** | Spring Data JPA | Database access with custom queries |
| **FeeJournal Entity** | JPA @Entity | Payment record per student per month |
| **Payment Status Enum** | Java Enum | PENDING, PAID, PARTIAL, OVERDUE |
| **Scheduler** | @Scheduled | Daily job to mark overdue payments |

### Implementation Flow

**Journal Entry Lifecycle:**
1. **Initial Creation**: Auto-generated when student enrolls (creates entries for all months in academic year)
2. **Payment Recording**: Updated when Fee Receipt is generated (marks as PAID, links receipt_id)
3. **Status Management**:
   - PENDING → Payment not received yet
   - PAID → Full payment received
   - PARTIAL → Partial payment made
   - OVERDUE → Past due date without payment
4. **Late Fee Calculation**: Scheduled job checks due_date and adds penalties
5. **Reporting**: Aggregated queries for pending dues, overdue lists, payment summaries

### Business Logic

**Journal Management Rules:**
```
Entry Creation:
  - One entry per student per month
  - Created on student enrollment for entire academic year
  - amount_due calculated using Drools (based on class)

Payment Processing:
  - On receipt generation, update corresponding journal entries
  - Set amount_paid, payment_date, status = PAID
  - Link fee_receipt_id for traceability

Status Transitions:
  - PENDING: Default status on creation
  - PAID: amount_paid = amount_due AND receipt exists
  - PARTIAL: 0 < amount_paid < amount_due
  - OVERDUE: current_date > due_date AND status = PENDING

Late Fee Calculation:
  - Applied when payment exceeds due_date
  - Configurable rate (e.g., ₹50/day or 1% per month)
  - Added to amount_due for next payment
```

### Database Integration

**FeeJournal Entity (JPA):**
- Primary table: `fee_journal`
- Foreign keys:
  - `student_id` → `students.id` (mandatory)
  - `fee_receipt_id` → `fee_receipts.id` (nullable, set on payment)
- Composite unique constraint: `(student_id, month_year)`
- Indexes: `student_id`, `status`, `due_date` (for performance)
- Audit fields: `created_at`, `updated_at`

**Relationships:**
```
Student 1 ──── N FeeJournal (one student, many monthly entries)
FeeReceipt 1 ──── N FeeJournal (one receipt can pay multiple months)
```

**Key Queries:**
- Find all pending payments for a student
- Find all overdue payments across all students
- Calculate total pending dues by class/student
- Payment history report (date range)

### API Contract

**Key Endpoints:**
1. `GET /api/fee-journal/student/{studentId}` - Complete payment history
2. `GET /api/fee-journal/student/{studentId}/pending` - Pending dues only
3. `GET /api/fee-journal/overdue` - All overdue payments (admin)
4. `POST /api/fee-journal/mark-paid` - Manually mark payment (internal use)
5. `GET /api/fee-journal/summary/{studentId}` - Payment summary (total paid, pending)

**Response Structure:**
```json
{
  "success": true,
  "data": {
    "studentId": 1,
    "studentName": "Rajesh Kumar",
    "journalEntries": [
      {
        "id": 1,
        "monthYear": "January 2025",
        "amountDue": 1500,
        "amountPaid": 1500,
        "status": "PAID",
        "paymentDate": "2025-01-05",
        "receiptId": 101,
        "receiptNumber": "REC-2025-00101"
      }
    ],
    "totalDue": 15000,
    "totalPaid": 3000,
    "totalPending": 12000
  }
}
```

**Error Handling:**
- `ResourceNotFoundException` - Student/Journal entry not found (404)
- `ValidationException` - Invalid date range, duplicate entry (400)
- `BusinessRuleException` - Cannot mark paid without receipt (409)

### Integration with Fee Receipt

**Transactional Flow:**
```java
@Transactional
public FeeReceiptResponseDTO generateReceipt(FeePaymentRequestDTO request) {
    // 1. Create receipt record
    FeeReceipt receipt = createReceipt(request);

    // 2. Update journal entries for each month paid
    for (String month : request.getMonthsPaid()) {
        FeeJournal journal = feeJournalRepository
            .findByStudentIdAndMonthYear(request.getStudentId(), month)
            .orElseThrow();

        journal.setAmountPaid(calculateMonthlyAmount(request.getFeeBreakdown()));
        journal.setPaymentDate(request.getPaymentDate());
        journal.setStatus(PaymentStatus.PAID);
        journal.setFeeReceiptId(receipt.getId());

        feeJournalRepository.save(journal);
    }

    // 3. Commit transaction (atomic operation)
    return mapToResponse(receipt);
}
```

### Scheduled Jobs

**Overdue Payment Checker:**
```java
@Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
public void markOverduePayments() {
    LocalDate today = LocalDate.now();

    List<FeeJournal> pendingEntries = feeJournalRepository
        .findByStatusAndDueDateBefore(PaymentStatus.PENDING, today);

    for (FeeJournal entry : pendingEntries) {
        entry.setStatus(PaymentStatus.OVERDUE);
        // Optional: Calculate and add late fee
        entry.setLateFee(calculateLateFee(entry.getDueDate(), today));
    }

    feeJournalRepository.saveAll(pendingEntries);

    // Optional: Send notification emails
    notificationService.sendOverdueAlerts(pendingEntries);
}
```

### Testing Strategy

**Backend Unit Tests:**
- Journal entry creation on student enrollment
- Status transition logic (PENDING → PAID → OVERDUE)
- Late fee calculation accuracy
- Pending dues aggregation
- Overdue detection by due date

**Integration Tests:**
- Fee receipt → Journal update (transactional)
- Rollback on receipt generation failure
- Concurrent payment handling (optimistic locking)
- Scheduled job execution

**Test Coverage Target:** 85%+ for services, 100% for payment status logic

### Performance Optimizations

**Database Indexes:**
```sql
CREATE INDEX idx_fee_journal_student ON fee_journal(student_id);
CREATE INDEX idx_fee_journal_status ON fee_journal(status);
CREATE INDEX idx_fee_journal_due_date ON fee_journal(due_date);
CREATE INDEX idx_fee_journal_composite ON fee_journal(student_id, month_year);
```

**Caching Strategy:**
- Cache student payment summary (5-minute TTL)
- Invalidate on receipt generation
- Use Spring Cache abstraction

**Query Optimization:**
- Use projection DTOs for summary queries
- Fetch joins for student details
- Batch processing for overdue job (1000 records/batch)

---

## Data Model

**Database Table**: `fee_journal`

| Field | Type | Description |
|-------|------|-------------|
| id | BIGINT | Primary key |
| student_id | BIGINT | FK to students |
| fee_receipt_id | BIGINT | FK to fee_receipts (nullable) |
| month_year | VARCHAR(20) | "January 2025" |
| amount_due | DECIMAL(10,2) | Expected fee amount |
| amount_paid | DECIMAL(10,2) | Actual paid amount |
| payment_date | DATE | Date of payment (nullable) |
| status | ENUM | PENDING, PAID, PARTIAL, OVERDUE |
| due_date | DATE | Payment deadline |
| late_fee | DECIMAL(10,2) | Penalty for late payment |

---

## API Endpoints

```
GET /api/fee-journal/student/{studentId}
GET /api/fee-journal/student/{studentId}/pending
GET /api/fee-journal/overdue
POST /api/fee-journal/mark-paid
```

---

## Frontend UI

```
┌──────────────────────────────────────────────────┐
│ Fee Payment History - Rajesh Kumar               │
├──────────────────────────────────────────────────┤
│ Month      │ Due    │ Paid   │ Status  │ Date   │
├────────────┼────────┼────────┼─────────┼────────┤
│ Jan 2025   │ 1,500  │ 1,500  │ ✅ PAID │ 05-Jan │
│ Feb 2025   │ 1,500  │ 1,500  │ ✅ PAID │ 05-Feb │
│ Mar 2025   │ 1,500  │ 0      │ ⏳ PENDING │ -  │
│ Apr 2025   │ 1,500  │ 0      │ ⏳ PENDING │ -  │
└──────────────────────────────────────────────────┘
Total Pending: ₹3,000
```
