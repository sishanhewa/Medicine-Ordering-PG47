package com.example.service;

import com.example.model.Payment;
import com.example.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment approvePayment(Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (!"PENDING".equals(payment.getStatus())) {
                return payment; // no-op if already processed
            }
            payment.setStatus("APPROVED");
            return paymentRepository.save(payment);
        }
        return null;
    }
    
    public Payment rejectPayment(Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("REJECTED");
            return paymentRepository.save(payment);
        }
        return null;
    }
    
    public BigDecimal getTotalApprovedAmount() {
        BigDecimal total = paymentRepository.getTotalApprovedAmount();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public Long getPendingPaymentsCount() {
        return paymentRepository.getPendingPaymentsCount();
    }
    
    public Long getApprovedPaymentsCount() {
        return paymentRepository.getApprovedPaymentsCount();
    }
    
    public Long getRejectedPaymentsCount() {
        return paymentRepository.getRejectedPaymentsCount();
    }
    
    public List<Payment> searchPayments(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPayments();
        }
        
        List<Payment> byPatient = paymentRepository.findByPatientNameContainingIgnoreCase(searchTerm);
        List<Payment> byMedicine = paymentRepository.findByMedicineNameContainingIgnoreCase(searchTerm);
        
        // Combine and remove duplicates
        byPatient.addAll(byMedicine);
        return byPatient.stream().distinct().toList();
    }
}

