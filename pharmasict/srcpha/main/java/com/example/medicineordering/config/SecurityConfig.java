package com.example.medicineordering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/uploads/**", "/", "/manager/**", "/customer/prescription/file/**").permitAll()
                        .requestMatchers("/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/post-login", true)
                        .permitAll()
                )
                .logout(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails pharmacist = User.withDefaultPasswordEncoder()
                .username("pharmacist")
                .password("pharma123")
                .roles("PHARMACIST")
                .build();
        UserDetails customer = User.withDefaultPasswordEncoder()
                .username("customer")
                .password("cust123")
                .roles("CUSTOMER")
                .build();
        return new InMemoryUserDetailsManager(pharmacist, customer);
    }
}


