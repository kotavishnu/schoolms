import { z } from 'zod';

/**
 * Validation schema for creating a new student
 * Matches backend validation constraints
 */
export const studentCreateSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(50, 'First name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(50, 'Last name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  dateOfBirth: z
    .date({
      required_error: 'Date of birth is required',
      invalid_type_error: 'Invalid date',
    })
    .max(new Date(), 'Date of birth cannot be in the future')
    .refine(
      (date) => {
        const age = Math.floor((Date.now() - date.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
        return age >= 3 && age <= 18;
      },
      { message: 'Student must be between 3 and 18 years old' }
    ),

  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits')
    .refine((val) => val.length === 10, 'Mobile number must be exactly 10 digits'),

  email: z
    .string()
    .email('Invalid email address')
    .optional()
    .or(z.literal('')),

  address: z
    .string()
    .max(500, 'Address must not exceed 500 characters')
    .optional()
    .or(z.literal('')),

  fathersName: z
    .string()
    .min(2, "Father's name must be at least 2 characters")
    .max(50, "Father's name must not exceed 50 characters")
    .regex(/^[a-zA-Z\s]+$/, "Father's name must contain only letters and spaces")
    .optional()
    .or(z.literal('')),

  mothersName: z
    .string()
    .min(2, "Mother's name must be at least 2 characters")
    .max(50, "Mother's name must not exceed 50 characters")
    .regex(/^[a-zA-Z\s]+$/, "Mother's name must contain only letters and spaces")
    .optional()
    .or(z.literal('')),

  identificationMark: z
    .string()
    .max(200, 'Identification mark must not exceed 200 characters')
    .optional()
    .or(z.literal('')),

  aadhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar number must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

/**
 * Validation schema for updating an existing student
 * Only includes editable fields (excludes immutable fields like dateOfBirth, aadhaarNumber)
 */
export const studentUpdateSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(50, 'First name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(50, 'Last name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  mobile: z
    .string()
    .regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits'),

  email: z
    .string()
    .email('Invalid email address')
    .optional()
    .or(z.literal('')),

  address: z
    .string()
    .max(500, 'Address must not exceed 500 characters')
    .optional()
    .or(z.literal('')),

  fathersName: z
    .string()
    .min(2, "Father's name must be at least 2 characters")
    .max(50, "Father's name must not exceed 50 characters")
    .regex(/^[a-zA-Z\s]+$/, "Father's name must contain only letters and spaces")
    .optional()
    .or(z.literal('')),

  mothersName: z
    .string()
    .min(2, "Mother's name must be at least 2 characters")
    .max(50, "Mother's name must not exceed 50 characters")
    .regex(/^[a-zA-Z\s]+$/, "Mother's name must contain only letters and spaces")
    .optional()
    .or(z.literal('')),

  identificationMark: z
    .string()
    .max(200, 'Identification mark must not exceed 200 characters')
    .optional()
    .or(z.literal('')),

  status: z.enum(['ACTIVE', 'INACTIVE'], {
    errorMap: () => ({ message: 'Status must be either ACTIVE or INACTIVE' }),
  }),
});

/**
 * Validation schema for student enrollment
 */
export const enrollmentSchema = z.object({
  standard: z
    .string()
    .min(1, 'Standard is required')
    .max(50, 'Standard must not exceed 50 characters'),

  section: z
    .string()
    .min(1, 'Section is required')
    .max(10, 'Section must not exceed 10 characters')
    .regex(/^[A-Z]$/, 'Section must be a single uppercase letter (A-Z)'),

  academicYear: z
    .string()
    .regex(/^\d{4}-\d{4}$/, 'Academic year must be in format YYYY-YYYY (e.g., 2023-2024)'),

  rollNumber: z
    .string()
    .max(20, 'Roll number must not exceed 20 characters')
    .optional()
    .or(z.literal('')),
});

// Type exports
export type StudentCreateFormData = z.infer<typeof studentCreateSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
export type EnrollmentFormData = z.infer<typeof enrollmentSchema>;
