import { useMutation, useQueryClient } from '@tanstack/react-query';
import { configService } from '@/services/api/configService';
import { useNotification } from '@/contexts/NotificationContext';

export const useDeleteConfiguration = () => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (id: number) => configService.deleteConfiguration(id),
    onSuccess: () => {
      // Invalidate configurations queries
      queryClient.invalidateQueries({ queryKey: ['configurations'] });

      addNotification('success', 'Configuration deleted successfully');
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || error.message || 'Failed to delete configuration';
      addNotification('error', message);
    },
  });
};
