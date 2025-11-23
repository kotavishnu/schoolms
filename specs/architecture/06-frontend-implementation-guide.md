# Frontend Implementation Guide - School Management System

## 1. Overview

This guide provides comprehensive implementation patterns for frontend developers building the School Management System using React 18, TypeScript, Vite, and modern frontend best practices.

## 2. Project Structure

### 2.1 Directory Organization

```
sms-frontend/
├── public/
│   ├── favicon.ico
│   └── assets/
├── src/
│   ├── main.tsx                    # Application entry point
│   ├── App.tsx                     # Root component
│   ├── components/                 # Reusable UI components
│   │   ├── common/
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Select.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Table.tsx
│   │   │   ├── Pagination.tsx
│   │   │   ├── LoadingSpinner.tsx
│   │   │   ├── ErrorBoundary.tsx
│   │   │   └── ConfirmDialog.tsx
│   │   └── layout/
│   │       ├── Header.tsx
│   │       ├── Sidebar.tsx
│   │       ├── Footer.tsx
│   │       └── MainLayout.tsx
│   ├── features/                   # Feature-based modules
│   │   ├── student/
│   │   │   ├── components/
│   │   │   │   ├── StudentList.tsx
│   │   │   │   ├── StudentForm.tsx
│   │   │   │   ├── StudentDetail.tsx
│   │   │   │   ├── SearchBar.tsx
│   │   │   │   ├── StatusBadge.tsx
│   │   │   │   └── StudentCard.tsx
│   │   │   ├── hooks/
│   │   │   │   ├── useStudents.ts
│   │   │   │   ├── useCreateStudent.ts
│   │   │   │   ├── useUpdateStudent.ts
│   │   │   │   └── useDeleteStudent.ts
│   │   │   └── types/
│   │   │       └── student.types.ts
│   │   └── configuration/
│   │       ├── components/
│   │       │   ├── ConfigList.tsx
│   │       │   └── ConfigForm.tsx
│   │       ├── hooks/
│   │       │   └── useConfigurations.ts
│   │       └── types/
│   │           └── config.types.ts
│   ├── pages/                      # Route pages
│   │   ├── HomePage.tsx
│   │   ├── StudentListPage.tsx
│   │   ├── StudentCreatePage.tsx
│   │   ├── StudentEditPage.tsx
│   │   ├── StudentDetailPage.tsx
│   │   ├── ConfigurationPage.tsx
│   │   └── NotFoundPage.tsx
│   ├── services/                   # API services
│   │   ├── api/
│   │   │   ├── client.ts           # Axios instance
│   │   │   ├── studentService.ts
│   │   │   └── configService.ts
│   │   └── utils/
│   │       └── apiUtils.ts
│   ├── hooks/                      # Global custom hooks
│   │   ├── useDebounce.ts
│   │   ├── usePagination.ts
│   │   └── useToast.ts
│   ├── contexts/                   # React contexts
│   │   ├── ThemeContext.tsx
│   │   └── NotificationContext.tsx
│   ├── schemas/                    # Zod validation schemas
│   │   ├── studentSchema.ts
│   │   └── configSchema.ts
│   ├── types/                      # Global TypeScript types
│   │   ├── api.types.ts
│   │   ├── common.types.ts
│   │   └── error.types.ts
│   ├── utils/                      # Utility functions
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   ├── dateUtils.ts
│   │   └── constants.ts
│   ├── styles/                     # Global styles
│   │   └── global.css
│   └── router/                     # Routing configuration
│       └── index.tsx
├── .env.development
├── .env.production
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
└── vitest.config.ts
```

## 3. State Management Strategy

### 3.1 Server State (React Query)

**Purpose**: Manage data from backend APIs (students, configurations)

**Setup - queryClient.ts:**
```typescript
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: 0,
    },
  },
});
```

**Main.tsx:**
```typescript
import React from 'react';
import ReactDOM from 'react-dom/client';
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from './services/queryClient';
import App from './App';
import './styles/global.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </React.StrictMode>
);
```

### 3.2 Client State (Context API)

**ThemeContext.tsx:**
```typescript
import React, { createContext, useContext, useState, ReactNode } from 'react';

type Theme = 'light' | 'dark';

interface ThemeContextType {
  theme: Theme;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider = ({ children }: { children: ReactNode }) => {
  const [theme, setTheme] = useState<Theme>('light');

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within ThemeProvider');
  }
  return context;
};
```

**NotificationContext.tsx:**
```typescript
import React, { createContext, useContext, useState, ReactNode } from 'react';

interface Notification {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

interface NotificationContextType {
  notifications: Notification[];
  addNotification: (type: Notification['type'], message: string) => void;
  removeNotification: (id: string) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider = ({ children }: { children: ReactNode }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  const addNotification = (type: Notification['type'], message: string) => {
    const id = Math.random().toString(36).substr(2, 9);
    setNotifications((prev) => [...prev, { id, type, message }]);

    // Auto-remove after 5 seconds
    setTimeout(() => removeNotification(id), 5000);
  };

  const removeNotification = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <NotificationContext.Provider value={{ notifications, addNotification, removeNotification }}>
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
};
```

## 4. API Integration

### 4.1 Axios Configuration

**client.ts:**
```typescript
import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';
import { v4 as uuidv4 } from 'uuid';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request interceptor - Add correlation ID
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const correlationId = uuidv4();
    config.headers = {
      ...config.headers,
      'X-Correlation-ID': correlationId,
    };

    console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors globally
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    const correlationId = response.config.headers?.['X-Correlation-ID'];
    console.log(`[${correlationId}] Response: ${response.status}`);
    return response;
  },
  (error: AxiosError<ProblemDetails>) => {
    const correlationId = error.config?.headers?.['X-Correlation-ID'];
    console.error(`[${correlationId}] Error: ${error.message}`, error.response?.data);

    // Handle specific error codes
    if (error.response) {
      const problemDetails = error.response.data;
      console.error('Problem Details:', problemDetails);
    }

    return Promise.reject(error);
  }
);

export default apiClient;

// Type for RFC 7807 Problem Details
export interface ProblemDetails {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  correlationId: string;
  timestamp: string;
  errors?: Array<{
    field: string;
    message: string;
    rejectedValue?: any;
  }>;
}
```

### 4.2 Student Service

**studentService.ts:**
```typescript
import apiClient from './client';
import {
  Student,
  CreateStudentRequest,
  UpdateStudentRequest,
  UpdateStatusRequest,
  StudentPageResponse,
  StudentSearchParams,
} from '@/types/student.types';

const STUDENT_BASE_URL = '/students';

export const studentService = {
  /**
   * Create a new student
   */
  async createStudent(data: CreateStudentRequest): Promise<Student> {
    const response = await apiClient.post<Student>(STUDENT_BASE_URL, data);
    return response.data;
  },

  /**
   * Get student by ID
   */
  async getStudentById(id: number): Promise<Student> {
    const response = await apiClient.get<Student>(`${STUDENT_BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Get student by StudentID
   */
  async getStudentByStudentId(studentId: string): Promise<Student> {
    const response = await apiClient.get<Student>(
      `${STUDENT_BASE_URL}/student-id/${studentId}`
    );
    return response.data;
  },

  /**
   * Search students with pagination and filters
   */
  async searchStudents(params: StudentSearchParams): Promise<StudentPageResponse> {
    const response = await apiClient.get<StudentPageResponse>(STUDENT_BASE_URL, {
      params,
    });
    return response.data;
  },

  /**
   * Update student
   */
  async updateStudent(id: number, data: UpdateStudentRequest): Promise<Student> {
    const response = await apiClient.put<Student>(`${STUDENT_BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Update student status
   */
  async updateStatus(id: number, data: UpdateStatusRequest): Promise<Student> {
    const response = await apiClient.patch<Student>(
      `${STUDENT_BASE_URL}/${id}/status`,
      data
    );
    return response.data;
  },

  /**
   * Delete student (soft delete)
   */
  async deleteStudent(id: number): Promise<void> {
    await apiClient.delete(`${STUDENT_BASE_URL}/${id}`);
  },
};
```

### 4.3 React Query Hooks

**useStudents.ts:**
```typescript
import { useQuery, UseQueryResult } from '@tanstack/react-query';
import { studentService } from '@/services/api/studentService';
import { StudentPageResponse, StudentSearchParams } from '@/types/student.types';

export const useStudents = (
  params: StudentSearchParams
): UseQueryResult<StudentPageResponse, Error> => {
  return useQuery({
    queryKey: ['students', params],
    queryFn: () => studentService.searchStudents(params),
    keepPreviousData: true, // Keep previous page data while fetching next page
  });
};
```

**useCreateStudent.ts:**
```typescript
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { studentService } from '@/services/api/studentService';
import { CreateStudentRequest, Student } from '@/types/student.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useCreateStudent = () => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (data: CreateStudentRequest) => studentService.createStudent(data),
    onSuccess: (newStudent: Student) => {
      // Invalidate students list cache
      queryClient.invalidateQueries(['students']);

      // Optimistically update cache
      queryClient.setQueryData(['student', newStudent.id], newStudent);

      addNotification('success', `Student ${newStudent.studentId} registered successfully`);
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to create student';
      addNotification('error', message);
    },
  });
};
```

**useUpdateStudent.ts:**
```typescript
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { studentService } from '@/services/api/studentService';
import { UpdateStudentRequest, Student } from '@/types/student.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useUpdateStudent = () => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateStudentRequest }) =>
      studentService.updateStudent(id, data),
    onSuccess: (updatedStudent: Student) => {
      // Invalidate queries
      queryClient.invalidateQueries(['students']);
      queryClient.invalidateQueries(['student', updatedStudent.id]);

      addNotification('success', 'Student updated successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to update student';
      addNotification('error', message);
    },
  });
};
```

## 5. Form Handling with React Hook Form + Zod

### 5.1 Zod Schema

**studentSchema.ts:**
```typescript
import { z } from 'zod';

const mobileRegex = /^\+?[0-9]{10,15}$/;
const aadhaarRegex = /^[0-9]{12}$/;

export const studentSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(100, 'First name must be at most 100 characters'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(100, 'Last name must be at most 100 characters'),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address must be at most 500 characters'),

  mobile: z
    .string()
    .regex(mobileRegex, 'Invalid mobile number format (10-15 digits)'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      const birthDate = new Date(date);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      const adjustedAge =
        monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())
          ? age - 1
          : age;
      return adjustedAge >= 3 && adjustedAge <= 18;
    }, 'Student must be between 3 and 18 years old'),

  fatherName: z
    .string()
    .min(1, 'Father name/Guardian is required')
    .max(100, 'Father name must be at most 100 characters'),

  motherName: z
    .string()
    .max(100, 'Mother name must be at most 100 characters')
    .optional()
    .or(z.literal('')),

  identificationMark: z
    .string()
    .max(200, 'Identification mark must be at most 200 characters')
    .optional()
    .or(z.literal('')),

  email: z
    .string()
    .email('Invalid email format')
    .max(100, 'Email must be at most 100 characters')
    .optional()
    .or(z.literal('')),

  aadhaarNumber: z
    .string()
    .regex(aadhaarRegex, 'Aadhaar must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export type StudentFormData = z.infer<typeof studentSchema>;

export const updateStudentSchema = studentSchema.pick({
  firstName: true,
  lastName: true,
  mobile: true,
});

export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;
```

### 5.2 Student Form Component

**StudentForm.tsx:**
```typescript
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema, StudentFormData } from '@/schemas/studentSchema';
import { Button } from '@/components/common/Button';
import { Input } from '@/components/common/Input';

interface StudentFormProps {
  initialData?: Partial<StudentFormData>;
  onSubmit: (data: StudentFormData) => void;
  isLoading?: boolean;
}

export const StudentForm: React.FC<StudentFormProps> = ({
  initialData,
  onSubmit,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Personal Information */}
      <div className="bg-white shadow-md rounded-lg p-6">
        <h3 className="text-lg font-semibold mb-4">Personal Information</h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="First Name"
            {...register('firstName')}
            error={errors.firstName?.message}
            required
          />

          <Input
            label="Last Name"
            {...register('lastName')}
            error={errors.lastName?.message}
            required
          />

          <Input
            label="Mobile"
            type="tel"
            {...register('mobile')}
            error={errors.mobile?.message}
            placeholder="+919876543210"
            required
          />

          <Input
            label="Date of Birth"
            type="date"
            {...register('dateOfBirth')}
            error={errors.dateOfBirth?.message}
            required
          />

          <Input
            label="Email"
            type="email"
            {...register('email')}
            error={errors.email?.message}
            placeholder="student@example.com"
          />

          <Input
            label="Aadhaar Number"
            {...register('aadhaarNumber')}
            error={errors.aadhaarNumber?.message}
            placeholder="123456789012"
            maxLength={12}
          />
        </div>

        <div className="mt-4">
          <Input
            label="Address"
            {...register('address')}
            error={errors.address?.message}
            placeholder="Complete residential address"
            required
            multiline
            rows={3}
          />
        </div>
      </div>

      {/* Guardian Information */}
      <div className="bg-white shadow-md rounded-lg p-6">
        <h3 className="text-lg font-semibold mb-4">Guardian Information</h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Father Name / Guardian"
            {...register('fatherName')}
            error={errors.fatherName?.message}
            required
          />

          <Input
            label="Mother Name"
            {...register('motherName')}
            error={errors.motherName?.message}
          />
        </div>

        <div className="mt-4">
          <Input
            label="Identification Mark"
            {...register('identificationMark')}
            error={errors.identificationMark?.message}
            placeholder="e.g., Mole on left cheek"
          />
        </div>
      </div>

      {/* Actions */}
      <div className="flex justify-end space-x-4">
        <Button type="button" variant="secondary" onClick={() => window.history.back()}>
          Cancel
        </Button>
        <Button type="submit" variant="primary" disabled={isLoading}>
          {isLoading ? 'Saving...' : 'Save Student'}
        </Button>
      </div>
    </form>
  );
};
```

### 5.3 Reusable Input Component

**Input.tsx:**
```typescript
import React, { forwardRef } from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement | HTMLTextAreaElement> {
  label?: string;
  error?: string;
  multiline?: boolean;
  rows?: number;
}

export const Input = forwardRef<HTMLInputElement | HTMLTextAreaElement, InputProps>(
  ({ label, error, multiline = false, rows = 3, className = '', ...props }, ref) => {
    const baseClasses =
      'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500';
    const errorClasses = error ? 'border-red-500' : 'border-gray-300';

    return (
      <div className="flex flex-col">
        {label && (
          <label className="mb-1 text-sm font-medium text-gray-700">
            {label}
            {props.required && <span className="text-red-500 ml-1">*</span>}
          </label>
        )}

        {multiline ? (
          <textarea
            ref={ref as React.Ref<HTMLTextAreaElement>}
            rows={rows}
            className={`${baseClasses} ${errorClasses} ${className}`}
            {...(props as React.TextareaHTMLAttributes<HTMLTextAreaElement>)}
          />
        ) : (
          <input
            ref={ref as React.Ref<HTMLInputElement>}
            className={`${baseClasses} ${errorClasses} ${className}`}
            {...(props as React.InputHTMLAttributes<HTMLInputElement>)}
          />
        )}

        {error && <span className="mt-1 text-sm text-red-500">{error}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';
```

## 6. Component Patterns

### 6.1 StudentList Component

**StudentList.tsx:**
```typescript
import React, { useState } from 'react';
import { useStudents } from '@/features/student/hooks/useStudents';
import { StudentCard } from './StudentCard';
import { SearchBar } from './SearchBar';
import { Pagination } from '@/components/common/Pagination';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export const StudentList: React.FC = () => {
  const [searchParams, setSearchParams] = useState({
    page: 0,
    size: 20,
    lastName: '',
    status: '',
  });

  const { data, isLoading, isError, error } = useStudents(searchParams);

  const handleSearch = (lastName: string) => {
    setSearchParams((prev) => ({ ...prev, lastName, page: 0 }));
  };

  const handlePageChange = (page: number) => {
    setSearchParams((prev) => ({ ...prev, page }));
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (isError) {
    return (
      <div className="text-center text-red-500">
        Error loading students: {error.message}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <SearchBar onSearch={handleSearch} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {data?.content.map((student) => (
          <StudentCard key={student.id} student={student} />
        ))}
      </div>

      {data && (
        <Pagination
          currentPage={data.pageNumber}
          totalPages={data.totalPages}
          onPageChange={handlePageChange}
        />
      )}
    </div>
  );
};
```

### 6.2 StatusBadge Component

**StatusBadge.tsx:**
```typescript
import React from 'react';

interface StatusBadgeProps {
  status: 'Active' | 'Inactive' | 'Graduated' | 'Transferred';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const getStatusStyles = () => {
    switch (status) {
      case 'Active':
        return 'bg-green-100 text-green-800 border-green-300';
      case 'Inactive':
        return 'bg-gray-100 text-gray-800 border-gray-300';
      case 'Graduated':
        return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'Transferred':
        return 'bg-yellow-100 text-yellow-800 border-yellow-300';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  return (
    <span
      className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border ${getStatusStyles()}`}
    >
      {status}
    </span>
  );
};
```

## 7. Routing Configuration

**router/index.tsx:**
```typescript
import { createBrowserRouter } from 'react-router-dom';
import { MainLayout } from '@/components/layout/MainLayout';
import { HomePage } from '@/pages/HomePage';
import { StudentListPage } from '@/pages/StudentListPage';
import { StudentCreatePage } from '@/pages/StudentCreatePage';
import { StudentEditPage } from '@/pages/StudentEditPage';
import { StudentDetailPage } from '@/pages/StudentDetailPage';
import { ConfigurationPage } from '@/pages/ConfigurationPage';
import { NotFoundPage } from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'students',
        children: [
          {
            index: true,
            element: <StudentListPage />,
          },
          {
            path: 'create',
            element: <StudentCreatePage />,
          },
          {
            path: ':id',
            element: <StudentDetailPage />,
          },
          {
            path: ':id/edit',
            element: <StudentEditPage />,
          },
        ],
      },
      {
        path: 'configurations',
        element: <ConfigurationPage />,
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
]);
```

## 8. Performance Optimization

### 8.1 Code Splitting

**Lazy Loading Routes:**
```typescript
import { lazy, Suspense } from 'react';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

const StudentListPage = lazy(() => import('@/pages/StudentListPage'));
const StudentCreatePage = lazy(() => import('@/pages/StudentCreatePage'));

// In router configuration
{
  path: 'students',
  element: (
    <Suspense fallback={<LoadingSpinner />}>
      <StudentListPage />
    </Suspense>
  ),
}
```

### 8.2 Memoization

**useMemo Example:**
```typescript
import { useMemo } from 'react';

const filteredStudents = useMemo(() => {
  return students.filter((student) =>
    student.lastName.toLowerCase().includes(searchTerm.toLowerCase())
  );
}, [students, searchTerm]);
```

**React.memo Example:**
```typescript
import React, { memo } from 'react';

export const StudentCard = memo<StudentCardProps>(({ student }) => {
  return (
    <div className="border rounded-lg p-4">
      {/* Card content */}
    </div>
  );
});
```

### 8.3 Debounced Search

**useDebounce.ts:**
```typescript
import { useState, useEffect } from 'react';

export const useDebounce = <T>(value: T, delay: number = 500): T => {
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
};

// Usage in SearchBar
const [searchTerm, setSearchTerm] = useState('');
const debouncedSearchTerm = useDebounce(searchTerm, 500);

useEffect(() => {
  if (debouncedSearchTerm) {
    onSearch(debouncedSearchTerm);
  }
}, [debouncedSearchTerm]);
```

## 9. Error Handling

### 9.1 Error Boundary

**ErrorBoundary.tsx:**
```typescript
import React, { Component, ReactNode } from 'react';

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

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        this.props.fallback || (
          <div className="flex items-center justify-center h-screen">
            <div className="text-center">
              <h1 className="text-2xl font-bold text-red-600 mb-4">
                Something went wrong
              </h1>
              <p className="text-gray-600 mb-4">{this.state.error?.message}</p>
              <button
                onClick={() => window.location.reload()}
                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              >
                Reload Page
              </button>
            </div>
          </div>
        )
      );
    }

    return this.props.children;
  }
}
```

## 10. TypeScript Types

**student.types.ts:**
```typescript
export interface Student {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  address: string;
  mobile: string;
  dateOfBirth: string;
  age: number;
  fatherName: string;
  motherName?: string;
  identificationMark?: string;
  email?: string;
  aadhaarNumber?: string;
  status: 'Active' | 'Inactive' | 'Graduated' | 'Transferred';
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  address: string;
  mobile: string;
  dateOfBirth: string;
  fatherName: string;
  motherName?: string;
  identificationMark?: string;
  email?: string;
  aadhaarNumber?: string;
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: string;
  version: number;
}

export interface UpdateStatusRequest {
  status: 'Active' | 'Inactive';
  version: number;
}

export interface StudentPageResponse {
  content: Student[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface StudentSearchParams {
  page?: number;
  size?: number;
  lastName?: string;
  guardianName?: string;
  status?: string;
  sort?: string;
}
```

## 11. Tailwind CSS Configuration

**tailwind.config.js:**
```javascript
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
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
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
};
```

## 12. Environment Variables

**.env.development:**
```env
VITE_API_BASE_URL=http://localhost:8081/api/v1
VITE_APP_NAME=School Management System
VITE_APP_VERSION=1.0.0
```

**.env.production:**
```env
VITE_API_BASE_URL=https://api.sms.example.com/api/v1
VITE_APP_NAME=School Management System
VITE_APP_VERSION=1.0.0
```

## 13. Summary

This frontend implementation guide provides:

- **Clear project structure** with feature-based organization
- **React Query** for efficient server state management
- **Context API** for client state (theme, notifications)
- **Axios interceptors** for centralized API handling
- **React Hook Form + Zod** for robust form validation
- **Reusable components** (Button, Input, Table, Modal)
- **Performance optimizations** (code splitting, memoization, debouncing)
- **TypeScript types** for type safety
- **Tailwind CSS** for rapid UI development
- **Error handling** with error boundaries

Frontend developers can follow these patterns to build a modern, performant, and maintainable React application.
