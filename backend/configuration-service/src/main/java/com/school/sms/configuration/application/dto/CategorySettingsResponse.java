package com.school.sms.configuration.application.dto;

import com.school.sms.configuration.domain.enums.SettingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for settings grouped by category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySettingsResponse {
    private SettingCategory category;
    private List<SettingResponse> settings;
}
