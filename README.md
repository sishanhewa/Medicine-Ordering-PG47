# 🏥 Medicine Ordering System

A comprehensive web-based medicine ordering and management system built with Spring Boot, featuring multiple user roles, prescription management, and real-time order tracking.

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [User Roles](#-user-roles)
- [Installation & Setup](#-installation--setup)
- [Database Configuration](#-database-configuration)
- [API Endpoints](#-api-endpoints)
- [Design Patterns](#-design-patterns)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🔐 Multi-Role Authentication System
- **Admin**: Complete system management and user oversight
- **Manager**: Order management, driver assignment, and inventory control
- **Pharmacist**: Medicine management, prescription verification, and inventory updates
- **Customer**: Medicine browsing, cart management, and order placement
- **Customer Support**: Inquiry management and customer assistance
- **Finance**: Payment processing and financial reporting
- **Delivery Driver**: Order delivery and status updates

### 🛒 E-commerce Functionality
- **Medicine Catalog**: Browse medicines by category with detailed information
- **Shopping Cart**: Add/remove items, quantity management
- **Prescription Upload**: Secure prescription file upload and verification
- **Order Management**: Complete order lifecycle from placement to delivery
- **Payment Processing**: Integrated payment system with status tracking

### 📱 Real-time Features
- **Order Tracking**: Real-time order status updates
- **Notifications**: System-wide notification system
- **Live Chat**: Customer support chat functionality
- **Inventory Management**: Real-time stock level monitoring

### 🔒 Security Features
- **Role-based Access Control**: Secure access based on user roles
- **Password Encryption**: Secure password storage and validation
- **Session Management**: Secure user session handling
- **File Upload Security**: Safe prescription file handling

## 🛠 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security 6**
- **Spring Data JDBC**
- **Maven** (Build Tool)

### Frontend
- **Thymeleaf** (Template Engine)
- **Bootstrap 5** (CSS Framework)
- **JavaScript** (Client-side functionality)
- **HTML5/CSS3**

### Database
- **Microsoft SQL Server**
- **JDBC Template** (Data Access)

### Additional Libraries
- **Lombok** (Code Generation)
- **HikariCP** (Connection Pooling)
- **Thymeleaf Extras Spring Security 6**

## 🏗 Architecture

The system follows a **layered architecture** pattern:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│     (Thymeleaf Templates + JS)     │
├─────────────────────────────────────┤
│            Controller Layer         │
│        (Spring MVC Controllers)     │
├─────────────────────────────────────┤
│             Service Layer           │
│        (Business Logic Services)    │
├─────────────────────────────────────┤
│           Repository Layer          │
│        (Data Access Objects)        │
├─────────────────────────────────────┤
│            Database Layer           │
│         (Microsoft SQL Server)      │
└─────────────────────────────────────┘
```

## 👥 User Roles

### 🔧 Admin
- **User Management**: Create, update, delete user accounts
- **System Configuration**: Manage system settings and parameters
- **Global Oversight**: Monitor all system activities
- **Database Management**: Access to all data and reports

### 👨‍💼 Manager
- **Order Management**: View, assign, and track all orders
- **Driver Management**: Assign delivery drivers to orders
- **Inventory Oversight**: Monitor medicine stock levels
- **Reporting**: Generate operational reports

### 💊 Pharmacist
- **Medicine Management**: Add, update, and manage medicine inventory
- **Prescription Verification**: Review and approve prescription orders
- **Stock Management**: Update medicine quantities and availability
- **Order Processing**: Prepare orders for delivery

### 🛒 Customer
- **Medicine Browsing**: Search and filter available medicines
- **Cart Management**: Add/remove items, manage quantities
- **Order Placement**: Place orders with prescription upload
- **Order Tracking**: Monitor order status and delivery progress
- **Profile Management**: Update personal information and preferences

### 🎧 Customer Support
- **Inquiry Management**: Handle customer questions and complaints
- **Live Chat**: Real-time customer assistance
- **Issue Resolution**: Track and resolve customer issues
- **Communication**: Send updates and notifications to customers

### 💰 Finance
- **Payment Processing**: Manage payment approvals and rejections
- **Financial Reporting**: Generate revenue and payment reports
- **Transaction Management**: Track all financial transactions
- **Budget Monitoring**: Monitor system financial performance

### 🚚 Delivery Driver
- **Order Delivery**: View assigned orders and delivery routes
- **Status Updates**: Update delivery status and location
- **Customer Communication**: Contact customers for delivery coordination
- **Route Management**: Optimize delivery routes

## 🚀 Installation & Setup

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **Microsoft SQL Server** (2019 or later)
- **Git**

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/Medicine-Ordering.git
cd Medicine-Ordering
```

### 2. Database Setup
1. **Install SQL Server** and create a database named `MedicineDB`
2. **Configure SQL Server** to accept TCP/IP connections on port 1433
3. **Update database credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MedicineDB;encrypt=true;trustServerCertificate=true
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 3. Build and Run
```bash
# Clean and compile
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

### 4. Access the Application
- **Main Application**: http://localhost:8080
- **Login Page**: http://localhost:8080/login

## 🗄 Database Configuration

The application uses **Microsoft SQL Server** with the following key tables:

### Core Tables
- **Users**: User authentication and role management
- **Customers**: Customer profile information
- **Medicines**: Medicine catalog and inventory
- **Orders**: Order management and tracking
- **OrderItems**: Individual order line items
- **Carts**: Shopping cart functionality
- **Prescriptions**: Prescription file management
- **Payments**: Payment processing and tracking

### Support Tables
- **ContactInquiries**: Customer support inquiries
- **Messages**: Live chat messages
- **Conversations**: Chat conversation threads
- **Notifications**: System notifications

### Schema Files
- `schema.sql`: Main database schema
- `data.sql`: Initial data and test users
- `schema-preserve.sql`: Schema updates that preserve existing data

## 🔌 API Endpoints

### Authentication
- `POST /login` - User login
- `POST /register` - User registration
- `GET /logout` - User logout

### Customer Endpoints
- `GET /customer/home` - Medicine catalog
- `GET /customer/cart` - Shopping cart
- `POST /customer/cart/add` - Add item to cart
- `POST /customer/order/place` - Place order
- `GET /customer/orders` - View orders

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - User management
- `POST /admin/users/create` - Create user

### Manager Endpoints
- `GET /manager/dashboard` - Manager dashboard
- `GET /manager/orders` - Order management
- `POST /manager/orders/assign` - Assign driver

## 🎨 Design Patterns

This project implements several **design patterns** for maintainable and scalable code:

### Strategy Pattern
- **Authentication Strategies**: Different login methods for different user types
- **Order Status Strategies**: Different processing rules for different order states

### Singleton Pattern
- **Application Configuration**: Single instance for app settings
- **Logging Service**: Centralized logging management

### Repository Pattern
- **Data Access Layer**: Clean separation between business logic and data access
- **Database Abstraction**: Easy to switch between different database implementations

## 📁 Project Structure

```
Medicine-Ordering/
├── src/main/java/com/example/medicineordering/
│   ├── controller/          # MVC Controllers
│   │   ├── AdminController.java
│   │   ├── CustomerController.java
│   │   ├── ManagerController.java
│   │   ├── PharmacistController.java
│   │   ├── CustomerSupportController.java
│   │   ├── FinanceController.java
│   │   └── DeliveryDriverController.java
│   ├── model/              # Data Models
│   │   ├── User.java
│   │   ├── Customer.java
│   │   ├── Medicine.java
│   │   ├── Order.java
│   │   └── Cart.java
│   ├── repository/         # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── MedicineRepository.java
│   │   ├── OrderRepository.java
│   │   └── CartRepository.java
│   ├── service/           # Business Logic
│   │   ├── AuthenticationService.java
│   │   ├── NotificationService.java
│   │   └── CustomerSupportService.java
│   ├── strategy/          # Design Patterns
│   │   ├── AuthenticationStrategy.java
│   │   └── OrderStatusStrategy.java
│   └── singleton/         # Singleton Patterns
│       ├── ApplicationConfig.java
│       └── LoggingService.java
├── src/main/resources/
│   ├── templates/         # Thymeleaf Templates
│   │   ├── admin/
│   │   ├── customer/
│   │   ├── manager/
│   │   └── pharmacist/
│   ├── static/           # Static Assets
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   ├── application.properties
│   ├── schema.sql
│   └── data.sql
├── uploads/              # File Uploads
│   └── prescriptions/
├── pom.xml
└── README.md
```

## 📸 Screenshots

### Login Page
![Login Page](docs/screenshots/login.png)

### Customer Dashboard
![Customer Dashboard](docs/screenshots/customer-dashboard.png)

### Medicine Catalog
![Medicine Catalog](docs/screenshots/medicine-catalog.png)

### Order Management
![Order Management](docs/screenshots/order-management.png)

### Admin Panel
![Admin Panel](docs/screenshots/admin-panel.png)

## 🚀 Getting Started

### Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Manager | manager | manager123 |
| Pharmacist | pharmacist | pharmacist123 |
| Customer | customer@example.com | customer123 |
| Finance | finance | finance123 |
| Support | support | support123 |

### Quick Start Guide

1. **Start the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access the login page**: http://localhost:8080/login

3. **Login with any of the default credentials** above

4. **Explore the system** based on your role

## 🧪 Testing

### Unit Tests
```bash
./mvnw test
```

### Integration Tests
```bash
./mvnw verify
```

### Manual Testing
- Use the provided test credentials to test different user roles
- Upload test prescription files
- Create test orders and track their status

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MedicineDB
spring.datasource.username=your_username
spring.datasource.password=your_password

# Server Configuration
server.port=8080

# File Upload Configuration
spring.servlet.multipart.max-file-size=5MB
app.upload.dir=uploads

# Logging Configuration
logging.level.org.springframework.jdbc.core=DEBUG
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add some amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Contribution Guidelines
- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

## 📞 Support

If you encounter any issues or have questions:

1. **Check the Issues** section on GitHub
2. **Create a new issue** with detailed description
3. **Contact the maintainer** via email

---

**⭐ Star this repository if you found it helpful!**

*Built with ❤️ using Spring Boot and modern web technologies*