package com.example.medicineordering.service;

import com.example.medicineordering.model.Notification;
import com.example.medicineordering.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(Notification notification) {
        notification.setTimestamp(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public Notification updateNotification(Notification notification) {
        Notification existing = notificationRepository.findById(notification.getId()).orElse(null);
        if (existing != null) {
            existing.setType(notification.getType());
            existing.setRecipient(notification.getRecipient());
            existing.setContent(notification.getContent());
            existing.setTimestamp(LocalDateTime.now());
            return notificationRepository.save(existing);
        }
        return null;
    }
}