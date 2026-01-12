package com.schoolms.configuration.presentation.dto;

import java.util.List;

/**
 * Configuration List Response DTO (BE-034)
 */
public record ConfigurationListResponse(
    List<ConfigurationResponse> configurations,
    long totalCount
) {
}
