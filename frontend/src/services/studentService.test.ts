import { describe, it, expect, beforeEach, vi } from 'vitest';
import { studentService } from './studentService';
import apiClient from './api';
import type { StudentBackendDTO, StudentCreateRequest, StudentUpdateRequest } from '@/types/student';

// Mock the API client
vi.mock('./api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('studentService - Field Mapping (QA-FE-003)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Field Mapping: Backend to Frontend', () => {
    it('maps studentId to id correctly', async () => {
      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole on left cheek',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.get).mockResolvedValueOnce({ data: backendDTO });

      const result = await studentService.getById('STD-20260123-0001');

      // CRITICAL: Backend studentId → Frontend id
      expect(result.id).toBe('STD-20260123-0001');
      expect((result as any).studentId).toBeUndefined();
    });

    it('maps mobile to phone correctly', async () => {
      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.get).mockResolvedValueOnce({ data: backendDTO });

      const result = await studentService.getById('STD-20260123-0001');

      // CRITICAL: Backend mobile → Frontend phone
      expect(result.phone).toBe('9876543210');
      expect((result as any).mobile).toBeUndefined();
    });

    it('maps aadhaarNumber to adhaarNumber correctly', async () => {
      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.get).mockResolvedValueOnce({ data: backendDTO });

      const result = await studentService.getById('STD-20260123-0001');

      // CRITICAL: Backend aadhaarNumber → Frontend adhaarNumber
      expect(result.adhaarNumber).toBe('123456789012');
      expect((result as any).aadhaarNumber).toBeUndefined();
    });
  });

  describe('Field Mapping: Frontend to Backend (Create)', () => {
    it('maps phone to mobile in create request', async () => {
      const createRequest: StudentCreateRequest = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
      };

      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.post).mockResolvedValueOnce({ data: backendDTO });

      await studentService.create(createRequest);

      // Verify API was called with mobile (not phone)
      expect(apiClient.post).toHaveBeenCalledWith(
        '/api/v1/students',
        expect.objectContaining({
          mobile: '9876543210',
          aadhaarNumber: '123456789012',
        })
      );

      // Ensure phone is NOT sent to backend
      expect((vi.mocked(apiClient.post).mock.calls[0][1] as any).phone).toBeUndefined();
    });

    it('maps adhaarNumber to aadhaarNumber in create request', async () => {
      const createRequest: StudentCreateRequest = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        adhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        phone: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
      };

      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.post).mockResolvedValueOnce({ data: backendDTO });

      await studentService.create(createRequest);

      // Verify API was called with aadhaarNumber (not adhaarNumber)
      expect(apiClient.post).toHaveBeenCalledWith(
        '/api/v1/students',
        expect.objectContaining({
          aadhaarNumber: '123456789012',
        })
      );

      // Ensure adhaarNumber is NOT sent to backend
      expect((vi.mocked(apiClient.post).mock.calls[0][1] as any).adhaarNumber).toBeUndefined();
    });
  });

  describe('Field Mapping: Frontend to Backend (Update)', () => {
    it('maps phone to mobile in update request', async () => {
      const updateRequest: StudentUpdateRequest = {
        firstName: 'John',
        lastName: 'Doe',
        phone: '9876543210',
        status: 'ACTIVE',
      };

      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'ACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.put).mockResolvedValueOnce({ data: backendDTO });

      await studentService.update('STD-20260123-0001', updateRequest);

      // Verify API was called with mobile (not phone)
      expect(apiClient.put).toHaveBeenCalledWith(
        '/api/v1/students/STD-20260123-0001',
        expect.objectContaining({
          mobile: '9876543210',
        })
      );

      // Ensure phone is NOT sent to backend
      expect((vi.mocked(apiClient.put).mock.calls[0][1] as any).phone).toBeUndefined();
    });

    it('sends only editable fields in update request', async () => {
      const updateRequest: StudentUpdateRequest = {
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '9876543210',
        status: 'INACTIVE',
      };

      const backendDTO: StudentBackendDTO = {
        studentId: 'STD-20260123-0001',
        firstName: 'Jane',
        lastName: 'Smith',
        dateOfBirth: '2015-05-15',
        age: 8,
        aadhaarNumber: '123456789012',
        identificationMarks: 'Mole',
        address: '123 Main St',
        guardianName: 'Jane Doe',
        motherName: 'Jane Doe',
        mobile: '9876543210',
        email: 'john.doe@example.com',
        status: 'INACTIVE',
        createdAt: '2026-01-23T00:00:00Z',
        updatedAt: '2026-01-23T00:00:00Z',
      };

      vi.mocked(apiClient.put).mockResolvedValueOnce({ data: backendDTO });

      await studentService.update('STD-20260123-0001', updateRequest);

      const requestPayload = vi.mocked(apiClient.put).mock.calls[0][1];

      // Should ONLY contain editable fields
      expect(requestPayload).toEqual({
        firstName: 'Jane',
        lastName: 'Smith',
        mobile: '9876543210',
        status: 'INACTIVE',
      });

      // Immutable fields should NOT be present
      expect((requestPayload as any).dateOfBirth).toBeUndefined();
      expect((requestPayload as any).email).toBeUndefined();
      expect((requestPayload as any).aadhaarNumber).toBeUndefined();
    });
  });

  describe('Phone Validation', () => {
    it('sends mobile field (not phone) to validate-phone endpoint', async () => {
      vi.mocked(apiClient.post).mockResolvedValueOnce({ data: { isUnique: true } });

      const result = await studentService.validatePhone('9876543210');

      // Verify API was called with 'mobile' field
      expect(apiClient.post).toHaveBeenCalledWith(
        '/api/v1/students/validate-phone',
        expect.objectContaining({
          mobile: '9876543210',
        })
      );

      expect(result).toBe(true);
    });

    it('handles validation endpoint failure gracefully', async () => {
      vi.mocked(apiClient.post).mockRejectedValueOnce(new Error('Endpoint not found'));

      const result = await studentService.validatePhone('9876543210');

      // Should return true (assume unique) if endpoint fails
      expect(result).toBe(true);
    });
  });
});
