import { z } from 'zod';

// Helper function to calculate age
function calculateAge(dateOfBirth: string): number {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
}

// Student validation schemas
export const createStudentSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(100, 'First name must be less than 100 characters')
    .trim(),
  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(100, 'Last name must be less than 100 characters')
    .trim(),
  dateOfBirth: z
    .string()
    .min(1, 'Date of birth is required')
    .refine((date) => {
      const birthDate = new Date(date);
      return !isNaN(birthDate.getTime()) && birthDate < new Date();
    }, 'Date of birth must be in the past')
    .refine((date) => {
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, 'Student must be between 3 and 18 years old'),
  mobile: z
    .string()
    .min(1, 'Mobile number is required')
    .regex(/^\+?[0-9]{10,15}$/, 'Mobile number must be 10-15 digits, optionally starting with +')
    .trim(),
  email: z
    .string()
    .email('Invalid email format')
    .max(100, 'Email must be less than 100 characters')
    .optional()
    .or(z.literal('')),
  address: z
    .string()
    .max(1000, 'Address must be less than 1000 characters')
    .optional()
    .or(z.literal('')),
  fatherNameOrGuardian: z
    .string()
    .max(100, 'Father/Guardian name must be less than 100 characters')
    .optional()
    .or(z.literal('')),
  motherName: z
    .string()
    .max(100, 'Mother name must be less than 100 characters')
    .optional()
    .or(z.literal('')),
  identificationMark: z
    .string()
    .max(200, 'Identification mark must be less than 200 characters')
    .optional()
    .or(z.literal('')),
  adhaarNumber: z
    .string()
    .regex(/^[0-9]{12}$/, 'Adhaar number must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export const updateStudentSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(100, 'First name must be less than 100 characters')
    .trim(),
  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(100, 'Last name must be less than 100 characters')
    .trim(),
  mobile: z
    .string()
    .min(1, 'Mobile number is required')
    .regex(/^\+?[0-9]{10,15}$/, 'Mobile number must be 10-15 digits, optionally starting with +')
    .trim(),
  status: z.enum(['Active', 'Inactive'], {
    required_error: 'Status is required',
  }),
});

// Configuration validation schemas
export const createConfigSchema = z.object({
  category: z.enum(['General', 'Academic', 'Financial'], {
    required_error: 'Category is required',
  }),
  key: z
    .string()
    .min(1, 'Setting key is required')
    .max(100, 'Setting key must be less than 100 characters')
    .regex(/^[a-z0-9._-]+$/, 'Setting key must contain only lowercase letters, numbers, dots, underscores, and hyphens')
    .trim(),
  value: z
    .string()
    .min(1, 'Setting value is required')
    .trim(),
  description: z
    .string()
    .max(500, 'Description must be less than 500 characters')
    .optional()
    .or(z.literal('')),
});

export const updateConfigSchema = z.object({
  value: z
    .string()
    .min(1, 'Setting value is required')
    .trim(),
  description: z
    .string()
    .max(500, 'Description must be less than 500 characters')
    .optional()
    .or(z.literal('')),
});

// Type inference
export type CreateStudentFormData = z.infer<typeof createStudentSchema>;
export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;
export type CreateConfigFormData = z.infer<typeof createConfigSchema>;
export type UpdateConfigFormData = z.infer<typeof updateConfigSchema>;
