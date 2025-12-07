package com.school.sms.configuration.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for configuration setting.
 *
 * <p>This DTO contains the complete configuration setting information
 * returned to clients.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationResponse {

    private Long settingId;
    private String category;
    private String configKey;
    private String configValue;
    private String dataType;
    private String description;
    private Boolean isEncrypted;
    private Long version;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
