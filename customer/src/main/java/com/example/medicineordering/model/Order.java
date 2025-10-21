package com.example.medicineordering.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private String orderNumber;
    private String customerName;
    private String deliveryAddress;
    private String deliveryWindow;
    private Double weight; // Changed to Double to fix the 400 error
    private String status;
    private java.time.LocalDateTime orderDate; // For display purposes
    private int itemCount; // For display purposes
}