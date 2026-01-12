/**
 * useConfigurations Hook
 * Manages configuration data fetching and operations
 */

import { useState, useEffect, useCallback } from 'react';
import { configurationService } from '../services/configurationService';
import type {
  Configuration,
  ConfigCategory,
  ConfigurationCreateDto,
  ConfigurationUpdateDto,
} from '../types';
import { ApiErrorClass } from '../types/api';

interface UseConfigurationsResult {
  configurations: Configuration[];
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  createConfiguration: (
    data: ConfigurationCreateDto
  ) => Promise<Configuration>;
  updateConfiguration: (
    id: string,
    data: ConfigurationUpdateDto
  ) => Promise<Configuration>;
  deleteConfiguration: (id: string) => Promise<void>;
}

export function useConfigurations(
  category?: ConfigCategory
): UseConfigurationsResult {
  const [configurations, setConfigurations] = useState<Configuration[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Fetch configurations with optional category filter
   */
  const fetchConfigurations = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await configurationService.getAll(category);
      setConfigurations(response.configurations);
    } catch (err) {
      const errorMessage =
        err instanceof ApiErrorClass
          ? err.message
          : 'Failed to fetch configurations';
      setError(errorMessage);
      console.error('Error fetching configurations:', err);
    } finally {
      setLoading(false);
    }
  }, [category]);

  /**
   * Create a new configuration
   */
  const createConfiguration = useCallback(
    async (data: ConfigurationCreateDto): Promise<Configuration> => {
      const newConfig = await configurationService.create(data);
      // Refresh the list after creation
      await fetchConfigurations();
      return newConfig;
    },
    [fetchConfigurations]
  );

  /**
   * Update an existing configuration
   */
  const updateConfiguration = useCallback(
    async (
      id: string,
      data: ConfigurationUpdateDto
    ): Promise<Configuration> => {
      const updatedConfig = await configurationService.update(id, data);
      // Update the configuration in the local list
      setConfigurations((prev) =>
        prev.map((config) => (config.id === id ? updatedConfig : config))
      );
      return updatedConfig;
    },
    []
  );

  /**
   * Delete a configuration
   */
  const deleteConfiguration = useCallback(async (id: string): Promise<void> => {
    await configurationService.delete(id);
    // Remove the configuration from the local list
    setConfigurations((prev) => prev.filter((config) => config.id !== id));
  }, []);

  /**
   * Fetch configurations on mount and when category changes
   */
  useEffect(() => {
    fetchConfigurations();
  }, [fetchConfigurations]);

  return {
    configurations,
    loading,
    error,
    refetch: fetchConfigurations,
    createConfiguration,
    updateConfiguration,
    deleteConfiguration,
  };
}

export default useConfigurations;
