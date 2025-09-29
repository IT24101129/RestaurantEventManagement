package com.G22_BanquetHall.restaurant.management.service;

import com.G22_BanquetHall.restaurant.management.model.EventBooking;
import com.G22_BanquetHall.restaurant.management.repository.EventBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventBookingService {

    @Autowired
    private EventBookingRepository eventBookingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE/UPDATE - Save or update booking
    public EventBooking saveBooking(EventBooking booking) {
        // Set default status if not provided
        if (booking.getStatus() == null) {
            booking.setStatus("PENDING");
        }

        // Check for scheduling conflicts
        if (hasSchedulingConflict(booking)) {
            throw new RuntimeException("Scheduling conflict: The hall is already booked for the selected time");
        }

        // Save the booking and retrieve the generated EventID
        eventBookingRepository.save(booking);
        Long generatedId = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Long.class);
        booking.setEventId(generatedId); // Set the generated ID

        return booking;
    }

    // READ - All bookings
    public List<EventBooking> getAllBookings() {
        return eventBookingRepository.findAll();
    }

    // READ - Pending bookings for approval
    public List<EventBooking> getPendingBookings() {
        return eventBookingRepository.findPendingBookings();
    }

    // READ - Upcoming events
    public List<EventBooking> getUpcomingEvents() {
        return eventBookingRepository.findUpcomingEvents();
    }

    // READ - Single booking
    public EventBooking getBookingById(Long id) {
        EventBooking booking = eventBookingRepository.findById(id);
        if (booking == null) {
            throw new RuntimeException("Booking not found with id: " + id);
        }
        return booking;
    }

    // UPDATE - Approve booking
    public EventBooking approveBooking(Long id) {
        EventBooking booking = getBookingById(id);

        // Check for conflicts before approving
        if (hasSchedulingConflict(booking)) {
            throw new RuntimeException("Cannot approve: Scheduling conflict detected");
        }

        eventBookingRepository.approveBooking(id);
        booking.setStatus("APPROVED");
        return booking;
    }

    // UPDATE - Reject booking
    public EventBooking rejectBooking(Long id) {
        EventBooking booking = getBookingById(id);
        eventBookingRepository.rejectBooking(id);
        booking.setStatus("REJECTED");
        return booking;
    }

    // UPDATE - Assign resources
    public void assignResources(Long bookingId, String staff, String equipment) {
        EventBooking booking = getBookingById(bookingId);

        if (!"APPROVED".equals(booking.getStatus())) {
            throw new RuntimeException("Can only assign resources to approved bookings");
        }

        // Implement logic to update Event_Equipment table
        jdbcTemplate.update("DELETE FROM Event_Equipment WHERE EventID = ?", bookingId); // Clear existing assignments
        if (equipment != null && !equipment.isEmpty()) {
            String[] equipmentItems = equipment.split(",");
            for (String item : equipmentItems) {
                jdbcTemplate.update("INSERT INTO Event_Equipment (EventID, EquipmentID) VALUES (?, ?)",
                        bookingId, item.trim()); // Assuming EquipmentID is passed as a string
            }
        }
        // Note: Staff assignment is not directly supported in the schema; consider adding a new table (e.g., Event_Staff)
    }

    // DELETE - Delete booking
    public void deleteBooking(Long id) {
        EventBooking booking = getBookingById(id);
        // Additional logic to delete related Schedule or Event_Equipment records if needed
        eventBookingRepository.findById(id); // Fetch to ensure it exists (optional)
        // Direct deletion not implemented in repository; consider adding a delete method
        jdbcTemplate.update("DELETE FROM [Event] WHERE EventID = ?", id);
        jdbcTemplate.update("DELETE FROM Schedule WHERE ScheduleID = (SELECT ScheduleID FROM Schedule WHERE HallID = (SELECT HallID FROM [Event] WHERE EventID = ?))", id);
    }

    // VALIDATION - Check scheduling conflict
    private boolean hasSchedulingConflict(EventBooking booking) {
        if (booking.getHallId() == null || booking.getStartTime() == null || booking.getEndTime() == null) {
            return false; // No conflict if essential data is missing
        }

        Long excludeId = (booking.getEventId() != null) ? booking.getEventId() : null;
        return eventBookingRepository.hasConflict(
                booking.getHallId(),
                booking.getStartTime(),
                booking.getEndTime(),
                excludeId
        );
    }

    // BUSINESS LOGIC - Check hall availability
    public boolean checkHallAvailability(Long hallId, LocalDateTime start, LocalDateTime end) {
        return !eventBookingRepository.hasConflict(hallId, start, end, null);
    }
}
