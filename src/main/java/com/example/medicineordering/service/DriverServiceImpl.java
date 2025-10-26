package com.example.medicineordering.service;

import com.example.medicineordering.files.StorageService;
import com.example.medicineordering.model.Driver;
import com.example.medicineordering.repository.DeliveryExtrasRepository;
import com.example.medicineordering.repository.DriverExtrasRepository;
import com.example.medicineordering.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired private DriverRepository driverRepo;
    @Autowired private DriverExtrasRepository driverExtras;
    @Autowired private DeliveryExtrasRepository deliveryExtras;
    @Autowired private StorageService storage;
    @Autowired private JdbcTemplate jdbc;

    @Override
    public Map<String,Object> getHeader(int driverId) {
        Driver d=driverRepo.findById(driverId).orElseThrow();
        Map<String,Object> m=new HashMap<>();
        m.put("driverId",driverId);
        m.put("driverName",d.getName());
        m.put("driverInitials",initialsOf(d.getName()));
        m.put("isAvailable",d.isAvailable());
        String shift=driverExtras.findShiftTime(driverId);
        m.put("shiftTime",shift!=null?shift:"8:00 AM - 5:00 PM");
        return m;
    }

    @Override public List<Map<String,Object>> getAssigned(int driverId){return deliveryExtras.findAssignedForDriver(driverId);}
    @Override public List<Map<String,Object>> getInProgress(int driverId){return deliveryExtras.findInProgressForDriver(driverId);}
    @Override public List<Map<String,Object>> getCompletedToday(int driverId){return deliveryExtras.findCompletedTodayForDriver(driverId);}

    @Override public void toggleAvailability(int driverId,boolean available){driverRepo.setAvailability(driverId,available);}

    @Override
    public void startDelivery(int deliveryId) {
        jdbc.update("UPDATE Deliveries SET status='picked_up' WHERE id=?", deliveryId);
        jdbc.update("UPDATE Deliveries SET status='in_transit' WHERE id=?", deliveryId);
        deliveryExtras.markPickedUpAndInTransit(deliveryId);
    }

    @Override public void updateEta(int deliveryId,String eta){deliveryExtras.setEta(deliveryId,eta);}

    @Override
    public void markDelivered(int deliveryId,String recipient,String notes,MultipartFile photo,byte[] sig) throws Exception {
        String photoUrl=storage.save(photo,"pod");
        String sigUrl=sig!=null&&sig.length>0?storage.saveBytes(sig,"sig","png"):null;
        jdbc.update("UPDATE Deliveries SET status='delivered' WHERE id=?",deliveryId);
        // Update the corresponding order status to 'Delivered'
        jdbc.update("UPDATE Orders SET status='Delivered' WHERE id=(SELECT orderId FROM Deliveries WHERE id=?)",deliveryId);
        deliveryExtras.markCompleted(deliveryId);
        deliveryExtras.upsertProof(deliveryId,recipient,notes,photoUrl,sigUrl);
    }

    @Override
    public void reportIssue(int deliveryId,String type,String desc,MultipartFile photo,String action) throws Exception {
        String photoUrl=storage.save(photo,"issue");
        jdbc.update("UPDATE Deliveries SET status='failed' WHERE id=?",deliveryId);
        // Update the corresponding order status to 'Failed'
        jdbc.update("UPDATE Orders SET status='Failed' WHERE id=(SELECT orderId FROM Deliveries WHERE id=?)",deliveryId);
        deliveryExtras.insertIssue(deliveryId,type,desc,photoUrl,action);
    }

    private String initialsOf(String name){
        if(name==null||name.isBlank())return "NA";
        String[]parts=name.trim().split("\\s+");
        StringBuilder sb=new StringBuilder();
        for(String p:parts){
            sb.append(Character.toUpperCase(p.charAt(0)));
            if(sb.length()==2)break;
        }
        return sb.toString();
    }
}