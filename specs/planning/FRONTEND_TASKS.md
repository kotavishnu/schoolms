# Frontend Implementation Plan - School Management System

## Overview
Sequential task checklist for React Developer to build the School Management System UI by reusing Figma-generated reference code and integrating with backend APIs.

**Execution Model:** Waterfall / Single-Pass Implementation
**Technology Stack:** React 18+, TypeScript, Tailwind CSS v4, shadcn/ui, React Hook Form, Zod, React Query
**Source of Truth:** `@specs/architecture/06-frontend-implementation-guide.md`, `@specs/FRONTEND_DESIGN_SPEC.md`, `@frontend/reference-code/`

**CRITICAL CONSTRAINT:** The code in `frontend/reference-code/` is the **authoritative implementation** of the UI. It MUST be reused. NO new UI components, styles, or layouts should be created from scratch.

---

## Phase 1: Project Setup & Reference Code Migration

### [FE-001] Initialize React Project
**Goal:** Create production-ready React + TypeScript + Vite project.

**Technical Details:**
- Use Vite for fast development and optimized builds
- Initialize with: `npm create vite@latest school-management-frontend -- --template react-ts`
- Project structure:
  ```
  frontend/
  ├── src/
  ├── public/
  ├── reference-code/  (existing - read only)
  └── package.json
  ```

**Dependencies:** None

**Acceptance Criteria:**
- [ ] Project builds with `npm run build`
- [ ] Development server starts with `npm run dev`
- [ ] TypeScript strict mode enabled
- [ ] ESLint configured for React + TypeScript
- [ ] No build warnings or errors

---

### [FE-002] Install Required Dependencies
**Goal:** Add all necessary libraries for functionality and design system.

**Technical Details:**
Install dependencies:
```json
{
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-router-dom": "^6.x",
    "react-hook-form": "^7.x",
    "zod": "^3.x",
    "@hookform/resolvers": "^3.x",
    "@tanstack/react-query": "^5.x",
    "axios": "^1.x",
    "@radix-ui/react-dialog": "^1.x",
    "@radix-ui/react-select": "^2.x",
    "@radix-ui/react-toast": "^1.x",
    "lucide-react": "latest",
    "sonner": "^1.x",
    "class-variance-authority": "^0.7.x",
    "clsx": "^2.x",
    "tailwind-merge": "^2.x"
  },
  "devDependencies": {
    "@types/react": "^18.3.x",
    "@types/react-dom": "^18.3.x",
    "tailwindcss": "^4.x",
    "typescript": "^5.x",
    "vite": "^6.x"
  }
}
```

**Dependencies:** FE-001

**Acceptance Criteria:**
- [ ] All dependencies install without conflicts
- [ ] `npm run dev` starts successfully
- [ ] TypeScript types resolve for all libraries
- [ ] Tailwind CSS v4 installed (check version)

---

### [FE-003] Copy Reference Styles to Project
**Goal:** Migrate Figma design tokens from reference code to project.

**Technical Details:**
**MANDATORY FILE COPIES** (exact 1:1 copy, NO modifications):
1. Copy `frontend/reference-code/styles/theme.css` → `src/styles/theme.css`
2. Copy `frontend/reference-code/styles/tailwind.css` → `src/styles/tailwind.css`
3. Copy `frontend/reference-code/styles/index.css` → `src/styles/index.css`

Import in `src/main.tsx`:
```typescript
import './styles/index.css';
import './styles/tailwind.css';
import './styles/theme.css';
```

**FORBIDDEN ACTIONS:**
- ❌ Creating new CSS variables
- ❌ Modifying color values
- ❌ Changing spacing or typography tokens

**Dependencies:** FE-002

**Acceptance Criteria:**
- [ ] `theme.css` copied with all CSS variables intact
- [ ] `tailwind.css` imports Tailwind v4 correctly
- [ ] Styles render correctly in browser
- [ ] CSS variables accessible in DevTools (check `--primary`, `--background`)
- [ ] No custom CSS files created

---

### [FE-004] Copy UI Component Library (shadcn/ui)
**Goal:** Migrate all reusable UI primitives from reference code.

**Technical Details:**
**MANDATORY DIRECTORY COPY** (entire folder, NO modifications):
- Copy `frontend/reference-code/app/components/ui/` → `src/components/ui/`

This includes:
- `button.tsx`, `input.tsx`, `label.tsx`, `textarea.tsx`
- `select.tsx`, `dialog.tsx`, `card.tsx`, `table.tsx`
- `toast.tsx`, `sonner.tsx`, `alert.tsx`, `badge.tsx`
- `form.tsx`, `checkbox.tsx`, `switch.tsx`
- `utils.ts` (cn helper function)
- ALL other UI components in the folder

**FORBIDDEN ACTIONS:**
- ❌ Creating new UI components
- ❌ Modifying component styles or structure
- ❌ Removing any components

**Dependencies:** FE-003

**Acceptance Criteria:**
- [ ] All UI components copied to `src/components/ui/`
- [ ] Components import successfully (no TypeScript errors)
- [ ] `cn()` utility function works for class merging
- [ ] Button renders with correct Tailwind classes
- [ ] Dialog/Modal components functional

---

### [FE-005] Copy Feature Components (Students & Configuration)
**Goal:** Migrate feature-specific UI components from reference code.

**Technical Details:**
**MANDATORY FILE COPIES:**
1. **Students Module:**
   - Copy `reference-code/app/components/StudentsPage.tsx` → `src/features/students/pages/StudentsPage.tsx`
   - Copy `reference-code/app/components/StudentDialog.tsx` → `src/features/students/components/StudentDialog.tsx`
   - Copy `reference-code/app/components/ViewStudentDialog.tsx` → `src/features/students/components/ViewStudentDialog.tsx`

2. **Configuration Module:**
   - Copy `reference-code/app/components/ConfigurationsPage.tsx` → `src/features/configuration/pages/ConfigurationsPage.tsx`
   - Copy `reference-code/app/components/ConfigurationDialog.tsx` → `src/features/configuration/components/ConfigurationDialog.tsx`

3. **Layout Components:**
   - Copy `reference-code/app/components/Header.tsx` → `src/components/layout/Header.tsx`
   - Copy `reference-code/app/components/HomePage.tsx` → `src/features/home/HomePage.tsx`

**FORBIDDEN ACTIONS:**
- ❌ Creating new page layouts
- ❌ Redesigning component structure
- ❌ Changing JSX hierarchy

**Dependencies:** FE-004

**Acceptance Criteria:**
- [ ] All feature components copied to correct directories
- [ ] Import paths updated (adjust relative imports to absolute)
- [ ] Components render without errors
- [ ] Visual layout matches reference screenshots
- [ ] NO custom components created

---

### [FE-006] Copy Type Definitions
**Goal:** Migrate TypeScript types from reference code.

**Technical Details:**
**MANDATORY FILE COPY:**
- Copy `reference-code/app/types/index.ts` → `src/types/reference.types.ts`

**Then CREATE NEW** `src/types/api.types.ts` to align with backend API:
```typescript
// Aligned with OpenAPI specification
export interface StudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;  // Note: API uses fathersName
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;  // Note: API uses aadhaarNumber (not adhaarNumber)
}

export interface StudentResponse extends StudentRequest {
  id: number;
  studentId: string; // e.g., "STD-20260203-0001"
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
  createdAt: string;
  updatedAt: string;
}

// Reference type mapping
export interface Student {  // Maps to StudentResponse
  id: string;  // Maps to studentId
  firstName: string;
  lastName: string;
  guardianName: string;  // Maps to fathersName
  motherName: string;    // Maps to mothersName
  phone: string;         // Maps to mobile
  age: number;           // Calculated from dateOfBirth
  email: string;
  address: string;
  identificationMarks: string; // Maps to identificationMark
  status: 'ACTIVE' | 'INACTIVE';
}
```

**Dependencies:** FE-005

**Acceptance Criteria:**
- [ ] Reference types copied unchanged
- [ ] API types match OpenAPI specification exactly
- [ ] Mapping documented between reference types and API types
- [ ] TypeScript compiles without errors
- [ ] Field name mismatches identified (guardianName → fathersName, phone → mobile)

---

## Phase 2: API Integration Layer

### [FE-007] Create Axios Configuration
**Goal:** Set up HTTP client for backend communication.

**Technical Details:**
Create `src/services/api/axios.config.ts`:
```typescript
import axios, { AxiosInstance } from 'axios';

const STUDENT_API_BASE_URL = import.meta.env.VITE_STUDENT_API_URL || 'http://localhost:8081/api/v1';
const CONFIG_API_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082/api/v1';

export const studentApiClient: AxiosInstance = axios.create({
  baseURL: STUDENT_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const configApiClient: AxiosInstance = axios.create({
  baseURL: CONFIG_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});
```

Create `.env.local`:
```
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

**Dependencies:** FE-002

**Acceptance Criteria:**
- [ ] Axios instances created for both services
- [ ] Base URLs configured from environment variables
- [ ] Timeout set to 10 seconds
- [ ] Default Content-Type header set

---

### [FE-008] Implement Request/Response Interceptors
**Goal:** Add global error handling and correlation ID propagation.

**Technical Details:**
Create `src/services/api/interceptors.ts`:
```typescript
import { toast } from 'sonner';

// Request Interceptor
export const requestInterceptor = (config: InternalAxiosRequestConfig) => {
  const correlationId = crypto.randomUUID();
  config.headers['X-Correlation-ID'] = correlationId;
  return config;
};

// Response Error Interceptor
export const errorInterceptor = (error: AxiosError<ErrorResponse>) => {
  const { response } = error;

  if (!response) {
    toast.error('Network Error', { description: 'Unable to connect to server' });
    return Promise.reject(error);
  }

  switch (response.status) {
    case 400:
      toast.error('Validation Error', { description: response.data?.detail });
      break;
    case 404:
      toast.error('Not Found', { description: response.data?.detail });
      break;
    case 409:
      toast.error('Conflict', { description: 'Resource was modified by another user' });
      break;
    case 500:
      toast.error('Server Error', { description: 'Please try again later' });
      break;
  }

  return Promise.reject(error);
};

// Attach to clients
studentApiClient.interceptors.request.use(requestInterceptor);
studentApiClient.interceptors.response.use(response => response, errorInterceptor);
```

**Dependencies:** FE-007

**Acceptance Criteria:**
- [ ] Correlation ID auto-generated for each request
- [ ] Toast notifications show on API errors
- [ ] Error messages display backend error details
- [ ] Network errors handled gracefully
- [ ] 409 Conflict shows optimistic locking message

---

### [FE-009] Create Student API Service
**Goal:** Implement service layer for all student operations.

**Technical Details:**
Create `src/services/api/student.api.ts`:
```typescript
import { studentApiClient } from './axios.config';
import { StudentRequest, StudentResponse, StudentUpdateRequest } from '@/types/api.types';

export const studentService = {
  getAllStudents: async (params?: {
    lastName?: string;
    status?: 'ACTIVE' | 'INACTIVE';
    page?: number;
    size?: number;
  }): Promise<{ content: StudentResponse[], pageable: any }> => {
    const response = await studentApiClient.get('/students', { params });
    return response.data;
  },

  getStudentById: async (studentId: string): Promise<StudentResponse> => {
    const response = await studentApiClient.get(`/students/${studentId}`);
    return response.data;
  },

  createStudent: async (data: StudentRequest): Promise<StudentResponse> => {
    const response = await studentApiClient.post('/students', data);
    return response.data;
  },

  updateStudent: async (studentId: string, data: StudentUpdateRequest): Promise<StudentResponse> => {
    const response = await studentApiClient.put(`/students/${studentId}`, data);
    return response.data;
  },

  deleteStudent: async (studentId: string): Promise<void> => {
    await studentApiClient.delete(`/students/${studentId}`);
  },
};
```

**FORBIDDEN ACTIONS:**
- ❌ Direct Axios calls in components
- ❌ Bypassing the service layer

**Dependencies:** FE-008, FE-006

**Acceptance Criteria:**
- [ ] All CRUD methods implemented
- [ ] Methods typed with API types
- [ ] Service returns unwrapped data (not AxiosResponse)
- [ ] Pagination parameters passed correctly
- [ ] NO Axios calls exist in component files

---

### [FE-010] Create Configuration API Service
**Goal:** Implement service layer for configuration operations.

**Technical Details:**
Create `src/services/api/configuration.api.ts`:
```typescript
import { configApiClient } from './axios.config';

export const configurationService = {
  getGroupedSettings: async (category: 'GENERAL' | 'ACADEMIC' | 'FINANCIAL'): Promise<Record<string, string>> => {
    const response = await configApiClient.get(`/configurations/grouped/${category}`);
    return response.data;
  },

  getAllConfigurations: async (): Promise<Configuration[]> => {
    const response = await configApiClient.get('/configurations');
    return response.data;
  },

  createConfiguration: async (data: ConfigurationRequest): Promise<Configuration> => {
    const response = await configApiClient.post('/configurations', data);
    return response.data;
  },

  updateConfiguration: async (id: number, data: ConfigurationRequest): Promise<Configuration> => {
    const response = await configApiClient.put(`/configurations/${id}`, data);
    return response.data;
  },

  deleteConfiguration: async (id: number): Promise<void> => {
    await configApiClient.delete(`/configurations/${id}`);
  },
};
```

**Dependencies:** FE-008

**Acceptance Criteria:**
- [ ] Grouped settings return key-value map
- [ ] All CRUD methods implemented
- [ ] Service layer prevents direct API calls in components

---

## Phase 3: Form Validation & State Management

### [FE-011] Create Zod Validation Schemas
**Goal:** Define validation rules matching backend constraints.

**Technical Details:**
Create `src/schemas/studentSchema.ts`:
```typescript
import { z } from 'zod';

export const studentRegistrationSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name cannot exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name can only contain letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name cannot exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name can only contain letters and spaces'),

  dateOfBirth: z
    .string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, 'Invalid date format (use YYYY-MM-DD)')
    .refine((date) => {
      const age = calculateAge(new Date(date));
      return age >= 3 && age <= 18;
    }, 'Student age must be between 3 and 18 years'),

  mobile: z.string().regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits'),

  email: z.string().email('Invalid email format').optional().or(z.literal('')),

  address: z.string().optional(),

  fathersName: z.string().optional(),

  mothersName: z.string().optional(),

  identificationMark: z.string().optional(),

  aadhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar number must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
}).refine(
  (data) => data.fathersName || data.mothersName,
  { message: 'At least one guardian name is required', path: ['fathersName'] }
);

export type StudentRegistrationFormData = z.infer<typeof studentRegistrationSchema>;

function calculateAge(birthDate: Date): number {
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  return age;
}
```

**CRITICAL ALIGNMENT:**
- [ ] All rules match backend validation (BR-1, BR-2, BR-5)
- [ ] Error messages consistent with backend

**Dependencies:** FE-002

**Acceptance Criteria:**
- [ ] Schema validates age between 3-18 years
- [ ] Mobile format enforced (10 digits)
- [ ] Guardian name requirement enforced
- [ ] Email validation matches backend regex
- [ ] Aadhaar format validated (12 digits)

---

### [FE-012] Create React Query Hooks
**Goal:** Implement data fetching hooks with caching and mutations.

**Technical Details:**
Create `src/hooks/useStudents.ts`:
```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentService } from '@/services/api/student.api';
import { toast } from 'sonner';

export function useStudents(params?: { lastName?: string; status?: 'ACTIVE' | 'INACTIVE'; page?: number; size?: number }) {
  return useQuery({
    queryKey: ['students', params],
    queryFn: () => studentService.getAllStudents(params),
    keepPreviousData: true,
  });
}

export function useStudent(studentId: string) {
  return useQuery({
    queryKey: ['students', studentId],
    queryFn: () => studentService.getStudentById(studentId),
    enabled: !!studentId,
  });
}

export function useCreateStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: StudentRequest) => studentService.createStudent(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      toast.success('Student registered successfully');
    },
  });
}

export function useUpdateStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ studentId, data }: { studentId: string; data: StudentUpdateRequest }) =>
      studentService.updateStudent(studentId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      queryClient.invalidateQueries({ queryKey: ['students', variables.studentId] });
      toast.success('Student updated successfully');
    },
  });
}

export function useDeleteStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (studentId: string) => studentService.deleteStudent(studentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      toast.success('Student deleted successfully');
    },
  });
}
```

**Dependencies:** FE-009

**Acceptance Criteria:**
- [ ] useQuery caches student data
- [ ] Mutations invalidate cache on success
- [ ] Toast notifications on success/error
- [ ] Loading and error states exposed
- [ ] Pagination works with keepPreviousData

---

## Phase 4: Component Integration (Refactor Only - NO New UI)

### [FE-013] Refactor StudentsPage to Use Real API
**Goal:** Replace mock data with API integration while keeping UI identical.

**Technical Details:**
Modify `src/features/students/pages/StudentsPage.tsx`:

**ALLOWED CHANGES ONLY:**
1. Replace `initialStudents` mock data:
   ```typescript
   // ❌ REMOVE
   const [students, setStudents] = useState<Student[]>(initialStudents);

   // ✅ ADD
   const { data, isLoading } = useStudents({ lastName: searchLastName, status: statusFilter !== 'all' ? statusFilter : undefined });
   const students = data?.content || [];
   ```

2. Replace `handleCreate` mock implementation:
   ```typescript
   // ❌ REMOVE
   const handleCreate = (student: Omit<Student, 'id'>) => { ... };

   // ✅ ADD
   const createMutation = useCreateStudent();
   const handleCreate = async (student: Omit<Student, 'id'>) => {
     const apiData: StudentRequest = {
       firstName: student.firstName,
       lastName: student.lastName,
       dateOfBirth: new Date().toISOString().split('T')[0], // Calculate from age
       mobile: student.phone,
       email: student.email,
       address: student.address,
       fathersName: student.guardianName,
       mothersName: student.motherName,
       identificationMark: student.identificationMarks,
     };
     await createMutation.mutateAsync(apiData);
   };
   ```

3. Similar refactoring for `handleUpdate`, `handleDelete`

**FORBIDDEN CHANGES:**
- ❌ Changing JSX structure
- ❌ Modifying CSS classes
- ❌ Altering component layout
- ❌ Adding new UI elements

**Dependencies:** FE-012, FE-005

**Acceptance Criteria:**
- [ ] Students load from API instead of mock data
- [ ] Search filters trigger API queries
- [ ] Create/Update/Delete use mutations
- [ ] UI appearance unchanged from reference
- [ ] JSX structure identical to reference code
- [ ] Loading states display during API calls

---

### [FE-014] Refactor StudentDialog Form to Use React Hook Form + Zod
**Goal:** Replace form logic with validated form handling while keeping UI identical.

**Technical Details:**
Modify `src/features/students/components/StudentDialog.tsx`:

**ALLOWED CHANGES ONLY:**
1. Add React Hook Form integration:
   ```typescript
   import { useForm } from 'react-hook-form';
   import { zodResolver } from '@hookform/resolvers/zod';
   import { studentRegistrationSchema } from '@/schemas/studentSchema';

   const { register, handleSubmit, formState: { errors }, reset } = useForm({
     resolver: zodResolver(studentRegistrationSchema),
     defaultValues: student || {},
   });
   ```

2. Replace input bindings:
   ```typescript
   // ❌ REMOVE
   <Input value={firstName} onChange={e => setFirstName(e.target.value)} />

   // ✅ ADD
   <Input {...register('firstName')} />
   {errors.firstName && <p className="text-sm text-destructive">{errors.firstName.message}</p>}
   ```

3. Replace onSubmit handler:
   ```typescript
   const onSubmit = handleSubmit(async (data) => {
     await onSubmitProp(data);
     reset();
   });
   ```

**FORBIDDEN CHANGES:**
- ❌ Changing input layout or styling
- ❌ Adding new form fields
- ❌ Modifying dialog structure

**Dependencies:** FE-011, FE-005

**Acceptance Criteria:**
- [ ] Form validates with Zod schema before submission
- [ ] Validation errors display below inputs
- [ ] Form resets after successful submission
- [ ] UI appearance unchanged
- [ ] All required fields enforced
- [ ] Age validation triggered on date change

---

### [FE-015] Refactor ConfigurationsPage to Use Real API
**Goal:** Replace mock configuration data with API integration.

**Technical Details:**
Modify `src/features/configuration/pages/ConfigurationsPage.tsx`:

**ALLOWED CHANGES ONLY:**
1. Replace mock data with React Query:
   ```typescript
   const { data: configurations, isLoading } = useQuery({
     queryKey: ['configurations'],
     queryFn: () => configurationService.getAllConfigurations(),
   });
   ```

2. Replace grouped settings:
   ```typescript
   const { data: generalSettings } = useQuery({
     queryKey: ['configurations', 'grouped', 'GENERAL'],
     queryFn: () => configurationService.getGroupedSettings('GENERAL'),
   });
   ```

3. Implement mutations for create/update/delete

**FORBIDDEN CHANGES:**
- ❌ Changing UI layout
- ❌ Modifying category grouping display
- ❌ Altering component structure

**Dependencies:** FE-010, FE-005

**Acceptance Criteria:**
- [ ] Configurations load from API
- [ ] Grouped settings display correctly
- [ ] Category filtering works
- [ ] Create/Update/Delete use API
- [ ] UI matches reference code exactly

---

### [FE-016] Integrate Toast Notifications (Sonner)
**Goal:** Add global toast provider for success/error feedback.

**Technical Details:**
Modify `src/App.tsx`:
```typescript
import { Toaster } from 'sonner';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      <Toaster position="top-right" />
    </QueryClientProvider>
  );
}
```

All toast calls are already implemented in:
- Interceptors (FE-008)
- React Query hooks (FE-012)

**Dependencies:** FE-002, FE-012

**Acceptance Criteria:**
- [ ] Toast notifications appear on API success
- [ ] Toast notifications appear on API errors
- [ ] Toasts auto-dismiss after 3 seconds
- [ ] Position is top-right corner
- [ ] Toasts show validation errors from backend

---

### [FE-017] Setup React Router
**Goal:** Configure client-side routing for navigation.

**Technical Details:**
Create `src/router/index.tsx`:
```typescript
import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/features/home/HomePage';
import StudentsPage from '@/features/students/pages/StudentsPage';
import ConfigurationsPage from '@/features/configuration/pages/ConfigurationsPage';
import MainLayout from '@/components/layout/MainLayout';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'students', element: <StudentsPage /> },
      { path: 'configurations', element: <ConfigurationsPage /> },
    ],
  },
]);
```

Create `src/components/layout/MainLayout.tsx`:
```typescript
import { Outlet } from 'react-router-dom';
import Header from './Header';

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main>
        <Outlet />
      </main>
    </div>
  );
}
```

**Dependencies:** FE-005

**Acceptance Criteria:**
- [ ] Navigation between pages works
- [ ] Header component displays on all pages
- [ ] URL updates on navigation
- [ ] Browser back/forward buttons work
- [ ] Layout structure matches reference

---

### [FE-018] Implement Debounced Search
**Goal:** Optimize search performance with debouncing.

**Technical Details:**
Create `src/hooks/useDebounce.ts`:
```typescript
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number = 500): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
```

Use in `StudentsPage.tsx`:
```typescript
const [searchLastName, setSearchLastName] = useState('');
const debouncedSearch = useDebounce(searchLastName, 500);

const { data } = useStudents({ lastName: debouncedSearch });
```

**Dependencies:** FE-013

**Acceptance Criteria:**
- [ ] Search queries debounced by 500ms
- [ ] API calls reduced during typing
- [ ] Search results update after user stops typing
- [ ] No lag in input field

---

## Phase 5: Production Readiness

### [FE-019] Configure Environment Variables
**Goal:** Externalize API URLs for different environments.

**Technical Details:**
Create `.env.development`:
```
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

Create `.env.production`:
```
VITE_STUDENT_API_URL=https://api.school.com/students/api/v1
VITE_CONFIG_API_URL=https://api.school.com/config/api/v1
```

**Dependencies:** FE-007

**Acceptance Criteria:**
- [ ] Environment variables load in Vite
- [ ] API URLs switch based on environment
- [ ] Production build uses production URLs
- [ ] No hardcoded URLs in codebase

---

### [FE-020] Create Docker Configuration
**Goal:** Containerize frontend application with Nginx.

**Technical Details:**
Create `Dockerfile`:
```dockerfile
# Build stage
FROM node:20-alpine AS build

WORKDIR /app
COPY package*.json ./
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

Create `nginx.conf`:
```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/v1/students {
        proxy_pass http://student-service:8081;
    }

    location /api/v1/configurations {
        proxy_pass http://config-service:8082;
    }
}
```

**Dependencies:** FE-001

**Acceptance Criteria:**
- [ ] Docker image builds successfully
- [ ] Nginx serves static files
- [ ] API requests proxied to backend services
- [ ] SPA routing works (refresh on /students doesn't 404)
- [ ] Container starts with `docker run`

---

### [FE-021] Add Error Boundaries
**Goal:** Catch and display component errors gracefully.

**Technical Details:**
Create `src/components/shared/ErrorBoundary.tsx`:
```typescript
import React, { Component, ErrorInfo, ReactNode } from 'react';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = { hasError: false };

  public static getDerivedStateFromError(): State {
    return { hasError: true };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error:', error, errorInfo);
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <h2 className="text-2xl font-bold mb-4">Something went wrong</h2>
            <button
              onClick={() => this.setState({ hasError: false })}
              className="px-4 py-2 bg-primary text-white rounded"
            >
              Try again
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
```

Wrap App in `main.tsx`:
```typescript
<ErrorBoundary>
  <App />
</ErrorBoundary>
```

**Dependencies:** FE-004

**Acceptance Criteria:**
- [ ] Error boundary catches component errors
- [ ] Fallback UI displays on error
- [ ] User can retry after error
- [ ] Errors logged to console
- [ ] App doesn't crash on component error

---

### [FE-022] Implement Loading States
**Goal:** Display loading spinners during API calls.

**Technical Details:**
Create `src/components/shared/LoadingSpinner.tsx`:
```typescript
export function LoadingSpinner() {
  return (
    <div className="flex items-center justify-center p-8">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>
  );
}
```

Use in pages:
```typescript
export function StudentsPage() {
  const { data, isLoading } = useStudents();

  if (isLoading) return <LoadingSpinner />;

  return (/* ... */);
}
```

**Dependencies:** FE-004

**Acceptance Criteria:**
- [ ] Loading spinner displays during API calls
- [ ] Spinner centered on page
- [ ] Animation smooth (CSS transition)
- [ ] Loading state cleared after data loads

---

### [FE-023] Add Responsive Design Verification
**Goal:** Ensure UI works on mobile, tablet, and desktop.

**Technical Details:**
Test breakpoints from reference code:
- Mobile: < 640px
- Tablet: 640px - 1024px
- Desktop: > 1024px

Verify all components from reference code work at each breakpoint:
- Header navigation collapses on mobile
- Student cards stack vertically on mobile
- Tables scroll horizontally on mobile
- Dialogs adapt to screen size

**NO NEW CSS** - Reference code already includes responsive classes:
- `md:grid-cols-2` (tablet)
- `lg:grid-cols-3` (desktop)
- `sm:px-6` (small screens)

**Dependencies:** FE-003, FE-005

**Acceptance Criteria:**
- [ ] UI tested on 375px width (mobile)
- [ ] UI tested on 768px width (tablet)
- [ ] UI tested on 1440px width (desktop)
- [ ] All reference components responsive
- [ ] NO custom media queries added

---

### [FE-024] Optimize Build for Production
**Goal:** Minimize bundle size and improve performance.

**Technical Details:**
Update `vite.config.ts`:
```typescript
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['@radix-ui/react-dialog', '@radix-ui/react-select'],
          'form-vendor': ['react-hook-form', 'zod', '@hookform/resolvers'],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
});
```

**Dependencies:** FE-001

**Acceptance Criteria:**
- [ ] Build completes without warnings
- [ ] Vendor chunks created for libraries
- [ ] Lazy loading for route components
- [ ] Bundle size < 500KB gzipped
- [ ] Lighthouse score > 90 for performance

---

## Phase 6: Testing & Validation

### [FE-025] Manual Testing Checklist
**Goal:** Verify all user flows work end-to-end.

**Test Scenarios:**
1. **Student Registration:**
   - [ ] Fill form with valid data → Success toast
   - [ ] Submit with age < 3 → Validation error
   - [ ] Submit with duplicate mobile → API error toast
   - [ ] Submit without guardian name → Validation error

2. **Student Search:**
   - [ ] Search by last name → Results filter
   - [ ] Change status filter → Results update
   - [ ] Pagination works (if > 20 students)

3. **Student Update:**
   - [ ] Edit student → Changes saved
   - [ ] Update with wrong version → 409 Conflict toast

4. **Student Delete:**
   - [ ] Delete student → Confirmation dialog
   - [ ] Confirm delete → Student removed

5. **Configuration Management:**
   - [ ] View grouped settings → Correct category display
   - [ ] Create configuration → Success toast
   - [ ] Update configuration → Cache invalidated

**Dependencies:** FE-013, FE-014, FE-015

**Acceptance Criteria:**
- [ ] All test scenarios pass
- [ ] No console errors
- [ ] Toast notifications appear correctly
- [ ] Loading states display
- [ ] Error states handled gracefully

---

### [FE-026] Visual Regression Check
**Goal:** Ensure UI matches reference code exactly.

**Technical Details:**
Compare rendered UI against reference screenshots:
1. Take screenshots of reference code pages
2. Take screenshots of implemented pages
3. Visual diff using image comparison tool

**Pages to Compare:**
- Home Page
- Students Page (empty state)
- Students Page (with data)
- Student Dialog (create mode)
- Student Dialog (edit mode)
- Configurations Page

**Dependencies:** FE-023

**Acceptance Criteria:**
- [ ] Visual diff shows < 1% difference
- [ ] Colors match exactly
- [ ] Spacing matches exactly
- [ ] Typography matches exactly
- [ ] Component layout identical

---

## Completion Checklist

### Reference Code Migration
- [ ] All UI components copied from reference-code/
- [ ] All styles copied from reference-code/styles/
- [ ] NO new UI components created
- [ ] NO custom CSS written
- [ ] Visual appearance matches reference 1:1

### API Integration
- [ ] Student CRUD operations functional
- [ ] Configuration CRUD operations functional
- [ ] Service layer prevents direct Axios calls in components
- [ ] Toast notifications on all API interactions
- [ ] Error handling for all HTTP status codes

### Form Validation
- [ ] Zod schemas match backend validation rules
- [ ] React Hook Form integrated in all forms
- [ ] Validation errors display below inputs
- [ ] Guardian name requirement enforced
- [ ] Age validation (3-18 years) enforced

### State Management
- [ ] React Query caches API responses
- [ ] Mutations invalidate cache on success
- [ ] Loading states displayed during API calls
- [ ] Optimistic updates not implemented (simple invalidation only)

### Production Readiness
- [ ] Docker build passes successfully
- [ ] Environment variables configured
- [ ] Nginx reverse proxy configured
- [ ] Error boundaries catch component crashes
- [ ] Responsive design works on mobile/tablet/desktop

### Performance
- [ ] Search debounced by 500ms
- [ ] Route-based code splitting
- [ ] Vendor chunks separated
- [ ] Bundle size < 500KB gzipped
- [ ] Lighthouse performance score > 90

---

**Total Tasks:** 26
**Estimated Effort:** 40-60 developer hours
**Critical Path:** FE-001 → FE-003 → FE-005 → FE-009 → FE-013 → FE-025

---

**Document Version:** 1.0
**Last Updated:** 2026-02-03
**Owner:** Technical SDLC Planner Agent
