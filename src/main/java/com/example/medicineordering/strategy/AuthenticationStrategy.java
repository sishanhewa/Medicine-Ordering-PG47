package com.example.medicineordering.strategy;

import com.example.medicineordering.model.User;

/**
 * Strategy interface for different authentication strategies
 * This demonstrates the Strategy pattern for handling different user authentication methods
 */
public interface AuthenticationStrategy {
    
    /**
     * Authenticates a user using the specific strategy
     * @param username the username to authenticate
     * @param password the password to verify
     * @return User object if authentication successful, null otherwise
     */
    User authenticate(String username, String password);
    
    /**
     * Gets the strategy name for logging purposes
     * @return the name of the authentication strategy
     */
    String getStrategyName();
    
    /**
     * Checks if this strategy supports the given user role
     * @param role the user role to check
     * @return true if this strategy supports the role
     */
    boolean supportsRole(String role);
}



