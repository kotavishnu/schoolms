import { z } from 'zod';

/**
 * Validation schema for configuration management
 */
export const configurationSchema = z.object({
  category: z.enum(['GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'], {
    errorMap: () => ({ message: 'Category must be GENERAL, ACADEMIC, FINANCIAL, or SYSTEM' }),
  }),

  key: z
    .string()
    .min(1, 'Key is required')
    .max(100, 'Key must not exceed 100 characters')
    .regex(
      /^[A-Z_][A-Z0-9_]*$/,
      'Key must start with uppercase letter or underscore, and contain only uppercase letters, digits, and underscores'
    ),

  value: z
    .string()
    .min(1, 'Value is required')
    .max(1000, 'Value must not exceed 1000 characters'),

  description: z
    .string()
    .max(500, 'Description must not exceed 500 characters')
    .optional()
    .or(z.literal('')),

  dataType: z.enum(['STRING', 'NUMBER', 'BOOLEAN', 'JSON'], {
    errorMap: () => ({ message: 'Data type must be STRING, NUMBER, BOOLEAN, or JSON' }),
  }),

  isEncrypted: z.boolean().default(false),
});

/**
 * Validation schema for updating configuration
 * (excludes category and key as they are path parameters)
 */
export const configurationUpdateSchema = z.object({
  value: z
    .string()
    .min(1, 'Value is required')
    .max(1000, 'Value must not exceed 1000 characters'),

  description: z
    .string()
    .max(500, 'Description must not exceed 500 characters')
    .optional()
    .or(z.literal('')),

  dataType: z.enum(['STRING', 'NUMBER', 'BOOLEAN', 'JSON'], {
    errorMap: () => ({ message: 'Data type must be STRING, NUMBER, BOOLEAN, or JSON' }),
  }),

  isEncrypted: z.boolean().default(false),
});

// Type exports
export type ConfigurationFormData = z.infer<typeof configurationSchema>;
export type ConfigurationUpdateFormData = z.infer<typeof configurationUpdateSchema>;
