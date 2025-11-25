import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { studentService } from '@/services/api/studentService';
import { useNotification } from '@/contexts/NotificationContext';

export const useDeleteStudent = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (id: number) => studentService.deleteStudent(id),
    onSuccess: () => {
      // Invalidate students list
      queryClient.invalidateQueries({ queryKey: ['students'] });

      // Show success notification
      addNotification('success', 'Student deleted successfully');

      // Navigate back to students list
      navigate('/students');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || error.message || 'Failed to delete student';
      addNotification('error', message);
    },
  });
};
