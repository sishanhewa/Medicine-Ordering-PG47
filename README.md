# Medicine Ordering System - Design Patterns

## 🎯 What are Design Patterns?

Design patterns are like **blueprints** for solving common programming problems. They help us write better, cleaner, and more maintainable code.

## 🔄 Strategy Pattern

### What is it?
The Strategy Pattern lets us choose different ways to do the same thing. It's like having different tools for the same job.

### Where is it used in our project?

#### 1. **User Login (Authentication)**
- **Problem**: Different types of users (Admin, Customer, Manager) need different ways to log in
- **Solution**: Each user type has its own login strategy
- **Files**:
  - `AuthenticationStrategy.java` - The main interface
  - `AdminAuthenticationStrategy.java` - How admins log in
  - `CustomerAuthenticationStrategy.java` - How customers log in
  - `ManagerAuthenticationStrategy.java` - How managers log in

#### 2. **Order Status Updates**
- **Problem**: Different order statuses need different rules for what can happen next
- **Solution**: Each order status has its own processing strategy
- **Files**:
  - `OrderStatusStrategy.java` - The main interface
  - `PendingOrderStrategy.java` - Rules for pending orders
  - `AssignedOrderStrategy.java` - Rules for assigned orders

### Why use Strategy Pattern?
- ✅ **Easy to add new user types** without changing existing code
- ✅ **Easy to add new order statuses** without breaking anything
- ✅ **Clean code** - no messy if-else statements
- ✅ **Easy to test** each strategy separately

---

## 🏗️ Singleton Pattern

### What is it?
The Singleton Pattern ensures only **one copy** of something exists in the entire application. It's like having only one manager for the whole company.

### Where is it used in our project?

#### 1. **Application Configuration**
- **Problem**: We need one place to store all app settings (database URL, server port, etc.)
- **Solution**: `ApplicationConfig.java` - One instance that holds all configuration
- **Why**: Prevents multiple copies of settings and ensures consistency

#### 2. **Logging Service**
- **Problem**: We need one place to log all messages from the entire application
- **Solution**: `LoggingService.java` - One instance that handles all logging
- **Why**: Prevents duplicate log files and ensures all logs go to the same place

### Why use Singleton Pattern?
- ✅ **Saves memory** - only one copy exists
- ✅ **Consistent data** - everyone uses the same settings
- ✅ **Easy to manage** - one place to change things
- ✅ **Thread-safe** - multiple users can't break it

---

## 🎨 How to See the Patterns in Action

### Demo Pages
Visit these URLs to see the patterns working:

1. **Authentication Strategy Demo**: `http://localhost:8080/pattern-demo/authentication`
2. **Order Status Strategy Demo**: `http://localhost:8080/pattern-demo/order-status`
3. **Configuration Singleton Demo**: `http://localhost:8080/pattern-demo/configuration`
4. **Logging Singleton Demo**: `http://localhost:8080/pattern-demo/logging`
5. **Pattern Integration Demo**: `http://localhost:8080/pattern-demo/integration`

### What You Can Do
- **Test different login strategies** for different user types
- **Test order status changes** and see what's allowed
- **View application configuration** in one place
- **See logging in action** across the application
- **Try interactive forms** to test the patterns

---

## 🚀 Real-World Examples

### Strategy Pattern Examples
- **Payment Methods**: Credit card, PayPal, bank transfer - all do the same thing (pay) but differently
- **Sorting Algorithms**: Quick sort, bubble sort, merge sort - all sort data but use different methods
- **File Compression**: ZIP, RAR, 7Z - all compress files but use different algorithms

### Singleton Pattern Examples
- **Database Connection**: Only one connection pool for the entire app
- **Logger**: One logging system for the entire application
- **Configuration Manager**: One place to store all app settings
- **Cache Manager**: One cache for the entire application

---

## 📁 File Structure

```
src/main/java/com/example/medicineordering/
├── strategy/                    # Strategy Pattern Files
│   ├── AuthenticationStrategy.java
│   ├── AuthenticationContext.java
│   ├── OrderStatusStrategy.java
│   ├── OrderStatusContext.java
│   └── impl/                    # Strategy Implementations
│       ├── AdminAuthenticationStrategy.java
│       ├── CustomerAuthenticationStrategy.java
│       ├── ManagerAuthenticationStrategy.java
│       ├── PendingOrderStrategy.java
│       └── AssignedOrderStrategy.java
├── singleton/                   # Singleton Pattern Files
│   ├── ApplicationConfig.java
│   └── LoggingService.java
└── controller/
    └── PatternDemoController.java  # Demo Controller
```

---

## 🎯 Benefits Summary

### Strategy Pattern Benefits
- **Flexibility**: Easy to add new ways of doing things
- **Maintainability**: Each strategy is separate and easy to fix
- **Testability**: Can test each strategy independently
- **Extensibility**: Add new strategies without changing existing code

### Singleton Pattern Benefits
- **Resource Management**: Only one copy uses less memory
- **Global Access**: Easy to access from anywhere in the app
- **Consistency**: Everyone uses the same data
- **Performance**: Faster access to shared resources

---

## 🔧 How to Run the Demo

1. **Start the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Open your browser** and go to:
   ```
   http://localhost:8080/pattern-demo/authentication
   ```

3. **Try the interactive demos** to see the patterns in action!

---

## 📚 Learning More

- **Strategy Pattern**: Think of it as "different tools for the same job"
- **Singleton Pattern**: Think of it as "only one manager for the whole company"
- **Both patterns** make your code more professional and easier to maintain
- **Real projects** use these patterns to handle complex business logic

---

*This project demonstrates professional software design patterns while maintaining all existing functionality. The patterns are implemented without changing any existing code, making them perfect examples of clean, maintainable software architecture.*



