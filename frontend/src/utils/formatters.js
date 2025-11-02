import { format, parseISO } from 'date-fns';

// Format date to readable string
export const formatDate = (dateString, formatStr = 'dd MMM yyyy') => {
  if (!dateString) return '-';

  try {
    const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
    return format(date, formatStr);
  } catch (error) {
    return dateString;
  }
};

// Format date to ISO string for input fields (YYYY-MM-DD)
export const formatDateForInput = (dateString) => {
  if (!dateString) return '';

  try {
    const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
    return format(date, 'yyyy-MM-dd');
  } catch (error) {
    return '';
  }
};

// Format currency (Indian Rupees)
export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return '-';

  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(amount);
};

// Format number with commas
export const formatNumber = (num) => {
  if (num === null || num === undefined) return '-';

  return new Intl.NumberFormat('en-IN').format(num);
};

// Format phone number (add spaces for readability)
export const formatPhoneNumber = (phone) => {
  if (!phone) return '-';

  // Format as: +91 XXXXX XXXXX
  if (phone.length === 10) {
    return `+91 ${phone.slice(0, 5)} ${phone.slice(5)}`;
  }

  return phone;
};

// Format status text (capitalize first letter)
export const formatStatus = (status) => {
  if (!status) return '-';

  return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
};

// Get initials from name
export const getInitials = (firstName, lastName) => {
  const first = firstName ? firstName.charAt(0).toUpperCase() : '';
  const last = lastName ? lastName.charAt(0).toUpperCase() : '';
  return `${first}${last}`;
};

// Format full name
export const formatFullName = (firstName, lastName) => {
  const parts = [];
  if (firstName) parts.push(firstName);
  if (lastName) parts.push(lastName);
  return parts.join(' ') || '-';
};

// Truncate text with ellipsis
export const truncateText = (text, maxLength = 50) => {
  if (!text) return '-';
  if (text.length <= maxLength) return text;
  return `${text.substring(0, maxLength)}...`;
};

// Format percentage
export const formatPercentage = (value, decimals = 1) => {
  if (value === null || value === undefined) return '-';
  return `${Number(value).toFixed(decimals)}%`;
};

// Format enum values to readable text
export const formatEnumValue = (value) => {
  if (!value) return '-';

  return value
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
};

// Parse date from input (handles both string and Date objects)
export const parseDate = (dateValue) => {
  if (!dateValue) return null;

  if (dateValue instanceof Date) {
    return dateValue;
  }

  try {
    return parseISO(dateValue);
  } catch (error) {
    return null;
  }
};

// Calculate age from date of birth
export const calculateAge = (dateOfBirth) => {
  if (!dateOfBirth) return null;

  const dob = typeof dateOfBirth === 'string' ? parseISO(dateOfBirth) : dateOfBirth;
  const today = new Date();

  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
    age--;
  }

  return age;
};
