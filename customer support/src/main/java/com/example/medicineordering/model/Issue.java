package com.example.medicineordering.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // e.g., "open", "resolved"

    @Column
    private Long relatedMessageId;

    @Column
    private boolean archived;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }
    public String getStatus() {
        return status; }
    public void setStatus(String status) {
        this.status = status; }
    public Long getRelatedMessageId() {
        return relatedMessageId; }
    public void setRelatedMessageId(Long relatedMessageId) {
        this.relatedMessageId = relatedMessageId; }
    public boolean isArchived() {
        return archived; }
    public void setArchived(boolean archived) {
        this.archived = archived; }
    public LocalDateTime getCreatedAt() {
        return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; }
}