import apiClient from './client';
import {
  Student,
  CreateStudentRequest,
  UpdateStudentRequest,
  StudentSearchParams,
  PagedStudentResponse,
  StudentStatistics,
  StudentStatus,
} from '@/types/student';

export const studentApi = {
  // Create student
  createStudent: async (data: CreateStudentRequest): Promise<Student> => {
    const response = await apiClient.post<Student>('/api/v1/students', data);
    return response.data;
  },

  // Get student by ID
  getStudentById: async (studentId: string): Promise<Student> => {
    const response = await apiClient.get<Student>(`/api/v1/students/${studentId}`);
    return response.data;
  },

  // Update student
  updateStudent: async (
    studentId: string,
    data: UpdateStudentRequest
  ): Promise<Student> => {
    const response = await apiClient.put<Student>(`/api/v1/students/${studentId}`, data);
    return response.data;
  },

  // Delete student
  deleteStudent: async (studentId: string): Promise<void> => {
    await apiClient.delete(`/api/v1/students/${studentId}`);
  },

  // Search students
  searchStudents: async (
    params: StudentSearchParams = {}
  ): Promise<PagedStudentResponse> => {
    const response = await apiClient.get<PagedStudentResponse>('/api/v1/students', {
      params: {
        ...params,
        page: params.page ?? 0,
        size: params.size ?? 20,
        sort: params.sort ?? 'createdAt,desc',
      },
    });
    return response.data;
  },

  // Update student status
  updateStatus: async (
    studentId: string,
    status: StudentStatus
  ): Promise<Student> => {
    const response = await apiClient.patch<Student>(
      `/api/v1/students/${studentId}/status`,
      { status }
    );
    return response.data;
  },

  // Get statistics
  getStatistics: async (): Promise<StudentStatistics> => {
    const response = await apiClient.get<StudentStatistics>('/api/v1/students/statistics');
    return response.data;
  },
};
