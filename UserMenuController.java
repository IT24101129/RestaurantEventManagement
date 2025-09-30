package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.model.Menu;
import com.resturant.restaurantapp.model.EventMenuSelection;
import com.resturant.restaurantapp.service.MenuService;
import com.resturant.restaurantapp.service.EventService;
import com.resturant.restaurantapp.repository.EventMenuSelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/menu")
public class UserMenuController {
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventMenuSelectionRepository eventMenuSelectionRepository;
    
    // Browse Available Menus
    @GetMapping("/browse")
    public String browseMenusPage(Model model) {
        List<Menu> availableMenus = menuService.getActiveMenus();
        model.addAttribute("menus", availableMenus);
        model.addAttribute("menuPackages", Menu.MenuPackage.values());
        return "user-browse-menus";
    }
    
    // View Menu Details
    @GetMapping("/{menuId}/details")
    public String viewMenuDetails(@PathVariable Long menuId, Model model) {
        Menu menu = menuService.getMenuById(menuId)
            .orElseThrow(() -> new RuntimeException("Menu not found"));
        
        model.addAttribute("menu", menu);
        return "user-menu-details";
    }
    
    // Select Menu for Event (User Side)
    @GetMapping("/events/{eventId}/select-menu")
    public String selectMenuForEvent(@PathVariable Long eventId, Model model, HttpSession session) {
        try {
            // Check if customer is logged in
            if (session.getAttribute("customerId") == null) {
                return "redirect:/customer/login";
            }
            
            Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            // Verify that the event belongs to the logged-in customer
            String loggedInCustomerName = (String) session.getAttribute("customerName");
            if (!event.getCustomerName().equals(loggedInCustomerName)) {
                model.addAttribute("errorMessage", "You can only select menus for your own events.");
                return "redirect:/user/events/my-bookings";
            }
            
            List<Menu> availableMenus = menuService.getActiveMenus();
            
            model.addAttribute("event", event);
            model.addAttribute("availableMenus", availableMenus);
            model.addAttribute("menuPackages", Menu.MenuPackage.values());
            
            // If no menus available, add a message
            if (availableMenus.isEmpty()) {
                model.addAttribute("noMenusMessage", "No menus are currently available. Please contact our staff for menu options.");
            }
            
            return "user-select-event-menu";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading menu selection: " + e.getMessage());
            return "error-page";
        }
    }
    
    @PostMapping("/events/{eventId}/select-menu")
    public String saveMenuSelectionForEvent(@PathVariable Long eventId, 
                                          @RequestParam Long menuId,
                                          @RequestParam(required = false) String specialRequests,
                                          @RequestParam(required = false) String dietaryRestrictions,
                                          @RequestParam(required = false) String customChanges,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        try {
            // Check if customer is logged in
            if (session.getAttribute("customerId") == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login to select a menu.");
                return "redirect:/customer/login";
            }
            
            Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            // Verify that the event belongs to the logged-in customer
            String loggedInCustomerName = (String) session.getAttribute("customerName");
            if (!event.getCustomerName().equals(loggedInCustomerName)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only select menus for your own events.");
                return "redirect:/user/events/my-bookings";
            }
            
            Menu menu = menuService.getMenuById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
            
            // Check if event already has a menu selection
            if (eventMenuSelectionRepository.existsByEventId(eventId)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "This event already has a menu selection. Please contact our staff to modify your selection.");
                return "redirect:/user/events/my-bookings";
            }
            
            // Create menu selection for the event
            EventMenuSelection menuSelection = new EventMenuSelection();
            menuSelection.setEvent(event);
            menuSelection.setSelectedMenu(menu);
            menuSelection.setNumberOfGuests(event.getNumberOfGuests());
            menuSelection.setSpecialNotes(specialRequests);
            menuSelection.setDietaryRestrictions(dietaryRestrictions);
            menuSelection.setCustomChanges(customChanges);
            menuSelection.setSelectionStatus(EventMenuSelection.SelectionStatus.PENDING);
            
            // Calculate total cost
            BigDecimal totalCost = menu.getBasePricePerGuest().multiply(BigDecimal.valueOf(event.getNumberOfGuests()));
            menuSelection.setTotalCost(totalCost);
            
            // Save the menu selection
            EventMenuSelection savedSelection = eventMenuSelectionRepository.save(menuSelection);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Menu selection submitted successfully! Your selection ID is: " + savedSelection.getSelectionId() + 
                " | Total Cost: Rs. " + totalCost + 
                ". Our staff will review your selection and confirm the details.");
            return "redirect:/user/menu/events/" + eventId + "/selection-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving menu selection: " + e.getMessage());
            return "redirect:/user/menu/events/" + eventId + "/select-menu";
        }
    }
    
    // Menu Selection Success Page
    @GetMapping("/events/{eventId}/selection-success")
    public String menuSelectionSuccessPage(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        Optional<EventMenuSelection> selection = eventMenuSelectionRepository.findByEventId(eventId);
        
        model.addAttribute("event", event);
        model.addAttribute("menuSelection", selection.orElse(null));
        
        return "user-menu-selection-success";
    }
    
    // View Event Menu Selection (User Side)
    @GetMapping("/events/{eventId}/view-selection")
    public String viewEventMenuSelection(@PathVariable Long eventId, Model model) {
        Optional<EventMenuSelection> selection = eventMenuSelectionRepository.findByEventId(eventId);
        if (selection.isPresent()) {
            model.addAttribute("menuSelection", selection.get());
            model.addAttribute("selectionStatuses", EventMenuSelection.SelectionStatus.values());
            return "user-view-menu-selection";
        } else {
            model.addAttribute("errorMessage", "No menu selection found for this event.");
            return "redirect:/user/events/my-bookings";
        }
    }
    
    // AJAX Endpoints for User Interface
    @GetMapping("/api/menus/by-package")
    @ResponseBody
    public List<Menu> getMenusByPackage(@RequestParam String packageName) {
        try {
            Menu.MenuPackage menuPackage = Menu.MenuPackage.valueOf(packageName.toUpperCase());
            return menuService.getActiveMenusByPackage(menuPackage);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    @GetMapping("/api/menu/{menuId}/cost")
    @ResponseBody
    public BigDecimal calculateMenuCost(@PathVariable Long menuId, @RequestParam int guests) {
        Menu menu = menuService.getMenuById(menuId)
            .orElseThrow(() -> new RuntimeException("Menu not found"));
        return menuService.calculateMenuCost(menu, guests);
    }
}
