package com.example.medicineordering.repository;

import com.example.medicineordering.model.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminUserRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<AdminUser> findAll() {
        String sql = "SELECT * FROM dbo.AdminUsers ORDER BY createdAt DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(AdminUser.class));
    }

    public List<AdminUser> findByRole(String role) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE role = ? ORDER BY createdAt DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(AdminUser.class), role);
    }

    public List<AdminUser> searchUsers(String searchTerm) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE " +
                    "LOWER(fullName) LIKE LOWER(?) OR " +
                    "LOWER(username) LIKE LOWER(?) OR " +
                    "LOWER(email) LIKE LOWER(?) " +
                    "ORDER BY createdAt DESC";
        String searchPattern = "%" + searchTerm + "%";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(AdminUser.class), 
                         searchPattern, searchPattern, searchPattern);
    }

    public List<AdminUser> findByRoleAndSearch(String role, String searchTerm) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE role = ? AND " +
                    "(LOWER(fullName) LIKE LOWER(?) OR " +
                    "LOWER(username) LIKE LOWER(?) OR " +
                    "LOWER(email) LIKE LOWER(?)) " +
                    "ORDER BY createdAt DESC";
        String searchPattern = "%" + searchTerm + "%";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(AdminUser.class), 
                         role, searchPattern, searchPattern, searchPattern);
    }

    public Optional<AdminUser> findById(Long id) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE id = ?";
        try {
            AdminUser user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(AdminUser.class), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<AdminUser> findByUsername(String username) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE username = ?";
        try {
            AdminUser user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(AdminUser.class), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<AdminUser> findByEmail(String email) {
        String sql = "SELECT * FROM dbo.AdminUsers WHERE email = ?";
        try {
            AdminUser user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(AdminUser.class), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public AdminUser save(AdminUser user) {
        if (user.getId() == null) {
            // Insert new user
            String sql = "INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active, createdAt, updatedAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getFullName());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getPhone());
                ps.setString(6, user.getRole());
                ps.setBoolean(7, user.getActive() != null ? user.getActive() : true);
                ps.setObject(8, user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now());
                ps.setObject(9, user.getUpdatedAt() != null ? user.getUpdatedAt() : LocalDateTime.now());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
            }
        } else {
            // Update existing user
            String sql = "UPDATE dbo.AdminUsers SET username = ?, passwordHash = ?, fullName = ?, email = ?, " +
                        "phone = ?, role = ?, active = ?, updatedAt = ? WHERE id = ?";
            jdbc.update(sql, user.getUsername(), user.getPasswordHash(), user.getFullName(), 
                       user.getEmail(), user.getPhone(), user.getRole(), user.getActive(), 
                       LocalDateTime.now(), user.getId());
        }
        return user;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM dbo.AdminUsers WHERE id = ?";
        jdbc.update(sql, id);
    }

    public long countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM dbo.AdminUsers WHERE role = ?";
        return jdbc.queryForObject(sql, Long.class, role);
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM dbo.AdminUsers";
        return jdbc.queryForObject(sql, Long.class);
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM dbo.AdminUsers WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM dbo.AdminUsers WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}




