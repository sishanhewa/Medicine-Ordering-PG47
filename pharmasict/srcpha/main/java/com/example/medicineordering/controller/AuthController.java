package com.example.medicineordering.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_PHARMACIST".equals(role)) {
                return "redirect:/pharmacist/dashboard";
            }
            if ("ROLE_CUSTOMER".equals(role)) {
                return "redirect:/customer";
            }
        }
        return "redirect:/login";
    }
}


