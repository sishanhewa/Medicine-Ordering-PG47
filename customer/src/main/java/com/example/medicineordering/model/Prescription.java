package com.example.medicineordering.model;

import java.util.Date;

/**
 * Prescription Model Class
 * This represents an uploaded prescription file like a doctor's prescription image or PDF
 * Customers upload these files when they need prescription medicines
 */
public class Prescription {
    // Database fields - these match the columns in dbo.Prescriptions table
    private int id;           // Unique prescription ID
    private Integer orderId;  // Which order this prescription is for
    private int customerId;   // Which customer uploaded this (foreign key to Customers table)
    private String fileName;  // Original name of the uploaded file
    private String filePath;  // Where the file is stored on our server
    private Date uploadDate;  // When the file was uploaded
    private String status;    // Status like Uploaded, Verified, Rejected


    public Prescription() {
    }


    public Prescription(int id, Integer orderId, int customerId, String fileName, String filePath, Date uploadDate, String status) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


