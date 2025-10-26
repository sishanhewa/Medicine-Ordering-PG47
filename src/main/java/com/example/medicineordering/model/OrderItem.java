package com.example.medicineordering.model;

/**
 * OrderItem Model Class
 * This represents one item in an order (what medicines were ordered)
 */
public class OrderItem {
    private int id;
    private int orderId;
    private int medicineId;
    private int quantity;
    private String medicineName; // For display purposes only
    private double price; // Price at time of order
    private String category; // For display purposes only
    private boolean requiresPrescription; // For display purposes only

    public OrderItem() {
    }

    public OrderItem(int id, int orderId, int medicineId, int quantity, double price) {
        this.id = id;
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }
}



