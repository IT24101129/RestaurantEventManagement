package com.restaurant.service;

import com.restaurant.model.Event;
import com.restaurant.model.EventBooking;
import com.restaurant.model.User;
import com.restaurant.repository.EventBookingRepository;
import com.restaurant.repository.EventRepository;
import com.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventBookingService {
    
    private final EventBookingRepository eventBookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public EventBookingService(EventBookingRepository eventBookingRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.eventBookingRepository = eventBookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    public List<EventBooking> getAllBookings() {
        return eventBookingRepository.findAll();
    }
    
    public List<EventBooking> getBookingsByUser(User user) {
        return eventBookingRepository.findByUser(user);
    }
    
    public List<EventBooking> getBookingsByEvent(Event event) {
        return eventBookingRepository.findByEvent(event);
    }
    
    public List<EventBooking> getBookingsByDate(LocalDate date) {
        return eventBookingRepository.findBookingsForDate(date);
    }
    
    public List<EventBooking> getBookingsInDateRange(LocalDate startDate, LocalDate endDate) {
        return eventBookingRepository.findBookingsInDateRange(startDate, endDate);
    }
    
    public List<EventBooking> getBookingsByStatus(EventBooking.BookingStatus status) {
        return eventBookingRepository.findByStatus(status);
    }
    
    public List<EventBooking> getPendingBookings() {
        return eventBookingRepository.findPendingBookings();
    }
    
    public List<EventBooking> getBookingsByClientEmail(String email) {
        return eventBookingRepository.findBookingsByClientEmail(email);
    }
    
    public Optional<EventBooking> getBookingById(Long id) {
        return eventBookingRepository.findById(id);
    }
    
    public EventBookingConflictResult checkForConflicts(EventBooking newBooking) {
        List<EventBooking> conflicts = eventBookingRepository.findConflictingBookings(
            newBooking.getEvent(),
            newBooking.getEventDate(),
            newBooking.getStartTime(),
            newBooking.getEndTime()
        );
        
        if (!conflicts.isEmpty()) {
            return new EventBookingConflictResult(true, conflicts, 
                "Time slot conflicts with existing booking(s)");
        }
        
        // Check if event capacity is exceeded
        Long bookingCount = eventBookingRepository.countBookingsForEventOnDate(
            newBooking.getEvent(), newBooking.getEventDate());
        
        if (bookingCount >= 1) { // Assuming only one booking per event per day
            return new EventBookingConflictResult(true, conflicts, 
                "Event is already booked for this date");
        }
        
        return new EventBookingConflictResult(false, conflicts, "No conflicts found");
    }
    
    public EventBookingConflictResult checkForConflictsExcluding(EventBooking newBooking, Long excludeId) {
        List<EventBooking> conflicts = eventBookingRepository.findConflictingBookingsExcluding(
            newBooking.getEvent(),
            newBooking.getEventDate(),
            newBooking.getStartTime(),
            newBooking.getEndTime(),
            excludeId
        );
        
        if (!conflicts.isEmpty()) {
            return new EventBookingConflictResult(true, conflicts, 
                "Time slot conflicts with existing booking(s)");
        }
        
        return new EventBookingConflictResult(false, conflicts, "No conflicts found");
    }
    
    public EventBooking saveBooking(EventBooking booking) {
        try {
            // Set created by if not set
            if (booking.getCreatedBy() == null) {
                booking.setCreatedBy("Event Coordinator");
            }
            
            EventBooking savedBooking = eventBookingRepository.save(booking);
            
            // Send confirmation notification
            notificationService.sendEventBookingConfirmation(savedBooking);
            
            return savedBooking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save event booking: " + e.getMessage(), e);
        }
    }
    
    public EventBooking updateBooking(EventBooking booking) {
        try {
            EventBooking updatedBooking = eventBookingRepository.save(booking);
            
            // Send update notification
            notificationService.sendEventBookingUpdate(updatedBooking);
            
            return updatedBooking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update event booking: " + e.getMessage(), e);
        }
    }
    
    public void deleteBooking(Long id) {
        try {
            Optional<EventBooking> bookingOpt = getBookingById(id);
            if (bookingOpt.isPresent()) {
                EventBooking booking = bookingOpt.get();
                eventBookingRepository.deleteById(id);
                
                // Send cancellation notification
                notificationService.sendEventBookingCancellation(booking);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete event booking: " + e.getMessage(), e);
        }
    }
    
    public EventBooking createBooking(Long eventId, Long userId, String clientName, String clientEmail,
                                    String clientPhone, LocalDate eventDate, LocalTime startTime,
                                    LocalTime endTime, Integer guestCount, String specialRequirements,
                                    String notes, String createdBy) {
        
        // Validate required fields
        if (clientName == null || clientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Client email is required");
        }
        if (clientPhone == null || clientPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Client phone is required");
        }
        
        // Get event and user
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Calculate total price
        Double totalPrice = calculateTotalPrice(event, guestCount, specialRequirements);
        
        // Create booking
        EventBooking booking = new EventBooking(event, user, clientName, clientEmail, clientPhone,
                                               eventDate, startTime, endTime, guestCount, totalPrice);
        booking.setSpecialRequirements(specialRequirements);
        booking.setNotes(notes);
        booking.setCreatedBy(createdBy);
        
        // Check for conflicts
        EventBookingConflictResult conflictResult = checkForConflicts(booking);
        if (conflictResult.hasConflict()) {
            throw new IllegalArgumentException(conflictResult.getMessage());
        }
        
        return saveBooking(booking);
    }
    
    private Double calculateTotalPrice(Event event, Integer guestCount, String specialRequirements) {
        Double basePrice = event.getBasePrice();
        Double totalPrice = basePrice;
        
        // Add charges for additional guests if capacity is exceeded
        if (guestCount > event.getCapacity()) {
            Integer extraGuests = guestCount - event.getCapacity();
            totalPrice += (extraGuests * 25.0); // $25 per extra guest
        }
        
        // Add charges for special requirements
        if (specialRequirements != null && !specialRequirements.trim().isEmpty()) {
            totalPrice += 50.0; // $50 for special requirements
        }
        
        return totalPrice;
    }
    
    public List<LocalTime> getAvailableTimeSlots(LocalDate date, Long eventId) {
        List<LocalTime> availableSlots = List.of();
        
        // Generate time slots from 9 AM to 10 PM (1-hour slots)
        for (int hour = 9; hour <= 21; hour++) {
            LocalTime startTime = LocalTime.of(hour, 0);
            LocalTime endTime = startTime.plusHours(1);
            
            // Check if this time slot is available
            if (isTimeSlotAvailable(date, startTime, endTime, eventId)) {
                availableSlots.add(startTime);
            }
        }
        
        return availableSlots;
    }
    
    private boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        List<EventBooking> conflicts = eventBookingRepository.findConflictingBookings(
            event, date, startTime, endTime);
        
        return conflicts.isEmpty();
    }
    
    // Inner class for conflict results
    public static class EventBookingConflictResult {
        private final boolean hasConflict;
        private final List<EventBooking> conflictingBookings;
        private final String message;
        
        public EventBookingConflictResult(boolean hasConflict, List<EventBooking> conflictingBookings, String message) {
            this.hasConflict = hasConflict;
            this.conflictingBookings = conflictingBookings;
            this.message = message;
        }
        
        public boolean hasConflict() {
            return hasConflict;
        }
        
        public List<EventBooking> getConflictingBookings() {
            return conflictingBookings;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
