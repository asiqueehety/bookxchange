# Admin Dashboard - Quick Reference Guide

## 🎯 Quick Start

### Access Admin Dashboard
```
URL: http://localhost:8080/dashboard
Login: admin / admin123
```

## 📊 Dashboard Tabs

### Tab 1: Manage Users
| Feature | How to Use |
|---------|-----------|
| View Users | Open "Manage Users" tab - shows all users |
| Search | Type username/email in search box, click Search |
| Pagination | Click Previous/Next for more pages |
| Edit Role | Click "Edit" button and select new role |
| Delete | Click "Delete" button to remove user |

**Example Searches:**
- Search "admin" → Find admin user
- Search "test@" → Find users by email domain

### Tab 2: Manage Books
| Feature | How to Use |
|---------|-----------|
| View Books | Open "Manage Books" tab - all books shown |
| Search | Type book title/author, click Search |
| Pagination | Click Previous/Next for more pages |
| View Details | Click "View" button |
| Delete | Click "Delete" button to remove book |

**Example Searches:**
- Search "Java" → Find all Java books
- Search "Harry" → Find Harry Potter series

### Tab 3: Book Requests
| Feature | How to Use |
|---------|-----------|
| View Requests | Open "Book Requests" tab |
| See Status | Check "Pending" or "Fulfilled" badge |
| Delete Request | Click "Delete" button |
| Pagination | Click Previous/Next for more pages |

### Tab 4: Reports
Ready for future implementation

## 📈 Dashboard Stats

Real-time statistics displayed at top:
- **Total Users** - All registered users
- **Total Books** - All books in system
- **Total Sales** - Books sold
- **Active Requests** - Pending book requests

Stats auto-refresh every time you switch tabs.

## 🔐 Security Features

- ✅ Only admins can access (`@PreAuthorize("hasRole('ADMIN')")`)
- ✅ Cannot delete last admin user
- ✅ Cannot change last admin's role
- ✅ Username/Email must be unique
- ✅ All actions require proper authorization

## 🛠️ Admin Actions

### Delete User
```
1. Go to "Manage Users" tab
2. Find the user
3. Click "Delete" button
4. Confirm deletion
```

### Change User Role
```
1. Go to "Manage Users" tab
2. Click "Edit" on user
3. Enter new role: ADMIN, SELLER, or BUYER
4. Save changes
```

### Delete Book
```
1. Go to "Manage Books" tab
2. Find the book
3. Click "Delete" button
4. Confirm deletion
```

### Delete Request
```
1. Go to "Book Requests" tab
2. Find the request
3. Click "Delete" button
4. Confirm deletion
```

## 🔍 Search Tips

### User Search Examples
```
admin          → Find admin user
test_buyer     → Find specific user
@gmail.com     → Find all Gmail users
buyer@test     → Find user by email pattern
```

### Book Search Examples
```
Java           → Find Java programming books
Effective      → Find "Effective Java"
Author Name    → Find books by author
Programming    → Find all programming books
```

## ⚠️ Important Warnings

- 🚨 Deleting a user deletes all related data
- 🚨 Changing last admin role cannot be undone
- 🚨 Deleted books cannot be recovered
- 🚨 No undo operation available

## 📱 API Endpoints

```
GET    /api/admin/stats              Get statistics
GET    /api/admin/users              Get all users
PUT    /api/admin/users/{id}/role    Change user role
DELETE /api/admin/users/{id}         Delete user
GET    /api/admin/requests           Get all requests
DELETE /api/admin/requests/{id}      Delete request
DELETE /api/admin/books/{id}         Delete book
```

## 🧪 Testing

### Run Tests
```bash
.\mvnw.cmd test -Dtest=AdminControllerTest
```

### Test Results Should Show
```
Tests run: 21, Failures: 0, Errors: 0
BUILD SUCCESS
```

## 📊 Current Test Coverage

- ✅ Statistics retrieval
- ✅ User management (get, update, delete)
- ✅ User search and pagination
- ✅ User role management
- ✅ Book requests management
- ✅ Error handling
- ✅ Security features
- ✅ Last admin protection
- ✅ Uniqueness validation

## 🐛 Troubleshooting

### Problem: Can't access admin dashboard
**Solution**: 
- Verify you're logged in as admin
- Check account has ADMIN role
- Clear browser cache and refresh

### Problem: Stats showing 0
**Solution**:
- Check database connection
- Ensure test data exists
- Refresh page

### Problem: Can't delete user
**Solution**:
- Check if it's the last admin
- Verify you have admin rights
- Check browser console for errors

### Problem: Search not working
**Solution**:
- Press Enter or click Search button
- Check exact spelling
- Try shorter search term
- Refresh page

### Problem: Pagination shows only 1 page
**Solution**:
- You have fewer than 20 items
- Add more test data if needed

## 📝 User Roles

| Role | Capabilities |
|------|-------------|
| ADMIN | Full access to all admin features |
| SELLER | Can post books for sale |
| BUYER | Can browse and purchase books |

## 🎓 Example Scenarios

### Scenario 1: Suspend a User
```
1. Go to Manage Users
2. Find user to suspend
3. Click Edit
4. Change role to something restricted
5. Confirm
```

### Scenario 2: Find All Books by Author
```
1. Go to Manage Books
2. Search author's name
3. View results
```

### Scenario 3: Clean Up Pending Requests
```
1. Go to Book Requests
2. Find pending requests
3. Click Delete on unwanted ones
4. Confirm
```

## 📞 Admin Resources

- Dashboard: `http://localhost:8080/dashboard`
- User Profile: `http://localhost:8080/user/profile`
- Logout: `http://localhost:8080/logout`
- API Docs: `/api/admin/stats` (test endpoint)

---

**Version**: 1.0
**Last Updated**: 2026-04-04
**Status**: ✅ Production Ready
