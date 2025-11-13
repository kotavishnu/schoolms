# Redis Cache Setup Guide

## Overview

This document provides comprehensive guidance for Redis cache configuration and usage in the School Management System. Redis is used as the distributed caching layer to improve application performance and reduce database load.

## Table of Contents

1. [Architecture](#architecture)
2. [Configuration](#configuration)
3. [Cache Regions](#cache-regions)
4. [Usage Examples](#usage-examples)
5. [Best Practices](#best-practices)
6. [Monitoring](#monitoring)
7. [Troubleshooting](#troubleshooting)
8. [Performance Tuning](#performance-tuning)

---

## Architecture

### Redis Integration

```
Application Layer
    |
    ├── @Cacheable Annotations
    |     |
    |     └── RedisCacheManager
    |           |
    |           ├── RedisTemplate (Serialization/Deserialization)
    |           |
    |           └── LettuceConnectionFactory (Connection Pool)
    |                 |
    |                 └── Redis Server
```

### Cache Strategy

- **Cache-Aside Pattern**: Application checks cache first, loads from DB if miss
- **Write-Through**: Updates both cache and database on write operations
- **TTL-based Expiration**: Different TTL for different data types
- **Namespace Isolation**: Key prefix `sms:` to avoid conflicts

---

## Configuration

### Application Properties

**Development** (`application-dev.yml`):
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20      # Maximum active connections
        max-idle: 8         # Maximum idle connections
        min-idle: 2         # Minimum idle connections
        max-wait: 2000ms    # Max wait time for connection
      shutdown-timeout: 100ms

  cache:
    type: redis
    redis:
      time-to-live: 3600000         # Default TTL: 1 hour (in ms)
      cache-null-values: false      # Don't cache null values
      use-key-prefix: true
      key-prefix: "sms:"
```

**Production** (`application-prod.yml`):
```yaml
spring:
  redis:
    host: ${REDIS_HOST}             # From environment variable
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}     # REQUIRED in production
    database: 0
    timeout: 3000ms
    ssl: true                       # Enable SSL in production
    lettuce:
      pool:
        max-active: 50
        max-idle: 10
        min-idle: 5
        max-wait: 3000ms
      shutdown-timeout: 200ms
```

### Environment Variables

```bash
# Redis Connection
REDIS_HOST=redis.production.com
REDIS_PORT=6379
REDIS_PASSWORD=your-secure-password

# Connection Pool
REDIS_POOL_MAX_ACTIVE=50
REDIS_POOL_MAX_IDLE=10
REDIS_POOL_MIN_IDLE=5
```

---

## Cache Regions

### Configured Caches

| Cache Name | TTL | Use Case | Eviction Strategy |
|------------|-----|----------|-------------------|
| `students` | 1 hour | Student master data | Manual + TTL |
| `fees` | 2 hours | Fee calculation results | Manual + TTL |
| `classes` | 12 hours | Class information | Manual + TTL |
| `configurations` | 24 hours | System configurations | Manual + TTL |
| `users` | 2 hours | User authentication data | Manual + TTL |
| `academicYears` | 24 hours | Academic year data | Manual + TTL |
| `feeStructures` | 24 hours | Fee structures | Manual + TTL |

### Cache Key Format

All cache keys use the prefix pattern: `sms:{cacheName}::{key}`

Examples:
```
sms:students::STU-2024-00001
sms:fees::student:123:month:4
sms:classes::class:5:section:A:year:2024
sms:configurations::school.name
sms:users::username:admin
```

### TTL Configuration

TTL values are configured in `RedisCacheConfiguration.CacheTTL`:

```java
private static final class CacheTTL {
    private static final long STUDENTS = 3600L;           // 1 hour
    private static final long FEES = 7200L;               // 2 hours
    private static final long CLASSES = 43200L;           // 12 hours
    private static final long CONFIGURATIONS = 86400L;    // 24 hours
    private static final long USERS = 7200L;              // 2 hours
    private static final long ACADEMIC_YEARS = 86400L;    // 24 hours
    private static final long FEE_STRUCTURES = 86400L;    // 24 hours
    private static final long DEFAULT = 3600L;            // 1 hour
}
```

---

## Usage Examples

### 1. Basic Caching with @Cacheable

**Cache student data**:
```java
@Service
public class StudentService {

    @Cacheable(value = "students", key = "#studentCode")
    public StudentDTO getStudentByCode(String studentCode) {
        // This method result will be cached
        // Next call with same studentCode will return from cache
        return studentRepository.findByStudentCode(studentCode)
                .map(studentMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }
}
```

### 2. Cache Eviction with @CacheEvict

**Evict on update**:
```java
@Service
public class StudentService {

    @CacheEvict(value = "students", key = "#studentCode")
    public StudentDTO updateStudent(String studentCode, UpdateStudentRequest request) {
        // Cache entry is removed before method execution
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow();

        // Update student
        student.update(request);
        studentRepository.save(student);

        return studentMapper.toDTO(student);
    }

    @CacheEvict(value = "students", allEntries = true)
    public void clearAllStudentsCache() {
        // Clears all entries in students cache
    }
}
```

### 3. Cache Update with @CachePut

**Update cache without evicting**:
```java
@Service
public class StudentService {

    @CachePut(value = "students", key = "#result.studentCode")
    public StudentDTO createStudent(CreateStudentRequest request) {
        // Method is always executed
        // Result is put in cache
        Student student = new Student(request);
        studentRepository.save(student);
        return studentMapper.toDTO(student);
    }
}
```

### 4. Conditional Caching

**Cache only active students**:
```java
@Cacheable(
    value = "students",
    key = "#studentId",
    condition = "#result != null && #result.status == 'ACTIVE'"
)
public StudentDTO getStudent(Long studentId) {
    return studentRepository.findById(studentId)
            .map(studentMapper::toDTO)
            .orElse(null);
}
```

### 5. Multiple Cache Operations

**Evict from multiple caches**:
```java
@Caching(evict = {
    @CacheEvict(value = "students", key = "#studentId"),
    @CacheEvict(value = "fees", key = "#studentId"),
    @CacheEvict(value = "classes", allEntries = true)
})
public void withdrawStudent(Long studentId) {
    // Evicts from multiple caches
    Student student = studentRepository.findById(studentId).orElseThrow();
    student.withdraw();
    studentRepository.save(student);
}
```

### 6. Custom Key Generation

**Complex cache key**:
```java
@Cacheable(
    value = "fees",
    key = "'student:' + #studentId + ':month:' + #month + ':year:' + #year"
)
public FeeDetailsDTO getMonthlyFee(Long studentId, int month, int year) {
    return feeCalculationService.calculateMonthlyFee(studentId, month, year);
}
```

### 7. Low-Level Redis Operations

**Using RedisTemplate directly**:
```java
@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheStudentCount(Long count) {
        redisTemplate.opsForValue().set("stats:student:count", count, Duration.ofMinutes(5));
    }

    public Long getStudentCount() {
        return (Long) redisTemplate.opsForValue().get("stats:student:count");
    }

    public void incrementLoginCount(String username) {
        String key = "login:count:" + username;
        redisTemplate.opsForValue().increment(key);
    }

    public void cacheUserSession(String sessionId, UserSession session) {
        redisTemplate.opsForValue().set(
            "session:" + sessionId,
            session,
            Duration.ofMinutes(30)
        );
    }
}
```

### 8. Cache with Fallback

**Handle cache failures gracefully**:
```java
@Cacheable(value = "students", key = "#studentCode")
public StudentDTO getStudent(String studentCode) {
    try {
        // Try to get from cache (handled by Spring)
        return studentRepository.findByStudentCode(studentCode)
                .map(studentMapper::toDTO)
                .orElseThrow();
    } catch (Exception e) {
        // Cache error handler logs the error
        // Method still executes and returns result
        log.warn("Cache error, falling back to DB", e);
        throw e;
    }
}
```

---

## Best Practices

### 1. Cache Granularity

**Good: Fine-grained caching**
```java
@Cacheable(value = "students", key = "#studentId")
public StudentDTO getStudent(Long studentId) { ... }
```

**Bad: Coarse-grained caching**
```java
@Cacheable(value = "students", key = "'all'")
public List<StudentDTO> getAllStudents() { ... }  // Don't cache entire lists
```

### 2. Cache Key Design

**Good: Specific, unique keys**
```java
key = "'student:' + #studentId + ':year:' + #year"
```

**Bad: Generic keys**
```java
key = "#studentId"  // Ambiguous, could conflict
```

### 3. TTL Selection

- **Frequently changing data**: Short TTL (1-2 hours)
  - Student attendance, daily reports
- **Stable data**: Long TTL (12-24 hours)
  - Academic years, fee structures, configurations
- **Real-time data**: Don't cache or very short TTL (< 5 minutes)
  - Current enrollment count, live dashboards

### 4. Cache Eviction Strategy

**Always evict on updates**:
```java
@CacheEvict(value = "students", key = "#studentId")
public StudentDTO updateStudent(Long studentId, UpdateRequest request) { ... }

@CacheEvict(value = "students", key = "#studentId")
public void deleteStudent(Long studentId) { ... }
```

### 5. Avoid Caching Null Values

Configuration already set to `cache-null-values: false`.

This prevents cache stampede when querying non-existent entities.

### 6. Use Specific Cache Names

**Good**:
```java
@Cacheable(value = "students", ...)
@Cacheable(value = "fees", ...)
```

**Bad**:
```java
@Cacheable(value = "default", ...)  // Too generic
```

### 7. Monitor Cache Hit Ratio

Aim for > 70% cache hit ratio for frequently accessed data.

### 8. Handle Large Objects

**Don't cache very large objects** (> 1MB):
```java
// Bad: Caching large report
@Cacheable(value = "reports", key = "#year")
public byte[] generateAnnualReport(int year) { ... }  // Could be several MB

// Good: Cache report metadata, store file separately
@Cacheable(value = "reportMetadata", key = "#year")
public ReportMetadata getReportMetadata(int year) { ... }
```

---

## Monitoring

### Health Checks

Redis health is automatically monitored via Spring Boot Actuator:

```bash
# Check Redis health
curl http://localhost:8080/api/actuator/health/redis

# Response
{
  "status": "UP",
  "details": {
    "version": "7.2.0"
  }
}
```

### Metrics

Monitor these metrics:

1. **Cache Hit Ratio**
   ```
   cache.gets(cache=students,result=hit) / cache.gets(cache=students)
   ```

2. **Cache Evictions**
   ```
   cache.evictions(cache=students)
   ```

3. **Cache Size**
   ```
   cache.size(cache=students)
   ```

4. **Redis Memory Usage**
   ```bash
   redis-cli INFO memory
   ```

5. **Connection Pool**
   ```
   lettuce.pool.active
   lettuce.pool.idle
   lettuce.pool.wait
   ```

### Redis CLI Commands

```bash
# Connect to Redis
redis-cli -h localhost -p 6379

# View all keys with prefix
redis-cli KEYS "sms:students::*"

# Get cache entry
redis-cli GET "sms:students::STU-2024-00001"

# Check TTL
redis-cli TTL "sms:students::STU-2024-00001"

# View cache size
redis-cli DBSIZE

# Clear all cache (DANGEROUS!)
redis-cli FLUSHDB

# Monitor commands in real-time
redis-cli MONITOR
```

---

## Troubleshooting

### Issue 1: Connection Refused

**Symptom**: `Connection refused: localhost/127.0.0.1:6379`

**Solutions**:
```bash
# Check if Redis is running
redis-cli ping

# Start Redis (Linux)
sudo systemctl start redis

# Start Redis (Docker)
docker run -d -p 6379:6379 redis:7.2-alpine

# Check Redis logs
tail -f /var/log/redis/redis-server.log
```

### Issue 2: Cache Not Working

**Symptom**: Methods always execute, cache not used

**Checklist**:
- [ ] `@EnableCaching` on configuration class
- [ ] `@Cacheable` annotation on public method
- [ ] Method not called from same class (proxy limitation)
- [ ] Redis server is running
- [ ] No exceptions in cache error handler logs

**Solution**:
```java
// Bad: Called from same class (proxy bypass)
public class StudentService {
    public StudentDTO getStudent(Long id) {
        return getStudentCached(id);  // Won't use cache!
    }

    @Cacheable("students")
    private StudentDTO getStudentCached(Long id) { ... }
}

// Good: Called from another class
public StudentDTO getStudent(Long id) {
    return studentCacheService.getStudentCached(id);  // Uses cache
}
```

### Issue 3: Serialization Errors

**Symptom**: `SerializationException: Could not read JSON`

**Cause**: Class structure changed, old cached data incompatible

**Solutions**:
```bash
# Option 1: Clear cache
redis-cli FLUSHDB

# Option 2: Clear specific cache
redis-cli KEYS "sms:students::*" | xargs redis-cli DEL

# Option 3: Wait for TTL expiration
```

**Prevention**:
- Use shorter TTL during development
- Version cache keys: `sms:students:v2::{key}`

### Issue 4: Memory Issues

**Symptom**: Redis running out of memory

**Solutions**:
```bash
# Check memory usage
redis-cli INFO memory

# Set maxmemory policy
redis-cli CONFIG SET maxmemory 2gb
redis-cli CONFIG SET maxmemory-policy allkeys-lru

# Manual eviction
redis-cli FLUSHDB
```

### Issue 5: Slow Performance

**Symptom**: Cache operations taking too long

**Causes & Solutions**:

1. **Large objects**: Don't cache objects > 1MB
2. **Network latency**: Use Redis on same network/server
3. **Serialization overhead**: Consider MessagePack instead of JSON
4. **Connection pool exhausted**: Increase `max-active` setting

---

## Performance Tuning

### Connection Pool Tuning

```yaml
spring:
  redis:
    lettuce:
      pool:
        # Set based on expected concurrent requests
        max-active: 50

        # Keep idle connections for quick reuse
        min-idle: 10
        max-idle: 20

        # Don't wait too long for connection
        max-wait: 1000ms

      # Graceful shutdown
      shutdown-timeout: 200ms
```

### Redis Configuration

**redis.conf tuning**:
```conf
# Memory management
maxmemory 4gb
maxmemory-policy allkeys-lru

# Persistence (RDB)
save 900 1
save 300 10
save 60 10000

# AOF persistence (for durability)
appendonly yes
appendfsync everysec

# Performance
tcp-keepalive 300
timeout 300
```

### Cache Optimization

1. **Use Partial Indexes**
   ```java
   @Cacheable(
       value = "students",
       condition = "#result != null && #result.isActive()"
   )
   ```

2. **Batch Operations**
   ```java
   public List<StudentDTO> getStudentsBatch(List<Long> ids) {
       List<String> keys = ids.stream()
           .map(id -> "sms:students::" + id)
           .collect(Collectors.toList());

       List<Object> values = redisTemplate.opsForValue().multiGet(keys);
       // Process cached and missing items
   }
   ```

3. **Cache Warming**
   ```java
   @EventListener(ApplicationReadyEvent.class)
   public void warmCache() {
       // Pre-load frequently accessed data
       List<Configuration> configs = configRepository.findAll();
       configs.forEach(config ->
           cacheManager.getCache("configurations").put(config.getKey(), config)
       );
   }
   ```

### Monitoring and Alerting

**Set up alerts for**:
- Cache hit ratio < 50%
- Redis memory > 80%
- Connection pool exhaustion
- Cache operation latency > 100ms

---

## Production Checklist

- [ ] Redis password configured
- [ ] SSL/TLS enabled for Redis connection
- [ ] maxmemory and eviction policy set
- [ ] Persistence configured (RDB + AOF)
- [ ] Connection pool sized appropriately
- [ ] Monitoring and alerting configured
- [ ] Backup strategy in place
- [ ] Failover/high availability configured (Redis Sentinel/Cluster)
- [ ] Cache warming strategy implemented
- [ ] Documentation updated

---

## Additional Resources

- [Redis Documentation](https://redis.io/documentation)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [Lettuce Documentation](https://lettuce.io/core/release/reference/)
- [Redis Best Practices](https://redis.io/topics/optimization)

---

**Document Version**: 1.0.0
**Last Updated**: 2025-11-11
**Maintained By**: School Management System Development Team
