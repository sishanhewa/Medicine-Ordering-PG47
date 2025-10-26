package com.example.medicineordering.repository;

import com.example.medicineordering.model.Issue;
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
public class IssueRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public IssueRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Issue> findAll() {
        String sql = "SELECT * FROM dbo.Issues ORDER BY createdAt DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Issue.class));
    }

    public Optional<Issue> findById(Long id) {
        String sql = "SELECT * FROM dbo.Issues WHERE id = ?";
        List<Issue> issues = jdbc.query(sql, new BeanPropertyRowMapper<>(Issue.class), id);
        return issues.stream().findFirst();
    }

    public Issue save(Issue issue) {
        if (issue.getId() == null) {
            // Insert new issue
            String sql = "INSERT INTO dbo.Issues (status, relatedMessageId, archived, createdAt) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, issue.getStatus());
                ps.setObject(2, issue.getRelatedMessageId());
                ps.setBoolean(3, issue.isArchived());
                ps.setObject(4, issue.getCreatedAt() != null ? issue.getCreatedAt() : LocalDateTime.now());
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                issue.setId(keyHolder.getKey().longValue());
            }
        } else {
            // Update existing issue
            String sql = "UPDATE dbo.Issues SET status = ?, relatedMessageId = ?, archived = ? WHERE id = ?";
            jdbc.update(sql, issue.getStatus(), issue.getRelatedMessageId(), issue.isArchived(), issue.getId());
        }
        return issue;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM dbo.Issues WHERE id = ?";
        jdbc.update(sql, id);
    }

    public List<Issue> findByStatus(String status) {
        String sql = "SELECT * FROM dbo.Issues WHERE status = ? ORDER BY createdAt DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Issue.class), status);
    }

    public List<Issue> findByArchived(boolean archived) {
        String sql = "SELECT * FROM dbo.Issues WHERE archived = ? ORDER BY createdAt DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Issue.class), archived);
    }
}




