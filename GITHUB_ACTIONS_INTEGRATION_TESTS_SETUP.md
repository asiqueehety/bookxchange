# GitHub Actions Integration Tests Setup

## Overview

All integration tests have been successfully added to your GitHub Actions CI/CD pipeline. When you push code to the `master` or `develop` branches, GitHub Actions will automatically run both unit and integration tests.

## What Was Done

### 1. **Maven Configuration Updates** (`pom.xml`)

#### Added Properties
- `skipUnitTests`: Controls whether unit tests are skipped (default: false)
- `skipITs`: Controls whether integration tests are skipped (default: false)

#### Added maven-failsafe Plugin
The maven-failsafe plugin is configured to:
- Run all integration tests matching patterns: `**/*IntegrationTest.java` and `**/*IT.java`
- Exclude unit tests matching pattern: `**/*Test.java`
- Execute during the `verify` phase
- Generate test reports in `target/failsafe-reports/`

#### Updated maven-surefire Plugin
- Added `skip` property bound to `${skipUnitTests}` for flexibility

### 2. **GitHub Actions Workflow Updates** (`.github/workflows/ci-cd.yml`)

#### Build and Test Job Improvements

**1. Database Initialization**
- Installs PostgreSQL client tools
- Runs SQL init script to set up Spring Session tables
- Creates all necessary database schema before tests run

**2. Separate Test Phases**

**Unit Tests Only:**
```bash
./mvnw test -DskipITs=true
```
- Runs only unit tests (excludes integration tests)
- Uses H2 in-memory database
- Skips integration test phase

**Integration Tests Only:**
```bash
./mvnw verify -DskipUnitTests=true -DskipTests=false
```
- Runs only integration tests  
- Uses PostgreSQL database service
- Excludes unit tests

**3. Enhanced Test Reporting**
- Unit test results published as "Unit Test Results" check
- Integration test results published as "Integration Test Results" check
- Both result sets available as downloadable artifacts
- Reports display in PR checks and workflow summary

**4. Environment Variables**
Both test phases use PostgreSQL configuration:
```
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/bookxchange
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
SPRING_JPA_HIBERNATE_DDL_AUTO: create-drop
SPRING_PROFILES_ACTIVE: test
```

## Integration Tests Included

Your project has **9 integration test files** all of which are now run on every push/PR:

1. **AuthControllerIntegrationTest** - Authentication & Registration (3 tests)
2. **BookControllerIntegrationTest** - Book Management (8 tests)
3. **BookRequestControllerIntegrationTest** - Book Requests (9 tests)
4. **CartControllerIntegrationTest** - Shopping Cart
5. **DashboardControllerIntegrationTest** - User Dashboards
6. **PurchaseControllerIntegrationTest** - Purchase Functionality
7. **PublicControllerIntegrationTest** - Public Pages
8. **UserProfileControllerIntegrationTest** - User Profile Management

**Total: 40+ integration tests** running in CI/CD pipeline

## How Tests Are Run Locally

### Run Unit Tests Only
```bash
./mvnw test -DskipITs=true
```

### Run Integration Tests Only
```bash
./mvnw verify -DskipUnitTests=true -DskipTests=false
```

### Run All Tests (Default)
```bash
./mvnw clean verify
```

### Maven Clean Install
```bash
./mvnw clean install -DskipTests
```

## Test Reports Location

### Locally After Running Tests
- **Unit Tests**: `target/surefire-reports/*.xml`
- **Integration Tests**: `target/failsafe-reports/*.xml`

### In GitHub Actions
- Test results published in PR checks
- Artifacts stored for 90 days (GitHub default)
- Download from "Artifacts" section in workflow run

## GitHub Actions Triggers

Tests automatically run when:
- ✅ Code is pushed to `master` branch
- ✅ Code is pushed to `develop` branch  
- ✅ Pull Request is created to `master` or `develop`

## Test Database Configuration

### In GitHub Actions
- PostgreSQL 15 service container runs during tests
- H2 in-memory database used for local unit tests
- Spring Session tables created automatically from `init.sql`

### Local PostgreSQL Setup
If you want to run integration tests locally with PostgreSQL:

```bash
# Set up PostgreSQL connection
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bookxchange
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password

# Run integration tests
./mvnw verify -DskipUnitTests=true -DskipTests=false
```

## Database Schema

The following tables are created for testing:

**Spring Session Tables:**
- `spring_session` - Stores session data
- `spring_session_attributes` - Stores session attributes

**Application Tables (auto-created by JPA):**
- `users` - User accounts
- `books` - Book listings
- `book_requests` - Book request posts
- `sold_books` - Purchase records

## Performance Optimizations

1. **Parallel Execution**: Tests use Maven's built-in test execution
2. **Test Isolation**: Each test runs in a transaction that's rolled back
3. **Context Caching**: Spring test context is cached between tests
4. **Connection Pooling**: HikariCP manages database connections

## Troubleshooting

### Tests Failing in GitHub Actions but Passing Locally

**Common Causes:**
1. Database connection issues - Check PostgreSQL service health
2. Environment variables - Verify they're set in workflow
3. Test data differences - H2 vs PostgreSQL behavior
4. Timing issues - Tests may need wait conditions

**Solution:**
Check the GitHub Actions logs:
1. Go to your repository
2. Click "Actions" tab
3. Click the failed workflow run
4. Expand the job to see detailed logs

### Tests Timing Out

Increase timeout in workflow by adding step timeout:
```yaml
- name: Run integration tests
  timeout-minutes: 10
  run: ./mvnw verify -DskipUnitTests=true -DskipTests=false
```

### Spring Session Tables Not Found

Ensure `src/main/resources/db/init.sql` runs during GitHub Actions:
- The workflow includes a database initialization step
- Tables are created before tests run
- Check that PostgreSQL service is healthy

## Next Steps

1. **Push to GitHub**: Your changes are ready for GitHub Actions
2. **Monitor Workflows**: Watch the Actions tab after your first push
3. **Review Reports**: Check PR checks for test results
4. **Iterate**: Failed tests will show in PR checks

## Files Modified

- ✅ `pom.xml` - Added maven-failsafe plugin and test properties
- ✅ `.github/workflows/ci-cd.yml` - Enhanced with integration test steps
- ✅ `src/main/resources/db/init.sql` - Spring Session table schema

## References

- Maven Failsafe Plugin: https://maven.apache.org/surefire/maven-failsafe-plugin/
- Spring Testing: https://spring.io/guides/gs/testing-web/
- GitHub Actions: https://docs.github.com/en/actions
