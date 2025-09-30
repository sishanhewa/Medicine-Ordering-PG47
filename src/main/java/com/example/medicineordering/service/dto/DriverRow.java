package com.example.medicineordering.service.dto;

public class DriverRow {
    private final int id;
    private final String initials;
    private final String name;
    private final String email;
    private final String phone;
    private final String vehicle;        // vehicleType + " - " + licensePlate
    private final String serviceArea;
    private final boolean available;
    private final int activeDeliveries;

    public DriverRow(int id, String initials, String name, String email, String phone,
                     String vehicle, String serviceArea, boolean available, int activeDeliveries) {
        this.id = id;
        this.initials = initials;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vehicle = vehicle;
        this.serviceArea = serviceArea;
        this.available = available;
        this.activeDeliveries = activeDeliveries;
    }

    public int getId() { return id; }
    public String getInitials() { return initials; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getVehicle() { return vehicle; }
    public String getServiceArea() { return serviceArea; }
    public boolean isAvailable() { return available; }
    public int getActiveDeliveries() { return activeDeliveries; }
}
