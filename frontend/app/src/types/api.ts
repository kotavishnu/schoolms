/**
 * API Response Type Definitions
 * School Management System - Phase 1
 */

/**
 * Generic API response wrapper
 */
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

/**
 * API Error structure
 */
export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>; // Field-level validation errors
}

/**
 * RFC 7807 Problem Detail for HTTP APIs
 * Used for standardized error responses
 */
export interface ProblemDetail {
  type: string; // URI reference that identifies the problem type
  title: string; // Short, human-readable summary
  status: number; // HTTP status code
  detail: string; // Human-readable explanation
  instance?: string; // URI reference that identifies the specific occurrence
  timestamp?: string; // ISO 8601 timestamp
  traceId?: string; // Trace ID for debugging
  errors?: Record<string, string[]>; // Validation errors by field
}

/**
 * Paginated response wrapper
 */
export interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  pagination: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}

/**
 * Custom error class for API errors
 */
export class ApiErrorClass extends Error {
  status: number;
  details?: Record<string, string[]>;
  code?: string;

  constructor(
    message: string,
    status: number = 0,
    details?: Record<string, string[]>,
    code?: string
  ) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
    this.code = code;
  }
}
