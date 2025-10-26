package com.example.config;

import com.example.model.Payment;
import com.example.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Clear existing data
        paymentRepository.deleteAll();
        
        // Create sample payments
        Payment payment1 = new Payment(
            "John Smith", 
            "Aspirin 100mg", 
            new BigDecimal("25.50"), 
            "PENDING", 
            "Prescription refill for blood pressure management"
        );
        payment1.setPaymentDate(LocalDateTime.now().minusHours(2));
        
        Payment payment2 = new Payment(
            "Sarah Johnson", 
            "Metformin 500mg", 
            new BigDecimal("45.75"), 
            "PENDING", 
            "Diabetes medication - monthly supply"
        );
        payment2.setPaymentDate(LocalDateTime.now().minusHours(1));
        
        Payment payment3 = new Payment(
            "Michael Brown", 
            "Lisinopril 10mg", 
            new BigDecimal("32.00"), 
            "PENDING", 
            "Heart medication - new prescription"
        );
        payment3.setPaymentDate(LocalDateTime.now().minusMinutes(30));
        
        // Add some approved and rejected payments for demonstration
        Payment approvedPayment1 = new Payment(
            "Emily Davis", 
            "Amoxicillin 250mg", 
            new BigDecimal("18.25"), 
            "APPROVED", 
            "Antibiotic course - approved yesterday"
        );
        approvedPayment1.setPaymentDate(LocalDateTime.now().minusDays(1));
        
        Payment approvedPayment2 = new Payment(
            "Robert Wilson", 
            "Atorvastatin 20mg", 
            new BigDecimal("55.80"), 
            "APPROVED", 
            "Cholesterol medication - approved last week"
        );
        approvedPayment2.setPaymentDate(LocalDateTime.now().minusDays(7));
        
        Payment rejectedPayment1 = new Payment(
            "Lisa Anderson", 
            "Oxycodone 5mg", 
            new BigDecimal("120.00"), 
            "REJECTED", 
            "Controlled substance - requires additional documentation"
        );
        rejectedPayment1.setPaymentDate(LocalDateTime.now().minusDays(2));
        
        // Save all payments
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);
        paymentRepository.save(approvedPayment1);
        paymentRepository.save(approvedPayment2);
        paymentRepository.save(rejectedPayment1);
        
        System.out.println("Sample data initialized with 6 payments (3 pending, 2 approved, 1 rejected)");
    }
}


