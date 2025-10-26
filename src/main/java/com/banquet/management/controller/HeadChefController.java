package com.banquet.management.controller;

import com.banquet.management.model.Order;
import com.banquet.management.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/headchef")
public class HeadChefController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> allOrders = orderService.getAllOrders();
        model.addAttribute("orders", allOrders);
        model.addAttribute("statuses", orderService.getAvailableStatuses());
        return "headchef/dashboard";
    }

    @GetMapping("/orders")
    public String getOrdersByStatus(@RequestParam(required = false) String status, Model model) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", orderService.getAvailableStatuses());
        model.addAttribute("selectedStatus", status);
        return "headchef/dashboard";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable Integer orderId,
                                    @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/headchef/dashboard";
    }
}