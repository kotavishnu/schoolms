# Frontend Implementation Tasks - School Management System

**Version:** 1.0
**Date:** 2026-01-22
**Target Agent:** Frontend Developer
**Execution Model:** Waterfall / Single-Pass Implementation

---

## STRICT CONSTRAINTS (MANDATORY)

### NO CUSTOM STYLES RULE

**FORBIDDEN ACTIVITIES:**
- Creating CSS files
- Creating SASS/SCSS files
- Using styled-components
- Writing inline styles (except for dynamic values)
- Creating custom Tailwind classes

**MANDATORY APPROACH:**
- Use ONLY Tailwind CSS 4 utility classes
- Use ONLY Shadcn/ui components
- Copy exact Tailwind classes from `specs/FRONTEND_DESIGN_SPECIFICATION.md`

**Violation Consequences:** Task rejection, code rewrite required

---

### Reference Code Reuse Policy

**Reference Code Location:** `frontend/reference-code/` (NOTE: This directory is currently empty, so build from spec)

**Priority:**
1. If reference code exists → COPY IT EXACTLY
2. If reference code missing → Build according to `FRONTEND_DESIGN_SPECIFICATION.md`
3. Never deviate from screenshots in `screenshots/` directory

---

### Field Mapping Layer (D-007)

**CRITICAL:** Frontend-Backend field name misalignment MUST be handled in service layer.

| Frontend Field | Backend Field | Mapping Location |
|----------------|---------------|------------------|
| `phone` | `mobile` | studentService.ts |
| `id` | `studentId` | studentService.ts |
| `adhaarNumber` | `aadhaarNumber` | studentService.ts |

**Implementation Example:**
```typescript
// Frontend → Backend (on API call)
const backendRequest = {
  mobile: frontendData.phone,  // Transform
  ...otherFields
};

// Backend → Frontend (on response)
const frontendData: Student = {
  phone: backendResponse.mobile,  // Transform
  id: backendResponse.studentId,  // Transform
  ...otherFields
};
```

---

### Technology Stack (EXACT Versions)

```json
{
  "react": "18.3.x",
  "typescript": "5.x",
  "vite": "5.x",
  "tailwindcss": "4.x",
  "@radix-ui/*": "latest",
  "react-hook-form": "7.x",
  "zod": "3.x",
  "axios": "1.x",
  "react-router-dom": "6.x",
  "lucide-react": "latest"
}
```

---

## Project Foundation

### [FE-001] Vite Project Setup

**Goal:** Initialize React + TypeScript + Vite project with all dependencies.

**Technical Details:**
```bash
# Create Vite project
npm create vite@latest frontend -- --template react-ts
cd frontend

# Install dependencies
npm install

# Install Tailwind CSS 4
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

# Install Shadcn/ui dependencies
npm install @radix-ui/react-dialog @radix-ui/react-select @radix-ui/react-toast
npm install class-variance-authority clsx tailwind-merge

# Install form libraries
npm install react-hook-form @hookform/resolvers zod

# Install HTTP client
npm install axios

# Install routing
npm install react-router-dom

# Install icons
npm install lucide-react

# Install date utilities
npm install date-fns
```

**Directory Structure:**
```
frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── layout/
│   │   ├── students/
│   │   ├── configurations/
│   │   ├── common/
│   │   └── ui/          # Shadcn/ui components
│   ├── pages/
│   ├── services/
│   ├── contexts/
│   ├── hooks/
│   ├── types/
│   ├── utils/
│   ├── styles/
│   ├── App.tsx
│   ├── main.tsx
│   └── vite-env.d.ts
├── .env.development
├── .env.production
├── tailwind.config.ts
├── tsconfig.json
├── vite.config.ts
└── package.json
```

**Success Criteria:**
- Project builds with `npm run dev`
- Vite dev server runs on http://localhost:5173
- TypeScript compilation successful
- No dependency errors

**Dependencies:** None

---

### [FE-002] Tailwind CSS 4 Configuration

**Goal:** Configure Tailwind CSS with Shadcn/ui theme.

**Technical Details:**

**tailwind.config.ts:**
```typescript
import type { Config } from 'tailwindcss';

export default {
  darkMode: ['class'],
  content: [
    './index.html',
    './src/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))',
        },
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))',
        },
        accent: {
          DEFAULT: 'hsl(var(--accent))',
          foreground: 'hsl(var(--accent-foreground))',
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
      },
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
      },
    },
  },
  plugins: [],
} satisfies Config;
```

**src/styles/globals.css:**
```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  :root {
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;
    --card: 0 0% 100%;
    --card-foreground: 222.2 84% 4.9%;
    --primary: 221.2 83.2% 53.3%;
    --primary-foreground: 210 40% 98%;
    --destructive: 0 84.2% 60.2%;
    --destructive-foreground: 210 40% 98%;
    --muted: 210 40% 96.1%;
    --muted-foreground: 215.4 16.3% 46.9%;
    --accent: 210 40% 96.1%;
    --accent-foreground: 222.2 47.4% 11.2%;
    --border: 214.3 31.8% 91.4%;
    --input: 214.3 31.8% 91.4%;
    --ring: 221.2 83.2% 53.3%;
    --radius: 0.5rem;
  }
}

@layer base {
  * {
    @apply border-border;
  }
  body {
    @apply bg-background text-foreground;
  }
}
```

**Success Criteria:**
- Tailwind classes apply correctly
- CSS variables defined
- Global styles loaded

**Dependencies:** FE-001

---

### [FE-003] Shadcn/ui Component Installation

**Goal:** Install required Shadcn/ui components.

**Technical Details:**

**Components to Install:**
1. Button
2. Dialog
3. Input
4. Label
5. Select
6. Textarea
7. Toast
8. Card
9. Badge
10. Alert
11. Skeleton

**Manual Installation (Copy component code from https://ui.shadcn.com/):**

Create each component in `src/components/ui/`:
- `button.tsx`
- `dialog.tsx`
- `input.tsx`
- `label.tsx`
- `select.tsx`
- `textarea.tsx`
- `toast.tsx`
- `toaster.tsx`
- `use-toast.ts`
- `card.tsx`
- `badge.tsx`
- `alert.tsx`
- `skeleton.tsx`

**Success Criteria:**
- All components imported without errors
- Components render correctly in test page

**Dependencies:** FE-002

---

### [FE-004] TypeScript Type Definitions

**Goal:** Define TypeScript interfaces matching backend DTOs.

**Technical Details:**

**src/types/student.ts:**
```typescript
export interface Student {
  // Identity (Frontend uses 'id', backend uses 'studentId')
  id: string;

  // Personal Information
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 format
  age: number; // Calculated from DOB
  adhaarNumber: string; // Frontend spelling
  identificationMarks?: string;
  address: string;

  // Guardian Information
  guardianName: string;
  motherName: string;

  // Contact (Frontend uses 'phone', backend uses 'mobile')
  phone: string;
  email: string;

  // Status
  status: 'ACTIVE' | 'INACTIVE';

  // Metadata
  createdAt: string;
  updatedAt: string;
}

export interface StudentCreateRequest {
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
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  phone: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
}

export type StudentStatus = 'ACTIVE' | 'INACTIVE';
```

**src/types/configuration.ts:**
```typescript
export interface Configuration {
  id: string;
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
  lastUpdated: string;
  createdAt: string;
}

export type ConfigCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM';

export interface ConfigurationCreateRequest {
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
}

export interface ConfigurationUpdateRequest {
  value: string;
  description?: string;
}
```

**src/types/api.ts:**
```typescript
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>;
}

export class ApiException extends Error {
  constructor(
    public statusCode: number,
    public error: ApiError
  ) {
    super(error.message);
    this.name = 'ApiException';
  }
}
```

**Success Criteria:**
- All types match backend DTOs
- Field naming documented (phone/mobile, id/studentId)

**Dependencies:** FE-001

---

### [FE-005] Axios API Client Configuration

**Goal:** Create Axios instance with interceptors.

**Technical Details:**

**src/services/api.ts:**
```typescript
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { ApiException, ApiError } from '../types/api';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Add correlation ID
apiClient.interceptors.request.use(
  (config) => {
    const correlationId = crypto.randomUUID();
    config.headers = config.headers || {};
    config.headers['X-Correlation-ID'] = correlationId;

    console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response) {
      console.error(`API Error: ${error.response.status}`, error.response.data);

      throw new ApiException(
        error.response.status,
        error.response.data || {
          code: 'UNKNOWN_ERROR',
          message: 'An unexpected error occurred',
        }
      );
    } else if (error.request) {
      console.error('Network Error:', error.message);
      throw new ApiException(0, {
        code: 'NETWORK_ERROR',
        message: 'Network error. Please check your connection.',
      });
    } else {
      console.error('Error:', error.message);
      throw new ApiException(0, {
        code: 'UNKNOWN_ERROR',
        message: error.message,
      });
    }
  }
);

export default apiClient;
```

**.env.development:**
```bash
VITE_API_BASE_URL=http://localhost:8080
```

**Success Criteria:**
- Axios instance configured
- Interceptors functional
- Correlation IDs added to requests

**Dependencies:** FE-004

---

## Service Layer Implementation

### [FE-006] Student Service with Field Mapping

**Goal:** Create student API service with frontend-backend field mapping.

**Technical Details:**

**src/services/studentService.ts:**
```typescript
import apiClient from './api';
import {
  Student,
  StudentCreateRequest,
  StudentUpdateRequest,
  StudentListResponse,
} from '../types/student';
import { calculateAge } from '../utils/formatting';

// Backend field mapping helper
function mapToBackend(data: StudentCreateRequest) {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    dateOfBirth: data.dateOfBirth,
    mobile: data.phone, // Frontend → Backend: phone → mobile
    email: data.email,
    address: data.address,
    aadhaarNumber: data.adhaarNumber, // Match backend spelling
    guardianName: data.guardianName,
    motherName: data.motherName,
    identificationMarks: data.identificationMarks,
    status: data.status,
  };
}

function mapFromBackend(backendStudent: any): Student {
  return {
    id: backendStudent.studentId, // Backend → Frontend: studentId → id
    firstName: backendStudent.firstName,
    lastName: backendStudent.lastName,
    dateOfBirth: backendStudent.dateOfBirth,
    age: calculateAge(backendStudent.dateOfBirth),
    phone: backendStudent.mobile, // Backend → Frontend: mobile → phone
    email: backendStudent.email,
    address: backendStudent.address,
    adhaarNumber: backendStudent.aadhaarNumber,
    guardianName: backendStudent.guardianName,
    motherName: backendStudent.motherName,
    identificationMarks: backendStudent.identificationMarks,
    status: backendStudent.status,
    createdAt: backendStudent.createdAt,
    updatedAt: backendStudent.updatedAt,
  };
}

export const studentService = {
  async getAll(filters?: { search?: string; status?: string }): Promise<Student[]> {
    const response = await apiClient.get('/api/v1/students', { params: filters });
    return response.data.content.map(mapFromBackend);
  },

  async getById(id: string): Promise<Student> {
    const response = await apiClient.get(`/api/v1/students/${id}`);
    return mapFromBackend(response.data);
  },

  async create(data: StudentCreateRequest): Promise<Student> {
    const backendRequest = mapToBackend(data);
    const response = await apiClient.post('/api/v1/students', backendRequest);
    return mapFromBackend(response.data);
  },

  async update(id: string, data: StudentUpdateRequest): Promise<Student> {
    const backendRequest = {
      firstName: data.firstName,
      lastName: data.lastName,
      mobile: data.phone, // Transform
      status: data.status,
    };
    const response = await apiClient.put(`/api/v1/students/${id}`, backendRequest);
    return mapFromBackend(response.data);
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  async validatePhone(phone: string, excludeId?: string): Promise<boolean> {
    const response = await apiClient.post('/api/v1/students/validate-phone', {
      mobile: phone, // Transform
      excludeId,
    });
    return response.data.isUnique;
  },

  async getStatistics(): Promise<{ total: number; active: number; inactive: number }> {
    const response = await apiClient.get('/api/v1/students/statistics');
    return response.data;
  },
};
```

**Success Criteria:**
- Field mapping working (phone↔mobile, id↔studentId)
- All CRUD operations functional
- Type-safe service methods

**Dependencies:** FE-005

---

### [FE-007] Configuration Service

**Goal:** Create configuration API service.

**Technical Details:**

**src/services/configurationService.ts:**
```typescript
import apiClient from './api';
import {
  Configuration,
  ConfigCategory,
  ConfigurationCreateRequest,
  ConfigurationUpdateRequest,
} from '../types/configuration';

export const configurationService = {
  async getAll(category?: ConfigCategory): Promise<Configuration[]> {
    const response = await apiClient.get('/api/v1/configurations', {
      params: category ? { category } : undefined,
    });
    return response.data.configurations || response.data;
  },

  async getByKey(category: ConfigCategory, key: string): Promise<Configuration> {
    const response = await apiClient.get(`/api/v1/configurations/${category}/${key}`);
    return response.data;
  },

  async upsert(
    category: ConfigCategory,
    key: string,
    data: ConfigurationUpdateRequest
  ): Promise<Configuration> {
    const response = await apiClient.put(
      `/api/v1/configurations/${category}/${key}`,
      data
    );
    return response.data;
  },

  async delete(category: ConfigCategory, key: string): Promise<void> {
    await apiClient.delete(`/api/v1/configurations/${category}/${key}`);
  },

  async getGrouped(category: ConfigCategory): Promise<Record<string, string>> {
    const response = await apiClient.get(`/api/v1/configurations/grouped/${category}`);
    return response.data.settings;
  },
};
```

**Success Criteria:**
- All configuration operations functional
- Category filtering working

**Dependencies:** FE-005

---

## Validation Layer

### [FE-008] Zod Validation Schemas

**Goal:** Create Zod schemas matching backend Drools rules.

**Technical Details:**

**src/utils/validation.ts:**
```typescript
import { z } from 'zod';

// Helper: Calculate age from date of birth
export const calculateAge = (dob: string): number => {
  const birthDate = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
};

// Student Create Schema (matches BR-STU-001 to BR-STU-007)
export const studentCreateSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(100, 'First name must be at most 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(100, 'Last name must be at most 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, 'Age must be between 3 and 18 years'), // BR-STU-001

  adhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar number must be exactly 12 digits'), // BR-STU-004

  identificationMarks: z
    .string()
    .max(200, 'Identification marks must be at most 200 characters')
    .optional(),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address must be at most 500 characters'),

  guardianName: z
    .string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name must be at most 100 characters'),

  motherName: z
    .string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name must be at most 100 characters'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone must be exactly 10 digits'), // BR-STU-006

  email: z
    .string()
    .email('Email must be in valid format'), // BR-STU-003

  status: z.enum(['ACTIVE', 'INACTIVE']).default('ACTIVE'),
});

// Student Update Schema (only editable fields - BR-STU-007)
export const studentUpdateSchema = z.object({
  firstName: z
    .string()
    .min(1)
    .max(100)
    .regex(/^[a-zA-Z\s]+$/),

  lastName: z
    .string()
    .min(1)
    .max(100)
    .regex(/^[a-zA-Z\s]+$/),

  phone: z.string().regex(/^\d{10}$/),

  status: z.enum(['ACTIVE', 'INACTIVE']),
});

// Configuration Schema
export const configurationSchema = z.object({
  category: z.enum(['GENERAL', 'ACADEMIC', 'FINANCE', 'SYSTEM']),

  key: z
    .string()
    .min(1, 'Key is required')
    .max(100, 'Key must be at most 100 characters')
    .regex(/^[A-Z0-9_]+$/, 'Key must contain only uppercase letters, numbers, and underscores'),

  value: z
    .string()
    .min(1, 'Value is required')
    .max(1000, 'Value must be at most 1000 characters'),

  description: z
    .string()
    .max(500, 'Description must be at most 500 characters')
    .optional(),
});

export type StudentCreateFormData = z.infer<typeof studentCreateSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
export type ConfigurationFormData = z.infer<typeof configurationSchema>;
```

**Success Criteria:**
- All business rules validated (BR-STU-001 to BR-STU-007)
- Validation errors match backend errors

**Dependencies:** FE-004

---

### [FE-009] Utility Functions

**Goal:** Create helper functions for formatting and validation.

**Technical Details:**

**src/utils/formatting.ts:**
```typescript
import { format, parseISO } from 'date-fns';

export function calculateAge(dob: string): number {
  const birthDate = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
}

export function formatDate(dateString: string): string {
  return format(parseISO(dateString), 'MMM dd, yyyy');
}

export function formatDateTime(dateString: string): string {
  return format(parseISO(dateString), 'MMM dd, yyyy h:mm a');
}

export function formatStudentId(id: string): string {
  // Format: STD-20260122-0001
  return id;
}
```

**src/utils/constants.ts:**
```typescript
export const STUDENT_STATUS = {
  ACTIVE: 'Active',
  INACTIVE: 'Inactive',
} as const;

export const CONFIG_CATEGORIES = {
  GENERAL: 'General',
  ACADEMIC: 'Academic',
  FINANCE: 'Finance',
  SYSTEM: 'System',
} as const;

export const API_ENDPOINTS = {
  STUDENTS: '/api/v1/students',
  CONFIGURATIONS: '/api/v1/configurations',
} as const;
```

**Success Criteria:**
- All utility functions working
- Constants defined

**Dependencies:** FE-001

---

## Component Implementation

### [FE-010] App Context and Toast Provider

**Goal:** Create global context for app state and toast notifications.

**Technical Details:**

**src/contexts/AppContext.tsx:**
```typescript
import { createContext, useContext, ReactNode } from 'react';
import { Toaster } from '@/components/ui/toaster';

interface AppContextType {
  // Add global state here if needed
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  return (
    <AppContext.Provider value={{}}>
      {children}
      <Toaster />
    </AppContext.Provider>
  );
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within AppProvider');
  }
  return context;
}
```

**Success Criteria:**
- Context provider wraps app
- Toast notifications functional

**Dependencies:** FE-003

---

### [FE-011] Custom Hooks - useStudents

**Goal:** Create custom hook for Student state management.

**Technical Details:**

**src/hooks/useStudents.ts:**
```typescript
import { useState, useCallback } from 'react';
import { studentService } from '../services/studentService';
import {
  Student,
  StudentCreateRequest,
  StudentUpdateRequest,
} from '../types/student';
import { useToast } from '@/components/ui/use-toast';

export const useStudents = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { toast } = useToast();

  const loadStudents = useCallback(
    async (filters?: { search?: string; status?: string }) => {
      setLoading(true);
      setError(null);
      try {
        const data = await studentService.getAll(filters);
        setStudents(data);
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Failed to load students';
        setError(message);
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  const createStudent = useCallback(
    async (data: StudentCreateRequest): Promise<Student | null> => {
      setLoading(true);
      try {
        const newStudent = await studentService.create(data);
        setStudents((prev) => [...prev, newStudent]);
        toast({
          title: 'Success',
          description: 'Student registered successfully',
        });
        return newStudent;
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Failed to create student';
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
        return null;
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  const updateStudent = useCallback(
    async (id: string, data: StudentUpdateRequest): Promise<boolean> => {
      setLoading(true);
      try {
        const updated = await studentService.update(id, data);
        setStudents((prev) => prev.map((s) => (s.id === id ? updated : s)));
        toast({
          title: 'Success',
          description: 'Student updated successfully',
        });
        return true;
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Failed to update student';
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
        return false;
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  const deleteStudent = useCallback(
    async (id: string): Promise<boolean> => {
      setLoading(true);
      try {
        await studentService.delete(id);
        setStudents((prev) => prev.filter((s) => s.id !== id));
        toast({
          title: 'Success',
          description: 'Student deleted successfully',
        });
        return true;
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Failed to delete student';
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
        return false;
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  return {
    students,
    loading,
    error,
    loadStudents,
    createStudent,
    updateStudent,
    deleteStudent,
  };
};
```

**Success Criteria:**
- Hook manages student state
- CRUD operations functional
- Toast notifications working

**Dependencies:** FE-006, FE-010

---

### [FE-012] Custom Hooks - useConfigurations

**Goal:** Create custom hook for Configuration state management.

**Technical Details:**

**src/hooks/useConfigurations.ts:**
```typescript
import { useState, useCallback } from 'react';
import { configurationService } from '../services/configurationService';
import {
  Configuration,
  ConfigCategory,
  ConfigurationUpdateRequest,
} from '../types/configuration';
import { useToast } from '@/components/ui/use-toast';

export const useConfigurations = () => {
  const [configurations, setConfigurations] = useState<Configuration[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { toast } = useToast();

  const loadConfigurations = useCallback(
    async (category?: ConfigCategory) => {
      setLoading(true);
      setError(null);
      try {
        const data = await configurationService.getAll(category);
        setConfigurations(data);
      } catch (err) {
        const message =
          err instanceof Error ? err.message : 'Failed to load configurations';
        setError(message);
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  const upsertConfiguration = useCallback(
    async (
      category: ConfigCategory,
      key: string,
      data: ConfigurationUpdateRequest
    ): Promise<boolean> => {
      setLoading(true);
      try {
        const updated = await configurationService.upsert(category, key, data);
        setConfigurations((prev) => {
          const index = prev.findIndex((c) => c.category === category && c.key === key);
          if (index >= 0) {
            const newConfigs = [...prev];
            newConfigs[index] = updated;
            return newConfigs;
          } else {
            return [...prev, updated];
          }
        });
        toast({
          title: 'Success',
          description: 'Configuration saved successfully',
        });
        return true;
      } catch (err) {
        const message =
          err instanceof Error ? err.message : 'Failed to save configuration';
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
        return false;
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  const deleteConfiguration = useCallback(
    async (category: ConfigCategory, key: string): Promise<boolean> => {
      setLoading(true);
      try {
        await configurationService.delete(category, key);
        setConfigurations((prev) =>
          prev.filter((c) => !(c.category === category && c.key === key))
        );
        toast({
          title: 'Success',
          description: 'Configuration deleted successfully',
        });
        return true;
      } catch (err) {
        const message =
          err instanceof Error ? err.message : 'Failed to delete configuration';
        toast({
          variant: 'destructive',
          title: 'Error',
          description: message,
        });
        return false;
      } finally {
        setLoading(false);
      }
    },
    [toast]
  );

  return {
    configurations,
    loading,
    error,
    loadConfigurations,
    upsertConfiguration,
    deleteConfiguration,
  };
};
```

**Success Criteria:**
- Hook manages configuration state
- CRUD operations functional

**Dependencies:** FE-007, FE-010

---

### [FE-013] Layout Components - Header

**Goal:** Create Header component matching screenshot.

**Technical Details:**

**Reference:** `screenshots/homepage.png` (header section)

**src/components/layout/Header.tsx:**
```typescript
import { Link, useLocation } from 'react-router-dom';
import { School } from 'lucide-react';

export function Header() {
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="border-b bg-white">
      <div className="container mx-auto flex h-16 items-center justify-between px-6">
        <Link to="/" className="flex items-center gap-2">
          <School className="h-6 w-6 text-blue-600" />
          <span className="text-xl font-bold text-gray-900">School MS</span>
        </Link>

        <nav className="flex gap-6">
          <Link
            to="/"
            className={`text-sm font-medium transition-colors hover:text-blue-600 ${
              isActive('/') ? 'text-blue-600' : 'text-gray-600'
            }`}
          >
            Home
          </Link>
          <Link
            to="/students"
            className={`text-sm font-medium transition-colors hover:text-blue-600 ${
              isActive('/students') ? 'text-blue-600' : 'text-gray-600'
            }`}
          >
            Students
          </Link>
          <Link
            to="/configurations"
            className={`text-sm font-medium transition-colors hover:text-blue-600 ${
              isActive('/configurations') ? 'text-blue-600' : 'text-gray-600'
            }`}
          >
            Configurations
          </Link>
        </nav>
      </div>
    </header>
  );
}
```

**Screenshot Validation Checklist:**
- Logo icon and text match
- Nav link spacing correct
- Active link color blue-600
- Header height 16 (64px)

**Success Criteria:**
- Header matches screenshot pixel-perfectly
- Navigation links functional
- Active state highlighting working

**Dependencies:** FE-003

---

### [FE-014] Layout Components - Layout Wrapper

**Goal:** Create main layout wrapper.

**Technical Details:**

**src/components/layout/Layout.tsx:**
```typescript
import { ReactNode } from 'react';
import { Header } from './Header';

interface LayoutProps {
  children: ReactNode;
}

export function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="container mx-auto p-6">{children}</main>
    </div>
  );
}
```

**Success Criteria:**
- Layout renders correctly
- Header included
- Content area styled

**Dependencies:** FE-013

---

### [FE-015] HomePage Implementation

**Goal:** Create HomePage matching screenshot.

**Reference:** `screenshots/homepage.png`

**Technical Details:**

**src/pages/HomePage.tsx:**
```typescript
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Users, UserCheck, Settings, UserPlus } from 'lucide-react';
import { studentService } from '../services/studentService';

export function HomePage() {
  const navigate = useNavigate();
  const [stats, setStats] = useState({ total: 0, active: 0, inactive: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    try {
      const data = await studentService.getStatistics();
      setStats(data);
    } catch (err) {
      console.error('Failed to load statistics', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="rounded-lg bg-blue-600 p-8 text-white">
        <h1 className="text-3xl font-bold">Welcome to School Management System</h1>
        <p className="mt-2 text-blue-100">
          Manage students and school configuration efficiently
        </p>
      </div>

      {/* Statistics Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Students</CardTitle>
            <Users className="h-4 w-4 text-gray-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '...' : stats.total}</div>
            <p className="text-xs text-gray-600">All registered students</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Students</CardTitle>
            <UserCheck className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '...' : stats.active}</div>
            <p className="text-xs text-gray-600">Currently enrolled</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">System Status</CardTitle>
            <Settings className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">Online</div>
            <p className="text-xs text-gray-600">All systems operational</p>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <div className="grid gap-4 sm:grid-cols-2">
        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => navigate('/students')}>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Manage Students
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-600">
              View, search, and manage student records
            </p>
          </CardContent>
        </Card>

        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => navigate('/students')}>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <UserPlus className="h-5 w-5" />
              Register Student
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-600">
              Add new student to the system
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
```

**Screenshot Validation Checklist:**
- Welcome banner: blue-600 background, white text
- 3 stat cards in grid (1 col mobile, 3 col desktop)
- Icons match (Users, UserCheck, Settings)
- Quick action cards present
- Card hover effects

**Success Criteria:**
- Page matches screenshot pixel-perfectly
- Statistics load from API
- Quick actions navigate correctly

**Dependencies:** FE-006, FE-014

---

### [FE-016] Student Components - StudentCard

**Goal:** Create StudentCard component matching screenshot.

**Reference:** `screenshots/students-page.png` (card layout)

**Technical Details:**

**src/components/students/StudentCard.tsx:**
```typescript
import { Student } from '../../types/student';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Eye, Edit, Trash2, Phone, Mail } from 'lucide-react';

interface StudentCardProps {
  student: Student;
  onView: (student: Student) => void;
  onEdit: (student: Student) => void;
  onDelete: (id: string) => void;
}

export function StudentCard({ student, onView, onEdit, onDelete }: StudentCardProps) {
  const statusColor =
    student.status === 'ACTIVE'
      ? 'bg-green-100 text-green-800'
      : 'bg-gray-100 text-gray-800';

  return (
    <Card>
      <CardContent className="p-4 space-y-3">
        <div className="flex items-start justify-between">
          <div>
            <p className="text-xs text-gray-600">ID: {student.id}</p>
            <h3 className="text-lg font-semibold text-gray-900">
              {student.firstName} {student.lastName}
            </h3>
          </div>
          <Badge className={statusColor}>{student.status}</Badge>
        </div>

        <div className="space-y-1 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <span className="font-medium">Guardian:</span>
            <span>{student.guardianName}</span>
          </div>
          <div className="flex items-center gap-2">
            <Phone className="h-3 w-3" />
            <span>{student.phone}</span>
          </div>
          <div className="flex items-center gap-2">
            <Mail className="h-3 w-3" />
            <span className="truncate">{student.email}</span>
          </div>
        </div>

        <div className="flex gap-2 pt-2">
          <Button
            size="sm"
            variant="outline"
            className="flex-1"
            onClick={() => onView(student)}
          >
            <Eye className="h-4 w-4 mr-1" />
            View
          </Button>
          <Button
            size="sm"
            variant="outline"
            className="flex-1"
            onClick={() => onEdit(student)}
          >
            <Edit className="h-4 w-4 mr-1" />
            Edit
          </Button>
          <Button
            size="sm"
            variant="destructive"
            onClick={() => onDelete(student.id)}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
```

**Screenshot Validation Checklist:**
- Card layout matches
- Status badge colors: green-100/green-800 (ACTIVE), gray-100/gray-800 (INACTIVE)
- Icon sizes: h-4 w-4 for buttons, h-3 w-3 for inline
- Button layout: View, Edit, Delete
- Text truncation for long emails

**Success Criteria:**
- Card matches screenshot
- All buttons functional
- Status badge colors correct

**Dependencies:** FE-003, FE-004

---

### [FE-017] Student Components - StudentDialog

**Goal:** Create StudentDialog for create/edit operations.

**Reference:** `screenshots/student-dialog-create.png`, `screenshots/student-dialog-edit.png`

**Technical Details:**

**src/components/students/StudentDialog.tsx:**
```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  studentCreateSchema,
  studentUpdateSchema,
  StudentCreateFormData,
  StudentUpdateFormData,
} from '../../utils/validation';
import { Student } from '../../types/student';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';

interface StudentDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: StudentCreateFormData | StudentUpdateFormData) => Promise<void>;
  mode: 'create' | 'edit';
  student?: Student;
}

export function StudentDialog({
  isOpen,
  onClose,
  onSubmit,
  mode,
  student,
}: StudentDialogProps) {
  const schema = mode === 'create' ? studentCreateSchema : studentUpdateSchema;

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
    setValue,
  } = useForm<StudentCreateFormData>({
    resolver: zodResolver(schema),
    defaultValues: mode === 'edit' && student ? {
      firstName: student.firstName,
      lastName: student.lastName,
      phone: student.phone,
      status: student.status,
    } : {
      status: 'ACTIVE',
    },
  });

  const handleFormSubmit = async (data: StudentCreateFormData) => {
    await onSubmit(data);
    reset();
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? 'Register New Student' : 'Edit Student'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          {/* Personal Information */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold">Personal Information</h3>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="firstName">First Name *</Label>
                <Input
                  id="firstName"
                  {...register('firstName')}
                  className={errors.firstName ? 'border-red-500' : ''}
                />
                {errors.firstName && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.firstName.message}
                  </p>
                )}
              </div>

              <div>
                <Label htmlFor="lastName">Last Name *</Label>
                <Input
                  id="lastName"
                  {...register('lastName')}
                  className={errors.lastName ? 'border-red-500' : ''}
                />
                {errors.lastName && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.lastName.message}
                  </p>
                )}
              </div>
            </div>

            {mode === 'create' && (
              <>
                <div>
                  <Label htmlFor="dateOfBirth">Date of Birth *</Label>
                  <Input
                    id="dateOfBirth"
                    type="date"
                    {...register('dateOfBirth')}
                    className={errors.dateOfBirth ? 'border-red-500' : ''}
                  />
                  {errors.dateOfBirth && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.dateOfBirth.message}
                    </p>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="adhaarNumber">Aadhaar Number *</Label>
                    <Input
                      id="adhaarNumber"
                      {...register('adhaarNumber')}
                      placeholder="123456789012"
                      maxLength={12}
                      className={errors.adhaarNumber ? 'border-red-500' : ''}
                    />
                    {errors.adhaarNumber && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.adhaarNumber.message}
                      </p>
                    )}
                  </div>

                  <div>
                    <Label htmlFor="identificationMarks">
                      Identification Marks
                    </Label>
                    <Input
                      id="identificationMarks"
                      {...register('identificationMarks')}
                      className={
                        errors.identificationMarks ? 'border-red-500' : ''
                      }
                    />
                    {errors.identificationMarks && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.identificationMarks.message}
                      </p>
                    )}
                  </div>
                </div>

                <div>
                  <Label htmlFor="address">Address *</Label>
                  <Textarea
                    id="address"
                    {...register('address')}
                    rows={3}
                    className={errors.address ? 'border-red-500' : ''}
                  />
                  {errors.address && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.address.message}
                    </p>
                  )}
                </div>
              </>
            )}
          </div>

          {/* Guardian Information (create only) */}
          {mode === 'create' && (
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">Guardian Information</h3>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="guardianName">Guardian Name *</Label>
                  <Input
                    id="guardianName"
                    {...register('guardianName')}
                    className={errors.guardianName ? 'border-red-500' : ''}
                  />
                  {errors.guardianName && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.guardianName.message}
                    </p>
                  )}
                </div>

                <div>
                  <Label htmlFor="motherName">Mother Name *</Label>
                  <Input
                    id="motherName"
                    {...register('motherName')}
                    className={errors.motherName ? 'border-red-500' : ''}
                  />
                  {errors.motherName && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.motherName.message}
                    </p>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Contact Information */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold">Contact Information</h3>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="phone">Phone *</Label>
                <Input
                  id="phone"
                  {...register('phone')}
                  placeholder="9876543210"
                  maxLength={10}
                  className={errors.phone ? 'border-red-500' : ''}
                />
                {errors.phone && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.phone.message}
                  </p>
                )}
              </div>

              {mode === 'create' && (
                <div>
                  <Label htmlFor="email">Email *</Label>
                  <Input
                    id="email"
                    type="email"
                    {...register('email')}
                    className={errors.email ? 'border-red-500' : ''}
                  />
                  {errors.email && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.email.message}
                    </p>
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Status */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold">Status</h3>

            <div>
              <Label htmlFor="status">Status *</Label>
              <Select
                defaultValue={mode === 'edit' && student ? student.status : 'ACTIVE'}
                onValueChange={(value) => setValue('status', value as 'ACTIVE' | 'INACTIVE')}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ACTIVE">Active</SelectItem>
                  <SelectItem value="INACTIVE">Inactive</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Form Actions */}
          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting
                ? 'Submitting...'
                : mode === 'create'
                ? 'Register'
                : 'Update'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
```

**Screenshot Validation Checklist:**
- Form sections: Personal Info, Guardian Info, Contact, Status
- Field layout: 2-column grid for name fields
- Disabled fields in edit mode (grayed out)
- Required field markers (*)
- Error message display below fields

**Success Criteria:**
- Dialog matches screenshot
- Create mode shows all fields
- Edit mode shows only editable fields (firstName, lastName, phone, status)
- Validation errors display correctly

**Dependencies:** FE-003, FE-008

---

### [FE-018] Student Components - ViewStudentDialog

**Goal:** Create read-only student view dialog.

**Reference:** `screenshots/student-dialog-view.png`

**Technical Details:**

**src/components/students/ViewStudentDialog.tsx:**
```typescript
import { Student } from '../../types/student';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatDate } from '../../utils/formatting';

interface ViewStudentDialogProps {
  isOpen: boolean;
  onClose: () => void;
  student: Student | null;
}

export function ViewStudentDialog({
  isOpen,
  onClose,
  student,
}: ViewStudentDialogProps) {
  if (!student) return null;

  const statusColor =
    student.status === 'ACTIVE'
      ? 'bg-green-100 text-green-800'
      : 'bg-gray-100 text-gray-800';

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Student Details</DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Personal Information */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold border-b pb-2">
              Personal Information
            </h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-600">Student ID</p>
                <p className="font-medium">{student.id}</p>
              </div>
              <div>
                <p className="text-gray-600">Status</p>
                <Badge className={statusColor}>{student.status}</Badge>
              </div>
              <div>
                <p className="text-gray-600">First Name</p>
                <p className="font-medium">{student.firstName}</p>
              </div>
              <div>
                <p className="text-gray-600">Last Name</p>
                <p className="font-medium">{student.lastName}</p>
              </div>
              <div>
                <p className="text-gray-600">Date of Birth</p>
                <p className="font-medium">{formatDate(student.dateOfBirth)}</p>
              </div>
              <div>
                <p className="text-gray-600">Age</p>
                <p className="font-medium">{student.age} years</p>
              </div>
              <div>
                <p className="text-gray-600">Aadhaar Number</p>
                <p className="font-medium">{student.adhaarNumber || 'N/A'}</p>
              </div>
              <div>
                <p className="text-gray-600">Identification Marks</p>
                <p className="font-medium">{student.identificationMarks || 'N/A'}</p>
              </div>
              <div className="col-span-2">
                <p className="text-gray-600">Address</p>
                <p className="font-medium">{student.address}</p>
              </div>
            </div>
          </div>

          {/* Guardian Information */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold border-b pb-2">
              Guardian Information
            </h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-600">Guardian Name</p>
                <p className="font-medium">{student.guardianName}</p>
              </div>
              <div>
                <p className="text-gray-600">Mother Name</p>
                <p className="font-medium">{student.motherName}</p>
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold border-b pb-2">
              Contact Information
            </h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-600">Phone</p>
                <p className="font-medium">{student.phone}</p>
              </div>
              <div>
                <p className="text-gray-600">Email</p>
                <p className="font-medium">{student.email}</p>
              </div>
            </div>
          </div>

          {/* Metadata */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold border-b pb-2">Metadata</h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-600">Created At</p>
                <p className="font-medium">{formatDate(student.createdAt)}</p>
              </div>
              <div>
                <p className="text-gray-600">Updated At</p>
                <p className="font-medium">{formatDate(student.updatedAt)}</p>
              </div>
            </div>
          </div>

          <div className="flex justify-end pt-4">
            <Button onClick={onClose}>Close</Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
```

**Screenshot Validation Checklist:**
- Read-only display (no input fields)
- Sections: Personal Info, Guardian Info, Contact, Metadata
- 2-column grid layout
- Label styling: text-gray-600
- Value styling: font-medium

**Success Criteria:**
- Dialog matches screenshot
- All fields displayed
- No form inputs (read-only)

**Dependencies:** FE-003, FE-004, FE-009

---

### [FE-019] StudentsPage Implementation

**Goal:** Create StudentsPage with all student management features.

**Reference:** `screenshots/students-page.png`

**Technical Details:**

**src/pages/StudentsPage.tsx:**
```typescript
import { useEffect, useState } from 'react';
import { useStudents } from '../hooks/useStudents';
import { Student, StudentCreateRequest, StudentUpdateRequest } from '../types/student';
import { StudentCard } from '../components/students/StudentCard';
import { StudentDialog } from '../components/students/StudentDialog';
import { ViewStudentDialog } from '../components/students/ViewStudentDialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { UserPlus, Search } from 'lucide-react';

export function StudentsPage() {
  const { students, loading, loadStudents, createStudent, updateStudent, deleteStudent } = useStudents();
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL');
  const [dialogState, setDialogState] = useState<{
    mode: 'create' | 'edit' | 'view' | null;
    student?: Student;
  }>({ mode: null });

  useEffect(() => {
    loadStudents(
      statusFilter !== 'ALL' ? { status: statusFilter } : undefined
    );
  }, [statusFilter, loadStudents]);

  const filteredStudents = students.filter((student) => {
    if (!search) return true;
    const searchLower = search.toLowerCase();
    return (
      student.id.toLowerCase().includes(searchLower) ||
      student.firstName.toLowerCase().includes(searchLower) ||
      student.lastName.toLowerCase().includes(searchLower) ||
      student.guardianName.toLowerCase().includes(searchLower)
    );
  });

  const handleCreate = async (data: StudentCreateRequest) => {
    await createStudent(data);
    setDialogState({ mode: null });
  };

  const handleUpdate = async (data: StudentUpdateRequest) => {
    if (dialogState.student) {
      await updateStudent(dialogState.student.id, data);
      setDialogState({ mode: null });
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      await deleteStudent(id);
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">Students</h1>
        <Button onClick={() => setDialogState({ mode: 'create' })}>
          <UserPlus className="h-4 w-4 mr-2" />
          Register New Student
        </Button>
      </div>

      {/* Filters */}
      <div className="flex gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <Input
            placeholder="Search by ID, name, or guardian..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select value={statusFilter} onValueChange={(value: any) => setStatusFilter(value)}>
          <SelectTrigger className="w-[180px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All Students</SelectItem>
            <SelectItem value="ACTIVE">Active</SelectItem>
            <SelectItem value="INACTIVE">Inactive</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Student Grid */}
      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Skeleton key={i} className="h-48" />
          ))}
        </div>
      ) : filteredStudents.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          No students found
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filteredStudents.map((student) => (
            <StudentCard
              key={student.id}
              student={student}
              onView={(s) => setDialogState({ mode: 'view', student: s })}
              onEdit={(s) => setDialogState({ mode: 'edit', student: s })}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      {/* Dialogs */}
      {dialogState.mode === 'create' && (
        <StudentDialog
          isOpen={true}
          onClose={() => setDialogState({ mode: null })}
          onSubmit={handleCreate}
          mode="create"
        />
      )}

      {dialogState.mode === 'edit' && dialogState.student && (
        <StudentDialog
          isOpen={true}
          onClose={() => setDialogState({ mode: null })}
          onSubmit={handleUpdate}
          mode="edit"
          student={dialogState.student}
        />
      )}

      {dialogState.mode === 'view' && (
        <ViewStudentDialog
          isOpen={true}
          onClose={() => setDialogState({ mode: null })}
          student={dialogState.student || null}
        />
      )}
    </div>
  );
}
```

**Screenshot Validation Checklist:**
- Search input with icon on left
- Status filter dropdown
- "Register New Student" button (top-right, blue primary)
- Grid layout: 1 col (mobile), 2 (tablet), 3 (desktop)
- Skeleton loaders during loading
- Empty state message

**Success Criteria:**
- Page matches screenshot
- Search filters locally
- Status filter loads from API
- All CRUD operations functional

**Dependencies:** FE-011, FE-016, FE-017, FE-018

---

### [FE-020] Configuration Components - ConfigurationDialog

**Goal:** Create ConfigurationDialog for create/edit operations.

**Reference:** `screenshots/configuration-dialog-add.png`, `screenshots/configuration-dialog-edit.png`

**Technical Details:**

**src/components/configurations/ConfigurationDialog.tsx:**
```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { configurationSchema, ConfigurationFormData } from '../../utils/validation';
import { Configuration, ConfigCategory } from '../../types/configuration';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

interface ConfigurationDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: ConfigurationFormData) => Promise<void>;
  mode: 'create' | 'edit';
  configuration?: Configuration;
}

export function ConfigurationDialog({
  isOpen,
  onClose,
  onSubmit,
  mode,
  configuration,
}: ConfigurationDialogProps) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
    setValue,
  } = useForm<ConfigurationFormData>({
    resolver: zodResolver(configurationSchema),
    defaultValues: mode === 'edit' && configuration ? {
      category: configuration.category,
      key: configuration.key,
      value: configuration.value,
      description: configuration.description,
    } : undefined,
  });

  const handleFormSubmit = async (data: ConfigurationFormData) => {
    await onSubmit(data);
    reset();
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? 'Add New Configuration' : 'Edit Configuration'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          <div>
            <Label htmlFor="category">Category *</Label>
            <Select
              defaultValue={mode === 'edit' && configuration ? configuration.category : undefined}
              onValueChange={(value) => setValue('category', value as ConfigCategory)}
              disabled={mode === 'edit'}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select category" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="GENERAL">General</SelectItem>
                <SelectItem value="ACADEMIC">Academic</SelectItem>
                <SelectItem value="FINANCE">Finance</SelectItem>
                <SelectItem value="SYSTEM">System</SelectItem>
              </SelectContent>
            </Select>
            {errors.category && (
              <p className="text-red-500 text-sm mt-1">
                {errors.category.message}
              </p>
            )}
          </div>

          <div>
            <Label htmlFor="key">Key *</Label>
            <Input
              id="key"
              {...register('key')}
              placeholder="SCHOOL_NAME"
              className={errors.key ? 'border-red-500' : ''}
              disabled={mode === 'edit'}
            />
            <p className="text-xs text-gray-500 mt-1">
              Uppercase letters, numbers, and underscores only
            </p>
            {errors.key && (
              <p className="text-red-500 text-sm mt-1">{errors.key.message}</p>
            )}
          </div>

          <div>
            <Label htmlFor="value">Value *</Label>
            <Textarea
              id="value"
              {...register('value')}
              rows={3}
              className={errors.value ? 'border-red-500' : ''}
            />
            {errors.value && (
              <p className="text-red-500 text-sm mt-1">{errors.value.message}</p>
            )}
          </div>

          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              {...register('description')}
              rows={2}
              className={errors.description ? 'border-red-500' : ''}
            />
            {errors.description && (
              <p className="text-red-500 text-sm mt-1">
                {errors.description.message}
              </p>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Saving...' : mode === 'create' ? 'Create' : 'Update'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
```

**Screenshot Validation Checklist:**
- Field layout matches
- Category and Key disabled in edit mode
- dataType field NOT visible (as per spec)
- Form max-width: max-w-md

**Success Criteria:**
- Dialog matches screenshot
- Create mode allows all fields
- Edit mode disables category and key
- dataType field hidden

**Dependencies:** FE-003, FE-008

---

### [FE-021] ConfigurationsPage Implementation

**Goal:** Create ConfigurationsPage with table view.

**Reference:** `screenshots/configurations-page.png`

**Technical Details:**

**src/pages/ConfigurationsPage.tsx:**
```typescript
import { useEffect, useState } from 'react';
import { useConfigurations } from '../hooks/useConfigurations';
import { Configuration, ConfigCategory, ConfigurationUpdateRequest } from '../types/configuration';
import { ConfigurationDialog } from '../components/configurations/ConfigurationDialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { formatDate } from '../utils/formatting';

export function ConfigurationsPage() {
  const { configurations, loading, loadConfigurations, upsertConfiguration, deleteConfiguration } = useConfigurations();
  const [categoryFilter, setCategoryFilter] = useState<ConfigCategory | 'ALL'>('ALL');
  const [dialogState, setDialogState] = useState<{
    mode: 'create' | 'edit' | null;
    configuration?: Configuration;
  }>({ mode: null });

  useEffect(() => {
    loadConfigurations(
      categoryFilter !== 'ALL' ? categoryFilter : undefined
    );
  }, [categoryFilter, loadConfigurations]);

  const handleUpsert = async (data: any) => {
    if (dialogState.mode === 'edit' && dialogState.configuration) {
      await upsertConfiguration(
        dialogState.configuration.category,
        dialogState.configuration.key,
        {
          value: data.value,
          description: data.description,
        }
      );
    } else {
      await upsertConfiguration(data.category, data.key, {
        value: data.value,
        description: data.description,
      });
    }
    setDialogState({ mode: null });
  };

  const handleDelete = async (category: ConfigCategory, key: string) => {
    if (window.confirm('Are you sure you want to delete this configuration?')) {
      await deleteConfiguration(category, key);
    }
  };

  const getCategoryColor = (category: ConfigCategory) => {
    const colors = {
      GENERAL: 'bg-blue-100 text-blue-800',
      ACADEMIC: 'bg-purple-100 text-purple-800',
      FINANCE: 'bg-green-100 text-green-800',
      SYSTEM: 'bg-gray-100 text-gray-800',
    };
    return colors[category];
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">System Configurations</h1>
        <Button onClick={() => setDialogState({ mode: 'create' })}>
          <Plus className="h-4 w-4 mr-2" />
          Add New Configuration
        </Button>
      </div>

      {/* Category Filter */}
      <div className="flex gap-4">
        <Select value={categoryFilter} onValueChange={(value: any) => setCategoryFilter(value)}>
          <SelectTrigger className="w-[200px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All Categories</SelectItem>
            <SelectItem value="GENERAL">General</SelectItem>
            <SelectItem value="ACADEMIC">Academic</SelectItem>
            <SelectItem value="FINANCE">Finance</SelectItem>
            <SelectItem value="SYSTEM">System</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Configurations Table */}
      {loading ? (
        <div className="space-y-2">
          {[1, 2, 3, 4, 5].map((i) => (
            <Skeleton key={i} className="h-16" />
          ))}
        </div>
      ) : configurations.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          No configurations found
        </div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">
                  Category
                </th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">
                  Key
                </th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">
                  Value
                </th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">
                  Description
                </th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">
                  Last Updated
                </th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y">
              {configurations.map((config) => (
                <tr key={`${config.category}-${config.key}`} className="hover:bg-gray-50">
                  <td className="px-4 py-3">
                    <Badge className={getCategoryColor(config.category)}>
                      {config.category}
                    </Badge>
                  </td>
                  <td className="px-4 py-3 text-sm font-medium">{config.key}</td>
                  <td className="px-4 py-3 text-sm text-gray-600 max-w-xs truncate">
                    {config.value}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-600 max-w-xs truncate">
                    {config.description || '-'}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    {formatDate(config.lastUpdated)}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex justify-end gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => setDialogState({ mode: 'edit', configuration: config })}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="destructive"
                        onClick={() => handleDelete(config.category, config.key)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Dialog */}
      {dialogState.mode && (
        <ConfigurationDialog
          isOpen={true}
          onClose={() => setDialogState({ mode: null })}
          onSubmit={handleUpsert}
          mode={dialogState.mode}
          configuration={dialogState.configuration}
        />
      )}
    </div>
  );
}
```

**Screenshot Validation Checklist:**
- Category filter dropdown
- Table columns: Category, Key, Value, Description, Last Updated, Actions
- Category badge colors: Blue (GENERAL), Purple (ACADEMIC), Green (FINANCE), Gray (SYSTEM)
- Action buttons: Edit, Delete
- "Add New Configuration" button

**Success Criteria:**
- Page matches screenshot
- Table layout correct
- Category filtering functional
- CRUD operations working

**Dependencies:** FE-012, FE-020

---

### [FE-022] App Routing Configuration

**Goal:** Setup React Router with all pages.

**Technical Details:**

**src/App.tsx:**
```typescript
import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Layout } from './components/layout/Layout';
import { AppProvider } from './contexts/AppContext';

const HomePage = lazy(() => import('./pages/HomePage').then(m => ({ default: m.HomePage })));
const StudentsPage = lazy(() => import('./pages/StudentsPage').then(m => ({ default: m.StudentsPage })));
const ConfigurationsPage = lazy(() => import('./pages/ConfigurationsPage').then(m => ({ default: m.ConfigurationsPage })));

function App() {
  return (
    <BrowserRouter>
      <AppProvider>
        <Layout>
          <Suspense fallback={<div className="text-center py-12">Loading...</div>}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/students" element={<StudentsPage />} />
              <Route path="/configurations" element={<ConfigurationsPage />} />
            </Routes>
          </Suspense>
        </Layout>
      </AppProvider>
    </BrowserRouter>
  );
}

export default App;
```

**src/main.tsx:**
```typescript
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './styles/globals.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

**Success Criteria:**
- All routes functional
- Code splitting working
- Lazy loading active

**Dependencies:** FE-010, FE-014, FE-015, FE-019, FE-021

---

## Testing & Optimization

### [FE-023] Debounced Search Hook

**Goal:** Create useDebounce hook for search optimization.

**Technical Details:**

**src/hooks/useDebounce.ts:**
```typescript
import { useEffect, useState } from 'react';

export const useDebounce = <T>(value: T, delay: number = 300): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
};
```

**Success Criteria:**
- Hook delays value update by specified ms
- Cleanup on unmount

**Dependencies:** FE-001

---

### [FE-024] Screenshot Validation Testing

**Goal:** Validate all pages match screenshots pixel-perfectly.

**Validation Process:**
1. Start dev server: `npm run dev`
2. Navigate to each page
3. Compare side-by-side with screenshot
4. Check using DevTools:
   - Text content
   - Color values (use color picker)
   - Spacing (margin/padding)
   - Font sizes
   - Icon sizes
5. Document deviations

**Pages to Validate:**
- HomePage: `screenshots/homepage.png`
- StudentsPage: `screenshots/students-page.png`
- Student Create Dialog: `screenshots/student-dialog-create.png`
- Student Edit Dialog: `screenshots/student-dialog-edit.png`
- Student View Dialog: `screenshots/student-dialog-view.png`
- ConfigurationsPage: `screenshots/configurations-page.png`
- Configuration Add Dialog: `screenshots/configuration-dialog-add.png`
- Configuration Edit Dialog: `screenshots/configuration-dialog-edit.png`

**Success Criteria:**
- All pages match screenshots
- Colors exact
- Spacing exact
- Typography exact

**Dependencies:** All component tasks

---

### [FE-025] Manual End-to-End Testing

**Goal:** Test all user workflows manually.

**Test Scenarios:**
1. Student Registration Flow
   - Open StudentsPage
   - Click "Register New Student"
   - Fill all fields with valid data
   - Submit form
   - Verify student appears in list
   - Verify toast notification

2. Student Edit Flow
   - Click Edit on a student
   - Change firstName, lastName, phone, status
   - Verify immutable fields disabled
   - Submit form
   - Verify changes reflected

3. Student Delete Flow
   - Click Delete on a student
   - Confirm deletion
   - Verify student removed from list

4. Student Search
   - Enter search term
   - Verify filtering works (ID, name, guardian)
   - Clear search
   - Verify all students show

5. Configuration CRUD
   - Create new configuration
   - Edit configuration
   - Delete configuration
   - Filter by category

6. Error Handling
   - Submit invalid data
   - Verify validation errors display
   - Trigger API error
   - Verify toast error notification

**Success Criteria:**
- All workflows functional
- No console errors
- All validation working
- Error handling working

**Dependencies:** All tasks

---

## Final Verification

### [FE-026] Build Verification

**Goal:** Verify production build works.

**Commands:**
```bash
npm run build
npm run preview
```

**Success Criteria:**
- Build completes without errors
- Preview server runs
- All pages load correctly
- No runtime errors

**Dependencies:** All tasks

---

### [FE-027] Responsive Design Testing

**Goal:** Verify responsive design on all breakpoints.

**Breakpoints to Test:**
- Mobile: 375px, 414px
- Tablet: 768px, 1024px
- Desktop: 1280px, 1920px

**Pages to Test:**
- HomePage
- StudentsPage
- ConfigurationsPage

**Success Criteria:**
- All pages responsive
- Grid layouts adjust correctly
- No horizontal scrolling
- Touch targets meet 44x44px minimum

**Dependencies:** All component tasks

---

## Task Summary

**Total Tasks:** 27

**Breakdown:**
- Project Foundation: 5 tasks
- Service Layer: 2 tasks
- Validation Layer: 2 tasks
- Component Implementation: 12 tasks
- Testing & Optimization: 3 tasks
- Final Verification: 3 tasks

**Critical Path:**
FE-001 → FE-002 → FE-003 → FE-004 → FE-005 → FE-006 → FE-008 → FE-011 → FE-017 → FE-019 → FE-024

**Estimated Effort:** 4-6 days for senior frontend developer

---

## References

- Design Spec: `specs/FRONTEND_DESIGN_SPECIFICATION.md`
- Architecture: `specs/architecture/06-frontend-implementation-guide.md`
- Screenshots: `screenshots/` directory
- API Spec: `specs/sms_api_specification.yaml`
- Backend Field Mapping: Global Directive D-007

---

**Document Status:** READY FOR FRONTEND DEVELOPER AGENT
**Next Phase:** Frontend QA (frontend-qa-orchestrator)
