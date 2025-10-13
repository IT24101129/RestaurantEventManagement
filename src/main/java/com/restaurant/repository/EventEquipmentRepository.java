package com.restaurant.repository;

import com.restaurant.model.EventEquipment;
import com.restaurant.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventEquipmentRepository extends JpaRepository<EventEquipment, Long> {
    
    List<EventEquipment> findByEventBooking(EventBooking eventBooking);
    
    List<EventEquipment> findByEventBookingAndStatus(EventBooking eventBooking, EventEquipment.EquipmentStatus status);
    
    @Query("SELECT ee FROM EventEquipment ee " +
           "JOIN ee.eventBooking eb " +
           "WHERE eb.eventDate = :date " +
           "AND ee.status IN ('REQUESTED', 'CONFIRMED', 'SETUP', 'IN_USE')")
    List<EventEquipment> findActiveEquipmentForDate(@Param("date") LocalDate date);
    
    @Query("SELECT ee FROM EventEquipment ee " +
           "JOIN ee.eventBooking eb " +
           "WHERE eb.eventDate BETWEEN :startDate AND :endDate " +
           "AND ee.status IN ('REQUESTED', 'CONFIRMED', 'SETUP', 'IN_USE')")
    List<EventEquipment> findActiveEquipmentForDateRange(@Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ee.equipmentType, SUM(ee.quantity) FROM EventEquipment ee " +
           "JOIN ee.eventBooking eb " +
           "WHERE eb.eventDate = :date " +
           "AND ee.status IN ('REQUESTED', 'CONFIRMED', 'SETUP', 'IN_USE') " +
           "GROUP BY ee.equipmentType")
    List<Object[]> getEquipmentUsageByTypeForDate(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(ee.totalCost) FROM EventEquipment ee " +
           "JOIN ee.eventBooking eb " +
           "WHERE eb.eventDate BETWEEN :startDate AND :endDate " +
           "AND ee.status IN ('REQUESTED', 'CONFIRMED', 'SETUP', 'IN_USE')")
    Double getTotalEquipmentCostForDateRange(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}
