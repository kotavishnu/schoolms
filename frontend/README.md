# School Management System - Frontend

A modern, production-ready React frontend application for the School Management System built with Next.js 15, TypeScript, and Tailwind CSS.

## Features

### Student Management
- Create, read, update, and delete student records
- Search students by last name and filter by status
- Paginated student list with 20 items per page
- View detailed student information
- Edit student name, mobile, and status
- Age validation (3-18 years) during registration
- Unique mobile number validation

### Configuration Management
- CRUD operations for system settings
- Settings organized by category (General, Academic, Financial)
- Filter settings by category
- Easy-to-use modal forms for create/edit

### User Experience
- Responsive design for mobile and desktop
- Real-time form validation with helpful error messages
- Loading states and error handling
- Success/error toast notifications
- Confirmation dialogs for destructive actions
- Debounced search for better performance

## Technology Stack

### Core
- **React 19** - Latest React with concurrent features
- **Next.js 15** - App Router for file-based routing and SSR
- **TypeScript 5.3** - Type-safe development
- **Tailwind CSS 3.4** - Utility-first CSS framework

### State & Data
- **React Query 5** - Server state management with caching
- **Axios 1.6** - HTTP client with interceptors
- **React Hook Form 7** - Performant form handling
- **Zod 3.22** - Runtime type validation

### UI Components
- **Lucide React** - Beautiful, consistent icons
- **React Hot Toast** - Elegant toast notifications
- **date-fns 4** - Modern date formatting

### Development
- **ESLint** - Code linting
- **PostCSS** - CSS processing
- **Vitest** - Unit testing framework

## Prerequisites

- Node.js 20.x or higher
- npm 10.x or higher
- Backend API running on http://localhost:8081

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Create environment file:
```bash
cp .env.local .env.local
```

4. Update environment variables in `.env.local`:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081/api/v1
```

## Development

Start the development server:
```bash
npm run dev
```

The application will be available at http://localhost:3000

### Available Scripts

- `npm run dev` - Start development server (port 3000)
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run ESLint
- `npm test` - Run tests with Vitest
- `npm run test:ui` - Run tests with UI
- `npm run test:coverage` - Generate coverage report

## Project Structure

```
frontend/
├── app/                          # Next.js App Router
│   ├── students/                 # Student pages
│   │   ├── [studentKey]/         # Dynamic routes
│   │   │   ├── edit/             # Edit student page
│   │   │   └── page.tsx          # Student detail page
│   │   ├── new/                  # Create student page
│   │   └── page.tsx              # Student list page
│   ├── settings/                 # Settings pages
│   │   └── page.tsx              # Configuration management
│   ├── layout.tsx                # Root layout
│   ├── page.tsx                  # Home page
│   ├── providers.tsx             # React Query provider
│   └── globals.css               # Global styles
├── components/                   # Reusable UI components
│   ├── ConfigForm.tsx            # Configuration form modal
│   ├── ConfirmDialog.tsx         # Confirmation dialog
│   ├── ErrorMessage.tsx          # Error display
│   ├── LoadingSpinner.tsx        # Loading states
│   ├── Navigation.tsx            # Top navigation
│   └── Pagination.tsx            # Pagination controls
├── services/                     # API service layer
│   ├── studentService.ts         # Student API calls
│   └── configService.ts          # Configuration API calls
├── lib/                          # Utilities and config
│   ├── api-client.ts             # Axios instance with interceptors
│   └── validations.ts            # Zod schemas
├── types/                        # TypeScript types
│   └── index.ts                  # All type definitions
├── hooks/                        # Custom React hooks
│   └── useDebounce.ts            # Debounce hook
├── utils/                        # Utility functions
│   └── format.ts                 # Formatting helpers
├── next.config.js                # Next.js configuration
├── tailwind.config.ts            # Tailwind configuration
├── tsconfig.json                 # TypeScript configuration
└── package.json                  # Dependencies and scripts
```

## Key Features Implementation

### API Integration
All API calls go through the centralized `api-client.ts` which:
- Adds correlation IDs to requests
- Handles RFC 7807 error format
- Provides consistent error handling
- Manages request/response interceptors

### Form Validation
Forms use React Hook Form with Zod resolvers:
```typescript
const schema = z.object({
  firstName: z.string().min(1, 'Required').max(100),
  dateOfBirth: z.string().refine(validateAge, 'Age 3-18 required'),
  mobile: z.string().regex(/^\+?[0-9]{10,15}$/, 'Invalid format'),
});
```

### Search with Debouncing
Search input is debounced by 500ms to reduce API calls:
```typescript
const debouncedSearch = useDebounce(searchTerm, 500);
```

### Pagination
Pagination follows Spring Data Page response format:
```typescript
interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  // ... other fields
}
```

## API Endpoints

The frontend integrates with these backend endpoints:

### Student API
- `POST /api/v1/students` - Create student
- `GET /api/v1/students/{studentKey}` - Get student by ID
- `PUT /api/v1/students/{studentKey}` - Update student
- `DELETE /api/v1/students/{studentKey}` - Delete student
- `GET /api/v1/students?lastName={term}&page={p}&size={s}` - Search students

### Configuration API
- `POST /api/v1/config/settings` - Create setting
- `GET /api/v1/config/settings/{id}` - Get setting
- `PUT /api/v1/config/settings/{id}` - Update setting
- `DELETE /api/v1/config/settings/{id}` - Delete setting
- `GET /api/v1/config/settings?category={cat}` - List settings

## Validation Rules

### Student Registration
- **Age**: Must be between 3 and 18 years at registration
- **Mobile**: 10-15 digits, optionally starting with +
- **Email**: Valid email format (optional)
- **Adhaar**: Exactly 12 digits (optional)
- **First/Last Name**: 1-100 characters (required)

### Student Update
Only these fields can be updated:
- First Name
- Last Name
- Mobile Number
- Status (Active/Inactive)

### Configuration Settings
- **Category**: Must be General, Academic, or Financial
- **Setting Key**: Lowercase with dots, underscores, hyphens only
- **Setting Value**: Required string
- **Description**: Optional, max 500 characters

## Error Handling

The application handles errors in multiple layers:

1. **API Client**: Catches network errors and RFC 7807 problem details
2. **Services**: Propagate errors with proper types
3. **Components**: Display user-friendly error messages
4. **Forms**: Show field-specific validation errors

Example error response:
```json
{
  "type": "validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "errors": [
    {
      "field": "mobile",
      "message": "Mobile number already registered",
      "code": "DUPLICATE_MOBILE"
    }
  ]
}
```

## Best Practices

### Code Organization
- Components are functional with TypeScript
- Services handle all API calls
- Validation schemas are centralized
- Types are explicitly defined
- Error handling is consistent

### Performance
- React Query caches API responses (5 min stale time)
- Search is debounced (500ms)
- Components use proper loading states
- Pagination reduces data transfer

### User Experience
- Clear loading indicators
- Helpful validation messages
- Confirmation dialogs for destructive actions
- Toast notifications for feedback
- Responsive design for all screens

### Accessibility
- Semantic HTML elements
- Proper form labels
- Keyboard navigation support
- ARIA attributes where needed
- Color contrast compliance

## Troubleshooting

### Backend Connection Issues
If you see "Unable to connect to server":
1. Verify backend is running on http://localhost:8081
2. Check CORS configuration in backend
3. Verify `.env.local` has correct API URL

### Build Errors
If build fails:
1. Delete `.next` folder and `node_modules`
2. Run `npm install` again
3. Run `npm run build`

### Type Errors
If TypeScript errors appear:
1. Ensure all dependencies are installed
2. Run `npm run lint` to see specific errors
3. Check that types align with backend API responses

## Deployment

### Production Build
```bash
npm run build
npm start
```

### Environment Variables
Set these in production:
```env
NEXT_PUBLIC_API_BASE_URL=https://api.yourschool.com/api/v1
```

### Docker Deployment
```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

## Browser Support

- Chrome/Edge: Last 2 versions
- Firefox: Last 2 versions
- Safari: Last 2 versions
- Mobile browsers: iOS Safari, Chrome Mobile

## Contributing

1. Follow the existing code style
2. Add types for all new code
3. Write unit tests for utilities
4. Update documentation for new features
5. Test on multiple screen sizes

## License

Copyright 2025 School Management System. All rights reserved.

## Support

For issues and questions:
- Check backend API logs
- Review browser console for errors
- Verify network requests in DevTools
- Check that backend APIs are accessible

## Changelog

### Version 1.0.0 (2025-12-08)
- Initial release
- Student management (CRUD)
- Configuration management (CRUD)
- Search and pagination
- Form validation
- Error handling
- Toast notifications
- Responsive design
