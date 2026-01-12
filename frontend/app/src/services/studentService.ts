/**
 * Student Service - API Integration
 * School Management System - Phase 1
 */

import apiClient from './api';
import type {
  Student,
  StudentCreateDto,
  StudentUpdateDto,
  StudentListResponse,
  StudentStatistics,
  StudentFilters,
  PhoneValidationRequest,
  PhoneValidationResponse,
} from '../types';

/**
 * Student Service
 * Encapsulates all Student API operations
 */
export const studentService = {
  /**
   * Get all students with optional filters
   * GET /api/v1/students
   */
  getAll: async (filters?: StudentFilters): Promise<StudentListResponse> => {
    const params: any = {};

    if (filters?.search) {
      params.search = filters.search;
    }

    if (filters?.status && filters.status !== 'ALL') {
      params.status = filters.status;
    }

    if (filters?.page) {
      params.page = filters.page;
    }

    if (filters?.size) {
      params.size = filters.size;
    }

    const response = await apiClient.get<StudentListResponse>('/api/v1/students', {
      params,
    });

    return response.data;
  },

  /**
   * Get student by ID
   * GET /api/v1/students/{id}
   */
  getById: async (id: string): Promise<Student> => {
    const response = await apiClient.get<Student>(`/api/v1/students/${id}`);
    return response.data;
  },

  /**
   * Create a new student
   * POST /api/v1/students
   */
  create: async (student: StudentCreateDto): Promise<Student> => {
    const response = await apiClient.post<Student>('/api/v1/students', student);
    return response.data;
  },

  /**
   * Update an existing student (only allowed fields)
   * PATCH /api/v1/students/{id}
   */
  update: async (id: string, updates: StudentUpdateDto): Promise<Student> => {
    const response = await apiClient.patch<Student>(
      `/api/v1/students/${id}`,
      updates
    );
    return response.data;
  },

  /**
   * Delete a student
   * DELETE /api/v1/students/{id}
   */
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  /**
   * Validate phone number uniqueness
   * POST /api/v1/students/validate-phone
   */
  validatePhone: async (
    phone: string,
    excludeId?: string
  ): Promise<boolean> => {
    const request: PhoneValidationRequest = { phone, excludeId };
    const response = await apiClient.post<PhoneValidationResponse>(
      '/api/v1/students/validate-phone',
      request
    );
    return response.data.isUnique;
  },

  /**
   * Get student statistics for dashboard
   * GET /api/v1/students/statistics
   */
  getStatistics: async (): Promise<StudentStatistics> => {
    const response = await apiClient.get<StudentStatistics>(
      '/api/v1/students/statistics'
    );
    return response.data;
  },

  /**
   * Search students by query
   * GET /api/v1/students/search
   */
  search: async (query: string): Promise<Student[]> => {
    const response = await apiClient.get<Student[]>('/api/v1/students/search', {
      params: { q: query },
    });
    return response.data;
  },
};

export default studentService;
