package com.example.medicineordering.service;

import com.example.medicineordering.model.Issue;
import com.example.medicineordering.model.Message;
import com.example.medicineordering.repository.IssueRepository;
import com.example.medicineordering.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerSupportService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private IssueRepository issueRepository;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    public Message saveMessage(Message message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        if (message.getStatus() == null) {
            message.setStatus("unread");
        }
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByStatus(String status) {
        return messageRepository.findByStatus(status);
    }

    public Message archiveMessage(Long id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setArchived(true);
            return messageRepository.save(message);
        }
        return null;
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    public List<Message> getArchivedMessages() {
        return messageRepository.findByArchived(true);
    }

    public Issue createIssue(Long messageId, String status) {
        Issue issue = new Issue();
        issue.setRelatedMessageId(messageId);
        issue.setStatus(status);
        issue.setCreatedAt(LocalDateTime.now());
        issue.setArchived(false);
        return issueRepository.save(issue);
    }

    public Issue markIssueResolved(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElse(null);
        if (issue != null) {
            issue.setStatus("resolved");
            return issueRepository.save(issue);
        }
        return null;
    }

    public Issue archiveIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElse(null);
        if (issue != null && "resolved".equals(issue.getStatus())) {
            issue.setArchived(true);
            return issueRepository.save(issue);
        }
        return null;
    }

    public List<Issue> getArchivedIssues() {
        return issueRepository.findByArchived(true);
    }
}