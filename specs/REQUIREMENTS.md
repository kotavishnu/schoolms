# High-Level Product Requirements Document (HLPRD)

**Product Name**: School Management System (SMS)  
**Version**: 1.0  
**Date**: November 6, 2025  
**Document Type**: High-Level Product Requirements  
**Author**: Product Management Team  

---

## 1. Executive Summary

The **School Management System (SMS)** is a web-based digital platform designed to automate and optimize administrative workflows within schools. It provides a unified interface for managing student registration, class organization, and fee collection. The goal is to minimize manual effort, ensure data accuracy, and deliver real-time financial transparency for school operations.

### Key Objectives
- **Efficiency**: Automate up to 80% of manual administrative tasks.
- **Transparency**: Provide real-time fee and student data.
- **Accuracy**: Achieve 99%+ data accuracy with validation.
- **Scalability**: Support up to 2,500 students and 100+ concurrent users.

### Target Users
- School Administrators  
- Office/Clerical Staff  
- Accounts Managers  
- School Management/Principal

### Business Impact
- Improved decision-making through centralized reporting.

---

## 2. Product Vision & Strategy

To build a **scalable, secure, and user-friendly** school administration system that streamlines student and fee management, paving the way for digital transformation in educational institutions.

### Vision Statement
> To empower schools with an intuitive digital solution that automates daily operations and provides actionable insights for better decision-making.

### Strategic Goals
| Goal | Description | KPI |
|------|--------------|-----|
| Digital Transformation | Replace manual workflows with digital ones | 80% paper reduction |
| Financial Visibility | Provide real-time fee tracking and dues report | 100% accuracy |
| Operational Efficiency | Reduce fee processing time | 50% improvement |
| Data Integrity | Ensure single source of truth | 99% accuracy |

---

## 3. Stakeholders

| Role | Interest | Influence |
|------|-----------|------------|
| School Principal | Operational performance | High |
| Administrative Staff | Ease of use | High |
| Accounts Manager | Financial accuracy | High |
| IT Administrator | Data security & uptime | Medium |

---

## 4. Scope Overview

### In Scope (Phase 1)
- Student registration & management
- Class structure & enrollment
- Fee structure setup and management
- Fee journal & payment tracking
- Receipt generation & reporting
- Configuration management

---

## 5. Core Modules

| Module | Description | Key Outcomes |
|---------|--------------|---------------|
| **Student Management** | Handles student admission, profiles, and enrollment | Streamlined registration |
| **Class Management** | Defines classes, sections, and academic years | Accurate class allocations |
| **Fee Management** | Manages fee structures and payment cycles | Automated fee calculations |
| **Payment Tracking** | Tracks dues and payments per student | Real-time balance visibility |
| **Receipt Management** | Generates and retrieves receipts | Transparent transactions |
| **Configuration Module** | Maintains global school settings | Centralized control |

---

## 6. High-Level Functional Overview

### 6.1 Student Lifecycle
- Register new students with profile validation.
- Assign to classes based on availability.
- Update or archive profiles.

### 6.2 Fee Management
- Define fee structures by type and frequency.
- Automate monthly journal entries.
- Track payments, dues, and generate reports.

### 6.3 Reporting
- Provide dashboards for payments, dues, and enrollments.
- Export data for audit and compliance.

### 6.4 Security & Access
- Role-based access control (RBAC).
- Encrypted data storage and transmission.
- Multi-factor authentication for administrators.

---


## 7. Constraints & Assumptions

### Constraints
- Limited IT maintenance staff.
- Fixed initial development budget.
- Cloud hosting dependency.

### Assumptions
- Users have basic computer literacy.
- Reliable internet connectivity available.
- Initial data available for migration.

---

## 11. Roadmap

| Phase | Duration | Key Deliverables |
|--------|-----------|------------------|
| **Phase 1 (6 months)** | Core modules (Student, Class, Fees, Receipts) |

---

# Detailed Feature Requirements Document (DFRD)

**Product Name**: School Management System (SMS)  
**Version**: 1.0  
**Date**: November 6, 2025  
**Document Type**: Feature-Level Requirements  
**Author**: Product & Engineering Teams

---

## 1. Functional Modules Overview

### 1.1 Student Management
**Goal**: Manage the full student lifecycle from registration to graduation.

**Key Features**:
- Student registration with personal and guardian details.
- Unique student ID generation.
- Validation: age (3–18 years), unique mobile numbers.
- Class assignment with capacity check.
- Status tracking (Active, Inactive, Graduated, Transferred).
- Audit trail for all modifications.

**Acceptance Criteria**:
- Registration form validations enforced.
- Class capacity not exceeded.
- Audit logs for profile edits stored successfully.

---

### 1.2 Class Management
**Goal**: Configure academic structure, sections, and year rollover.

**Key Features**:
- Classes 1–10 with sections (A–Z).
- Academic year format: YYYY–YYYY.
- Year-end rollover automation.
- Enrollment statistics per class.

**Acceptance Criteria**:
- No duplicate class-section-year entries.
- Year rollover creates new class structures.
- Enrollment count accuracy verified.

---

### 1.3 Fee Structure Configuration
**Goal**: Flexible configuration of fees with rule-based calculation.

**Key Features**:
- Multiple fee types: tuition, library, computer, etc.
- Frequency: Monthly, Quarterly, Annual, One-time.
- Fee versioning with historical tracking.
- Automatic rule-based calculations.

**Acceptance Criteria**:
- Fee calculation accuracy tested for all frequencies.
- Versioning audit retained per academic year.

---

### 1.4 Fee Journal & Payment Tracking
**Goal**: Track dues, payments, and overdue statuses.

**Key Features**:
- Monthly fee journal generation per student.
- Payment status: Pending, Partial, Paid, Overdue, Waived.
- Dues and overdue reporting.
- Payment history per student.

**Acceptance Criteria**:
- Journal prevents duplicate entries.
- Payment updates status automatically.
- Reports show accurate balances.

---

### 1.5 Receipt Generation
**Goal**: Generate and manage receipts for all fee payments.

**Key Features**:
- Auto-generated receipt numbers (REC-YYYY-NNNNN).
- Supports cash and online payments.
- Includes fee breakdown and months covered.
- Search and filter by date, method, and student.

**Acceptance Criteria**:
- Unique receipt number format validated.
- Receipts include all mandatory data.
- Daily collection report accuracy verified.

---

### 1.6 Configuration Module
**Goal**: Centralize global school-level settings.

**Key Features**:
- Store school profile and branding.
- Configure academic and financial defaults.
- Version-controlled configuration changes.
- Key-value pair management (General, Academic, Financial).

**Acceptance Criteria**:
- Configuration changes tracked with timestamps.
- Non-editable fields protected.

---

## 2. Non-Functional Requirements

| Category | Requirement | Target |
|-----------|-------------|---------|
| Performance | API response time | < 200 ms |
| Availability | Uptime | 99.5% |
| Security | PII encryption | AES-256 |
| Compliance | Data retention | 7 years |
| UX | User satisfaction | 85% |

---

## 3. Data Model Overview

### 3.1 Student Table
| Field | Type | Validation |
|--------|------|------------|
| StudentID | Auto | Unique |
| Name | Text | 2–50 chars |
| DOB | Date | Age 3–18 yrs |
| Mobile | Numeric | Unique, 10 digits |

### 3.2 Fee Master Table
| Field | Type | Validation |
|--------|------|------------|
| FeeType | Enum | Predefined list |
| Amount | Decimal | Positive, 2 decimals |
| Frequency | Enum | Monthly/Quarterly/etc. |
| AcademicYear | Text | YYYY–YYYY |

### 3.3 Receipt Table
| Field | Type | Validation |
|--------|------|------------|
| ReceiptNo | Text | REC-YYYY-NNNNN |
| Amount | Decimal | > 0 |
| Method | Enum | Cash/Online |
| Date | Date | ≤ today |

---

## 4. Business Rules Summary

| ID | Rule | Description |
|----|------|-------------|
| BR-1 | Age Validation | Students must be 3–18 years old |
| BR-2 | Mobile Uniqueness | Mobile number unique per student |
| BR-3 | Class Capacity | Cannot exceed max class size |
| BR-5 | Fee Calculation | Based on class level and add-ons |
| BR-9 | Payment Validation | Paid ≤ Due amount |
| BR-11 | Receipt Generation | Sequential unique receipt numbers |

---

## 5. Reporting Requirements

| Report | Description |
|---------|--------------|
| Pending Fees Report | List of unpaid students per month |
| Overdue Report | Students with late payments |
| Monthly Collection Report | Total collection by payment mode |
| Class Enrollment Report | Active students per class |

---

## 6. Acceptance Criteria Summary

| Area | Criteria |
|-------|----------|
| Functional | All Phase 1 features operational |
| Performance | Response time < 200 ms |
| Security | No PII leaks; audit logging enabled |
| Usability | 85% positive user feedback |
| Reliability | 99.5% uptime maintained |

---

**End of Two-Tier Product Requirements Document**