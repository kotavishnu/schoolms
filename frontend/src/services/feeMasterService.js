import api from './api';

export const feeMasterService = {
  // Create fee master
  create: (data) => api.post('/fee-masters', data),

  // Get fee master by ID
  getById: (id) => api.get(`/fee-masters/${id}`),

  // Get all fee masters
  getAll: () => api.get('/fee-masters'),

  // Get fee masters by type
  getByType: (feeType) => api.get(`/fee-masters/by-type/${feeType}`),

  // Get active fee masters
  getActive: () => api.get('/fee-masters/active'),

  // Get currently applicable fee masters
  getApplicable: () => api.get('/fee-masters/applicable'),

  // Get latest applicable fee master by type
  getLatest: (feeType) => api.get(`/fee-masters/latest/${feeType}`),

  // Update fee master
  update: (id, data) => api.put(`/fee-masters/${id}`, data),

  // Deactivate fee master
  deactivate: (id) => api.patch(`/fee-masters/${id}/deactivate`),

  // Activate fee master
  activate: (id) => api.patch(`/fee-masters/${id}/activate`),

  // Delete fee master
  delete: (id) => api.delete(`/fee-masters/${id}`),

  // Count active fee masters
  count: () => api.get('/fee-masters/count'),
};
