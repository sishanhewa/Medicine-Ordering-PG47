package com.example.medicineordering.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // e.g., "order_status", "delay", "general"

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Getters and Setters
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }
    public String getType() {
        return type; }
    public void setType(String type) {
        this.type = type; }
    public String getContent() {
        return content; }
    public void setContent(String content) {
        this.content = content; }
    public String getRecipient() {
        return recipient; }
    public void setRecipient(String recipient) {
        this.recipient = recipient; }
    public LocalDateTime getTimestamp() {
        return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp; }
}