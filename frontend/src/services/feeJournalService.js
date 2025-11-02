import api from './api';

export const feeJournalService = {
  // Create fee journal entry
  create: (data) => api.post('/fee-journals', data),

  // Get fee journal by ID
  getById: (id) => api.get(`/fee-journals/${id}`),

  // Get all fee journals
  getAll: () => api.get('/fee-journals'),

  // Get journals for specific student
  getByStudent: (studentId) => api.get(`/fee-journals/student/${studentId}`),

  // Get pending entries for student
  getPendingByStudent: (studentId) => {
    return api.get(`/fee-journals/student/${studentId}/pending`);
  },

  // Get journals by month and year
  getByMonth: (month, year) => {
    return api.get('/fee-journals/by-month', {
      params: { month, year }
    });
  },

  // Get journals by payment status
  getByStatus: (status) => api.get(`/fee-journals/by-status/${status}`),

  // Get overdue journals
  getOverdue: () => api.get('/fee-journals/overdue'),

  // Get student dues summary
  getStudentSummary: (studentId) => {
    return api.get(`/fee-journals/student/${studentId}/summary`);
  },

  // Update fee journal
  update: (id, data) => api.put(`/fee-journals/${id}`, data),

  // Record payment
  recordPayment: (id, amount) => {
    return api.patch(`/fee-journals/${id}/payment`, { amount });
  },

  // Delete fee journal
  delete: (id) => api.delete(`/fee-journals/${id}`),
};
