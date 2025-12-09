package com.sms.student.config.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA Entity for Configuration Settings.
 * Stores application configuration key-value pairs.
 */
@Entity
@Table(name = "configuration_setting", indexes = {
    @Index(name = "idx_config_category", columnList = "category"),
    @Index(name = "idx_config_key", columnList = "key"),
    @Index(name = "idx_config_category_key", columnList = "category,key")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationSettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Get full key as category:key.
     */
    @Transient
    public String getFullKey() {
        return category + ":" + key;
    }
}
