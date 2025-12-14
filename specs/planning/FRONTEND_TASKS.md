# Frontend Implementation Tasks - School Management System

## Overview
This document provides a sequential, atomic task list for building the React-based frontend using React 18, TypeScript, Vite, Tailwind CSS, and modern best practices.

**Execution Model:** Single-pass Waterfall implementation (No Sprints)
**Target:** Complete frontend implementation for Phase 1

---

## Project Setup Tasks

### [FE-001] Initialize Vite React TypeScript Project
**Goal:** Create the base React application with TypeScript.

**Technical Details:**
- Run `npm create vite@latest sms-frontend -- --template react-ts`
- Initialize git repository
- Create .gitignore for node_modules, dist, .env files

- **Best Practice**:
  - Use consistent API path patterns that route through the Gateway (e.g., `/api/v1/service-name/**`)
  - Remove any hardcoded service URLs from frontend code

**Dependencies:** None

---

### [FE-002] Install Core Dependencies
**Goal:** Add essential libraries and frameworks.

**Technical Details:**
- Install dependencies:
  - React Router DOM 6.20.1
  - React Query (TanStack Query) 4.x
  - Axios 1.6.5
  - React Hook Form 7.x
  - Zod 3.x for validation
  - @hookform/resolvers
  - UUID for correlation IDs
- Install dev dependencies:
  - TypeScript 5.x
  - Vite types
  - ESLint and Prettier

**Dependencies:** FE-001

---

### [FE-003] Install and Configure Tailwind CSS
**Goal:** Set up utility-first CSS framework.

**Technical Details:**
- Install: tailwindcss, postcss, autoprefixer
- Install @tailwindcss/forms plugin
- Run `npx tailwindcss init -p`
- Configure tailwind.config.js with content paths
- Create src/styles/global.css with Tailwind directives
- Import global.css in main.tsx

**Dependencies:** FE-001

---

### [FE-004] Configure Environment Variables
**Goal:** Set up environment-specific configuration.

**Technical Details:**
- Create .env.development:
  - VITE_API_BASE_URL=http://localhost:8081/api/v1
  - VITE_APP_NAME=School Management System
  - VITE_APP_VERSION=1.0.0
- Create .env.production with production API URL
- Create src/config/env.ts to export typed env variables

**Dependencies:** FE-001

---

### [FE-005] Set Up Project Structure
**Goal:** Create organized directory structure.

**Technical Details:**
- Create directories:
  - src/components/common
  - src/components/layout
  - src/features/student/components
  - src/features/student/hooks
  - src/features/student/types
  - src/features/configuration/components
  - src/features/configuration/hooks
  - src/features/configuration/types
  - src/pages
  - src/services/api
  - src/hooks
  - src/contexts
  - src/schemas
  - src/types
  - src/utils
  - src/router

**Dependencies:** FE-001

---

## API Integration Layer

### [FE-006] Create Axios Client
**Goal:** Set up centralized HTTP client with interceptors.

**Technical Details:**
- Create src/services/api/client.ts
- Configure axios instance:
  - baseURL from environment
  - timeout: 10000ms
  - headers: Content-Type, Accept application/json
- Add request interceptor:
  - Generate and add X-Correlation-ID header
  - Log request method and URL
- Add response interceptor:
  - Log response status
  - Handle errors globally
  - Extract RFC 7807 Problem Details
- Export ProblemDetails TypeScript interface

**Dependencies:** FE-002, FE-004

---

### [FE-007] Create TypeScript Types
**Goal:** Define type-safe interfaces for API data.

**Technical Details:**
- Create src/types/student.types.ts with interfaces:
  - Student (complete student object)
  - CreateStudentRequest
  - UpdateStudentRequest
  - UpdateStatusRequest
  - StudentPageResponse
  - StudentSearchParams
- Create src/types/config.types.ts with interfaces:
  - Configuration
  - CreateConfigRequest
  - UpdateConfigRequest
  - ConfigurationPageResponse
- Create src/types/api.types.ts with common types:
  - PageableResponse
  - ApiError

**Dependencies:** FE-001

---

### [FE-008] Create Student Service
**Goal:** Implement API service for student operations.

**Technical Details:**
- Create src/services/api/studentService.ts
- Implement methods:
  - createStudent(data: CreateStudentRequest): Promise<Student>
  - getStudentById(id: number): Promise<Student>
  - getStudentByStudentId(studentId: string): Promise<Student>
  - searchStudents(params: StudentSearchParams): Promise<StudentPageResponse>
  - updateStudent(id: number, data: UpdateStudentRequest): Promise<Student>
  - updateStatus(id: number, data: UpdateStatusRequest): Promise<Student>
  - deleteStudent(id: number): Promise<void>
- Use axios client for all requests
- Add proper error handling

**Dependencies:** FE-006, FE-007

---

### [FE-009] Create Configuration Service
**Goal:** Implement API service for configuration operations.

**Technical Details:**
- Create src/services/api/configService.ts
- Implement methods:
  - createConfiguration(data: CreateConfigRequest): Promise<Configuration>
  - getAllConfigurations(page: number, size: number): Promise<ConfigurationPageResponse>
  - getConfigurationsByCategory(category: string): Promise<Configuration[]>
  - updateConfiguration(id: number, data: UpdateConfigRequest): Promise<Configuration>
  - deleteConfiguration(id: number): Promise<void>

**Dependencies:** FE-006, FE-007

---

## State Management Setup

### [FE-010] Configure React Query
**Goal:** Set up server state management.

**Technical Details:**
- Create src/services/queryClient.ts
- Create QueryClient with default options:
  - staleTime: 5 minutes
  - cacheTime: 10 minutes
  - retry: 1
  - refetchOnWindowFocus: false
- Wrap App in QueryClientProvider in main.tsx
- Add React Query Devtools for development

**Dependencies:** FE-002

---

### [FE-011] Create Theme Context
**Goal:** Implement light/dark theme switching.

**Technical Details:**
- Create src/contexts/ThemeContext.tsx
- Implement ThemeProvider with useState for theme state
- Provide toggleTheme function
- Create useTheme hook
- Apply theme class to root element

**Dependencies:** FE-001

---

### [FE-012] Create Notification Context
**Goal:** Implement toast notification system.

**Technical Details:**
- Create src/contexts/NotificationContext.tsx
- Implement NotificationProvider with notification state
- Provide methods:
  - addNotification(type, message)
  - removeNotification(id)
- Auto-remove notifications after 5 seconds
- Create useNotification hook

**Dependencies:** FE-001

---

## Validation and Schemas

### [FE-013] Create Student Validation Schema
**Goal:** Define Zod schemas for student forms.

**Technical Details:**
- Create src/schemas/studentSchema.ts
- Define studentSchema with validation rules:
  - firstName: string, min 1, max 100
  - lastName: string, min 1, max 100
  - address: string, min 10, max 500
  - mobile: regex pattern for 10-15 digits
  - dateOfBirth: custom refine for age 3-18
  - fatherName: required, max 100
  - motherName: optional, max 100
  - email: optional email format
  - aadhaarNumber: optional, exactly 12 digits
- Export StudentFormData type
- Create updateStudentSchema (subset of fields)

**Dependencies:** FE-002

---

### [FE-014] Create Configuration Validation Schema
**Goal:** Define Zod schemas for configuration forms.

**Technical Details:**
- Create src/schemas/configSchema.ts
- Define configSchema with validation:
  - category: enum (General, Academic, Financial, System)
  - configKey: string, min 1, max 100
  - configValue: string, min 1
  - description: optional, max 500
- Export ConfigFormData type

**Dependencies:** FE-002

---

## Common Components

### [FE-015] Create Button Component
**Goal:** Build reusable styled button.

**Technical Details:**
- Create src/components/common/Button.tsx
- Props: variant (primary, secondary, danger), size, disabled, onClick
- Use Tailwind classes for styling
- Support loading state with spinner

**Dependencies:** FE-003

---

### [FE-016] Create Input Component
**Goal:** Build reusable form input with validation.

**Technical Details:**
- Create src/components/common/Input.tsx
- Props: label, error, type, required, multiline, rows
- Use forwardRef for React Hook Form compatibility
- Display error message below input
- Support textarea variant

**Dependencies:** FE-003

---

### [FE-017] Create Select Component
**Goal:** Build reusable dropdown select.

**Technical Details:**
- Create src/components/common/Select.tsx
- Props: label, options, error, required, placeholder
- Use forwardRef for form integration
- Display error message

**Dependencies:** FE-003

---

### [FE-018] Create Modal Component
**Goal:** Build reusable modal dialog.

**Technical Details:**
- Create src/components/common/Modal.tsx
- Props: isOpen, onClose, title, children, size
- Use React Portal for rendering
- Add backdrop click to close
- Add ESC key to close
- Trap focus within modal

**Dependencies:** FE-003

---

### [FE-019] Create Table Component
**Goal:** Build reusable data table.

**Technical Details:**
- Create src/components/common/Table.tsx
- Props: columns (array of column definitions), data, loading, emptyMessage
- Support column headers with sorting indicators
- Responsive design (horizontal scroll on mobile)
- Striped rows for readability

**Dependencies:** FE-003

---

### [FE-020] Create Pagination Component
**Goal:** Build reusable pagination controls.

**Technical Details:**
- Create src/components/common/Pagination.tsx
- Props: currentPage, totalPages, onPageChange
- Show page numbers with ellipsis for large page counts
- Add Previous and Next buttons
- Disable buttons at boundaries
- Highlight current page

**Dependencies:** FE-003

---

### [FE-021] Create Loading Spinner Component
**Goal:** Build loading indicator.

**Technical Details:**
- Create src/components/common/LoadingSpinner.tsx
- Props: size, message
- Animated spinner using Tailwind animations
- Center on screen by default
- Support inline variant

**Dependencies:** FE-003

---

### [FE-022] Create Error Boundary Component
**Goal:** Implement error boundary for error handling.

**Technical Details:**
- Create src/components/common/ErrorBoundary.tsx
- Extend React.Component with error state
- Implement getDerivedStateFromError
- Implement componentDidCatch for logging
- Render fallback UI with error message
- Add "Reload Page" button

**Dependencies:** FE-001

---

### [FE-023] Create Confirm Dialog Component
**Goal:** Build confirmation modal.

**Technical Details:**
- Create src/components/common/ConfirmDialog.tsx
- Props: isOpen, title, message, onConfirm, onCancel, confirmText, cancelText
- Use Modal component as base
- Highlight confirm button based on action type (danger for delete)

**Dependencies:** FE-018

---

## Layout Components

### [FE-024] Create Header Component
**Goal:** Build application header with navigation.

**Technical Details:**
- Create src/components/layout/Header.tsx
- Display app name and logo
- Navigation links: Home, Students, Configurations
- Theme toggle button
- Responsive mobile menu

**Dependencies:** FE-003, FE-011

---

### [FE-025] Create Sidebar Component
**Goal:** Build sidebar navigation (optional for Phase 1).

**Technical Details:**
- Create src/components/layout/Sidebar.tsx
- List navigation items
- Active route highlighting
- Collapsible on mobile
- Future: User profile section

**Dependencies:** FE-003

---

### [FE-026] Create Footer Component
**Goal:** Build application footer.

**Technical Details:**
- Create src/components/layout/Footer.tsx
- Display copyright
- Display app version from env
- Links to documentation (placeholder)

**Dependencies:** FE-003, FE-004

---

### [FE-027] Create Main Layout Component
**Goal:** Build main application layout structure.

**Technical Details:**
- Create src/components/layout/MainLayout.tsx
- Compose Header, Sidebar (optional), Footer
- Render children in main content area
- Use React Router Outlet for nested routes
- Responsive grid layout

**Dependencies:** FE-024, FE-026

---

## Student Feature - React Query Hooks

### [FE-028] Create useStudents Hook
**Goal:** Implement hook for fetching students list.

**Technical Details:**
- Create src/features/student/hooks/useStudents.ts
- Use useQuery with queryKey: ['students', params]
- Call studentService.searchStudents
- Enable keepPreviousData for pagination
- Return query result with data, isLoading, isError, error

**Dependencies:** FE-008, FE-010

---

### [FE-029] Create useStudent Hook
**Goal:** Implement hook for fetching single student.

**Technical Details:**
- Create src/features/student/hooks/useStudent.ts
- Use useQuery with queryKey: ['student', id]
- Call studentService.getStudentById
- Enable caching with 5-minute stale time

**Dependencies:** FE-008, FE-010

---

### [FE-030] Create useCreateStudent Hook
**Goal:** Implement hook for creating student.

**Technical Details:**
- Create src/features/student/hooks/useCreateStudent.ts
- Use useMutation with mutationFn: studentService.createStudent
- onSuccess:
  - Invalidate ['students'] query
  - Show success notification
  - Navigate to student list or detail
- onError:
  - Extract error message from Problem Details
  - Show error notification

**Dependencies:** FE-008, FE-010, FE-012

---

### [FE-031] Create useUpdateStudent Hook
**Goal:** Implement hook for updating student.

**Technical Details:**
- Create src/features/student/hooks/useUpdateStudent.ts
- Use useMutation with mutationFn: studentService.updateStudent
- onSuccess:
  - Invalidate ['students'] and ['student', id] queries
  - Show success notification
- onError: Show error notification

**Dependencies:** FE-008, FE-010, FE-012

---

### [FE-032] Create useDeleteStudent Hook
**Goal:** Implement hook for deleting student.

**Technical Details:**
- Create src/features/student/hooks/useDeleteStudent.ts
- Use useMutation with mutationFn: studentService.deleteStudent
- onSuccess:
  - Invalidate ['students'] query
  - Show success notification
  - Navigate to student list
- onError: Show error notification

**Dependencies:** FE-008, FE-010, FE-012

---

## Student Feature - Components

### [FE-033] Create Status Badge Component
**Goal:** Build colored badge for student status.

**Technical Details:**
- Create src/features/student/components/StatusBadge.tsx
- Props: status (Active, Inactive, Graduated, Transferred)
- Color mapping:
  - Active: green
  - Inactive: gray
  - Graduated: blue
  - Transferred: yellow
- Use Tailwind badge styles

**Dependencies:** FE-003

---

### [FE-034] Create Student Card Component
**Goal:** Build card for displaying student summary.

**Technical Details:**
- Create src/features/student/components/StudentCard.tsx
- Props: student object
- Display: studentId, name, mobile, status badge, age
- Add View Details button
- Add Edit and Delete action buttons
- Use Tailwind card styles with hover effects

**Dependencies:** FE-003, FE-033

---

### [FE-035] Create Search Bar Component
**Goal:** Build search input with filters.

**Technical Details:**
- Create src/features/student/components/SearchBar.tsx
- Props: onSearch function
- Input fields:
  - Last name search (text input)
  - Guardian name search (text input)
  - Status filter (select dropdown)
- Use debounced search (500ms delay)
- Clear filters button

**Dependencies:** FE-016, FE-017

---

### [FE-036] Create Student Form Component
**Goal:** Build reusable form for create/edit.

**Technical Details:**
- Create src/features/student/components/StudentForm.tsx
- Props: initialData (optional), onSubmit, isLoading
- Use React Hook Form with Zod resolver
- Group fields into sections:
  - Personal Information
  - Guardian Information
- Display validation errors inline
- Cancel and Save buttons

**Dependencies:** FE-013, FE-016

---

### [FE-037] Create Student Detail Component
**Goal:** Build detailed view of student information.

**Technical Details:**
- Create src/features/student/components/StudentDetail.tsx
- Props: student object
- Display all student fields in read-only format
- Organized sections with labels
- Action buttons: Edit, Change Status, Delete
- Show created/updated timestamps

**Dependencies:** FE-003, FE-033

---

### [FE-038] Create Student List Component
**Goal:** Build main student listing with search and pagination.

**Technical Details:**
- Create src/features/student/components/StudentList.tsx
- Use useStudents hook with search params state
- Render SearchBar component
- Render grid of StudentCard components
- Render Pagination component
- Handle loading and error states
- Add "Register New Student" button

**Dependencies:** FE-028, FE-034, FE-035, FE-020

---

## Configuration Feature - Hooks and Components

### [FE-039] Create useConfigurations Hook
**Goal:** Implement hook for fetching configurations.

**Technical Details:**
- Create src/features/configuration/hooks/useConfigurations.ts
- Use useQuery with queryKey: ['configurations', category]
- Call configService.getConfigurationsByCategory
- Cache for 1 hour (configs change infrequently)

**Dependencies:** FE-009, FE-010

---

### [FE-040] Create useCreateConfiguration Hook
**Goal:** Implement hook for creating configuration.

**Technical Details:**
- Create src/features/configuration/hooks/useCreateConfiguration.ts
- Use useMutation
- Invalidate configurations query on success
- Show notifications

**Dependencies:** FE-009, FE-010, FE-012

---

### [FE-041] Create useUpdateConfiguration Hook
**Goal:** Implement hook for updating configuration.

**Technical Details:**
- Create src/features/configuration/hooks/useUpdateConfiguration.ts
- Use useMutation
- Invalidate configurations query on success

**Dependencies:** FE-009, FE-010, FE-012

---

### [FE-042] Create useDeleteConfiguration Hook
**Goal:** Implement hook for deleting configuration.

**Technical Details:**
- Create src/features/configuration/hooks/useDeleteConfiguration.ts
- Use useMutation
- Invalidate configurations query on success

**Dependencies:** FE-009, FE-010, FE-012

---

### [FE-043] Create Configuration Form Component
**Goal:** Build form for create/edit configuration.

**Technical Details:**
- Create src/features/configuration/components/ConfigForm.tsx
- Use React Hook Form with Zod resolver
- Fields: category (select), configKey, configValue, description
- Display validation errors

**Dependencies:** FE-014, FE-016, FE-017

---

### [FE-044] Create Configuration List Component
**Goal:** Build configuration listing grouped by category.

**Technical Details:**
- Create src/features/configuration/components/ConfigList.tsx
- Use useConfigurations hook
- Display configurations in table format
- Group by category with expandable sections
- Show Edit and Delete buttons per config
- Add "Add New Configuration" button

**Dependencies:** FE-039, FE-019

---

## Page Components

### [FE-045] Create Home Page
**Goal:** Build landing page with dashboard.

**Technical Details:**
- Create src/pages/HomePage.tsx
- Display welcome message
- Show summary statistics:
  - Total students
  - Active students
  - Recent registrations
- Quick action buttons to Students and Configurations
- Future: Charts and graphs

**Dependencies:** FE-027

---

### [FE-046] Create Student List Page
**Goal:** Build page for browsing students.

**Technical Details:**
- Create src/pages/StudentListPage.tsx
- Render StudentList component
- Set page title
- Add breadcrumbs navigation

**Dependencies:** FE-038

---

### [FE-047] Create Student Create Page
**Goal:** Build page for registering new student.

**Technical Details:**
- Create src/pages/StudentCreatePage.tsx
- Render StudentForm component
- Use useCreateStudent hook
- Handle form submission
- Navigate to student detail on success
- Set page title: "Register New Student"

**Dependencies:** FE-030, FE-036

---

### [FE-048] Create Student Detail Page
**Goal:** Build page for viewing student details.

**Technical Details:**
- Create src/pages/StudentDetailPage.tsx
- Get student ID from URL params
- Use useStudent hook to fetch data
- Render StudentDetail component
- Handle loading and not found states
- Breadcrumbs: Home > Students > [Student Name]

**Dependencies:** FE-029, FE-037

---

### [FE-049] Create Student Edit Page
**Goal:** Build page for editing student.

**Technical Details:**
- Create src/pages/StudentEditPage.tsx
- Get student ID from URL params
- Use useStudent hook to fetch current data
- Render StudentForm with initialData
- Use useUpdateStudent hook
- Navigate to student detail on success
- Set page title: "Edit Student"

**Dependencies:** FE-029, FE-031, FE-036

---

### [FE-050] Create Configuration Page
**Goal:** Build page for managing configurations.

**Technical Details:**
- Create src/pages/ConfigurationPage.tsx
- Render ConfigList component
- Add modal for create/edit config
- Use configuration hooks for CRUD operations
- Set page title: "School Configurations"

**Dependencies:** FE-040, FE-041, FE-042, FE-044

---

### [FE-051] Create Not Found Page
**Goal:** Build 404 error page.

**Technical Details:**
- Create src/pages/NotFoundPage.tsx
- Display "Page Not Found" message
- Add "Go Home" button
- Use friendly illustration or icon

**Dependencies:** FE-015

---

## Routing Configuration

### [FE-052] Configure React Router
**Goal:** Set up application routing.

**Technical Details:**
- Create src/router/index.tsx
- Use createBrowserRouter with routes:
  - / -> HomePage
  - /students -> StudentListPage
  - /students/create -> StudentCreatePage
  - /students/:id -> StudentDetailPage
  - /students/:id/edit -> StudentEditPage
  - /configurations -> ConfigurationPage
  - * -> NotFoundPage
- Wrap all routes in MainLayout
- Export router

**Dependencies:** FE-045 to FE-051

---

### [FE-053] Integrate Router in App
**Goal:** Connect router to application.

**Technical Details:**
- Update src/App.tsx
- Import RouterProvider from react-router-dom
- Import router from src/router
- Render RouterProvider with router prop
- Wrap in ErrorBoundary

**Dependencies:** FE-052, FE-022

---

## Custom Hooks and Utilities

### [FE-054] Create useDebounce Hook
**Goal:** Implement debouncing for search inputs.

**Technical Details:**
- Create src/hooks/useDebounce.ts
- Generic hook that delays value updates
- Default delay: 500ms
- Return debounced value

**Dependencies:** FE-001

---

### [FE-055] Create usePagination Hook
**Goal:** Implement pagination state management.

**Technical Details:**
- Create src/hooks/usePagination.ts
- Manage current page, page size state
- Provide functions: goToPage, nextPage, prevPage
- Return pagination state and controls

**Dependencies:** FE-001

---

### [FE-056] Create Date Utility Functions
**Goal:** Implement date formatting and calculation utilities.

**Technical Details:**
- Create src/utils/dateUtils.ts
- Functions:
  - formatDate(date: string, format: string): string
  - calculateAge(dateOfBirth: string): number
  - isValidDateOfBirth(date: string): boolean
  - toISODate(date: Date): string

**Dependencies:** FE-001

---

### [FE-057] Create Format Utility Functions
**Goal:** Implement data formatting utilities.

**Technical Details:**
- Create src/utils/formatters.ts
- Functions:
  - formatPhoneNumber(phone: string): string
  - formatStudentId(id: string): string
  - capitalize(text: string): string
  - truncate(text: string, maxLength: number): string

**Dependencies:** FE-001

---

### [FE-058] Create Constants File
**Goal:** Define application constants.

**Technical Details:**
- Create src/utils/constants.ts
- Export constants:
  - STATUS_OPTIONS array
  - CATEGORY_OPTIONS array
  - DATE_FORMAT strings
  - API_TIMEOUT
  - CACHE_DURATIONS

**Dependencies:** FE-001

---

## Styling and Theming

### [FE-059] Configure Tailwind Theme
**Goal:** Customize Tailwind theme colors and spacing.

**Technical Details:**
- Update tailwind.config.js
- Extend theme with custom colors:
  - primary (blue shades)
  - secondary (gray shades)
  - success (green)
  - danger (red)
  - warning (yellow)
- Add custom spacing values if needed

**Dependencies:** FE-003

---

### [FE-060] Create Global Styles
**Goal:** Add global CSS rules.

**Technical Details:**
- Update src/styles/global.css
- Add base styles for body, headings, links
- Add utility classes for common patterns
- Add custom animations (fade, slide)
- Ensure responsive typography

**Dependencies:** FE-003

---

## Testing Setup (Optional but Recommended)

### [FE-061] Configure Vitest
**Goal:** Set up unit testing framework.

**Technical Details:**
- Install vitest, @testing-library/react, @testing-library/user-event
- Create vitest.config.ts
- Configure test environment: jsdom
- Add test script to package.json

**Dependencies:** FE-001

---

### [FE-062] Write Component Tests
**Goal:** Test common components.

**Technical Details:**
- Create tests for:
  - Button component (click, disabled states)
  - Input component (value changes, errors)
  - Modal component (open/close)
- Use React Testing Library
- Target: 70%+ coverage for components

**Dependencies:** FE-061

---

### [FE-063] Write Hook Tests
**Goal:** Test custom hooks.

**Technical Details:**
- Test useDebounce hook
- Test usePagination hook
- Use @testing-library/react-hooks
- Verify state updates and side effects

**Dependencies:** FE-061

---

### [FE-064] Set Up Mock Service Worker (MSW)
**Goal:** Mock API calls for testing and development.

**Technical Details:**
- Install msw
- Create src/mocks/handlers.ts with API mocks
- Create src/mocks/browser.ts for browser setup
- Create src/mocks/server.ts for test setup
- Mock student and config endpoints
- Return realistic fake data

**Dependencies:** FE-061

---

## Build and Deployment

### [FE-065] Configure Production Build
**Goal:** Optimize build for production.

**Technical Details:**
- Update vite.config.ts:
  - Configure build output directory
  - Enable minification
  - Configure chunk splitting
  - Set base URL for deployment
- Test production build: `npm run build`
- Test preview: `npm run preview`

**Dependencies:** FE-001

---

### [FE-066] Add Build Scripts
**Goal:** Create npm scripts for common tasks.

**Technical Details:**
- Update package.json scripts:
  - dev: Start dev server
  - build: Production build
  - preview: Preview production build
  - lint: Run ESLint
  - format: Run Prettier
  - test: Run Vitest
  - test:coverage: Coverage report

**Dependencies:** FE-001

---

### [FE-067] Configure ESLint and Prettier
**Goal:** Set up code quality tools.

**Technical Details:**
- Configure .eslintrc.json:
  - Extend recommended React and TypeScript rules
  - Add custom rules for consistency
- Configure .prettierrc.json:
  - Set code formatting preferences
  - Semi-colons, quotes, trailing commas, etc.
- Add pre-commit hooks with Husky (optional)

**Dependencies:** FE-002

---

### [FE-068] Optimize Performance
**Goal:** Implement performance optimizations.

**Technical Details:**
- Add React.memo to StudentCard and other list items
- Use useMemo for expensive calculations
- Implement lazy loading for pages (React.lazy)
- Add Suspense boundaries with fallbacks
- Optimize images (use WebP, proper sizes)
- Enable Vite code splitting

**Dependencies:** FE-053

---

### [FE-069] Add Loading and Error States
**Goal:** Improve UX with proper state handling.

**Technical Details:**
- Review all pages and components
- Ensure loading spinners display during data fetching
- Ensure error messages display on failures
- Add empty states for lists with no data
- Add retry buttons on error states

**Dependencies:** FE-021

---

### [FE-070] Create README for Frontend
**Goal:** Document setup and development.

**Technical Details:**
- Create sms-frontend/README.md with:
  - Prerequisites (Node.js 18+, npm)
  - Installation instructions
  - Development server startup
  - Build instructions
  - Environment variables
  - Project structure overview
  - Available scripts
  - Troubleshooting guide

**Dependencies:** FE-065

---

## Summary

Total Frontend Tasks: 70
- Project Setup: 5 tasks (FE-001 to FE-005)
- API Integration: 5 tasks (FE-006 to FE-010)
- State Management: 3 tasks (FE-010 to FE-012)
- Validation: 2 tasks (FE-013 to FE-014)
- Common Components: 9 tasks (FE-015 to FE-023)
- Layout Components: 4 tasks (FE-024 to FE-027)
- Student Hooks: 5 tasks (FE-028 to FE-032)
- Student Components: 6 tasks (FE-033 to FE-038)
- Configuration: 6 tasks (FE-039 to FE-044)
- Pages: 7 tasks (FE-045 to FE-051)
- Routing: 2 tasks (FE-052 to FE-053)
- Utilities: 5 tasks (FE-054 to FE-058)
- Styling: 2 tasks (FE-059 to FE-060)
- Testing: 4 tasks (FE-061 to FE-064)
- Build: 6 tasks (FE-065 to FE-070)

**Estimated Effort:** 3-5 weeks for experienced React developer

**Key Technologies:**
- React 18, TypeScript 5
- Vite 7.1.12, Tailwind CSS 3.4.1
- React Router 6.20.1
- React Query (TanStack Query) 4.x
- React Hook Form 7.x, Zod 3.x
- Axios 1.6.5
- Vitest (for testing)

**Key Features:**
- Type-safe API integration
- Form validation with Zod
- Server state management with React Query
- Responsive design with Tailwind
- Theme switching support
- Toast notifications
- Error boundaries
- Debounced search
- Pagination
- Loading and error states
