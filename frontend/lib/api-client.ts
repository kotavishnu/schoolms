import axios, { AxiosError } from 'axios';
import { ApiError } from '@/types';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8081/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Generate correlation ID for request tracking
    const correlationId = `web-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
    config.headers['X-Correlation-ID'] = correlationId;

    // Add authentication token when available (Phase 2)
    // const token = getAuthToken();
    // if (token) {
    //   config.headers['Authorization'] = `Bearer ${token}`;
    // }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error: AxiosError<ApiError>) => {
    // Handle RFC 7807 error format
    if (error.response?.data) {
      const problemDetails = error.response.data;

      // Create a more user-friendly error object
      const apiError: ApiError = {
        type: problemDetails.type || 'unknown-error',
        title: problemDetails.title || 'An error occurred',
        status: problemDetails.status || error.response.status,
        detail: problemDetails.detail || error.message,
        instance: problemDetails.instance || '',
        correlationId: problemDetails.correlationId || '',
        timestamp: problemDetails.timestamp || new Date().toISOString(),
        errors: problemDetails.errors || [],
      };

      return Promise.reject(apiError);
    }

    // Network error or timeout
    if (error.code === 'ECONNABORTED' || error.message === 'Network Error') {
      const networkError: ApiError = {
        type: 'network-error',
        title: 'Network Error',
        status: 0,
        detail: 'Unable to connect to the server. Please check your internet connection.',
        instance: '',
        correlationId: '',
        timestamp: new Date().toISOString(),
      };
      return Promise.reject(networkError);
    }

    return Promise.reject(error);
  }
);

export default apiClient;

// Helper function to extract error message
export function getErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null && 'detail' in error) {
    return (error as ApiError).detail;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'An unexpected error occurred';
}

// Helper function to extract field errors
export function getFieldErrors(error: unknown): Record<string, string> {
  const fieldErrors: Record<string, string> = {};

  if (typeof error === 'object' && error !== null && 'errors' in error) {
    const apiError = error as ApiError;
    if (apiError.errors && Array.isArray(apiError.errors)) {
      apiError.errors.forEach((fieldError) => {
        fieldErrors[fieldError.field] = fieldError.message;
      });
    }
  }

  return fieldErrors;
}
