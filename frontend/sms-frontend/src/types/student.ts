export type StudentStatus = 'ACTIVE' | 'INACTIVE';

export interface Address {
  street: string;
  city: string;
  state: string;
  pinCode: string;
  country: string;
}

export interface Student {
  studentId: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  age: number;
  address: Address;
  mobile: string;
  email?: string;
  fatherNameOrGuardian: string;
  motherName?: string;
  caste?: string;
  moles?: string;
  aadhaarNumber?: string;
  status: StudentStatus;
  createdAt: string;
  updatedAt: string;
  version?: number;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  street: string;
  city: string;
  state: string;
  pinCode: string;
  country: string;
  mobile: string;
  email?: string;
  fatherNameOrGuardian: string;
  motherName?: string;
  caste?: string;
  moles?: string;
  aadhaarNumber?: string;
}

export interface UpdateStudentRequest extends CreateStudentRequest {
  version: number;
}

export interface StudentSearchParams {
  studentId?: string;
  firstName?: string;
  lastName?: string;
  mobile?: string;
  status?: StudentStatus;
  minAge?: number;
  maxAge?: number;
  caste?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PagedStudentResponse {
  content: Student[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface StudentStatistics {
  totalStudents: number;
  activeStudents: number;
  inactiveStudents: number;
  averageAge: number;
  ageDistribution: Record<string, number>;
  casteDistribution: Record<string, number>;
}
