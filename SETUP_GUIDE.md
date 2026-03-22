# BookXchange Setup & Implementation Guide

## ✅ What Has Been Created

### 1. **Entity Classes**
- `User.java` - User entity with roles (ADMIN, SELLER, BUYER)
- `Book.java` - Book listing entity with seller relationship
- `BookRequest.java` - Book request entity for buyer requests
- `SoldBook.java` - Purchase history entity
- `UserRole.java` - Enum for user roles

### 2. **Repository Interfaces**
- `UserRepository.java` - CRUD operations and custom queries for users
- `BookRepository.java` - Book management queries
- `BookRequestRepository.java` - Book request queries
- `SoldBookRepository.java` - Purchase history queries

### 3. **Services**
- `AuthService.java` - User registration, login, and role switching
- `CustomUserDetailsService.java` - Spring Security integration

### 4. **Controllers**
- `PublicController.java` - Landing page, login, register pages
- `AuthController.java` - Registration form processing
- `DashboardController.java` - Dashboard routing based on user role

### 5. **Templates (Thymeleaf)**
- `landing.html` - Public landing page with feature overview
- `auth/login.html` - Login form
- `auth/register.html` - User registration form
- `dashboard/buyer-dashboard.html` - Buyer interface (Browse, Requests, Purchases)
- `dashboard/seller-dashboard.html` - Seller interface (Add books, My books, Requests)
- `dashboard/admin-dashboard.html` - Admin panel (Manage users, books, requests, reports)

### 6. **Configuration**
- `SecurityConfig.java` - Spring Security configuration with role-based access
- `application.properties` - Database and application settings

### 7. **Database**
- `db/init.sql` - SQL initialization script with all tables and indexes

---

## 🚀 Setup Instructions

### **Step 1: Database Setup**

#### Option A: Using Docker Compose (Recommended)
Your `compose.yaml` file should already be configured. Start PostgreSQL:
```bash
docker-compose up -d
```

#### Option B: Manual PostgreSQL Setup
1. Install PostgreSQL (if not already installed)
2. Create a new database:
```sql
CREATE DATABASE bookxchange;
```
3. Connect to the database:
```sql
\c bookxchange
```
4. Run the initialization script:
```bash
psql -U postgres -d bookxchange -f src/main/resources/db/init.sql
```

### **Step 2: Update Application Properties**
Edit `src/main/resources/application.properties` and ensure:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookxchange
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Change the username and password if your PostgreSQL has different credentials.

### **Step 3: Build the Project**
```bash
mvn clean install
```

### **Step 4: Run the Application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---

## 📋 Default Login Credentials

Three default accounts have been created in the database:

| Username | Email | Password | Role |
|----------|-------|----------|------|
| admin | admin@bookxchange.com | admin123 | ADMIN |
| test_seller | seller@test.com | admin123 | SELLER |
| test_buyer | buyer@test.com | admin123 | BUYER |

**⚠️ IMPORTANT**: Change these passwords in production!

---

## 🔐 Security Features Implemented

✅ **Role-Based Access Control (RBAC)**
- Admin: Full platform control
- Seller: Can list books, view requests
- Buyer: Can browse books, make requests, purchase

✅ **Password Encryption**
- Passwords are hashed using BCrypt
- Never stored in plain text

✅ **Spring Security Integration**
- Custom UserDetailsService
- Form-based login
- CSRF protection enabled (can be disabled for APIs)
- Session-based authentication

✅ **Path-Based Authorization**
- `/admin/**` - Admin only
- `/seller/**` - Seller only
- `/buyer/**` - Buyer only
- `/` - Public access

---

## 📄 Database Schema

### **Users Table**
```
uid (UUID, PK)
├── username (VARCHAR, UNIQUE)
├── user_email (VARCHAR, UNIQUE)
├── profile_pic (TEXT)
├── password_hash (VARCHAR)
├── user_role (ENUM: ADMIN, SELLER, BUYER)
└── date_joined (TIMESTAMP)
```

### **Books Table**
```
book_id (UUID, PK)
├── book_name (VARCHAR)
├── book_cover_pic (TEXT)
├── book_author (VARCHAR)
├── book_short_preview (TEXT)
├── book_price (NUMERIC)
├── quantity_left (INTEGER)
├── seller_id (FK → Users)
└── created_at (TIMESTAMP)
```

### **Book Requests Table**
```
req_id (UUID, PK)
├── req_subject (VARCHAR)
├── req_description (TEXT)
├── buyer_id (FK → Users)
├── req_fulfiller_id (FK → Users, NULLABLE)
└── created_at (TIMESTAMP)
```

### **Sold Books Table**
```
sold_id (UUID, PK)
├── buyer_id (FK → Users)
└── book_id (FK → Books)
```

---

## 🎯 Next Steps to Complete

### **Phase 1: Core Features** (Start here)
- [ ] **Book Management**
  - Create book listing service
  - Add book edit/delete functionality
  - Implement book search and filtering
  - Add book cover image upload
  
- [ ] **Book Purchasing**
  - Create cart system
  - Implement checkout process
  - Create payment integration (Stripe, PayPal, etc.)
  - Update inventory management

- [ ] **Book Requests**
  - Implement request creation from buyer
  - Add request notification system
  - Allow sellers to fulfill requests
  - Create request messaging

### **Phase 2: Advanced Features**
- [ ] **User Profiles**
  - Profile customization page
  - Review/rating system
  - User statistics dashboard
  - Profile picture upload

- [ ] **Notification System**
  - Email notifications (Spring Mail)
  - In-app notifications
  - Request status updates

- [ ] **Search & Filter**
  - Advanced search with filters
  - Category/Genre system
  - Price range filters

### **Phase 3: DevOps & Deployment**
- [ ] **Docker Setup**
  - Create Dockerfile for application
  - Docker Compose with PostgreSQL
  - Health checks and logging

- [ ] **CI/CD Pipeline** (GitHub Actions)
  - Build automation
  - Testing on pull requests
  - Deployment to Render

- [ ] **Render Deployment**
  - Configure environment variables
  - Database migration strategy
  - Staging and production environments

### **Phase 4: Enhancements**
- [ ] **API Development** (REST API)
  - Create REST endpoints
  - API documentation (Swagger/OpenAPI)
  - Mobile app support

- [ ] **Admin Features**
  - User suspension/banning
  - Platform statistics and reports
  - Content moderation

- [ ] **Performance**
  - Caching (Redis)
  - Database query optimization
  - Pagination implementation

---

## 🧪 Testing the Application

### **Manual Testing Checklist**

1. **User Registration**
   - [ ] Visit `http://localhost:8080/register`
   - [ ] Create new account with unique username and email
   - [ ] Try duplicate username/email (should show error)
   - [ ] Try password mismatch (should show error)

2. **User Login**
   - [ ] Visit `http://localhost:8080/login`
   - [ ] Login with test credentials
   - [ ] Try wrong password (should show error)
   - [ ] Verify redirect to dashboard

3. **Buyer Dashboard**
   - [ ] Login as test_buyer
   - [ ] Navigate tabs: Browse Books, My Requests, My Purchases
   - [ ] Click "Switch to Seller Mode"

4. **Seller Dashboard**
   - [ ] Login as test_seller
   - [ ] Check stats display
   - [ ] Navigate tabs: Add Book, My Books, Book Requests
   - [ ] Click "Switch to Buyer Mode"

5. **Admin Dashboard**
   - [ ] Login as admin
   - [ ] Check all stats
   - [ ] Navigate tabs: Users, Books, Requests, Reports

---

## 📝 Important Notes

1. **Database Initialization**
   - Hibernate is set to `ddl-auto=update`, so tables will be created automatically
   - The init.sql script provides manual setup if needed

2. **Password Hashing**
   - All passwords use BCrypt with cost factor 10
   - Default test password is "admin123"

3. **Session Management**
   - Session timeout is set to 30 minutes
   - Sessions are stored in JDBC (database)

4. **CORS/CSRF**
   - CSRF protection is currently disabled for simplicity
   - Enable it in SecurityConfig if using APIs

5. **Logging**
   - Root logging level is INFO
   - Application logging level is DEBUG
   - Adjust in application.properties as needed

---

## 🐛 Troubleshooting

### **Database Connection Error**
- Ensure PostgreSQL is running
- Check credentials in application.properties
- Verify database name is 'bookxchange'

### **Port Already in Use**
```bash
# Change port in application.properties
server.port=8081
```

### **Dependencies Not Found**
```bash
# Clear Maven cache and rebuild
mvn clean install -U
```

### **Thymeleaf Templates Not Found**
- Ensure templates are in `src/main/resources/templates/`
- Check file names match exactly (case-sensitive)

---

## 📚 Project Structure
```
bookxchange/
├── src/main/
│   ├── java/com/example/bookxchange/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── config/
│   │   └── BookxchangeApplication.java
│   └── resources/
│       ├── templates/
│       │   ├── landing.html
│       │   ├── auth/
│       │   └── dashboard/
│       ├── static/
│       ├── db/
│       │   └── init.sql
│       └── application.properties
├── pom.xml
├── compose.yaml
└── README.md
```

---

## 🔗 Useful URLs

- **Landing Page**: `http://localhost:8080/`
- **Login**: `http://localhost:8080/login`
- **Register**: `http://localhost:8080/register`
- **Dashboard**: `http://localhost:8080/dashboard`
- **Logout**: `http://localhost:8080/logout`

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review Spring Boot and Spring Security documentation
3. Check Thymeleaf template syntax
4. Verify database connectivity

Good luck with your BookXchange application! 🚀📚
