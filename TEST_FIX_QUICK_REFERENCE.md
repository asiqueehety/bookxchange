# Quick Action Plan - Remaining Test Issues

## Summary
**Current State**: 239/251 tests passing (95.22% success rate)
**Remaining Issues**: 12 tests failing/with errors
**Estimated Fix Time**: 15-30 minutes

---

## Issue #1: SPRING_SESSION Table Missing (9 Tests)

### Quick Fix (Recommended)
Update `src/test/resources/application-test.properties`:

**Before**:
```properties
spring.session.store-type=none
spring.session.jdbc.initialize-schema=never
spring.session.jdbc.table-name=SPRING_SESSION
```

**After** (Choose ONE):

**Option A** - Use In-Memory Session Storage:
```properties
spring.session.store-type=in_memory
spring.session.jdbc.initialize-schema=never
```

**Option B** - Disable Sessions Completely:
```properties
spring.session.store-type=none
spring.session.jdbc.initialize-schema=never
# Remove the SPRING_SESSION table-name
```

**Option C** - Create SPRING_SESSION Table (More Complex):
Create new file: `src/test/resources/schema-h2.sql`
```sql
CREATE TABLE IF NOT EXISTS SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BLOB NOT NULL,
	PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);
```

Then update `application-test.properties`:
```properties
spring.session.jdbc.initialize-schema=always
spring.jpa.properties.hibernate.jdbc.init_sql=classpath:schema-h2.sql
```

### Affected Tests (9 total)
```
✗ UserProfileControllerIntegrationTest.changePassword_Success
✗ UserProfileControllerIntegrationTest.deleteAccount_Success
✗ UserProfileControllerIntegrationTest.deleteProfilePicture_Success
✗ UserProfileControllerIntegrationTest.updateProfile_Success
✗ UserProfileControllerIntegrationTest.uploadProfilePicture_EmptyFile_ReturnsError
✗ UserProfileControllerIntegrationTest.uploadProfilePicture_Success
✗ PublicControllerIntegrationTest.bookDetail_AsAnonymousUser_ReturnsBookDetailPage
✗ PublicControllerIntegrationTest.bookDetail_BookNotFound_ReturnsError
✗ DashboardControllerIntegrationTest.dashboard_Unauthenticated_RedirectsToLogin
✗ BookRequestControllerIntegrationTest.healthCheck_Success
```

---

## Issue #2: Mockito Error in BookControllerIntegrationTest

### File: `src/test/java/com/example/bookxchange/controller/BookControllerIntegrationTest.java`

**Find** (around line 189):
```java
doNothing().when(bookService).methodName(...);
```

**Replace with** (check method return type first):
```java
when(bookService.methodName(...)).thenReturn(expectedValue);
```

### Diagnostic Steps
1. Open BookControllerIntegrationTest
2. Search for "purchaseBook_Success" method
3. Find the mocking setup for bookService
4. Check BookService.purchaseBook() method signature in `src/main/java/com/example/bookxchange/service/BookService.java`
5. If it returns a value (not void), use `when().thenReturn()`

### Affected Test (1 total)
```
✗ BookControllerIntegrationTest.purchaseBook_Success
```

---

## Issue #3: HTTP 400 Error in BookRequestController

### File: `src/test/java/com/example/bookxchange/controller/BookRequestControllerIntegrationTest.java`

**Problem**: Test expects 200 but gets 400

### Debug Steps
1. Find `fulfillRequest_Success()` test method
2. Check what parameters are being sent in the POST request
3. Look at BookRequestController.fulfillRequest() endpoint definition
4. Compare test parameters with endpoint requirements
5. Check for validation annotations on BookRequest entity

### Common Causes:
- Missing required `@RequestParam` values
- Invalid UUID format in test data
- Request payload validation failures
- CORS or security filter rejection

### Affected Test (1 total)
```
✗ BookRequestControllerIntegrationTest.fulfillRequest_Success
```

---

## Testing After Fixes

```bash
# Test only the previously failing tests
./mvnw.cmd test -Dtest=UserProfileControllerIntegrationTest,BookControllerIntegrationTest,BookRequestControllerIntegrationTest,DashboardControllerIntegrationTest,PublicControllerIntegrationTest

# Run all tests
./mvnw.cmd clean test

# See detailed output
./mvnw.cmd test -X 2>&1 | tail -100
```

---

## Expected Results After Fixes

### Scenario 1: Only Fix SPRING_SESSION (Option A - Recommended)
- ✅ 248/251 tests passing (98.80% success rate)
- ⚠️ 3 remaining (2 Mockito errors, 1 HTTP 400)

### Scenario 2: Fix All Issues
- ✅ 251/251 tests passing (100% success rate)
- 🎉 ALL TESTS PASS

---

## Files to Monitor
- `src/test/resources/application-test.properties` ← Session configuration
- `src/test/java/com/example/bookxchange/controller/BookControllerIntegrationTest.java` ← Mockito config
- `src/test/java/com/example/bookxchange/controller/BookRequestControllerIntegrationTest.java` ← HTTP 400 issue
- `src/main/java/com/example/bookxchange/service/BookService.java` ← For method signatures
- `src/main/java/com/example/bookxchange/controller/BookRequestController.java` ← For endpoint validation

---

## Next Steps
1. **Immediately**: Apply Option A or B for SPRING_SESSION fix (5 minutes)
2. **Follow-up**: Fix Mockito error in BookControllerIntegrationTest (5 minutes)
3. **Debug**: Investigate HTTP 400 error in BookRequestControllerIntegrationTest (10-15 minutes)
4. **Verify**: Run full test suite to confirm 100% pass rate (2 minutes)

**Total Estimated Time**: 22-27 minutes

---

## Quick Reference - Command Line Tests

```bash
# Test a single class
./mvnw.cmd test -Dtest=BookControllerIntegrationTest

# Test a single method
./mvnw.cmd test -Dtest=BookControllerIntegrationTest#purchaseBook_Success

# Run tests, ignore failures, continue
./mvnw.cmd test -Dmaven.test.failure.ignore=true

# Show test output
./mvnw.cmd test -DforkCount=0

# Clean before test
./mvnw.cmd clean test -o
```

---

**Last Updated**: April 3, 2026 19:52  
**Status**: ✅ Ready for fixes  
**Confidence Level**: 🟢 High (Session issue is well-understood)

