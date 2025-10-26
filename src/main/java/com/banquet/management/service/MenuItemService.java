package com.banquet.management.service;

import com.banquet.management.model.MenuItem;
import com.banquet.management.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAllMenuItems();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findAvailableMenuItems();
    }

    public List<MenuItem> getUnavailableMenuItems() {
        return menuItemRepository.findUnavailableMenuItems();
    }

    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findMenuItemById(id);
    }

    public boolean updateMenuItem(MenuItem menuItem) {
        int rowsAffected = menuItemRepository.updateMenuItem(menuItem);
        return rowsAffected > 0;
    }

    public boolean updateAvailability(Integer itemId, String availability) {
        int rowsAffected = menuItemRepository.updateMenuItemAvailability(itemId, availability);
        return rowsAffected > 0;
    }

    public boolean addNewMenuItem(MenuItem menuItem) {
        int rowsAffected = menuItemRepository.addNewMenuItem(menuItem);
        return rowsAffected > 0;
    }

    public boolean deleteMenuItem(Integer itemId) {
        int rowsAffected = menuItemRepository.deleteMenuItem(itemId);
        return rowsAffected > 0;
    }

    // Get menu summary statistics
    public MenuSummary getMenuSummary() {
        List<MenuItem> allItems = getAllMenuItems();
        long totalItems = allItems.size();
        long availableItems = allItems.stream().filter(MenuItem::isAvailable).count();
        long unavailableItems = totalItems - availableItems;

        return new MenuSummary(totalItems, availableItems, unavailableItems);
    }

    // Inner class for menu summary
    public static class MenuSummary {
        private final long totalItems;
        private final long availableItems;
        private final long unavailableItems;

        public MenuSummary(long totalItems, long availableItems, long unavailableItems) {
            this.totalItems = totalItems;
            this.availableItems = availableItems;
            this.unavailableItems = unavailableItems;
        }

        // Getters
        public long getTotalItems() { return totalItems; }
        public long getAvailableItems() { return availableItems; }
        public long getUnavailableItems() { return unavailableItems; }
    }
}