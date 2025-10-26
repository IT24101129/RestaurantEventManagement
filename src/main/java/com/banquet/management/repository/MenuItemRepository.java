package com.banquet.management.repository;

import com.banquet.management.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MenuItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for MenuItem
    private final RowMapper<MenuItem> menuItemRowMapper = new RowMapper<MenuItem>() {
        @Override
        public MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MenuItem(
                    rs.getInt("ItemID"),
                    rs.getString("Name"),
                    rs.getString("Ingredients"),
                    rs.getString("Description"),
                    rs.getBigDecimal("Price"),
                    rs.getString("Availability")
            );
        }
    };

    public List<MenuItem> findAllMenuItems() {
        try {
            String sql = "SELECT * FROM MenuItem ORDER BY Name";
            return jdbcTemplate.query(sql, menuItemRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching menu items: " + e.getMessage());
            return List.of();
        }
    }

    public List<MenuItem> findAvailableMenuItems() {
        try {
            String sql = "SELECT * FROM MenuItem WHERE Availability = 'Available' ORDER BY Name";
            return jdbcTemplate.query(sql, menuItemRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching available menu items: " + e.getMessage());
            return List.of();
        }
    }

    public List<MenuItem> findUnavailableMenuItems() {
        try {
            String sql = "SELECT * FROM MenuItem WHERE Availability != 'Available' ORDER BY Name";
            return jdbcTemplate.query(sql, menuItemRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching unavailable menu items: " + e.getMessage());
            return List.of();
        }
    }

    public MenuItem findMenuItemById(Integer id) {
        try {
            String sql = "SELECT * FROM MenuItem WHERE ItemID = ?";
            return jdbcTemplate.queryForObject(sql, menuItemRowMapper, id);
        } catch (Exception e) {
            System.err.println("Error fetching menu item by ID: " + e.getMessage());
            return null;
        }
    }

    public int updateMenuItem(MenuItem menuItem) {
        try {
            String sql = "UPDATE MenuItem SET Name = ?, Ingredients = ?, Description = ?, Price = ?, Availability = ? WHERE ItemID = ?";
            return jdbcTemplate.update(sql,
                    menuItem.getName(),
                    menuItem.getIngredients(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItem.getAvailability(),
                    menuItem.getItemId());
        } catch (Exception e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            return 0;
        }
    }

    public int updateMenuItemAvailability(Integer itemId, String availability) {
        try {
            String sql = "UPDATE MenuItem SET Availability = ? WHERE ItemID = ?";
            return jdbcTemplate.update(sql, availability, itemId);
        } catch (Exception e) {
            System.err.println("Error updating menu item availability: " + e.getMessage());
            return 0;
        }
    }

    public int addNewMenuItem(MenuItem menuItem) {
        try {
            String sql = "INSERT INTO MenuItem (Name, Ingredients, Description, Price, Availability) VALUES (?, ?, ?, ?, ?)";
            return jdbcTemplate.update(sql,
                    menuItem.getName(),
                    menuItem.getIngredients(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItem.getAvailability());
        } catch (Exception e) {
            System.err.println("Error adding new menu item: " + e.getMessage());
            return 0;
        }
    }

    public int deleteMenuItem(Integer itemId) {
        try {
            // First check if the item is used in any orders
            String checkSql = "SELECT COUNT(*) FROM Order_MenuItem WHERE ItemID = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, itemId);

            if (count != null && count > 0) {
                // If item is used in orders, set it as unavailable instead of deleting
                String updateSql = "UPDATE MenuItem SET Availability = 'Unavailable' WHERE ItemID = ?";
                return jdbcTemplate.update(updateSql, itemId);
            } else {
                // If item is not used in orders, delete it
                String deleteSql = "DELETE FROM MenuItem WHERE ItemID = ?";
                return jdbcTemplate.update(deleteSql, itemId);
            }
        } catch (Exception e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
            return 0;
        }
    }
}