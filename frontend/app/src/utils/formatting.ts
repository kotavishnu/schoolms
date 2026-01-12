/**
 * Formatting Utility Functions
 * School Management System - Phase 1
 */

import { format, parseISO } from 'date-fns';
import type { StudentStatus, ConfigCategory, CategoryBadgeStyle } from '../types';

/**
 * Formats a date string to a readable format
 */
export function formatDate(
  date: string | Date,
  formatStr: string = 'MMM dd, yyyy'
): string {
  try {
    const dateObj = typeof date === 'string' ? parseISO(date) : date;
    return format(dateObj, formatStr);
  } catch (error) {
    console.error('Error formatting date:', error);
    return 'Invalid date';
  }
}

/**
 * Formats a datetime string to include time
 */
export function formatDateTime(
  date: string | Date,
  formatStr: string = 'MMM dd, yyyy HH:mm'
): string {
  return formatDate(date, formatStr);
}

/**
 * Returns styling for student status badge
 */
export function getStatusBadgeStyle(status: StudentStatus): {
  text: string;
  variant: 'default' | 'secondary' | 'destructive' | 'outline';
  className: string;
} {
  switch (status) {
    case 'ACTIVE':
      return {
        text: 'Active',
        variant: 'default',
        className: 'bg-green-100 text-green-800 hover:bg-green-100',
      };
    case 'INACTIVE':
      return {
        text: 'Inactive',
        variant: 'secondary',
        className: 'bg-gray-100 text-gray-800 hover:bg-gray-100',
      };
    default:
      return {
        text: status,
        variant: 'outline',
        className: '',
      };
  }
}

/**
 * Returns styling for configuration category badge
 */
export function getCategoryBadgeStyle(category: ConfigCategory): CategoryBadgeStyle {
  switch (category) {
    case 'GENERAL':
      return {
        text: 'General',
        bgColor: 'bg-blue-100',
        textColor: 'text-blue-800',
      };
    case 'ACADEMIC':
      return {
        text: 'Academic',
        bgColor: 'bg-purple-100',
        textColor: 'text-purple-800',
      };
    case 'FINANCE':
      return {
        text: 'Finance',
        bgColor: 'bg-green-100',
        textColor: 'text-green-800',
      };
    case 'SYSTEM':
      return {
        text: 'System',
        bgColor: 'bg-gray-100',
        textColor: 'text-gray-800',
      };
    default:
      return {
        text: category,
        bgColor: 'bg-gray-100',
        textColor: 'text-gray-800',
      };
  }
}

/**
 * Masks middle digits of phone number for privacy
 */
export function maskPhone(phone: string): string {
  if (phone.length !== 10) return phone;
  return `${phone.substring(0, 3)}***${phone.substring(7)}`;
}

/**
 * Formats phone number with dashes
 */
export function formatPhoneNumber(phone: string): string {
  if (phone.length !== 10) return phone;
  return `${phone.substring(0, 3)}-${phone.substring(3, 6)}-${phone.substring(6)}`;
}

/**
 * Capitalizes first letter of each word
 */
export function capitalizeWords(text: string): string {
  return text
    .split(' ')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
}

/**
 * Truncates text to specified length with ellipsis
 */
export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength - 3) + '...';
}
