package com.example.medicineordering.controller;

import com.example.medicineordering.model.User;
import com.example.medicineordering.model.Customer;
import com.example.medicineordering.repository.UserRepository;
import com.example.medicineordering.repository.CustomerRepository;
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
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        // Check if already logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            return redirectBasedOnRole(currentUser.getRole());
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
        
        // Validate input
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            ra.addFlashAttribute("error", "Username and password are required.");
            return "redirect:/login";
        }

        try {
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                ra.addFlashAttribute("error", "Invalid username or password.");
                return "redirect:/login";
            }

            User user = userOpt.get();
            
            // Use BCrypt to check password
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                ra.addFlashAttribute("error", "Invalid username or password.");
                return "redirect:/login";
            }

            // Login successful
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());

            // Set customer-specific session attributes for backward compatibility
            if (user.isCustomer()) {
                session.setAttribute("customerId", user.getId());
                // SECURITY FIX: Don't store customerName in session - use user.getFullName() directly
                // session.setAttribute("customerName", user.getFullName()); // REMOVED - causes cross-contamination
            }

            ra.addFlashAttribute("success", "Welcome back, " + user.getFullName() + "!");
            
            return redirectBasedOnRole(user.getRole());

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Login failed. Please try again.");
            return "redirect:/login";
        }
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
     * Redirect based on user role
     */
    private String redirectBasedOnRole(String role) {
        switch (role) {
            case "Customer":
                return "redirect:/customer/dashboard";
            case "Admin":
                return "redirect:/admin/dashboard";
            case "DeliveryManager":
                return "redirect:/delivery-manager/dashboard";
            case "DeliveryPersonnel":
                return "redirect:/delivery-personnel/dashboard";
            case "Pharmacist":
                return "redirect:/pharmacist/dashboard";
            case "CustomerSupport":
                return "redirect:/customer-support/dashboard";
            case "FinanceManager":
                return "redirect:/finance-manager/dashboard";
            default:
                return "redirect:/login";
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
