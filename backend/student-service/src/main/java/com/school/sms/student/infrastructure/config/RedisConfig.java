package com.school.sms.student.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration for Student Service.
 *
 * Implements caching strategy as per LESSONS_LEARNED.md [D-003], [D-004], [D-006]
 *
 * Cache Configuration:
 * - Database: 0 (student-service specific)
 * - Key Prefix: sms:student:
 * - Serialization: Jackson2Json for complex objects
 * - Connection Pool: Lettuce with configured limits
 *
 * Cache Names and TTLs:
 * - students: 3600s (1 hour) - Individual student data
 * - studentSearchResults: 900s (15 minutes) - Search query results
 * - activeStudentsCount: 900s (15 minutes) - Count aggregations
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Configure RedisCacheManager with cache-specific TTLs and serialization.
     *
     * Reference: LESSONS_LEARNED.md [D-003] - Use Jackson2JsonRedisSerializer for complex objects
     * Reference: LESSONS_LEARNED.md [D-004] - Use consistent key prefix pattern
     *
     * @param connectionFactory Redis connection factory (auto-configured by Spring Boot)
     * @return Configured RedisCacheManager
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration with Jackson2Json serialization
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(3600)) // Default: 1 hour
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer()))
                .disableCachingNullValues()
                .prefixCacheNameWith("sms:student:");

        // Cache-specific configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Student cache: 1 hour TTL for stable data
        cacheConfigurations.put("students", defaultConfig
                .entryTtl(Duration.ofSeconds(3600)));

        // Search results cache: 15 minutes TTL for dynamic data
        cacheConfigurations.put("studentSearchResults", defaultConfig
                .entryTtl(Duration.ofSeconds(900)));

        // Active students count cache: 15 minutes TTL
        cacheConfigurations.put("activeStudentsCount", defaultConfig
                .entryTtl(Duration.ofSeconds(900)));

        // Enrollment cache: 1 hour TTL
        cacheConfigurations.put("enrollments", defaultConfig
                .entryTtl(Duration.ofSeconds(3600)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // Enable transaction support
                .build();
    }

    /**
     * Configure RedisTemplate for manual Redis operations.
     *
     * @param connectionFactory Redis connection factory
     * @return Configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use Jackson2Json serializer for values
        GenericJackson2JsonRedisSerializer serializer = createJsonSerializer();
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Create Jackson2JsonRedisSerializer with proper type handling.
     *
     * Enables polymorphic type handling to support serialization of complex domain objects.
     *
     * @return Configured GenericJackson2JsonRedisSerializer
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Enable type information for proper deserialization
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
