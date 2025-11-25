import { useMutation, useQueryClient } from '@tanstack/react-query';
import { studentService } from '@/services/api/studentService';
import { type UpdateStudentRequest } from '@/types/student.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useUpdateStudent = (id: number) => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (data: UpdateStudentRequest) => studentService.updateStudent(id, data),
    onSuccess: (student) => {
      // Invalidate both students list and individual student cache
      queryClient.invalidateQueries({ queryKey: ['students'] });
      queryClient.invalidateQueries({ queryKey: ['student', id] });

      // Show success notification
      addNotification('success', `Student ${student.firstName} ${student.lastName} updated successfully!`);
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || error.message || 'Failed to update student';
      addNotification('error', message);
    },
  });
};
