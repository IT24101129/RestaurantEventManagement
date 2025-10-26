package com.banquet.management.service;

import com.banquet.management.model.Order;
import com.banquet.management.model.OrderMenuItem;
import com.banquet.management.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrders();
        // Populate order items for each order
        orders.forEach(order -> {
            List<OrderMenuItem> items = orderRepository.findOrderItemsByOrderId(order.getOrderId());
            order.setOrderItems(items);
        });
        return orders;
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findOrdersByStatus(status);
        // Populate order items for each order
        orders.forEach(order -> {
            List<OrderMenuItem> items = orderRepository.findOrderItemsByOrderId(order.getOrderId());
            order.setOrderItems(items);
        });
        return orders;
    }

    public boolean updateOrderStatus(Integer orderId, String status) {
        int rowsAffected = orderRepository.updateOrderStatus(orderId, status);
        return rowsAffected > 0;
    }

    public List<String> getAvailableStatuses() {
        return List.of("Pending", "Confirmed", "In Progress", "Ready", "Completed", "Cancelled");
    }
}