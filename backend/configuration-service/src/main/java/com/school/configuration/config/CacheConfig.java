package com.school.configuration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Cache Configuration
 *
 * Configures Redis caching for Configuration Service.
 * Uses Redis database 1 (D-003) - Student Service uses database 0.
 *
 * Cache TTLs (D-003):
 * - configurations: 4 hours (stable data - all configurations)
 * - configurationsByCategory: 4 hours (stable data - category-filtered)
 * - configuration: 4 hours (stable data - single configuration)
 *
 * Key Naming Convention (D-004):
 * - Prefix: sms:configuration:
 * - Format: sms:configuration:{cacheName}::{key}
 * - Example: sms:configuration:configuration::GENERAL:SCHOOL_NAME
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis Cache Manager
     *
     * Features:
     * - JSON serialization with Jackson (supports LocalDate, LocalDateTime)
     * - Consistent TTL of 4 hours for all caches (configuration data is stable)
     * - Consistent key prefix (sms:configuration:)
     *
     * @param connectionFactory Redis connection factory (configured to use DB 1)
     * @return Configured cache manager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure ObjectMapper for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Support for Java 8 date/time types

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(objectMapper);

        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(4)) // Configuration data is stable, cache for 4 hours
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            )
            .prefixCacheNameWith("sms:configuration:"); // Key prefix per D-004

        // Build cache manager with specific configurations
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            // All configuration caches use same TTL (4 hours)
            .withCacheConfiguration("configurations",
                defaultConfig.entryTtl(Duration.ofHours(4)))
            .withCacheConfiguration("configurationsByCategory",
                defaultConfig.entryTtl(Duration.ofHours(4)))
            .withCacheConfiguration("configuration",
                defaultConfig.entryTtl(Duration.ofHours(4)))
            .build();
    }
}
