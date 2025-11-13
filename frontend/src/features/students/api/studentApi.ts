import { apiClient } from '@/api/client';
import type {
  Student,
  StudentFilters,
  StudentListResponse,
  StudentFormData,
  StudentCreateResponse,
  StudentUpdateResponse,
  StudentDeleteResponse,
  ClassOption,
} from '../types/student.types';

/**
 * Student API service functions
 *
 * Note: These functions will be integrated with React Query hooks
 */

const STUDENTS_ENDPOINT = '/api/v1/students';
const CLASSES_ENDPOINT = '/api/v1/classes';

/**
 * Get paginated list of students with optional filters
 *
 * @param filters - Search, status, class, and pagination filters
 * @returns Paginated student list
 */
export const getStudents = async (filters?: StudentFilters): Promise<StudentListResponse> => {
  const params = new URLSearchParams();

  if (filters?.search) params.append('search', filters.search);
  if (filters?.status) params.append('status', filters.status);
  if (filters?.classId) params.append('classId', filters.classId.toString());
  if (filters?.page) params.append('page', filters.page.toString());
  if (filters?.limit) params.append('limit', filters.limit.toString());

  const response = await apiClient.get<StudentListResponse>(
    `${STUDENTS_ENDPOINT}?${params.toString()}`
  );

  return response.data;
};

/**
 * Get a single student by ID
 *
 * @param id - Student ID
 * @returns Student details
 */
export const getStudentById = async (id: number): Promise<Student> => {
  const response = await apiClient.get<Student>(`${STUDENTS_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Create a new student
 *
 * @param data - Student form data
 * @returns Created student
 */
export const createStudent = async (data: StudentFormData): Promise<StudentCreateResponse> => {
  // If photo is provided, use FormData for multipart/form-data
  if (data.photo && data.photo instanceof File) {
    const formData = new FormData();

    // Append all fields to FormData
    formData.append('firstName', data.firstName);
    formData.append('lastName', data.lastName);
    formData.append('dateOfBirth', data.dateOfBirth);
    formData.append('gender', data.gender);

    if (data.bloodGroup) formData.append('bloodGroup', data.bloodGroup);
    if (data.email) formData.append('email', data.email);
    if (data.phone) formData.append('phone', data.phone);

    // Append nested objects as JSON strings
    formData.append('address', JSON.stringify(data.address));
    formData.append('guardian', JSON.stringify(data.guardian));
    formData.append('academic', JSON.stringify(data.academic));

    // Append photo file
    formData.append('photo', data.photo);

    const response = await apiClient.post<StudentCreateResponse>(STUDENTS_ENDPOINT, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  // If no photo, send as JSON
  const { photo, ...studentData } = data;
  const response = await apiClient.post<StudentCreateResponse>(STUDENTS_ENDPOINT, studentData);

  return response.data;
};

/**
 * Update an existing student
 *
 * @param id - Student ID
 * @param data - Updated student form data
 * @returns Updated student
 */
export const updateStudent = async (
  id: number,
  data: StudentFormData
): Promise<StudentUpdateResponse> => {
  // If photo is provided, use FormData for multipart/form-data
  if (data.photo && data.photo instanceof File) {
    const formData = new FormData();

    // Append all fields to FormData
    formData.append('firstName', data.firstName);
    formData.append('lastName', data.lastName);
    formData.append('dateOfBirth', data.dateOfBirth);
    formData.append('gender', data.gender);

    if (data.bloodGroup) formData.append('bloodGroup', data.bloodGroup);
    if (data.email) formData.append('email', data.email);
    if (data.phone) formData.append('phone', data.phone);

    // Append nested objects as JSON strings
    formData.append('address', JSON.stringify(data.address));
    formData.append('guardian', JSON.stringify(data.guardian));
    formData.append('academic', JSON.stringify(data.academic));

    // Append photo file
    formData.append('photo', data.photo);

    const response = await apiClient.put<StudentUpdateResponse>(`${STUDENTS_ENDPOINT}/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  // If no photo, send as JSON
  const { photo, ...studentData } = data;
  const response = await apiClient.put<StudentUpdateResponse>(`${STUDENTS_ENDPOINT}/${id}`, studentData);

  return response.data;
};

/**
 * Delete a student
 *
 * @param id - Student ID
 * @returns Deletion confirmation
 */
export const deleteStudent = async (id: number): Promise<StudentDeleteResponse> => {
  const response = await apiClient.delete<StudentDeleteResponse>(`${STUDENTS_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Get available classes for student assignment
 *
 * @returns List of classes with capacity information
 */
export const getAvailableClasses = async (): Promise<ClassOption[]> => {
  const response = await apiClient.get<ClassOption[]>(`${CLASSES_ENDPOINT}/available`);
  return response.data;
};

/**
 * Export all student API functions
 */
export const studentApi = {
  getStudents,
  getStudentById,
  createStudent,
  updateStudent,
  deleteStudent,
  getAvailableClasses,
};

export default studentApi;
