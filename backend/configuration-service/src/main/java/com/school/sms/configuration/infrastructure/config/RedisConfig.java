package com.school.sms.configuration.infrastructure.config;

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
 * Redis cache configuration for Configuration Service.
 *
 * Implements caching strategy as per LESSONS_LEARNED.md [D-003], [D-004], [D-006]
 *
 * Cache Configuration:
 * - Database: 1 (configuration-service specific)
 * - Key Prefix: sms:config:
 * - Serialization: Jackson2Json for complex objects
 * - Connection Pool: Lettuce with configured limits
 *
 * Cache Names and TTLs:
 * - configurations: 14400s (4 hours) - Individual configuration settings
 * - configByCategory: 14400s (4 hours) - All configs in a category
 * - groupedConfigs: 14400s (4 hours) - Grouped configuration maps
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
                .entryTtl(Duration.ofSeconds(14400)) // Default: 4 hours for stable config data
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer()))
                .disableCachingNullValues()
                .prefixCacheNameWith("sms:config:");

        // Cache-specific configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Configuration settings cache: 4 hours TTL for stable configuration data
        cacheConfigurations.put("configurations", defaultConfig
                .entryTtl(Duration.ofSeconds(14400)));

        // Configuration by category cache: 4 hours TTL
        cacheConfigurations.put("configByCategory", defaultConfig
                .entryTtl(Duration.ofSeconds(14400)));

        // Grouped configurations cache: 4 hours TTL
        cacheConfigurations.put("groupedConfigs", defaultConfig
                .entryTtl(Duration.ofSeconds(14400)));

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
