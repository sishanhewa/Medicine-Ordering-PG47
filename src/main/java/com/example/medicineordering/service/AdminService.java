package com.example.medicineordering.service;

import com.example.medicineordering.model.AdminUser;
import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.AdminUserRepository;
import com.example.medicineordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<AdminUser> getAllUsers() {
        return adminUserRepository.findAll();
    }

    public List<AdminUser> getUsersByRole(String role) {
        return adminUserRepository.findByRole(role);
    }

    public List<AdminUser> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        return adminUserRepository.searchUsers(searchTerm.trim());
    }

    public List<AdminUser> searchUsersByRole(String role, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getUsersByRole(role);
        }
        return adminUserRepository.findByRoleAndSearch(role, searchTerm.trim());
    }

    public Optional<AdminUser> getUserById(Long id) {
        return adminUserRepository.findById(id);
    }

    public Optional<AdminUser> getUserByUsername(String username) {
        return adminUserRepository.findByUsername(username);
    }

    public Optional<AdminUser> getUserByEmail(String email) {
        return adminUserRepository.findByEmail(email);
    }

    public AdminUser createUser(AdminUser user) {
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }

        // Check for duplicate username
        if (adminUserRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check for duplicate email
        if (adminUserRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Set default active status
        if (user.getActive() == null) {
            user.setActive(true);
        }

        return adminUserRepository.save(user);
    }

    public AdminUser updateUser(AdminUser user) {
        // Validate required fields
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID is required for update");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }

        // Get existing user
        Optional<AdminUser> existingUserOpt = adminUserRepository.findById(user.getId());
        if (!existingUserOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        AdminUser existingUser = existingUserOpt.get();

        // Check for duplicate username (excluding current user)
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            adminUserRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check for duplicate email (excluding current user)
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            adminUserRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Handle password update
        if (user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty()) {
            // New password provided, hash it
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        } else {
            // No new password, keep existing password
            user.setPasswordHash(existingUser.getPasswordHash());
        }

        // Preserve creation timestamp
        user.setCreatedAt(existingUser.getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());

        return adminUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        Optional<AdminUser> user = adminUserRepository.findById(id);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        adminUserRepository.deleteById(id);
    }

    public long getUserCountByRole(String role) {
        return adminUserRepository.countByRole(role);
    }

    public long getTotalUserCount() {
        return adminUserRepository.countAll();
    }

    // Real user counts from existing Users table
    public long getRealUserCountByRole(String role) {
        try {
            return userRepository.countByRole(role);
        } catch (Exception e) {
            System.err.println("Error getting real user count for role " + role + ": " + e.getMessage());
            return 0;
        }
    }

    public long getRealTotalUserCount() {
        try {
            return userRepository.countAll();
        } catch (Exception e) {
            System.err.println("Error getting real total user count: " + e.getMessage());
            return 0;
        }
    }

    // Get real users from existing Users table
    public List<User> getRealUsersByRole(String role) {
        try {
            return userRepository.findByRole(role);
        } catch (Exception e) {
            System.err.println("Error getting real users for role " + role + ": " + e.getMessage());
            return List.of();
        }
    }

    public List<User> getAllRealUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all real users: " + e.getMessage());
            return List.of();
        }
    }

    public List<String> getAvailableRoles() {
        return List.of(
            "CUSTOMER",
            "DELIVERY_MANAGER", 
            "PHARMACIST",
            "FINANCE_MANAGER",
            "CustomerSupport"
        );
    }

    public boolean isUsernameAvailable(String username) {
        return !adminUserRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !adminUserRepository.existsByEmail(email);
    }
}
