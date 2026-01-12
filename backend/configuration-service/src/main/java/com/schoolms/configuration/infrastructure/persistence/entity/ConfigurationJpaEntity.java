package com.schoolms.configuration.infrastructure.persistence.entity;

import com.schoolms.configuration.domain.model.ConfigCategory;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Configuration JPA Entity (BE-030)
 * Persistence entity mapping to configuration_settings table
 */
@Entity
@Table(name = "configuration_settings")
public class ConfigurationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ConfigCategory category;

    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Column(name = "value", nullable = false, length = 1000)
    private String value;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (lastUpdated == null) {
            lastUpdated = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }

    // Default constructor
    public ConfigurationJpaEntity() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfigCategory getCategory() {
        return category;
    }

    public void setCategory(ConfigCategory category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
