// Student Status Options
export const STUDENT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  GRADUATED: 'GRADUATED',
  TRANSFERRED: 'TRANSFERRED',
};

export const STUDENT_STATUS_OPTIONS = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'GRADUATED', label: 'Graduated' },
  { value: 'TRANSFERRED', label: 'Transferred' },
];

// Fee Type Options
export const FEE_TYPE = {
  TUITION: 'TUITION',
  LIBRARY: 'LIBRARY',
  COMPUTER: 'COMPUTER',
  SPORTS: 'SPORTS',
  SPECIAL: 'SPECIAL',
  EXAMINATION: 'EXAMINATION',
  MAINTENANCE: 'MAINTENANCE',
  TRANSPORT: 'TRANSPORT',
};

export const FEE_TYPE_OPTIONS = [
  { value: 'TUITION', label: 'Tuition' },
  { value: 'LIBRARY', label: 'Library' },
  { value: 'COMPUTER', label: 'Computer' },
  { value: 'SPORTS', label: 'Sports' },
  { value: 'SPECIAL', label: 'Special' },
  { value: 'EXAMINATION', label: 'Examination' },
  { value: 'MAINTENANCE', label: 'Maintenance' },
  { value: 'TRANSPORT', label: 'Transport' },
];

// Fee Frequency Options
export const FEE_FREQUENCY = {
  MONTHLY: 'MONTHLY',
  QUARTERLY: 'QUARTERLY',
  ANNUAL: 'ANNUAL',
  ONE_TIME: 'ONE_TIME',
};

export const FEE_FREQUENCY_OPTIONS = [
  { value: 'MONTHLY', label: 'Monthly' },
  { value: 'QUARTERLY', label: 'Quarterly' },
  { value: 'ANNUAL', label: 'Annual' },
  { value: 'ONE_TIME', label: 'One Time' },
];

// Payment Status Options
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  PARTIAL: 'PARTIAL',
  PAID: 'PAID',
  OVERDUE: 'OVERDUE',
};

export const PAYMENT_STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'PARTIAL', label: 'Partial' },
  { value: 'PAID', label: 'Paid' },
  { value: 'OVERDUE', label: 'Overdue' },
];

// Payment Method Options
export const PAYMENT_METHOD = {
  CASH: 'CASH',
  ONLINE: 'ONLINE',
  CHEQUE: 'CHEQUE',
  CARD: 'CARD',
};

export const PAYMENT_METHOD_OPTIONS = [
  { value: 'CASH', label: 'Cash' },
  { value: 'ONLINE', label: 'Online' },
  { value: 'CHEQUE', label: 'Cheque' },
  { value: 'CARD', label: 'Card' },
];

// Class Numbers
export const CLASS_NUMBERS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

// Months
export const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

export const MONTH_OPTIONS = MONTHS.map(month => ({
  value: month,
  label: month
}));

// Config Data Types
export const CONFIG_DATA_TYPE = {
  STRING: 'STRING',
  INTEGER: 'INTEGER',
  BOOLEAN: 'BOOLEAN',
  JSON: 'JSON',
};

export const CONFIG_DATA_TYPE_OPTIONS = [
  { value: 'STRING', label: 'String' },
  { value: 'INTEGER', label: 'Integer' },
  { value: 'BOOLEAN', label: 'Boolean' },
  { value: 'JSON', label: 'JSON' },
];

// Config Categories
export const CONFIG_CATEGORY_OPTIONS = [
  { value: 'GENERAL', label: 'General' },
  { value: 'FEE', label: 'Fee' },
  { value: 'ACADEMIC', label: 'Academic' },
  { value: 'SYSTEM', label: 'System' },
];

// Academic Year Helper
export const getCurrentAcademicYear = () => {
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1; // 0-indexed

  // Academic year typically starts in April (month 4)
  if (currentMonth >= 4) {
    return `${currentYear}-${currentYear + 1}`;
  } else {
    return `${currentYear - 1}-${currentYear}`;
  }
};

// Generate academic year options (last 5 years + next 2 years)
export const getAcademicYearOptions = () => {
  const currentYear = new Date().getFullYear();
  const years = [];

  for (let i = -5; i <= 2; i++) {
    const year = currentYear + i;
    years.push({
      value: `${year}-${year + 1}`,
      label: `${year}-${year + 1}`
    });
  }

  return years;
};

// Status Badge Colors
export const STATUS_COLORS = {
  ACTIVE: 'bg-green-100 text-green-800',
  INACTIVE: 'bg-gray-100 text-gray-800',
  GRADUATED: 'bg-blue-100 text-blue-800',
  TRANSFERRED: 'bg-yellow-100 text-yellow-800',
  PENDING: 'bg-yellow-100 text-yellow-800',
  PARTIAL: 'bg-orange-100 text-orange-800',
  PAID: 'bg-green-100 text-green-800',
  OVERDUE: 'bg-red-100 text-red-800',
};
