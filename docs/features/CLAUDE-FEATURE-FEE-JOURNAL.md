# CLAUDE-FEATURE-FEE-JOURNAL.md

**Tier 3: Fee Journal Feature Agent**

Track payment history of student fee payments. Every time the parent pays fee for the student make entry in journel and can be used for  pending dues, and monthly payment status per student.

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
