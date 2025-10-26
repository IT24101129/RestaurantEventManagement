package com.banquet.management.repository;

import com.banquet.management.model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for Inventory
    private final RowMapper<Inventory> inventoryRowMapper = new RowMapper<Inventory>() {
        @Override
        public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalDate lastRestockDate = null;
            if (rs.getDate("LastRestockDate") != null) {
                lastRestockDate = rs.getDate("LastRestockDate").toLocalDate();
            }

            return new Inventory(
                    rs.getInt("InventoryItemID"),
                    rs.getString("Name"),
                    rs.getInt("Quantity"),
                    lastRestockDate,
                    rs.getInt("ReorderLevel")
            );
        }
    };

    public List<Inventory> findAllInventoryItems() {
        try {
            String sql = "SELECT * FROM Inventory ORDER BY Name";
            return jdbcTemplate.query(sql, inventoryRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching inventory items: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Inventory> findLowStockItems() {
        try {
            String sql = "SELECT * FROM Inventory WHERE Quantity <= ReorderLevel AND Quantity > 0 ORDER BY Quantity ASC";
            return jdbcTemplate.query(sql, inventoryRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching low stock items: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Inventory> findOutOfStockItems() {
        try {
            String sql = "SELECT * FROM Inventory WHERE Quantity = 0 ORDER BY Name";
            return jdbcTemplate.query(sql, inventoryRowMapper);
        } catch (Exception e) {
            System.err.println("Error fetching out of stock items: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Inventory findInventoryItemById(Integer id) {
        try {
            String sql = "SELECT * FROM Inventory WHERE InventoryItemID = ?";
            return jdbcTemplate.queryForObject(sql, inventoryRowMapper, id);
        } catch (Exception e) {
            System.err.println("Error fetching inventory item by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int updateInventoryQuantity(Integer id, Integer newQuantity) {
        try {
            String sql = "UPDATE Inventory SET Quantity = ? WHERE InventoryItemID = ?";
            return jdbcTemplate.update(sql, newQuantity, id);
        } catch (Exception e) {
            System.err.println("Error updating inventory quantity: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int restockInventoryItem(Integer id, Integer quantityToAdd) {
        try {
            String sql = "UPDATE Inventory SET Quantity = Quantity + ?, LastRestockDate = GETDATE() WHERE InventoryItemID = ?";
            return jdbcTemplate.update(sql, quantityToAdd, id);
        } catch (Exception e) {
            System.err.println("Error restocking inventory item: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int addNewInventoryItem(Inventory inventory) {
        try {
            String sql = "INSERT INTO Inventory (Name, Quantity, LastRestockDate, ReorderLevel) VALUES (?, ?, ?, ?)";
            return jdbcTemplate.update(sql,
                    inventory.getName(),
                    inventory.getQuantity(),
                    inventory.getLastRestockDate() != null ? inventory.getLastRestockDate() : LocalDate.now(),
                    inventory.getReorderLevel());
        } catch (Exception e) {
            System.err.println("Error adding new inventory item: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}