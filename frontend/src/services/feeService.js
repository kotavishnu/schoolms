import api from './api';

/**
 * Fee Service - Handles all fee-related API calls
 */

// Get fee master configuration
export const getFeeMaster = () => {
  return api.get('/fee-masters');
};

// Create fee master configuration
export const createFeeMaster = (data) => {
  return api.post('/fee-masters', data);
};

// Update fee master configuration
export const updateFeeMaster = (id, data) => {
  return api.put(`/fee-masters/${id}`, data);
};

// Delete fee master configuration
export const deleteFeeMaster = (id) => {
  return api.delete(`/fee-masters/${id}`);
};

// Calculate fee for a student
export const calculateFee = (studentId, params) => {
  return api.get(`/fees/calculate/${studentId}`, { params });
};

// Get fee journal for a student
export const getFeeJournal = (studentId) => {
  return api.get(`/fees/journal/${studentId}`);
};

// Record fee payment
export const recordPayment = (data) => {
  return api.post('/fee-receipts', data);
};

// Generate fee receipt
export const generateReceipt = (paymentId) => {
  return api.get(`/fee-receipts/${paymentId}`, {
    responseType: 'blob' // For PDF download
  });
};

// Get pending fees for a student
export const getPendingFees = (studentId) => {
  return api.get(`/fees/pending/${studentId}`);
};

// Get all payments
export const getAllPayments = (params) => {
  return api.get('/fees/payments', { params });
};

export default {
  getFeeMaster,
  createFeeMaster,
  updateFeeMaster,
  deleteFeeMaster,
  calculateFee,
  getFeeJournal,
  recordPayment,
  generateReceipt,
  getPendingFees,
  getAllPayments
};
