package com.example.medicineordering.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, RedirectAttributes redirectAttributes) {
        // Don't log static resource exceptions as they're not real errors
        if (!(ex instanceof NoResourceFoundException)) {
            System.err.println("Global exception handler caught: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "An unexpected error occurred. Please try again.");
        
        return modelAndView;
    }
}
