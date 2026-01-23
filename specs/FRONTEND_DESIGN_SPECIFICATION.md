# Frontend Design Specification
**School Management System - Phase 1**

Version: 1.0 | Date: Jan 8, 2026 | Status: Active

---

## Quick Reference

**Tech Stack:** React 18 + TypeScript 5 + Vite 5 + Tailwind 4 + Shadcn/ui  
**Routing:** React Router v6 | **Forms:** React Hook Form v7 | **HTTP:** Axios v1  
**Reference:** `@frontend/reference-code/` (Figma-generated)  
**Screenshots:** `@frontend/screenshots/` (UI validation source)

---

## Core Requirements

### Scope
- Student Management (CRUD + search/filter)
- Configuration Management (CRUD + category filter)
- Dashboard (real-time stats)
- Responsive (desktop + mobile)
- Backend integration (Student + Config microservices)

### Users
- School Administrators (full access)
- Clerical Staff (operations + data entry)

---

## Architecture

### Directory Structure
```
src/
├── components/
│   ├── layout/         # Header, Footer, Layout
│   ├── students/       # Student components
│   ├── configurations/ # Config components
│   ├── common/         # Shared components
│   └── ui/             # Shadcn/ui components
├── pages/              # HomePage, StudentsPage, ConfigurationsPage
├── services/           # api.ts, studentService.ts, configurationService.ts
├── contexts/           # AppContext.tsx
├── hooks/              # useStudents, useConfigurations, useToast
├── types/              # student.ts, configuration.ts, api.ts
├── utils/              # validation, formatting, constants
└── styles/             # Global CSS + Tailwind config
```

### Patterns
- **Components:** Presentation (props) + Container (state/API)
- **Services:** Centralized API layer, type-safe
- **Hooks:** Encapsulated stateful logic
- **Context:** Global state (user, theme, notifications)
- **Error Boundaries:** Graceful error handling

---

## Data Models

### Student
```typescript
interface Student {
  // Identity
  id: string;                    // Backend-generated, e.g., "STU-2025-00001"
  
  // Personal (All required except identificationMarks)
  firstName: string;             // 1-50 chars
  lastName: string;              // 1-50 chars
  dateOfBirth: string;           // ISO 8601, age 3-18 at registration
  age: number;                   // Calculated from DOB
  adhaarNumber: string;          // 12 digits, unique
  identificationMarks?: string;  // 0-200 chars
  address: string;               // 10-500 chars
  
  // Guardian (All required)
  guardianName: string;          // 1-100 chars
  motherName: string;            // 1-100 chars
  
  // Contact (All required, unique)
  phone: string;                 // 10 digits
  email: string;                 // Valid format
  
  // Status
  status: 'ACTIVE' | 'INACTIVE'; // Default: ACTIVE
  
  // Metadata (Backend-managed)
  createdAt: string;
  updatedAt: string;
}
```

**Edit Restrictions:**
- ✅ Editable: `firstName`, `lastName`, `phone`, `status`
- ❌ Immutable: `id`, `dateOfBirth`, `adhaarNumber`, `email`, `createdAt`

### Configuration
```typescript
interface Configuration {
  id: string;
  category: 'GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM';
  key: string;                   // 1-100 chars, unique per category
  value: string;                 // 1-1000 chars
  description?: string;          // 0-500 chars
  dataType: string;              // HIDDEN - Do not display
  lastUpdated: string;
  createdAt: string;
}
```

### API Responses
```typescript
interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: { code: string; message: string; details?: Record<string, string[]> };
  message?: string;
}

interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
}
```

---

## API Endpoints

### Student Service
| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| GET | `/api/students?search={q}&status={s}` | - | `StudentListResponse` |
| GET | `/api/students/{id}` | - | `ApiResponse<Student>` |
| POST | `/api/students` | `Omit<Student, 'id'\|'createdAt'\|'updatedAt'>` | `ApiResponse<Student>` |
| PATCH | `/api/students/{id}` | `{firstName?, lastName?, phone?, status?}` | `ApiResponse<Student>` |
| DELETE | `/api/students/{id}` | - | `ApiResponse<void>` |
| POST | `/api/students/validate-phone` | `{phone, excludeId?}` | `{isUnique: boolean}` |

### Configuration Service
| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| GET | `/api/configurations?category={c}` | - | `ApiResponse<Configuration[]>` |
| GET | `/api/configurations/{id}` | - | `ApiResponse<Configuration>` |
| POST | `/api/configurations` | `Omit<Configuration, 'id'\|'createdAt'\|'lastUpdated'>` | `ApiResponse<Configuration>` |
| PATCH | `/api/configurations/{id}` | `Partial<...>` | `ApiResponse<Configuration>` |
| DELETE | `/api/configurations/{id}` | - | `ApiResponse<void>` |

**Environment:**
```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080
```

---

## Pages & Components

### HomePage
**Screenshot:** `screenshots/homepage.png`

**State:** `{ statistics: {total, active, inactive}, loading, error }`

**UI Checklist:**
- [ ] Welcome banner
- [ ] 3 stat cards: Total Students, Active, System Status
- [ ] 2 quick action cards: Manage Students, Register Student
- [ ] Loading: Skeleton loaders
- [ ] Error: Alert with retry

**Data:** Fetch on mount, auto-refresh every 30s (optional)

---

### StudentsPage
**Screenshot:** `screenshots/students-page.png`

**State:**
```typescript
{
  students: Student[];
  filteredStudents: Student[];
  loading: boolean;
  error: string | null;
  filters: { search: string; status: 'ALL'|'ACTIVE'|'INACTIVE' };
  dialogState: { mode: 'create'|'edit'|'view'|null; student?: Student };
  deleteConfirmation: { isOpen: boolean; studentId?: string };
}
```

**UI Checklist:**
- [ ] Search input (debounced 300ms): "Search by ID, name, or guardian..."
- [ ] Status filter dropdown: All / Active / Inactive
- [ ] "Register New Student" button (top-right, primary)
- [ ] Grid: 1 col (mobile), 2 (tablet), 3 (desktop)
- [ ] Student cards with: ID, Name, Guardian, Phone, Email, Status badge, Actions (View/Edit/Delete)
- [ ] Empty state: "No students found"
- [ ] Pagination if count > 50
- [ ] Loading: Skeleton cards
- [ ] Dialogs: StudentDialog, ViewStudentDialog, Delete confirmation

**Features:**
- Real-time search (backend)
- Optimistic updates with rollback

---

### ConfigurationsPage
**Screenshot:** `screenshots/configurations-page.png`

**State:**
```typescript
{
  configurations: Configuration[];
  loading: boolean;
  categoryFilter: ConfigCategory | 'ALL';
  dialogState: { mode: 'create'|'edit'|null; configuration? };
  deleteConfirmation: { isOpen: boolean; configId?: string };
}
```

**UI Checklist:**
- [ ] Title: "System Configurations"
- [ ] Category filter tabs/dropdown
- [ ] "Add New Configuration" button
- [ ] Table columns: Category (badge), Key, Value, Description, Last Updated, Actions
- [ ] Category colors: GENERAL=Blue, ACADEMIC=Purple, FINANCE=Green, SYSTEM=Gray
- [ ] Empty state: "No configurations found"
- [ ] Loading: Skeleton rows
- [ ] Dialogs: ConfigurationDialog, Delete confirmation

---

### StudentDialog
**Screenshot:** `screenshots/student-dialog-create.png` | `screenshots/student-dialog-edit.png`

**Props:** `{ mode: 'create'|'edit'; student?; isOpen; onClose; onSubmit }`

**Form Sections:**
1. Personal Info: First/Last Name, DOB, Adhaar, Address, ID Marks
2. Guardian Info: Guardian Name, Mother Name
3. Contact: Phone, Email
4. Status: ACTIVE/INACTIVE

**Validation Rules:**
```typescript
{
  firstName: { required, minLength: 1, maxLength: 50, pattern: /^[a-zA-Z\s]+$/ },
  lastName: { required, minLength: 1, maxLength: 50, pattern: /^[a-zA-Z\s]+$/ },
  dateOfBirth: { 
    required, 
    validate: age >= 3 && age <= 18, 
    notFuture 
  },
  adhaarNumber: { required, pattern: /^\d{12}$/ },
  phone: { 
    required, 
    pattern: /^\d{10}$/, 
    validate: async uniqueness 
  },
  email: { required, pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ },
  address: { required, minLength: 10, maxLength: 500 },
  guardianName: { required, maxLength: 100 },
  motherName: { required, maxLength: 100 },
  identificationMarks: { maxLength: 200 },
  status: { required }
}
```

**Edit Mode Restrictions:**
- Disabled fields: `dateOfBirth`, `adhaarNumber`, `email`, `id`
- Editable only: `firstName`, `lastName`, `phone`, `status`

**Behavior:**
- [ ] Async phone validation (skip if unchanged in edit mode)
- [ ] Submit: Validate → API call → Success (close + toast) | Error (show errors)
- [ ] Cancel: Reset + close
- [ ] Loading state on submit button

---

### ViewStudentDialog
**Screenshot:** `screenshots/student-dialog-view.png`

**Props:** `{ student; isOpen; onClose }`

**UI:** Read-only display of all student fields + timestamps, no form inputs

---

### ConfigurationDialog
**Screenshot:** `screenshots/configuration-dialog-edit.png`

**Props:** `{ mode: 'create'|'edit'; configuration?; isOpen; onClose; onSubmit }`

**Fields:** Category (select), Key (text), Value (text), Description (textarea)

**Validation:**
```typescript
{
  category: { required },
  key: { required, maxLength: 100, pattern: /^[A-Z0-9_]+$/ },
  value: { required, maxLength: 1000 },
  description: { maxLength: 500 }
}
```

**Note:** Hide `dataType` field from UI

---

### Header
**Screenshot:** `screenshots/header.png`

**UI:** Logo (left) + Nav links (center): Home, Students, Configurations + Dark mode toggle (right, optional)

**Navigation:** React Router `<Link>` with active highlighting

---

## Validation

### Student Form
- **Client-side:** Immediate feedback, reduce server load
- **Server-side:** Authoritative, uniqueness checks

**Helper:**
```typescript
function calculateAge(dob: string): number {
  const age = today.year - birthDate.year;
  if (monthDiff < 0 || (monthDiff === 0 && today.date < birth.date)) age--;
  return age;
}
```

**Error Display:**
- Field-level: Below input
- General API errors: Alert at top

---

## Styling Guidelines

### Tailwind Setup
```typescript
// tailwind.config.ts
theme: {
  extend: {
    colors: { primary, background, foreground, card, ... },
    borderRadius: { lg: 'var(--radius)', ... }
  }
}
```

### Patterns
- **Spacing:** p-4, p-6, p-8 | gap-4, space-y-4
- **Typography:** text-3xl (h1), text-2xl (h2), text-xl (h3), text-base (body)
- **Colors:** 
  - Primary: `bg-blue-600 hover:bg-blue-700`
  - Destructive: `bg-red-600`
  - Status badges: `bg-green-100 text-green-800` (active)
- **Shadows:** `shadow-sm` (cards), `hover:shadow-md` (interactive)
- **Focus:** `focus:ring-2 focus:ring-blue-500`

### Responsive
```typescript
// Mobile-first breakpoints
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
```

### Accessibility
- [ ] Visible focus indicators
- [ ] WCAG AA contrast (4.5:1)
- [ ] Min touch target 44x44px
- [ ] ARIA labels for icons
- [ ] Semantic HTML

---

## Error Handling

### Strategy
1. **API Errors:** Axios interceptor → Custom ApiError → Toast + field errors
2. **Network Errors:** "Network error. Check connection."
3. **Validation:** React Hook Form errors
4. **Runtime:** Error Boundary → Fallback UI

### Implementation
```typescript
// Service layer
try {
  const res = await apiClient.post('/api/students', data);
  return res.data;
} catch (error) {
  if (axios.isAxiosError(error)) {
    throw new ApiError(error.response?.data.message, error.response?.status);
  }
  throw new ApiError('Unexpected error');
}

// Component
try {
  await studentService.create(data);
  toast.success('Created');
} catch (err) {
  setError(err.message);
  if (err.details) form.setError(field, { message });
  toast.error('Failed');
}
```

### Toast Notifications
- Success: `toast.success('Student created')`
- Error: `toast.error('Failed to save')`
- With action: `toast.error('Failed', { action: { label: 'Retry', onClick } })`

---

## Performance

### Optimizations
- [ ] Code splitting: `lazy(() => import('./pages/StudentsPage'))`
- [ ] Memoization: `memo()`, `useMemo()`, `useCallback()`
- [ ] Debounce search: 300ms
- [ ] Tree-shake: Import specific icons only
- [ ] Lazy load images
- [ ] Bundle analysis: `vite-bundle-visualizer`

### Future: React Query
```typescript
const { data } = useQuery('students', studentService.getAll, {
  staleTime: 5 * 60 * 1000
});
```

---

## Screenshot Validation

### Validation Checklist

**Before code approval, verify each screenshot match:**

#### HomePage (`screenshots/homepage.png`)
- [ ] Welcome banner text matches
- [ ] 3 stat cards present with correct icons
- [ ] Card layout: 1 col mobile, 3 col desktop
- [ ] Quick action cards: exact text + icons
- [ ] Color scheme matches (blues, grays)
- [ ] Spacing consistent with design

#### StudentsPage (`screenshots/students-page.png`)
- [ ] Search input placeholder exact
- [ ] Status filter dropdown options match
- [ ] "Register New Student" button position + style
- [ ] Student card layout: fields order, icon placement
- [ ] Status badge colors (green ACTIVE, gray INACTIVE)
- [ ] Grid responsive breakpoints
- [ ] Action buttons (View/Edit/Delete) with correct icons

#### Student Dialogs
- [ ] `screenshots/student-dialog-create.png`: All fields present, grouped correctly
- [ ] `screenshots/student-dialog-edit.png`: Disabled fields grayed out
- [ ] `screenshots/student-dialog-view.png`: No form inputs, read-only display
- [ ] Form section headers match
- [ ] Submit/Cancel button text + colors

#### ConfigurationsPage (`screenshots/configurations-page.png`)
- [ ] Category filter tabs/dropdown
- [ ] Table columns order exact
- [ ] Category badge colors: Blue/Purple/Green/Gray
- [ ] Action icons match
- [ ] "Add New Configuration" button style

#### Add New Configuration Dialog (`screenshots/configuration-dialog-add.png`)
- [ ] Field labels exact
- [ ] `dataType` field NOT visible
- [ ] Dropdown options match
- [ ] Form layout matches

#### Edit New Configuration Dialog (`screenshots/configuration-dialog-edit.png`)
- [ ] Field labels exact
- [ ] `dataType` field NOT visible
- [ ] Dropdown options match
- [ ] Form layout matches

#### Header (`screenshots/homepage.png`)
- [ ] Logo position + icon
- [ ] Nav links: text, order, spacing
- [ ] Active link highlighting style
- [ ] Dark mode toggle (if visible)

### Validation Process
1. Generate component code
2. Run dev server: `npm run dev`
3. Navigate to page/open dialog
4. Screenshot comparison (side-by-side or overlay)
5. Check:
   - Text content exact match
   - Color values (use DevTools color picker)
   - Spacing (margin/padding via DevTools)
   - Font sizes/weights
   - Icon sizes/types
   - Layout breakpoints
6. Document deviations in `SCREENSHOT_VALIDATION.md`
7. Fix mismatches before PR approval

---

## Implementation Checklist

### P0: Core  - MUST HAVE
- [ ] Project setup (Vite + deps)
- [ ] API layer (axios + services)
- [ ] Data models (Student/Config interfaces)
- [ ] Migrate components from reference code
- [ ] StudentDialog: new fields + validation
- [ ] Backend integration (all CRUD)
- [ ] Loading states + error handling
- [ ] Toast notifications
- [ ] Form validation (client + async)
- [ ] Edit restrictions enforcement
- [ ] Manual testing all operations
- [ ] **Screenshot validation: All pages match**

### P1: Enhanced UX  - SHOULD HAVE
- [ ] Student ID search
- [ ] Backend search integration
- [ ] Pagination (if >50 students)
- [ ] AlertDialog confirmations (replace `window.confirm`)
- [ ] ARIA labels + keyboard nav
- [ ] **Screenshot validation: Dialogs/interactions**

### P2: Nice-to-Have
- [ ] Sortable columns
- [ ] Advanced filters (age/date range)
- [ ] Dark mode functional
- [ ] CSV export

### P3: Quality 
- [ ] Unit tests (Jest)
- [ ] E2E tests (Playwright + visual regression)
- [ ] Bundle optimization
- [ ] Error tracking (Sentry)
- [ ] Storybook docs

---

## Development Workflow

1. **Setup:** `git clone → npm install → cp .env.example .env.development`
2. **Dev:** `npm run dev` (http://localhost:5173)
3. **Lint:** `npm run lint`
4. **Build:** `npm run build`
5. **Preview:** `npm run preview`

### Pre-Commit Checklist
- [ ] No console errors
- [ ] Linter passes
- [ ] Screenshots validated to match 1:1
- [ ] Responsive tested (mobile/tablet/desktop)

---

## Appendix

**Browser Support:** Chrome/Firefox/Edge (latest 2 versions),

**Resources:**
- Figma: https://www.figma.com/make/KUhsQSwQUwAPtVf1Dc9XZh/Review-School-Management-Screens?t=tjhCLUkYsZrYdPg3-1
- Shadcn/ui: https://ui.shadcn.com/
- Lucide Icons: https://lucide.dev/

**Revision:** v1.0 | 2026-01-08 | System

---