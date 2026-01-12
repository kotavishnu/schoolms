# Frontend Design Specification
**School Management System - Phase 1**

**Version**: 1.0  
**Date**: January 8, 2026  
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Data Models](#data-models)
5. [API Integration](#api-integration)
6. [Component Specifications](#component-specifications)
7. [Routing & Navigation](#routing--navigation)
8. [State Management](#state-management)
9. [Form Validation](#form-validation)
10. [Styling Guidelines](#styling-guidelines)
11. [Error Handling](#error-handling)
12. [Performance & Optimization](#performance--optimization)
13. [Gap Analysis](#gap-analysis)
14. [Implementation Priorities](#implementation-priorities)

---

## Overview

### Purpose
This document provides comprehensive design specifications for the School Management System frontend application. It bridges the gap between the Figma-generated reference code and production-ready implementation with full backend integration.

### Scope
- Student Management interface with complete CRUD operations
- Configuration Management for school settings
- Dashboard with real-time statistics
- Responsive web application for desktop and mobile devices
- Integration with backend microservices (Student Service, Configuration Service)

### Target Users
- **School Administrators**: Full administrative privileges
- **Clerical Staff**: Day-to-day operations and data entry

### Reference Code
The design is based on Figma-generated React code located in `frontend/reference-code/`. This reference provides the visual design, component structure, and UX patterns to be enhanced with backend integration.

---

## Technology Stack

### Core Framework
- **React**: 18+ (Function components with Hooks)
- **TypeScript**: 5.x (Strict mode enabled)
- **Build Tool**: Vite 5.x (Fast development, optimized builds)
- **Package Manager**: npm or pnpm

### Routing
- **React Router**: v6 (Client-side routing, SPA architecture)

### UI Framework
- **Tailwind CSS**: v4 (Utility-first styling)
- **Shadcn/ui**: Component library built on Radix UI primitives
  - Accessible, customizable components
  - Pre-styled with Tailwind
  - Headless UI architecture

### Form Management
- **React Hook Form**: v7 (Performance, validation, error handling)

### HTTP Client
- **Axios**: v1 (API communication, interceptors, error handling)

### Icons
- **Lucide React**: Modern, consistent icon set

### Notifications
- **Sonner**: Toast notification system

### Development Tools
- **ESLint**: Code linting
- **Prettier**: Code formatting
- **TypeScript ESLint**: TypeScript-specific linting

---

## Architecture

### Application Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── layout/         # Header, Footer, Layout
│   │   ├── students/       # Student-specific components
│   │   ├── configurations/ # Configuration components
│   │   ├── common/         # Shared components
│   │   └── ui/             # Shadcn/ui components
│   ├── pages/              # Page-level components
│   │   ├── HomePage.tsx
│   │   ├── StudentsPage.tsx
│   │   └── ConfigurationsPage.tsx
│   ├── services/           # API integration layer
│   │   ├── api.ts          # HTTP client setup
│   │   ├── studentService.ts
│   │   └── configurationService.ts
│   ├── contexts/           # React Context providers
│   │   └── AppContext.tsx
│   ├── hooks/              # Custom React hooks
│   │   ├── useStudents.ts
│   │   ├── useConfigurations.ts
│   │   └── useToast.ts
│   ├── types/              # TypeScript type definitions
│   │   ├── student.ts
│   │   ├── configuration.ts
│   │   └── api.ts
│   ├── utils/              # Helper functions
│   │   ├── validation.ts
│   │   ├── formatting.ts
│   │   └── constants.ts
│   ├── styles/             # Global styles
│   │   ├── index.css
│   │   ├── tailwind.css
│   │   └── theme.css
│   ├── App.tsx             # Root component
│   └── main.tsx            # Entry point
├── public/                 # Static assets
├── .env.development        # Development environment variables
├── .env.production         # Production environment variables
├── vite.config.ts          # Vite configuration
├── tailwind.config.ts      # Tailwind configuration
├── tsconfig.json           # TypeScript configuration
└── package.json            # Dependencies
```

### Architectural Patterns

#### 1. Component-Based Architecture
- **Presentation Components**: Pure UI, receive props, emit events
- **Container Components**: Manage state, API calls, business logic
- **Compound Components**: Complex components with sub-components (dialogs, forms)

#### 2. Service Layer Pattern
- Centralized API communication in service modules
- Abstraction from HTTP implementation details
- Reusable service methods across components
- Type-safe request/response handling

#### 3. Custom Hooks Pattern
- Encapsulate stateful logic (data fetching, form handling)
- Reusable across components
- Separation of concerns (UI vs logic)

#### 4. Context for Global State
- Application-level state (user info, theme, notifications)
- Avoid prop drilling
- Lightweight alternative to Redux for current scope

#### 5. Error Boundary Pattern
- Graceful error handling
- Component-level and global error boundaries
- Fallback UI for failures

---

## Data Models

### Student Interface

```typescript
interface Student {
  // Identification
  id: string;                    // Backend-generated, e.g., "STU-2025-00001"
  
  // Personal Information
  firstName: string;             // Required, 1-50 characters
  lastName: string;              // Required, 1-50 characters
  dateOfBirth: string;           // Required, ISO 8601 format (YYYY-MM-DD)
  age: number;                   // Calculated from DOB, 3-18 at registration
  adhaarNumber: string;          // Required, 12 digits, unique
  identificationMarks: string;   // Optional, 0-200 characters
  address: string;               // Required, 10-500 characters
  
  // Guardian Information
  guardianName: string;          // Required (Father/Guardian), 1-100 characters
  motherName: string;            // Required, 1-100 characters
  
  // Contact Information
  phone: string;                 // Required, unique, 10 digits
  email: string;                 // Required, valid email format, unique
  
  // Status
  status: 'ACTIVE' | 'INACTIVE'; // Required, default: ACTIVE
  
  // Metadata
  createdAt: string;             // Backend-managed, ISO 8601 timestamp
  updatedAt: string;             // Backend-managed, ISO 8601 timestamp
}
```

**Field Constraints**:
- **Editable Fields** (after creation): `firstName`, `lastName`, `phone`, `status` only
- **Immutable Fields**: `id`, `dateOfBirth`, `adhaarNumber`, `email`, `createdAt`
- **Auto-calculated**: `age` (from `dateOfBirth`), `updatedAt` (on backend)

### Configuration Interface

```typescript
interface Configuration {
  // Identification
  id: string;                    // Backend-generated UUID or auto-increment
  
  // Configuration Data
  category: ConfigCategory;      // Required, one of predefined categories
  key: string;                   // Required, unique within category, 1-100 characters
  value: string;                 // Required, 1-1000 characters
  description: string;           // Optional, 0-500 characters
  
  // Metadata
  lastUpdated: string;           // Backend-managed, ISO 8601 timestamp
  createdAt: string;             // Backend-managed, ISO 8601 timestamp
}

type ConfigCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM';
```

### API Response Types

```typescript
// Generic API response wrapper
interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

// Error structure
interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>; // Field-level validation errors
}

// Paginated response
interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  pagination: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}

// List response for students
interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
}
```

---

## API Integration

### HTTP Client Setup

**File**: `src/services/api.ts`

```typescript
import axios, { AxiosInstance, AxiosError } from 'axios';

const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available (future)
    // const token = localStorage.getItem('authToken');
    // if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // Global error handling
    if (error.response?.status === 401) {
      // Handle unauthorized (future auth)
    }
    if (error.response?.status === 500) {
      // Log server errors
      console.error('Server error:', error);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### Student Service API

**File**: `src/services/studentService.ts`

**Endpoints**:

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/students` | List all students with optional filters | - | `StudentListResponse` |
| GET | `/api/students?search={query}` | Search by ID, name, or guardian | - | `StudentListResponse` |
| GET | `/api/students?status={status}` | Filter by status | - | `StudentListResponse` |
| GET | `/api/students/{id}` | Get student by ID | - | `ApiResponse<Student>` |
| POST | `/api/students` | Create new student | `Omit<Student, 'id' \| 'createdAt' \| 'updatedAt'>` | `ApiResponse<Student>` |
| PATCH | `/api/students/{id}` | Update student (restricted fields) | `{ firstName?, lastName?, phone?, status? }` | `ApiResponse<Student>` |
| DELETE | `/api/students/{id}` | Delete student | - | `ApiResponse<void>` |
| POST | `/api/students/validate-phone` | Check phone uniqueness | `{ phone: string, excludeId?: string }` | `ApiResponse<{ isUnique: boolean }>` |

**Service Methods**:

```typescript
export const studentService = {
  // Get all students with optional filters
  getAll: async (filters?: StudentFilters): Promise<Student[]> => { ... },
  
  // Get student by ID
  getById: async (id: string): Promise<Student> => { ... },
  
  // Create new student
  create: async (student: StudentCreateDto): Promise<Student> => { ... },
  
  // Update student (only allowed fields)
  update: async (id: string, updates: StudentUpdateDto): Promise<Student> => { ... },
  
  // Delete student
  delete: async (id: string): Promise<void> => { ... },
  
  // Validate phone uniqueness
  validatePhone: async (phone: string, excludeId?: string): Promise<boolean> => { ... },
  
  // Search students
  search: async (query: string): Promise<Student[]> => { ... },
};
```

### Configuration Service API

**File**: `src/services/configurationService.ts`

**Endpoints**:

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/configurations` | List all configurations | - | `ApiResponse<Configuration[]>` |
| GET | `/api/configurations?category={category}` | Filter by category | - | `ApiResponse<Configuration[]>` |
| GET | `/api/configurations/{id}` | Get configuration by ID | - | `ApiResponse<Configuration>` |
| POST | `/api/configurations` | Create new configuration | `Omit<Configuration, 'id' \| 'createdAt' \| 'lastUpdated'>` | `ApiResponse<Configuration>` |
| PATCH | `/api/configurations/{id}` | Update configuration | `Partial<Omit<Configuration, 'id' \| 'createdAt'>>` | `ApiResponse<Configuration>` |
| DELETE | `/api/configurations/{id}` | Delete configuration | - | `ApiResponse<void>` |

**Service Methods**:

```typescript
export const configurationService = {
  // Get all configurations
  getAll: async (): Promise<Configuration[]> => { ... },
  
  // Get by category
  getByCategory: async (category: ConfigCategory): Promise<Configuration[]> => { ... },
  
  // Get by ID
  getById: async (id: string): Promise<Configuration> => { ... },
  
  // Create configuration
  create: async (config: ConfigurationCreateDto): Promise<Configuration> => { ... },
  
  // Update configuration
  update: async (id: string, updates: ConfigurationUpdateDto): Promise<Configuration> => { ... },
  
  // Delete configuration
  delete: async (id: string): Promise<void> => { ... },
};
```

### Environment Configuration

**File**: `.env.development`
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_STUDENT_SERVICE_URL=http://localhost:8081
VITE_CONFIG_SERVICE_URL=http://localhost:8082
```

**File**: `.env.production`
```env
VITE_API_BASE_URL=https://api.schoolms.com
VITE_STUDENT_SERVICE_URL=https://students.schoolms.com
VITE_CONFIG_SERVICE_URL=https://config.schoolms.com
```

---

## Component Specifications

### Page Components

#### HomePage.tsx

**Purpose**: Dashboard with statistics and quick actions

**State**:
```typescript
interface HomePageState {
  statistics: {
    totalStudents: number;
    activeStudents: number;
    inactiveStudents: number;
  };
  loading: boolean;
  error: string | null;
}
```

**Data Fetching**:
- On mount: Fetch student statistics from API
- Auto-refresh: Optional, every 30 seconds

**UI Structure**:
1. Welcome banner
2. Statistics cards (3 cards):
   - Total Students (with Users icon)
   - Active Students (with CheckCircle icon)
   - System Status (with Settings icon)
3. Quick action cards (2 cards):
   - Manage Students (navigate to `/students`)
   - Register Student (navigate to `/students` with dialog open)

**Loading State**: Skeleton loaders for statistics cards

**Error Handling**: Display error alert if statistics fetch fails, fallback to zero values

---

#### StudentsPage.tsx

**Purpose**: Student listing, search, filter, and CRUD operations

**State**:
```typescript
interface StudentsPageState {
  students: Student[];
  filteredStudents: Student[];
  loading: boolean;
  error: string | null;
  filters: {
    search: string;        // Search by ID, name, guardian
    status: 'ALL' | 'ACTIVE' | 'INACTIVE';
  };
  dialogState: {
    mode: 'create' | 'edit' | 'view' | null;
    student?: Student;
  };
  deleteConfirmation: {
    isOpen: boolean;
    studentId?: string;
  };
}
```

**Data Fetching**:
- On mount: Fetch all students
- On filter change: Re-fetch with filters (backend search)
- Optimistic updates: Update UI immediately, rollback on API failure

**UI Structure**:
1. **Search/Filter Section**:
   - Search input (placeholder: "Search by ID, name, or guardian...")
   - Status filter dropdown (All / Active / Inactive)
   - "Register New Student" button (primary, top-right)

2. **Student Grid**:
   - Responsive grid: 1 column (mobile), 2 columns (tablet), 3 columns (desktop)
   - Student cards with:
     - Student ID (bold, top)
     - Name (firstName + lastName)
     - Guardian name
     - Phone, Email
     - Status badge (green for ACTIVE, gray for INACTIVE)
     - Action buttons: View, Edit, Delete

3. **Empty State**: "No students found" message when list is empty

**Dialogs**:
- `StudentDialog` (create/edit mode)
- `ViewStudentDialog` (read-only view)
- Confirmation dialog for delete

**Loading State**: Skeleton cards during fetch

**Error Handling**: Toast notification on API errors, retry button

**Features**:
- Real-time search (debounced 300ms)
- Client-side and server-side filtering
- Pagination (if student count > 50)

---

#### ConfigurationsPage.tsx

**Purpose**: Configuration management with category filtering

**State**:
```typescript
interface ConfigurationsPageState {
  configurations: Configuration[];
  filteredConfigurations: Configuration[];
  loading: boolean;
  error: string | null;
  categoryFilter: ConfigCategory | 'ALL';
  dialogState: {
    mode: 'create' | 'edit' | null;
    configuration?: Configuration;
  };
  deleteConfirmation: {
    isOpen: boolean;
    configId?: string;
  };
}
```

**Data Fetching**:
- On mount: Fetch all configurations
- On category filter change: Fetch by category (backend filter)

**UI Structure**:
1. **Header Section**:
   - Title: "System Configurations"
   - Category filter tabs or dropdown
   - "Add New Configuration" button

2. **Configuration Table**:
   - Columns: Category, Key, Value, Description, Last Updated, Actions
   - Category badges with color coding:
     - GENERAL: Blue
     - ACADEMIC: Purple
     - FINANCE: Green
     - SYSTEM: Gray
   - Actions: Edit (pencil icon), Delete (trash icon)

3. **Empty State**: "No configurations found" message

**Dialogs**:
- `ConfigurationDialog` (create/edit mode)
- Confirmation dialog for delete

**Loading State**: Skeleton table rows during fetch

**Error Handling**: Toast notification on errors

**Features**:
- Category-based filtering
- Sortable columns (optional)
- Inline editing (optional future enhancement)

---

### Dialog Components

#### StudentDialog.tsx

**Purpose**: Create or edit student with validation

**Props**:
```typescript
interface StudentDialogProps {
  mode: 'create' | 'edit';
  student?: Student;           // Required if mode is 'edit'
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (student: StudentCreateDto | StudentUpdateDto) => Promise<void>;
}
```

**Form Fields**:

**Section 1: Personal Information**
- First Name (text, required, max 50 chars)
- Last Name (text, required, max 50 chars)
- Date of Birth (date picker, required, age must be 3-18)
- Adhaar Number (text, required, 12 digits, disabled in edit mode)
- Address (textarea, required, 10-500 chars)
- Identification Marks (textarea, optional, max 200 chars)

**Section 2: Guardian Information**
- Guardian Name (text, required, max 100 chars)
- Mother Name (text, required, max 100 chars)

**Section 3: Contact Information**
- Phone (text, required, 10 digits, unique validation)
- Email (email, required, valid format, disabled in edit mode)

**Section 4: Status**
- Status (select, ACTIVE/INACTIVE, default: ACTIVE)

**Validation Rules**:
```typescript
const validationSchema = {
  firstName: { required: true, minLength: 1, maxLength: 50 },
  lastName: { required: true, minLength: 1, maxLength: 50 },
  dateOfBirth: { 
    required: true,
    validate: (value) => calculateAge(value) >= 3 && calculateAge(value) <= 18
  },
  adhaarNumber: {
    required: true,
    pattern: /^\d{12}$/,
    // Additional backend uniqueness check
  },
  phone: {
    required: true,
    pattern: /^\d{10}$/,
    validate: async (value) => await studentService.validatePhone(value)
  },
  email: {
    required: true,
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  },
  address: { required: true, minLength: 10, maxLength: 500 },
  guardianName: { required: true, minLength: 1, maxLength: 100 },
  motherName: { required: true, minLength: 1, maxLength: 100 },
  status: { required: true },
};
```

**Behavior**:
- **Create Mode**: All fields editable, generate ID on backend
- **Edit Mode**: Only firstName, lastName, phone, status editable; other fields disabled
- **Submit**: 
  - Validate form
  - Show loading state on submit button
  - Call `onSubmit` prop function
  - On success: Close dialog, show success toast
  - On error: Display field errors or general error, keep dialog open
- **Cancel**: Reset form, close dialog

**Loading States**:
- Phone uniqueness check (async validation)
- Form submission

**Error Display**:
- Field-level errors below inputs
- General error alert at top of form

---

#### ViewStudentDialog.tsx

**Purpose**: Read-only student details view

**Props**:
```typescript
interface ViewStudentDialogProps {
  student: Student;
  isOpen: boolean;
  onClose: () => void;
}
```

**UI Structure**:
- Student ID (prominent, top)
- Status badge (colored)
- All student fields displayed in labeled sections (same sections as StudentDialog)
- Timestamps: Created At, Updated At (formatted dates)
- Close button (bottom-right)

**No Form**: Pure display component, no inputs

---

#### ConfigurationDialog.tsx

**Purpose**: Create or edit configuration

**Props**:
```typescript
interface ConfigurationDialogProps {
  mode: 'create' | 'edit';
  configuration?: Configuration;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (config: ConfigurationCreateDto | ConfigurationUpdateDto) => Promise<void>;
}
```

**Form Fields**:
- Category (select, required, GENERAL/ACADEMIC/FINANCE/SYSTEM)
- Key (text, required, max 100 chars)
- Value (text, required, max 1000 chars)
- Description (textarea, optional, max 500 chars)

**Validation Rules**:
```typescript
const validationSchema = {
  category: { required: true },
  key: { required: true, minLength: 1, maxLength: 100 },
  value: { required: true, minLength: 1, maxLength: 1000 },
  description: { maxLength: 500 },
};
```

**Behavior**: Similar to StudentDialog (submit, cancel, error handling)

---

### Layout Components

#### Header.tsx

**Purpose**: Global navigation and branding

**UI Structure**:
- Left: Logo (GraduationCap icon) + "School Management" text
- Center: Navigation links (Home, Students, Configurations)
- Right: Dark mode toggle (optional future feature)

**Navigation**: React Router `<Link>` components with active state highlighting

**Responsive**: Hamburger menu on mobile (optional)

---

### Shared UI Components

Use Shadcn/ui components from `src/components/ui/`:
- `Button` - Primary actions, secondary actions, variants
- `Input` - Text inputs with labels and errors
- `Textarea` - Multi-line text inputs
- `Select` - Dropdowns for status, category
- `Dialog` - Modal overlays for forms
- `Alert` / `AlertDialog` - Error messages, confirmations
- `Badge` - Status indicators, categories
- `Card` - Student cards, statistic cards
- `Table` - Configuration listing
- `Skeleton` - Loading placeholders
- `Separator` - Visual dividers
- `Label` - Form labels

---

## Routing & Navigation

### Route Configuration

**File**: `src/App.tsx`

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/layout/Header';
import HomePage from './pages/HomePage';
import StudentsPage from './pages/StudentsPage';
import ConfigurationsPage from './pages/ConfigurationsPage';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50">
        <Header />
        <main>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/configurations" element={<ConfigurationsPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
```

### Route Structure

| Path | Component | Description | Access |
|------|-----------|-------------|--------|
| `/` | HomePage | Dashboard | Public |
| `/students` | StudentsPage | Student management | Public (future: Admin/Staff) |
| `/configurations` | ConfigurationsPage | System configurations | Public (future: Admin only) |
| `*` | Redirect to `/` | Catch-all fallback | - |

### Navigation Patterns

1. **Header Navigation**: Always visible, links to main pages
2. **Quick Actions**: Cards on HomePage that navigate to specific pages
3. **Back Navigation**: Browser back button (SPA preserves state)
4. **Deep Linking**: Direct URL access to any page
5. **Active Route Highlighting**: Current page highlighted in header

### Future: Protected Routes

When authentication is added:
```typescript
<Route path="/students" element={<ProtectedRoute><StudentsPage /></ProtectedRoute>} />
```

---

## State Management

### Strategy: React Context API + Custom Hooks

**Rationale**: Current application scope is manageable with Context API. Avoid unnecessary complexity of Redux/Zustand until truly needed.

### Global State (AppContext)

**File**: `src/contexts/AppContext.tsx`

```typescript
interface AppContextValue {
  // User info (future auth)
  user: User | null;
  
  // Theme
  theme: 'light' | 'dark';
  setTheme: (theme: 'light' | 'dark') => void;
  
  // Global loading (e.g., initial data fetch)
  isLoading: boolean;
  
  // Toast notifications
  toast: {
    success: (message: string) => void;
    error: (message: string) => void;
    info: (message: string) => void;
  };
}
```

### Component-Level State

Use `useState` and `useReducer` for:
- Form data (managed by react-hook-form)
- UI state (dialog open/close, selected filters)
- Component-specific loading/error states

### Custom Hooks

**File**: `src/hooks/useStudents.ts`

Encapsulate student data fetching and management:

```typescript
export function useStudents() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchStudents = async (filters?: StudentFilters) => { ... };
  const createStudent = async (student: StudentCreateDto) => { ... };
  const updateStudent = async (id: string, updates: StudentUpdateDto) => { ... };
  const deleteStudent = async (id: string) => { ... };

  return { students, loading, error, fetchStudents, createStudent, updateStudent, deleteStudent };
}
```

Similar pattern for `useConfigurations.ts`.

### Data Flow

1. **Component mounts** → Calls custom hook → Hook fetches data from service
2. **User action** → Component calls hook method → Hook calls service → Updates state
3. **State changes** → Component re-renders with new data

### Caching Strategy

- **No caching initially**: Always fetch fresh data
- **Future enhancement**: Add React Query or SWR for automatic caching, revalidation

---

## Form Validation

### Validation Strategy

**Two-Layer Validation**:
1. **Client-Side**: Immediate feedback, reduce server load (React Hook Form)
2. **Server-Side**: Authoritative validation, security, uniqueness checks

### Student Form Validation

#### Client-Side Rules

```typescript
const studentValidationRules = {
  firstName: {
    required: 'First name is required',
    minLength: { value: 1, message: 'First name cannot be empty' },
    maxLength: { value: 50, message: 'First name cannot exceed 50 characters' },
    pattern: {
      value: /^[a-zA-Z\s]+$/,
      message: 'First name can only contain letters and spaces'
    }
  },
  
  lastName: {
    required: 'Last name is required',
    minLength: { value: 1, message: 'Last name cannot be empty' },
    maxLength: { value: 50, message: 'Last name cannot exceed 50 characters' },
    pattern: {
      value: /^[a-zA-Z\s]+$/,
      message: 'Last name can only contain letters and spaces'
    }
  },
  
  dateOfBirth: {
    required: 'Date of birth is required',
    validate: {
      ageRange: (value: string) => {
        const age = calculateAge(value);
        if (age < 3) return 'Student must be at least 3 years old';
        if (age > 18) return 'Student must be 18 years or younger at registration';
        return true;
      },
      notFuture: (value: string) => {
        return new Date(value) <= new Date() || 'Date of birth cannot be in the future';
      }
    }
  },
  
  adhaarNumber: {
    required: 'Adhaar number is required',
    pattern: {
      value: /^\d{12}$/,
      message: 'Adhaar number must be exactly 12 digits'
    }
  },
  
  phone: {
    required: 'Phone number is required',
    pattern: {
      value: /^\d{10}$/,
      message: 'Phone number must be exactly 10 digits'
    },
    validate: {
      unique: async (value: string, formValues) => {
        // Skip if editing and phone hasn't changed
        if (mode === 'edit' && value === originalStudent?.phone) return true;
        
        const isUnique = await studentService.validatePhone(value, formValues.id);
        return isUnique || 'Phone number is already registered';
      }
    }
  },
  
  email: {
    required: 'Email is required',
    pattern: {
      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: 'Please enter a valid email address'
    }
  },
  
  address: {
    required: 'Address is required',
    minLength: { value: 10, message: 'Address must be at least 10 characters' },
    maxLength: { value: 500, message: 'Address cannot exceed 500 characters' }
  },
  
  identificationMarks: {
    maxLength: { value: 200, message: 'Identification marks cannot exceed 200 characters' }
  },
  
  guardianName: {
    required: 'Guardian name is required',
    minLength: { value: 1, message: 'Guardian name cannot be empty' },
    maxLength: { value: 100, message: 'Guardian name cannot exceed 100 characters' }
  },
  
  motherName: {
    required: 'Mother name is required',
    minLength: { value: 1, message: 'Mother name cannot be empty' },
    maxLength: { value: 100, message: 'Mother name cannot exceed 100 characters' }
  },
  
  status: {
    required: 'Status is required'
  }
};
```

#### Helper Functions

```typescript
// Calculate age from date of birth
function calculateAge(dob: string): number {
  const today = new Date();
  const birthDate = new Date(dob);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  
  return age;
}
```

#### Server-Side Validation

Backend must validate:
- All required fields present
- Data type correctness
- Length constraints
- Format validation (email, phone, adhaar)
- **Uniqueness constraints**: phone, email, adhaar (database level)
- Age calculation and range
- **Edit restrictions**: In PATCH requests, reject changes to immutable fields

#### Error Display

```typescript
// Field-level errors
<Input
  {...register('firstName', validationRules.firstName)}
  error={errors.firstName?.message}
/>

// General form errors (from backend)
{apiError && (
  <Alert variant="destructive">
    <AlertDescription>{apiError}</AlertDescription>
  </Alert>
)}
```

### Configuration Form Validation

#### Client-Side Rules

```typescript
const configValidationRules = {
  category: {
    required: 'Category is required'
  },
  
  key: {
    required: 'Key is required',
    minLength: { value: 1, message: 'Key cannot be empty' },
    maxLength: { value: 100, message: 'Key cannot exceed 100 characters' },
    pattern: {
      value: /^[A-Z0-9_]+$/,
      message: 'Key must be uppercase letters, numbers, and underscores only'
    }
  },
  
  value: {
    required: 'Value is required',
    minLength: { value: 1, message: 'Value cannot be empty' },
    maxLength: { value: 1000, message: 'Value cannot exceed 1000 characters' }
  },
  
  description: {
    maxLength: { value: 500, message: 'Description cannot exceed 500 characters' }
  }
};
```

#### Server-Side Validation

Backend must validate:
- Key uniqueness within category
- Valid category enum value
- All length constraints

---

## Styling Guidelines

### Tailwind CSS Configuration

**File**: `tailwind.config.ts`

```typescript
import type { Config } from 'tailwindcss';

const config: Config = {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
      },
      colors: {
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        // ... other color definitions
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};

export default config;
```

### Theme Variables

**File**: `src/styles/theme.css`

```css
@layer base {
  :root {
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;
    --card: 0 0% 100%;
    --card-foreground: 222.2 84% 4.9%;
    --primary: 221.2 83.2% 53.3%;
    --primary-foreground: 210 40% 98%;
    --radius: 0.625rem;
    /* ... other variables */
  }

  .dark {
    --background: 222.2 84% 4.9%;
    --foreground: 210 40% 98%;
    /* ... dark mode variables */
  }
}
```

### Responsive Breakpoints

```css
/* Mobile First Approach */
/* Default: Mobile (< 640px) */
/* sm: Tablet (>= 640px) */
/* md: Small Desktop (>= 768px) */
/* lg: Desktop (>= 1024px) */
/* xl: Large Desktop (>= 1280px) */
/* 2xl: Extra Large (>= 1536px) */
```

**Example Usage**:
```typescript
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
  {/* 1 column mobile, 2 tablet, 3 desktop */}
</div>
```

### Component Styling Patterns

#### 1. Utility-First Approach
```typescript
// Prefer inline Tailwind classes
<button className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
  Submit
</button>
```

#### 2. Consistent Spacing
- Use spacing scale: `p-2`, `p-4`, `p-6`, `p-8`, `p-12`
- Page padding: `p-6` (mobile), `p-8` (desktop)
- Component spacing: `space-y-4`, `gap-4`

#### 3. Typography
```typescript
// Headings
<h1 className="text-3xl font-bold">Page Title</h1>
<h2 className="text-2xl font-semibold">Section Title</h2>
<h3 className="text-xl font-medium">Card Title</h3>

// Body text
<p className="text-base text-gray-700">Regular text</p>
<p className="text-sm text-gray-500">Small text, hints</p>
```

#### 4. Color Usage
- **Primary Actions**: `bg-blue-600 hover:bg-blue-700`
- **Destructive Actions**: `bg-red-600 hover:bg-red-700`
- **Secondary Actions**: `bg-gray-200 hover:bg-gray-300 text-gray-900`
- **Success**: `bg-green-100 text-green-800` (badges, alerts)
- **Warning**: `bg-yellow-100 text-yellow-800`
- **Error**: `bg-red-100 text-red-800`

#### 5. Shadows & Borders
```typescript
// Cards
<div className="bg-white rounded-lg shadow-sm border border-gray-200">

// Elevated cards (hover)
<div className="hover:shadow-md transition-shadow">

// Focused inputs
<input className="border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200">
```

### Accessibility Considerations

1. **Focus States**: Always provide visible focus indicators
2. **Color Contrast**: Ensure WCAG AA compliance (4.5:1 for text)
3. **Interactive Elements**: Minimum touch target size 44x44px
4. **Screen Readers**: Use semantic HTML, ARIA labels when needed

```typescript
<button
  aria-label="Delete student John Doe"
  className="focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
>
  <Trash2 className="h-4 w-4" />
</button>
```

---

## Error Handling

### Error Types

1. **Network Errors**: Connection failures, timeouts
2. **API Errors**: 4xx, 5xx responses from backend
3. **Validation Errors**: Form validation failures
4. **Runtime Errors**: JavaScript exceptions, component errors

### Error Handling Strategy

#### 1. API Error Handling

```typescript
// In service layer
try {
  const response = await apiClient.post('/api/students', studentData);
  return response.data;
} catch (error) {
  if (axios.isAxiosError(error)) {
    if (error.response) {
      // Server responded with error
      throw new ApiError(
        error.response.data.message || 'Server error',
        error.response.status,
        error.response.data.details
      );
    } else if (error.request) {
      // Network error
      throw new ApiError('Network error. Please check your connection.', 0);
    }
  }
  throw new ApiError('An unexpected error occurred');
}
```

#### 2. Component Error Handling

```typescript
// In component
const [error, setError] = useState<string | null>(null);

const handleCreateStudent = async (data: StudentCreateDto) => {
  try {
    setError(null);
    await studentService.create(data);
    toast.success('Student created successfully');
    onClose();
  } catch (err) {
    if (err instanceof ApiError) {
      setError(err.message);
      
      // Field-level validation errors
      if (err.details) {
        Object.entries(err.details).forEach(([field, messages]) => {
          form.setError(field as any, { message: messages[0] });
        });
      }
    } else {
      setError('An unexpected error occurred');
    }
    toast.error('Failed to create student');
  }
};
```

#### 3. Error Boundary

**File**: `src/components/common/ErrorBoundary.tsx`

```typescript
class ErrorBoundary extends React.Component<Props, State> {
  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // Log to error tracking service (e.g., Sentry)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <Alert variant="destructive">
            <AlertTitle>Something went wrong</AlertTitle>
            <AlertDescription>
              Please refresh the page or contact support if the problem persists.
            </AlertDescription>
            <Button onClick={() => window.location.reload()}>
              Refresh Page
            </Button>
          </Alert>
        </div>
      );
    }

    return this.props.children;
  }
}
```

#### 4. Toast Notifications

Use Sonner for user-friendly feedback:

```typescript
import { toast } from 'sonner';

// Success
toast.success('Student created successfully');

// Error
toast.error('Failed to delete configuration');

// Info
toast.info('Data is being synchronized...');

// With action
toast.error('Failed to save', {
  action: {
    label: 'Retry',
    onClick: () => handleRetry(),
  },
});
```

### Error Messages

**User-Friendly Messages**:
- ❌ "Error 500"
- ✅ "Unable to save student. Please try again."

**Specific Guidance**:
- ❌ "Validation failed"
- ✅ "Phone number is already registered to another student"

### Loading States

Always show loading indicators during async operations:

```typescript
{loading ? (
  <Skeleton className="h-8 w-full" />
) : (
  <StudentCard student={student} />
)}

<Button disabled={isSubmitting}>
  {isSubmitting ? 'Saving...' : 'Save Student'}
</Button>
```

---

## Performance & Optimization

### Code Splitting

Use React lazy loading for routes:

```typescript
import { lazy, Suspense } from 'react';

const StudentsPage = lazy(() => import('./pages/StudentsPage'));

<Suspense fallback={<LoadingSpinner />}>
  <StudentsPage />
</Suspense>
```

### Memoization

Prevent unnecessary re-renders:

```typescript
import { memo, useMemo, useCallback } from 'react';

// Memoize expensive components
const StudentCard = memo(({ student }: StudentCardProps) => { ... });

// Memoize expensive calculations
const sortedStudents = useMemo(
  () => students.sort((a, b) => a.lastName.localeCompare(b.lastName)),
  [students]
);

// Memoize callbacks passed to child components
const handleDelete = useCallback(
  (id: string) => deleteStudent(id),
  [deleteStudent]
);
```

### Debouncing

Debounce search input to reduce API calls:

```typescript
import { useDebounce } from './hooks/useDebounce';

const [searchQuery, setSearchQuery] = useState('');
const debouncedQuery = useDebounce(searchQuery, 300);

useEffect(() => {
  if (debouncedQuery) {
    fetchStudents({ search: debouncedQuery });
  }
}, [debouncedQuery]);
```

### Image Optimization

- Use WebP format with PNG/JPEG fallback
- Lazy load images below the fold
- Provide `width` and `height` attributes

### Bundle Size

- Tree-shake unused code
- Analyze bundle with `vite-bundle-visualizer`
- Import only used Lucide icons: `import { Users } from 'lucide-react'`

### Caching (Future)

Consider adding React Query:
```typescript
const { data: students } = useQuery('students', studentService.getAll, {
  staleTime: 5 * 60 * 1000, // 5 minutes
  cacheTime: 10 * 60 * 1000, // 10 minutes
});
```

---

## Gap Analysis

### Reference Code vs Production Requirements

| Feature | Reference Code | Production Requirement | Gap |
|---------|----------------|------------------------|-----|
| **Student ID Generation** | Frontend-generated | Backend-generated | ❌ Critical |
| **Data Persistence** | Local state (resets on refresh) | Backend database | ❌ Critical |
| **Student DOB Field** | Age field only | DOB + calculated age | ❌ High |
| **Adhaar Number Field** | Missing | Required unique field | ❌ High |
| **Timestamps** | Missing createdAt/updatedAt | Required metadata | ❌ Medium |
| **Edit Restrictions** | All fields editable | Only Name/Mobile/Status | ❌ High |
| **Phone Uniqueness** | No validation | Must validate uniqueness | ❌ Critical |
| **Age Validation** | No validation | Must be 3-18 at registration | ❌ High |
| **Backend Integration** | No API calls | Full CRUD via APIs | ❌ Critical |
| **Search Functionality** | Client-side only | Backend search with pagination | ❌ Medium |
| **Configuration Categories** | Display only | Filter by category (backend) | ❌ Medium |
| **Loading States** | No loading indicators | Skeletons, spinners | ❌ Medium |
| **Error Handling** | Basic `window.confirm` | Toast notifications, proper dialogs | ❌ Medium |
| **Dashboard Statistics** | Hardcoded values | Real-time from API | ❌ High |
| **Form Validation** | Basic required fields | Comprehensive validation | ❌ High |
| **Responsive Design** | ✅ Implemented | Responsive | ✅ Complete |
| **UI Components** | ✅ Shadcn/ui | Same | ✅ Complete |
| **Routing** | ✅ React Router | Same | ✅ Complete |
| **Visual Design** | ✅ Figma design | Same | ✅ Complete |

### Missing Features

1. ❌ **API Service Layer** - No HTTP client or service methods
2. ❌ **Authentication** - Out of scope for Phase 1, but needs integration points
3. ❌ **Pagination** - Not implemented (needed for >50 students)
4. ❌ **Student ID Search** - Only name/guardian search available
5. ❌ **Bulk Operations** - No multi-select, bulk delete, export
6. ❌ **Advanced Filtering** - No age range, date range filters
7. ❌ **Sorting** - No sortable columns
8. ❌ **Data Export** - No CSV/PDF export
9. ❌ **Audit Log** - No change history tracking
10. ❌ **Dark Mode** - Toggle exists but not functional
11. ❌ **Accessibility Audit** - No comprehensive a11y testing
12. ❌ **Unit Tests** - No test coverage
13. ❌ **E2E Tests** - No automated testing
14. ❌ **Error Tracking** - No Sentry or similar integration
15. ❌ **Performance Monitoring** - No metrics collection

---

## Implementation Priorities

### Phase 1: Core Functionality (Critical)

**Priority**: P0 (Must Have)  
**Timeline**: Week 1-2

1. ✅ **Project Setup**
   - Initialize Vite + React + TypeScript project
   - Install dependencies (Tailwind, Shadcn/ui, React Router, etc.)
   - Configure build tools
   - Set up environment variables

2. ✅ **API Integration Layer**
   - HTTP client setup (axios)
   - Student service with all CRUD endpoints
   - Configuration service with all CRUD endpoints
   - Error handling utilities
   - Type definitions for API requests/responses

3. ✅ **Data Model Updates**
   - Update Student interface (add DOB, Adhaar, timestamps)
   - Update Configuration interface
   - Create DTOs for create/update operations

4. ✅ **Component Migration & Enhancement**
   - Migrate all components from reference-code
   - Update StudentDialog with new fields and validation
   - Implement backend integration in all pages
   - Add loading states and error handling
   - Implement toast notifications

5. ✅ **Form Validation**
   - Implement all client-side validation rules
   - Add async phone uniqueness validation
   - Age calculation from DOB
   - Edit restrictions enforcement

6. ✅ **Testing & Bug Fixes**
   - Manual testing of all CRUD operations
   - Verify all validation rules
   - Test error scenarios
   - Fix any bugs found

### Phase 2: Enhanced UX (High Priority)

**Priority**: P1 (Should Have)  
**Timeline**: Week 3

1. ⏳ **Search & Filtering Enhancements**
   - Student ID search field
   - Backend search integration
   - Category filtering for configurations
   - Improved filter UI

2. ⏳ **Pagination**
   - Implement pagination component
   - Backend pagination support
   - Page size selector

3. ⏳ **Better Confirmation Dialogs**
   - Replace `window.confirm` with AlertDialog
   - Styled confirmation modals
   - Action-specific messaging

4. ⏳ **Accessibility Improvements**
   - Add ARIA labels
   - Keyboard navigation
   - Screen reader testing
   - Focus management

### Phase 3: Nice-to-Have Features (Medium Priority)

**Priority**: P2 (Could Have)  
**Timeline**: Week 4

1. ⏳ **Sorting**
   - Sortable table columns
   - Sort indicators
   - Multi-column sorting

2. ⏳ **Advanced Filtering**
   - Age range filter
   - Date range filter
   - Multi-select filters

3. ⏳ **Dark Mode**
   - Functional theme toggle
   - Persist theme preference
   - Smooth transitions

4. ⏳ **Data Export**
   - Export student list to CSV
   - Export configurations
   - Print-friendly views

### Phase 4: Quality & Optimization (Lower Priority)

**Priority**: P3 (Nice to Have)  
**Timeline**: Week 5+

1. ⏳ **Testing Infrastructure**
   - Unit tests (Jest + React Testing Library)
   - Integration tests
   - E2E tests (Playwright)
   - Test coverage reports

2. ⏳ **Performance Optimization**
   - Code splitting
   - Lazy loading
   - Bundle optimization
   - Caching strategy (React Query)

3. ⏳ **Monitoring & Analytics**
   - Error tracking (Sentry)
   - Performance monitoring
   - User analytics

4. ⏳ **Documentation**
   - Component Storybook
   - API documentation
   - Developer onboarding guide

### Out of Scope (Future Phases)

- **Authentication & Authorization** (External system integration)
- **Multi-School Support** (Phase 2 requirement)
- **Role-Based Access Control** (Future)
- **Advanced Reporting & Analytics** (Future)
- **Mobile Native App** (Future)
- **Offline Support** (Future)
- **Real-Time Collaboration** (Future)

---

## Appendix

### Development Workflow

1. **Setup**: Clone repo, install dependencies, configure environment
2. **Development**: Run `npm run dev`, hot reload enabled
3. **Linting**: Run `npm run lint` before committing
4. **Build**: Run `npm run build` for production bundle
5. **Preview**: Run `npm run preview` to test production build

### Deployment Checklist

- [ ] Environment variables configured
- [ ] API endpoints tested
- [ ] Build succeeds without errors
- [ ] No console errors in browser
- [ ] All features manually tested
- [ ] Responsive design verified
- [ ] CORS configured on backend
- [ ] Error tracking enabled
- [ ] Performance metrics acceptable

### Browser Support

- **Modern Browsers**: Chrome, Firefox, Safari, Edge (latest 2 versions)
- **Mobile**: iOS Safari 14+, Chrome Android 90+
- **Not Supported**: IE11, Opera Mini

### Design Resources

- **Figma Design**: [Link to Figma file]
- **Color Palette**: Defined in `theme.css`
- **Icons**: [Lucide React](https://lucide.dev/)
- **Component Library**: [Shadcn/ui Docs](https://ui.shadcn.com/)

---

**Document Revision History**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-01-08 | System | Initial specification based on Figma reference code |

---

**End of Frontend Design Specification**
