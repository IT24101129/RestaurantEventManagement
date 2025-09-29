package com.G22_BanquetHall.restaurant.management.controller;

import com.G22_BanquetHall.restaurant.management.model.EventBooking;
import com.G22_BanquetHall.restaurant.management.service.EventBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/banquet-hall")
@CrossOrigin(origins = "http://localhost:3000")
public class BanquetHallController {

    @Autowired
    private EventBookingService eventBookingService;

    // CREATE - Add new booking
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody EventBooking booking) {
        try {
            // Parse date and time if provided as strings (adjust based on your model)
            if (booking.getStartTime() == null && booking.getDateTimeStr() != null && !booking.getDateTimeStr().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
                LocalDateTime dateTime = LocalDateTime.parse(booking.getDateTimeStr(), formatter);
                booking.setStartTime(dateTime);
            }
            // Removed getEndTimeStr() reference; rely on endTime from JSON
            if (booking.getEndTime() == null) {
                booking.setEndTime(booking.getStartTime() != null ? booking.getStartTime().plusHours(4) : null); // Default to 4 hours if not provided
            }

            // Ensure required fields are set (fallback to defaults if undefined)
            if (booking.getStatus() == null) booking.setStatus("PENDING");
            if (booking.getHallId() == null) booking.setHallId(0L);

            EventBooking savedBooking = eventBookingService.saveBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating booking: " + e.getMessage());
        }
    }

    // READ - Get all bookings
    @GetMapping("/bookings")
    public List<EventBooking> getAllBookings() {
        return eventBookingService.getAllBookings();
    }

    // READ - Get pending bookings for approval
    @GetMapping("/bookings/pending")
    public List<EventBooking> getPendingBookings() {
        return eventBookingService.getPendingBookings();
    }

    // READ - Get upcoming events
    @GetMapping("/bookings/upcoming")
    public List<EventBooking> getUpcomingEvents() {
        return eventBookingService.getUpcomingEvents();
    }

    // READ - Get single booking
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            EventBooking booking = eventBookingService.getBookingById(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE - Approve booking
    @PutMapping("/bookings/{id}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        try {
            EventBooking approvedBooking = eventBookingService.approveBooking(id);
            return ResponseEntity.ok(approvedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving booking: " + e.getMessage());
        }
    }

    // UPDATE - Reject booking
    @PutMapping("/bookings/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id) {
        try {
            EventBooking rejectedBooking = eventBookingService.rejectBooking(id);
            return ResponseEntity.ok(rejectedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting booking: " + e.getMessage());
        }
    }

    // UPDATE - Assign resources
    @PutMapping("/bookings/{id}/assign")
    public ResponseEntity<?> assignResources(@PathVariable Long id,
                                             @RequestParam String staff,
                                             @RequestParam String equipment) {
        try {
            eventBookingService.assignResources(id, staff, equipment);
            return ResponseEntity.ok("Resources assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning resources: " + e.getMessage());
        }
    }

    // BUSINESS OPERATION - Check availability
    @GetMapping("/availability")
    public ResponseEntity<?> checkAvailability(@RequestParam Long hallId,
                                               @RequestParam String startTime,
                                               @RequestParam String endTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            boolean available = eventBookingService.checkHallAvailability(hallId, start, end);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking availability: " + e.getMessage());
        }
    }

    // DASHBOARD - Get supervisor dashboard data
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            List<EventBooking> pendingBookings = eventBookingService.getPendingBookings();
            List<EventBooking> upcomingEvents = eventBookingService.getUpcomingEvents();

            // Create a simple dashboard response
            String dashboard = String.format(
                    "Dashboard - Pending: %d bookings, Upcoming: %d events",
                    pendingBookings.size(), upcomingEvents.size()
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading dashboard: " + e.getMessage());
        }
    }

    // DELETE - Remove booking
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            eventBookingService.deleteBooking(id);
            return ResponseEntity.ok("Booking deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting booking: " + e.getMessage());
        }
    }
}