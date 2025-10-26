package com.example.medicineordering.repository;

import com.example.medicineordering.model.Message;
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
public class MessageRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public MessageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Message> findAll() {
        String sql = "SELECT * FROM dbo.Messages ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class));
    }

    public Optional<Message> findById(Long id) {
        String sql = "SELECT * FROM dbo.Messages WHERE id = ?";
        List<Message> messages = jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), id);
        return messages.stream().findFirst();
    }

    public Message save(Message message) {
        if (message.getId() == null) {
            // Insert new message
            String sql = "INSERT INTO dbo.Messages (content, sender, receiver, timestamp, status, archived) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, message.getContent());
                ps.setString(2, message.getSender());
                ps.setString(3, message.getReceiver());
                ps.setObject(4, message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.now());
                ps.setString(5, message.getStatus() != null ? message.getStatus() : "unread");
                ps.setBoolean(6, message.getArchived() != null ? message.getArchived() : false);
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                message.setId(keyHolder.getKey().longValue());
            }
        } else {
            // Update existing message
            String sql = "UPDATE dbo.Messages SET content = ?, sender = ?, receiver = ?, timestamp = ?, status = ?, archived = ? WHERE id = ?";
            jdbc.update(sql, message.getContent(), message.getSender(), message.getReceiver(), 
                       message.getTimestamp(), message.getStatus(), message.getArchived(), message.getId());
        }
        return message;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM dbo.Messages WHERE id = ?";
        jdbc.update(sql, id);
    }

    public List<Message> findByStatus(String status) {
        String sql = "SELECT * FROM dbo.Messages WHERE status = ? ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), status);
    }

    public List<Message> findByArchived(boolean archived) {
        String sql = "SELECT * FROM dbo.Messages WHERE archived = ? ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), archived);
    }

    public List<Message> findByReceiver(String receiver) {
        String sql = "SELECT * FROM dbo.Messages WHERE receiver = ? ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), receiver);
    }

    public List<Message> findBySender(String sender) {
        String sql = "SELECT * FROM dbo.Messages WHERE sender = ? ORDER BY timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), sender);
    }

    // Conversation support methods
    public List<Message> findByConversationId(Long conversationId) {
        String sql = "SELECT * FROM dbo.Messages WHERE conversationId = ? ORDER BY timestamp ASC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), conversationId);
    }

    public List<Message> findConversationsBySender(String sender) {
        String sql = "SELECT DISTINCT m1.* FROM dbo.Messages m1 " +
                    "WHERE m1.sender = ? AND m1.conversationId = m1.id " +
                    "ORDER BY m1.timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), sender);
    }

    public List<Message> findConversationsByReceiver(String receiver) {
        String sql = "SELECT DISTINCT m1.* FROM dbo.Messages m1 " +
                    "WHERE m1.receiver = ? AND m1.conversationId = m1.id " +
                    "ORDER BY m1.timestamp DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), receiver);
    }

    public List<Message> findRepliesByParentMessageId(Long parentMessageId) {
        String sql = "SELECT * FROM dbo.Messages WHERE parentMessageId = ? ORDER BY timestamp ASC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Message.class), parentMessageId);
    }

    public Message saveWithConversation(Message message) {
        if (message.getId() == null) {
            // Insert new message
            String sql = "INSERT INTO dbo.Messages (content, sender, receiver, timestamp, status, archived, conversationId, parentMessageId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, message.getContent());
                ps.setString(2, message.getSender());
                ps.setString(3, message.getReceiver());
                ps.setObject(4, message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.now());
                ps.setString(5, message.getStatus() != null ? message.getStatus() : "unread");
                ps.setBoolean(6, message.getArchived() != null ? message.getArchived() : false);
                ps.setObject(7, message.getConversationId());
                ps.setObject(8, message.getParentMessageId());
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                message.setId(keyHolder.getKey().longValue());
                // If this is a new conversation, set conversationId to the message ID
                if (message.getConversationId() == null) {
                    message.setConversationId(message.getId());
                    updateConversationId(message.getId(), message.getId());
                }
            }
        } else {
            // Update existing message
            String sql = "UPDATE dbo.Messages SET content = ?, sender = ?, receiver = ?, timestamp = ?, status = ?, archived = ?, conversationId = ?, parentMessageId = ? WHERE id = ?";
            jdbc.update(sql, message.getContent(), message.getSender(), message.getReceiver(), 
                       message.getTimestamp(), message.getStatus(), message.getArchived(), 
                       message.getConversationId(), message.getParentMessageId(), message.getId());
        }
        return message;
    }

    private void updateConversationId(Long messageId, Long conversationId) {
        String sql = "UPDATE dbo.Messages SET conversationId = ? WHERE id = ?";
        jdbc.update(sql, conversationId, messageId);
    }
}
