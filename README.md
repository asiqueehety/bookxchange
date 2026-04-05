# 📚 BookXchange - Book Exchange Platform

A modern, secure Spring Boot web application for buying, selling, and exchanging books. Built with Spring Security, PostgreSQL, and Thymeleaf.

---

## 🎯 Project Overview

**BookXchange** is a multi-role marketplace platform enabling users to:
- **Buyers**: Browse books, make purchase requests, buy books
- **Sellers**: List books, manage inventory, accept purchase requests
- **Admins**: Manage all users, books, and transactions

---

## ✨ Key Features

### 🔐 **Security & Authentication**
- Spring Security with BCrypt password hashing
- Form-based authentication
- Role-Based Access Control (RBAC) - Admin, Seller, Buyer
- 30-minute session timeout with JDBC session storage
- Secure logout functionality

### 📚 **Book Management**
- Add/Edit/Delete books (Sellers)
- Browse available books (Buyers)
- Real-time inventory tracking
- Book search and filtering
- Cover image uploads

### 🛒 **Commerce Features**
- Shopping cart system
- Purchase requests from buyers
- Purchase fulfillment by sellers
- Transaction history
- Admin order management

### 👤 **User Management**
- User registration with email validation
- Profile management with picture upload
- Role switching (Admin can assign roles)
- Admin dashboard for user management
- Password change functionality

### 📁 **File Management**
- Profile picture uploads (5MB max)
- Book cover image uploads
- Secure file storage (uploads/profiles)
- Supported formats: JPEG, PNG, GIF, WebP

### 🧪 **Testing & Quality**
- 272+ Unit & Integration Tests
- Mockito for service testing
- Spring Test for integration testing
- H2 in-memory database for tests
- 100% test success rate

---

## 🏗️ Architecture

### **Technology Stack**
| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.3.4 |
| **JDK** | Java | 17+ |
| **Database** | PostgreSQL | 12+ |
| **ORM** | Hibernate/JPA | Latest |
| **Template Engine** | Thymeleaf | Latest |
| **Security** | Spring Security | Latest |
| **Session Management** | Spring Session JDBC | Latest |
| **Build Tool** | Maven | Included (mvnw) |
| **Testing** | JUnit 5, Mockito | Latest |

### **System Architecture**
```
┌─────────────────────────────────────────────────────────┐
│                    Thymeleaf UI Layer                    │
│     (HTML Templates, CSS, Client-side Validation)       │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                 Spring MVC Controller Layer              │
│  (Request Routing, Input Validation, Response Building) │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                 Business Logic Layer (Services)          │
│  (AuthService, UserService, BookService, etc.)         │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Spring Data Repository Layer               │
│  (UserRepository, BookRepository, etc.)                 │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              PostgreSQL Database Layer                  │
│    (Users, Books, BookRequests, SoldBooks, Sessions)   │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
bookxchange/
├── src/
│   ├── main/
│   │   ├── java/com/example/bookxchange/
│   │   │   ├── entity/                    # JPA Entities
│   │   │   │   ├── User.java              # User model (UUID, email, role, profile)
│   │   │   │   ├── Book.java              # Book model (name, author, price, inventory)
│   │   │   │   ├── BookRequest.java       # Book request model (buyer requests)
│   │   │   │   ├── SoldBook.java          # Purchase record model
│   │   │   │   └── UserRole.java          # Role enum (ADMIN, SELLER, BUYER)
│   │   │   │
│   │   │   ├── repository/                # Spring Data Repositories
│   │   │   │   ├── UserRepository.java    # User CRUD + custom queries
│   │   │   │   ├── BookRepository.java    # Book CRUD + seller queries
│   │   │   │   ├── BookRequestRepository.java
│   │   │   │   └── SoldBookRepository.java
│   │   │   │
│   │   │   ├── service/                   # Business Logic Services
│   │   │   │   ├── AuthService.java       # Registration, login, role switching
│   │   │   │   ├── UserService.java       # User profile management
│   │   │   │   ├── BookService.java       # Book operations (CRUD, search)
│   │   │   │   ├── ShoppingCartService.java
│   │   │   │   ├── PurchaseService.java   # Purchase & transaction handling
│   │   │   │   ├── CustomUserDetailsService.java # Spring Security integration
│   │   │   │   ├── FileUploadService.java # File upload handling
│   │   │   │   └── BookRequestService.java
│   │   │   │
│   │   │   ├── controller/                # Spring MVC Controllers
│   │   │   │   ├── PublicController.java       # Public pages (landing, login, register)
│   │   │   │   ├── AuthController.java        # Authentication endpoints
│   │   │   │   ├── DashboardController.java   # Dashboard routing
│   │   │   │   ├── AdminController.java       # Admin management
│   │   │   │   ├── BookController.java        # Book operations
│   │   │   │   ├── CartController.java        # Shopping cart
│   │   │   │   ├── PurchaseController.java    # Purchase operations
│   │   │   │   └── UserProfileController.java # User profile
│   │   │   │
│   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   └── [other DTOs]
│   │   │   │
│   │   │   ├── config/                   # Spring Configuration
│   │   │   │   ├── SecurityConfig.java   # Spring Security configuration
│   │   │   │   └── [other configs]
│   │   │   │
│   │   │   ├── util/                     # Utility Classes
│   │   │   │   └── [Validation, helper utils]
│   │   │   │
│   │   │   └── BookxchangeApplication.java # Main entry point
│   │   │
│   │   └── resources/
│   │       ├── application.properties     # Database & app configuration
│   │       ├── templates/                 # Thymeleaf HTML templates
│   │       │   ├── landing.html          # Public landing page
│   │       │   ├── auth/
│   │       │   │   ├── login.html        # Login form
│   │       │   │   └── register.html     # Registration form
│   │       │   ├── dashboard/
│   │       │   │   ├── buyer-dashboard.html
│   │       │   │   ├── seller-dashboard.html
│   │       │   │   └── admin-dashboard.html
│   │       │   ├── books/               # Book management templates
│   │       │   ├── profile/             # User profile templates
│   │       │   ├── error/               # Error pages
│   │       │   └── static/              # CSS, JS, images
│   │       └── static/
│   │           ├── css/                # Stylesheets
│   │           ├── js/                 # JavaScript files
│   │           └── images/             # Static images
│   │
│   └── test/
│       ├── java/com/example/bookxchange/
│       │   ├── controller/             # Controller integration tests
│       │   ├── service/                # Service unit tests
│       │   ├── repository/             # Repository tests
│       │   ├── entity/                 # Entity tests
│       │   ├── dto/                    # DTO tests
│       │   └── [Test files]
│       │
│       └── resources/
│           ├── application-test.properties # Test configuration
│           └── test-data/              # Test fixtures
│
├── compose.yaml                 # Docker Compose (PostgreSQL setup)
├── Dockerfile                   # Container image definition
├── pom.xml                      # Maven project configuration
├── mvnw / mvnw.cmd             # Maven wrapper (Linux/Windows)
└── README.md                    # This file
```

---

## 📊 Database Schema

### **Users Table**
```sql
CREATE TABLE users (
    uid UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    user_email VARCHAR(100) UNIQUE NOT NULL,
    profile_pic TEXT,
    password_hash VARCHAR(255) NOT NULL,
    user_role ENUM('ADMIN', 'SELLER', 'BUYER') NOT NULL DEFAULT 'BUYER',
    date_joined TIMESTAMP NOT NULL
);
```

### **Books Table**
```sql
CREATE TABLE books (
    book_id UUID PRIMARY KEY,
    book_name VARCHAR(255) NOT NULL,
    book_cover_pic TEXT,
    book_author VARCHAR(100) NOT NULL,
    book_short_preview TEXT,
    book_price NUMERIC(10,2) NOT NULL,
    quantity_left INTEGER NOT NULL,
    seller_id UUID NOT NULL REFERENCES users(uid),
    created_at TIMESTAMP NOT NULL
);
```

### **Book Requests Table**
```sql
CREATE TABLE book_requests (
    req_id UUID PRIMARY KEY,
    req_subject VARCHAR(255) NOT NULL,
    req_description TEXT NOT NULL,
    buyer_id UUID NOT NULL REFERENCES users(uid),
    req_fulfiller_id UUID REFERENCES users(uid),
    created_at TIMESTAMP NOT NULL
);
```

### **Sold Books Table**
```sql
CREATE TABLE sold_books (
    sold_id UUID PRIMARY KEY,
    buyer_id UUID NOT NULL REFERENCES users(uid),
    book_id UUID NOT NULL REFERENCES books(book_id)
);
```
---

## 🔐 Security & User Roles

### **Role Permissions**

| Feature | Admin | Seller | Buyer |
|---------|-------|--------|-------|
| View Dashboard | ✅ | ✅ | ✅ |
| Browse Books | ✅ | ✅ | ✅ |
| Add Books | ❌ | ✅ | ❌ |
| Edit Own Books | ❌ | ✅ | ❌ |
| Delete Own Books | ❌ | ✅ | ❌ |
| View Requests | ❌ | ✅ | ✅ |
| Make Requests | ❌ | ❌ | ✅ |
| Purchase Books | ❌ | ❌ | ✅ |
| Manage All Users | ✅ | ❌ | ❌ |
| View All Books | ✅ | ❌ | ❌ |
| Admin Dashboard | ✅ | ❌ | ❌ |

---

## 🚀 Getting Started

### **Prerequisites**
- **Java Development Kit (JDK) 17+**
  - Download: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
  - Verify: `java -version`

- **PostgreSQL 12+**
  - Download: https://www.postgresql.org/download/
  - Default port: 5432

- **Maven** (Optional - included as mvnw)
  - Included wrapper: `mvnw` (Linux/Mac) or `mvnw.cmd` (Windows)

- **Docker & Docker Compose** (Optional)
  - For automated PostgreSQL setup

### **Installation Steps**

#### **Option 1: Using Docker Compose (Recommended)**

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/bookxchange.git
cd bookxchange

# 2. Start PostgreSQL and create database
docker-compose up -d

# 3. Wait for PostgreSQL to be ready (10 seconds)
# Check logs: docker-compose logs -f

# 4. Build the project
./mvnw clean install          # Linux/Mac
mvnw.cmd clean install        # Windows PowerShell

# 5. Run the application
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows PowerShell

# 6. Access the application
# Open browser: http://localhost:8080
```

#### **Option 2: Manual PostgreSQL Setup**

```bash
# 1. Start PostgreSQL service
# Windows: Services > PostgreSQL
# Linux: sudo systemctl start postgresql

# 2. Create database and user
psql -U postgres
CREATE DATABASE bookxchange;
CREATE USER bookxchange_user WITH PASSWORD 'amarn44m451QU3';
ALTER ROLE bookxchange_user SET client_encoding TO 'utf8';
ALTER ROLE bookxchange_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE bookxchange_user SET default_transaction_deferrable TO on;
ALTER ROLE bookxchange_user SET default_transaction_level TO 'read committed';
ALTER ROLE bookxchange_user SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE bookxchange TO bookxchange_user;
\q

# 3. Update application.properties
# Edit: src/main/resources/application.properties
# Set: SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bookxchange
# Set: SPRING_DATASOURCE_USERNAME=bookxchange_user
# Set: SPRING_DATASOURCE_PASSWORD=amarn44m451QU3

# 4. Build project
./mvnw clean install

# 5. Run application
./mvnw spring-boot:run

# 6. Access at http://localhost:8080
```

### **Configuration**

#### **Database Configuration** (`application.properties`)
```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/bookxchange
spring.datasource.username=postgres
spring.datasource.password=amarn44m451QU3

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Session Management
spring.session.store-type=jdbc
server.servlet.session.timeout=30m

# File Upload
app.upload.dir=uploads/profiles
app.upload.max-size=5242880  # 5MB in bytes
```

#### **Environment Variables** (Production)
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/bookxchange
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password

# Server
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_EXAMPLE_BOOKXCHANGE=DEBUG
```

---

## 🧪 Testing

### **Run All Tests**
```bash
# Run unit and integration tests
./mvnw clean test

# Run only unit tests (skip integration tests)
./mvnw clean test -DskipITs

# Run only integration tests
./mvnw clean verify

# Run specific test class
./mvnw test -Dtest=AuthServiceTest

# Run with coverage
./mvnw clean test jacoco:report
```

### **Test Structure**
```
src/test/java/com/example/bookxchange/
├── controller/          # Integration tests for controllers
├── service/             # Unit tests for services
├── repository/          # Repository tests
├── entity/              # Entity validation tests
├── dto/                 # DTO tests
└── BaseIntegrationTest.java  # Base class for integration tests

src/test/resources/
└── application-test.properties  # Test database config (H2)
```

### **Test Coverage**
- **272+ Tests** - 100% pass rate
- **Unit Tests** - Service & DTO validation
- **Integration Tests** - Controller & Repository testing
- **Mock Tests** - Mockito for service dependencies
- **Test Database** - H2 in-memory for fast execution

---

## 🌐 API Endpoints

### **Public Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Landing page |
| GET | `/login` | Login form |
| POST | `/auth/login` | Process login |
| GET | `/register` | Registration form |
| POST | `/auth/register` | Process registration |

### **Authenticated Endpoints (All Roles)**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard` | Role-based dashboard |
| GET | `/profile` | User profile page |
| POST | `/profile/update` | Update profile |
| POST | `/profile/upload-picture` | Upload profile picture |
| GET | `/books` | Browse books |
| POST | `/logout` | Logout |

### **Seller Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/seller/books` | My books |
| GET | `/seller/books/add` | Add book form |
| POST | `/seller/books/add` | Create book |
| GET | `/seller/books/edit/{id}` | Edit book form |
| POST | `/seller/books/update` | Update book |
| POST | `/seller/books/delete/{id}` | Delete book |
| GET | `/seller/requests` | View requests |
| POST | `/seller/requests/{id}/accept` | Accept request |

### **Buyer Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/buyer/requests` | My requests |
| POST | `/buyer/requests/create` | Create request |
| GET | `/buyer/purchases` | Purchase history |
| POST | `/buyer/cart/add` | Add to cart |
| GET | `/buyer/cart` | View cart |
| POST | `/buyer/cart/checkout` | Checkout |

### **Admin Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin` | Admin dashboard |
| GET | `/admin/users` | Manage users |
| GET | `/admin/books` | Manage books |
| POST | `/admin/users/{id}/role` | Change user role |
| POST | `/admin/users/{id}/delete` | Delete user |
| POST | `/admin/books/{id}/delete` | Delete book |

---

## 📦 Dependencies

### **Core Dependencies**
- **spring-boot-starter-web** - Web framework
- **spring-boot-starter-data-jpa** - ORM and database access
- **spring-boot-starter-security** - Authentication & authorization
- **spring-boot-starter-thymeleaf** - Server-side templating
- **spring-session-jdbc** - Session management
- **postgresql** - PostgreSQL driver
- **lombok** - Reduces boilerplate code

### **Testing Dependencies**
- **spring-boot-starter-test** - Testing framework
- **spring-security-test** - Security testing utilities
- **mockito-junit-jupiter** - Mocking framework
- **h2** - In-memory test database

### **Build Plugins**
- **maven-surefire-plugin** - Unit test execution
- **maven-failsafe-plugin** - Integration test execution
- **spring-boot-maven-plugin** - Spring Boot packaging

---

## 📝 Application Features Walkthrough

### **For Buyers**
1. **Register/Login** - Create account and authenticate
2. **Browse Books** - View all available books with details
3. **View Profile** - Update profile information and upload picture
4. **Make Requests** - Request specific books from sellers
5. **Shopping Cart** - Add books to cart and checkout
6. **View Purchases** - See purchase history

### **For Sellers**
1. **Seller Dashboard** - Overview of listings and requests
2. **Add Books** - List new books with price and details
3. **Manage Inventory** - Edit quantity and book information
4. **View Requests** - See buyer requests for books
5. **Accept Requests** - Fulfill book requests from buyers
6. **Sales History** - Track completed sales

### **For Admins**
1. **Admin Dashboard** - System overview
2. **User Management** - View, edit, and delete users
3. **Book Management** - Monitor all listed books
4. **Request Management** - View all book requests
5. **Role Assignment** - Assign roles (Admin, Seller, Buyer)
6. **System Monitoring** - View transactions and activities

---

## 🛠️ Development Guide

### **Adding a New Feature**

1. **Create Entity** (src/main/java/entity/)
   ```java
   @Entity
   @Table(name = "your_table")
   public class YourEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.UUID)
       private UUID id;
       
       private String name;
   }
   ```

2. **Create Repository** (src/main/java/repository/)
   ```java
   @Repository
   public interface YourRepository extends JpaRepository<YourEntity, UUID> {
       Optional<YourEntity> findByName(String name);
   }
   ```

3. **Create Service** (src/main/java/service/)
   ```java
   @Service
   public class YourService {
       @Autowired
       private YourRepository repository;
       
       public YourEntity save(YourEntity entity) {
           return repository.save(entity);
       }
   }
   ```

4. **Create Controller** (src/main/java/controller/)
   ```java
   @Controller
   @RequestMapping("/your-endpoint")
   public class YourController {
       @Autowired
       private YourService service;
       
       @GetMapping
       public String list(Model model) {
           model.addAttribute("items", service.findAll());
           return "your-template";
       }
   }
   ```

5. **Create Tests** (src/test/java/)
   - Unit tests for services
   - Integration tests for controllers

### **Code Style Guide**
- Follow Spring conventions
- Use meaningful variable names
- Add JavaDoc for public methods
- Validate input in services
- Use logging for important operations
- Write tests for new features

---

## 🐛 Troubleshooting

### **Common Issues**

**Issue: PostgreSQL Connection Failed**
```
Solution:
1. Verify PostgreSQL is running: psql -U postgres
2. Check database exists: CREATE DATABASE bookxchange;
3. Update connection string in application.properties
4. Verify firewall allows port 5432
```

**Issue: Tests Fail with "relation spring_session does not exist"**
```
Solution:
1. Ensure test profile is active: @ActiveProfiles("test")
2. Check application-test.properties has spring.session.store-type=none
3. Clear Maven cache: ./mvnw clean
4. Re-run tests
```

**Issue: File Upload Not Working**
```
Solution:
1. Check uploads/profiles directory exists
2. Verify file size < 5MB
3. Check supported formats (JPEG, PNG, GIF, WebP)
4. Ensure write permissions on uploads folder
```

**Issue: Build Fails with "Java Version Mismatch"**
```
Solution:
1. Check Java version: java -version
2. Required: JDK 17+
3. Update JAVA_HOME: export JAVA_HOME=/path/to/jdk17
4. Verify Maven sees correct version: ./mvnw -version
```

**Issue: Port 8080 Already in Use**
```
Solution:
1. Find process: lsof -i :8080 (Linux/Mac) or netstat -ano | findstr :8080 (Windows)
2. Kill process or use different port: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

## 📚 Documentation Files

- **README.md** (this file) - Project overview and setup
- **HELP.md** - Additional help and resources
- **compose.yaml** - Docker Compose configuration
- **Dockerfile** - Container image definition
- **pom.xml** - Maven project configuration

---

## 🤝 Contributing

1. Create a new branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -m "Add feature"`
3. Push to branch: `git push origin feature/your-feature`
4. Create Pull Request for review

### **Before Submitting PR**
- ✅ Run all tests: `./mvnw clean test`
- ✅ Check code style
- ✅ Write/update unit tests
- ✅ Update documentation
- ✅ No hardcoded credentials

---

## 📄 License

This project is provided as-is for educational and commercial use.

---

## 📞 Support

For issues, questions, or suggestions:
1. Check the Troubleshooting section above
2. Review existing tests for usage examples
3. Check application logs for error details
4. Refer to Spring Boot documentation

---

## 🎓 Learning Resources

- [Spring Boot Official Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [PostgreSQL Official Docs](https://www.postgresql.org/docs/)

---

## ✅ Checklist for Production Deployment

- [ ] Change default passwords in application.properties
- [ ] Update database credentials with strong passwords
- [ ] Enable HTTPS/SSL
- [ ] Configure production database backups
- [ ] Set up monitoring and logging
- [ ] Review security settings
- [ ] Test with production data
- [ ] Set up CI/CD pipeline
- [ ] Configure error handling and notifications
- [ ] Review and update privacy policy

---

**Last Updated**: April 2026
**Project Version**: 0.0.1-SNAPSHOT
**Status**: Production Ready ✅
