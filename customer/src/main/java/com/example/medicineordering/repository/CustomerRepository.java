package com.example.medicineordering.repository;

import com.example.medicineordering.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Customer Repository Class
 * This handles all database operations for customers
 */
@Repository  // This tells Spring this class handles database operations
public class CustomerRepository {

    // JdbcTemplate is Spring's tool for running SQL queries easily
    private final JdbcTemplate jdbc;

    // Spring automatically gives us a JdbcTemplate when this class is created
    @Autowired
    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CRUD: READ - Find a customer by their ID
     * Returns Optional because customer might not exist
     */
    public Optional<Customer> findById(int id) {
        String sql = "SELECT * FROM dbo.Customers WHERE id = ?";
        // Run the query and convert database rows to Customer objects
        List<Customer> list = jdbc.query(sql, new BeanPropertyRowMapper<>(Customer.class), id);
        if (list.isEmpty()) {
            return Optional.empty();  // No customer found
        }
        return Optional.of(list.get(0));  // Return the first and only customer found
    }

    /**
     * CRUD: READ - Find a customer by their email address
     * Useful for login systems
     */
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM dbo.Customers WHERE email = ?";
        List<Customer> list = jdbc.query(sql, new BeanPropertyRowMapper<>(Customer.class), email);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    /**
     * CRUD: CREATE - Save a new customer to the database with manual ID
     * The ID is provided by the User record to maintain consistency
     */
    public Customer save(Customer customer) {
        String sql = "INSERT INTO dbo.Customers (id, name, email, phone, password, address) VALUES (?,?,?,?,?,?)";
        
        jdbc.update(sql, 
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getPassword(),
            customer.getAddress()
        );
        
        return customer;
    }

    /**
     * CRUD: UPDATE - Update an existing customer's information
     * Note: We don't update email because it's used for identification
     */
    public void update(Customer customer) {
        String sql = "UPDATE dbo.Customers SET name = ?, phone = ?, password = ?, address = ? WHERE id = ?";
        jdbc.update(sql, customer.getName(), customer.getPhone(), customer.getPassword(), customer.getAddress(), customer.getId());
    }

    /**
     * Update customer ID (for linking with User records)
     * This method handles the case where we need to change the ID of an existing customer
     */
    public void updateId(int oldId, int newId) {
        try {
            // First, get the existing customer data
            Optional<Customer> existingCustomer = findById(oldId);
            if (!existingCustomer.isPresent()) {
                System.err.println("Customer with ID " + oldId + " not found for update");
                return;
            }
            
            Customer customer = existingCustomer.get();
            
            // Delete the old record
            String deleteSql = "DELETE FROM dbo.Customers WHERE id = ?";
            jdbc.update(deleteSql, oldId);
            
            // Insert with new ID
            String insertSql = "INSERT INTO dbo.Customers (id, name, email, phone, password, address) VALUES (?, ?, ?, ?, ?, ?)";
            jdbc.update(insertSql, newId, customer.getName(), customer.getEmail(), 
                       customer.getPhone(), customer.getPassword(), customer.getAddress());
            
            System.out.println("Successfully updated Customer ID from " + oldId + " to " + newId);
        } catch (Exception e) {
            System.err.println("Error updating Customer ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CRUD: READ - Find all customers
     * Useful for debugging and admin functions
     */
    public List<Customer> findAll() {
        String sql = "SELECT * FROM dbo.Customers ORDER BY id";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Customer.class));
    }
}


