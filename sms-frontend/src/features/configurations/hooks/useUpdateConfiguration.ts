import { useMutation, useQueryClient } from '@tanstack/react-query';
import { configService } from '@/services/api/configService';
import { type UpdateConfigRequest } from '@/types/config.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useUpdateConfiguration = (id: number) => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (data: UpdateConfigRequest) => configService.updateConfiguration(id, data),
    onSuccess: (config) => {
      // Invalidate configurations queries
      queryClient.invalidateQueries({ queryKey: ['configurations'] });

      addNotification('success', `Configuration "${config.configKey}" updated successfully!`);
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || error.message || 'Failed to update configuration';
      addNotification('error', message);
    },
  });
};
