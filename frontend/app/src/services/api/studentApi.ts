import { studentApiClient } from './client';

// Request DTOs
export interface StudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 (YYYY-MM-DD)
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
}

export interface EnrollmentRequest {
  standard: string;
  section: string;
  academicYear: string;
  rollNumber?: string;
}

// Response DTOs
export interface StudentResponse {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  mobile: string;
  email?: string;
  address?: string;
  fathersName?: string;
  mothersName?: string;
  identificationMark?: string;
  aadhaarNumber?: string;
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface Enrollment {
  id: number;
  studentId: string;
  standard: string;
  section: string;
  academicYear: string;
  rollNumber?: string;
  enrollmentDate: string;
  status: string;
}

export interface EnrollmentHistoryResponse {
  studentId: string;
  enrollments: Enrollment[];
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface SearchParams {
  firstName?: string;
  lastName?: string;
  mobile?: string;
  status?: 'ACTIVE' | 'INACTIVE';
  page?: number;
  size?: number;
  sort?: string;
}

// API Methods
class StudentApiService {
  /**
   * Create a new student
   */
  async createStudent(data: StudentRequest): Promise<StudentResponse> {
    const response = await studentApiClient.post<StudentResponse>('/students', data);
    return response.data;
  }

  /**
   * Get student by student ID
   */
  async getStudent(studentId: string): Promise<StudentResponse> {
    const response = await studentApiClient.get<StudentResponse>(`/students/${studentId}`);
    return response.data;
  }

  /**
   * Search students with pagination and filters
   */
  async searchStudents(params: SearchParams = {}): Promise<PaginatedResponse<StudentResponse>> {
    const response = await studentApiClient.get<PaginatedResponse<StudentResponse>>('/students', {
      params: {
        firstName: params.firstName,
        lastName: params.lastName,
        mobile: params.mobile,
        status: params.status,
        page: params.page || 0,
        size: params.size || 20,
        sort: params.sort || 'createdAt,desc',
      },
    });
    return response.data;
  }

  /**
   * Update student information
   */
  async updateStudent(
    studentId: string,
    data: StudentUpdateRequest
  ): Promise<StudentResponse> {
    const response = await studentApiClient.put<StudentResponse>(
      `/students/${studentId}`,
      data
    );
    return response.data;
  }

  /**
   * Delete student (soft delete)
   */
  async deleteStudent(studentId: string): Promise<void> {
    await studentApiClient.delete(`/students/${studentId}`);
  }

  /**
   * Get enrollment history for a student
   */
  async getEnrollmentHistory(studentId: string): Promise<EnrollmentHistoryResponse> {
    const response = await studentApiClient.get<EnrollmentHistoryResponse>(
      `/students/${studentId}/enrollment-history`
    );
    return response.data;
  }

  /**
   * Create enrollment for a student
   */
  async createEnrollment(studentId: string, data: EnrollmentRequest): Promise<Enrollment> {
    const response = await studentApiClient.post<Enrollment>(
      `/students/${studentId}/enrollment-history`,
      data
    );
    return response.data;
  }

  /**
   * Check if mobile number is available (for async validation)
   */
  async checkMobileAvailability(mobile: string, excludeStudentId?: string): Promise<boolean> {
    try {
      const response = await this.searchStudents({ mobile, size: 1 });
      if (response.content.length === 0) {
        return true; // Available
      }
      // If excluding a student ID, check if the result is that student
      if (excludeStudentId && response.content[0].studentId === excludeStudentId) {
        return true; // Available (it's the same student)
      }
      return false; // Not available
    } catch (error) {
      return false; // Assume not available on error
    }
  }
}

export const studentApi = new StudentApiService();
