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

@Controller
@RequestMapping("/admin/events-test")
public class EventControllerTest {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private EventMenuSelectionRepository eventMenuSelectionRepository;
    
    @GetMapping
    public String testPage(Model model) {
        // Test if we can access the classes
        Event.EventType[] eventTypes = Event.EventType.values();
        Event.EventPackage[] eventPackages = Event.EventPackage.values();
        Event.BookingStatus[] bookingStatuses = Event.BookingStatus.values();
        
        model.addAttribute("eventTypes", eventTypes);
        model.addAttribute("eventPackages", eventPackages);
        model.addAttribute("bookingStatuses", bookingStatuses);
        
        return "test-page";
    }
}
