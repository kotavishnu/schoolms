import { useMutation, useQueryClient } from '@tanstack/react-query';
import { configService } from '@/services/api/configService';
import { type CreateConfigRequest } from '@/types/config.types';
import { useNotification } from '@/contexts/NotificationContext';

export const useCreateConfiguration = () => {
  const queryClient = useQueryClient();
  const { addNotification } = useNotification();

  return useMutation({
    mutationFn: (data: CreateConfigRequest) => configService.createConfiguration(data),
    onSuccess: (config) => {
      // Invalidate configurations queries
      queryClient.invalidateQueries({ queryKey: ['configurations'] });

      addNotification('success', `Configuration "${config.configKey}" created successfully!`);
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || error.message || 'Failed to create configuration';
      addNotification('error', message);
    },
  });
};
