import { format, parseISO } from 'date-fns';

/**
 * Format date string to readable format
 */
export function formatDate(dateString: string, formatStr: string = 'MMM dd, yyyy'): string {
  try {
    return format(parseISO(dateString), formatStr);
  } catch (error) {
    return dateString;
  }
}

/**
 * Format datetime string to readable format
 */
export function formatDateTime(dateString: string, formatStr: string = 'MMM dd, yyyy HH:mm'): string {
  try {
    return format(parseISO(dateString), formatStr);
  } catch (error) {
    return dateString;
  }
}

/**
 * Format mobile number for display
 */
export function formatMobile(mobile: string): string {
  // Remove all non-digit characters
  const cleaned = mobile.replace(/\D/g, '');

  // Format as +91 98765 43210
  if (cleaned.length === 12 && cleaned.startsWith('91')) {
    return `+91 ${cleaned.slice(2, 7)} ${cleaned.slice(7)}`;
  }

  // Format as 98765 43210
  if (cleaned.length === 10) {
    return `${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
  }

  return mobile;
}

/**
 * Truncate text to specified length
 */
export function truncate(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

/**
 * Calculate age from date of birth
 */
export function calculateAge(dateOfBirth: string): number {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
}

/**
 * Format status with appropriate styling
 */
export function getStatusColor(status: 'Active' | 'Inactive'): string {
  return status === 'Active'
    ? 'bg-green-100 text-green-800'
    : 'bg-gray-100 text-gray-800';
}
