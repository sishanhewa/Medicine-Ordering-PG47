package com.example.medicineordering.service.dto;

public class AvailableDriverRow {
    private final int id;
    private final String initials;
    private final String name;
    private final String vehicle;     // vehicleType + " - " + licensePlate
    private final String serviceArea;
    private final boolean available;
    private final int currentLoad;    // count of active deliveries

    public AvailableDriverRow(int id, String initials, String name, String vehicle,
                              String serviceArea, boolean available, int currentLoad) {
        this.id = id;
        this.initials = initials;
        this.name = name;
        this.vehicle = vehicle;
        this.serviceArea = serviceArea;
        this.available = available;
        this.currentLoad = currentLoad;
    }

    public int getId() { return id; }
    public String getInitials() { return initials; }
    public String getName() { return name; }
    public String getVehicle() { return vehicle; }
    public String getServiceArea() { return serviceArea; }
    public boolean isAvailable() { return available; }
    public int getCurrentLoad() { return currentLoad; }
}
