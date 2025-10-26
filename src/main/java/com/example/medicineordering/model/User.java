package com.example.medicineordering.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private String email;
    private String fullName;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;

    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String passwordHash, String role, String email, String fullName, String phone) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods for role checking
    public boolean isCustomer() {
        return "Customer".equals(this.role);
    }

    public boolean isAdmin() {
        return "Admin".equals(this.role);
    }

    public boolean isDeliveryManager() {
        return "DeliveryManager".equals(this.role);
    }

    public boolean isDeliveryPersonnel() {
        return "DeliveryPersonnel".equals(this.role);
    }

    public boolean isPharmacist() {
        return "Pharmacist".equals(this.role);
    }

    public boolean isCustomerSupport() {
        return "CustomerSupport".equals(this.role);
    }

    public boolean isFinanceManager() {
        return "FinanceManager".equals(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}



