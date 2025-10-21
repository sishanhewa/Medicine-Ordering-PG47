package com.example.medicineordering.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String vehicleType;
    private String licensePlate;
    private String serviceArea;
    private boolean available;
    private LocalDateTime createdAt;
}
