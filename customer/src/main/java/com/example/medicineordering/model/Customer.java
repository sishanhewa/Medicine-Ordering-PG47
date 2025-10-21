package com.example.medicineordering.model;

/**
 * Customer Model Class
 * This represents a customer in our system - like a person who wants to buy medicines
 * Each field matches a column in the dbo.Customers database table
 */
public class Customer {
    // Database fields - these match the columns in dbo.Customers table
    private int id;           // Unique customer ID
    private String name;      // Customer's full name
    private String email;     // Customer's email address
    private String phone;     // Customer's phone number
    private String password;  // Simple password
    private String address;   // Customer's delivery address


    public Customer() {
    }


    public Customer(int id, String name, String email, String phone, String password, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}


