import { describe, it, expect } from 'vitest';
import { studentCreateSchema, studentUpdateSchema } from './studentSchema';

describe('studentCreateSchema', () => {
  describe('firstName validation', () => {
    it('should accept valid first names', () => {
      const validData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
        email: 'john@example.com',
      };
      const result = studentCreateSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject first names shorter than 2 characters', () => {
      const invalidData = {
        firstName: 'J',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toContain('at least 2 characters');
      }
    });

    it('should reject first names longer than 50 characters', () => {
      const invalidData = {
        firstName: 'A'.repeat(51),
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toContain('not exceed 50 characters');
      }
    });

    it('should reject names with special characters or numbers', () => {
      const invalidData = {
        firstName: 'John123',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toContain('only letters and spaces');
      }
    });
  });

  describe('age validation', () => {
    it('should accept students aged 3-18', () => {
      const today = new Date();
      const validAge = new Date(today.getFullYear() - 10, today.getMonth(), today.getDate());

      const validData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: validAge,
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject students younger than 3', () => {
      const today = new Date();
      const tooYoung = new Date(today.getFullYear() - 2, today.getMonth(), today.getDate());

      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: tooYoung,
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toContain('between 3 and 18 years old');
      }
    });

    it('should reject students older than 18', () => {
      const today = new Date();
      const tooOld = new Date(today.getFullYear() - 19, today.getMonth(), today.getDate());

      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: tooOld,
        mobile: '1234567890',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toContain('between 3 and 18 years old');
      }
    });
  });

  describe('mobile validation', () => {
    it('should accept valid 10-digit mobile numbers', () => {
      const validData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '9876543210',
      };
      const result = studentCreateSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject mobile numbers with less than 10 digits', () => {
      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '987654321',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject mobile numbers with more than 10 digits', () => {
      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '98765432101',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject mobile numbers with non-numeric characters', () => {
      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '98765abc10',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('aadhaarNumber validation', () => {
    it('should accept valid 12-digit Aadhaar numbers', () => {
      const validData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
        aadhaarNumber: '123456789012',
      };
      const result = studentCreateSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should accept empty Aadhaar numbers', () => {
      const validData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
        aadhaarNumber: '',
      };
      const result = studentCreateSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject Aadhaar numbers with less than 12 digits', () => {
      const invalidData = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: new Date('2015-01-01'),
        mobile: '1234567890',
        aadhaarNumber: '12345678901',
      };
      const result = studentCreateSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });
});

describe('studentUpdateSchema', () => {
  it('should accept valid update data', () => {
    const validData = {
      firstName: 'John',
      lastName: 'Doe',
      mobile: '1234567890',
      email: 'john@example.com',
      status: 'ACTIVE' as const,
    };
    const result = studentUpdateSchema.safeParse(validData);
    expect(result.success).toBe(true);
  });

  it('should enforce 50 character limit on names', () => {
    const invalidData = {
      firstName: 'A'.repeat(51),
      lastName: 'Doe',
      mobile: '1234567890',
      status: 'ACTIVE' as const,
    };
    const result = studentUpdateSchema.safeParse(invalidData);
    expect(result.success).toBe(false);
  });

  it('should only accept ACTIVE or INACTIVE status', () => {
    const invalidData = {
      firstName: 'John',
      lastName: 'Doe',
      mobile: '1234567890',
      status: 'PENDING',
    };
    const result = studentUpdateSchema.safeParse(invalidData);
    expect(result.success).toBe(false);
  });
});
