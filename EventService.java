package com.resturant.restaurantapp.service;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.repository.EventRepository;
import com.resturant.restaurantapp.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Transactional
    public Event createEvent(Event event) {
        System.out.println("Creating event: " + event.getCustomerName() + " for " + event.getEventDate());
        
        // Validate unique constraints - only if eventId is already set
        if (event.getEventId() != null && eventRepository.existsByEventId(event.getEventId())) {
            throw new RuntimeException("Event ID already exists: " + event.getEventId());
        }
        
        // Check for time conflicts - temporarily disabled for testing
        /*
        Long conflictCount = eventRepository.countByDateTimeAndActiveStatus(event.getEventDate(), event.getEventTime());
        if (conflictCount != null && conflictCount > 0) {
            throw new RuntimeException("Time slot is already booked for " + event.getEventDate() + " at " + event.getEventTime());
        }
        */
        System.out.println("Time conflict check temporarily disabled for testing");
        
        // Calculate total cost based on package and number of guests
        event.setTotalCost(calculateEventCost(event));
        
        Event savedEvent = eventRepository.save(event);
        System.out.println("Event saved with ID: " + savedEvent.getId() + ", Event ID: " + savedEvent.getEventId());
        
        // Log the event creation
        auditService.logEventCreation(savedEvent, "system");
        
        return savedEvent;
    }
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDate.now());
    }
    
    public List<Event> getActiveBookings() {
        return eventRepository.findActiveBookings();
    }
    
    public List<Event> getEventsByType(Event.EventType eventType) {
        return eventRepository.findByEventType(eventType);
    }
    
    public List<Event> getEventsByStatus(Event.BookingStatus status) {
        return eventRepository.findByBookingStatus(status);
    }
    
    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByEventDate(date);
    }
    
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findEventsByDateRange(startDate, endDate);
    }
    
    public List<Event> getEventsByCustomer(String customerName, String customerPhone) {
        return eventRepository.findByCustomerNameAndCustomerPhone(customerName, customerPhone);
    }
    
    public List<Event> searchEventsByCustomerName(String customerName) {
        return eventRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }
    
    public List<Event> getEventsByAssignedStaff(String staffName) {
        return eventRepository.findEventsByAssignedStaff(staffName);
    }
    
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
    
    public Optional<Event> getEventByEventId(String eventId) {
        return eventRepository.findByEventId(eventId);
    }
    
    public Event updateEvent(Event event) {
        if (event.getId() == null) {
            throw new RuntimeException("Event ID cannot be null for update");
        }
        
        Optional<Event> existingEvent = eventRepository.findById(event.getId());
        if (existingEvent.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + event.getId());
        }
        
        // Preserve the original createdAt, updatedAt, and eventId values
        Event existing = existingEvent.get();
        event.setCreatedAt(existing.getCreatedAt()); // Preserve original creation time
        event.setUpdatedAt(LocalDateTime.now()); // Set new update time
        event.setEventId(existing.getEventId()); // Preserve original event ID
        
        // Recalculate cost if package or guest count changed
        if (!existing.getEventPackage().equals(event.getEventPackage()) || 
            !existing.getNumberOfGuests().equals(event.getNumberOfGuests())) {
            event.setTotalCost(calculateEventCost(event));
        }
        
        // Log the event update before saving
        auditService.logEventUpdate(existing, event, "system");
        
        return eventRepository.save(event);
    }
    
    public void deleteEvent(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + id);
        }
        
        // Log the event deletion before deleting
        auditService.logEventDeletion(event.get(), "system");
        
        eventRepository.deleteById(id);
    }
    
    public Event confirmEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + id);
        }
        
        Event event = eventOpt.get();
        event.setBookingStatus(Event.BookingStatus.CONFIRMED);
        return eventRepository.save(event);
    }
    
    public Event cancelEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + id);
        }
        
        Event event = eventOpt.get();
        event.setBookingStatus(Event.BookingStatus.CANCELLED);
        return eventRepository.save(event);
    }
    
    public Event startEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + id);
        }
        
        Event event = eventOpt.get();
        event.setBookingStatus(Event.BookingStatus.IN_PROGRESS);
        return eventRepository.save(event);
    }
    
    public Event completeEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found with ID: " + id);
        }
        
        Event event = eventOpt.get();
        event.setBookingStatus(Event.BookingStatus.COMPLETED);
        return eventRepository.save(event);
    }
    
    public boolean isTimeSlotAvailable(LocalDate date, LocalTime time) {
        try {
            System.out.println("Checking availability for date: " + date + ", time: " + time);
            Long count = eventRepository.countByDateTimeAndActiveStatus(date, time);
            System.out.println("Found " + count + " conflicting events");
            boolean available = count == null || count == 0;
            System.out.println("Time slot available: " + available);
            return available;
        } catch (Exception e) {
            System.out.println("Error checking availability: " + e.getMessage());
            // If there's an error (like table doesn't exist), assume it's available
            return true;
        }
    }
    
    public Long getEventCountByDate(LocalDate date) {
        return eventRepository.countEventsByDate(date);
    }
    
    private Double calculateEventCost(Event event) {
        // Base cost per guest for different packages
        double baseCostPerGuest = 0.0;
        
        switch (event.getEventPackage()) {
            case BASIC:
                baseCostPerGuest = 2500.0; // LKR 2,500 per guest
                break;
            case STANDARD:
                baseCostPerGuest = 3500.0; // LKR 3,500 per guest
                break;
            case PREMIUM:
                baseCostPerGuest = 5000.0; // LKR 5,000 per guest
                break;
            case DELUXE:
                baseCostPerGuest = 7500.0; // LKR 7,500 per guest
                break;
            case CUSTOM:
                baseCostPerGuest = 4000.0; // LKR 4,000 per guest (default)
                break;
        }
        
        return baseCostPerGuest * event.getNumberOfGuests();
    }
    
    public List<Event> getEventsForToday() {
        return getEventsByDate(LocalDate.now());
    }
    
    public List<Event> getPendingApprovals() {
        return getEventsByStatus(Event.BookingStatus.PENDING);
    }
}

