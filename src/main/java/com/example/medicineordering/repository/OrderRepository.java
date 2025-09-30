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

    public void updateOrderStatus(int id, String status) {
        jdbc.update("UPDATE Orders SET status = ? WHERE id = ?", status, id);
    }

    public void setOrderWindow(int id, String window) {
        jdbc.update("UPDATE Orders SET deliveryWindow = ? WHERE id = ?", window, id);
    }
}
