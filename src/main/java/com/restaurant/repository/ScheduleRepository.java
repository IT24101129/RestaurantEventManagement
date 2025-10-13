package com.restaurant.repository;

import com.restaurant.model.Schedule;
import com.restaurant.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByScheduleDate(LocalDate date);
    
    List<Schedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Schedule> findByStaff(Staff staff);
    
    List<Schedule> findByStaffAndScheduleDateBetween(Staff staff, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Schedule s WHERE s.staff = :staff AND s.scheduleDate = :date " +
           "AND s.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findConflictingSchedules(@Param("staff") Staff staff, 
                                           @Param("date") LocalDate date,
                                           @Param("startTime") LocalTime startTime,
                                           @Param("endTime") LocalTime endTime);
    
    @Query("SELECT s FROM Schedule s WHERE s.staff = :staff AND s.scheduleDate = :date " +
           "AND s.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND s.id != :excludeId " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findConflictingSchedulesExcluding(@Param("staff") Staff staff, 
                                                    @Param("date") LocalDate date,
                                                    @Param("startTime") LocalTime startTime,
                                                    @Param("endTime") LocalTime endTime,
                                                    @Param("excludeId") Long excludeId);
    
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.scheduleDate, s.startTime")
    List<Schedule> findSchedulesInDateRange(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.staff = :staff AND s.scheduleDate BETWEEN :startDate AND :endDate " +
           "AND s.status IN ('SCHEDULED', 'CONFIRMED')")
    Long countScheduledHoursForStaff(@Param("staff") Staff staff, 
                                    @Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
}
