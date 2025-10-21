package com.example.medicineordering.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DriverService {
    Map<String,Object> getHeader(int driverId);
    List<Map<String,Object>> getAssigned(int driverId);
    List<Map<String,Object>> getInProgress(int driverId);
    List<Map<String,Object>> getCompletedToday(int driverId);

    void toggleAvailability(int driverId, boolean available);
    void startDelivery(int deliveryId);
    void updateEta(int deliveryId,String eta);
    void markDelivered(int deliveryId,String recipient,String notes,MultipartFile photo,byte[] signature) throws Exception;
    void reportIssue(int deliveryId,String type,String desc,MultipartFile photo,String action) throws Exception;
}