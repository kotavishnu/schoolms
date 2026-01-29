import axios, { type AxiosInstance, type AxiosError } from 'axios';
import { toast } from 'sonner';

// Environment variables with defaults
const STUDENT_API_BASE_URL = import.meta.env.VITE_STUDENT_API_URL || 'http://localhost:8081/api/v1';
const CONFIG_API_BASE_URL = import.meta.env.VITE_CONFIG_API_URL || 'http://localhost:8082/api/v1';

// Utility to generate correlation ID
const generateCorrelationId = (): string => {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 15)}`;
};

// Create Axios instance for Student API
export const studentApiClient: AxiosInstance = axios.create({
  baseURL: STUDENT_API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create Axios instance for Configuration API
export const configApiClient: AxiosInstance = axios.create({
  baseURL: CONFIG_API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add correlation ID and timestamp logging
const requestInterceptor = (config: any) => {
  const correlationId = generateCorrelationId();
  config.headers['X-Correlation-ID'] = correlationId;

  console.log(`[${new Date().toISOString()}] ${config.method?.toUpperCase()} ${config.url}`, {
    correlationId,
    data: config.data,
  });

  return config;
};

// Response error interceptor - Handle errors globally
const responseErrorInterceptor = (error: AxiosError) => {
  const status = error.response?.status;
  const data = error.response?.data as any;

  console.error(`[${new Date().toISOString()}] API Error:`, {
    status,
    url: error.config?.url,
    data,
  });

  // Handle specific error codes
  switch (status) {
    case 400:
      // Bad Request - show validation errors
      if (data?.errors && Array.isArray(data.errors)) {
        data.errors.forEach((err: any) => {
          toast.error(err.message || err);
        });
      } else if (data?.detail) {
        toast.error(data.detail);
      } else {
        toast.error('Invalid request. Please check your input.');
      }
      break;

    case 401:
      toast.error('Unauthorized. Please log in.');
      break;

    case 403:
      toast.error('You do not have permission to perform this action.');
      break;

    case 404:
      toast.error('Resource not found.');
      break;

    case 409:
      // Conflict - typically duplicate resource
      if (data?.detail) {
        toast.error(data.detail);
      } else {
        toast.error('Resource already exists.');
      }
      break;

    case 500:
      toast.error('Server error. Please try again later.');
      break;

    default:
      if (error.code === 'ECONNABORTED') {
        toast.error('Request timeout. Please try again.');
      } else if (error.message === 'Network Error') {
        toast.error('Network error. Please check your connection.');
      } else {
        toast.error('An unexpected error occurred.');
      }
  }

  return Promise.reject(error);
};

// Apply interceptors to Student API client
studentApiClient.interceptors.request.use(requestInterceptor);
studentApiClient.interceptors.response.use(
  (response) => response,
  responseErrorInterceptor
);

// Apply interceptors to Configuration API client
configApiClient.interceptors.request.use(requestInterceptor);
configApiClient.interceptors.response.use(
  (response) => response,
  responseErrorInterceptor
);

export { STUDENT_API_BASE_URL, CONFIG_API_BASE_URL };
