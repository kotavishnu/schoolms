import { useState, useEffect } from 'react';
import { studentService } from '@/services/studentService';
import type { Student, StudentCreateRequest, StudentUpdateRequest } from '@/types/student';
import { toast } from 'sonner';

interface UseStudentsReturn {
  students: Student[];
  loading: boolean;
  error: string | null;
  createStudent: (data: StudentCreateRequest) => Promise<Student | null>;
  updateStudent: (id: string, data: StudentUpdateRequest) => Promise<Student | null>;
  deleteStudent: (id: string) => Promise<boolean>;
  refreshStudents: () => Promise<void>;
  searchStudents: (search?: string, status?: 'ACTIVE' | 'INACTIVE') => Promise<void>;
}

/**
 * Custom hook for managing student CRUD operations
 */
export function useStudents(): UseStudentsReturn {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const refreshStudents = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentService.getAll();
      setStudents(response.students || []);
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to load students';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const searchStudents = async (search?: string, status?: 'ACTIVE' | 'INACTIVE') => {
    setLoading(true);
    setError(null);
    try {
      const params: any = {};
      if (search) params.search = search;
      if (status) params.status = status;

      const response = await studentService.getAll(params);
      setStudents(response.students || []);
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Search failed';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const createStudent = async (data: StudentCreateRequest): Promise<Student | null> => {
    try {
      const newStudent = await studentService.create(data);
      toast.success('Student created successfully');
      await refreshStudents();
      return newStudent;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to create student';
      toast.error(errorMessage);
      return null;
    }
  };

  const updateStudent = async (id: string, data: StudentUpdateRequest): Promise<Student | null> => {
    try {
      const updatedStudent = await studentService.update(id, data);
      toast.success('Student updated successfully');
      await refreshStudents();
      return updatedStudent;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to update student';
      toast.error(errorMessage);
      return null;
    }
  };

  const deleteStudent = async (id: string): Promise<boolean> => {
    try {
      await studentService.delete(id);
      toast.success('Student deleted successfully');
      await refreshStudents();
      return true;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to delete student';
      toast.error(errorMessage);
      return false;
    }
  };

  useEffect(() => {
    refreshStudents();
  }, []);

  return {
    students,
    loading,
    error,
    createStudent,
    updateStudent,
    deleteStudent,
    refreshStudents,
    searchStudents,
  };
}
