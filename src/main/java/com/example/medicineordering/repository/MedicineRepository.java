package com.example.medicineordering.repository;

import com.example.medicineordering.model.Medicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicineRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public MedicineRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CRUD: READ - Fetches all available medicines. (Required for customer home/search view)
     * @return A list of all Medicine objects.
     */
    public List<Medicine> findAll() {
        String sql = "SELECT id, name, category, description, price, stockLevel, requiresPrescription, imageUrl FROM dbo.Medicines WHERE stockLevel > 0 ORDER BY name";
        // BeanPropertyRowMapper maps SQL columns to Java object fields automatically
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Medicine.class));
    }

    /**
     * Fetches a single medicine by ID. (Useful for displaying details)
     * @param id The ID of the medicine.
     * @return The Medicine object or null if not found.
     */
    public Medicine findById(int id) {
        String sql = "SELECT id, name, category, description, price, stockLevel, requiresPrescription, imageUrl FROM dbo.Medicines WHERE id = ?";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Medicine.class), id);
    }

    // Simple search by name or category (beginner-friendly LIKE queries)
    public List<Medicine> search(String name, String category) {
        String base = "SELECT id, name, category, description, price, stockLevel, requiresPrescription, imageUrl FROM dbo.Medicines WHERE stockLevel > 0";
        if (name != null && name.trim().length() > 0 && category != null && category.trim().length() > 0) {
            return jdbc.query(base + " AND name LIKE ? AND category LIKE ? ORDER BY name",
                    new BeanPropertyRowMapper<>(Medicine.class), "%" + name + "%", "%" + category + "%");
        }
        if (name != null && name.trim().length() > 0) {
            return jdbc.query(base + " AND name LIKE ? ORDER BY name",
                    new BeanPropertyRowMapper<>(Medicine.class), "%" + name + "%");
        }
        if (category != null && category.trim().length() > 0) {
            return jdbc.query(base + " AND category LIKE ? ORDER BY name",
                    new BeanPropertyRowMapper<>(Medicine.class), "%" + category + "%");
        }
        return jdbc.query(base + " ORDER BY name", new BeanPropertyRowMapper<>(Medicine.class));
    }
    
    /**
     * Check if medicine has enough stock for the requested quantity
     */
    public boolean hasEnoughStock(int medicineId, int requestedQuantity) {
        String sql = "SELECT stockLevel FROM dbo.Medicines WHERE id = ?";
        Integer currentStock = jdbc.queryForObject(sql, Integer.class, medicineId);
        return currentStock != null && currentStock >= requestedQuantity;
    }
    
    /**
     * Reduce stock quantity when an order is placed
     */
    public boolean reduceStock(int medicineId, int quantity) {
        try {
            String sql = "UPDATE dbo.Medicines SET stockLevel = stockLevel - ? WHERE id = ? AND stockLevel >= ?";
            int rowsAffected = jdbc.update(sql, quantity, medicineId, quantity);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error reducing stock for medicine " + medicineId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current stock level for a medicine
     */
    public int getStockLevel(int medicineId) {
        String sql = "SELECT stockLevel FROM dbo.Medicines WHERE id = ?";
        try {
            Integer stock = jdbc.queryForObject(sql, Integer.class, medicineId);
            return stock != null ? stock : 0;
        } catch (Exception e) {
            System.err.println("Error getting stock level for medicine " + medicineId + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Restore stock quantity when an order is cancelled
     */
    public boolean restoreStock(int medicineId, int quantity) {
        try {
            String sql = "UPDATE dbo.Medicines SET stockLevel = stockLevel + ? WHERE id = ?";
            int rowsAffected = jdbc.update(sql, quantity, medicineId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error restoring stock for medicine " + medicineId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find medicines by name (for pharmacist operations)
     */
    public List<Medicine> findByName(String name) {
        String sql = "SELECT id, name, category, description, price, stockLevel, requiresPrescription, imageUrl FROM dbo.Medicines WHERE name = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Medicine.class), name);
    }
    
    /**
     * Save or update a medicine (for pharmacist operations)
     */
    public Medicine save(Medicine medicine) {
        if (medicine.getId() == 0) {
            // Insert new medicine
            String sql = "INSERT INTO dbo.Medicines (name, category, description, price, stockLevel, requiresPrescription, imageUrl) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, medicine.getName(), medicine.getCategory(), medicine.getDescription(), 
                       medicine.getPrice(), medicine.getStockLevel(), medicine.isRequiresPrescription(), medicine.getImageUrl());
            
            // Get the generated ID
            String getIdSql = "SELECT id FROM dbo.Medicines WHERE name = ? AND category = ?";
            Integer id = jdbc.queryForObject(getIdSql, Integer.class, medicine.getName(), medicine.getCategory());
            if (id != null) {
                medicine.setId(id);
            }
        } else {
            // Update existing medicine
            String sql = "UPDATE dbo.Medicines SET name = ?, category = ?, description = ?, price = ?, stockLevel = ?, requiresPrescription = ?, imageUrl = ? WHERE id = ?";
            jdbc.update(sql, medicine.getName(), medicine.getCategory(), medicine.getDescription(), 
                       medicine.getPrice(), medicine.getStockLevel(), medicine.isRequiresPrescription(), 
                       medicine.getImageUrl(), medicine.getId());
        }
        return medicine;
    }
    
    /**
     * Delete a medicine by ID (for pharmacist operations)
     */
    public void deleteById(Integer id) {
        String sql = "DELETE FROM dbo.Medicines WHERE id = ?";
        jdbc.update(sql, id);
    }
}
