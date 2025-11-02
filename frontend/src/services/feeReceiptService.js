import api from './api';

export const feeReceiptService = {
  // Generate fee receipt
  create: (data) => api.post('/fee-receipts', data),

  // Get receipt by ID
  getById: (id) => api.get(`/fee-receipts/${id}`),

  // Get receipt by receipt number
  getByReceiptNumber: (receiptNumber) => {
    return api.get(`/fee-receipts/number/${receiptNumber}`);
  },

  // Get all receipts
  getAll: () => api.get('/fee-receipts'),

  // Get receipts for specific student
  getByStudent: (studentId) => api.get(`/fee-receipts/student/${studentId}`),

  // Get receipts by date range
  getByDateRange: (startDate, endDate) => {
    return api.get('/fee-receipts/by-date', {
      params: { startDate, endDate }
    });
  },

  // Get receipts by payment method
  getByPaymentMethod: (paymentMethod) => {
    return api.get(`/fee-receipts/by-method/${paymentMethod}`);
  },

  // Get today's receipts
  getToday: () => api.get('/fee-receipts/today'),

  // Get total collection for date range
  getCollection: (startDate, endDate) => {
    return api.get('/fee-receipts/collection', {
      params: { startDate, endDate }
    });
  },

  // Get collection by payment method
  getCollectionByMethod: (startDate, endDate) => {
    return api.get('/fee-receipts/collection/by-method', {
      params: { startDate, endDate }
    });
  },

  // Get collection summary
  getCollectionSummary: (startDate, endDate) => {
    return api.get('/fee-receipts/collection/summary', {
      params: { startDate, endDate }
    });
  },

  // Count receipts for student
  countByStudent: (studentId) => {
    return api.get(`/fee-receipts/count/${studentId}`);
  },
};
