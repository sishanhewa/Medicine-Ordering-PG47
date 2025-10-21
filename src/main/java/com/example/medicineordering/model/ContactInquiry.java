package com.example.medicineordering.model;

import java.time.LocalDateTime;

/**
 * ContactInquiry Model Class
 * This represents a customer inquiry sent to customer support
 */
public class ContactInquiry {
    private int id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String subject;
    private String message;
    private String status; // New, In Progress, Resolved
    private LocalDateTime inquiryDate;
    private String priority; // Low, Medium, High, Urgent

    public ContactInquiry() {
    }

    public ContactInquiry(String customerName, String customerEmail, String customerPhone, 
                          String subject, String message, String priority) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.subject = subject;
        this.message = message;
        this.priority = priority;
        this.status = "New";
        this.inquiryDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDateTime inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
