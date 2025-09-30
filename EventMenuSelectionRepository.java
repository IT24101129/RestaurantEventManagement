package com.resturant.restaurantapp.repository;

import com.resturant.restaurantapp.model.EventMenuSelection;
import com.resturant.restaurantapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventMenuSelectionRepository extends JpaRepository<EventMenuSelection, Long> {
    
    // Find menu selection by event
    Optional<EventMenuSelection> findByEvent(Event event);
    
    // Find menu selection by event ID
    Optional<EventMenuSelection> findByEventId(Long eventId);
    
    // Find menu selections by status
    List<EventMenuSelection> findBySelectionStatus(EventMenuSelection.SelectionStatus status);
    
    // Find pending menu selections
    @Query("SELECT ems FROM EventMenuSelection ems WHERE ems.selectionStatus = 'PENDING'")
    List<EventMenuSelection> findPendingSelections();
    
    // Find confirmed menu selections
    @Query("SELECT ems FROM EventMenuSelection ems WHERE ems.selectionStatus = 'CONFIRMED'")
    List<EventMenuSelection> findConfirmedSelections();
    
    // Find menu selections by selection ID
    Optional<EventMenuSelection> findBySelectionId(String selectionId);
    
    // Check if event already has a menu selection
    boolean existsByEventId(Long eventId);
    
    // Find menu selections for events happening today
    @Query("SELECT ems FROM EventMenuSelection ems WHERE ems.event.eventDate = :today")
    List<EventMenuSelection> findTodaysMenuSelections(@Param("today") java.time.LocalDate today);
    
    // Find menu selections for upcoming events
    @Query("SELECT ems FROM EventMenuSelection ems WHERE ems.event.eventDate >= :today")
    List<EventMenuSelection> findUpcomingMenuSelections(@Param("today") java.time.LocalDate today);
}
