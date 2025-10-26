package com.banquet.management.controller;

import com.banquet.management.model.Inventory;
import com.banquet.management.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/headchef")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/inventory")
    public String inventoryDashboard(Model model) {
        List<Inventory> inventoryItems = inventoryService.getAllInventoryItems();
        InventoryService.InventorySummary summary = inventoryService.getInventorySummary();

        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("summary", summary);
        model.addAttribute("lowStockItems", inventoryService.getLowStockItems());
        model.addAttribute("outOfStockItems", inventoryService.getOutOfStockItems());

        return "headchef/inventory";
    }

    @GetMapping("/inventory/low-stock")
    public String lowStockItems(Model model) {
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        model.addAttribute("inventoryItems", lowStockItems);
        model.addAttribute("pageTitle", "Low Stock Items");
        model.addAttribute("summary", inventoryService.getInventorySummary());
        return "headchef/inventory";
    }

    @GetMapping("/inventory/out-of-stock")
    public String outOfStockItems(Model model) {
        List<Inventory> outOfStockItems = inventoryService.getOutOfStockItems();
        model.addAttribute("inventoryItems", outOfStockItems);
        model.addAttribute("pageTitle", "Out of Stock Items");
        model.addAttribute("summary", inventoryService.getInventorySummary());
        return "headchef/inventory";
    }

    // FIXED: Changed parameter name from 'quantity' to 'newQuantity'
    @PostMapping("/inventory/update-quantity")
    public String updateQuantity(@RequestParam("itemId") Integer itemId,
                                 @RequestParam("quantity") Integer newQuantity, // Changed parameter name
                                 RedirectAttributes redirectAttributes) {
        boolean success = inventoryService.updateItemQuantity(itemId, newQuantity);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Quantity updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update quantity.");
        }
        return "redirect:/headchef/inventory";
    }

    // FIXED: Changed parameter name from 'quantity' to 'quantityToAdd'
    @PostMapping("/inventory/restock")
    public String restockItem(@RequestParam("itemId") Integer itemId,
                              @RequestParam("quantity") Integer quantityToAdd, // Changed parameter name
                              RedirectAttributes redirectAttributes) {
        boolean success = inventoryService.restockItem(itemId, quantityToAdd);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Item restocked successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to restock item.");
        }
        return "redirect:/headchef/inventory";
    }

    @PostMapping("/inventory/add")
    public String addNewItem(@ModelAttribute Inventory inventory,
                             RedirectAttributes redirectAttributes) {
        boolean success = inventoryService.addNewItem(inventory);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "New item added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add new item.");
        }
        return "redirect:/headchef/inventory";
    }
}