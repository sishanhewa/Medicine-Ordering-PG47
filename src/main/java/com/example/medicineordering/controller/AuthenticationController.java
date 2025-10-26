package com.example.medicineordering.controller;

import com.example.medicineordering.model.User;
import com.example.medicineordering.model.Customer;
import com.example.medicineordering.model.Driver;
import com.example.medicineordering.repository.UserRepository;
import com.example.medicineordering.repository.CustomerRepository;
import com.example.medicineordering.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthenticationController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(UserRepository userRepository, CustomerRepository customerRepository, DriverRepository driverRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        // Check if already logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            return redirectBasedOnRole(currentUser.getRole());
        }
        
        // Add error message if login failed
        if ("true".equals(error)) {
            model.addAttribute("error", "Invalid username or password.");
        }
        
        return "login";
    }

    /**
     * Process login
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                       @RequestParam("password") String password,
                       HttpSession session,
                       RedirectAttributes ra) {
        
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + username);
        System.out.println("Password length: " + (password != null ? password.length() : "null"));
        
        // Validate input
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            System.out.println("Validation failed: empty username or password");
            ra.addFlashAttribute("error", "Username and password are required.");
            return "redirect:/login";
        }

        try {
            // First try to find user by username
            System.out.println("Looking up user: " + username);
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isPresent()) {
                // User found - check password
                User user = userOpt.get();
                System.out.println("User found: " + user.getUsername() + ", Role: " + user.getRole());
                
                boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
                System.out.println("Password matches: " + passwordMatches);
                
                if (!passwordMatches) {
                    System.out.println("Password verification failed");
                    ra.addFlashAttribute("error", "Invalid username or password.");
                    return "redirect:/login";
                }
                
                // User login successful
                handleUserLogin(user, session, ra);
                return redirectBasedOnRole(user.getRole());
            } else {
                // Try to find driver by email
                System.out.println("User not found, checking drivers: " + username);
                Optional<Driver> driverOpt = driverRepository.findByEmail(username);
                
                if (driverOpt.isPresent()) {
                    Driver driver = driverOpt.get();
                    System.out.println("Driver found: " + driver.getEmail());
                    System.out.println("Driver password hash: " + (driver.getPasswordHash() != null ? driver.getPasswordHash() : "NULL"));
                    System.out.println("Driver password hash length: " + (driver.getPasswordHash() != null ? driver.getPasswordHash().length() : "NULL"));
                    
                    boolean passwordMatches = passwordEncoder.matches(password, driver.getPasswordHash());
                    System.out.println("Driver password matches: " + passwordMatches);
                    
                    if (!passwordMatches) {
                        System.out.println("Driver password verification failed");
                        ra.addFlashAttribute("error", "Invalid username or password.");
                        return "redirect:/login";
                    }
                    
                    // Driver login successful
                    handleDriverLogin(driver, session, ra);
                    return "redirect:/driver/dashboard";
                } else {
                    System.out.println("Neither user nor driver found: " + username);
                    ra.addFlashAttribute("error", "Invalid username or password.");
                    return "redirect:/login";
                }
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Login failed. Please try again.");
            return "redirect:/login";
        }
    }

    /**
     * Handle login success redirect
     */
    @GetMapping("/login-success")
    public String loginSuccess(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return redirectBasedOnRole(user.getRole());
        }
        return "redirect:/customer/dashboard";
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/login";
    }

    /**
     * Handle user login
     */
    private void handleUserLogin(User user, HttpSession session, RedirectAttributes ra) {
        System.out.println("Login successful for user: " + user.getUsername());
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("fullName", user.getFullName());

        // Set customer-specific session attributes for backward compatibility
        if (user.isCustomer()) {
            session.setAttribute("customerId", user.getId());
        }

        ra.addFlashAttribute("success", "Welcome back, " + user.getFullName() + "!");
    }

    /**
     * Handle driver login
     */
    private void handleDriverLogin(Driver driver, HttpSession session, RedirectAttributes ra) {
        System.out.println("Login successful for driver: " + driver.getEmail());
        session.setAttribute("driver", driver);
        session.setAttribute("driverId", driver.getId());
        session.setAttribute("userRole", "DeliveryPersonnel");
        session.setAttribute("username", driver.getEmail());
        session.setAttribute("fullName", driver.getName());

        ra.addFlashAttribute("success", "Welcome back, " + driver.getName() + "!");
    }

    /**
     * Redirect based on user role
     */
    private String redirectBasedOnRole(String role) {
        switch (role) {
            case "Customer":
                return "redirect:/customer/dashboard";
            case "Admin":
            case "DeliveryManager":
                return "redirect:/manager/dashboard";
            case "DeliveryPersonnel":
                return "redirect:/driver/dashboard";
            case "Pharmacist":
                return "redirect:/pharmacist/dashboard";
            case "CustomerSupport":
                return "redirect:/support/dashboard";
            case "FinanceManager":
                return "redirect:/finance/dashboard";
            case "ADMIN":
                return "redirect:/admin/dashboard";
            default:
                return "redirect:/customer/dashboard";
        }
    }

    /**
     * Show user profile (for any role)
     */
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user_profile";
    }

    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        return "access_denied";
    }

    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    /**
     * Process customer registration
     */
    @PostMapping("/register/customer")
    public String registerCustomer(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  @RequestParam("fullName") String fullName,
                                  @RequestParam("email") String email,
                                  @RequestParam("phone") String phone,
                                  RedirectAttributes ra) {
        
        try {
            // Validate input
            if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || 
                !StringUtils.hasText(fullName) || !StringUtils.hasText(email)) {
                ra.addFlashAttribute("error", "Please fill in all required fields.");
                return "redirect:/register";
            }

            if (!password.equals(confirmPassword)) {
                ra.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/register";
            }

            if (password.length() < 6) {
                ra.addFlashAttribute("error", "Password must be at least 6 characters long.");
                return "redirect:/register";
            }

            // Check if username or email already exists
            if (userRepository.existsByUsername(username)) {
                ra.addFlashAttribute("error", "Username already exists. Please choose a different one.");
                return "redirect:/register";
            }

            if (userRepository.existsByEmail(email)) {
                ra.addFlashAttribute("error", "Email already exists. Please use a different email.");
                return "redirect:/register";
            }

            // Create new customer user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setRole("Customer");
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setActive(true);

            // Save user
            userRepository.save(newUser);
            
            // Create corresponding Customer record for cart functionality
            System.out.println("=== CREATING CUSTOMER RECORD ===");
            System.out.println("User ID: " + newUser.getId());
            System.out.println("User Email: " + newUser.getEmail());
            System.out.println("User Full Name: " + newUser.getFullName());
            
            try {
                Customer customer = new Customer();
                customer.setId(newUser.getId()); // Use the same ID as User
                customer.setName(newUser.getFullName());
                customer.setEmail(newUser.getEmail());
                customer.setPhone(newUser.getPhone() != null ? newUser.getPhone() : "+94 77 123 4567");
                customer.setAddress("123 Health Street, Colombo 05, Sri Lanka");
                customer.setPassword(""); // Empty password for Customer record
                
                customerRepository.save(customer);
                System.out.println("✅ Customer record created successfully for User ID: " + newUser.getId());
            } catch (Exception e) {
                System.err.println("❌ Failed to create Customer record: " + e.getMessage());
                e.printStackTrace();
                ra.addFlashAttribute("error", "Account created but cart functionality may not work properly. Please contact support.");
                return "redirect:/login";
            }

            ra.addFlashAttribute("success", "Account created successfully! You can now login.");
            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("Customer registration error: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Registration failed. Please try again.");
            return "redirect:/register";
        }
    }


}
