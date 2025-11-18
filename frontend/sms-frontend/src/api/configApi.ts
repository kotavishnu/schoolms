import apiClient from './client';
import {
  ConfigSetting,
  CreateSettingRequest,
  UpdateSettingRequest,
  SchoolProfile,
  UpdateSchoolProfileRequest,
  ConfigCategory,
} from '@/types/config';

// Note: Configuration service runs on port 8082, need to configure properly
const configBaseURL = import.meta.env.VITE_CONFIG_API_BASE_URL || 'http://localhost:8082';

export const configApi = {
  // Create configuration setting
  createSetting: async (data: CreateSettingRequest): Promise<ConfigSetting> => {
    const response = await apiClient.post<ConfigSetting>(
      '/configurations/settings',
      data,
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Get setting by ID
  getSettingById: async (settingId: number): Promise<ConfigSetting> => {
    const response = await apiClient.get<ConfigSetting>(
      `/configurations/settings/${settingId}`,
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Update setting
  updateSetting: async (
    settingId: number,
    data: UpdateSettingRequest
  ): Promise<ConfigSetting> => {
    const response = await apiClient.put<ConfigSetting>(
      `/configurations/settings/${settingId}`,
      data,
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Delete setting
  deleteSetting: async (settingId: number): Promise<void> => {
    await apiClient.delete(`/configurations/settings/${settingId}`, {
      baseURL: configBaseURL,
    });
  },

  // Get settings by category
  getSettingsByCategory: async (
    category: ConfigCategory
  ): Promise<{ category: string; settings: ConfigSetting[] }> => {
    const response = await apiClient.get(
      `/configurations/settings/category/${category}`,
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Get all settings grouped by category
  getAllSettings: async (): Promise<Record<string, ConfigSetting[]>> => {
    const response = await apiClient.get<Record<string, ConfigSetting[]>>(
      '/configurations/settings',
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Get school profile
  getSchoolProfile: async (): Promise<SchoolProfile> => {
    const response = await apiClient.get<SchoolProfile>(
      '/configurations/school-profile',
      { baseURL: configBaseURL }
    );
    return response.data;
  },

  // Update school profile
  updateSchoolProfile: async (
    data: UpdateSchoolProfileRequest
  ): Promise<SchoolProfile> => {
    const response = await apiClient.put<SchoolProfile>(
      '/configurations/school-profile',
      data,
      { baseURL: configBaseURL }
    );
    return response.data;
  },
};
