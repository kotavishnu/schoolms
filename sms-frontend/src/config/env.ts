export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1',
  configServiceUrl: import.meta.env.VITE_CONFIG_SERVICE_URL || 'http://localhost:8082/api/v1',
  appName: import.meta.env.VITE_APP_NAME || 'School Management System',
  appVersion: import.meta.env.VITE_APP_VERSION || '1.0.0',
  environment: import.meta.env.VITE_ENV || 'development',
  isDevelopment: import.meta.env.DEV,
  isProduction: import.meta.env.PROD,
} as const;

export default env;
