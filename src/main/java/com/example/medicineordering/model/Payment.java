package com.example.medicineordering.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    
    private Long id;
    private String patientName;
    private String medicineName;
    private BigDecimal amount;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime paymentDate;
    private String notes;
    
    // Constructors
    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Payment(String patientName, String medicineName, BigDecimal amount, String status, String notes) {
        this.patientName = patientName;
        this.medicineName = medicineName;
        this.amount = amount;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}



