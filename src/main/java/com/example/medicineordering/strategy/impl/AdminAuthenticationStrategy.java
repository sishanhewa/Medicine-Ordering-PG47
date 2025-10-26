package com.example.medicineordering.strategy.impl;

import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.UserRepository;
import com.example.medicineordering.strategy.AuthenticationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Concrete strategy for Admin user authentication
 * This demonstrates the Strategy pattern for admin-specific authentication logic
 */
@Component
public class AdminAuthenticationStrategy implements AuthenticationStrategy {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AdminAuthenticationStrategy(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User authenticate(String username, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if ("ADMIN".equals(user.getRole()) && user.isActive()) {
                    if (passwordEncoder.matches(password, user.getPasswordHash())) {
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            // Log authentication failure
            System.err.println("Admin authentication failed for user: " + username);
        }
        return null;
    }
    
    @Override
    public String getStrategyName() {
        return "Admin Authentication Strategy";
    }
    
    @Override
    public boolean supportsRole(String role) {
        return "ADMIN".equals(role);
    }
}
