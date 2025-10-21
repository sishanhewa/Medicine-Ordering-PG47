package com.example.medicineordering.repository;

import com.example.medicineordering.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    public List<Order> findPendingOrders() {
        String sql = "SELECT * FROM Orders WHERE status IN ('Pending','Ready') ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

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

    // Recent orders for simple "My Orders" view (demo-friendly)
    public List<Order> findRecent(int limit) {
        String sql = "SELECT TOP (?) * FROM Orders ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class), limit);
    }

    public List<Order> findByCustomerName(String customerName) {
        String sql = "SELECT * FROM Orders WHERE customerName = ? ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Order.class), customerName);
    }

    public void updateOrderStatus(int id, String status) {
        jdbc.update("UPDATE Orders SET status = ? WHERE id = ?", status, id);
    }

    public void setOrderWindow(int id, String window) {
        jdbc.update("UPDATE Orders SET deliveryWindow = ? WHERE id = ?", window, id);
    }
    // --- END OF EXISTING METHODS ---


    /**
     * CRUD: CREATE - Saves a new Order to the database and retrieves the generated ID.
     * @param order The order object to save.
     * @return The saved Order object, including the generated primary key ID.
     */
    public Order save(Order order) {
        String sql = "INSERT INTO dbo.Orders (orderNumber, customerName, deliveryAddress, status) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Use PreparedStatementCreator to specify that we want to retrieve generated keys
        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderNumber());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getDeliveryAddress());
            ps.setString(4, order.getStatus());
            return ps;
        };

        jdbc.update(psc, keyHolder);

        // Retrieve the generated ID and set it back on the Order object
        if (keyHolder.getKey() != null) {
            order.setId(keyHolder.getKey().intValue());
        }
        return order;
    }

    /**
     * CRUD: DELETE - Deletes an order by ID.
     * @param id The ID of the order to delete.
     */
    public void deleteById(int id) {
        String sql = "DELETE FROM dbo.Orders WHERE id = ?";
        jdbc.update(sql, id);
    }
}
