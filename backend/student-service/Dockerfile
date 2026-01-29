# Multi-stage build for Student Service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build application (skip tests for Docker build)
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/student-service-*.jar app.jar

# Expose port
EXPOSE 8081

# Set timezone to UTC (Global Directive D-010)
ENV TZ=UTC
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run application with UTC timezone
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Duser.timezone=UTC -jar app.jar"]
