# Redis Caching Strategy - School Management System

## Overview

This document describes the Redis caching implementation for the School Management System backend microservices.

**Version:** 1.0.0
**Last Updated:** 2025-12-06
**Status:** Production Ready

---

## Table of Contents

1. [Architecture](#architecture)
2. [Cache Configuration](#cache-configuration)
3. [Cache Key Naming Conventions](#cache-key-naming-conventions)
4. [TTL Strategy](#ttl-strategy)
5. [Cache Eviction Strategy](#cache-eviction-strategy)
6. [Monitoring and Metrics](#monitoring-and-metrics)
7. [Administration](#administration)
8. [Troubleshooting](#troubleshooting)
9. [Recovery Procedures](#recovery-procedures)

---

## Architecture

### Redis Setup

- **Image:** redis:7-alpine
- **Port:** 6379
- **Max Memory:** 256MB
- **Eviction Policy:** allkeys-lru (Least Recently Used)
- **Persistence:** AOF (Append-Only File) + RDB (Snapshots)

### Database Separation

Following LESSONS_LEARNED.md [D-003], services use separate Redis databases:

- **Database 0:** student-service
- **Database 1:** configuration-service

This separation provides:
- Isolation between services
- Independent cache clearing
- Easier debugging and monitoring
- Reduced key collision risk

---

## Cache Configuration

### Student Service

**Database:** 0
**Key Prefix:** `sms:student:`
**Serialization:** Jackson2Json

#### Cache Names and TTLs

| Cache Name | TTL | Description |
|------------|-----|-------------|
| `students` | 3600s (1 hour) | Individual student records |
| `studentSearchResults` | 900s (15 minutes) | Search query results |
| `activeStudentsCount` | 900s (15 minutes) | Count of active students |
| `enrollments` | 3600s (1 hour) | Student enrollment history |

### Configuration Service

**Database:** 1
**Key Prefix:** `sms:config:`
**Serialization:** Jackson2Json

#### Cache Names and TTLs

| Cache Name | TTL | Description |
|------------|-----|-------------|
| `configurations` | 14400s (4 hours) | Individual configuration settings |
| `configByCategory` | 14400s (4 hours) | All configs in a category |
| `groupedConfigs` | 14400s (4 hours) | Grouped configuration maps |

---

## Cache Key Naming Conventions

Following LESSONS_LEARNED.md [D-004], all cache keys follow consistent patterns:

### Student Service Patterns

```
sms:student:id:{studentId}                    # Student by ID
sms:student:search:{md5hash}                  # Search results
sms:student:count:active                      # Active student count
sms:student:enrollment:{studentId}            # Student enrollments
```

### Configuration Service Patterns

```
sms:config:category:{category}:key:{key}      # Specific configuration
sms:config:category:{category}:all            # All configs in category
sms:config:grouped:{category}                 # Grouped configurations
```

### Key Length Constraint

All keys are kept **under 100 characters** for optimal performance. Keys exceeding this limit are hashed using MD5.

---

## TTL Strategy

### TTL Selection Criteria

TTLs are chosen based on data volatility:

**Stable Data (1-4 hours):**
- Student records
- Configuration settings
- Enrollment history

**Dynamic Data (5-15 minutes):**
- Search results
- Count aggregations
- Frequently changing data

### TTL Configuration

TTLs are configured in:
- `RedisConfig.java` (programmatic)
- `application.yml` (default TTL)

```yaml
spring:
  cache:
    redis:
      time-to-live: 3600000  # Default: 1 hour
```

---

## Cache Eviction Strategy

Following LESSONS_LEARNED.md [D-005], cache eviction ensures data consistency:

### Eviction on Updates

When a student is updated:
```java
@CacheEvict(value = {"students", "studentSearchResults", "activeStudentsCount"},
            key = "'id:' + #studentId")
public Student updateStudent(String studentId, UpdateStudentCommand command)
```

### Eviction on Deletes

When a student is deleted:
```java
@CacheEvict(value = {"students", "studentSearchResults", "activeStudentsCount"},
            key = "'id:' + #studentId")
public void deleteStudent(String studentId)
```

### Configuration Service Eviction

Configuration updates evict all related caches:
```java
@CacheEvict(value = {"configurations", "configByCategory", "groupedConfigs"},
            allEntries = true)
public ConfigurationResponse createOrUpdate(...)
```

### Scheduled Cleanup

**Student Service:**
- Search results: Cleared hourly
- Count cache: Cleared every 30 minutes
- All caches: Daily at 2:00 AM

**Configuration Service:**
- All caches: Daily at 3:00 AM

---

## Monitoring and Metrics

Following LESSONS_LEARNED.md [D-007], comprehensive metrics are tracked:

### Micrometer Metrics

| Metric | Type | Target | Alert Threshold |
|--------|------|--------|-----------------|
| `cache.hits.total` | Counter | N/A | N/A |
| `cache.misses.total` | Counter | N/A | N/A |
| `cache.hit.ratio` | Gauge | >80% | <70% |
| `cache.evictions.total` | Counter | N/A | N/A |
| `redis.memory.used.bytes` | Gauge | <200MB | >200MB |

### Prometheus Endpoint

Metrics are exported at: `http://localhost:8081/actuator/prometheus`

### Grafana Dashboard

Import dashboard: `docs/grafana/cache-dashboard.json`

**Dashboard Features:**
- Cache hit ratio over time (with alerts)
- Hits vs misses comparison
- Redis memory usage gauge
- Cache evictions per minute
- Connection pool metrics
- Top cached keys by size

---

## Administration

### Cache Admin Endpoints

Available at `/api/v1/admin/cache`:

#### Clear Specific Cache
```bash
POST http://localhost:8081/api/v1/admin/cache/clear/{cacheName}
```

#### Clear All Caches
```bash
POST http://localhost:8081/api/v1/admin/cache/clear-all
```

#### Get Cache Statistics
```bash
GET http://localhost:8081/api/v1/admin/cache/stats
```

#### List Cache Names
```bash
GET http://localhost:8081/api/v1/admin/cache/names
```

#### Cache Health Check
```bash
GET http://localhost:8081/api/v1/admin/cache/health
```

### Redis CLI Commands

**Connect to Redis:**
```bash
docker exec -it sms-redis redis-cli
```

**Select Database:**
```bash
SELECT 0  # Student service
SELECT 1  # Configuration service
```

**View Keys:**
```bash
KEYS sms:student:*
KEYS sms:config:*
```

**Get Key TTL:**
```bash
TTL sms:student:id:STD-20241206-0001
```

**Memory Usage:**
```bash
INFO memory
```

---

## Troubleshooting

### Common Issues

#### Issue: Cache Hit Ratio Below 70%

**Symptoms:**
- Grafana alert triggered
- Slow response times

**Diagnosis:**
```bash
# Check cache statistics
curl http://localhost:8081/api/v1/admin/cache/stats

# Check Redis memory
docker exec sms-redis redis-cli INFO memory
```

**Solutions:**
1. Review TTL settings (may be too short)
2. Check if cache is being evicted prematurely
3. Verify cache warmup is executing
4. Increase maxmemory if needed

#### Issue: Redis Memory Exceeds 200MB

**Symptoms:**
- Memory alert triggered
- Cache evictions increasing

**Diagnosis:**
```bash
# Check memory usage
docker exec sms-redis redis-cli INFO memory

# Check largest keys
docker exec sms-redis redis-cli --bigkeys
```

**Solutions:**
1. Review cached data size
2. Reduce TTLs for large objects
3. Clear unnecessary caches
4. Increase maxmemory limit

#### Issue: Redis Unavailable

**Symptoms:**
- Health check failing
- Circuit breaker open
- Requests falling back to database

**Diagnosis:**
```bash
# Check Redis container
docker ps | grep sms-redis

# Check Redis logs
docker logs sms-redis

# Test connection
docker exec sms-redis redis-cli ping
```

**Solutions:**
1. Restart Redis container: `docker restart sms-redis`
2. Check network connectivity
3. Verify Redis configuration
4. Review application logs for connection errors

---

## Recovery Procedures

Following LESSONS_LEARNED.md [D-008]:

### Immediate Rollback Plan

If Redis causes production issues, disable caching immediately:

**Option 1: Environment Variable**
```bash
export SPRING_CACHE_TYPE=none
```

**Option 2: Application Property**
```yaml
spring:
  cache:
    type: none
```

**Option 3: Restart Services**
```bash
# Stop services
./scripts/stop-services.sh

# Set environment variable
export SPRING_CACHE_TYPE=none

# Start services
./scripts/start-services.sh
```

### Redis Data Recovery

Redis uses dual persistence (AOF + RDB):

**Restore from AOF:**
```bash
# Stop Redis
docker stop sms-redis

# Restore appendonly.aof
docker cp backup/appendonly.aof sms-redis:/data/

# Start Redis
docker start sms-redis
```

**Restore from RDB:**
```bash
# Stop Redis
docker stop sms-redis

# Restore dump.rdb
docker cp backup/dump.rdb sms-redis:/data/

# Start Redis
docker start sms-redis
```

### Cache Warmup After Recovery

Cache is automatically warmed up on application startup via `CacheWarmupService`.

Manual warmup:
```bash
# Clear all caches
curl -X POST http://localhost:8081/api/v1/admin/cache/clear-all

# Restart application (triggers warmup)
./scripts/restart-student-service.sh
```

---

## Circuit Breaker Configuration

Following LESSONS_LEARNED.md [D-008], circuit breaker provides graceful degradation:

**Configuration:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      redis:
        slidingWindowSize: 10
        failureRateThreshold: 50%
        waitDurationInOpenState: 30s
```

**Behavior:**
- **Closed:** Normal operation, cache used
- **Open:** Redis failures detected, fallback to database
- **Half-Open:** Testing if Redis recovered

---

## Performance Targets

| Metric | Target | Alert Threshold |
|--------|--------|-----------------|
| Cache Hit Ratio | >80% | <70% |
| Response Time (cached) | <10ms | >50ms |
| Response Time (uncached) | <100ms | >200ms |
| Redis Memory Usage | <200MB | >200MB |
| Connection Pool Exhaustion | 0 | >0 |

---

## References

- LESSONS_LEARNED.md [D-003]: Redis Cache Configuration
- LESSONS_LEARNED.md [D-004]: Cache Key Management
- LESSONS_LEARNED.md [D-005]: Cache Eviction Strategy
- LESSONS_LEARNED.md [D-006]: Redis Connection Pool Configuration
- LESSONS_LEARNED.md [D-007]: Cache Monitoring and Observability
- LESSONS_LEARNED.md [D-008]: Handling Redis Failures

---

**Document Version:** 1.0.0
**Last Updated:** 2025-12-06
**Maintained By:** SMS Backend Team
