package com.example.medicineordering.model;

import java.time.LocalDateTime;

public class Issue {
    private Long id;
    private String status; // e.g., "open", "resolved"
    private Long relatedMessageId;
    private boolean archived;
    private LocalDateTime createdAt;

    public Issue() {
    }

    public Issue(Long id, String status, Long relatedMessageId, boolean archived, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.relatedMessageId = relatedMessageId;
        this.archived = archived;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRelatedMessageId() {
        return relatedMessageId;
    }

    public void setRelatedMessageId(Long relatedMessageId) {
        this.relatedMessageId = relatedMessageId;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}




