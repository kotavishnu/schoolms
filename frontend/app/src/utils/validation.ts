/**
 * Validation Utility Functions
 * School Management System - Phase 1
 */

import { AGE_RANGE } from './constants';

/**
 * Validates if a phone number is in correct format (10 digits)
 */
export function isValidPhone(phone: string): boolean {
  const phoneRegex = /^\d{10}$/;
  return phoneRegex.test(phone);
}

/**
 * Validates if an email is in correct format
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validates if an Adhaar number is in correct format (12 digits)
 */
export function isValidAdhaar(adhaar: string): boolean {
  const adhaarRegex = /^\d{12}$/;
  return adhaarRegex.test(adhaar);
}

/**
 * Calculates age from date of birth
 */
export function calculateAge(dob: string | Date): number {
  const today = new Date();
  const birthDate = new Date(dob);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
}

/**
 * Validates if age is within the acceptable range (3-18 years)
 */
export function isAgeInRange(dob: string | Date): boolean {
  const age = calculateAge(dob);
  return age >= AGE_RANGE.MIN && age <= AGE_RANGE.MAX;
}

/**
 * Validates if a date is not in the future
 */
export function isNotFutureDate(date: string | Date): boolean {
  return new Date(date) <= new Date();
}

/**
 * Validates name format (only letters and spaces)
 */
export function isValidName(name: string): boolean {
  const nameRegex = /^[a-zA-Z\s]+$/;
  return nameRegex.test(name);
}

/**
 * Validates configuration key format (uppercase letters, numbers, underscores)
 */
export function isValidConfigKey(key: string): boolean {
  const keyRegex = /^[A-Z0-9_]+$/;
  return keyRegex.test(key);
}

/**
 * Validates string length
 */
export function isValidLength(
  value: string,
  min?: number,
  max?: number
): { valid: boolean; message?: string } {
  if (min !== undefined && value.length < min) {
    return {
      valid: false,
      message: `Must be at least ${min} characters`,
    };
  }

  if (max !== undefined && value.length > max) {
    return {
      valid: false,
      message: `Must be at most ${max} characters`,
    };
  }

  return { valid: true };
}
