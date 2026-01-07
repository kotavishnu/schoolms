# School Management System - Frontend

React-based frontend application for the School Management System.

## Tech Stack

- **Framework**: React 18
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **Notifications**: React Hot Toast

## Prerequisites

- Node.js 18 or higher
- npm or yarn

## Getting Started

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Configure environment**:
   - Copy `.env` file and update `VITE_API_URL` if needed
   - Default backend URL: `http://localhost:8080/api`

3. **Start development server**:
   ```bash
   npm run dev
   ```
   The app will be available at `http://localhost:3000`

4. **Build for production**:
   ```bash
   npm run build
   ```

5. **Preview production build**:
   ```bash
   npm run preview
   ```

## Project Structure

```
src/
├── components/       # Reusable UI components
│   ├── Button.jsx
│   ├── Card.jsx
│   ├── Input.jsx
│   ├── Select.jsx
│   ├── Loading.jsx
│   └── Layout.jsx
├── pages/           # Page-level components
│   ├── Home.jsx
│   ├── StudentRegistration.jsx
│   ├── ClassManagement.jsx
│   ├── FeeMaster.jsx
│   ├── FeeReceipt.jsx
│   ├── ParentPortal.jsx
│   └── SchoolConfig.jsx
├── services/        # API integration layer
│   ├── api.js
│   ├── studentService.js
│   ├── feeService.js
│   ├── classService.js
│   └── schoolService.js
├── hooks/           # Custom React hooks (future)
├── contexts/        # Global state providers (future)
├── utils/           # Helper functions (future)
└── assets/          # Static files
```

## Features

### 1. Student Registration
- Complete student enrollment form
- Fields: firstName, lastName, dob, address, caste, mobile, religion, molesOnBody, motherName, fatherName, class selection
- Form validation
- Success/error notifications

### 2. Class Management
- View all classes (Classes 1-10)
- View students in each class
- Student count per class

### 3. Fee Master
- Configure fee structures
- Multiple fee types (Tuition, Library, Computer, Sports, etc.)
- Fee frequency (Monthly, Quarterly, Yearly)
- Edit and delete fee masters

### 4. Fee Receipt
- Search students with autocomplete
- Calculate fees automatically
- Record payments
- Generate PDF receipts

### 5. Parent Portal
- Login with Student ID and mobile number
- View student information
- Check pending fees
- View payment history
- Online payment (placeholder)

### 6. School Configuration
- Setup school details
- Configure fee frequency
- Contact information
- Address details

## API Integration

All API calls go through the centralized `services/api.js` which:
- Sets base URL from environment variable
- Adds authentication headers
- Handles request/response interceptors
- Provides error handling

### Service Modules

- **studentService.js**: Student CRUD operations
- **feeService.js**: Fee-related operations
- **classService.js**: Class management
- **schoolService.js**: School configuration

## Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Proxy Configuration

The Vite dev server is configured to proxy API requests to the backend:

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

## Styling

Using Tailwind CSS with custom utility classes defined in `index.css`:

- `.btn-primary` - Primary button style
- `.btn-secondary` - Secondary button style
- `.input-field` - Form input style
- `.form-label` - Form label style
- `.card` - Card container style

## Error Handling

- All API calls are wrapped in try-catch blocks
- User-friendly error messages via react-hot-toast
- Network error handling in axios interceptors
- 401 errors redirect to login page

## Future Enhancements

- [ ] Add authentication/authorization
- [ ] Implement unit tests with React Testing Library
- [ ] Add E2E tests with Cypress
- [ ] Implement real-time notifications
- [ ] Add dark mode support
- [ ] Improve accessibility (ARIA labels)
- [ ] Add data export features
- [ ] Implement advanced search and filters

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Follow the component patterns established in existing code
2. Use functional components with hooks
3. Maintain consistent styling with Tailwind CSS
4. Add proper error handling for all API calls
5. Test thoroughly before committing

## License

[Your License Here]
