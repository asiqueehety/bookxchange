# BookXchange - Testing & CI/CD Documentation

## Overview

This document describes the comprehensive testing strategy and CI/CD pipeline for the BookXchange application.

## Test Structure

### 1. Unit Tests

Unit tests verify individual components in isolation using mocks.

#### Service Layer Tests
- **AuthServiceTest**: Tests authentication and registration logic
  - `testRegisterSuccess()` - Validates successful user registration
  - `testRegisterPasswordMismatch()` - Ensures password validation
  - `testRegisterUsernameExists()` - Checks duplicate username prevention
  - `testRegisterEmailExists()` - Checks duplicate email prevention
  - `testGetUserByUsername()` - Verifies user retrieval
  - `testSwitchRole()` - Tests role switching functionality

- **CustomUserDetailsServiceTest**: Tests Spring Security user details loading
  - `testLoadUserByUsernameSuccess()` - Validates user details loading
  - `testLoadUserByUsernameNotFound()` - Handles missing users
  - `testLoadUserByUsernameWithBuyerRole()` - Verifies BUYER role assignment
  - `testLoadUserByUsernameWithSellerRole()` - Verifies SELLER role assignment
  - `testLoadUserByUsernameWithAdminRole()` - Verifies ADMIN role assignment

#### Entity Tests
- **UserTest**: Tests User entity creation and manipulation
  - Entity creation validation
  - Setter/getter verification
  - UserRole enum validation

- **BookTest**: Tests Book entity functionality
  - Book creation and properties
  - Price and quantity handling
  - Quantity decrement logic

#### DTO Tests
- **RegisterRequestTest**: Validates registration request DTO
- **UserDTOTest**: Validates user data transfer object

### 2. Integration Tests

Integration tests verify component interactions with real dependencies.

#### Repository Tests
- **UserRepositoryTest**: Database operations for users
  - `testFindByUsername()` - Username lookup
  - `testExistsByUsername()` - Username existence check
  - `testExistsByUserEmail()` - Email existence check
  - `testSaveUser()` - User persistence
  - `testUpdateUser()` - User updates
  - `testDeleteUser()` - User deletion

- **BookRepositoryTest**: Database operations for books
  - `testFindBySellerUid()` - Books by seller
  - `testSaveBook()` - Book persistence
  - `testUpdateBook()` - Book updates
  - `testDeleteBook()` - Book deletion

- **SoldBookRepositoryTest**: Database operations for sold books
  - `testFindByBuyerUid()` - Purchase history
  - `testExistsByBuyerUidAndBookBookId()` - Duplicate purchase check

#### Controller Tests
- **PublicControllerTest**: Web layer testing
  - `testIndexPageAccessible()` - Landing page access
  - `testLoginPageAccessible()` - Login page access
  - `testRegisterPageAccessible()` - Registration page access
  - Authenticated user access verification

- **AuthControllerTest**: Authentication endpoints
  - `testRegisterEndpointSuccess()` - Successful registration
  - `testRegisterEndpointError()` - Registration error handling

## Running Tests Locally

### Run All Tests
```bash
./mvnw clean test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=AuthServiceTest
```

### Run Tests with Coverage
```bash
./mvnw clean test jacoco:report
```

### Run Integration Tests Only
```bash
./mvnw verify
```

## GitHub Actions CI/CD Pipeline

The CI/CD pipeline is defined in `.github/workflows/ci-cd.yml` and includes:

### 1. Build & Test Job
- **Triggers**: Push to main/develop, Pull requests
- **Services**: PostgreSQL database
- **Steps**:
  - Checkout code
  - Setup JDK 17
  - Install dependencies
  - Run unit tests
  - Run integration tests
  - Build project
  - Upload test results
  - Upload JAR artifacts

### 2. Security Scan Job
- **OWASP Dependency Check**: Scans for vulnerable dependencies
- **Results**: Uploaded as artifacts

### 3. Code Quality Job
- **Maven Build**: Compiles and packages code
- **PMD Check**: Checks for common programming flaws
- **Checkstyle**: Validates code style

### 4. Docker Build Job
- **Trigger**: Only on push to main branch
- **Build**: Docker image for deployment
- **Caching**: Uses GitHub Actions cache

### 5. Notification Job
- **Slack Integration**: Notifies on build failures
- **Status Check**: Reports build status

## GitHub Actions Setup

### Prerequisites
1. GitHub repository with the code
2. Secrets configured in GitHub:
   - `SONAR_TOKEN` (optional, for SonarCloud)
   - `SLACK_WEBHOOK_URL` (optional, for Slack notifications)

### Configuration

1. **Enable Actions**: Settings → Actions → Allow all actions
2. **Add Secrets**: Settings → Secrets and variables → Actions
3. **Workflow**: `.github/workflows/ci-cd.yml` should auto-trigger

### Viewing Results
- Go to **Actions** tab in GitHub
- Click on the workflow run
- View logs for each job
- Download artifacts (test results, JAR files)

## Test Coverage Requirements

- Minimum coverage: 70%
- Critical paths: 90%
- Services: 85%
- Controllers: 80%

### Generate Coverage Report
```bash
./mvnw clean test jacoco:report
open target/site/jacoco/index.html
```

## Best Practices

1. **Write Tests First**: Follow TDD principles
2. **Mock External Dependencies**: Use Mockito for isolation
3. **Test Edge Cases**: Include error scenarios
4. **Keep Tests Fast**: Avoid unnecessary database hits
5. **Name Tests Clearly**: `test[What][When][Then]` format
6. **One Assertion Per Test**: Avoid multiple assertions when possible
7. **Use @Before/@After**: Setup and teardown shared test data
8. **Parallel Execution**: Tests run in parallel for speed

## Continuous Integration Benefits

✅ **Early Bug Detection**: Catch issues before merge
✅ **Code Quality**: Automated quality checks
✅ **Security**: Dependency vulnerability scanning
✅ **Consistency**: Same build environment for all
✅ **Deployment Ready**: JAR artifacts pre-built
✅ **Team Confidence**: All tests must pass before merge
✅ **Automated Feedback**: Instant test results on PR

## Troubleshooting

### Tests Fail Locally but Pass in CI
- Check Java version: `java -version` should be 17
- Check PostgreSQL: Ensure service is running
- Update dependencies: `./mvnw clean install`

### GitHub Actions Not Running
- Check workflow file: `.github/workflows/ci-cd.yml`
- Verify branch naming: main or develop
- Check Actions permissions: Enable in Settings

### Build Timeout
- Increase timeout in workflow (default: 360 minutes)
- Optimize test execution
- Check resource usage

## Example Workflow

1. **Developer** creates feature branch
2. **Pushes** code to GitHub
3. **GitHub Actions** automatically triggers CI/CD
4. **Tests run** in parallel (unit, integration, security)
5. **Results** displayed on PR
6. **All checks pass** → Ready to merge
7. **Main branch** → Docker image builds
8. **Artifacts** ready for deployment

## Next Steps

1. Push code to GitHub repository
2. Configure GitHub secrets (if needed)
3. CI/CD pipeline auto-triggers
4. Monitor Actions tab for results
5. Integrate with Slack for notifications
6. Deploy JAR artifacts to Render

