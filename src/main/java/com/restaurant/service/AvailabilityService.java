package com.restaurant.service;

import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    public List<RestaurantTable> getAvailableTables(LocalDateTime dateTime, Integer numberOfGuests) {
        List<RestaurantTable> allTables = tableRepository.findByAvailableTrue();
        
        // Filter tables that can accommodate the party size
        List<RestaurantTable> suitableTables = allTables.stream()
                .filter(table -> table.getCapacity() >= numberOfGuests)
                .collect(Collectors.toList());

        // Check for existing reservations at the same time
        List<Reservation> existingReservations = reservationRepository
                .findByReservationDateTimeBetween(
                    dateTime.minusHours(2), 
                    dateTime.plusHours(2)
                );

        // Filter out tables that are already reserved
        Set<Long> reservedTableIds = existingReservations.stream()
                .map(reservation -> reservation.getTable().getId())
                .collect(Collectors.toSet());

        return suitableTables.stream()
                .filter(table -> !reservedTableIds.contains(table.getId()))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableTimeSlots(LocalDate date, Integer numberOfGuests) {
        List<String> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(17, 0); // 5:00 PM
        LocalTime endTime = LocalTime.of(22, 0);   // 10:00 PM

        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            List<RestaurantTable> availableTables = getAvailableTables(dateTime, numberOfGuests);
            
            if (!availableTables.isEmpty()) {
                timeSlots.add(time.toString());
            }
        }

        return timeSlots;
    }

    public List<String> getAlternativeTimeSlots(LocalDate date, Integer numberOfGuests, String preferredTime) {
        List<String> alternatives = new ArrayList<>();
        LocalTime preferred = LocalTime.parse(preferredTime);
        
        // Check slots within 1 hour of preferred time
        for (int minutes = -60; minutes <= 60; minutes += 30) {
            if (minutes == 0) continue; // Skip the original time
            
            LocalTime alternativeTime = preferred.plusMinutes(minutes);
            if (alternativeTime.isBefore(LocalTime.of(17, 0)) || 
                alternativeTime.isAfter(LocalTime.of(22, 0))) {
                continue;
            }
            
            LocalDateTime dateTime = LocalDateTime.of(date, alternativeTime);
            List<RestaurantTable> availableTables = getAvailableTables(dateTime, numberOfGuests);
            
            if (!availableTables.isEmpty()) {
                alternatives.add(alternativeTime.toString());
            }
        }

        return alternatives;
    }

    public Map<String, Object> checkAvailability(LocalDateTime dateTime, Integer numberOfGuests) {
        Map<String, Object> result = new HashMap<>();
        
        List<RestaurantTable> availableTables = getAvailableTables(dateTime, numberOfGuests);
        
        result.put("isAvailable", !availableTables.isEmpty());
        result.put("availableTables", availableTables);
        result.put("totalCapacity", availableTables.stream()
                .mapToInt(RestaurantTable::getCapacity)
                .sum());
        
        if (availableTables.isEmpty()) {
            // Suggest alternatives
            List<String> alternatives = getAlternativeTimeSlots(
                dateTime.toLocalDate(), 
                numberOfGuests, 
                dateTime.toLocalTime().toString()
            );
            result.put("alternatives", alternatives);
            result.put("suggestion", "No tables available at this time. Consider these alternatives: " + 
                String.join(", ", alternatives));
        }
        
        return result;
    }

    public List<RestaurantTable> getTablesForGroupReservation(Integer numberOfGuests) {
        List<RestaurantTable> allTables = tableRepository.findByAvailableTrue();
        
        if (numberOfGuests <= 10) {
            // Try to find a single large table
            return allTables.stream()
                    .filter(table -> table.getCapacity() >= numberOfGuests)
                    .sorted(Comparator.comparing(RestaurantTable::getCapacity))
                    .collect(Collectors.toList());
        } else {
            // For large groups, suggest table combinations
            return allTables.stream()
                    .filter(table -> table.getCapacity() >= 4) // Minimum table size for groups
                    .sorted(Comparator.comparing(RestaurantTable::getCapacity).reversed())
                    .collect(Collectors.toList());
        }
    }

    public boolean detectConflictingBookings(LocalDateTime dateTime, Integer numberOfGuests) {
        List<Reservation> nearbyReservations = reservationRepository
                .findByReservationDateTimeBetween(
                    dateTime.minusHours(1), 
                    dateTime.plusHours(1)
                );
        
        // Check if there are too many reservations around the same time
        return nearbyReservations.size() >= 3; // Threshold for conflict detection
    }

    public Map<String, Object> getAvailabilitySummary(LocalDate date) {
        Map<String, Object> summary = new HashMap<>();
        
        List<String> timeSlots = Arrays.asList("17:00", "17:30", "18:00", "18:30", "19:00", 
                                              "19:30", "20:00", "20:30", "21:00", "21:30");
        
        Map<String, Integer> availability = new HashMap<>();
        for (String time : timeSlots) {
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(time));
            List<RestaurantTable> available = getAvailableTables(dateTime, 4); // Default to 4 guests
            availability.put(time, available.size());
        }
        
        summary.put("date", date);
        summary.put("availability", availability);
        summary.put("totalTables", tableRepository.findByAvailableTrue().size());
        
        return summary;
    }
}
