import type {
  Configuration,
  ConfigurationCreateRequest,
  ConfigurationUpdateRequest,
  ConfigCategory,
} from '@/types/configuration';

const CONFIG_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082';

// Create separate axios instance for configuration service
import axios from 'axios';
const configApiClient = axios.create({
  baseURL: CONFIG_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add same interceptors
configApiClient.interceptors.request.use(
  (config) => {
    const correlationId = crypto.randomUUID();
    config.headers['X-Correlation-ID'] = correlationId;
    console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => Promise.reject(error)
);

configApiClient.interceptors.response.use(
  (response) => response,
  (error: any) => {
    if (error.response) {
      const apiError = {
        code: error.response.data?.code || 'UNKNOWN_ERROR',
        message: error.response.data?.message || 'An error occurred',
        details: error.response.data?.details,
        timestamp: error.response.data?.timestamp,
        path: error.response.data?.path,
      };
      console.error(`API Error [${error.response.status}]:`, apiError);
      return Promise.reject(apiError);
    }
    return Promise.reject({
      code: 'NETWORK_ERROR',
      message: 'Network error. Please check your connection.',
    });
  }
);

export const configurationService = {
  /**
   * Get all configurations with optional category filter
   */
  async getAll(category?: ConfigCategory): Promise<Configuration[]> {
    const response = await configApiClient.get<Configuration[]>('/api/v1/configurations', {
      params: category ? { category } : undefined,
    });
    return response.data;
  },

  /**
   * Get configuration by ID
   */
  async getById(id: string): Promise<Configuration> {
    const response = await configApiClient.get<Configuration>(`/api/v1/configurations/${id}`);
    return response.data;
  },

  /**
   * Get configuration by category and key
   */
  async getByCategoryAndKey(category: string, key: string): Promise<Configuration> {
    const response = await configApiClient.get<Configuration>(
      `/api/v1/configurations/${category}/${key}`
    );
    return response.data;
  },

  /**
   * Create new configuration
   */
  async create(data: ConfigurationCreateRequest): Promise<Configuration> {
    const response = await configApiClient.post<Configuration>('/api/v1/configurations', data);
    return response.data;
  },

  /**
   * Update configuration (upsert pattern)
   */
  async update(category: string, key: string, data: ConfigurationUpdateRequest): Promise<Configuration> {
    const response = await configApiClient.put<Configuration>(
      `/api/v1/configurations/${category}/${key}`,
      data
    );
    return response.data;
  },

  /**
   * Delete configuration
   */
  async delete(category: string, key: string): Promise<void> {
    await configApiClient.delete(`/api/v1/configurations/${category}/${key}`);
  },

  /**
   * Get configurations grouped by category
   */
  async getGrouped(category?: string): Promise<Record<string, Configuration[]>> {
    const response = await configApiClient.get<Record<string, Configuration[]>>(
      `/api/v1/configurations/grouped/${category || 'all'}`
    );
    return response.data;
  },
};
