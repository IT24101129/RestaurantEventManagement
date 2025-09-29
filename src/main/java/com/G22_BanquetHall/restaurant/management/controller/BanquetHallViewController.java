package com.G22_BanquetHall.restaurant.management.controller;



import com.G22_BanquetHall.restaurant.management.model.EventBooking;
import com.G22_BanquetHall.restaurant.management.service.EventBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/banquet-hall")
public class BanquetHallViewController {

    @Autowired
    private EventBookingService eventBookingService;

    /**
     * Display the main supervisor dashboard
     * Data is loaded via JavaScript API calls
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // We're using JavaScript to load data via APIs
        // This just returns the HTML template
        return "dashboard";
    }

    /**
     * Display all bookings in a table view
     * Data is loaded via JavaScript API calls
     */
    @GetMapping("/bookings")
    public String viewAllBookings(Model model) {
        // We're using JavaScript to load data via APIs
        // This just returns the HTML template
        return "bookings";
    }


}