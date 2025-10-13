package com.restaurant.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/new")
    public String newOrder(@RequestParam(value = "items", required = false) String itemsJson, Model model) {
        try {
            List<OrderItemDto> items = (itemsJson == null || itemsJson.isBlank())
                    ? Collections.emptyList()
                    : objectMapper.readValue(itemsJson, new TypeReference<List<OrderItemDto>>() {});
            double total = items.stream().mapToDouble(i -> i.price * i.quantity).sum();
            model.addAttribute("items", items);
            model.addAttribute("total", total);
        } catch (Exception e) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("total", 0.0);
            model.addAttribute("error", "Could not read order items.");
        }
        return "orders/checkout";
    }

    public static class OrderItemDto {
        public String name;
        public double price;
        public int quantity;
        public String prepTime;

        public OrderItemDto() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getPrepTime() { return prepTime; }
        public void setPrepTime(String prepTime) { this.prepTime = prepTime; }
    }
}
