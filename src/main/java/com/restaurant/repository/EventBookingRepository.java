package com.restaurant.repository;

import com.restaurant.model.EventBooking;
import com.restaurant.model.Event;
import com.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {
    
    List<EventBooking> findByUser(User user);
    
    List<EventBooking> findByEvent(Event event);
    
    List<EventBooking> findByEventDate(LocalDate eventDate);
    
    List<EventBooking> findByStatus(EventBooking.BookingStatus status);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.eventDate BETWEEN :startDate AND :endDate")
    List<EventBooking> findBookingsInDateRange(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.event = :event AND eb.eventDate = :date " +
           "AND eb.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS') " +
           "AND ((eb.startTime < :endTime AND eb.endTime > :startTime))")
    List<EventBooking> findConflictingBookings(@Param("event") Event event,
                                              @Param("date") LocalDate date,
                                              @Param("startTime") LocalTime startTime,
                                              @Param("endTime") LocalTime endTime);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.event = :event AND eb.eventDate = :date " +
           "AND eb.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS') " +
           "AND eb.id != :excludeId " +
           "AND ((eb.startTime < :endTime AND eb.endTime > :startTime))")
    List<EventBooking> findConflictingBookingsExcluding(@Param("event") Event event,
                                                        @Param("date") LocalDate date,
                                                        @Param("startTime") LocalTime startTime,
                                                        @Param("endTime") LocalTime endTime,
                                                        @Param("excludeId") Long excludeId);
    
    @Query("SELECT COUNT(eb) FROM EventBooking eb WHERE eb.event = :event AND eb.eventDate = :date " +
           "AND eb.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')")
    Long countBookingsForEventOnDate(@Param("event") Event event, @Param("date") LocalDate date);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.eventDate = :date " +
           "AND eb.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS') " +
           "ORDER BY eb.startTime")
    List<EventBooking> findBookingsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.clientEmail = :email " +
           "ORDER BY eb.eventDate DESC, eb.createdAt DESC")
    List<EventBooking> findBookingsByClientEmail(@Param("email") String email);
    
    @Query("SELECT eb FROM EventBooking eb WHERE eb.status = 'PENDING' " +
           "ORDER BY eb.createdAt ASC")
    List<EventBooking> findPendingBookings();
    
    List<EventBooking> findByEventDateBetweenOrderByEventDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);
}
