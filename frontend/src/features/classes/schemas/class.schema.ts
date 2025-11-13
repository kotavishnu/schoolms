/**
 * Zod validation schemas for Class Management Module
 */

import { z } from 'zod';

/**
 * Class name validation schema
 */
const classNameEnum = z.enum([
  'CLASS_1',
  'CLASS_2',
  'CLASS_3',
  'CLASS_4',
  'CLASS_5',
  'CLASS_6',
  'CLASS_7',
  'CLASS_8',
  'CLASS_9',
  'CLASS_10',
]);

/**
 * Section validation schema
 */
const sectionEnum = z.enum(['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']);

/**
 * Class form validation schema
 *
 * Business Rules:
 * - Class name is required (CLASS_1 through CLASS_10)
 * - Section is required (A through J)
 * - Academic year ID is required
 * - Max capacity must be between 1 and 100 students (BR-3)
 * - Room number is optional
 * - Class teacher is optional
 */
export const classFormSchema = z.object({
  className: classNameEnum.describe('Grade level for the class'),

  section: sectionEnum.describe('Section identifier (A-J)'),

  academicYearId: z
    .number()
    .int()
    .positive('Academic year is required'),

  maxCapacity: z
    .number()
    .int()
    .min(1, 'Class must have at least 1 student capacity')
    .max(100, 'Class capacity cannot exceed 100 students'), // BR-3: Class capacity limits

  classTeacherId: z
    .number()
    .int()
    .positive()
    .optional()
    .nullable(),

  roomNumber: z
    .string()
    .max(20, 'Room number must be at most 20 characters')
    .optional()
    .nullable(),

  isActive: z
    .boolean()
    .optional()
    .default(true),
});

/**
 * Academic year form validation schema
 *
 * Business Rules:
 * - Year code format: YYYY-YYYY (e.g., "2024-2025")
 * - Start date must be before end date
 * - End date must be after start date
 */
export const academicYearFormSchema = z.object({
  yearCode: z
    .string()
    .regex(/^\d{4}-\d{4}$/, 'Year code must be in format YYYY-YYYY (e.g., 2024-2025)')
    .refine(
      (code) => {
        const [startYear, endYear] = code.split('-').map(Number);
        return endYear === startYear + 1;
      },
      { message: 'End year must be exactly one year after start year' }
    ),

  startDate: z
    .string()
    .min(1, 'Start date is required')
    .refine((date) => !isNaN(Date.parse(date)), {
      message: 'Invalid date format',
    }),

  endDate: z
    .string()
    .min(1, 'End date is required')
    .refine((date) => !isNaN(Date.parse(date)), {
      message: 'Invalid date format',
    }),

  isCurrent: z
    .boolean()
    .optional()
    .default(false),
}).refine(
  (data) => {
    const start = new Date(data.startDate);
    const end = new Date(data.endDate);
    return end > start;
  },
  {
    message: 'End date must be after start date',
    path: ['endDate'],
  }
);

/**
 * Student enrollment validation schema
 *
 * Business Rules:
 * - Student ID is required
 * - Class ID is required
 * - Enrollment date is required and cannot be in the future
 */
export const enrollStudentSchema = z.object({
  studentId: z
    .number()
    .int()
    .positive('Student is required'),

  classId: z
    .number()
    .int()
    .positive('Class is required'),

  enrollmentDate: z
    .string()
    .min(1, 'Enrollment date is required')
    .refine((date) => !isNaN(Date.parse(date)), {
      message: 'Invalid date format',
    })
    .refine(
      (date) => {
        const enrollDate = new Date(date);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Reset time to start of day
        return enrollDate <= today;
      },
      { message: 'Enrollment date cannot be in the future' }
    ),
});

/**
 * Remove student from class validation schema
 *
 * Business Rules:
 * - Enrollment ID is required
 * - Withdrawal date is required
 * - Withdrawal reason is required and must be meaningful
 */
export const removeStudentSchema = z.object({
  enrollmentId: z
    .number()
    .int()
    .positive('Enrollment is required'),

  withdrawalDate: z
    .string()
    .min(1, 'Withdrawal date is required')
    .refine((date) => !isNaN(Date.parse(date)), {
      message: 'Invalid date format',
    })
    .refine(
      (date) => {
        const withdrawDate = new Date(date);
        const today = new Date();
        today.setHours(23, 59, 59, 999); // End of day
        return withdrawDate <= today;
      },
      { message: 'Withdrawal date cannot be in the future' }
    ),

  withdrawalReason: z
    .string()
    .min(5, 'Withdrawal reason must be at least 5 characters')
    .max(500, 'Withdrawal reason must be at most 500 characters'),
});

/**
 * Class filters validation schema (for search/filter)
 */
export const classFiltersSchema = z.object({
  search: z.string().optional(),
  academicYearId: z.number().int().positive().optional(),
  className: classNameEnum.optional(),
  section: sectionEnum.optional(),
  isActive: z.boolean().optional(),
  page: z.number().int().min(0).optional().default(0),
  limit: z.number().int().min(1).max(100).optional().default(20),
});

/**
 * Type inference from schemas
 */
export type ClassFormData = z.infer<typeof classFormSchema>;
export type AcademicYearFormData = z.infer<typeof academicYearFormSchema>;
export type EnrollStudentData = z.infer<typeof enrollStudentSchema>;
export type RemoveStudentData = z.infer<typeof removeStudentSchema>;
export type ClassFiltersData = z.infer<typeof classFiltersSchema>;

/**
 * Export all schemas
 */
export const classSchemas = {
  classForm: classFormSchema,
  academicYearForm: academicYearFormSchema,
  enrollStudent: enrollStudentSchema,
  removeStudent: removeStudentSchema,
  classFilters: classFiltersSchema,
};

export default classSchemas;
