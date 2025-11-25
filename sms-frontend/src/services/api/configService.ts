import axios, { AxiosError, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios';
import { v4 as uuidv4 } from 'uuid';
import { type ProblemDetails } from '@/types/api.types';
import env from '@/config/env';
import {
  type Configuration,
  type CreateConfigRequest,
  type UpdateConfigRequest,
  type ConfigurationPageResponse,
} from '@/types/config.types';

// Create a separate API client for configuration service (port 8082)
const configApiClient = axios.create({
  baseURL: env.configServiceUrl,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request interceptor - Add correlation ID
configApiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const correlationId = uuidv4();
    config.headers.set('X-Correlation-ID', correlationId);

    if (env.isDevelopment) {
      console.log(`[${correlationId}] GET ${config.url}`);
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors globally
configApiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    const correlationId = response.config.headers['X-Correlation-ID'];

    if (env.isDevelopment) {
      console.log(`[${correlationId}] Response: ${response.status}`);
    }

    return response;
  },
  (error: AxiosError<ProblemDetails>) => {
    const correlationId = error.config?.headers?.['X-Correlation-ID'];

    if (env.isDevelopment) {
      console.error(`[${correlationId}] Error: ${error.message}`, error.response?.data);
    }

    // Handle specific error codes
    if (error.response) {
      const problemDetails = error.response.data;

      // Log the problem details for debugging
      if (env.isDevelopment && problemDetails) {
        console.error('Problem Details:', {
          type: problemDetails.type,
          title: problemDetails.title,
          detail: problemDetails.detail,
          status: problemDetails.status,
          errors: problemDetails.errors,
        });
      }
    }

    return Promise.reject(error);
  }
);

const CONFIG_BASE_URL = '/configurations';

export const configService = {
  /**
   * Create a new configuration
   */
  async createConfiguration(data: CreateConfigRequest): Promise<Configuration> {
    const response = await configApiClient.post<Configuration>(CONFIG_BASE_URL, data);
    return response.data;
  },

  /**
   * Get all configurations with pagination
   */
  async getAllConfigurations(page: number = 0, size: number = 50): Promise<ConfigurationPageResponse> {
    const response = await configApiClient.get<ConfigurationPageResponse>(CONFIG_BASE_URL, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get configurations by category
   */
  async getConfigurationsByCategory(category: string): Promise<Configuration[]> {
    const response = await configApiClient.get<Configuration[]>(
      `${CONFIG_BASE_URL}/category/${category}`
    );
    return response.data;
  },

  /**
   * Update configuration
   */
  async updateConfiguration(id: number, data: UpdateConfigRequest): Promise<Configuration> {
    const response = await configApiClient.put<Configuration>(`${CONFIG_BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Delete configuration
   */
  async deleteConfiguration(id: number): Promise<void> {
    await configApiClient.delete(`${CONFIG_BASE_URL}/${id}`);
  },
};

export default configService;
