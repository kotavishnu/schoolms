import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { studentApi } from '../api/studentApi';
import type { StudentFilters, StudentFormData } from '../types/student.types';

/**
 * Query keys for React Query cache management
 */
export const studentKeys = {
  all: ['students'] as const,
  lists: () => [...studentKeys.all, 'list'] as const,
  list: (filters?: StudentFilters) => [...studentKeys.lists(), filters] as const,
  details: () => [...studentKeys.all, 'detail'] as const,
  detail: (id: number) => [...studentKeys.details(), id] as const,
  classes: () => ['classes', 'available'] as const,
};

/**
 * Hook to fetch paginated list of students with filters
 *
 * @param filters - Search, status, class, and pagination filters
 * @returns Query result with student list data
 */
export const useStudents = (filters?: StudentFilters) => {
  return useQuery({
    queryKey: studentKeys.list(filters),
    queryFn: () => studentApi.getStudents(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Hook to fetch a single student by ID
 *
 * @param id - Student ID
 * @returns Query result with student details
 */
export const useStudent = (id: number) => {
  return useQuery({
    queryKey: studentKeys.detail(id),
    queryFn: () => studentApi.getStudentById(id),
    staleTime: 5 * 60 * 1000, // 5 minutes
    enabled: !!id, // Only fetch if ID is provided
  });
};

/**
 * Hook to create a new student
 *
 * @returns Mutation function and state
 */
export const useCreateStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: StudentFormData) => studentApi.createStudent(data),
    onSuccess: (response) => {
      // Invalidate and refetch student lists
      queryClient.invalidateQueries({ queryKey: studentKeys.lists() });

      toast.success(response.message || 'Student registered successfully');
    },
    onError: (error: any) => {
      const errorMessage =
        error.response?.data?.detail ||
        error.response?.data?.message ||
        'Failed to register student';

      toast.error(errorMessage);
    },
  });
};

/**
 * Hook to update an existing student
 *
 * @returns Mutation function and state
 */
export const useUpdateStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: StudentFormData }) =>
      studentApi.updateStudent(id, data),
    onSuccess: (response, variables) => {
      // Invalidate and refetch student lists
      queryClient.invalidateQueries({ queryKey: studentKeys.lists() });

      // Invalidate specific student detail
      queryClient.invalidateQueries({ queryKey: studentKeys.detail(variables.id) });

      toast.success(response.message || 'Student updated successfully');
    },
    onError: (error: any) => {
      const errorMessage =
        error.response?.data?.detail ||
        error.response?.data?.message ||
        'Failed to update student';

      toast.error(errorMessage);
    },
  });
};

/**
 * Hook to delete a student
 *
 * @returns Mutation function and state
 */
export const useDeleteStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => studentApi.deleteStudent(id),
    onSuccess: (response) => {
      // Invalidate and refetch student lists
      queryClient.invalidateQueries({ queryKey: studentKeys.lists() });

      toast.success(response.message || 'Student deleted successfully');
    },
    onError: (error: any) => {
      const errorMessage =
        error.response?.data?.detail ||
        error.response?.data?.message ||
        'Failed to delete student';

      toast.error(errorMessage);
    },
  });
};

/**
 * Hook to fetch available classes
 *
 * @returns Query result with available classes
 */
export const useAvailableClasses = () => {
  return useQuery({
    queryKey: studentKeys.classes(),
    queryFn: () => studentApi.getAvailableClasses(),
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};
