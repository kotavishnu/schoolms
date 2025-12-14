import axios, { AxiosError, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios';
import { v4 as uuidv4 } from 'uuid';
import { type ProblemDetails } from '@/types/api.types';
import env from '@/config/env';

const API_BASE_URL = env.apiBaseUrl;

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request interceptor - Add correlation ID
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const correlationId = uuidv4();
    config.headers.set('X-Correlation-ID', correlationId);

    if (env.isDevelopment) {
      console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors globally
apiClient.interceptors.response.use(
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

export default apiClient;

export type { ProblemDetails };
