export type ConfigCategory = 'GENERAL' | 'ACADEMIC' | 'FINANCIAL';

export interface ConfigSetting {
  settingId: number;
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
  updatedAt: string;
  updatedBy: string;
  version?: number;
}

export interface CreateSettingRequest {
  category: ConfigCategory;
  key: string;
  value: string;
  description?: string;
}

export interface UpdateSettingRequest {
  value: string;
  description?: string;
  version: number;
}

export interface SchoolProfile {
  id: number;
  schoolName: string;
  schoolCode: string;
  logoPath?: string;
  address: string;
  phone: string;
  email: string;
  updatedAt: string;
  updatedBy: string;
}

export interface UpdateSchoolProfileRequest {
  schoolName: string;
  schoolCode: string;
  logoPath?: string;
  address: string;
  phone: string;
  email: string;
}
