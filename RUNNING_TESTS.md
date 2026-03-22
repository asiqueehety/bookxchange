# Running & Monitoring Tests - Quick Reference

## 🚀 Quick Start Commands

### Local Testing

```bash
# Navigate to project directory
cd bookxchange

# Run all tests (unit + integration)
./mvnw clean test

# Run only unit tests (skip integration tests)
./mvnw test -DskipITs

# Run specific test class
./mvnw test -Dtest=BookRepositoryTest

# Run specific test method
./mvnw test -Dtest=BookRepositoryTest#testFindById

# Run with verbose output
./mvnw test -X

# Skip tests during build
./mvnw clean install -DskipTests
```

## 📊 Test Results on Local Machine

After running `./mvnw clean test`, you'll see output like:

```
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Test reports are generated in:
```
target/surefire-reports/
```

## 🔄 GitHub Actions CI/CD Monitoring

### View Results

1. **Go to GitHub Repository**
   - Navigate to your repository on GitHub
   - Click on "Actions" tab at the top

2. **Select Workflow Run**
   - Find the most recent workflow run (by commit message or date)
   - Click on it to view details

3. **Monitor Build Status**
   - ✅ Green checkmark = All tests passed
   - ❌ Red X = Tests failed or build error
   - 🟡 Yellow circle = Build in progress

### Download Artifacts

1. **Test Reports**
   - Click on the workflow run
   - Scroll to "Artifacts" section
   - Download "test-results" (contains JUnit XML reports)

2. **Built JAR File**
   - Download "jar-file" artifact
   - Can be deployed directly

3. **Security Reports**
   - Download "dependency-check-results"
   - Contains vulnerability scan results

### Workflow Details

In the workflow details page, you can:

- View each job's status
- Expand each step to see logs
- See execution time for each step
- Check environment variables used
- View PostgreSQL service logs (if failed)

## 📈 Build Status Indicators

### Pipeline Stages (in order):

1. **build-and-test** ← Primary job (must pass)
   - Runs unit tests
   - Runs integration tests
   - Builds JAR package
   - Uploads artifacts

2. **security-scan** (parallel with above)
   - Scans for vulnerabilities
   - Non-blocking (allows failure)

3. **code-quality** (parallel with above)
   - Validates code compilation
   - Quick validation build

4. **notification** (final check)
   - Verifies build-and-test passed
   - Blocks merge if failed

## 🔍 Understanding Test Output

### Local Test Output Example:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.bookxchange.BookxchangeApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.608 s -- in com.example.bookxchange.BookxchangeApplicationTests
[INFO] Running com.example.bookxchange.dto.RegisterRequestTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in com.example.bookxchange.dto.RegisterRequestTest
...
[INFO] Results:
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Key Metrics:
- **Tests run**: Total number of test methods executed
- **Failures**: Logic failures (assertion failures)
- **Errors**: Exceptions during test execution
- **Skipped**: Tests that were not run
- **Time elapsed**: Total execution time

## 🐛 Debugging Failed Tests

### If tests fail locally:

1. **Check PostgreSQL is running**
   ```bash
   docker ps  # Should show postgres container
   ```

2. **View full error output**
   ```bash
   ./mvnw test -X -e
   ```

3. **Run single failing test**
   ```bash
   ./mvnw test -Dtest=FailingTestClass
   ```

4. **Check logs**
   ```bash
   tail -f target/surefire-reports/*.txt
   ```

### If tests fail in GitHub Actions:

1. Go to Actions → Failed workflow
2. Click on `build-and-test` job
3. Click "Run unit tests" step
4. Scroll to see error message
5. Check PostgreSQL logs if database error

## 📋 Test Coverage Matrix

| Component | Tests | Status |
|-----------|-------|--------|
| User Entity | 3 | ✅ PASS |
| Book Entity | 4 | ✅ PASS |
| User DTO | 3 | ✅ PASS |
| Register DTO | 2 | ✅ PASS |
| User Repository | 5 | ✅ PASS |
| Book Repository | 5 | ✅ PASS |
| SoldBook Repository | 4 | ✅ PASS |
| Auth Service | 9 | ✅ PASS |
| UserDetails Service | 5 | ✅ PASS |
| Context Load (Integration) | 1 | ✅ PASS |
| **TOTAL** | **41** | **✅ PASS** |

## 🔐 Security Scanning

OWASP Dependency Check runs automatically:

- Scans for CVEs in dependencies
- Reports vulnerabilities
- Doesn't block build (warning only)
- Results in "dependency-check-results" artifact

## ⚡ Performance Tips

### Speed up local tests:
```bash
# Use offline mode (faster)
./mvnw test -o

# Skip checksum validation
./mvnw test -npu

# Use parallel test execution (if no shared state)
./mvnw test -T 1C
```

### Speed up CI/CD:
- Maven cache enabled (automatically)
- Uses faster temurin distribution
- Parallel job execution
- Skip tests on secondary builds

## 📝 Test Report Locations

### Local Machine:
```
target/surefire-reports/
├── com.example.bookxchange.BookxchangeApplicationTests.txt
├── com.example.bookxchange.entity.BookTest.txt
├── com.example.bookxchange.repository.UserRepositoryTest.txt
└── ... (other test reports)
```

### GitHub Actions:
```
Artifacts section in workflow details
├── test-results/ (all above files)
└── jar-file/ (built application)
```

## 🎯 CI/CD Workflow Timeline

```
Code Push to main/develop
    ↓
GitHub Actions triggered
    ├─→ [build-and-test] ← CRITICAL (must pass)
    ├─→ [security-scan] (non-blocking)
    └─→ [code-quality] (informational)
    ↓
[notification] checks build-and-test result
    ↓
✅ All passed → Ready to merge/deploy
❌ Failed → Check logs, fix code, push again
```

## 🚨 Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| PostgreSQL connection failed | DB not running | `docker-compose up -d` |
| Maven cache issues | Corrupted cache | `./mvnw clean -U test` |
| Tests timeout | Slow system | Increase timeout in pom.xml |
| Different results local vs CI | Environment mismatch | Check Java version, OS |
| Flaky tests | Race conditions | Add Thread.sleep() or use WaitFor |

## 📚 Additional Commands

```bash
# Generate test coverage report
./mvnw clean test jacoco:report

# Run tests and create HTML report
./mvnw surefire-report:report

# View HTML report
open target/site/surefire-report.html  # macOS
xdg-open target/site/surefire-report.html  # Linux
start target/site/surefire-report.html  # Windows
```

## ✨ Best Practices

✅ Run tests before every push
✅ Fix failing tests immediately
✅ Keep tests fast (< 15 seconds for unit tests)
✅ Use meaningful test names
✅ Mock external dependencies
✅ Test both success and failure cases
✅ Review CI/CD logs for warnings
✅ Keep dependencies updated

---

**Remember**: Green build status = Code is ready to merge! 🎉
