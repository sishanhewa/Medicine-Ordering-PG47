package com.example.medicineordering.repository;

import com.example.medicineordering.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem Repository
 * Handles database operations for order items (what medicines were in each order)
 */
@Repository
public class OrderItemRepository {
    
    @Autowired
    private JdbcTemplate jdbc;
    
    /**
     * Save order items when an order is placed
     */
    public void saveOrderItems(int orderId, List<OrderItem> items) {
        String sql = "INSERT INTO dbo.OrderItems (orderId, medicineId, quantity, price) VALUES (?, ?, ?, ?)";
        
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            jdbc.update(sql, orderId, item.getMedicineId(), item.getQuantity(), item.getPrice());
        }
    }
    
    /**
     * Get all items for a specific order
     */
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT oi.id, oi.orderId, oi.medicineId, oi.quantity, oi.price, " +
                     "m.name as medicineName, m.category, m.requiresPrescription " +
                     "FROM dbo.OrderItems oi " +
                     "LEFT JOIN dbo.Medicines m ON oi.medicineId = m.id " +
                     "WHERE oi.orderId = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(OrderItem.class), orderId);
    }
    
    /**
     * Delete order items when an order is cancelled
     */
    public void deleteByOrderId(int orderId) {
        String sql = "DELETE FROM dbo.OrderItems WHERE orderId = ?";
        jdbc.update(sql, orderId);
    }
}
