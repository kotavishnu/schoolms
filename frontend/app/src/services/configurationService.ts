/**
 * Configuration Service - API Integration
 * School Management System - Phase 1
 */

import { configApiClient } from './api';
import type {
  Configuration,
  ConfigCategory,
  ConfigurationCreateDto,
  ConfigurationUpdateDto,
  ConfigurationListResponse,
} from '../types';

/**
 * Configuration Service
 * Encapsulates all Configuration API operations
 */
export const configurationService = {
  /**
   * Get all configurations with optional category filter
   * GET /api/v1/configurations
   */
  getAll: async (category?: ConfigCategory): Promise<ConfigurationListResponse> => {
    const params: any = {};

    if (category) {
      params.category = category;
    }

    const response = await configApiClient.get<ConfigurationListResponse>(
      '/api/v1/configurations',
      { params }
    );

    return response.data;
  },

  /**
   * Get configurations by category
   * GET /api/v1/configurations?category={category}
   */
  getByCategory: async (category: ConfigCategory): Promise<Configuration[]> => {
    const response = await configApiClient.get<Configuration[]>(
      '/api/v1/configurations',
      {
        params: { category },
      }
    );
    return response.data;
  },

  /**
   * Get configuration by ID
   * GET /api/v1/configurations/{id}
   */
  getById: async (id: string): Promise<Configuration> => {
    const response = await configApiClient.get<Configuration>(
      `/api/v1/configurations/${id}`
    );
    return response.data;
  },

  /**
   * Create a new configuration
   * POST /api/v1/configurations
   */
  create: async (config: ConfigurationCreateDto): Promise<Configuration> => {
    const response = await configApiClient.post<Configuration>(
      '/api/v1/configurations',
      config
    );
    return response.data;
  },

  /**
   * Update an existing configuration
   * PATCH /api/v1/configurations/{id}
   */
  update: async (
    id: string,
    updates: ConfigurationUpdateDto
  ): Promise<Configuration> => {
    const response = await configApiClient.patch<Configuration>(
      `/api/v1/configurations/${id}`,
      updates
    );
    return response.data;
  },

  /**
   * Delete a configuration
   * DELETE /api/v1/configurations/{id}
   */
  delete: async (id: string): Promise<void> => {
    await configApiClient.delete(`/api/v1/configurations/${id}`);
  },
};

export default configurationService;
