package com.example.medicineordering.controller;

import com.example.medicineordering.model.Message;
import com.example.medicineordering.model.Notification;
import com.example.medicineordering.model.User;
import com.example.medicineordering.service.CustomerSupportService;
import com.example.medicineordering.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/support")
public class CustomerSupportController {

    @Autowired
    private CustomerSupportService supportService;

    @Autowired
    private NotificationService notificationService;

    // ---------------- Dashboard ----------------
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            model.addAttribute("messages", supportService.getAllMessages());
            model.addAttribute("unreadMessages", supportService.getMessagesByStatus("unread").size());
            model.addAttribute("issues", supportService.getArchivedIssues());
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("messages", new java.util.ArrayList<>());
            model.addAttribute("unreadMessages", 0);
            model.addAttribute("issues", new java.util.ArrayList<>());
            model.addAttribute("error", "Some data could not be loaded. Tables may not exist yet.");
        }
        return "support/dashboard";
    }

    // ---------------- Messages ----------------
    @GetMapping("/messages")
    public String showMessages(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        // Show only non-archived messages
        List<Message> messages = supportService.getAllMessages()
                .stream()
                .filter(m -> !Boolean.TRUE.equals(m.getArchived()))
                .toList();
        model.addAttribute("messages", messages);
        return "support/messages";
    }

    @GetMapping("/messages/{id}")
    public String showMessageDetail(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            Message message = supportService.getMessageById(id);
            if (message == null) {
                ra.addFlashAttribute("error", "Message not found.");
                return "redirect:/support/messages";
            }
            
            // Get the full conversation for this message
            List<Message> conversationMessages = new ArrayList<>();
            if (message.getConversationId() != null) {
                conversationMessages = supportService.getConversationMessages(message.getConversationId());
            } else {
                // If no conversationId, just show this message
                conversationMessages.add(message);
            }
            
            model.addAttribute("message", message);
            model.addAttribute("conversationMessages", conversationMessages);
        } catch (Exception e) {
            System.err.println("Error loading message detail: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Could not load message details.");
            return "redirect:/support/messages";
        }
        return "support/message-detail";
    }

    @PostMapping("/messages")
    public String respondToMessage(@ModelAttribute Message message, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            supportService.saveMessageWithConversation(message);
            ra.addFlashAttribute("success", "Message sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error sending message: " + e.getMessage());
        }
        return "redirect:/support/messages";
    }

    @PostMapping("/messages/{id}/reply")
    public String replyToMessage(@PathVariable Long id, 
                               @RequestParam("content") String content,
                               HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Reply content cannot be empty.");
            return "redirect:/support/messages/" + id;
        }

        try {
            // Get the original message to find the customer
            Message originalMessage = supportService.getMessageById(id);
            if (originalMessage == null) {
                ra.addFlashAttribute("error", "Original message not found.");
                return "redirect:/support/messages";
            }

            // Find the customer email (first message sender)
            String customerEmail = originalMessage.getSender();
            
            // Create reply
            supportService.replyToMessage(id, content.trim(), 
                "support@crystalcare.com", customerEmail);
            ra.addFlashAttribute("success", "Your reply has been sent to the customer!");
        } catch (Exception e) {
            System.err.println("Error sending reply: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error sending reply: " + e.getMessage());
        }

        return "redirect:/support/messages/" + id;
    }

    @PostMapping("/messages/{id}/archive")
    public String archiveMessage(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        supportService.archiveMessage(id);
        ra.addFlashAttribute("success", "Message archived successfully.");
        return "redirect:/support/messages";
    }

    @GetMapping("/archived-messages")
    public String showArchivedMessages(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        List<Message> archivedMessages = supportService.getArchivedMessages();
        model.addAttribute("archivedMessages", archivedMessages != null ? archivedMessages : new ArrayList<>());
        return "support/archived-messages";
    }

    @GetMapping("/messages/{id}/delete-confirm")
    public String showDeleteConfirm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        Message message = supportService.getMessageById(id);
        model.addAttribute("message", message != null ? message : new Message());
        return "support/delete-confirm";
    }

    @PostMapping("/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        supportService.deleteMessage(id);
        ra.addFlashAttribute("success", "Message deleted successfully.");
        return "redirect:/support/messages";
    }

    @GetMapping("/messages/{id}/delete-cancel")
    public String cancelDelete(@PathVariable Long id) {
        return "redirect:/support/messages";
    }

    // ---------------- Notifications ----------------
    @GetMapping("/notifications")
    public String showNotificationForm(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        model.addAttribute("notification", new Notification());
        return "support/notifications";
    }

    @PostMapping("/notifications")
    public String sendNotification(@ModelAttribute Notification notification, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            notificationService.sendNotification(notification);
            ra.addFlashAttribute("success", "Notification sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to send notification. Database tables may not exist. Please contact administrator.");
        }
        return "redirect:/support/dashboard";
    }

    @GetMapping("/view-notifications")
    public String showNotifications(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications != null ? notifications : new ArrayList<>());
        return "support/view-notifications";
    }

    @GetMapping("/notifications/{id}/edit")
    public String showEditNotificationForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        Notification notification = notificationService.getNotificationById(id);
        model.addAttribute("notification", notification != null ? notification : new Notification());
        return "support/edit-notification";
    }

    @PostMapping("/notifications/{id}/update")
    public String updateNotification(@PathVariable Long id, @ModelAttribute Notification notification, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        if (notification != null) {
            notification.setId(id);
            notificationService.updateNotification(notification);
            ra.addFlashAttribute("success", "Notification updated successfully.");
        }
        return "redirect:/support/view-notifications";
    }

    // ---------------- Issues ----------------
    @PostMapping("/issues/{id}/resolve")
    public String resolveIssue(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        supportService.markIssueResolved(id);
        ra.addFlashAttribute("success", "Issue resolved successfully.");
        return "redirect:/support/dashboard";
    }

    @PostMapping("/issues/{id}/archive")
    public String archiveIssue(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        supportService.archiveIssue(id);
        ra.addFlashAttribute("success", "Issue archived successfully.");
        return "redirect:/support/dashboard";
    }

    @GetMapping("/archive")
    public String showArchive(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        model.addAttribute("archivedIssues", supportService.getArchivedIssues());
        return "support/archive";
    }

    // ---------------- Root Redirect ----------------
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/support/dashboard";
    }

    // ---------------- Test Endpoint ----------------
    @GetMapping("/test-tables")
    public String testTables(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            return "redirect:/login";
        }

        try {
            // Test if tables exist by trying to query them
            supportService.getAllMessages();
            model.addAttribute("messagesTable", "Messages table exists and accessible");
        } catch (Exception e) {
            model.addAttribute("messagesTable", "Messages table error: " + e.getMessage());
        }

        try {
            notificationService.getAllNotifications();
            model.addAttribute("notificationsTable", "Notifications table exists and accessible");
        } catch (Exception e) {
            model.addAttribute("notificationsTable", "Notifications table error: " + e.getMessage());
        }

        return "support/test-tables";
    }

    // ---------------- Conversation Support ----------------
    @GetMapping("/conversations")
    public String showConversations(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            // Get all conversations (distinct conversation threads)
            List<Message> conversations = supportService.getConversationsByReceiver("support@crystalcare.com");
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            System.err.println("Error loading conversations: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("conversations", new ArrayList<>());
            model.addAttribute("error", "Could not load conversations. Please try again later.");
        }
        return "support/conversations";
    }

    @GetMapping("/conversation/{conversationId}")
    public String viewConversation(@PathVariable Long conversationId, HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        try {
            // Get conversation messages
            List<Message> messages = supportService.getConversationMessages(conversationId);
            model.addAttribute("messages", messages);
            model.addAttribute("conversationId", conversationId);
        } catch (Exception e) {
            System.err.println("Error loading conversation: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("messages", new ArrayList<>());
            model.addAttribute("error", "Could not load conversation. Please try again later.");
        }
        return "support/conversation-detail";
    }

    @PostMapping("/conversation/{conversationId}/reply")
    public String replyToConversation(@PathVariable Long conversationId,
                                    @RequestParam("content") String content,
                                    HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("CustomerSupport")) {
            ra.addFlashAttribute("error", "Access denied. Customer Support login required.");
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Reply content cannot be empty.");
            return "redirect:/support/conversation/" + conversationId;
        }

        try {
            // Get the original message in the conversation to find the customer
            List<Message> conversationMessages = supportService.getConversationMessages(conversationId);
            if (conversationMessages.isEmpty()) {
                ra.addFlashAttribute("error", "Conversation not found.");
                return "redirect:/support/conversations";
            }

            // Find the customer email (first message sender)
            String customerEmail = conversationMessages.get(0).getSender();
            
            // Create reply
            supportService.replyToMessage(conversationId, content.trim(), 
                "support@crystalcare.com", customerEmail);
            ra.addFlashAttribute("success", "Your reply has been sent to the customer!");
        } catch (Exception e) {
            System.err.println("Error sending reply: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error sending reply: " + e.getMessage());
        }

        return "redirect:/support/conversation/" + conversationId;
    }
}
