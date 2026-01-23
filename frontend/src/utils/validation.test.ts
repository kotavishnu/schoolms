import { describe, it, expect } from 'vitest';
import {
  calculateAge,
  studentCreateSchema,
  studentUpdateSchema,
  configurationSchema,
} from './validation';

describe('Validation Schemas (QA-FE-004)', () => {
  describe('calculateAge', () => {
    it('calculates age correctly', () => {
      // For a person born on 2015-01-15
      const dob = '2015-01-15';
      const age = calculateAge(dob);

      // Age should be between 8 and 10 depending on current date
      expect(age).toBeGreaterThanOrEqual(8);
      expect(age).toBeLessThanOrEqual(11);
    });

    it('handles birthday not yet occurred this year', () => {
      const today = new Date();
      const futureMonth = new Date(today.getFullYear(), today.getMonth() + 2, 1);
      const dob = new Date(futureMonth.getFullYear() - 10, futureMonth.getMonth(), futureMonth.getDate())
        .toISOString()
        .split('T')[0];

      const age = calculateAge(dob);
      expect(age).toBeLessThanOrEqual(10);
    });
  });

  describe('studentCreateSchema - Age Validation (BR-STU-001)', () => {
    it('accepts age between 3 and 18 years', () => {
      const validStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15', // ~8 years old
        adhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(validStudent);
      expect(result.success).toBe(true);
    });

    it('rejects age less than 3 years', () => {
      const youngStudent = {
        firstName: 'Baby',
        lastName: 'Doe',
        dateOfBirth: new Date().toISOString().split('T')[0], // Born today
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'baby.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(youngStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        const errorMessages = result.error.issues.map(e => e.message).join(' ');
        expect(errorMessages).toContain('Age must be between 3 and 18 years');
      }
    });

    it('rejects age greater than 18 years', () => {
      const oldStudent = {
        firstName: 'Old',
        lastName: 'Doe',
        dateOfBirth: '1990-01-15', // ~34 years old
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'old.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(oldStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        const errorMessages = result.error.issues.map(e => e.message).join(' ');
        expect(errorMessages).toContain('Age must be between 3 and 18 years');
      }
    });
  });

  describe('studentCreateSchema - Phone Validation (BR-STU-006)', () => {
    it('accepts valid 10-digit phone', () => {
      const validStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(validStudent);
      expect(result.success).toBe(true);
    });

    it('rejects phone with less than 10 digits', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '987654321', // 9 digits
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Phone must be exactly 10 digits');
      }
    });

    it('rejects phone with more than 10 digits', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '98765432101', // 11 digits
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Phone must be exactly 10 digits');
      }
    });

    it('rejects phone with letters', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '987ABC4321',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Phone must be exactly 10 digits');
      }
    });
  });

  describe('studentCreateSchema - Aadhaar Validation (BR-STU-004)', () => {
    it('accepts valid 12-digit Aadhaar', () => {
      const validStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(validStudent);
      expect(result.success).toBe(true);
    });

    it('rejects Aadhaar with less than 12 digits', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '12345678901', // 11 digits
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Aadhaar number must be exactly 12 digits');
      }
    });

    it('rejects Aadhaar with more than 12 digits', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '1234567890123', // 13 digits
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Aadhaar number must be exactly 12 digits');
      }
    });

    it('rejects Aadhaar with letters', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '12345678901A',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Aadhaar number must be exactly 12 digits');
      }
    });
  });

  describe('studentCreateSchema - Name Validation (BR-STU-005)', () => {
    it('accepts names with letters and spaces', () => {
      const validStudent = {
        firstName: 'John Doe',
        lastName: 'Smith Jr',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(validStudent);
      expect(result.success).toBe(true);
    });

    it('rejects first name with numbers', () => {
      const invalidStudent = {
        firstName: 'John123',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('First name must contain only letters and spaces');
      }
    });

    it('rejects last name with special characters', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe@#$',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Last name must contain only letters and spaces');
      }
    });
  });

  describe('studentCreateSchema - Email Validation (BR-STU-003)', () => {
    it('accepts valid email', () => {
      const validStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(validStudent);
      expect(result.success).toBe(true);
    });

    it('rejects invalid email (no @)', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doeexample.com',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Email must be in valid format');
      }
    });

    it('rejects invalid email (no domain)', () => {
      const invalidStudent = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        address: '123 Main Street, City, State 12345',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@',
        status: 'ACTIVE' as const,
      };

      const result = studentCreateSchema.safeParse(invalidStudent);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Email must be in valid format');
      }
    });
  });

  describe('studentUpdateSchema - Editable Fields Only', () => {
    it('accepts only editable fields (firstName, lastName, phone, status)', () => {
      const validUpdate = {
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '9876543210',
        status: 'INACTIVE' as const,
      };

      const result = studentUpdateSchema.safeParse(validUpdate);
      expect(result.success).toBe(true);
    });

    it('does not require immutable fields (dateOfBirth, email, adhaarNumber)', () => {
      const updateWithoutImmutableFields = {
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '9876543210',
        status: 'ACTIVE' as const,
      };

      const result = studentUpdateSchema.safeParse(updateWithoutImmutableFields);
      expect(result.success).toBe(true);
    });
  });

  describe('configurationSchema - Key Validation', () => {
    it('accepts key with uppercase letters, numbers, and underscores', () => {
      const validConfig = {
        category: 'GENERAL' as const,
        key: 'SCHOOL_NAME_123',
        value: 'ABC School',
        description: 'School name configuration',
      };

      const result = configurationSchema.safeParse(validConfig);
      expect(result.success).toBe(true);
    });

    it('rejects key with lowercase letters', () => {
      const invalidConfig = {
        category: 'GENERAL' as const,
        key: 'school_name',
        value: 'ABC School',
        description: 'School name configuration',
      };

      const result = configurationSchema.safeParse(invalidConfig);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Key must contain only uppercase letters, numbers, and underscores');
      }
    });

    it('rejects key with special characters', () => {
      const invalidConfig = {
        category: 'GENERAL' as const,
        key: 'SCHOOL-NAME@',
        value: 'ABC School',
        description: 'School name configuration',
      };

      const result = configurationSchema.safeParse(invalidConfig);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues.map(e => e.message).join(' ')).toContain('Key must contain only uppercase letters, numbers, and underscores');
      }
    });
  });

  describe('configurationSchema - Category Validation', () => {
    it('accepts valid categories (GENERAL, ACADEMIC, FINANCE, SYSTEM)', () => {
      const categories = ['GENERAL', 'ACADEMIC', 'FINANCE', 'SYSTEM'] as const;

      categories.forEach((category) => {
        const validConfig = {
          category,
          key: 'TEST_KEY',
          value: 'Test Value',
        };

        const result = configurationSchema.safeParse(validConfig);
        expect(result.success).toBe(true);
      });
    });

    it('rejects invalid category', () => {
      const invalidConfig = {
        category: 'INVALID',
        key: 'TEST_KEY',
        value: 'Test Value',
      };

      const result = configurationSchema.safeParse(invalidConfig);
      expect(result.success).toBe(false);
    });
  });
});
