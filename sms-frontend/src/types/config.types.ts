import { type PageableResponse } from './api.types';

export type ConfigCategory = 'General' | 'Academic' | 'Financial' | 'System';

export interface Configuration {
  id: number;
  category: ConfigCategory;
  configKey: string;
  configValue: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CreateConfigRequest {
  category: ConfigCategory;
  configKey: string;
  configValue: string;
  description?: string;
}

export interface UpdateConfigRequest {
  category: ConfigCategory;
  configKey: string;
  configValue: string;
  description?: string;
  version: number;
}

export type ConfigurationPageResponse = PageableResponse<Configuration>;
