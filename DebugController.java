package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.model.Menu;
import com.resturant.restaurantapp.service.EventService;
import com.resturant.restaurantapp.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/debug")
public class DebugController {
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private EventService eventService;
    
    @GetMapping("/menus")
    @ResponseBody
    public String debugMenus() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Menu Debug Information</h2>");
        
        try {
            // Check all menus
            List<Menu> allMenus = menuService.getAllMenus();
            result.append("<h3>All Menus (").append(allMenus.size()).append("):</h3>");
            result.append("<ul>");
            for (Menu menu : allMenus) {
                result.append("<li>ID: ").append(menu.getId())
                      .append(", Name: ").append(menu.getMenuName())
                      .append(", Package: ").append(menu.getMenuPackage())
                      .append(", Active: ").append(menu.getIsActive())
                      .append(", Price: ").append(menu.getBasePricePerGuest())
                      .append("</li>");
            }
            result.append("</ul>");
            
            // Check active menus
            List<Menu> activeMenus = menuService.getActiveMenus();
            result.append("<h3>Active Menus (").append(activeMenus.size()).append("):</h3>");
            result.append("<ul>");
            for (Menu menu : activeMenus) {
                result.append("<li>ID: ").append(menu.getId())
                      .append(", Name: ").append(menu.getMenuName())
                      .append(", Package: ").append(menu.getMenuPackage())
                      .append("</li>");
            }
            result.append("</ul>");
            
        } catch (Exception e) {
            result.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>");
            result.append("<p>Stack trace:</p><pre>").append(e.getStackTrace()[0]).append("</pre>");
        }
        
        return result.toString();
    }
    
    @GetMapping("/events")
    @ResponseBody
    public String debugEvents() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Event Debug Information</h2>");
        
        try {
            List<Event> allEvents = eventService.getAllEvents();
            result.append("<h3>All Events (").append(allEvents.size()).append("):</h3>");
            result.append("<ul>");
            for (Event event : allEvents) {
                result.append("<li>ID: ").append(event.getId())
                      .append(", EventID: ").append(event.getEventId())
                      .append(", Customer: ").append(event.getCustomerName())
                      .append(", Date: ").append(event.getEventDate())
                      .append(", Guests: ").append(event.getNumberOfGuests())
                      .append("</li>");
            }
            result.append("</ul>");
            
        } catch (Exception e) {
            result.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>");
        }
        
        return result.toString();
    }
    
    @GetMapping("/menu-selection-test/{eventId}")
    @ResponseBody
    public String testMenuSelection(@PathVariable Long eventId) {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Menu Selection Test for Event ID: ").append(eventId).append("</h2>");
        
        try {
            // Test event retrieval
            Event event = eventService.getEventById(eventId).orElse(null);
            if (event == null) {
                result.append("<p style='color: red;'>Event not found!</p>");
                return result.toString();
            }
            
            result.append("<h3>Event Found:</h3>");
            result.append("<ul>");
            result.append("<li>ID: ").append(event.getId()).append("</li>");
            result.append("<li>EventID: ").append(event.getEventId()).append("</li>");
            result.append("<li>Customer: ").append(event.getCustomerName()).append("</li>");
            result.append("<li>Date: ").append(event.getEventDate()).append("</li>");
            result.append("<li>Guests: ").append(event.getNumberOfGuests()).append("</li>");
            result.append("</ul>");
            
            // Test menu retrieval
            List<Menu> activeMenus = menuService.getActiveMenus();
            result.append("<h3>Active Menus (").append(activeMenus.size()).append("):</h3>");
            result.append("<ul>");
            for (Menu menu : activeMenus) {
                result.append("<li>ID: ").append(menu.getId())
                      .append(", Name: ").append(menu.getMenuName())
                      .append(", Package: ").append(menu.getMenuPackage())
                      .append(", Active: ").append(menu.getIsActive())
                      .append("</li>");
            }
            result.append("</ul>");
            
            if (activeMenus.isEmpty()) {
                result.append("<p style='color: orange;'>No active menus found! This is likely the cause of the error.</p>");
                result.append("<p><a href='/admin/init/create-sample-data'>Create Sample Menus</a></p>");
            }
            
        } catch (Exception e) {
            result.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>");
            result.append("<p>Stack trace:</p><pre>").append(e.getStackTrace()[0]).append("</pre>");
        }
        
        return result.toString();
    }
}
