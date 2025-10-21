package com.example.medicineordering.repository;

import com.example.medicineordering.model.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PrescriptionRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public PrescriptionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Custom RowMapper for Prescription to handle LocalDateTime conversion
    private static class PrescriptionRowMapper implements RowMapper<Prescription> {
        @Override
        public Prescription mapRow(ResultSet rs, int rowNum) throws SQLException {
            Prescription prescription = new Prescription();
            prescription.setId(rs.getInt("id"));
            prescription.setOrderId(rs.getObject("orderId", Integer.class));
            prescription.setCustomerId(rs.getInt("customerId"));
            prescription.setCustomerName(rs.getString("customerName"));
            prescription.setFileName(rs.getString("fileName"));
            prescription.setFilePath(rs.getString("filePath"));
            
            // Convert Timestamp to LocalDateTime
            Timestamp timestamp = rs.getTimestamp("uploadDate");
            if (timestamp != null) {
                prescription.setUploadDate(timestamp.toLocalDateTime());
            }
            
            prescription.setStatus(rs.getString("status"));
            prescription.setRejectionReason(rs.getString("rejectionReason"));
            return prescription;
        }
    }

    public List<Prescription> findByCustomerId(int customerId) {
        // Check if rejectionReason column exists first
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        String sql;
        if (hasRejectionReasonColumn) {
            sql = "SELECT p.*, c.name as customerName, p.rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.customerId = ? ORDER BY p.id DESC";
        } else {
            sql = "SELECT p.*, c.name as customerName, NULL as rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.customerId = ? ORDER BY p.id DESC";
        }
        
        return jdbc.query(sql, new PrescriptionRowMapper(), customerId);
    }

    public List<Prescription> findAll() {
        // Check if rejectionReason column exists first
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        String sql;
        if (hasRejectionReasonColumn) {
            sql = "SELECT p.*, c.name as customerName, p.rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "ORDER BY p.id DESC";
        } else {
            sql = "SELECT p.*, c.name as customerName, NULL as rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "ORDER BY p.id DESC";
        }
        
        return jdbc.query(sql, new PrescriptionRowMapper());
    }

    public List<Prescription> findById(int id) {
        // Check if rejectionReason column exists first
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        String sql;
        if (hasRejectionReasonColumn) {
            sql = "SELECT p.*, c.name as customerName, p.rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.id = ?";
        } else {
            sql = "SELECT p.*, c.name as customerName, NULL as rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.id = ?";
        }
        
        return jdbc.query(sql, new PrescriptionRowMapper(), id);
    }

    public List<Prescription> findByStatus(String status) {
        // Check if rejectionReason column exists first
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        String sql;
        if (hasRejectionReasonColumn) {
            sql = "SELECT p.*, c.name as customerName, p.rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.status = ? ORDER BY p.id DESC";
        } else {
            sql = "SELECT p.*, c.name as customerName, NULL as rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.status = ? ORDER BY p.id DESC";
        }
        
        return jdbc.query(sql, new PrescriptionRowMapper(), status);
    }

    /**
     * Manually add rejectionReason column if it doesn't exist
     */
    public void ensureRejectionReasonColumnExists() {
        try {
            String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
            boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
            
            if (!hasRejectionReasonColumn) {
                String addColumnSql = "ALTER TABLE dbo.Prescriptions ADD rejectionReason NVARCHAR(500) NULL";
                jdbc.update(addColumnSql);
                System.out.println("Added rejectionReason column to Prescriptions table");
            }
        } catch (Exception e) {
            System.err.println("Error adding rejectionReason column: " + e.getMessage());
        }
    }

    public Prescription save(Prescription p) {
        // Check if rejectionReason column exists
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        try {
            if (p.getId() > 0) {
                // Update existing prescription
                String updateSql;
                if (hasRejectionReasonColumn) {
                    updateSql = "UPDATE dbo.Prescriptions SET orderId = ?, customerId = ?, fileName = ?, filePath = ?, status = ?, rejectionReason = ? WHERE id = ?";
                } else {
                    updateSql = "UPDATE dbo.Prescriptions SET orderId = ?, customerId = ?, fileName = ?, filePath = ?, status = ? WHERE id = ?";
                }
                
                if (hasRejectionReasonColumn) {
                    jdbc.update(updateSql, 
                        p.getOrderId(), p.getCustomerId(), p.getFileName(), p.getFilePath(), p.getStatus(), p.getRejectionReason(), p.getId());
                } else {
                    jdbc.update(updateSql, 
                        p.getOrderId(), p.getCustomerId(), p.getFileName(), p.getFilePath(), p.getStatus(), p.getId());
                }
                
                System.out.println("Updated prescription with ID: " + p.getId());
                return p;
            } else {
                // Insert new prescription
                String insertSql;
                if (hasRejectionReasonColumn) {
                    insertSql = "INSERT INTO dbo.Prescriptions (orderId, customerId, fileName, filePath, status, rejectionReason) VALUES (?,?,?,?,?,?)";
                } else {
                    insertSql = "INSERT INTO dbo.Prescriptions (orderId, customerId, fileName, filePath, status) VALUES (?,?,?,?,?)";
                }
                
                KeyHolder keyHolder = new GeneratedKeyHolder();
                
                jdbc.update(con -> {
                    PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    if (p.getOrderId() == null) {
                        ps.setNull(1, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(1, p.getOrderId());
                    }
                    ps.setInt(2, p.getCustomerId());
                    ps.setString(3, p.getFileName());
                    ps.setString(4, p.getFilePath());
                    ps.setString(5, p.getStatus());
                    
                    if (hasRejectionReasonColumn) {
                        ps.setString(6, p.getRejectionReason());
                    }
                    
                    return ps;
                }, keyHolder);
                
                if (keyHolder.getKey() != null) {
                    p.setId(keyHolder.getKey().intValue());
                }
                
                // Set uploadDate to current time since database uses GETDATE()
                p.setUploadDate(java.time.LocalDateTime.now());
                
                System.out.println("Created new prescription with ID: " + p.getId());
                return p;
            }
        } catch (Exception e) {
            System.err.println("Database save error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Prescription findByOrderId(int orderId) {
        String checkColumnSql = "SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason'";
        boolean hasRejectionReasonColumn = jdbc.queryForObject(checkColumnSql, Integer.class) > 0;
        
        String sql;
        if (hasRejectionReasonColumn) {
            sql = "SELECT p.*, c.name as customerName, p.rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.orderId = ?";
        } else {
            sql = "SELECT p.*, c.name as customerName, NULL as rejectionReason " +
                  "FROM dbo.Prescriptions p " +
                  "LEFT JOIN dbo.Customers c ON p.customerId = c.id " +
                  "WHERE p.orderId = ?";
        }
        
        System.out.println("Looking for prescription with orderId: " + orderId);
        List<Prescription> prescriptions = jdbc.query(sql, new PrescriptionRowMapper(), orderId);
        System.out.println("Found " + prescriptions.size() + " prescriptions for orderId: " + orderId);
        
        if (!prescriptions.isEmpty()) {
            Prescription prescription = prescriptions.get(0);
            System.out.println("Prescription found: " + prescription.getFileName() + " - " + prescription.getStatus());
        }
        
        return prescriptions.isEmpty() ? null : prescriptions.get(0);
    }
}


