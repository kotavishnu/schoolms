import apiClient from './api';
import type {
  Student,
  StudentCreateRequest,
  StudentUpdateRequest,
  StudentListResponse,
  StudentBackendDTO,
} from '@/types/student';
import type { SearchParams } from '@/types/api';

// Field mapping helpers (D-007: Critical field mapping layer)
const mapBackendToFrontend = (backend: StudentBackendDTO): Student => ({
  id: backend.studentId, // Backend studentId → Frontend id
  firstName: backend.firstName,
  lastName: backend.lastName,
  dateOfBirth: backend.dateOfBirth,
  age: backend.age,
  adhaarNumber: backend.aadhaarNumber, // Backend aadhaarNumber → Frontend adhaarNumber
  identificationMarks: backend.identificationMarks,
  address: backend.address,
  guardianName: backend.guardianName,
  motherName: backend.motherName,
  phone: backend.mobile, // Backend mobile → Frontend phone
  email: backend.email,
  status: backend.status,
  createdAt: backend.createdAt,
  updatedAt: backend.updatedAt,
});

const mapFrontendToBackend = (
  frontend: StudentCreateRequest | StudentUpdateRequest
): any => {
  if ('dateOfBirth' in frontend) {
    // Create request
    return {
      firstName: frontend.firstName,
      lastName: frontend.lastName,
      dateOfBirth: frontend.dateOfBirth,
      aadhaarNumber: frontend.adhaarNumber, // Frontend adhaarNumber → Backend aadhaarNumber
      identificationMarks: frontend.identificationMarks,
      address: frontend.address,
      guardianName: frontend.guardianName,
      motherName: frontend.motherName,
      mobile: frontend.phone, // Frontend phone → Backend mobile
      email: frontend.email,
      status: frontend.status,
    };
  } else {
    // Update request
    return {
      firstName: frontend.firstName,
      lastName: frontend.lastName,
      mobile: frontend.phone, // Frontend phone → Backend mobile
      status: frontend.status,
    };
  }
};

export const studentService = {
  /**
   * Get all students with optional filters
   */
  async getAll(params?: SearchParams): Promise<StudentListResponse> {
    const response = await apiClient.get<any>('/api/v1/students', {
      params,
    });

    // Backend returns paginated response: { content: [], totalElements: n, ... }
    // Frontend expects: { students: [], totalCount: n, activeCount: n }
    const backendData = response.data;
    const students = (backendData.content || []).map((s: any) =>
      mapBackendToFrontend(s as StudentBackendDTO)
    );

    const activeCount = students.filter((s: Student) => s.status === 'ACTIVE').length;

    return {
      students,
      totalCount: backendData.totalElements || 0,
      activeCount,
    };
  },

  /**
   * Get student by ID
   */
  async getById(id: string): Promise<Student> {
    const response = await apiClient.get<StudentBackendDTO>(`/api/v1/students/${id}`);
    return mapBackendToFrontend(response.data);
  },

  /**
   * Create new student
   */
  async create(data: StudentCreateRequest): Promise<Student> {
    const backendRequest = mapFrontendToBackend(data);
    const response = await apiClient.post<StudentBackendDTO>('/api/v1/students', backendRequest);
    return mapBackendToFrontend(response.data);
  },

  /**
   * Update student (only allowed fields: firstName, lastName, phone, status)
   */
  async update(id: string, data: StudentUpdateRequest): Promise<Student> {
    const backendRequest = mapFrontendToBackend(data);
    const response = await apiClient.put<StudentBackendDTO>(`/api/v1/students/${id}`, backendRequest);
    return mapBackendToFrontend(response.data);
  },

  /**
   * Delete student
   */
  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  /**
   * Validate phone uniqueness
   */
  async validatePhone(phone: string, excludeId?: string): Promise<boolean> {
    try {
      const response = await apiClient.post<{ isUnique: boolean }>('/api/v1/students/validate-phone', {
        mobile: phone, // Backend expects 'mobile'
        excludeId,
      });
      return response.data.isUnique;
    } catch (error) {
      // If validation endpoint doesn't exist, assume unique
      console.warn('Phone validation endpoint not available:', error);
      return true;
    }
  },

  /**
   * Get student statistics
   */
  async getStatistics(): Promise<{ totalCount: number; activeCount: number }> {
    try {
      const response = await apiClient.get<{ totalCount: number; activeCount: number }>(
        '/api/v1/students/statistics'
      );
      return response.data;
    } catch (error) {
      // Fallback: get all students and calculate
      const all = await this.getAll();
      return {
        totalCount: all.totalCount,
        activeCount: all.activeCount,
      };
    }
  },
};
