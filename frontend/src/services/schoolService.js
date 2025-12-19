import api from './api';

/**
 * School Service - Handles school configuration API calls
 */

// Get school configuration
export const getSchoolConfig = () => {
  return api.get('/school-config');
};

// Create or update school configuration
export const saveSchoolConfig = (data) => {
  return api.post('/school-config', data);
};

// Update school configuration
export const updateSchoolConfig = (data) => {
  return api.put('/school-config', data);
};

export default {
  getSchoolConfig,
  saveSchoolConfig,
  updateSchoolConfig
};
