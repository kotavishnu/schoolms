# Frontend Implementation Plan - School Management System
**Waterfall / Single-Pass Implementation**
**Date**: 2026-01-15
**Architecture**: React SPA with Strict Reference Code Reuse

---

## CRITICAL CONSTRAINTS (MANDATORY)

### STRICT REUSE POLICY

1. **FORBIDDEN ACTIVITIES**:
   - Creating new UI components from scratch
   - Writing custom CSS, SASS, or styled-components
   - Designing new layouts or visual patterns
   - Creating new UI primitives

2. **MANDATORY ACTIVITIES**:
   - Copy/paste components EXACTLY from `frontend/reference-code/`
   - Maintain identical JSX structure
   - Preserve all Tailwind classes
   - Keep visual design 1:1 with reference

3. **ALLOWED MODIFICATIONS**:
   - Replace mock data with API calls
   - Add React Query/SWR hooks for data fetching
   - Wire forms to Zod validation
   - Add error handling and loading states
   - Integrate with backend APIs

### REFERENCE CODE LOCATION
**Source**: `D:\SCHOOL-GIT-AUTONOMOUS_FE_INSTR_ITR2\schoolms\frontend\reference-code\`

---

## Overview

This plan provides a sequential, atomic task breakdown for implementing the School Management System frontend as a single continuous execution flow (no sprints). The primary goal is **migration and integration**, NOT design or development from scratch.

**Technology Stack**:
- React 18+
- TypeScript 5.x
- Vite 5.x
- Tailwind CSS v4
- Shadcn/ui components
- React Hook Form + Zod
- Axios
- React Router v6
- Sonner (Toast notifications)

---

## Phase 1: Project Foundation

### [FE-001] Initialize React Project with Vite
**Goal**: Set up the base React + TypeScript + Vite project.

**Technical Details**:
- **Command**: `npm create vite@latest frontend -- --template react-ts`
- **Project Structure**:
  ```
  frontend/
  ├── src/
  │   ├── components/        # UI components (copied from reference)
  │   ├── pages/             # Page-level components
  │   ├── services/          # API integration
  │   ├── types/             # TypeScript types
  │   ├── utils/             # Helper functions
  │   ├── hooks/             # Custom hooks
  │   ├── App.tsx
  │   └── main.tsx
  ├── public/
  ├── index.html
  ├── package.json
  ├── tailwind.config.ts     # COPY from reference-code
  ├── tsconfig.json
  └── vite.config.ts
  ```

**Dependencies to Install**:
```json
{
  "dependencies": {
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "react-router-dom": "^6.22.0",
    "axios": "^1.6.0",
    "react-hook-form": "^7.50.0",
    "zod": "^3.22.0",
    "@hookform/resolvers": "^3.3.0",
    "lucide-react": "^0.344.0",
    "sonner": "^1.4.0",
    "@radix-ui/react-dialog": "^1.0.5",
    "@radix-ui/react-select": "^2.0.0",
    "@radix-ui/react-label": "^2.0.2",
    "@radix-ui/react-slot": "^1.0.2"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@vitejs/plugin-react": "^4.2.0",
    "typescript": "^5.3.0",
    "vite": "^5.1.0",
    "tailwindcss": "^4.0.0",
    "autoprefixer": "^10.4.0",
    "postcss": "^8.4.0"
  }
}
```

**Dependencies**: None

---

### [FE-002] Copy Tailwind Configuration and Styles
**Goal**: Ensure exact visual parity with reference code.

**Technical Details**:
**MANDATE**: Copy the following files EXACTLY from reference-code to frontend project root:

1. **Copy `tailwind.config.ts`**:
   - Source: `frontend/reference-code/tailwind.config.ts`
   - Destination: `frontend/tailwind.config.ts`
   - DO NOT modify any colors, fonts, or spacing

2. **Copy global styles**:
   - Source: `frontend/reference-code/app/styles/` (if exists)
   - Destination: `frontend/src/styles/`
   - Include `index.css`, theme variables

3. **Verify Tailwind classes**:
   - Ensure all `@layer` directives are preserved
   - Confirm HSL color variables match reference

**Dependencies**: Requires FE-001

---

### [FE-003] Copy UI Primitives (Shadcn/ui Components)
**Goal**: Copy all base UI components from reference code.

**Technical Details**:
**MANDATE**: Copy the ENTIRE `frontend/reference-code/app/components/ui/` directory to `frontend/src/components/ui/`

**Components to Copy** (60+ components):
- accordion.tsx
- alert-dialog.tsx
- alert.tsx
- avatar.tsx
- badge.tsx
- button.tsx
- calendar.tsx
- card.tsx
- checkbox.tsx
- dialog.tsx
- dropdown-menu.tsx
- form.tsx
- input.tsx
- label.tsx
- select.tsx
- separator.tsx
- sheet.tsx
- skeleton.tsx
- sonner.tsx
- table.tsx
- tabs.tsx
- textarea.tsx
- tooltip.tsx
- ... (all UI components)

**CRITICAL**: DO NOT modify any component logic, styles, or props.

**Dependencies**: Requires FE-002

---

## Phase 2: Type Definitions and API Layer

### [FE-004] Create TypeScript Type Definitions
**Goal**: Define types matching backend API contracts.

**Technical Details**:
- **File**: `src/types/index.ts`

**Types Required**:

```typescript
// Student Types
export interface Student {
  id: string;                    // studentId from backend
  firstName: string;
  lastName: string;
  dateOfBirth: string;           // ISO 8601 format
  age: number;                   // Calculated
  adhaarNumber: string;
  identificationMarks: string;
  address: string;
  guardianName: string;          // Maps to fathersName
  motherName: string;
  phone: string;                 // Maps to mobile
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number;               // For optimistic locking
  createdAt: string;
  updatedAt: string;
}

export interface StudentCreateDto {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  adhaarNumber: string;
  identificationMarks?: string;
  address: string;
  guardianName: string;
  motherName: string;
  phone: string;
  email: string;
}

export interface StudentUpdateDto {
  firstName?: string;
  lastName?: string;
  phone?: string;
  status?: 'ACTIVE' | 'INACTIVE';
  version: number;
}

// Configuration Types
export interface Configuration {
  id: string;
  category: 'GENERAL' | 'ACADEMIC' | 'FINANCIAL' | 'SYSTEM';
  key: string;
  value: string;
  description: string;
  dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  isEncrypted: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp: string;
  errors?: FieldError[];
}

export interface FieldError {
  field: string;
  message: string;
  code: string;
}
```

**Dependencies**: Requires FE-001

---

### [FE-005] Create HTTP Client and API Configuration
**Goal**: Set up Axios instance with interceptors.

**Technical Details**:
- **File**: `src/services/api.ts`

**Implementation**:
```typescript
import axios, { AxiosInstance, AxiosError } from 'axios';

const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Add correlation ID for tracing
    config.headers['X-Correlation-ID'] = crypto.randomUUID();
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
      // Future: Handle unauthorized
    }
    if (error.response?.status === 500) {
      console.error('Server error:', error);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

**Environment Variables** (`.env.development`):
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_STUDENT_SERVICE_URL=http://localhost:8081
VITE_CONFIG_SERVICE_URL=http://localhost:8082
```

**Dependencies**: Requires FE-004

---

### [FE-006] Create Student Service
**Goal**: API service methods for student CRUD operations.

**Technical Details**:
- **File**: `src/services/studentService.ts`

**Service Methods**:
```typescript
import apiClient from './api';
import { Student, StudentCreateDto, StudentUpdateDto, ApiResponse } from '../types';

export const studentService = {
  // Get all students with optional filters
  getAll: async (filters?: { status?: string; lastName?: string }): Promise<Student[]> => {
    const params = new URLSearchParams();
    if (filters?.status) params.append('status', filters.status);
    if (filters?.lastName) params.append('lastName', filters.lastName);

    const response = await apiClient.get<ApiResponse<Student[]>>(
      `/api/v1/students?${params.toString()}`
    );
    return response.data.data || [];
  },

  // Get student by ID
  getById: async (id: string): Promise<Student> => {
    const response = await apiClient.get<ApiResponse<Student>>(`/api/v1/students/${id}`);
    return response.data.data!;
  },

  // Create new student
  create: async (student: StudentCreateDto): Promise<Student> => {
    const response = await apiClient.post<ApiResponse<Student>>('/api/v1/students', student);
    return response.data.data!;
  },

  // Update student (only allowed fields)
  update: async (id: string, updates: StudentUpdateDto): Promise<Student> => {
    const response = await apiClient.put<ApiResponse<Student>>(
      `/api/v1/students/${id}`,
      updates
    );
    return response.data.data!;
  },

  // Delete student
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  // Validate phone uniqueness
  validatePhone: async (phone: string, excludeId?: string): Promise<boolean> => {
    const response = await apiClient.post<ApiResponse<{ isUnique: boolean }>>(
      '/api/v1/students/validate-phone',
      { phone, excludeId }
    );
    return response.data.data!.isUnique;
  },

  // Get statistics
  getStatistics: async (): Promise<{ totalStudents: number; activeStudents: number; inactiveStudents: number }> => {
    const response = await apiClient.get<ApiResponse<any>>('/api/v1/students/statistics');
    return response.data.data!;
  },
};
```

**Dependencies**: Requires FE-005

---

### [FE-007] Create Configuration Service
**Goal**: API service methods for configuration CRUD.

**Technical Details**:
- **File**: `src/services/configurationService.ts`

**Service Methods**:
```typescript
import apiClient from './api';
import { Configuration, ApiResponse } from '../types';

export const configurationService = {
  // Get all configurations
  getAll: async (category?: string): Promise<Configuration[]> => {
    const url = category
      ? `/api/v1/configurations?category=${category}`
      : '/api/v1/configurations';
    const response = await apiClient.get<ApiResponse<Configuration[]>>(url);
    return response.data.data || [];
  },

  // Get by category and key
  getByKey: async (category: string, key: string): Promise<Configuration> => {
    const response = await apiClient.get<ApiResponse<Configuration>>(
      `/api/v1/configurations/${category}/${key}`
    );
    return response.data.data!;
  },

  // Create or update configuration
  upsert: async (category: string, key: string, config: Partial<Configuration>): Promise<Configuration> => {
    const response = await apiClient.put<ApiResponse<Configuration>>(
      `/api/v1/configurations/${category}/${key}`,
      config
    );
    return response.data.data!;
  },

  // Delete configuration
  delete: async (category: string, key: string): Promise<void> => {
    await apiClient.delete(`/api/v1/configurations/${category}/${key}`);
  },
};
```

**Dependencies**: Requires FE-005

---

## Phase 3: Core Page Components (Reference Code Migration)

### [FE-008] Copy and Wire HomePage Component
**Goal**: Dashboard with statistics from backend.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/HomePage.tsx` to `frontend/src/pages/HomePage.tsx`

**STEP 2 - REFACTOR (Integration Only)**:
**DO NOT change**:
- JSX structure
- Tailwind classes
- Component layout
- Card designs

**DO change**:
- Replace mock statistics with API call to `studentService.getStatistics()`
- Add loading state using `<Skeleton>` components
- Add error handling with toast notifications
- Keep navigation links intact

**Example Refactor**:
```typescript
// BEFORE (Reference Code)
const stats = {
  totalStudents: 250,
  activeStudents: 235,
  inactiveStudents: 15
};

// AFTER (Integrated)
const [stats, setStats] = useState<StudentStatistics | null>(null);
const [loading, setLoading] = useState(true);

useEffect(() => {
  const fetchStats = async () => {
    try {
      const data = await studentService.getStatistics();
      setStats(data);
    } catch (error) {
      toast.error('Failed to load statistics');
    } finally {
      setLoading(false);
    }
  };
  fetchStats();
}, []);

if (loading) return <Skeleton className="h-64 w-full" />;
```

**Dependencies**: Requires FE-003, FE-006

---

### [FE-009] Copy and Wire StudentsPage Component
**Goal**: Student listing with search, filter, and CRUD operations.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/StudentsPage.tsx` to `frontend/src/pages/StudentsPage.tsx`

**STEP 2 - REFACTOR**:
**DO NOT change**:
- Student card layout
- Grid responsiveness
- Search input design
- Filter dropdown design
- Action button styles

**DO change**:
- Replace `mockStudents` array with `useEffect` + `studentService.getAll()`
- Add loading skeleton for student cards
- Wire search input to API filter (debounced 300ms)
- Wire status filter to API filter
- Wire "Register New Student" button to dialog state
- Add error boundary for API failures

**Loading State**:
```typescript
{loading ? (
  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
    {[1, 2, 3, 4, 5, 6].map(i => (
      <Skeleton key={i} className="h-48 w-full" />
    ))}
  </div>
) : (
  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
    {students.map(student => (
      <StudentCard key={student.id} student={student} />
    ))}
  </div>
)}
```

**Dependencies**: Requires FE-003, FE-006, FE-008

---

### [FE-010] Copy and Wire StudentDialog Component
**Goal**: Create/Edit student dialog with validation.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/StudentDialog.tsx` to `frontend/src/components/StudentDialog.tsx`

**STEP 2 - REFACTOR**:
**DO NOT change**:
- Dialog layout
- Form field arrangement
- Input styles
- Section separators
- Button styles

**DO change**:
- Replace mock form submission with `studentService.create()` or `studentService.update()`
- Wire React Hook Form with Zod validation
- Add async phone uniqueness validation
- Add loading state on submit button
- Show API errors in field-level or form-level alerts
- Close dialog on success + show toast
- Refetch student list on success

**Zod Schema** (create new file `src/utils/validation.ts`):
```typescript
import { z } from 'zod';

export const studentCreateSchema = z.object({
  firstName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
  lastName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
  dateOfBirth: z.string().refine((date) => {
    const age = calculateAge(date);
    return age >= 3 && age <= 18;
  }, { message: 'Age must be between 3 and 18 years' }),
  adhaarNumber: z.string().regex(/^\d{12}$/),
  address: z.string().min(10).max(500),
  guardianName: z.string().min(1).max(100),
  motherName: z.string().min(1).max(100),
  phone: z.string().regex(/^\d{10}$/),
  email: z.string().email(),
  identificationMarks: z.string().max(200).optional(),
});

export const studentUpdateSchema = z.object({
  firstName: z.string().min(2).max(100).optional(),
  lastName: z.string().min(2).max(100).optional(),
  phone: z.string().regex(/^\d{10}$/).optional(),
  status: z.enum(['ACTIVE', 'INACTIVE']).optional(),
  version: z.number(),
});
```

**Form Integration**:
```typescript
const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
  resolver: zodResolver(studentCreateSchema),
  defaultValues: student || {}
});

const onSubmit = async (data: StudentCreateDto) => {
  try {
    if (mode === 'create') {
      await studentService.create(data);
      toast.success('Student registered successfully');
    } else {
      await studentService.update(student.id, data);
      toast.success('Student updated successfully');
    }
    onClose();
    refetch(); // Trigger refetch in parent
  } catch (error) {
    toast.error('Failed to save student');
    // Display field errors if available
  }
};
```

**Dependencies**: Requires FE-003, FE-006, FE-009

---

### [FE-011] Copy and Wire ViewStudentDialog Component
**Goal**: Read-only student details view.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/ViewStudentDialog.tsx` to `frontend/src/components/ViewStudentDialog.tsx`

**STEP 2 - VERIFY**: No refactoring needed. This component is pure display. Just ensure it receives `student` prop from parent.

**Dependencies**: Requires FE-003, FE-009

---

### [FE-012] Copy and Wire ConfigurationsPage Component
**Goal**: Configuration management with category filtering.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/ConfigurationsPage.tsx` to `frontend/src/pages/ConfigurationsPage.tsx`

**STEP 2 - REFACTOR**:
**DO NOT change**:
- Table layout
- Category badge colors
- Filter tabs design
- Action icons

**DO change**:
- Replace mock configurations with `configurationService.getAll()`
- Wire category filter to API filter
- Add loading skeleton for table rows
- Wire "Add New Configuration" button to dialog
- Wire delete action to `configurationService.delete()`

**Dependencies**: Requires FE-003, FE-007

---

### [FE-013] Copy and Wire ConfigurationDialog Component
**Goal**: Create/Edit configuration dialog.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/ConfigurationDialog.tsx` to `frontend/src/components/ConfigurationDialog.tsx`

**STEP 2 - REFACTOR**:
**DO NOT change**:
- Dialog layout
- Form fields
- Category dropdown options

**DO change**:
- Replace mock form submission with `configurationService.upsert()`
- Wire React Hook Form with Zod validation
- Add loading state on submit
- Show API errors
- Close dialog on success + show toast

**Dependencies**: Requires FE-003, FE-007, FE-012

---

### [FE-014] Copy and Wire Header Component
**Goal**: Global navigation and branding.

**Technical Details**:
**STEP 1 - COPY**: Copy `frontend/reference-code/app/components/Header.tsx` to `frontend/src/components/Header.tsx`

**STEP 2 - REFACTOR**:
**DO NOT change**:
- Logo design
- Navigation link styles
- Layout

**DO change**:
- Replace hardcoded links with React Router `<Link>` components
- Add active state highlighting based on current route
- Ensure navigation works with routing

**Dependencies**: Requires FE-003

---

## Phase 4: Routing and Navigation

### [FE-015] Set Up React Router
**Goal**: Client-side routing configuration.

**Technical Details**:
- **File**: `src/App.tsx`

**Implementation**:
```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import StudentsPage from './pages/StudentsPage';
import ConfigurationsPage from './pages/ConfigurationsPage';
import { Toaster } from 'sonner';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50">
        <Header />
        <main className="container mx-auto px-4 py-6">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/configurations" element={<ConfigurationsPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Toaster position="top-right" richColors />
      </div>
    </BrowserRouter>
  );
}

export default App;
```

**Dependencies**: Requires FE-008 through FE-014

---

## Phase 5: Validation and Error Handling

### [FE-016] Create Zod Validation Schemas
**Goal**: Centralized validation logic.

**Technical Details**:
- **File**: `src/utils/validation.ts`

**Schemas**:
- `studentCreateSchema` (see FE-010)
- `studentUpdateSchema`
- `configurationCreateSchema`

**Helper Functions**:
```typescript
export function calculateAge(dob: string): number {
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

**Dependencies**: Requires FE-004

---

### [FE-017] Implement Error Handling and Toast Notifications
**Goal**: User-friendly error feedback.

**Technical Details**:
- **Library**: Sonner (already in reference code)
- **Usage**: Import `{ toast }` from 'sonner' in all service-calling components

**Error Handling Pattern**:
```typescript
try {
  await studentService.create(data);
  toast.success('Student registered successfully');
} catch (error) {
  if (axios.isAxiosError(error)) {
    const apiError = error.response?.data as ApiError;
    if (apiError?.errors) {
      // Field-level errors
      apiError.errors.forEach(err => {
        setError(err.field as any, { message: err.message });
      });
    } else {
      toast.error(apiError?.detail || 'An error occurred');
    }
  } else {
    toast.error('An unexpected error occurred');
  }
}
```

**Dependencies**: Requires FE-005

---

## Phase 6: DevOps and Deployment

### [FE-018] Create Dockerfile for Production Build
**Goal**: Containerize frontend application.

**Technical Details**:
- **File**: `frontend/Dockerfile`

**Dockerfile**:
```dockerfile
# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**nginx.conf**:
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://student-service:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

**Dependencies**: Requires FE-015

---

### [FE-019] Create Docker Compose for Frontend + Backend
**Goal**: Single command to start full stack.

**Technical Details**:
- **File**: `docker-compose.yml` (root level)

**Services**:
- frontend (port 80)
- student-service (port 8081)
- configuration-service (port 8082)
- student-db (port 5433)
- config-db (port 5434)

**Dependencies**: Requires FE-018 and BE-025

---

## Phase 7: Testing and Validation

### [FE-020] Manual UI Testing
**Goal**: Verify all user flows work correctly.

**Test Scenarios**:

1. **Student Management**:
   - [ ] Create student with valid data
   - [ ] Create student with invalid age → validation error
   - [ ] Create student with duplicate phone → API error
   - [ ] Search students by last name
   - [ ] Filter students by status
   - [ ] Edit student (only allowed fields)
   - [ ] View student details
   - [ ] Delete student
   - [ ] Verify statistics update after CRUD operations

2. **Configuration Management**:
   - [ ] Create new configuration
   - [ ] Update existing configuration
   - [ ] Filter by category
   - [ ] Delete configuration

3. **Navigation**:
   - [ ] Navigate between pages via header
   - [ ] Navigate via quick action cards on homepage
   - [ ] Browser back/forward buttons work

4. **Error Handling**:
   - [ ] Network error shows toast
   - [ ] Validation errors show inline
   - [ ] API errors show toast

**Dependencies**: Requires FE-015, BE-024

---

### [FE-021] Cross-Browser Testing
**Goal**: Ensure compatibility.

**Browsers to Test**:
- Chrome (latest)
- Firefox (latest)
- Edge (latest)
- Safari (latest) - if on Mac

**Dependencies**: Requires FE-020

---

### [FE-022] Responsive Design Verification
**Goal**: Ensure mobile, tablet, desktop work correctly.

**Viewports to Test**:
- Mobile: 375px (iPhone SE)
- Tablet: 768px (iPad)
- Desktop: 1280px, 1920px

**Test**:
- [ ] Student cards stack correctly on mobile
- [ ] Header hamburger menu works (if implemented in reference)
- [ ] Dialogs are scrollable on mobile
- [ ] Tables scroll horizontally on mobile

**Dependencies**: Requires FE-020

---

## Phase 8: Documentation and Handoff

### [FE-023] Create Frontend README
**Goal**: Documentation for running and building frontend.

**Technical Details**:
- **File**: `frontend/README.md`

**Sections**:
- Prerequisites (Node 20+, npm)
- Installation (`npm install`)
- Development server (`npm run dev`)
- Production build (`npm run build`)
- Environment variables
- Project structure
- Reference code mapping
- Testing instructions

**Dependencies**: Requires FE-021

---

### [FE-024] Verify Reference Code Alignment
**Goal**: Ensure UI matches reference 1:1.

**Checklist**:
- [ ] All Tailwind classes preserved
- [ ] All component structures identical
- [ ] All colors match reference
- [ ] All spacing matches reference
- [ ] All fonts match reference
- [ ] No custom CSS added
- [ ] All UI primitives copied from reference
- [ ] Forms use same layout as reference

**Dependencies**: Requires FE-022

---

## Summary

**Total Tasks**: 24
**Estimated Effort**: 5-7 days (1 developer)
**Critical Path**: FE-001 → FE-003 → FE-006 → FE-009 → FE-010 → FE-015 → FE-020

**Key Deliverables**:
1. React SPA with Vite (port 5173)
2. All UI components copied from reference-code
3. Full backend integration via Axios
4. Form validation with Zod
5. Toast notifications with Sonner
6. Docker-ready production build
7. Comprehensive testing results

**Success Criteria**:
- UI mirrors reference code 1:1
- All CRUD operations functional
- Forms validate correctly
- API integration working
- No console errors
- Responsive design verified
- Docker build succeeds
- Documentation complete

**VALIDATION CHECKLIST**:
- [ ] Zero new UI components created
- [ ] Zero custom CSS written
- [ ] All components copied from reference-code
- [ ] All API integrations working
- [ ] All forms validating with Zod
- [ ] All toast notifications working
- [ ] All routes functioning
- [ ] All cross-browser tests passing
- [ ] All responsive tests passing
