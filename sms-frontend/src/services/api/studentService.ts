import apiClient from './client';
import {
  type Student,
  type CreateStudentRequest,
  type UpdateStudentRequest,
  type UpdateStatusRequest,
  type StudentPageResponse,
  type StudentSearchParams,
} from '@/types/student.types';

const STUDENT_BASE_URL = '/students';

export const studentService = {
  /**
   * Create a new student
   */
  async createStudent(data: CreateStudentRequest): Promise<Student> {
    const response = await apiClient.post<Student>(STUDENT_BASE_URL, data);
    return response.data;
  },

  /**
   * Get student by ID
   */
  async getStudentById(id: number): Promise<Student> {
    const response = await apiClient.get<Student>(`${STUDENT_BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Get student by StudentID
   */
  async getStudentByStudentId(studentId: string): Promise<Student> {
    const response = await apiClient.get<Student>(
      `${STUDENT_BASE_URL}/student-id/${studentId}`
    );
    return response.data;
  },

  /**
   * Search students with pagination and filters
   */
  async searchStudents(params: StudentSearchParams): Promise<StudentPageResponse> {
    const response = await apiClient.get<StudentPageResponse>(STUDENT_BASE_URL, {
      params,
    });
    return response.data;
  },

  /**
   * Update student
   */
  async updateStudent(id: number, data: UpdateStudentRequest): Promise<Student> {
    const response = await apiClient.put<Student>(`${STUDENT_BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Update student status
   */
  async updateStatus(id: number, data: UpdateStatusRequest): Promise<Student> {
    const response = await apiClient.patch<Student>(
      `${STUDENT_BASE_URL}/${id}/status`,
      data
    );
    return response.data;
  },

  /**
   * Delete student (soft delete)
   */
  async deleteStudent(id: number): Promise<void> {
    await apiClient.delete(`${STUDENT_BASE_URL}/${id}`);
  },
};

export default studentService;
