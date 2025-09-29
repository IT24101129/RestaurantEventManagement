package com.G22_BanquetHall.restaurant.management.service;


import com.G22_BanquetHall.restaurant.management.model.BanquetHall;
import com.G22_BanquetHall.restaurant.management.repository.BanquetHallRepository;
import com.G22_BanquetHall.restaurant.management.repository.EventBookingRepository; // Add this if needed
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BanquetHallService {

    @Autowired
    private BanquetHallRepository banquetHallRepository;

    @Autowired
    private EventBookingRepository eventBookingRepository; // Inject if using conflict check

    public List<BanquetHall> getAllHalls() {
        return banquetHallRepository.findAll();
    }

    public BanquetHall getHallById(Long id) {
        BanquetHall hall = banquetHallRepository.findById(id);
        if (hall == null) {
            throw new RuntimeException("Banquet hall not found with id: " + id);
        }
        return hall;
    }

    // Update availability logic (e.g., mark as unavailable based on schedule conflicts)
    public void updateHallAvailability(Long id, Boolean available) {
        BanquetHall hall = getHallById(id);
        // Note: Since 'available' isn't a column, you might update a status or log this change
        // For now, this method is a placeholder. You could update Schedule or a status table.
        // Example: Update Schedule to reflect availability
        // This requires additional logic or a new table.
    }

    // Derive available halls based on no current or future conflicts
    public List<BanquetHall> getAvailableHalls() {
        List<BanquetHall> allHalls = getAllHalls();
        LocalDateTime now = LocalDateTime.now(); // Current time as of 02:28 PM +0530, Sep 28, 2025
        return allHalls.stream()
                .filter(hall -> {
                    // Check if the hall has no approved bookings conflicting with now
                    return !eventBookingRepository.hasConflict(
                            hall.getHallId(),
                            now,
                            now.plusHours(1), // Check next hour as an example
                            null // No exclusion
                    );
                })
                .toList();
    }
}