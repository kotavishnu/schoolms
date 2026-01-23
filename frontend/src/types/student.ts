export interface Student {
  // Identity (Frontend uses 'id', backend uses 'studentId')
  id: string;

  // Personal Information
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 format
  age: number; // Calculated from DOB
  adhaarNumber: string; // Frontend spelling (backend: aadhaarNumber)
  identificationMarks?: string;
  address: string;

  // Guardian Information
  guardianName: string;
  motherName: string;

  // Contact (Frontend uses 'phone', backend uses 'mobile')
  phone: string;
  email: string;

  // Status
  status: 'ACTIVE' | 'INACTIVE';

  // Metadata
  createdAt: string;
  updatedAt: string;
}

export interface StudentCreateRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  adhaarNumber: string;
  identificationMarks?: string;
  address: string;
  guardianName: string;
  motherName: string;
  phone: string;
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  phone: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
}

export type StudentStatus = 'ACTIVE' | 'INACTIVE';

// Backend DTOs (for field mapping in service layer)
export interface StudentBackendDTO {
  studentId: string; // Maps to frontend 'id'
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  age: number;
  aadhaarNumber: string; // Backend spelling (frontend: adhaarNumber)
  identificationMarks?: string;
  address: string;
  guardianName: string;
  motherName: string;
  mobile: string; // Maps to frontend 'phone'
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}
