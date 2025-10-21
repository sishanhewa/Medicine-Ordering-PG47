package com.example.medicineordering.repository;

import com.example.medicineordering.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Pending = orders ready for assignment
    public List<Order> findPendingOrders() {
        String sql = "SELECT * FROM Orders WHERE status IN ('Pending','Ready') ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    // Unscheduled = not yet given a time window (null/empty)
    public List<Order> findUnscheduledOrders() {
        String sql = "SELECT * FROM Orders WHERE (deliveryWindow IS NULL OR LTRIM(RTRIM(deliveryWindow)) = '') " +
                "AND status IN ('Pending','Ready') ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    public Optional<Order> findById(int id) {
        String sql = "SELECT * FROM Orders WHERE id = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class), id)
                .stream().findFirst();
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM Orders ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    public void updateOrderStatus(int id, String status) {
        jdbc.update("UPDATE Orders SET status = ? WHERE id = ?", status, id);
    }

    public void setOrderWindow(int id, String window) {
        jdbc.update("UPDATE Orders SET deliveryWindow = ? WHERE id = ?", window, id);
    }

    // Save a new order and return the saved order with generated ID
    public Order save(Order order) {
        String sql = "INSERT INTO dbo.Orders (orderNumber, customerName, deliveryAddress, deliveryWindow, weight, status) VALUES (?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, 
            order.getOrderNumber(),
            order.getCustomerName(),
            order.getDeliveryAddress(),
            order.getDeliveryWindow(),
            order.getWeight(),
            order.getStatus()
        );
        
        // Get the generated ID
        String getIdSql = "SELECT TOP 1 id FROM dbo.Orders WHERE orderNumber = ? ORDER BY id DESC";
        Integer generatedId = jdbc.queryForObject(getIdSql, Integer.class, order.getOrderNumber());
        if (generatedId != null) {
            order.setId(generatedId);
        }
        
        return order;
    }

    // Delete order by ID
    public void deleteById(int id) {
        String sql = "DELETE FROM dbo.Orders WHERE id = ?";
        jdbc.update(sql, id);
    }

    // Find recent orders (for customer order history)
    public List<Order> findRecent(int limit) {
        String sql = "SELECT TOP " + limit + " * FROM dbo.Orders ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }
}
