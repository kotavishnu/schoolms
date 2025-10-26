# CLAUDE-FEATURE-CLASS.md

**Tier 3: Class Feature Agent**

Complete implementation specification for Class Management feature.

---

## Feature Overview

**Feature Name**: Class Management

**Purpose**: Manage school class structure (Classes 1-10), sections, capacity, and academic year assignments.

**User Roles**: Administrator, Principal

**Priority**: P0 (Core Feature - Pre-configured, minimal management needed)

---

## Feature Goals

### Primary Goals
1. **Pre-configured Setup**: Classes 1-10 automatically created during system initialization
2. **Capacity Management**: Track and enforce student enrollment limits
3. **Academic Year Tracking**: Associate classes with specific academic years
4. **Section Support**: Enable multiple sections per class (A, B, C, etc.)

### Success Metrics
- System initialization time: <5 seconds
- Capacity check accuracy: 100%
- Section assignment errors: 0%

---

## Data Model

### SchoolClass Entity

**Database Table**: `school_classes`

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| **id** | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| **class_number** | INTEGER | NOT NULL, CHECK (1-10) | Class number (1 to 10) |
| **section** | VARCHAR(10) | NOT NULL, DEFAULT 'A' | Section identifier (A, B, C) |
| **academic_year** | VARCHAR(20) | NOT NULL | Format: "2024-2025" |
| **capacity** | INTEGER | NOT NULL, DEFAULT 50 | Maximum students allowed |
| **current_enrollment** | INTEGER | DEFAULT 0 | Current student count (computed) |
| **class_teacher** | VARCHAR(100) | NULLABLE | Assigned class teacher name |
| **room_number** | VARCHAR(20) | NULLABLE | Classroom location |
| **created_at** | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| **updated_at** | TIMESTAMP | NOT NULL, AUTO | Last update timestamp |

**Indexes**:
- `idx_class_year` on `class_number, academic_year` (UNIQUE composite)
- `idx_academic_year` on `academic_year`

**Relationships**:
- **One-to-Many**: SchoolClass → Student (A class contains multiple students)

**Unique Constraint**: 
- `(class_number, section, academic_year)` must be unique

---

## Business Rules

### Initialization Rules

1. **Auto-Create Classes**: On first system startup, create Classes 1-10, Section A, capacity 50
2. **Academic Year**: Default to current academic year (Apr-Mar cycle)
3. **Default Capacity**: 50 students per section (configurable)

### Capacity Management

1. **Enrollment Check**: Before assigning student to class, verify `current_enrollment < capacity`
2. **Real-time Count**: Update `current_enrollment` on student add/remove
3. **Overflow Handling**: If capacity reached, suggest creating new section

### Academic Year Validation

1. **Format**: "YYYY-YYYY" (e.g., "2024-2025")
2. **Transition**: Academic year changes on April 1st
3. **Historical Data**: Maintain old academic year classes for reporting

---

## Backend Implementation

### API Endpoints

#### 1. Get All Classes
```
GET /api/classes
GET /api/classes?academicYear=2024-2025
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "classNumber": 1,
      "section": "A",
      "academicYear": "2024-2025",
      "capacity": 50,
      "currentEnrollment": 32,
      "availableSeats": 18,
      "classTeacher": "Mrs. Sharma",
      "roomNumber": "101"
    },
    {
      "id": 2,
      "classNumber": 2,
      "section": "A",
      "academicYear": "2024-2025",
      "capacity": 50,
      "currentEnrollment": 45,
      "availableSeats": 5,
      "classTeacher": "Mr. Patel",
      "roomNumber": "102"
    }
  ],
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 2. Get Class by ID
```
GET /api/classes/{id}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": 5,
    "classNumber": 5,
    "section": "A",
    "academicYear": "2024-2025",
    "capacity": 50,
    "currentEnrollment": 38,
    "availableSeats": 12,
    "classTeacher": "Mrs. Reddy",
    "roomNumber": "105",
    "students": [
      {
        "id": 1,
        "fullName": "Rajesh Kumar",
        "rollNumber": "5A-15"
      }
    ]
  },
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 3. Create Class (Admin Only)
```
POST /api/classes
```

**Request Body**:
```json
{
  "classNumber": 5,
  "section": "B",
  "academicYear": "2024-2025",
  "capacity": 45,
  "classTeacher": "Ms. Gupta",
  "roomNumber": "105B"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Class created successfully",
  "data": {
    "id": 15,
    "classNumber": 5,
    "section": "B",
    "academicYear": "2024-2025",
    "capacity": 45,
    "currentEnrollment": 0,
    "availableSeats": 45,
    "classTeacher": "Ms. Gupta",
    "roomNumber": "105B"
  },
  "timestamp": "2024-10-26T10:30:00"
}
```

#### 4. Update Class
```
PUT /api/classes/{id}
```

**Request Body**: Same as Create (partial updates supported)

#### 5. Check Class Availability
```
GET /api/classes/{id}/availability
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "classId": 5,
    "className": "Class 5 - A",
    "capacity": 50,
    "currentEnrollment": 48,
    "availableSeats": 2,
    "isAvailable": true,
    "warnings": [
      "Only 2 seats remaining. Consider opening Section B."
    ]
  },
  "timestamp": "2024-10-26T10:30:00"
}
```

---

## Frontend Implementation

### Page: Class Management

**Route**: `/classes`

**Layout**: Card grid with class summary

#### UI Structure

```
┌─────────────────────────────────────────────────────────┐
│ Class Management                       [+ New Section]  │
│                                                          │
│ Academic Year: [2024-2025 ▼]             Filter: [All] │
├─────────────────────────────────────────────────────────┤
│                                                          │
│ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│ │ Class 1       │ │ Class 2       │ │ Class 3       │ │
│ │ Section A     │ │ Section A     │ │ Section A     │ │
│ ├───────────────┤ ├───────────────┤ ├───────────────┤ │
│ │ 32/50 Students│ │ 45/50 Students│ │ 28/50 Students│ │
│ │ 🟢 Available  │ │ 🟡 Almost Full│ │ 🟢 Available  │ │
│ │               │ │               │ │               │ │
│ │ Room: 101     │ │ Room: 102     │ │ Room: 103     │ │
│ │ Teacher:      │ │ Teacher:      │ │ Teacher:      │ │
│ │ Mrs. Sharma   │ │ Mr. Patel     │ │ Ms. Verma     │ │
│ │               │ │               │ │               │ │
│ │ [View Details]│ │ [View Details]│ │ [View Details]│ │
│ └───────────────┘ └───────────────┘ └───────────────┘ │
│                                                          │
│ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│ │ Class 4       │ │ Class 5       │ │ Class 6       │ │
│ │ Section A     │ │ Section A     │ │ Section A     │ │
│ ├───────────────┤ ├───────────────┤ ├───────────────┤ │
│ │ 50/50 Students│ │ 38/50 Students│ │ 41/50 Students│ │
│ │ 🔴 Full       │ │ 🟢 Available  │ │ 🟢 Available  │ │
│ │               │ │               │ │               │ │
│ │ Room: 104     │ │ Room: 105     │ │ Room: 201     │ │
│ │ Teacher:      │ │ Teacher:      │ │ Teacher:      │ │
│ │ Mr. Singh     │ │ Mrs. Reddy    │ │ Ms. Khan      │ │
│ │               │ │               │ │               │ │
│ │ [View Details]│ │ [View Details]│ │ [View Details]│ │
│ └───────────────┘ └───────────────┘ └───────────────┘ │
└─────────────────────────────────────────────────────────┘
```

#### Visual Design

**Class Card**:
- Width: 300px
- Height: 250px
- Border: 1px solid #E5E7EB
- Border Radius: 8px
- Padding: 20px
- Shadow: hover:shadow-lg

**Status Indicators**:
- 🟢 Available: <80% capacity, Green (#10B981)
- 🟡 Almost Full: 80-95% capacity, Yellow (#F59E0B)
- 🔴 Full: ≥95% capacity, Red (#EF4444)

**Progress Bar** (Student Enrollment):
```
32/50 Students
[████████████░░░░░░░░] 64%
```

### Page: Class Details

**Route**: `/classes/{id}`

**Layout**: Detail view with student list

```
┌─────────────────────────────────────────────────────────┐
│ ← Back to Classes                                       │
│                                                          │
│ Class 5 - Section A (2024-2025)                         │
├─────────────────────────────────────────────────────────┤
│ Overview                                                 │
│ ┌──────────────────┐ ┌──────────────────┐             │
│ │ Capacity         │ │ Class Teacher    │             │
│ │ 50 students      │ │ Mrs. Reddy       │             │
│ └──────────────────┘ └──────────────────┘             │
│                                                          │
│ ┌──────────────────┐ ┌──────────────────┐             │
│ │ Current Students │ │ Room Number      │             │
│ │ 38 (76%)         │ │ 105              │             │
│ └──────────────────┘ └──────────────────┘             │
│                                                          │
│ Enrollment Trend                                         │
│ [Line graph showing enrollment over months]             │
│                                                          │
│ Student List                        [+ Add Student]     │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Roll │ Name          │ Mobile     │ Status │ ••• │ │
│ ├──────┼───────────────┼────────────┼────────┼─────┤ │
│ │ 5A-1 │ Rajesh Kumar  │ 9876543210 │ Active │ ⋮   │ │
│ │ 5A-2 │ Priya Sharma  │ 9988776655 │ Active │ ⋮   │ │
│ └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## Database Seeding

**DataInitializer.java**:

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final ClassRepository classRepository;
    
    @Override
    public void run(String... args) {
        if (classRepository.count() == 0) {
            String academicYear = getCurrentAcademicYear();
            
            for (int i = 1; i <= 10; i++) {
                SchoolClass schoolClass = SchoolClass.builder()
                    .classNumber(i)
                    .section("A")
                    .academicYear(academicYear)
                    .capacity(50)
                    .currentEnrollment(0)
                    .build();
                
                classRepository.save(schoolClass);
            }
            
            log.info("Initialized Classes 1-10 for academic year {}", academicYear);
        }
    }
    
    private String getCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        
        // Academic year starts April 1
        if (today.getMonthValue() >= 4) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
}
```

---

## Service Layer Logic

**Key Method: updateEnrollmentCount**

```java
@Service
@Transactional
public class ClassService {
    
    @Transactional
    public void updateEnrollmentCount(Long classId) {
        SchoolClass schoolClass = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        
        long count = studentRepository.countBySchoolClassId(classId);
        schoolClass.setCurrentEnrollment((int) count);
        
        classRepository.save(schoolClass);
    }
    
    public boolean hasAvailableSeats(Long classId) {
        SchoolClass schoolClass = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        
        return schoolClass.getCurrentEnrollment() < schoolClass.getCapacity();
    }
    
    public int getAvailableSeats(Long classId) {
        SchoolClass schoolClass = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        
        return schoolClass.getCapacity() - schoolClass.getCurrentEnrollment();
    }
}
```

---

## Testing Strategy

### Backend Tests

```java
@Test
void shouldInitializeClassesOnStartup()
@Test
void shouldCheckAvailableSeats()
@Test
void shouldPreventEnrollmentWhenClassFull()
@Test
void shouldUpdateEnrollmentCountAfterStudentAdd()
@Test
void shouldSuggestNewSectionWhenCapacityReached()
```

### Frontend Tests

```javascript
test('displays all classes in grid', () => {});
test('shows correct capacity indicator', () => {});
test('filters classes by academic year', () => {});
test('navigates to class details on click', () => {});
```

---

## User Workflows

### Workflow: View Class Capacity

1. Admin opens Class Management page
2. System loads all classes with current enrollment
3. Admin sees visual capacity indicators
4. Admin clicks class card
5. System shows detailed student list

---

## Performance Considerations

- **Caching**: Cache class list for 5 minutes (rarely changes)
- **Computed Field**: `currentEnrollment` updated via database trigger or scheduled job
- **Lazy Loading**: Load student list only when viewing class details

---

**Agent Directive**: This is a Tier 3 feature agent for Class Management.
