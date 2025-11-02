// Mobile number validation
export const validateMobile = (mobile) => {
  if (!mobile) return 'Mobile number is required';

  const mobileRegex = /^[6-9]\d{9}$/;
  if (!mobileRegex.test(mobile)) {
    return 'Mobile number must be 10 digits and start with 6-9';
  }

  return null;
};

// Age validation (3-18 years)
export const validateAge = (dateOfBirth) => {
  if (!dateOfBirth) return 'Date of birth is required';

  const dob = new Date(dateOfBirth);
  const today = new Date();

  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
    age--;
  }

  if (age < 3 || age > 18) {
    return 'Student age must be between 3 and 18 years';
  }

  return null;
};

// Future date validation
export const validateNotFutureDate = (date, fieldName = 'Date') => {
  if (!date) return `${fieldName} is required`;

  // Normalize both dates to compare only date parts (not time)
  const selectedDate = new Date(date);
  selectedDate.setHours(0, 0, 0, 0);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (selectedDate > today) {
    return `${fieldName} cannot be in the future`;
  }

  return null;
};

// Past or present date validation
export const validatePastOrPresentDate = (date, fieldName = 'Date') => {
  if (!date) return `${fieldName} is required`;

  const selectedDate = new Date(date);
  const today = new Date();
  today.setHours(23, 59, 59, 999);

  if (selectedDate > today) {
    return `${fieldName} must be a past or present date`;
  }

  return null;
};

// Future date validation (must be future)
export const validateFutureDate = (date, fieldName = 'Date') => {
  if (!date) return `${fieldName} is required`;

  const selectedDate = new Date(date);
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (selectedDate <= today) {
    return `${fieldName} must be a future date`;
  }

  return null;
};

// Positive number validation
export const validatePositiveNumber = (value, fieldName = 'Value') => {
  if (value === null || value === undefined || value === '') {
    return `${fieldName} is required`;
  }

  const num = Number(value);
  if (isNaN(num) || num <= 0) {
    return `${fieldName} must be a positive number`;
  }

  return null;
};

// Non-negative number validation
export const validateNonNegativeNumber = (value, fieldName = 'Value') => {
  if (value === null || value === undefined || value === '') {
    return `${fieldName} is required`;
  }

  const num = Number(value);
  if (isNaN(num) || num < 0) {
    return `${fieldName} must be a non-negative number`;
  }

  return null;
};

// Academic year format validation (YYYY-YYYY)
export const validateAcademicYear = (academicYear) => {
  if (!academicYear) return 'Academic year is required';

  const regex = /^\d{4}-\d{4}$/;
  if (!regex.test(academicYear)) {
    return 'Academic year must be in format YYYY-YYYY';
  }

  const [startYear, endYear] = academicYear.split('-').map(Number);
  if (endYear !== startYear + 1) {
    return 'Academic year end must be one year after start';
  }

  return null;
};

// Year validation (2000-2100)
export const validateYear = (year) => {
  if (!year) return 'Year is required';

  const yearNum = Number(year);
  if (isNaN(yearNum) || yearNum < 2000 || yearNum > 2100) {
    return 'Year must be between 2000 and 2100';
  }

  return null;
};

// Class number validation (1-10)
export const validateClassNumber = (classNumber) => {
  if (!classNumber) return 'Class number is required';

  const num = Number(classNumber);
  if (isNaN(num) || num < 1 || num > 10) {
    return 'Class number must be between 1 and 10';
  }

  return null;
};

// String length validation
export const validateMaxLength = (value, maxLength, fieldName = 'Field') => {
  if (!value) return null;

  if (value.length > maxLength) {
    return `${fieldName} must not exceed ${maxLength} characters`;
  }

  return null;
};

// Required field validation
export const validateRequired = (value, fieldName = 'Field') => {
  if (!value || (typeof value === 'string' && value.trim() === '')) {
    return `${fieldName} is required`;
  }

  return null;
};

// Email validation (if needed in future)
export const validateEmail = (email) => {
  if (!email) return null; // Email is optional

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return 'Invalid email format';
  }

  return null;
};
