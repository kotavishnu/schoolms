package com.school.sms.configuration.application.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConfigurationResponse {
    private Long id;
    private String category;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}
