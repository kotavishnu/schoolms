import api from './api';

/**
 * Class Service - Handles all class-related API calls
 */

// Get all classes
export const getAllClasses = () => {
  return api.get('/classes');
};

// Get a class by ID
export const getClass = (id) => {
  return api.get(`/classes/${id}`);
};

// Create a new class
export const createClass = (data) => {
  return api.post('/classes', data);
};

// Update a class
export const updateClass = (id, data) => {
  return api.put(`/classes/${id}`, data);
};

// Delete a class
export const deleteClass = (id) => {
  return api.delete(`/classes/${id}`);
};

// Get students in a class
export const getClassStudents = (classId) => {
  return api.get(`/classes/${classId}/students`);
};

export default {
  getAllClasses,
  getClass,
  createClass,
  updateClass,
  deleteClass,
  getClassStudents
};
