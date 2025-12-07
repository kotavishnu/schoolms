# Frontend Implementation Guide

## 1. Overview

This document provides comprehensive implementation guidelines for Frontend Developer Agents building the School Management System (SMS) user interface. It enforces React 19, Next.js 15, TypeScript, and modern frontend patterns.

## 2. Project Structure

### 2.1 Next.js 15 App Router Structure

```
sms-frontend/
├── package.json
├── tsconfig.json
├── next.config.js
├── tailwind.config.ts
├── postcss.config.js
├── .env.local
├── .env.example
├── public/
│   ├── images/
│   ├── icons/
│   └── favicon.ico
├── src/
│   ├── app/                          # Next.js App Router
│   │   ├── layout.tsx               # Root layout
│   │   ├── page.tsx                 # Home page
│   │   ├── loading.tsx              # Loading UI
│   │   ├── error.tsx                # Error UI
│   │   ├── students/                # Students module
│   │   │   ├── page.tsx            # List students
│   │   │   ├── [id]/
│   │   │   │   └── page.tsx        # Student detail
│   │   │   ├── new/
│   │   │   │   └── page.tsx        # Create student
│   │   │   └── [id]/edit/
│   │   │       └── page.tsx        # Edit student
│   │   └── configuration/
│   │       └── page.tsx
│   ├── components/                   # Reusable components
│   │   ├── ui/                      # Base UI components
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Table.tsx
│   │   │   └── Spinner.tsx
│   │   ├── forms/                   # Form components
│   │   │   ├── StudentForm.tsx
│   │   │   └── ConfigurationForm.tsx
│   │   ├── layout/                  # Layout components
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── Footer.tsx
│   │   └── shared/                  # Shared components
│   │       ├── ErrorBoundary.tsx
│   │       ├── LoadingSpinner.tsx
│   │       └── Pagination.tsx
│   ├── lib/                         # Utilities & configs
│   │   ├── api/                     # API layer
│   │   │   ├── axios.ts            # Axios configuration
│   │   │   ├── students.ts         # Student API calls
│   │   │   └── configurations.ts   # Configuration API calls
│   │   ├── hooks/                   # Custom hooks
│   │   │   ├── useStudents.ts
│   │   │   ├── useStudent.ts
│   │   │   └── useConfigurations.ts
│   │   ├── utils/                   # Utility functions
│   │   │   ├── formatters.ts
│   │   │   ├── validators.ts
│   │   │   └── constants.ts
│   │   └── schemas/                 # Zod validation schemas
│   │       ├── student.schema.ts
│   │       └── configuration.schema.ts
│   ├── types/                       # TypeScript types
│   │   ├── student.types.ts
│   │   ├── configuration.types.ts
│   │   └── api.types.ts
│   └── styles/                      # Global styles
│       └── globals.css
└── tests/                           # Test files
    ├── unit/
    ├── integration/
    └── e2e/
```

## 3. Technology Stack Configuration

### 3.1 Package.json

```json
{
  "name": "sms-frontend",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "test": "vitest",
    "test:e2e": "playwright test",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "next": "^15.0.0",
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-hook-form": "^7.49.0",
    "zod": "^3.22.0",
    "@hookform/resolvers": "^3.3.0",
    "@tanstack/react-query": "^4.36.0",
    "axios": "^1.6.0",
    "react-router-dom": "^6.20.0",
    "tailwindcss": "^3.4.0",
    "clsx": "^2.0.0",
    "date-fns": "^3.0.0"
  },
  "devDependencies": {
    "@types/node": "^20.10.0",
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "typescript": "^5.3.0",
    "vitest": "^1.0.0",
    "@vitest/ui": "^1.0.0",
    "@testing-library/react": "^14.1.0",
    "@testing-library/jest-dom": "^6.1.0",
    "@testing-library/user-event": "^14.5.0",
    "@playwright/test": "^1.40.0",
    "eslint": "^8.55.0",
    "eslint-config-next": "^15.0.0",
    "prettier": "^3.1.0",
    "autoprefixer": "^10.4.0",
    "postcss": "^8.4.0"
  }
}
```

### 3.2 TypeScript Configuration

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["dom", "dom.iterable", "esnext"],
    "allowJs": true,
    "skipLibCheck": true,
    "strict": true,
    "noEmit": true,
    "esModuleInterop": true,
    "module": "esnext",
    "moduleResolution": "bundler",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "jsx": "preserve",
    "incremental": true,
    "plugins": [
      {
        "name": "next"
      }
    ],
    "paths": {
      "@/*": ["./src/*"],
      "@/components/*": ["./src/components/*"],
      "@/lib/*": ["./src/lib/*"],
      "@/types/*": ["./src/types/*"]
    }
  },
  "include": ["next-env.d.ts", "**/*.ts", "**/*.tsx", ".next/types/**/*.ts"],
  "exclude": ["node_modules"]
}
```

### 3.3 Tailwind CSS Configuration

```typescript
// tailwind.config.ts
import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        },
        danger: {
          500: '#ef4444',
          600: '#dc2626',
        },
        success: {
          500: '#10b981',
          600: '#059669',
        },
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}

export default config
```

## 4. State Management

### 4.1 React Query for Server State

**Principle:** Use React Query for all server-side state (API data).

```typescript
// src/lib/hooks/useStudents.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentApi } from '@/lib/api/students';
import type { Student, CreateStudentRequest, UpdateStudentRequest } from '@/types/student.types';

/**
 * Fetch all students with pagination
 */
export function useStudents(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: ['students', page, size],
    queryFn: () => studentApi.getStudents({ page, size }),
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes (formerly cacheTime)
  });
}

/**
 * Fetch single student by ID
 */
export function useStudent(studentId: string) {
  return useQuery({
    queryKey: ['students', studentId],
    queryFn: () => studentApi.getStudent(studentId),
    enabled: !!studentId,
    staleTime: 5 * 60 * 1000,
  });
}

/**
 * Create student mutation
 */
export function useCreateStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateStudentRequest) => studentApi.createStudent(data),
    onSuccess: () => {
      // Invalidate and refetch students list
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
  });
}

/**
 * Update student mutation
 */
export function useUpdateStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ studentId, data }: { studentId: string; data: UpdateStudentRequest }) =>
      studentApi.updateStudent(studentId, data),
    onSuccess: (_, variables) => {
      // Invalidate specific student and list
      queryClient.invalidateQueries({ queryKey: ['students', variables.studentId] });
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
  });
}

/**
 * Delete student mutation
 */
export function useDeleteStudent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (studentId: string) => studentApi.deleteStudent(studentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
  });
}

/**
 * Search students
 */
export function useSearchStudents(searchParams: {
  lastName?: string;
  fathersName?: string;
  status?: string;
  page?: number;
  size?: number;
}) {
  return useQuery({
    queryKey: ['students', 'search', searchParams],
    queryFn: () => studentApi.searchStudents(searchParams),
    enabled: Object.values(searchParams).some(v => v !== undefined),
  });
}
```

### 4.2 Context API for Client State

**Principle:** Use Context API for client-side state (UI state, theme, etc.).

```typescript
// src/lib/context/ThemeContext.tsx
'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';

type Theme = 'light' | 'dark';

interface ThemeContextType {
  theme: Theme;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<Theme>('light');

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme) {
      setTheme(savedTheme);
    }
  }, []);

  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within ThemeProvider');
  }
  return context;
}
```

## 5. API Layer

### 5.1 Axios Configuration

```typescript
// src/lib/api/axios.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';

const studentApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_STUDENT_API_URL || 'http://localhost:8081/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

const configApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_CONFIG_API_URL || 'http://localhost:8082/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor - Add correlation ID
studentApi.interceptors.request.use(
  (config) => {
    const correlationId = crypto.randomUUID();
    config.headers['X-Correlation-ID'] = correlationId;

    // Add auth token if available (Phase 2)
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors
studentApi.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ProblemDetail>) => {
    if (error.response?.data?.type) {
      // RFC 7807 Problem Detail format
      const problem = error.response.data;
      console.error('API Error:', problem);

      // Handle specific error types
      if (problem.status === 401) {
        // Redirect to login (Phase 2)
        // window.location.href = '/login';
      }

      return Promise.reject(problem);
    }

    return Promise.reject(error);
  }
);

// Apply same interceptors to configApi
configApi.interceptors.request.use(studentApi.interceptors.request.handlers[0].fulfilled);
configApi.interceptors.response.use(
  studentApi.interceptors.response.handlers[0].fulfilled,
  studentApi.interceptors.response.handlers[0].rejected
);

export { studentApi, configApi };

// Types
export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp: string;
  correlationId: string;
  errors?: ErrorDetail[];
}

export interface ErrorDetail {
  field: string | null;
  message: string;
  code: string;
}
```

### 5.2 Student API Client

```typescript
// src/lib/api/students.ts
import { studentApi } from './axios';
import type {
  Student,
  CreateStudentRequest,
  UpdateStudentRequest,
  PagedResponse,
  SearchParams,
} from '@/types/student.types';

export const studentApiClient = {
  /**
   * Get all students with pagination
   */
  async getStudents(params: { page?: number; size?: number } = {}): Promise<PagedResponse<Student>> {
    const { data } = await studentApi.get<PagedResponse<Student>>('/students', { params });
    return data;
  },

  /**
   * Get single student by ID
   */
  async getStudent(studentId: string): Promise<Student> {
    const { data } = await studentApi.get<Student>(`/students/${studentId}`);
    return data;
  },

  /**
   * Create new student
   */
  async createStudent(request: CreateStudentRequest): Promise<Student> {
    const { data } = await studentApi.post<Student>('/students', request);
    return data;
  },

  /**
   * Update student
   */
  async updateStudent(studentId: string, request: UpdateStudentRequest): Promise<Student> {
    const { data } = await studentApi.put<Student>(`/students/${studentId}`, request);
    return data;
  },

  /**
   * Delete student
   */
  async deleteStudent(studentId: string): Promise<void> {
    await studentApi.delete(`/students/${studentId}`);
  },

  /**
   * Search students
   */
  async searchStudents(params: SearchParams): Promise<PagedResponse<Student>> {
    const { data } = await studentApi.get<PagedResponse<Student>>('/students', { params });
    return data;
  },

  /**
   * Get student enrollment history
   */
  async getEnrollmentHistory(studentId: string): Promise<Enrollment[]> {
    const { data } = await studentApi.get<{ enrollments: Enrollment[] }>(
      `/students/${studentId}/enrollment-history`
    );
    return data.enrollments;
  },
};

export default studentApiClient;
```

## 6. Forms with React Hook Form + Zod

### 6.1 Zod Validation Schema

```typescript
// src/lib/schemas/student.schema.ts
import { z } from 'zod';

const phoneRegex = /^\d{10}$/;
const aadhaarRegex = /^\d{12}$/;
const nameRegex = /^[a-zA-Z\s]+$/;

export const createStudentSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name must not exceed 100 characters')
    .regex(nameRegex, 'First name can only contain letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name must not exceed 100 characters')
    .regex(nameRegex, 'Last name can only contain letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      const dob = new Date(date);
      const age = Math.floor((Date.now() - dob.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
      return age >= 3 && age <= 18;
    }, 'Student must be between 3 and 18 years old'),

  mobile: z
    .string()
    .regex(phoneRegex, 'Mobile must be exactly 10 digits'),

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
    .regex(aadhaarRegex, 'Aadhaar must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export const updateStudentSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name must not exceed 100 characters')
    .regex(nameRegex, 'First name can only contain letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name must not exceed 100 characters'),

  mobile: z
    .string()
    .regex(phoneRegex, 'Mobile must be exactly 10 digits'),

  status: z.enum(['ACTIVE', 'INACTIVE']),

  version: z.number(),
});

export type CreateStudentFormData = z.infer<typeof createStudentSchema>;
export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;
```

### 6.2 Student Form Component

```typescript
// src/components/forms/StudentForm.tsx
'use client';

import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createStudentSchema, type CreateStudentFormData } from '@/lib/schemas/student.schema';
import { useCreateStudent } from '@/lib/hooks/useStudents';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export function StudentForm() {
  const router = useRouter();
  const createStudent = useCreateStudent();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<CreateStudentFormData>({
    resolver: zodResolver(createStudentSchema),
  });

  const onSubmit = async (data: CreateStudentFormData) => {
    try {
      await createStudent.mutateAsync(data);
      reset();
      router.push('/students');
    } catch (error) {
      console.error('Failed to create student:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <Input
            label="First Name"
            {...register('firstName')}
            error={errors.firstName?.message}
            required
          />
        </div>

        <div>
          <Input
            label="Last Name"
            {...register('lastName')}
            error={errors.lastName?.message}
            required
          />
        </div>

        <div>
          <Input
            label="Date of Birth"
            type="date"
            {...register('dateOfBirth')}
            error={errors.dateOfBirth?.message}
            required
          />
        </div>

        <div>
          <Input
            label="Mobile"
            type="tel"
            {...register('mobile')}
            error={errors.mobile?.message}
            required
          />
        </div>

        <div>
          <Input
            label="Email"
            type="email"
            {...register('email')}
            error={errors.email?.message}
          />
        </div>

        <div>
          <Input
            label="Aadhaar Number"
            {...register('aadhaarNumber')}
            error={errors.aadhaarNumber?.message}
          />
        </div>

        <div>
          <Input
            label="Father's Name"
            {...register('fathersName')}
            error={errors.fathersName?.message}
          />
        </div>

        <div>
          <Input
            label="Mother's Name"
            {...register('mothersName')}
            error={errors.mothersName?.message}
          />
        </div>

        <div className="md:col-span-2">
          <Input
            label="Address"
            {...register('address')}
            error={errors.address?.message}
          />
        </div>

        <div className="md:col-span-2">
          <Input
            label="Identification Mark"
            {...register('identificationMark')}
            error={errors.identificationMark?.message}
          />
        </div>
      </div>

      <div className="flex gap-4">
        <Button type="submit" disabled={isSubmitting || createStudent.isPending}>
          {createStudent.isPending ? 'Creating...' : 'Create Student'}
        </Button>

        <Button
          type="button"
          variant="secondary"
          onClick={() => router.back()}
        >
          Cancel
        </Button>
      </div>

      {createStudent.isError && (
        <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-md">
          <p className="text-red-800">
            {createStudent.error?.detail || 'Failed to create student'}
          </p>
        </div>
      )}
    </form>
  );
}
```

## 7. Reusable UI Components

### 7.1 Button Component

```typescript
// src/components/ui/Button.tsx
import React from 'react';
import clsx from 'clsx';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = 'primary', size = 'md', isLoading, children, disabled, ...props }, ref) => {
    return (
      <button
        ref={ref}
        className={clsx(
          'inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed',
          {
            'bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500': variant === 'primary',
            'bg-gray-200 text-gray-900 hover:bg-gray-300 focus:ring-gray-500': variant === 'secondary',
            'bg-danger-600 text-white hover:bg-danger-700 focus:ring-danger-500': variant === 'danger',
            'px-3 py-1.5 text-sm': size === 'sm',
            'px-4 py-2 text-base': size === 'md',
            'px-6 py-3 text-lg': size === 'lg',
          },
          className
        )}
        disabled={disabled || isLoading}
        {...props}
      >
        {isLoading && (
          <svg className="animate-spin -ml-1 mr-3 h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        )}
        {children}
      </button>
    );
  }
);

Button.displayName = 'Button';
```

### 7.2 Input Component

```typescript
// src/components/ui/Input.tsx
import React from 'react';
import clsx from 'clsx';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, helperText, required, ...props }, ref) => {
    const id = props.id || props.name;

    return (
      <div className="w-full">
        {label && (
          <label htmlFor={id} className="block text-sm font-medium text-gray-700 mb-1">
            {label}
            {required && <span className="text-red-500 ml-1">*</span>}
          </label>
        )}

        <input
          ref={ref}
          id={id}
          className={clsx(
            'block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-0 sm:text-sm',
            {
              'border-gray-300 focus:ring-primary-500 focus:border-primary-500': !error,
              'border-red-300 focus:ring-red-500 focus:border-red-500': error,
            },
            className
          )}
          aria-invalid={error ? 'true' : 'false'}
          aria-describedby={error ? `${id}-error` : helperText ? `${id}-helper` : undefined}
          {...props}
        />

        {error && (
          <p id={`${id}-error`} className="mt-1 text-sm text-red-600">
            {error}
          </p>
        )}

        {helperText && !error && (
          <p id={`${id}-helper`} className="mt-1 text-sm text-gray-500">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';
```

### 7.3 Table Component

```typescript
// src/components/ui/Table.tsx
import React from 'react';
import clsx from 'clsx';

interface TableProps {
  children: React.ReactNode;
  className?: string;
}

export function Table({ children, className }: TableProps) {
  return (
    <div className="overflow-x-auto">
      <table className={clsx('min-w-full divide-y divide-gray-200', className)}>
        {children}
      </table>
    </div>
  );
}

export function TableHead({ children }: { children: React.ReactNode }) {
  return <thead className="bg-gray-50">{children}</thead>;
}

export function TableBody({ children }: { children: React.ReactNode }) {
  return <tbody className="bg-white divide-y divide-gray-200">{children}</tbody>;
}

export function TableRow({ children, className }: { children: React.ReactNode; className?: string }) {
  return <tr className={className}>{children}</tr>;
}

export function TableHeader({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <th
      scope="col"
      className={clsx('px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', className)}
    >
      {children}
    </th>
  );
}

export function TableCell({ children, className }: { children: React.ReactNode; className?: string }) {
  return <td className={clsx('px-6 py-4 whitespace-nowrap text-sm text-gray-900', className)}>{children}</td>;
}
```

## 8. Pages (Next.js App Router)

### 8.1 Students List Page

```typescript
// src/app/students/page.tsx
'use client';

import React, { useState } from 'react';
import { useStudents, useDeleteStudent } from '@/lib/hooks/useStudents';
import { Table, TableHead, TableBody, TableRow, TableHeader, TableCell } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Pagination } from '@/components/shared/Pagination';
import Link from 'next/link';
import { format } from 'date-fns';

export default function StudentsPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');

  const { data, isLoading, error } = useStudents(page, 20);
  const deleteStudent = useDeleteStudent();

  const handleDelete = async (studentId: string) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      await deleteStudent.mutateAsync(studentId);
    }
  };

  if (isLoading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-4">
        <p className="text-red-800">Failed to load students. Please try again.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Students</h1>
        <Link href="/students/new">
          <Button>Add Student</Button>
        </Link>
      </div>

      <div className="bg-white shadow rounded-lg">
        <div className="p-4 border-b">
          <Input
            placeholder="Search by last name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <Table>
          <TableHead>
            <TableRow>
              <TableHeader>Student ID</TableHeader>
              <TableHeader>Name</TableHeader>
              <TableHeader>Mobile</TableHeader>
              <TableHeader>Status</TableHeader>
              <TableHeader>Created At</TableHeader>
              <TableHeader>Actions</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.content.map((student) => (
              <TableRow key={student.id}>
                <TableCell>{student.studentId}</TableCell>
                <TableCell>
                  {student.firstName} {student.lastName}
                </TableCell>
                <TableCell>{student.mobile}</TableCell>
                <TableCell>
                  <span
                    className={clsx('px-2 py-1 text-xs rounded-full', {
                      'bg-green-100 text-green-800': student.status === 'ACTIVE',
                      'bg-gray-100 text-gray-800': student.status === 'INACTIVE',
                    })}
                  >
                    {student.status}
                  </span>
                </TableCell>
                <TableCell>{format(new Date(student.createdAt), 'MMM dd, yyyy')}</TableCell>
                <TableCell>
                  <div className="flex gap-2">
                    <Link href={`/students/${student.studentId}`}>
                      <Button size="sm" variant="secondary">View</Button>
                    </Link>
                    <Link href={`/students/${student.studentId}/edit`}>
                      <Button size="sm" variant="secondary">Edit</Button>
                    </Link>
                    <Button
                      size="sm"
                      variant="danger"
                      onClick={() => handleDelete(student.studentId)}
                    >
                      Delete
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <div className="p-4 border-t">
          <Pagination
            currentPage={page}
            totalPages={data?.pageable.totalPages || 0}
            onPageChange={setPage}
          />
        </div>
      </div>
    </div>
  );
}
```

### 8.2 Create Student Page

```typescript
// src/app/students/new/page.tsx
'use client';

import React from 'react';
import { StudentForm } from '@/components/forms/StudentForm';

export default function NewStudentPage() {
  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Create New Student</h1>

      <div className="bg-white shadow rounded-lg p-6">
        <StudentForm />
      </div>
    </div>
  );
}
```

## 9. Performance Optimization

### 9.1 Code Splitting & Lazy Loading

```typescript
// Dynamic imports for heavy components
import dynamic from 'next/dynamic';

const StudentDetailModal = dynamic(() => import('@/components/StudentDetailModal'), {
  loading: () => <div>Loading...</div>,
  ssr: false,
});

// Lazy load non-critical components
const EnrollmentHistory = React.lazy(() => import('@/components/EnrollmentHistory'));
```

### 9.2 Memoization

```typescript
// Use React.memo for expensive components
export const StudentCard = React.memo(({ student }: { student: Student }) => {
  return (
    <div className="card">
      {/* Student details */}
    </div>
  );
});

// Use useMemo for expensive computations
const filteredStudents = useMemo(() => {
  return students.filter(s => s.lastName.includes(search));
}, [students, search]);

// Use useCallback for stable function references
const handleSearch = useCallback((value: string) => {
  setSearch(value);
}, []);
```

### 9.3 Image Optimization

```typescript
import Image from 'next/image';

<Image
  src="/images/student-avatar.png"
  alt="Student"
  width={100}
  height={100}
  priority={false}
  loading="lazy"
/>
```

## 10. Error Handling

### 10.1 Error Boundary

```typescript
// src/components/shared/ErrorBoundary.tsx
'use client';

import React, { Component, ErrorInfo, ReactNode } from 'react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        this.props.fallback || (
          <div className="flex flex-col items-center justify-center h-screen">
            <h2 className="text-2xl font-bold text-red-600 mb-4">Something went wrong</h2>
            <p className="text-gray-600 mb-4">{this.state.error?.message}</p>
            <button
              onClick={() => this.setState({ hasError: false })}
              className="px-4 py-2 bg-primary-600 text-white rounded-md"
            >
              Try again
            </button>
          </div>
        )
      );
    }

    return this.props.children;
  }
}
```

## 11. TypeScript Types

### 11.1 Student Types

```typescript
// src/types/student.types.ts
export interface Student {
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

export interface CreateStudentRequest {
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
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
}

export interface PagedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface SearchParams {
  lastName?: string;
  fathersName?: string;
  status?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}
```

## 12. Environment Variables

```bash
# .env.example
NEXT_PUBLIC_STUDENT_API_URL=http://localhost:8081/api/v1
NEXT_PUBLIC_CONFIG_API_URL=http://localhost:8082/api/v1
NEXT_PUBLIC_APP_NAME=School Management System
```

## 13. Accessibility

```typescript
// Ensure all components are accessible
<button
  aria-label="Delete student"
  aria-describedby="delete-description"
  onClick={handleDelete}
>
  Delete
</button>

<input
  aria-invalid={!!error}
  aria-describedby={error ? 'error-message' : undefined}
/>
```

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED
