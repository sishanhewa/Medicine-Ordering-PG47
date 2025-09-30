package com.example.medicineordering.repository;

import com.example.medicineordering.model.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeliveryRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public DeliveryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void assignDriver(int orderId, int driverId) {
        jdbc.update("INSERT INTO Deliveries(orderId, driverId, status) VALUES (?, ?, 'Assigned')",
                orderId, driverId);
        jdbc.update("UPDATE Orders SET status = 'Assigned' WHERE id = ?", orderId);
    }

    public void updateStatus(int deliveryId, String status, String notes) {
        jdbc.update("UPDATE Deliveries SET status = ?, notes = ? WHERE id = ?",
                status, notes, deliveryId);
        // Also sync the order's status when it reaches Delivered/Failed
        if ("delivered".equalsIgnoreCase(status)) {
            jdbc.update("UPDATE Orders SET status = 'Delivered' WHERE id = " +
                    "(SELECT orderId FROM Deliveries WHERE id = ?)", deliveryId);
        } else if ("failed".equalsIgnoreCase(status)) {
            jdbc.update("UPDATE Orders SET status = 'Failed' WHERE id = " +
                    "(SELECT orderId FROM Deliveries WHERE id = ?)", deliveryId);
        }
    }

    public Optional<Delivery> findById(int id) {
        String sql = "SELECT * FROM Deliveries WHERE id = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Delivery.class), id)
                .stream().findFirst();
    }

    public List<Delivery> findActive() {
        String sql = "SELECT * FROM Deliveries WHERE status IN ('Assigned','picked_up','in_transit')";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Delivery.class));
    }

    public List<Delivery> findByDriver(int driverId) {
        String sql = "SELECT * FROM Deliveries WHERE driverId = ? ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Delivery.class), driverId);
    }
}
