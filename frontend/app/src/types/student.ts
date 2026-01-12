/**
 * Student Type Definitions
 * School Management System - Phase 1
 */

export interface Student {
  // Identification
  id: string; // Backend-generated, e.g., "STU-2025-00001"

  // Personal Information
  firstName: string; // Required, 1-50 characters
  lastName: string; // Required, 1-50 characters
  dateOfBirth: string; // Required, ISO 8601 format (YYYY-MM-DD)
  age: number; // Calculated from DOB, 3-18 at registration
  adhaarNumber: string; // Required, 12 digits, unique
  identificationMarks?: string; // Optional, 0-200 characters
  address: string; // Required, 10-500 characters

  // Guardian Information
  guardianName: string; // Required (Father/Guardian), 1-100 characters
  motherName: string; // Required, 1-100 characters

  // Contact Information
  phone: string; // Required, unique, 10 digits
  email: string; // Required, valid email format, unique

  // Status
  status: StudentStatus; // Required, default: ACTIVE

  // Metadata
  createdAt: string; // Backend-managed, ISO 8601 timestamp
  updatedAt: string; // Backend-managed, ISO 8601 timestamp
}

export type StudentStatus = 'ACTIVE' | 'INACTIVE';

/**
 * Data Transfer Object for creating a new student
 * Excludes auto-generated fields
 */
export interface StudentCreateDto {
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
  status?: StudentStatus; // Optional, defaults to ACTIVE on backend
}

/**
 * Data Transfer Object for updating a student
 * Only editable fields after creation
 */
export interface StudentUpdateDto {
  firstName?: string;
  lastName?: string;
  phone?: string;
  status?: StudentStatus;
}

/**
 * Response from the list students API
 */
export interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
  inactiveCount: number;
}

/**
 * Dashboard statistics
 */
export interface StudentStatistics {
  totalStudents: number;
  activeStudents: number;
  inactiveStudents: number;
}

/**
 * Filters for querying students
 */
export interface StudentFilters {
  search?: string; // Search by ID, name, or guardian
  status?: 'ALL' | StudentStatus; // Filter by status
  page?: number; // Pagination
  size?: number; // Page size
}

/**
 * Phone validation request
 */
export interface PhoneValidationRequest {
  phone: string;
  excludeId?: string; // Exclude this student ID from uniqueness check (for edits)
}

/**
 * Phone validation response
 */
export interface PhoneValidationResponse {
  isUnique: boolean;
}
