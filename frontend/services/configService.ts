import apiClient from '@/lib/api-client';
import {
  ConfigurationSetting,
  CreateConfigRequest,
  UpdateConfigRequest,
  SettingCategory,
} from '@/types';

export const configService = {
  /**
   * Get all configuration settings, optionally filtered by category
   */
  async getSettings(category?: SettingCategory): Promise<ConfigurationSetting[]> {
    const params = category ? { category } : {};
    const response = await apiClient.get<{ content: ConfigurationSetting[] }>('/config/settings', { params });
    return response.data.content || [];
  },

  /**
   * Get a specific configuration setting by ID
   */
  async getSetting(id: number): Promise<ConfigurationSetting> {
    const response = await apiClient.get<ConfigurationSetting>(`/config/settings/${id}`);
    return response.data;
  },

  /**
   * Create a new configuration setting
   */
  async createSetting(data: CreateConfigRequest): Promise<ConfigurationSetting> {
    const response = await apiClient.post<ConfigurationSetting>('/config/settings', data);
    return response.data;
  },

  /**
   * Update an existing configuration setting
   */
  async updateSetting(id: number, data: UpdateConfigRequest): Promise<ConfigurationSetting> {
    const response = await apiClient.put<ConfigurationSetting>(`/config/settings/${id}`, data);
    return response.data;
  },

  /**
   * Delete a configuration setting
   */
  async deleteSetting(id: number): Promise<void> {
    await apiClient.delete(`/config/settings/${id}`);
  },
};
