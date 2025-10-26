package com.example.medicineordering.service;

import com.example.medicineordering.model.Issue;
import com.example.medicineordering.model.Message;
import com.example.medicineordering.repository.IssueRepository;
import com.example.medicineordering.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Optional<Message> message = messageRepository.findById(id);
        return message.orElse(null);
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
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
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
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setStatus("resolved");
            return issueRepository.save(issue);
        }
        return null;
    }

    public Issue archiveIssue(Long issueId) {
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            if ("resolved".equals(issue.getStatus())) {
                issue.setArchived(true);
                return issueRepository.save(issue);
            }
        }
        return null;
    }

    public List<Issue> getArchivedIssues() {
        return issueRepository.findByArchived(true);
    }

    public List<Message> getMessagesByReceiver(String receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    public List<Message> getMessagesBySender(String sender) {
        return messageRepository.findBySender(sender);
    }

    // Conversation support methods
    public List<Message> getConversationsBySender(String sender) {
        return messageRepository.findConversationsBySender(sender);
    }

    public List<Message> getConversationsByReceiver(String receiver) {
        return messageRepository.findConversationsByReceiver(receiver);
    }

    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public List<Message> getRepliesToMessage(Long parentMessageId) {
        return messageRepository.findRepliesByParentMessageId(parentMessageId);
    }

    public Message saveMessageWithConversation(Message message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        if (message.getStatus() == null) {
            message.setStatus("unread");
        }
        return messageRepository.saveWithConversation(message);
    }

    public Message replyToMessage(Long parentMessageId, String content, String sender, String receiver) {
        // Get the parent message to inherit conversation ID
        Optional<Message> parentMessageOpt = messageRepository.findById(parentMessageId);
        if (!parentMessageOpt.isPresent()) {
            throw new IllegalArgumentException("Parent message not found");
        }
        
        Message parentMessage = parentMessageOpt.get();
        Message reply = new Message();
        reply.setContent(content);
        reply.setSender(sender);
        reply.setReceiver(receiver);
        reply.setTimestamp(LocalDateTime.now());
        reply.setStatus("unread");
        reply.setArchived(false);
        reply.setConversationId(parentMessage.getConversationId());
        reply.setParentMessageId(parentMessageId);
        
        return messageRepository.saveWithConversation(reply);
    }
}
