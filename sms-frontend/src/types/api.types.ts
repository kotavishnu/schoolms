// RFC 7807 Problem Details
export interface ProblemDetails {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  correlationId: string;
  timestamp: string;
  errors?: Array<{
    field: string;
    message: string;
    rejectedValue?: any;
  }>;
  [key: string]: any; // Allow additional custom fields
}

// Pageable response structure (matches backend API)
export interface PageableResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

// Generic API error type
export interface ApiError {
  message: string;
  statusCode?: number;
  problemDetails?: ProblemDetails;
}
