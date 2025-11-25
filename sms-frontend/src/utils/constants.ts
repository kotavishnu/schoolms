export const STATUS_OPTIONS = [
  { value: 'Active', label: 'Active' },
  { value: 'Inactive', label: 'Inactive' },
  { value: 'Graduated', label: 'Graduated' },
  { value: 'Transferred', label: 'Transferred' },
] as const;

export const CATEGORY_OPTIONS = [
  { value: 'General', label: 'General' },
  { value: 'Academic', label: 'Academic' },
  { value: 'Financial', label: 'Financial' },
  { value: 'System', label: 'System' },
] as const;

export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  API: 'yyyy-MM-dd',
  FULL: 'MMMM dd, yyyy HH:mm:ss',
} as const;

export const API_TIMEOUT = 10000; // 10 seconds

export const CACHE_DURATIONS = {
  SHORT: 2 * 60 * 1000, // 2 minutes
  MEDIUM: 5 * 60 * 1000, // 5 minutes
  LONG: 60 * 60 * 1000, // 1 hour
} as const;

export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 20,
  SIZE_OPTIONS: [10, 20, 50, 100],
} as const;
