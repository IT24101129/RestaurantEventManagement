package com.restaurant.repository;

import com.restaurant.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByIsAvailableTrue();
    
    List<Event> findByEventType(Event.EventType eventType);
    
    List<Event> findByIsAvailableTrueAndEventType(Event.EventType eventType);
    
    @Query("SELECT e FROM Event e WHERE e.isAvailable = true AND e.capacity >= :guestCount")
    List<Event> findAvailableEventsForGuestCount(@Param("guestCount") Integer guestCount);
    
    @Query("SELECT e FROM Event e WHERE e.isAvailable = true AND e.eventType = :eventType AND e.capacity >= :guestCount")
    List<Event> findAvailableEventsByTypeAndGuestCount(@Param("eventType") Event.EventType eventType, 
                                                      @Param("guestCount") Integer guestCount);
    
    @Query("SELECT e FROM Event e WHERE e.isAvailable = true AND e.basePrice <= :maxPrice")
    List<Event> findAvailableEventsWithinBudget(@Param("maxPrice") Double maxPrice);
    
    @Query("SELECT e FROM Event e WHERE e.isAvailable = true AND e.eventType = :eventType AND e.capacity >= :guestCount AND e.basePrice <= :maxPrice")
    List<Event> findAvailableEventsWithFilters(@Param("eventType") Event.EventType eventType,
                                              @Param("guestCount") Integer guestCount,
                                              @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT e FROM Event e WHERE e.isAvailable = true AND e.id NOT IN " +
           "(SELECT eb.event.id FROM EventBooking eb WHERE eb.eventDate = :date " +
           "AND eb.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS') " +
           "AND ((eb.startTime < :endTime AND eb.endTime > :startTime)))")
    List<Event> findAvailableEventsForTimeSlot(@Param("date") LocalDate date,
                                              @Param("startTime") LocalTime startTime,
                                              @Param("endTime") LocalTime endTime);
}
