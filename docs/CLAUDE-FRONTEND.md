# CLAUDE-FRONTEND.md

**Tier 2: Frontend Component Agent**

This agent provides comprehensive guidance for building the React-based frontend of the School Management System. For feature-specific details, refer to Tier 3 feature agents in `docs/features/`.

---

## Agent Role

**Purpose**: Guide implementation of React 18 frontend architecture, component patterns, API integration, and state management.

**Scope**: Application-wide frontend concerns, not feature-specific logic (see Tier 3 agents).

---

## Tech Stack & Versions

```json
{
  "framework": "React 18.2+",
  "bundler": "Vite 5.0+",
  "styling": "Tailwind CSS 3.4+",
  "routing": "React Router 6.20+",
  "http": "Axios 1.6+",
  "state": "Context API + useState/useReducer",
  "forms": "React Hook Form 7.49+",
  "validation": "Zod 3.22+",
  "testing": "Vitest + React Testing Library",
  "nodeVersion": "20.x LTS"
}
```

---

## Quick Start Commands

```bash
cd frontend

# Development
npm install              # Install dependencies
npm run dev              # Dev server at http://localhost:3000
npm run dev -- --host    # Expose to network

# Building
npm run build            # Production build to dist/
npm run preview          # Preview production build

# Quality Checks
npm run lint             # ESLint check
npm run lint:fix         # Auto-fix linting issues
npm run format           # Prettier formatting
npm run type-check       # TypeScript checks (if using TS)

# Testing
npm run test             # Run all tests
npm run test:watch       # Watch mode
npm run test:coverage    # Coverage report
npm run test:ui          # Vitest UI mode
```

---

## Project Structure

```
frontend/
├── public/                    # Static assets (favicon, images)
├── src/
│   ├── main.jsx              # App entry point
│   ├── App.jsx               # Root component with routing
│   │
│   ├── components/           # Reusable UI components
│   │   ├── common/           # Generic components
│   │   │   ├── Button.jsx
│   │   │   ├── Input.jsx
│   │   │   ├── Card.jsx
│   │   │   ├── Modal.jsx
│   │   │   ├── Loading.jsx
│   │   │   └── ErrorBoundary.jsx
│   │   │
│   │   ├── forms/            # Form-specific components
│   │   │   ├── FormField.jsx
│   │   │   ├── FormSelect.jsx
│   │   │   ├── FormDatePicker.jsx
│   │   │   └── FormValidation.jsx
│   │   │
│   │   └── layout/           # Layout components
│   │       ├── Header.jsx
│   │       ├── Sidebar.jsx
│   │       ├── Footer.jsx
│   │       └── PageLayout.jsx
│   │
│   ├── pages/                # Route-level page components
│   │   ├── Dashboard.jsx
│   │   ├── StudentRegistration.jsx
│   │   ├── ClassManagement.jsx
│   │   ├── FeeMaster.jsx
│   │   ├── FeeJournal.jsx
│   │   ├── FeeReceipt.jsx
│   │   └── SchoolConfig.jsx
│   │
│   ├── services/             # API integration layer
│   │   ├── api.js            # Axios instance & interceptors
│   │   ├── studentService.js
│   │   ├── classService.js
│   │   ├── feeService.js
│   │   ├── receiptService.js
│   │   └── authService.js
│   │
│   ├── hooks/                # Custom React hooks
│   │   ├── useAuth.js
│   │   ├── useDebounce.js
│   │   ├── useLocalStorage.js
│   │   ├── useFetch.js
│   │   └── useForm.js
│   │
│   ├── contexts/             # Global state providers
│   │   ├── AuthContext.jsx
│   │   ├── SchoolConfigContext.jsx
│   │   └── NotificationContext.jsx
│   │
│   ├── utils/                # Helper functions
│   │   ├── formatters.js     # Date, currency formatting
│   │   ├── validators.js     # Custom validation rules
│   │   ├── constants.js      # App-wide constants
│   │   └── helpers.js        # Misc utilities
│   │
│   ├── styles/               # Global styles
│   │   ├── index.css         # Tailwind imports
│   │   └── themes.css        # Custom theme variables
│   │
│   └── __tests__/            # Test files
│       ├── components/
│       ├── pages/
│       ├── hooks/
│       └── services/
│
├── .env.development          # Dev environment variables
├── .env.production           # Prod environment variables
├── vite.config.js            # Vite configuration
├── tailwind.config.js        # Tailwind configuration
├── postcss.config.js         # PostCSS configuration
├── vitest.config.js          # Vitest test configuration
├── .eslintrc.cjs             # ESLint rules
├── .prettierrc               # Prettier rules
└── package.json
```

---

## Configuration Files

### vite.config.js

```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@pages': path.resolve(__dirname, './src/pages'),
      '@services': path.resolve(__dirname, './src/services'),
      '@hooks': path.resolve(__dirname, './src/hooks'),
      '@utils': path.resolve(__dirname, './src/utils'),
    },
  },
  
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  
  build: {
    outDir: 'dist',
    sourcemap: true,
    chunkSizeWarningLimit: 1000,
  },
});
```

### .env.development

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=School Management System
VITE_APP_VERSION=1.0.0
```

### tailwind.config.js

```javascript
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        },
        // Add school brand colors
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}
```

---

## Architecture Patterns

### 1. Component Hierarchy

```
App.jsx (Router)
  ├── Layout
  │   ├── Header
  │   ├── Sidebar
  │   └── PageLayout
  │       └── {Page Component}
  │           ├── Feature-specific components
  │           └── Common components (Button, Input, etc.)
  └── Context Providers
      ├── AuthContext
      ├── SchoolConfigContext
      └── NotificationContext
```

### 2. Data Flow Pattern

```
User Action (Component)
    ↓
Event Handler
    ↓
Service Method (API call)
    ↓
Backend REST API
    ↓
Response
    ↓
State Update (useState/Context)
    ↓
UI Re-render
```

### 3. Error Handling Flow

```
API Call Fails
    ↓
Axios Interceptor (logs error)
    ↓
Service catches error
    ↓
Component displays error message
    ↓
NotificationContext (toast/alert)
```

---

## Core Implementation Patterns

### API Service Layer

**services/api.js** (Base Axios Configuration):

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor (add auth token)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor (error handling)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirect to login
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
```

**services/studentService.js** (Example Service):

```javascript
import api from './api';

export const studentService = {
  // Create
  createStudent: (data) => api.post('/students', data),
  
  // Read
  getStudent: (id) => api.get(`/students/${id}`),
  getAllStudents: () => api.get('/students'),
  getStudentsByClass: (classId) => api.get(`/students?classId=${classId}`),
  
  // Update
  updateStudent: (id, data) => api.put(`/students/${id}`, data),
  
  // Delete
  deleteStudent: (id) => api.delete(`/students/${id}`),
  
  // Search (autocomplete)
  searchStudents: (query) => api.get(`/students/search?q=${query}`),
};
```

### Component Patterns

**Functional Components with Hooks**:

```javascript
import { useState, useEffect } from 'react';
import { studentService } from '@services/studentService';
import { useNotification } from '@contexts/NotificationContext';

function StudentForm({ studentId, onSuccess }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dob: '',
    classId: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const { showSuccess, showError } = useNotification();
  
  // Load existing student if editing
  useEffect(() => {
    if (studentId) {
      loadStudent(studentId);
    }
  }, [studentId]);
  
  const loadStudent = async (id) => {
    try {
      const response = await studentService.getStudent(id);
      setFormData(response.data);
    } catch (error) {
      showError('Failed to load student data');
    }
  };
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear error for this field
    setErrors(prev => ({ ...prev, [name]: '' }));
  };
  
  const validateForm = () => {
    const newErrors = {};
    if (!formData.firstName) newErrors.firstName = 'First name is required';
    if (!formData.lastName) newErrors.lastName = 'Last name is required';
    if (!formData.dob) newErrors.dob = 'Date of birth is required';
    if (!formData.classId) newErrors.classId = 'Class is required';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    try {
      if (studentId) {
        await studentService.updateStudent(studentId, formData);
        showSuccess('Student updated successfully');
      } else {
        await studentService.createStudent(formData);
        showSuccess('Student registered successfully');
      }
      
      onSuccess?.();
    } catch (error) {
      const message = error.response?.data?.message || 'An error occurred';
      showError(message);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">First Name</label>
        <input
          type="text"
          name="firstName"
          value={formData.firstName}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded ${
            errors.firstName ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.firstName && (
          <p className="text-red-500 text-sm mt-1">{errors.firstName}</p>
        )}
      </div>
      
      {/* More fields... */}
      
      <button
        type="submit"
        disabled={loading}
        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? 'Saving...' : studentId ? 'Update' : 'Register'}
      </button>
    </form>
  );
}

export default StudentForm;
```

**Autocomplete Search Component**:

```javascript
import { useState, useEffect } from 'react';
import { useDebounce } from '@hooks/useDebounce';
import { studentService } from '@services/studentService';

function StudentSearchAutocomplete({ onSelect }) {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const debouncedQuery = useDebounce(query, 300);
  
  useEffect(() => {
    if (debouncedQuery.length > 2) {
      searchStudents(debouncedQuery);
    } else {
      setSuggestions([]);
    }
  }, [debouncedQuery]);
  
  const searchStudents = async (searchQuery) => {
    setLoading(true);
    try {
      const response = await studentService.searchStudents(searchQuery);
      setSuggestions(response.data);
    } catch (error) {
      console.error('Search failed:', error);
      setSuggestions([]);
    } finally {
      setLoading(false);
    }
  };
  
  const handleSelect = (student) => {
    setQuery(`${student.firstName} ${student.lastName}`);
    setSuggestions([]);
    onSelect(student);
  };
  
  return (
    <div className="relative">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search student by name..."
        className="w-full px-4 py-2 border border-gray-300 rounded"
      />
      
      {loading && (
        <div className="absolute right-3 top-3">
          <LoadingSpinner size="sm" />
        </div>
      )}
      
      {suggestions.length > 0 && (
        <ul className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded shadow-lg max-h-60 overflow-auto">
          {suggestions.map((student) => (
            <li
              key={student.id}
              onClick={() => handleSelect(student)}
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
            >
              <div className="font-medium">
                {student.firstName} {student.lastName}
              </div>
              <div className="text-sm text-gray-600">
                Class {student.classNumber} • Roll No: {student.rollNumber}
              </div>
            </li>
          ))}
        </ul>
      )}
      
      {debouncedQuery.length > 2 && suggestions.length === 0 && !loading && (
        <div className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded shadow-lg px-4 py-2 text-gray-500">
          No students found
        </div>
      )}
    </div>
  );
}

export default StudentSearchAutocomplete;
```

---

## Custom Hooks

**hooks/useDebounce.js**:

```javascript
import { useState, useEffect } from 'react';

export function useDebounce(value, delay) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    
    return () => clearTimeout(handler);
  }, [value, delay]);
  
  return debouncedValue;
}
```

**hooks/useFetch.js**:

```javascript
import { useState, useEffect } from 'react';

export function useFetch(fetchFn, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    let isMounted = true;
    
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const response = await fetchFn();
        if (isMounted) {
          setData(response.data);
        }
      } catch (err) {
        if (isMounted) {
          setError(err);
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };
    
    fetchData();
    
    return () => {
      isMounted = false;
    };
  }, deps);
  
  return { data, loading, error, refetch: fetchData };
}
```

---

## State Management

### Context API Pattern

**contexts/AuthContext.jsx**:

```javascript
import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '@services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    checkAuth();
  }, []);
  
  const checkAuth = async () => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const response = await authService.getCurrentUser();
        setUser(response.data);
      } catch (error) {
        localStorage.removeItem('authToken');
      }
    }
    setLoading(false);
  };
  
  const login = async (credentials) => {
    const response = await authService.login(credentials);
    const { token, user } = response.data;
    localStorage.setItem('authToken', token);
    setUser(user);
  };
  
  const logout = () => {
    localStorage.removeItem('authToken');
    setUser(null);
  };
  
  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
```

---

## Testing Patterns

**Component Test Example**:

```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import StudentForm from '@pages/StudentRegistration';
import * as studentService from '@services/studentService';

// Mock service
vi.mock('@services/studentService');

describe('StudentForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });
  
  test('renders all required fields', () => {
    render(<StudentForm />);
    
    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
  });
  
  test('shows validation errors on empty submit', async () => {
    render(<StudentForm />);
    
    const submitButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText(/first name is required/i)).toBeInTheDocument();
    });
  });
  
  test('submits form with valid data', async () => {
    studentService.createStudent.mockResolvedValue({ data: { id: 1 } });
    
    render(<StudentForm />);
    
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' }
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: 'Doe' }
    });
    
    const submitButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(studentService.createStudent).toHaveBeenCalledWith(
        expect.objectContaining({
          firstName: 'John',
          lastName: 'Doe'
        })
      );
    });
  });
});
```

---

## Code Style Guidelines

### Component Naming
- **PascalCase** for components: `StudentForm.jsx`
- **camelCase** for utilities: `formatDate.js`
- **kebab-case** for CSS files: `student-form.css`

### File Organization
```javascript
// 1. Imports (grouped)
import { useState } from 'react';              // React
import { useNavigate } from 'react-router-dom'; // Third-party
import Button from '@components/common/Button'; // Components
import { studentService } from '@services';     // Services
import { formatDate } from '@utils';            // Utils
import './StudentForm.css';                     // Styles

// 2. Component definition
function StudentForm({ prop1, prop2 }) {
  // 3. State declarations
  const [state, setState] = useState();
  
  // 4. Effects
  useEffect(() => {}, []);
  
  // 5. Event handlers
  const handleSubmit = () => {};
  
  // 6. Render
  return <div>...</div>;
}

// 7. Export
export default StudentForm;
```

### Best Practices
- ✅ **Do**: Use functional components with hooks
- ✅ **Do**: Destructure props: `function Button({ onClick, children })`
- ✅ **Do**: Use async/await for API calls
- ✅ **Do**: Keep components small and focused (<200 lines)
- ✅ **Do**: Extract reusable logic into custom hooks
- ❌ **Don't**: Use class components
- ❌ **Don't**: Mutate state directly
- ❌ **Don't**: Use inline styles (use Tailwind classes)
- ❌ **Don't**: Mix business logic with presentation

---

## Performance Optimization

### Code Splitting
```javascript
import { lazy, Suspense } from 'react';

const StudentRegistration = lazy(() => import('@pages/StudentRegistration'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <StudentRegistration />
    </Suspense>
  );
}
```

### Memoization
```javascript
import { useMemo, useCallback } from 'react';

const expensiveValue = useMemo(() => computeExpensive(data), [data]);

const memoizedCallback = useCallback(() => {
  doSomething(a, b);
}, [a, b]);
```

---

## Feature Integration

When implementing a new feature:

1. **Read Tier 3 feature agent** (e.g., `docs/features/CLAUDE-FEATURE-STUDENT.md`)
2. **Create service methods** in `services/` for API calls
3. **Build page component** in `pages/` with routing
4. **Extract reusable components** to `components/`
5. **Add custom hooks** if complex logic needs sharing
6. **Write tests** for components and hooks
7. **Update routing** in `App.jsx`
8. **Test integration** with backend running

---

## Common Pitfalls & Solutions

| Issue | Solution |
|-------|----------|
| CORS errors | Ensure Vite proxy configured correctly |
| Infinite re-renders | Check useEffect dependencies |
| Stale closures | Use functional setState updates |
| Memory leaks | Cleanup in useEffect return function |
| Form resets on submit | Use `e.preventDefault()` |
| Slow autocomplete | Implement debouncing |

---

## Next Steps

- For specific feature implementation details, load the relevant **Tier 3 feature agent**
- For backend API contracts, reference **CLAUDE-BACKEND.md**
- For testing strategies, see **CLAUDE-TESTING.md**
- For Git workflow, see **CLAUDE-GIT.md**

---

**Agent Directive**: This is a Tier 2 component agent. When working on frontend tasks, combine this agent's patterns with the relevant Tier 3 feature agent for complete implementation guidance.
