/**
 * Fee Management API service functions
 */
import { apiClient } from '@/api/client';
import type {
  FeeStructure,
  FeeStructureFormData,
  FeeStructureListResponse,
  FeeStructureCreateResponse,
  FeeStructureUpdateResponse,
  FeeStructureDeleteResponse,
  FeeFilters,
  StudentFeeAssignmentRequest,
  StudentFeeAssignmentResponse,
  StudentFee,
  FeeDashboardStats,
  AcademicYearOption,
  ClassOption,
  FeeCalculationRequest,
  FeeCalculationResponse,
  FeeJournal,
} from '../types/fee.types';

const FEE_STRUCTURES_ENDPOINT = '/api/v1/fee-structures';
const FEE_ASSIGN_ENDPOINT = '/api/v1/fees/assign';
const FEE_DASHBOARD_ENDPOINT = '/api/v1/fees/dashboard';
const ACADEMIC_YEARS_ENDPOINT = '/api/v1/academic-years';
const CLASSES_ENDPOINT = '/api/v1/classes';

/**
 * Get paginated list of fee structures with optional filters
 */
export const getFeeStructures = async (
  filters?: FeeFilters
): Promise<FeeStructureListResponse> => {
  const params = new URLSearchParams();

  if (filters?.search) params.append('search', filters.search);
  if (filters?.academicYear) params.append('academicYear', filters.academicYear);
  if (filters?.feeType) params.append('feeType', filters.feeType);
  if (filters?.frequency) params.append('frequency', filters.frequency);
  if (filters?.isActive !== undefined) params.append('isActive', String(filters.isActive));
  if (filters?.applicableClass) params.append('applicableClass', filters.applicableClass);
  if (filters?.page !== undefined) params.append('page', String(filters.page));
  if (filters?.limit) params.append('size', String(filters.limit));

  const response = await apiClient.get<FeeStructureListResponse>(
    `${FEE_STRUCTURES_ENDPOINT}?${params.toString()}`
  );

  return response.data;
};

/**
 * Get a single fee structure by ID
 */
export const getFeeStructureById = async (id: number): Promise<FeeStructure> => {
  const response = await apiClient.get<FeeStructure>(`${FEE_STRUCTURES_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Create a new fee structure
 */
export const createFeeStructure = async (
  data: FeeStructureFormData
): Promise<FeeStructureCreateResponse> => {
  const response = await apiClient.post<FeeStructureCreateResponse>(
    FEE_STRUCTURES_ENDPOINT,
    data
  );
  return response.data;
};

/**
 * Update an existing fee structure
 */
export const updateFeeStructure = async (
  id: number,
  data: FeeStructureFormData
): Promise<FeeStructureUpdateResponse> => {
  const response = await apiClient.put<FeeStructureUpdateResponse>(
    `${FEE_STRUCTURES_ENDPOINT}/${id}`,
    data
  );
  return response.data;
};

/**
 * Delete a fee structure
 */
export const deleteFeeStructure = async (id: number): Promise<FeeStructureDeleteResponse> => {
  const response = await apiClient.delete<FeeStructureDeleteResponse>(
    `${FEE_STRUCTURES_ENDPOINT}/${id}`
  );
  return response.data;
};

/**
 * Activate or deactivate a fee structure
 */
export const toggleFeeStructureStatus = async (
  id: number,
  isActive: boolean
): Promise<FeeStructureUpdateResponse> => {
  const response = await apiClient.patch<FeeStructureUpdateResponse>(
    `${FEE_STRUCTURES_ENDPOINT}/${id}/status`,
    { isActive }
  );
  return response.data;
};

/**
 * Assign fee structure to student
 */
export const assignFeeToStudent = async (
  data: StudentFeeAssignmentRequest
): Promise<StudentFeeAssignmentResponse> => {
  const response = await apiClient.post<StudentFeeAssignmentResponse>(
    FEE_ASSIGN_ENDPOINT,
    data
  );
  return response.data;
};

/**
 * Get student's assigned fees
 */
export const getStudentFees = async (studentId: number): Promise<StudentFee[]> => {
  const response = await apiClient.get<StudentFee[]>(`/api/v1/fees/students/${studentId}`);
  return response.data;
};

/**
 * Calculate fees for a student
 */
export const calculateStudentFees = async (
  request: FeeCalculationRequest
): Promise<FeeCalculationResponse> => {
  const response = await apiClient.post<FeeCalculationResponse>(
    `${FEE_STRUCTURES_ENDPOINT}/calculate`,
    request
  );
  return response.data;
};

/**
 * Get fee dashboard statistics
 */
export const getFeeDashboardStats = async (): Promise<FeeDashboardStats> => {
  const response = await apiClient.get<FeeDashboardStats>(FEE_DASHBOARD_ENDPOINT);
  return response.data;
};

/**
 * Get available academic years for dropdowns
 */
export const getAcademicYears = async (): Promise<AcademicYearOption[]> => {
  const response = await apiClient.get<AcademicYearOption[]>(ACADEMIC_YEARS_ENDPOINT);
  return response.data;
};

/**
 * Get available classes for dropdowns
 */
export const getAvailableClasses = async (): Promise<ClassOption[]> => {
  const response = await apiClient.get<ClassOption[]>(`${CLASSES_ENDPOINT}/available`);
  return response.data;
};

/**
 * Get fee journals for a student
 */
export const getStudentFeeJournals = async (
  studentId: number,
  filters?: {
    academicYear?: string;
    status?: string;
    feeMonth?: string;
  }
): Promise<{ journals: FeeJournal[]; summary: any }> => {
  const params = new URLSearchParams();

  if (filters?.academicYear) params.append('academicYear', filters.academicYear);
  if (filters?.status) params.append('status', filters.status);
  if (filters?.feeMonth) params.append('feeMonth', filters.feeMonth);

  const response = await apiClient.get(
    `/api/v1/students/${studentId}/fee-journals?${params.toString()}`
  );
  return response.data;
};

/**
 * Export all fee API functions
 */
export const feeApi = {
  getFeeStructures,
  getFeeStructureById,
  createFeeStructure,
  updateFeeStructure,
  deleteFeeStructure,
  toggleFeeStructureStatus,
  assignFeeToStudent,
  getStudentFees,
  calculateStudentFees,
  getFeeDashboardStats,
  getAcademicYears,
  getAvailableClasses,
  getStudentFeeJournals,
};

export default feeApi;
