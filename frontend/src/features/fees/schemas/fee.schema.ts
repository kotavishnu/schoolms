/**
 * Zod validation schemas for Fee Management Module
 */
import { z } from 'zod';

/**
 * Fee type schema
 */
export const feeTypeSchema = z.enum([
  'TUITION',
  'LIBRARY',
  'COMPUTER',
  'SPORTS',
  'TRANSPORT',
  'LAB',
  'OTHER',
]);

/**
 * Fee frequency schema
 */
export const feeFrequencySchema = z.enum(['MONTHLY', 'QUARTERLY', 'ANNUAL', 'ONE_TIME']);

/**
 * Fee component schema
 */
export const feeComponentSchema = z.object({
  feeType: feeTypeSchema,
  feeName: z
    .string()
    .min(2, 'Fee name must be at least 2 characters')
    .max(100, 'Fee name must not exceed 100 characters'),
  amount: z
    .number()
    .positive('Amount must be positive')
    .max(1000000, 'Amount must not exceed 1,000,000')
    .refine((val) => Number(val.toFixed(2)) === val, {
      message: 'Amount must have at most 2 decimal places',
    }),
  description: z.string().max(500, 'Description must not exceed 500 characters').optional(),
});

/**
 * Due date configuration schema
 */
export const dueDateConfigSchema = z.object({
  dueDay: z
    .number()
    .int('Due day must be an integer')
    .min(1, 'Due day must be at least 1')
    .max(31, 'Due day must not exceed 31'),
  gracePeriodDays: z
    .number()
    .int('Grace period must be an integer')
    .min(0, 'Grace period cannot be negative')
    .max(30, 'Grace period must not exceed 30 days'),
  lateFeeAmount: z
    .number()
    .nonnegative('Late fee amount cannot be negative')
    .max(10000, 'Late fee amount must not exceed 10,000')
    .refine((val) => Number(val.toFixed(2)) === val, {
      message: 'Late fee amount must have at most 2 decimal places',
    }),
  lateFeePercentage: z
    .number()
    .nonnegative('Late fee percentage cannot be negative')
    .max(100, 'Late fee percentage must not exceed 100%')
    .optional(),
});

/**
 * Fee structure form validation schema
 */
export const feeStructureFormSchema = z
  .object({
    structureName: z
      .string()
      .min(3, 'Structure name must be at least 3 characters')
      .max(200, 'Structure name must not exceed 200 characters'),
    academicYearCode: z
      .string()
      .regex(/^\d{4}-\d{4}$/, 'Academic year must be in format YYYY-YYYY (e.g., 2025-2026)'),
    frequency: feeFrequencySchema,
    applicableClasses: z
      .array(z.string())
      .min(1, 'At least one class must be selected')
      .max(50, 'Cannot select more than 50 classes'),
    effectiveFrom: z.string().refine((date) => !isNaN(Date.parse(date)), {
      message: 'Effective from date must be a valid date',
    }),
    effectiveTo: z
      .string()
      .refine((date) => !date || !isNaN(Date.parse(date)), {
        message: 'Effective to date must be a valid date',
      })
      .optional(),
    description: z.string().max(1000, 'Description must not exceed 1000 characters').optional(),
    components: z
      .array(feeComponentSchema)
      .min(1, 'At least one fee component is required')
      .max(20, 'Cannot add more than 20 fee components'),
    dueDateConfig: dueDateConfigSchema,
  })
  .refine(
    (data) => {
      // Validate that effectiveTo is after effectiveFrom if both are provided
      if (data.effectiveTo) {
        const from = new Date(data.effectiveFrom);
        const to = new Date(data.effectiveTo);
        return to > from;
      }
      return true;
    },
    {
      message: 'Effective to date must be after effective from date',
      path: ['effectiveTo'],
    }
  )
  .refine(
    (data) => {
      // Validate that late fee percentage is provided if late fee amount is 0
      if (data.dueDateConfig.lateFeeAmount === 0 && !data.dueDateConfig.lateFeePercentage) {
        return false;
      }
      return true;
    },
    {
      message: 'Either late fee amount or late fee percentage must be provided',
      path: ['dueDateConfig', 'lateFeeAmount'],
    }
  );

/**
 * Type inference from schema
 */
export type FeeStructureFormInput = z.infer<typeof feeStructureFormSchema>;

/**
 * Discount schema
 */
export const discountSchema = z.object({
  discountType: z.enum(['PERCENTAGE', 'FIXED']),
  discountValue: z
    .number()
    .positive('Discount value must be positive')
    .max(1000000, 'Discount value must not exceed 1,000,000'),
  reason: z
    .string()
    .min(5, 'Discount reason must be at least 5 characters')
    .max(500, 'Discount reason must not exceed 500 characters'),
}).refine(
  (data) => {
    if (data.discountType === 'PERCENTAGE') {
      return data.discountValue <= 100;
    }
    return true;
  },
  {
    message: 'Percentage discount must not exceed 100%',
    path: ['discountValue'],
  }
);

/**
 * Student fee assignment schema
 */
export const studentFeeAssignmentSchema = z.object({
  studentId: z.number().int().positive('Student ID must be a positive integer'),
  feeStructureId: z.number().int().positive('Fee structure ID must be a positive integer'),
  customAmount: z
    .number()
    .positive('Custom amount must be positive')
    .max(1000000, 'Custom amount must not exceed 1,000,000')
    .refine((val) => Number(val.toFixed(2)) === val, {
      message: 'Custom amount must have at most 2 decimal places',
    })
    .optional(),
  discount: discountSchema.optional(),
  effectiveFrom: z.string().refine((date) => !isNaN(Date.parse(date)), {
    message: 'Effective from date must be a valid date',
  }),
  effectiveTo: z
    .string()
    .refine((date) => !date || !isNaN(Date.parse(date)), {
      message: 'Effective to date must be a valid date',
    })
    .optional(),
});

/**
 * Type inference from schema
 */
export type StudentFeeAssignmentInput = z.infer<typeof studentFeeAssignmentSchema>;

/**
 * Fee filters schema
 */
export const feeFiltersSchema = z.object({
  search: z.string().optional(),
  academicYear: z.string().optional(),
  feeType: feeTypeSchema.optional(),
  frequency: feeFrequencySchema.optional(),
  isActive: z.boolean().optional(),
  applicableClass: z.string().optional(),
  page: z.number().int().nonnegative().optional(),
  limit: z.number().int().positive().max(100).optional(),
});

/**
 * Helper function to get default fee component
 */
export const getDefaultFeeComponent = () => ({
  feeType: 'TUITION' as const,
  feeName: '',
  amount: 0,
  description: '',
});

/**
 * Helper function to get default due date config
 */
export const getDefaultDueDateConfig = () => ({
  dueDay: 5,
  gracePeriodDays: 7,
  lateFeeAmount: 0,
  lateFeePercentage: 0,
});

/**
 * Helper function to get default fee structure form data
 */
export const getDefaultFeeStructureForm = (): Partial<FeeStructureFormInput> => ({
  structureName: '',
  academicYearCode: '',
  frequency: 'MONTHLY',
  applicableClasses: [],
  effectiveFrom: new Date().toISOString().split('T')[0],
  effectiveTo: undefined,
  description: '',
  components: [getDefaultFeeComponent()],
  dueDateConfig: getDefaultDueDateConfig(),
});

/**
 * Validation helper functions
 */
export const validateFeeStructureForm = (data: unknown) => {
  return feeStructureFormSchema.safeParse(data);
};

export const validateStudentFeeAssignment = (data: unknown) => {
  return studentFeeAssignmentSchema.safeParse(data);
};

export const validateFeeFilters = (data: unknown) => {
  return feeFiltersSchema.safeParse(data);
};
