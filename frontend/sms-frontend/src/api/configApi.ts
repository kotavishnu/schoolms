import apiClient from './client';
import {
  ConfigSetting,
  CreateSettingRequest,
  UpdateSettingRequest,
  SchoolProfile,
  UpdateSchoolProfileRequest,
  ConfigCategory,
} from '@/types/config';

export const configApi = {
  // Create configuration setting
  createSetting: async (data: CreateSettingRequest): Promise<ConfigSetting> => {
    const response = await apiClient.post<ConfigSetting>(
      '/api/v1/configurations/settings',
      data
    );
    return response.data;
  },

  // Get setting by ID
  getSettingById: async (settingId: number): Promise<ConfigSetting> => {
    const response = await apiClient.get<ConfigSetting>(
      `/api/v1/configurations/settings/${settingId}`
    );
    return response.data;
  },

  // Update setting
  updateSetting: async (
    settingId: number,
    data: UpdateSettingRequest
  ): Promise<ConfigSetting> => {
    const response = await apiClient.put<ConfigSetting>(
      `/api/v1/configurations/settings/${settingId}`,
      data
    );
    return response.data;
  },

  // Delete setting
  deleteSetting: async (settingId: number): Promise<void> => {
    await apiClient.delete(`/api/v1/configurations/settings/${settingId}`);
  },

  // Get settings by category
  getSettingsByCategory: async (
    category: ConfigCategory
  ): Promise<{ category: string; settings: ConfigSetting[] }> => {
    const response = await apiClient.get(
      `/api/v1/configurations/settings/category/${category}`
    );
    return response.data;
  },

  // Get all settings grouped by category
  getAllSettings: async (): Promise<Record<string, ConfigSetting[]>> => {
    const response = await apiClient.get<Record<string, ConfigSetting[]>>(
      '/api/v1/configurations/settings'
    );
    return response.data;
  },

  // Get school profile
  getSchoolProfile: async (): Promise<SchoolProfile> => {
    const response = await apiClient.get<SchoolProfile>(
      '/api/v1/configurations/school-profile'
    );
    return response.data;
  },

  // Update school profile
  updateSchoolProfile: async (
    data: UpdateSchoolProfileRequest
  ): Promise<SchoolProfile> => {
    const response = await apiClient.put<SchoolProfile>(
      '/api/v1/configurations/school-profile',
      data
    );
    return response.data;
  },
};
