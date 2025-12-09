# Frontend Implementation Tasks - School Management System

## Overview
This document provides a comprehensive, sequential task list for implementing the Frontend application using React 19, Next.js 15, TypeScript, and Tailwind CSS.

**Target:** Complete frontend implementation with Student Management and Configuration Management features.

---

## Phase 1: Project Setup & Infrastructure

### [FE-001] Initialize Next.js 15 Project with TypeScript
**Goal:** Set up the Next.js 15 project with all necessary dependencies.

**Technical Details:**
- Run `npx create-next-app@latest sms-web --typescript --tailwind --app --use-pnpm`
- Configure project with:
  - TypeScript
  - Tailwind CSS
  - App Router (Next.js 15)
  - pnpm as package manager
- Install core dependencies:
  - `@tanstack/react-query` (v4.x) - Server state management
  - `axios` (v1.6) - HTTP client
  - `react-hook-form` (v7.49) - Form management
  - `zod` (v3.22) - Schema validation
  - `@hookform/resolvers` - Zod integration
  - `date-fns` - Date manipulation

**Acceptance Criteria:**
- Project structure is created
- All dependencies are installed
- `pnpm dev` starts development server
- TypeScript compiles without errors

**Dependencies:** None

---

### [FE-002] Configure TypeScript
**Goal:** Set up strict TypeScript configuration.

**Technical Details:**
- Update `tsconfig.json`:
  - Set `strict: true`
  - Enable `strictNullChecks`
  - Configure path aliases: `@/` for `src/`
  - Set target: ES2022
  - Enable `esModuleInterop`, `skipLibCheck`
- Create `types` directory for global type definitions
- Create `src/types/common.types.ts` for shared types

**Acceptance Criteria:**
- TypeScript compiles with strict mode
- Path aliases work correctly
- No type errors in initial setup

**Dependencies:** FE-001

---

### [FE-003] Configure Tailwind CSS
**Goal:** Set up Tailwind CSS with custom theme and utilities.

**Technical Details:**
- Update `tailwind.config.js`:
  - Configure content paths: `./src/**/*.{js,ts,jsx,tsx}`
  - Extend theme with custom colors:
    - primary: '#3B82F6'
    - secondary: '#10B981'
    - danger: '#EF4444'
    - warning: '#F59E0B'
  - Add custom spacing, fonts
- Install Tailwind plugins:
  - `@tailwindcss/forms` - Better form styling
  - `@tailwindcss/typography` (optional)
- Create `src/styles/globals.css`:
  - Import Tailwind directives
  - Add custom CSS utilities

**Acceptance Criteria:**
- Tailwind classes work in components
- Custom theme colors are applied
- Forms plugin is functional

**Dependencies:** FE-001

---

### [FE-004] Configure Environment Variables
**Goal:** Set up environment configuration for API connection.

**Technical Details:**
- Create `.env.local` file:
  - `NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1`
- Create `.env.development`:
  - Development-specific variables
- Create `.env.production`:
  - Production API URL
- Add `.env*.local` to `.gitignore`
- Create `src/lib/config.ts`:
  - Export typed environment variables
  - Validate required variables

**Acceptance Criteria:**
- Environment variables are accessible
- API URL is configurable per environment
- Type-safe access to variables

**Dependencies:** FE-001

---

### [FE-005] Set Up Axios API Client
**Goal:** Create configured Axios instance with interceptors.

**Technical Details:**
- Create `src/lib/api/client.ts`:
  - Create Axios instance with base URL from env
  - Set default headers: Content-Type, Accept
  - Configure timeout: 30000ms
- Create `src/lib/api/interceptors.ts`:
  - Request interceptor:
    - Generate correlation ID (UUID)
    - Add X-Correlation-ID header
    - Log requests in development
  - Response interceptor:
    - Handle successful responses
    - Transform error responses (RFC 7807 format)
    - Extract error message from ProblemDetail
- Create `src/lib/errors/ApiError.ts`:
  - Custom error class for API errors
  - Parse RFC 7807 Problem Details
  - Extract field-level errors

**Acceptance Criteria:**
- Axios instance is configured
- Correlation IDs are added to requests
- Error responses are properly handled
- ApiError class parses RFC 7807 format

**Dependencies:** FE-001, FE-004

---

### [FE-006] Configure React Query
**Goal:** Set up React Query for server state management.

**Technical Details:**
- Create `src/lib/queryClient.ts`:
  - Create QueryClient with default options:
    - `staleTime: 5 * 60 * 1000` (5 minutes)
    - `cacheTime: 10 * 60 * 1000` (10 minutes)
    - `retry: 1`
    - `refetchOnWindowFocus: false`
  - Configure error handling
  - Configure success handling
- Create `src/app/providers.tsx`:
  - Wrap app with `QueryClientProvider`
  - Add `ReactQueryDevtools` for development
- Update `src/app/layout.tsx`:
  - Wrap children with Providers component

**Acceptance Criteria:**
- React Query is configured
- QueryClient is available throughout app
- Devtools are accessible in development
- Default options are applied

**Dependencies:** FE-001

---

### [FE-007] Set Up ESLint and Prettier
**Goal:** Configure code quality and formatting tools.

**Technical Details:**
- Install dev dependencies:
  - `eslint-config-prettier`
  - `prettier`
  - `prettier-plugin-tailwindcss`
- Update `.eslintrc.js`:
  - Extend Next.js config
  - Add TypeScript rules
  - Add Prettier integration
  - Configure rules: no-unused-vars, no-console (warn)
- Create `.prettierrc`:
  - Configure formatting: semi, singleQuote, tabWidth, etc.
  - Add Tailwind plugin
- Add scripts to `package.json`:
  - `"lint": "next lint"`
  - `"format": "prettier --write \"src/**/*.{ts,tsx}\""`
  - `"type-check": "tsc --noEmit"`

**Acceptance Criteria:**
- ESLint checks pass
- Prettier formats code correctly
- Tailwind classes are sorted automatically
- npm scripts work

**Dependencies:** FE-001

---

## Phase 2: Shared Components & Layout

### [FE-008] Create Base UI Components - Button
**Goal:** Create reusable Button component with variants.

**Technical Details:**
- Create `src/components/ui/Button/Button.tsx`:
  - Props: children, variant, size, disabled, loading, onClick, type
  - Variants: primary, secondary, danger, outline, ghost
  - Sizes: sm, md, lg
  - Add loading spinner
  - Use Tailwind for styling
  - Forward ref for form compatibility
- Create `src/components/ui/Button/Button.test.tsx`:
  - Test all variants
  - Test disabled state
  - Test click handler
- Create `src/components/ui/Button/index.ts`:
  - Export Button component

**Acceptance Criteria:**
- Button renders with all variants
- Loading state shows spinner
- Disabled state prevents clicks
- Component tests pass

**Dependencies:** FE-003

---

### [FE-009] Create Base UI Components - Input
**Goal:** Create reusable Input component with validation support.

**Technical Details:**
- Create `src/components/ui/Input/Input.tsx`:
  - Props: label, error, type, placeholder, required, disabled, value, onChange, onBlur
  - Support for text, email, tel, date inputs
  - Display error message below input
  - Use Tailwind Forms plugin styling
  - Forward ref for React Hook Form
- Create `src/components/ui/Input/Input.test.tsx`:
  - Test input rendering
  - Test error display
  - Test onChange/onBlur handlers

**Acceptance Criteria:**
- Input renders with label
- Error messages are displayed
- Component integrates with React Hook Form
- Tests pass

**Dependencies:** FE-003

---

### [FE-010] Create Base UI Components - Card, Table, Modal, Spinner
**Goal:** Create additional reusable UI components.

**Components:**
1. **Card** (`src/components/ui/Card/Card.tsx`):
   - Props: title, children, footer
   - Styled container with shadow and padding

2. **Table** (`src/components/ui/Table/Table.tsx`):
   - Props: columns, data, loading
   - Responsive table with fixed header
   - Loading state with skeleton

3. **Modal** (`src/components/ui/Modal/Modal.tsx`):
   - Props: isOpen, onClose, title, children
   - Overlay with backdrop
   - Close on escape key
   - Focus trap

4. **Spinner** (`src/components/ui/Spinner/Spinner.tsx`):
   - Props: size, color
   - Loading indicator

**Acceptance Criteria:**
- All components render correctly
- Components are styled with Tailwind
- Components are responsive
- Unit tests pass

**Dependencies:** FE-003, FE-008

---

### [FE-011] Create Pagination Component
**Goal:** Create reusable Pagination component for lists.

**Technical Details:**
- Create `src/components/ui/Pagination/Pagination.tsx`:
  - Props: currentPage, totalPages, onPageChange, pageSize, onPageSizeChange
  - Display page numbers with ellipsis
  - Previous/Next buttons
  - Page size selector (10, 20, 50, 100)
  - Disable buttons at boundaries
- Create custom hook `src/hooks/usePagination.ts`:
  - Calculate visible page numbers
  - Handle page navigation logic

**Acceptance Criteria:**
- Pagination displays correct page numbers
- Page navigation works correctly
- Page size selection works
- Component is keyboard accessible

**Dependencies:** FE-008

---

### [FE-012] Create Layout Components
**Goal:** Create main layout structure for the application.

**Technical Details:**
- Create `src/components/layout/Header/Header.tsx`:
  - Display school logo and name
  - Navigation links: Students, Configurations, School Profile
  - Responsive hamburger menu for mobile
- Create `src/components/layout/Sidebar/Sidebar.tsx`:
  - Collapsible sidebar
  - Navigation menu
  - Active link highlighting
- Create `src/components/layout/Footer/Footer.tsx`:
  - Copyright notice
  - Version information
- Create `src/components/layout/PageLayout/PageLayout.tsx`:
  - Compose Header, Sidebar, Footer, and main content area
  - Responsive layout
- Update `src/app/layout.tsx`:
  - Use PageLayout as root layout

**Acceptance Criteria:**
- Layout renders correctly
- Navigation works
- Layout is responsive (mobile, tablet, desktop)
- Active links are highlighted

**Dependencies:** FE-008, FE-010

---

### [FE-013] Create Common Components
**Goal:** Create error, loading, and message components.

**Technical Details:**
- Create `src/components/common/ErrorMessage/ErrorMessage.tsx`:
  - Props: error (ApiError or string), onRetry
  - Display user-friendly error message
  - Show field errors if available
  - Retry button
- Create `src/components/common/LoadingSpinner/LoadingSpinner.tsx`:
  - Full-page loading spinner
  - Overlay with backdrop
- Create `src/components/common/SuccessMessage/SuccessMessage.tsx`:
  - Props: message, onClose
  - Toast-style success notification
  - Auto-dismiss after 3 seconds
- Create `src/components/common/ConfirmDialog/ConfirmDialog.tsx`:
  - Props: isOpen, title, message, onConfirm, onCancel
  - Confirmation modal for destructive actions

**Acceptance Criteria:**
- Error messages display correctly
- Loading spinner shows during async operations
- Success messages auto-dismiss
- Confirm dialog prevents accidental deletions

**Dependencies:** FE-008, FE-010

---

## Phase 3: Student Feature - API Integration

### [FE-014] Create Student TypeScript Types
**Goal:** Define TypeScript interfaces for Student domain.

**Technical Details:**
- Create `src/features/student/types/student.types.ts`:
  - `Student` interface:
    - All fields from StudentDTO
    - Use strict typing
  - `CreateStudentRequest` interface:
    - Fields for creation
  - `UpdateStudentRequest` interface:
    - Fields for update
  - `EnrollmentHistory` interface:
    - All enrollment history fields
  - `StudentSearchParams` interface:
    - lastName, guardianName, status, page, size, sort
  - `PageResponse<T>` generic interface:
    - content, pageable, totalPages, totalElements, etc.

**Acceptance Criteria:**
- All types are defined
- Types match API response format
- Types are exported correctly

**Dependencies:** FE-002

---

### [FE-015] Create Student API Client
**Goal:** Implement API calls for Student operations.

**Technical Details:**
- Create `src/features/student/api/studentApi.ts`:
  - Function: `createStudent(data: CreateStudentRequest): Promise<Student>`
    - POST /api/v1/students
    - Handle 201 response
    - Extract data from response
  - Function: `getStudent(studentKey: string): Promise<Student>`
    - GET /api/v1/students/{studentKey}
    - Handle 404 error
  - Function: `searchStudents(params: StudentSearchParams): Promise<PageResponse<Student>>`
    - GET /api/v1/students
    - Build query string from params
    - Return paginated response
  - Function: `updateStudent(studentKey: string, data: UpdateStudentRequest): Promise<Student>`
    - PUT /api/v1/students/{studentKey}
    - Handle 409 optimistic lock error
  - Function: `deleteStudent(studentKey: string): Promise<void>`
    - DELETE /api/v1/students/{studentKey}
  - Function: `getEnrollmentHistory(studentKey: string): Promise<EnrollmentHistory[]>`
    - GET /api/v1/students/{studentKey}/enrollment-history

**Acceptance Criteria:**
- All API functions are implemented
- Functions use configured Axios client
- Errors are properly thrown as ApiError
- Functions are typed correctly

**Dependencies:** FE-005, FE-014

---

### [FE-016] Create Student React Query Hooks
**Goal:** Create custom hooks for Student data fetching and mutations.

**Technical Details:**
- Create `src/features/student/hooks/useStudent.ts`:
  - `useStudent(studentKey: string)`:
    - Use `useQuery` with key `['student', studentKey]`
    - Call `getStudent` API
    - Handle loading, error states
    - Return `{ data, isLoading, error, refetch }`

- Create `src/features/student/hooks/useStudents.ts`:
  - `useStudents(searchParams: StudentSearchParams)`:
    - Use `useQuery` with key `['students', searchParams]`
    - Call `searchStudents` API
    - Enable pagination
    - Return `{ data, isLoading, error, refetch }`

- Create `src/features/student/hooks/useCreateStudent.ts`:
  - `useCreateStudent()`:
    - Use `useMutation` for createStudent API
    - Invalidate `['students']` query on success
    - Return `{ mutate, mutateAsync, isLoading, error }`

- Create `src/features/student/hooks/useUpdateStudent.ts`:
  - `useUpdateStudent()`:
    - Use `useMutation` for updateStudent API
    - Invalidate `['students']` and `['student', studentKey]` on success
    - Handle optimistic lock errors

- Create `src/features/student/hooks/useDeleteStudent.ts`:
  - `useDeleteStudent()`:
    - Use `useMutation` for deleteStudent API
    - Invalidate queries on success

**Acceptance Criteria:**
- All hooks are implemented
- Hooks use React Query correctly
- Cache invalidation works
- Error handling is in place
- Loading states are managed

**Dependencies:** FE-006, FE-015

---

## Phase 4: Student Feature - Validation Schemas

### [FE-017] Create Student Zod Validation Schemas
**Goal:** Define validation schemas for student forms.

**Technical Details:**
- Create `src/features/student/schemas/studentSchema.ts`:
  - `createStudentSchema`:
    - firstName: string, min(1), max(100), required
    - lastName: string, min(1), max(100), required
    - dateOfBirth: string (date format), required, custom validation:
      - Age must be between 3 and 18 years
      - Use refine() method with custom validator
    - mobile: string, regex pattern `^\+?[0-9]{10,15}$`, required
    - email: string, email format, optional
    - address: string, max(1000), optional
    - fatherNameOrGuardian: string, max(100), optional
    - motherName: string, max(100), optional
    - identificationMark: string, max(200), optional
    - adhaarNumber: string, regex `^[0-9]{12}$`, optional

  - `updateStudentSchema`:
    - firstName, lastName: required
    - mobile: required
    - status: enum('Active', 'Inactive'), required

- Create helper functions:
  - `calculateAge(dateOfBirth: string): number`
  - `isValidAge(age: number): boolean` (3-18 years)

**Acceptance Criteria:**
- Schemas validate all fields correctly
- Age validation works (3-18 years)
- Mobile and email validation works
- Adhaar validation works (12 digits)
- Error messages are user-friendly

**Dependencies:** FE-001

---

## Phase 5: Student Feature - UI Components

### [FE-018] Create Student Form Component
**Goal:** Create reusable form for creating and editing students.

**Technical Details:**
- Create `src/components/student/StudentForm/StudentForm.tsx`:
  - Props: initialData (optional), onSubmit, onCancel, isLoading
  - Use React Hook Form with Zod resolver
  - Fields:
    - First Name (Input)
    - Last Name (Input)
    - Date of Birth (Input type="date")
    - Mobile (Input type="tel")
    - Email (Input type="email")
    - Address (Textarea)
    - Father/Guardian Name (Input)
    - Mother Name (Input)
    - Identification Mark (Input)
    - Adhaar Number (Input)
    - Status (Select - only for update)
  - Display validation errors below each field
  - Submit button with loading state
  - Cancel button
  - Use custom Input components

**Acceptance Criteria:**
- Form renders all fields
- Validation works on submit
- Real-time validation on blur
- Form can be pre-filled with initialData
- Loading state disables form
- Unit tests pass

**Dependencies:** FE-009, FE-016, FE-017

---

### [FE-019] Create Student List Component
**Goal:** Create component to display list of students.

**Technical Details:**
- Create `src/components/student/StudentList/StudentList.tsx`:
  - Props: students, isLoading, onStudentClick
  - Display students in table format (responsive)
  - Columns:
    - Student Key
    - Name (First + Last)
    - Mobile
    - Guardian
    - Status (badge with color)
    - Actions (Edit, Delete buttons)
  - Loading skeleton while fetching
  - Empty state when no students
- Create `src/components/student/StudentList/StudentListItem.tsx`:
  - Single row in table
  - Click to view details

**Acceptance Criteria:**
- List displays all students
- Table is responsive (cards on mobile)
- Loading state shows skeleton
- Empty state is user-friendly
- Status badges have correct colors

**Dependencies:** FE-008, FE-010

---

### [FE-020] Create Student Search Component
**Goal:** Create search and filter component.

**Technical Details:**
- Create `src/components/student/StudentSearch/StudentSearch.tsx`:
  - Props: onSearch, initialParams
  - Search fields:
    - Last Name (Input with debounce)
    - Guardian Name (Input with debounce)
    - Status (Select: All, Active, Inactive)
  - Search button
  - Clear filters button
  - Use custom hook `useDebounce` for input fields
- Create `src/hooks/useDebounce.ts`:
  - Debounce value changes (500ms delay)

**Acceptance Criteria:**
- Search fields update URL query params
- Debounce prevents excessive API calls
- Clear button resets all filters
- Component is responsive

**Dependencies:** FE-009

---

### [FE-021] Create Student Card Component
**Goal:** Create card for displaying student details.

**Technical Details:**
- Create `src/components/student/StudentCard/StudentCard.tsx`:
  - Props: student, onEdit, onDelete
  - Display:
    - Student Key (highlighted)
    - Full Name (heading)
    - Age (calculated)
    - Mobile, Email
    - Address
    - Guardian names
    - Identification mark
    - Adhaar number (masked: XXX-XXX-1234)
    - Status (badge)
    - Created/Updated dates
  - Action buttons: Edit, Delete
  - Use Card component

**Acceptance Criteria:**
- Card displays all student information
- Adhaar is masked for privacy
- Status badge has correct color
- Action buttons work

**Dependencies:** FE-010

---

### [FE-022] Create Enrollment History Table Component
**Goal:** Create component to display enrollment history.

**Technical Details:**
- Create `src/components/student/EnrollmentHistoryTable/EnrollmentHistoryTable.tsx`:
  - Props: history, isLoading
  - Display in table format:
    - Date (formatted)
    - Status (badge)
    - Changed By
    - Remarks
  - Sort by date descending (most recent first)
  - Loading skeleton
  - Empty state if no history

**Acceptance Criteria:**
- Table displays enrollment history
- Dates are formatted nicely
- Most recent changes appear first
- Loading and empty states work

**Dependencies:** FE-010

---

## Phase 6: Student Feature - Pages

### [FE-023] Create Student List Page
**Goal:** Create main student listing page with search and pagination.

**Technical Details:**
- Create `src/app/students/page.tsx`:
  - Use `useStudents` hook with search params from URL
  - Implement search with StudentSearch component
  - Display results with StudentList component
  - Add Pagination component
  - Add "Register New Student" button (navigate to /students/new)
  - Handle loading state (full-page spinner)
  - Handle error state (error message with retry)
  - Update URL query params when searching/paginating
  - SEO metadata

**Acceptance Criteria:**
- Page displays list of students
- Search updates URL and filters results
- Pagination works correctly
- Loading and error states are handled
- "Register New" button navigates correctly

**Dependencies:** FE-012, FE-016, FE-019, FE-020, FE-011

---

### [FE-024] Create Student Registration Page
**Goal:** Create page for registering new students.

**Technical Details:**
- Create `src/app/students/new/page.tsx`:
  - Use `useCreateStudent` mutation hook
  - Render StudentForm component
  - Handle form submission:
    - Show loading state during submission
    - On success:
      - Show success message
      - Navigate to student detail page (`/students/{studentKey}`)
    - On error:
      - Display error message (field errors, duplicate mobile, etc.)
  - SEO metadata
  - Breadcrumb: Home > Students > New

**Acceptance Criteria:**
- Page renders form correctly
- Form submission creates student
- Success redirects to detail page
- Errors are displayed with details
- Age validation (3-18) works
- Duplicate mobile error is handled

**Dependencies:** FE-016, FE-018

---

### [FE-025] Create Student Detail Page
**Goal:** Create page for viewing student details.

**Technical Details:**
- Create `src/app/students/[studentKey]/page.tsx`:
  - Extract studentKey from URL params
  - Use `useStudent(studentKey)` hook
  - Display StudentCard component
  - Add "Edit" button (navigate to edit page)
  - Add "Delete" button (show confirm dialog, then delete)
  - Add "View Enrollment History" button
  - Handle loading state
  - Handle not found error (404)
  - SEO metadata with student name
  - Breadcrumb: Home > Students > {studentKey}

**Acceptance Criteria:**
- Page displays student details
- Edit button navigates to edit page
- Delete button shows confirmation
- Delete success redirects to list
- 404 error shows "Student not found"

**Dependencies:** FE-016, FE-021, FE-013

---

### [FE-026] Create Student Edit Page
**Goal:** Create page for editing student information.

**Technical Details:**
- Create `src/app/students/[studentKey]/edit/page.tsx`:
  - Extract studentKey from URL params
  - Use `useStudent(studentKey)` to fetch current data
  - Use `useUpdateStudent` mutation hook
  - Render StudentForm with initialData
  - Handle form submission:
    - On success:
      - Show success message
      - Navigate back to detail page
    - On error:
      - Display error message
      - Handle optimistic lock error (version conflict)
  - SEO metadata
  - Breadcrumb: Home > Students > {studentKey} > Edit

**Acceptance Criteria:**
- Page loads student data into form
- Form updates student correctly
- Optimistic lock errors are handled
- Success redirects to detail page
- Cancel button goes back

**Dependencies:** FE-016, FE-018, FE-025

---

### [FE-027] Create Enrollment History Page
**Goal:** Create page for viewing student enrollment history.

**Technical Details:**
- Create `src/app/students/[studentKey]/history/page.tsx`:
  - Extract studentKey from URL params
  - Fetch student and enrollment history
  - Display student name at top
  - Render EnrollmentHistoryTable component
  - Add "Back to Student" button
  - Handle loading state
  - Handle empty history
  - SEO metadata
  - Breadcrumb: Home > Students > {studentKey} > History

**Acceptance Criteria:**
- Page displays enrollment history
- History is sorted by date (newest first)
- Loading state is shown
- Empty state is handled
- Back button works

**Dependencies:** FE-022, FE-025

---

## Phase 7: Configuration Feature - API Integration

### [FE-028] Create Configuration TypeScript Types
**Goal:** Define TypeScript interfaces for Configuration domain.

**Technical Details:**
- Create `src/features/configuration/types/configuration.types.ts`:
  - `SchoolProfile` interface:
    - All fields from SchoolProfileDTO
  - `UpdateSchoolProfileRequest` interface:
    - Fields for update
  - `ConfigurationSetting` interface:
    - All fields from ConfigurationSettingDTO
  - `CreateSettingRequest` interface:
    - category, settingKey, settingValue, description
  - `UpdateSettingRequest` interface:
    - settingValue, description
  - `SettingCategory` type:
    - Union: 'General' | 'Academic' | 'Financial'

**Acceptance Criteria:**
- All types are defined
- Types match API response format
- Types are exported correctly

**Dependencies:** FE-002

---

### [FE-029] Create Configuration API Client
**Goal:** Implement API calls for Configuration operations.

**Technical Details:**
- Create `src/features/configuration/api/configurationApi.ts`:
  - Function: `getSchoolProfile(): Promise<SchoolProfile>`
    - GET /api/v1/school/profile
  - Function: `updateSchoolProfile(data: UpdateSchoolProfileRequest): Promise<SchoolProfile>`
    - PUT /api/v1/school/profile
  - Function: `getConfigurations(category?: SettingCategory): Promise<ConfigurationSetting[]>`
    - GET /api/v1/configurations
    - Optional category query param
  - Function: `getConfiguration(settingKey: string): Promise<ConfigurationSetting>`
    - GET /api/v1/configurations/{settingKey}
  - Function: `createConfiguration(data: CreateSettingRequest): Promise<ConfigurationSetting>`
    - POST /api/v1/configurations
  - Function: `updateConfiguration(settingKey: string, data: UpdateSettingRequest): Promise<ConfigurationSetting>`
    - PUT /api/v1/configurations/{settingKey}
  - Function: `deleteConfiguration(settingKey: string): Promise<void>`
    - DELETE /api/v1/configurations/{settingKey}

**Acceptance Criteria:**
- All API functions are implemented
- Functions use configured Axios client
- Errors are properly handled
- Functions are typed correctly

**Dependencies:** FE-005, FE-028

---

### [FE-030] Create Configuration React Query Hooks
**Goal:** Create custom hooks for Configuration data fetching and mutations.

**Technical Details:**
- Create hooks in `src/features/configuration/hooks/`:
  - `useSchoolProfile()`:
    - Use `useQuery` with key `['schoolProfile']`
    - Cache for 30 minutes
  - `useUpdateSchoolProfile()`:
    - Use `useMutation`
    - Invalidate `['schoolProfile']` on success
  - `useConfigurations(category?: SettingCategory)`:
    - Use `useQuery` with key `['configurations', category]`
    - Cache for 30 minutes
  - `useConfiguration(settingKey: string)`:
    - Use `useQuery` with key `['configuration', settingKey]`
  - `useCreateConfiguration()`:
    - Use `useMutation`
    - Invalidate `['configurations']` on success
  - `useUpdateConfiguration()`:
    - Use `useMutation`
    - Invalidate queries on success
  - `useDeleteConfiguration()`:
    - Use `useMutation`
    - Invalidate queries on success

**Acceptance Criteria:**
- All hooks are implemented
- Cache invalidation works
- Error handling is in place

**Dependencies:** FE-006, FE-029

---

## Phase 8: Configuration Feature - Validation & UI

### [FE-031] Create Configuration Zod Validation Schemas
**Goal:** Define validation schemas for configuration forms.

**Technical Details:**
- Create `src/features/configuration/schemas/configurationSchema.ts`:
  - `updateSchoolProfileSchema`:
    - schoolName: string, min(1), max(200), required
    - schoolCode: string, min(1), max(20), required
    - schoolLogoUrl: string, url, optional
    - address: string, max(1000), optional
    - contactNumber: string, regex `^\+?[0-9]{10,15}$`, optional
    - email: string, email, optional
    - principalName: string, max(100), optional
    - establishedDate: string (date format), optional

  - `createSettingSchema`:
    - category: enum('General', 'Academic', 'Financial'), required
    - settingKey: string, regex `^[a-z0-9._-]+$`, min(1), max(100), required
    - settingValue: string, required
    - description: string, max(500), optional

  - `updateSettingSchema`:
    - settingValue: string, required
    - description: string, max(500), optional

**Acceptance Criteria:**
- Schemas validate all fields correctly
- Setting key pattern validation works
- Error messages are user-friendly

**Dependencies:** FE-001

---

### [FE-032] Create School Profile Form Component
**Goal:** Create form for editing school profile.

**Technical Details:**
- Create `src/components/configuration/SchoolProfileForm/SchoolProfileForm.tsx`:
  - Props: initialData, onSubmit, onCancel, isLoading
  - Use React Hook Form with Zod resolver
  - Fields:
    - School Name
    - School Code
    - School Logo URL
    - Address (Textarea)
    - Contact Number
    - Email
    - Principal Name
    - Established Date
  - Submit and Cancel buttons

**Acceptance Criteria:**
- Form renders all fields
- Validation works
- Form can be pre-filled
- Unit tests pass

**Dependencies:** FE-009, FE-031

---

### [FE-033] Create Configuration Setting Form Component
**Goal:** Create form for creating/editing configuration settings.

**Technical Details:**
- Create `src/components/configuration/ConfigurationForm/ConfigurationForm.tsx`:
  - Props: initialData (optional), onSubmit, onCancel, isLoading
  - Use React Hook Form with Zod resolver
  - Fields:
    - Category (Select - disabled if editing)
    - Setting Key (Input - disabled if editing)
    - Setting Value (Textarea)
    - Description (Textarea, optional)
  - Submit and Cancel buttons

**Acceptance Criteria:**
- Form works for create and edit
- Key and category are disabled when editing
- Validation works
- Unit tests pass

**Dependencies:** FE-009, FE-031

---

### [FE-034] Create Configuration List Component
**Goal:** Create component to display configuration settings grouped by category.

**Technical Details:**
- Create `src/components/configuration/ConfigurationList/ConfigurationList.tsx`:
  - Props: configurations, isLoading, onEdit, onDelete
  - Group settings by category
  - Display as accordion or tabs (General, Academic, Financial)
  - For each setting, show:
    - Setting Key (bold)
    - Setting Value
    - Description
    - Updated At
    - Edit and Delete buttons
  - Loading skeleton
  - Empty state per category

**Acceptance Criteria:**
- List displays all settings grouped by category
- UI is organized and easy to navigate
- Edit and delete actions work
- Loading and empty states work

**Dependencies:** FE-008, FE-010

---

## Phase 9: Configuration Feature - Pages

### [FE-035] Create School Profile Page
**Goal:** Create page for viewing and editing school profile.

**Technical Details:**
- Create `src/app/school/profile/page.tsx`:
  - Use `useSchoolProfile` hook
  - Use `useUpdateSchoolProfile` mutation hook
  - Display profile in read-only view initially
  - Add "Edit" button to switch to edit mode
  - In edit mode:
    - Render SchoolProfileForm with current data
    - Handle form submission
    - On success: show success message, switch back to read mode
    - On error: display error message
  - Handle loading state
  - Handle case where profile doesn't exist (404)
  - SEO metadata
  - Breadcrumb: Home > School Profile

**Acceptance Criteria:**
- Page displays school profile
- Edit mode shows form
- Update works correctly
- Success shows message
- Errors are displayed

**Dependencies:** FE-030, FE-032

---

### [FE-036] Create Configuration Management Page
**Goal:** Create page for managing configuration settings.

**Technical Details:**
- Create `src/app/configurations/page.tsx`:
  - Use `useConfigurations` hook
  - Add category filter tabs (All, General, Academic, Financial)
  - Display ConfigurationList component
  - Add "Add New Setting" button (open modal)
  - Modal for creating new setting:
    - Render ConfigurationForm
    - Handle creation
    - Close modal on success
  - Handle edit:
    - Open modal with ConfigurationForm
    - Pre-fill with current data
    - Handle update
  - Handle delete:
    - Show confirm dialog
    - Delete on confirmation
  - SEO metadata
  - Breadcrumb: Home > Configurations

**Acceptance Criteria:**
- Page displays all settings
- Category filtering works
- Create, update, delete operations work
- Modals open and close correctly
- Success/error messages are shown

**Dependencies:** FE-030, FE-033, FE-034, FE-013

---

### [FE-037] Create Category-Specific Configuration Pages (Optional)
**Goal:** Create dedicated pages for each configuration category.

**Technical Details:**
- Create `src/app/configurations/[category]/page.tsx`:
  - Extract category from URL params
  - Use `useConfigurations(category)` hook
  - Display settings for specific category
  - Same CRUD operations as main page
  - SEO metadata
  - Breadcrumb: Home > Configurations > {Category}

**Acceptance Criteria:**
- Page displays category-specific settings
- All operations work
- Navigation between categories works

**Dependencies:** FE-036

---

## Phase 10: Testing

### [FE-038] Set Up Vitest for Unit Testing
**Goal:** Configure Vitest for component and utility testing.

**Technical Details:**
- Install dev dependencies:
  - `vitest`
  - `@testing-library/react`
  - `@testing-library/jest-dom`
  - `@testing-library/user-event`
  - `@vitest/ui`
- Create `vitest.config.ts`:
  - Configure test environment: jsdom
  - Configure globals
  - Configure path aliases
  - Configure coverage (threshold: 80%)
- Create `src/test/setup.ts`:
  - Import jest-dom matchers
  - Setup global test utilities
- Update `package.json` scripts:
  - `"test": "vitest"`
  - `"test:ui": "vitest --ui"`
  - `"test:coverage": "vitest --coverage"`

**Acceptance Criteria:**
- Vitest runs successfully
- Test environment is configured
- Coverage reports are generated

**Dependencies:** FE-001

---

### [FE-039] Write Unit Tests for UI Components
**Goal:** Write comprehensive unit tests for all UI components.

**Technical Details:**
- Test files to create:
  - `Button.test.tsx`:
    - Test all variants
    - Test loading state
    - Test disabled state
    - Test onClick handler
  - `Input.test.tsx`:
    - Test input rendering
    - Test error display
    - Test onChange/onBlur
  - `Card.test.tsx`:
    - Test rendering with title, children, footer
  - `Modal.test.tsx`:
    - Test open/close
    - Test backdrop click
    - Test escape key
  - `Pagination.test.tsx`:
    - Test page navigation
    - Test page size change
    - Test boundary conditions

**Acceptance Criteria:**
- All UI component tests pass
- Test coverage > 80%
- Edge cases are tested

**Dependencies:** FE-038, FE-008, FE-009, FE-010, FE-011

---

### [FE-040] Write Unit Tests for Student Components
**Goal:** Write tests for Student-specific components.

**Technical Details:**
- Test files to create:
  - `StudentForm.test.tsx`:
    - Test form rendering
    - Test validation errors
    - Test submission
    - Test pre-filled data
  - `StudentList.test.tsx`:
    - Test rendering list
    - Test loading state
    - Test empty state
  - `StudentCard.test.tsx`:
    - Test data display
    - Test action buttons
  - `StudentSearch.test.tsx`:
    - Test search input
    - Test filter changes
    - Test clear filters

**Acceptance Criteria:**
- All student component tests pass
- Form validation is tested
- User interactions are tested

**Dependencies:** FE-038, FE-018, FE-019, FE-020, FE-021

---

### [FE-041] Write Unit Tests for Configuration Components
**Goal:** Write tests for Configuration-specific components.

**Technical Details:**
- Test files to create:
  - `SchoolProfileForm.test.tsx`
  - `ConfigurationForm.test.tsx`
  - `ConfigurationList.test.tsx`

**Acceptance Criteria:**
- All configuration component tests pass
- Test coverage > 80%

**Dependencies:** FE-038, FE-032, FE-033, FE-034

---

### [FE-042] Write Unit Tests for Custom Hooks
**Goal:** Write tests for custom React hooks.

**Technical Details:**
- Test files to create:
  - `useDebounce.test.ts`:
    - Test debounce delay
    - Test value updates
  - `usePagination.test.ts`:
    - Test page number calculations
    - Test ellipsis logic

**Acceptance Criteria:**
- All hook tests pass
- Hooks work as expected

**Dependencies:** FE-038, FE-020, FE-011

---

### [FE-043] Set Up Playwright for E2E Testing
**Goal:** Configure Playwright for end-to-end testing.

**Technical Details:**
- Install dependencies:
  - `@playwright/test`
- Create `playwright.config.ts`:
  - Configure browsers: chromium, firefox, webkit
  - Configure base URL: `http://localhost:3000`
  - Configure test directory: `tests/e2e`
  - Configure screenshots on failure
  - Configure video on failure
- Create `tests/fixtures/testData.ts`:
  - Sample student data
  - Sample configuration data
- Update `package.json` scripts:
  - `"test:e2e": "playwright test"`
  - `"test:e2e:ui": "playwright test --ui"`

**Acceptance Criteria:**
- Playwright is configured
- Test runner works
- Browsers are installed

**Dependencies:** FE-001

---

### [FE-044] Write E2E Tests for Student Registration Flow
**Goal:** Write end-to-end test for complete student registration.

**Technical Details:**
- Create `tests/e2e/student/student-registration.spec.ts`:
  - Test: Navigate to registration page
  - Test: Fill all required fields
  - Test: Submit form
  - Test: Verify redirect to detail page
  - Test: Verify student appears in list
  - Test: Verify data is correct
  - Test: Validation errors display correctly
  - Test: Duplicate mobile error is handled

**Acceptance Criteria:**
- E2E test covers complete registration flow
- Test passes consistently
- Error scenarios are tested

**Dependencies:** FE-043, FE-024

---

### [FE-045] Write E2E Tests for Student Search and Update
**Goal:** Write end-to-end tests for search and update flows.

**Technical Details:**
- Create `tests/e2e/student/student-search.spec.ts`:
  - Test: Search by last name
  - Test: Search by guardian name
  - Test: Filter by status
  - Test: Pagination
  - Test: Click to view details

- Create `tests/e2e/student/student-update.spec.ts`:
  - Test: Navigate to edit page
  - Test: Update fields
  - Test: Submit form
  - Test: Verify updates on detail page

**Acceptance Criteria:**
- E2E tests cover search and update flows
- Tests pass consistently

**Dependencies:** FE-043, FE-023, FE-026

---

### [FE-046] Write E2E Tests for Configuration Management
**Goal:** Write end-to-end test for configuration management.

**Technical Details:**
- Create `tests/e2e/configuration/configuration-management.spec.ts`:
  - Test: View school profile
  - Test: Edit school profile
  - Test: View configurations
  - Test: Create new setting
  - Test: Update existing setting
  - Test: Delete setting
  - Test: Category filtering

**Acceptance Criteria:**
- E2E test covers configuration CRUD
- Tests pass consistently

**Dependencies:** FE-043, FE-035, FE-036

---

## Phase 11: Optimization & Accessibility

### [FE-047] Implement Code Splitting and Lazy Loading
**Goal:** Optimize bundle size with code splitting.

**Technical Details:**
- Use Next.js dynamic imports:
  - Lazy load Modal component
  - Lazy load heavy components (tables, forms)
  - Split routes at page level (automatic with App Router)
- Use React.lazy() for component-level splitting
- Analyze bundle size with `@next/bundle-analyzer`:
  - Install `@next/bundle-analyzer`
  - Configure in `next.config.js`
  - Run `pnpm build` to generate report
  - Identify large dependencies
  - Consider alternatives or split further

**Acceptance Criteria:**
- Initial bundle size < 500KB
- Code splitting is implemented
- Lazy loaded components work correctly
- Bundle analyzer shows optimized chunks

**Dependencies:** FE-001

---

### [FE-048] Add Loading States and Skeleton Screens
**Goal:** Improve perceived performance with loading states.

**Technical Details:**
- Create skeleton components:
  - `StudentListSkeleton.tsx`:
    - Animated skeleton rows
  - `StudentCardSkeleton.tsx`:
    - Animated skeleton for card layout
  - `TableSkeleton.tsx`:
    - Generic table skeleton
- Use React Suspense for loading boundaries:
  - Wrap async components with Suspense
  - Provide fallback with skeleton
- Update pages to use loading.tsx:
  - Create `loading.tsx` files in app directory
  - Use skeletons as loading UI

**Acceptance Criteria:**
- Skeleton screens are displayed during loading
- Loading states are smooth and animated
- Suspense boundaries work correctly

**Dependencies:** FE-010

---

### [FE-049] Implement Accessibility (a11y) Improvements
**Goal:** Ensure WCAG 2.1 Level AA compliance.

**Technical Details:**
- Add ARIA attributes:
  - `aria-label`, `aria-describedby`, `aria-invalid`
  - `role` attributes for custom components
- Implement keyboard navigation:
  - Tab order is logical
  - All interactive elements are keyboard accessible
  - Escape key closes modals
  - Enter key submits forms
- Add focus styles:
  - Visible focus indicators (not just outline)
  - Custom focus-visible styles
- Ensure color contrast:
  - Check contrast ratios with tool
  - Adjust colors if needed
- Add screen reader support:
  - Meaningful labels for form fields
  - Error announcements
  - Loading state announcements
- Use semantic HTML:
  - Proper heading hierarchy (h1, h2, h3)
  - `<nav>`, `<main>`, `<header>`, `<footer>`
- Install and run accessibility linters:
  - `eslint-plugin-jsx-a11y`
  - Configure in ESLint

**Acceptance Criteria:**
- Lighthouse accessibility score > 90
- Keyboard navigation works throughout app
- Screen reader can navigate app
- Color contrast meets WCAG standards
- Accessibility linter passes

**Dependencies:** FE-012

---

### [FE-050] Add SEO and Meta Tags
**Goal:** Improve SEO with proper meta tags.

**Technical Details:**
- Update `src/app/layout.tsx`:
  - Add metadata object:
    - title: "School Management System"
    - description: "..."
    - keywords: "..."
  - Add Open Graph tags
  - Add Twitter Card tags
- Add page-specific metadata:
  - Each page exports metadata object
  - Dynamic metadata for detail pages (student name, etc.)
- Add structured data (JSON-LD):
  - Organization schema for school profile
- Add robots.txt and sitemap.xml:
  - Create `public/robots.txt`
  - Generate sitemap dynamically (optional)

**Acceptance Criteria:**
- Meta tags are present on all pages
- Dynamic metadata works for detail pages
- Lighthouse SEO score > 90

**Dependencies:** FE-012

---

## Phase 12: Documentation & Deployment

### [FE-051] Create Docker Image
**Goal:** Create Docker image for production deployment.

**Technical Details:**
- Create `Dockerfile`:
  - Multi-stage build
  - Stage 1: Build app with Node 20
  - Stage 2: Production image with Node 20 Alpine
  - Copy build artifacts
  - Expose port 3000
  - Set CMD to start Next.js
- Create `.dockerignore`:
  - Exclude node_modules, .next, .git
- Build image:
  - `docker build -t sms/frontend:1.0.0 .`
- Test image:
  - `docker run -p 3000:3000 sms/frontend:1.0.0`

**Acceptance Criteria:**
- Docker image builds successfully
- Image runs correctly
- Image size is optimized
- Environment variables work

**Dependencies:** FE-001

---

### [FE-052] Write Frontend Documentation
**Goal:** Create comprehensive documentation for developers.

**Technical Details:**
- Create `frontend/sms-web/README.md`:
  - Project overview
  - Prerequisites (Node 20, pnpm)
  - Installation instructions
  - Development commands:
    - `pnpm dev` - Start dev server
    - `pnpm build` - Build for production
    - `pnpm start` - Start production server
    - `pnpm lint` - Run linter
    - `pnpm format` - Format code
    - `pnpm test` - Run unit tests
    - `pnpm test:e2e` - Run E2E tests
  - Project structure explanation
  - Component documentation
  - API integration guide
  - Testing guide
  - Deployment guide
  - Troubleshooting section
- Create component documentation:
  - Document props and usage for each component
  - Add Storybook (optional for Phase 2)

**Acceptance Criteria:**
- README is complete and accurate
- Setup instructions work for new developers
- All commands are documented

**Dependencies:** FE-051

---

### [FE-053] Set Up CI/CD Pipeline
**Goal:** Automate testing and deployment with GitHub Actions.

**Technical Details:**
- Create `.github/workflows/frontend-ci.yml`:
  - Trigger: push to main, develop; pull requests
  - Jobs:
    - **lint**: Run ESLint
    - **type-check**: Run TypeScript compiler
    - **test**: Run unit tests with Vitest
    - **e2e**: Run Playwright tests
    - **build**: Build production app
    - **docker**: Build Docker image (optional)
  - Use matrix strategy for Node versions (optional)
  - Cache node_modules for faster builds
  - Upload test coverage reports

**Acceptance Criteria:**
- CI pipeline runs on push and PR
- All checks pass
- Build artifacts are created
- Coverage reports are uploaded

**Dependencies:** FE-038, FE-043, FE-051

---

## Summary

**Total Tasks:** 53

**Estimated Timeline:**
- Phase 1 (Setup): 2-3 days
- Phase 2 (Shared Components): 2-3 days
- Phase 3-4 (Student API & Validation): 2 days
- Phase 5-6 (Student UI & Pages): 4-5 days
- Phase 7-8 (Configuration API & UI): 3-4 days
- Phase 9 (Configuration Pages): 2 days
- Phase 10 (Testing): 4-5 days
- Phase 11 (Optimization): 2-3 days
- Phase 12 (Documentation & Deployment): 2 days

**Total Estimated Time:** 23-32 days

**Key Success Criteria:**
- All 53 tasks completed
- Unit test coverage > 80%
- E2E tests cover critical user flows
- Lighthouse scores: Performance > 80, Accessibility > 90, SEO > 90
- Application is responsive (mobile, tablet, desktop)
- All API integrations work correctly
- Docker image is production-ready

**Best Practices:**
- Use TypeScript strictly (no `any` types)
- Follow React and Next.js best practices
- Write tests for all components
- Ensure accessibility (WCAG 2.1 Level AA)
- Optimize bundle size and performance
- Document all components and utilities
