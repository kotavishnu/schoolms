package com.schoolms.configuration.presentation.dto;

import com.schoolms.configuration.domain.model.ConfigCategory;
import java.time.Instant;

/**
 * Configuration Response DTO (BE-034)
 */
public record ConfigurationResponse(
    Long id,
    ConfigCategory category,
    String key,
    String value,
    String description,
    Instant createdAt,
    Instant lastUpdated
) {
}
