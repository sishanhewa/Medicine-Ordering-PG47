package com.example.controller;

import com.example.model.Payment;
import com.example.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        List<Payment> allPayments = paymentService.getAllPayments();
        List<Payment> pendingPayments = paymentService.getPendingPayments();
        
        model.addAttribute("allPayments", allPayments);
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("totalApprovedAmount", paymentService.getTotalApprovedAmount());
        model.addAttribute("pendingCount", paymentService.getPendingPaymentsCount());
        model.addAttribute("approvedCount", paymentService.getApprovedPaymentsCount());
        model.addAttribute("rejectedCount", paymentService.getRejectedPaymentsCount());
        
        return "dashboard";
    }
    
    @GetMapping("/payments")
    public String paymentsPage(Model model, @RequestParam(required = false) String search) {
        List<Payment> payments;
        if (search != null && !search.trim().isEmpty()) {
            payments = paymentService.searchPayments(search);
            model.addAttribute("searchTerm", search);
        } else {
            payments = paymentService.getAllPayments();
        }
        
        model.addAttribute("payments", payments);
        return "payments";
    }
    
    @PostMapping("/payments/{id}/approve")
    public String approvePayment(@PathVariable Long id) {
        paymentService.approvePayment(id);
        return "redirect:/";
    }
    
    @PostMapping("/payments/{id}/reject")
    public String rejectPayment(@PathVariable Long id) {
        paymentService.rejectPayment(id);
        return "redirect:/";
    }
    
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        List<Payment> allPayments = paymentService.getAllPayments();
        List<Payment> approvedPayments = paymentService.getPaymentsByStatus("APPROVED");
        List<Payment> rejectedPayments = paymentService.getPaymentsByStatus("REJECTED");
        
        BigDecimal totalApprovedAmount = paymentService.getTotalApprovedAmount();
        Long pendingCount = paymentService.getPendingPaymentsCount();
        Long approvedCount = paymentService.getApprovedPaymentsCount();
        Long rejectedCount = paymentService.getRejectedPaymentsCount();
        
        model.addAttribute("allPayments", allPayments);
        model.addAttribute("approvedPayments", approvedPayments);
        model.addAttribute("rejectedPayments", rejectedPayments);
        model.addAttribute("totalApprovedAmount", totalApprovedAmount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        
        return "reports";
    }
    
    @GetMapping("/add-payment")
    public String addPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
        return "add-payment";
    }
    
    @PostMapping("/add-payment")
    public String addPayment(@Valid @ModelAttribute Payment payment, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("payment", payment);
            return "add-payment";
        }
        payment.setStatus("PENDING");
        paymentService.savePayment(payment);
        return "redirect:/";
    }
}

