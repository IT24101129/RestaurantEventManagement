package com.banquet.management.controller;

import com.banquet.management.model.MenuItem;
import com.banquet.management.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/headchef")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping("/menu")
    public String menuManagement(Model model) {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        MenuItemService.MenuSummary summary = menuItemService.getMenuSummary();

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("summary", summary);
        model.addAttribute("availableItems", menuItemService.getAvailableMenuItems());
        model.addAttribute("unavailableItems", menuItemService.getUnavailableMenuItems());

        return "headchef/menu";
    }

    @GetMapping("/menu/available")
    public String availableMenuItems(Model model) {
        List<MenuItem> availableItems = menuItemService.getAvailableMenuItems();
        model.addAttribute("menuItems", availableItems);
        model.addAttribute("pageTitle", "Available Menu Items");
        model.addAttribute("summary", menuItemService.getMenuSummary());
        return "headchef/menu";
    }

    @GetMapping("/menu/unavailable")
    public String unavailableMenuItems(Model model) {
        List<MenuItem> unavailableItems = menuItemService.getUnavailableMenuItems();
        model.addAttribute("menuItems", unavailableItems);
        model.addAttribute("pageTitle", "Unavailable Menu Items");
        model.addAttribute("summary", menuItemService.getMenuSummary());
        return "headchef/menu";
    }

    @PostMapping("/menu/add")
    public String addNewMenuItem(@ModelAttribute MenuItem menuItem,
                                 RedirectAttributes redirectAttributes) {
        boolean success = menuItemService.addNewMenuItem(menuItem);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add menu item.");
        }
        return "redirect:/headchef/menu";
    }

    @PostMapping("/menu/update")
    public String updateMenuItem(@ModelAttribute MenuItem menuItem,
                                 RedirectAttributes redirectAttributes) {
        boolean success = menuItemService.updateMenuItem(menuItem);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Menu item updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update menu item.");
        }
        return "redirect:/headchef/menu";
    }

    @PostMapping("/menu/update-availability")
    public String updateAvailability(@RequestParam Integer itemId,
                                     @RequestParam String availability,
                                     RedirectAttributes redirectAttributes) {
        boolean success = menuItemService.updateAvailability(itemId, availability);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Availability updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update availability.");
        }
        return "redirect:/headchef/menu";
    }

    @PostMapping("/menu/delete")
    public String deleteMenuItem(@RequestParam Integer itemId,
                                 RedirectAttributes redirectAttributes) {
        boolean success = menuItemService.deleteMenuItem(itemId);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete menu item.");
        }
        return "redirect:/headchef/menu";
    }
}