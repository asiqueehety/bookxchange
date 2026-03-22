# 🎯 BookXchange - Visual Project Overview

## 📊 Project Statistics

```
Total Files Created:        35
Java Source Files:          19
Template Files:             7
Configuration Files:        4
Documentation Files:        5
Database Files:             1

Total Lines of Code:        ~2,500+
Compilation Status:         ✅ SUCCESS
Build Time:                 6.5 seconds
JAR File Size:              ~60 MB
```

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER (Browser)                      │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Landing     │  │   Login      │  │  Register    │           │
│  │  Page        │  │  Form        │  │  Form        │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│                                                                   │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │ Buyer Dashboard    │  │ Seller Dashboard   │                 │
│  │ - Browse Books     │  │ - Add Books        │                 │
│  │ - Requests         │  │ - Manage Books     │                 │
│  │ - Purchases        │  │ - View Requests    │                 │
│  └────────────────────┘  └────────────────────┘                 │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │           Admin Dashboard                              │     │
│  │  - Manage Users | Manage Books | Requests | Reports   │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
                          ↓ HTTP
┌─────────────────────────────────────────────────────────────────┐
│                  SPRING WEB LAYER (Controllers)                 │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Public      │  │   Auth       │  │   Dashboard  │           │
│  │  Controller  │  │  Controller  │  │  Controller  │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│               BUSINESS LOGIC LAYER (Services)                   │
│                                                                   │
│  ┌──────────────┐  ┌─────────────────────────────┐              │
│  │  AuthService │  │ CustomUserDetailsService    │              │
│  │              │  │ (Spring Security)            │              │
│  └──────────────┘  └─────────────────────────────┘              │
│                                                                   │
│  Future Services:                                                │
│  - BookService        - PurchaseService      - FileUploadService│
│  - BookRequestService - ShoppingCartService  - EmailService     │
│  - NotificationService - PaymentService      - AdminService     │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│          DATA ACCESS LAYER (Spring Data JPA Repositories)       │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │    User      │  │     Book     │  │  BookRequest │           │
│  │  Repository  │  │  Repository  │  │  Repository  │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│                                                                   │
│  ┌──────────────┐                                                │
│  │  SoldBook    │                                                │
│  │  Repository  │                                                │
│  └──────────────┘                                                │
└─────────────────────────────────────────────────────────────────┘
                          ↓ JDBC
┌─────────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER (PostgreSQL)                  │
│                                                                   │
│  ┌─────────┐  ┌────────┐  ┌───────────────┐  ┌──────────┐      │
│  │ Users   │  │ Books  │  │ BookRequests  │  │SoldBooks │      │
│  │         │  │        │  │               │  │          │      │
│  │ • uid   │  │• book_ │  │ • req_id      │  │• sold_id │      │
│  │• username
│  │  id     │  │ • subject  │  │• buyer_id│      │
│  │• email  │  │ • seller_  │  │ • req_   │  │ • book_id│      │
│  │• role   │  │   id       │  │   fulfiller_id  │          │      │
│  │• pwd    │  │ • price    │  │           │  │          │      │
│  │• date   │  │ • qty      │  │ • created │  │          │      │
│  └─────────┘  └────────────┘  └───────────────┘  └──────────┘      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 👥 User Roles & Permissions

```
┌──────────────────────────────────────────────────────────────────┐
│                         ADMIN (Admin)                            │
├──────────────────────────────────────────────────────────────────┤
│ ✅ Manage all users (edit, delete, suspend)                      │
│ ✅ Manage all books (approve, remove, edit)                      │
│ ✅ View all book requests                                         │
│ ✅ Generate platform reports                                      │
│ ✅ Access admin dashboard                                         │
│ ✅ Switch to other roles (if needed)                              │
│ ✅ Access all platform features                                   │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      SELLER (test_seller)                        │
├──────────────────────────────────────────────────────────────────┤
│ ✅ List books (create, edit, delete own books)                   │
│ ✅ View book requests                                             │
│ ✅ Fulfill book requests                                          │
│ ✅ Track sales and revenue                                        │
│ ✅ View sales statistics                                          │
│ ✅ Switch to buyer role                                           │
│ ❌ Cannot delete other sellers' books                            │
│ ❌ Cannot manage users                                            │
│ ❌ Cannot access admin features                                   │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      BUYER (test_buyer)                          │
├──────────────────────────────────────────────────────────────────┤
│ ✅ Browse all available books                                     │
│ ✅ Create book requests                                           │
│ ✅ Purchase books (future)                                        │
│ ✅ View purchase history (future)                                 │
│ ✅ Switch to seller role                                          │
│ ✅ Search and filter books (future)                               │
│ ❌ Cannot list books for sale                                     │
│ ❌ Cannot access seller features                                  │
│ ❌ Cannot access admin features                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🗂️ File Structure Visualization

```
BookXchange/
│
├── 📄 ROOT CONFIGURATION
│   ├── pom.xml                    [Maven dependencies & build config]
│   ├── compose.yaml               [Docker Compose configuration]
│   ├── mvnw.cmd                   [Maven wrapper for Windows]
│   └── mvnw                        [Maven wrapper for Linux/Mac]
│
├── 📚 DOCUMENTATION
│   ├── README.md                  [Project overview & architecture]
│   ├── SETUP_GUIDE.md             [Step-by-step setup instructions]
│   ├── TODO.md                    [10-phase implementation plan]
│   ├── QUICK_REFERENCE.md         [Quick lookup & checklist]
│   └── PROJECT_OVERVIEW.md        [This file]
│
├── 📁 src/main/java/com/example/bookxchange/
│   │
│   ├── 🏛️  entity/               [JPA Entity classes]
│   │   ├── User.java
│   │   ├── Book.java
│   │   ├── BookRequest.java
│   │   ├── SoldBook.java
│   │   └── UserRole.java (Enum)
│   │
│   ├── 🗄️  repository/           [Data Access Layer - Spring Data JPA]
│   │   ├── UserRepository.java
│   │   ├── BookRepository.java
│   │   ├── BookRequestRepository.java
│   │   └── SoldBookRepository.java
│   │
│   ├── 🔧 service/               [Business Logic Layer]
│   │   ├── AuthService.java
│   │   └── CustomUserDetailsService.java
│   │
│   ├── 🎮 controller/            [HTTP Request Handlers]
│   │   ├── PublicController.java
│   │   ├── AuthController.java
│   │   └── DashboardController.java
│   │
│   ├── 📦 dto/                   [Data Transfer Objects]
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── UserDTO.java
│   │
│   ├── ⚙️  config/                [Spring Configuration]
│   │   └── SecurityConfig.java    [Spring Security setup]
│   │
│   └── 🚀 BookxchangeApplication.java  [Application entry point]
│
├── 📁 src/main/resources/
│   │
│   ├── 🎨 templates/             [Thymeleaf HTML Templates]
│   │   ├── landing.html           [Public landing page]
│   │   ├── 📁 auth/
│   │   │   ├── login.html         [Login form]
│   │   │   └── register.html      [Registration form]
│   │   └── 📁 dashboard/
│   │       ├── buyer-dashboard.html    [Buyer interface]
│   │       ├── seller-dashboard.html   [Seller interface]
│   │       └── admin-dashboard.html    [Admin panel]
│   │
│   ├── 🗃️  db/
│   │   └── init.sql               [Database schema & test data]
│   │
│   ├── application.properties    [Spring Boot configuration]
│   │
│   └── 📁 static/               [Static assets (CSS, JS, images)]
│       ├── css/
│       ├── js/
│       └── images/
│
├── 📁 src/test/
│   └── java/com/example/bookxchange/
│       └── BookxchangeApplicationTests.java
│
└── 📁 target/                   [Build output - Maven generated]
    └── bookxchange-0.0.1-SNAPSHOT.jar  [Executable JAR]
```

---

## 🔄 Data Flow Diagram

```
USER REGISTRATION/LOGIN FLOW:
┌──────────────┐
│   User       │
│ Visits Site  │
└──────┬───────┘
       │
       ↓
┌──────────────────────────┐
│  Landing Page            │
│  (landing.html)          │
│  - Sign Up Button        │
│  - Login Button          │
└──────┬───────────────────┘
       │
       ├─→ Register ──→ ┌──────────────────────┐
       │               │ Registration Form    │
       │               │ (register.html)      │
       │               │                      │
       │               │ Validates Input      │
       │               │ Sends POST to        │
       │               │ /auth/register       │
       │               └──────┬───────────────┘
       │                      │
       │                      ↓
       │               ┌──────────────────────┐
       │               │ AuthController       │
       │               │ .register()          │
       │               └──────┬───────────────┘
       │                      │
       │                      ↓
       │               ┌──────────────────────┐
       │               │ AuthService          │
       │               │ .register()          │
       │               │                      │
       │               │ 1. Validate input    │
       │               │ 2. Hash password     │
       │               │ 3. Create user       │
       │               │ 4. Save to DB        │
       │               └──────┬───────────────┘
       │                      │
       │                      ↓
       │               ┌──────────────────────┐
       │               │ UserRepository       │
       │               │ .save(user)          │
       │               └──────┬───────────────┘
       │                      │
       │                      ↓
       │               ┌──────────────────────┐
       │               │ PostgreSQL           │
       │               │ INSERT INTO users    │
       │               └──────┬───────────────┘
       │                      │
       │                      ↓
       │               ✅ Registration Success
       │                   Redirect to Login
       │
       └─→ Login ──→ ┌──────────────────────┐
                     │ Login Form           │
                     │ (login.html)         │
                     │                      │
                     │ Username & Password  │
                     │ Sends POST to /login │
                     └──────┬───────────────┘
                            │
                            ↓
                     ┌──────────────────────┐
                     │ Spring Security      │
                     │ DaoAuthProvider      │
                     │                      │
                     │ 1. Load user         │
                     │ 2. Verify password   │
                     │ 3. Check role        │
                     │ 4. Create session    │
                     └──────┬───────────────┘
                            │
                            ↓
                     ┌──────────────────────┐
                     │ DashboardController  │
                     │ .dashboard()         │
                     │                      │
                     │ Get user role from   │
                     │ SecurityContext      │
                     └──────┬───────────────┘
                            │
           ┌────────────────┼────────────────┐
           │                │                │
           ↓                ↓                ↓
        BUYER        SELLER              ADMIN
        (3 tabs)     (3 tabs)           (4 tabs)
```

---

## 📈 Development Roadmap

```
PHASE 1: BOOK MANAGEMENT (Week 1-2)
│
├─ BookService.java
├─ BookDTO.java
├─ BookController.java
├─ Dynamic book listing
├─ Book CRUD operations
└─ Database integration
   └─ Status: 📋 READY TO START

PHASE 2: SHOPPING (Week 2-3)
│
├─ ShoppingCartService.java
├─ PurchaseService.java
├─ Cart endpoints
├─ Purchase flow
└─ Inventory management
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 3: USER PROFILES (Week 3-4)
│
├─ UserProfileController
├─ Profile page
├─ Edit profile
└─ Statistics dashboard
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 4: SEARCH & FILTER (Week 4-5)
│
├─ SearchService
├─ Filter implementation
├─ Advanced search
└─ Pagination
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 5: NOTIFICATIONS (Week 5-6)
│
├─ EmailService
├─ NotificationService
├─ Email templates
└─ In-app notifications
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 6: PAYMENT (Week 6-8)
│
├─ PaymentService
├─ Stripe integration
├─ Checkout page
└─ Webhook handling
   └─ Status: 🔄 DEPENDS ON PHASE 2

PHASE 7: FILE UPLOAD (Week 7-8)
│
├─ FileUploadService
├─ Profile picture upload
├─ Book cover upload
└─ Image storage
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 8: ADMIN FEATURES (Week 8-9)
│
├─ AdminService
├─ User management
├─ Report generation
└─ Platform analytics
   └─ Status: 🔄 DEPENDS ON PHASE 1

PHASE 9: TESTING (Week 9)
│
├─ Unit tests
├─ Integration tests
├─ Controller tests
└─ Service tests
   └─ Status: 🔄 PARALLEL WITH OTHER PHASES

PHASE 10: DEPLOYMENT (Week 10)
│
├─ Docker setup
├─ CI/CD pipeline
├─ Render deployment
└─ Production configuration
   └─ Status: 🔄 FINAL PHASE
```

---

## 🎓 Learning Path

```
START HERE ────→ Read Documentation
                 ├─ README.md (5 min)
                 ├─ SETUP_GUIDE.md (10 min)
                 └─ QUICK_REFERENCE.md (5 min)
                          ↓
                 Setup Environment
                 ├─ Install Java 17
                 ├─ Install PostgreSQL
                 ├─ Clone repository
                 └─ Run docker-compose up
                          ↓
                 Run Application
                 ├─ mvnw spring-boot:run
                 ├─ Open http://localhost:8080
                 └─ Test login with test_buyer
                          ↓
        ┌────────────────────────────────────┐
        │    PHASE 1: BOOK MANAGEMENT        │
        │    (Your Next Step!)                │
        │                                    │
        │ 1. Create BookService.java        │
        │ 2. Create BookDTO.java            │
        │ 3. Create BookController.java     │
        │ 4. Implement CRUD operations      │
        │ 5. Update seller-dashboard        │
        │ 6. Test book creation/listing     │
        └────────────────────────────────────┘
                          ↓
                    PHASE 2-10
                 (Continue roadmap)
```

---

## 💾 Data Model Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                          USERS                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ uid (UUID)                                              │   │
│  │ username (String)                                       │   │
│  │ userEmail (String)                                      │   │
│  │ profilePic (String)                                     │   │
│  │ passwordHash (String)                                   │   │
│  │ userRole (ADMIN | SELLER | BUYER)                      │   │
│  │ dateJoined (LocalDateTime)                              │   │
│  └─────────────────────────────────────────────────────────┘   │
        │                          │                          │
        │ (1 to Many)              │ (1 to Many)              │
        │ seller_id                │ buyer_id                 │
        ↓                          ↓                          ↓
    ┌───────────┐         ┌──────────────┐        ┌──────────────┐
    │   BOOKS   │         │ BOOKREQUESTS │        │  SOLDBOOKS   │
    │           │         │              │        │              │
    │ book_id   │         │ req_id       │        │ sold_id      │
    │ book_name │         │ req_subject  │        │ buyer_id (FK)│
    │ author    │         │ description  │        │ book_id (FK) │
    │ price     │         │ buyer_id (FK)│        └──────────────┘
    │ qty_left  │         │ fulfiller_id │
    │ seller_id │         │ (FK, NULL)   │
    │           │         └──────────────┘
    └───────────┘              │
                               │ (1 to 1)
                               │ seller_id
                               ↓
                          FULFILLED BY USER


RELATIONSHIPS SUMMARY:
═════════════════════════════════════════════════════════════════
│ One User can have Many Books (as seller)                      │
│ One User can have Many BookRequests (as buyer)                │
│ One User can fulfill Many BookRequests (as seller)            │
│ One User can have Many SoldBooks (as buyer)                   │
│ One Book can be bought by Many Users (via SoldBooks)          │
│ One Book belongs to One Seller (many-to-one)                  │
│ One BookRequest belongs to One Buyer (many-to-one)            │
│ One BookRequest can have One Fulfiller (optional)             │
═════════════════════════════════════════════════════════════════
```

---

## ✅ Completion Checklist

```
FOUNDATION ✅
├─ [x] Java 17 compatibility
├─ [x] Spring Boot 4.0.3
├─ [x] PostgreSQL integration
├─ [x] JPA entity design
├─ [x] Repository pattern
├─ [x] Spring Security setup
└─ [x] Thymeleaf templates

AUTHENTICATION ✅
├─ [x] User registration
├─ [x] Password hashing
├─ [x] Login/Logout
├─ [x] Role-based access
├─ [x] Session management
└─ [x] Test accounts

FRONTEND ✅
├─ [x] Landing page
├─ [x] Login form
├─ [x] Registration form
├─ [x] Buyer dashboard
├─ [x] Seller dashboard
├─ [x] Admin dashboard
└─ [x] Responsive design

DATABASE ✅
├─ [x] Schema design
├─ [x] Table creation
├─ [x] Relationships
├─ [x] Indexes
├─ [x] Initialization script
└─ [x] Test data

DOCUMENTATION ✅
├─ [x] README.md
├─ [x] SETUP_GUIDE.md
├─ [x] TODO.md
├─ [x] QUICK_REFERENCE.md
└─ [x] PROJECT_OVERVIEW.md

BUILD STATUS ✅
├─ [x] Compilation successful
├─ [x] All dependencies resolved
├─ [x] JAR file generated
└─ [x] Ready for testing
```

---

## 🚀 Current Status Summary

```
╔════════════════════════════════════════════════════════════════╗
║                   BOOKXCHANGE APPLICATION                     ║
║                    FOUNDATION COMPLETE ✅                      ║
╠════════════════════════════════════════════════════════════════╣
║                                                                 ║
║  Compilation Status:        ✅ SUCCESS                         ║
║  Build Time:                6.5 seconds                        ║
║  Total Files:               35 files                           ║
║  Java Classes:              19 classes                         ║
║  Templates:                 7 templates                        ║
║  Database:                  Ready                              ║
║  Security:                  ✅ Configured                      ║
║  Documentation:             Complete                           ║
║                                                                 ║
║  Ready for:                 PHASE 1 DEVELOPMENT                ║
║  Next Step:                 Book Management (Phase 1)          ║
║                                                                 ║
╚════════════════════════════════════════════════════════════════╝
```

---

**Created on**: March 22, 2026
**Project Version**: 0.0.1-SNAPSHOT
**Status**: Foundation Complete ✅

---

👉 **Next Action**: Read SETUP_GUIDE.md and begin Phase 1 development!
