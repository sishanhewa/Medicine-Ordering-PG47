package com.example.medicineordering.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
    private int id;
    private int orderId;
    private int driverId;
    private String status;
    private String eta;
    private String notes;
}
