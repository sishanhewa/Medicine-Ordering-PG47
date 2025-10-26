package com.example.medicineordering.repository;

import com.example.medicineordering.model.Notification;
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
public class NotificationRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public NotificationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Notification> findAll() {
        String sql = "SELECT * FROM dbo.Notifications ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Notification.class));
    }

    public Optional<Notification> findById(Long id) {
        String sql = "SELECT * FROM dbo.Notifications WHERE id = ?";
        List<Notification> notifications = jdbc.query(sql, new BeanPropertyRowMapper<>(Notification.class), id);
        return notifications.stream().findFirst();
    }

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            // Insert new notification
            String sql = "INSERT INTO dbo.Notifications (type, content, recipient, timestamp) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, notification.getType());
                ps.setString(2, notification.getContent());
                ps.setString(3, notification.getRecipient());
                ps.setObject(4, notification.getTimestamp() != null ? notification.getTimestamp() : LocalDateTime.now());
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                notification.setId(keyHolder.getKey().longValue());
            }
        } else {
            // Update existing notification
            String sql = "UPDATE dbo.Notifications SET type = ?, content = ?, recipient = ?, timestamp = ? WHERE id = ?";
            jdbc.update(sql, notification.getType(), notification.getContent(), 
                       notification.getRecipient(), notification.getTimestamp(), notification.getId());
        }
        return notification;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM dbo.Notifications WHERE id = ?";
        jdbc.update(sql, id);
    }

    public List<Notification> findByRecipient(String recipient) {
        String sql = "SELECT * FROM dbo.Notifications WHERE recipient = ? ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Notification.class), recipient);
    }
}




