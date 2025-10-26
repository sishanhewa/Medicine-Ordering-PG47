package com.example.repository;

import com.example.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStatus(String status);
    
    List<Payment> findByPatientNameContainingIgnoreCase(String patientName);
    
    List<Payment> findByMedicineNameContainingIgnoreCase(String medicineName);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'APPROVED'")
    BigDecimal getTotalApprovedAmount();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    Long getPendingPaymentsCount();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'APPROVED'")
    Long getApprovedPaymentsCount();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'REJECTED'")
    Long getRejectedPaymentsCount();
}

