package com.restaurant.config;

import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

// @Component  // Disabled - using SQL scripts instead
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample tables if none exist
        if (tableRepository.count() == 0) {
            createSampleTables();
        }
    }

    private void createSampleTables() {
        try {
            // Create sample tables
            RestaurantTable table1 = new RestaurantTable();
            table1.setName("1");
            table1.setCapacity(2);
            table1.setAvailable(true);
            tableRepository.save(table1);

            RestaurantTable table2 = new RestaurantTable();
            table2.setName("2");
            table2.setCapacity(4);
            table2.setAvailable(true);
            tableRepository.save(table2);

            RestaurantTable table3 = new RestaurantTable();
            table3.setName("3");
            table3.setCapacity(4);
            table3.setAvailable(true);
            tableRepository.save(table3);

            RestaurantTable table4 = new RestaurantTable();
            table4.setName("4");
            table4.setCapacity(6);
            table4.setAvailable(true);
            tableRepository.save(table4);

            RestaurantTable table5 = new RestaurantTable();
            table5.setName("5");
            table5.setCapacity(8);
            table5.setAvailable(true);
            tableRepository.save(table5);

            RestaurantTable table6 = new RestaurantTable();
            table6.setName("6");
            table6.setCapacity(10);
            table6.setAvailable(true);
            tableRepository.save(table6);

            System.out.println("Sample tables created successfully!");
        } catch (Exception e) {
            System.err.println("Error creating sample tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
