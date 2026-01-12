/**
 * useStudents Hook
 * Manages student data fetching and operations
 */

import { useState, useEffect, useCallback } from 'react';
import { studentService } from '../services/studentService';
import type {
  Student,
  StudentCreateDto,
  StudentUpdateDto,
  StudentFilters,
  StudentStatistics,
} from '../types';
import { ApiErrorClass } from '../types/api';

interface UseStudentsResult {
  students: Student[];
  loading: boolean;
  error: string | null;
  statistics: StudentStatistics | null;
  refetch: () => Promise<void>;
  createStudent: (data: StudentCreateDto) => Promise<Student>;
  updateStudent: (id: string, data: StudentUpdateDto) => Promise<Student>;
  deleteStudent: (id: string) => Promise<void>;
  fetchStatistics: () => Promise<void>;
}

export function useStudents(filters?: StudentFilters): UseStudentsResult {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [statistics, setStatistics] = useState<StudentStatistics | null>(null);

  /**
   * Fetch students with current filters
   */
  const fetchStudents = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await studentService.getAll(filters);
      setStudents(response.students);
    } catch (err) {
      const errorMessage =
        err instanceof ApiErrorClass
          ? err.message
          : 'Failed to fetch students';
      setError(errorMessage);
      console.error('Error fetching students:', err);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  /**
   * Fetch student statistics
   */
  const fetchStatistics = useCallback(async () => {
    try {
      const stats = await studentService.getStatistics();
      setStatistics(stats);
    } catch (err) {
      console.error('Error fetching statistics:', err);
    }
  }, []);

  /**
   * Create a new student
   */
  const createStudent = useCallback(
    async (data: StudentCreateDto): Promise<Student> => {
      const newStudent = await studentService.create(data);
      // Refresh the list after creation
      await fetchStudents();
      await fetchStatistics();
      return newStudent;
    },
    [fetchStudents, fetchStatistics]
  );

  /**
   * Update an existing student
   */
  const updateStudent = useCallback(
    async (id: string, data: StudentUpdateDto): Promise<Student> => {
      const updatedStudent = await studentService.update(id, data);
      // Update the student in the local list
      setStudents((prev) =>
        prev.map((student) => (student.id === id ? updatedStudent : student))
      );
      await fetchStatistics();
      return updatedStudent;
    },
    [fetchStatistics]
  );

  /**
   * Delete a student
   */
  const deleteStudent = useCallback(
    async (id: string): Promise<void> => {
      await studentService.delete(id);
      // Remove the student from the local list
      setStudents((prev) => prev.filter((student) => student.id !== id));
      await fetchStatistics();
    },
    [fetchStatistics]
  );

  /**
   * Fetch students on mount and when filters change
   */
  useEffect(() => {
    fetchStudents();
  }, [fetchStudents]);

  /**
   * Fetch statistics on mount
   */
  useEffect(() => {
    fetchStatistics();
  }, [fetchStatistics]);

  return {
    students,
    loading,
    error,
    statistics,
    refetch: fetchStudents,
    createStudent,
    updateStudent,
    deleteStudent,
    fetchStatistics,
  };
}

export default useStudents;
