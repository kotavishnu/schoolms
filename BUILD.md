# Build Instructions - School Management System

## Prerequisites Verification

Before building, ensure you have the required tools installed:

### 1. Java Development Kit (JDK) 21

```bash
# Check Java version
java -version

# Expected output:
# openjdk version "21.x.x"
# OpenJDK Runtime Environment (build 21.x.x)
# OpenJDK 64-Bit Server VM (build 21.x.x, mixed mode, sharing)
```

**Installation**:
- Download from: https://adoptium.net/
- Or use SDK Manager: `sdk install java 21.0.1-tem`

### 2. Apache Maven 3.9+

```bash
# Check Maven version
mvn -version

# Expected output:
# Apache Maven 3.9.x
# Maven home: /path/to/maven
# Java version: 21.x.x
```

**Installation**:
- Download from: https://maven.apache.org/download.cgi
- Or use package manager:
  - Windows: `choco install maven`
  - macOS: `brew install maven`
  - Linux: `sudo apt install maven`

## Build Commands

### 1. Clean Install (Recommended First Build)

```bash
mvn clean install
```

This command:
- Cleans previous build artifacts (`clean`)
- Compiles source code
- Runs unit tests
- Packages application into JAR
- Installs JAR to local Maven repository (`install`)

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
[INFO] Finished at: YYYY-MM-DDTHH:MM:SS
```

### 2. Compile Only (No Tests)

```bash
mvn clean compile -DskipTests
```

Use this for quick compilation checks without running tests.

### 3. Run Tests Only

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SchoolManagementApplicationTest

# Run tests matching pattern
mvn test -Dtest=*IntegrationTest
```

### 4. Run with Specific Profile

```bash
# Development profile (default)
mvn clean install -Pdev

# Test profile
mvn clean install -Ptest

# Production profile (creates shaded JAR)
mvn clean install -Pprod
```

### 5. Generate Code Coverage Report

```bash
mvn clean test jacoco:report

# View report
# Open: target/site/jacoco/index.html
```

**Coverage Targets**:
- Overall: ≥ 80%
- Domain Layer: ≥ 90%
- Application Layer: ≥ 85%
- Presentation Layer: ≥ 75%
- Infrastructure Layer: ≥ 70%

### 6. Run Integration Tests

```bash
mvn verify

# This runs:
# - Unit tests (via Surefire)
# - Integration tests (via Failsafe)
# - Code coverage checks
```

### 7. SonarQube Analysis

```bash
# Requires SonarQube token
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=school-management-system \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_SONAR_TOKEN
```

## Build Profiles Explained

### Development Profile (`dev`)

**Activation**: Default, or use `-Pdev`

**Characteristics**:
- Development optimizations
- Detailed logging
- H2 in-memory database for quick testing
- Hot reload enabled (Spring DevTools)

**Use Case**: Local development

### Test Profile (`test`)

**Activation**: `-Ptest`

**Characteristics**:
- TestContainers for real PostgreSQL
- Test-specific configurations
- Parallel test execution
- Extended timeouts for integration tests

**Use Case**: CI/CD pipelines, comprehensive testing

### Production Profile (`prod`)

**Activation**: `-Pprod`

**Characteristics**:
- Optimized for production
- Shaded JAR with all dependencies
- Security hardening
- Minimized logging
- Production-grade connection pooling

**Use Case**: Production deployment

## Troubleshooting Build Issues

### Issue 1: "Cannot find symbol" compilation errors

**Cause**: Lombok or MapStruct annotation processors not configured

**Solution**:
```bash
# Clean and rebuild
mvn clean install -U

# If using IDE, enable annotation processing:
# IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
# Eclipse: Project Properties → Java Compiler → Annotation Processing → Enable
```

### Issue 2: Tests fail with database connection errors

**Cause**: PostgreSQL not running or TestContainers cannot start

**Solution**:
```bash
# Use H2 in-memory database for tests
mvn test -Dspring.profiles.active=test

# Or start PostgreSQL:
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=school_management_test \
  -e POSTGRES_USER=test \
  -e POSTGRES_PASSWORD=test \
  postgres:18
```

### Issue 3: Out of memory during build

**Cause**: Insufficient heap memory for Maven

**Solution**:
```bash
# Set Maven options
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Then run build
mvn clean install
```

### Issue 4: JaCoCo coverage check fails

**Cause**: Code coverage below 80% threshold

**Solution**:
```bash
# View coverage report
open target/site/jacoco/index.html

# Write more tests to increase coverage
# Then rebuild:
mvn clean test jacoco:report
```

### Issue 5: Dependency download failures

**Cause**: Network issues or Maven Central unavailable

**Solution**:
```bash
# Use multiple Maven repositories (already configured in POM)
# Or force update:
mvn clean install -U

# Clear local repository cache:
rm -rf ~/.m2/repository/com/school
mvn clean install
```

## Build Verification Checklist

Before committing code, verify:

- [ ] `mvn clean compile` succeeds
- [ ] `mvn test` passes all unit tests
- [ ] `mvn verify` passes integration tests
- [ ] Code coverage ≥ 80% (check `target/site/jacoco/index.html`)
- [ ] No critical SonarQube issues
- [ ] `mvn clean package -Pprod` creates executable JAR
- [ ] Application starts: `java -jar target/*.jar`

## IDE-Specific Setup

### IntelliJ IDEA

1. **Import Project**:
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Enable Annotation Processing**:
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

3. **Configure Java 21**:
   - Project Structure → Project → SDK: Java 21
   - Project Structure → Modules → Language Level: 21

4. **Run Configuration**:
   - Run → Edit Configurations → Add New → Spring Boot
   - Main class: `com.school.management.SchoolManagementApplication`
   - Active profiles: `dev`

### Eclipse

1. **Import Project**:
   - File → Import → Maven → Existing Maven Projects
   - Select project directory

2. **Install Lombok Plugin**:
   - Download lombok.jar from projectlombok.org
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation

3. **Enable Annotation Processing**:
   - Project Properties → Java Compiler → Annotation Processing
   - Check "Enable project specific settings"
   - Check "Enable annotation processing"

### VS Code

1. **Install Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Configure Java**:
   - Settings → Java: Home → Set to JDK 21 path

3. **Run Application**:
   - Open `SchoolManagementApplication.java`
   - Click "Run" above main method

## Continuous Integration

### GitHub Actions Workflow

Build automatically runs on:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

**Workflow steps**:
1. Checkout code
2. Set up Java 21
3. Cache Maven dependencies
4. Run `mvn clean verify`
5. Upload test reports
6. Run SonarQube analysis
7. Archive build artifacts

**Status Badge**: Check README.md for build status

## Performance Benchmarks

Expected build times on modern hardware:

| Command | Time (Approximate) |
|---------|-------------------|
| `mvn clean compile` | 15-20 seconds |
| `mvn test` | 30-45 seconds |
| `mvn verify` | 1.5-2 minutes |
| `mvn clean install -Pprod` | 2-3 minutes |

Factors affecting build time:
- CPU cores (parallel test execution)
- Available RAM
- Network speed (dependency downloads)
- SSD vs HDD

## Next Steps After Successful Build

1. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Access application**:
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - Health: http://localhost:8080/api/actuator/health

3. **Run database migrations**:
   - Flyway runs automatically on startup
   - Check logs for migration status

4. **Explore API documentation**:
   - Open Swagger UI in browser
   - Test endpoints interactively

## Support

For build issues:
1. Check this document first
2. Review build logs: `build.log`
3. Check Maven logs: `~/.m2/repository/*.log`
4. Contact: backend-team-lead@school-sms.com

---

**Last Updated**: November 11, 2025
**Version**: 1.0.0
