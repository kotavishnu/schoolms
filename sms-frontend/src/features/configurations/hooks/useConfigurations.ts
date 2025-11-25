import { useQuery } from '@tanstack/react-query';
import { configService } from '@/services/api/configService';

export const useConfigurations = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: ['configurations', page, size],
    queryFn: () => configService.getAllConfigurations(page, size),
    staleTime: 60 * 60 * 1000, // 1 hour - configs change infrequently
  });
};

export const useConfigurationsByCategory = (category?: string) => {
  return useQuery({
    queryKey: ['configurations', 'category', category],
    queryFn: () => configService.getConfigurationsByCategory(category!),
    enabled: !!category, // Only fetch if category is provided
    staleTime: 60 * 60 * 1000, // 1 hour
  });
};
