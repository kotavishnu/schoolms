export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>;
  timestamp?: string;
  path?: string;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

export interface SearchParams {
  search?: string;
  status?: string;
  category?: string;
}
