/**
 * Common TypeScript types used across the application
 */

export interface PageResponse<T> {
  content: T[];
  page: PageInfo;
}

export interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiError {
  status: number;
  message: string;
  errors?: Record<string, string[]>;
  timestamp?: string;
  path?: string;
}

export type Status = 'ACTIVE' | 'INACTIVE';

export interface BaseEntity {
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  updatedBy?: string;
}
