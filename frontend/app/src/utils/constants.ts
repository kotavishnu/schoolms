/**
 * Application Constants
 * School Management System - Phase 1
 */

import type { ConfigCategory, StudentStatus } from '../types';

/**
 * Student status constants
 */
export const STUDENT_STATUS = {
  ACTIVE: 'ACTIVE' as StudentStatus,
  INACTIVE: 'INACTIVE' as StudentStatus,
  ALL: 'ALL' as const,
} as const;

/**
 * Configuration categories
 */
export const CONFIG_CATEGORIES: ConfigCategory[] = [
  'GENERAL',
  'ACADEMIC',
  'FINANCE',
  'SYSTEM',
];

/**
 * Age range constraints for student registration
 */
export const AGE_RANGE = {
  MIN: 3,
  MAX: 18,
} as const;

/**
 * Field length constraints
 */
export const FIELD_LENGTHS = {
  PHONE: 10,
  ADHAAR: 12,
  FIRST_NAME_MAX: 50,
  LAST_NAME_MAX: 50,
  ADDRESS_MIN: 10,
  ADDRESS_MAX: 500,
  IDENTIFICATION_MARKS_MAX: 200,
  GUARDIAN_NAME_MAX: 100,
  MOTHER_NAME_MAX: 100,
  CONFIG_KEY_MAX: 100,
  CONFIG_VALUE_MAX: 1000,
  CONFIG_DESCRIPTION_MAX: 500,
} as const;

/**
 * API pagination defaults
 */
export const PAGINATION = {
  DEFAULT_PAGE: 1,
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
} as const;

/**
 * Debounce delays (in milliseconds)
 */
export const DEBOUNCE_DELAYS = {
  SEARCH: 300,
  VALIDATION: 500,
} as const;

/**
 * Toast notification durations (in milliseconds)
 */
export const TOAST_DURATION = {
  SUCCESS: 3000,
  ERROR: 5000,
  INFO: 4000,
} as const;
