# Multi-stage build for Configuration Service
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/configuration-service-*.jar app.jar

# Expose port
EXPOSE 8082

# Set JVM options for containerized environment
# - UseContainerSupport: Respect container memory limits
# - MaxRAMPercentage: Use up to 75% of container memory
# - Duser.timezone: Set timezone to UTC for consistency
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Duser.timezone=UTC", \
    "-jar", \
    "app.jar"]
