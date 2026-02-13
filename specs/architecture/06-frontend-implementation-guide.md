# 06 - Frontend Implementation Guide

## Cross-Reference Index
- Frontend Design Tokens: `specs/FRONTEND_DESIGN_SPEC.md` (Figma token system, Tailwind v4)
- API Contracts: `specs/sms_api_specification.yaml` (All schemas and paths)
- System Architecture: `specs/architecture/01-system-architecture.md` (Service URLs)
- Testing Strategy: `specs/TESTING_STRATEGY.md` (Vitest, Playwright, MSW)
- Visual Regression: `specs/visual-regression-mapping.json`

---

## STRICT CONSTRAINTS

1. NO NEW STYLES: Custom CSS, SASS, or styled-components are forbidden. All styling must use Tailwind utility classes from the token system defined in `specs/FRONTEND_DESIGN_SPEC.md`.
2. REFERENCE CODE ONLY: All UI components, layouts, and classes must be copied exactly from the reference code in `frontend/reference-code/`. Do not invent new component structures.
3. SEMANTIC TOKEN USAGE: Never use hardcoded color values (e.g., `bg-[#3b82f6]`). Always reference semantic tokens (e.g., `bg-primary`).

---

## 1. Project Structure

```
frontend/
  src/
    components/
      ui/                      (shadcn/ui components - from reference-code)
        button.tsx
        card.tsx
        dialog.tsx
        input.tsx
        label.tsx
        select.tsx
        table.tsx
        toast.tsx
        badge.tsx
      students/
        StudentListPage.tsx
        StudentTable.tsx
        StudentRegisterDialog.tsx
        StudentEditDialog.tsx
        StudentViewDialog.tsx
        StudentSearchBar.tsx
      configurations/
        ConfigurationListPage.tsx
        ConfigurationTable.tsx
        ConfigurationAddDialog.tsx
        ConfigurationEditDialog.tsx
      layout/
        AppLayout.tsx
        Sidebar.tsx
        Header.tsx
    services/
      api.ts                   (Axios instance with interceptors)
      studentService.ts        (Student API calls)
      configurationService.ts  (Configuration API calls)
    hooks/
      useStudents.ts
      useStudentForm.ts
      useConfigurations.ts
      useConfigurationForm.ts
      useToast.ts
    context/
      ToastContext.tsx
    schemas/
      studentSchema.ts         (Zod validation)
      configurationSchema.ts
    types/
      student.ts               (TypeScript interfaces matching API)
      configuration.ts
      api.ts                   (Generic API types: PagedResponse, ErrorResponse)
    pages/
      HomePage.tsx
      StudentsPage.tsx
      ConfigurationsPage.tsx
    styles/
      theme.css                (Figma tokens: primitives + semantic mapping)
      tailwind.css             (Tailwind v4 import)
  public/
  index.html
  vite.config.ts
  tailwind.config.ts           (Tailwind v4 configuration)
  tsconfig.json
  Dockerfile
  docker-compose.yml
```

---

## 2. TypeScript Type Definitions

These types directly mirror `specs/sms_api_specification.yaml` schemas.

```typescript
// src/types/student.ts
export type StudentStatus = 'ACTIVE' | 'INACTIVE';

export interface StudentBase {
  firstName: string;
  lastName: string;
  dateOfBirth: string;       // ISO date: "YYYY-MM-DD"
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
}

export interface StudentResponse extends StudentBase {
  id: number;
  studentId: string;          // "STD-YYYYMMDD-NNNN"
  status: StudentStatus;
  version: number;
  createdAt: string;          // ISO-8601 datetime
  updatedAt: string;
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: StudentStatus;
  version: number;            // Required for optimistic locking
}

export interface PagedStudentResponse {
  content: StudentResponse[];
  pageable: PaginationMetadata;
}
```

```typescript
// src/types/configuration.ts
export type ConfigurationCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';
export type ConfigurationDataType = 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';

export interface Configuration {
  id: number;
  category: ConfigurationCategory;
  key: string;
  value: string;
  description?: string;
  dataType: ConfigurationDataType;
  isEncrypted: boolean;
  version: number;
  updatedAt: string;
}

export interface UpsertConfigurationRequest {
  value: string;
  description?: string;
  dataType: ConfigurationDataType;
  isEncrypted: boolean;
}

export interface GroupedConfigurationResponse {
  category: ConfigurationCategory;
  settings: Record<string, string>;
}
```

```typescript
// src/types/api.ts
export interface PaginationMetadata {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FieldError {
  field: string;
  message: string;
  code: string;
}

export interface ErrorResponse {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp: string;
  correlationId?: string;
  errors: FieldError[];
}
```

---

## 3. Zod Validation Schemas

Zod schemas mirror backend Jakarta Bean Validation logic exactly.

```typescript
// src/schemas/studentSchema.ts
import { z } from 'zod';

const today = new Date();
const minDob = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
const maxDob = new Date(today.getFullYear() - 3, today.getMonth(), today.getDate());

export const createStudentSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name must not exceed 100 characters')
    .regex(/^[a-zA-Z ]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name must not exceed 100 characters')
    .regex(/^[a-zA-Z ]+$/, 'Last name must contain only letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((val) => {
      const dob = new Date(val);
      return dob >= minDob && dob <= maxDob;
    }, { message: 'Student age must be between 3 and 18 years' }),

  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),

  email: z.string().email('Invalid email address').optional().or(z.literal('')),

  address: z.string().optional(),
  fathersName: z.string().optional(),
  mothersName: z.string().optional(),
  identificationMark: z.string().optional(),

  aadhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export const updateStudentSchema = z.object({
  firstName: z
    .string()
    .min(2).max(100)
    .regex(/^[a-zA-Z ]+$/),
  lastName: z
    .string()
    .min(2).max(100)
    .regex(/^[a-zA-Z ]+$/),
  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),
  status: z.enum(['ACTIVE', 'INACTIVE']),
  version: z.number().int(),
});

export type CreateStudentFormData = z.infer<typeof createStudentSchema>;
export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;
```

```typescript
// src/schemas/configurationSchema.ts
import { z } from 'zod';

export const upsertConfigurationSchema = z.object({
  value: z.string().min(1, 'Value is required'),
  description: z.string().optional(),
  dataType: z.enum(['STRING', 'NUMBER', 'BOOLEAN', 'JSON']),
  isEncrypted: z.boolean().default(false),
});

export type UpsertConfigurationFormData = z.infer<typeof upsertConfigurationSchema>;
```

---

## 4. Service Layer (Axios)

### 4.1 Centralized Axios Instance

```typescript
// src/services/api.ts
import axios, { AxiosError, AxiosInstance } from 'axios';
import { ErrorResponse } from '../types/api';

const STUDENT_API_BASE = import.meta.env.VITE_STUDENT_API_URL ?? 'http://localhost:8081/api/v1';
const CONFIG_API_BASE = import.meta.env.VITE_CONFIG_API_URL ?? 'http://localhost:8082/api/v1';

function createApiClient(baseURL: string): AxiosInstance {
  const client = axios.create({
    baseURL,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor: inject correlation ID
  client.interceptors.request.use((config) => {
    config.headers['X-Correlation-ID'] = crypto.randomUUID();
    return config;
  });

  // Response interceptor: normalize errors
  client.interceptors.response.use(
    (response) => response,
    (error: AxiosError<ErrorResponse>) => {
      const apiError = error.response?.data;
      return Promise.reject(apiError ?? { title: 'Network Error', detail: error.message, status: 0 });
    }
  );

  return client;
}

export const studentApiClient = createApiClient(STUDENT_API_BASE);
export const configApiClient = createApiClient(CONFIG_API_BASE);
```

### 4.2 Student Service

```typescript
// src/services/studentService.ts
import { studentApiClient } from './api';
import {
  StudentBase, StudentResponse, UpdateStudentRequest,
  PagedStudentResponse
} from '../types/student';
import { Enrollment } from '../types/enrollment';

export interface StudentSearchParams {
  lastName?: string;
  status?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export const studentService = {
  async register(data: StudentBase): Promise<StudentResponse> {
    const res = await studentApiClient.post<StudentResponse>('/students', data);
    return res.data;
  },

  async search(params: StudentSearchParams): Promise<PagedStudentResponse> {
    const res = await studentApiClient.get<PagedStudentResponse>('/students', { params });
    return res.data;
  },

  async getById(studentId: string): Promise<StudentResponse> {
    const res = await studentApiClient.get<StudentResponse>(`/students/${studentId}`);
    return res.data;
  },

  async update(studentId: string, data: UpdateStudentRequest): Promise<StudentResponse> {
    const res = await studentApiClient.put<StudentResponse>(`/students/${studentId}`, data);
    return res.data;
  },

  async delete(studentId: string): Promise<void> {
    await studentApiClient.delete(`/students/${studentId}`);
  },

  async getEnrollmentHistory(studentId: string): Promise<{ studentId: string; enrollments: Enrollment[] }> {
    const res = await studentApiClient.get(`/students/${studentId}/enrollment-history`);
    return res.data;
  },

  async addEnrollment(studentId: string, data: {
    academicYear: string; gradeClass: string; section: string; enrollmentDate: string; remarks?: string;
  }): Promise<Enrollment> {
    const res = await studentApiClient.post(`/students/${studentId}/enrollment-history`, data);
    return res.data;
  },
};
```

### 4.3 Configuration Service

```typescript
// src/services/configurationService.ts
import { configApiClient } from './api';
import {
  Configuration, UpsertConfigurationRequest,
  GroupedConfigurationResponse, ConfigurationCategory
} from '../types/configuration';

export const configurationService = {
  async getAll(category?: ConfigurationCategory): Promise<{ configurations: Configuration[] }> {
    const res = await configApiClient.get('/configurations', { params: { category } });
    return res.data;
  },

  async getGrouped(category: ConfigurationCategory): Promise<GroupedConfigurationResponse> {
    const res = await configApiClient.get(`/configurations/grouped/${category}`);
    return res.data;
  },

  async upsert(
    category: ConfigurationCategory,
    key: string,
    data: UpsertConfigurationRequest
  ): Promise<Configuration> {
    const res = await configApiClient.put(`/configurations/${category}/${key}`, data);
    return res.data;
  },

  async delete(category: ConfigurationCategory, key: string): Promise<void> {
    await configApiClient.delete(`/configurations/${category}/${key}`);
  },
};
```

---

## 5. State Management Strategy

Per `specs/FRONTEND_DESIGN_SPEC.md`:

| State Type | Tool | Usage |
|---|---|---|
| Global (Toast notifications) | React Context API | `ToastContext` wraps entire app |
| Server state (lists, details) | Custom hooks with `useState` + `useEffect` | `useStudents`, `useConfigurations` |
| Form state | React Hook Form | Per dialog component |
| URL/navigation state | React Router DOM | Page routing |

### 5.1 Toast Context

```typescript
// src/context/ToastContext.tsx
import { createContext, useContext, useState, useCallback, ReactNode } from 'react';

type ToastVariant = 'success' | 'error' | 'info';

interface Toast {
  id: string;
  message: string;
  variant: ToastVariant;
}

interface ToastContextValue {
  addToast: (message: string, variant: ToastVariant) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const addToast = useCallback((message: string, variant: ToastVariant) => {
    const id = crypto.randomUUID();
    setToasts(prev => [...prev, { id, message, variant }]);
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), 4000);
  }, []);

  return (
    <ToastContext.Provider value={{ addToast }}>
      {children}
      {/* Render toasts using shadcn/ui Toast components */}
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
```

### 5.2 Student Data Hook

```typescript
// src/hooks/useStudents.ts
import { useState, useCallback } from 'react';
import { studentService, StudentSearchParams } from '../services/studentService';
import { StudentResponse, PagedStudentResponse } from '../types/student';
import { useToast } from '../context/ToastContext';
import { ErrorResponse } from '../types/api';

export function useStudents() {
  const { addToast } = useToast();
  const [students, setStudents] = useState<StudentResponse[]>([]);
  const [pagination, setPagination] = useState({ page: 0, size: 20, totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);

  const search = useCallback(async (params: StudentSearchParams) => {
    setLoading(true);
    try {
      const data: PagedStudentResponse = await studentService.search(params);
      setStudents(data.content);
      setPagination(data.pageable);
    } catch (err) {
      const error = err as ErrorResponse;
      addToast(error.detail ?? 'Failed to load students', 'error');
    } finally {
      setLoading(false);
    }
  }, [addToast]);

  const register = useCallback(async (data: Parameters<typeof studentService.register>[0]) => {
    try {
      const student = await studentService.register(data);
      addToast(`Student ${student.studentId} registered successfully`, 'success');
      return student;
    } catch (err) {
      const error = err as ErrorResponse;
      const msg = error.errors?.length > 0
        ? error.errors.map(e => e.message).join('; ')
        : error.detail ?? 'Registration failed';
      addToast(msg, 'error');
      throw err;
    }
  }, [addToast]);

  return { students, pagination, loading, search, register };
}
```

---

## 6. Form Component Pattern (React Hook Form + Zod)

```typescript
// src/components/students/StudentRegisterDialog.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createStudentSchema, CreateStudentFormData } from '../../schemas/studentSchema';
import { useStudents } from '../../hooks/useStudents';
// All components imported from reference-code:
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';

interface Props {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export function StudentRegisterDialog({ open, onClose, onSuccess }: Props) {
  const { register: registerStudent } = useStudents();

  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } =
    useForm<CreateStudentFormData>({
      resolver: zodResolver(createStudentSchema),
    });

  const onSubmit = async (data: CreateStudentFormData) => {
    try {
      await registerStudent(data);
      reset();
      onSuccess();
      onClose();
    } catch {
      // Error toast shown by hook; dialog stays open for correction
    }
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>Register New Student</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="firstName">First Name *</Label>
              <Input
                id="firstName"
                data-testid="input-first-name"
                {...register('firstName')}
                className={errors.firstName ? 'border-destructive' : ''}
              />
              {errors.firstName && (
                <p className="text-sm text-destructive">{errors.firstName.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="lastName">Last Name *</Label>
              <Input
                id="lastName"
                data-testid="input-last-name"
                {...register('lastName')}
                className={errors.lastName ? 'border-destructive' : ''}
              />
              {errors.lastName && (
                <p className="text-sm text-destructive">{errors.lastName.message}</p>
              )}
            </div>
          </div>
          {/* Additional fields follow same pattern */}
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Registering...' : 'Register Student'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
```

---

## 7. Performance Requirements

### 7.1 Lazy Loading (Route-Level)

```typescript
// src/pages/index.ts
import { lazy, Suspense } from 'react';

const StudentsPage = lazy(() => import('./StudentsPage'));
const ConfigurationsPage = lazy(() => import('./ConfigurationsPage'));

// Wrap routes in <Suspense fallback={<LoadingSpinner />}>
```

### 7.2 Memoization

- Use `React.memo` for `StudentTable` and `ConfigurationTable` (rendered in lists)
- Use `useCallback` for all event handlers passed to child components
- Use `useMemo` for derived state (e.g., filtered student list)

### 7.3 Image Optimization

- No dynamic images in Phase 1 (school logo placeholder only)
- When logo support is added: use `<img loading="lazy" decoding="async" />` attributes
- Maximum logo size: 200KB, WebP format preferred

---

## 8. Accessibility (a11y) Requirements

- All form inputs must have associated `<Label>` elements with matching `htmlFor`/`id`
- Error messages must use `role="alert"` or be associated via `aria-describedby`
- Dialog components must trap focus (handled by shadcn/ui Dialog)
- All interactive elements must have visible focus indicators (Tailwind `focus-visible:ring-2`)
- `data-testid` attributes on all interactive elements for E2E stability

---

## 9. Vite Environment Variables

```
# .env.local (not committed)
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

```
# .env.example (committed)
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

---

## 10. Dockerfile

```dockerfile
# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:1.27-alpine AS runtime
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx.conf
server {
  listen 80;
  root /usr/share/nginx/html;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  location /api/ {
    return 404;
  }
}
```

---

## 11. Testing Alignment

Per `specs/TESTING_STRATEGY.md`:

| Layer | Tool | Coverage Target | Examples |
|---|---|---|---|
| Component | Vitest + React Testing Library | 70% statement | `StudentRegisterDialog`, form validation |
| Custom Hooks | Vitest + RTL `renderHook` | 70% statement | `useStudents`, `useToast` |
| E2E | Playwright | Happy path only | Register student, search, config CRUD |
| API Mocking | MSW 2.x | All service calls | Mock student list, 409 conflict |

### MSW Setup

```typescript
// src/test/mocks/handlers.ts
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get('http://localhost:8081/api/v1/students', () => {
    return HttpResponse.json({
      content: [],
      pageable: { page: 0, size: 20, totalElements: 0, totalPages: 0 },
    });
  }),
  http.post('http://localhost:8081/api/v1/students', () => {
    return HttpResponse.json({
      id: 1,
      studentId: 'STD-20260212-0001',
      firstName: 'John',
      lastName: 'Doe',
      status: 'ACTIVE',
      version: 0,
    }, { status: 201 });
  }),
];
```

### Playwright E2E (under 50 lines per test)

```typescript
// tests/e2e/student-registration.spec.ts
import { test, expect } from '@playwright/test';
import { fillStudentForm } from './helpers/studentHelpers';

test('Register a new student successfully', async ({ page }) => {
  await page.goto('/students');
  await page.getByTestId('btn-register-student').click();
  await fillStudentForm(page, {
    firstName: 'John', lastName: 'Doe',
    dateOfBirth: '2015-06-15', mobile: '9876543210',
  });
  await page.getByTestId('btn-submit-register').click();
  await expect(page.getByText('STD-')).toBeVisible();
});
```
