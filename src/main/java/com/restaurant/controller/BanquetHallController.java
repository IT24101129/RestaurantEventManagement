package com.restaurant.controller;

import com.restaurant.model.*;
import com.restaurant.service.BanquetHallService;
import com.restaurant.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/banquet")
public class BanquetHallController {
    
    private final BanquetHallService banquetHallService;
    private final userService userService;
    
    @Autowired
    public BanquetHallController(BanquetHallService banquetHallService, userService userService) {
        this.banquetHallService = banquetHallService;
        this.userService = userService;
    }
    
    /**
     * Banquet Hall Supervisor Dashboard - Event Schedule Management
     */
    @GetMapping("/dashboard")
    public String banquetDashboard(Model model, Authentication authentication) {
        try {
            // Get current user
            if (authentication != null) {
                String email = authentication.getName();
                Optional<User> user = userService.findByEmail(email);
                user.ifPresent(u -> model.addAttribute("user", u));
            }
            
            // Get current week's events
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            Map<String, Object> dashboardData = banquetHallService.getEventScheduleDashboard(weekStart, weekEnd);
            
            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("weekStart", weekStart);
            model.addAttribute("weekEnd", weekEnd);
            model.addAttribute("today", today);
            
            return "banquet/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load banquet hall dashboard: " + e.getMessage());
            return "banquet/dashboard";
        }
    }
    
    /**
     * Get event schedule data for AJAX updates
     */
    @GetMapping("/api/events")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEventData(
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate startDate = startDateStr != null ? 
                LocalDate.parse(startDateStr) : LocalDate.now();
            LocalDate endDate = endDateStr != null ? 
                LocalDate.parse(endDateStr) : startDate.plusDays(7);
            
            Map<String, Object> dashboardData = banquetHallService.getEventScheduleDashboard(startDate, endDate);
            
            response.put("success", true);
            response.put("data", dashboardData);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load event data: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get upcoming events with details
     */
    @GetMapping("/events/upcoming")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUpcomingEvents(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(days);
            
            List<EventBooking> upcomingEvents = banquetHallService.getUpcomingEvents(startDate, endDate);
            
            response.put("success", true);
            response.put("events", upcomingEvents);
            response.put("count", upcomingEvents.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load upcoming events: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Review hall availability and resource allocation
     */
    @GetMapping("/availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkHallAvailability(
            @RequestParam("date") String dateStr) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate date = LocalDate.parse(dateStr);
            
            // Check for conflicts
            List<EventBooking> conflicts = banquetHallService.findHallAvailabilityConflicts(date, date);
            
            // Get resource allocation summary
            Map<String, Integer> resourceAllocation = banquetHallService.getResourceAllocationSummary(date);
            
            response.put("success", true);
            response.put("conflicts", conflicts);
            response.put("resourceAllocation", resourceAllocation);
            response.put("isAvailable", conflicts.isEmpty());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to check hall availability: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Assign staff to event
     */
    @PostMapping("/events/{eventId}/assign-staff")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignStaffToEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String assignedBy = auth.getName();
            
            Long staffId = Long.valueOf(request.get("staffId").toString());
            String role = request.get("role").toString();
            Integer assignedHours = Integer.valueOf(request.get("assignedHours").toString());
            String startTime = request.get("startTime").toString();
            String endTime = request.get("endTime").toString();
            
            EventStaffAssignment assignment = banquetHallService.assignStaffToEvent(
                eventId, staffId, role, assignedHours, startTime, endTime, assignedBy
            );
            
            response.put("success", true);
            response.put("assignment", assignment);
            response.put("message", "Staff assigned successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to assign staff: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Assign equipment to event
     */
    @PostMapping("/events/{eventId}/assign-equipment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignEquipmentToEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String assignedBy = auth.getName();
            
            String equipmentName = request.get("equipmentName").toString();
            String equipmentType = request.get("equipmentType").toString();
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            BigDecimal unitCost = new BigDecimal(request.get("unitCost").toString());
            
            // Check resource availability
            EventBooking eventBooking = banquetHallService.getUpcomingEvents().stream()
                .filter(booking -> booking.getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            if (!banquetHallService.checkResourceAvailability(equipmentType, quantity, eventBooking.getEventDate())) {
                Map<String, Object> errorResponse = banquetHallService.handleResourceAllocationExceeded(equipmentType, quantity);
                return ResponseEntity.ok(errorResponse);
            }
            
            EventEquipment equipment = banquetHallService.assignEquipmentToEvent(
                eventId, equipmentName, equipmentType, quantity, unitCost, assignedBy
            );
            
            response.put("success", true);
            response.put("equipment", equipment);
            response.put("message", "Equipment assigned successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to assign equipment: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Confirm event schedule
     */
    @PostMapping("/events/{eventId}/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmEventSchedule(@PathVariable Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String confirmedBy = auth.getName();
            
            EventBooking confirmedBooking = banquetHallService.confirmEventSchedule(eventId, confirmedBy);
            
            response.put("success", true);
            response.put("booking", confirmedBooking);
            response.put("message", "Event schedule confirmed successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to confirm event schedule: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update event status
     */
    @PostMapping("/events/{eventId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateEventStatus(
            @PathVariable Long eventId,
            @RequestParam("status") String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            EventBooking.BookingStatus newStatus = EventBooking.BookingStatus.valueOf(status.toUpperCase());
            EventBooking updatedBooking = banquetHallService.updateEventStatus(eventId, newStatus);
            
            response.put("success", true);
            response.put("booking", updatedBooking);
            response.put("message", "Event status updated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to update event status: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get available staff for event
     */
    @GetMapping("/staff/available")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableStaff(
            @RequestParam("eventDate") String eventDateStr,
            @RequestParam(value = "role", required = false) String role) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate eventDate = LocalDate.parse(eventDateStr);
            List<Staff> availableStaff = banquetHallService.getAvailableStaffForEvent(eventDate, role);
            
            response.put("success", true);
            response.put("staff", availableStaff);
            response.put("count", availableStaff.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load available staff: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get alternative time slots when hall is booked
     */
    @GetMapping("/events/alternative-slots")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAlternativeTimeSlots(
            @RequestParam("date") String dateStr,
            @RequestParam("preferredStart") String preferredStartStr,
            @RequestParam("preferredEnd") String preferredEndStr) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime preferredStart = LocalTime.parse(preferredStartStr);
            LocalTime preferredEnd = LocalTime.parse(preferredEndStr);
            
            List<LocalTime> alternatives = banquetHallService.getAlternativeTimeSlots(date, preferredStart, preferredEnd);
            
            response.put("success", true);
            response.put("alternativeSlots", alternatives);
            response.put("count", alternatives.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to get alternative time slots: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle event data load failure
     */
    @GetMapping("/error/event-data-failure")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleEventDataLoadFailure() {
        Map<String, Object> response = banquetHallService.handleEventDataLoadFailure();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle hall booking conflict
     */
    @PostMapping("/error/hall-conflict")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleHallBookingConflict(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long eventId = Long.valueOf(request.get("eventId").toString());
            EventBooking conflictingBooking = banquetHallService.getUpcomingEvents().stream()
                .filter(booking -> booking.getId().equals(eventId))
                .findFirst()
                .orElse(null);
            
            if (conflictingBooking != null) {
                response = banquetHallService.handleHallBookingConflict(conflictingBooking);
            } else {
                response.put("success", false);
                response.put("error", "Conflicting booking not found");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to handle hall conflict: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
