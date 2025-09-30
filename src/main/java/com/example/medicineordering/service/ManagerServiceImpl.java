package com.example.medicineordering.service;

import com.example.medicineordering.model.Delivery;
import com.example.medicineordering.model.Driver;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.repository.DeliveryRepository;
import com.example.medicineordering.repository.DriverRepository;
import com.example.medicineordering.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.medicineordering.service.dto.AvailableDriverRow;
import com.example.medicineordering.service.dto.DriverRow;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final OrderRepository orderRepo;
    private final DriverRepository driverRepo;
    private final DeliveryRepository deliveryRepo;

    @Autowired
    public ManagerServiceImpl(OrderRepository orderRepo, DriverRepository driverRepo, DeliveryRepository deliveryRepo) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.deliveryRepo = deliveryRepo;
    }

    @Override
    public List<Order> getPendingOrders() {
        return orderRepo.findPendingOrders();
    }

    @Override
    public List<Order> getUnscheduledOrders() {
        return orderRepo.findUnscheduledOrders();
    }

    @Override
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = driverRepo.findAll();
        // augment: activeDeliveries count for UI column
        for (Driver d : drivers) {
            int count = driverRepo.countActiveDeliveries(d.getId());
            // if your Driver model doesn't have activeDeliveries, you can ignore;
            // otherwise set it via reflection or add a setter. Keeping logic simple here.
        }
        return drivers;
    }

    @Override
    public List<Driver> getAvailableDrivers() {
        return driverRepo.findAvailableDrivers();
    }

    @Override
    public List<Map<String, Object>> getActiveDeliveriesView() {
        List<Delivery> deliveries = deliveryRepo.findActive();
        // build lookup maps
        Map<Integer, Driver> driverById = driverRepo.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));
        Map<Integer, Order> orderById = orderRepo.findPendingOrders().stream()
                .collect(Collectors.toMap(Order::getId, o -> o, (a, b) -> a));

        List<Map<String,Object>> view = new ArrayList<>();
        for (Delivery d : deliveries) {
            Order o = orderById.getOrDefault(d.getOrderId(), orderRepo.findById(d.getOrderId()).orElse(null));
            Driver r = driverById.getOrDefault(d.getDriverId(), driverRepo.findById(d.getDriverId()).orElse(null));
            if (o == null || r == null) continue;

            String initials = Arrays.stream(r.getName().trim().split("\\s+"))
                    .map(s -> s.substring(0,1).toUpperCase())
                    .collect(Collectors.joining());
            String status = normalizeStatus(d.getStatus());
            String statusClass = switch (status) {
                case "In Transit" -> "status-assigned";
                case "Picked Up" -> "status-assigned";
                case "Assigned" -> "status-ready";
                default -> "status-ready";
            };

            Map<String,Object> row = new HashMap<>();
            row.put("id", d.getId());
            row.put("orderNumber", o.getOrderNumber());
            row.put("driverInitials", initials);
            row.put("driverName", r.getName());
            row.put("customerName", o.getCustomerName());
            row.put("status", status);
            row.put("statusClass", statusClass);
            row.put("eta", d.getEta() == null ? "" : d.getEta());
            view.add(row);
        }
        return view;
    }

    private String normalizeStatus(String dbStatus) {
        if (dbStatus == null) return "Assigned";
        switch (dbStatus.toLowerCase(Locale.ROOT)) {
            case "picked_up": return "Picked Up";
            case "in_transit": return "In Transit";
            case "delivered": return "Delivered";
            case "failed": return "Failed";
            default: return "Assigned";
        }
    }

    @Override
    public List<Map<String, Object>> getTimeSlotsView() {
        // This demo groups by deliveryWindow and driver, then computes simple capacity %
        // In real life you'd have capacity by vehicle. Here we assume 5 orders == 100% for a slot.
        final int MAX_ORDERS_PER_SLOT = 5;

        List<Order> all = orderRepo.findPendingOrders();
        List<Delivery> active = deliveryRepo.findActive();
        Map<Integer, Driver> driverById = driverRepo.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));

        Map<String, List<Delivery>> byWindow = active.stream()
                .collect(Collectors.groupingBy(d -> {
                    // find window from the order row
                    Optional<Order> o = all.stream().filter(x -> x.getId() == d.getOrderId()).findFirst();
                    return o.map(Order::getDeliveryWindow).orElse("09:00 - 12:00");
                }));

        List<Map<String,Object>> rows = new ArrayList<>();
        byWindow.forEach((window, deliveries) -> {
            Map<Integer, Long> countByDriver = deliveries.stream()
                    .collect(Collectors.groupingBy(Delivery::getDriverId, Collectors.counting()));
            countByDriver.forEach((driverId, count) -> {
                Driver driver = driverById.get(driverId);
                Map<String,Object> row = new HashMap<>();
                row.put("timeRange", window == null ? "Unscheduled" : window);
                row.put("driverName", driver == null ? "Unknown" : driver.getName());
                row.put("orderCount", count.intValue());
                int pct = (int)Math.round(Math.min(100.0, (count * 100.0) / MAX_ORDERS_PER_SLOT));
                row.put("capacityPercent", pct);
                rows.add(row);
            });
        });

        // Ensure we always show at least one row for empty state
        if (rows.isEmpty()) {
            Map<String,Object> empty = new HashMap<>();
            empty.put("timeRange","09:00 - 12:00");
            empty.put("driverName","—");
            empty.put("orderCount",0);
            empty.put("capacityPercent",0);
            rows.add(empty);
        }
        return rows;
    }

    @Override
    public void assignDriver(int orderId, int driverId) {
        deliveryRepo.assignDriver(orderId, driverId);
    }

    @Override
    public void addDriver(Driver driver) {
        if (driver.getName() == null || driver.getName().isBlank()) {
            throw new IllegalArgumentException("Driver name required");
        }
        // default availability true if not set
        if (!Boolean.TRUE.equals(driver.isAvailable())) {
            driver.setAvailable(true);
        }
        driverRepo.save(driver);
    }

    @Override
    public void removeDriver(int driverId) {
        driverRepo.deleteById(driverId);
    }

    @Override
    public void updateDeliveryStatus(int deliveryId, String status, String notes) {
        deliveryRepo.updateStatus(deliveryId, status, notes);
    }

    @Override
    public Optional<Order> getOrder(int id) {
        return orderRepo.findById(id);
    }

    @Override
    public Optional<Driver> getDriver(int id) {
        return driverRepo.findById(id);
    }

    @Override
    public List<DriverRow> getDriversView() {
        var drivers = driverRepo.findAll();
        var rows = new java.util.ArrayList<DriverRow>(drivers.size());
        for (var d : drivers) {
            var initials = computeInitials(d.getName());
            var vehicle = (d.getVehicleType() == null ? "" : d.getVehicleType())
                    + " - "
                    + (d.getLicensePlate() == null ? "" : d.getLicensePlate());
            int active = driverRepo.countActiveDeliveries(d.getId());
            rows.add(new DriverRow(
                    d.getId(),
                    initials,
                    d.getName(),
                    d.getEmail(),
                    d.getPhone(),
                    vehicle,
                    d.getServiceArea(),
                    d.isAvailable(),
                    active
            ));
        }
        return rows;
    }

    private String computeInitials(String name) {
        if (name == null || name.isBlank()) return "NA";
        var parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
            if (sb.length() == 2) break; // keep it short
        }
        return sb.toString();
    }

    private String initialsOf(String name) {
        if (name == null || name.isBlank()) return "NA";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
            if (sb.length() == 2) break;
        }
        return sb.toString();
    }

    private String vehicleOf(com.example.medicineordering.model.Driver d) {
        String type = d.getVehicleType() == null ? "" : d.getVehicleType();
        String plate = d.getLicensePlate() == null ? "" : d.getLicensePlate();
        return (type.isBlank() ? "" : type) + (plate.isBlank() ? "" : (type.isBlank() ? "" : " - ") + plate);
    }

    @Override
    public List<AvailableDriverRow> getAvailableDriversView() {
        var drivers = driverRepo.findAvailableDrivers();
        var rows = new java.util.ArrayList<AvailableDriverRow>(drivers.size());
        for (var d : drivers) {
            int load = driverRepo.countActiveDeliveries(d.getId());
            rows.add(new AvailableDriverRow(
                    d.getId(),
                    initialsOf(d.getName()),
                    d.getName(),
                    vehicleOf(d),
                    d.getServiceArea(),
                    d.isAvailable(),
                    load                       // currentLoad
            ));
        }
        return rows;
    }

}
