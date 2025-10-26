package com.example.medicineordering.controller;

import com.example.medicineordering.model.Order;
import com.example.medicineordering.model.User;
import com.example.medicineordering.singleton.ApplicationConfig;
import com.example.medicineordering.singleton.LoggingService;
import com.example.medicineordering.strategy.AuthenticationContext;
import com.example.medicineordering.strategy.OrderStatusContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Demonstration controller for Strategy and Singleton patterns
 * This controller shows how the implemented patterns work in practice
 */
@Controller
@RequestMapping("/pattern-demo")
public class PatternDemoController {
    
    private final AuthenticationContext authenticationContext;
    private final OrderStatusContext orderStatusContext;
    private final ApplicationConfig applicationConfig;
    private final LoggingService loggingService;
    
    @Autowired
    public PatternDemoController(AuthenticationContext authenticationContext,
                                OrderStatusContext orderStatusContext,
                                ApplicationConfig applicationConfig,
                                LoggingService loggingService) {
        this.authenticationContext = authenticationContext;
        this.orderStatusContext = orderStatusContext;
        this.applicationConfig = applicationConfig;
        this.loggingService = loggingService;
    }
    
    /**
     * Demonstrates the Strategy pattern for authentication
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/authentication")
    public String authenticationDemo(Model model) {
        loggingService.info("Authentication Strategy Pattern Demo", "PatternDemoController");
        
        // Demonstrate different authentication strategies
        model.addAttribute("strategies", authenticationContext.getAllStrategies());
        model.addAttribute("demoTitle", "Strategy Pattern - Authentication Strategies");
        model.addAttribute("patternDescription", 
            "This demonstrates the Strategy pattern for different user authentication methods. " +
            "Each user role (Admin, Customer, Manager) has its own authentication strategy.");
        
        return "pattern_demo";
    }
    
    /**
     * Demonstrates the Strategy pattern for order status processing
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/order-status")
    public String orderStatusDemo(Model model) {
        loggingService.info("Order Status Strategy Pattern Demo", "PatternDemoController");
        
        // Demonstrate different order status strategies
        model.addAttribute("strategies", orderStatusContext.getAllStrategies());
        model.addAttribute("demoTitle", "Strategy Pattern - Order Status Processing");
        model.addAttribute("patternDescription", 
            "This demonstrates the Strategy pattern for different order status processing methods. " +
            "Each order status (Pending, Assigned, etc.) has its own processing strategy.");
        
        return "pattern_demo";
    }
    
    /**
     * Demonstrates the Singleton pattern for application configuration
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/configuration")
    public String configurationDemo(Model model) {
        loggingService.info("Singleton Pattern - Application Configuration Demo", "PatternDemoController");
        
        // Demonstrate singleton configuration
        model.addAttribute("config", applicationConfig);
        model.addAttribute("configSummary", applicationConfig.getConfigurationSummary());
        model.addAttribute("demoTitle", "Singleton Pattern - Application Configuration");
        model.addAttribute("patternDescription", 
            "This demonstrates the Singleton pattern for application-wide configuration management. " +
            "Only one instance exists throughout the application lifecycle.");
        
        return "pattern_demo";
    }
    
    /**
     * Demonstrates the Singleton pattern for logging service
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/logging")
    public String loggingDemo(Model model) {
        loggingService.info("Singleton Pattern - Logging Service Demo", "PatternDemoController");
        
        // Demonstrate singleton logging service
        model.addAttribute("loggingService", loggingService);
        model.addAttribute("recentLogs", loggingService.getRecentLogEntries(10));
        model.addAttribute("logCount", loggingService.getLogCount());
        model.addAttribute("demoTitle", "Singleton Pattern - Logging Service");
        model.addAttribute("patternDescription", 
            "This demonstrates the Singleton pattern for centralized logging management. " +
            "All logging operations go through a single instance.");
        
        return "pattern_demo";
    }
    
    /**
     * Demonstrates pattern integration
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/integration")
    public String integrationDemo(Model model) {
        loggingService.info("Pattern Integration Demo", "PatternDemoController");
        
        // Demonstrate how patterns work together
        model.addAttribute("authStrategies", authenticationContext.getAllStrategies());
        model.addAttribute("orderStrategies", orderStatusContext.getAllStrategies());
        model.addAttribute("config", applicationConfig);
        model.addAttribute("recentLogs", loggingService.getRecentLogEntries(5));
        model.addAttribute("demoTitle", "Pattern Integration Demo");
        model.addAttribute("patternDescription", 
            "This demonstrates how Strategy and Singleton patterns work together " +
            "in a real application scenario.");
        
        return "pattern_demo";
    }
    
    /**
     * Test authentication strategy
     * @param username the username to test
     * @param password the password to test
     * @param role the role to test
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/test-auth")
    public String testAuthentication(@RequestParam String username, 
                                   @RequestParam String password, 
                                   @RequestParam String role, 
                                   Model model) {
        loggingService.info("Testing authentication for user: " + username + " with role: " + role, "PatternDemoController");
        
        User user = authenticationContext.authenticate(username, password, role);
        model.addAttribute("testResult", user != null ? "Authentication successful" : "Authentication failed");
        model.addAttribute("user", user);
        model.addAttribute("testedRole", role);
        
        return "pattern_demo";
    }
    
    /**
     * Test order status strategy
     * @param currentStatus the current order status
     * @param newStatus the new order status
     * @param model the model to add attributes
     * @return the demo page
     */
    @GetMapping("/test-order-status")
    public String testOrderStatus(@RequestParam String currentStatus, 
                                @RequestParam String newStatus, 
                                Model model) {
        loggingService.info("Testing order status update from " + currentStatus + " to " + newStatus, "PatternDemoController");
        
        // Create a mock order for testing
        Order mockOrder = new Order();
        mockOrder.setId(999);
        mockOrder.setStatus(currentStatus);
        
        boolean success = orderStatusContext.processOrderStatusUpdate(mockOrder, newStatus);
        model.addAttribute("testResult", success ? "Status update successful" : "Status update failed");
        model.addAttribute("order", mockOrder);
        model.addAttribute("nextPossibleStatuses", orderStatusContext.getNextPossibleStatuses(currentStatus));
        
        return "pattern_demo";
    }
}



