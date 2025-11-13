/**
 * React Query hooks for Fee Management Module
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import {
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
} from '../api/feeApi';
import type {
  FeeFilters,
  FeeStructureFormData,
  StudentFeeAssignmentRequest,
  FeeCalculationRequest,
} from '../types/fee.types';

/**
 * Query keys for caching
 */
export const FEE_QUERY_KEYS = {
  all: ['fees'] as const,
  structures: () => [...FEE_QUERY_KEYS.all, 'structures'] as const,
  structure: (id: number) => [...FEE_QUERY_KEYS.structures(), id] as const,
  studentFees: (studentId: number) => [...FEE_QUERY_KEYS.all, 'student', studentId] as const,
  dashboard: () => [...FEE_QUERY_KEYS.all, 'dashboard'] as const,
  academicYears: () => [...FEE_QUERY_KEYS.all, 'academicYears'] as const,
  classes: () => [...FEE_QUERY_KEYS.all, 'classes'] as const,
  journals: (studentId: number, filters?: any) =>
    [...FEE_QUERY_KEYS.all, 'journals', studentId, filters] as const,
};

/**
 * Hook to fetch fee structures with filters
 */
export const useFeeStructures = (filters?: FeeFilters) => {
  return useQuery({
    queryKey: [...FEE_QUERY_KEYS.structures(), filters],
    queryFn: () => getFeeStructures(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Hook to fetch a single fee structure by ID
 */
export const useFeeStructure = (id: number) => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.structure(id),
    queryFn: () => getFeeStructureById(id),
    enabled: !!id && id > 0,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Hook to create a new fee structure
 */
export const useCreateFeeStructure = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: FeeStructureFormData) => createFeeStructure(data),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structures() });
      toast.success(response.message || 'Fee structure created successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to create fee structure';
      toast.error(message);
    },
  });
};

/**
 * Hook to update an existing fee structure
 */
export const useUpdateFeeStructure = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: FeeStructureFormData }) =>
      updateFeeStructure(id, data),
    onSuccess: (response, { id }) => {
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structure(id) });
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structures() });
      toast.success(response.message || 'Fee structure updated successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to update fee structure';
      toast.error(message);
    },
  });
};

/**
 * Hook to delete a fee structure
 */
export const useDeleteFeeStructure = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => deleteFeeStructure(id),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structures() });
      toast.success(response.message || 'Fee structure deleted successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to delete fee structure';
      toast.error(message);
    },
  });
};

/**
 * Hook to toggle fee structure status (activate/deactivate)
 */
export const useToggleFeeStructureStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, isActive }: { id: number; isActive: boolean }) =>
      toggleFeeStructureStatus(id, isActive),
    onSuccess: (response, { id }) => {
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structure(id) });
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.structures() });
      const status = response.feeStructure.isActive ? 'activated' : 'deactivated';
      toast.success(`Fee structure ${status} successfully`);
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to update fee structure status';
      toast.error(message);
    },
  });
};

/**
 * Hook to assign fee structure to student
 */
export const useAssignFeeToStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: StudentFeeAssignmentRequest) => assignFeeToStudent(data),
    onSuccess: (response, { studentId }) => {
      queryClient.invalidateQueries({ queryKey: FEE_QUERY_KEYS.studentFees(studentId) });
      toast.success(response.message || 'Fee assigned to student successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to assign fee to student';
      toast.error(message);
    },
  });
};

/**
 * Hook to fetch student's assigned fees
 */
export const useStudentFees = (studentId: number) => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.studentFees(studentId),
    queryFn: () => getStudentFees(studentId),
    enabled: !!studentId && studentId > 0,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Hook to calculate fees for a student
 */
export const useCalculateStudentFees = () => {
  return useMutation({
    mutationFn: (request: FeeCalculationRequest) => calculateStudentFees(request),
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to calculate fees';
      toast.error(message);
    },
  });
};

/**
 * Hook to fetch fee dashboard statistics
 */
export const useFeeDashboard = () => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.dashboard(),
    queryFn: getFeeDashboardStats,
    staleTime: 2 * 60 * 1000, // 2 minutes - refresh more frequently for dashboard
    refetchOnWindowFocus: true, // Refetch when window regains focus
  });
};

/**
 * Hook to fetch academic years for dropdowns
 */
export const useAcademicYears = () => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.academicYears(),
    queryFn: getAcademicYears,
    staleTime: 30 * 60 * 1000, // 30 minutes - academic years don't change often
  });
};

/**
 * Hook to fetch available classes for dropdowns
 */
export const useAvailableClasses = () => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.classes(),
    queryFn: getAvailableClasses,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

/**
 * Hook to fetch student fee journals
 */
export const useStudentFeeJournals = (
  studentId: number,
  filters?: {
    academicYear?: string;
    status?: string;
    feeMonth?: string;
  }
) => {
  return useQuery({
    queryKey: FEE_QUERY_KEYS.journals(studentId, filters),
    queryFn: () => getStudentFeeJournals(studentId, filters),
    enabled: !!studentId && studentId > 0,
    staleTime: 5 * 60 * 1000,
  });
};
