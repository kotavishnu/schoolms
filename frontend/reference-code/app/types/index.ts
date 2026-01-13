export interface Student {
  id: string;
  firstName: string;
  lastName: string;
  guardianName: string;
  motherName: string;
  phone: string;
  dateOfBirth: string; // ISO 8601 format (YYYY-MM-DD)
  adhaarNumber: string; // 12-digit unique identifier
  email: string;
  address: string;
  identificationMarks: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Configuration {
  id: string;
  category: string;
  key: string;
  value: string;
  description: string;
  lastUpdated: string;
}