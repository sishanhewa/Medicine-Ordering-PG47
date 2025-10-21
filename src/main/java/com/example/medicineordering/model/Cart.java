package com.example.medicineordering.model;

/**
 * Cart Model Class
 * This represents one item in a customer's shopping cart
 */
public class Cart {
    // Database fields - these match the columns in dbo.Carts table
    private int id;           // Unique cart line ID
    private int customerId;   // Which customer owns this cart item (foreign key to Customers table)
    private int medicineId;   // Which medicine is in the cart (foreign key to Medicines table)
    private int quantity;     // How many of this medicine the customer wants
    

    private String medicineName;
    private int stockLevel; // For display purposes only - not stored in database
    private double price; // For display purposes only - not stored in database
    private String category; // For display purposes only - not stored in database
    private boolean requiresPrescription; // For display purposes only - not stored in database


    public Cart() {
    }


    public Cart(int id, int customerId, int medicineId, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
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


