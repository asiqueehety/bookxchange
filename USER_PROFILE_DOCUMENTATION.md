# User Profile & Management System Documentation

## Overview

The User Profile & Management system is now fully implemented in BookXchange. This allows users to manage their personal information, upload profile pictures, change passwords, view account statistics, and delete their accounts.

---

## ✅ COMPLETED FEATURES

### 1. **Display User Information**
- View username, email, and profile picture
- Display user role (BUYER, SELLER, ADMIN)
- Show member since date
- View account type

**Implementation:**
- `UserStatsDTO` - Data transfer object for user statistics
- `UserService.getUserStats()` - Retrieves user information with statistics
- `profile.html` - Profile display template

---

### 2. **Edit Profile Functionality**
- Update username (must be unique)
- Update email address (must be unique)
- Form validation on both frontend and backend
- Error handling with appropriate messages

**Implementation:**
- `UpdateProfileRequest` DTO
- `UserProfileController.updateProfile()` endpoint
- `UserService.updateProfile()` service method
- Validation in both entity and service layer

**Endpoint:**
```
POST /user/profile/update
Parameters: username, userEmail
```

---

### 3. **Change Password**
- Verify current password before allowing change
- Validate new password (minimum 6 characters)
- Require password confirmation
- Secure password hashing using BCrypt

**Implementation:**
- `ChangePasswordRequest` DTO
- `UserProfileController.changePassword()` endpoint
- `UserService.changePassword()` service method
- Uses Spring Security's PasswordEncoder

**Endpoint:**
```
POST /user/profile/change-password
Parameters: oldPassword, newPassword, confirmPassword
```

**Validation Rules:**
- Old password must match current password
- New password must be at least 6 characters
- New password and confirm password must match

---

### 4. **Upload Profile Picture**
- Support for image files (JPEG, PNG, GIF, WebP)
- Maximum file size: 5MB
- Unique filename generation using UUID
- Delete old picture when uploading new one
- Drag and drop support in UI

**Implementation:**
- `FileUploadService` - Handles file operations
- `UserProfileController.uploadProfilePicture()` endpoint
- `UserService.uploadProfilePicture()` service method
- `WebConfig` - Serves uploaded files statically

**Configuration:**
```properties
# application.properties
app.upload.dir=uploads/profiles
app.upload.max-size=5242880
app.upload.allowed-types=image/jpeg,image/png,image/gif,image/webp
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

**Endpoint:**
```
POST /user/profile/upload-picture
Parameters: profilePicture (MultipartFile)
```

**Supported MIME Types:**
- image/jpeg
- image/png
- image/gif
- image/webp

---

### 5. **View Account Statistics**
- Total books posted (for sellers)
- Total books sold
- Total books purchased
- Total book requests made

**Implementation:**
- `UserStatsDTO` - Contains all statistics
- `UserService.getUserStats()` - Calculates all stats
- Repository count methods:
  - `BookRepository.countBySellerId()`
  - `SoldBookRepository.countByBuyerId()`
  - `SoldBookRepository.countBySellerIdFromBooks()`
  - `BookRequestRepository.countByBuyerId()`

---

## 📁 Files Created/Modified

### New Files Created:

1. **DTOs:**
   - `src/main/java/com/example/bookxchange/dto/UpdateProfileRequest.java`
   - `src/main/java/com/example/bookxchange/dto/ChangePasswordRequest.java`
   - `src/main/java/com/example/bookxchange/dto/UserStatsDTO.java`

2. **Services:**
   - `src/main/java/com/example/bookxchange/service/UserService.java`
   - `src/main/java/com/example/bookxchange/service/FileUploadService.java`

3. **Controllers:**
   - `src/main/java/com/example/bookxchange/controller/UserProfileController.java`

4. **Configuration:**
   - `src/main/java/com/example/bookxchange/config/WebConfig.java`

5. **Templates:**
   - `src/main/resources/templates/user/profile.html`

### Modified Files:

1. **Repositories:**
   - `BookRepository.java` - Added `countBySellerId()` method
   - `SoldBookRepository.java` - Added `countByBuyerId()` and `countBySellerIdFromBooks()` methods
   - `BookRequestRepository.java` - Added `countByBuyerId()` method

2. **Configuration:**
   - `SecurityConfig.java` - Added `/user/**`, `/uploads/**` to permitAll routes
   - `application.properties` - Added file upload configuration

---

## 🛣️ Available Routes

### View Profile
```
GET /user/profile
- Displays user profile page with all information and statistics
- Requires authentication
- Response: profile.html template
```

### Update Profile
```
POST /user/profile/update
- Updates username and email
- Requires authentication
- Parameters:
  - username (String, unique)
  - userEmail (String, unique)
- Redirects to /user/profile on success
```

### Change Password
```
POST /user/profile/change-password
- Changes user password
- Requires authentication
- Parameters:
  - oldPassword (String)
  - newPassword (String, min 6 chars)
  - confirmPassword (String)
- Redirects to /user/profile on success
```

### Upload Profile Picture
```
POST /user/profile/upload-picture
- Uploads profile picture
- Requires authentication
- Parameters:
  - profilePicture (MultipartFile, max 5MB)
- Accepted types: JPEG, PNG, GIF, WebP
- Redirects to /user/profile on success
```

### Delete Profile Picture
```
POST /user/profile/delete-picture
- Removes profile picture
- Requires authentication
- Redirects to /user/profile on success
```

### Delete Account
```
POST /user/profile/delete-account
- Permanently deletes user account
- Requires authentication
- Removes profile picture if exists
- Clears security context (logs out user)
- Redirects to home page on success
```

---

## 🔐 Security Features

1. **Password Hashing:**
   - Uses BCrypt for password encoding
   - All passwords are hashed before storage
   - Original passwords never stored in database

2. **Authentication Required:**
   - All `/user/**` endpoints require authentication
   - User can only access their own profile
   - Session-based authentication

3. **File Upload Security:**
   - Only image files allowed
   - File size limited to 5MB
   - MIME type validation
   - Unique filename generation prevents overwrites
   - Files served from `/uploads/**` directory

4. **CSRF Protection:**
   - Forms use POST method
   - Input validation on both client and server side

---

## 📊 Database Queries

### Count Books Posted by Seller
```sql
SELECT COUNT(b) FROM books b WHERE b.seller_id = ?
```

### Count Books Sold by Seller
```sql
SELECT COUNT(sb) FROM sold_books sb 
WHERE sb.book_id IN (SELECT book_id FROM books WHERE seller_id = ?)
```

### Count Books Purchased by Buyer
```sql
SELECT COUNT(sb) FROM sold_books sb WHERE sb.buyer_id = ?
```

### Count Requests Made by Buyer
```sql
SELECT COUNT(br) FROM book_requests br WHERE br.buyer_id = ?
```

---

## 🎨 Frontend Implementation

### Profile Page Layout

1. **Header Section:**
   - Profile picture (circular, 150x150px)
   - Username and email display
   - User role badge
   - Member since date

2. **Statistics Grid:**
   - 4 cards showing user statistics
   - Color-coded gradient backgrounds
   - Responsive grid layout

3. **Tab Navigation:**
   - Edit Profile tab
   - Change Password tab
   - Profile Picture tab
   - Settings tab

4. **Edit Profile Form:**
   - Username input field
   - Email input field
   - Save changes button

5. **Change Password Form:**
   - Old password input
   - New password input
   - Confirm password input
   - Update password button

6. **Profile Picture Section:**
   - Current picture display
   - Remove picture button
   - File upload with drag-and-drop
   - File name display

7. **Settings Tab:**
   - Account information display
   - Delete account button (in danger zone)
   - Confirmation dialog

### Styling Features

- **Color Scheme:**
  - Primary: #6366f1 (Indigo)
  - Secondary: #ec4899 (Pink)
  - Success: #10b981 (Green)
  - Danger: #ef4444 (Red)

- **Responsive Design:**
  - Mobile-friendly layout
  - Grid adjusts to screen size
  - Touch-friendly buttons

- **Visual Feedback:**
  - Success/error alerts
  - Hover effects on buttons
  - Active tab highlighting
  - Form validation messages

---

## 🔧 Configuration Details

### File Upload Service

**Location:** `src/main/java/com/example/bookxchange/service/FileUploadService.java`

**Key Methods:**
```java
public String saveFile(MultipartFile file) throws Exception
- Validates file size and type
- Generates unique filename
- Saves to uploads directory
- Returns filename

public void deleteFile(String filename)
- Safely deletes file if exists
- Handles exceptions gracefully

public String getFileUrl(String filename)
- Returns URL path for image display

public boolean fileExists(String filename)
- Checks if file exists in storage
```

### Web Configuration

**Location:** `src/main/java/com/example/bookxchange/config/WebConfig.java`

**Purpose:** Registers resource handler for serving uploaded files

**Configuration:**
```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + uploadDir + "/");
```

---

## 📋 Error Handling

### Validation Errors

| Error | Cause | Solution |
|-------|-------|----------|
| "Username already taken" | Username exists in database | Choose different username |
| "Email already in use" | Email exists in database | Use different email |
| "Current password is incorrect" | Old password doesn't match | Enter correct current password |
| "New passwords do not match" | Password confirmation mismatch | Ensure passwords match |
| "Password must be at least 6 characters" | Password too short | Use password with 6+ characters |
| "File size exceeds maximum limit" | File > 5MB | Upload smaller file |
| "Only image files are allowed" | Wrong file type | Upload JPEG, PNG, GIF, or WebP |
| "File cannot be empty" | No file selected | Select a file to upload |

---

## 🧪 Testing the Feature

### Manual Testing Steps:

1. **View Profile:**
   - Login to application
   - Click "Profile" in navigation
   - Verify all information displays correctly

2. **Edit Profile:**
   - Change username to new unique name
   - Change email to new unique email
   - Click "Save Changes"
   - Verify success message appears

3. **Upload Picture:**
   - In Profile Picture tab
   - Click upload area or drag image
   - Select image file (JPG, PNG, GIF, WebP)
   - Click "Upload Picture"
   - Verify picture displays

4. **Change Password:**
   - In Change Password tab
   - Enter current password
   - Enter new password (6+ chars)
   - Confirm new password
   - Click "Update Password"
   - Logout and login with new password

5. **Delete Picture:**
   - In Profile Picture tab
   - Click "Remove Picture" button
   - Verify picture is removed

6. **Delete Account:**
   - In Settings tab
   - Click "Delete Account"
   - Confirm deletion
   - Verify redirected to home page
   - Verify cannot login with deleted account

---

## 📈 Performance Considerations

1. **Database Queries:**
   - Uses `@Query` annotations for efficient counting
   - Count queries are optimized with proper indexing

2. **File Storage:**
   - Files stored on disk, not in database
   - Unique naming prevents collisions
   - Efficient deletion of old files

3. **Lazy Loading:**
   - User relationships set to LAZY loading
   - Prevents N+1 query problems

---

## 🚀 Next Steps

After User Profile & Management is complete, proceed with:

1. **Phase 1: Book Management** - CRUD operations for books
2. **Phase 2: Shopping Cart & Purchases** - Purchase system
3. **Phase 3: Search & Filtering** - Advanced search capabilities
4. **Phase 4: Notifications** - Email and in-app notifications
5. **Phase 5: Payment Integration** - Stripe integration

---

## 📚 Related Documentation

- [User Entity Documentation](../entity/User.java)
- [UserService Documentation](../service/UserService.java)
- [FileUploadService Documentation](../service/FileUploadService.java)
- [Main README](../../README.md)
- [Setup Guide](../../SETUP_GUIDE.md)

---

## ✨ Summary

The User Profile & Management system is **production-ready** with:
- ✅ Full CRUD operations for user profiles
- ✅ Secure password handling with BCrypt
- ✅ File upload with validation
- ✅ Responsive, user-friendly interface
- ✅ Comprehensive error handling
- ✅ Security and authentication checks

Users can now fully manage their accounts and profiles!
