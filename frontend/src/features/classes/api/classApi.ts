import { apiClient } from '@/api/client';
import type {
  SchoolClass,
  ClassFormData,
  ClassFilters,
  ClassListResponse,
  ClassCreateResponse,
  ClassUpdateResponse,
  ClassDeleteResponse,
  AcademicYear,
  AcademicYearFormData,
  Enrollment,
  EnrollStudentRequest,
  RemoveStudentRequest,
  EnrollmentResponse,
  EnrollableStudent,
  ClassStatistics,
} from '../types/class.types';

/**
 * Class API service functions
 *
 * These functions handle all HTTP requests for the Class Management Module.
 * They are integrated with React Query hooks for state management.
 */

const CLASSES_ENDPOINT = '/api/v1/classes';
const ACADEMIC_YEARS_ENDPOINT = '/api/v1/academic-years';
const ENROLLMENTS_ENDPOINT = '/api/v1/enrollments';

// ============================================================================
// CLASS CRUD OPERATIONS
// ============================================================================

/**
 * Get paginated list of classes with optional filters
 *
 * @param filters - Search, academic year, grade, section, and pagination filters
 * @returns Paginated class list
 */
export const getClasses = async (filters?: ClassFilters): Promise<ClassListResponse> => {
  const params = new URLSearchParams();

  if (filters?.search) params.append('search', filters.search);
  if (filters?.academicYearId) params.append('academicYearId', filters.academicYearId.toString());
  if (filters?.className) params.append('className', filters.className);
  if (filters?.section) params.append('section', filters.section);
  if (filters?.isActive !== undefined) params.append('isActive', filters.isActive.toString());
  if (filters?.page !== undefined) params.append('page', filters.page.toString());
  if (filters?.limit !== undefined) params.append('size', filters.limit.toString());

  const response = await apiClient.get<ClassListResponse>(
    `${CLASSES_ENDPOINT}?${params.toString()}`
  );

  return response.data;
};

/**
 * Get a single class by ID
 *
 * @param id - Class ID
 * @returns Class details
 */
export const getClassById = async (id: number): Promise<SchoolClass> => {
  const response = await apiClient.get<SchoolClass>(`${CLASSES_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Create a new class
 *
 * @param data - Class form data
 * @returns Created class
 */
export const createClass = async (data: ClassFormData): Promise<ClassCreateResponse> => {
  const response = await apiClient.post<ClassCreateResponse>(CLASSES_ENDPOINT, data);
  return response.data;
};

/**
 * Update an existing class
 *
 * @param id - Class ID
 * @param data - Updated class form data
 * @returns Updated class
 */
export const updateClass = async (
  id: number,
  data: ClassFormData
): Promise<ClassUpdateResponse> => {
  const response = await apiClient.put<ClassUpdateResponse>(`${CLASSES_ENDPOINT}/${id}`, data);
  return response.data;
};

/**
 * Delete a class (only if no enrollments exist)
 *
 * @param id - Class ID
 * @returns Deletion confirmation
 */
export const deleteClass = async (id: number): Promise<ClassDeleteResponse> => {
  const response = await apiClient.delete<ClassDeleteResponse>(`${CLASSES_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Get classes with available capacity
 *
 * @param academicYearId - Optional academic year filter
 * @returns Classes that have available seats
 */
export const getClassesWithCapacity = async (academicYearId?: number): Promise<SchoolClass[]> => {
  const params = new URLSearchParams();
  if (academicYearId) params.append('academicYearId', academicYearId.toString());

  const response = await apiClient.get<SchoolClass[]>(
    `${CLASSES_ENDPOINT}/available?${params.toString()}`
  );

  return response.data;
};

// ============================================================================
// ACADEMIC YEAR OPERATIONS
// ============================================================================

/**
 * Get all academic years
 *
 * @returns List of academic years ordered by start date descending
 */
export const getAcademicYears = async (): Promise<AcademicYear[]> => {
  const response = await apiClient.get<AcademicYear[]>(ACADEMIC_YEARS_ENDPOINT);
  return response.data;
};

/**
 * Get current academic year
 *
 * @returns Current academic year
 */
export const getCurrentAcademicYear = async (): Promise<AcademicYear> => {
  const response = await apiClient.get<AcademicYear>(`${ACADEMIC_YEARS_ENDPOINT}/current`);
  return response.data;
};

/**
 * Get academic year by ID
 *
 * @param id - Academic year ID
 * @returns Academic year details
 */
export const getAcademicYearById = async (id: number): Promise<AcademicYear> => {
  const response = await apiClient.get<AcademicYear>(`${ACADEMIC_YEARS_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Create a new academic year
 *
 * @param data - Academic year form data
 * @returns Created academic year
 */
export const createAcademicYear = async (data: AcademicYearFormData): Promise<AcademicYear> => {
  const response = await apiClient.post<AcademicYear>(ACADEMIC_YEARS_ENDPOINT, data);
  return response.data;
};

/**
 * Update an existing academic year
 *
 * @param id - Academic year ID
 * @param data - Updated academic year form data
 * @returns Updated academic year
 */
export const updateAcademicYear = async (
  id: number,
  data: AcademicYearFormData
): Promise<AcademicYear> => {
  const response = await apiClient.put<AcademicYear>(`${ACADEMIC_YEARS_ENDPOINT}/${id}`, data);
  return response.data;
};

/**
 * Set an academic year as current
 *
 * @param id - Academic year ID to set as current
 * @returns Updated academic year
 */
export const setCurrentAcademicYear = async (id: number): Promise<AcademicYear> => {
  const response = await apiClient.put<AcademicYear>(
    `${ACADEMIC_YEARS_ENDPOINT}/${id}/set-current`,
    {}
  );
  return response.data;
};

// ============================================================================
// ENROLLMENT OPERATIONS
// ============================================================================

/**
 * Get all enrollments for a class
 *
 * @param classId - Class ID
 * @returns List of enrollments for the class
 */
export const getClassEnrollments = async (classId: number): Promise<Enrollment[]> => {
  const response = await apiClient.get<Enrollment[]>(
    `${CLASSES_ENDPOINT}/${classId}/enrollments`
  );
  return response.data;
};

/**
 * Get enrollment statistics for a class
 *
 * @param classId - Class ID
 * @returns Class statistics including enrollment counts
 */
export const getClassStatistics = async (classId: number): Promise<ClassStatistics> => {
  const response = await apiClient.get<ClassStatistics>(
    `${CLASSES_ENDPOINT}/${classId}/statistics`
  );
  return response.data;
};

/**
 * Enroll a student in a class
 *
 * @param data - Enrollment request data (studentId, classId, enrollmentDate)
 * @returns Created enrollment
 */
export const enrollStudent = async (data: EnrollStudentRequest): Promise<EnrollmentResponse> => {
  const response = await apiClient.post<EnrollmentResponse>(ENROLLMENTS_ENDPOINT, data);
  return response.data;
};

/**
 * Remove a student from a class (withdraw)
 *
 * @param enrollmentId - Enrollment ID
 * @param data - Withdrawal data (withdrawalDate, withdrawalReason)
 * @returns Updated enrollment
 */
export const removeStudentFromClass = async (
  enrollmentId: number,
  data: Omit<RemoveStudentRequest, 'enrollmentId'>
): Promise<EnrollmentResponse> => {
  const response = await apiClient.put<EnrollmentResponse>(
    `${ENROLLMENTS_ENDPOINT}/${enrollmentId}/withdraw`,
    data
  );
  return response.data;
};

/**
 * Get students available for enrollment
 * (students who are not currently enrolled in any class for the given academic year)
 *
 * @param academicYearId - Academic year ID
 * @returns List of students available for enrollment
 */
export const getAvailableStudentsForEnrollment = async (
  academicYearId: number
): Promise<EnrollableStudent[]> => {
  const response = await apiClient.get<EnrollableStudent[]>(
    `${ENROLLMENTS_ENDPOINT}/available-students?academicYearId=${academicYearId}`
  );
  return response.data;
};

/**
 * Get enrollment history for a student
 *
 * @param studentId - Student ID
 * @returns List of all enrollments for the student
 */
export const getStudentEnrollments = async (studentId: number): Promise<Enrollment[]> => {
  const response = await apiClient.get<Enrollment[]>(
    `${ENROLLMENTS_ENDPOINT}/student/${studentId}`
  );
  return response.data;
};

// ============================================================================
// EXPORT ALL API FUNCTIONS
// ============================================================================

/**
 * Exported class API object with all functions
 */
export const classApi = {
  // Class operations
  getClasses,
  getClassById,
  createClass,
  updateClass,
  deleteClass,
  getClassesWithCapacity,

  // Academic year operations
  getAcademicYears,
  getCurrentAcademicYear,
  getAcademicYearById,
  createAcademicYear,
  updateAcademicYear,
  setCurrentAcademicYear,

  // Enrollment operations
  getClassEnrollments,
  getClassStatistics,
  enrollStudent,
  removeStudentFromClass,
  getAvailableStudentsForEnrollment,
  getStudentEnrollments,
};

export default classApi;
