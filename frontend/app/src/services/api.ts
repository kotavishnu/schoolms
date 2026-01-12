/**
 * HTTP Client Configuration
 * School Management System - Phase 1
 */

import axios, { AxiosError } from 'axios';
import type { AxiosInstance } from 'axios';
import { ApiErrorClass } from '../types/api';

/**
 * Main API client for Student Service
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * API client for Configuration Service
 */
export const configApiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_CONFIG_API_BASE_URL || 'http://localhost:8082',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Generate a unique request ID for tracing
 */
function generateRequestId(): string {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
}

/**
 * Request interceptor for both clients
 */
const requestInterceptor = (config: any) => {
  // Add request ID for tracing
  config.headers['X-Request-ID'] = generateRequestId();

  // Future: Add authentication token
  // const token = localStorage.getItem('authToken');
  // if (token) {
  //   config.headers.Authorization = `Bearer ${token}`;
  // }

  return config;
};

/**
 * Response error interceptor for both clients
 */
const responseErrorInterceptor = (error: AxiosError) => {
  if (error.response) {
    // Server responded with error
    const status = error.response.status;
    const data = error.response.data as any;

    // Log specific status codes
    if (status === 401) {
      console.warn('Unauthorized access - authentication required');
      // Future: Redirect to login
    } else if (status === 500) {
      console.error('Server error:', error);
    }

    // Extract error message from RFC 7807 Problem Detail or fallback
    const message =
      data?.detail ||
      data?.message ||
      data?.title ||
      'An error occurred while processing your request';

    throw new ApiErrorClass(
      message,
      status,
      data?.errors || data?.details,
      data?.type || `HTTP_${status}`
    );
  } else if (error.request) {
    // Network error - no response received
    console.error('Network error:', error);
    throw new ApiErrorClass(
      'Network error. Please check your internet connection and try again.',
      0
    );
  } else {
    // Something else happened
    console.error('Request setup error:', error);
    throw new ApiErrorClass('An unexpected error occurred', 0);
  }
};

// Apply interceptors to both clients
apiClient.interceptors.request.use(requestInterceptor, (error) =>
  Promise.reject(error)
);
apiClient.interceptors.response.use((response) => response, responseErrorInterceptor);

configApiClient.interceptors.request.use(requestInterceptor, (error) =>
  Promise.reject(error)
);
configApiClient.interceptors.response.use(
  (response) => response,
  responseErrorInterceptor
);

export default apiClient;
