# 1. Backend(High Priority)
## Global Directives
### 1. [D-001] Spring OpenAPI : Always check Spring Boot and SpringDoc compatibility matrix when upgrading Spring Boot versions.Add version compatibility testing to CI/CD pipeline. SpringDoc OpenAPI compatibility reference:
Spring Boot 3.0.x - 3.2.x: SpringDoc 2.0.x - 2.3.x
Spring Boot 3.3.x - 3.5.x: SpringDoc 2.5.x - 2.7.x+

### 2. [D-001] Document all frontend development server ports in README.
Configure CORS to accept requests from all known development ports
Consider using environment-based CORS configuration:
```
@Value("${cors.allowed-origins}")
private String[] allowedOrigins;
```
Add CORS testing to integration tests

### 3. [D-003] Redis Cache Configuration
Always use separate Redis databases for different microservices (database 0 for student-service, database 1 for configuration-service).
Configure appropriate TTLs based on data volatility:
- Stable data (student info, configurations): 1-4 hours
- Dynamic data (search results, counts): 5-15 minutes
Use JSON serialization (Jackson2JsonRedisSerializer) for complex objects to ensure proper deserialization.
Implement cache warmup for frequently accessed data on application startup.
Enable Redis persistence (AOF + RDB) to prevent data loss on restart.

### 4. [D-004] Cache Key Management
Use consistent naming convention for cache keys with service prefix (e.g., sms:student:, sms:config:).
Include entity type in key: student:id:{studentId}, config:category:{category}
Use hash-based keys for search results to avoid key collisions: student:search:{md5hash}
Avoid overly long key names (keep under 100 characters for performance).
Document all cache key patterns in code comments and architecture docs.

### 5. [D-005] Cache Eviction Strategy
Always evict related caches on updates to prevent stale data:
```java
@CacheEvict(value = {"students", "studentSearchResults", "activeStudentsCount"}, key = "#studentId")
public void updateStudent(String studentId) { ... }
```
Use @CacheEvict(allEntries=true) sparingly - only when necessary to clear entire cache.
Implement scheduled cleanup for stale entries (e.g., clear search results cache hourly).
Monitor cache size and hit ratio continuously - alert if hit ratio drops below 70%.
Always include optimistic locking version in cache keys when applicable.

### 6. [D-006] Redis Connection Pool Configuration
Configure connection pool with appropriate limits based on expected load:
```yaml
spring.data.redis.lettuce.pool:
  max-active: 20  # Maximum connections
  max-idle: 10    # Maximum idle connections
  min-idle: 5     # Minimum idle connections
  max-wait: -1ms  # Wait indefinitely for connection
```
Use Lettuce client (default) over Jedis for better async support.
Set connection timeout to 60 seconds to handle network latency.
Monitor connection pool metrics - alert if pool exhaustion occurs.

### 7. [D-007] Cache Monitoring and Observability
Implement comprehensive cache metrics using Micrometer:
- cache.hits.total (Counter)
- cache.misses.total (Counter)
- cache.hit.ratio (Gauge) - Target >80%
- cache.evictions.total (Counter)
- redis.memory.used (Gauge) - Alert if >200MB

Create Grafana dashboards for cache performance visualization.
Add health indicators for Redis connectivity.
Implement cache event listeners to log cache operations at DEBUG level.
Export metrics to Prometheus for alerting and historical analysis.

### 8. [D-008] Handling Redis Failures
Implement circuit breaker pattern for Redis failures to fallback to database:
```java
@CircuitBreaker(name = "redis", fallbackMethod = "fallbackToDatabase")
public Optional<Student> getStudent(String studentId) { ... }
```
Configure Redis maxmemory and eviction policy (allkeys-lru recommended).
Test application behavior when Redis is unavailable - ensure graceful degradation.
Implement rollback plan: Set spring.cache.type=none to disable caching immediately.
Document Redis recovery procedures in operations runbook.

### 9. [D-009] PostgresSQL DB Creation Docker.
While generating the docker postgres db just use the username/password from the environement variables. Use the port 5433 instead of default port.

# 1. Frontend(High Priority)
## Global Directives
### 1. [D-001] Document all frontend development server ports in README.
Configure CORS to accept requests from all known development ports
Add CORS testing to integration tests
Check browser console during initial frontend-backend integration
---