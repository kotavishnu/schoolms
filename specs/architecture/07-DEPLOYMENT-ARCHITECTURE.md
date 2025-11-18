# Deployment Architecture

## Table of Contents
1. [Deployment Overview](#deployment-overview)
2. [Container Strategy](#container-strategy)
3. [Local Development Environment](#local-development-environment)
4. [Production Deployment](#production-deployment)
5. [CI/CD Pipeline](#cicd-pipeline)
6. [Monitoring and Health Checks](#monitoring-and-health-checks)
7. [Backup and Recovery](#backup-and-recovery)
8. [Scalability Considerations](#scalability-considerations)

## Deployment Overview

### Deployment Architecture Diagram

```mermaid
graph TB
    subgraph "Load Balancer Layer"
        LB[Load Balancer<br/>NGINX/HAProxy]
    end

    subgraph "Application Layer"
        GW1[API Gateway Instance 1]
        GW2[API Gateway Instance 2]

        SS1[Student Service Instance 1]
        SS2[Student Service Instance 2]

        CS1[Configuration Service Instance 1]

        EUR[Eureka Server]
    end

    subgraph "Data Layer"
        SDB[Student DB<br/>PostgreSQL]
        CDB[Configuration DB<br/>PostgreSQL]

        SDB_Replica[Student DB Replica<br/>Read-Only]
    end

    subgraph "Infrastructure"
        Prom[Prometheus]
        Graf[Grafana]
        ELK[ELK Stack]
    end

    LB --> GW1
    LB --> GW2

    GW1 --> SS1
    GW1 --> SS2
    GW1 --> CS1
    GW2 --> SS1
    GW2 --> SS2
    GW2 --> CS1

    SS1 --> SDB
    SS2 --> SDB
    SS1 -.Read.-> SDB_Replica
    SS2 -.Read.-> SDB_Replica

    CS1 --> CDB

    SS1 --> EUR
    SS2 --> EUR
    CS1 --> EUR
    GW1 --> EUR
    GW2 --> EUR

    SS1 --> Prom
    SS2 --> Prom
    CS1 --> Prom
    GW1 --> Prom
    GW2 --> Prom

    Prom --> Graf

    SS1 --> ELK
    SS2 --> ELK
    CS1 --> ELK

    style "Application Layer" fill:#4CAF50,color:#fff
    style "Data Layer" fill:#2196F3,color:#fff
    style "Infrastructure" fill:#FF9800,color:#fff
```

### Deployment Environments

| Environment | Purpose | Configuration |
|-------------|---------|---------------|
| **Development** | Local development | Docker Compose, single instances |
| **Staging** | Pre-production testing | Docker Swarm or Kubernetes, mirrors production |
| **Production** | Live system | Kubernetes or Docker Swarm, redundant, monitored |

## Container Strategy

### Containerization Benefits

1. **Consistency**: Same environment across dev/staging/production
2. **Isolation**: Each service in its own container
3. **Scalability**: Easy horizontal scaling
4. **Portability**: Run anywhere Docker runs
5. **Resource Efficiency**: Lightweight compared to VMs

### Docker Images

#### Multi-Stage Build Strategy

**Student Service Dockerfile**:
```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from build stage
COPY --from=build /app/target/student-service-*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
```

**Configuration Service Dockerfile**:
```dockerfile
# Similar structure to Student Service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/configuration-service-*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
```

**API Gateway Dockerfile**:
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/api-gateway-*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
```

### Image Optimization

**Best Practices**:
1. **Multi-stage builds**: Separate build and runtime
2. **Minimal base images**: Use Alpine Linux
3. **Layer caching**: Order COPY commands strategically
4. **Non-root user**: Run as non-privileged user
5. **Health checks**: Built-in container health monitoring

**Image Size Comparison**:
- JDK image: ~400MB
- JRE Alpine image: ~180MB
- After multi-stage build: ~200MB (includes application)

## Local Development Environment

### Docker Compose Setup

**Complete docker-compose.yml**:
```yaml
version: '3.8'

services:
  # Service Registry
  eureka-server:
    build: ./eureka-server
    container_name: eureka-server
    ports:
      - "8761:8761"
    networks:
      - sms-network
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  # API Gateway
  api-gateway:
    build: ./api-gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    depends_on:
      eureka-server:
        condition: service_healthy
    networks:
      - sms-network
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  # Student Database
  student-db:
    image: postgres:15-alpine
    container_name: student-db
    ports:
      - "5432:5432"
    volumes:
      - student-data:/var/lib/postgresql/data
      - ./init-scripts/student-db:/docker-entrypoint-initdb.d
    networks:
      - sms-network
    environment:
      - POSTGRES_DB=student_db
      - POSTGRES_USER=student_user
      - POSTGRES_PASSWORD=student_pass
      - PGDATA=/var/lib/postgresql/data/pgdata
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U student_user -d student_db"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  # Student Service
  student-service:
    build: ./student-service
    container_name: student-service
    ports:
      - "8081:8081"
    depends_on:
      student-db:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    networks:
      - sms-network
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://student-db:5432/student_db
      - SPRING_DATASOURCE_USERNAME=student_user
      - SPRING_DATASOURCE_PASSWORD=student_pass
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
      - JAVA_OPTS=-Xmx512m -Xms256m
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # Configuration Database
  config-db:
    image: postgres:15-alpine
    container_name: config-db
    ports:
      - "5433:5432"
    volumes:
      - config-data:/var/lib/postgresql/data
      - ./init-scripts/config-db:/docker-entrypoint-initdb.d
    networks:
      - sms-network
    environment:
      - POSTGRES_DB=configuration_db
      - POSTGRES_USER=config_user
      - POSTGRES_PASSWORD=config_pass
      - PGDATA=/var/lib/postgresql/data/pgdata
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U config_user -d configuration_db"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  # Configuration Service
  configuration-service:
    build: ./configuration-service
    container_name: configuration-service
    ports:
      - "8082:8082"
    depends_on:
      config-db:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    networks:
      - sms-network
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://config-db:5432/configuration_db
      - SPRING_DATASOURCE_USERNAME=config_user
      - SPRING_DATASOURCE_PASSWORD=config_pass
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
      - JAVA_OPTS=-Xmx256m -Xms128m
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8082/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # Prometheus
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    networks:
      - sms-network
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'

  # Grafana
  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - grafana-data:/var/lib/grafana
      - ./monitoring/grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./monitoring/grafana/datasources:/etc/grafana/provisioning/datasources
    networks:
      - sms-network
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false

networks:
  sms-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.25.0.0/16

volumes:
  student-data:
  config-data:
  prometheus-data:
  grafana-data:
```

### Environment-Specific Configurations

**application-docker.yml** (for Docker environment):
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}
  instance:
    prefer-ip-address: true
    hostname: ${spring.application.name}

logging:
  level:
    root: INFO
    com.school.sms: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### Development Commands

**Start all services**:
```bash
docker-compose up -d
```

**View logs**:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f student-service
```

**Rebuild and restart**:
```bash
docker-compose up -d --build student-service
```

**Stop all services**:
```bash
docker-compose down
```

**Clean up (including volumes)**:
```bash
docker-compose down -v
```

## Production Deployment

### Kubernetes Deployment

**Namespace**:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: sms-production
```

**ConfigMap** (Shared Configuration):
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: sms-config
  namespace: sms-production
data:
  eureka.server.url: "http://eureka-server:8761/eureka/"
  prometheus.scrape.interval: "15s"
```

**Secret** (Database Credentials):
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: sms-production
type: Opaque
data:
  student-db-username: c3R1ZGVudF91c2Vy  # base64 encoded
  student-db-password: c3R1ZGVudF9wYXNz  # base64 encoded
  config-db-username: Y29uZmlnX3VzZXI=    # base64 encoded
  config-db-password: Y29uZmlnX3Bhc3M=    # base64 encoded
```

**Student Service Deployment**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: student-service
  namespace: sms-production
  labels:
    app: student-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: student-service
  template:
    metadata:
      labels:
        app: student-service
    spec:
      containers:
      - name: student-service
        image: school-sms/student-service:1.0.0
        ports:
        - containerPort: 8081
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://student-db-service:5432/student_db"
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: student-db-username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: student-db-password
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          valueFrom:
            configMapKeyRef:
              name: sms-config
              key: eureka.server.url
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 40
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: student-service
  namespace: sms-production
spec:
  selector:
    app: student-service
  ports:
  - protocol: TCP
    port: 8081
    targetPort: 8081
  type: ClusterIP
```

**Student Database StatefulSet**:
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: student-db
  namespace: sms-production
spec:
  serviceName: student-db-service
  replicas: 1
  selector:
    matchLabels:
      app: student-db
  template:
    metadata:
      labels:
        app: student-db
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
          name: postgres
        env:
        - name: POSTGRES_DB
          value: student_db
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: student-db-username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: student-db-password
        - name: PGDATA
          value: /var/lib/postgresql/data/pgdata
        volumeMounts:
        - name: student-db-storage
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - student_user
          initialDelaySeconds: 30
          periodSeconds: 10
  volumeClaimTemplates:
  - metadata:
      name: student-db-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 20Gi
---
apiVersion: v1
kind: Service
metadata:
  name: student-db-service
  namespace: sms-production
spec:
  selector:
    app: student-db
  ports:
  - protocol: TCP
    port: 5432
    targetPort: 5432
  clusterIP: None  # Headless service for StatefulSet
```

**API Gateway with LoadBalancer**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: sms-production
spec:
  replicas: 2
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: school-sms/api-gateway:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          valueFrom:
            configMapKeyRef:
              name: sms-config
              key: eureka.server.url
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: sms-production
spec:
  selector:
    app: api-gateway
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

### Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: student-service-hpa
  namespace: sms-production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: student-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## CI/CD Pipeline

### GitHub Actions Workflow

**.github/workflows/ci-cd.yml**:
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn clean install

    - name: Run tests
      run: mvn test

    - name: Generate test coverage report
      run: mvn jacoco:report

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3

    - name: Run security scan
      run: mvn org.owasp:dependency-check-maven:check

  build-docker-images:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    strategy:
      matrix:
        service: [student-service, configuration-service, api-gateway, eureka-server]

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: ./${{ matrix.service }}
        push: true
        tags: school-sms/${{ matrix.service }}:${{ github.sha }},school-sms/${{ matrix.service }}:latest
        cache-from: type=registry,ref=school-sms/${{ matrix.service }}:buildcache
        cache-to: type=registry,ref=school-sms/${{ matrix.service }}:buildcache,mode=max

  deploy-to-staging:
    needs: build-docker-images
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up kubectl
      uses: azure/setup-kubectl@v3

    - name: Configure kubectl
      run: |
        echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > kubeconfig
        export KUBECONFIG=./kubeconfig

    - name: Deploy to staging
      run: |
        kubectl set image deployment/student-service \
          student-service=school-sms/student-service:${{ github.sha }} \
          -n sms-staging

        kubectl set image deployment/configuration-service \
          configuration-service=school-sms/configuration-service:${{ github.sha }} \
          -n sms-staging

    - name: Wait for rollout
      run: |
        kubectl rollout status deployment/student-service -n sms-staging
        kubectl rollout status deployment/configuration-service -n sms-staging

  deploy-to-production:
    needs: deploy-to-staging
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment:
      name: production
      url: https://sms.school.com

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up kubectl
      uses: azure/setup-kubectl@v3

    - name: Configure kubectl
      run: |
        echo "${{ secrets.KUBE_CONFIG_PROD }}" | base64 -d > kubeconfig
        export KUBECONFIG=./kubeconfig

    - name: Deploy to production
      run: |
        kubectl set image deployment/student-service \
          student-service=school-sms/student-service:${{ github.sha }} \
          -n sms-production

        kubectl set image deployment/configuration-service \
          configuration-service=school-sms/configuration-service:${{ github.sha }} \
          -n sms-production

    - name: Wait for rollout
      run: |
        kubectl rollout status deployment/student-service -n sms-production
        kubectl rollout status deployment/configuration-service -n sms-production

    - name: Run smoke tests
      run: |
        ./scripts/smoke-tests.sh https://api.sms.school.com
```

## Monitoring and Health Checks

### Health Check Endpoints

**Liveness Probe**: Is the application running?
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8081
  initialDelaySeconds: 60
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

**Readiness Probe**: Is the application ready to serve traffic?
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8081
  initialDelaySeconds: 40
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

### Prometheus Configuration

**monitoring/prometheus.yml**:
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'student-service'
    metrics_path: '/actuator/prometheus'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
            - sms-production
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        regex: student-service
        action: keep

  - job_name: 'configuration-service'
    metrics_path: '/actuator/prometheus'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
            - sms-production
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        regex: configuration-service
        action: keep
```

## Backup and Recovery

### Database Backup Strategy

**Automated Backup CronJob** (Kubernetes):
```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: student-db-backup
  namespace: sms-production
spec:
  schedule: "0 2 * * *"  # Daily at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: postgres-backup
            image: postgres:15-alpine
            env:
            - name: PGPASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: student-db-password
            command:
            - /bin/sh
            - -c
            - |
              pg_dump -h student-db-service -U student_user student_db \
                | gzip > /backup/student_db_$(date +%Y%m%d_%H%M%S).sql.gz

              # Delete backups older than 30 days
              find /backup -name "student_db_*.sql.gz" -mtime +30 -delete
            volumeMounts:
            - name: backup-storage
              mountPath: /backup
          restartPolicy: OnFailure
          volumes:
          - name: backup-storage
            persistentVolumeClaim:
              claimName: db-backup-pvc
```

## Scalability Considerations

### Horizontal Scaling

**Service-Level Scaling**:
- Student Service: 2-10 replicas based on CPU/memory
- Configuration Service: 1-3 replicas (less traffic)
- API Gateway: 2-5 replicas

**Database Scaling**:
- **Read Replicas**: For read-heavy operations
- **Connection Pooling**: HikariCP with appropriate pool size
- **Vertical Scaling**: Increase resources for database pod

### Performance Optimization

**JVM Tuning**:
```dockerfile
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
```

## Summary

The deployment architecture provides:

1. **Containerization**: Docker-based deployment for consistency
2. **Orchestration**: Kubernetes for production, Docker Compose for development
3. **Scalability**: Horizontal pod autoscaling based on metrics
4. **Reliability**: Health checks, readiness probes, and self-healing
5. **CI/CD**: Automated build, test, and deployment pipeline
6. **Monitoring**: Comprehensive observability with Prometheus and Grafana
7. **Backup**: Automated database backups with retention policy

The next document ([Implementation Guide](08-IMPLEMENTATION-GUIDE.md)) provides step-by-step development instructions.

---

**Version**: 1.0
**Last Updated**: 2025-11-17
**Status**: Draft for Review
