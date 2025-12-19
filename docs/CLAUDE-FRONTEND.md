# CLAUDE-FRONTEND.md

Frontend guidance for Claude Code working with the School Management System React application.

## Tech Stack
- **Framework**: React 18 + Next.js and Tailwind CSS
- **Build**: Vite
- **HTTP Client**: Axios
- **Dev Server**: http://localhost:3000

## Quick Commands

```bash
cd frontend
npm install            # Install dependencies
npm run dev            # Dev server with HMR
npm run build          # Production build
npm run preview        # Preview prod build
npm run lint           # Lint check
npm run test           # Run tests
```

## Architecture

```
src/
├── components/  # Reusable UI components
├── pages/       # Page-level components
├── services/    # API integration layer
├── hooks/       # Custom React hooks
├── contexts/    # Global state providers
├── utils/       # Helper functions
└── assets/      # Static files
```

## Key Pages

**StudentRegistration.jsx**: Form with firstName, lastName, dob, address, caste, mobile, religion, molesOnBody, motherName, fatherName, class selection

**ClassManagement.jsx**: View/manage Classes 1-10

**FeeMaster.jsx**: Configure fee structures (amount, type, frequency, classes)

**FeeReceipt.jsx**: Search students (autocomplete by name) → Calculate fee → Generate receipt

**ParentPortal.jsx**: Parent-facing fee payment interface

**SchoolConfig.jsx**: Setup school name, address, fee frequency

## API Service Layer

**services/api.js** (Base config):
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 10000,
});

// Add interceptors for auth, error handling
export default api;
```

**services/studentService.js**:
```javascript
import api from './api';

export const createStudent = (data) => api.post('/students', data);
export const getStudent = (id) => api.get(`/students/${id}`);
export const searchStudents = (query) => api.get(`/students/search?q=${query}`);
// ... other methods
```

**services/feeService.js**: Fee-related API calls
**services/classService.js**: Class-related API calls

## Configuration

**vite.config.js** (Proxy for dev):
```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
});
```

**.env**:
```
VITE_API_URL=http://localhost:8080/api
```

## Component Patterns

**Functional components with hooks**:
```javascript
import { useState, useEffect } from 'react';

function StudentForm() {
  const [formData, setFormData] = useState({});
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await studentService.createStudent(formData);
      // Success notification
    } catch (error) {
      // Error handling
    }
  };
  
  return <form onSubmit={handleSubmit}>...</form>;
}
```

**Autocomplete Search** (FeeReceipt):
```javascript
const [searchQuery, setSearchQuery] = useState('');
const [suggestions, setSuggestions] = useState([]);

useEffect(() => {
  if (searchQuery.length > 2) {
    const debounce = setTimeout(async () => {
      const results = await studentService.searchStudents(searchQuery);
      setSuggestions(results.data);
    }, 300);
    return () => clearTimeout(debounce);
  }
}, [searchQuery]);
```

## Error Handling

```javascript
try {
  await studentService.createStudent(data);
  toast.success('Student registered successfully');
} catch (error) {
  const message = error.response?.data?.message || 'An error occurred';
  toast.error(message);
}
```

## State Management

- Use React Context for global state (user auth, school config)
- Local state with `useState` for component-specific data
- Custom hooks for reusable logic

## Testing (to implement)

**Component Tests** (React Testing Library):
```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import StudentForm from './StudentForm';

test('submits student registration', async () => {
  render(<StudentForm />);
  fireEvent.change(screen.getByLabelText('First Name'), {
    target: { value: 'John' }
  });
  // ... test flow
});
```

## Code Style

- Functional components only
- Use async/await for API calls
- Prefer `const`/`let` over `var`
- Follow ESLint rules
- Keep components focused & small

## Adding New Features

1. Create service method in `services/`
2. Create/update component in `pages/` or `components/`
3. Add form validation
4. Implement error handling
5. Write component tests
6. Test API integration with backend

Make sure frontend and backend are in sync with fields on UI and in PostgreSQL Table