# Frontend Implementation Plan
# School Management System - React SPA
# Execution Model: Single-Pass Waterfall (no sprints)

## Source of Truth
- Reference Code:      `frontend/reference-code/` (MANDATORY REUSE - copy exactly)
- API Contract:        `specs/sms_api_specification.yaml`
- Frontend Guide:      `specs/architecture/06-frontend-implementation-guide.md`
- Design Tokens:       `specs/FRONTEND_DESIGN_SPEC.md`
- System Architecture: `specs/architecture/01-system-architecture.md`

## Strict Constraints (Read Before Implementing Any Task)

1. NO NEW STYLES. Creating custom CSS, SASS, or styled-components is FORBIDDEN.
2. REFERENCE CODE IS THE AUTHORITATIVE SOURCE. All JSX structure, component names, Tailwind class names, and layout patterns MUST be copied exactly from `frontend/reference-code/`. The developer's job is migration and integration, NOT design.
3. SEMANTIC TOKEN USAGE ONLY. Never hardcode color values such as `bg-[#3b82f6]`. Always use semantic tokens such as `bg-primary` or `text-destructive`.
4. NO COMPONENT INVENTION. If a component exists in `frontend/reference-code/app/components/ui/`, copy it; do not recreate it from scratch.

## Reference Code to Source File Mapping (Mandatory)

| Reference File (copy FROM) | Target File (copy TO) | Notes |
|---|---|---|
| `frontend/reference-code/app/components/ui/button.tsx` | `frontend/src/components/ui/button.tsx` | Copy exactly, no changes |
| `frontend/reference-code/app/components/ui/card.tsx` | `frontend/src/components/ui/card.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/dialog.tsx` | `frontend/src/components/ui/dialog.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/input.tsx` | `frontend/src/components/ui/input.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/label.tsx` | `frontend/src/components/ui/label.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/select.tsx` | `frontend/src/components/ui/select.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/table.tsx` | `frontend/src/components/ui/table.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/toast.tsx` | `frontend/src/components/ui/toast.tsx` | Copy exactly |
| `frontend/reference-code/app/components/ui/badge.tsx` | `frontend/src/components/ui/badge.tsx` | Copy exactly |
| `frontend/reference-code/styles/theme.css` | `frontend/src/styles/theme.css` | Copy exactly - Figma design tokens |
| `frontend/reference-code/styles/tailwind.css` | `frontend/src/styles/tailwind.css` | Copy exactly |
| `frontend/reference-code/tailwind.config.ts` | `frontend/tailwind.config.ts` | Copy exactly |
| `frontend/reference-code/app/pages/StudentsPage.tsx` | `frontend/src/pages/StudentsPage.tsx` | Refactor only - replace mockData with API hooks |
| `frontend/reference-code/app/pages/ConfigurationsPage.tsx` | `frontend/src/pages/ConfigurationsPage.tsx` | Refactor only |
| `frontend/reference-code/app/components/StudentDialog.tsx` | `frontend/src/components/students/StudentRegisterDialog.tsx` | Refactor only |
| `frontend/reference-code/app/components/ConfigurationDialog.tsx` | `frontend/src/components/configurations/ConfigurationAddDialog.tsx` | Refactor only |
| `frontend/reference-code/app/components/layout/AppLayout.tsx` | `frontend/src/components/layout/AppLayout.tsx` | Copy, add React Router Outlet |
| `frontend/reference-code/app/components/layout/Sidebar.tsx` | `frontend/src/components/layout/Sidebar.tsx` | Copy exactly |

---

## Phase 1: Project Setup and Asset Migration

### [FE-001] Initialize Vite + React + TypeScript Project

**Goal:** Create the base project scaffold at `frontend/` using Vite with React 18 and TypeScript.

**Components:** None (infrastructure only).

**Integration:** None.

**Steps:**
1. Run: `npm create vite@latest frontend -- --template react-ts` from the project root.
2. Navigate to `frontend/` and run `npm install`.
3. Install all required dependencies:
   ```
   npm install axios@^1.7 react-hook-form@^7 @hookform/resolvers zod@^3 react-router-dom@^6
   ```
4. Install dev dependencies:
   ```
   npm install -D tailwindcss@^4 @tailwindcss/vite postcss autoprefixer vitest @testing-library/react @testing-library/jest-dom @vitejs/plugin-react msw@^2 @playwright/test
   ```
5. Install shadcn/ui peer dependencies:
   ```
   npm install class-variance-authority clsx tailwind-merge lucide-react @radix-ui/react-dialog @radix-ui/react-select @radix-ui/react-label @radix-ui/react-toast @radix-ui/react-slot
   ```
6. Create `frontend/tsconfig.json` with `"strict": true`, `"jsx": "react-jsx"`, `"paths": { "@/*": ["./src/*"] }`.
7. Create `frontend/vite.config.ts` with `@vitejs/plugin-react` and `resolve.alias: { '@': '/src' }`.

**Dependencies:** None (first task).

---

### [FE-002] Copy Design System Assets from Reference Code

**Goal:** Migrate the exact Figma token-based CSS and Tailwind configuration from reference code to establish visual parity from the start. This is mandatory before writing any component code.

**Components:** None (CSS assets only).

**Integration:** None.

**Steps:**
1. Create directory `frontend/src/styles/`.
2. COPY `frontend/reference-code/styles/theme.css` to `frontend/src/styles/theme.css`. DO NOT modify.
3. COPY `frontend/reference-code/styles/tailwind.css` to `frontend/src/styles/tailwind.css`. DO NOT modify.
4. COPY `frontend/reference-code/tailwind.config.ts` to `frontend/tailwind.config.ts`. DO NOT modify.
5. Update `frontend/vite.config.ts` to import the Tailwind v4 Vite plugin and point to `src/styles/tailwind.css`.
6. Update `frontend/src/main.tsx` to import `./styles/theme.css` and `./styles/tailwind.css` (in that order, primitives before Tailwind).
7. Verify the CSS custom properties from `theme.css` (e.g., `--primary`, `--background`, `--destructive`) are available in the browser DevTools after running `npm run dev`.

**Dependencies:** FE-001.

---

### [FE-003] Copy All shadcn/ui Primitive Components from Reference Code

**Goal:** Migrate the entire `ui/` component folder from reference code so all feature components have their building blocks available.

**Components:**
- `frontend/src/components/ui/button.tsx`
- `frontend/src/components/ui/card.tsx`
- `frontend/src/components/ui/dialog.tsx`
- `frontend/src/components/ui/input.tsx`
- `frontend/src/components/ui/label.tsx`
- `frontend/src/components/ui/select.tsx`
- `frontend/src/components/ui/table.tsx`
- `frontend/src/components/ui/toast.tsx`
- `frontend/src/components/ui/badge.tsx`

**Integration:** None (no API calls).

**Steps:**
1. Create directory `frontend/src/components/ui/`.
2. For each file listed in the Reference Code to Source File Mapping table above, copy the file from `frontend/reference-code/app/components/ui/` to `frontend/src/components/ui/`.
3. Update all internal import paths if necessary to use `@/` alias instead of relative paths.
4. Create `frontend/src/components/ui/index.ts` that re-exports all ui components.
5. Verify no TypeScript errors with `npx tsc --noEmit`.

**Dependencies:** FE-002.

---

## Phase 2: Type Definitions and Service Layer

### [FE-004] Implement TypeScript Type Definitions

**Goal:** Create all TypeScript interfaces matching the OpenAPI schemas exactly. No invented fields; mirror `specs/sms_api_specification.yaml` field-for-field.

**Components:** Type files only (no UI).

**Integration:** Referenced by all service and component files.

**Steps:**
1. Create `frontend/src/types/student.ts`:
   ```typescript
   export type StudentStatus = 'ACTIVE' | 'INACTIVE';
   export interface StudentBase { firstName: string; lastName: string; dateOfBirth: string; mobile: string; email?: string; address?: string; fathersName?: string; mothersName?: string; identificationMark?: string; aadhaarNumber?: string; }
   export interface StudentResponse extends StudentBase { id: number; studentId: string; status: StudentStatus; version: number; createdAt: string; updatedAt: string; }
   export interface UpdateStudentRequest { firstName: string; lastName: string; mobile: string; status: StudentStatus; version: number; }
   export interface PagedStudentResponse { content: StudentResponse[]; pageable: PaginationMetadata; }
   ```
2. Create `frontend/src/types/configuration.ts`:
   ```typescript
   export type ConfigurationCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';
   export type ConfigurationDataType = 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
   export interface Configuration { id: number; category: ConfigurationCategory; key: string; value: string; description?: string; dataType: ConfigurationDataType; isEncrypted: boolean; version: number; updatedAt: string; }
   export interface UpsertConfigurationRequest { value: string; description?: string; dataType: ConfigurationDataType; isEncrypted: boolean; }
   export interface GroupedConfigurationResponse { category: ConfigurationCategory; settings: Record<string, string>; }
   ```
3. Create `frontend/src/types/api.ts`:
   ```typescript
   export interface PaginationMetadata { page: number; size: number; totalElements: number; totalPages: number; }
   export interface FieldError { field: string; message: string; code: string; }
   export interface ErrorResponse { type: string; title: string; status: number; detail: string; instance: string; timestamp: string; correlationId?: string; errors: FieldError[]; }
   ```
4. Create `frontend/src/types/enrollment.ts` mirroring the `Enrollment` OpenAPI schema.

**Dependencies:** FE-001.

---

### [FE-005] Implement Zod Validation Schemas

**Goal:** Create Zod validation schemas that mirror backend Jakarta Bean Validation rules exactly. Same regex patterns, same length limits, same business rule constraints.

**Components:** Schema files only (no UI).

**Integration:** Used by React Hook Form resolvers in all form dialogs.

**Steps:**
1. Create `frontend/src/schemas/studentSchema.ts`:
   - `createStudentSchema`: Validate `firstName` (min 2, max 100, pattern `^[a-zA-Z ]+$`), `lastName` (same), `dateOfBirth` (refine: age between 3-18 years computed from `new Date(val)` vs `today - 3 years` and `today - 18 years`), `mobile` (pattern `^\d{10}$`), `email` (optional email), `aadhaarNumber` (optional, pattern `^\d{12}$`). Optional string fields: `address`, `fathersName`, `mothersName`, `identificationMark`.
   - `updateStudentSchema`: Validate `firstName`, `lastName`, `mobile` (same as above), `status` (`z.enum(['ACTIVE','INACTIVE'])`), `version` (`z.number().int()`).
   - Export inferred types: `CreateStudentFormData`, `UpdateStudentFormData`.
2. Create `frontend/src/schemas/configurationSchema.ts`:
   - `upsertConfigurationSchema`: `value` (min 1), `description` (optional), `dataType` (`z.enum(['STRING','NUMBER','BOOLEAN','JSON'])`), `isEncrypted` (`z.boolean().default(false)`).
   - Export inferred type: `UpsertConfigurationFormData`.

**Dependencies:** FE-004.

---

### [FE-006] Implement Centralized Axios API Clients and Service Modules

**Goal:** Create the HTTP service layer with two separate Axios instances (one per backend service) and typed service functions for all API operations.

**Components:** Service files only (no UI).

**Integration:**
- `studentApiClient` base URL -> `VITE_STUDENT_API_URL` (default: `http://localhost:8081/api/v1`)
- `configApiClient` base URL -> `VITE_CONFIG_API_URL` (default: `http://localhost:8082/api/v1`)

**Steps:**
1. Create `frontend/.env.example`:
   ```
   VITE_STUDENT_API_URL=http://localhost:8081/api/v1
   VITE_CONFIG_API_URL=http://localhost:8082/api/v1
   ```
2. Create `frontend/src/services/api.ts`:
   - `createApiClient(baseURL)`: `axios.create` with `timeout: 10000`, `Content-Type: application/json`.
   - Request interceptor: inject `X-Correlation-ID: crypto.randomUUID()`.
   - Response error interceptor: extract `error.response?.data as ErrorResponse`, reject with normalized error object. If no response data, reject with `{ title: 'Network Error', detail: error.message, status: 0 }`.
   - Export `studentApiClient` and `configApiClient`.
3. Create `frontend/src/services/studentService.ts` implementing all methods per `specs/architecture/06-frontend-implementation-guide.md` Section 4.2: `register`, `search`, `getById`, `update`, `delete`, `getEnrollmentHistory`, `addEnrollment`.
4. Create `frontend/src/services/configurationService.ts` implementing: `getAll`, `getGrouped`, `upsert`, `delete` per Section 4.3.

**Dependencies:** FE-004.

---

## Phase 3: State Management and Custom Hooks

### [FE-007] Implement Toast Context and Provider

**Goal:** Create the global toast notification system using React Context API. All user feedback (success/error) flows through this context.

**Components:** `frontend/src/context/ToastContext.tsx`

**Integration:** None (pure UI state).

**Steps:**
1. Create `frontend/src/context/ToastContext.tsx` implementing `ToastProvider` and `useToast` hook:
   - Types: `ToastVariant = 'success' | 'error' | 'info'`, `Toast = { id: string; message: string; variant: ToastVariant }`.
   - State: `useState<Toast[]>([])`.
   - `addToast(message, variant)`: adds toast with `crypto.randomUUID()` id, sets `setTimeout` to remove after 4000ms.
   - Renders toast list using the `toast.tsx` component copied in FE-003.
   - Export `ToastProvider` and `useToast`.
2. Update `frontend/src/main.tsx` to wrap the entire application in `<ToastProvider>`.
3. Add `data-testid="toast-container"` to the toast list wrapper element.

**Dependencies:** FE-003.

---

### [FE-008] Implement useStudents Custom Hook

**Goal:** Encapsulate all student server-state management (fetch list, register, update, delete) in a single reusable hook.

**Components:** `frontend/src/hooks/useStudents.ts`

**Integration:**
- `studentService.search` -> `GET /students`
- `studentService.register` -> `POST /students`
- `studentService.update` -> `PUT /students/{studentId}`
- `studentService.delete` -> `DELETE /students/{studentId}`

**Steps:**
1. Create `frontend/src/hooks/useStudents.ts`:
   - State: `students: StudentResponse[]`, `pagination: PaginationMetadata`, `loading: boolean`.
   - `search(params: StudentSearchParams)`: sets loading, calls `studentService.search(params)`, updates students and pagination, catches errors and calls `addToast(error.detail, 'error')`.
   - `register(data: StudentBase)`: calls `studentService.register(data)`, on success calls `addToast('Student {studentId} registered successfully', 'success')`, returns student. On error: extracts `error.errors[].message` joined by '; ', calls `addToast(msg, 'error')`, re-throws.
   - `update(studentId, data)`: calls `studentService.update`, on success `addToast('Student updated', 'success')`. On 409: `addToast('Record was modified. Please refresh.', 'error')`.
   - `remove(studentId)`: calls `studentService.delete`, on success `addToast('Student deleted', 'success')`.
   - Return: `{ students, pagination, loading, search, register, update, remove }`.

**Dependencies:** FE-006, FE-007.

---

### [FE-009] Implement useConfigurations Custom Hook

**Goal:** Encapsulate all configuration server-state management in a reusable hook.

**Components:** `frontend/src/hooks/useConfigurations.ts`

**Integration:**
- `configurationService.getAll` -> `GET /configurations`
- `configurationService.upsert` -> `PUT /configurations/{category}/{key}`
- `configurationService.delete` -> `DELETE /configurations/{category}/{key}`

**Steps:**
1. Create `frontend/src/hooks/useConfigurations.ts`:
   - State: `configurations: Configuration[]`, `loading: boolean`.
   - `fetchAll(category?: ConfigurationCategory)`: calls `configurationService.getAll(category)`, sets `configurations`.
   - `upsert(category, key, data)`: calls `configurationService.upsert`, on success `addToast('Configuration saved', 'success')`, re-fetches list.
   - `remove(category, key)`: calls `configurationService.delete`, on success `addToast('Configuration deleted', 'success')`, re-fetches list.
   - All errors: `addToast(error.detail ?? 'Operation failed', 'error')`.
   - Return: `{ configurations, loading, fetchAll, upsert, remove }`.

**Dependencies:** FE-006, FE-007.

---

## Phase 4: Layout Components

### [FE-010] Migrate AppLayout, Sidebar, and Header from Reference Code

**Goal:** Establish the application shell (navigation + content area) by migrating layout components exactly from reference code.

**Components:**
- `frontend/src/components/layout/AppLayout.tsx`
- `frontend/src/components/layout/Sidebar.tsx`
- `frontend/src/components/layout/Header.tsx`

**Integration:** React Router `<Outlet />` renders page content.

**Steps:**
1. Create directory `frontend/src/components/layout/`.
2. COPY `frontend/reference-code/app/components/layout/AppLayout.tsx` to `frontend/src/components/layout/AppLayout.tsx`. Update import to add `import { Outlet } from 'react-router-dom'` and replace static content area with `<Outlet />`. Preserve all JSX structure and Tailwind classes exactly.
3. COPY `frontend/reference-code/app/components/layout/Sidebar.tsx` to `frontend/src/components/layout/Sidebar.tsx`. Replace any static `<a href>` navigation links with React Router `<NavLink>` pointing to `/students` and `/configurations`. Preserve all Tailwind classes, icons, and layout structure exactly.
4. COPY `frontend/reference-code/app/components/layout/Header.tsx` (if it exists) to `frontend/src/components/layout/Header.tsx`. If it does not exist, create a minimal header preserving whatever header structure is visible in `AppLayout.tsx` from reference code.
5. Add `data-testid="sidebar"`, `data-testid="nav-students"`, `data-testid="nav-configurations"` to the relevant elements for E2E targeting.

**Dependencies:** FE-003.

---

## Phase 5: Student Feature Components

### [FE-011] Migrate StudentRegisterDialog from Reference Code

**Goal:** Migrate the student registration dialog from reference code and wire it to the API via React Hook Form + Zod + useStudents hook. Keep JSX structure identical to reference code. Replace mockData behavior with real API calls only.

**Components:** `frontend/src/components/students/StudentRegisterDialog.tsx`

**Integration:**
- `useStudents().register` -> `POST /students`
- On success: close dialog, call `onSuccess()` to refresh list, show success toast (handled by hook).

**Steps:**
1. Create directory `frontend/src/components/students/`.
2. COPY `frontend/reference-code/app/components/StudentDialog.tsx` (or equivalent register dialog file) to `frontend/src/components/students/StudentRegisterDialog.tsx`.
3. REFACTOR ONLY - do not change any JSX structure or Tailwind classes:
   - Add `import { useForm } from 'react-hook-form'` and `import { zodResolver } from '@hookform/resolvers/zod'`.
   - Add `import { createStudentSchema, CreateStudentFormData } from '../../schemas/studentSchema'`.
   - Add `import { useStudents } from '../../hooks/useStudents'`.
   - Wire `useForm<CreateStudentFormData>({ resolver: zodResolver(createStudentSchema) })`.
   - Wire `{...register('fieldName')}` on each `<Input>` component.
   - Add error display `{errors.fieldName && <p className="text-sm text-destructive">{errors.fieldName.message}</p>}` below each input.
   - On form submit: call `registerStudent(data)` from `useStudents()`, catch errors (toast shown by hook), reset form on success, call `onClose()` and `onSuccess()`.
   - Add `data-testid` attributes: `btn-register-student`, `input-first-name`, `input-last-name`, `input-dob`, `input-mobile`, `input-email`, `input-aadhaar`, `btn-submit-register`.
4. Props interface: `{ open: boolean; onClose: () => void; onSuccess: () => void }`.

**Dependencies:** FE-005, FE-008, FE-003.

---

### [FE-012] Migrate StudentEditDialog from Reference Code

**Goal:** Migrate the student edit dialog and wire it to the update API. Preserve JSX structure exactly. Populate form with existing student data on open.

**Components:** `frontend/src/components/students/StudentEditDialog.tsx`

**Integration:**
- `useStudents().update` -> `PUT /students/{studentId}`
- Form pre-populated with `student.firstName`, `student.lastName`, `student.mobile`, `student.status`.
- `version` field passed as hidden value for optimistic locking.

**Steps:**
1. COPY the edit dialog from `frontend/reference-code/` (edit form variation of student dialog) to `frontend/src/components/students/StudentEditDialog.tsx`.
2. REFACTOR ONLY:
   - Wire `useForm<UpdateStudentFormData>({ resolver: zodResolver(updateStudentSchema), defaultValues: { firstName: student.firstName, lastName: student.lastName, mobile: student.mobile, status: student.status, version: student.version } })`.
   - Use `reset(student)` inside `useEffect` when `student` prop changes and `open` is true.
   - On submit: call `update(student.studentId, data)`.
   - On 409 response: toast "Record was modified. Please refresh." (handled by hook).
   - Add `data-testid="btn-submit-edit"`, `data-testid="select-status"`.
3. Props interface: `{ open: boolean; student: StudentResponse | null; onClose: () => void; onSuccess: () => void }`.

**Dependencies:** FE-005, FE-008, FE-003.

---

### [FE-013] Migrate StudentViewDialog from Reference Code

**Goal:** Migrate the read-only student detail view dialog. No form submission; purely displays all student fields.

**Components:** `frontend/src/components/students/StudentViewDialog.tsx`

**Integration:**
- Receives `student: StudentResponse` as prop (no API call; data already fetched).

**Steps:**
1. COPY the view dialog from `frontend/reference-code/` to `frontend/src/components/students/StudentViewDialog.tsx`.
2. REFACTOR ONLY: Replace any static/mock student data with props. Render all `StudentResponse` fields: `studentId`, `firstName`, `lastName`, `dateOfBirth`, `mobile`, `email`, `address`, `fathersName`, `mothersName`, `identificationMark`, `aadhaarNumber`, `status`, `createdAt`.
3. Add `data-testid="student-detail-dialog"`.
4. Props interface: `{ open: boolean; student: StudentResponse | null; onClose: () => void }`.

**Dependencies:** FE-003, FE-004.

---

### [FE-014] Migrate StudentsPage and Wire to API

**Goal:** Migrate the Students list page from reference code and replace all mockData with API-driven state. Preserve JSX structure, table layout, search bar, and action buttons exactly.

**Components:**
- `frontend/src/pages/StudentsPage.tsx`
- `frontend/src/components/students/StudentSearchBar.tsx`
- `frontend/src/components/students/StudentTable.tsx` (wrapped in `React.memo`)

**Integration:**
- On mount: `useStudents().search({})` to load initial list.
- Search by last name: `useStudents().search({ lastName: searchTerm })` on search submit.
- Register button click: open `StudentRegisterDialog`.
- Table row actions: View -> `StudentViewDialog`, Edit -> `StudentEditDialog`, Delete -> confirm then `useStudents().remove(studentId)`.
- Pagination: render pagination controls using `pagination.totalPages`, `pagination.page`.

**Steps:**
1. COPY `frontend/reference-code/app/pages/StudentsPage.tsx` to `frontend/src/pages/StudentsPage.tsx`.
2. REFACTOR ONLY:
   - Remove all `import mockData` or inline mock arrays.
   - Add `const { students, pagination, loading, search, remove } = useStudents()`.
   - Add `const [searchTerm, setSearchTerm] = useState('')`.
   - Add `useEffect(() => { search({}); }, [])` for initial load.
   - Replace mock data source for the table with `students` from hook.
   - Wire search form submission to `search({ lastName: searchTerm })`.
   - Wire Register button to `setRegisterOpen(true)`.
   - Wire Delete action to `remove(row.studentId)` after browser `confirm()`.
   - Add loading state: show skeleton or spinner when `loading === true`.
   - Add pagination controls below table.
3. COPY `frontend/reference-code/app/components/StudentTable.tsx` (or equivalent) to `frontend/src/components/students/StudentTable.tsx`. Wrap component export in `React.memo`. Add `data-testid="student-table"`, `data-testid="student-row-{studentId}"`.
4. Add `data-testid="btn-register-student"` to the Register button, `data-testid="search-input"` to the search input.

**Dependencies:** FE-011, FE-012, FE-013, FE-008.

---

## Phase 6: Configuration Feature Components

### [FE-015] Migrate ConfigurationAddDialog from Reference Code

**Goal:** Migrate the configuration add/upsert dialog and wire it to the upsert API.

**Components:** `frontend/src/components/configurations/ConfigurationAddDialog.tsx`

**Integration:**
- Form fields: `category` (select: GENERAL/ACADEMIC/FINANCIAL), `key` (text input), `value` (text input), `description` (optional), `dataType` (select), `isEncrypted` (checkbox).
- On submit: `useConfigurations().upsert(category, key, { value, description, dataType, isEncrypted })` -> `PUT /configurations/{category}/{key}`.

**Steps:**
1. COPY `frontend/reference-code/app/components/ConfigurationDialog.tsx` to `frontend/src/components/configurations/ConfigurationAddDialog.tsx`.
2. REFACTOR ONLY:
   - Wire `useForm<UpsertConfigurationFormData>({ resolver: zodResolver(upsertConfigurationSchema) })`.
   - Add `category` and `key` as separate controlled fields (not in Zod schema but in form state via `useState`).
   - On submit: call `upsert(category, key, data)`.
   - On success: close dialog, call `onSuccess()`.
   - Add `data-testid="select-category"`, `data-testid="input-key"`, `data-testid="input-value"`, `data-testid="btn-submit-config"`.
3. Props interface: `{ open: boolean; onClose: () => void; onSuccess: () => void }`.

**Dependencies:** FE-005, FE-009, FE-003.

---

### [FE-016] Migrate ConfigurationEditDialog from Reference Code

**Goal:** Migrate the configuration edit dialog, pre-populated with existing setting data.

**Components:** `frontend/src/components/configurations/ConfigurationEditDialog.tsx`

**Integration:**
- `useConfigurations().upsert` -> `PUT /configurations/{category}/{key}`

**Steps:**
1. COPY the edit variation of `frontend/reference-code/app/components/ConfigurationDialog.tsx` to `frontend/src/components/configurations/ConfigurationEditDialog.tsx`.
2. REFACTOR ONLY:
   - Pre-populate with `config.value`, `config.description`, `config.dataType`, `config.isEncrypted` using `reset()` in `useEffect`.
   - Category and key are read-only (displayed, not editable fields).
   - On submit: call `upsert(config.category, config.key, data)`.
3. Props interface: `{ open: boolean; config: Configuration | null; onClose: () => void; onSuccess: () => void }`.

**Dependencies:** FE-005, FE-009, FE-003.

---

### [FE-017] Migrate ConfigurationsPage and Wire to API

**Goal:** Migrate the Configurations list page from reference code and replace mockData with API-driven state.

**Components:**
- `frontend/src/pages/ConfigurationsPage.tsx`
- `frontend/src/components/configurations/ConfigurationTable.tsx` (wrapped in `React.memo`)

**Integration:**
- On mount: `useConfigurations().fetchAll()`.
- Category filter: `fetchAll(selectedCategory)`.
- Add button: open `ConfigurationAddDialog`.
- Edit action: open `ConfigurationEditDialog` with selected config.
- Delete action: confirm then `remove(config.category, config.key)`.

**Steps:**
1. COPY `frontend/reference-code/app/pages/ConfigurationsPage.tsx` to `frontend/src/pages/ConfigurationsPage.tsx`.
2. REFACTOR ONLY:
   - Remove mock data imports.
   - Add `const { configurations, loading, fetchAll, remove } = useConfigurations()`.
   - Add `useEffect(() => { fetchAll(); }, [])` for initial load.
   - Wire category filter `<Select>` to `fetchAll(category)`.
   - Wire Add button to open `ConfigurationAddDialog`.
   - Wire Edit table action to open `ConfigurationEditDialog`.
   - Wire Delete action to `remove(row.category, row.key)`.
3. COPY configuration table component from reference code to `frontend/src/components/configurations/ConfigurationTable.tsx`. Wrap in `React.memo`. Add `data-testid="config-table"`.
4. Add `data-testid="btn-add-config"`, `data-testid="filter-category"`.

**Dependencies:** FE-015, FE-016, FE-009.

---

## Phase 7: Routing and Application Entry Point

### [FE-018] Implement React Router and Application Entry Point

**Goal:** Wire all pages into a React Router DOM hierarchy with lazy loading. Wrap the application in all required providers.

**Components:**
- `frontend/src/pages/HomePage.tsx`
- `frontend/src/App.tsx`
- `frontend/src/main.tsx`

**Integration:** All routes resolve to lazy-loaded page components.

**Steps:**
1. Create `frontend/src/pages/HomePage.tsx`: simple redirect to `/students` using `<Navigate to="/students" replace />`.
2. Create `frontend/src/App.tsx`:
   ```tsx
   import { lazy, Suspense } from 'react';
   import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
   import { ToastProvider } from './context/ToastContext';
   import { AppLayout } from './components/layout/AppLayout';
   const StudentsPage = lazy(() => import('./pages/StudentsPage'));
   const ConfigurationsPage = lazy(() => import('./pages/ConfigurationsPage'));

   export function App() {
     return (
       <ToastProvider>
         <BrowserRouter>
           <Routes>
             <Route path="/" element={<AppLayout />}>
               <Route index element={<Navigate to="/students" replace />} />
               <Route path="students" element={<Suspense fallback={<div>Loading...</div>}><StudentsPage /></Suspense>} />
               <Route path="configurations" element={<Suspense fallback={<div>Loading...</div>}><ConfigurationsPage /></Suspense>} />
             </Route>
           </Routes>
         </BrowserRouter>
       </ToastProvider>
     );
   }
   ```
3. Update `frontend/src/main.tsx` to render `<App />`.
4. Update `frontend/index.html` title to `"School Management System"`.

**Dependencies:** FE-010, FE-014, FE-017, FE-007.

---

## Phase 8: Testing Infrastructure

### [FE-019] Set Up Vitest, React Testing Library, and MSW

**Goal:** Configure the testing toolchain so unit and component tests can run in isolation without a live backend.

**Components:** Test configuration files.

**Integration:** MSW mocks `GET /students`, `POST /students`, `PUT /students/{id}`, `DELETE /students/{id}`, `GET /configurations`, `PUT /configurations/{category}/{key}`.

**Steps:**
1. Create `frontend/vitest.config.ts`:
   ```ts
   import { defineConfig } from 'vitest/config';
   export default defineConfig({ test: { environment: 'jsdom', setupFiles: './src/test/setup.ts', globals: true } });
   ```
2. Create `frontend/src/test/setup.ts`: import `@testing-library/jest-dom`, start `msw` server `beforeAll`, reset handlers `afterEach`, close server `afterAll`.
3. Create `frontend/src/test/mocks/handlers.ts` with MSW request handlers for all Student and Configuration API endpoints, returning realistic payloads per the OpenAPI schemas.
4. Create `frontend/src/test/mocks/server.ts`: `setupServer(...handlers)`.
5. Add test script to `package.json`: `"test": "vitest run"`, `"test:watch": "vitest"`, `"test:coverage": "vitest run --coverage"`.

**Dependencies:** FE-001.

---

### [FE-020] Write Component Unit Tests

**Goal:** Achieve 70% statement coverage across components and hooks using Vitest + React Testing Library.

**Components:** Test files for student and configuration components.

**Integration:** All API calls mocked via MSW handlers from FE-019.

**Steps:**
1. Create `frontend/src/test/components/StudentRegisterDialog.test.tsx`:
   - Test: renders dialog when `open=true`.
   - Test: `firstName` validation shows error for empty or non-alpha input.
   - Test: `mobile` validation shows error for non-10-digit input.
   - Test: `dateOfBirth` validation shows error for age outside 3-18.
   - Test: successful submit calls `POST /students` and closes dialog.
   - Test: 409 conflict response shows toast error.
2. Create `frontend/src/test/components/StudentEditDialog.test.tsx`:
   - Test: pre-populates form with existing student data.
   - Test: submit calls `PUT /students/{studentId}` with `version` field.
3. Create `frontend/src/test/hooks/useStudents.test.ts`:
   - Test: `search()` populates `students` state from API response.
   - Test: `register()` adds success toast on HTTP 201.
   - Test: `remove()` calls delete endpoint and shows success toast.
4. Create `frontend/src/test/hooks/useConfigurations.test.ts`:
   - Test: `fetchAll()` populates `configurations` from API.
   - Test: `upsert()` calls `PUT` and shows success toast.

**Dependencies:** FE-019, FE-011, FE-012, FE-008, FE-009.

---

### [FE-021] Configure Playwright E2E Tests

**Goal:** Write critical happy-path E2E tests using `data-testid` locators. Each test must stay under 50 lines per `specs/TESTING_STRATEGY.md`.

**Components:** Playwright test files.

**Integration:** Requires running frontend at `http://localhost:5173` and backend services at `:8081` / `:8082`.

**Steps:**
1. Run `npx playwright install`.
2. Create `playwright.config.ts` with `baseURL: 'http://localhost:5173'`, `headless: true`, `testDir: './tests/e2e'`.
3. Create `frontend/tests/e2e/helpers/studentHelpers.ts`:
   ```ts
   export async function fillStudentForm(page, data) {
     await page.getByTestId('input-first-name').fill(data.firstName);
     await page.getByTestId('input-last-name').fill(data.lastName);
     await page.getByTestId('input-dob').fill(data.dateOfBirth);
     await page.getByTestId('input-mobile').fill(data.mobile);
   }
   ```
4. Create `frontend/tests/e2e/student-registration.spec.ts` (under 50 lines):
   - Navigate to `/students`.
   - Click `btn-register-student`.
   - Call `fillStudentForm` helper.
   - Click `btn-submit-register`.
   - Assert toast success message containing `"STD-"` student ID.
5. Create `frontend/tests/e2e/student-search.spec.ts` (under 50 lines):
   - Seed a student via API.
   - Navigate to `/students`.
   - Fill `search-input` with known last name.
   - Assert `student-table` row is visible with matching name.
6. Create `frontend/tests/e2e/configuration-crud.spec.ts` (under 50 lines):
   - Navigate to `/configurations`.
   - Click `btn-add-config`, fill form (category GENERAL, key `schoolName`, value `Test School`).
   - Submit. Assert toast success.
   - Assert row visible in `config-table`.

**Dependencies:** FE-018, FE-014, FE-017.

---

## Phase 9: Docker and Production Build

### [FE-022] Create Frontend Dockerfile and nginx.conf

**Goal:** Produce a production-ready Docker image for the React SPA using multi-stage build and Nginx.

**Components:** `frontend/Dockerfile`, `frontend/nginx.conf`.

**Integration:** Docker image serves the Vite build output. Nginx routes all non-asset paths to `index.html` for SPA client-side routing.

**Steps:**
1. Create `frontend/Dockerfile`:
   ```dockerfile
   FROM node:20-alpine AS builder
   WORKDIR /app
   COPY package*.json ./
   RUN npm ci
   COPY . .
   RUN npm run build

   FROM nginx:1.27-alpine AS runtime
   COPY --from=builder /app/dist /usr/share/nginx/html
   COPY nginx.conf /etc/nginx/conf.d/default.conf
   EXPOSE 80
   CMD ["nginx", "-g", "daemon off;"]
   ```
2. Create `frontend/nginx.conf`:
   ```nginx
   server {
     listen 80;
     root /usr/share/nginx/html;
     index index.html;
     location / { try_files $uri $uri/ /index.html; }
     location /api/ { return 404; }
   }
   ```
3. Create `frontend/.dockerignore`: `node_modules/`, `dist/`, `.git/`, `*.md`, `tests/`.
4. Verify Docker build: `docker build -t schoolms-frontend ./frontend`. Build must complete without errors.
5. Update root `docker-compose.yml` (from BE-029) to add:
   ```yaml
   frontend:
     build: ./frontend
     ports:
       - "80:80"
     depends_on:
       - student-service
       - config-service
   ```
6. Add `VITE_STUDENT_API_URL` and `VITE_CONFIG_API_URL` environment variables to the Docker service so they point to the container names at build time (use `ARG` in Dockerfile, pass via `--build-arg`).

**Dependencies:** FE-018.

---

## Deliverables Checklist

- [ ] UI mirrors Reference Code 1:1 (verified by visual inspection against screenshots in `screenshots/`).
- [ ] Forms validate via Zod schemas matching backend validation rules exactly.
- [ ] Service layer handles all HTTP methods: GET, POST, PUT, DELETE.
- [ ] All API errors surface as toast notifications with the error `detail` message.
- [ ] `docker build` passes successfully for the frontend image.
- [ ] Vitest coverage report shows >= 70% statement coverage for components and hooks.
- [ ] All Playwright E2E tests pass against a running full-stack environment.
- [ ] No custom CSS files or styled-components exist anywhere in `frontend/src/`.
- [ ] All interactive elements have `data-testid` attributes.
- [ ] `npm run build` produces a production Vite bundle without TypeScript errors.
