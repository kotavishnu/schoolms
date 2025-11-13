import { z } from 'zod';

/**
 * Zod validation schemas for Payment & Receipts Module
 */

/**
 * Payment method enum schema
 */
export const paymentMethodSchema = z.enum(['Cash', 'Card', 'Bank Transfer', 'UPI', 'Cheque']);

/**
 * Fee item schema for payment recording
 */
export const feeItemSchema = z.object({
  feeId: z.number().int().positive('Fee ID must be a positive integer'),
  amountPaid: z
    .number()
    .positive('Amount must be greater than zero')
    .max(999999.99, 'Amount cannot exceed 999,999.99'),
});

/**
 * Payment form validation schema
 */
export const paymentFormSchema = z
  .object({
    studentId: z.number().int().positive('Please select a student'),
    feeItems: z
      .array(feeItemSchema)
      .min(1, 'Please select at least one fee item to pay')
      .refine(
        (items) => {
          // Ensure no duplicate fee IDs
          const feeIds = items.map((item) => item.feeId);
          return new Set(feeIds).size === feeIds.length;
        },
        { message: 'Duplicate fee items are not allowed' }
      ),
    paymentDate: z
      .string()
      .min(1, 'Payment date is required')
      .refine(
        (date) => {
          const paymentDate = new Date(date);
          const today = new Date();
          today.setHours(23, 59, 59, 999);
          return paymentDate <= today;
        },
        { message: 'Payment date cannot be in the future' }
      ),
    paymentMethod: paymentMethodSchema,
    transactionReference: z
      .string()
      .max(100, 'Transaction reference cannot exceed 100 characters')
      .optional()
      .or(z.literal('')),
    notes: z
      .string()
      .max(500, 'Notes cannot exceed 500 characters')
      .optional()
      .or(z.literal('')),
  })
  .refine(
    (data) => {
      // Transaction reference required for non-cash payments
      if (data.paymentMethod !== 'Cash' && !data.transactionReference) {
        return false;
      }
      return true;
    },
    {
      message: 'Transaction reference is required for non-cash payments',
      path: ['transactionReference'],
    }
  )
  .refine(
    (data) => {
      // Cheque number required for cheque payments
      if (data.paymentMethod === 'Cheque' && !data.transactionReference) {
        return false;
      }
      return true;
    },
    {
      message: 'Cheque number is required for cheque payments',
      path: ['transactionReference'],
    }
  );

/**
 * Refund form validation schema
 */
export const refundFormSchema = z
  .object({
    paymentId: z.number().int().positive('Payment ID is required'),
    refundAmount: z
      .number()
      .positive('Refund amount must be greater than zero')
      .max(999999.99, 'Refund amount cannot exceed 999,999.99'),
    reason: z
      .string()
      .min(10, 'Reason must be at least 10 characters')
      .max(500, 'Reason cannot exceed 500 characters'),
    isFullRefund: z.boolean(),
  })
  .refine(
    (data) => {
      // If full refund, amount validation will be done on server side
      return true;
    },
    {
      message: 'Invalid refund amount',
      path: ['refundAmount'],
    }
  );

/**
 * Payment filters validation schema
 */
export const paymentFiltersSchema = z.object({
  search: z.string().optional(),
  paymentMethod: paymentMethodSchema.optional(),
  status: z.enum(['Completed', 'Pending', 'Failed', 'Refunded', 'Partially Refunded']).optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  studentId: z.number().int().positive().optional(),
  page: z.number().int().positive().optional(),
  limit: z.number().int().positive().max(100).optional(),
});

/**
 * Export type inference from schemas
 */
export type PaymentFormValues = z.infer<typeof paymentFormSchema>;
export type RefundFormValues = z.infer<typeof refundFormSchema>;
export type PaymentFiltersValues = z.infer<typeof paymentFiltersSchema>;
