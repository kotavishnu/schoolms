package com.school.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing school configuration settings.
 *
 * Database Table: school_config
 *
 * This entity stores system-wide configuration parameters for the school
 * such as school name, address, contact details, and operational settings.
 *
 * Business Rules:
 * - Config keys should be unique
 * - Provides centralized configuration management
 * - Values can be of different types (stored as string, parsed by application)
 *
 * @author School Management Team
 */
@Entity
@Table(name = "school_config", indexes = {
    @Index(name = "idx_config_key", columnList = "config_key", unique = true),
    @Index(name = "idx_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Config key is required")
    private String configKey;

    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Config value is required")
    private String configValue;

    @Column(name = "category", length = 50)
    @Builder.Default
    private String category = "GENERAL";

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_editable", nullable = false)
    @Builder.Default
    private Boolean isEditable = true;

    @Column(name = "data_type", length = 20)
    @Builder.Default
    private String dataType = "STRING"; // STRING, INTEGER, BOOLEAN, JSON

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Get config value as integer
     * @return Integer value or null if not parseable
     */
    @Transient
    public Integer getIntValue() {
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get config value as boolean
     * @return Boolean value
     */
    @Transient
    public Boolean getBooleanValue() {
        return Boolean.parseBoolean(configValue);
    }

    /**
     * Check if config is system-defined (not editable)
     * @return true if not editable
     */
    @Transient
    public boolean isSystemConfig() {
        return Boolean.FALSE.equals(isEditable);
    }
}
