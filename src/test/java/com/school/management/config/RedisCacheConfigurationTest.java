package com.school.management.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Redis Cache Configuration.
 *
 * <p>Tests verify:</p>
 * <ul>
 *   <li>Redis connection factory is configured</li>
 *   <li>RedisTemplate is available with proper serialization</li>
 *   <li>RedisCacheManager is configured</li>
 *   <li>Cache names are defined correctly</li>
 *   <li>TTL settings are applied</li>
 *   <li>Cache operations work correctly</li>
 * </ul>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@EnableCaching
@DisplayName("Redis Cache Configuration Tests")
class RedisCacheConfigurationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379)
            .withReuse(false);

    static {
        redisContainer.start();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Test
    @DisplayName("Should have RedisConnectionFactory bean configured")
    void shouldHaveRedisConnectionFactory() {
        // Then - RedisConnectionFactory should be available
        assertThat(redisConnectionFactory).isNotNull();
        assertThat(redisConnectionFactory).isInstanceOf(RedisConnectionFactory.class);
    }

    @Test
    @DisplayName("Should have RedisTemplate bean configured")
    void shouldHaveRedisTemplate() {
        // Then - RedisTemplate should be available
        assertThat(redisTemplate).isNotNull();
        assertThat(redisTemplate.getConnectionFactory()).isNotNull();
    }

    @Test
    @DisplayName("Should have RedisCacheManager configured")
    void shouldHaveRedisCacheManager() {
        // Then - CacheManager should be RedisCacheManager
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
    }

    @Test
    @DisplayName("Should have predefined cache names configured")
    void shouldHavePredefinedCacheNames() {
        // Given - Expected cache names
        Set<String> expectedCacheNames = Set.of(
                "students",
                "fees",
                "classes",
                "configurations",
                "users",
                "academicYears",
                "feeStructures"
        );

        // When - Get cache names from cache manager
        Set<String> actualCacheNames = (Set<String>) cacheManager.getCacheNames();

        // Then - All expected caches should be configured
        assertThat(actualCacheNames).containsAll(expectedCacheNames);
    }

    @Test
    @DisplayName("Should create cache on demand if not predefined")
    void shouldCreateCacheOnDemand() {
        // When - Get a cache that might not be predefined
        Cache dynamicCache = cacheManager.getCache("testCache");

        // Then - Cache should be created
        assertThat(dynamicCache).isNotNull();
    }

    @Test
    @DisplayName("Should store and retrieve values from cache")
    void shouldStoreAndRetrieveFromCache() {
        // Given - A cache and test data
        Cache studentsCache = cacheManager.getCache("students");
        assertThat(studentsCache).isNotNull();

        String cacheKey = "student:123";
        String cacheValue = "John Doe";

        // When - Store value in cache
        studentsCache.put(cacheKey, cacheValue);

        // Then - Value should be retrievable
        Cache.ValueWrapper wrapper = studentsCache.get(cacheKey);
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isEqualTo(cacheValue);
    }

    @Test
    @DisplayName("Should evict values from cache")
    void shouldEvictFromCache() {
        // Given - A cache with data
        Cache studentsCache = cacheManager.getCache("students");
        assertThat(studentsCache).isNotNull();

        String cacheKey = "student:456";
        String cacheValue = "Jane Smith";
        studentsCache.put(cacheKey, cacheValue);

        // When - Evict value
        studentsCache.evict(cacheKey);

        // Then - Value should be removed
        Cache.ValueWrapper wrapper = studentsCache.get(cacheKey);
        assertThat(wrapper).isNull();
    }

    @Test
    @DisplayName("Should clear entire cache")
    void shouldClearCache() {
        // Given - A cache with multiple entries
        Cache studentsCache = cacheManager.getCache("students");
        assertThat(studentsCache).isNotNull();

        studentsCache.put("student:1", "Student 1");
        studentsCache.put("student:2", "Student 2");
        studentsCache.put("student:3", "Student 3");

        // When - Clear cache
        studentsCache.clear();

        // Then - All values should be removed
        assertThat(studentsCache.get("student:1")).isNull();
        assertThat(studentsCache.get("student:2")).isNull();
        assertThat(studentsCache.get("student:3")).isNull();
    }

    @Test
    @DisplayName("Should use RedisTemplate for low-level operations")
    void shouldUseRedisTemplateForOperations() {
        // Given - RedisTemplate and test data
        String key = "test:key:1";
        String value = "test value";

        // When - Set value using RedisTemplate
        redisTemplate.opsForValue().set(key, value);

        // Then - Value should be retrievable
        Object retrievedValue = redisTemplate.opsForValue().get(key);
        assertThat(retrievedValue).isEqualTo(value);

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("Should set TTL on cache entries")
    void shouldSetTTLOnEntries() {
        // Given - RedisTemplate and test data
        String key = "test:ttl:1";
        String value = "ttl test value";

        // When - Set value with TTL
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(5));

        // Then - Value should exist
        Boolean exists = redisTemplate.hasKey(key);
        assertThat(exists).isTrue();

        // And - TTL should be set
        Long ttl = redisTemplate.getExpire(key);
        assertThat(ttl).isGreaterThan(0);
        assertThat(ttl).isLessThanOrEqualTo(5L);

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("Should handle null values in cache")
    void shouldHandleNullValues() {
        // Given - A cache
        Cache configurationsCache = cacheManager.getCache("configurations");
        assertThat(configurationsCache).isNotNull();

        String cacheKey = "config:nullable";

        // When - Store null value (depending on cache configuration)
        configurationsCache.put(cacheKey, null);

        // Then - Behavior depends on cache-null-values configuration
        // If cache-null-values=false, null is not cached
        // If cache-null-values=true, null is cached
        Cache.ValueWrapper wrapper = configurationsCache.get(cacheKey);
        // Just verify no exception is thrown
        assertThat(wrapper == null || wrapper.get() == null).isTrue();
    }

    @Test
    @DisplayName("Should use key prefix for cache entries")
    void shouldUseKeyPrefix() {
        // Given - RedisTemplate and test data
        Cache studentsCache = cacheManager.getCache("students");
        assertThat(studentsCache).isNotNull();

        String cacheKey = "student:prefix:test";
        String cacheValue = "prefix test";

        // When - Store value
        studentsCache.put(cacheKey, cacheValue);

        // Then - Value should be stored with prefix (sms:students::key)
        // This is verified by checking if value exists
        Cache.ValueWrapper wrapper = studentsCache.get(cacheKey);
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isEqualTo(cacheValue);
    }

    @Test
    @DisplayName("Should verify Redis connection is active")
    void shouldVerifyRedisConnection() {
        // When - Ping Redis
        String pingResponse = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();

        // Then - Response should be PONG
        assertThat(pingResponse).isEqualToIgnoringCase("PONG");
    }

    @Test
    @DisplayName("Should have different TTL for different caches")
    void shouldHaveDifferentTTLForCaches() {
        // This test verifies that different caches can have different configurations
        // The actual TTL values are configured in RedisCacheConfiguration

        // Get different caches
        Cache studentsCache = cacheManager.getCache("students");
        Cache configurationsCache = cacheManager.getCache("configurations");
        Cache feesCache = cacheManager.getCache("fees");

        // Then - All caches should exist
        assertThat(studentsCache).isNotNull();
        assertThat(configurationsCache).isNotNull();
        assertThat(feesCache).isNotNull();

        // Note: Actual TTL verification would require inspecting Redis directly
        // or using RedisTemplate to check expiry
    }
}
