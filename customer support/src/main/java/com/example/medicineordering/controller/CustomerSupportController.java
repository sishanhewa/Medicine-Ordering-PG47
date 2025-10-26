package com.example.medicineordering.controller;

import com.example.medicineordering.model.Message;
import com.example.medicineordering.model.Notification;
import com.example.medicineordering.service.CustomerSupportService;
import com.example.medicineordering.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String showDashboard(Model model) {
        model.addAttribute("messages", supportService.getAllMessages());
        model.addAttribute("unreadMessages", supportService.getMessagesByStatus("unread").size());
        model.addAttribute("issues", supportService.getArchivedIssues());
        return "support/dashboard";
    }

    // ---------------- Messages ----------------
    @GetMapping("/messages")
    public String showMessages(Model model) {
        // Show only non-archived messages
        List<Message> messages = supportService.getAllMessages()
                .stream()
                .filter(m -> !Boolean.TRUE.equals(m.getArchived()))
                .toList();
        model.addAttribute("messages", messages);
        return "support/messages";
    }

    @GetMapping("/messages/{id}")
    public String showMessageDetail(@PathVariable Long id, Model model) {
        Message message = supportService.getMessageById(id);
        model.addAttribute("message", message);
        return "support/message-detail";
    }

    @PostMapping("/messages")
    public String respondToMessage(@ModelAttribute Message message) {
        supportService.saveMessage(message);
        supportService.createIssue(message.getId(), "open");
        return "redirect:/support/messages";
    }

    @PostMapping("/messages/{id}/archive")
    public String archiveMessage(@PathVariable Long id) {
        supportService.archiveMessage(id);
        return "redirect:/support/messages";
    }

    @GetMapping("/archived-messages")
    public String showArchivedMessages(Model model) {
        List<Message> archivedMessages = supportService.getArchivedMessages();
        model.addAttribute("archivedMessages", archivedMessages != null ? archivedMessages : new ArrayList<>());
        return "support/archived-messages";
    }

    @GetMapping("/messages/{id}/delete-confirm")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        Message message = supportService.getMessageById(id);
        model.addAttribute("message", message != null ? message : new Message());
        return "support/delete-confirm";
    }

    @PostMapping("/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id) {
        supportService.deleteMessage(id);
        return "redirect:/support/messages";
    }

    @GetMapping("/messages/{id}/delete-cancel")
    public String cancelDelete(@PathVariable Long id) {
        return "redirect:/support/messages";
    }

    // ---------------- Notifications ----------------
    @GetMapping("/notifications")
    public String showNotificationForm(Model model) {
        model.addAttribute("notification", new Notification());
        return "support/notifications";
    }

    @PostMapping("/notifications")
    public String sendNotification(@ModelAttribute Notification notification) {
        notificationService.sendNotification(notification);
        return "redirect:/support/dashboard";
    }

    @GetMapping("/view-notifications")
    public String showNotifications(Model model) {
        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications != null ? notifications : new ArrayList<>());
        return "support/view-notifications";
    }

    @GetMapping("/notifications/{id}/edit")
    public String showEditNotificationForm(@PathVariable Long id, Model model) {
        Notification notification = notificationService.getNotificationById(id);
        model.addAttribute("notification", notification != null ? notification : new Notification());
        return "support/edit-notification";
    }

    @PostMapping("/notifications/{id}/update")
    public String updateNotification(@PathVariable Long id, @ModelAttribute Notification notification) {
        if (notification != null) {
            notification.setId(id);
            notificationService.updateNotification(notification);
        }
        return "redirect:/support/view-notifications";
    }

    // ---------------- Issues ----------------
    @PostMapping("/issues/{id}/resolve")
    public String resolveIssue(@PathVariable Long id) {
        supportService.markIssueResolved(id);
        return "redirect:/support/dashboard";
    }

    @PostMapping("/issues/{id}/archive")
    public String archiveIssue(@PathVariable Long id) {
        supportService.archiveIssue(id);
        return "redirect:/support/dashboard";
    }

    @GetMapping("/archive")
    public String showArchive(Model model) {
        model.addAttribute("archivedIssues", supportService.getArchivedIssues());
        return "support/archive";
    }

    // ---------------- Root Redirect ----------------
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/support/dashboard";
    }
}
