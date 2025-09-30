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
    private double weight;
    private String status;
}
