import { z } from 'zod';

// Helper: Calculate age from date of birth
export const calculateAge = (dob: string): number => {
  const birthDate = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
};

// Student Create Schema (all fields required except identificationMarks)
export const studentCreateSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(50, 'First name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(50, 'Last name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      if (!date) return false;
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, 'Age must be between 3 and 18 years'),

  adhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar number must be exactly 12 digits'),

  identificationMarks: z
    .string()
    .max(200, 'Identification marks must be at most 200 characters')
    .optional(),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address must be at most 500 characters'),

  guardianName: z
    .string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name must be at most 100 characters'),

  motherName: z
    .string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name must be at most 100 characters'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone must be exactly 10 digits'),

  email: z
    .string()
    .email('Email must be in valid format'),

  status: z.enum(['ACTIVE', 'INACTIVE']),
});

// Student Update Schema (only editable fields)
export const studentUpdateSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(50, 'First name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(50, 'Last name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone must be exactly 10 digits'),

  status: z.enum(['ACTIVE', 'INACTIVE']),
});

// Configuration Schema
export const configurationSchema = z.object({
  category: z.enum(['GENERAL', 'ACADEMIC', 'FINANCE', 'SYSTEM'], {
    message: 'Category is required',
  }),

  key: z
    .string()
    .min(1, 'Key is required')
    .max(100, 'Key must be at most 100 characters')
    .regex(/^[A-Z0-9_]+$/, 'Key must contain only uppercase letters, numbers, and underscores'),

  value: z
    .string()
    .min(1, 'Value is required')
    .max(1000, 'Value must be at most 1000 characters'),

  description: z
    .string()
    .max(500, 'Description must be at most 500 characters')
    .optional(),
});

export type StudentCreateFormData = z.infer<typeof studentCreateSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
export type ConfigurationFormData = z.infer<typeof configurationSchema>;
