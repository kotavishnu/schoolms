import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add any auth tokens here if needed in future
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    // Extract data from the standard response format
    return response.data;
  },
  (error) => {
    // Handle different error scenarios
    if (error.response) {
      // Server responded with error status
      const errorData = error.response.data;

      // Create a user-friendly error object
      const customError = {
        message: errorData.message || 'An error occurred',
        errors: errorData.errors || {},
        status: error.response.status,
        timestamp: errorData.timestamp,
      };

      return Promise.reject(customError);
    } else if (error.request) {
      // Request made but no response received
      return Promise.reject({
        message: 'No response from server. Please check your connection.',
        errors: {},
        status: 0,
      });
    } else {
      // Error in request configuration
      return Promise.reject({
        message: error.message || 'Request failed',
        errors: {},
        status: 0,
      });
    }
  }
);

export default api;
