import { z } from 'zod';

const mobileRegex = /^\+?[0-9]{10,15}$/;
const aadhaarRegex = /^[0-9]{12}$/;

export const studentSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(100, 'First name must be at most 100 characters'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(100, 'Last name must be at most 100 characters'),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address must be at most 500 characters'),

  mobile: z
    .string()
    .regex(mobileRegex, 'Invalid mobile number format (10-15 digits)'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      const birthDate = new Date(date);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      const adjustedAge =
        monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())
          ? age - 1
          : age;
      return adjustedAge >= 3 && adjustedAge <= 18;
    }, 'Student must be between 3 and 18 years old'),

  fatherName: z
    .string()
    .min(1, 'Father name/Guardian is required')
    .max(100, 'Father name must be at most 100 characters'),

  motherName: z
    .string()
    .max(100, 'Mother name must be at most 100 characters')
    .optional()
    .or(z.literal('')),

  identificationMark: z
    .string()
    .max(200, 'Identification mark must be at most 200 characters')
    .optional()
    .or(z.literal('')),

  email: z
    .string()
    .email('Invalid email format')
    .max(100, 'Email must be at most 100 characters')
    .optional()
    .or(z.literal('')),

  aadhaarNumber: z
    .string()
    .regex(aadhaarRegex, 'Aadhaar must be exactly 12 digits')
    .optional()
    .or(z.literal('')),
});

export type StudentFormData = z.infer<typeof studentSchema>;

export const updateStudentSchema = z.object({
  firstName: z.string().min(1, 'First name is required').max(100),
  lastName: z.string().min(1, 'Last name is required').max(100),
  mobile: z.string().regex(mobileRegex, 'Invalid mobile number format'),
  status: z.enum(['Active', 'Inactive', 'Graduated', 'Transferred']),
});

export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;
