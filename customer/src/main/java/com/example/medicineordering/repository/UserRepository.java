package com.example.medicineordering.repository;

import com.example.medicineordering.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, passwordHash, role, email, fullName, phone, isActive, createdAt FROM dbo.Users WHERE username = ? AND isActive = 1";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, passwordHash, role, email, fullName, phone, isActive, createdAt FROM dbo.Users WHERE id = ? AND isActive = 1";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, username, passwordHash, role, email, fullName, phone, isActive, createdAt FROM dbo.Users WHERE email = ? AND isActive = 1";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find all users by role
     */
    public List<User> findByRole(String role) {
        String sql = "SELECT id, username, passwordHash, role, email, fullName, phone, isActive, createdAt FROM dbo.Users WHERE role = ? AND isActive = 1 ORDER BY fullName";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(User.class), role);
    }

    /**
     * Find all active users
     */
    public List<User> findAll() {
        String sql = "SELECT id, username, passwordHash, role, email, fullName, phone, isActive, createdAt FROM dbo.Users WHERE isActive = 1 ORDER BY role, fullName";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    /**
     * Save a new user
     */
    public User save(User user) {
        String sql = "INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, user.getUsername(), user.getPasswordHash(), user.getRole(), 
                   user.getEmail(), user.getFullName(), user.getPhone(), user.isActive(), user.getCreatedAt());
        return user;
    }

    /**
     * Update user
     */
    public void update(User user) {
        String sql = "UPDATE dbo.Users SET username = ?, passwordHash = ?, role = ?, email = ?, fullName = ?, phone = ?, isActive = ? WHERE id = ?";
        jdbc.update(sql, user.getUsername(), user.getPasswordHash(), user.getRole(), 
                   user.getEmail(), user.getFullName(), user.getPhone(), user.isActive(), user.getId());
    }

    /**
     * Deactivate user (soft delete)
     */
    public void deactivate(int id) {
        String sql = "UPDATE dbo.Users SET isActive = 0 WHERE id = ?";
        jdbc.update(sql, id);
    }

    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM dbo.Users WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM dbo.Users WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
