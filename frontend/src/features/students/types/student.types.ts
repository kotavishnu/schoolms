/**
 * TypeScript type definitions for Student Management Module
 */

/**
 * Student status enumeration
 */
export type StudentStatus = 'Active' | 'Inactive' | 'Graduated' | 'Dropped';

/**
 * Gender enumeration
 */
export type Gender = 'Male' | 'Female' | 'Other';

/**
 * Blood group enumeration
 */
export type BloodGroup = 'A+' | 'A-' | 'B+' | 'B-' | 'AB+' | 'AB-' | 'O+' | 'O-';

/**
 * Guardian relationship enumeration
 */
export type GuardianRelationship = 'Father' | 'Mother' | 'Guardian' | 'Other';

/**
 * Address information
 */
export interface Address {
  street: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

/**
 * Guardian information
 */
export interface Guardian {
  name: string;
  relationship: GuardianRelationship;
  phone: string;
  email?: string;
  occupation?: string;
}

/**
 * Academic information for a student
 */
export interface AcademicInfo {
  classId: number;
  className?: string;
  section?: string;
  rollNumber?: string;
  admissionDate: string;
  previousSchool?: string;
}

/**
 * Complete student information
 */
export interface Student {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  bloodGroup?: BloodGroup;
  email?: string;
  phone?: string;
  photoUrl?: string;
  address: Address;
  guardian: Guardian;
  academic: AcademicInfo;
  status: StudentStatus;
  createdAt: string;
  updatedAt: string;
}

/**
 * Student form data for registration/edit
 */
export interface StudentFormData {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  bloodGroup?: BloodGroup;
  email?: string;
  phone?: string;
  photo?: File | null;
  address: {
    street: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
  };
  guardian: {
    name: string;
    relationship: GuardianRelationship;
    phone: string;
    email?: string;
    occupation?: string;
  };
  academic: {
    classId: number;
    rollNumber?: string;
    admissionDate: string;
    previousSchool?: string;
  };
}

/**
 * Filters for student list
 */
export interface StudentFilters {
  search?: string;
  status?: StudentStatus;
  classId?: number;
  page?: number;
  limit?: number;
}

/**
 * Paginated student list response
 */
export interface StudentListResponse {
  students: Student[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

/**
 * Class information for dropdowns
 */
export interface ClassOption {
  id: number;
  name: string;
  section: string;
  capacity: number;
  currentEnrollment: number;
}

/**
 * Student creation response
 */
export interface StudentCreateResponse {
  student: Student;
  message: string;
}

/**
 * Student update response
 */
export interface StudentUpdateResponse {
  student: Student;
  message: string;
}

/**
 * Student deletion response
 */
export interface StudentDeleteResponse {
  message: string;
}
