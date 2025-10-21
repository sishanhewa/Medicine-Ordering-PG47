package com.example.medicineordering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow public access to customer dashboard and related pages
                .requestMatchers("/", "/customer/dashboard", "/customer/home").permitAll()
                // Allow access to login and registration
                .requestMatchers("/login", "/register", "/register/**").permitAll()
                // Allow access to static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Allow guest access to cart (view and add items)
                .requestMatchers("/customer/cart", "/customer/cart/add").permitAll()
                // Allow access to manager and driver pages (we handle auth manually in controllers)
                .requestMatchers("/manager/**", "/driver/**", "/admin/**").permitAll()
                // Allow access to pharmacist pages (we handle auth manually in controllers)
                .requestMatchers("/pharmacist/**").permitAll()
                // Allow access to customer pages (we handle auth manually in controllers)
                .requestMatchers("/customer/**").permitAll()
                // Allow all other requests
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity

        return http.build();
    }
}
