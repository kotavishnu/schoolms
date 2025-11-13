# CI/CD Pipeline Guide

## Overview

This document describes the Continuous Integration and Continuous Deployment (CI/CD) pipeline for the School Management System backend. The pipeline is implemented using GitHub Actions and provides automated building, testing, quality analysis, security scanning, and deployment.

## Table of Contents

1. [Pipeline Architecture](#pipeline-architecture)
2. [Pipeline Jobs](#pipeline-jobs)
3. [Configuration](#configuration)
4. [Secrets Management](#secrets-management)
5. [Workflow Triggers](#workflow-triggers)
6. [Deployment Environments](#deployment-environments)
7. [Monitoring and Notifications](#monitoring-and-notifications)
8. [Best Practices](#best-practices)
9. [Troubleshooting](#troubleshooting)

---

## Pipeline Architecture

### Workflow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Trigger Event                           │
│        (Push, Pull Request, Manual Workflow Dispatch)           │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         v
┌─────────────────────────────────────────────────────────────────┐
│                    Job 1: Build & Test                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ - Checkout Code                                           │   │
│  │ - Set up JDK 21                                           │   │
│  │ - Cache Maven Dependencies                                │   │
│  │ - Maven Clean & Compile                                   │   │
│  │ - Run Unit Tests (PostgreSQL + Redis TestContainers)     │   │
│  │ - Run Integration Tests                                   │   │
│  │ - Generate JaCoCo Coverage Report                         │   │
│  │ - Package Application                                     │   │
│  │ - Upload Artifacts (JAR, Test Results, Coverage)         │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │
          ┌──────────────┴──────────────┬──────────────┐
          v                             v               v
┌──────────────────┐      ┌──────────────────┐  ┌────────────────┐
│ Job 2: Code      │      │ Job 3: Security  │  │ Job 4: Docker  │
│ Quality Analysis │      │ Vulnerability    │  │ Build          │
│                  │      │ Scan             │  │                │
│ - SonarQube      │      │ - OWASP Check    │  │ - Build Image  │
│ - Coverage       │      │ - Trivy Scan     │  │ - Push to Hub  │
└──────┬───────────┘      └────────┬─────────┘  └────────┬───────┘
       │                           │                     │
       └───────────────┬───────────┴─────────────────────┘
                       v
          ┌────────────────────────────┐
          │ Job 5: Notifications       │
          │ - Slack                    │
          │ - Email                    │
          └────────────┬───────────────┘
                       │
          ┌────────────┴────────────┐
          v                         v
┌──────────────────┐      ┌──────────────────┐
│ Job 6: Deploy    │      │ Job 7: Deploy    │
│ Staging          │      │ Production       │
│ (develop branch) │      │ (main + tag)     │
└──────────────────┘      └──────────────────┘
```

---

## Pipeline Jobs

### Job 1: Build and Test

**Purpose**: Compile code, run tests, generate coverage reports

**Duration**: ~5-10 minutes

**Steps**:
1. **Checkout Code**: Clone repository with full history
2. **Setup Environment**: JDK 21, Maven, PostgreSQL, Redis
3. **Build**: `mvn clean compile`
4. **Unit Tests**: `mvn test` (exclude integration tests)
5. **Integration Tests**: `mvn verify` (with TestContainers)
6. **Coverage**: `mvn jacoco:report`
7. **Package**: `mvn package` (create JAR)
8. **Upload Artifacts**: Test results, coverage reports, JAR file

**Services**:
- PostgreSQL 18 (port 5432)
- Redis 7.2 (port 6379)

**Artifacts Produced**:
- `application-jar`: School Management System JAR file
- `test-results`: JUnit test reports (XML)
- `coverage-reports`: JaCoCo HTML coverage reports

**Success Criteria**:
- All tests pass (100%)
- Code coverage >= 80%
- No compilation errors
- JAR file created successfully

---

### Job 2: Code Quality Analysis

**Purpose**: Static code analysis with SonarQube

**Duration**: ~5 minutes

**Steps**:
1. Download coverage reports from Job 1
2. Run SonarQube Maven plugin
3. Upload results to SonarQube server

**Metrics Analyzed**:
- Code coverage (target: 80%+)
- Code smells
- Bugs
- Vulnerabilities
- Security hotspots
- Technical debt
- Duplications

**Quality Gates**:
- Coverage on new code >= 80%
- No new blocker/critical issues
- No new security vulnerabilities
- Maintainability rating: A

**Configuration**:
```bash
mvn sonar:sonar \
  -Dsonar.projectKey=school-management-system \
  -Dsonar.organization=school \
  -Dsonar.host.url=$SONAR_HOST_URL \
  -Dsonar.login=$SONAR_TOKEN
```

---

### Job 3: Security Vulnerability Scan

**Purpose**: Identify security vulnerabilities in dependencies

**Duration**: ~5-10 minutes

**Tools**:

1. **OWASP Dependency Check**
   - Scans Maven dependencies
   - Checks against CVE database
   - Fails build on CVSS >= 7
   - Produces HTML report

2. **Trivy**
   - Filesystem vulnerability scanner
   - Checks for OS packages, language-specific dependencies
   - Outputs SARIF format for GitHub Security

**Steps**:
1. Run OWASP Dependency Check
2. Upload dependency check report
3. Run Trivy scanner
4. Upload results to GitHub Security tab

**Suppression**:
False positives can be suppressed in `.github/dependency-check-suppression.xml`

---

### Job 4: Docker Build (Optional)

**Purpose**: Build and push Docker image

**Trigger**: Only on `main` or `develop` branches

**Duration**: ~5-10 minutes

**Steps**:
1. Download JAR from Job 1
2. Setup Docker Buildx
3. Login to Docker Hub
4. Build Docker image
5. Tag with branch name and SHA
6. Push to Docker registry

**Tags**:
- `latest` (main branch)
- `develop` (develop branch)
- `{branch-name}`
- `{git-sha}`
- `v{version}` (on release tags)

**Docker Registry**: Configured via secrets

---

### Job 5: Notifications

**Purpose**: Send build status notifications

**Trigger**: Always runs (after all previous jobs)

**Duration**: < 1 minute

**Channels**:

1. **Slack**:
   ```
   ✅ Build success
   Repository: school-management/backend
   Branch: main
   Commit: abc123
   Author: developer
   ```

2. **Email** (on failure):
   ```
   Subject: [FAILURE] School Management System Build Failed

   Build failed for branch: main
   Commit: abc123
   View: https://github.com/...
   ```

**Configuration**: Requires secrets for webhook URLs and credentials

---

### Job 6: Deploy to Staging

**Purpose**: Deploy to staging environment

**Trigger**: Only on `develop` branch push

**Environment**: `staging`

**Duration**: ~5 minutes

**Steps**:
1. Download JAR artifact
2. Deploy to staging server (SSH/SCP)
3. Restart application
4. Run smoke tests
5. Verify health endpoints

**Staging URL**: https://staging.school-sms.com

**Smoke Tests**:
- `/api/actuator/health` returns 200
- Application starts successfully
- Database connections established

---

### Job 7: Deploy to Production

**Purpose**: Deploy to production environment

**Trigger**: Only on `main` branch with version tag (e.g., `v1.0.0`)

**Environment**: `production`

**Duration**: ~10 minutes

**Steps**:
1. Download JAR artifact
2. Backup current production version
3. Deploy new version
4. Rolling restart (zero-downtime)
5. Health checks
6. Create GitHub release

**Production URL**: https://school-sms.com

**Deployment Strategy**: Blue-Green or Rolling Update

**Rollback Plan**: Automatic rollback if health checks fail

---

### Job 8: Publish Test Results

**Purpose**: Display test results in GitHub UI

**Duration**: < 1 minute

**Steps**:
1. Download test result artifacts
2. Parse JUnit XML files
3. Display in GitHub Actions summary
4. Show pass/fail statistics

**Output**: Test summary on PR checks

---

## Configuration

### Environment Variables

Defined in workflow file (`.github/workflows/maven-build.yml`):

```yaml
env:
  JAVA_VERSION: '21'
  MAVEN_VERSION: '3.9.6'
  MAVEN_OPTS: '-Xmx1024m'
```

### Maven Profiles

- **Test Profile** (`-Dspring.profiles.active=test`):
  - Uses TestContainers for databases
  - H2 in-memory for unit tests
  - PostgreSQL container for integration tests

### Service Containers

**PostgreSQL**:
```yaml
services:
  postgres:
    image: postgres:18
    env:
      POSTGRES_DB: school_test_db
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_password
    ports:
      - 5432:5432
    options: --health-cmd pg_isready
```

**Redis**:
```yaml
services:
  redis:
    image: redis:7.2-alpine
    ports:
      - 6379:6379
    options: --health-cmd "redis-cli ping"
```

---

## Secrets Management

### Required Secrets

Configure these in GitHub Settings > Secrets and Variables > Actions:

| Secret Name | Description | Used In |
|-------------|-------------|---------|
| `SONAR_HOST_URL` | SonarQube server URL | Code Quality job |
| `SONAR_TOKEN` | SonarQube authentication token | Code Quality job |
| `DOCKER_USERNAME` | Docker Hub username | Docker Build job |
| `DOCKER_PASSWORD` | Docker Hub password/token | Docker Build job |
| `SLACK_WEBHOOK_URL` | Slack incoming webhook URL | Notifications job |
| `EMAIL_USERNAME` | SMTP username for emails | Notifications job |
| `EMAIL_PASSWORD` | SMTP password | Notifications job |
| `EMAIL_RECIPIENTS` | Comma-separated email list | Notifications job |
| `STAGING_HOST` | Staging server hostname | Deploy Staging job |
| `STAGING_USER` | Staging server SSH user | Deploy Staging job |
| `STAGING_SSH_KEY` | Staging server SSH private key | Deploy Staging job |
| `PROD_HOST` | Production server hostname | Deploy Production job |
| `PROD_USER` | Production server SSH user | Deploy Production job |
| `PROD_SSH_KEY` | Production server SSH private key | Deploy Production job |

### Setting Secrets

```bash
# Via GitHub CLI
gh secret set SONAR_TOKEN --body "your-token-here"

# Via GitHub Web UI
Settings > Secrets and Variables > Actions > New repository secret
```

---

## Workflow Triggers

### Push Events

```yaml
on:
  push:
    branches:
      - main           # Production releases
      - develop        # Staging deployments
      - 'feature/**'   # Feature branches
      - 'release/**'   # Release candidates
```

### Pull Request Events

```yaml
on:
  pull_request:
    branches:
      - main
      - develop
```

Triggers on:
- Pull request opened
- New commits pushed to PR
- PR reopened

### Manual Workflow Dispatch

```yaml
on:
  workflow_dispatch:
```

Allows manual trigger from GitHub Actions UI

### Tag-based Deployment

```yaml
if: startsWith(github.ref, 'refs/tags/v')
```

Production deployment only on version tags (e.g., `v1.0.0`)

---

## Deployment Environments

### Staging Environment

**Purpose**: Pre-production testing

**Configuration**:
- Environment name: `staging`
- URL: https://staging.school-sms.com
- Database: Staging PostgreSQL instance
- Redis: Staging Redis instance

**Access**:
- Requires `staging` environment approval (optional)
- Automatic deployment on `develop` branch

**Protection Rules**:
- Review from maintainers (optional)
- Deployment logs retained

### Production Environment

**Purpose**: Live application for end users

**Configuration**:
- Environment name: `production`
- URL: https://school-sms.com
- Database: Production PostgreSQL cluster
- Redis: Production Redis cluster

**Access**:
- Requires `production` environment approval
- Only on `main` branch with version tag
- Manual approval required

**Protection Rules**:
- Required reviewers: 2
- Deployment window: Business hours only
- Rollback plan mandatory

---

## Monitoring and Notifications

### Slack Integration

**Setup**:
1. Create Slack incoming webhook
2. Add to GitHub secrets as `SLACK_WEBHOOK_URL`

**Message Format**:
```
✅ Build success | ❌ Build failure
Repository: school-management/backend
Branch: main
Commit: abc123 - "Add student enrollment"
Author: @developer
Duration: 8m 32s
```

### Email Notifications

**Trigger**: Only on build failures

**Recipients**: Configured in `EMAIL_RECIPIENTS` secret

**Content**:
- Repository and branch
- Commit SHA and author
- Link to failed workflow run
- Failure reason (if available)

### GitHub Checks

Automatic status checks on pull requests:
- Build and Test
- Code Quality
- Security Scan

**Branch Protection**: Require checks to pass before merge

---

## Best Practices

### 1. Parallel Jobs

Jobs that don't depend on each other run in parallel:
- Code Quality
- Security Scan
- Docker Build

This reduces total pipeline time.

### 2. Artifact Management

- Upload artifacts from one job, download in another
- Set retention period (7 days for JARs, 30 days for reports)
- Clean up old artifacts automatically

### 3. Caching Strategy

**Maven Dependencies**:
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

**SonarQube Cache**:
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.sonar/cache
    key: ${{ runner.os }}-sonar
```

### 4. Fail Fast

Use `continue-on-error: true` for non-critical jobs:
- SonarQube scan (report only)
- Dependency check (investigate later)
- Notifications (don't fail build)

### 5. Timeout Configuration

Set reasonable timeouts to avoid hanging jobs:
```yaml
timeout-minutes: 30
```

### 6. Branch Protection Rules

**Main Branch**:
- Require pull request reviews (2)
- Require status checks to pass
- Require branches to be up to date
- Include administrators

**Develop Branch**:
- Require pull request reviews (1)
- Require status checks to pass

### 7. Version Tagging

Use semantic versioning:
```bash
# Create release
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

Format: `v{MAJOR}.{MINOR}.{PATCH}`

---

## Troubleshooting

### Issue 1: Build Fails on "Maven Clean"

**Symptom**: `mvn clean` fails with permission error

**Solution**:
```bash
# Check file permissions in workflow
- name: Fix Permissions
  run: chmod +x mvnw

- name: Maven Clean
  run: ./mvnw clean
```

### Issue 2: Tests Fail in CI but Pass Locally

**Common Causes**:
1. **Timezone differences**: Use `UTC` in tests
2. **Missing environment variables**: Check workflow env vars
3. **Service dependencies**: Ensure PostgreSQL/Redis are healthy

**Solution**:
```yaml
# Add debug logging
- name: Run Tests
  run: mvn test -X  # -X for debug output
  env:
    TZ: UTC
```

### Issue 3: Out of Memory

**Symptom**: `java.lang.OutOfMemoryError`

**Solution**:
```yaml
env:
  MAVEN_OPTS: '-Xmx2048m'  # Increase heap size
```

### Issue 4: Slow Build Times

**Optimizations**:
1. **Enable caching**: Maven, SonarQube
2. **Parallelize tests**: `mvn test -T 2C` (2 threads per CPU core)
3. **Skip tests for documentation changes**:
   ```yaml
   if: "!contains(github.event.head_commit.message, '[skip ci]')"
   ```

### Issue 5: Docker Build Fails

**Common Issues**:
- Missing Dockerfile
- Invalid secrets for Docker Hub
- Network issues

**Debug**:
```yaml
- name: Debug Docker
  run: |
    docker version
    docker buildx version
    docker login --username ${{ secrets.DOCKER_USERNAME }} --password-stdin
```

### Issue 6: Deployment Fails

**Rollback**:
```bash
# SSH to server
ssh user@staging-server

# Restore previous version
mv /app/school-management-backup.jar /app/school-management.jar

# Restart service
sudo systemctl restart school-management
```

### Issue 7: SonarQube Scan Fails

**Solutions**:
1. **Check token**: Ensure `SONAR_TOKEN` is valid
2. **Check project key**: Must match SonarQube configuration
3. **Use continue-on-error**: Don't fail build on scan errors

---

## Pipeline Metrics

### Key Performance Indicators

| Metric | Target | Current |
|--------|--------|---------|
| Build Time | < 15 min | TBD |
| Test Success Rate | 100% | TBD |
| Code Coverage | >= 80% | TBD |
| Critical Vulnerabilities | 0 | TBD |
| Deployment Frequency | Daily (staging) | TBD |
| Mean Time to Recovery | < 1 hour | TBD |

### Monitoring

Track metrics in:
- GitHub Actions insights
- SonarQube dashboard
- Custom monitoring (Grafana/Prometheus)

---

## Maintenance

### Regular Tasks

**Weekly**:
- Review failed builds
- Update dependencies with vulnerabilities
- Clean up old artifacts

**Monthly**:
- Review and update secrets
- Optimize workflow performance
- Update runner images/versions

**Quarterly**:
- Security audit of pipeline
- Review access permissions
- Update documentation

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Wrapper](https://github.com/takari/maven-wrapper)
- [SonarQube Integration](https://docs.sonarqube.org/latest/analysis/github-integration/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)

---

**Document Version**: 1.0.0
**Last Updated**: 2025-11-11
**Maintained By**: School Management System DevOps Team
