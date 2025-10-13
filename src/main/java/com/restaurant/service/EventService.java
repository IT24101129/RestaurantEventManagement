package com.restaurant.service;

import com.restaurant.model.Event;
import com.restaurant.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {
    
    private final EventRepository eventRepository;
    
    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    public List<Event> getAllAvailableEvents() {
        return eventRepository.findByIsAvailableTrue();
    }
    
    public List<Event> getEventsByType(Event.EventType eventType) {
        return eventRepository.findByEventType(eventType);
    }
    
    public List<Event> getAvailableEventsByType(Event.EventType eventType) {
        return eventRepository.findByIsAvailableTrueAndEventType(eventType);
    }
    
    public List<Event> getAvailableEventsForGuestCount(Integer guestCount) {
        return eventRepository.findAvailableEventsForGuestCount(guestCount);
    }
    
    public List<Event> getAvailableEventsByTypeAndGuestCount(Event.EventType eventType, Integer guestCount) {
        return eventRepository.findAvailableEventsByTypeAndGuestCount(eventType, guestCount);
    }
    
    public List<Event> getAvailableEventsWithinBudget(Double maxPrice) {
        return eventRepository.findAvailableEventsWithinBudget(maxPrice);
    }
    
    public List<Event> getAvailableEventsWithFilters(Event.EventType eventType, Integer guestCount, Double maxPrice) {
        return eventRepository.findAvailableEventsWithFilters(eventType, guestCount, maxPrice);
    }
    
    public List<Event> getAvailableEventsForTimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return eventRepository.findAvailableEventsForTimeSlot(date, startTime, endTime);
    }
    
    public List<Event> suggestEventPackages(Event.EventType eventType, Integer guestCount, Double maxPrice) {
        if (eventType != null && guestCount != null && maxPrice != null) {
            return getAvailableEventsWithFilters(eventType, guestCount, maxPrice);
        } else if (eventType != null && guestCount != null) {
            return getAvailableEventsByTypeAndGuestCount(eventType, guestCount);
        } else if (eventType != null) {
            return getAvailableEventsByType(eventType);
        } else if (guestCount != null) {
            return getAvailableEventsForGuestCount(guestCount);
        } else if (maxPrice != null) {
            return getAvailableEventsWithinBudget(maxPrice);
        } else {
            return getAllAvailableEvents();
        }
    }
    
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
    
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public boolean isEventAvailable(Long eventId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Optional<Event> eventOpt = getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        if (!event.getIsAvailable()) {
            return false;
        }
        
        List<Event> availableEvents = getAvailableEventsForTimeSlot(date, startTime, endTime);
        return availableEvents.stream().anyMatch(e -> e.getId().equals(eventId));
    }
    
    public List<LocalDate> getAvailableDatesForEvent(Long eventId, LocalDate startDate, LocalDate endDate) {
        Optional<Event> eventOpt = getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return List.of();
        }
        
        Event event = eventOpt.get();
        if (!event.getIsAvailable()) {
            return List.of();
        }
        
        List<LocalDate> availableDates = List.of();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            // Check if the event is available for the entire day
            LocalTime startTime = LocalTime.of(9, 0); // 9 AM
            LocalTime endTime = LocalTime.of(22, 0);  // 10 PM
            
            if (isEventAvailable(eventId, currentDate, startTime, endTime)) {
                availableDates.add(currentDate);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return availableDates;
    }
}
