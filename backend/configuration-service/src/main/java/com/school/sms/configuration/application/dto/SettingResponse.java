package com.school.sms.configuration.application.dto;

import com.school.sms.configuration.domain.enums.SettingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for configuration setting response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingResponse {
    private Long settingId;
    private SettingCategory category;
    private String key;
    private String value;
    private String description;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Long version;
}
