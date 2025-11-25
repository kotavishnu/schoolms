import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { studentService } from '@/services/api/studentService';
import { type CreateStudentRequest } from '@/types/student.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useCreateStudent = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (data: CreateStudentRequest) => studentService.createStudent(data),
    onSuccess: (student) => {
      // Invalidate students list to refetch
      queryClient.invalidateQueries({ queryKey: ['students'] });

      // Show success notification
      addNotification('success', `Student ${student.firstName} ${student.lastName} registered successfully!`);

      // Navigate to student detail page
      navigate(`/students/${student.id}`);
    },
    onError: (error: any) => {
      // Extract error message from RFC 7807 Problem Details or use default
      const message = error.response?.data?.detail || error.message || 'Failed to create student';
      addNotification('error', message);
    },
  });
};
