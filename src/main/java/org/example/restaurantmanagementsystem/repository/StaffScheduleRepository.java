package org.example.restaurantmanagementsystem.repository;

import com.restaurant.entity.Staff;
import com.restaurant.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface StaffScheduleRepository<StaffSchedule, Staff> extends JpaRepository<StaffSchedule, Long> {
    List<StaffSchedule> findByStaff(Staff staff);
    List<StaffSchedule> findByScheduleDate(LocalDate scheduleDate);
    List<StaffSchedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.staff = :staff AND ss.scheduleDate = :date AND ss.status != 'CANCELLED'")
    List<StaffSchedule> findExistingSchedules(@Param("staff") Staff staff, @Param("date") LocalDate date);

    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.staff = :staff AND ss.scheduleDate = :date AND ss.status != 'CANCELLED' " +
            "AND ((ss.startTime <= :endTime AND ss.endTime >= :startTime))")
    List<StaffSchedule> findConflictingSchedules(@Param("staff") Staff staff,
                                                 @Param("date") LocalDate date,
                                                 @Param("startTime") LocalTime startTime,
                                                 @Param("endTime") LocalTime endTime);

    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.scheduleDate >= :startDate AND ss.scheduleDate <= :endDate ORDER BY ss.scheduleDate, ss.startTime")
    List<StaffSchedule> findSchedulesInDateRange(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}