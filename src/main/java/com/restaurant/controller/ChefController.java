package com.restaurant.controller;

import com.restaurant.model.User;
import com.restaurant.service.userService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ChefController {

    private final userService userService;

    public ChefController(userService userService) {
        this.userService = userService;
    }

    @GetMapping("/chef/dashboard")
    public String chefDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
        }

        return "chef/dashboard";
    }

    // Removed conflicting mapping - /kitchen/dashboard is handled by KitchenController

    @GetMapping("/chef/orders")
    public String chefOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
        }

        return "redirect:/kitchen/orders";
    }

    @GetMapping("/chef/tasks")
    public String chefTasks(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
        }

        return "redirect:/kitchen/tasks";
    }

    @GetMapping("/chef/inventory")
    public String chefInventory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
        }

        return "redirect:/kitchen/inventory";
    }
}
