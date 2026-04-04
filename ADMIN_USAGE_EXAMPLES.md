# Admin Dashboard - Usage Examples & Common Tasks

## 🎯 Common Admin Tasks

### Task 1: Manage User Roles

**Scenario**: You want to promote a buyer to seller status

**Steps**:
```
1. Access admin dashboard at http://localhost:8080/dashboard
2. Log in as admin (admin / admin123)
3. Click "Manage Users" tab
4. Search for the user's username
5. Click "Edit" button next to their name
6. Enter the new role: SELLER
7. Confirm the change
8. User will now have seller privileges
```

**Expected Result**:
```
✅ User's role changes to SELLER
✅ Success message displayed
✅ Table updates immediately
✅ User can now post books
```

---

### Task 2: Remove Inactive Users

**Scenario**: You need to clean up inactive user accounts

**Steps**:
```
1. Go to "Manage Users" tab
2. Search for the user to delete
3. Review their information
4. Click "Delete" button
5. Confirm deletion
6. User account and all related data removed
```

**Safety Checks**:
```
✅ Cannot delete if it's the only admin account
✅ Confirmation dialog appears before deletion
✅ All user's data is cleaned up
✅ Action logged in system
```

---

### Task 3: Search for Users

**Scenario**: You want to find a specific user quickly

**Methods**:
```
METHOD 1: Search by username
- Enter exact username: "john_doe"
- Click Search
- Results display immediately

METHOD 2: Search by email
- Enter email pattern: "@gmail.com"
- Click Search
- Shows all Gmail users

METHOD 3: Partial search
- Enter partial name: "john"
- Click Search
- Shows all matching users

METHOD 4: Browse all
- Leave search empty
- Click Search
- Shows all users with pagination
```

---

### Task 4: Monitor Platform Statistics

**Scenario**: You want to see platform overview

**Information Shown**:
```
📊 Total Users
   - All registered users in system
   - Updates in real-time
   - Includes admins, sellers, buyers

📚 Total Books
   - All books listed on platform
   - Includes in-stock and out-of-stock
   - Real-time count

💰 Total Sales
   - Books that have been sold
   - Number of successful transactions
   - Real-time tracking

📋 Active Requests
   - Pending buyer requests
   - Not yet fulfilled
   - Real-time count
```

**How to View**:
```
1. Look at top of admin dashboard
2. Four stat cards display current numbers
3. Stats auto-update when you switch tabs
4. Click refresh to force update
```

---

### Task 5: Delete Problematic Books

**Scenario**: A book has inappropriate content and needs removal

**Steps**:
```
1. Click "Manage Books" tab
2. Search for the book title
3. Review book information
4. Click "Delete" button
5. Confirm deletion
6. Book removed from system
```

**Result**:
```
✅ Book no longer visible to buyers
✅ Seller's listing removed
✅ Any pending sales cancelled
✅ Stock adjusted automatically
```

---

### Task 6: Resolve Book Request Issues

**Scenario**: A book request is no longer needed

**Steps**:
```
1. Click "Book Requests" tab
2. Find the request in the list
3. Review request details
4. Click "Delete" button
5. Confirm deletion
```

**Information Shown**:
```
- Request subject
- Buyer who requested it
- Seller fulfilling it (if any)
- Current status (Pending/Fulfilled)
```

---

## 🔍 Search Techniques

### Effective User Searches
```
Search Term          → Result
"admin"              → Find admin users
"test_"              → Find users starting with test_
"@gmail"             → Find all Gmail users
"2026-01"            → Find users joined in January 2026
"seller"             → Find seller accounts (if searchable)
"buyer"              → Find buyer accounts
```

### Effective Book Searches
```
Search Term          → Result
"Java"               → Find all Java books
"Programming"        → Find programming books
"Clean Code"         → Find specific book title
"Robert"             → Find books by Robert Martin
"2026"               → Find books published in 2026
"Science Fiction"    → Find books by genre
```

---

## 📊 Understanding Statistics

### What Each Stat Means

**Total Users**
```
Includes:
- Admin accounts
- Seller accounts
- Buyer accounts
- Inactive accounts
- Suspended accounts (if any)

Does NOT include:
- Deleted accounts (removed from count)
- Duplicate entries (prevented by system)
```

**Total Books**
```
Includes:
- In-stock books (quantity > 0)
- Out-of-stock books (quantity = 0)
- Books from all sellers
- Single copies and bulk listings

Does NOT include:
- Deleted books (removed from count)
```

**Total Sales**
```
Includes:
- All completed purchases
- All books sold to any buyer
- From any seller
- Any price range

Does NOT include:
- Pending transactions
- Cancelled orders
```

**Active Requests**
```
Includes:
- Pending requests (no fulfiller yet)
- Only unfulfilled requests
- From any buyer
- Any book type

Does NOT include:
- Fulfilled requests
- Deleted requests
- Cancelled requests
```

---

## ⚠️ Important Warnings

### Critical Actions

🚨 **Deleting a User**
```
WARNING: This action cannot be undone!
- All user data is permanently deleted
- Their books are removed
- Their purchase history is deleted
- Their requests are deleted

Recommendation:
Consider changing role to "SUSPENDED" instead of deleting
if system supports it in future
```

🚨 **Deleting a Book**
```
WARNING: Cannot be recovered!
- Book is removed from all listings
- Pending sales cancelled
- Buyer notifications may not be sent
- Seller loses listing

Best Practice:
Mark book as unavailable first if possible
before permanent deletion
```

🚨 **Changing Admin Role**
```
WARNING: Cannot change last admin's role!
- System prevents accidental admin removal
- Error message displayed if attempted
- Create new admin account first if needed
- Never leave system without admin access
```

---

## 🆘 Troubleshooting Common Issues

### Issue: Can't find user
```
Solution:
1. Check exact spelling of username
2. Try searching by email instead
3. Try partial username search
4. Use different search term
5. Leave search blank to see all users
6. Check pagination for more pages
```

### Issue: Search returns no results
```
Solution:
1. Verify data exists in system
2. Try different search term
3. Check for typos
4. Try searching without spaces
5. Use partial matches instead
6. Leave search empty and browse
```

### Issue: Can't delete user
```
Solution:
1. Check if it's the last admin
2. Verify you have admin permissions
3. Check browser console for errors
4. Try refreshing page
5. Check database connection
6. Restart application if needed
```

### Issue: Stats show zero
```
Solution:
1. Verify database connection
2. Add test data if needed
3. Refresh page
4. Check application logs
5. Verify user roles in database
6. Ensure PostgreSQL is running
```

### Issue: Pagination not working
```
Solution:
1. Ensure more than 20 items exist
2. Click Previous/Next buttons properly
3. Check page number display
4. Refresh page
5. Clear browser cache
6. Try different search criteria
```

---

## 📈 Performance Tips

### Optimize Admin Experience

**Use Search Effectively**
```
✅ Search before browsing large lists
✅ Use keywords instead of scrolling
✅ Combine search with pagination
✅ Clear search after done to reset view
```

**Manage Large Datasets**
```
✅ Use pagination to load data slowly
✅ Search to narrow down large lists
✅ Sort by relevant columns (click headers if available)
✅ Don't load all items at once
```

**Batch Operations**
```
✅ Perform deletions in batches if needed
✅ Use search to group similar items
✅ Process one type at a time (users/books)
✅ Document changes made for audit trail
```

---

## 🔐 Security Best Practices

### Admin Account Security
```
✅ Use strong password
✅ Change password regularly
✅ Don't share admin credentials
✅ Log out when finished
✅ Use secure network connection
✅ Don't access from public WiFi
```

### Admin Actions
```
✅ Review before deleting
✅ Confirm important changes
✅ Don't delete last admin
✅ Document sensitive changes
✅ Keep audit trail
✅ Review action confirmations
```

### Data Protection
```
✅ Never share user passwords
✅ Keep personal data confidential
✅ Respect user privacy
✅ Don't modify user data unnecessarily
✅ Report suspicious activity
✅ Maintain system security
```

---

## 📝 Example Workflows

### Workflow 1: User Promotion
```
1. Admin reviews new sellers in "Manage Users"
2. Searches for "new_seller" username
3. Clicks "Edit" button
4. Changes role from BUYER to SELLER
5. Confirms change
6. User now appears as SELLER in table
7. User can immediately start posting books
```

### Workflow 2: Content Moderation
```
1. Admin clicks "Manage Books" tab
2. Reviews questionable book
3. Clicks "View" to see details
4. Decides content is inappropriate
5. Returns to list
6. Clicks "Delete" button
7. Confirms deletion
8. Book is removed from platform
```

### Workflow 3: Request Cleanup
```
1. Admin clicks "Book Requests" tab
2. Sees old, fulfilled requests
3. Decides to clean up old data
4. Selects requests to delete
5. Deletes one by one
6. System updates statistics
7. Database becomes cleaner
```

---

## 🎓 Learning Resources

### Understanding Admin Dashboard
```
📖 Complete Documentation
   → ADMIN_DASHBOARD_COMPLETE.md

🚀 Quick Reference
   → ADMIN_DASHBOARD_QUICK_REFERENCE.md

📊 API Documentation
   → Review AdminController.java code

🧪 Test Examples
   → AdminControllerTest.java
```

### API Testing
```
Test All Endpoints:
$ .\mvnw.cmd test -Dtest=AdminControllerTest

Test Specific Feature:
$ .\mvnw.cmd test -Dtest=AdminControllerTest::testGetStats
```

---

## ✅ Quality Assurance

### Before Making Changes
```
☑️ Understand the change
☑️ Review what will be affected
☑️ Consider consequences
☑️ Plan alternative if needed
☑️ Have backup if possible
```

### After Making Changes
```
☑️ Verify change was successful
☑️ Check statistics updated
☑️ Confirm data integrity
☑️ Test related features
☑️ Document change if needed
```

---

## 📞 Support & Help

### Getting Help
```
1. Check ADMIN_DASHBOARD_QUICK_REFERENCE.md
2. Review API documentation
3. Check browser console (F12) for errors
4. Review server logs for issues
5. Consult system administrator
```

### Reporting Issues
```
Include:
- What you were trying to do
- What happened instead
- Error messages received
- Screenshot if applicable
- Steps to reproduce
- System information
```

---

**Version**: 1.0
**Created**: 2026-04-04
**Status**: ✅ Production Ready
**Last Updated**: 2026-04-04
