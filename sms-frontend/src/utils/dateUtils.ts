import { format, parseISO, differenceInYears, isValid } from 'date-fns';

/**
 * Format a date string to a readable format
 */
export const formatDate = (dateString: string, formatStr: string = 'MMM dd, yyyy'): string => {
  try {
    const date = parseISO(dateString);
    return isValid(date) ? format(date, formatStr) : dateString;
  } catch {
    return dateString;
  }
};

/**
 * Calculate age from date of birth
 */
export const calculateAge = (dateOfBirth: string): number => {
  try {
    const birthDate = parseISO(dateOfBirth);
    return differenceInYears(new Date(), birthDate);
  } catch {
    return 0;
  }
};

/**
 * Check if date of birth is valid (age between 3-18)
 */
export const isValidDateOfBirth = (dateOfBirth: string): boolean => {
  const age = calculateAge(dateOfBirth);
  return age >= 3 && age <= 18;
};

/**
 * Convert Date to ISO date string (YYYY-MM-DD)
 */
export const toISODate = (date: Date): string => {
  return format(date, 'yyyy-MM-dd');
};

/**
 * Format timestamp to readable date time
 */
export const formatDateTime = (dateString: string): string => {
  return formatDate(dateString, 'MMM dd, yyyy HH:mm:ss');
};
