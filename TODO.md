# BookXchange - Implementation Checklist & Next Steps

## ✅ COMPLETED

### Backend
- ✅ Entity models (User, Book, BookRequest, SoldBook)
- ✅ Repository interfaces with JPA
- ✅ AuthService for registration and authentication
- ✅ Spring Security configuration with role-based access
- ✅ CustomUserDetailsService for Spring Security
- ✅ Controllers for public pages, auth, and dashboard
- ✅ Database configuration for PostgreSQL
- ✅ Compilation and build verified

### Frontend
- ✅ Landing page with feature overview
- ✅ Login form (styled, responsive)
- ✅ Registration form with validation (styled, responsive)
- ✅ Buyer Dashboard (3 tabs: Browse, Requests, Purchases)
- ✅ Seller Dashboard (3 tabs: Add Book, My Books, Requests)
- ✅ Admin Dashboard (4 tabs: Users, Books, Requests, Reports)
- ✅ Navigation and logout functionality

### Database
- ✅ PostgreSQL schema design
- ✅ Database initialization script (db/init.sql)
- ✅ Indexes for performance
- ✅ Test data (admin, test_seller, test_buyer)

---

## 📋 TODO - IMMEDIATE NEXT STEPS

### **PHASE 1: Core Book Management Features** (Priority: HIGH)

#### 1. **Book Management Service & Controller**
```java
// Create BookService.java
- createBook(bookDTO, sellerId): Book
- updateBook(bookId, bookDTO, sellerId): Book
- deleteBook(bookId, sellerId): void
- getBookById(bookId): Book
- getBooksBySeller(sellerId): List<Book>
- getAllAvailableBooks(): List<Book>
- searchBooks(query, filters): List<Book>

// Create BookController.java
- POST /seller/books - Add new book
- GET /books/{id} - Get book details
- PUT /books/{id} - Update book
- DELETE /books/{id} - Delete book
- GET /books - Browse all books (with pagination)
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/BookService.java`
- `src/main/java/com/example/bookxchange/dto/BookDTO.java`
- `src/main/java/com/example/bookxchange/controller/BookController.java`
- `src/main/resources/templates/books/book-detail.html`

#### 2. **Book Request Management**
```java
// Create BookRequestService.java
- createRequest(requestDTO, buyerId): BookRequest
- updateRequestStatus(reqId, newStatus): BookRequest
- fulfillRequest(reqId, sellerId): BookRequest
- getRequestById(reqId): BookRequest
- getRequestsByBuyer(buyerId): List<BookRequest>
- getPendingRequests(): List<BookRequest>

// Create BookRequestController.java
- POST /buyer/requests - Create new request
- GET /requests/{id} - Get request details
- PUT /requests/{id}/fulfill - Fulfill request
- GET /buyer/requests - User's requests
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/BookRequestService.java`
- `src/main/java/com/example/bookxchange/dto/BookRequestDTO.java`
- `src/main/java/com/example/bookxchange/controller/BookRequestController.java`

#### 3. **Shopping Cart & Purchase System**
```java
// Create ShoppingCartService.java
- addToCart(userId, bookId, quantity): void
- removeFromCart(userId, bookId): void
- getCart(userId): Cart
- clearCart(userId): void

// Create PurchaseService.java
- purchaseBooks(userId, items): Purchase
- getPurchaseHistory(userId): List<Purchase>
- validatePurchase(items): boolean

// Create SoldBookService.java
- recordSale(buyerId, bookId): SoldBook
- getBuyerPurchases(buyerId): List<Book>
- getSellerSales(sellerId): List<SoldBook>
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/ShoppingCartService.java`
- `src/main/java/com/example/bookxchange/service/PurchaseService.java`
- `src/main/java/com/example/bookxchange/controller/CartController.java`
- `src/main/java/com/example/bookxchange/controller/PurchaseController.java`

#### 4. **Update Dashboards with Dynamic Content**

**Buyer Dashboard Updates:**
```html
- Replace static book cards with dynamic data from database
- Implement "Add to Cart" and "Buy Now" buttons
- Show actual user's requests
- Show actual purchase history
- Implement search/filter functionality
```

**Seller Dashboard Updates:**
```html
- Show seller's actual books
- Show actual statistics (total books, sold, revenue)
- Show actual pending book requests
- Make Add Book form functional
- Add Edit/Delete book functionality
```

**Admin Dashboard Updates:**
```html
- Load all users with pagination
- Load all books with search
- Load all book requests
- Implement user edit/delete functionality
- Add real statistics

Files to update:
- src/main/resources/templates/dashboard/buyer-dashboard.html
- src/main/resources/templates/dashboard/seller-dashboard.html
- src/main/resources/templates/dashboard/admin-dashboard.html
```

---

### **PHASE 2: File Upload & Image Handling** (Priority: HIGH)

#### 1. **Profile Picture Upload**
```java
// Add to UserService
- uploadProfilePicture(userId, file): String
- deleteProfilePicture(userId): void

// Create file storage utility
- saveFile(file): String (returns filename/path)
- deleteFile(filename): void
- getFileUrl(filename): String
```

**Configuration needed:**
```properties
# In application.properties
app.upload.dir=uploads/profiles
app.upload.max-size=5MB
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/FileUploadService.java`
- `src/main/java/com/example/bookxchange/util/FileValidator.java`

#### 2. **Book Cover Image Upload**
```java
// Add to BookService
- uploadBookCover(bookId, file): String
- updateBookCover(bookId, file): String
```

**Files to modify:**
- `src/main/java/com/example/bookxchange/service/BookService.java`
- `src/main/resources/templates/dashboard/seller-dashboard.html` (Add file input)

---

### **PHASE 3: User Profile & Management** (Priority: MEDIUM)

#### 1. **User Profile Page**
```html
Template: src/main/resources/templates/user/profile.html
Features:
- Display user information
- Edit profile functionality
- Change password
- Upload profile picture
- View account statistics
```

#### 2. **User Management Service**
```java
// UserService.java
- updateProfile(userId, userDTO): User
- changePassword(userId, oldPassword, newPassword): void
- deleteAccount(userId): void
- getUserStats(userId): UserStats
```

---

### **PHASE 4: Search, Filter & Pagination** (Priority: MEDIUM)

```java
// Update BookRepository
- findByBookNameContainingIgnoreCase(name): List<Book>
- findByBookAuthorContainingIgnoreCase(author): List<Book>
- findByBookPriceBetween(min, max): List<Book>
- Page<Book> findAll(Pageable): Page<Book>

// Create SearchService
- searchBooks(query, filters, page, size): Page<Book>
- getCategories(): List<String>
- getPriceRange(): PriceRange
```

**Controller endpoints:**
```
GET /books?search=query&author=name&minPrice=10&maxPrice=50&page=0&size=12
```

---

### **PHASE 5: Notifications & Messaging** (Priority: MEDIUM)

#### 1. **Email Notifications**
Add dependency to pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

```java
// Create EmailService.java
- sendWelcomeEmail(email, username)
- sendRequestNotification(email, requestDetails)
- sendPurchaseConfirmation(email, purchaseDetails)
- sendPasswordResetEmail(email, resetToken)

// Create NotificationService.java
- createNotification(userId, message, type)
- getNotifications(userId): List<Notification>
- markAsRead(notificationId)
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/EmailService.java`
- `src/main/java/com/example/bookxchange/service/NotificationService.java`
- `src/main/java/com/example/bookxchange/entity/Notification.java`

#### 2. **In-App Notifications**
```html
Template: src/main/resources/templates/fragments/notifications.html
- Bell icon in navbar showing unread count
- Notification dropdown
- Mark as read functionality
```

---

### **PHASE 6: Admin Features** (Priority: MEDIUM)

```java
// Create AdminService.java
- suspendUser(userId, reason): void
- banUser(userId): void
- approveBook(bookId): void
- removeBook(bookId, reason): void
- generateReport(type, dateRange): Report

// Create ReportController.java
- GET /admin/reports/sales
- GET /admin/reports/users
- GET /admin/reports/platform
- GET /admin/reports/export
```

---

### **PHASE 7: Payment Integration** (Priority: HIGH)

#### 1. **Stripe Integration**
```xml
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>20.0.0</version>
</dependency>
```

```java
// Create PaymentService.java
- createCheckoutSession(cartItems, userId): String (Stripe session ID)
- verifyPayment(sessionId): PaymentStatus
- refundPayment(transactionId): void
- getPaymentHistory(userId): List<Payment>

// Create PaymentController.java
- POST /pay/checkout
- GET /pay/success
- GET /pay/cancel
- POST /webhook/stripe (Webhook for payment confirmation)
```

**Files to create:**
- `src/main/java/com/example/bookxchange/service/PaymentService.java`
- `src/main/java/com/example/bookxchange/controller/PaymentController.java`
- `src/main/java/com/example/bookxchange/entity/Payment.java`
- `src/main/resources/templates/checkout.html`

---

### **PHASE 8: Docker & Deployment** (Priority: MEDIUM)

#### 1. **Docker Configuration**
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Update `compose.yaml`:
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookxchange
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DATABASE: bookxchange
      POSTGRES_PASSWORD: postgres
```

#### 2. **Environment Configuration**
Create `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/bookxchange
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.mail.host=${MAIL_HOST}
spring.mail.username=${MAIL_USER}
stripe.api.key=${STRIPE_API_KEY}
```

---

### **PHASE 9: Testing** (Priority: MEDIUM)

```java
// Create test classes
- UserServiceTest
- BookServiceTest
- PurchaseServiceTest
- AuthenticationTest
- SecurityConfigTest

// Integration tests
- BookControllerIntegrationTest
- PurchaseFlowIntegrationTest
```

---

### **PHASE 10: CI/CD Pipeline** (Priority: HIGH for production)

Create `.github/workflows/build-deploy.yml`:
```yaml
name: Build and Deploy
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
      - run: mvn clean package
      - run: docker build -t bookxchange .
      - deploy to Render (if main branch)
```

---

## 📊 Suggested Implementation Order

1. **Week 1**: Book Management (CRUD operations)
2. **Week 2**: Book Requests & Shopping Cart
3. **Week 3**: Purchase System & Dynamic Dashboards
4. **Week 4**: File Upload & User Profiles
5. **Week 5**: Search, Filter & Pagination
6. **Week 6**: Payment Integration (Stripe)
7. **Week 7**: Email & Notifications
8. **Week 8**: Admin Features & Reports
9. **Week 9**: Docker & Testing
10. **Week 10**: CI/CD & Deployment to Render

---

## 🛠️ Database Updates Needed

After each phase, update database schema:

```sql
-- After Phase 3 (Notifications)
ALTER TABLE book_requests ADD COLUMN status VARCHAR(50) DEFAULT 'PENDING';
ALTER TABLE sold_books ADD COLUMN purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sold_books ADD COLUMN amount NUMERIC(10, 2);

-- After Phase 4 (Categories)
ALTER TABLE books ADD COLUMN category VARCHAR(100);
ALTER TABLE books ADD COLUMN isbn VARCHAR(50);

-- After Phase 5 (Notifications)
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(uid),
    message TEXT NOT NULL,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- After Phase 7 (Payments)
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(uid),
    amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50),
    transaction_id VARCHAR(255),
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔑 Key Implementation Tips

1. **Use DTOs** for data transfer between layers
2. **Implement pagination** for better performance
3. **Add proper error handling** with custom exceptions
4. **Use transactions** for multi-step operations
5. **Implement logging** for debugging
6. **Add input validation** for all forms
7. **Use repository pattern** for database access
8. **Implement caching** for frequently accessed data
9. **Use Spring events** for decoupled notifications
10. **Write unit tests** for critical business logic

---

## 📖 Current Status

**Compilation**: ✅ PASSING
**Project Structure**: ✅ COMPLETE
**Base Features**: ✅ IMPLEMENTED
**Database Setup**: ✅ READY
**Ready for**: 📚 Phase 1 - Book Management

---

## 🚀 Quick Start Commands

```bash
# Compile and verify
./mvnw.cmd clean compile

# Run the application
./mvnw.cmd spring-boot:run

# Run tests (after Phase 9)
./mvnw.cmd test

# Build Docker image
docker build -t bookxchange .

# Run with Docker Compose
docker-compose up

# Access application
# http://localhost:8080
```

---

## 🔗 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf](https://www.thymeleaf.org/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Stripe API](https://stripe.com/docs)
- [Docker Docs](https://docs.docker.com/)

---

## ✨ Summary

Your BookXchange application foundation is complete! You have:
- ✅ Proper entity design with relationships
- ✅ Secure authentication system
- ✅ Role-based access control
- ✅ Beautiful responsive UI templates
- ✅ Database schema ready
- ✅ Compilation successful

**Next Step**: Start with **Phase 1 - Book Management** to add CRUD operations for books and implement the shopping functionality.

Good luck! 🚀📚
