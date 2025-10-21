package com.example.medicineordering.controller;

import com.example.medicineordering.model.Driver;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.model.OrderItem;
import com.example.medicineordering.model.Prescription;
import com.example.medicineordering.model.User;
import java.util.List;
import com.example.medicineordering.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Date;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, HttpSession session, RedirectAttributes ra) {
        // Check if user is logged in and is a manager
        User user = (User) session.getAttribute("user");
        if (user == null || (!user.getRole().equals("DeliveryManager") && !user.getRole().equals("Admin"))) {
            ra.addFlashAttribute("error", "Access denied. Manager login required.");
            return "redirect:/login";
        }
        
        model.addAttribute("managerName", user.getFullName());
        model.addAttribute("lastUpdated", Date.from(Instant.now()));

        // sections used by your Thymeleaf
        model.addAttribute("pendingOrders", managerService.getPendingOrders());
        model.addAttribute("unscheduledOrders", managerService.getUnscheduledOrders());
        model.addAttribute("timeSlots", managerService.getTimeSlotsView());
        model.addAttribute("activeDeliveries", managerService.getActiveDeliveriesView());
        model.addAttribute("drivers", managerService.getDriversView());                 // has initials, vehicle, activeDeliveries
        model.addAttribute("availableDrivers", managerService.getAvailableDriversView()); // has initials, vehicle, currentLoad
        
        // Debug: Show all orders for troubleshooting
        model.addAttribute("allOrders", managerService.getAllOrders());

        return "manager_dashboard"; // your uploaded HTML (renamed to .html and placed in templates/)
    }

    // === Actions from the modals/buttons ===

    @PostMapping("/assign-driver")
    public String assignDriver(@RequestParam int orderId, @RequestParam int driverId) {
        managerService.assignDriver(orderId, driverId);
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/add-driver")
    public String addDriver(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String vehicleType,
                           @RequestParam String licensePlate,
                           @RequestParam String serviceArea,
                           RedirectAttributes ra) {
        try {
            // Create driver object
            Driver driver = new Driver();
            driver.setName(name);
            driver.setEmail(email);
            driver.setPhone(phone);
            driver.setPasswordHash(passwordEncoder.encode(password)); // Hash the password
            driver.setVehicleType(vehicleType);
            driver.setLicensePlate(licensePlate);
            driver.setServiceArea(serviceArea);
            driver.setAvailable(true);
            
            managerService.addDriver(driver);
            ra.addFlashAttribute("success", "Driver added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to add driver: " + e.getMessage());
        }
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
        
        // Get order items for this order
        List<OrderItem> orderItems = managerService.getOrderItems(id);
        
        // Get prescription for this order (if it's a prescription order)
        Prescription prescription = managerService.getPrescriptionByOrderId(id);
        
        System.out.println("Order details for ID: " + id);
        System.out.println("Order: " + order.getOrderNumber());
        System.out.println("Order items count: " + orderItems.size());
        System.out.println("Prescription: " + (prescription != null ? prescription.getFileName() : "null"));
        
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("prescription", prescription);
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
