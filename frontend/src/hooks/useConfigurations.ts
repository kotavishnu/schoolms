import { useState, useEffect } from 'react';
import { configurationService } from '@/services/configurationService';
import type {
  Configuration,
  ConfigurationCreateRequest,
  ConfigurationUpdateRequest,
  ConfigCategory,
} from '@/types/configuration';
import { toast } from 'sonner';

interface UseConfigurationsReturn {
  configurations: Configuration[];
  loading: boolean;
  error: string | null;
  createConfiguration: (data: ConfigurationCreateRequest) => Promise<Configuration | null>;
  updateConfiguration: (category: string, key: string, data: ConfigurationUpdateRequest) => Promise<Configuration | null>;
  deleteConfiguration: (category: string, key: string) => Promise<boolean>;
  refreshConfigurations: () => Promise<void>;
  filterByCategory: (category?: ConfigCategory) => Promise<void>;
}

/**
 * Custom hook for managing configuration CRUD operations
 */
export function useConfigurations(): UseConfigurationsReturn {
  const [configurations, setConfigurations] = useState<Configuration[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const refreshConfigurations = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await configurationService.getAll();
      setConfigurations(data || []);
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to load configurations';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const filterByCategory = async (category?: ConfigCategory) => {
    setLoading(true);
    setError(null);
    try {
      const data = await configurationService.getAll(category);
      setConfigurations(data || []);
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Filter failed';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const createConfiguration = async (data: ConfigurationCreateRequest): Promise<Configuration | null> => {
    try {
      const newConfig = await configurationService.create(data);
      toast.success('Configuration created successfully');
      await refreshConfigurations();
      return newConfig;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to create configuration';
      toast.error(errorMessage);
      return null;
    }
  };

  const updateConfiguration = async (
    category: string,
    key: string,
    data: ConfigurationUpdateRequest
  ): Promise<Configuration | null> => {
    try {
      const updatedConfig = await configurationService.update(category, key, data);
      toast.success('Configuration updated successfully');
      await refreshConfigurations();
      return updatedConfig;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to update configuration';
      toast.error(errorMessage);
      return null;
    }
  };

  const deleteConfiguration = async (category: string, key: string): Promise<boolean> => {
    try {
      await configurationService.delete(category, key);
      toast.success('Configuration deleted successfully');
      await refreshConfigurations();
      return true;
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to delete configuration';
      toast.error(errorMessage);
      return false;
    }
  };

  useEffect(() => {
    refreshConfigurations();
  }, []);

  return {
    configurations,
    loading,
    error,
    createConfiguration,
    updateConfiguration,
    deleteConfiguration,
    refreshConfigurations,
    filterByCategory,
  };
}
