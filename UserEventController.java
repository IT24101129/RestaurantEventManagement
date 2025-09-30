package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.service.EventService;
import com.resturant.restaurantapp.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user/events")
public class UserEventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private AuditService auditService;
    
    // User Event Booking Page
    @GetMapping("/book")
    public String bookEventPage(Model model, HttpSession session) {
        // Check if customer is logged in
        if (session.getAttribute("customerId") == null) {
            return "redirect:/customer/login";
        }
        
        model.addAttribute("event", new Event());
        model.addAttribute("eventTypes", Event.EventType.values());
        model.addAttribute("eventPackages", Event.EventPackage.values());
        model.addAttribute("bookingStatuses", Event.BookingStatus.values());
        return "user-book-event";
    }
    
    @PostMapping("/book")
    public String bookEvent(@ModelAttribute Event event, 
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            // Check if customer is logged in
            if (session.getAttribute("customerId") == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please login to book an event.");
                return "redirect:/customer/login";
            }
            
            // Get logged-in customer's details from session and set them in the event
            String customerName = (String) session.getAttribute("customerName");
            String customerEmail = (String) session.getAttribute("customerEmail");
            String customerPhone = (String) session.getAttribute("customerPhone");
            
            if (customerName != null && customerEmail != null && customerPhone != null) {
                event.setCustomerName(customerName);
                event.setCustomerEmail(customerEmail);
                event.setCustomerPhone(customerPhone);
            }
            
            // Set initial status for user bookings
            event.setBookingStatus(Event.BookingStatus.PENDING);
            
            Event savedEvent = eventService.createEvent(event);
            
            // Log the event creation
            auditService.logEventCreation(savedEvent, "USER_SYSTEM");
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Event booking request submitted successfully! Your booking ID is: " + savedEvent.getEventId() + 
                ". We will review your request and get back to you soon.");
            return "redirect:/user/events/booking-success/" + savedEvent.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error submitting event booking: " + e.getMessage());
            return "redirect:/user/events/book";
        }
    }
    
    // Booking Success Page
    @GetMapping("/booking-success/{id}")
    public String bookingSuccessPage(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        model.addAttribute("event", event);
        return "user-booking-success";
    }
    
    // View User's Bookings
    @GetMapping("/my-bookings")
    public String myBookingsPage(Model model, 
                                HttpSession session,
                                @RequestParam(required = false) String customerName,
                                @RequestParam(required = false) String customerPhone) {
        try {
            // Check if customer is logged in
            if (session.getAttribute("customerId") == null) {
                model.addAttribute("errorMessage", "Please login to view your bookings.");
                model.addAttribute("events", List.of());
                return "user-my-bookings";
            }
            
            List<Event> userBookings;
            
            if (customerName != null && !customerName.isEmpty() && 
                customerPhone != null && !customerPhone.isEmpty()) {
                // Search by provided customer details
                userBookings = eventService.getEventsByCustomer(customerName, customerPhone);
            } else {
                // Get logged-in customer's details from session
                String loggedInCustomerName = (String) session.getAttribute("customerName");
                String loggedInCustomerPhone = (String) session.getAttribute("customerPhone");
                
                if (loggedInCustomerName != null && loggedInCustomerPhone != null) {
                    // Show only logged-in customer's bookings
                    userBookings = eventService.getEventsByCustomer(loggedInCustomerName, loggedInCustomerPhone);
                } else {
                    // Fallback: show empty list if session data is missing
                    userBookings = List.of();
                    model.addAttribute("errorMessage", "Unable to retrieve customer information. Please login again.");
                }
            }
            
            model.addAttribute("events", userBookings);
            model.addAttribute("eventTypes", Event.EventType.values());
            model.addAttribute("eventPackages", Event.EventPackage.values());
            model.addAttribute("bookingStatuses", Event.BookingStatus.values());
            
            return "user-my-bookings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading bookings: " + e.getMessage());
            model.addAttribute("events", List.of());
            return "user-my-bookings";
        }
    }
    
    // Search Bookings Form
    @GetMapping("/search-bookings")
    public String searchBookingsPage() {
        return "user-search-bookings";
    }
}
