import { z } from 'zod';

export const configSchema = z.object({
  category: z.enum(['General', 'Academic', 'Financial', 'System'], {
    message: 'Invalid category',
  }),

  configKey: z
    .string()
    .min(1, 'Config key is required')
    .max(100, 'Config key must be at most 100 characters'),

  configValue: z
    .string()
    .min(1, 'Config value is required'),

  description: z
    .string()
    .max(500, 'Description must be at most 500 characters')
    .optional()
    .or(z.literal('')),
});

export type ConfigFormData = z.infer<typeof configSchema>;
