package com.example.medicineordering.controller;

import com.example.medicineordering.model.Medicine;
import com.example.medicineordering.model.Prescription;
import com.example.medicineordering.model.Order;
import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.MedicineRepository;
import com.example.medicineordering.repository.PrescriptionRepository;
import com.example.medicineordering.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {
    
    @Autowired
    private MedicineRepository medicineRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping({"", "/dashboard"})
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        // Ensure rejectionReason column exists
        prescriptionRepository.ensureRejectionReasonColumnExists();
        
        // Get statistics for dashboard
        List<Medicine> medicines = medicineRepository.findAll();
        List<Prescription> pendingPrescriptions = prescriptionRepository.findByStatus("PENDING");
        List<Prescription> approvedPrescriptions = prescriptionRepository.findByStatus("APPROVED");
        
        model.addAttribute("totalMedicines", medicines.size());
        model.addAttribute("pendingPrescriptions", pendingPrescriptions.size());
        model.addAttribute("approvedPrescriptions", approvedPrescriptions.size());
        model.addAttribute("lowStockMedicines", medicines.stream()
            .filter(m -> m.getStockLevel() < 10)
            .count());
        
        return "pharmacist_dashboard";
    }

    @GetMapping("/medicines")
    public String medicines(HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        List<Medicine> medicines = medicineRepository.findAll();
        model.addAttribute("medicines", medicines);
        model.addAttribute("medicine", new Medicine());
        return "pharmacist_medicines";
    }

    @GetMapping("/prescriptions")
    public String prescriptions(HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        model.addAttribute("prescriptions", prescriptions);
        return "pharmacist_prescriptions";
    }

    @PostMapping("/medicine")
    public String addOrUpdateMedicine(@ModelAttribute("medicine") Medicine medicine, 
                                    BindingResult result, 
                                    HttpSession session, 
                                    RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Please fill in all required fields.");
            return "redirect:/pharmacist/medicines";
        }
        
        try {
            // Check if medicine exists by name
            List<Medicine> existingMedicines = medicineRepository.findByName(medicine.getName());
            if (!existingMedicines.isEmpty()) {
                // Update existing medicine
                Medicine existing = existingMedicines.get(0);
                existing.setPrice(medicine.getPrice());
                existing.setStockLevel(medicine.getStockLevel());
                existing.setCategory(medicine.getCategory());
                existing.setDescription(medicine.getDescription());
                existing.setRequiresPrescription(medicine.isRequiresPrescription());
                existing.setImageUrl(medicine.getImageUrl());
                medicineRepository.save(existing);
                ra.addFlashAttribute("success", "Medicine updated successfully.");
            } else {
                // Add new medicine
                medicineRepository.save(medicine);
                ra.addFlashAttribute("success", "Medicine added successfully.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error saving medicine: " + e.getMessage());
        }
        
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/medicine/{id}/delete")
    public String deleteMedicine(@PathVariable Integer id, 
                               HttpSession session, 
                               RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        try {
            // Check if medicine exists before attempting deletion
            Medicine medicine = medicineRepository.findById(id);
            if (medicine == null) {
                ra.addFlashAttribute("error", "Medicine not found.");
                return "redirect:/pharmacist/medicines";
            }
            
            medicineRepository.deleteById(id);
            ra.addFlashAttribute("success", "Medicine '" + medicine.getName() + "' deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting medicine with ID " + id + ": " + e.getMessage());
            ra.addFlashAttribute("error", "Error deleting medicine: " + e.getMessage());
        }
        
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/medicine/{id}/stock")
    public String updateMedicineStock(@PathVariable Integer id, 
                                    @RequestParam("stockLevel") Integer stockLevel,
                                    HttpSession session, 
                                    RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        try {
            Medicine medicine = medicineRepository.findById(id);
            if (medicine != null) {
                medicine.setStockLevel(stockLevel);
                medicineRepository.save(medicine);
                ra.addFlashAttribute("success", "Stock level updated successfully.");
            } else {
                ra.addFlashAttribute("error", "Medicine not found.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating stock: " + e.getMessage());
        }
        
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/prescription/{id}/approve")
    public String approvePrescription(@PathVariable Integer id, 
                                    HttpSession session, 
                                    RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        try {
            List<Prescription> prescriptions = prescriptionRepository.findById(id);
            if (!prescriptions.isEmpty()) {
                Prescription prescription = prescriptions.get(0);
                prescription.setStatus("APPROVED");
                prescriptionRepository.save(prescription);
                
                // Create an order for the approved prescription
                createOrderFromPrescription(prescription);
                
                ra.addFlashAttribute("success", "Prescription approved and order created.");
            } else {
                ra.addFlashAttribute("error", "Prescription not found.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error approving prescription: " + e.getMessage());
        }
        
        return "redirect:/pharmacist/prescriptions";
    }

    @PostMapping("/prescription/{id}/reject")
    public String rejectPrescription(@PathVariable Integer id, 
                                   @RequestParam("rejectionReason") String rejectionReason,
                                   HttpSession session, 
                                   RedirectAttributes ra) {
        // Check if user is logged in and is a pharmacist
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("Pharmacist")) {
            ra.addFlashAttribute("error", "Access denied. Pharmacist login required.");
            return "redirect:/login";
        }
        
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Rejection reason is required.");
            return "redirect:/pharmacist/prescriptions";
        }
        
        try {
            List<Prescription> prescriptions = prescriptionRepository.findById(id);
            if (!prescriptions.isEmpty()) {
                Prescription prescription = prescriptions.get(0);
                prescription.setStatus("REJECTED");
                prescription.setRejectionReason(rejectionReason.trim());
                prescriptionRepository.save(prescription);
                ra.addFlashAttribute("success", "Prescription rejected with reason: " + rejectionReason);
            } else {
                ra.addFlashAttribute("error", "Prescription not found.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error rejecting prescription: " + e.getMessage());
        }
        
        return "redirect:/pharmacist/prescriptions";
    }
    
    private void createOrderFromPrescription(Prescription prescription) {
        try {
            // If prescription already has an order, update it
            if (prescription.getOrderId() != null) {
                Optional<Order> existingOrderOpt = orderRepository.findById(prescription.getOrderId());
                if (existingOrderOpt.isPresent()) {
                    Order existingOrder = existingOrderOpt.get();
                    existingOrder.setStatus("Ready"); // Ready for delivery assignment
                    orderRepository.save(existingOrder);
                    System.out.println("Updated existing order for prescription: " + prescription.getId());
                    return;
                }
            }
            
            // Create new order if none exists
            Order order = new Order();
            order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            order.setCustomerName(prescription.getCustomerName());
            order.setDeliveryAddress("To be confirmed"); // Will be updated by customer
            order.setDeliveryWindow("Standard delivery");
            order.setWeight(0.5); // Default weight
            order.setStatus("Ready"); // Ready for delivery assignment
            order.setOrderDate(LocalDateTime.now());
            order.setItemCount(1); // Default item count
            
            orderRepository.save(order);
            
            // Update prescription with order ID
            prescription.setOrderId(order.getId());
            prescriptionRepository.save(prescription);
            
            System.out.println("Order created for prescription: " + prescription.getId());
        } catch (Exception e) {
            System.err.println("Error creating order from prescription: " + e.getMessage());
        }
    }
}
