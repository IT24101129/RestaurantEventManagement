package com.banquet.management.repository;

import com.banquet.management.model.Order;
import com.banquet.management.model.OrderMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for Order with null handling
    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order(
                    rs.getInt("OrderID"),
                    rs.getDate("OrderDate") != null ? rs.getDate("OrderDate").toLocalDate() : LocalDate.now(),
                    rs.getString("Type"),
                    rs.getString("DietaryNotes"),
                    rs.getBigDecimal("TotalCost"),
                    rs.getString("Status"),
                    rs.getInt("UserID")
            );

            // Ensure no null values
            if (order.getType() == null) order.setType("");
            if (order.getDietaryNotes() == null) order.setDietaryNotes("");
            if (order.getStatus() == null) order.setStatus("Pending");

            return order;
        }
    };

    // RowMapper for OrderMenuItem with null handling
    private final RowMapper<OrderMenuItem> orderMenuItemRowMapper = new RowMapper<OrderMenuItem>() {
        @Override
        public OrderMenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderMenuItem item = new OrderMenuItem(
                    rs.getInt("OrderID"),
                    rs.getInt("ItemID"),
                    rs.getInt("Quantity")
            );
            item.setItemName(rs.getString("Name") != null ? rs.getString("Name") : "Unknown Item");
            item.setDescription(rs.getString("Description") != null ? rs.getString("Description") : "");
            return item;
        }
    };

    public List<Order> findAllOrders() {
        try {
            String sql = "SELECT * FROM [Order] ORDER BY OrderDate DESC, OrderID DESC";
            return jdbcTemplate.query(sql, orderRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Order> findOrdersByStatus(String status) {
        try {
            String sql = "SELECT * FROM [Order] WHERE Status = ? ORDER BY OrderDate DESC";
            return jdbcTemplate.query(sql, orderRowMapper, status);
        } catch (Exception e) {
            System.err.println("Error fetching orders by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<OrderMenuItem> findOrderItemsByOrderId(Integer orderId) {
        try {
            String sql = """
                SELECT omi.OrderID, omi.ItemID, omi.Quantity, mi.Name, mi.Description 
                FROM Order_MenuItem omi 
                JOIN MenuItem mi ON omi.ItemID = mi.ItemID 
                WHERE omi.OrderID = ?
                """;
            return jdbcTemplate.query(sql, orderMenuItemRowMapper, orderId);
        } catch (Exception e) {
            System.err.println("Error fetching order items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int updateOrderStatus(Integer orderId, String status) {
        try {
            String sql = "UPDATE [Order] SET Status = ? WHERE OrderID = ?";
            return jdbcTemplate.update(sql, status, orderId);
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return 0;
        }
    }
}