package com.restaurant.controller;

import com.restaurant.dto.GuestReservationRequest;
import com.restaurant.dto.ReservationRequest;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.User;
import com.restaurant.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class EnhancedReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TableService tableService;

    @Autowired
    private com.restaurant.service.userService userService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoyaltyService loyaltyService;

    // Main reservation page with availability display
    @GetMapping
    public String reservationPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && !auth.getName().equals("anonymousUser");
        
        if (isLoggedIn) {
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            model.addAttribute("user", user.orElse(null));
            model.addAttribute("loyaltyTier", user.map(u -> loyaltyService.getLoyaltyTier(u)).orElse("Bronze"));
            model.addAttribute("loyaltyPoints", user.map(u -> u.getLoyaltyPoints()).orElse(0));
        } else {
            model.addAttribute("user", null);
        }

        // Get availability summary for today and next 7 days
        LocalDate today = LocalDate.now();
        model.addAttribute("availabilitySummary", availabilityService.getAvailabilitySummary(today));
        model.addAttribute("isLoggedIn", isLoggedIn);
        
        return "reservations/enhanced-index";
    }

    // Check availability for specific date/time
    @GetMapping("/check-availability")
    @ResponseBody
    public Map<String, Object> checkAvailability(@RequestParam String date, 
                                                @RequestParam String time, 
                                                @RequestParam Integer guests) {
        LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time);
        return availabilityService.checkAvailability(dateTime, guests);
    }

    // Get available time slots for a date
    @GetMapping("/time-slots")
    @ResponseBody
    public List<String> getTimeSlots(@RequestParam String date, @RequestParam Integer guests) {
        LocalDate localDate = LocalDate.parse(date);
        return availabilityService.getAvailableTimeSlots(localDate, guests);
    }

    // Get alternative time slots
    @GetMapping("/alternatives")
    @ResponseBody
    public List<String> getAlternatives(@RequestParam String date, 
                                       @RequestParam String time, 
                                       @RequestParam Integer guests) {
        LocalDate localDate = LocalDate.parse(date);
        return availabilityService.getAlternativeTimeSlots(localDate, guests, time);
    }

    // Login prompt for non-authenticated users
    @GetMapping("/login-required")
    public String loginRequired(Model model) {
        model.addAttribute("message", "Please log in to make a reservation, or continue as a guest.");
        return "reservations/login-prompt";
    }

    // Guest reservation form
    @GetMapping("/guest")
    public String guestReservationForm(Model model) {
        model.addAttribute("guestRequest", new GuestReservationRequest());
        model.addAttribute("tables", tableService.findAvailableTables());
        return "reservations/guest-form";
    }

    // Process guest reservation
    @PostMapping("/guest")
    public String processGuestReservation(@Valid @ModelAttribute("guestRequest") GuestReservationRequest request,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tables", tableService.findAvailableTables());
            return "reservations/guest-form";
        }

        try {
            // Check availability
            Map<String, Object> availability = availabilityService.checkAvailability(
                request.getReservationDateTime(), request.getNumberOfGuests());
            
            if (!(Boolean) availability.get("isAvailable")) {
                model.addAttribute("error", "Selected time slot is no longer available.");
                model.addAttribute("alternatives", availability.get("alternatives"));
                model.addAttribute("tables", tableService.findAvailableTables());
                return "reservations/guest-form";
            }

            // Check for conflicts
            if (availabilityService.detectConflictingBookings(request.getReservationDateTime(), request.getNumberOfGuests())) {
                model.addAttribute("warning", "High demand at this time. We recommend booking an alternative slot.");
                model.addAttribute("alternatives", availability.get("alternatives"));
            }

            // Create guest reservation
            Reservation reservation = createGuestReservation(request);
            
            // Send confirmation
            notificationService.sendGuestReservationConfirmation(request.getGuestEmail(), reservation);
            
            redirectAttributes.addFlashAttribute("message", "Reservation confirmed! Check your email for details.");
            return "redirect:/reservations/confirmation/" + reservation.getId();
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create reservation: " + e.getMessage());
            model.addAttribute("tables", tableService.findAvailableTables());
            return "reservations/guest-form";
        }
    }

    // Enhanced user reservation form
    @GetMapping("/new")
    public String newReservationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userService.findByEmail(username);
        
        model.addAttribute("reservationRequest", new ReservationRequest());
        model.addAttribute("tables", tableService.findAvailableTables());
        model.addAttribute("user", user.orElse(null));
        model.addAttribute("loyaltyTier", user.map(u -> loyaltyService.getLoyaltyTier(u)).orElse("Bronze"));
        model.addAttribute("loyaltyBenefits", user.map(u -> loyaltyService.getLoyaltyBenefits(u)).orElse(""));
        model.addAttribute("loyaltyPoints", user.map(u -> u.getLoyaltyPoints()).orElse(0));
        
        return "reservations/enhanced-form";
    }

    // Process user reservation with loyalty points
    @PostMapping
    public String createReservation(@Valid @ModelAttribute("reservationRequest") ReservationRequest request,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            model.addAttribute("tables", tableService.findAvailableTables());
            model.addAttribute("user", user.orElse(null));
            model.addAttribute("loyaltyTier", user.map(u -> loyaltyService.getLoyaltyTier(u)).orElse("Bronze"));
            model.addAttribute("loyaltyBenefits", user.map(u -> loyaltyService.getLoyaltyBenefits(u)).orElse(""));
            model.addAttribute("loyaltyPoints", user.map(u -> u.getLoyaltyPoints()).orElse(0));
            return "reservations/enhanced-form";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (!user.isPresent()) {
                model.addAttribute("error", "User not found. Please log in again.");
                model.addAttribute("tables", tableService.findAvailableTables());
                return "reservations/enhanced-form";
            }

            // Check availability
            Map<String, Object> availability = availabilityService.checkAvailability(
                request.getReservationDateTime(), request.getNumberOfGuests());
            
            if (!(Boolean) availability.get("isAvailable")) {
                model.addAttribute("error", "Selected time slot is no longer available.");
                model.addAttribute("alternatives", availability.get("alternatives"));
                model.addAttribute("tables", tableService.findAvailableTables());
                model.addAttribute("user", user.get());
                model.addAttribute("loyaltyTier", loyaltyService.getLoyaltyTier(user.get()));
                model.addAttribute("loyaltyBenefits", loyaltyService.getLoyaltyBenefits(user.get()));
                model.addAttribute("loyaltyPoints", user.get().getLoyaltyPoints() != null ? user.get().getLoyaltyPoints() : 0);
                return "reservations/enhanced-form";
            }

            // Create reservation
            Reservation reservation = reservationService.createReservation(user.get(), request);
            
            // Award loyalty points
            loyaltyService.awardPointsForReservation(user.get(), request.getNumberOfGuests());
            
            // Send confirmation
            notificationService.sendReservationConfirmation(reservation);
            
            redirectAttributes.addFlashAttribute("message", "Reservation confirmed! You earned loyalty points!");
            return "redirect:/reservations/confirmation/" + reservation.getId();
            
        } catch (Exception e) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            model.addAttribute("error", "Failed to create reservation: " + e.getMessage());
            model.addAttribute("tables", tableService.findAvailableTables());
            model.addAttribute("user", user.orElse(null));
            model.addAttribute("loyaltyTier", user.map(u -> loyaltyService.getLoyaltyTier(u)).orElse("Bronze"));
            model.addAttribute("loyaltyBenefits", user.map(u -> loyaltyService.getLoyaltyBenefits(u)).orElse(""));
            model.addAttribute("loyaltyPoints", user.map(u -> u.getLoyaltyPoints()).orElse(0));
            return "reservations/enhanced-form";
        }
    }

    // Group reservation form
    @GetMapping("/group")
    public String groupReservationForm(Model model) {
        model.addAttribute("guestRequest", new GuestReservationRequest());
        model.addAttribute("tables", tableService.findAvailableTables());
        return "reservations/group-form";
    }

    // Process group reservation
    @PostMapping("/group")
    public String processGroupReservation(@Valid @ModelAttribute("guestRequest") GuestReservationRequest request,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tables", tableService.findAvailableTables());
            return "reservations/group-form";
        }

        try {
            // Handle large group reservations
            if (request.isRequiresMultipleTables()) {
                List<RestaurantTable> suggestedTables = availabilityService.getTablesForGroupReservation(request.getNumberOfGuests());
                model.addAttribute("suggestedTables", suggestedTables);
                model.addAttribute("groupSize", request.getNumberOfGuests());
                return "reservations/group-table-selection";
            }

            // Process as regular reservation
            return processGuestReservation(request, bindingResult, model, redirectAttributes);
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to process group reservation: " + e.getMessage());
            model.addAttribute("tables", tableService.findAvailableTables());
            return "reservations/group-form";
        }
    }

    // Confirmation page
    @GetMapping("/confirmation/{id}")
    public String confirmationPage(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            return "redirect:/reservations";
        }

        model.addAttribute("reservation", reservation);
        return "reservations/confirmation";
    }

    @Autowired
    private com.restaurant.payment.PaymentServiceManager paymentServiceManager;

    // Payment integration using Adapter Pattern
    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            return "redirect:/reservations";
        }

        // Get available payment processors and methods
        model.addAttribute("reservation", reservation);
        model.addAttribute("availableProcessors", paymentServiceManager.getProcessorStatus());
        model.addAttribute("supportedMethods", paymentServiceManager.getAllSupportedPaymentMethods());
        
        return "reservations/payment";
    }

    // Process payment using Adapter Pattern
    @PostMapping("/payment/{id}")
    public String processPayment(@PathVariable Long id, 
                               @RequestParam String paymentMethod,
                               @RequestParam(required = false) String processor,
                               RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.findById(id);
            if (reservation == null) {
                redirectAttributes.addFlashAttribute("error", "Reservation not found");
                return "redirect:/reservations";
            }

            // Calculate payment amount (e.g., deposit amount)
            double amount = 25.00; // Example deposit amount
            
            // Process payment using the specified processor or default
            com.restaurant.payment.PaymentResult result;
            if (processor != null && !processor.isEmpty()) {
                result = paymentServiceManager.processReservationPayment(processor, reservation, amount, paymentMethod);
            } else {
                result = paymentServiceManager.processReservationPayment(reservation, amount, paymentMethod);
            }
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("message", 
                    "Payment processed successfully! Transaction ID: " + result.getTransactionId());
                redirectAttributes.addFlashAttribute("processor", result.getPaymentMethod());
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "Payment failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Payment processing error: " + e.getMessage());
        }
        
        return "redirect:/reservations/confirmation/" + id;
    }

    private Reservation createGuestReservation(GuestReservationRequest request) {
        // Create a temporary user for guest reservations
        User guestUser = new User();
        guestUser.setName(request.getGuestName());
        guestUser.setEmail(request.getGuestEmail());
        guestUser.setPhone(request.getGuestPhone());
        guestUser.setRole(User.Role.CUSTOMER);
        
        // Convert to regular reservation request
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setReservationDate(request.getReservationDate());
        reservationRequest.setReservationTime(request.getReservationTime());
        reservationRequest.setNumberOfGuests(request.getNumberOfGuests());
        reservationRequest.setSpecialRequests(request.getSpecialRequests());
        
        return reservationService.createReservation(guestUser, reservationRequest);
    }
}
