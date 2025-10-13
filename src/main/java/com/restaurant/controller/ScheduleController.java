package com.restaurant.controller;

import com.restaurant.model.Schedule;
import com.restaurant.model.Staff;
import com.restaurant.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/dashboard")
    public String scheduleDashboard(Model model) {
        // Get current week's schedules
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        Map<LocalDate, List<Schedule>> schedules = scheduleService.getSchedulesGroupedByDate(weekStart, weekEnd);
        List<Staff> availableStaff = scheduleService.getAvailableStaff();
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("availableStaff", availableStaff);
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("today", today);
        
        return "schedule/dashboard";
    }

    @GetMapping("/dashboard/{startDate}/{endDate}")
    public String scheduleDashboardForDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        Map<LocalDate, List<Schedule>> schedules = scheduleService.getSchedulesGroupedByDate(startDate, endDate);
        List<Staff> availableStaff = scheduleService.getAvailableStaff();
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("availableStaff", availableStaff);
        model.addAttribute("weekStart", startDate);
        model.addAttribute("weekEnd", endDate);
        model.addAttribute("today", LocalDate.now());
        
        return "schedule/dashboard";
    }

    @GetMapping("/staff/available")
    @ResponseBody
    public ResponseEntity<List<Staff>> getAvailableStaffForDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Staff.Position position) {
        
        LocalDate effectiveDate = (date != null) ? date : LocalDate.now();
        List<Staff> availableStaff;
        if (position != null) {
            availableStaff = scheduleService.getAvailableStaffForPosition(effectiveDate, position);
        } else {
            availableStaff = scheduleService.getAvailableStaff(effectiveDate);
        }
        
        return ResponseEntity.ok(availableStaff);
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createSchedule(
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find staff member
            List<Staff> allStaff = scheduleService.getAllStaff();
            Staff staff = allStaff.stream()
                .filter(s -> s.getId().equals(staffId))
                .findFirst()
                .orElse(null);
            
            if (staff == null) {
                response.put("success", false);
                response.put("message", "Staff member not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create new schedule
            Schedule newSchedule = new Schedule(staff, scheduleDate, startTime, endTime);
            if (notes != null && !notes.trim().isEmpty()) {
                newSchedule.setNotes(notes.trim());
            }
            
            // Check for conflicts
            ScheduleService.ScheduleConflictResult conflictResult = scheduleService.checkForConflicts(newSchedule);
            if (conflictResult.hasConflict()) {
                response.put("success", false);
                response.put("message", conflictResult.getMessage());
                response.put("conflicts", conflictResult.getConflictingSchedules());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Save schedule
            String createdBy = authentication != null ? authentication.getName() : "System";
            Schedule savedSchedule = scheduleService.saveSchedule(newSchedule, createdBy);
            
            response.put("success", true);
            response.put("message", "Schedule created successfully");
            response.put("schedule", savedSchedule);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create schedule: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create-multiple")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createMultipleSchedules(
            @RequestBody List<ScheduleRequest> scheduleRequests,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Schedule> schedules = scheduleRequests.stream()
                .map(req -> {
                    Staff staff = scheduleService.getAllStaff().stream()
                        .filter(s -> s.getId().equals(req.getStaffId()))
                        .findFirst()
                        .orElse(null);
                    
                    if (staff == null) {
                        throw new RuntimeException("Staff member not found with ID: " + req.getStaffId());
                    }
                    
                    Schedule schedule = new Schedule(staff, req.getScheduleDate(), req.getStartTime(), req.getEndTime());
                    if (req.getNotes() != null && !req.getNotes().trim().isEmpty()) {
                        schedule.setNotes(req.getNotes().trim());
                    }
                    return schedule;
                })
                .toList();
            
            String createdBy = authentication != null ? authentication.getName() : "System";
            List<Schedule> savedSchedules = scheduleService.saveMultipleSchedules(schedules, createdBy);
            
            response.put("success", true);
            response.put("message", "Schedules created successfully");
            response.put("schedules", savedSchedules);
            response.put("count", savedSchedules.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create schedules: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update/{scheduleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) Schedule.ScheduleStatus status,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find staff member
            List<Staff> allStaff = scheduleService.getAllStaff();
            Staff staff = allStaff.stream()
                .filter(s -> s.getId().equals(staffId))
                .findFirst()
                .orElse(null);
            
            if (staff == null) {
                response.put("success", false);
                response.put("message", "Staff member not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create updated schedule
            Schedule updatedSchedule = new Schedule(staff, scheduleDate, startTime, endTime);
            if (notes != null && !notes.trim().isEmpty()) {
                updatedSchedule.setNotes(notes.trim());
            }
            if (status != null) {
                updatedSchedule.setStatus(status);
            }
            
            // Update schedule
            String updatedBy = authentication != null ? authentication.getName() : "System";
            Schedule savedSchedule = scheduleService.updateSchedule(scheduleId, updatedSchedule, updatedBy);
            
            response.put("success", true);
            response.put("message", "Schedule updated successfully");
            response.put("schedule", savedSchedule);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update schedule: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{scheduleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSchedule(@PathVariable Long scheduleId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            scheduleService.deleteSchedule(scheduleId);
            response.put("success", true);
            response.put("message", "Schedule deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete schedule: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/check-conflicts")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkConflicts(
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Long excludeScheduleId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find staff member
            List<Staff> allStaff = scheduleService.getAllStaff();
            Staff staff = allStaff.stream()
                .filter(s -> s.getId().equals(staffId))
                .findFirst()
                .orElse(null);
            
            if (staff == null) {
                response.put("success", false);
                response.put("message", "Staff member not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create schedule for conflict checking
            Schedule schedule = new Schedule(staff, scheduleDate, startTime, endTime);
            
            // Check for conflicts
            ScheduleService.ScheduleConflictResult conflictResult;
            if (excludeScheduleId != null) {
                conflictResult = scheduleService.checkForConflictsExcluding(schedule, excludeScheduleId);
            } else {
                conflictResult = scheduleService.checkForConflicts(schedule);
            }
            
            response.put("success", true);
            response.put("hasConflict", conflictResult.hasConflict());
            response.put("message", conflictResult.getMessage());
            response.put("conflicts", conflictResult.getConflictingSchedules());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to check conflicts: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DTO for schedule requests
    public static class ScheduleRequest {
        private Long staffId;
        private LocalDate scheduleDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String notes;

        // Getters and Setters
        public Long getStaffId() { return staffId; }
        public void setStaffId(Long staffId) { this.staffId = staffId; }

        public LocalDate getScheduleDate() { return scheduleDate; }
        public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
