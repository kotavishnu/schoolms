package com.school.configuration.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity for configuration_settings table.
 * Separate from domain model to maintain clean architecture.
 * Stores configuration settings with category, key, value, and metadata.
 */
@Entity
@Table(
    name = "configuration_settings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_config_category_key",
            columnNames = {"category", "config_key"}
        )
    },
    indexes = {
        @Index(name = "idx_config_category", columnList = "category"),
        @Index(name = "idx_config_key", columnList = "config_key")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationEntity {

    /**
     * Primary key (auto-generated)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Configuration category stored as string (GENERAL, ACADEMIC, FINANCIAL)
     */
    @Column(name = "category", nullable = false, length = 20)
    private String category;

    /**
     * Configuration key (unique within category)
     */
    @Column(name = "config_key", nullable = false, length = 100)
    private String key;

    /**
     * Configuration value (stored as string, parsed based on dataType)
     */
    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    /**
     * Description of the configuration setting
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Data type stored as string (STRING, NUMBER, BOOLEAN, JSON)
     */
    @Column(name = "data_type", nullable = false, length = 20)
    private String dataType;

    /**
     * Indicates if the value is encrypted
     */
    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Last updated timestamp (auto-updated)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
