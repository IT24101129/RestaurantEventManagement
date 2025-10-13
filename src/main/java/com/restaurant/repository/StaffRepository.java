package com.restaurant.repository;

import com.restaurant.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    Optional<Staff> findByEmail(String email);
    
    List<Staff> findByIsAvailableTrue();
    
    List<Staff> findByPosition(Staff.Position position);
    
    List<Staff> findByIsAvailableTrueAndPosition(Staff.Position position);
    
    @Query("SELECT s FROM Staff s WHERE s.isAvailable = true AND s.id NOT IN " +
           "(SELECT sch.staff.id FROM Schedule sch WHERE sch.scheduleDate = :date " +
           "AND sch.status IN ('SCHEDULED', 'CONFIRMED'))")
    List<Staff> findAvailableStaffForDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Staff s WHERE s.isAvailable = true AND s.position = :position " +
           "AND s.id NOT IN (SELECT sch.staff.id FROM Schedule sch WHERE sch.scheduleDate = :date " +
           "AND sch.status IN ('SCHEDULED', 'CONFIRMED'))")
    List<Staff> findAvailableStaffForDateAndPosition(@Param("date") LocalDate date, 
                                                    @Param("position") Staff.Position position);
    
    boolean existsByEmail(String email);
}
