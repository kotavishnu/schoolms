import api from './api';

export const studentService = {
  // Create a new student
  create: (data) => api.post('/students', data),

  // Get student by ID
  getById: (id) => api.get(`/students/${id}`),

  // Get all students (optional: filter by class)
  getAll: (classId = null) => {
    const params = classId ? { classId } : {};
    return api.get('/students', { params });
  },

  // Search students by name
  search: (query) => {
    return api.get('/students/search', {
      params: { q: query }
    });
  },

  // Autocomplete search for students
  autocomplete: (query) => {
    return api.get('/students/autocomplete', {
      params: { q: query }
    });
  },

  // Update student details
  update: (id, data) => api.put(`/students/${id}`, data),

  // Delete student
  delete: (id) => api.delete(`/students/${id}`),

  // Get students with pending fees
  getPendingFees: () => api.get('/students/pending-fees'),
};
