import { z } from 'zod';

/**
 * Zod validation schemas for Student forms
 */

/**
 * Calculate age from date of birth
 */
const calculateAge = (dateOfBirth: Date): number => {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
};

/**
 * Address schema
 */
const addressSchema = z.object({
  street: z.string().min(5, 'Street address must be at least 5 characters'),
  city: z.string().min(2, 'City must be at least 2 characters'),
  state: z.string().min(2, 'State must be at least 2 characters'),
  postalCode: z.string().regex(/^\d{6}$/, 'Postal code must be exactly 6 digits'),
  country: z.string().min(2, 'Country must be at least 2 characters'),
});

/**
 * Guardian schema
 */
const guardianSchema = z.object({
  name: z.string().min(2, 'Guardian name must be at least 2 characters').max(50, 'Guardian name must not exceed 50 characters'),
  relationship: z.enum(['Father', 'Mother', 'Guardian', 'Other'], {
    message: 'Please select a valid relationship',
  }),
  phone: z.string().regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits'),
  email: z.string().optional(),
  occupation: z.string().max(50, 'Occupation must not exceed 50 characters').optional(),
});

/**
 * Academic information schema
 */
const academicSchema = z.object({
  classId: z.number().int().positive('Please select a class'),
  rollNumber: z.string().max(20, 'Roll number must not exceed 20 characters').optional(),
  admissionDate: z.string().min(1, 'Admission date is required'),
  previousSchool: z.string().max(100, 'Previous school name must not exceed 100 characters').optional(),
});

/**
 * Main student registration schema
 * Implements BR-1: Age validation (3-18 years)
 * Implements BR-2: Mobile uniqueness (handled by API)
 */
export const studentRegistrationSchema = z.object({
  firstName: z.string()
    .min(2, 'First name must be at least 2 characters')
    .max(50, 'First name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters'),

  lastName: z.string()
    .min(2, 'Last name must be at least 2 characters')
    .max(50, 'Last name must not exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters'),

  dateOfBirth: z.string()
    .min(1, 'Date of birth is required')
    .refine((dateStr) => {
      const date = new Date(dateStr);
      return !isNaN(date.getTime());
    }, 'Invalid date format')
    .refine((dateStr) => {
      const date = new Date(dateStr);
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, 'Student must be between 3-18 years old (BR-1)'),

  gender: z.enum(['Male', 'Female', 'Other'], {
    message: 'Please select a gender',
  }),

  bloodGroup: z.enum(['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']).optional(),

  email: z.string().email('Invalid email format').optional(),

  phone: z.string()
    .regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits')
    .optional(),

  photo: z.instanceof(File).optional().nullable()
    .refine(
      (file) => !file || file.size <= 5 * 1024 * 1024,
      'Photo size must be less than 5MB'
    )
    .refine(
      (file) => !file || ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'].includes(file.type),
      'Photo must be in JPEG, PNG, or WebP format'
    ),

  address: addressSchema,

  guardian: guardianSchema,

  academic: academicSchema,
}).refine(
  (data) => {
    // Allow empty string or valid email
    if (data.email && data.email.trim() !== '') {
      return z.string().email().safeParse(data.email).success;
    }
    return true;
  },
  {
    message: 'Invalid email format',
    path: ['email'],
  }
).refine(
  (data) => {
    // Allow empty string or valid phone
    if (data.phone && data.phone.trim() !== '') {
      return /^\d{10}$/.test(data.phone);
    }
    return true;
  },
  {
    message: 'Mobile number must be exactly 10 digits',
    path: ['phone'],
  }
);

/**
 * Student update schema (same as registration)
 */
export const studentUpdateSchema = studentRegistrationSchema;

/**
 * Type inference from Zod schemas
 */
export type StudentRegistrationFormData = z.infer<typeof studentRegistrationSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
