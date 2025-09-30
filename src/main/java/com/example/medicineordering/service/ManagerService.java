package com.example.medicineordering.service;

import com.example.medicineordering.model.Driver;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.service.dto.DriverRow;
import com.example.medicineordering.service.dto.AvailableDriverRow;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.List;

public interface ManagerService {
    // lists for dashboard
    List<AvailableDriverRow> getAvailableDriversView();

    List<Order> getPendingOrders();
    List<Order> getUnscheduledOrders();
    List<Driver> getAllDrivers();
    List<Driver> getAvailableDrivers();
    List<Map<String,Object>> getActiveDeliveriesView();  // map has keys: id, orderNumber, driverInitials, driverName, customerName, status, statusClass, eta
    List<DriverRow> getDriversView();
    // time slots visualization (maps with keys: timeRange, driverName, orderCount, capacityPercent)
    List<Map<String,Object>> getTimeSlotsView();

    // actions
    void assignDriver(int orderId, int driverId);
    void addDriver(Driver driver);
    void removeDriver(int driverId);
    void updateDeliveryStatus(int deliveryId, String status, String notes);

    Optional<Order> getOrder(int id);
    Optional<Driver> getDriver(int id);
}
