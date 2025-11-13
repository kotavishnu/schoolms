package com.school.management.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration for School Management System.
 *
 * <p>Configures Redis as the caching provider with:</p>
 * <ul>
 *   <li>Lettuce connection pool for high performance</li>
 *   <li>JSON serialization for complex objects</li>
 *   <li>Multiple cache regions with different TTL settings</li>
 *   <li>Key prefix for namespace isolation</li>
 *   <li>Error handling for cache failures</li>
 * </ul>
 *
 * <p>Cache Regions:</p>
 * <ul>
 *   <li>students: Student data (TTL: 1 hour)</li>
 *   <li>fees: Fee calculation results (TTL: 2 hours)</li>
 *   <li>classes: Class information (TTL: 12 hours)</li>
 *   <li>configurations: System configurations (TTL: 24 hours)</li>
 *   <li>users: User data (TTL: 2 hours)</li>
 *   <li>academicYears: Academic year data (TTL: 24 hours)</li>
 *   <li>feeStructures: Fee structures (TTL: 24 hours)</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheConfiguration implements CachingConfigurer {

    /**
     * Cache name constants for type-safe cache access.
     */
    public static final class CacheNames {
        public static final String STUDENTS = "students";
        public static final String FEES = "fees";
        public static final String CLASSES = "classes";
        public static final String CONFIGURATIONS = "configurations";
        public static final String USERS = "users";
        public static final String ACADEMIC_YEARS = "academicYears";
        public static final String FEE_STRUCTURES = "feeStructures";

        private CacheNames() {
            // Utility class
        }
    }

    /**
     * TTL configuration for different cache regions (in seconds).
     */
    private static final class CacheTTL {
        private static final long STUDENTS = 3600L;           // 1 hour
        private static final long FEES = 7200L;               // 2 hours
        private static final long CLASSES = 43200L;           // 12 hours
        private static final long CONFIGURATIONS = 86400L;    // 24 hours
        private static final long USERS = 7200L;              // 2 hours
        private static final long ACADEMIC_YEARS = 86400L;    // 24 hours
        private static final long FEE_STRUCTURES = 86400L;    // 24 hours
        private static final long DEFAULT = 3600L;            // 1 hour (default)

        private CacheTTL() {
            // Utility class
        }
    }

    private final CacheProperties cacheProperties;

    public RedisCacheConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    /**
     * Configures RedisTemplate with JSON serialization.
     *
     * <p>Uses Jackson for serializing/deserializing objects to/from JSON.
     * Supports Java 8 time types (LocalDate, LocalDateTime, etc.)</p>
     *
     * @param connectionFactory Redis connection factory
     * @return configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure Jackson ObjectMapper for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // Use String serializer for keys
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Use JSON serializer for values
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Configures RedisCacheManager with multiple cache configurations.
     *
     * <p>Each cache region can have different TTL and serialization settings.</p>
     *
     * @param connectionFactory Redis connection factory
     * @return configured RedisCacheManager
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        org.springframework.data.redis.cache.RedisCacheConfiguration defaultConfig =
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.DEFAULT));

        // Per-cache configurations with different TTLs
        Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> cacheConfigurations =
                new HashMap<>();

        cacheConfigurations.put(CacheNames.STUDENTS,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.STUDENTS)));

        cacheConfigurations.put(CacheNames.FEES,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.FEES)));

        cacheConfigurations.put(CacheNames.CLASSES,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.CLASSES)));

        cacheConfigurations.put(CacheNames.CONFIGURATIONS,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.CONFIGURATIONS)));

        cacheConfigurations.put(CacheNames.USERS,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.USERS)));

        cacheConfigurations.put(CacheNames.ACADEMIC_YEARS,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.ACADEMIC_YEARS)));

        cacheConfigurations.put(CacheNames.FEE_STRUCTURES,
                createCacheConfiguration(Duration.ofSeconds(CacheTTL.FEE_STRUCTURES)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * Creates cache configuration with specified TTL.
     *
     * @param ttl time-to-live duration
     * @return Redis cache configuration
     */
    private org.springframework.data.redis.cache.RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        // Configure Jackson ObjectMapper for cache values
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        return org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues()  // Don't cache null values
                .computePrefixWith(cacheName -> "sms:" + cacheName + "::");  // Key prefix: sms:cacheName::key
    }

    /**
     * Configures cache key generator.
     *
     * <p>Uses default Spring key generation strategy:
     * - No params: SimpleKey.EMPTY
     * - One param: param itself
     * - Multiple params: SimpleKey(params)</p>
     *
     * @return key generator
     */
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    /**
     * Configures cache error handler.
     *
     * <p>Logs cache errors but doesn't fail the application.
     * This ensures that cache failures don't break business logic.</p>
     *
     * @return cache error handler
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                // Log error but don't fail
                System.err.println("Cache GET error for cache: " + cache.getName() +
                        ", key: " + key + ", error: " + exception.getMessage());
                // In production, use proper logger:
                // log.error("Cache GET error for cache: {}, key: {}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                // Log error but don't fail
                System.err.println("Cache PUT error for cache: " + cache.getName() +
                        ", key: " + key + ", error: " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                // Log error but don't fail
                System.err.println("Cache EVICT error for cache: " + cache.getName() +
                        ", key: " + key + ", error: " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                // Log error but don't fail
                System.err.println("Cache CLEAR error for cache: " + cache.getName() +
                        ", error: " + exception.getMessage());
            }
        };
    }
}
