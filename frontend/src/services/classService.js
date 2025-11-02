import api from './api';

export const classService = {
  // Create a new class
  create: (data) => api.post('/classes', data),

  // Get class by ID
  getById: (id) => api.get(`/classes/${id}`),

  // Get all classes (optional: filter by academic year)
  getAll: (academicYear = null) => {
    const params = academicYear ? { academicYear } : {};
    return api.get('/classes', { params });
  },

  // Get classes by class number
  getByNumber: (classNumber, academicYear) => {
    return api.get('/classes/by-number', {
      params: { classNumber, academicYear }
    });
  },

  // Get classes with available seats
  getAvailable: () => api.get('/classes/available'),

  // Get almost full classes (>80% capacity)
  getAlmostFull: () => api.get('/classes/almost-full'),

  // Get total students for academic year
  getTotalStudents: (academicYear) => {
    return api.get('/classes/total-students', {
      params: { academicYear }
    });
  },

  // Update class details
  update: (id, data) => api.put(`/classes/${id}`, data),

  // Delete class
  delete: (id) => api.delete(`/classes/${id}`),

  // Check if class exists
  exists: (classNumber, section, academicYear) => {
    return api.get('/classes/exists', {
      params: { classNumber, section, academicYear }
    });
  },
};
