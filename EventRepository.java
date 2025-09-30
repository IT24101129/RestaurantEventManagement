package com.resturant.restaurantapp.repository;

import com.resturant.restaurantapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    Optional<Event> findByEventId(String eventId);
    
    List<Event> findByEventType(Event.EventType eventType);
    
    List<Event> findByEventPackage(Event.EventPackage eventPackage);
    
    List<Event> findByBookingStatus(Event.BookingStatus bookingStatus);
    
    List<Event> findByEventDate(LocalDate eventDate);
    
    List<Event> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Event> findByCustomerPhone(String customerPhone);
    
    List<Event> findByCustomerNameAndCustomerPhone(String customerName, String customerPhone);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate ORDER BY e.eventDate, e.eventTime")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today ORDER BY e.eventDate, e.eventTime")
    List<Event> findUpcomingEvents(@Param("today") LocalDate today);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate = :date AND e.eventTime = :time")
    List<Event> findEventsByDateTime(@Param("date") LocalDate date, @Param("time") java.time.LocalTime time);
    
    @Query("SELECT e FROM Event e WHERE e.eventType = :eventType AND e.bookingStatus = :status ORDER BY e.eventDate")
    List<Event> findEventsByTypeAndStatus(@Param("eventType") Event.EventType eventType, @Param("status") Event.BookingStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.assignedStaff = :staffName ORDER BY e.eventDate")
    List<Event> findEventsByAssignedStaff(@Param("staffName") String staffName);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate = :date")
    Long countEventsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT e FROM Event e WHERE e.bookingStatus IN ('PENDING', 'CONFIRMED') ORDER BY e.eventDate, e.eventTime")
    List<Event> findActiveBookings();
    
    boolean existsByEventId(String eventId);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate = :date AND e.eventTime = :time AND e.bookingStatus IN ('CONFIRMED', 'IN_PROGRESS')")
    Long countByDateTimeAndActiveStatus(@Param("date") LocalDate date, @Param("time") java.time.LocalTime time);
}

