package com.example.medicineordering.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Home page - redirect to customer dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/customer/dashboard";
    }
}
