# Frontend Implementation Guide - School Management System

## 1. Overview

This guide provides strict implementation patterns for the Frontend Developer Agent to build the React-based School Management System UI. All implementations must adhere to the design token system from `FRONTEND_DESIGN_SPEC.md` and integrate with the backend APIs defined in `sms_api_specification.yaml`.

## 2. Project Structure

### 2.1 Directory Layout

```
frontend/
├── public/
│   ├── index.html
│   └── assets/
│       └── logo.svg
├── src/
│   ├── main.tsx                      # Application entry point
│   ├── App.tsx                       # Root component
│   ├── components/                   # Reusable UI components
│   │   ├── ui/                       # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   ├── input.tsx
│   │   │   ├── select.tsx
│   │   │   ├── table.tsx
│   │   │   ├── dialog.tsx
│   │   │   └── toast.tsx
│   │   ├── layout/
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── MainLayout.tsx
│   │   └── shared/
│   │       ├── LoadingSpinner.tsx
│   │       ├── ErrorBoundary.tsx
│   │       └── Pagination.tsx
│   ├── features/                     # Feature modules
│   │   ├── students/
│   │   │   ├── pages/
│   │   │   │   ├── StudentListPage.tsx
│   │   │   │   ├── StudentDetailPage.tsx
│   │   │   │   ├── StudentCreatePage.tsx
│   │   │   │   └── StudentEditPage.tsx
│   │   │   ├── components/
│   │   │   │   ├── StudentForm.tsx
│   │   │   │   ├── StudentTable.tsx
│   │   │   │   ├── StudentCard.tsx
│   │   │   │   └── EnrollmentHistory.tsx
│   │   │   ├── hooks/
│   │   │   │   ├── useStudents.ts
│   │   │   │   ├── useStudentForm.ts
│   │   │   │   └── useStudentValidation.ts
│   │   │   └── schemas/
│   │   │       └── studentSchema.ts
│   │   └── configuration/
│   │       ├── pages/
│   │       │   └── ConfigurationPage.tsx
│   │       ├── components/
│   │       │   └── ConfigurationForm.tsx
│   │       └── hooks/
│   │           └── useConfiguration.ts
│   ├── services/                     # API integration
│   │   ├── api/
│   │   │   ├── axios.config.ts
│   │   │   ├── student.api.ts
│   │   │   ├── configuration.api.ts
│   │   │   └── interceptors.ts
│   │   └── types/
│   │       ├── student.types.ts
│   │       ├── configuration.types.ts
│   │       └── api.types.ts
│   ├── context/                      # Global state
│   │   ├── AuthContext.tsx           # (Future - Phase 2)
│   │   └── ToastContext.tsx
│   ├── hooks/                        # Shared hooks
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   └── usePagination.ts
│   ├── utils/                        # Utility functions
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   └── constants.ts
│   ├── styles/                       # Styling
│   │   ├── theme.css                 # Figma tokens (Tier 1 + 2)
│   │   └── tailwind.css              # Tailwind imports
│   └── router/
│       └── index.tsx                 # React Router config
├── reference-code/                   # Figma-generated components (READ ONLY)
│   └── (Existing reference components)
├── tailwind.config.js
├── vite.config.ts
├── tsconfig.json
├── Dockerfile
└── package.json
```

## 3. Architecture Alignment (Mandatory)

### 3.1 Service Layer Pattern

**Constraint**: ALL backend communication MUST go through the service layer. NO direct Axios calls in components.

```typescript
// ❌ FORBIDDEN - Direct API call in component
function StudentListPage() {
  useEffect(() => {
    axios.get('http://localhost:8081/api/v1/students')
      .then(response => setStudents(response.data));
  }, []);
}

// ✅ CORRECT - Use service layer
import { studentService } from '@/services/api/student.api';

function StudentListPage() {
  const { data: students, isLoading, error } = useQuery(
    ['students'],
    () => studentService.getAllStudents()
  );
}
```

### 3.2 State Management Strategy

| State Type | Strategy | Example |
|------------|----------|---------|
| **Global State** | React Context | Toast notifications, theme |
| **Server State** | React Query / TanStack Query | Student data, configurations |
| **Local State** | useState, useReducer | Form inputs, UI toggles |
| **Form State** | React Hook Form | Student registration form |

**Rationale**: Aligns with `FRONTEND_DESIGN_SPEC.md` directive to use Context for global state and Hooks for local state.

## 4. API Integration Layer

### 4.1 Axios Configuration

```typescript
// src/services/api/axios.config.ts
import axios, { AxiosInstance } from 'axios';

const STUDENT_API_BASE_URL = import.meta.env.VITE_STUDENT_API_URL || 'http://localhost:8081/api/v1';
const CONFIG_API_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082/api/v1';

// Student Service Client
export const studentApiClient: AxiosInstance = axios.create({
  baseURL: STUDENT_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Configuration Service Client
export const configApiClient: AxiosInstance = axios.create({
  baseURL: CONFIG_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});
```

### 4.2 Request/Response Interceptors

```typescript
// src/services/api/interceptors.ts
import { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { toast } from '@/components/ui/use-toast';

// Request Interceptor - Add correlation ID
export const requestInterceptor = (config: InternalAxiosRequestConfig) => {
  const correlationId = crypto.randomUUID();
  config.headers['X-Correlation-ID'] = correlationId;

  // Phase 2: Add authentication token
  // const token = localStorage.getItem('auth_token');
  // if (token) {
  //   config.headers.Authorization = `Bearer ${token}`;
  // }

  return config;
};

// Response Interceptor - Handle errors globally
export const responseInterceptor = (response: AxiosResponse) => {
  return response;
};

export const errorInterceptor = (error: AxiosError<ErrorResponse>) => {
  const { response } = error;

  if (!response) {
    toast({
      title: 'Network Error',
      description: 'Unable to connect to the server. Please check your connection.',
      variant: 'destructive',
    });
    return Promise.reject(error);
  }

  switch (response.status) {
    case 400:
      toast({
        title: 'Validation Error',
        description: response.data?.detail || 'Invalid input data',
        variant: 'destructive',
      });
      break;
    case 404:
      toast({
        title: 'Not Found',
        description: response.data?.detail || 'Resource not found',
        variant: 'destructive',
      });
      break;
    case 409:
      toast({
        title: 'Conflict',
        description: response.data?.detail || 'Resource conflict (duplicate or version mismatch)',
        variant: 'destructive',
      });
      break;
    case 500:
      toast({
        title: 'Server Error',
        description: 'Internal server error. Please try again later.',
        variant: 'destructive',
      });
      break;
  }

  return Promise.reject(error);
};

// Attach interceptors
studentApiClient.interceptors.request.use(requestInterceptor);
studentApiClient.interceptors.response.use(responseInterceptor, errorInterceptor);
configApiClient.interceptors.request.use(requestInterceptor);
configApiClient.interceptors.response.use(responseInterceptor, errorInterceptor);
```

### 4.3 Student Service API

```typescript
// src/services/api/student.api.ts
import { studentApiClient } from './axios.config';
import {
  StudentResponse,
  StudentRequest,
  StudentUpdateRequest,
  PaginatedStudentResponse,
  EnrollmentResponse,
} from '@/services/types/student.types';
import { AxiosResponse } from 'axios';

export const studentService = {
  // GET /api/v1/students (with pagination)
  getAllStudents: async (params?: {
    lastName?: string;
    status?: 'ACTIVE' | 'INACTIVE';
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
  }): Promise<PaginatedStudentResponse> => {
    const response: AxiosResponse<PaginatedStudentResponse> = await studentApiClient.get(
      '/students',
      { params }
    );
    return response.data;
  },

  // GET /api/v1/students/{studentId}
  getStudentById: async (studentId: string): Promise<StudentResponse> => {
    const response: AxiosResponse<StudentResponse> = await studentApiClient.get(
      `/students/${studentId}`
    );
    return response.data;
  },

  // POST /api/v1/students
  createStudent: async (data: StudentRequest): Promise<StudentResponse> => {
    const response: AxiosResponse<StudentResponse> = await studentApiClient.post(
      '/students',
      data
    );
    return response.data;
  },

  // PUT /api/v1/students/{studentId}
  updateStudent: async (
    studentId: string,
    data: StudentUpdateRequest
  ): Promise<StudentResponse> => {
    const response: AxiosResponse<StudentResponse> = await studentApiClient.put(
      `/students/${studentId}`,
      data
    );
    return response.data;
  },

  // DELETE /api/v1/students/{studentId}
  deleteStudent: async (studentId: string): Promise<void> => {
    await studentApiClient.delete(`/students/${studentId}`);
  },

  // GET /api/v1/students/{studentId}/enrollment-history
  getEnrollmentHistory: async (studentId: string): Promise<EnrollmentResponse> => {
    const response: AxiosResponse<EnrollmentResponse> = await studentApiClient.get(
      `/students/${studentId}/enrollment-history`
    );
    return response.data;
  },
};
```

### 4.4 TypeScript Type Definitions

```typescript
// src/services/types/student.types.ts (Aligned with OpenAPI spec)

export interface StudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 date format
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
}

export interface StudentResponse extends StudentRequest {
  id: number;
  studentId: string; // e.g., "STD-20260203-0001"
  status: 'ACTIVE' | 'INACTIVE';
  version: number; // Optimistic locking
  createdAt: string;
  updatedAt: string;
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number; // Required for optimistic locking
}

export interface PaginatedStudentResponse {
  content: StudentResponse[];
  pageable: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface EnrollmentResponse {
  studentId: string;
  enrollments: Enrollment[];
}

export interface Enrollment {
  id: number;
  academicYear: string;
  gradeClass: string;
  section: string;
  enrollmentDate: string;
  withdrawalDate?: string;
  status: string;
  remarks?: string;
  createdAt: string;
}

export interface ErrorResponse {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
  timestamp: string;
  correlationId?: string;
  errors?: Array<{
    field: string;
    message: string;
    code: string;
  }>;
}
```

## 5. Form Validation with Zod

### 5.1 Student Registration Schema

**Critical Alignment**: Zod schemas MUST match backend validation rules from `sms_api_specification.yaml`.

```typescript
// src/features/students/schemas/studentSchema.ts
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

  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits'),

  email: z
    .string()
    .email('Invalid email format')
    .optional()
    .or(z.literal('')),

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
  {
    message: 'At least one guardian name (Father or Mother) is required',
    path: ['fathersName'],
  }
);

export const studentUpdateSchema = z.object({
  firstName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
  lastName: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
  mobile: z.string().regex(/^\d{10}$/),
  status: z.enum(['ACTIVE', 'INACTIVE']),
  version: z.number(), // Optimistic locking
});

export type StudentRegistrationFormData = z.infer<typeof studentRegistrationSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;

// Utility function
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

## 6. React Hook Form Integration

### 6.1 Student Registration Form Component

```tsx
// src/features/students/components/StudentForm.tsx
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentRegistrationSchema, StudentRegistrationFormData } from '../schemas/studentSchema';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { useToast } from '@/components/ui/use-toast';

interface StudentFormProps {
  onSubmit: (data: StudentRegistrationFormData) => Promise<void>;
  defaultValues?: Partial<StudentRegistrationFormData>;
  isLoading?: boolean;
}

export function StudentForm({ onSubmit, defaultValues, isLoading }: StudentFormProps) {
  const { toast } = useToast();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<StudentRegistrationFormData>({
    resolver: zodResolver(studentRegistrationSchema),
    defaultValues,
  });

  const handleFormSubmit = async (data: StudentRegistrationFormData) => {
    try {
      await onSubmit(data);
      toast({
        title: 'Success',
        description: 'Student registered successfully',
      });
      reset();
    } catch (error) {
      // Error handled by interceptor
      console.error('Form submission error:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      {/* First Name */}
      <div className="space-y-2">
        <Label htmlFor="firstName">First Name *</Label>
        <Input
          id="firstName"
          {...register('firstName')}
          placeholder="Enter first name"
          aria-invalid={errors.firstName ? 'true' : 'false'}
        />
        {errors.firstName && (
          <p className="text-sm text-destructive">{errors.firstName.message}</p>
        )}
      </div>

      {/* Last Name */}
      <div className="space-y-2">
        <Label htmlFor="lastName">Last Name *</Label>
        <Input
          id="lastName"
          {...register('lastName')}
          placeholder="Enter last name"
        />
        {errors.lastName && (
          <p className="text-sm text-destructive">{errors.lastName.message}</p>
        )}
      </div>

      {/* Date of Birth */}
      <div className="space-y-2">
        <Label htmlFor="dateOfBirth">Date of Birth *</Label>
        <Input
          id="dateOfBirth"
          type="date"
          {...register('dateOfBirth')}
        />
        {errors.dateOfBirth && (
          <p className="text-sm text-destructive">{errors.dateOfBirth.message}</p>
        )}
      </div>

      {/* Mobile */}
      <div className="space-y-2">
        <Label htmlFor="mobile">Mobile Number *</Label>
        <Input
          id="mobile"
          {...register('mobile')}
          placeholder="9876543210"
          maxLength={10}
        />
        {errors.mobile && (
          <p className="text-sm text-destructive">{errors.mobile.message}</p>
        )}
      </div>

      {/* Email */}
      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          {...register('email')}
          placeholder="student@example.com"
        />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email.message}</p>
        )}
      </div>

      {/* Father's Name */}
      <div className="space-y-2">
        <Label htmlFor="fathersName">Father's Name</Label>
        <Input
          id="fathersName"
          {...register('fathersName')}
          placeholder="Enter father's name"
        />
        {errors.fathersName && (
          <p className="text-sm text-destructive">{errors.fathersName.message}</p>
        )}
      </div>

      {/* Mother's Name */}
      <div className="space-y-2">
        <Label htmlFor="mothersName">Mother's Name</Label>
        <Input
          id="mothersName"
          {...register('mothersName')}
          placeholder="Enter mother's name"
        />
      </div>

      {/* Address */}
      <div className="space-y-2">
        <Label htmlFor="address">Address</Label>
        <Textarea
          id="address"
          {...register('address')}
          placeholder="Enter residential address"
          rows={3}
        />
      </div>

      {/* Aadhaar Number */}
      <div className="space-y-2">
        <Label htmlFor="aadhaarNumber">Aadhaar Number</Label>
        <Input
          id="aadhaarNumber"
          {...register('aadhaarNumber')}
          placeholder="123456789012"
          maxLength={12}
        />
        {errors.aadhaarNumber && (
          <p className="text-sm text-destructive">{errors.aadhaarNumber.message}</p>
        )}
      </div>

      {/* Identification Mark */}
      <div className="space-y-2">
        <Label htmlFor="identificationMark">Identification Mark</Label>
        <Input
          id="identificationMark"
          {...register('identificationMark')}
          placeholder="Enter identification mark"
        />
      </div>

      {/* Submit Button */}
      <div className="flex justify-end gap-4">
        <Button type="button" variant="outline" onClick={() => reset()}>
          Reset
        </Button>
        <Button type="submit" disabled={isSubmitting || isLoading}>
          {isSubmitting || isLoading ? 'Submitting...' : 'Register Student'}
        </Button>
      </div>
    </form>
  );
}
```

## 7. Custom Hooks for Data Fetching

### 7.1 useStudents Hook (React Query)

```typescript
// src/features/students/hooks/useStudents.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentService } from '@/services/api/student.api';
import { StudentRequest, StudentUpdateRequest } from '@/services/types/student.types';
import { useToast } from '@/components/ui/use-toast';

export function useStudents(params?: {
  lastName?: string;
  status?: 'ACTIVE' | 'INACTIVE';
  page?: number;
  size?: number;
}) {
  return useQuery({
    queryKey: ['students', params],
    queryFn: () => studentService.getAllStudents(params),
    keepPreviousData: true, // For pagination
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
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: StudentRequest) => studentService.createStudent(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      toast({
        title: 'Success',
        description: 'Student registered successfully',
      });
    },
  });
}

export function useUpdateStudent() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      studentId,
      data,
    }: {
      studentId: string;
      data: StudentUpdateRequest;
    }) => studentService.updateStudent(studentId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      queryClient.invalidateQueries({ queryKey: ['students', variables.studentId] });
      toast({
        title: 'Success',
        description: 'Student updated successfully',
      });
    },
  });
}

export function useDeleteStudent() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (studentId: string) => studentService.deleteStudent(studentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      toast({
        title: 'Success',
        description: 'Student deleted successfully',
      });
    },
  });
}
```

## 8. Page Components

### 8.1 Student List Page

```tsx
// src/features/students/pages/StudentListPage.tsx
import React, { useState } from 'react';
import { useStudents, useDeleteStudent } from '../hooks/useStudents';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { LoadingSpinner } from '@/components/shared/LoadingSpinner';
import { useNavigate } from 'react-router-dom';
import { useDebounce } from '@/hooks/useDebounce';

export function StudentListPage() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const debouncedSearch = useDebounce(searchQuery, 500);

  const { data, isLoading, error } = useStudents({
    lastName: debouncedSearch,
    page,
    size: 20,
  });

  const deleteMutation = useDeleteStudent();

  const handleDelete = async (studentId: string) => {
    if (confirm('Are you sure you want to delete this student?')) {
      await deleteMutation.mutateAsync(studentId);
    }
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <div>Error loading students</div>;

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Students</h1>
        <Button onClick={() => navigate('/students/create')}>
          Add Student
        </Button>
      </div>

      <div className="mb-4">
        <Input
          placeholder="Search by last name..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="max-w-sm"
        />
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Student ID</TableHead>
            <TableHead>Name</TableHead>
            <TableHead>Mobile</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data?.content.map((student) => (
            <TableRow key={student.id}>
              <TableCell>{student.studentId}</TableCell>
              <TableCell>{`${student.firstName} ${student.lastName}`}</TableCell>
              <TableCell>{student.mobile}</TableCell>
              <TableCell>
                <span
                  className={`px-2 py-1 rounded ${
                    student.status === 'ACTIVE'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-red-100 text-red-800'
                  }`}
                >
                  {student.status}
                </span>
              </TableCell>
              <TableCell>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => navigate(`/students/${student.studentId}`)}
                >
                  View
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => navigate(`/students/${student.studentId}/edit`)}
                  className="ml-2"
                >
                  Edit
                </Button>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => handleDelete(student.studentId)}
                  className="ml-2"
                >
                  Delete
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Pagination */}
      <div className="flex justify-between items-center mt-4">
        <Button
          onClick={() => setPage(page - 1)}
          disabled={page === 0}
        >
          Previous
        </Button>
        <span>
          Page {page + 1} of {data?.pageable.totalPages}
        </span>
        <Button
          onClick={() => setPage(page + 1)}
          disabled={page + 1 >= (data?.pageable.totalPages || 0)}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
```

## 9. Design Token Integration (Strict)

### 9.1 Theme CSS (Figma Tokens)

**Constraint**: COPY tokens from Figma export. NO custom color definitions.

```css
/* src/styles/theme.css */

:root {
  /* Tier 1: Primitive Tokens (from Figma) */
  --color-brand-main: #030213;
  --color-neutral-100: #ffffff;
  --color-neutral-200: #f1f5f9;
  --color-neutral-800: #1e293b;
  --color-blue-500: #3b82f6;
  --color-green-500: #10b981;
  --color-red-500: #ef4444;

  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;

  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  /* Tier 2: Semantic Tokens (Map to shadcn/ui) */
  --background: var(--color-neutral-100);
  --foreground: var(--color-brand-main);
  --primary: var(--color-blue-500);
  --primary-foreground: var(--color-neutral-100);
  --destructive: var(--color-red-500);
  --destructive-foreground: var(--color-neutral-100);
  --border: var(--color-neutral-200);
  --radius: var(--radius-md);
}

.dark {
  --background: var(--color-brand-main);
  --foreground: var(--color-neutral-100);
  --border: var(--color-neutral-800);
}
```

### 9.2 Tailwind Configuration

```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        background: 'var(--background)',
        foreground: 'var(--foreground)',
        primary: 'var(--primary)',
        destructive: 'var(--destructive)',
      },
      borderRadius: {
        DEFAULT: 'var(--radius)',
        sm: 'var(--radius-sm)',
        lg: 'var(--radius-lg)',
      },
      spacing: {
        xs: 'var(--spacing-xs)',
        sm: 'var(--spacing-sm)',
        md: 'var(--spacing-md)',
        lg: 'var(--spacing-lg)',
        xl: 'var(--spacing-xl)',
      },
    },
  },
  plugins: [],
};
```

## 10. Performance Optimization

### 10.1 Lazy Loading

```tsx
// src/router/index.tsx
import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { LoadingSpinner } from '@/components/shared/LoadingSpinner';

const StudentListPage = lazy(() => import('@/features/students/pages/StudentListPage'));
const StudentCreatePage = lazy(() => import('@/features/students/pages/StudentCreatePage'));
const StudentEditPage = lazy(() => import('@/features/students/pages/StudentEditPage'));

export function AppRouter() {
  return (
    <BrowserRouter>
      <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          <Route path="/students" element={<StudentListPage />} />
          <Route path="/students/create" element={<StudentCreatePage />} />
          <Route path="/students/:studentId/edit" element={<StudentEditPage />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
```

### 10.2 Memoization

```tsx
import React, { useMemo } from 'react';

export function StudentTable({ students }) {
  const sortedStudents = useMemo(() => {
    return [...students].sort((a, b) => a.lastName.localeCompare(b.lastName));
  }, [students]);

  return (
    <table>
      {sortedStudents.map(student => (
        <StudentRow key={student.id} student={student} />
      ))}
    </table>
  );
}

const StudentRow = React.memo(({ student }) => {
  return (
    <tr>
      <td>{student.studentId}</td>
      <td>{student.firstName}</td>
    </tr>
  );
});
```

## 11. Docker Configuration

```dockerfile
# Dockerfile
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

```nginx
# nginx.conf
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
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/configurations {
        proxy_pass http://config-service:8082;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 12. Deliverables Checklist

### Phase 1 Completion Criteria

- [ ] UI mirrors Reference Code 1:1 (layouts, spacing, typography)
- [ ] All forms validate via Zod schemas matching backend rules
- [ ] Service layer handles ALL HTTP methods (GET, POST, PUT, DELETE)
- [ ] Toast notifications for success/error states
- [ ] Pagination implemented for student list
- [ ] Search with debouncing (500ms delay)
- [ ] Loading states for all async operations
- [ ] Error boundary catches component crashes
- [ ] `docker build` passes successfully
- [ ] All Figma design tokens integrated into theme.css
- [ ] No hardcoded colors or spacing (use tokens only)
- [ ] Responsive design (mobile, tablet, desktop)

---

**Document Version**: 1.0
**Last Updated**: 2026-02-03
**Owner**: Architect Agent
