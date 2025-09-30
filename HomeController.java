package com.resturant.restaurantapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String homePage(Model model, HttpSession session) {
        // Check if customer is logged in
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("customerName", session.getAttribute("customerName"));
            model.addAttribute("customerEmail", session.getAttribute("customerEmail"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        return "user-home";
    }
    
    @GetMapping("/user")
    public String userHomePage(Model model) {
        return "redirect:/";
    }
}
