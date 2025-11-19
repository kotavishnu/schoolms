## 1. Product Summary
The School Management System (SMS) is a web-based digital platform designed to automate and optimize administrative workflows within schools. It provides a unified interface for managing student registration and school configuration.

## 2. Scope (Phase 1)
In Scope:
- Student registration and profile management
- Student status updates (Active, Inactive)
- School configuration (key-value settings: General, Academic, Financial)

## 3. User Personas
- School Administrator - The users in this role will have adminstrative privileges to administer school configuration and workflows
- Clerical Staff - The users in this role will be using this product for day to day operations

## 4. Core Features
### 4.1 Student Management:
- CRUD services such as create a student record, read a student record, edit a student profile, delete a student record. 
#### 4.1.1 Create student record
- Allows creations of a student record with personal information such as :
1. �First Name
2. �Last Name
3. �Address
4. �Mobile
5. �Father's Name/Guardian
6. �Mother's Name
7. �Identification Mark
8. �Email �

Upon successful creation of a student record, an auto-generated Student ID will be assigned to this record
#### 4.1.2 Edit student profile
- Allows edits to a student record using a student key
#### 4.1.3 Delete student profile
- Allows deletion of a student record using a student key

#### 4.1.4 List/search students
- Allows listing of student records with summary information using student's last name or guardian's name
- Allows searching for details on a specific student record using student key

### 4.3 School Configuration:
- Store school profile (name, code, logo placeholder)
- Manage key-value settings grouped by category

## 5. Functional Requirements
Student Registration:
- System assigns StudentID
- Default status: Active
- Validation Rules:
� � - Age between 3 and 18 at registration
� � - Mobile unique per student
� � - Only following fields are allowed for edits to a student record:
� � � � - Name, Mobile, Status (Active/Inactive)

School Configuration:
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
- Identification Mark
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
- All registered students to have a valid system generated student ID
- Cannot register students outside age range.
- Duplicate mobile rejected.
- Configuration retrieval returns grouped settings.

## 8. Assumptions
- Single school instance (no multi-tenant).
- No authentication scope in Phase 1 (handled externally).
- Student and Configuration to be built in a microservices style architecture

