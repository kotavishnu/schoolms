import { describe, it, expect } from 'vitest';
import { configurationSchema } from './configSchema';

describe('configurationSchema', () => {
  describe('category validation', () => {
    it('should accept valid categories', () => {
      const categories = ['GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'];

      categories.forEach((category) => {
        const validData = {
          category,
          key: 'TEST_KEY',
          value: 'test value',
          dataType: 'STRING' as const,
        };
        const result = configurationSchema.safeParse(validData);
        expect(result.success).toBe(true);
      });
    });

    it('should reject invalid categories', () => {
      const invalidData = {
        category: 'INVALID',
        key: 'TEST_KEY',
        value: 'test value',
        dataType: 'STRING',
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('key validation', () => {
    it('should accept keys with uppercase letters and underscores', () => {
      const validKeys = ['TEST_KEY', 'SCHOOL_NAME', 'API_KEY', '_PRIVATE_KEY', 'KEY123'];

      validKeys.forEach((key) => {
        const validData = {
          category: 'GENERAL' as const,
          key,
          value: 'test value',
          dataType: 'STRING' as const,
        };
        const result = configurationSchema.safeParse(validData);
        expect(result.success).toBe(true);
      });
    });

    it('should reject keys with lowercase letters', () => {
      const invalidData = {
        category: 'GENERAL' as const,
        key: 'test_key',
        value: 'test value',
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject keys with special characters', () => {
      const invalidData = {
        category: 'GENERAL' as const,
        key: 'TEST-KEY',
        value: 'test value',
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject keys starting with numbers', () => {
      const invalidData = {
        category: 'GENERAL' as const,
        key: '123_KEY',
        value: 'test value',
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('dataType validation', () => {
    it('should accept valid data types', () => {
      const dataTypes = ['STRING', 'NUMBER', 'BOOLEAN', 'JSON'];

      dataTypes.forEach((dataType) => {
        const validData = {
          category: 'GENERAL' as const,
          key: 'TEST_KEY',
          value: 'test value',
          dataType,
        };
        const result = configurationSchema.safeParse(validData);
        expect(result.success).toBe(true);
      });
    });

    it('should reject invalid data types', () => {
      const invalidData = {
        category: 'GENERAL',
        key: 'TEST_KEY',
        value: 'test value',
        dataType: 'ARRAY',
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('value validation', () => {
    it('should accept values up to 1000 characters', () => {
      const validData = {
        category: 'GENERAL' as const,
        key: 'TEST_KEY',
        value: 'a'.repeat(1000),
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject values exceeding 1000 characters', () => {
      const invalidData = {
        category: 'GENERAL' as const,
        key: 'TEST_KEY',
        value: 'a'.repeat(1001),
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject empty values', () => {
      const invalidData = {
        category: 'GENERAL' as const,
        key: 'TEST_KEY',
        value: '',
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('isEncrypted field', () => {
    it('should default to false if not provided', () => {
      const validData = {
        category: 'GENERAL' as const,
        key: 'TEST_KEY',
        value: 'test value',
        dataType: 'STRING' as const,
      };
      const result = configurationSchema.safeParse(validData);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.isEncrypted).toBe(false);
      }
    });

    it('should accept explicit boolean values', () => {
      const validData = {
        category: 'GENERAL' as const,
        key: 'TEST_KEY',
        value: 'test value',
        dataType: 'STRING' as const,
        isEncrypted: true,
      };
      const result = configurationSchema.safeParse(validData);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.isEncrypted).toBe(true);
      }
    });
  });
});
