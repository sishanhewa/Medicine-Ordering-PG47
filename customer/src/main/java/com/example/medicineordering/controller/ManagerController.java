package com.example.medicineordering.controller;

import com.example.medicineordering.model.Driver;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("managerName", "Delivery Manager");
        model.addAttribute("lastUpdated", Date.from(Instant.now()));

        // sections used by your Thymeleaf
        model.addAttribute("pendingOrders", managerService.getPendingOrders());
        model.addAttribute("unscheduledOrders", managerService.getUnscheduledOrders());
        model.addAttribute("timeSlots", managerService.getTimeSlotsView());
        model.addAttribute("activeDeliveries", managerService.getActiveDeliveriesView());
        model.addAttribute("drivers", managerService.getDriversView());                 // has initials, vehicle, activeDeliveries
        model.addAttribute("availableDrivers", managerService.getAvailableDriversView()); // has initials, vehicle, currentLoad

        return "manager_dashboard"; // your uploaded HTML (renamed to .html and placed in templates/)
    }

    // === Actions from the modals/buttons ===

    @PostMapping("/assign-driver")
    public String assignDriver(@RequestParam int orderId, @RequestParam int driverId) {
        managerService.assignDriver(orderId, driverId);
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/add-driver")
    public String addDriver(Driver driver) {
        managerService.addDriver(driver);
        return "redirect:/manager/dashboard#manage-drivers-section";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam int deliveryId,
                               @RequestParam String status,
                               @RequestParam(required = false) String notes) {
        managerService.updateDeliveryStatus(deliveryId, status, notes);
        return "redirect:/manager/dashboard#track-deliveries-section";
    }

    // === Links from the cards/buttons in your page ===

    @GetMapping("/order/{id}")
    public String orderDetails(@PathVariable int id, Model model) {
        Order order = managerService.getOrder(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        model.addAttribute("order", order);
        return "manager_order"; // small detail page
    }

    @GetMapping("/driver/{id}")
    public String driverDetails(@PathVariable int id, Model model) {
        Driver driver = managerService.getDriver(id)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + id));
        model.addAttribute("driver", driver);
        model.addAttribute("activeDeliveries", managerService.getActiveDeliveriesView());
        return "manager_driver"; // small detail page
    }

    @GetMapping("/remove-driver/{id}")
    public String removeDriver(@PathVariable int id) {
        managerService.removeDriver(id);
        return "redirect:/manager/dashboard#manage-drivers-section";
    }
}
