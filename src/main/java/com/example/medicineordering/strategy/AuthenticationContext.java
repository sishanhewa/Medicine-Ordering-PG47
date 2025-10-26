package com.example.medicineordering.strategy;

import com.example.medicineordering.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for the Strategy pattern
 * This demonstrates how to use different authentication strategies based on user role
 */
@Component
public class AuthenticationContext {
    
    private final List<AuthenticationStrategy> authenticationStrategies;
    
    @Autowired
    public AuthenticationContext(List<AuthenticationStrategy> authenticationStrategies) {
        this.authenticationStrategies = authenticationStrategies;
    }
    
    /**
     * Authenticates a user using the appropriate strategy based on their role
     * @param username the username to authenticate
     * @param password the password to verify
     * @param role the user role to determine which strategy to use
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password, String role) {
        AuthenticationStrategy strategy = getStrategyForRole(role);
        if (strategy != null) {
            System.out.println("Using " + strategy.getStrategyName() + " for role: " + role);
            return strategy.authenticate(username, password);
        }
        System.err.println("No authentication strategy found for role: " + role);
        return null;
    }
    
    /**
     * Gets the appropriate authentication strategy for the given role
     * @param role the user role
     * @return the authentication strategy for the role, or null if not found
     */
    private AuthenticationStrategy getStrategyForRole(String role) {
        return authenticationStrategies.stream()
                .filter(strategy -> strategy.supportsRole(role))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all available authentication strategies
     * @return list of all authentication strategies
     */
    public List<AuthenticationStrategy> getAllStrategies() {
        return authenticationStrategies;
    }
}



