package com.restaurant.repository;

import com.restaurant.model.EventStaffAssignment;
import com.restaurant.model.EventBooking;
import com.restaurant.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventStaffAssignmentRepository extends JpaRepository<EventStaffAssignment, Long> {
    
    List<EventStaffAssignment> findByEventBooking(EventBooking eventBooking);
    
    List<EventStaffAssignment> findByStaff(Staff staff);
    
    List<EventStaffAssignment> findByEventBookingAndStatus(EventBooking eventBooking, EventStaffAssignment.AssignmentStatus status);
    
    @Query("SELECT esa FROM EventStaffAssignment esa " +
           "JOIN esa.eventBooking eb " +
           "WHERE eb.eventDate = :date " +
           "AND esa.status IN ('ASSIGNED', 'CONFIRMED', 'IN_PROGRESS')")
    List<EventStaffAssignment> findActiveAssignmentsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT esa FROM EventStaffAssignment esa " +
           "JOIN esa.eventBooking eb " +
           "WHERE eb.eventDate BETWEEN :startDate AND :endDate " +
           "AND esa.status IN ('ASSIGNED', 'CONFIRMED', 'IN_PROGRESS')")
    List<EventStaffAssignment> findActiveAssignmentsForDateRange(@Param("startDate") LocalDate startDate, 
                                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(esa) FROM EventStaffAssignment esa " +
           "JOIN esa.eventBooking eb " +
           "WHERE esa.staff = :staff " +
           "AND eb.eventDate = :date " +
           "AND esa.status IN ('ASSIGNED', 'CONFIRMED', 'IN_PROGRESS')")
    Long countActiveAssignmentsForStaffOnDate(@Param("staff") Staff staff, @Param("date") LocalDate date);
    
    @Query("SELECT esa FROM EventStaffAssignment esa " +
           "WHERE esa.staff = :staff " +
           "AND esa.status IN ('ASSIGNED', 'CONFIRMED', 'IN_PROGRESS')")
    List<EventStaffAssignment> findActiveAssignmentsForStaff(Staff staff);
}
