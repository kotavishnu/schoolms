# Frontend Implementation Plan
**School Management System - React Developer Tasks**

Version: 1.0.0
Execution Model: Waterfall / Single-Pass Implementation
Target: Complete Frontend Application in One Continuous Flow

---

## Task Overview

This plan provides a sequential checklist for implementing the **Frontend SPA** (Port 3000) that consumes the Student Service (Port 8081) and Configuration Service (Port 8082) APIs.

**CRITICAL CONSTRAINTS (MUST FOLLOW):**
1. **NO NEW STYLES**: Do not create custom CSS, SASS, or styled-components
2. **REFERENCE CODE ONLY**: Copy/paste UI components, layouts, and themes EXACTLY from `frontend/reference-code/`
3. **NO DEVIATIONS**: Use existing `shadcn/ui` components without modifications
4. **MANDATORY MIGRATION**: Developer's job is purely *migration* and *integration*, not design

**Reference Code Location:** `D:\SCHOOL-GIT-AUTONOMOUS_FE_SPEC\schoolms\frontend\reference-code\`

---

## STRICT REUSE POLICY

**Golden Rule:** Every UI element, style, and component MUST come from `reference-code/`. Creating new designs or modifying existing components is FORBIDDEN.

**Mapping Reference Code to Source:**
```
reference-code/styles/                  → src/styles/
reference-code/app/components/ui/       → src/components/ui/
reference-code/app/components/          → src/components/
reference-code/app/types/               → src/types/
reference-code/app/lib/                 → src/lib/
```

---

## Phase 1: Project Setup & Asset Migration

### [FE-001] Initialize React + Vite Project
**Goal:** Create React project with TypeScript and Vite.

**Components:** N/A (Project scaffolding)

**Integration:** N/A

**Technical Details:**
- Run: `npm create vite@latest frontend -- --template react-ts`
- Navigate to `frontend` directory
- Install dependencies:
  ```bash
  npm install
  npm install react-router-dom@6
  npm install axios@1.6
  npm install react-hook-form@7
  npm install @hookform/resolvers zod@3
  npm install sonner
  npm install lucide-react
  npm install date-fns
  npm install clsx tailwind-merge
  npm install tailwindcss@4 -D
  npm install @types/node -D
  ```
- Verify project builds: `npm run dev`

**Dependencies:** None

**Acceptance Criteria:**
- Development server starts successfully on port 3000 or 5173
- TypeScript compilation has no errors
- All dependencies listed in `package.json`

**Strict Reuse Enforcement:** Project structure created from scratch (no reference code yet).

---

### [FE-002] Copy Design Tokens and Tailwind Configuration
**Goal:** Migrate theme tokens and Tailwind config EXACTLY from reference code to ensure visual parity.

**Components:** `styles/theme.css`, `styles/tailwind.css`, `styles/index.css`, `tailwind.config.js`

**Integration:** N/A

**Technical Details:**
- **MANDATORY ACTION:** Copy these files EXACTLY as-is:
  1. `reference-code/styles/theme.css` → `src/styles/theme.css`
  2. `reference-code/styles/tailwind.css` → `src/styles/tailwind.css`
  3. `reference-code/styles/index.css` → `src/styles/index.css`
  4. `reference-code/tailwind.config.ts` → `tailwind.config.ts` (if exists)
- **DO NOT MODIFY:** Do not change any CSS variable names, values, or Tailwind classes
- Import styles in `src/main.tsx`:
  ```typescript
  import './styles/index.css';
  import './styles/tailwind.css';
  import './styles/theme.css';
  ```

**Dependencies:** FE-001

**Acceptance Criteria:**
- All CSS files copied byte-for-byte from reference code
- Tailwind CSS variables accessible in components (e.g., `bg-background`, `text-foreground`)
- No custom styles added outside reference code

**Strict Reuse Enforcement:** Zero tolerance for style modifications. Any deviation breaks 1:1 visual parity.

---

### [FE-003] Copy shadcn/ui Primitive Components
**Goal:** Migrate entire `ui/` component library from reference code.

**Components:** All files in `reference-code/app/components/ui/` (40+ components)

**Integration:** N/A

**Technical Details:**
- **MANDATORY ACTION:** Copy the ENTIRE `reference-code/app/components/ui/` directory to `src/components/ui/`
- This includes (but not limited to):
  - `button.tsx`, `card.tsx`, `dialog.tsx`, `form.tsx`, `input.tsx`, `label.tsx`, `select.tsx`, `table.tsx`, `badge.tsx`, `alert.tsx`, `avatar.tsx`, `checkbox.tsx`, `calendar.tsx`, `dropdown-menu.tsx`, etc.
- **DO NOT MODIFY:** These are production-ready, accessible components. Do not change props, styles, or behavior.
- Verify imports resolve correctly (may need to adjust `@/` alias in `tsconfig.json` and `vite.config.ts`)

**Dependencies:** FE-002

**Acceptance Criteria:**
- All UI components compile without TypeScript errors
- No modifications to component logic or styling
- Path aliases (`@/components/ui`) configured correctly

**Strict Reuse Enforcement:** UI library is immutable. Treat it as a locked dependency.

---

### [FE-004] Copy Utility Functions and Types
**Goal:** Migrate helper functions and TypeScript type definitions.

**Components:** `reference-code/app/lib/utils.ts`, `reference-code/app/types/index.ts`

**Integration:** N/A

**Technical Details:**
- Copy `reference-code/app/lib/utils.ts` → `src/lib/utils.ts`
- Copy `reference-code/app/types/index.ts` → `src/types/index.ts`
- These files contain:
  - `cn()` utility for Tailwind class merging
  - Domain types: `Student`, `Enrollment`, `Configuration`
- **DO NOT MODIFY:** Types must match API response structures

**Dependencies:** FE-001

**Acceptance Criteria:**
- `cn()` utility function works correctly with Tailwind classes
- TypeScript types align with backend DTOs
- No type errors in IDE

**Strict Reuse Enforcement:** Types and utils are foundational. No custom utility functions allowed.

---

## Phase 2: API Service Layer Implementation

### [FE-005] Create Axios HTTP Client Configuration
**Goal:** Set up Axios instances for Student and Configuration APIs with interceptors.

**Components:** `src/services/api/client.ts`

**Integration:** Student API (http://localhost:8081/api/v1), Configuration API (http://localhost:8082/api/v1)

**Technical Details:**
- Create `src/services/api/client.ts` with:
  - Two Axios instances: `studentApiClient`, `configApiClient`
  - Request interceptor: Add CSRF token (from cookies), correlation ID (UUID), timestamp logging
  - Response error interceptor: Handle 400, 401, 403, 404, 409, 500 errors globally
  - Timeout: 10 seconds
  - `withCredentials: true` for CSRF protection
- Environment variables:
  ```typescript
  const STUDENT_API_BASE_URL = import.meta.env.VITE_STUDENT_API_URL || 'http://localhost:8081/api/v1';
  const CONFIG_API_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082/api/v1';
  ```
- Create `.env.development`:
  ```env
  VITE_STUDENT_API_URL=http://localhost:8081/api/v1
  VITE_CONFIG_API_URL=http://localhost:8082/api/v1
  ```

**Dependencies:** FE-001

**Acceptance Criteria:**
- Axios clients instantiate without errors
- Interceptors log requests to console (for debugging)
- CORS headers included in requests
- Network errors handled gracefully (show toast notification)

**Strict Reuse Enforcement:** HTTP client logic can be custom (no reference code for this).

---

### [FE-006] Implement Student API Service
**Goal:** Create service methods for all Student API endpoints.

**Components:** `src/services/api/studentApi.ts`

**Integration:** Student API endpoints (POST /students, GET /students, PUT /students/{id}, DELETE /students/{id}, GET /students/{id}/enrollment-history, POST /students/{id}/enrollment-history)

**Technical Details:**
- Create `src/services/api/studentApi.ts` with TypeScript interfaces:
  ```typescript
  export interface StudentRequest {
      firstName: string;
      lastName: string;
      dateOfBirth: string; // ISO 8601 (YYYY-MM-DD)
      mobile: string;
      email?: string;
      address?: string;
      fathersName?: string;
      mothersName?: string;
      identificationMark?: string;
      aadhaarNumber?: string;
  }
  export interface StudentResponse {
      id: number;
      studentId: string;
      firstName: string;
      lastName: string;
      dateOfBirth: string;
      mobile: string;
      email?: string;
      address?: string;
      fathersName?: string;
      mothersName?: string;
      identificationMark?: string;
      aadhaarNumber?: string;
      status: 'ACTIVE' | 'INACTIVE';
      version: number;
      createdAt: string;
      updatedAt: string;
  }
  ```
- Implement methods:
  - `createStudent(data: StudentRequest): Promise<StudentResponse>`
  - `getStudent(studentId: string): Promise<StudentResponse>`
  - `searchStudents(params): Promise<PaginatedResponse<StudentResponse>>`
  - `updateStudent(studentId: string, data): Promise<StudentResponse>`
  - `deleteStudent(studentId: string): Promise<void>`
  - `getEnrollmentHistory(studentId: string): Promise<{studentId: string; enrollments: Enrollment[]}>`
  - `createEnrollment(studentId: string, data): Promise<Enrollment>`

**Dependencies:** FE-005

**Acceptance Criteria:**
- All API methods typed correctly with request/response interfaces
- Methods use `studentApiClient` from `client.ts`
- Axios errors bubble up to calling component for handling

**Strict Reuse Enforcement:** API service layer can be custom (no reference code).

---

### [FE-007] Implement Configuration API Service
**Goal:** Create service methods for all Configuration API endpoints.

**Components:** `src/services/api/configApi.ts`

**Integration:** Configuration API endpoints (GET /configurations, GET /configurations/{category}/{key}, PUT /configurations/{category}/{key}, DELETE /configurations/{category}/{key}, GET /configurations/grouped/{category})

**Technical Details:**
- Create `src/services/api/configApi.ts` with interfaces:
  ```typescript
  export interface Configuration {
      id: number;
      category: 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';
      key: string;
      value: string;
      description?: string;
      dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
      isEncrypted: boolean;
      version: number;
      updatedAt: string;
  }
  ```
- Implement methods:
  - `getAllConfigurations(category?: string): Promise<{configurations: Configuration[]}>`
  - `getConfiguration(category: string, key: string): Promise<Configuration>`
  - `upsertConfiguration(category, key, data): Promise<Configuration>`
  - `deleteConfiguration(category, key): Promise<void>`
  - `getGroupedConfigurations(category): Promise<{category: string; settings: Record<string, string>}>`

**Dependencies:** FE-005

**Acceptance Criteria:**
- All API methods typed correctly
- Methods use `configApiClient`
- Upsert method handles both create (201) and update (200) status codes

**Strict Reuse Enforcement:** API service layer can be custom.

---

## Phase 3: Form Validation Setup

### [FE-008] Create Zod Validation Schemas
**Goal:** Define client-side validation rules mirroring backend constraints.

**Components:** `src/services/validation/studentSchema.ts`, `src/services/validation/configSchema.ts`

**Integration:** React Hook Form (used in dialogs)

**Technical Details:**
- Create `src/services/validation/studentSchema.ts`:
  ```typescript
  export const studentSchema = z.object({
      firstName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/, 'Name must contain only letters and spaces'),
      lastName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
      dateOfBirth: z.date().max(new Date()).refine((date) => {
          const age = new Date().getFullYear() - date.getFullYear();
          return age >= 3 && age <= 18;
      }, { message: 'Student must be between 3 and 18 years old' }),
      mobile: z.string().regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),
      email: z.string().email().optional().or(z.literal('')),
      // ... other fields
  });
  export type StudentFormData = z.infer<typeof studentSchema>;
  ```
- Create update schema (only editable fields):
  ```typescript
  export const studentUpdateSchema = z.object({
      firstName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
      lastName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
      mobile: z.string().regex(/^\d{10}$/),
      status: z.enum(['ACTIVE', 'INACTIVE']),
  });
  ```
- Create `src/services/validation/configSchema.ts` for configuration validation

**Dependencies:** FE-001

**Acceptance Criteria:**
- Zod schemas enforce same validation rules as backend
- Age calculation logic correct (3-18 years)
- Mobile and Aadhaar regex patterns match backend

**Strict Reuse Enforcement:** Validation logic can be custom (no reference code).

---

## Phase 4: Component Migration & Integration

### [FE-009] Copy and Refactor Header Component
**Goal:** Migrate navigation header from reference code.

**Components:** `reference-code/app/components/Header.tsx` → `src/components/Header.tsx`

**Integration:** React Router (navigation links)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/Header.tsx` EXACTLY to `src/components/Header.tsx`
- **REFACTOR ONLY:** Replace any hardcoded navigation logic with React Router:
  ```typescript
  import { Link, useLocation } from 'react-router-dom';
  // Replace <a href="/students"> with <Link to="/students">
  ```
- **KEEP JSX IDENTICAL:** Do not change HTML structure, classes, or styling
- Logo, colors, layout must match reference code 1:1

**Dependencies:** FE-003

**Acceptance Criteria:**
- Header renders identically to reference code
- Navigation links use React Router (no page reloads)
- Active route highlighted correctly

**Strict Reuse Enforcement:** JSX structure is sacred. Only replace `<a>` with `<Link>`, nothing else.

---

### [FE-010] Copy and Refactor Home Page Component
**Goal:** Migrate landing page from reference code.

**Components:** `reference-code/app/components/HomePage.tsx` → `src/components/HomePage.tsx`

**Integration:** React Router (navigation buttons)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/HomePage.tsx` EXACTLY to `src/components/HomePage.tsx`
- **REFACTOR ONLY:** Replace navigation buttons with React Router:
  ```typescript
  import { useNavigate } from 'react-router-dom';
  const navigate = useNavigate();
  <Button onClick={() => navigate('/students')}>Manage Students</Button>
  ```
- **KEEP JSX IDENTICAL:** Do not change layout, text, icons, or styles

**Dependencies:** FE-003

**Acceptance Criteria:**
- Home page renders identically to reference code
- Navigation buttons route correctly
- Icons (lucide-react) display properly

**Strict Reuse Enforcement:** Only replace onClick logic, keep everything else unchanged.

---

### [FE-011] Copy and Integrate Students Page Component
**Goal:** Migrate student list page and connect to Student API.

**Components:** `reference-code/app/components/StudentsPage.tsx` → `src/components/StudentsPage.tsx`

**Integration:** Student API (`searchStudents`, `deleteStudent`)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/StudentsPage.tsx` EXACTLY to `src/components/StudentsPage.tsx`
- **INTEGRATION POINTS (Replace mock data with API calls):**
  1. **Import API service:**
     ```typescript
     import { studentApi, StudentResponse } from '../services/api/studentApi';
     import { toast } from 'sonner';
     ```
  2. **Replace mock data state:**
     ```typescript
     // BEFORE (reference code):
     const [students, setStudents] = useState<Student[]>(mockStudents);

     // AFTER (integrated):
     const [students, setStudents] = useState<StudentResponse[]>([]);
     const [loading, setLoading] = useState(false);
     ```
  3. **Fetch students on mount:**
     ```typescript
     useEffect(() => {
         fetchStudents();
     }, []);

     const fetchStudents = async () => {
         setLoading(true);
         try {
             const response = await studentApi.searchStudents({});
             setStudents(response.content);
         } catch (error) {
             toast.error('Failed to fetch students');
         } finally {
             setLoading(false);
         }
     };
     ```
  4. **Integrate delete action:**
     ```typescript
     const handleDelete = async (studentId: string) => {
         if (confirm('Are you sure?')) {
             try {
                 await studentApi.deleteStudent(studentId);
                 toast.success('Student deleted');
                 fetchStudents(); // Refresh list
             } catch (error) {
                 toast.error('Failed to delete student');
             }
         }
     };
     ```
- **KEEP JSX STRUCTURE:** Do not change table layout, buttons, or search UI
- Add loading spinner when `loading === true` (use shadcn/ui `Skeleton` component)

**Dependencies:** FE-006, FE-003

**Acceptance Criteria:**
- Student list displays data from backend API
- Search filters call API with query parameters
- Delete action removes student and refreshes list
- Loading state shows spinner during API calls
- Error handling displays toast notifications
- JSX structure matches reference code 100%

**Strict Reuse Enforcement:** Only replace data fetching logic. Do NOT redesign UI or change component structure.

---

### [FE-012] Copy and Integrate Student Dialog Component (Create/Edit)
**Goal:** Migrate student registration/edit dialog and connect to form validation + API.

**Components:** `reference-code/app/components/StudentDialog.tsx` → `src/components/StudentDialog.tsx`

**Integration:** React Hook Form + Zod validation + Student API (`createStudent`, `updateStudent`)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/StudentDialog.tsx` EXACTLY to `src/components/StudentDialog.tsx`
- **INTEGRATION POINTS:**
  1. **Import validation schema:**
     ```typescript
     import { useForm } from 'react-hook-form';
     import { zodResolver } from '@hookform/resolvers/zod';
     import { studentSchema, StudentFormData } from '../services/validation/studentSchema';
     import { studentApi } from '../services/api/studentApi';
     ```
  2. **Initialize form with validation:**
     ```typescript
     const form = useForm<StudentFormData>({
         resolver: zodResolver(studentSchema),
         defaultValues: {
             firstName: '',
             lastName: '',
             mobile: '',
             email: '',
             // ... all fields
         },
     });
     ```
  3. **Replace submit handler:**
     ```typescript
     const onSubmit = async (data: StudentFormData) => {
         try {
             const apiData = {
                 ...data,
                 dateOfBirth: data.dateOfBirth.toISOString().split('T')[0], // Convert Date to string
             };
             await studentApi.createStudent(apiData);
             toast.success('Student registered successfully');
             form.reset();
             onOpenChange(false);
             onSuccess(); // Callback to refresh parent list
         } catch (error: any) {
             if (error.response?.status === 409) {
                 toast.error('Mobile number already registered');
             } else if (error.response?.status === 400) {
                 const errors = error.response.data.errors || [];
                 errors.forEach((err: any) => toast.error(err.message || err));
             } else {
                 toast.error('Failed to register student');
             }
         }
     };
     ```
  4. **Bind form fields to React Hook Form:**
     ```typescript
     <FormField
         control={form.control}
         name="firstName"
         render={({ field }) => (
             <FormItem>
                 <FormLabel>First Name *</FormLabel>
                 <FormControl>
                     <Input placeholder="John" {...field} />
                 </FormControl>
                 <FormMessage />
             </FormItem>
         )}
     />
     ```
- **EDIT MODE:** If dialog is in edit mode, pre-populate form with existing student data:
  ```typescript
  useEffect(() => {
      if (editingStudent) {
          form.reset({
              firstName: editingStudent.firstName,
              lastName: editingStudent.lastName,
              // ... other fields
          });
      }
  }, [editingStudent]);
  ```
- **KEEP JSX LAYOUT:** Do not change form field order, labels, or dialog structure

**Dependencies:** FE-008, FE-006, FE-003

**Acceptance Criteria:**
- Form validates input client-side using Zod schema
- Form displays field-level error messages
- Create action calls `createStudent` API and shows success toast
- Edit action calls `updateStudent` API with version number
- Duplicate mobile error (409) handled gracefully
- Backend validation errors displayed to user
- Dialog closes on successful submission
- JSX structure matches reference code

**Strict Reuse Enforcement:** Only replace form logic and API calls. Do NOT change UI layout or field positioning.

---

### [FE-013] Copy and Integrate View Student Dialog Component
**Goal:** Migrate student details view dialog (if exists in reference code).

**Components:** `reference-code/app/components/ViewStudentDialog.tsx` → `src/components/ViewStudentDialog.tsx` (if exists)

**Integration:** Student API (`getStudent`, `getEnrollmentHistory`)

**Technical Details:**
- **MANDATORY ACTION:** If `ViewStudentDialog.tsx` exists in reference code, copy EXACTLY
- **INTEGRATION POINTS:**
  1. Fetch student details on dialog open:
     ```typescript
     useEffect(() => {
         if (open && studentId) {
             fetchStudentDetails();
         }
     }, [open, studentId]);

     const fetchStudentDetails = async () => {
         try {
             const student = await studentApi.getStudent(studentId);
             const enrollments = await studentApi.getEnrollmentHistory(studentId);
             setStudentDetails(student);
             setEnrollments(enrollments.enrollments);
         } catch (error) {
             toast.error('Failed to load student details');
         }
     };
     ```
- Display read-only student information + enrollment history table
- **KEEP JSX LAYOUT:** Do not change display format or styling

**Dependencies:** FE-006, FE-003

**Acceptance Criteria:**
- Dialog displays student details from API
- Enrollment history table shows all enrollments
- Loading state during API calls
- JSX structure matches reference code

**Strict Reuse Enforcement:** Only replace data fetching. Do NOT redesign UI.

---

### [FE-014] Copy and Integrate Configurations Page Component
**Goal:** Migrate configuration management page and connect to Configuration API.

**Components:** `reference-code/app/components/ConfigurationsPage.tsx` → `src/components/ConfigurationsPage.tsx`

**Integration:** Configuration API (`getAllConfigurations`, `deleteConfiguration`)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/ConfigurationsPage.tsx` EXACTLY to `src/components/ConfigurationsPage.tsx`
- **INTEGRATION POINTS:**
  1. Fetch configurations on mount:
     ```typescript
     const [configurations, setConfigurations] = useState<Configuration[]>([]);
     const [selectedCategory, setSelectedCategory] = useState<string>('all');

     useEffect(() => {
         fetchConfigurations();
     }, [selectedCategory]);

     const fetchConfigurations = async () => {
         try {
             const response = await configApi.getAllConfigurations(
                 selectedCategory !== 'all' ? selectedCategory : undefined
             );
             setConfigurations(response.configurations);
         } catch (error) {
             toast.error('Failed to fetch configurations');
         }
     };
     ```
  2. Integrate delete action:
     ```typescript
     const handleDelete = async (category: string, key: string) => {
         if (confirm('Delete this configuration?')) {
             try {
                 await configApi.deleteConfiguration(category, key);
                 toast.success('Configuration deleted');
                 fetchConfigurations();
             } catch (error) {
                 toast.error('Failed to delete configuration');
             }
         }
     };
     ```
- **Category filter dropdown:** Connect to API call with selected category
- **KEEP JSX LAYOUT:** Do not change table structure or filter UI

**Dependencies:** FE-007, FE-003

**Acceptance Criteria:**
- Configuration list displays data from API
- Category filter calls API with selected category
- Delete action removes configuration and refreshes list
- Toast notifications for success/error
- JSX structure matches reference code

**Strict Reuse Enforcement:** Only replace data fetching. Do NOT change UI.

---

### [FE-015] Copy and Integrate Configuration Dialog Component (Create/Edit)
**Goal:** Migrate configuration upsert dialog and connect to form validation + API.

**Components:** `reference-code/app/components/ConfigurationDialog.tsx` → `src/components/ConfigurationDialog.tsx`

**Integration:** React Hook Form + Zod validation + Configuration API (`upsertConfiguration`)

**Technical Details:**
- **MANDATORY ACTION:** Copy `reference-code/app/components/ConfigurationDialog.tsx` EXACTLY to `src/components/ConfigurationDialog.tsx`
- **INTEGRATION POINTS:**
  1. Initialize form with validation:
     ```typescript
     const form = useForm({
         resolver: zodResolver(configSchema),
         defaultValues: {
             category: 'GENERAL',
             key: '',
             value: '',
             description: '',
             dataType: 'STRING',
             isEncrypted: false,
         },
     });
     ```
  2. Submit handler for upsert:
     ```typescript
     const onSubmit = async (data) => {
         try {
             await configApi.upsertConfiguration(data.category, data.key, {
                 value: data.value,
                 description: data.description,
                 dataType: data.dataType,
                 isEncrypted: data.isEncrypted,
             });
             toast.success('Configuration saved');
             form.reset();
             onOpenChange(false);
             onSuccess();
         } catch (error) {
             toast.error('Failed to save configuration');
         }
     };
     ```
- **EDIT MODE:** Pre-populate form if editing existing configuration
- **KEEP JSX LAYOUT:** Do not change form structure or field order

**Dependencies:** FE-008, FE-007, FE-003

**Acceptance Criteria:**
- Form validates input using Zod schema
- Upsert action calls correct API method
- Success/error toasts displayed
- Dialog closes on successful submission
- JSX structure matches reference code

**Strict Reuse Enforcement:** Only replace form logic and API calls. Do NOT change UI.

---

## Phase 5: Routing & Application Shell

### [FE-016] Create Application Root Component
**Goal:** Set up React Router and main application layout.

**Components:** `src/App.tsx`

**Integration:** All page components (Header, HomePage, StudentsPage, ConfigurationsPage)

**Technical Details:**
- Create `src/App.tsx`:
  ```typescript
  import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
  import { Toaster } from 'sonner';
  import { Header } from './components/Header';
  import { HomePage } from './components/HomePage';
  import { StudentsPage } from './components/StudentsPage';
  import { ConfigurationsPage } from './components/ConfigurationsPage';

  function App() {
      return (
          <BrowserRouter>
              <div className="min-h-screen bg-background">
                  <Header />
                  <Routes>
                      <Route path="/" element={<HomePage />} />
                      <Route path="/students" element={<StudentsPage />} />
                      <Route path="/configurations" element={<ConfigurationsPage />} />
                      <Route path="*" element={<Navigate to="/" replace />} />
                  </Routes>
                  <Toaster position="top-right" richColors />
              </div>
          </BrowserRouter>
      );
  }

  export default App;
  ```
- Update `src/main.tsx`:
  ```typescript
  import React from 'react';
  import ReactDOM from 'react-dom/client';
  import App from './App';
  import './styles/index.css';
  import './styles/tailwind.css';
  import './styles/theme.css';

  ReactDOM.createRoot(document.getElementById('root')!).render(
      <React.StrictMode>
          <App />
      </React.StrictMode>
  );
  ```

**Dependencies:** FE-009, FE-010, FE-011, FE-014

**Acceptance Criteria:**
- Application loads without errors
- All routes navigate correctly
- Toast notifications work across all pages
- Background color and typography match reference code

**Strict Reuse Enforcement:** Root layout can be custom (no reference code for App.tsx).

---

## Phase 6: Testing & Optimization

### [FE-017] Test Student Management Workflow
**Goal:** Manually verify all student operations work end-to-end.

**Components:** StudentsPage, StudentDialog, ViewStudentDialog

**Integration:** Student API (all endpoints)

**Technical Details:**
- **Test Scenario 1:** Register new student
  - Fill form with valid data
  - Submit and verify toast success message
  - Check student appears in list
- **Test Scenario 2:** Search students by last name
  - Enter last name in search box
  - Verify filtered results
- **Test Scenario 3:** Edit student
  - Click edit button
  - Modify allowed fields (name, mobile, status)
  - Submit and verify changes reflected
- **Test Scenario 4:** Delete student
  - Click delete button
  - Confirm deletion
  - Verify student removed from list
- **Test Scenario 5:** Handle validation errors
  - Submit form with invalid mobile (e.g., "123")
  - Verify error message displays
- **Test Scenario 6:** Handle duplicate mobile
  - Register student with same mobile as existing student
  - Verify 409 error toast displays

**Dependencies:** FE-011, FE-012

**Acceptance Criteria:**
- All scenarios pass successfully
- No console errors
- API calls complete within 2 seconds
- UI matches reference code visually

**Strict Reuse Enforcement:** Testing validates correct integration, not UI design.

---

### [FE-018] Test Configuration Management Workflow
**Goal:** Manually verify all configuration operations work end-to-end.

**Components:** ConfigurationsPage, ConfigurationDialog

**Integration:** Configuration API (all endpoints)

**Technical Details:**
- **Test Scenario 1:** Create new configuration
  - Fill form with category, key, value
  - Submit and verify appears in list
- **Test Scenario 2:** Filter by category
  - Select "GENERAL" category
  - Verify only GENERAL configs displayed
- **Test Scenario 3:** Edit configuration
  - Click edit button
  - Update value
  - Verify upsert works (200 or 201 status)
- **Test Scenario 4:** Delete configuration
  - Click delete button
  - Confirm deletion
  - Verify config removed from list

**Dependencies:** FE-014, FE-015

**Acceptance Criteria:**
- All scenarios pass successfully
- Upsert correctly handles both create and update
- No console errors

**Strict Reuse Enforcement:** Testing validates correct integration.

---

### [FE-019] Implement Performance Optimizations
**Goal:** Optimize bundle size and rendering performance.

**Components:** All components

**Integration:** N/A

**Technical Details:**
- **Code Splitting:** Lazy load route components:
  ```typescript
  import { lazy, Suspense } from 'react';
  const StudentsPage = lazy(() => import('./components/StudentsPage'));
  const ConfigurationsPage = lazy(() => import('./components/ConfigurationsPage'));

  <Suspense fallback={<div>Loading...</div>}>
      <Routes>...</Routes>
  </Suspense>
  ```
- **Memoization:** Use `useMemo` for expensive computations (e.g., filtered lists):
  ```typescript
  const filteredStudents = useMemo(() => {
      return students.filter(s => s.lastName.includes(searchTerm));
  }, [students, searchTerm]);
  ```
- **Callback Memoization:** Use `useCallback` for event handlers passed to child components
- **Image Optimization:** Use `loading="lazy"` for images (if any)

**Dependencies:** FE-016

**Acceptance Criteria:**
- Lighthouse Performance score >90
- Bundle size <500KB (gzipped)
- Time to Interactive <3 seconds

**Strict Reuse Enforcement:** Performance optimizations do not change UI appearance.

---

## Phase 7: Build & Deployment

### [FE-020] Configure Production Build
**Goal:** Set up Vite build configuration for production.

**Components:** `vite.config.ts`

**Integration:** N/A

**Technical Details:**
- Create `vite.config.ts`:
  ```typescript
  import { defineConfig } from 'vite';
  import react from '@vitejs/plugin-react';
  import path from 'path';

  export default defineConfig({
      plugins: [react()],
      resolve: {
          alias: {
              '@': path.resolve(__dirname, './src'),
          },
      },
      server: {
          port: 3000,
          proxy: {
              '/api/v1/students': {
                  target: 'http://localhost:8081',
                  changeOrigin: true,
              },
              '/api/v1/configurations': {
                  target: 'http://localhost:8082',
                  changeOrigin: true,
              },
          },
      },
      build: {
          outDir: 'dist',
          sourcemap: true,
          rollupOptions: {
              output: {
                  manualChunks: {
                      vendor: ['react', 'react-dom', 'react-router-dom'],
                      ui: ['@radix-ui/react-dialog', '@radix-ui/react-select'],
                  },
              },
          },
      },
  });
  ```
- Verify production build: `npm run build`

**Dependencies:** FE-001

**Acceptance Criteria:**
- Build completes without errors
- Output directory `dist/` contains optimized assets
- Source maps generated for debugging

**Strict Reuse Enforcement:** Build configuration is custom (no reference code).

---

### [FE-021] Create Dockerfile for Frontend
**Goal:** Containerize frontend application with Nginx.

**Components:** `Dockerfile`, `nginx.conf`

**Integration:** N/A

**Technical Details:**
- Create `frontend/Dockerfile`:
  ```dockerfile
  # Build stage
  FROM node:20-alpine AS builder
  WORKDIR /app
  COPY package*.json ./
  RUN npm ci
  COPY . .
  RUN npm run build

  # Production stage
  FROM nginx:alpine
  COPY --from=builder /app/dist /usr/share/nginx/html
  COPY nginx.conf /etc/nginx/conf.d/default.conf
  EXPOSE 80
  CMD ["nginx", "-g", "daemon off;"]
  ```
- Create `frontend/nginx.conf`:
  ```nginx
  server {
      listen 80;
      server_name localhost;
      root /usr/share/nginx/html;
      index index.html;

      # Gzip compression
      gzip on;
      gzip_types text/css application/javascript application/json image/svg+xml;

      # SPA fallback
      location / {
          try_files $uri $uri/ /index.html;
      }

      # API proxy
      location /api/v1/students {
          proxy_pass http://student-service:8081;
      }
      location /api/v1/configurations {
          proxy_pass http://configuration-service:8082;
      }

      # Security headers
      add_header X-Frame-Options "SAMEORIGIN" always;
      add_header X-Content-Type-Options "nosniff" always;
      add_header X-XSS-Protection "1; mode=block" always;
  }
  ```

**Dependencies:** FE-020

**Acceptance Criteria:**
- Docker image builds successfully: `docker build -t sms/frontend .`
- Container starts and serves application on port 80
- SPA routing works (no 404 on page refresh)
- API proxy forwards requests to backend services

**Strict Reuse Enforcement:** Docker configuration is custom.

---

### [FE-022] Update Docker Compose with Frontend Service
**Goal:** Add frontend service to existing Docker Compose setup.

**Components:** `docker-compose.yml` (root project)

**Integration:** Backend services (student-service, configuration-service)

**Technical Details:**
- Update `docker-compose.yml` to include frontend service:
  ```yaml
  version: '3.8'
  services:
      # ... existing backend services ...

      frontend:
          build: ./frontend
          ports:
              - "3000:80"
          depends_on:
              - student-service
              - configuration-service
          environment:
              VITE_STUDENT_API_URL: http://localhost:8081/api/v1
              VITE_CONFIG_API_URL: http://localhost:8082/api/v1
  ```

**Dependencies:** FE-021

**Acceptance Criteria:**
- `docker-compose up -d` starts all services (frontend + backend + databases)
- Frontend accessible at `http://localhost:3000`
- Frontend can communicate with backend services
- Health checks pass for all services

**Strict Reuse Enforcement:** Docker Compose configuration is custom.

---

## Phase 8: Final Validation

### [FE-023] Cross-Browser Compatibility Testing
**Goal:** Verify application works across major browsers.

**Components:** All components

**Integration:** N/A

**Technical Details:**
- Test on:
  - Google Chrome (latest)
  - Mozilla Firefox (latest)
  - Safari (latest, if on macOS)
  - Microsoft Edge (latest)
- Verify:
  - Layout renders correctly
  - Forms submit successfully
  - Dialogs open/close properly
  - Toast notifications appear

**Dependencies:** FE-016

**Acceptance Criteria:**
- No console errors in any browser
- UI looks identical across browsers
- All features functional

**Strict Reuse Enforcement:** Testing validates compatibility, not design.

---

### [FE-024] Accessibility Audit
**Goal:** Ensure application meets WCAG 2.1 AA standards.

**Components:** All components

**Integration:** N/A

**Technical Details:**
- Use tools:
  - Lighthouse accessibility audit
  - axe DevTools browser extension
- Verify:
  - All interactive elements keyboard-navigable
  - ARIA labels on buttons
  - Form fields have associated labels
  - Focus indicators visible
  - Color contrast ratios meet AA standards

**Dependencies:** FE-016

**Acceptance Criteria:**
- Lighthouse accessibility score >90
- No critical accessibility issues in axe audit
- Keyboard navigation works for all features

**Strict Reuse Enforcement:** shadcn/ui components already accessible (reference code).

---

### [FE-025] Documentation and Handoff
**Goal:** Complete frontend implementation documentation.

**Components:** N/A

**Integration:** N/A

**Technical Details:**
- Create `frontend/README.md`:
  - Project setup instructions
  - Development commands (`npm run dev`, `npm run build`)
  - Environment variables reference
  - Docker commands
  - Component structure overview
  - API integration notes
- Document reference code migration mapping
- List all copied files and their sources

**Dependencies:** FE-022

**Acceptance Criteria:**
- Developer can set up project from scratch using README
- All commands tested and verified
- Reference code sources documented

**Strict Reuse Enforcement:** Documentation describes what was copied/integrated.

---

## Task Dependency Summary

```
FE-001 (Project Setup)
  ├─→ FE-002 (Copy Styles)
  │     └─→ FE-003 (Copy UI Components)
  ├─→ FE-004 (Copy Utils/Types)
  ├─→ FE-005 (Axios Client)
  │     └─→ FE-006 (Student API)
  │     └─→ FE-007 (Config API)
  ├─→ FE-008 (Zod Schemas)

FE-003 + FE-006 → FE-009 (Header) → FE-016 (App Root)
FE-003 + FE-006 → FE-010 (HomePage) → FE-016
FE-003 + FE-006 → FE-011 (StudentsPage) → FE-016
FE-003 + FE-006 + FE-008 → FE-012 (StudentDialog) → FE-011
FE-003 + FE-006 → FE-013 (ViewStudentDialog) → FE-011
FE-003 + FE-007 → FE-014 (ConfigurationsPage) → FE-016
FE-003 + FE-007 + FE-008 → FE-015 (ConfigurationDialog) → FE-014

FE-016 → FE-017 (Test Student Workflow)
FE-016 → FE-018 (Test Config Workflow)
FE-016 → FE-019 (Performance Optimization)
FE-001 → FE-020 (Build Config) → FE-021 (Dockerfile) → FE-022 (Docker Compose)
FE-016 → FE-023 (Browser Testing)
FE-016 → FE-024 (Accessibility Audit)
FE-022 → FE-025 (Documentation)
```

---

## Success Criteria for Frontend Completion

- [ ] All reference code components copied EXACTLY (no custom styles)
- [ ] UI matches reference code 1:1 visually
- [ ] All API integrations work correctly (Student + Configuration)
- [ ] Forms validate using Zod schemas
- [ ] Toast notifications display for all API operations
- [ ] Loading states shown during API calls
- [ ] Error handling implemented (400, 409, 500 errors)
- [ ] React Router navigation works without page reloads
- [ ] Docker image builds successfully
- [ ] `docker-compose up` starts entire application stack
- [ ] Lighthouse Performance score >90
- [ ] Lighthouse Accessibility score >90
- [ ] Cross-browser compatibility verified (Chrome, Firefox, Safari, Edge)
- [ ] Production build completes without errors (`npm run build`)
- [ ] README documentation complete and accurate

---

## Reference Code Verification Checklist

**Before marking frontend complete, verify:**
- [ ] `styles/theme.css` copied byte-for-byte from reference code
- [ ] `components/ui/` directory contains ALL shadcn/ui components from reference
- [ ] `Header.tsx` JSX structure matches reference code
- [ ] `HomePage.tsx` JSX structure matches reference code
- [ ] `StudentsPage.tsx` JSX structure matches reference code (only data fetching changed)
- [ ] `StudentDialog.tsx` JSX structure matches reference code (only form logic changed)
- [ ] `ConfigurationsPage.tsx` JSX structure matches reference code
- [ ] `ConfigurationDialog.tsx` JSX structure matches reference code
- [ ] No custom CSS classes created outside reference code
- [ ] No modifications to shadcn/ui component props or styles
- [ ] Tailwind utility classes used are from reference code only

---

**Document Version:** 1.0.0
**Last Updated:** 2026-01-28
**Next Review:** Post-Implementation
