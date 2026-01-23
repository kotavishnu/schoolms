package com.school.student.config;

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
 * Configures Redis caching for Student Service.
 * Uses Redis database 0 (D-003).
 *
 * Cache TTLs (D-003):
 * - students: 4 hours (stable data)
 * - studentSearchResults: 15 minutes (dynamic data)
 * - studentStatistics: 15 minutes (frequently updated)
 *
 * Key Naming Convention (D-004):
 * - Prefix: sms:student:
 * - Format: sms:student:{cacheName}::{key}
 * - Example: sms:student:students::STD-20260122-0001
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis Cache Manager
     *
     * Features:
     * - JSON serialization with Jackson (supports LocalDate, LocalDateTime)
     * - Separate TTLs for different cache types
     * - Consistent key prefix (sms:student:)
     *
     * @param connectionFactory Redis connection factory
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
            .entryTtl(Duration.ofHours(2)) // Default TTL: 2 hours
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            )
            .prefixCacheNameWith("sms:student:"); // Key prefix per D-004

        // Build cache manager with specific configurations
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            // Stable data: longer TTL
            .withCacheConfiguration("students",
                defaultConfig.entryTtl(Duration.ofHours(4)))
            // Dynamic data: shorter TTL
            .withCacheConfiguration("studentSearchResults",
                defaultConfig.entryTtl(Duration.ofMinutes(15)))
            .withCacheConfiguration("studentStatistics",
                defaultConfig.entryTtl(Duration.ofMinutes(15)))
            .build();
    }
}
