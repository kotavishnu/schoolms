// Common types
export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  correlationId: string;
  timestamp: string;
  errors?: FieldError[];
}

export interface FieldError {
  field: string;
  message: string;
  rejectedValue?: any;
  code?: string;
}

// Pagination types
export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    offset: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}

// Student types
export interface Student {
  studentId: number;
  studentKey: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  age: number;
  mobile: string;
  email?: string;
  address?: string;
  fatherNameOrGuardian?: string;
  motherName?: string;
  identificationMark?: string;
  adhaarNumber?: string;
  status: 'Active' | 'Inactive';
  createdAt: string;
  updatedAt: string;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  mobile: string;
  email?: string;
  address?: string;
  fatherNameOrGuardian?: string;
  motherName?: string;
  identificationMark?: string;
  adhaarNumber?: string;
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  mobile: string;
  status: 'Active' | 'Inactive';
}

export interface StudentSearchParams extends PageRequest {
  lastName?: string;
  guardianName?: string;
  status?: 'Active' | 'Inactive';
}

export interface StudentListItem {
  studentId: number;
  studentKey: string;
  firstName: string;
  lastName: string;
  mobile: string;
  fatherNameOrGuardian?: string;
  status: 'Active' | 'Inactive';
  createdAt: string;
}

// Configuration types
export type SettingCategory = 'General' | 'Academic' | 'Financial';

export interface ConfigurationSetting {
  settingId: number;
  category: SettingCategory;
  settingKey: string;
  settingValue: string;
  description?: string;
  createdAt?: string;
  updatedAt: string;
  updatedBy?: string;
}

export interface CreateConfigRequest {
  category: SettingCategory;
  key: string;
  value: string;
  description?: string;
}

export interface UpdateConfigRequest {
  value: string;
  description?: string;
}

export interface ConfigSearchParams extends PageRequest {
  category?: SettingCategory;
}

// School Profile types
export interface SchoolProfile {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
  schoolLogoUrl?: string;
  address?: string;
  contactNumber?: string;
  email?: string;
  principalName?: string;
  establishedDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateSchoolProfileRequest {
  schoolName: string;
  schoolCode: string;
  schoolLogoUrl?: string;
  address?: string;
  contactNumber?: string;
  email?: string;
  principalName?: string;
  establishedDate?: string;
}
