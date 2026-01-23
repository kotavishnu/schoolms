export interface Configuration {
  id: string;
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
  lastUpdated: string;
  createdAt: string;
}

export type ConfigCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCE' | 'SYSTEM';

export interface ConfigurationCreateRequest {
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
}

export interface ConfigurationUpdateRequest {
  category?: ConfigCategory;
  key?: string;
  value?: string;
  description?: string;
}

export interface ConfigurationListResponse {
  configurations: Configuration[];
  totalCount: number;
}
