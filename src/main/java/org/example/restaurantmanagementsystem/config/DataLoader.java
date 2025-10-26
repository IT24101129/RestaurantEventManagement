package org.example.restaurantmanagementsystem.config;

import com.restaurant.entity.Staff;
import com.restaurant.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public void run(String... args) throws Exception {
        // Add sample staff members for testing
        if (staffRepository.count() == 0) {
            staffRepository.save(new Staff("John", "Doe", "john.doe@restaurant.com", "1234567890", "MANAGER"));
            staffRepository.save(new Staff("Jane", "Smith", "jane.smith@restaurant.com", "1234567891", "WAITER"));
            staffRepository.save(new Staff("Mike", "Johnson", "mike.johnson@restaurant.com", "1234567892", "CHEF"));
            staffRepository.save(new Staff("Sarah", "Wilson", "sarah.wilson@restaurant.com", "1234567893", "CASHIER"));
        }
    }
}