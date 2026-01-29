import { configApiClient } from './client';

// DTOs
export interface Configuration {
  id: number;
  category: 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';
  key: string;
  value: string;
  description?: string;
  dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  isEncrypted: boolean;
  version: number;
  updatedAt: string;
}

export interface ConfigurationRequest {
  value: string;
  description?: string;
  dataType?: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  isEncrypted?: boolean;
}

export interface ConfigurationsResponse {
  configurations: Configuration[];
}

export interface GroupedConfigResponse {
  category: string;
  settings: Record<string, string>;
}

// API Methods
class ConfigurationApiService {
  /**
   * Get all configurations, optionally filtered by category
   */
  async getAllConfigurations(category?: string): Promise<Configuration[]> {
    const response = await configApiClient.get<Configuration[]>('/configurations', {
      params: category ? { category } : {},
    });
    return response.data;
  }

  /**
   * Get a specific configuration by category and key
   */
  async getConfiguration(category: string, key: string): Promise<Configuration> {
    const response = await configApiClient.get<Configuration>(
      `/configurations/${category}/${key}`
    );
    return response.data;
  }

  /**
   * Create or update a configuration (upsert)
   */
  async upsertConfiguration(
    category: string,
    key: string,
    data: ConfigurationRequest,
    version?: number
  ): Promise<Configuration> {
    const headers: Record<string, string> = {};
    if (version !== undefined) {
      headers['If-Match'] = version.toString();
    }

    const response = await configApiClient.put<Configuration>(
      `/configurations/${category}/${key}`,
      data,
      { headers }
    );
    return response.data;
  }

  /**
   * Delete a configuration
   */
  async deleteConfiguration(category: string, key: string): Promise<void> {
    await configApiClient.delete(`/configurations/${category}/${key}`);
  }

  /**
   * Get grouped configurations by category (key-value pairs)
   */
  async getGroupedConfigurations(category: string): Promise<GroupedConfigResponse> {
    const response = await configApiClient.get<GroupedConfigResponse>(
      `/configurations/grouped/${category}`
    );
    return response.data;
  }

  /**
   * Bulk update configurations for a category
   */
  async bulkUpdateConfigurations(
    category: string,
    configurations: Array<{ key: string; value: string }>
  ): Promise<Configuration[]> {
    const promises = configurations.map((config) =>
      this.upsertConfiguration(category, config.key, {
        value: config.value,
        dataType: 'STRING',
      })
    );

    await Promise.all(promises);
    return this.getAllConfigurations(category);
  }
}

export const configApi = new ConfigurationApiService();
