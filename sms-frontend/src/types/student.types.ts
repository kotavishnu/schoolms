import { type PageableResponse } from './api.types';

export type StudentStatus = 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'TRANSFERRED';

export interface Student {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  address: string;
  mobile: string;
  dateOfBirth: string;
  currentAge: number;
  fatherName: string;
  motherName?: string;
  identificationMark?: string;
  email?: string;
  aadhaarNumber?: string;
  status: StudentStatus;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  address: string;
  mobile: string;
  dateOfBirth: string;
  fatherName: string;
  motherName?: string;
  identificationMark?: string;
  email?: string;
  aadhaarNumber?: string;
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: StudentStatus;
  version: number;
}

export interface UpdateStatusRequest {
  status: StudentStatus;
  version: number;
}

export type StudentPageResponse = PageableResponse<Student>;

export interface StudentSearchParams {
  page?: number;
  size?: number;
  lastName?: string;
  guardianName?: string;
  status?: string;
  mobile?: string;
  sort?: string;
}
