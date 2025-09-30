package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.model.EventAudit;
import com.resturant.restaurantapp.model.EventMenuSelection;
import com.resturant.restaurantapp.service.EventService;
import com.resturant.restaurantapp.service.AuditService;
import com.resturant.restaurantapp.repository.EventMenuSelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private EventMenuSelectionRepository eventMenuSelectionRepository;
    
    @GetMapping
    public String eventManagementPage(Model model) {
        List<Event> allEvents = eventService.getAllEvents();
        model.addAttribute("events", allEvents);
        model.addAttribute("eventTypes", Event.EventType.values());
        model.addAttribute("eventPackages", Event.EventPackage.values());
        model.addAttribute("bookingStatuses", Event.BookingStatus.values());
        
        // Add menu selection information for each event
        Map<Long, EventMenuSelection> menuSelections = new HashMap<>();
        for (Event event : allEvents) {
            eventMenuSelectionRepository.findByEventId(event.getId())
                .ifPresent(selection -> menuSelections.put(event.getId(), selection));
        }
        model.addAttribute("menuSelections", menuSelections);
        
        return "event-management";
    }
    
    // Note: Event creation is now handled by user booking system
    // Admin can only edit existing events that were created through user bookings
    
    @GetMapping("/edit/{id}")
    public String editEventPage(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("eventTypes", Event.EventType.values());
        model.addAttribute("eventPackages", Event.EventPackage.values());
        model.addAttribute("bookingStatuses", Event.BookingStatus.values());
        return "edit-event";
    }
    
    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event, 
                           RedirectAttributes redirectAttributes) {
        try {
            event.setId(id);
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event updated successfully!");
            return "redirect:/admin/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating event: " + e.getMessage());
            return "redirect:/admin/events/edit/" + id;
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
    
    @PostMapping("/confirm/{id}")
    public String confirmEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.confirmEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error confirming event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
    
    @PostMapping("/cancel/{id}")
    public String cancelEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.cancelEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error cancelling event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
    
    @PostMapping("/start/{id}")
    public String startEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.startEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event started successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error starting event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
    
    @PostMapping("/complete/{id}")
    public String completeEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.completeEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error completing event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }
    
    @GetMapping("/upcoming")
    public String upcomingEventsPage(Model model) {
        try {
            List<Event> upcomingEvents = eventService.getUpcomingEvents();
            model.addAttribute("events", upcomingEvents);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            return "upcoming-events";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading upcoming events: " + e.getMessage());
            model.addAttribute("events", new ArrayList<>());
            return "upcoming-events";
        }
    }
    
    @GetMapping("/pending")
    public String pendingEventsPage(Model model) {
        try {
            List<Event> pendingEvents = eventService.getPendingApprovals();
            model.addAttribute("events", pendingEvents);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            return "pending-events";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading pending events: " + e.getMessage());
            model.addAttribute("events", new ArrayList<>());
            return "pending-events";
        }
    }
    
    @GetMapping("/by-type/{eventType}")
    public String getEventsByType(@PathVariable String eventType, Model model) {
        try {
            Event.EventType type = Event.EventType.valueOf(eventType.toUpperCase());
            List<Event> events = eventService.getEventsByType(type);
            model.addAttribute("events", events);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            model.addAttribute("selectedType", type);
            return "event-management";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/events";
        }
    }
    
    @GetMapping("/by-status/{status}")
    public String getEventsByStatus(@PathVariable String status, Model model) {
        try {
            Event.BookingStatus bookingStatus = Event.BookingStatus.valueOf(status.toUpperCase());
            List<Event> events = eventService.getEventsByStatus(bookingStatus);
            model.addAttribute("events", events);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            model.addAttribute("selectedStatus", bookingStatus);
            return "event-management";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/events";
        }
    }
    
    @GetMapping("/today")
    public String todayEventsPage(Model model) {
        try {
            List<Event> todayEvents = eventService.getEventsForToday();
            model.addAttribute("events", todayEvents);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            return "today-events";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading today's events: " + e.getMessage());
            model.addAttribute("events", new ArrayList<>());
            return "today-events";
        }
    }
    
    @GetMapping("/check-availability")
    @ResponseBody
    public boolean checkAvailability(@RequestParam String date, @RequestParam String time) {
        try {
            LocalDate eventDate = LocalDate.parse(date);
            LocalTime eventTime = LocalTime.parse(time);
            return eventService.isTimeSlotAvailable(eventDate, eventTime);
        } catch (Exception e) {
            return false;
        }
    }
    
    @GetMapping("/audit")
    public String auditHistoryPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String fieldName,
            @RequestParam(required = false) String changedBy,
            Model model) {
        
        try {
            // Get all audit history
            List<EventAudit> allAuditHistory = auditService.getAllAuditHistory();
            
            // Apply filters
            List<EventAudit> filteredHistory = allAuditHistory.stream()
                .filter(audit -> eventId == null || eventId.isEmpty() || audit.getEventId().contains(eventId))
                .filter(audit -> changeType == null || changeType.isEmpty() || audit.getChangeType().name().equals(changeType))
                .filter(audit -> fieldName == null || fieldName.isEmpty() || audit.getFieldName().toLowerCase().contains(fieldName.toLowerCase()))
                .filter(audit -> changedBy == null || changedBy.isEmpty() || audit.getChangedBy().toLowerCase().contains(changedBy.toLowerCase()))
                .toList();
            
            // Calculate statistics
            long totalAuditRecords = allAuditHistory.size();
            long totalEvents = allAuditHistory.stream().map(EventAudit::getEventId).distinct().count();
            long recentChanges = allAuditHistory.stream()
                .filter(audit -> audit.getChangedAt().toLocalDate().equals(LocalDate.now()))
                .count();
            
            // Pagination
            int pageSize = 50;
            int totalPages = (int) Math.ceil((double) filteredHistory.size() / pageSize);
            int startIndex = page * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredHistory.size());
            
            List<EventAudit> paginatedHistory = filteredHistory.subList(startIndex, endIndex);
            
            model.addAttribute("auditHistory", paginatedHistory);
            model.addAttribute("totalAuditRecords", totalAuditRecords);
            model.addAttribute("totalEvents", totalEvents);
            model.addAttribute("recentChanges", recentChanges);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            
            return "audit-history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading audit history: " + e.getMessage());
            return "audit-history";
        }
    }
    
    @GetMapping("/audit/{eventId}")
    public String eventAuditHistory(@PathVariable String eventId, Model model) {
        try {
            List<EventAudit> eventAuditHistory = auditService.getAuditHistoryByEventId(eventId);
            model.addAttribute("auditHistory", eventAuditHistory);
            model.addAttribute("eventId", eventId);
            return "audit-history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading audit history for event: " + e.getMessage());
            return "audit-history";
        }
    }
    
    @GetMapping("/test-audit")
    @ResponseBody
    public String testAudit() {
        try {
            List<EventAudit> allAudits = auditService.getAllAuditHistory();
            return "Audit system working! Found " + allAudits.size() + " audit records.";
        } catch (Exception e) {
            return "Audit system error: " + e.getMessage();
        }
    }
    
    @GetMapping("/create-test-event")
    @ResponseBody
    public String createTestEvent() {
        try {
            Event testEvent = new Event();
            testEvent.setEventType(Event.EventType.WEDDING);
            testEvent.setEventDate(LocalDate.now().plusDays(7)); // Next week
            testEvent.setEventTime(LocalTime.of(18, 0)); // 6 PM
            testEvent.setCustomerName("Test Customer");
            testEvent.setCustomerPhone("0771234567");
            testEvent.setCustomerEmail("test@example.com");
            testEvent.setNumberOfGuests(50);
            testEvent.setEventPackage(Event.EventPackage.STANDARD);
            testEvent.setSpecialNotes("Test event for upcoming events page");
            testEvent.setBookingStatus(Event.BookingStatus.CONFIRMED);
            
            Event savedEvent = eventService.createEvent(testEvent);
            return "Test event created successfully! Event ID: " + savedEvent.getId();
        } catch (Exception e) {
            return "Error creating test event: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug-all-events")
    @ResponseBody
    public String debugAllEvents() {
        try {
            List<Event> allEvents = eventService.getAllEvents();
            StringBuilder result = new StringBuilder();
            result.append("Total events in database: ").append(allEvents.size()).append("\n\n");
            
            for (Event event : allEvents) {
                result.append("Event ID: ").append(event.getId())
                      .append(", Event ID: ").append(event.getEventId())
                      .append(", Customer: ").append(event.getCustomerName())
                      .append(", Phone: ").append(event.getCustomerPhone())
                      .append(", Status: ").append(event.getBookingStatus())
                      .append(", Date: ").append(event.getEventDate())
                      .append(", Type: ").append(event.getEventType())
                      .append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Error retrieving events: " + e.getMessage();
        }
    }
}

