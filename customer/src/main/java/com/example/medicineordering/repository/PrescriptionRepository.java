package com.example.medicineordering.repository;

import com.example.medicineordering.model.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PrescriptionRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public PrescriptionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Prescription> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM dbo.Prescriptions WHERE customerId = ? ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Prescription.class), customerId);
    }

    public Prescription save(Prescription p) {
        String sql = "INSERT INTO dbo.Prescriptions (orderId, customerId, fileName, filePath, status) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (p.getOrderId() == null) {
                    ps.setNull(1, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(1, p.getOrderId());
                }
                ps.setInt(2, p.getCustomerId());
                ps.setString(3, p.getFileName());
                ps.setString(4, p.getFilePath());
                ps.setString(5, p.getStatus());
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                p.setId(keyHolder.getKey().intValue());
            }
            
            // Set uploadDate to current time since database uses GETDATE()
            p.setUploadDate(new java.util.Date());
            
            return p;
        } catch (Exception e) {
            System.err.println("Database save error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}


