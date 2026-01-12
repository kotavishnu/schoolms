# Frontend Implementation Plan
**School Management System - React Developer**

**Version**: 1.0
**Date**: 2026-01-08
**Execution Model**: Waterfall / Single-Pass Implementation

---

## Overview

This document provides a **sequential, atomic task list** for implementing the complete React frontend for the School Management System. All tasks must be completed in order, building upon the reference code in `frontend/reference-code/`.

**Scope**: Complete React application with Vite, TypeScript, Tailwind CSS, Shadcn/ui components, React Hook Form, and full backend API integration.

**Reference Code**: Located in `frontend/reference-code/` - provides UI components and styling to be enhanced with backend integration.

**Constraints**:
- No authentication/authorization (Phase 1)
- Service Layer Pattern is mandatory
- Use Context API for state (not Redux/Zustand)
- Follow exact directory structure from spec

---

## Project Setup Tasks

### [FE-001] Initialize Vite React TypeScript Project
**Goal**: Create the frontend project using Vite

**Technical Details**:
- Run: `npm create vite@latest frontend -- --template react-ts`
- Navigate to `frontend` directory
- Verify package.json created
- Install dependencies: `npm install`
- Test dev server: `npm run dev`

**Components**: None
**API Integration**: None
**Dependencies**: None

---

### [FE-002] Install Core Dependencies
**Goal**: Add React Router, Axios, and essential libraries

**Technical Details**:
- Run: `npm install react-router-dom axios`
- Run: `npm install -D @types/node`
- Verify package.json updated
- Run `npm install` to ensure clean install

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-003] Install and Configure Tailwind CSS
**Goal**: Set up Tailwind CSS v4 with PostCSS

**Technical Details**:
- Run: `npm install -D tailwindcss@latest postcss autoprefixer`
- Run: `npx tailwindcss init -p`
- Configure `tailwind.config.ts`:
  - Content paths: `./index.html`, `./src/**/*.{js,ts,jsx,tsx}`
  - Add theme extensions if needed
- Create `src/styles/tailwind.css` with Tailwind directives
- Import in `src/main.tsx`

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-004] Install and Configure Shadcn/ui
**Goal**: Set up Shadcn/ui component library

**Technical Details**:
- Run: `npx shadcn-ui@latest init`
- Select options:
  - Style: Default
  - Base color: Slate
  - CSS variables: Yes
- This creates `components/ui/` directory structure
- Verify `components.json` created

**Components**: ui/* (installed by CLI)
**API Integration**: None
**Dependencies**: Requires FE-003

---

### [FE-005] Install Form Management and Validation Libraries
**Goal**: Add React Hook Form and Zod

**Technical Details**:
- Run: `npm install react-hook-form zod @hookform/resolvers`
- Verify package.json updated

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-006] Install Additional Dependencies
**Goal**: Add icons, notifications, and utilities

**Technical Details**:
- Run: `npm install lucide-react sonner date-fns`
- lucide-react: Icon library
- sonner: Toast notifications
- date-fns: Date formatting and manipulation

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-007] Install Development Tools
**Goal**: ESLint, Prettier, TypeScript plugins

**Technical Details**:
- Run: `npm install -D @typescript-eslint/eslint-plugin @typescript-eslint/parser`
- Run: `npm install -D eslint-config-prettier eslint-plugin-react`
- Run: `npm install -D prettier`
- Create `.prettierrc` with formatting rules
- Create `.eslintrc.cjs` if not exists

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-008] Create Project Directory Structure
**Goal**: Establish complete folder structure per specification

**Technical Details**:
Create directories under `src/`:
- `components/layout/`
- `components/students/`
- `components/configurations/`
- `components/common/`
- `components/ui/` (already created by Shadcn)
- `pages/`
- `services/`
- `contexts/`
- `hooks/`
- `types/`
- `utils/`
- `styles/`

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-009] Create Environment Configuration Files
**Goal**: Set up environment variables

**Technical Details**:
- Create `.env.development`:
  ```
  VITE_API_BASE_URL=http://localhost:8081
  VITE_CONFIG_API_BASE_URL=http://localhost:8082
  ```
- Create `.env.production`:
  ```
  VITE_API_BASE_URL=https://api.schoolms.com
  VITE_CONFIG_API_BASE_URL=https://config-api.schoolms.com
  ```
- Add to `.gitignore`: `.env.local`

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

## Type Definitions

### [FE-010] Create Student TypeScript Interfaces
**Goal**: Define Student data models

**Technical Details**:
- File: `src/types/student.ts`
- Define interfaces:
  - `Student`: Complete student object (matches backend StudentResponse)
  - `StudentCreateDto`: Fields for creating student (matches backend CreateStudentRequest)
  - `StudentUpdateDto`: Fields for updating (firstName, lastName, phone, status - all optional)
  - `StudentListResponse`: { students: Student[], totalCount: number, activeCount: number, inactiveCount: number }
  - `StudentStatistics`: { totalStudents, activeStudents, inactiveStudents }
- Include field constraints as JSDoc comments

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

### [FE-011] Create Configuration TypeScript Interfaces
**Goal**: Define Configuration data models

**Technical Details**:
- File: `src/types/configuration.ts`
- Define interfaces:
  - `Configuration`: Complete config object
  - `ConfigCategory`: Type union ('GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM')
  - `ConfigurationCreateDto`: Fields for creating
  - `ConfigurationUpdateDto`: value, description (optional)
  - `ConfigurationListResponse`: { configurations: Configuration[], totalCount: number }

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

### [FE-012] Create API Response Types
**Goal**: Define generic API response wrappers

**Technical Details**:
- File: `src/types/api.ts`
- Define interfaces:
  - `ApiResponse<T>`: Generic wrapper with success, data, error, message
  - `ApiError`: { code, message, details? }
  - `ProblemDetail`: RFC 7807 structure (type, title, status, detail, instance, timestamp, traceId, errors?)

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

## Service Layer (API Integration)

### [FE-013] Create HTTP Client (Axios Instance)
**Goal**: Set up centralized API client with interceptors

**Technical Details**:
- File: `src/services/api.ts`
- Create `apiClient` AxiosInstance:
  - Base URL from env: `import.meta.env.VITE_API_BASE_URL`
  - Timeout: 10000ms
  - Headers: Content-Type: application/json
- Request interceptor:
  - Add X-Request-ID (UUID) for tracing
  - Future: Add Authorization header
- Response interceptor:
  - Handle 401 (unauthorized) - log warning
  - Handle 500 (server error) - log error
  - Re-throw for component handling
- Export: `export default apiClient`

**Components**: None
**API Integration**: Axios configuration
**Dependencies**: Requires FE-002, FE-009

---

### [FE-014] Create Student Service
**Goal**: Encapsulate all Student API calls

**Technical Details**:
- File: `src/services/studentService.ts`
- Import: `apiClient` from `./api`, types from `@/types/student`
- Export object: `studentService` with methods:
  - `getAll(params?: { search?, status?, page?, size? }): Promise<StudentListResponse>`
    - GET /api/v1/students
  - `getById(id: string): Promise<Student>`
    - GET /api/v1/students/{id}
  - `create(student: StudentCreateDto): Promise<Student>`
    - POST /api/v1/students
  - `update(id: string, updates: StudentUpdateDto): Promise<Student>`
    - PATCH /api/v1/students/{id}
  - `delete(id: string): Promise<void>`
    - DELETE /api/v1/students/{id}
  - `validatePhone(phone: string, excludeId?: string): Promise<boolean>`
    - POST /api/v1/students/validate-phone
  - `getStatistics(): Promise<StudentStatistics>`
    - GET /api/v1/students/statistics
- All methods handle errors, extract data from AxiosResponse

**Components**: None
**API Integration**: All Student API endpoints
**Dependencies**: Requires FE-010, FE-013

---

### [FE-015] Create Configuration Service
**Goal**: Encapsulate all Configuration API calls

**Technical Details**:
- File: `src/services/configurationService.ts`
- Import: Create separate Axios instance for config service (different base URL)
  - Base URL: `import.meta.env.VITE_CONFIG_API_BASE_URL`
- Export object: `configurationService` with methods:
  - `getAll(category?: ConfigCategory): Promise<ConfigurationListResponse>`
    - GET /api/v1/configurations
  - `getById(id: string): Promise<Configuration>`
    - GET /api/v1/configurations/{id}
  - `create(config: ConfigurationCreateDto): Promise<Configuration>`
    - POST /api/v1/configurations
  - `update(id: string, updates: ConfigurationUpdateDto): Promise<Configuration>`
    - PATCH /api/v1/configurations/{id}
  - `delete(id: string): Promise<void>`
    - DELETE /api/v1/configurations/{id}

**Components**: None
**API Integration**: All Configuration API endpoints
**Dependencies**: Requires FE-011, FE-013

---

## Custom Hooks

### [FE-016] Create useStudents Hook
**Goal**: Manage student data fetching and state

**Technical Details**:
- File: `src/hooks/useStudents.ts`
- Export: `useStudents(filters?: { search?, status? })`
- State:
  - `students: Student[]`
  - `loading: boolean`
  - `error: string | null`
  - `statistics: StudentStatistics | null`
- Effects:
  - Fetch students on mount and when filters change
  - Fetch statistics on mount
- Methods:
  - `refetch()`: Reload students
  - `createStudent(data: StudentCreateDto): Promise<Student>`
  - `updateStudent(id: string, data: StudentUpdateDto): Promise<Student>`
  - `deleteStudent(id: string): Promise<void>`
- Use studentService for all API calls
- Handle errors, set loading states

**Components**: None
**API Integration**: Via studentService
**Dependencies**: Requires FE-014

---

### [FE-017] Create useConfigurations Hook
**Goal**: Manage configuration data

**Technical Details**:
- File: `src/hooks/useConfigurations.ts`
- Export: `useConfigurations(category?: ConfigCategory)`
- State:
  - `configurations: Configuration[]`
  - `loading: boolean`
  - `error: string | null`
- Effects: Fetch configurations on mount, when category changes
- Methods:
  - `refetch()`
  - `createConfiguration(data: ConfigurationCreateDto): Promise<Configuration>`
  - `updateConfiguration(id: string, data: ConfigurationUpdateDto): Promise<Configuration>`
  - `deleteConfiguration(id: string): Promise<void>`

**Components**: None
**API Integration**: Via configurationService
**Dependencies**: Requires FE-015

---

### [FE-018] Create useToast Hook (Wrapper for Sonner)
**Goal**: Simplify toast notifications

**Technical Details**:
- File: `src/hooks/useToast.ts`
- Wrap Sonner toast methods
- Export:
  - `success(message: string)`
  - `error(message: string)`
  - `info(message: string)`
  - `warning(message: string)`

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-006

---

### [FE-019] Create useDebounce Hook
**Goal**: Debounce search inputs

**Technical Details**:
- File: `src/hooks/useDebounce.ts`
- Export: `useDebounce<T>(value: T, delay: number): T`
- Use setTimeout to delay value updates

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

## Utility Functions

### [FE-020] Create Validation Utilities
**Goal**: Reusable validation helpers

**Technical Details**:
- File: `src/utils/validation.ts`
- Export functions:
  - `isValidPhone(phone: string): boolean` - 10 digits
  - `isValidEmail(email: string): boolean` - email regex
  - `isValidAdhaar(adhaar: string): boolean` - 12 digits
  - `isAgeInRange(dob: Date): boolean` - 3-18 years

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

### [FE-021] Create Formatting Utilities
**Goal**: Format dates, status badges

**Technical Details**:
- File: `src/utils/formatting.ts`
- Export functions:
  - `formatDate(date: string | Date, format?: string): string` - use date-fns
  - `formatStatus(status: 'ACTIVE' | 'INACTIVE'): { text, color }` - badge styling
  - `maskPhone(phone: string): string` - mask middle digits for privacy

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-006, FE-008

---

### [FE-022] Create Constants File
**Goal**: Centralize magic strings and config

**Technical Details**:
- File: `src/utils/constants.ts`
- Export:
  - `STUDENT_STATUS`: { ACTIVE: 'ACTIVE', INACTIVE: 'INACTIVE', ALL: 'ALL' }
  - `CONFIG_CATEGORIES`: Array of ConfigCategory values
  - `AGE_RANGE`: { MIN: 3, MAX: 18 }
  - `PHONE_LENGTH`: 10
  - `ADHAAR_LENGTH`: 12

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-008

---

## Shared UI Components (Migrate from Reference Code)

### [FE-023] Migrate Shadcn/ui Components from Reference Code
**Goal**: Copy and verify all ui components from reference

**Technical Details**:
- Source: `frontend/reference-code/app/components/ui/`
- Destination: `src/components/ui/`
- Migrate files:
  - button.tsx
  - dialog.tsx
  - input.tsx
  - label.tsx
  - select.tsx
  - card.tsx
  - badge.tsx
  - table.tsx
  - form.tsx (React Hook Form integration)
  - sonner.tsx (Toast component)
- Verify imports work with new project structure
- Update relative paths as needed

**Components**: All ui/* components
**API Integration**: None
**Dependencies**: Requires FE-004, FE-005, FE-006

---

## Layout Components

### [FE-024] Create Header Component
**Goal**: Top navigation bar

**Technical Details**:
- File: `src/components/layout/Header.tsx`
- Reference: `frontend/reference-code/app/components/Header.tsx`
- Migrate and enhance:
  - Logo/Brand
  - Navigation links: Home, Students, Configurations
  - Active link highlighting (React Router NavLink)
  - Responsive design (mobile menu)
- Props: None
- State: Mobile menu open/closed

**Components**: Header
**API Integration**: None
**Dependencies**: Requires FE-002, FE-023

---

### [FE-025] Create Layout Component
**Goal**: Common page layout wrapper

**Technical Details**:
- File: `src/components/layout/Layout.tsx`
- Structure:
  - `<Header />` at top
  - `<main>` with children (page content)
  - Optional: `<Footer />` (future)
- Props: `children: React.ReactNode`
- Use React Router Outlet for nested routes

**Components**: Layout
**API Integration**: None
**Dependencies**: Requires FE-024

---

## Common Components

### [FE-026] Create LoadingSpinner Component
**Goal**: Reusable loading indicator

**Technical Details**:
- File: `src/components/common/LoadingSpinner.tsx`
- Props:
  - `size?: 'sm' | 'md' | 'lg'` (default: md)
  - `text?: string` (optional loading text)
- Use Lucide Loader2 icon with spin animation

**Components**: LoadingSpinner
**API Integration**: None
**Dependencies**: Requires FE-006

---

### [FE-027] Create ErrorBoundary Component
**Goal**: Catch and display component errors

**Technical Details**:
- File: `src/components/common/ErrorBoundary.tsx`
- Use React class component (ErrorBoundary pattern)
- State: `hasError: boolean`, `error: Error | null`
- Fallback UI: Error message, retry button
- Log errors to console

**Components**: ErrorBoundary
**API Integration**: None
**Dependencies**: Requires FE-008

---

### [FE-028] Create ConfirmDialog Component
**Goal**: Reusable confirmation modal

**Technical Details**:
- File: `src/components/common/ConfirmDialog.tsx`
- Use Shadcn Dialog component
- Props:
  - `open: boolean`
  - `onOpenChange: (open: boolean) => void`
  - `onConfirm: () => void`
  - `title: string`
  - `description: string`
  - `confirmText?: string` (default: "Confirm")
  - `cancelText?: string` (default: "Cancel")

**Components**: ConfirmDialog
**API Integration**: None
**Dependencies**: Requires FE-023

---

## Student Components

### [FE-029] Migrate and Enhance StudentDialog Component
**Goal**: Create/Edit student form dialog

**Technical Details**:
- File: `src/components/students/StudentDialog.tsx`
- Reference: `frontend/reference-code/app/components/StudentDialog.tsx`
- Enhancements:
  - Integrate React Hook Form with Zod validation
  - Define Zod schema for StudentCreateDto
  - Validation rules:
    - firstName, lastName: required, 1-50 chars
    - dateOfBirth: required, age 3-18
    - adhaarNumber: required, 12 digits, unique
    - phone: required, 10 digits, unique
    - email: required, valid email, unique
    - address: required, 10-500 chars
    - guardianName, motherName: required, 1-100 chars
    - identificationMarks: optional, 0-200 chars
  - Async phone validation (call validatePhone service)
  - Handle create vs edit mode
  - Display backend validation errors
  - Submit: call useStudents hook methods
  - Toast notifications on success/error
- Props:
  - `open: boolean`
  - `onOpenChange: (open: boolean) => void`
  - `student?: Student` (for edit mode)
  - `onSubmit: (data: StudentCreateDto | StudentUpdateDto) => Promise<void>`

**Components**: StudentDialog
**API Integration**: Via useStudents hook
**Dependencies**: Requires FE-005, FE-010, FE-016, FE-018, FE-023

---

### [FE-030] Migrate and Enhance ViewStudentDialog Component
**Goal**: Read-only student details dialog

**Technical Details**:
- File: `src/components/students/ViewStudentDialog.tsx`
- Reference: `frontend/reference-code/app/components/ViewStudentDialog.tsx`
- Display all student fields in read-only format
- Formatted dates, status badges
- Props:
  - `open: boolean`
  - `onOpenChange: (open: boolean) => void`
  - `student: Student | null`

**Components**: ViewStudentDialog
**API Integration**: None (display only)
**Dependencies**: Requires FE-010, FE-021, FE-023

---

### [FE-031] Create StudentCard Component
**Goal**: Display student in card format

**Technical Details**:
- File: `src/components/students/StudentCard.tsx`
- Display:
  - Name, student ID
  - Status badge
  - Phone, age, email (icons)
  - Action buttons: View Details, Edit, Delete
- Props:
  - `student: Student`
  - `onView: (student: Student) => void`
  - `onEdit: (student: Student) => void`
  - `onDelete: (student: Student) => void`
- Responsive card design

**Components**: StudentCard
**API Integration**: None (presentation only)
**Dependencies**: Requires FE-010, FE-021, FE-023

---

### [FE-032] Create StudentFilters Component
**Goal**: Search and filter controls

**Technical Details**:
- File: `src/components/students/StudentFilters.tsx`
- Filters:
  - Search input (last name / guardian name) - debounced
  - Status select: All, Active, Inactive
- Props:
  - `onSearchChange: (search: string) => void`
  - `onStatusChange: (status: string) => void`
  - `currentSearch: string`
  - `currentStatus: string`
- Use useDebounce hook for search

**Components**: StudentFilters
**API Integration**: None
**Dependencies**: Requires FE-019, FE-023

---

## Configuration Components

### [FE-033] Migrate and Enhance ConfigurationDialog Component
**Goal**: Create/Edit configuration form dialog

**Technical Details**:
- File: `src/components/configurations/ConfigurationDialog.tsx`
- Reference: `frontend/reference-code/app/components/ConfigurationDialog.tsx`
- Enhancements:
  - React Hook Form + Zod validation
  - Validation rules:
    - category: required, one of ConfigCategory values
    - key: required, 1-100 chars, uppercase/numbers/underscores
    - value: required, 1-1000 chars
    - description: optional, 0-500 chars
  - Handle create vs edit mode
  - Edit mode: category and key readonly
  - Submit: call useConfigurations hook
  - Toast notifications
- Props:
  - `open: boolean`
  - `onOpenChange: (open: boolean) => void`
  - `configuration?: Configuration`
  - `onSubmit: (data: ConfigurationCreateDto | ConfigurationUpdateDto) => Promise<void>`

**Components**: ConfigurationDialog
**API Integration**: Via useConfigurations hook
**Dependencies**: Requires FE-005, FE-011, FE-017, FE-018, FE-023

---

### [FE-034] Migrate and Enhance ConfigurationsTable Component
**Goal**: Display configurations in table format

**Technical Details**:
- File: `src/components/configurations/ConfigurationsTable.tsx`
- Reference: `frontend/reference-code/app/components/ConfigurationsPage.tsx` (extract table)
- Display columns: Category, Key, Value, Description, Last Updated, Actions
- Actions: Edit, Delete buttons
- Props:
  - `configurations: Configuration[]`
  - `onEdit: (config: Configuration) => void`
  - `onDelete: (config: Configuration) => void`
- Use Shadcn Table component

**Components**: ConfigurationsTable
**API Integration**: None (presentation)
**Dependencies**: Requires FE-011, FE-021, FE-023

---

## Page Components

### [FE-035] Migrate and Enhance HomePage Component
**Goal**: Dashboard with statistics

**Technical Details**:
- File: `src/pages/HomePage.tsx`
- Reference: `frontend/reference-code/app/components/HomePage.tsx`
- Enhancements:
  - Integrate useStudents hook to fetch statistics
  - Display cards:
    - Total Students
    - Active Students
    - Inactive Students
  - Loading state with LoadingSpinner
  - Error handling
  - Welcome message
  - Navigation cards to Students and Configurations pages

**Components**: HomePage
**API Integration**: useStudents.statistics
**Dependencies**: Requires FE-016, FE-026

---

### [FE-036] Migrate and Enhance StudentsPage Component
**Goal**: Complete student management interface

**Technical Details**:
- File: `src/pages/StudentsPage.tsx`
- Reference: `frontend/reference-code/app/components/StudentsPage.tsx`
- Enhancements:
  - Replace local state with useStudents hook
  - Integrate StudentFilters component
  - Display students in grid using StudentCard
  - Handle create: open StudentDialog
  - Handle view: open ViewStudentDialog
  - Handle edit: open StudentDialog with student data
  - Handle delete: show ConfirmDialog, call deleteStudent
  - Loading state
  - Error handling
  - Empty state message
  - Display filtered count
- State:
  - Dialog open states
  - Selected student for view/edit
  - Filters (search, status)

**Components**: StudentsPage
**API Integration**: Full CRUD via useStudents
**Dependencies**: Requires FE-016, FE-026, FE-028, FE-029, FE-030, FE-031, FE-032

---

### [FE-037] Migrate and Enhance ConfigurationsPage Component
**Goal**: Configuration management interface

**Technical Details**:
- File: `src/pages/ConfigurationsPage.tsx`
- Reference: `frontend/reference-code/app/components/ConfigurationsPage.tsx`
- Enhancements:
  - Replace local state with useConfigurations hook
  - Category filter dropdown
  - Display ConfigurationsTable
  - Handle create: open ConfigurationDialog
  - Handle edit: open ConfigurationDialog with config data
  - Handle delete: show ConfirmDialog, call deleteConfiguration
  - Loading state
  - Error handling
  - Empty state
- State:
  - Dialog open state
  - Selected configuration for edit
  - Category filter

**Components**: ConfigurationsPage
**API Integration**: Full CRUD via useConfigurations
**Dependencies**: Requires FE-017, FE-026, FE-028, FE-033, FE-034

---

## Routing and App Setup

### [FE-038] Create Routing Configuration
**Goal**: Set up React Router with all pages

**Technical Details**:
- File: `src/App.tsx`
- Import React Router: `BrowserRouter`, `Routes`, `Route`, `Navigate`
- Route structure:
  ```tsx
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="students" element={<StudentsPage />} />
        <Route path="configurations" element={<ConfigurationsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  </BrowserRouter>
  ```
- Wrap with ErrorBoundary

**Components**: App
**API Integration**: None
**Dependencies**: Requires FE-002, FE-025, FE-027, FE-035, FE-036, FE-037

---

### [FE-039] Set up Toast Notifications Provider
**Goal**: Enable global toast notifications

**Technical Details**:
- File: `src/App.tsx` (update)
- Import Toaster from sonner
- Add `<Toaster />` component at root level (inside BrowserRouter)
- Configure position, theme

**Components**: None (provider)
**API Integration**: None
**Dependencies**: Requires FE-006, FE-038

---

### [FE-040] Create Global Styles
**Goal**: Import Tailwind and custom styles

**Technical Details**:
- File: `src/styles/index.css`
- Import reference styles from:
  - `frontend/reference-code/styles/tailwind.css`
  - `frontend/reference-code/styles/theme.css`
- Add Tailwind directives
- Add custom CSS variables for theme
- Import in `src/main.tsx`

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-003, FE-008

---

### [FE-041] Update main.tsx Entry Point
**Goal**: Render App with all providers

**Technical Details**:
- File: `src/main.tsx`
- Import React, ReactDOM, App
- Import global styles
- Render:
  ```tsx
  ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  )
  ```

**Components**: None (entry point)
**API Integration**: None
**Dependencies**: Requires FE-038, FE-040

---

## Testing and Quality Assurance

### [FE-042] Add PropTypes or TypeScript Validation
**Goal**: Ensure all components have proper type checking

**Technical Details**:
- Review all components
- Ensure all props have TypeScript interfaces
- Ensure all API calls have proper type annotations
- No `any` types allowed

**Components**: All components
**API Integration**: None
**Dependencies**: Requires all FE tasks

---

### [FE-043] Test API Integration End-to-End
**Goal**: Verify all API calls work with backend

**Technical Details**:
- Start backend services (Student Service, Configuration Service)
- Start frontend dev server: `npm run dev`
- Manual testing:
  - Create student: verify API call, success toast, list refresh
  - Edit student: verify PATCH call, validation
  - Delete student: verify DELETE call, confirmation
  - Search/filter: verify query params
  - View student details: verify data display
  - Create configuration: verify POST
  - Edit configuration: verify PATCH
  - Delete configuration: verify DELETE
  - Category filter: verify query param
- Check browser DevTools Network tab for API calls
- Verify error handling (disconnect backend, test error messages)

**Components**: All pages
**API Integration**: All endpoints
**Dependencies**: Requires FE-036, FE-037, Backend services running

---

### [FE-044] Responsive Design Testing
**Goal**: Verify mobile and tablet layouts

**Technical Details**:
- Test on different screen sizes:
  - Mobile: 375px, 414px
  - Tablet: 768px, 1024px
  - Desktop: 1280px, 1920px
- Verify:
  - Header mobile menu works
  - Student cards stack properly on mobile
  - Tables scroll horizontally if needed
  - Dialogs fit on small screens
  - Forms are usable on mobile

**Components**: All components
**API Integration**: None
**Dependencies**: Requires all FE tasks

---

### [FE-045] Accessibility Testing
**Goal**: Ensure keyboard navigation and screen reader support

**Technical Details**:
- Test keyboard navigation:
  - Tab through all interactive elements
  - Enter/Space to activate buttons
  - Escape to close dialogs
- Test with screen reader (NVDA/JAWS)
- Verify ARIA labels on buttons, dialogs
- Ensure form validation errors are announced
- Check color contrast (WCAG AA)

**Components**: All components
**API Integration**: None
**Dependencies**: Requires all FE tasks

---

## Build and Deployment

### [FE-046] Configure Production Build
**Goal**: Optimize build for production

**Technical Details**:
- File: `vite.config.ts`
- Configure:
  - Base path if not root
  - Output directory: `dist`
  - Minification: true
  - Source maps: false (production)
  - Chunk splitting for vendors
- Test build: `npm run build`
- Verify `dist/` folder created

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-001

---

### [FE-047] Create Frontend README
**Goal**: Documentation for running and building

**Technical Details**:
- File: `frontend/README.md`
- Include:
  - Prerequisites (Node.js version)
  - Installation: `npm install`
  - Development: `npm run dev`
  - Build: `npm run build`
  - Preview: `npm run preview`
  - Environment variables setup
  - Project structure overview
  - Technology stack
  - Troubleshooting tips

**Components**: None
**API Integration**: None
**Dependencies**: Requires all FE tasks

---

### [FE-048] Create Dockerfile for Frontend
**Goal**: Containerize frontend application

**Technical Details**:
- File: `frontend/Dockerfile`
- Multi-stage build:
  - Stage 1: Build (Node.js, npm build)
  - Stage 2: Serve (nginx:alpine)
- Copy dist to nginx html directory
- Expose port 80
- Create nginx.conf for SPA routing

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-046

---

### [FE-049] Update Docker Compose for Full Stack
**Goal**: Run frontend, backend, and databases together

**Technical Details**:
- File: `docker-compose.yml` (root)
- Add service:
  ```yaml
  frontend:
    build: ./frontend
    ports:
      - "5173:80"
    depends_on:
      - student-service
      - configuration-service
    environment:
      - VITE_API_BASE_URL=http://localhost:8081
      - VITE_CONFIG_API_BASE_URL=http://localhost:8082
  ```

**Components**: None
**API Integration**: None
**Dependencies**: Requires FE-048

---

### [FE-050] Final Integration Testing
**Goal**: End-to-end verification of complete application

**Technical Details**:
- Start all services via Docker Compose
- Test complete user workflows:
  - Navigate to homepage
  - View statistics
  - Register new student
  - Search students
  - Edit student
  - View student details
  - Delete student
  - Manage configurations
  - Test error scenarios
- Verify data persistence across page refreshes
- Verify API error handling
- Check console for errors
- Verify responsive design
- Performance check (Lighthouse audit)

**Components**: Entire application
**API Integration**: All APIs
**Dependencies**: Requires all FE and BE tasks

---

## Summary

**Total Tasks**: 50
**Estimated Effort**: 12-15 developer days (single-pass implementation)

**Execution Order**: Sequential (FE-001 through FE-050)

**Key Deliverables**:
1. Vite + React + TypeScript project
2. Complete UI migrated from reference code
3. Full backend API integration via Service Layer
4. Form validation with React Hook Form + Zod
5. Responsive design (mobile, tablet, desktop)
6. Toast notifications and error handling
7. Dockerized application
8. Production-ready build

**Integration Points**:
- Student Service API: http://localhost:8081
- Configuration Service API: http://localhost:8082

**Next Phase**: QA testing and deployment to production.
