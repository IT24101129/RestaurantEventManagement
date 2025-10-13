package com.restaurant.service;

import com.restaurant.model.Schedule;
import com.restaurant.model.Staff;
import com.restaurant.repository.ScheduleRepository;
import com.restaurant.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository, 
                          StaffRepository staffRepository,
                          NotificationService notificationService) {
        this.scheduleRepository = scheduleRepository;
        this.staffRepository = staffRepository;
        this.notificationService = notificationService;
    }

    public List<Staff> getAvailableStaff() {
        return staffRepository.findByIsAvailableTrue();
    }

    public List<Staff> getAvailableStaff(LocalDate date) {
        return staffRepository.findAvailableStaffForDate(date);
    }

    public List<Staff> getAvailableStaffForPosition(LocalDate date, Staff.Position position) {
        return staffRepository.findAvailableStaffForDateAndPosition(date, position);
    }

    public List<Schedule> getSchedulesForDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findSchedulesInDateRange(startDate, endDate);
    }

    public List<Schedule> getSchedulesForDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date);
    }

    public Map<LocalDate, List<Schedule>> getSchedulesGroupedByDate(LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = getSchedulesForDateRange(startDate, endDate);
        return schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getScheduleDate));
    }

    public ScheduleConflictResult checkForConflicts(Schedule newSchedule) {
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
            newSchedule.getStaff(), 
            newSchedule.getScheduleDate(),
            newSchedule.getStartTime(),
            newSchedule.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            return new ScheduleConflictResult(true, conflicts, 
                "Scheduling conflict detected. Staff member has overlapping shifts.");
        }

        // Check if staff exceeds max hours per week
        LocalDate weekStart = newSchedule.getScheduleDate().minusDays(newSchedule.getScheduleDate().getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        Long currentHours = scheduleRepository.countScheduledHoursForStaff(
            newSchedule.getStaff(), weekStart, weekEnd);
        
        long newHours = newSchedule.getDurationInHours();
        long totalHours = currentHours + newHours;
        
        if (totalHours > newSchedule.getStaff().getMaxHoursPerWeek()) {
            return new ScheduleConflictResult(true, new ArrayList<>(), 
                String.format("Staff member would exceed maximum hours per week (%d/%d hours).", 
                    totalHours, newSchedule.getStaff().getMaxHoursPerWeek()));
        }

        return new ScheduleConflictResult(false, new ArrayList<>(), "No conflicts detected.");
    }

    public ScheduleConflictResult checkForConflictsExcluding(Schedule newSchedule, Long excludeId) {
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedulesExcluding(
            newSchedule.getStaff(), 
            newSchedule.getScheduleDate(),
            newSchedule.getStartTime(),
            newSchedule.getEndTime(),
            excludeId
        );

        if (!conflicts.isEmpty()) {
            return new ScheduleConflictResult(true, conflicts, 
                "Scheduling conflict detected. Staff member has overlapping shifts.");
        }

        return new ScheduleConflictResult(false, new ArrayList<>(), "No conflicts detected.");
    }

    public Schedule saveSchedule(Schedule schedule, String createdBy) {
        try {
            schedule.setCreatedBy(createdBy);
            Schedule savedSchedule = scheduleRepository.save(schedule);
            
            // Send notification to staff member
            notificationService.sendScheduleNotification(savedSchedule);
            
            return savedSchedule;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save schedule: " + e.getMessage(), e);
        }
    }

    public List<Schedule> saveMultipleSchedules(List<Schedule> schedules, String createdBy) {
        List<Schedule> savedSchedules = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Schedule schedule : schedules) {
            try {
                ScheduleConflictResult conflictResult = checkForConflicts(schedule);
                if (conflictResult.hasConflict()) {
                    errors.add(String.format("Conflict for %s on %s: %s", 
                        schedule.getStaff().getName(), 
                        schedule.getScheduleDate(), 
                        conflictResult.getMessage()));
                    continue;
                }

                schedule.setCreatedBy(createdBy);
                Schedule savedSchedule = scheduleRepository.save(schedule);
                savedSchedules.add(savedSchedule);
                
                // Send notification to staff member
                notificationService.sendScheduleNotification(savedSchedule);
                
            } catch (Exception e) {
                errors.add(String.format("Failed to save schedule for %s: %s", 
                    schedule.getStaff().getName(), e.getMessage()));
            }
        }

        if (!errors.isEmpty() && savedSchedules.isEmpty()) {
            throw new RuntimeException("Failed to save any schedules: " + String.join("; ", errors));
        }

        return savedSchedules;
    }

    public Schedule updateSchedule(Long scheduleId, Schedule updatedSchedule, String updatedBy) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        // Check for conflicts excluding the current schedule
        ScheduleConflictResult conflictResult = checkForConflictsExcluding(updatedSchedule, scheduleId);
        if (conflictResult.hasConflict()) {
            throw new RuntimeException(conflictResult.getMessage());
        }

        try {
            existingSchedule.setStaff(updatedSchedule.getStaff());
            existingSchedule.setScheduleDate(updatedSchedule.getScheduleDate());
            existingSchedule.setStartTime(updatedSchedule.getStartTime());
            existingSchedule.setEndTime(updatedSchedule.getEndTime());
            existingSchedule.setStatus(updatedSchedule.getStatus());
            existingSchedule.setNotes(updatedSchedule.getNotes());
            existingSchedule.setUpdatedAt(java.time.LocalDateTime.now());

            Schedule savedSchedule = scheduleRepository.save(existingSchedule);
            
            // Send notification about schedule update
            notificationService.sendScheduleUpdateNotification(savedSchedule);
            
            return savedSchedule;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update schedule: " + e.getMessage(), e);
        }
    }

    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));
        
        try {
            scheduleRepository.deleteById(scheduleId);
            
            // Send notification about schedule cancellation
            notificationService.sendScheduleCancellationNotification(schedule);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete schedule: " + e.getMessage(), e);
        }
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public static class ScheduleConflictResult {
        private final boolean hasConflict;
        private final List<Schedule> conflictingSchedules;
        private final String message;

        public ScheduleConflictResult(boolean hasConflict, List<Schedule> conflictingSchedules, String message) {
            this.hasConflict = hasConflict;
            this.conflictingSchedules = conflictingSchedules;
            this.message = message;
        }

        public boolean hasConflict() { return hasConflict; }
        public List<Schedule> getConflictingSchedules() { return conflictingSchedules; }
        public String getMessage() { return message; }
    }
}
