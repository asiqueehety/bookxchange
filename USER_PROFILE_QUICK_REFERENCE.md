# User Profile System - Quick Reference Guide

## 🚀 Quick Start

### To Access User Profile
```
URL: http://localhost:8080/user/profile
(Requires authentication)
```

### To Test the System

1. **Start the application:**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

2. **Login with test account:**
   - Username: `test_buyer`
   - Password: `admin123`

3. **Navigate to profile:**
   - Click "Profile" in top navigation
   - Or go to `/user/profile` directly

---

## 📁 Key Files & Locations

### Services
| File | Purpose |
|------|---------|
| `UserService.java` | Core profile management |
| `FileUploadService.java` | File handling |

### Controllers
| File | Purpose |
|------|---------|
| `UserProfileController.java` | API endpoints for profile operations |

### Templates
| File | Purpose |
|------|---------|
| `profile.html` | User profile UI |

### Configuration
| File | Purpose |
|------|---------|
| `WebConfig.java` | Static file serving |
| `application.properties` | Upload settings |

---

## 🔗 API Endpoints Reference

### View Profile
```
GET /user/profile
Returns: profile.html template with UserStatsDTO
```

### Update Profile
```
POST /user/profile/update
Content-Type: application/x-www-form-urlencoded

Parameters:
- username=string (unique)
- userEmail=string (unique)

Response: Redirect to /user/profile with success/error flash
```

### Change Password
```
POST /user/profile/change-password
Content-Type: application/x-www-form-urlencoded

Parameters:
- oldPassword=string
- newPassword=string (6+ chars)
- confirmPassword=string

Response: Redirect to /user/profile with success/error flash
```

### Upload Picture
```
POST /user/profile/upload-picture
Content-Type: multipart/form-data

Parameters:
- profilePicture=file (max 5MB, image only)

Response: Redirect to /user/profile with success/error flash
```

### Delete Picture
```
POST /user/profile/delete-picture
Response: Redirect to /user/profile
```

### Delete Account
```
POST /user/profile/delete-account
Response: Redirect to / (home page) + logout
```

---

## 🔐 Security Details

### Authentication
- All endpoints require login (Spring Security)
- User identified by username (UID)
- Session-based authentication

### Authorization
- Users can only access their own profile
- No role-specific restrictions on these endpoints

### Password Handling
- BCrypt with 10 rounds
- Salt generated automatically
- Never logged or exposed

### File Security
- Whitelist: JPEG, PNG, GIF, WebP
- Size limit: 5MB
- Unique names prevent overwrites
- Served from `/uploads/profiles/`

---

## 📊 Data Transfer Objects (DTOs)

### UpdateProfileRequest
```java
{
  "username": "string",
  "userEmail": "string"
}
```

### ChangePasswordRequest
```java
{
  "oldPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

### UserStatsDTO
```java
{
  "uid": "UUID",
  "username": "string",
  "userEmail": "string",
  "profilePic": "filename",
  "userRole": "BUYER|SELLER|ADMIN",
  "dateJoined": "LocalDateTime",
  "totalBooksPosted": 0,
  "totalBooksSold": 0,
  "totalBooksPurchased": 0,
  "totalRequestsMade": 0
}
```

---

## ⚙️ Configuration

### application.properties
```properties
# File upload settings
app.upload.dir=uploads/profiles
app.upload.max-size=5242880
app.upload.allowed-types=image/jpeg,image/png,image/gif,image/webp

# Multipart configuration
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

---

## 📈 Database Queries

### Get User Statistics
```sql
-- Books posted
SELECT COUNT(*) FROM books WHERE seller_id = $1;

-- Books sold
SELECT COUNT(*) FROM sold_books sb
JOIN books b ON sb.book_id = b.book_id
WHERE b.seller_id = $1;

-- Books purchased
SELECT COUNT(*) FROM sold_books WHERE buyer_id = $1;

-- Requests made
SELECT COUNT(*) FROM book_requests WHERE buyer_id = $1;
```

---

## 🧪 Testing Examples

### Test Update Profile
```bash
curl -X POST http://localhost:8080/user/profile/update \
  -d "username=newusername&userEmail=newemail@test.com" \
  -b "JSESSIONID=your_session_id"
```

### Test Upload Picture
```bash
curl -X POST http://localhost:8080/user/profile/upload-picture \
  -F "profilePicture=@/path/to/image.jpg" \
  -b "JSESSIONID=your_session_id"
```

### Test Change Password
```bash
curl -X POST http://localhost:8080/user/profile/change-password \
  -d "oldPassword=admin123&newPassword=newpass123&confirmPassword=newpass123" \
  -b "JSESSIONID=your_session_id"
```

---

## 🛠️ Troubleshooting

### Issue: "File size exceeds maximum limit"
**Solution:** Upload file smaller than 5MB

### Issue: "Only image files are allowed"
**Solution:** Use JPEG, PNG, GIF, or WebP format

### Issue: "Username already taken"
**Solution:** Choose a different username

### Issue: "Current password is incorrect"
**Solution:** Verify you're entering the correct current password

### Issue: "New passwords do not match"
**Solution:** Ensure new password and confirmation match exactly

### Issue: Profile picture not displaying
**Solution:** Check if file exists in `uploads/profiles/` directory

---

## 📝 Common Code Patterns

### Get Current User ID
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String userId = authentication.getName();
```

### Save User Statistics
```java
UserStatsDTO stats = userService.getUserStats(userId);
model.addAttribute("userStats", stats);
```

### Validate Password Update
```java
if (!request.getNewPassword().equals(request.getConfirmPassword())) {
    throw new Exception("Passwords do not match");
}
if (request.getNewPassword().length() < 6) {
    throw new Exception("Password too short");
}
```

### Upload File
```java
String filename = fileUploadService.saveFile(multipartFile);
user.setProfilePic(filename);
userRepository.save(user);
```

---

## 🎯 Performance Tips

1. **Lazy Load Relationships:**
   - User relationships use LAZY loading
   - Reduces unnecessary queries

2. **Efficient Counting:**
   - Use @Query for count operations
   - Avoids loading full result sets

3. **File Storage:**
   - Store files on disk, not database
   - Unique names prevent lookup collisions

4. **Session Management:**
   - Default timeout: 30 minutes
   - Can be configured in application.properties

---

## 📚 Related Documentation

- Full documentation: `USER_PROFILE_DOCUMENTATION.md`
- Implementation summary: `USER_PROFILE_IMPLEMENTATION_COMPLETE.md`
- TODO list: `TODO.md`

---

## 🔄 Update History

| Date | Change |
|------|--------|
| 2026-03-23 | Initial implementation complete |
| - | All features tested and working |
| - | Documentation complete |

---

## ✨ Feature Summary

✅ Display User Information  
✅ Edit Profile (username, email)  
✅ Change Password (secure)  
✅ Upload Profile Picture  
✅ Delete Profile Picture  
✅ View Account Statistics  
✅ Delete Account  
✅ File Validation & Security  
✅ Error Handling & Validation  
✅ Responsive UI Design  

---

**Status: Production Ready ✅**
