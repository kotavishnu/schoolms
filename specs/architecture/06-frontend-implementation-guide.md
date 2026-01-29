# Frontend Implementation Guidelines
**School Management System (SMS) - Developer Agent Reference**
Version: 1.0.0
Last Updated: 2026-01-28

---

## 1. Overview

This document provides **mandatory** implementation patterns for the Frontend Developer Agent. The frontend must achieve 1:1 visual fidelity with the reference code while integrating backend APIs.

### 1.1 Strict Constraints

**CRITICAL RULES**:
1. **NO NEW STYLES**: Do not create custom CSS, SASS, or styled-components
2. **REFERENCE CODE ONLY**: Copy/paste UI components, layouts, and themes EXACTLY from `frontend/reference-code/`
3. **NO DEVIATIONS**: Use existing `shadcn/ui` components without modifications
4. **FIGMA TOKEN COMPLIANCE**: All design tokens must come from `styles/theme.css`

### 1.2 Technology Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Framework** | React | 18 | Component-based UI |
| **Language** | TypeScript | 5+ | Type safety |
| **Build Tool** | Vite | 5+ | Fast development server |
| **Styling** | Tailwind CSS | 4+ | Utility-first CSS |
| **Components** | shadcn/ui | Latest | Pre-built accessible components |
| **Forms** | React Hook Form | 7+ | Form state management |
| **Validation** | Zod | 3+ | Schema validation |
| **HTTP Client** | Axios | 1.6+ | API requests |
| **Routing** | React Router | 6+ | Client-side routing |
| **Notifications** | Sonner | Latest | Toast notifications |

---

## 2. Project Structure

### 2.1 Directory Layout

```
frontend/
├── public/
│   ├── logo.svg
│   └── favicon.ico
├── src/
│   ├── app/
│   │   ├── App.tsx                  # Root component
│   │   ├── components/
│   │   │   ├── Header.tsx
│   │   │   ├── HomePage.tsx
│   │   │   ├── StudentsPage.tsx
│   │   │   ├── StudentDialog.tsx
│   │   │   ├── ViewStudentDialog.tsx
│   │   │   ├── ConfigurationsPage.tsx
│   │   │   ├── ConfigurationDialog.tsx
│   │   │   └── ui/                  # shadcn/ui components (DO NOT MODIFY)
│   │   │       ├── button.tsx
│   │   │       ├── card.tsx
│   │   │       ├── dialog.tsx
│   │   │       ├── form.tsx
│   │   │       ├── input.tsx
│   │   │       ├── label.tsx
│   │   │       ├── select.tsx
│   │   │       ├── table.tsx
│   │   │       └── ...
│   │   ├── types/
│   │   │   └── index.ts             # Domain types
│   │   └── lib/
│   │       └── utils.ts             # Utility functions
│   ├── services/
│   │   ├── api/
│   │   │   ├── client.ts            # Axios configuration
│   │   │   ├── studentApi.ts        # Student API calls
│   │   │   └── configApi.ts         # Configuration API calls
│   │   └── validation/
│   │       ├── studentSchema.ts     # Zod schemas
│   │       └── configSchema.ts
│   ├── hooks/
│   │   ├── useStudents.ts           # Student data fetching
│   │   ├── useConfigurations.ts     # Configuration data fetching
│   │   └── useToast.ts              # Toast notifications
│   ├── context/
│   │   └── AppContext.tsx           # Global state (if needed)
│   ├── styles/
│   │   ├── index.css                # Global styles
│   │   ├── tailwind.css             # Tailwind directives
│   │   └── theme.css                # Figma design tokens
│   └── main.tsx                     # Entry point
├── index.html
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── package.json
└── Dockerfile
```

---

## 3. Reference Code Usage

### 3.1 Copying Components

**DO**:
```typescript
// ✅ Copy EXACTLY from reference-code/app/components/StudentsPage.tsx
import { useState } from 'react';
import { Phone, Calendar, Mail, Trash2, Edit, Eye } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
// ... rest of imports exactly as in reference

export function StudentsPage() {
  // Copy implementation exactly
}
```

**DON'T**:
```typescript
// ❌ DO NOT create custom styles
const StyledCard = styled.div`
  background: linear-gradient(...);
`;

// ❌ DO NOT modify shadcn/ui components
<Button className="custom-fancy-button">...</Button>
```

### 3.2 Theme Consumption

**Use Existing CSS Variables**:
```typescript
// ✅ Use Tailwind classes that reference theme.css variables
<div className="bg-background text-foreground">
  <h1 className="text-2xl font-medium">Students</h1>
  <Button className="bg-primary text-primary-foreground">Register</Button>
</div>

// ❌ DO NOT hardcode colors
<div style={{ backgroundColor: '#030213' }}>
```

---

## 4. API Integration

### 4.1 Axios Client Configuration

**File**: `src/services/api/client.ts`

```typescript
import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import Cookies from 'js-cookie';

const STUDENT_API_BASE_URL = import.meta.env.VITE_STUDENT_API_URL || 'http://localhost:8081/api/v1';
const CONFIG_API_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082/api/v1';

// Student Service Client
export const studentApiClient: AxiosInstance = axios.create({
  baseURL: STUDENT_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable cookies for CSRF
});

// Configuration Service Client
export const configApiClient: AxiosInstance = axios.create({
  baseURL: CONFIG_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Request Interceptor: Add CSRF token and correlation ID
const requestInterceptor = (config: InternalAxiosRequestConfig) => {
  // Add CSRF token for state-changing requests
  const csrfToken = Cookies.get('XSRF-TOKEN');
  if (csrfToken && ['post', 'put', 'delete', 'patch'].includes(config.method || '')) {
    config.headers['X-XSRF-TOKEN'] = csrfToken;
  }

  // Add correlation ID for distributed tracing
  config.headers['X-Correlation-ID'] = crypto.randomUUID();

  // Add timestamp for debugging
  console.log(`[${new Date().toISOString()}] ${config.method?.toUpperCase()} ${config.url}`);

  return config;
};

// Response Interceptor: Handle errors globally
const responseErrorInterceptor = (error: AxiosError) => {
  if (error.response) {
    // Server responded with error
    const status = error.response.status;
    const data = error.response.data as any;

    console.error(`API Error [${status}]:`, data);

    switch (status) {
      case 400:
        // Validation errors - let component handle
        break;
      case 401:
        // Unauthorized - redirect to login (future)
        window.location.href = '/login';
        break;
      case 403:
        // Forbidden - show toast
        console.error('Access denied');
        break;
      case 404:
        // Not found - let component handle
        break;
      case 409:
        // Conflict (e.g., duplicate mobile) - let component handle
        break;
      case 500:
        // Server error - show generic toast
        console.error('Server error occurred');
        break;
      default:
        console.error('Unexpected error occurred');
    }
  } else if (error.request) {
    // Request made but no response
    console.error('Network error - server unreachable');
  } else {
    // Something else happened
    console.error('Request setup error:', error.message);
  }

  return Promise.reject(error);
};

// Apply interceptors
studentApiClient.interceptors.request.use(requestInterceptor);
studentApiClient.interceptors.response.use(
  (response) => response,
  responseErrorInterceptor
);

configApiClient.interceptors.request.use(requestInterceptor);
configApiClient.interceptors.response.use(
  (response) => response,
  responseErrorInterceptor
);
```

### 4.2 Student API Service

**File**: `src/services/api/studentApi.ts`

```typescript
import { studentApiClient } from './client';
import { Student, Enrollment } from '../../app/types';

export interface StudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 format (YYYY-MM-DD)
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
}

export interface StudentResponse {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export const studentApi = {
  // Create new student
  async createStudent(data: StudentRequest): Promise<StudentResponse> {
    const response = await studentApiClient.post<StudentResponse>('/students', data);
    return response.data;
  },

  // Get student by ID
  async getStudent(studentId: string): Promise<StudentResponse> {
    const response = await studentApiClient.get<StudentResponse>(`/students/${studentId}`);
    return response.data;
  },

  // Search students with pagination
  async searchStudents(params: {
    lastName?: string;
    status?: 'ACTIVE' | 'INACTIVE';
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
  }): Promise<PaginatedResponse<StudentResponse>> {
    const response = await studentApiClient.get<PaginatedResponse<StudentResponse>>('/students', {
      params,
    });
    return response.data;
  },

  // Update student (only allowed fields)
  async updateStudent(
    studentId: string,
    data: {
      firstName: string;
      lastName: string;
      mobile: string;
      status: 'ACTIVE' | 'INACTIVE';
      version: number;
    }
  ): Promise<StudentResponse> {
    const response = await studentApiClient.put<StudentResponse>(`/students/${studentId}`, data);
    return response.data;
  },

  // Delete student
  async deleteStudent(studentId: string): Promise<void> {
    await studentApiClient.delete(`/students/${studentId}`);
  },

  // Get enrollment history
  async getEnrollmentHistory(studentId: string): Promise<{ studentId: string; enrollments: Enrollment[] }> {
    const response = await studentApiClient.get(`/students/${studentId}/enrollment-history`);
    return response.data;
  },

  // Create enrollment
  async createEnrollment(
    studentId: string,
    data: {
      academicYear: string;
      gradeClass: string;
      section: string;
      enrollmentDate: string;
      remarks?: string;
    }
  ): Promise<Enrollment> {
    const response = await studentApiClient.post(`/students/${studentId}/enrollment-history`, data);
    return response.data;
  },
};
```

### 4.3 Configuration API Service

**File**: `src/services/api/configApi.ts`

```typescript
import { configApiClient } from './client';

export interface Configuration {
  id: number;
  category: 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';
  key: string;
  value: string;
  description?: string;
  dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  isEncrypted: boolean;
  version: number;
  updatedAt: string;
}

export const configApi = {
  // Get all configurations (optionally filtered by category)
  async getAllConfigurations(category?: string): Promise<{ configurations: Configuration[] }> {
    const response = await configApiClient.get('/configurations', {
      params: category ? { category } : undefined,
    });
    return response.data;
  },

  // Get specific configuration
  async getConfiguration(category: string, key: string): Promise<Configuration> {
    const response = await configApiClient.get(`/configurations/${category}/${key}`);
    return response.data;
  },

  // Create or update configuration (upsert)
  async upsertConfiguration(
    category: string,
    key: string,
    data: {
      value: string;
      description?: string;
      dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
      isEncrypted?: boolean;
    }
  ): Promise<Configuration> {
    const response = await configApiClient.put(`/configurations/${category}/${key}`, data);
    return response.data;
  },

  // Delete configuration
  async deleteConfiguration(category: string, key: string): Promise<void> {
    await configApiClient.delete(`/configurations/${category}/${key}`);
  },

  // Get grouped configurations
  async getGroupedConfigurations(category: string): Promise<{ category: string; settings: Record<string, string> }> {
    const response = await configApiClient.get(`/configurations/grouped/${category}`);
    return response.data;
  },
};
```

---

## 5. Form Handling & Validation

### 5.1 Zod Schema Definition

**File**: `src/services/validation/studentSchema.ts`

```typescript
import { z } from 'zod';

export const studentSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name must not exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name must not exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Name must contain only letters and spaces'),

  dateOfBirth: z
    .date()
    .max(new Date(), 'Date of birth must be in the past')
    .refine(
      (date) => {
        const age = new Date().getFullYear() - date.getFullYear();
        return age >= 3 && age <= 18;
      },
      { message: 'Student must be between 3 and 18 years old' }
    ),

  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),

  email: z
    .string()
    .email('Invalid email format')
    .optional()
    .or(z.literal('')),

  address: z
    .string()
    .max(500, 'Address must not exceed 500 characters')
    .optional()
    .or(z.literal('')),

  fathersName: z
    .string()
    .max(100)
    .optional()
    .or(z.literal('')),

  mothersName: z
    .string()
    .max(100)
    .optional()
    .or(z.literal('')),

  identificationMark: z
    .string()
    .max(200)
    .optional()
    .or(z.literal('')),

  aadhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export type StudentFormData = z.infer<typeof studentSchema>;

// Update schema (only editable fields)
export const studentUpdateSchema = z.object({
  firstName: z
    .string()
    .min(2)
    .max(100)
    .regex(/^[a-zA-Z\s]+$/),

  lastName: z
    .string()
    .min(2)
    .max(100)
    .regex(/^[a-zA-Z\s]+$/),

  mobile: z
    .string()
    .regex(/^\d{10}$/),

  status: z.enum(['ACTIVE', 'INACTIVE']),
});

export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
```

### 5.2 React Hook Form Integration

**Example: Student Registration Form**

```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema, StudentFormData } from '../services/validation/studentSchema';
import { studentApi } from '../services/api/studentApi';
import { toast } from 'sonner';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from './ui/form';

export function StudentDialog({ open, onOpenChange, onSuccess }: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess: () => void;
}) {
  const form = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      mobile: '',
      email: '',
      address: '',
      fathersName: '',
      mothersName: '',
      identificationMark: '',
      aadhaarNumber: '',
    },
  });

  const onSubmit = async (data: StudentFormData) => {
    try {
      // Convert Date to ISO string for API
      const apiData = {
        ...data,
        dateOfBirth: data.dateOfBirth.toISOString().split('T')[0],
      };

      await studentApi.createStudent(apiData);
      toast.success('Student registered successfully');
      form.reset();
      onOpenChange(false);
      onSuccess();
    } catch (error: any) {
      // Handle API errors
      if (error.response?.status === 409) {
        toast.error('Mobile number already registered');
      } else if (error.response?.status === 400) {
        const errors = error.response.data.errors || [];
        errors.forEach((err: any) => {
          toast.error(err.message || err);
        });
      } else {
        toast.error('Failed to register student');
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Register New Student</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>First Name *</FormLabel>
                    <FormControl>
                      <Input placeholder="John" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Last Name *</FormLabel>
                    <FormControl>
                      <Input placeholder="Doe" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            {/* More form fields... */}

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                Cancel
              </Button>
              <Button type="submit" disabled={form.formState.isSubmitting}>
                {form.formState.isSubmitting ? 'Registering...' : 'Register Student'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
```

---

## 6. State Management

### 6.1 Local State (React Hooks)

**Use for component-specific state**:
```typescript
export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    lastName: '',
    status: 'all',
  });

  useEffect(() => {
    fetchStudents();
  }, [searchParams]);

  const fetchStudents = async () => {
    setLoading(true);
    try {
      const response = await studentApi.searchStudents({
        lastName: searchParams.lastName || undefined,
        status: searchParams.status !== 'all' ? searchParams.status : undefined,
      });
      setStudents(response.content);
    } catch (error) {
      toast.error('Failed to fetch students');
    } finally {
      setLoading(false);
    }
  };

  return (
    // Component JSX
  );
}
```

### 6.2 Custom Hooks (Reusable Logic)

**File**: `src/hooks/useStudents.ts`

```typescript
import { useState, useEffect, useCallback } from 'react';
import { studentApi, StudentResponse } from '../services/api/studentApi';
import { toast } from 'sonner';

export function useStudents(initialParams?: {
  lastName?: string;
  status?: 'ACTIVE' | 'INACTIVE';
}) {
  const [students, setStudents] = useState<StudentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchStudents = useCallback(async (params?: typeof initialParams) => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentApi.searchStudents(params || {});
      setStudents(response.content);
    } catch (err: any) {
      const message = err.response?.data?.detail || 'Failed to fetch students';
      setError(message);
      toast.error(message);
    } finally {
      setLoading(false);
    }
  }, []);

  const createStudent = useCallback(async (data: any) => {
    try {
      await studentApi.createStudent(data);
      toast.success('Student registered successfully');
      await fetchStudents(initialParams);
      return true;
    } catch (err: any) {
      if (err.response?.status === 409) {
        toast.error('Mobile number already registered');
      } else {
        toast.error('Failed to register student');
      }
      return false;
    }
  }, [fetchStudents, initialParams]);

  const updateStudent = useCallback(async (studentId: string, data: any) => {
    try {
      await studentApi.updateStudent(studentId, data);
      toast.success('Student updated successfully');
      await fetchStudents(initialParams);
      return true;
    } catch (err: any) {
      toast.error('Failed to update student');
      return false;
    }
  }, [fetchStudents, initialParams]);

  const deleteStudent = useCallback(async (studentId: string) => {
    try {
      await studentApi.deleteStudent(studentId);
      toast.success('Student deleted successfully');
      await fetchStudents(initialParams);
      return true;
    } catch (err: any) {
      toast.error('Failed to delete student');
      return false;
    }
  }, [fetchStudents, initialParams]);

  useEffect(() => {
    fetchStudents(initialParams);
  }, [fetchStudents, initialParams]);

  return {
    students,
    loading,
    error,
    fetchStudents,
    createStudent,
    updateStudent,
    deleteStudent,
  };
}
```

---

## 7. Routing

**File**: `src/app/App.tsx`

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from './components/ui/sonner';
import { Header } from './components/Header';
import { HomePage } from './components/HomePage';
import { StudentsPage } from './components/StudentsPage';
import { ConfigurationsPage } from './components/ConfigurationsPage';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-background">
        <Header />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/students" element={<StudentsPage />} />
          <Route path="/configurations" element={<ConfigurationsPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        <Toaster position="top-right" richColors />
      </div>
    </BrowserRouter>
  );
}

export default App;
```

---

## 8. Error Handling

### 8.1 API Error Display

```typescript
// Display backend validation errors
if (error.response?.status === 400) {
  const problemDetail = error.response.data;

  if (problemDetail.errors && Array.isArray(problemDetail.errors)) {
    problemDetail.errors.forEach((err: any) => {
      if (err.field) {
        // Set form field error
        form.setError(err.field, {
          type: 'manual',
          message: err.message,
        });
      } else {
        // Show as toast
        toast.error(err.message || err);
      }
    });
  } else {
    toast.error(problemDetail.detail || 'Validation failed');
  }
}
```

### 8.2 Network Error Handling

```typescript
// In API service or component
try {
  const response = await studentApi.createStudent(data);
} catch (error: any) {
  if (error.code === 'ERR_NETWORK') {
    toast.error('Network error - please check your connection');
  } else if (error.code === 'ECONNABORTED') {
    toast.error('Request timeout - please try again');
  } else {
    toast.error('An unexpected error occurred');
  }
}
```

---

## 9. Performance Optimization

### 9.1 Code Splitting

```typescript
import { lazy, Suspense } from 'react';
import { Skeleton } from './components/ui/skeleton';

const StudentsPage = lazy(() => import('./components/StudentsPage'));
const ConfigurationsPage = lazy(() => import('./components/ConfigurationsPage'));

function App() {
  return (
    <Suspense fallback={<Skeleton className="h-screen" />}>
      <Routes>
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/configurations" element={<ConfigurationsPage />} />
      </Routes>
    </Suspense>
  );
}
```

### 9.2 Memoization

```typescript
import { useMemo, useCallback } from 'react';

export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [searchTerm, setSearchTerm] = useState('');

  // Memoize filtered results
  const filteredStudents = useMemo(() => {
    return students.filter((student) =>
      student.lastName.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [students, searchTerm]);

  // Memoize callback function
  const handleDelete = useCallback((studentId: string) => {
    if (confirm('Are you sure?')) {
      deleteStudent(studentId);
    }
  }, []);

  return (
    // Component JSX
  );
}
```

### 9.3 Image Optimization

```typescript
import { useState } from 'react';

export function ImageWithFallback({ src, alt, fallback }: {
  src: string;
  alt: string;
  fallback: string;
}) {
  const [imgSrc, setImgSrc] = useState(src);

  return (
    <img
      src={imgSrc}
      alt={alt}
      onError={() => setImgSrc(fallback)}
      loading="lazy"
      className="w-full h-auto"
    />
  );
}
```

---

## 10. Testing

### 10.1 Component Testing (Vitest + React Testing Library)

```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { StudentsPage } from './StudentsPage';
import { studentApi } from '../services/api/studentApi';

vi.mock('../services/api/studentApi');

describe('StudentsPage', () => {
  it('should render students list', async () => {
    const mockStudents = [
      {
        id: 1,
        studentId: 'STD-001',
        firstName: 'John',
        lastName: 'Doe',
        status: 'ACTIVE',
      },
    ];

    vi.mocked(studentApi.searchStudents).mockResolvedValue({
      content: mockStudents,
      pageable: { page: 0, size: 20, totalElements: 1, totalPages: 1 },
    });

    render(<StudentsPage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
  });

  it('should open registration dialog on button click', () => {
    render(<StudentsPage />);

    const registerButton = screen.getByRole('button', { name: /register new student/i });
    fireEvent.click(registerButton);

    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });
});
```

### 10.2 API Mocking (MSW)

**File**: `src/mocks/handlers.ts`

```typescript
import { http, HttpResponse } from 'msw';

export const handlers = [
  // Mock GET /students
  http.get('http://localhost:8081/api/v1/students', () => {
    return HttpResponse.json({
      content: [
        {
          id: 1,
          studentId: 'STD-20260128-0001',
          firstName: 'John',
          lastName: 'Doe',
          status: 'ACTIVE',
        },
      ],
      pageable: { page: 0, size: 20, totalElements: 1, totalPages: 1 },
    });
  }),

  // Mock POST /students
  http.post('http://localhost:8081/api/v1/students', async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: 1,
        studentId: 'STD-20260128-0001',
        ...body,
        status: 'ACTIVE',
        version: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      { status: 201 }
    );
  }),
];
```

---

## 11. Build & Deployment

### 11.1 Environment Variables

**File**: `.env.development`
```env
VITE_STUDENT_API_URL=http://localhost:8081/api/v1
VITE_CONFIG_API_URL=http://localhost:8082/api/v1
```

**File**: `.env.production`
```env
VITE_STUDENT_API_URL=https://api.school.com/student-service/api/v1
VITE_CONFIG_API_URL=https://api.school.com/config-service/api/v1
```

### 11.2 Vite Configuration

**File**: `vite.config.ts`

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api/v1/students': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/v1/configurations': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          ui: ['@radix-ui/react-dialog', '@radix-ui/react-select'],
        },
      },
    },
  },
});
```

### 11.3 Docker Configuration

**Dockerfile**:
```dockerfile
# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
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

    # Gzip compression
    gzip on;
    gzip_types text/css application/javascript application/json image/svg+xml;
    gzip_min_length 1000;

    # SPA fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy (optional)
    location /api/v1/students {
        proxy_pass http://student-service:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/configurations {
        proxy_pass http://config-service:8082;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

---

## 12. Accessibility

### 12.1 ARIA Attributes

```typescript
<Button
  aria-label="Delete student"
  aria-describedby="delete-description"
  onClick={handleDelete}
>
  <Trash2 className="w-4 h-4" />
  <span id="delete-description" className="sr-only">
    Permanently delete this student record
  </span>
</Button>
```

### 12.2 Keyboard Navigation

```typescript
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent
    onKeyDown={(e) => {
      if (e.key === 'Escape') {
        onOpenChange(false);
      }
    }}
  >
    {/* Dialog content */}
  </DialogContent>
</Dialog>
```

---

## 13. Code Quality

### 13.1 ESLint Configuration

**File**: `.eslintrc.cjs`

```javascript
module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react-hooks/recommended',
  ],
  parser: '@typescript-eslint/parser',
  plugins: ['react-refresh'],
  rules: {
    'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],
    '@typescript-eslint/no-explicit-any': 'warn',
    'no-console': ['warn', { allow: ['warn', 'error'] }],
  },
};
```

### 13.2 Prettier Configuration

**File**: `.prettierrc`

```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 120,
  "tabWidth": 2,
  "arrowParens": "always"
}
```

---

## 14. Mandatory Checklist

### Pre-Commit Checklist

- [ ] All components copied from `reference-code/` without modifications
- [ ] No custom CSS classes created (only Tailwind utilities used)
- [ ] All API calls go through `services/api/` layer
- [ ] All forms use React Hook Form + Zod validation
- [ ] Error handling implemented with toast notifications
- [ ] Loading states displayed during API calls
- [ ] TypeScript types aligned with backend DTOs
- [ ] No `any` types without justification
- [ ] Accessibility attributes added to interactive elements
- [ ] Component tested with sample data

### Pre-Deployment Checklist

- [ ] Environment variables configured for production
- [ ] `npm run build` executes successfully
- [ ] Docker image builds without errors
- [ ] All API endpoints return expected responses
- [ ] CORS configured correctly on backend
- [ ] Error boundaries implemented for runtime errors
- [ ] Performance tested (Lighthouse score >90)
- [ ] Browser compatibility verified (Chrome, Firefox, Safari)

---

**Document Status**: Final
**Next Review**: 2026-04-28
