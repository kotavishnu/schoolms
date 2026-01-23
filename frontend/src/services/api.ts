import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import type { ApiError } from '@/types/api';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Add correlation ID
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const correlationId = crypto.randomUUID();
    config.headers['X-Correlation-ID'] = correlationId;
    console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response) {
      const apiError: ApiError = {
        code: error.response.data?.code || 'UNKNOWN_ERROR',
        message: error.response.data?.message || 'An error occurred',
        details: error.response.data?.details,
        timestamp: error.response.data?.timestamp,
        path: error.response.data?.path,
      };
      console.error(`API Error [${error.response.status}]:`, apiError);
      return Promise.reject(apiError);
    } else if (error.request) {
      const networkError: ApiError = {
        code: 'NETWORK_ERROR',
        message: 'Network error. Please check your connection.',
      };
      console.error('Network Error:', error.message);
      return Promise.reject(networkError);
    }
    return Promise.reject({
      code: 'UNKNOWN_ERROR',
      message: 'An unexpected error occurred',
    } as ApiError);
  }
);

export default apiClient;
