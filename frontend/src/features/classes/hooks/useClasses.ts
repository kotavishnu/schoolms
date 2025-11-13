import { useQuery, useMutation, useQueryClient, type UseQueryOptions } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import {
  getClasses,
  getClassById,
  createClass,
  updateClass,
  deleteClass,
  getClassesWithCapacity,
  getAcademicYears,
  getCurrentAcademicYear,
  getAcademicYearById,
  createAcademicYear,
  updateAcademicYear,
  setCurrentAcademicYear,
  getClassEnrollments,
  getClassStatistics,
  enrollStudent,
  removeStudentFromClass,
  getAvailableStudentsForEnrollment,
  getStudentEnrollments,
} from '../api/classApi';
import type {
  ClassFilters,
  ClassFormData,
  AcademicYearFormData,
  EnrollStudentRequest,
  RemoveStudentRequest,
} from '../types/class.types';

/**
 * React Query hooks for Class Management Module
 *
 * These hooks provide a consistent interface for managing server state
 * with automatic caching, background refetching, and optimistic updates.
 */

// ============================================================================
// CLASS HOOKS
// ============================================================================

/**
 * Hook to fetch paginated list of classes with filters
 *
 * @param filters - Optional filters for search, academic year, grade, etc.
 * @returns Query result with class list data
 */
export const useClasses = (filters?: ClassFilters) => {
  return useQuery({
    queryKey: ['classes', filters],
    queryFn: () => getClasses(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
    placeholderData: (previousData) => previousData, // Keep previous data while loading
  });
};

/**
 * Hook to fetch a single class by ID
 *
 * @param classId - Class ID
 * @param options - Additional query options
 * @returns Query result with class details
 */
export const useClass = (classId: number, options?: UseQueryOptions) => {
  return useQuery({
    queryKey: ['class', classId],
    queryFn: () => getClassById(classId),
    enabled: !!classId && classId > 0,
    staleTime: 5 * 60 * 1000, // 5 minutes
    ...options,
  });
};

/**
 * Hook to fetch classes with available capacity
 *
 * @param academicYearId - Optional academic year filter
 * @returns Query result with classes that have available seats
 */
export const useClassesWithCapacity = (academicYearId?: number) => {
  return useQuery({
    queryKey: ['classes', 'available', academicYearId],
    queryFn: () => getClassesWithCapacity(academicYearId),
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

/**
 * Hook to create a new class
 *
 * @returns Mutation object for creating a class
 */
export const useCreateClass = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: ClassFormData) => createClass(data),
    onSuccess: (response) => {
      // Invalidate class list queries to refetch
      queryClient.invalidateQueries({ queryKey: ['classes'] });
      toast.success(response.message || 'Class created successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to create class';
      toast.error(message);
    },
  });
};

/**
 * Hook to update an existing class
 *
 * @returns Mutation object for updating a class
 */
export const useUpdateClass = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ classId, data }: { classId: number; data: ClassFormData }) =>
      updateClass(classId, data),
    onSuccess: (response, variables) => {
      // Invalidate specific class and list queries
      queryClient.invalidateQueries({ queryKey: ['class', variables.classId] });
      queryClient.invalidateQueries({ queryKey: ['classes'] });
      toast.success(response.message || 'Class updated successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to update class';
      toast.error(message);
    },
  });
};

/**
 * Hook to delete a class
 *
 * @returns Mutation object for deleting a class
 */
export const useDeleteClass = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (classId: number) => deleteClass(classId),
    onSuccess: (response) => {
      // Invalidate class list queries
      queryClient.invalidateQueries({ queryKey: ['classes'] });
      toast.success(response.message || 'Class deleted successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to delete class';
      toast.error(message);
    },
  });
};

// ============================================================================
// ACADEMIC YEAR HOOKS
// ============================================================================

/**
 * Hook to fetch all academic years
 *
 * @returns Query result with list of academic years
 */
export const useAcademicYears = () => {
  return useQuery({
    queryKey: ['academicYears'],
    queryFn: getAcademicYears,
    staleTime: 10 * 60 * 1000, // 10 minutes (academic years change infrequently)
  });
};

/**
 * Hook to fetch current academic year
 *
 * @returns Query result with current academic year
 */
export const useCurrentAcademicYear = () => {
  return useQuery({
    queryKey: ['academicYears', 'current'],
    queryFn: getCurrentAcademicYear,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

/**
 * Hook to fetch academic year by ID
 *
 * @param academicYearId - Academic year ID
 * @returns Query result with academic year details
 */
export const useAcademicYear = (academicYearId: number) => {
  return useQuery({
    queryKey: ['academicYear', academicYearId],
    queryFn: () => getAcademicYearById(academicYearId),
    enabled: !!academicYearId && academicYearId > 0,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

/**
 * Hook to create a new academic year
 *
 * @returns Mutation object for creating an academic year
 */
export const useCreateAcademicYear = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AcademicYearFormData) => createAcademicYear(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['academicYears'] });
      toast.success('Academic year created successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to create academic year';
      toast.error(message);
    },
  });
};

/**
 * Hook to update an academic year
 *
 * @returns Mutation object for updating an academic year
 */
export const useUpdateAcademicYear = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ academicYearId, data }: { academicYearId: number; data: AcademicYearFormData }) =>
      updateAcademicYear(academicYearId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['academicYear', variables.academicYearId] });
      queryClient.invalidateQueries({ queryKey: ['academicYears'] });
      toast.success('Academic year updated successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to update academic year';
      toast.error(message);
    },
  });
};

/**
 * Hook to set an academic year as current
 *
 * @returns Mutation object for setting current academic year
 */
export const useSetCurrentAcademicYear = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (academicYearId: number) => setCurrentAcademicYear(academicYearId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['academicYears'] });
      toast.success('Current academic year updated successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to set current academic year';
      toast.error(message);
    },
  });
};

// ============================================================================
// ENROLLMENT HOOKS
// ============================================================================

/**
 * Hook to fetch enrollments for a class
 *
 * @param classId - Class ID
 * @returns Query result with list of enrollments
 */
export const useClassEnrollments = (classId: number) => {
  return useQuery({
    queryKey: ['enrollments', 'class', classId],
    queryFn: () => getClassEnrollments(classId),
    enabled: !!classId && classId > 0,
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

/**
 * Hook to fetch class statistics
 *
 * @param classId - Class ID
 * @returns Query result with class statistics
 */
export const useClassStatistics = (classId: number) => {
  return useQuery({
    queryKey: ['statistics', 'class', classId],
    queryFn: () => getClassStatistics(classId),
    enabled: !!classId && classId > 0,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Hook to enroll a student in a class
 *
 * @returns Mutation object for enrolling a student
 */
export const useEnrollStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: EnrollStudentRequest) => enrollStudent(data),
    onSuccess: (response, variables) => {
      // Invalidate relevant queries
      queryClient.invalidateQueries({ queryKey: ['enrollments', 'class', variables.classId] });
      queryClient.invalidateQueries({ queryKey: ['class', variables.classId] });
      queryClient.invalidateQueries({ queryKey: ['statistics', 'class', variables.classId] });
      queryClient.invalidateQueries({ queryKey: ['enrollments', 'student', variables.studentId] });
      toast.success(response.message || 'Student enrolled successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to enroll student';
      toast.error(message);
    },
  });
};

/**
 * Hook to remove a student from a class
 *
 * @returns Mutation object for removing a student
 */
export const useRemoveStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      enrollmentId,
      data,
    }: {
      enrollmentId: number;
      data: Omit<RemoveStudentRequest, 'enrollmentId'>;
    }) => removeStudentFromClass(enrollmentId, data),
    onSuccess: (response) => {
      // Invalidate enrollment queries
      queryClient.invalidateQueries({ queryKey: ['enrollments'] });
      queryClient.invalidateQueries({ queryKey: ['statistics'] });
      queryClient.invalidateQueries({ queryKey: ['classes'] });
      toast.success(response.message || 'Student removed from class successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to remove student';
      toast.error(message);
    },
  });
};

/**
 * Hook to fetch students available for enrollment
 *
 * @param academicYearId - Academic year ID
 * @returns Query result with list of available students
 */
export const useAvailableStudentsForEnrollment = (academicYearId: number) => {
  return useQuery({
    queryKey: ['students', 'available', academicYearId],
    queryFn: () => getAvailableStudentsForEnrollment(academicYearId),
    enabled: !!academicYearId && academicYearId > 0,
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

/**
 * Hook to fetch enrollment history for a student
 *
 * @param studentId - Student ID
 * @returns Query result with student's enrollment history
 */
export const useStudentEnrollments = (studentId: number) => {
  return useQuery({
    queryKey: ['enrollments', 'student', studentId],
    queryFn: () => getStudentEnrollments(studentId),
    enabled: !!studentId && studentId > 0,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Export all hooks
 */
export const classHooks = {
  useClasses,
  useClass,
  useClassesWithCapacity,
  useCreateClass,
  useUpdateClass,
  useDeleteClass,
  useAcademicYears,
  useCurrentAcademicYear,
  useAcademicYear,
  useCreateAcademicYear,
  useUpdateAcademicYear,
  useSetCurrentAcademicYear,
  useClassEnrollments,
  useClassStatistics,
  useEnrollStudent,
  useRemoveStudent,
  useAvailableStudentsForEnrollment,
  useStudentEnrollments,
};

export default classHooks;
