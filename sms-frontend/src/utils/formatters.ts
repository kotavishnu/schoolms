/**
 * Format phone number for display
 */
export const formatPhoneNumber = (phone: string): string => {
  // Remove any non-digit characters
  const cleaned = phone.replace(/\D/g, '');

  // Format as +91 98765 43210 for Indian numbers
  if (cleaned.length === 10) {
    return `+91 ${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
  }

  // Return original if doesn't match expected format
  return phone;
};

/**
 * Format student ID for display
 */
export const formatStudentId = (id: string): string => {
  return id; // Already formatted as STU-YYYY-XXXXX
};

/**
 * Capitalize first letter of each word
 */
export const capitalize = (text: string): string => {
  return text.replace(/\b\w/g, (char) => char.toUpperCase());
};

/**
 * Truncate text to max length with ellipsis
 */
export const truncate = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + '...';
};

/**
 * Format Aadhaar number with masking (show only last 4 digits)
 */
export const formatAadhaar = (aadhaar: string): string => {
  if (aadhaar.length !== 12) return aadhaar;
  return `XXXX XXXX ${aadhaar.slice(-4)}`;
};
