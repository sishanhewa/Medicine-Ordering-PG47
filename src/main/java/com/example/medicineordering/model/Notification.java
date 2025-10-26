package com.example.medicineordering.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String type; // e.g., "order_status", "delay", "general"
    private String content;
    private String recipient;
    private LocalDateTime timestamp;

    public Notification() {
    }

    public Notification(Long id, String type, String content, String recipient, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.recipient = recipient;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}




