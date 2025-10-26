# CLAUDE-FEATURE-SCHOOL-CONFIG.md

**Tier 3: School Configuration Feature Agent**

System-wide school settings and configuration management.

---

## Data Model

**Database Table**: `school_config`

| Field | Type | Description |
|-------|------|-------------|
| id | BIGINT | Primary key |
| school_name | VARCHAR(200) | School name |
| address | VARCHAR(500) | Full address |
| phone | VARCHAR(15) | Contact number |
| email | VARCHAR(100) | Official email |
| fee_frequency | ENUM | MONTHLY, QUARTERLY, YEARLY |
| academic_year_start_month | INTEGER | 1-12 (typically 4 for April) |
| late_fee_percentage | DECIMAL(5,2) | Penalty % for late payment |
| logo_url | VARCHAR(500) | School logo path |

---

## API Endpoints

```
GET /api/config
PUT /api/config
```

---

## UI

```
┌──────────────────────────────────────────────────┐
│ School Configuration                              │
├──────────────────────────────────────────────────┤
│ Basic Information                                 │
│ School Name:   [XYZ International School_____]   │
│ Address:       [123 School Road, Bangalore___]   │
│ Phone:         [080-12345678_________________]   │
│ Email:         [admin@xyzschool.com__________]   │
│                                                   │
│ Fee Settings                                      │
│ Fee Frequency: [Monthly ▼]                       │
│ Academic Year: Starts in [April ▼]              │
│ Late Fee:      [2.5]% per month                  │
│                                                   │
│ Logo Upload:   [Choose File] [Upload]           │
│                                                   │
│ [Save Configuration]                             │
└──────────────────────────────────────────────────┘
```
