/**
 * Configuration Type Definitions
 * School Management System - Phase 1
 */

export interface Configuration {
  // Identification
  id: string; // Backend-generated UUID or auto-increment

  // Configuration Data
  category: ConfigCategory; // Required, one of predefined categories
  key: string; // Required, unique within category, 1-100 characters
  value: string; // Required, 1-1000 characters
  description?: string; // Optional, 0-500 characters

  // Metadata
  lastUpdated: string; // Backend-managed, ISO 8601 timestamp
  createdAt: string; // Backend-managed, ISO 8601 timestamp
}

/**
 * Configuration categories
 */
export type ConfigCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM';

/**
 * Data Transfer Object for creating a new configuration
 */
export interface ConfigurationCreateDto {
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
}

/**
 * Data Transfer Object for updating a configuration
 */
export interface ConfigurationUpdateDto {
  value?: string;
  description?: string;
}

/**
 * Response from the list configurations API
 */
export interface ConfigurationListResponse {
  configurations: Configuration[];
  totalCount: number;
}

/**
 * Category badge styling configuration
 */
export interface CategoryBadgeStyle {
  text: string;
  bgColor: string;
  textColor: string;
}
