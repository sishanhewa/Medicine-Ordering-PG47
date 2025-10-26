package com.example.medicineordering.controller;

import com.example.medicineordering.model.Payment;
import com.example.medicineordering.model.User;
import com.example.medicineordering.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        try {
            List<Payment> allPayments = paymentService.getAllPayments();
            List<Payment> pendingPayments = paymentService.getPendingPayments();
            
            model.addAttribute("user", user);
            model.addAttribute("allPayments", allPayments);
            model.addAttribute("pendingPayments", pendingPayments);
            model.addAttribute("totalApprovedAmount", paymentService.getTotalApprovedAmount());
            model.addAttribute("pendingCount", paymentService.getPendingPaymentsCount());
            model.addAttribute("approvedCount", paymentService.getApprovedPaymentsCount());
            model.addAttribute("rejectedCount", paymentService.getRejectedPaymentsCount());
        } catch (Exception e) {
            System.err.println("Error loading finance dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Provide fallback data when Payments table doesn't exist
            model.addAttribute("user", user);
            model.addAttribute("allPayments", List.of());
            model.addAttribute("pendingPayments", List.of());
            model.addAttribute("totalApprovedAmount", java.math.BigDecimal.ZERO);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("approvedCount", 0L);
            model.addAttribute("rejectedCount", 0L);
            model.addAttribute("error", "Payments table not found. Please run the create-payments-table.sql script on your database.");
        }
        
        return "finance/dashboard";
    }
    
    @GetMapping("/payments")
    public String paymentsPage(@RequestParam(required = false) String search,
                              HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        try {
            List<Payment> payments;
            if (search != null && !search.trim().isEmpty()) {
                payments = paymentService.searchPayments(search);
                model.addAttribute("searchTerm", search);
            } else {
                payments = paymentService.getAllPayments();
            }
            
            model.addAttribute("user", user);
            model.addAttribute("payments", payments);
        } catch (Exception e) {
            System.err.println("Error loading payments: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("user", user);
            model.addAttribute("payments", List.of());
            model.addAttribute("error", "Payments table not found. Please run the create-payments-table.sql script on your database.");
        }
        
        return "finance/payments";
    }
    
    @PostMapping("/payments/{id}/approve")
    public String approvePayment(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        paymentService.approvePayment(id);
        ra.addFlashAttribute("success", "Payment approved successfully.");
        return "redirect:/finance/dashboard";
    }
    
    @PostMapping("/payments/{id}/reject")
    public String rejectPayment(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        paymentService.rejectPayment(id);
        ra.addFlashAttribute("success", "Payment rejected successfully.");
        return "redirect:/finance/dashboard";
    }
    
    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        List<Payment> allPayments = paymentService.getAllPayments();
        List<Payment> approvedPayments = paymentService.getPaymentsByStatus("APPROVED");
        List<Payment> rejectedPayments = paymentService.getPaymentsByStatus("REJECTED");
        
        BigDecimal totalApprovedAmount = paymentService.getTotalApprovedAmount();
        Long pendingCount = paymentService.getPendingPaymentsCount();
        Long approvedCount = paymentService.getApprovedPaymentsCount();
        Long rejectedCount = paymentService.getRejectedPaymentsCount();
        
        model.addAttribute("user", user);
        model.addAttribute("allPayments", allPayments);
        model.addAttribute("approvedPayments", approvedPayments);
        model.addAttribute("rejectedPayments", rejectedPayments);
        model.addAttribute("totalApprovedAmount", totalApprovedAmount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        
        return "finance/reports";
    }
    
    @GetMapping("/add-payment")
    public String addPaymentForm(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("payment", new Payment());
        return "finance/add-payment";
    }
    
    @PostMapping("/add-payment")
    public String addPayment(@ModelAttribute Payment payment, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        try {
            payment.setStatus("PENDING");
            paymentService.savePayment(payment);
            ra.addFlashAttribute("success", "Payment added successfully.");
            return "redirect:/finance/dashboard";
        } catch (Exception e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error adding payment: " + e.getMessage());
            return "redirect:/finance/add-payment";
        }
    }
    
    @GetMapping("/test-database")
    public String testDatabase(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("FinanceManager")) {
            ra.addFlashAttribute("error", "Access denied. Finance Manager login required.");
            return "redirect:/login";
        }

        try {
            // Test if Payments table exists by trying to get count
            Long count = paymentService.getPendingPaymentsCount();
            model.addAttribute("user", user);
            model.addAttribute("message", "Payments table exists and is accessible. Pending payments count: " + count);
            model.addAttribute("success", true);
        } catch (Exception e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("user", user);
            model.addAttribute("message", "Payments table not found. Error: " + e.getMessage());
            model.addAttribute("success", false);
        }
        
        return "finance/test-database";
    }
}
