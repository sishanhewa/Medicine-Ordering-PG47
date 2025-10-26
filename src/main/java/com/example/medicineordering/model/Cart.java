package com.example.medicineordering.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private int id;
    private int customerId;
    private int medicineId;
    private int quantity;
    private double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Medicine information (populated when needed)
    private String medicineName;
    private int stockLevel;
    private String category;
    private boolean requiresPrescription;
}
