package com.example.medicineordering.repository;

import com.example.medicineordering.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Payment> paymentRowMapper = new RowMapper<Payment>() {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment payment = new Payment();
            payment.setId(rs.getLong("id"));
            payment.setPatientName(rs.getString("patientName"));
            payment.setMedicineName(rs.getString("medicineName"));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setStatus(rs.getString("status"));
            payment.setPaymentDate(rs.getTimestamp("paymentDate").toLocalDateTime());
            payment.setNotes(rs.getString("notes"));
            return payment;
        }
    };
    
    public List<Payment> findAll() {
        String sql = "SELECT * FROM dbo.Payments ORDER BY paymentDate DESC";
        return jdbcTemplate.query(sql, paymentRowMapper);
    }
    
    public Optional<Payment> findById(Long id) {
        String sql = "SELECT * FROM dbo.Payments WHERE id = ?";
        List<Payment> payments = jdbcTemplate.query(sql, paymentRowMapper, id);
        return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
    }
    
    public List<Payment> findByStatus(String status) {
        String sql = "SELECT * FROM dbo.Payments WHERE status = ? ORDER BY paymentDate DESC";
        return jdbcTemplate.query(sql, paymentRowMapper, status);
    }
    
    public List<Payment> findByPatientNameContainingIgnoreCase(String patientName) {
        String sql = "SELECT * FROM dbo.Payments WHERE LOWER(patientName) LIKE LOWER(?) ORDER BY paymentDate DESC";
        return jdbcTemplate.query(sql, paymentRowMapper, "%" + patientName + "%");
    }
    
    public List<Payment> findByMedicineNameContainingIgnoreCase(String medicineName) {
        String sql = "SELECT * FROM dbo.Payments WHERE LOWER(medicineName) LIKE LOWER(?) ORDER BY paymentDate DESC";
        return jdbcTemplate.query(sql, paymentRowMapper, "%" + medicineName + "%");
    }
    
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            // Insert new payment
            String sql = "INSERT INTO dbo.Payments (patientName, medicineName, amount, status, paymentDate, notes) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, payment.getPatientName());
                ps.setString(2, payment.getMedicineName());
                ps.setBigDecimal(3, payment.getAmount());
                ps.setString(4, payment.getStatus());
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(payment.getPaymentDate()));
                ps.setString(6, payment.getNotes());
                return ps;
            }, keyHolder);
            
            payment.setId(keyHolder.getKey().longValue());
        } else {
            // Update existing payment
            String sql = "UPDATE dbo.Payments SET patientName = ?, medicineName = ?, amount = ?, status = ?, paymentDate = ?, notes = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                payment.getPatientName(), 
                payment.getMedicineName(), 
                payment.getAmount(), 
                payment.getStatus(), 
                java.sql.Timestamp.valueOf(payment.getPaymentDate()), 
                payment.getNotes(), 
                payment.getId());
        }
        return payment;
    }
    
    public BigDecimal getTotalApprovedAmount() {
        String sql = "SELECT SUM(amount) FROM dbo.Payments WHERE status = 'APPROVED'";
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return result != null ? result : BigDecimal.ZERO;
    }
    
    public Long getPendingPaymentsCount() {
        String sql = "SELECT COUNT(*) FROM dbo.Payments WHERE status = 'PENDING'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    public Long getApprovedPaymentsCount() {
        String sql = "SELECT COUNT(*) FROM dbo.Payments WHERE status = 'APPROVED'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    public Long getRejectedPaymentsCount() {
        String sql = "SELECT COUNT(*) FROM dbo.Payments WHERE status = 'REJECTED'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}



