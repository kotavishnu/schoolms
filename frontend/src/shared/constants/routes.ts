/**
 * Application route constants
 */

export const ROUTES = {
  // Auth routes
  LOGIN: '/login',
  UNAUTHORIZED: '/unauthorized',

  // Dashboard
  DASHBOARD: '/',

  // Student routes
  STUDENTS: '/students',
  STUDENT_NEW: '/students/new',
  STUDENT_DETAILS: '/students/:studentId',
  STUDENT_EDIT: '/students/:studentId/edit',

  // Class routes
  CLASSES: '/classes',
  CLASS_NEW: '/classes/new',
  CLASS_DETAILS: '/classes/:classId',

  // Fee routes
  FEES: '/fees',
  FEE_STRUCTURES: '/fees/structures',
  FEE_JOURNALS: '/fees/journals',

  // Payment routes
  PAYMENTS: '/payments',
  PAYMENT_NEW: '/payments/new',
  PAYMENT_HISTORY: '/payments/history',

  // Receipt routes
  RECEIPTS: '/receipts',

  // Report routes
  REPORTS: '/reports',
  REPORT_ENROLLMENT: '/reports/enrollment',
  REPORT_FEES: '/reports/fees',
  REPORT_COLLECTION: '/reports/collection',

  // Config routes
  CONFIG: '/config',
} as const;
