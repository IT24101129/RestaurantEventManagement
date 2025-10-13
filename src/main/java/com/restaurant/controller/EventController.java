package com.restaurant.controller;

import com.restaurant.model.Event;
import com.restaurant.model.EventBooking;
import com.restaurant.model.User;
import com.restaurant.service.EventService;
import com.restaurant.service.EventBookingService;
import com.restaurant.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    private final EventBookingService eventBookingService;
    private final userService userService;
    
    @Autowired
    public EventController(EventService eventService, EventBookingService eventBookingService, userService userService) {
        this.eventService = eventService;
        this.eventBookingService = eventBookingService;
        this.userService = userService;
    }
    
    /**
     * Event booking dashboard - displays calendar and available events
     */
    @GetMapping("/booking")
    public String eventBookingDashboard(Model model) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isEmpty()) {
                model.addAttribute("error", "User not found. Please log in again.");
                return "events/booking";
            }
            
            // Get available events
            List<Event> availableEvents = eventService.getAllAvailableEvents();
            
            // Get today's bookings for calendar
            List<EventBooking> todayBookings = eventBookingService.getBookingsByDate(LocalDate.now());
            
            model.addAttribute("user", user.get());
            model.addAttribute("availableEvents", availableEvents);
            model.addAttribute("todayBookings", todayBookings);
            model.addAttribute("currentDate", LocalDate.now());
            model.addAttribute("eventTypes", Event.EventType.values());
            
            return "events/booking";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load event booking module. Please try again.");
            return "events/booking";
        }
    }
    
    /**
     * Get available time slots for a specific date and event
     */
    @GetMapping("/available-slots")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableTimeSlots(
            @RequestParam("date") String dateStr,
            @RequestParam("eventId") Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<LocalTime> availableSlots = eventBookingService.getAvailableTimeSlots(date, eventId);
            
            response.put("success", true);
            response.put("availableSlots", availableSlots);
            response.put("message", "Available time slots retrieved successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load available time slots: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get suggested event packages based on criteria
     */
    @GetMapping("/suggest-packages")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> suggestEventPackages(
            @RequestParam(value = "eventType", required = false) String eventTypeStr,
            @RequestParam(value = "guestCount", required = false) Integer guestCount,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Event.EventType eventType = null;
            if (eventTypeStr != null && !eventTypeStr.trim().isEmpty()) {
                eventType = Event.EventType.valueOf(eventTypeStr.toUpperCase());
            }
            
            List<Event> suggestedEvents = eventService.suggestEventPackages(eventType, guestCount, maxPrice);
            
            response.put("success", true);
            response.put("suggestedEvents", suggestedEvents);
            response.put("message", "Event packages suggested successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to suggest event packages: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check availability for a specific time slot
     */
    @PostMapping("/check-availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAvailability(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String dateStr = request.get("date").toString();
            String startTimeStr = request.get("startTime").toString();
            String endTimeStr = request.get("endTime").toString();
            
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            
            boolean isAvailable = eventService.isEventAvailable(eventId, date, startTime, endTime);
            
            response.put("success", true);
            response.put("isAvailable", isAvailable);
            response.put("message", isAvailable ? "Time slot is available" : "Time slot is not available");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to check availability: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create a new event booking
     */
    @PostMapping("/create-booking")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found. Please log in again.");
                return ResponseEntity.ok(response);
            }
            
            // Extract booking details
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String clientName = request.get("clientName").toString();
            String clientEmail = request.get("clientEmail").toString();
            String clientPhone = request.get("clientPhone").toString();
            String dateStr = request.get("date").toString();
            String startTimeStr = request.get("startTime").toString();
            String endTimeStr = request.get("endTime").toString();
            Integer guestCount = Integer.valueOf(request.get("guestCount").toString());
            String specialRequirements = request.get("specialRequirements") != null ? 
                request.get("specialRequirements").toString() : "";
            String notes = request.get("notes") != null ? 
                request.get("notes").toString() : "";
            
            // Validate required fields
            if (clientName == null || clientName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client name is required");
                return ResponseEntity.ok(response);
            }
            if (clientEmail == null || clientEmail.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client email is required");
                return ResponseEntity.ok(response);
            }
            if (clientPhone == null || clientPhone.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client phone is required");
                return ResponseEntity.ok(response);
            }
            
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            
            // Create booking
            EventBooking booking = eventBookingService.createBooking(
                eventId, user.get().getId(), clientName, clientEmail, clientPhone,
                date, startTime, endTime, guestCount, specialRequirements, notes, "Event Coordinator"
            );
            
            response.put("success", true);
            response.put("bookingId", booking.getId());
            response.put("message", "Event booking created successfully");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create booking: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get alternative dates when no time slots are available
     */
    @GetMapping("/alternative-dates")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAlternativeDates(
            @RequestParam("eventId") Long eventId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            List<LocalDate> alternativeDates = eventService.getAvailableDatesForEvent(eventId, startDate, endDate);
            
            response.put("success", true);
            response.put("alternativeDates", alternativeDates);
            response.put("message", "Alternative dates retrieved successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get alternative dates: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * View all event bookings
     */
    @GetMapping("/bookings")
    public String viewBookings(Model model) {
        try {
            List<EventBooking> bookings = eventBookingService.getAllBookings();
            model.addAttribute("bookings", bookings);
            return "events/bookings";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load bookings: " + e.getMessage());
            return "events/bookings";
        }
    }
    
    /**
     * View booking details
     */
    @GetMapping("/booking/{id}")
    public String viewBooking(@PathVariable Long id, Model model) {
        try {
            Optional<EventBooking> booking = eventBookingService.getBookingById(id);
            if (booking.isPresent()) {
                model.addAttribute("booking", booking.get());
                return "events/booking-details";
            } else {
                model.addAttribute("error", "Booking not found");
                return "events/bookings";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load booking: " + e.getMessage());
            return "events/bookings";
        }
    }
    
    /**
     * Update booking status
     */
    @PostMapping("/booking/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<EventBooking> bookingOpt = eventBookingService.getBookingById(id);
            if (bookingOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Booking not found");
                return ResponseEntity.ok(response);
            }
            
            EventBooking booking = bookingOpt.get();
            booking.setStatus(EventBooking.BookingStatus.valueOf(status.toUpperCase()));
            eventBookingService.updateBooking(booking);
            
            response.put("success", true);
            response.put("message", "Booking status updated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update booking status: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete booking
     */
    @DeleteMapping("/booking/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            eventBookingService.deleteBooking(id);
            response.put("success", true);
            response.put("message", "Booking deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete booking: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
