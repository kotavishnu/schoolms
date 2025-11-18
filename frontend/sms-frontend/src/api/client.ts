import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ProblemDetail } from '@/types/error';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Add X-User-ID header (default to SYSTEM for now, will be replaced with auth later)
    config.headers['X-User-ID'] = config.headers['X-User-ID'] || 'SYSTEM';

    // Add X-Request-ID for tracing
    config.headers['X-Request-ID'] = generateRequestId();

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
  (error: AxiosError<ProblemDetail>) => {
    // Handle different error types
    if (error.response) {
      // Server responded with error status
      const problemDetail = error.response.data;

      console.error('API Error:', {
        status: error.response.status,
        problemDetail,
      });

      // Return structured error
      return Promise.reject({
        problemDetail,
        statusCode: error.response.status,
      });
    } else if (error.request) {
      // Request was made but no response
      console.error('Network Error:', error.message);
      return Promise.reject({
        problemDetail: {
          type: 'https://api.school.com/problems/network-error',
          title: 'Network Error',
          status: 0,
          detail: 'Unable to connect to the server. Please check your connection.',
          timestamp: new Date().toISOString(),
        },
        statusCode: 0,
      });
    } else {
      // Something else happened
      console.error('Error:', error.message);
      return Promise.reject({
        problemDetail: {
          type: 'https://api.school.com/problems/unknown-error',
          title: 'Unknown Error',
          status: 0,
          detail: error.message,
          timestamp: new Date().toISOString(),
        },
        statusCode: 0,
      });
    }
  }
);

// Helper function to generate request ID
function generateRequestId(): string {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

export default apiClient;
