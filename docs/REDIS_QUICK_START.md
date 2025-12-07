# Redis Caching - Quick Start Guide

## For Developers

This guide provides quick reference for implementing and working with Redis caching in the School Management System.

---

## Prerequisites

- Docker and Docker Compose installed
- Student Service and Configuration Service implemented
- PostgreSQL database running
- Maven 3.9+

---

## Quick Setup (5 Minutes)

### 1. Start Redis

```bash
# Navigate to project root
cd D:\wks-sms-specs-itr3

# Start Redis using Docker Compose
docker-compose up -d redis

# Verify Redis is running
docker exec redis redis-cli ping
# Expected output: PONG
```

### 2. Add Dependencies

Add to your service's `pom.xml`:

```xml
<dependencies>
    <!-- Spring Data Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- Connection Pooling -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>

    <!-- Spring Cache -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
</dependencies>
```

### 3. Configure Redis

Add to `application.yml`:

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour
      cache-null-values: false
      key-prefix: "sms:student:"
      use-key-prefix: true

  data:
    redis:
      host: localhost
      port: 6379
      database: 0  # Use 0 for student-service, 1 for config-service
      timeout: 60000
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
```

### 4. Enable Caching

Add to your main application class:

```java
@SpringBootApplication
@EnableCaching  // <- Add this annotation
public class StudentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
```

### 5. Test It

```bash
# Check health endpoint
curl http://localhost:8081/actuator/health

# Should show Redis status:
{
  "status": "UP",
  "components": {
    "redis": {
      "status": "UP",
      "details": {
        "redis": "Connected"
      }
    }
  }
}
```

---

## Common Caching Patterns

### Pattern 1: Cacheable Read Operation

```java
@Service
public class StudentApplicationService {

    @Cacheable(value = "students", key = "#studentId")
    public Student getStudent(String studentId) {
        // This method result will be cached
        // Next call with same studentId will return from cache
        return studentRepository.findByStudentId(StudentId.of(studentId))
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }
}
```

**What happens:**
1. First call: Executes method, stores result in cache
2. Subsequent calls: Returns from cache (no database hit)
3. Cache key: `sms:student:STD-20241206-0001`

---

### Pattern 2: Cache Update on Write

```java
@Service
public class StudentApplicationService {

    @CachePut(value = "students", key = "#result.studentId.value")
    public Student createStudent(CreateStudentCommand command) {
        // Method always executes
        // Result is stored in cache
        Student student = Student.createNew(/* ... */);
        return studentRepository.save(student);
    }
}
```

**What happens:**
1. Method always executes
2. Result is stored in cache
3. Future reads will use cached value

---

### Pattern 3: Cache Eviction on Update/Delete

```java
@Service
public class StudentApplicationService {

    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        // Cache entry is removed before method execution
        Student student = getStudent(studentId);
        studentRepository.delete(student);
    }

    @CacheEvict(
        value = {"students", "studentSearchResults"},
        key = "#command.studentId"
    )
    public Student updateStudent(UpdateStudentCommand command) {
        // Evicts from multiple caches
        // Fresh data will be cached on next read
        // Implementation...
    }
}
```

**What happens:**
1. Cache entry removed before method executes
2. Method executes
3. Next read will fetch fresh data from database

---

### Pattern 4: Multiple Cache Eviction

```java
@Service
public class StudentApplicationService {

    @CacheEvict(
        value = {
            "students",              // Individual student cache
            "studentSearchResults",  // Search results cache
            "activeStudentsCount"    // Count cache
        },
        key = "#studentId"
    )
    public void updateStudentStatus(String studentId, StudentStatus newStatus) {
        // Evicts from ALL related caches
        // Ensures consistency across all cache layers
    }
}
```

---

## Cache Key Patterns

### Recommended Key Formats

```java
// Student by ID
"student:id:STD-20241206-0001"

// Student by mobile
"student:mobile:9876543210"

// Student search results (use hash)
"student:search:abc123def456"

// Configuration by category
"config:category:GENERAL"

// Configuration by key
"config:key:GENERAL:school.name"

// Active count
"student:count:active"
```

### Custom Key Generator

```java
@Component("customKeyGenerator")
public class CustomCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + ":" +
               method.getName() + ":" +
               StringUtils.arrayToDelimitedString(params, ":");
    }
}

// Usage
@Cacheable(value = "students", keyGenerator = "customKeyGenerator")
public List<Student> searchStudents(String lastName, String status) {
    // Custom key will be: StudentApplicationService:searchStudents:Doe:ACTIVE
}
```

---

## Redis Configuration Class

Complete Redis configuration example:

```java
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON serializer for values
        Jackson2JsonRedisSerializer<Object> serializer =
            new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        serializer.setObjectMapper(objectMapper);

        // String serializer for keys
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Students cache: 1 hour TTL
        cacheConfigurations.put("students",
            createCacheConfiguration(Duration.ofHours(1)));

        // Search results: 15 minutes TTL
        cacheConfigurations.put("studentSearchResults",
            createCacheConfiguration(Duration.ofMinutes(15)));

        // Active count: 5 minutes TTL
        cacheConfigurations.put("activeStudentsCount",
            createCacheConfiguration(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(createCacheConfiguration(Duration.ofHours(1)))
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }

    private RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
    }
}
```

---

## Testing Cache Behavior

### Unit Test Example

```java
@SpringBootTest
@Testcontainers
class CachingTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @Autowired
    private StudentApplicationService studentService;

    @Autowired
    private CacheManager cacheManager;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Test
    void shouldCacheStudentOnRead() {
        String studentId = "STD-20241206-0001";

        // First read - from database
        Student student1 = studentService.getStudent(studentId);

        // Verify cache contains student
        Cache cache = cacheManager.getCache("students");
        Student cached = cache.get(studentId, Student.class);

        assertNotNull(cached);
        assertEquals(studentId, cached.getStudentId().getValue());

        // Second read - from cache (verify by checking logs or metrics)
        Student student2 = studentService.getStudent(studentId);
        assertEquals(student1.getStudentId(), student2.getStudentId());
    }

    @Test
    void shouldEvictCacheOnUpdate() {
        String studentId = "STD-20241206-0001";

        // Cache the student
        studentService.getStudent(studentId);

        // Update student
        UpdateStudentCommand command = new UpdateStudentCommand(
            studentId, "Updated", "Name", "9999999999", "ACTIVE", 0L
        );
        studentService.updateStudent(command);

        // Verify cache was evicted
        Cache cache = cacheManager.getCache("students");
        assertNull(cache.get(studentId));
    }
}
```

---

## Monitoring and Debugging

### Check Cache Statistics

```bash
# Get cache statistics via admin endpoint
curl http://localhost:8081/api/v1/admin/cache/stats

# Response:
{
  "students": {
    "size": 150,
    "hitRate": 0.85,
    "ttl": 3600
  },
  "studentSearchResults": {
    "size": 45,
    "hitRate": 0.72,
    "ttl": 900
  }
}
```

### View Cache Keys

```bash
# List all cache keys
curl http://localhost:8081/api/v1/admin/cache/keys

# List keys matching pattern
curl "http://localhost:8081/api/v1/admin/cache/keys?pattern=student:id:*"
```

### Clear Cache

```bash
# Clear specific cache
curl -X DELETE http://localhost:8081/api/v1/admin/cache/students

# Clear all caches
curl -X DELETE http://localhost:8081/api/v1/admin/cache/all
```

### Redis CLI Commands

```bash
# Connect to Redis
docker exec -it redis redis-cli

# View all keys
KEYS sms:student:*

# Get a specific key
GET "sms:student:student:id:STD-20241206-0001"

# Check TTL (time to live)
TTL "sms:student:student:id:STD-20241206-0001"

# Delete a key
DEL "sms:student:student:id:STD-20241206-0001"

# View Redis info
INFO

# Monitor real-time commands
MONITOR
```

---

## Metrics and Monitoring

### Prometheus Metrics

```bash
# View cache hit metrics
curl http://localhost:8081/actuator/metrics/cache.hits

# View cache miss metrics
curl http://localhost:8081/actuator/metrics/cache.misses

# View cache eviction metrics
curl http://localhost:8081/actuator/metrics/cache.evictions

# View cache size
curl http://localhost:8081/actuator/metrics/cache.size
```

### Health Check

```bash
# Check Redis health
curl http://localhost:8081/actuator/health/redis

# Response:
{
  "status": "UP",
  "details": {
    "redis": "Connected",
    "status": "UP"
  }
}
```

---

## Common Issues and Solutions

### Issue 1: Cache Not Working

**Symptoms:**
- Every request hits database
- Cache metrics show 0 hits

**Diagnosis:**
```java
// Check if @EnableCaching is present
@SpringBootApplication
@EnableCaching  // <- Must be present
public class StudentServiceApplication { }

// Check if method is public and in a Spring bean
@Service  // <- Must be a Spring component
public class StudentApplicationService {
    @Cacheable(value = "students", key = "#studentId")  // <- Method must be public
    public Student getStudent(String studentId) { }
}
```

**Common Causes:**
- Missing `@EnableCaching` annotation
- Method not public (Spring AOP requires public methods)
- Calling cached method from same class (use self-invocation workaround)

---

### Issue 2: Serialization Errors

**Symptoms:**
- ClassCastException when retrieving from cache
- Data corruption in cache

**Solution:**
```java
// Ensure proper serialization configuration
@Bean
public RedisTemplate<String, Object> redisTemplate(...) {
    // Use Jackson2JsonRedisSerializer
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(Object.class);

    // Configure ObjectMapper
    ObjectMapper mapper = new ObjectMapper();
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL
    );
    serializer.setObjectMapper(mapper);

    template.setValueSerializer(serializer);
    return template;
}
```

---

### Issue 3: Stale Data in Cache

**Symptoms:**
- Old data returned after updates
- Cache doesn't reflect recent changes

**Solution:**
```java
// Ensure cache eviction on updates
@CacheEvict(value = "students", key = "#studentId")
public Student updateStudent(String studentId, ...) {
    // Implementation
}

// For complex scenarios, evict multiple caches
@CacheEvict(
    value = {"students", "studentSearchResults", "activeStudentsCount"},
    allEntries = true
)
public void bulkUpdateStudents(...) {
    // Implementation
}
```

---

### Issue 4: Redis Connection Failures

**Symptoms:**
- Application startup fails
- Connection timeout errors

**Diagnosis:**
```bash
# Check if Redis is running
docker ps | grep redis

# Check Redis logs
docker logs redis

# Test connection
docker exec redis redis-cli ping
```

**Solution:**
```yaml
# Increase connection timeout
spring:
  data:
    redis:
      timeout: 60000  # 60 seconds

# Configure circuit breaker
resilience4j:
  circuitbreaker:
    instances:
      redis:
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
```

---

## Performance Tips

### 1. Use Appropriate TTLs

```java
// Short TTL for frequently changing data
@Cacheable(value = "activeCount", ttl = 300)  // 5 minutes

// Long TTL for stable data
@Cacheable(value = "students", ttl = 3600)    // 1 hour

// Very long TTL for rarely changing data
@Cacheable(value = "configurations", ttl = 14400)  // 4 hours
```

### 2. Avoid Caching Large Objects

```java
// Bad: Caching entire list
@Cacheable("students")
public List<Student> getAllStudents() {
    return studentRepository.findAll();  // Could be thousands
}

// Good: Cache individual students
@Cacheable(value = "students", key = "#studentId")
public Student getStudent(String studentId) {
    return studentRepository.findById(studentId);  // Single object
}
```

### 3. Use Cache Warming

```java
@Component
public class CacheWarmingService {

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        // Pre-load frequently accessed data
        List<Student> recentStudents = studentRepository.findTop100ByOrderByCreatedAtDesc();
        Cache cache = cacheManager.getCache("students");

        recentStudents.forEach(student -> {
            cache.put(student.getStudentId().getValue(), student);
        });

        log.info("Cache warmed with {} students", recentStudents.size());
    }
}
```

### 4. Monitor Cache Hit Ratio

```java
// Target: >80% hit ratio
// If below 70%, investigate:
// - Are keys consistent?
// - Is TTL too short?
// - Is data too volatile?
// - Are there cache eviction issues?
```

---

## Next Steps

1. Review full implementation plan: `specs/planning/REDIS_CACHING_TASKS.md`
2. Read architecture documentation: `specs/architecture/08-caching-strategy.md`
3. Check lessons learned: `LESSONS_LEARNED.md` (sections D-003 to D-008)
4. Review operations runbook: `docs/operations/redis-runbook.md`

---

## Quick Reference Commands

```bash
# Start Redis
docker-compose up -d redis

# Stop Redis
docker-compose stop redis

# View Redis logs
docker logs -f redis

# Redis CLI
docker exec -it redis redis-cli

# Clear all Redis data (USE WITH CAUTION)
docker exec redis redis-cli FLUSHALL

# Check Redis memory usage
docker exec redis redis-cli INFO memory

# Restart services with cache
./scripts/start-services.sh
```

---

**Last Updated:** 2025-12-06
**Version:** 1.0
