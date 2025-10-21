package com.example.medicineordering.model;


public class Medicine {
    private int id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int stockLevel;
    private boolean requiresPrescription;
    private String imageUrl;

    public Medicine() {
    }

    public Medicine(int id, String name, String description, double price, int stockLevel, boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockLevel = stockLevel;
        this.requiresPrescription = requiresPrescription;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}