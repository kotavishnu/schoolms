import api from './api';

export const schoolConfigService = {
  // Create configuration
  create: (data) => api.post('/school-config', data),

  // Get config by ID
  getById: (id) => api.get(`/school-config/${id}`),

  // Get config by key
  getByKey: (key) => api.get(`/school-config/key/${key}`),

  // Get config value only
  getValue: (key) => api.get(`/school-config/value/${key}`),

  // Get all configs (optional: filter by category)
  getAll: (category = null) => {
    const params = category ? { category } : {};
    return api.get('/school-config', { params });
  },

  // Get editable configs only
  getEditable: () => api.get('/school-config/editable'),

  // Update configuration
  update: (id, data) => api.put(`/school-config/${id}`, data),

  // Update config value only
  updateValue: (key, value) => {
    return api.patch(`/school-config/${key}`, { value });
  },

  // Delete configuration
  delete: (id) => api.delete(`/school-config/${id}`),

  // Check if config key exists
  exists: (key) => api.get(`/school-config/exists/${key}`),
};
