# CLAUDE-FEATURE-STUDENT.md

**Tier 3: Student Feature Agent**

Complete implementation specification for Student Registration and Management feature.

---

## Feature Overview

**Feature Name**: Student Registration & Management

**Purpose**: Enable school administrators to register new students, manage student records, search students, and maintain complete student lifecycle from enrollment to graduation.

**User Roles**: Administrator, Office Staff

**Priority**: P0 (Core Feature)

---

## Feature Goals

### Primary Goals
1. **Streamline Enrollment**: Reduce student registration time from 30 minutes to 5 minutes
2. **Data Accuracy**: Ensure 100% data validation at point of entry
3. **Quick Search**: Enable sub-second student lookup by name or mobile
4. **Comprehensive Records**: Maintain complete student profile with family details

### Success Metrics
- Registration completion rate: >95%
- Search response time: <200ms
- Data validation error rate: <2%
- User satisfaction score: >4.5/5

---

## Data Model

### Student Entity

**Database Table**: `students`

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| **id** | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| **first_name** | VARCHAR(50) | NOT NULL | Student's first name |
| **last_name** | VARCHAR(50) | NOT NULL | Student's last name |
| **date_of_birth** | DATE | NOT NULL | Birth date (for age calculation) |
| **address** | VARCHAR(200) | NOT NULL | Current residential address |
| **caste** | VARCHAR(50) | NULLABLE | Caste category (for reporting) |
| **mobile** | VARCHAR(15) | NOT NULL, UNIQUE | Contact number (10 digits) |
| **religion** | VARCHAR(50) | NULLABLE | Religious affiliation |
| **moles_on_body** | VARCHAR(200) | NULLABLE | Physical identification marks |
| **mother_name** | VARCHAR(100) | NOT NULL | Mother's full name |
| **father_name** | VARCHAR(100) | NOT NULL | Father's full name |
| **class_id** | BIGINT | NOT NULL, FOREIGN KEY | Reference to school_classes.id |
| **enrollment_date** | DATE | NOT NULL | Date of admission |
| **status** | ENUM | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE, GRADUATED, TRANSFERRED |
| **created_at** | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| **updated_at** | TIMESTAMP | NOT NULL, AUTO | Last update timestamp |

**Indexes**:
- `idx_mobile` on `mobile` (UNIQUE)
- `idx_class_id` on `class_id`
- `idx_status` on `status`
- `idx_name` on `first_name, last_name` (for search)

**Relationships**:
- **Many-to-One**: Student → SchoolClass (A student belongs to one class)
- **One-to-Many**: Student ← FeeJournal (A student has multiple fee records)
- **One-to-Many**: Student ← FeeReceipt (A student has multiple receipts)

---

## Business Rules

### Validation Rules

1. **Age Constraints**:
   - Minimum age: 3 years (pre-primary)
   - Maximum age: 18 years for Class 10
   - Age must match class enrollment norms

2. **Mobile Number**:
   - Must be exactly 10 digits
   - Must be unique across all students
   - Must start with 6, 7, 8, or 9

3. **Name Requirements**:
   - First name: 2-50 characters, alphabets only
   - Last name: 2-50 characters, alphabets only
   - No special characters except spaces and hyphens

4. **Class Assignment**:
   - Class must exist in system
   - Class must not be at full capacity
   - Age should be appropriate for class level

5. **Address**:
   - Minimum 10 characters
   - Maximum 200 characters
   - Required field

### Enrollment Rules

1. **New Enrollment**:
   - Enrollment date defaults to current date
   - Status automatically set to ACTIVE
   - Must be assigned to a valid class
   - Parent details (mother/father name) mandatory

2. **Status Transitions**:
   - ACTIVE → INACTIVE (temporary leave)
   - ACTIVE → GRADUATED (completed Class 10)
   - ACTIVE → TRANSFERRED (moved to different school)
   - Cannot transition from GRADUATED back to ACTIVE

3. **Duplicate Prevention**:
   - Check mobile number uniqueness before registration
   - Warn if similar name exists in same class

---

## Backend Implementation

### API Endpoints

#### 1. Create Student
```
POST /api/students
```

**Request Body**:
```json
{
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "dateOfBirth": "2012-05-15",
  "address": "123 MG Road, Bangalore, Karnataka - 560001",
  "caste": "General",
  "mobile": "9876543210",
  "religion": "Hindu",
  "molesOnBody": "Small mole on left cheek",
  "motherName": "Priya Kumar",
  "fatherName": "Suresh Kumar",
  "classId": 5
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Student registered successfully",
  "data": {
    "id": 1,
    "firstName": "Rajesh",
    "lastName": "Kumar",
    "fullName": "Rajesh Kumar",
    "dateOfBirth": "2012-05-15",
    "age": 12,
    "address": "123 MG Road, Bangalore, Karnataka - 560001",
    "caste": "General",
    "mobile": "9876543210",
    "religion": "Hindu",
    "molesOnBody": "Small mole on left cheek",
    "motherName": "Priya Kumar",
    "fatherName": "Suresh Kumar",
    "classId": 5,
    "className": "Class 5",
    "enrollmentDate": "2024-10-26",
    "status": "ACTIVE",
    "createdAt": "2024-10-26T10:30:00"
  },
  "timestamp": "2024-10-26T10:30:00"
}
```

**Validation Errors** (400 Bad Request):
```json
{
  "success": false,
  "status": 400,
  "error": "Validation Error",
  "message": "Input validation failed",
  "fieldErrors": {
    "mobile": "Mobile number already exists",
    "firstName": "First name must be between 2 and 50 characters",
    "dateOfBirth": "Date of birth must be in the past"
  },
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 2. Get Student by ID
```
GET /api/students/{id}
```

**Response** (200 OK): Same structure as Create response

**Error** (404 Not Found):
```json
{
  "success": false,
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with id: 999",
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 3. Get All Students (with optional class filter)
```
GET /api/students
GET /api/students?classId=5
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "firstName": "Rajesh",
      "lastName": "Kumar",
      "fullName": "Rajesh Kumar",
      "classId": 5,
      "className": "Class 5",
      "mobile": "9876543210",
      "status": "ACTIVE"
    },
    // ... more students
  ],
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 4. Search Students (Autocomplete)
```
GET /api/students/search?q={query}
```

**Use Case**: Autocomplete search in Fee Receipt module

**Query Parameter**: `q` - Search query (minimum 3 characters)

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "firstName": "Rajesh",
      "lastName": "Kumar",
      "fullName": "Rajesh Kumar",
      "classId": 5,
      "className": "Class 5",
      "rollNumber": "5A-15",
      "mobile": "9876543210"
    },
    {
      "id": 2,
      "firstName": "Raj",
      "lastName": "Sharma",
      "fullName": "Raj Sharma",
      "classId": 8,
      "className": "Class 8",
      "rollNumber": "8A-22",
      "mobile": "9988776655"
    }
  ],
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 5. Update Student
```
PUT /api/students/{id}
```

**Request Body**: Same as Create (partial updates not supported)

**Response** (200 OK): Updated student object

#### 6. Delete Student
```
DELETE /api/students/{id}
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Student deleted successfully",
  "timestamp": "2024-10-26T10:30:00"
}
```

**Note**: Consider soft delete (status = INACTIVE) instead of hard delete for audit trail.

---

## Frontend Implementation

### Page: Student Registration

**Route**: `/students/register`

**Layout**: Full-page form with left-aligned labels

### UI Components Structure

```
StudentRegistration.jsx
├── PageHeader (Title + Breadcrumb)
├── StudentForm (Main Form)
│   ├── PersonalInformationSection
│   │   ├── FormInput (First Name)
│   │   ├── FormInput (Last Name)
│   │   └── FormDatePicker (Date of Birth)
│   ├── ContactInformationSection
│   │   ├── FormInput (Mobile)
│   │   └── FormTextArea (Address)
│   ├── FamilyInformationSection
│   │   ├── FormInput (Mother's Name)
│   │   └── FormInput (Father's Name)
│   ├── AdditionalDetailsSection
│   │   ├── FormInput (Caste)
│   │   ├── FormInput (Religion)
│   │   └── FormTextArea (Moles on Body)
│   └── ClassAssignmentSection
│       └── FormSelect (Select Class)
└── FormActions
    ├── Button (Submit - Primary)
    └── Button (Cancel - Secondary)
```

### UI/UX Specifications

#### Form Layout

**Design Pattern**: Two-column responsive form

```
┌─────────────────────────────────────────────────────────┐
│ Student Registration                      [? Help Icon] │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  Personal Information                                     │
│  ┌─────────────────────┐  ┌──────────────────────────┐ │
│  │ First Name *        │  │ Last Name *              │ │
│  │ [Rajesh___________] │  │ [Kumar_________________] │ │
│  └─────────────────────┘  └──────────────────────────┘ │
│                                                           │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Date of Birth *                                   │  │
│  │ [📅 15/05/2012]                                   │  │
│  └───────────────────────────────────────────────────┘  │
│                                                           │
│  Contact Information                                      │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Mobile Number * (10 digits)                       │  │
│  │ [9876543210________________________________]       │  │
│  └───────────────────────────────────────────────────┘  │
│                                                           │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Address *                                          │  │
│  │ [_____________________________________________]    │  │
│  │ [_____________________________________________]    │  │
│  │ [_____________________________________________]    │  │
│  └───────────────────────────────────────────────────┘  │
│                                                           │
│  Family Information                                       │
│  ┌─────────────────────┐  ┌──────────────────────────┐ │
│  │ Mother's Name *     │  │ Father's Name *          │ │
│  │ [Priya Kumar______] │  │ [Suresh Kumar__________] │ │
│  └─────────────────────┘  └──────────────────────────┘ │
│                                                           │
│  Additional Details                                       │
│  ┌─────────────────────┐  ┌──────────────────────────┐ │
│  │ Caste               │  │ Religion                 │ │
│  │ [General__________] │  │ [Hindu_________________] │ │
│  └─────────────────────┘  └──────────────────────────┘ │
│                                                           │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Identification Marks (Moles on Body)              │  │
│  │ [Small mole on left cheek___________________]    │  │
│  │ [_____________________________________________]    │  │
│  └───────────────────────────────────────────────────┘  │
│                                                           │
│  Class Assignment                                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Select Class *                                     │  │
│  │ [▼ Class 5________________________]               │  │
│  └───────────────────────────────────────────────────┘  │
│                                                           │
│  ┌──────────────────┐  ┌──────────────────────────────┐│
│  │ ← Cancel         │  │ ✓ Register Student          ││
│  └──────────────────┘  └──────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

#### Visual Design System

**Colors**:
- Primary: `#3B82F6` (Blue-500) - Action buttons
- Success: `#10B981` (Green-500) - Success messages
- Error: `#EF4444` (Red-500) - Error messages, validation
- Text Primary: `#111827` (Gray-900)
- Text Secondary: `#6B7280` (Gray-500)
- Border: `#D1D5DB` (Gray-300)
- Background: `#F9FAFB` (Gray-50)

**Typography**:
- Page Title: `text-2xl font-bold` (24px, Bold)
- Section Headers: `text-lg font-semibold` (18px, Semi-bold)
- Form Labels: `text-sm font-medium` (14px, Medium)
- Input Text: `text-base` (16px, Regular)
- Help Text: `text-xs text-gray-500` (12px, Regular)

**Spacing**:
- Section Gaps: `space-y-6` (24px)
- Field Gaps: `space-y-4` (16px)
- Input Padding: `px-3 py-2` (12px horizontal, 8px vertical)

**Form Inputs**:
- Height: `40px`
- Border Radius: `6px`
- Border: `1px solid #D1D5DB`
- Focus: `ring-2 ring-blue-500 border-blue-500`
- Error State: `border-red-500 ring-red-500`

#### Validation Feedback

**Real-time Validation** (on blur):
- Show error message below field
- Change border color to red
- Display error icon (❌) in field

**Example Error States**:
```
┌───────────────────────────────────────────────────┐
│ Mobile Number * (10 digits)                       │
│ [1234567❌_____________________________]  ←Red   │
│ ❌ Mobile number must be exactly 10 digits       │
└───────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────┐
│ Mobile Number * (10 digits)                       │
│ [9876543210❌_____________________________] ←Red  │
│ ❌ Mobile number already registered               │
└───────────────────────────────────────────────────┘
```

**Success State**:
```
┌───────────────────────────────────────────────────┐
│ Mobile Number * (10 digits)                       │
│ [9876543210✓_____________________________] ←Green│
└───────────────────────────────────────────────────┘
```

#### Button States

**Submit Button**:
- Default: `bg-blue-600 text-white hover:bg-blue-700`
- Loading: `bg-blue-400 cursor-not-allowed` + Spinner icon
- Disabled: `bg-gray-300 cursor-not-allowed`

**Cancel Button**:
- Default: `bg-white border border-gray-300 hover:bg-gray-50`

#### Notifications

**Success Toast** (Top-right corner):
```
┌────────────────────────────────────────┐
│ ✅ Success                             │
│ Student registered successfully!       │
│ Roll No: 5A-15 | ID: #001             │
└────────────────────────────────────────┘
```

**Error Toast**:
```
┌────────────────────────────────────────┐
│ ❌ Error                               │
│ Failed to register student             │
│ Please check all fields and try again  │
└────────────────────────────────────────┘
```

### Page: Student Management

**Route**: `/students`

**Layout**: Data table with search and filters

#### UI Structure

```
┌─────────────────────────────────────────────────────────┐
│ Students                         [+ New Student] Button │
├─────────────────────────────────────────────────────────┤
│ [🔍 Search by name or mobile___] [Filter: All Classes▼]│
├─────────────────────────────────────────────────────────┤
│ ID │ Name          │ Class │ Mobile     │ Status │ ••• │
├────┼───────────────┼───────┼────────────┼────────┼─────┤
│ 1  │ Rajesh Kumar  │ 5     │ 9876543210 │ Active │ ⋮   │
│ 2  │ Priya Sharma  │ 8     │ 9988776655 │ Active │ ⋮   │
│ 3  │ Amit Patel    │ 10    │ 9876512345 │ Active │ ⋮   │
└─────────────────────────────────────────────────────────┘
  Showing 1-10 of 245 students        [< 1 2 3 ... 25 >]
```

**Actions Menu** (⋮):
- View Details
- Edit Student
- View Fee History
- Mark Inactive
- Delete (with confirmation)

---

## Frontend Code Structure

### Component Hierarchy

```javascript
// StudentRegistration.jsx
import StudentForm from '@components/forms/StudentForm';
import PageLayout from '@components/layout/PageLayout';

// StudentManagement.jsx
import StudentTable from '@components/tables/StudentTable';
import SearchBar from '@components/common/SearchBar';
import FilterDropdown from '@components/common/FilterDropdown';
```

### Key Implementation Code

**StudentForm.jsx** (Simplified):
```javascript
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { studentService } from '@services/studentService';
import { useNotification } from '@contexts/NotificationContext';
import FormInput from '@components/forms/FormInput';
import FormDatePicker from '@components/forms/FormDatePicker';
import FormTextArea from '@components/forms/FormTextArea';
import FormSelect from '@components/forms/FormSelect';

export default function StudentForm() {
  const navigate = useNavigate();
  const { showSuccess, showError } = useNotification();
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    address: '',
    caste: '',
    mobile: '',
    religion: '',
    molesOnBody: '',
    motherName: '',
    fatherName: '',
    classId: '',
  });
  
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    try {
      const response = await studentService.createStudent(formData);
      showSuccess(`Student registered successfully! Roll No: ${response.data.rollNumber}`);
      navigate('/students');
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed';
      showError(message);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <form onSubmit={handleSubmit} className="max-w-4xl mx-auto p-6">
      {/* Form sections... */}
    </form>
  );
}
```

---

## Testing Strategy

### Backend Tests

**Unit Tests** (StudentServiceTest.java):
```java
@Test
void shouldCreateStudentSuccessfully()
@Test
void shouldThrowExceptionWhenMobileExists()
@Test
void shouldThrowExceptionWhenClassNotFound()
@Test
void shouldThrowExceptionWhenClassFull()
@Test
void shouldUpdateStudentSuccessfully()
@Test
void shouldSearchStudentsByName()
```

**Integration Tests** (StudentRepositoryTest.java):
```java
@Test
void shouldSaveStudentToDatabase()
@Test
void shouldFindStudentByMobile()
@Test
void shouldSearchStudentsByNameIgnoringCase()
```

### Frontend Tests

**Component Tests** (StudentForm.test.jsx):
```javascript
test('renders all form fields', () => {});
test('shows validation errors on empty submit', () => {});
test('calls API on valid form submission', () => {});
test('displays success message after registration', () => {});
test('handles API error gracefully', () => {});
```

---

## User Workflows

### Workflow 1: Register New Student

1. Admin clicks "+ New Student" button
2. System navigates to registration form
3. Admin fills mandatory fields (marked with *)
4. System validates each field on blur
5. Admin clicks "Register Student"
6. System validates entire form
7. System calls POST /api/students
8. Backend validates business rules
9. Backend saves to database
10. System shows success notification with roll number
11. System navigates to student list

**Edge Cases**:
- Mobile number already exists → Show error, suggest viewing existing record
- Class at full capacity → Show error with alternative class suggestions
- Network failure → Show retry option

### Workflow 2: Search and View Student

1. User enters search query (min 3 characters)
2. System debounces input (300ms)
3. System calls GET /api/students/search?q=raj
4. System displays autocomplete dropdown
5. User selects student from dropdown
6. System navigates to student detail view

---

## Performance Considerations

### Backend Optimization
- **Database Indexing**: Index on `mobile`, `first_name`, `last_name` for fast search
- **Pagination**: Use `Pageable` for student list (default 20 per page)
- **Lazy Loading**: Use `FetchType.LAZY` for class relationship
- **Query Optimization**: Use `@EntityGraph` to avoid N+1 queries

### Frontend Optimization
- **Debounced Search**: 300ms delay before API call
- **Virtual Scrolling**: For large student lists (>100 students)
- **Form Auto-save**: Save draft to localStorage every 30 seconds
- **Image Optimization**: Compress student photos to <100KB

---

## Accessibility

- All form fields have associated `<label>` elements
- Use `aria-label` for icon buttons
- Support keyboard navigation (Tab, Enter, Escape)
- Provide clear error messages with `aria-invalid`
- Ensure color contrast ratio ≥ 4.5:1

---

## Security Considerations

- Validate mobile number format on both frontend and backend
- Sanitize all text inputs to prevent XSS
- Use parameterized queries to prevent SQL injection
- Implement rate limiting on search endpoint (max 10/minute)
- Hash/encrypt sensitive data if storing photos

---

## Future Enhancements

1. **Photo Upload**: Add student photo with face detection
2. **Bulk Import**: CSV upload for batch student registration
3. **Family Linking**: Link siblings with shared parent information
4. **Attendance Integration**: Quick-access to attendance from student profile
5. **Document Upload**: Store scanned documents (birth certificate, ID proof)
6. **SMS Notifications**: Send registration confirmation to parents

---

## Related Features

- **Class Management**: For class capacity checks
- **Fee Journal**: View fee payment history
- **Fee Receipt**: Generate receipts for this student
- **Parent Portal**: Parents view student details

---

**Agent Directive**: This is a Tier 3 feature agent. Use alongside CLAUDE-BACKEND.md and CLAUDE-FRONTEND.md for complete implementation guidance.
