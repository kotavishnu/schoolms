package com.school.sms.configuration.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for grouped configuration settings.
 *
 * <p>This DTO returns configurations grouped by category as a simple
 * key-value map for easy consumption by clients.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupedConfigurationResponse {

    private String category;
    private Map<String, String> configurations;
}
