import apiClient from '@/lib/api-client';
import {
  Student,
  CreateStudentRequest,
  UpdateStudentRequest,
  StudentSearchParams,
  PageResponse,
  StudentListItem,
} from '@/types';

export const studentService = {
  /**
   * Create a new student
   */
  async createStudent(data: CreateStudentRequest): Promise<Student> {
    const response = await apiClient.post<Student>('/students', data);
    return response.data;
  },

  /**
   * Get student by key
   */
  async getStudent(studentKey: string): Promise<Student> {
    const response = await apiClient.get<Student>(`/students/${studentKey}`);
    return response.data;
  },

  /**
   * Search students with pagination
   */
  async searchStudents(params: StudentSearchParams = {}): Promise<PageResponse<StudentListItem>> {
    const queryParams: Record<string, any> = {
      page: params.page ?? 0,
      size: params.size ?? 20,
    };

    if (params.lastName) {
      queryParams.lastName = params.lastName;
    }

    if (params.guardianName) {
      queryParams.guardianName = params.guardianName;
    }

    if (params.status) {
      queryParams.status = params.status;
    }

    if (params.sort) {
      queryParams.sort = params.sort;
    }

    const response = await apiClient.get<PageResponse<StudentListItem>>('/students', {
      params: queryParams,
    });

    return response.data;
  },

  /**
   * Update student
   */
  async updateStudent(studentKey: string, data: UpdateStudentRequest): Promise<Student> {
    const response = await apiClient.put<Student>(`/students/${studentKey}`, data);
    return response.data;
  },

  /**
   * Delete student
   */
  async deleteStudent(studentKey: string): Promise<void> {
    await apiClient.delete(`/students/${studentKey}`);
  },

  /**
   * Get all students (without pagination) - for exports or bulk operations
   */
  async getAllStudents(): Promise<Student[]> {
    const response = await apiClient.get<PageResponse<StudentListItem>>('/students', {
      params: { size: 1000 },
    });
    return response.data.content as any;
  },
};
