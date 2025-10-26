package com.example.medicineordering.repository;

import com.example.medicineordering.model.ContactInquiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * ContactInquiry Repository
 * Handles database operations for customer inquiries
 */
@Repository
public class ContactInquiryRepository {
    
    @Autowired
    private JdbcTemplate jdbc;
    
    /**
     * Save a new customer inquiry
     */
    public ContactInquiry save(ContactInquiry inquiry) {
        String sql = "INSERT INTO dbo.ContactInquiries (customerName, customerEmail, customerPhone, subject, message, status, inquiryDate, priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, inquiry.getCustomerName());
            ps.setString(2, inquiry.getCustomerEmail());
            ps.setString(3, inquiry.getCustomerPhone());
            ps.setString(4, inquiry.getSubject());
            ps.setString(5, inquiry.getMessage());
            ps.setString(6, inquiry.getStatus());
            ps.setObject(7, inquiry.getInquiryDate());
            ps.setString(8, inquiry.getPriority());
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            inquiry.setId(keyHolder.getKey().intValue());
        }
        return inquiry;
    }
    
    /**
     * Get all inquiries (for customer support view)
     */
    public List<ContactInquiry> findAll() {
        String sql = "SELECT * FROM dbo.ContactInquiries ORDER BY inquiryDate DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(ContactInquiry.class));
    }
    
    /**
     * Get inquiries by status
     */
    public List<ContactInquiry> findByStatus(String status) {
        String sql = "SELECT * FROM dbo.ContactInquiries WHERE status = ? ORDER BY inquiryDate DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(ContactInquiry.class), status);
    }
    
    /**
     * Update inquiry status
     */
    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.ContactInquiries SET status = ? WHERE id = ?";
        jdbc.update(sql, status, id);
    }
}




