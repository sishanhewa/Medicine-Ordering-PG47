package com.example.medicineordering.model;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private String content;
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;
    private String status; // e.g., "unread", "read"
    private Boolean archived = false;
    private Long conversationId; // Links messages in the same conversation
    private Long parentMessageId; // References the original message this is replying to

    public Message() {
    }

    public Message(Long id, String content, String sender, String receiver, LocalDateTime timestamp, String status, Boolean archived) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.status = status;
        this.archived = archived;
    }

    public Message(Long id, String content, String sender, String receiver, LocalDateTime timestamp, String status, Boolean archived, Long conversationId, Long parentMessageId) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.status = status;
        this.archived = archived;
        this.conversationId = conversationId;
        this.parentMessageId = parentMessageId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }
}
