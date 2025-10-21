package com.example.medicineordering.repository;

import com.example.medicineordering.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Cart Repository Class
 * This handles all database operations for shopping cart items
 */
@Repository
public class CartRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public CartRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CRUD: READ - Get all cart items for a specific customer
     * Returns them in reverse order (newest first) so recent additions appear at top
     */
    public List<Cart> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM dbo.Carts WHERE customerId = ? ORDER BY id DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Cart.class), customerId);
    }

    /**
     * CRUD: CREATE - Add a new item to the cart
     * This happens when customer clicks Add to Cart on a medicine
     */
    public void addItem(int customerId, int medicineId, int quantity) {
        String sql = "INSERT INTO dbo.Carts (customerId, medicineId, quantity) VALUES (?,?,?)";
        jdbc.update(sql, customerId, medicineId, quantity);
    }

    /**
     * CRUD: UPDATE - Change the quantity of an existing cart item
     * This happens when customer changes quantity and clicks Update
     */
    public void updateQuantity(int id, int quantity) {
        String sql = "UPDATE dbo.Carts SET quantity = ? WHERE id = ?";
        jdbc.update(sql, quantity, id);
    }

    /**
     * CRUD: DELETE - Remove a specific item from the cart
     * This happens when customer clicks Remove on a cart item
     */
    public void removeItem(int id) {
        String sql = "DELETE FROM dbo.Carts WHERE id = ?";
        jdbc.update(sql, id);
    }

    /**
     * CRUD: DELETE - Remove all items from a customer's cart
     * This happens after customer successfully places an order
     */
    public void clearCustomerCart(int customerId) {
        String sql = "DELETE FROM dbo.Carts WHERE customerId = ?";
        jdbc.update(sql, customerId);
    }
    
    /**
     * Find a cart item by its ID
     */
    public Cart findById(int id) {
        String sql = "SELECT id, customerId, medicineId, quantity FROM dbo.Carts WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Cart.class), id);
        } catch (Exception e) {
            return null;
        }
    }
}


