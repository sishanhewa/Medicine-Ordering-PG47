package com.example.medicineordering.controller;

import com.example.medicineordering.model.User;
import com.example.medicineordering.repository.UserRepository;
import com.example.medicineordering.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        // Always add user to model first
        model.addAttribute("user", user);

        try {
            // Get real user counts from existing Users table
            long deliveryCount = adminService.getRealUserCountByRole("DeliveryManager");
            long pharmacistCount = adminService.getRealUserCountByRole("Pharmacist");
            long financeCount = adminService.getRealUserCountByRole("FinanceManager");
            long customerCount = adminService.getRealUserCountByRole("Customer");
            long supportCount = adminService.getRealUserCountByRole("CustomerSupport");

            model.addAttribute("deliveryCount", deliveryCount);
            model.addAttribute("pharmacistCount", pharmacistCount);
            model.addAttribute("financeCount", financeCount);
            model.addAttribute("customerCount", customerCount);
            model.addAttribute("supportCount", supportCount);

        } catch (Exception e) {
            System.err.println("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("deliveryCount", 0);
            model.addAttribute("pharmacistCount", 0);
            model.addAttribute("financeCount", 0);
            model.addAttribute("customerCount", 0);
            model.addAttribute("supportCount", 0);
            model.addAttribute("error", "Could not load dashboard data. Please check database connection.");
        }

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String role,
                           @RequestParam(required = false) String search,
                           HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            List<User> users;
            String selectedRole = null;

            if (role != null && !role.trim().isEmpty()) {
                selectedRole = role;
                if (search != null && !search.trim().isEmpty()) {
                    // For now, we'll get all users and filter in memory since we don't have search in UserRepository
                    users = adminService.getAllRealUsers().stream()
                            .filter(u -> u.getRole().equals(role))
                            .filter(u -> u.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                                       u.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                       u.getEmail().toLowerCase().contains(search.toLowerCase()))
                            .toList();
                } else {
                    users = adminService.getRealUsersByRole(role);
                }
            } else {
                if (search != null && !search.trim().isEmpty()) {
                    // Filter all users by search term
                    users = adminService.getAllRealUsers().stream()
                            .filter(u -> u.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                                       u.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                       u.getEmail().toLowerCase().contains(search.toLowerCase()))
                            .toList();
                } else {
                    users = adminService.getAllRealUsers();
                }
            }

            model.addAttribute("users", users);
            model.addAttribute("selectedRole", selectedRole);
            model.addAttribute("search", search);
            model.addAttribute("roles", List.of("Customer", "DeliveryManager", "Pharmacist", "FinanceManager", "CustomerSupport"));

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("users", List.of());
            model.addAttribute("error", "Could not load users. Please try again later.");
        }

        return "admin/users-list";
    }

    @GetMapping("/users/new")
    public String showCreateForm(@RequestParam(required = false) String role,
                                HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        User newUser = new User();
        if (role != null && !role.trim().isEmpty()) {
            newUser.setRole(role);
        }

        model.addAttribute("user", newUser);
        model.addAttribute("isEdit", false);
        model.addAttribute("roles", List.of("Customer", "DeliveryManager", "Pharmacist", "FinanceManager", "CustomerSupport"));

        return "admin/user-form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user,
                           @RequestParam String password,
                           HttpSession session, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            // Hash the password before saving
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setActive(true);
            user.setCreatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            ra.addFlashAttribute("success", "User created successfully.");
            return "redirect:/admin/users?role=" + user.getRole();
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            ra.addFlashAttribute("user", user);
            return "redirect:/admin/users/new?role=" + (user.getRole() != null ? user.getRole() : "");
        }
    }

    @GetMapping("/users/{id}/edit")
    public String showEditForm(@PathVariable int id,
                              HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                ra.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/users";
            }

            model.addAttribute("user", userOpt.get());
            model.addAttribute("isEdit", true);
            model.addAttribute("roles", List.of("Customer", "DeliveryManager", "Pharmacist", "FinanceManager", "CustomerSupport"));

        } catch (Exception e) {
            System.err.println("Error loading user for edit: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Could not load user for editing.");
            return "redirect:/admin/users";
        }

        return "admin/user-form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable int id,
                            @ModelAttribute User user,
                            @RequestParam(required = false) String newPassword,
                            HttpSession session, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            // Get the existing user to preserve password if no new password provided
            Optional<User> existingUserOpt = userRepository.findById(id);
            if (!existingUserOpt.isPresent()) {
                ra.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/users";
            }

            User existingUser = existingUserOpt.get();
            user.setId(id);
            
            // Handle password update
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // Hash the new password
                user.setPasswordHash(passwordEncoder.encode(newPassword));
            } else {
                // Keep existing password
                user.setPasswordHash(existingUser.getPasswordHash());
            }
            
            // Preserve creation date
            user.setCreatedAt(existingUser.getCreatedAt());
            
            userRepository.update(user);
            ra.addFlashAttribute("success", "User updated successfully.");
            return "redirect:/admin/users?role=" + user.getRole();
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }
    }

    @GetMapping("/users/{id}/delete")
    public String showDeleteConfirmation(@PathVariable int id,
                                       HttpSession session, Model model, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                ra.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/users";
            }

            model.addAttribute("user", userOpt.get());

        } catch (Exception e) {
            System.err.println("Error loading user for deletion: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Could not load user for deletion.");
            return "redirect:/admin/users";
        }

        return "admin/confirm-delete";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id,
                           HttpSession session, RedirectAttributes ra) {
        // Check if user is logged in and has admin role
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) {
            ra.addFlashAttribute("error", "Access denied. Admin login required.");
            return "redirect:/login";
        }

        try {
            userRepository.deactivate(id);
            ra.addFlashAttribute("success", "User deactivated successfully.");
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error deactivating user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
