# Phase 5 Handoff Document: Frontend Development

**From Phase:** 2 - SDLC Planning
**To Phase:** 5 - Frontend Development
**Target Agent:** senior-frontend-developer
**Date:** 2026-01-22
**Note:** Running in parallel with Phase 3 (Backend Development)

---

## Objective

The senior-frontend-developer agent must implement the React TypeScript frontend following the task breakdown in FRONTEND_TASKS.md and adhering to all architectural patterns defined in Phase 1.

---

## Context Provided

### Preserved Artifacts

#### Requirements & Architecture
- `specs/REQUIREMENTS.md` - Core product requirements
- `specs/architecture/06-frontend-implementation-guide.md` - **PRIMARY REFERENCE**
- `specs/architecture/02-database-design.md` - For field alignment matrix
- `FRONTEND_DESIGN_SPECIFICATION.md` - UI specifications (if exists in specs/)

#### Task Breakdown
- `docs/tasks/FRONTEND_TASKS.md` - **EXECUTION CHECKLIST (28 tasks: FE-001 to FE-028)**
- `docs/tasks/TASK_DEPENDENCIES.md` - Dependency graph
- `docs/tasks/QA_TASKS.md` - For understanding test requirements

#### Reference Materials
- `screenshots/` - Visual validation targets (pixel-perfect requirement)
- `frontend/reference-code/` - Reference implementations (if exists)

---

## Execution Model

### Task Sequence
Follow FRONTEND_TASKS.md sequentially: **FE-001 → FE-002 → ... → FE-028**

### Critical Path
```
FE-001 → FE-002 → FE-003 → FE-004 → FE-005 → FE-006 → FE-008 → FE-011 → FE-017 → FE-019 → FE-024
```

**Bottleneck:** FE-017 (StudentDialog) - Complex form with validation

### Parallel Execution Opportunities
- **After FE-007:** Student UI (FE-008 to FE-019) and Configuration UI (FE-020 to FE-023) can partially overlap
- **FE-024 to FE-028:** Optimization tasks can run after main features complete

---

## STRICT CONSTRAINTS (MANDATORY)

### 1. NO CUSTOM STYLES RULE

**FORBIDDEN:**
- Creating CSS files
- Creating SASS/SCSS files
- Using styled-components
- Writing inline styles (except dynamic values)
- Creating custom Tailwind classes

**MANDATORY:**
- Use ONLY Tailwind CSS 4 utility classes
- Use ONLY Shadcn/ui components
- Copy exact Tailwind classes from FRONTEND_DESIGN_SPECIFICATION.md

**Violation:** Code will be rejected in QA phase

### 2. Field Mapping Layer (D-007)

**CRITICAL:** Frontend-Backend field name misalignment MUST be handled in service layer.

| Frontend Field | Backend Field | Mapping Location |
|----------------|---------------|------------------|
| `phone` | `mobile` | studentService.ts |
| `id` | `studentId` | studentService.ts |
| `adhaarNumber` | `aadhaarNumber` | studentService.ts |

**Implementation Pattern:**
```typescript
// In studentService.ts

// Frontend → Backend (API request)
const createStudent = async (student: Student) => {
  const backendRequest = {
    mobile: student.phone,  // Transform
    studentId: student.id,  // Transform
    aadhaarNumber: student.adhaarNumber,  // Transform
    ...otherFields
  };
  const response = await axios.post('/api/v1/students', backendRequest);
  return mapBackendToFrontend(response.data);
};

// Backend → Frontend (API response)
const mapBackendToFrontend = (backend: any): Student => ({
  phone: backend.mobile,  // Transform
  id: backend.studentId,  // Transform
  adhaarNumber: backend.aadhaarNumber,  // Transform
  ...otherFields
});
```

### 3. Technology Stack (EXACT Versions)

```json
{
  "react": "18.3.x",
  "typescript": "5.x",
  "vite": "5.x",
  "tailwindcss": "4.x",
  "@radix-ui/react-*": "latest",
  "react-hook-form": "7.x",
  "zod": "3.x",
  "axios": "1.x",
  "react-router-dom": "6.x"
}
```

### 4. Directory Structure (ENFORCED)

```
frontend/
├── src/
│   ├── components/     # Reusable UI components (Button, Input, Table, etc.)
│   ├── pages/          # Route pages (StudentList, StudentForm, ConfigurationList)
│   ├── services/       # API clients (studentService.ts, configService.ts)
│   ├── hooks/          # Custom React hooks (useStudents, useDebounce)
│   ├── types/          # TypeScript interfaces (Student, Configuration)
│   ├── utils/          # Helper functions
│   ├── App.tsx         # Main app component
│   ├── main.tsx        # Entry point
│   └── index.css       # Tailwind imports ONLY
├── public/
├── index.html
├── vite.config.ts
├── tailwind.config.js
├── tsconfig.json
└── package.json
```

### 5. Zod Validation Rules (MUST MATCH BACKEND)

```typescript
// In types/student.ts or validation schema

const studentSchema = z.object({
  firstName: z.string().min(1, "First name is required"),
  lastName: z.string().min(1, "Last name is required"),
  dateOfBirth: z.date()
    .refine(date => {
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, "Age must be between 3 and 18 years"),
  phone: z.string()
    .regex(/^\d{10}$/, "Phone must be 10 digits")
    .min(10).max(10),
  email: z.string().email("Invalid email").optional(),
  status: z.enum(["Active", "Inactive"]).default("Active"),
  fatherName: z.string().optional(),
  motherName: z.string().optional(),
  identificationMark: z.string().optional(),
  adhaarNumber: z.string()
    .regex(/^\d{12}$/, "Aadhaar must be 12 digits")
    .optional(),
});
```

**Reference:** specs/architecture/03-business-rules.md (BR-STU-001 to BR-STU-007)

---

## API Endpoints (Backend Contract)

### Student Service (http://localhost:8081)

```
POST   /api/v1/students              - Create student
GET    /api/v1/students              - List students (with search)
GET    /api/v1/students/{studentId}  - Get student by ID
PUT    /api/v1/students/{studentId}  - Update student
DELETE /api/v1/students/{studentId}  - Delete student
PATCH  /api/v1/students/{studentId}/status - Update status
```

### Configuration Service (http://localhost:8082)

```
POST   /api/v1/configurations                 - Create configuration
GET    /api/v1/configurations                 - List all
GET    /api/v1/configurations/{settingId}     - Get by ID
GET    /api/v1/configurations/category/{cat}  - Get by category
PUT    /api/v1/configurations/{settingId}     - Update
DELETE /api/v1/configurations/{settingId}     - Delete
```

---

## Implementation Patterns

### Axios Service Layer with Interceptors

```typescript
// src/services/apiClient.ts

import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - add correlation ID
apiClient.interceptors.request.use(config => {
  const correlationId = uuidv4();
  config.headers['X-Correlation-ID'] = correlationId;
  console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
  return config;
});

// Response interceptor - error handling
apiClient.interceptors.response.use(
  response => response,
  error => {
    const message = error.response?.data?.message || 'An error occurred';
    console.error(`API Error: ${message}`, error);
    return Promise.reject(error);
  }
);

export default apiClient;
```

### React Hook Form with Zod

```typescript
// In StudentForm component

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema } from '@/types/student';

const StudentForm = () => {
  const form = useForm({
    resolver: zodResolver(studentSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      status: 'Active',
      // ...
    },
  });

  const onSubmit = async (data: Student) => {
    try {
      await studentService.createStudent(data);
      toast.success('Student created successfully');
    } catch (error) {
      toast.error('Failed to create student');
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        {/* Form fields */}
      </form>
    </Form>
  );
};
```

---

## Shadcn/ui Components to Install

Required components (install via CLI):

```bash
npx shadcn-ui@latest add button
npx shadcn-ui@latest add input
npx shadcn-ui@latest add table
npx shadcn-ui@latest add dialog
npx shadcn-ui@latest add form
npx shadcn-ui@latest add select
npx shadcn-ui@latest add toast
npx shadcn-ui@latest add skeleton
npx shadcn-ui@latest add card
npx shadcn-ui@latest add label
npx shadcn-ui@latest add alert
```

**Reference:** docs/tasks/FRONTEND_TASKS.md (FE-003)

---

## Expected Deliverables

### Code Artifacts

1. **Vite React Project** (FE-001)
   - React 18.3, TypeScript 5, Vite 5
   - Proper tsconfig.json configuration

2. **Tailwind CSS Setup** (FE-002)
   - Tailwind CSS 4 configured
   - Only utility classes used

3. **Shadcn/ui Components** (FE-003)
   - All required components installed
   - Configured in components/ui/

4. **Directory Structure** (FE-004)
   - components/, pages/, services/, hooks/, types/, utils/

5. **Service Layer** (FE-005, FE-011, FE-022)
   - apiClient.ts with interceptors
   - studentService.ts with field mapping
   - configService.ts

6. **TypeScript Types** (FE-006)
   - Student interface
   - Configuration interface
   - API response types

7. **Student Management UI** (FE-008 to FE-019)
   - StudentList page with search
   - StudentTable component
   - StudentDialog (create/edit)
   - StudentForm with React Hook Form + Zod
   - Delete confirmation
   - Error handling

8. **Configuration Management UI** (FE-020 to FE-023)
   - ConfigurationList page
   - ConfigurationTable with category grouping
   - Configuration CRUD dialog

9. **Optimization** (FE-024 to FE-027)
   - Code splitting (React.lazy)
   - Debounced search
   - Memoization
   - Loading states (Skeleton)

10. **Screenshot Validation** (FE-028)
    - Compare against screenshots/ directory
    - Pixel-perfect match documented

---

## Success Criteria

### Functional
- ✅ All 28 frontend tasks (FE-001 to FE-028) completed
- ✅ Student CRUD operations working (create, read, update, delete)
- ✅ Configuration CRUD operations working
- ✅ Search functionality (by lastName or guardian)
- ✅ Form validation (all Zod rules enforced)
- ✅ Error handling (toast notifications)
- ✅ Loading states (skeleton components)

### Technical
- ✅ NO custom CSS files (only Tailwind + Shadcn/ui)
- ✅ Field mapping in service layer (phone↔mobile, id↔studentId) - D-007
- ✅ Zod validation matches backend Drools rules - D-008
- ✅ Directory structure enforced
- ✅ TypeScript strict mode enabled
- ✅ Axios interceptors with correlation IDs

### Visual
- ✅ Shadcn/ui components used exclusively
- ✅ Responsive design (mobile, tablet, desktop)
- ✅ Screenshot validation passed (pixel-perfect) - D-009
- ✅ Consistent styling across all pages

---

## Files to Reference During Implementation

### Primary References (MUST READ)
1. **docs/tasks/FRONTEND_TASKS.md** - Task checklist (FE-001 to FE-028)
2. **specs/architecture/06-frontend-implementation-guide.md** - Implementation patterns
3. **screenshots/** - Visual validation targets

### Secondary References
4. **specs/architecture/02-database-design.md** - Field alignment matrix (section 5)
5. **docs/tasks/TASK_DEPENDENCIES.md** - Critical path
6. **docs/tasks/QA_TASKS.md** - Test requirements (QA-FE-001 to QA-FE-010)

### Tertiary References
7. **specs/REQUIREMENTS.md** - Original requirements
8. **FRONTEND_DESIGN_SPECIFICATION.md** - UI specifications (if exists)

---

## Agent Invocation Command

```
Task tool with:
subagent_type: senior-frontend-developer
description: "Implement frontend application"
prompt: "
You are the senior-frontend-developer agent for Phase 5 of the DevPipeline.

Context:
- Phase 1 (Architecture) and Phase 2 (SDLC Planning) complete
- Phase 3 (Backend Development) running in parallel
- Handoff: docs/phases/PHASE_5_HANDOFF.md
- Tasks: docs/tasks/FRONTEND_TASKS.md (28 tasks)

Your Task:
Implement React TypeScript frontend following FRONTEND_TASKS.md:
1. Foundation setup (FE-001 to FE-007)
2. Student Management UI (FE-008 to FE-019)
3. Configuration Management UI (FE-020 to FE-023)
4. Optimization (FE-024 to FE-028)

STRICT CONSTRAINTS:
- NO custom styles (only Tailwind CSS 4 + Shadcn/ui)
- Field mapping in service layer (D-007): phone↔mobile, id↔studentId
- Zod validation matches backend (D-008)
- Screenshot pixel-perfect validation (D-009)
- Directory structure enforced

Deliverables:
- Complete React app (Vite + TypeScript)
- Student CRUD pages
- Configuration CRUD pages
- Service layer with field mapping
- Form validation (Zod + React Hook Form)
- Screenshot validation complete

PROCEED WITHOUT ASKING FOR PERMISSION.
"
```

---

## Next Phase Preview

**Phase 6:** Frontend QA (frontend-qa-orchestrator agent)
- Executes QA tasks from QA_TASKS.md (QA-FE-001 to QA-FE-010)
- Component unit tests (React Testing Library)
- E2E tests (Playwright)
- Screenshot pixel-perfect validation
- Field mapping verification (phone↔mobile)
- Cross-browser testing
- Fixes issues in loop until all tests pass

---

## Handoff Complete

**Status:** ✅ READY FOR PHASE 5
**Target Agent:** senior-frontend-developer
**Execution Mode:** Parallel with Phase 3 (Backend Development)
