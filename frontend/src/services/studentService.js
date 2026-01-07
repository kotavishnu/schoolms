import api from './api';

/**
 * Student Service - Handles all student-related API calls
 */

// Create a new student
export const createStudent = (data) => {
  return api.post('/students', data);
};

// Get a student by ID
export const getStudent = (id) => {
  return api.get(`/students/${id}`);
};

// Get all students
export const getAllStudents = (params) => {
  return api.get('/students', { params });
};

// Update a student
export const updateStudent = (id, data) => {
  return api.put(`/students/${id}`, data);
};

// Delete a student
export const deleteStudent = (id) => {
  return api.delete(`/students/${id}`);
};

// Search students by name
export const searchStudents = (query) => {
  return api.get(`/students/search`, { params: { q: query } });
};

// Get students by class
export const getStudentsByClass = (classId) => {
  return api.get(`/students/class/${classId}`);
};

export default {
  createStudent,
  getStudent,
  getAllStudents,
  updateStudent,
  deleteStudent,
  searchStudents,
  getStudentsByClass
};
