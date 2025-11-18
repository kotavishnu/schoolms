## 1. Product Summary
The School Management System (SMS) is a web-based digital platform designed to automate and optimize administrative workflows within schools. It provides a unified interface for managing student registration and school configuration.

## 2. Scope (Phase 1)
In Scope:
- Student registration and profile management
- Student status updates (Active, Inactive)
- School configuration (key-value settings: General, Academic, Financial)

## 3. Users
- School Administrator
- Clerical Staff

## 4. Core Features
Student Management:
- Create student with personal information such as :
1.	First Name
2.	Last Name
3.	Address
4.	Mobile
5.	Father Name/Guardian
6.	Mother Name
7.	Caste
8.	Moles on body
9.	Email
- Auto-generate unique Student ID
- Edit student profile
- List/search students

Configuration:
- Store school profile (name, code, logo placeholder)
- Manage key-value settings grouped by category

## 5. Functional Requirements
Student Registration:
- Required: Name (2–50 chars), DOB (age 3–18), Mobile (10 digits, unique)
- System assigns StudentID
- Default status: Active
Validation Rules:
- Age between 3 and 18 at registration
- Mobile unique per student
Student Update:
- Editable: Name, Mobile, Status (Active/Inactive)

Configuration:
- CRUD key-value pairs (category, key, value)
- Retrieve settings by category

## 6. Data Model (Simplified)
Student:
- StudentID (auto)
- Name
- DOB
- Mobile
- Father Name/Guardian
- Mother Name
- Caste
- Moles on body
- Adhaar Number
- Email
- Status
- CreatedAt / UpdatedAt
ConfigurationSetting:
- SettingID (auto)
- Category
- Key
- Value
- UpdatedAt

## 7. Acceptance Criteria
- Cannot register students outside age range.
- Duplicate mobile rejected.
- Configuration retrieval returns grouped settings.

## 8. Assumptions
- Single school instance (no multi-tenant).
- No authentication scope in Phase 1 (handled externally).
- Student and Configuration as a microservices