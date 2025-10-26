package com.example.medicineordering.strategy.impl;

import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.UserRepository;
import com.example.medicineordering.strategy.AuthenticationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Concrete strategy for Customer user authentication
 * This demonstrates the Strategy pattern for customer-specific authentication logic
 */
@Component
public class CustomerAuthenticationStrategy implements AuthenticationStrategy {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public CustomerAuthenticationStrategy(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User authenticate(String username, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if ("Customer".equals(user.getRole()) && user.isActive()) {
                    if (passwordEncoder.matches(password, user.getPasswordHash())) {
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            // Log authentication failure
            System.err.println("Customer authentication failed for user: " + username);
        }
        return null;
    }
    
    @Override
    public String getStrategyName() {
        return "Customer Authentication Strategy";
    }
    
    @Override
    public boolean supportsRole(String role) {
        return "Customer".equals(role);
    }
}
